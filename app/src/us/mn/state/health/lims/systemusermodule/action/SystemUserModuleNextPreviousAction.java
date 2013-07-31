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
 * 
 * Contributor(s): CIRG, University of Washington, Seattle WA.
 */
package us.mn.state.health.lims.systemusermodule.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.systemusermodule.dao.PermissionAgentModuleDAO;
import us.mn.state.health.lims.systemusermodule.daoimpl.RoleModuleDAOImpl;
import us.mn.state.health.lims.systemusermodule.daoimpl.SystemUserModuleDAOImpl;
import us.mn.state.health.lims.systemusermodule.valueholder.PermissionModule;
import us.mn.state.health.lims.systemusermodule.valueholder.RoleModule;
import us.mn.state.health.lims.systemusermodule.valueholder.SystemUserModule;

/**
 * @author Hung Nguyen (Hung.Nguyen@health.state.mn.us)
 */
public class SystemUserModuleNextPreviousAction extends BaseAction {

	protected ActionForward performAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(IActionConstants.ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "false");
		request.setAttribute(NEXT_DISABLED, "false");

		String id = request.getParameter("ID");

		boolean permissionAgentIsUser = SystemConfiguration.getInstance().getPermissionAgent().equals("USER");

		String start = (String) request.getParameter("startingRecNo");
		String direction = (String) request.getParameter("direction");

		PermissionModule permissionAgentModule = permissionAgentIsUser ? new SystemUserModule() : new RoleModule();

		permissionAgentModule.setId(id);
		try {

			PermissionAgentModuleDAO permissionAgentModuleDAO = permissionAgentIsUser ? new SystemUserModuleDAOImpl()
					: new RoleModuleDAOImpl();
			permissionAgentModuleDAO.getData(permissionAgentModule);

			if (FWD_NEXT.equals(direction)) {
				List permissionModules = permissionAgentModuleDAO.getNextPermissionModuleRecord(permissionAgentModule.getId());

				if (permissionModules != null && permissionModules.size() > 0) {
					permissionAgentModule = (PermissionModule) permissionModules.get(0);
					permissionAgentModuleDAO.getData(permissionAgentModule);
					if (permissionModules.size() < 2) {
						request.setAttribute(NEXT_DISABLED, "true");
					}
					id = permissionAgentModule.getId();
				} else {
					request.setAttribute(NEXT_DISABLED, "true");
				}
			}

			if (FWD_PREVIOUS.equals(direction)) {
				List permissionAgentModules = permissionAgentModuleDAO.getPreviousPermissionModuleRecord(permissionAgentModule.getId());
				
				if (permissionAgentModules != null && permissionAgentModules.size() > 0) {
					permissionAgentModule = (PermissionModule) permissionAgentModules.get(0);
					permissionAgentModuleDAO.getData(permissionAgentModule);
					if (permissionAgentModules.size() < 2) {
						request.setAttribute(PREVIOUS_DISABLED, "true");
					}
					id = permissionAgentModule.getId();
				} else {
					request.setAttribute(PREVIOUS_DISABLED, "true");
				}
			}

		} catch (LIMSRuntimeException lre) {
			// bugzilla 2154
			LogEvent.logError("SystemUserModuleNextPreviousAction", "performAction()", lre.toString());
			request.setAttribute(IActionConstants.ALLOW_EDITS_KEY, "false");
			request.setAttribute(PREVIOUS_DISABLED, "true");
			request.setAttribute(NEXT_DISABLED, "true");
			forward = FWD_FAIL;
		}
		if (forward.equals(FWD_FAIL))
			return mapping.findForward(forward);

		if (permissionAgentModule.getId() != null && !permissionAgentModule.getId().equals("0")) {
			request.setAttribute("ID", permissionAgentModule.getId());

		}

		return getForward(mapping.findForward(forward), id, start);

	}

	protected String getPageTitleKey() {
		return null;
	}

	protected String getPageSubtitleKey() {
		return null;
	}

}