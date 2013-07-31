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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.common.action.BaseMenuAction;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.systemusermodule.dao.PermissionAgentModuleDAO;
import us.mn.state.health.lims.systemusermodule.daoimpl.RoleModuleDAOImpl;
import us.mn.state.health.lims.systemusermodule.daoimpl.SystemUserModuleDAOImpl;
import us.mn.state.health.lims.systemusermodule.valueholder.RoleModule;
import us.mn.state.health.lims.systemusermodule.valueholder.SystemUserModule;

/**
 *  @author     Hung Nguyen (Hung.Nguyen@health.state.mn.us)
 */
public class SystemUserModuleMenuAction extends BaseMenuAction {

	private String permissionAgent = "USER";
	
	protected List createMenuList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
				
		permissionAgent = SystemConfiguration.getInstance().getPermissionAgent();
		
    	//bugzilla 2154
    	LogEvent.logDebug("SystemUserModuleMenuAction","createMenuList()","I am in SystemUserModuleMenuAction createMenuList()");
		List systemUserModules = new ArrayList();

		String stringStartingRecNo = (String) request.getAttribute("startingRecNo");
		int startingRecNo = Integer.parseInt(stringStartingRecNo);

		PermissionAgentModuleDAO systemUserModuleDAO = null;//
		
		if( permissionAgent.equals("USER")){
			systemUserModuleDAO = new SystemUserModuleDAOImpl();
		}else { //equals("ROLE")
			systemUserModuleDAO = new RoleModuleDAOImpl();
		}

		
		
		systemUserModules = systemUserModuleDAO.getPageOfPermissionModules(startingRecNo);
		
		request.setAttribute("menuDefinition", "SystemUserModuleMenuDefinition");
		
		if( permissionAgent.equals("USER")){
			setDisplayPageBounds(request, systemUserModules.size(), startingRecNo, systemUserModuleDAO, SystemUserModule.class);
		}else { //equals("ROLE")
			setDisplayPageBounds(request, systemUserModules.size(), startingRecNo, systemUserModuleDAO, RoleModule.class);
		}
		
	
		return systemUserModules;
	}

	protected String getPageTitleKey() {
		//redo if there are more than two choices
		return permissionAgent.equals("USER") ? "systemusermodule.browse.title" : "system.role.module.browse.title";
	}

	protected String getPageSubtitleKey() {
		//redo if there are more than two choices
		return permissionAgent.equals("USER") ? "systemusermodule.browse.title" : "system.role.module.browse.title";
	}

	protected int getPageSize() {
		return SystemConfiguration.getInstance().getDefaultPageSize();
	}

	protected String getDeactivateDisabled() {
	//bugzilla 2206 
		return permissionAgent.equals("USER")? "true" :  "false";
	}

}
