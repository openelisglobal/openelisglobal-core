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
package us.mn.state.health.lims.common.provider.validation;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import us.mn.state.health.lims.common.servlet.validation.AjaxServlet;
import us.mn.state.health.lims.login.dao.LoginDAO;
import us.mn.state.health.lims.login.daoimpl.LoginDAOImpl;
import us.mn.state.health.lims.login.valueholder.Login;
import us.mn.state.health.lims.systemuser.dao.SystemUserDAO;
import us.mn.state.health.lims.systemuser.daoimpl.SystemUserDAOImpl;
import us.mn.state.health.lims.systemuser.valueholder.SystemUser;
import us.mn.state.health.lims.systemusermodule.dao.PermissionAgentModuleDAO;
import us.mn.state.health.lims.systemusermodule.daoimpl.PermissionAgentFactory;

public class UserValidationProvider extends BaseValidationProvider {

	public static final String TECHICIAN = "technician";
	
	public UserValidationProvider() {
		super();
	}

	public UserValidationProvider(AjaxServlet ajaxServlet) {
		this.ajaxServlet = ajaxServlet;
	}

	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {

		String userName = request.getParameter("userName");
		String password = request.getParameter("password");
		String passwordReq = request.getParameter("passwordReq");
		String role = request.getParameter("role");
		String formField = request.getParameter("field");
		

		String result = null; 
		
		if( "true".equals( passwordReq )){
			result = validateUserPasswordAndRole(userName, password, role);
		}else{
			result = validateUserForTaskRole(userName, role);
		}
		ajaxServlet.sendData(formField, result, request, response);
	}

	public String validateUserPasswordAndRole(String userLoginName, String password, String role) {
		String result = validateUserForTaskRole(userLoginName, role);
		
		if( result == VALID){
			Login login = new Login();
			login.setLoginName(userLoginName);
			login.setPassword(password);
						
			LoginDAO loginDAO = new LoginDAOImpl();
			result = loginDAO.getValidateLogin(login) == null ? INVALID : VALID;
		}
		
		return result;
	}

	public String validateUserForTaskRole( String userLoginName, String taskRole){
		SystemUserDAO systemUserDAO = new SystemUserDAOImpl();
		
		SystemUser user = systemUserDAO.getDataForLoginUser(userLoginName);
		
		if( user == null){
			return INVALID;
		}
		
		if( TECHICIAN.equals(taskRole)){
			return VALID;
		}
		
		//must be supervisor
		PermissionAgentModuleDAO sumDAO = PermissionAgentFactory.getPermissionAgentImpl();
		
		return sumDAO.isAgentAllowedAccordingToName(user.getId(), PermissionAgentModuleDAO.SUPERVISOR) ? VALID : INVALID;
	}
	
}
