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
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.util.resources.ResourceLocator;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.role.dao.RoleDAO;
import us.mn.state.health.lims.role.daoimpl.RoleDAOImpl;
import us.mn.state.health.lims.role.valueholder.Role;
import us.mn.state.health.lims.systemmodule.dao.SystemModuleDAO;
import us.mn.state.health.lims.systemmodule.daoimpl.SystemModuleDAOImpl;
import us.mn.state.health.lims.systemmodule.valueholder.SystemModule;
import us.mn.state.health.lims.systemuser.dao.SystemUserDAO;
import us.mn.state.health.lims.systemuser.daoimpl.SystemUserDAOImpl;
import us.mn.state.health.lims.systemuser.valueholder.SystemUser;
import us.mn.state.health.lims.systemusermodule.dao.PermissionAgentModuleDAO;
import us.mn.state.health.lims.systemusermodule.daoimpl.RoleModuleDAOImpl;
import us.mn.state.health.lims.systemusermodule.daoimpl.SystemUserModuleDAOImpl;
import us.mn.state.health.lims.systemusermodule.valueholder.PermissionAgent;
import us.mn.state.health.lims.systemusermodule.valueholder.PermissionModule;
import us.mn.state.health.lims.systemusermodule.valueholder.RoleModule;
import us.mn.state.health.lims.systemusermodule.valueholder.SystemUserModule;

/**
 *  @author     Hung Nguyen (Hung.Nguyen@health.state.mn.us)
 */
public class SystemUserModuleUpdateAction extends BaseAction {

	private boolean isNew = false;
	private boolean permissionAgentIsUser = true;
	
	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(IActionConstants.ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "false");
		request.setAttribute(NEXT_DISABLED, "false");

		String id = request.getParameter("ID");
		
		String sysUserId = getSysUserId(request);
		
		isNew = StringUtil.isNullorNill(id) || "0".equals(id);
		permissionAgentIsUser = SystemConfiguration.getInstance().getPermissionAgent().equals("USER");
		
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

		PermissionModule permissionAgentModule = permissionAgentIsUser ? new SystemUserModule() : new RoleModule();
		
		permissionAgentModule.setSysUserId(sysUserId);
		
		// populate valueholder from form
		PropertyUtils.copyProperties(permissionAgentModule, dynaForm);
		
		String systemUserId = (String) dynaForm.get("systemUserId");
		PermissionAgent permissionAgent = getPermissionAgent(dynaForm, systemUserId);

		String systemModuleId = (String) dynaForm.get("systemModuleId");
		SystemModule systemModule = getSystemModule(dynaForm, systemModuleId);
	
		permissionAgentModule.setPermissionAgent(permissionAgent);
		permissionAgentModule.setSystemModule(systemModule);

		org.hibernate.Transaction tx = HibernateUtil.getSession().beginTransaction();	
		
		try {
			PermissionAgentModuleDAO permissionAgentModuleDAO = permissionAgentIsUser ? new SystemUserModuleDAOImpl() : new RoleModuleDAOImpl();

			if (!isNew) {
				// UPDATE
				permissionAgentModuleDAO.updateData(permissionAgentModule);
			} else {
				// INSERT
				permissionAgentModuleDAO.insertData(permissionAgentModule);
			}
			tx.commit();
		} catch (LIMSRuntimeException lre) {
    		//bugzilla 2154
			LogEvent.logError("SystemUserModuleUpdateAction","performAction()",lre.toString());
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
		PropertyUtils.copyProperties(dynaForm, permissionAgentModule);

		if ("true".equalsIgnoreCase(request.getParameter("close"))) {
			forward = FWD_CLOSE;
		}

		if (permissionAgentModule.getId() != null && !permissionAgentModule.getId().equals("0")) {
			request.setAttribute("ID", permissionAgentModule.getId());

		}

		if (isNew) forward = FWD_SUCCESS_INSERT;
		return getForward(mapping.findForward(forward), id, start, direction);

	}


	private SystemModule getSystemModule(BaseActionForm dynaForm, String systemModuleId) {
		List systemModules = new ArrayList();
		if (dynaForm.get("systemmodules") != null) {
			systemModules = (List) dynaForm.get("systemmodules");
		} else {
			SystemModuleDAO systemModuleDAO = new SystemModuleDAOImpl();
			systemModules = systemModuleDAO.getAllSystemModules();
		}
		
		SystemModule systemModule = null;
		for (int i = 0; i < systemModules.size(); i++) {
			SystemModule sysModule = (SystemModule) systemModules.get(i);
			if (sysModule.getId().equals(systemModuleId)) {
				systemModule = sysModule;
				break;
			}
		}
		return systemModule;
	}

	
	@SuppressWarnings("unchecked")
	private PermissionAgent getPermissionAgent(BaseActionForm dynaForm, String agentId) {
		//the intent of this code is not clear, either we are getting the list of potential matches from the
		//form or the DB.  Not sure of why one over the other.
		
		PermissionAgent permissionAgent = null;
		
		List<PermissionAgent> permissionAgents = new ArrayList();
		if (dynaForm.get("systemusers") != null) {
			permissionAgents = (List<PermissionAgent>) dynaForm.get("systemusers");
			
			for( PermissionAgent agent : permissionAgents){
				if( agent.getId().equals(agentId)){
					permissionAgent = agent;
					break;
				}
			}			
		} else {
			if( permissionAgentIsUser ){
				SystemUserDAO systemUserDAO = new SystemUserDAOImpl();
				SystemUser user = new SystemUser();
				user.setId(agentId);
				systemUserDAO.getData(user);
				permissionAgent = user;
			}else {
				RoleDAO roleDAO = new RoleDAOImpl();
				Role role = new Role();
				role.setId(agentId);
				roleDAO.getData(role);
				permissionAgent = role;
			}
				
		}
		
		return permissionAgent;
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "systemusermodule.add.title";
		} else {
			return "systemusermodule.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "systemusermodule.add.title";
		} else {
			return "systemusermodule.edit.title";
		}
	}
}