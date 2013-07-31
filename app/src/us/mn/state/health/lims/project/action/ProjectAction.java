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
package us.mn.state.health.lims.project.action;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.program.dao.ProgramDAO;
import us.mn.state.health.lims.program.daoimpl.ProgramDAOImpl;
import us.mn.state.health.lims.project.dao.ProjectDAO;
import us.mn.state.health.lims.project.daoimpl.ProjectDAOImpl;
import us.mn.state.health.lims.project.valueholder.Project;
import us.mn.state.health.lims.systemuser.dao.SystemUserDAO;
import us.mn.state.health.lims.systemuser.daoimpl.SystemUserDAOImpl;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class ProjectAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new Project.
		// If there is a parameter present, we should bring up an existing
		// Project to edit.

		String id = request.getParameter(ID);

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		DynaActionForm dynaForm = (DynaActionForm) form;

		// initialize the form
		dynaForm.initialize(mapping);

		Project project = new Project();
		//System.out.println("I am in ProjectAction and this is id " + id);
		if ((id != null) && (!"0".equals(id))) { // this is an existing
													// project

			project.setId(id);
			ProjectDAO projectDAO = new ProjectDAOImpl();
			projectDAO.getData(project);

			// initialize sysUserId
			if (project.getSystemUser() != null) {
				project.setSysUserId(project.getSystemUser().getId());
			}
			// initialize scriptlet
			if (project.getScriptlet() != null) {
				project.setScriptletName(project.getScriptlet().getScriptletName());
			}

			isNew = false; // this is to set correct page title
			
			// do we need to enable next or previous?
			//bugzilla 1427 pass in name not id
			List projects = projectDAO.getNextProjectRecord(project.getProjectName());
			if (projects.size() > 0) {
				// enable next button
				request.setAttribute(NEXT_DISABLED, "false");
			}
			//bugzilla 1427 pass in name not id
			projects = projectDAO.getPreviousProjectRecord(project.getProjectName());
			if (projects.size() > 0) {
				// enable next button
				request.setAttribute(PREVIOUS_DISABLED, "false");
			}
			// end of logic to enable next or previous button

		} else { // this is a new project
			// default started date to today's date
			Date today = Calendar.getInstance().getTime();
			Locale locale = (Locale) request.getSession().getAttribute(
					"org.apache.struts.action.LOCALE");

			String dateAsText = DateUtil.formatDateAsText(today, locale);

			project.setStartedDateForDisplay(dateAsText);

			// default isActive to 'Y'
			project.setIsActive("Y");

			isNew = true; // this is to set correct page title

		}

		if (project.getId() != null && !project.getId().equals("0")) {
			request.setAttribute(ID, project.getId());
		}

		// populate form from valueholder
		PropertyUtils.copyProperties(form, project);

		ProgramDAO programDAO = new ProgramDAOImpl();
		SystemUserDAO sysUserDAO = new SystemUserDAOImpl();

		List programs = programDAO.getAllPrograms();
		List sysUsers = sysUserDAO.getAllSystemUsers();

		PropertyUtils.setProperty(form, "programs", programs);
		PropertyUtils.setProperty(form, "sysUsers", sysUsers);

		//System.out.println("I am in ProjectAction this is forward " + forward);
		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "project.add.title";
		} else {
			return "project.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "project.add.title";
		} else {
			return "project.edit.title";
		}
	}

}
