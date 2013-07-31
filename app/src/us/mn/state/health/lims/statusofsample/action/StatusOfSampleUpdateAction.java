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
package us.mn.state.health.lims.statusofsample.action;


import java.util.Locale;

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
import us.mn.state.health.lims.common.exception.LIMSDuplicateRecordException;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.resources.ResourceLocator;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.valueholder.UserSessionData;
import us.mn.state.health.lims.statusofsample.dao.StatusOfSampleDAO;
import us.mn.state.health.lims.statusofsample.daoimpl.StatusOfSampleDAOImpl;
import us.mn.state.health.lims.statusofsample.valueholder.StatusOfSample;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class StatusOfSampleUpdateAction extends BaseAction {
	
	public StatusOfSampleUpdateAction() {}
	
	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new StatusOfSample.
		// If there is a parameter present, we should bring up an existing
		// StatusOfSample to edit.
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

		StatusOfSample statusOfSample = new StatusOfSample();
		//get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());	
		statusOfSample.setSysUserId(sysUserId);			
		org.hibernate.Transaction tx = null; 
		
		
		// populate valueholder from form
		PropertyUtils.copyProperties(statusOfSample, dynaForm);

		try {
			tx = HibernateUtil.getSession().beginTransaction();
			
			StatusOfSampleDAO statusOfSampleDAO = new StatusOfSampleDAOImpl();

			if (!isNew) {
				// UPDATE
				statusOfSampleDAO.updateData(statusOfSample);				
			} else {
				// INSERT
				statusOfSampleDAO.insertData(statusOfSample);
				
			}
			
			tx.commit();
			
			//bugzilla 2154
			LogEvent.logDebug("StatusOfSampleUpdateAction","performAction()","Inserting statusOfSample id: " + 
								statusOfSample.getId() + 
								" Desc: " + statusOfSample.getDescription() +
								" Status Type: " + statusOfSample.getStatusType());
			
		} catch (LIMSRuntimeException lre) {
			//bugzilla 2154
			LogEvent.logError("StatusOfSampleUpdateAction","performAction()",lre.toString());
			tx.rollback();
			ActionError error = null;			
			errors = new ActionMessages();
			Locale locale = (Locale) request.getSession()
			.getAttribute("org.apache.struts.action.LOCALE");
			
			if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
				// how can I get popup instead of struts error at the top of
				// page?
				// ActionMessages errors = dynaForm.validate(mapping, request);
				error = new ActionError("errors.OptimisticLockException", null,
						null);
			} else {
				// bugzilla 1482
				if (lre.getException() instanceof LIMSDuplicateRecordException) {
					String messageKey = "statusofsample.statusofsample";
					String msg = ResourceLocator.getInstance()
							.getMessageResources().getMessage(locale,
									messageKey);
					error = new ActionError("errors.DuplicateRecord",
							msg, null);

				} else {
					error = new ActionError("errors.UpdateException", null,
							null);
				}
			}
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			//bugzilla 1485: allow change and try updating again (enable save button)
			//request.setAttribute(IActionConstants.ALLOW_EDITS_KEY, "false");
			// disable previous and next
			request.setAttribute(PREVIOUS_DISABLED, "true");
			request.setAttribute(NEXT_DISABLED, "true");
			forward = FWD_FAIL;
			
			//bugzilla 2154
			LogEvent.logError("StatusOfSampleUpdateAction","performAction()",
			                 "Exception caught adding/updating Status Of Sample record id: " +
			                 statusOfSample.getId() + " Desc: " +
			                 statusOfSample.getDescription() + " Code: " +
			                 statusOfSample.getCode() + " Status Type: " +
			                 statusOfSample.getStatusType() + "\nException: " +
			                 lre.toString());
			
		}finally {
			HibernateUtil.closeSession();
		}
		
		if (forward.equals(FWD_FAIL))
			return mapping.findForward(forward);
		
		// initialize the form
		dynaForm.initialize(mapping);
		// repopulate the form from valueholder
		PropertyUtils.copyProperties(dynaForm, statusOfSample);

		//PropertyUtils.setProperty(dynaForm, "domains", domains);

		if ("true".equalsIgnoreCase(request.getParameter("close"))) {
			forward = FWD_CLOSE;
		}

		if (statusOfSample.getId() != null
				&& !statusOfSample.getId().equals("0")) {
			request.setAttribute(ID, statusOfSample.getId());

		}

		//bugzilla 1400
		if (isNew) forward = FWD_SUCCESS_INSERT;
		//bugzilla 1467 added direction for redirect to NextPreviousAction
		return getForward(mapping.findForward(forward), id, start, direction);

	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "statusofsample.add.title";
		} else {
			return "statusofsample.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "statusofsample.add.title";
		} else {
			return "statusofsample.edit.title";
		}
	}
}