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
package us.mn.state.health.lims.analyte.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import us.mn.state.health.lims.analyte.dao.AnalyteDAO;
import us.mn.state.health.lims.analyte.daoimpl.AnalyteDAOImpl;
import us.mn.state.health.lims.analyte.valueholder.Analyte;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSDuplicateRecordException;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.resources.ResourceLocator;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.valueholder.UserSessionData;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class AnalyteUpdateAction extends BaseAction {
	
	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new Analyte.
		// If there is a parameter present, we should bring up an existing
		// Analyte to edit.

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
		try {
			errors = validateAll(request, errors, dynaForm);
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("AnalyteUpdateAction","performAction()",e.toString());			
			ActionError error = new ActionError("errors.ValidationException",
					null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}

		if (errors != null && errors.size() > 0) {
			// System.out.println("Server side validation errors "
			// + errors.toString());
			saveErrors(request, errors);
			// since we forward to jsp - not Action we don't need to repopulate
			// the lists here
			return mapping.findForward(FWD_FAIL);
		}

		String start = (String) request.getParameter("startingRecNo");
		String direction = (String) request.getParameter("direction");

		// System.out.println("This is ID from request " + id);
		Analyte analyte = new Analyte();
		//get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());		
		analyte.setSysUserId(sysUserId);
		org.hibernate.Transaction tx = HibernateUtil.getSession().beginTransaction();
		
		// String selectedAnalyteId = (String)
		// dynaForm.get("selectedAnalyteId");
		String parentAnalyteName = (String) dynaForm.get("parentAnalyteName");

		// System.out.println("This is selectedAnalyteId "
		// + dynaForm.get("selectedAnalyteId"));
		List anals = new ArrayList();
		if (dynaForm.get("parentAnalytes") != null) {
			anals = (List) dynaForm.get("parentAnalytes");
		} else {
			AnalyteDAO analDAO = new AnalyteDAOImpl();
			anals = analDAO.getAllAnalytes();
		}

		Analyte parentAnalyte = null;

		// get the right parentAnalyte to update analyte with
		for (int i = 0; i < anals.size(); i++) {
			Analyte o = (Analyte) anals.get(i);
			// if (o.getId().equals(selectedAnalyteId)) {
			if ( (o != null) && (parentAnalyteName != null) ) {
				if (o.getAnalyteName().equals(parentAnalyteName)) {
					parentAnalyte = o;
					break;
				}
			}	
		}

		// populate valueholder from form
		PropertyUtils.copyProperties(analyte, dynaForm);

		analyte.setAnalyte(parentAnalyte);

		try {

			AnalyteDAO analyteDAO = new AnalyteDAOImpl();
			if (!isNew) {
				// UPDATE
                analyteDAO.updateData(analyte);
			} else {
				// INSERT
				analyteDAO.insertData(analyte);
			}
			tx.commit();
		} catch (LIMSRuntimeException lre) {
			//bugzilla 2154
			LogEvent.logError("AnalyteUpdateAction","performAction()",lre.toString());			
			tx.rollback();
			errors = new ActionMessages();
			ActionError error = null;
			//bugzilla 1482
			java.util.Locale locale = (java.util.Locale) request.getSession()
			.getAttribute("org.apache.struts.action.LOCALE");
			if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
				// how can I get popup instead of struts error at the top of
				// page?
				// ActionMessages errors = dynaForm.validate(mapping, request);
				error = new ActionError("errors.OptimisticLockException", null,
						null);

			} else {
				//bugzilla 1482
				if (lre.getException() instanceof LIMSDuplicateRecordException) {
				    //bugzilla 2432
					String messageKey = "analyte.analyteNameOrLocalAbbreviation";
					String msg =  ResourceLocator.getInstance().getMessageResources().getMessage(
							locale, messageKey);
					error = new ActionError("errors.DuplicateRecord.activate", msg,
							null);

				} else {
				error = new ActionError("errors.UpdateException", null, null);
				}
			}

			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			//bugzilla 1485: allow change and try updating again (enable save button)
			//request.setAttribute(ALLOW_EDITS_KEY, "false");
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
		PropertyUtils.copyProperties(dynaForm, analyte);

		// need to repopulate in case of FWD_FAIL?
		PropertyUtils.setProperty(form, "parentAnalytes", anals);

		if ("true".equalsIgnoreCase(request.getParameter("close"))) {
			forward = FWD_CLOSE;
		}

		if (analyte.getId() != null && !analyte.getId().equals("0")) {
			request.setAttribute(ID, analyte.getId());
		}

		//bugzilla 1400
		if (isNew) forward = FWD_SUCCESS_INSERT;
		//bugzilla 1467 added direction for redirect to NextPreviousAction
		return getForward(mapping.findForward(forward), id, start, direction);

	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "analyte.add.title";
		} else {
			return "analyte.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "analyte.add.title";
		} else {
			return "analyte.edit.title";
		}
	}

	protected ActionMessages validateAll(HttpServletRequest request,
			ActionMessages errors, BaseActionForm dynaForm) throws Exception {

		// parent analyte validation against database
		String parentAnalyteSelected = (String) dynaForm
				.get("parentAnalyteName");

		if (!StringUtil.isNullorNill(parentAnalyteSelected)) {
			Analyte analyte = new Analyte();
			analyte.setAnalyteName(parentAnalyteSelected);
			AnalyteDAO analyteDAO = new AnalyteDAOImpl();
			//bugzilla 1367
			analyte = analyteDAO.getAnalyteByName(analyte, false);

			String messageKey = "analyte.parent";

			if (analyte == null) {
				// the analyte is not in database - not valid parentAnalyte
				ActionError error = new ActionError("errors.invalid",
						getMessageForKey(messageKey), null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			}
		}

		return errors;
	}

}