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
package us.mn.state.health.lims.reports.send.sample.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.security.IAuthorizationActionConstants;
import us.mn.state.health.lims.login.dao.UserTestSectionDAO;
import us.mn.state.health.lims.login.daoimpl.UserTestSectionDAOImpl;
import us.mn.state.health.lims.method.dao.MethodDAO;
import us.mn.state.health.lims.method.daoimpl.MethodDAOImpl;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class SampleXMLByTestAction extends BaseAction {

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String forward = FWD_SUCCESS;
		BaseActionForm dynaForm = (BaseActionForm) form;

		request.setAttribute(ALLOW_EDITS_KEY, "false");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");
		request.setAttribute(
				IAuthorizationActionConstants.UPDATE_TESTCOMPONENT_TESTRESULT,
				"true");

		// CHANGED
		// String selectedTestId = (String) request.getParameter("Test");

		List methods = new ArrayList();
		MethodDAO methodDAO = new MethodDAOImpl();
		methods = methodDAO.getAllMethods();

		//Get tests/testsections by user system id
		//bugzilla 2160
		UserTestSectionDAO userTestSectionDAO = new UserTestSectionDAOImpl();
		List testSections = userTestSectionDAO.getAllUserTestSections(request);
		//bugzilla 2291
		List tests = userTestSectionDAO.getAllUserTests(request, true);			
		
		// get 3 drop down selections so we can repopulate
		String selectedTestSectionId = (String) dynaForm
				.get("selectedTestSectionId");
		String selectedMethodId = (String) dynaForm.get("selectedMethodId");
		String selectedTestId = (String) dynaForm.get("selectedTestId");
		// CHANGED

		// initialize the form
		dynaForm.initialize(mapping);
		
	
		PropertyUtils.setProperty(dynaForm, "testSections", testSections);
		PropertyUtils.setProperty(dynaForm, "methods", methods);
		PropertyUtils.setProperty(dynaForm, "tests", tests);

		PropertyUtils.setProperty(dynaForm, "selectedTestSectionId",
				selectedTestSectionId);
		PropertyUtils.setProperty(dynaForm, "selectedMethodId",
				selectedMethodId);
		PropertyUtils.setProperty(dynaForm, "selectedTestId", selectedTestId);

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		return "report.sample.xml.by.test.title";
	}

	protected String getPageSubtitleKey() {
		return "report.sample.xml.by.test.subtitle";
	}


}
