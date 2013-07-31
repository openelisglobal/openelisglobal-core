/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/ 
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The Minnesota Department of Health.  All Rights Reserved.
*/
package us.mn.state.health.lims.sample.action;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.valueholder.UserSessionData;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.sampledomain.dao.SampleDomainDAO;
import us.mn.state.health.lims.sampledomain.daoimpl.SampleDomainDAOImpl;
import us.mn.state.health.lims.systemuser.dao.SystemUserDAO;
import us.mn.state.health.lims.systemuser.daoimpl.SystemUserDAOImpl;
import us.mn.state.health.lims.systemuser.valueholder.SystemUser;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class SampleUpdateAction extends BaseAction {
	
	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new Sample.
		// If there is a parameter present, we should bring up an existing
		// Sample to edit.
		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "false");
		request.setAttribute(NEXT_DISABLED, "false");
		
		String id = request.getParameter(ID);
		
		if (StringUtil.isNullorNill(id) || "0".equals(id)) {
			isNew = true;
		} else {
			isNew = false;
		}

		BaseActionForm dynaForm = (BaseActionForm) form;

		// server-side validation (validation.xml)
		ActionMessages errors = dynaForm.validate(mapping, request);
		if (errors != null && errors.size() > 0) {
			saveErrors(request, errors);
			// since we forward to jsp - not Action we don't need to repopulate
			// the lists here
			return mapping.findForward(FWD_FAIL);
		}

		String start = (String) request.getParameter("startingRecNo");
		String direction = (String) request.getParameter("direction");

		Sample sample = new Sample();
		//get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId1 = String.valueOf(usd.getSystemUserId());
		sample.setSysUserId(sysUserId1);
		org.hibernate.Transaction tx = HibernateUtil.getSession().beginTransaction();

		String sysUserId = (String) dynaForm.get("sysUserId");
		//String sampleIdRelatesTo = (String) dynaForm.get("sampleIdRelatesTo");

		List sysUsers = new ArrayList();
		//List samps = new ArrayList();
		List sampleDomains = new ArrayList();

		if (dynaForm.get("sysUsers") != null) {
			sysUsers = (List) dynaForm.get("sysUsers");
		} else {
			SystemUserDAO sysUserDAO = new SystemUserDAOImpl();
			sysUsers = sysUserDAO.getAllSystemUsers();
		}
		if (dynaForm.get("sampleDomains") != null) {
			sampleDomains = (List) dynaForm.get("sampleDomains");
		} else {
			SampleDomainDAO sampleDomainDAO = new SampleDomainDAOImpl();
			sampleDomains = sampleDomainDAO.getAllSampleDomains();
		}

		SystemUser sysUser = null;

		for (int i = 0; i < sysUsers.size(); i++) {
			SystemUser su = (SystemUser) sysUsers.get(i);
			if (su.getId().equals(sysUserId)) {
				sysUser = su;
				break;
			}
		}

		// populate valueholder from form
		PropertyUtils.copyProperties(sample, dynaForm);

		// set the system user
		sample.setSystemUser(sysUser);

		// set collection time
		String time = (String) dynaForm.get("collectionTimeForDisplay");

		if (StringUtil.isNullorNill(time)) {
			time = "00:00";
		}
		sample.setCollectionTimeForDisplay(time);

		Timestamp d = sample.getCollectionDate();
		if (time.indexOf(":") > 0) {
			//bugzilla 1857 deprecated stuff
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time.substring(0, 2)).intValue());
			cal.set(Calendar.MINUTE, Integer.valueOf(time.substring(3, 5)).intValue());
			//d.setHours(Integer.valueOf(time.substring(0, 2)).intValue());
			//d.setMinutes(Integer.valueOf(time.substring(3, 5)).intValue());
            d = new Timestamp(cal.getTimeInMillis());
			sample.setCollectionDate(d);
		}

		try {

			SampleDAO sampleDAO = new SampleDAOImpl();

			if (!isNew) {
				// UPDATE
				sampleDAO.updateData(sample);
				if (FWD_NEXT.equals(direction)) {
					List samples = sampleDAO
							.getNextSampleRecord(sample.getId());
					if (samples != null && samples.size() > 0) {
						sample = (Sample) samples.get(0);
						sampleDAO.getData(sample);
						if (samples.size() < 2) {
							// disable next button
							request.setAttribute(NEXT_DISABLED, "true");
						}
						id = sample.getId();
					} else {
						// disable next button
						request.setAttribute(NEXT_DISABLED, "true");
					}
					forward = FWD_NEXT;
				}

				if (FWD_PREVIOUS.equals(direction)) {
					List samples = sampleDAO.getPreviousSampleRecord(sample
							.getId());
					if (samples != null && samples.size() > 0) {
						sample = (Sample) samples.get(0);
						sampleDAO.getData(sample);
						if (samples.size() < 2) {
							// disable previous button
							request.setAttribute(PREVIOUS_DISABLED, "true");
						}
						id = sample.getId();
					} else {
						// disable previous button
						request.setAttribute(PREVIOUS_DISABLED, "true");
					}
					forward = FWD_PREVIOUS;
				}
			} else {
				// INSERT

				sampleDAO.insertData(sample);
			}
			tx.commit();
		} catch (LIMSRuntimeException lre) {
    		//bugzilla 2154
			LogEvent.logError("SampleUpdateAction","performAction()",lre.toString());
			tx.rollback();
			errors = new ActionMessages();
			ActionError error = null;
			if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
				// how can I get popup instead of struts error at the top of
				// page?
				// ActionMessages errors = dynaForm.validate(mapping, request);
				error = new ActionError("errors.OptimisticLockException", null,
						null);
			} else {
				error = new ActionError("errors.UpdateException", null, null);
			}
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute(ALLOW_EDITS_KEY, "false");
			// disable previous and next
			request.setAttribute(PREVIOUS_DISABLED, "true");
			request.setAttribute(NEXT_DISABLED, "true");
			forward = FWD_FAIL;

		} finally {
            HibernateUtil.closeSession();
        }
		if (forward.equals(FWD_FAIL))
			return mapping.findForward(forward);

		// initialize the form
		dynaForm.initialize(mapping);
		// repopulate the form from valueholder
		PropertyUtils.copyProperties(dynaForm, sample);

		// need to repopulate in case of FWD_FAIL?
		PropertyUtils.setProperty(dynaForm, "sysUsers", sysUsers);
		PropertyUtils.setProperty(dynaForm, "sampleDomains", sampleDomains);

		if ("true".equalsIgnoreCase(request.getParameter("close"))) {
			forward = FWD_CLOSE;
		}

		if (sample.getId() != null && !sample.getId().equals("0")) {
			request.setAttribute(ID, sample.getId());

		}

		
		//bugzilla 1400
		if (isNew) forward = FWD_SUCCESS_INSERT;
		return getForward(mapping.findForward(forward), id, start);

	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "sample.add.title";
		} else {
			return "sample.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "sample.add.title";
		} else {
			return "sample.edit.title";
		}
	}

}