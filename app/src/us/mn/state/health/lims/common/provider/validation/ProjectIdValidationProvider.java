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
import us.mn.state.health.lims.project.dao.ProjectDAO;
import us.mn.state.health.lims.project.daoimpl.ProjectDAOImpl;
import us.mn.state.health.lims.project.valueholder.Project;
/**
 * @author benzd1
 * bugzilla 1978 ProjectIdValidationProvider: only allows active projects
 */
public class ProjectIdValidationProvider extends BaseValidationProvider {

	public ProjectIdValidationProvider() {
		super();
	}

	public ProjectIdValidationProvider(AjaxServlet ajaxServlet) {
		this.ajaxServlet = ajaxServlet;
	}

	public void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// get id from request
		String targetId = (String) request.getParameter("id");
		String formField = (String) request.getParameter("field");
		String result = validate(targetId);
		//System.out.println("This is field being validated " + formField);
		ajaxServlet.sendData(formField, result, request, response);
	}

	// modified for efficiency bugzilla 1367
	public String validate(String targetId) throws LIMSRuntimeException {
		StringBuffer s = new StringBuffer();

		if (targetId != null) {
			ProjectDAO projectDAO = new ProjectDAOImpl();
			Project project = new Project();
			//bugzilla 2438
			project.setLocalAbbreviation(targetId.trim());
			project = projectDAO.getProjectByLocalAbbreviation(project, true);
			if (project != null) {
				//bugzilla 1978
				if (project.getIsActive().equals(YES)) {
					s.append(VALID);
					// This is particular to projId validation for HSE1 and HSE2:
					// the message appended to VALID is the projectName that
					// can then be displayed when User enters valid Project Id
					// (see humanSampleOne.jsp, humanSampleTwo.jsp)
					
					s.append(project.getProjectName());
				} else {
					s.append(INVALID);
				}
				
			} else {
				s.append(INVALID);
			}
		} else {
			//bugzilla 1697
			s.append(VALID);
		}

		/*
		 * System.out.println("I am in projIdValidator returning " +
		 * s.toString() + " targetId " + targetId);
		 */
		return s.toString();
	}

}
