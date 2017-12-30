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
package us.mn.state.health.lims.common.provider.autocomplete;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.project.dao.ProjectDAO;
import us.mn.state.health.lims.project.daoimpl.ProjectDAOImpl;

/**
 * @author benzd1
 * ProjectAutocompleteProvider: gets ONLY active projects
 */
public class ProjectAutocompleteProvider extends BaseAutocompleteProvider {

	/**
	 * @see org.ajaxtags.demo.servlet.BaseAjaxServlet#getXmlContent(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public List processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String projectName = request.getParameter("projectName");
		ProjectDAO projectDAO = new ProjectDAOImpl();
		//bugzilla 1978 this only gets the active projects
		List list = projectDAO.getProjects(projectName, true);
		
		//bugzilla 2154
		LogEvent.logDebug("BaseMenuAction","processRequest()","I am in ProjectAutocompleteProvider ProjectName: " + projectName + " List size: " + list.size());

		return list;
	}

}
