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

import java.util.Collections;
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
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.systemmodule.dao.SystemModuleDAO;
import us.mn.state.health.lims.systemmodule.daoimpl.SystemModuleDAOImpl;
import us.mn.state.health.lims.systemmodule.valueholder.SystemModuleComparator;
import us.mn.state.health.lims.systemuser.dao.SystemUserDAO;
import us.mn.state.health.lims.systemuser.daoimpl.SystemUserDAOImpl;
import us.mn.state.health.lims.systemusermodule.dao.PermissionAgentModuleDAO;
import us.mn.state.health.lims.systemusermodule.daoimpl.RoleModuleDAOImpl;
import us.mn.state.health.lims.systemusermodule.daoimpl.SystemUserModuleDAOImpl;
import us.mn.state.health.lims.systemusermodule.valueholder.PermissionModule;
import us.mn.state.health.lims.systemusermodule.valueholder.RoleModule;
import us.mn.state.health.lims.systemusermodule.valueholder.SystemUserModule;
import us.mn.state.health.lims.role.dao.RoleDAO;
import us.mn.state.health.lims.role.daoimpl.RoleDAOImpl;

/**
 *  @author     Hung Nguyen (Hung.Nguyen@health.state.mn.us)
 */
public class SystemUserModuleAction extends BaseAction {

	private boolean isNew = false;
	private boolean permissionAgentIsUser = true;
	
	@SuppressWarnings("unchecked")
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

		
		//bugzilla 2154
		LogEvent.logDebug("SystemUserModuleAction","performAction()"," I am in SystemUserModuleAction and this is id " + id);
		
		isNew = id == null || "0".equals(id);
		permissionAgentIsUser = SystemConfiguration.getInstance().getPermissionAgent().equals("USER");
		
		PermissionModule permissionAgentModule = permissionAgentIsUser ? new SystemUserModule() : new RoleModule();
		
		if ( !isNew ) {
			permissionAgentModule.setId(id);
			PermissionAgentModuleDAO permissionAgentModuleDAO = permissionAgentIsUser ? new SystemUserModuleDAOImpl() : new RoleModuleDAOImpl();
			permissionAgentModuleDAO.getData(permissionAgentModule);

			List permissionAgentModules = permissionAgentModuleDAO.getNextPermissionModuleRecord(permissionAgentModule.getId());
			if (permissionAgentModules.size() > 0) {
				// enable next button
				request.setAttribute(NEXT_DISABLED, "false");
			}
			permissionAgentModules = permissionAgentModuleDAO.getPreviousPermissionModuleRecord(permissionAgentModule.getId());
			if (permissionAgentModules.size() > 0) {
				// enable next button
				request.setAttribute(PREVIOUS_DISABLED, "false");
			}
			// end of logic to enable next or previous button
		}
		
		if (permissionAgentModule.getId() != null && !permissionAgentModule.getId().equals("0")) {
			request.setAttribute("ID", permissionAgentModule.getId());
		}

		// populate form from valueholder
		PropertyUtils.copyProperties(form, permissionAgentModule);

		List permissionAgents;
		
		if( permissionAgentIsUser){
			SystemUserDAO systemUserDAO =  new SystemUserDAOImpl();
			permissionAgents = systemUserDAO.getAllSystemUsers();
		}else{
			RoleDAO roleDAO = new RoleDAOImpl();
			permissionAgents = roleDAO.getAllRoles();
		}
			
		
		SystemModuleDAO systemModuleDAO = new SystemModuleDAOImpl();
		List systemModules = systemModuleDAO.getAllSystemModules();
		
		if ( permissionAgentModule.getPermissionAgent() != null )			
			PropertyUtils.setProperty(form, "systemUserId", permissionAgentModule.getPermissionAgent().getId());
		else
			PropertyUtils.setProperty(form, "systemUserId", "");
		if ( permissionAgentModule.getSystemModule() != null )
			PropertyUtils.setProperty(form, "systemModuleId", permissionAgentModule.getSystemModule().getId());
		else
			PropertyUtils.setProperty(form, "systemModuleId", "");
		
		Collections.sort(systemModules, SystemModuleComparator.DESC_COMPARATOR);
		PropertyUtils.setProperty(form, "systemusers", permissionAgents);		
		PropertyUtils.setProperty(form, "systemmodules", systemModules);
		
		//bugzilla 2154
		LogEvent.logDebug("SystemUserModuleAction","performAction()"," I am in SystemUserModuleAction this is forward " + forward);
		
		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		return getTitleKey();
	}

	protected String getPageSubtitleKey() {
		return getTitleKey();
	}
	
	private String getTitleKey(){
		if (isNew) {			
			return permissionAgentIsUser ? "systemusermodule.add.title" : "system.role.module.add.title";
		} else {
			return permissionAgentIsUser ? "systemusermodule.edit.title" : "system.role.module.edit.title";
		}
	}

}
