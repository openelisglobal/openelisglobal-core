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
package us.mn.state.health.lims.testanalyte.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.action.BaseMenuAction;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.login.dao.UserTestSectionDAO;
import us.mn.state.health.lims.login.daoimpl.UserTestSectionDAOImpl;
import us.mn.state.health.lims.method.dao.MethodDAO;
import us.mn.state.health.lims.method.daoimpl.MethodDAOImpl;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.testanalyte.dao.TestAnalyteTestResultDAO;
import us.mn.state.health.lims.testanalyte.daoimpl.TestAnalyteTestResultDAOImpl;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class TestAnalyteTestResultMenuAction extends BaseMenuAction {

	// override this from base class BaseMenuAction since we need to
	// initialize collections particular to this menuForm
	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// System.out.println("I am in BaseMenuAction performAction");
		String forward = "success";

		BaseActionForm dynaForm = (BaseActionForm) form;

		int action = -1;
		if (request.getParameter("paging") != null) {
			action = Integer.parseInt((String) request.getParameter("paging"));
		}
		List menuList = new ArrayList();

		try {
			switch (action) {
			case PREVIOUS:
				menuList = doPreviousPage(mapping, form, request, response);
				break;
			case NEXT:
				menuList = doNextPage(mapping, form, request, response);
				break;
			default:
				menuList = doNone(mapping, form, request, response);
			}
		} catch (Exception e) {
    		//bugzilla 2154
			LogEvent.logError("TestAnalyteTestResultMenuAction","performAction()",e.toString());
			forward = FWD_FAIL;
		}

		// First try to get collections from form submitted
		List testSections = new ArrayList();
		// if (dynaForm.get("testSections") != null) {
		// testSections = (List) dynaForm.get("testSections");
		// } else {
		//bugzilla 2160
		UserTestSectionDAO userTestSectionDAO = new UserTestSectionDAOImpl();
		testSections = userTestSectionDAO.getAllUserTestSections(request);
		// }

		List methods = new ArrayList();
		// if (dynaForm.get("methods") != null) {
		// methods = (List) dynaForm.get("methods");
		// } else {
		MethodDAO methodDAO = new MethodDAOImpl();
		methods = methodDAO.getAllMethods();
		// }

		List tests = new ArrayList();
		// if (dynaForm.get("tests") != null) {
		// tests = (List) dynaForm.get("tests");
		// } else {
		//bugzilla 2160
		//bugzilla 2291 added onlyTestsFullySetup
		tests = userTestSectionDAO.getAllUserTests(request, false);

		// }

		// get 3 drop down selections so we can repopulate
		String selectedTestSectionId = (String) dynaForm
				.get("selectedTestSectionId");
		String selectedMethodId = (String) dynaForm.get("selectedMethodId");
		String selectedTestId = (String) dynaForm.get("selectedTestId");

		// initialize the form
		dynaForm.initialize(mapping);

		// repopulate the form
		PropertyUtils.setProperty(dynaForm, "menuList", menuList);

		// set deactivate
		request.setAttribute(DEACTIVATE_DISABLED, getDeactivateDisabled());
		// initialize selectedIDs
		String[] selectedIDs = new String[5];

		PropertyUtils.setProperty(dynaForm, "selectedIDs", selectedIDs);

		// THIS IS REASON FOR OVERRIDING THIS METHOD -> REPOPULATE DROPDOWNS
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

	protected List createMenuList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

 		//System.out
		//		.println("I am in AnalyteTestResultMenuAction createMenuList()");

		List testAnalyteTestResults = new ArrayList();
		BaseActionForm dynaForm = (BaseActionForm) form;
		String selectedTestId = (String) dynaForm.get("selectedTestId");
		//System.out.println("createmenulist " + selectedTestId);
		if (!StringUtil.isNullorNill(selectedTestId)) {
			request.setAttribute(ALLOW_EDITS_KEY, "true");
			String stringStartingRecNo = (String) request
					.getAttribute("startingRecNo");
			int startingRecNo = Integer.parseInt(stringStartingRecNo);
			TestAnalyteTestResultDAO testAnalyteTestResultDAO = new TestAnalyteTestResultDAOImpl();
			//System.out.println("Going to get next page " + selectedTestId);
			Test test = new Test();
			test.setId(selectedTestId);
			TestDAO testDAO = new TestDAOImpl();
			testDAO.getData(test);

			// testAnalyteTestResults =
			// testAnalyteTestResultDAO.getAllTestAnalyteTestResultsPerTest(test);
			testAnalyteTestResults = testAnalyteTestResultDAO
					.getPageOfTestAnalyteTestResults(startingRecNo, test);

		} else {
			// this is to disallow ADD (new TestAnalyte/TestResult) without
			// having selected a test from dropdown
			request.setAttribute(ALLOW_EDITS_KEY, "false");
		}

		request.setAttribute("menuDefinition",
				"TestAnalyteTestResultMenuDefinition");

		return testAnalyteTestResults;

	}

	protected String getPageTitleKey() {
		return "testanalytetestresult.browse.title";
	}

	protected String getPageSubtitleKey() {
		return "testanalytetestresult.browse.title";
	}

	protected int getPageSize() {
		return SystemConfiguration.getInstance().getDefaultPageSize();
	}

	protected String getDeactivateDisabled() {
		return "true";
	}

}
