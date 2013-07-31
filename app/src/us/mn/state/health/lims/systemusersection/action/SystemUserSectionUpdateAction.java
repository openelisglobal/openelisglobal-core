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
package us.mn.state.health.lims.systemusersection.action;

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

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.exception.LIMSDuplicateRecordException;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.resources.ResourceLocator;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.dao.UserTestSectionDAO;
import us.mn.state.health.lims.login.daoimpl.UserTestSectionDAOImpl;
import us.mn.state.health.lims.login.valueholder.UserSessionData;
import us.mn.state.health.lims.systemuser.dao.SystemUserDAO;
import us.mn.state.health.lims.systemuser.daoimpl.SystemUserDAOImpl;
import us.mn.state.health.lims.systemuser.valueholder.SystemUser;
import us.mn.state.health.lims.systemusersection.dao.SystemUserSectionDAO;
import us.mn.state.health.lims.systemusersection.daoimpl.SystemUserSectionDAOImpl;
import us.mn.state.health.lims.systemusersection.valueholder.SystemUserSection;
import us.mn.state.health.lims.test.valueholder.TestSection;

/**
 *  @author     Hung Nguyen (Hung.Nguyen@health.state.mn.us)
 */
public class SystemUserSectionUpdateAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(IActionConstants.ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "false");
		request.setAttribute(NEXT_DISABLED, "false");

		String id = request.getParameter("ID");
		
		//get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());	
		
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

		SystemUserSection systemUserSection = new SystemUserSection();
		systemUserSection.setSysUserId(sysUserId);
		org.hibernate.Transaction tx = HibernateUtil.getSession().beginTransaction();	
		
		// populate valueholder from form
		PropertyUtils.copyProperties(systemUserSection, dynaForm);
		
		String systemUserId = (String) dynaForm.get("systemUserId");		
		List systemUsers = new ArrayList();
		if (dynaForm.get("systemusers") != null) {
			systemUsers = (List) dynaForm.get("systemusers");
		} else {
			SystemUserDAO systemUserDAO = new SystemUserDAOImpl();
			systemUsers = systemUserDAO.getAllSystemUsers();
		}
		
		SystemUser systemUser = null;
		for (int i = 0; i < systemUsers.size(); i++) {
			SystemUser sysUser = (SystemUser) systemUsers.get(i);
			if (sysUser.getId().equals(systemUserId)) {
				systemUser = sysUser;
				break;
			}
		}

		String testSectionId = (String) dynaForm.get("testSectionId");
		List testSections = new ArrayList();
		if (dynaForm.get("testsections") != null) {
			testSections = (List) dynaForm.get("testsections");
		} else {
			//Get testsections by user system id
			//bugzilla 2160			
			UserTestSectionDAO userTestSectionDAO = new UserTestSectionDAOImpl();
			testSections = userTestSectionDAO.getAllUserTestSections(request);
		}
		
		TestSection testSection = null;
		for (int i = 0; i < testSections.size(); i++) {
			TestSection testSect = (TestSection) testSections.get(i);
			if (testSect.getId().equals(testSectionId)) {
				testSection = testSect;
				break;
			}
		}
	
		systemUserSection.setSystemUser(systemUser);
		systemUserSection.setTestSection(testSection);
			
		try {
			SystemUserSectionDAO systemUserSectionDAO = new SystemUserSectionDAOImpl();

			if (!isNew) {
				// UPDATE
				systemUserSectionDAO.updateData(systemUserSection);
			} else {
				// INSERT
				systemUserSectionDAO.insertData(systemUserSection);
			}
			tx.commit();
		} catch (LIMSRuntimeException lre) {
    		//bugzilla 2154
			LogEvent.logError("SystemUserSectionUpdateAction","performAction()",lre.toString());
			tx.rollback();
			errors = new ActionMessages();
			java.util.Locale locale = (java.util.Locale) request.getSession()
			.getAttribute("org.apache.struts.action.LOCALE");
			ActionError error = null;
			if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
				error = new ActionError("errors.OptimisticLockException", null,null);
			} else {
				if (lre.getException() instanceof LIMSDuplicateRecordException) {
					String messageKey = "systemmodule.name";
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
		PropertyUtils.copyProperties(dynaForm, systemUserSection);

		if ("true".equalsIgnoreCase(request.getParameter("close"))) {
			forward = FWD_CLOSE;
		}

		if (systemUserSection.getId() != null && !systemUserSection.getId().equals("0")) {
			request.setAttribute("ID", systemUserSection.getId());

		}

		if (isNew) forward = FWD_SUCCESS_INSERT;
		return getForward(mapping.findForward(forward), id, start, direction);

	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "systemusersection.add.title";
		} else {
			return "systemusersection.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "systemusersection.add.title";
		} else {
			return "systemusersection.edit.title";
		}
	}
}