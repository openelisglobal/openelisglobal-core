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
package us.mn.state.health.lims.common.provider.validation;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.servlet.validation.AjaxServlet;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.project.dao.ProjectDAO;
import us.mn.state.health.lims.project.daoimpl.ProjectDAOImpl;
import us.mn.state.health.lims.project.valueholder.Project;

/**
 * @author benzd1
 * bugzilla 1978 BasicProjectIdOrNameValidationProvider: allows active AND inactive projects
 */
public class BasicProjectIdOrNameValidationProvider extends BaseValidationProvider {

	public BasicProjectIdOrNameValidationProvider() {
		super();
	}

	public BasicProjectIdOrNameValidationProvider(AjaxServlet ajaxServlet) {
		this.ajaxServlet = ajaxServlet;
	}

	public void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// get id from request
		String targetId = (String) request.getParameter("id");
		String formField = (String) request.getParameter("field");
		String result = validate(targetId);
		ajaxServlet.sendData(formField, result, request, response);
	}

	public String validate(String targetId) throws LIMSRuntimeException {
		StringBuffer s = new StringBuffer();

		if (!StringUtil.isNullorNill(targetId)) {
			ProjectDAO projectDAO = new ProjectDAOImpl();
			Project project = new Project();
			try {
				int i = Integer.parseInt(targetId);
				//bugzilla 2438
				project.setLocalAbbreviation(targetId.trim());

			} catch (NumberFormatException nfe) {
                //bugzilla 2154
			    LogEvent.logError("BasicProjectIdOrNameValidationProvider","validate()",nfe.toString());
				//if the id was not a number
				project = null;
			}

			if (project != null) {
				//bugzilla 2438
				project = projectDAO.getProjectByLocalAbbreviation(project, true);
			}
			if (project != null) {				
				
				if(null != project.getProjectName()){
					s.append(VALID);
					s.append(project.getProjectName());
				}else
					s.append(INVALID);
				
			} else {
				project = new Project();
				project.setId("");
				project.setProjectName(targetId.trim());
				project = projectDAO.getProjectByName(project, true, false);
				if (project != null) {
					s.append(VALID);
					//bugzilla 2438
					s.append(project.getLocalAbbreviation());
				} else {
					s.append(INVALID);
				}
			}
		} else {
			s.append(VALID);
		}


		return s.toString();

	}

}
