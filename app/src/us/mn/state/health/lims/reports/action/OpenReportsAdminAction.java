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
package us.mn.state.health.lims.reports.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionRedirect;

import us.mn.state.health.lims.common.util.SystemConfiguration;

/**
 * @author diane benz
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class OpenReportsAdminAction extends OpenReportsBaseAction {

	private static final Object ADMIN_ACTION = "ReportAdmin";

	@Override
	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new Project.
		// If there is a parameter present, we should bring up an existing
		// Project to edit.

		if( form == null){
			request.setAttribute(ACTION_KEY, ADMIN_ACTION);
		}

		String action = request.getParameter("action");
		//String report = request.getParameter("report");

		//String reportPropertiesString = "openreports.report." + report;

		//String reportId = SystemConfiguration.getInstance().getOpenReportsReportId(reportPropertiesString);

		String password = getPasswordFor( WELL_KNOWN_REPORT_ADMIN);
		String group = "admin";

		String groupPropertiesString = "openreports.group." + group;

		String groupId = SystemConfiguration.getInstance().getOpenReportsGroupId(groupPropertiesString);

		setLoginCookie(response, WELL_KNOWN_REPORT_ADMIN, password);

		String forward = FWD_SUCCESS;

		ActionForward actionForward = mapping.findForward(forward);
		return getForward(actionForward, groupId);
	}

	@Override
	protected String getPageTitleKey() {
		return null;
	}

	@Override
	protected String getPageSubtitleKey() {
		return null;
	}

	protected ActionForward getForward(ActionForward forward, String groupId) {
		ActionRedirect redirect = new ActionRedirect(forward);
		//System.out.println("This is forward " + forward.getRedirect() + " "
				//+ forward.getPath());

		//these are parameters needed by org.efs.openreports.actions.LoginAction

		if (groupId != null) {
			redirect.addParameter("groupId", groupId);
		}


		//System.out.println("This is redirect " + redirect.getPath());
		return redirect;
	}

	@Override
	protected Object getReportAction() {
		// no-op
		return null;
	}

}
