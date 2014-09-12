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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.services.TestService;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.login.dao.UserTestSectionDAO;
import us.mn.state.health.lims.login.daoimpl.UserTestSectionDAOImpl;
import us.mn.state.health.lims.method.dao.MethodDAO;
import us.mn.state.health.lims.method.daoimpl.MethodDAOImpl;
import us.mn.state.health.lims.method.valueholder.MethodComparator;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.test.valueholder.TestComparator;
import us.mn.state.health.lims.test.valueholder.TestSectionComparator;
import us.mn.state.health.lims.testanalyte.dao.TestAnalyteDAO;
import us.mn.state.health.lims.testanalyte.daoimpl.TestAnalyteDAOImpl;
import us.mn.state.health.lims.testanalyte.form.TestAnalyteTestResultActionForm;
import us.mn.state.health.lims.testanalyte.valueholder.TestAnalyte;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class TestAnalyteTestResultAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new TestAnalyte.
		// If there is a parameter present, we should bring up an existing
		// TestAnalyte to edit.
		TestAnalyteTestResultActionForm dynaForm = (TestAnalyteTestResultActionForm) form;

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "false");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

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
		List tests = userTestSectionDAO.getAllUserTests(request, false);	
		
		List selectedAnalyteTypes = new ArrayList();
		//bugzilla 1870
		List selectedAnalyteIsReportables = new ArrayList();
		List selectedAnalyteResultGroups = new ArrayList();
		List selectedAnalyteIds = new ArrayList();
		List selectedAnalyteNames = new ArrayList();
		List selectedTestAnalyteIds = new ArrayList();
		List testAnalyteLastupdatedList = new ArrayList();

		List testResultResultGroups = new ArrayList();
		List testResultResultGroupTypes = new ArrayList();
		List testResultValueList = new ArrayList();
		List dictionaryEntryIdList = new ArrayList();
		List flagsList = new ArrayList();
		//bugzilla 1845 add testResult sortOrder
		List sortList = new ArrayList();
		List significantDigitsList = new ArrayList();
		List quantLimitList = new ArrayList();
		List testResultIdList = new ArrayList();
		List testResultLastupdatedList = new ArrayList();
		Test test = new Test();

		// FIXED BUG - NEED TO REFRESH SELECTIONS ON GOING INTO FORM
		// get 3 drop down selections so we can repopulate
		// String selectedTestSectionId = (String) dynaForm
		// .get("selectedTestSectionId");
		// String selectedMethodId = (String) dynaForm.get("selectedMethodId");
		// String selectedTestId = (String) dynaForm.get("selectedTestId");

		String selectedTestId = null;
		String selectedTestSectionId = null;
		String selectedMethodId = null;
		// END BUG FIX

		// initialize the form
		dynaForm.initialize(mapping);
		dynaForm.resetLists();

		// bugzilla 1350 need to check if test has been selected so stray
		// components don't display
		if (!StringUtil.isNullorNill(selectedTestId)) {
			// if test is selected enable the save button
			request.setAttribute(ALLOW_EDITS_KEY, "true");
			test.setId(selectedTestId);
			TestDAO testDAO = new TestDAOImpl();
			testDAO.getData(test);

			TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();
			List testAnalytes = testAnalyteDAO.getAllTestAnalytesPerTest(test);

			TestResultDAO testResultDAO = new TestResultDAOImpl();
			List testResults = testResultDAO.getAllActiveTestResultsPerTest( test );

			DictionaryDAO dictDAO = new DictionaryDAOImpl();

			if (testAnalytes != null && testAnalytes.size() > 0) {

				for (int i = 0; i < testAnalytes.size(); i++) {
					TestAnalyte ta = (TestAnalyte) testAnalytes.get(i);
					selectedAnalyteTypes.add(ta.getTestAnalyteType());
					//bugzilla 1870
					selectedAnalyteIsReportables.add(ta.getIsReportable());
					if (ta.getResultGroup() != null) {
						selectedAnalyteResultGroups.add(ta.getResultGroup());
					} else {
						selectedAnalyteResultGroups.add("");
					}
					selectedAnalyteIds.add(ta.getAnalyte().getId());
					selectedAnalyteNames.add(ta.getAnalyte().getAnalyteName());
					selectedTestAnalyteIds.add(ta.getId());
				}

				if (testResults != null && testResults.size() > 0) {
					for (int i = 0; i < testResults.size(); i++) {
						TestResult tr = (TestResult) testResults.get(i);
						testResultIdList.add(tr.getId());
						testResultLastupdatedList.add(tr.getLastupdated());
						testResultResultGroups.add(tr.getResultGroup());
						testResultResultGroupTypes.add(tr.getTestResultType());
						if (tr.getTestResultType().equals(
								SystemConfiguration.getInstance()
										.getDictionaryType())) {
							dictionaryEntryIdList.add(tr.getValue());
							Dictionary dict = new Dictionary();
							dict.setId(tr.getValue());
							dictDAO.getData(dict);
							//bugzilla 1847: use dictEntryDisplayValue
							testResultValueList.add(dict.getDictEntryDisplayValue());
						} else {
							dictionaryEntryIdList.add("");
							testResultValueList.add(tr.getValue());
						}
						if (tr.getFlags() == null) {
							flagsList.add("");
						} else {
							flagsList.add(tr.getFlags());
						}
                		//bugzilla 1845 add testResult sortOrder
						if (tr.getSortOrder() == null) {
							sortList.add("");
						} else {
							sortList.add(tr.getSortOrder());
						}
						if (tr.getSignificantDigits() == null) {
							significantDigitsList.add("");
						} else {
							significantDigitsList
									.add(tr.getSignificantDigits());
						}
						if (tr.getQuantLimit() == null) {
							quantLimitList.add("");
						} else {
							quantLimitList.add(tr.getQuantLimit());
						}

					}
				}
				isNew = false;
			} else {

				isNew = true;
			}
		} else {
			isNew = true;
		}

		PropertyUtils.setProperty(dynaForm, "selectedAnalyteTypes",
				selectedAnalyteTypes);
				//bugzilla 1870
		PropertyUtils.setProperty(dynaForm, "selectedAnalyteIsReportables",
				selectedAnalyteIsReportables);
		PropertyUtils.setProperty(dynaForm, "selectedAnalyteResultGroups",
				selectedAnalyteResultGroups);
		PropertyUtils.setProperty(dynaForm, "selectedAnalyteIds",
				selectedAnalyteIds);
		PropertyUtils.setProperty(dynaForm, "selectedTestAnalyteIds",
				selectedTestAnalyteIds);
		PropertyUtils.setProperty(dynaForm, "testAnalyteLastupdatedList",
				testAnalyteLastupdatedList);
		PropertyUtils.setProperty(dynaForm, "selectedAnalyteNames",
				selectedAnalyteNames);

		PropertyUtils.setProperty(dynaForm, "testResultResultGroups",
				testResultResultGroups);

		PropertyUtils.setProperty(dynaForm, "testResultResultGroupTypes",
				testResultResultGroupTypes);

		PropertyUtils.setProperty(dynaForm, "testResultValueList",
				testResultValueList);

		PropertyUtils.setProperty(dynaForm, "testResultIdList",
				testResultIdList);

		PropertyUtils.setProperty(dynaForm, "testResultLastupdatedList",
				testResultLastupdatedList);

		PropertyUtils.setProperty(dynaForm, "dictionaryEntryIdList",
				dictionaryEntryIdList);

		PropertyUtils.setProperty(dynaForm, "flagsList", flagsList);
		//bugzilla 1845 add testResult sortOrder
		PropertyUtils.setProperty(dynaForm, "sortList", sortList);

		PropertyUtils.setProperty(dynaForm, "significantDigitsList",
				significantDigitsList);

		PropertyUtils.setProperty(dynaForm, "quantLimitList", quantLimitList);

		PropertyUtils.setProperty(dynaForm, "test", 
				(test == null? new Test(): test));

		// sort three drop down lists
		Collections.sort(testSections, TestSectionComparator.NAME_COMPARATOR);
		
		//bugzilla 1844 - no longer sort by name but by description 
		Collections.sort(tests, TestComparator.DESCRIPTION_COMPARATOR);
		Collections.sort(methods, MethodComparator.NAME_COMPARATOR);

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
		if (isNew) {
			return "testanalytetestresult.add.title";
		} else {
			return "testanalytetestresult.edit.title";
		}
	}

	protected String getPageTitleKeyParameter(HttpServletRequest request,
			ActionForm form) {
		BaseActionForm dynaForm = (BaseActionForm) form;
		Test test = (Test) dynaForm.get("test");
        return TestService.getUserLocalizedTestName( test );
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "testanalytetestresult.add.title";
		} else {
			return "testanalytetestresult.edit.title";
		}
	}

	protected String getPageSubtitleKeyParameter(HttpServletRequest request,
			ActionForm form) {

		BaseActionForm dynaForm = (BaseActionForm) form;
		Test test = (Test) dynaForm.get("test");
        return TestService.getUserLocalizedTestName( test );
	}

}
