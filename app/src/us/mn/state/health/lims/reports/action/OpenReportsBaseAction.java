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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionRedirect;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.hibernate.HibernateUtil;

/**
 * @author diane benz
 *
 *         To change this generated comment edit the template variable
 *         "typecomment": Window>Preferences>Java>Templates. To enable and
 *         disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
public abstract class OpenReportsBaseAction extends BaseAction {

	protected static final String WELL_KNOWN_REPORT_USER = "Benzd1";
	protected static final String WELL_KNOWN_REPORT_ADMIN = "admin";

	protected abstract String getPageTitleKey();

	protected abstract String getPageSubtitleKey();

	protected abstract Object getReportAction();


	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new Project.
		// If there is a parameter present, we should bring up an existing
		// Project to edit.

		if( form == null){
			request.setAttribute(ACTION_KEY, getReportAction());
		}

		String report = request.getParameter("report");

		Map<String, String[]> paramMap = request.getParameterMap();

		HashMap<String,String> parameters = new HashMap<String,String>();

		Set<String> keySet = paramMap.keySet();

		Collection<String[]> values = paramMap.values();

		Iterator<String> itKey = keySet.iterator();

		List<String> keyList = new ArrayList<String>();

		while (itKey.hasNext()) {
			keyList.add(itKey.next());
		}

		Iterator<String[]> itVal = values.iterator();

		List<String> valList = new ArrayList<String>();

		while (itVal.hasNext()) {
			String[] vals = itVal.next();
			valList.add(vals[0]);
		}

		for (int i = 0; i < keyList.size(); i++) {
			if (!keyList.get(i).equals("action")
				&& !keyList.get(i).equals("report")) {
				parameters.put(keyList.get(i), valList.get(i));
			}
		}

		String reportPropertiesString = "openreports.report." + report;

		String reportId = SystemConfiguration.getInstance()
				.getOpenReportsReportId(reportPropertiesString);


		String password = getPasswordFor(WELL_KNOWN_REPORT_USER);
		String group = "user";

		String groupPropertiesString = "openreports.group." + group;

		String groupId = SystemConfiguration.getInstance().getOpenReportsGroupId(groupPropertiesString);

		setLoginCookie(response, WELL_KNOWN_REPORT_USER, password);

		String forward = FWD_SUCCESS;

		ActionForward actionForward = mapping.findForward(forward);

    	return getForward(actionForward, reportId, groupId, parameters);

	}

	private ActionForward getForward(ActionForward forward,
									 String reportId,
									 String groupId,
									 Map<String,String> parameterMap) {
		ActionRedirect redirect = new ActionRedirect(forward);

		// these are parameters needed by
		// org.efs.openreports.actions.LimsReportDetailAction
		if (reportId != null) {
			redirect.addParameter("reportId", reportId);
		}

		if (groupId != null) {
			redirect.addParameter("groupId", groupId);
		}

		if (parameterMap != null && parameterMap.size() > 0) {
			Set<String> keySet = parameterMap.keySet();
			Iterator<String> it = keySet.iterator();
			while (it.hasNext()) {
				String key = it.next();
				redirect.addParameter(key, parameterMap.get(key));
			}
		}

		addAdditionalReportParams(redirect);

		return redirect;

	}

	protected void addAdditionalReportParams(ActionRedirect redirect) {
		//default is no-op
	}

	protected void setLoginCookie(HttpServletResponse response, String user, String password) {

		Cookie nameCookie = new Cookie("userName", user);
		nameCookie.setPath("/");
		// bugzilla 2225 commented out this next line!(login with
		// apache/linux/tomcat/IE not working)
		nameCookie.setMaxAge(60);
		response.addCookie(nameCookie);

		Cookie passwordCookie = new Cookie("password", password);
		passwordCookie.setPath("/");
		// bugzilla 2225 commented out this next line!(login with
		// apache/linux/tomcat/IE not working)

		passwordCookie.setMaxAge(60);
		response.addCookie(passwordCookie);
	}

	protected String getPasswordFor(String userName) {

		Session session = HibernateUtil.getSession();

		try {

			String sql = "select password from report_user where name = :userName";

			Query query = session.createSQLQuery(sql);
			query.setString("userName", userName);
			String pw = (String) query.uniqueResult();

			return pw;
		} catch (HibernateException he) {
			throw he;
		}

	}

}
