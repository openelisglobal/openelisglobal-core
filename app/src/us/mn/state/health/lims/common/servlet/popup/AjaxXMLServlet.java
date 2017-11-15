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
package us.mn.state.health.lims.common.servlet.popup;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.provider.popup.BasePopupProvider;
import us.mn.state.health.lims.common.provider.popup.PopupProviderFactory;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.login.dao.UserModuleDAO;
import us.mn.state.health.lims.login.daoimpl.UserModuleDAOImpl;

public class AjaxXMLServlet extends AjaxServlet {

	private BasePopupProvider popupProvider = null;

	public void sendData(List list,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		if (list != null && list.size() > 0) {

			response.setContentType("text/xml");
			response.setHeader("Cache-Control", "no-cache");
			response.getWriter().write("<popupData>");
			response.getWriter().write("</popupData>");
		} else {
			//System.out.println("Returning no content with field " + field	+ " message  " + message);
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		}
	}
	
	public void sendData(String string1, String string2,
			HttpServletRequest request,  HttpServletResponse response)
			throws IOException, ServletException {

		if (!StringUtil.isNullorNill(string1)) {

			response.setContentType("text/xml");
			response.setHeader("Cache-Control", "no-cache");
			response.getWriter().write("<popupData>");
			response.getWriter().write("</popupData>");
		} else {
			//System.out.println("Returning no content with field " + field	+ " message  " + message);
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException, LIMSRuntimeException {
		//check for authentication
		UserModuleDAO userModuleDAO = new UserModuleDAOImpl();
		if (userModuleDAO.isSessionExpired(request)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("text/html; charset=utf-8");
			response.getWriter().println(StringUtil.getMessageForKey("message.error.unauthorized"));
			return;
		}

		String popupProvider = request.getParameter("provider");
		BasePopupProvider provider = (BasePopupProvider) PopupProviderFactory
				.getInstance().getPopupProvider(popupProvider);
		provider.setServlet(this);
		provider.processRequest(request, response);
	}

}
