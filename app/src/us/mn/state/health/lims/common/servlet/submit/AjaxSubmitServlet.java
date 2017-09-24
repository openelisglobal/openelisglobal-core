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
package us.mn.state.health.lims.common.servlet.submit;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;

import us.mn.state.health.lims.common.provider.data.BaseDataProvider;
import us.mn.state.health.lims.common.provider.data.DataProviderFactory;
import us.mn.state.health.lims.common.servlet.data.AjaxServlet;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.login.dao.UserModuleDAO;
import us.mn.state.health.lims.login.daoimpl.UserModuleDAOImpl;
import us.mn.state.health.lims.security.SecureXmlHttpServletRequest;

/**
 * @author diane benz
 * bugzilla 2443
 */
public class AjaxSubmitServlet extends AjaxServlet {

	public void sendData(String field, String message,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
/*		if (!StringUtil.isNullorNill(field)
				&& !StringUtil.isNullorNill(message)) {
		}*/	response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.getWriter().write(message);
		

		
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		//check for authentication
		UserModuleDAO userModuleDAO = new UserModuleDAOImpl();
		if (userModuleDAO.isSessionExpired(request)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			System.out.println("Invalid request - no active session found");
			return;
		}
		Enumeration<String> params = request.getParameterNames();
		while(params.hasMoreElements()){
			 String paramName = params.nextElement();
			 System.out.println("Parameter Name - "+paramName+", Value - "+request.getParameter(paramName));
			}
		
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.getWriter().write("<message>response</message>");
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		//check for authentication
		UserModuleDAO userModuleDAO = new UserModuleDAOImpl();
		if (userModuleDAO.isSessionExpired(request)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			System.out.println("Invalid request - no active session found");
			return;
		}
		Enumeration<String> params = request.getParameterNames();
		while(params.hasMoreElements()){
			 String paramName = params.nextElement();
			 System.out.println("Parameter Name - "+paramName+", Value - "+request.getParameter(paramName));
		}
		
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.getWriter().write("<message></message>");
	}

}
