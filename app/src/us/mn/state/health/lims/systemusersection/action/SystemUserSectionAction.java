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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.login.dao.UserTestSectionDAO;
import us.mn.state.health.lims.login.daoimpl.UserTestSectionDAOImpl;
import us.mn.state.health.lims.systemuser.dao.SystemUserDAO;
import us.mn.state.health.lims.systemuser.daoimpl.SystemUserDAOImpl;
import us.mn.state.health.lims.systemusersection.dao.SystemUserSectionDAO;
import us.mn.state.health.lims.systemusersection.daoimpl.SystemUserSectionDAOImpl;
import us.mn.state.health.lims.systemusersection.valueholder.SystemUserSection;

/**
 *  @author     Hung Nguyen (Hung.Nguyen@health.state.mn.us)
 */
public class SystemUserSectionAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String id = request.getParameter("ID");

		String forward = FWD_SUCCESS;
		request.setAttribute(IActionConstants.ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		DynaActionForm dynaForm = (DynaActionForm) form;

		// initialize the form
		dynaForm.initialize(mapping);

		SystemUserSection systemUserSection = new SystemUserSection();
		//bugzilla 2154
		LogEvent.logDebug("SystemUserSectionAction","performAction()","I am in SystemUserSectionAction and this is id " + id);
		
		if ((id != null) && (!"0".equals(id))) {
			systemUserSection.setId(id);
			SystemUserSectionDAO systemUserSectionDAO = new SystemUserSectionDAOImpl();
			systemUserSectionDAO.getData(systemUserSection);

			isNew = false;
			
			List systemUserSections = systemUserSectionDAO.getNextSystemUserSectionRecord(systemUserSection.getId());
			if (systemUserSections.size() > 0) {
				// enable next button
				request.setAttribute(NEXT_DISABLED, "false");
			}
			systemUserSections = systemUserSectionDAO.getPreviousSystemUserSectionRecord(systemUserSection.getId());
			if (systemUserSections.size() > 0) {
				// enable next button
				request.setAttribute(PREVIOUS_DISABLED, "false");
			}
			// end of logic to enable next or previous button


		} else {

			isNew = true; // this is to set correct page title

		}

		if (systemUserSection.getId() != null && !systemUserSection.getId().equals("0")) {
			request.setAttribute("ID", systemUserSection.getId());
		}

		// populate form from valueholder
		PropertyUtils.copyProperties(form, systemUserSection);

		SystemUserDAO systemUserDAO = new SystemUserDAOImpl();
		List systemUsers = systemUserDAO.getAllSystemUsers();

		//Get testsections by user system id
		//bugzilla 2160
		UserTestSectionDAO userTestSectionDAO = new UserTestSectionDAOImpl();
		List testSections = userTestSectionDAO.getAllUserTestSections(request);
		
		if ( systemUserSection.getSystemUser() != null )			
			PropertyUtils.setProperty(form, "systemUserId", systemUserSection.getSystemUser().getId());
		else
			PropertyUtils.setProperty(form, "systemUserId", "");
		if ( systemUserSection.getTestSection() != null )
			PropertyUtils.setProperty(form, "testSectionId", systemUserSection.getTestSection().getId());
		else
			PropertyUtils.setProperty(form, "testSectionId", "");
		
		PropertyUtils.setProperty(form, "systemusers", systemUsers);		
		PropertyUtils.setProperty(form, "testsections", testSections);
		
        //bugzilla 2154
		LogEvent.logDebug("SystemUserSectionAction","performAction()","I am in SystemUserSectionAction this is forward " + forward);		
		
		return mapping.findForward(forward);
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
