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
package us.mn.state.health.lims.result.action;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.result.form.ResultsEntryReflexTestPopupActionForm;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.testanalyte.dao.TestAnalyteDAO;
import us.mn.state.health.lims.testanalyte.daoimpl.TestAnalyteDAOImpl;
import us.mn.state.health.lims.testanalyte.valueholder.TestAnalyte;
import us.mn.state.health.lims.testreflex.dao.TestReflexDAO;
import us.mn.state.health.lims.testreflex.daoimpl.TestReflexDAOImpl;
import us.mn.state.health.lims.testreflex.valueholder.TestReflex;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 * bugzilla 1802: redesign results entry - now all tests are displayed and edited in one page
 *               testAnalyteTestResults is now an array in Test_TestAnalyte rather than a form
 *               variable
 * bugzilla 1883: 
 *       removed logic surrounding listOfExistingTestIds as we no longer disable options
 *       on the reflex test popup
 *       see modifications to resultsEntry.jsp:
 *       only trigger popup if same reflex rule has not triggered an added reflex test before that was saved
 *       (i.e. where parent analysis/parent result + analyte/added test are same)
 *       all reflex tests on popup will be preselected and enabled 
 *       only allow a test result to trigger a popup if it has just been changed (isDirty)
 */
public class ResultsEntryReflexTestPopupAction extends ResultsEntryBaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new Result.
		// If there is a parameter present, we should bring up an existing
		// Result to edit.
		String id = request.getParameter(ID);
		String analysisId = (String) request.getParameter("analysisId");
		String listOfSelectedIds = (String) request
				.getParameter("listOfSelectedIds");
		//bugzilla 1684 
		String listOfSelectedIdAnalytes = (String)request.getParameter("listOfSelectedIdAnalytes");
		//bugzilla 1882
		String listOfSelectedIdAnalyses = (String)request.getParameter("listOfSelectedIdAnalyses");

		String idSeparator = SystemConfiguration.getInstance()
				.getDefaultIdSeparator();
		StringTokenizer st = new StringTokenizer(listOfSelectedIds, idSeparator);
		List selectedTestResultIds = new ArrayList();

		while (st.hasMoreElements()) {
			String trId = (String) st.nextElement();
			selectedTestResultIds.add(trId);
		}

		//bugzilla 1684
		StringTokenizer st3 = new StringTokenizer(listOfSelectedIdAnalytes, idSeparator);
    	List selectedTestAnalyteIds = new ArrayList();
		while (st3.hasMoreElements()) {
			String taId = (String) st3.nextElement();
			selectedTestAnalyteIds.add(taId);
		}
		
		//bugzilla 1882
		StringTokenizer st4 = new StringTokenizer(listOfSelectedIdAnalyses, idSeparator);
    	List selectedAnalysisIds = new ArrayList();
		while (st4.hasMoreElements()) {
			String aId = (String) st4.nextElement();
			selectedAnalysisIds.add(aId);
		}

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		ResultsEntryReflexTestPopupActionForm dynaForm = (ResultsEntryReflexTestPopupActionForm) form;

		List listOfReflexTests = new ArrayList();
		List listOfReflexTestIds = new ArrayList();
		List listOfReflexTestsDisabledFlags = new ArrayList();
		List listOfParentResults = new ArrayList();
		// preload checkbox selection
		List preSelectedAddedTests = new ArrayList();
		List listOfParentAnalytes = new ArrayList();
		//bugzilla 1882
		List listOfParentAnalyses = new ArrayList();

		TestReflexDAO testReflexDAO = new TestReflexDAOImpl();
		TestResultDAO testResultDAO = new TestResultDAOImpl();
		TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();
		AnalysisDAO analysisDAO = new AnalysisDAOImpl();
		
		for (int i = 0; i < selectedTestResultIds.size(); i++) {
			TestResult testResult = new TestResult();
			String testResultId = (String) selectedTestResultIds.get(i);
			testResult.setId(testResultId);
			testResultDAO.getData(testResult);
			
			//bugzilla 1684
			TestAnalyte testAnalyte = new TestAnalyte();
			String testAnalyteId = (String)selectedTestAnalyteIds.get(i);
			testAnalyte.setId(testAnalyteId);
			testAnalyteDAO.getData(testAnalyte);
			//bugzilla 1882
			Analysis parentAnalysis = new Analysis();
			String aId = (String)selectedAnalysisIds.get(i);
			parentAnalysis.setId(aId);
            analysisDAO.getData(parentAnalysis);
            
			//bugzilla 1684: added testAnalyte to criteria
			List reflexes = testReflexDAO
					.getTestReflexesByTestResultAndTestAnalyte(testResult, testAnalyte);
			if (reflexes != null) {
				for (int j = 0; j < reflexes.size(); j++) {
					TestReflex testReflex = (TestReflex) reflexes.get(j);
					String testReflexId = testReflex.getId();
					testReflex.setId(testReflexId);
					testReflexDAO.getData(testReflex);

					if (testReflex != null && testReflex.getAddedTest() != null) {
						Test addedTest = (Test) testReflex.getAddedTest();
						if (addedTest.getId() != null) {
								listOfReflexTestsDisabledFlags.add(NO);
								preSelectedAddedTests.add(addedTest.getId());
						}
						//bugzilla 1684 - check to see if a different result
						//already generated this reflex test
						//only add it if not
						//bugzilla 1802 - display all reflex tests even if
						//already generated by diff. result
						//if (!listOfReflexTestIds.contains(addedTest.getId())) {
						  listOfReflexTests.add(addedTest);
						  listOfReflexTestIds.add(addedTest.getId());
						//}					
						listOfParentResults.add(testResult);
						listOfParentAnalytes.add(testAnalyte);
						//bugzilla 1882
                        listOfParentAnalyses.add(parentAnalysis);
					}
				}
			}
		}

		// initialize the form
		dynaForm.initialize(mapping);

		PropertyUtils.setProperty(dynaForm, "listOfReflexTests",
				listOfReflexTests);

		PropertyUtils.setProperty(dynaForm, "listOfReflexTestsDisabledFlags",
				listOfReflexTestsDisabledFlags);
		PropertyUtils.setProperty(dynaForm, "listOfParentResults",
				listOfParentResults);
		PropertyUtils.setProperty(dynaForm, "listOfParentAnalytes", listOfParentAnalytes);
		//bugzilla 1882
		PropertyUtils.setProperty(dynaForm, "listOfParentAnalyses", listOfParentAnalyses);
		int numberOfPreselectedItems = preSelectedAddedTests.size();
		String[] selectedAddedTests = new String[numberOfPreselectedItems];
		for (int i = 0; i < preSelectedAddedTests.size(); i++) {
			String testId = (String)preSelectedAddedTests.get(i);
			selectedAddedTests[i] = testId;
		}
		PropertyUtils.setProperty(dynaForm, "selectedAddedTests",
				selectedAddedTests);

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "resultsentry.reflexTestPopup.title";
		} else {
			return "resultsentry.reflexTestPopup.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "resultsentry.reflexTestPopup.subtitle";
		} else {
			return "resultsentry.reflexTestPopup.subtitle";
		}
	}

}
