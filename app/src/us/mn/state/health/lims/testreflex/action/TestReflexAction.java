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
package us.mn.state.health.lims.testreflex.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.login.dao.UserTestSectionDAO;
import us.mn.state.health.lims.login.daoimpl.UserTestSectionDAOImpl;
import us.mn.state.health.lims.scriptlet.dao.ScriptletDAO;
import us.mn.state.health.lims.scriptlet.daoimpl.ScriptletDAOImpl;
import us.mn.state.health.lims.scriptlet.valueholder.Scriptlet;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.TestComparator;
import us.mn.state.health.lims.testanalyte.dao.TestAnalyteDAO;
import us.mn.state.health.lims.testanalyte.daoimpl.TestAnalyteDAOImpl;
import us.mn.state.health.lims.testanalyte.valueholder.TestAnalyteComparator;
import us.mn.state.health.lims.testreflex.dao.TestReflexDAO;
import us.mn.state.health.lims.testreflex.daoimpl.TestReflexDAOImpl;
import us.mn.state.health.lims.testreflex.valueholder.TestReflex;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;
import us.mn.state.health.lims.testresult.valueholder.TestResultComparator;

/**
 * @author diane benz
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class TestReflexAction extends BaseAction {

	private boolean isNew = false;

	@SuppressWarnings("unchecked")
	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new TestReflex.
		// If there is a parameter present, we should bring up an existing
		// TestReflex to edit.
		String id = request.getParameter(ID);

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		DynaActionForm dynaForm = (DynaActionForm) form;

		// initialize the form
		dynaForm.initialize(mapping);
		isNew = id == null || id.equals("0");

		TestReflex testReflex = new TestReflex();

		if (!isNew) {

			testReflex.setId(id);
			TestReflexDAO testReflexDAO = new TestReflexDAOImpl();
			testReflexDAO.getData(testReflex);

			// initialize testResultId & testAnalyteId
			// initialize testId & addedTestId
			if (testReflex.getTestResult() != null) {
				testReflex.setTestResultId(testReflex.getTestResult().getId());
			}

			if (testReflex.getTestAnalyte() != null) {
				testReflex
						.setTestAnalyteId(testReflex.getTestAnalyte().getId());
			}

			if (testReflex.getTest() != null) {
				testReflex.setTestId(testReflex.getTest().getId());
			}

			if (testReflex.getAddedTest() != null) {
				testReflex.setAddedTestId(testReflex.getAddedTest().getId());
			}

			// do we need to enable next or previous?
			List testReflexs = testReflexDAO.getNextTestReflexRecord(testReflex
					.getId());
			if (testReflexs.size() > 0) {
				// enable next button
				request.setAttribute(NEXT_DISABLED, "false");
			}
			testReflexs = testReflexDAO.getPreviousTestReflexRecord(testReflex
					.getId());
			if (testReflexs.size() > 0) {
				// enable next button
				request.setAttribute(PREVIOUS_DISABLED, "false");
			}
			// end of logic to enable next or previous button
		}

		if (testReflex.getId() != null && !testReflex.getId().equals("0")) {
			request.setAttribute(ID, testReflex.getId());
		}

		// populate form from valueholder
		PropertyUtils.copyProperties(form, testReflex);

		TestDAO testDAO = new TestDAOImpl();
		TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();
		TestResultDAO testResultDAO = new TestResultDAOImpl();
		DictionaryDAO dictDAO = new DictionaryDAOImpl();

		//Get tests by user system id
		//bugzilla 2160
		UserTestSectionDAO userTestSectionDAO = new UserTestSectionDAOImpl();
		//2291 replaces 2223 (filter out tests not setup yet
		List tests = userTestSectionDAO.getAllUserTests(request, true);
		ScriptletDAO scriptletDAO = new ScriptletDAOImpl();
		List<Scriptlet> scriptletList = scriptletDAO.getAllScriptlets();

		//bugzilla 1844 - no longer sort by name but by description
		Collections.sort(tests, TestComparator.DESCRIPTION_COMPARATOR);

		List testAnalytes = new ArrayList();
		List testResults = new ArrayList();
		if (testReflex.getTest() != null) {
			testAnalytes = testAnalyteDAO.getAllTestAnalytesPerTest(testReflex
					.getTest());
			Collections.sort(testAnalytes,
					TestAnalyteComparator.NAME_COMPARATOR);
		} else {
			// testAnalytes = testAnalyteDAO.getAllTestAnalytes();
		}

		if (testReflex.getTestAnalyte() != null) {
			testResults = testResultDAO
					.getTestResultsByTestAndResultGroup(testReflex
							.getTestAnalyte());
		} else {
			// testResults = testResultDAO.getAllTestResults();
		}

		// for testResults load the value field with dict entry if needed
		List newListOfTestResults = new ArrayList();
		for (int i = 0; i < testResults.size(); i++) {
			TestResult tr = new TestResult();
			tr = (TestResult) testResults.get(i);
			if (tr.getTestResultType().equals(
					SystemConfiguration.getInstance().getDictionaryType())) {
				// get from dictionary
				Dictionary dictionary = new Dictionary();
				dictionary.setId(tr.getValue());
				dictDAO.getData(dictionary);

				//bugzilla 1847: use dictEntryDisplayValue
				tr.setValue(dictionary.getDictEntryDisplayValue());

			}
			newListOfTestResults.add(tr);
		}

		Collections.sort(newListOfTestResults, TestResultComparator.VALUE_COMPARATOR);

		PropertyUtils.setProperty(form, "tests", tests);
		PropertyUtils.setProperty(form, "testAnalytes", testAnalytes);
		PropertyUtils.setProperty(form, "testResults", newListOfTestResults);
		PropertyUtils.setProperty(form, "addedTests", tests);
		PropertyUtils.setProperty(form, "actionScriptlets", scriptletList);

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		return isNew ? "testreflex.add.title" : "testreflex.edit.title";
	}

	protected String getPageSubtitleKey() {
		return isNew ? "testreflex.add.title" : "testreflex.edit.title";
	}

}
