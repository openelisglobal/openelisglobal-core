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
package us.mn.state.health.lims.messageorganization.action;

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
import us.mn.state.health.lims.messageorganization.dao.MessageOrganizationDAO;
import us.mn.state.health.lims.messageorganization.daoimpl.MessageOrganizationDAOImpl;
import us.mn.state.health.lims.messageorganization.valueholder.MessageOrganization;
import us.mn.state.health.lims.organization.dao.OrganizationDAO;
import us.mn.state.health.lims.organization.daoimpl.OrganizationDAOImpl;
import us.mn.state.health.lims.organization.valueholder.Organization;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class MessageOrganizationUpdateAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new MessageOrganization.
		// If there is a parameter present, we should bring up an existing
		// MessageOrganization to edit.

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
		
		//bugzilla 1923 validateAll was not being executed
		if (errors == null || errors.size() == 0) {
		 try {
			errors = validateAll(request, errors, dynaForm);
		 } catch (Exception e) {
    		//bugzilla 2154
			LogEvent.logError("MessageOrganizationUpdateAction","performAction()",e.toString());  
			ActionError error = new ActionError("errors.ValidationException",
					null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		 }
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
		MessageOrganization messageOrganization = new MessageOrganization();
		// get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());
		messageOrganization.setSysUserId(sysUserId);
		org.hibernate.Transaction tx = HibernateUtil.getSession()
				.beginTransaction();

		// populate valueholder from form
		PropertyUtils.copyProperties(messageOrganization, dynaForm);

		// get org valueholder to be set in messageOrganization
		OrganizationDAO organizationDAO = new OrganizationDAOImpl();
		Organization organization = new Organization();
		organization.setOrganizationName(messageOrganization
				.getOrganizationName());
		organization = organizationDAO
				.getOrganizationByName(organization, true);
		organizationDAO.getData(organization);
		messageOrganization.setOrganization(organization);

		try {

			MessageOrganizationDAO messageOrganizationDAO = new MessageOrganizationDAOImpl();

			if (!isNew) {
				// UPDATE

				messageOrganizationDAO.updateData(messageOrganization);

			} else {
				// INSERT

				messageOrganizationDAO.insertData(messageOrganization);
			}
			tx.commit();
		} catch (LIMSRuntimeException lre) {
            //bugzilla 2154
			LogEvent.logError("MessageOrganizationUpdateAction","performAction()",lre.toString());
			tx.rollback();
			errors = new ActionMessages();
			java.util.Locale locale = (java.util.Locale) request.getSession()
					.getAttribute("org.apache.struts.action.LOCALE");
			ActionError error = null;
			if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
				// how can I get popup instead of struts error at the top of
				// page?
				// ActionMessages errors = dynaForm.validate(mapping, request);
				error = new ActionError("errors.OptimisticLockException", null,
						null);

			} else {
				// bugzilla 1482
				if (lre.getException() instanceof LIMSDuplicateRecordException) {
					String messageKey = "messageorganization.organization";
					String msg = ResourceLocator.getInstance()
							.getMessageResources().getMessage(locale,
									messageKey);
					error = new ActionError("errors.DuplicateRecord", msg, null);

				} else {
					error = new ActionError("errors.UpdateException", null,
							null);
				}
			}

			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			// bugzilla 1485: allow change and try updating again (enable save
			// button)
			// request.setAttribute(IActionConstants.ALLOW_EDITS_KEY, "false");
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
		PropertyUtils.copyProperties(dynaForm, messageOrganization);

		if ("true".equalsIgnoreCase(request.getParameter("close"))) {
			forward = FWD_CLOSE;
		}

		if (messageOrganization.getId() != null
				&& !messageOrganization.getId().equals("0")) {
			request.setAttribute(ID, messageOrganization.getId());

		}

		// bugzilla 1400
		if (isNew)
			forward = FWD_SUCCESS_INSERT;
		// bugzilla 1467 added direction for redirect to NextPreviousAction
		return getForward(mapping.findForward(forward), id, start, direction);

	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "messageorganization.add.title";
		} else {
			return "messageorganization.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "messageorganization.add.title";
		} else {
			return "messageorganization.edit.title";
		}
	}

	protected ActionMessages validateAll(HttpServletRequest request,
			ActionMessages errors, BaseActionForm dynaForm) throws Exception {

		// organization validation against database
		String organizationSelected = (String) dynaForm.get("organizationName");

		if (!StringUtil.isNullorNill(organizationSelected)) {
			Organization org = new Organization();
			org.setOrganizationName(organizationSelected);
			OrganizationDAO orgDAO = new OrganizationDAOImpl();
			org = orgDAO.getOrganizationByName(org, true);

			String messageKey = "messageorganization.organization";

			if (org == null) {
				// the organization is not in database - not valid
				ActionError error = new ActionError("errors.invalid",
						getMessageForKey(messageKey), null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			}
		}
		return errors;
	}

}