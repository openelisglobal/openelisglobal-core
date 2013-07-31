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
 * bugzilla 1978 ProjectIdOrNameValidationProvider: only allows active projects
 */
public class ProjectIdOrNameValidationProvider extends BaseValidationProvider {

	public ProjectIdOrNameValidationProvider() {
		super();
	}

	public ProjectIdOrNameValidationProvider(AjaxServlet ajaxServlet) {
		this.ajaxServlet = ajaxServlet;
	}

	public void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// get id from request
		String targetId = (String) request.getParameter("id");
		String formField = (String) request.getParameter("field");
		String result = validate(targetId);
		// System.out.println("This is field being validated " + formField);
		ajaxServlet.sendData(formField, result, request, response);
	}

	// modified for efficiency bugzilla 1367
	public String validate(String targetId) throws LIMSRuntimeException {
		StringBuffer s = new StringBuffer();

		//bgm added StringUtil to check against a space being entered.
		if (!StringUtil.isNullorNill(targetId)) {
			ProjectDAO projectDAO = new ProjectDAOImpl();
			Project project = new Project();
			try {
				int i = Integer.parseInt(targetId);
				//bugzilla 2438
				project.setLocalAbbreviation(targetId.trim());

			} catch (NumberFormatException nfe) {
                //bugzilla 2154
				LogEvent.logError("ProjectIdOrNameValidationProvider","validate()",nfe.toString());   			
				//if the id was not a number
				project = null;
			}

			if (project != null) {
				//bugzilla 2438
				project = projectDAO.getProjectByLocalAbbreviation(project, true);
			}
			//bugzilla 2112
			try {
				if (null != project) {
					//bugzilla 1978			
					if (project.getIsActive().equals(YES)) {
						// This is particular to projId validation for HSE1 and HSE2:
						// the message appended to VALID is the projectName that
						// can then be displayed when User enters valid Project Id
						// (see humanSampleOne.jsp, humanSampleTwo.jsp)
					
						//bgm - bugzilla 1535 commented out s.append(project.getProjectName()); to check first
						if(null != project.getProjectName()){
							s.append(VALID);
							s.append(project.getProjectName());
						}else
							s.append(INVALID);
					} else {
						s.append(INVALID);
					}				
				} else {
					project = new Project();
					project.setId("");
					project.setProjectName(targetId.trim());
					project = projectDAO.getProjectByName(project, true, true);
					if (project != null) {
						s.append(VALID);
						//bugzilla 2438
						s.append(project.getLocalAbbreviation());
					} else {
						s.append(INVALID);
					}
				}
			} catch (Exception e) {
                //bugzilla 2154
			    LogEvent.logError("ProjectIdOrNameValidationProvider","validate()",e.toString());
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
