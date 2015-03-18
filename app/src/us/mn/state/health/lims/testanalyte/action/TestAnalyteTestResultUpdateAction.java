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

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.analyte.dao.AnalyteDAO;
import us.mn.state.health.lims.analyte.daoimpl.AnalyteDAOImpl;
import us.mn.state.health.lims.analyte.valueholder.Analyte;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.valueholder.UserSessionData;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.testanalyte.dao.TestAnalyteDAO;
import us.mn.state.health.lims.testanalyte.daoimpl.TestAnalyteDAOImpl;
import us.mn.state.health.lims.testanalyte.form.TestAnalyteTestResultActionForm;
import us.mn.state.health.lims.testanalyte.valueholder.TestAnalyte;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class TestAnalyteTestResultUpdateAction extends
		TestAnalyteTestResultBaseAction {

	private boolean isNew = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.mn.state.health.lims.common.action.BaseAction#performAction(org.apache.struts.action.ActionMapping,
	 *      org.apache.struts.action.ActionForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new TestAnalyte.
		// If there is a parameter present, we should bring up an existing
		// TestAnalyte to edit.
		// System.out.println("I am in TestAnalyteTestResultUpdateAction");
		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "false");
		request.setAttribute(NEXT_DISABLED, "false");

		String id = request.getParameter(ID);

		isNew = false;

		TestAnalyteTestResultActionForm dynaForm = (TestAnalyteTestResultActionForm) form;

		// initialize dynaForm Lists
		dynaForm.resetLists();

		// server-side validation (validation.xml)
		ActionMessages errors = dynaForm.validate(mapping, request);

		if (errors != null && errors.size() > 0) {
			// System.out.println("Server side validation errors "
			// + errors.toString());
			saveErrors(request, errors);
			// since we forward to jsp - not Action we don't need to repopulate
			// the lists here
			return mapping.findForward(FWD_FAIL);
		}

		String start = (String) request.getParameter("startingRecNo");
		String direction = (String) request.getParameter("direction");
		
		String locale = SystemConfiguration.getInstance().getDefaultLocale().toString();

		String hiddenSelectedAnalyteIds = (String) dynaForm
				.get("hiddenSelectedAnalyteIds");

		List selectedAnalyteResultGroups = (List) dynaForm
				.get("selectedAnalyteResultGroups");
		List selectedAnalyteNames = (List) dynaForm.get("selectedAnalyteNames");

		List selectedAnalyteIds = (List) dynaForm.get("selectedAnalyteIds");

		// hiddenSelectedAnalyteIds is used to identify when dynamic collection
		// has been cleared from page
		if (hiddenSelectedAnalyteIds.equals("0")) {
			selectedAnalyteIds.clear();
		}

		List selectedAnalyteTypes = (List) dynaForm.get("selectedAnalyteTypes");
		//bugzilla 1870
		List selectedAnalyteIsReportables = (List) dynaForm.get("selectedAnalyteIsReportables");
		List selectedTestAnalyteIds = (List) dynaForm
				.get("selectedTestAnalyteIds");
		List testAnalyteLastupdatedList = (List) dynaForm
				.get("testAnalyteLastupdatedList");

		List testResultResultGroups = (List) dynaForm
				.get("testResultResultGroups");

		List testResultResultGroupTypes = (List) dynaForm
				.get("testResultResultGroupTypes");
		List testResultValueList = (List) dynaForm.get("testResultValueList");

		List dictionaryEntryIdList = (List) dynaForm
				.get("dictionaryEntryIdList");
		List flagsList = (List) dynaForm.get("flagsList");
		//bugzilla 1845 add testResult sortOrder
		List sortList = (List) dynaForm.get("sortList");
		List significantDigitsList = (List) dynaForm
				.get("significantDigitsList");
		List quantLimitList = (List) dynaForm.get("quantLimitList");

		List testResultIdList = (List) dynaForm.get("testResultIdList");

		List testResultLastupdatedList = (List) dynaForm
				.get("testResultLastupdatedList");

		// set test object
		// Test test = new Test();
		Test test = (Test) dynaForm.get("test");
		// test.setTestName(testName);

		TestDAO testDAO = new TestDAOImpl();
		test = testDAO.getTestByName(test);

		//get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());		
		org.hibernate.Transaction tx = HibernateUtil.getSession().beginTransaction();

		try {

			// create hash of result groups and assigned rg number to insert
			Hashtable rgs = new Hashtable();
			int ctr = 1;
			for (int i = 0; i < selectedAnalyteResultGroups.size(); i++) {

				if (!rgs.containsKey(selectedAnalyteResultGroups.get(i))) {
					if (!StringUtil
							.isNullorNill((String) selectedAnalyteResultGroups
									.get(i))) {
						rgs.put(selectedAnalyteResultGroups.get(i),
								new Integer(ctr++));
					} else {
						rgs.put(selectedAnalyteResultGroups.get(i), "");
					}
				}
			}

			// 1. TEST_RESULT
			TestResultDAO testResultDAO = new TestResultDAOImpl();

			// save off list of old testresults
			List oldTestResults = testResultDAO.getAllActiveTestResultsPerTest( test );
			List testResultsToDelete = new ArrayList();

			HashMap oldTestResultsMap = new HashMap();

			for (int i = 0; i < oldTestResults.size(); i++) {
				TestResult tr = (TestResult) oldTestResults.get(i);
				//bugzilla 1932/1926
				tr.setSysUserId(sysUserId);
				oldTestResultsMap.put(tr.getId(), tr);
			}

			List testResults = new ArrayList();
			Hashtable testResultsByRG = new Hashtable();

			// go through list of testResults and insert/update database
			for (int j = 0; j < testResultValueList.size(); j++) {
				if (!StringUtil.isNullorNill((String) testResultValueList
						.get(j))) {
					String rg = (String) testResultResultGroups.get(j);

					TestResult testResult = new TestResult();
					//bugzilla 1926
					testResult.setSysUserId(sysUserId);							
					
					testResult.setTest(test);
					//bugzilla 1625
					testResult.setIsActive(true);
					if (flagsList.get(j) != null) {
						testResult.setFlags((String) flagsList.get(j));
					} else {
						testResult.setFlags("");
					}
					//bugzilla 1845 add testResult sortOrder
					if (sortList.get(j) != null) {
						testResult.setSortOrder((String)sortList.get(j));
					} else {
						testResult.setSortOrder("");
					}
					if (significantDigitsList.get(j) != null) {
						testResult
								.setSignificantDigits((String) significantDigitsList
										.get(j));
					} else {
						testResult.setSignificantDigits("");
					}
					if (quantLimitList.get(j) != null) {
						testResult
								.setQuantLimit((String) quantLimitList.get(j));
					} else {
						testResult.setQuantLimit("");
					}

					testResult
							.setTestResultType((String) testResultResultGroupTypes
									.get(j));
					if (testResult.getTestResultType().equals(
							SystemConfiguration.getInstance()
									.getDictionaryType())) {
						testResult.setValue((String) dictionaryEntryIdList
								.get(j));
					} else {
						testResult
								.setValue((String) testResultValueList.get(j));
					}
					testResult.setContLevel("");
					testResult.setScriptlet(null);
					String trid = (String) testResultIdList.get(j);

					testResult.setId(trid);
					if (StringUtil.isNullorNill(testResult.getId())) {
						testResult.setId(null);

					} else {
						Timestamp trlastupdated = null;
						if (testResultLastupdatedList != null
								&& testResultLastupdatedList.get(j) != null) {
							if (testResultLastupdatedList.get(j) instanceof java.lang.String
									&& !StringUtil
											.isNullorNill((String) testResultLastupdatedList
													.get(j))) {
								trlastupdated = DateUtil.formatStringToTimestamp((String)testResultLastupdatedList.get(j));
							} else {
								if (testResultLastupdatedList.get(j) instanceof java.sql.Timestamp) {
									trlastupdated = (Timestamp) testResultLastupdatedList
											.get(j);
								}
							}
							testResult.setLastupdated(trlastupdated);

						}
					}
					if (testResultResultGroups.get(j) != null
							&& (rgs.containsKey(rg))) {
						// we are re-assigning result groups here
						testResult.setResultGroup(rgs.get(rg).toString());
						addTestResultToTestResultsByRG(testResultsByRG,
								testResult);
						// testResults.add(testResult);
						if (oldTestResultsMap.containsKey(testResult.getId())) {
							//bugzilla 1857 deprecated stuff
							//System.out.println("Updating testResult "
							//		+ testResult.getId()
							//		+ " "
							//		+ StringUtil.formatDateAsText(testResult.getLastupdated(), SystemConfiguration.getInstance().getDefaultLocale()));
							testResultDAO.updateData(testResult);
							oldTestResultsMap.remove(testResult.getId());

						} else {
							testResultDAO.insertData(testResult);
						}
					} else {
						// this is a test result that is no longer needed (not
						// assigned to a test component)
						//bugzilla 1862: make sure we don't try to delete one that
						//doesn't yet exist in database..Just ignore those
						if (!StringUtil.isNullorNill(testResult.getId())) {
							testResultsToDelete.add(testResult);
						}
					}
				}
			}

			// 2. TEST_ANALYTE
			TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();

			// save off list of old testanalytes
			List oldTestAnalytes = testAnalyteDAO
					.getAllTestAnalytesPerTest(test);
			List testAnalytesToDelete = new ArrayList();

			HashMap oldTestAnalytesMap = new HashMap();

			for (int i = 0; i < oldTestAnalytes.size(); i++) {
				TestAnalyte ta = (TestAnalyte) oldTestAnalytes.get(i);
				//bugzilla 1932/1926
				ta.setSysUserId(sysUserId);
				oldTestAnalytesMap.put(ta.getId(), ta);
			}

			List testAnalytes = new ArrayList();

			for (int i = 0; i < selectedAnalyteIds.size(); i++) {

				Analyte analyte = new Analyte();
				analyte.setId(String.valueOf(selectedAnalyteIds.get(i)));
				AnalyteDAO analyteDAO = new AnalyteDAOImpl();
				analyteDAO.getData(analyte);

				TestAnalyte testAnalyte = new TestAnalyte();

				testAnalyte.setTest(test);
				testAnalyte.setAnalyte(analyte);
				String rg = (String) selectedAnalyteResultGroups.get(i);
				// we are re-assigning result groups here
				if (!StringUtil.isNullorNill(rgs.get(rg).toString())) {
					testAnalyte.setResultGroup(rgs.get(rg).toString());
				} else {
					testAnalyte.setResultGroup(null);
				}

				testAnalyte.setSortOrder(String.valueOf(i + 1));
				// bug#1342 since we disabled select if test is locked don't
				// update the value as
				// it is not correctly submitted in the form (if resort
				// happened)
				if (!isTestLockedByResult(testResultIdList)) {
					testAnalyte
							.setTestAnalyteType((String) selectedAnalyteTypes
									.get(i));
				} else {
					// bug#1342 this is done so that we don't update the analyte
					// type if
					// only re-sorting is allowed (disabled select doesn't
					// correctly update)
					testAnalyte.setTestAnalyteType(null);
				}
				//bugzilla 1870
				testAnalyte.setIsReportable((String)selectedAnalyteIsReportables.get(i));

				testAnalyte.setTestResults(new ArrayList());
				testAnalyte.setId((String) selectedTestAnalyteIds.get(i));

				List testAnalyte_testResults = new ArrayList();
				if (testAnalyte.getResultGroup() != null) {
					testAnalyte_testResults = (List) testResultsByRG
							.get(testAnalyte.getResultGroup());
				}
				testAnalyte.setTestResults(testAnalyte_testResults);
				if (StringUtil.isNullorNill(testAnalyte.getId())) {
					testAnalyte.setId(null);
				} else {
					Timestamp talastupdated = null;
					if (testAnalyteLastupdatedList != null
							&& testAnalyteLastupdatedList.get(i) != null) {
						if (testAnalyteLastupdatedList.get(i) instanceof java.lang.String
								&& !StringUtil.isNullorNill((String)testAnalyteLastupdatedList.get(i))) {
							talastupdated = DateUtil.formatStringToTimestamp((String)testAnalyteLastupdatedList.get(i));
						} else {
							if (testAnalyteLastupdatedList.get(i) instanceof java.sql.Timestamp) {
								talastupdated = (Timestamp) testAnalyteLastupdatedList
										.get(i);
							}
						}
						testAnalyte.setLastupdated(talastupdated);
					}

				}

				if (oldTestAnalytesMap.containsKey(testAnalyte.getId())) {
					testAnalyte.setSysUserId(sysUserId);
					testAnalyteDAO.updateData(testAnalyte);
					oldTestAnalytesMap.remove(testAnalyte.getId());
				} else {
					//bugzilla 1932
					testAnalyte.setSysUserId(sysUserId);
					testAnalyteDAO.insertData(testAnalyte);
				}
				// testAnalytes.add(testAnalyte);
			}

			// remove deleted testAnalytes, testResults

			// move testResult objects to delete from HashMap to ArrayList

			Iterator it = oldTestResultsMap.values().iterator();

			//bugzilla 2289: fix delete error because dup test results in array testResultsToDelete
			List testResultIdsToDelete = new ArrayList();
			for (int i = 0; i < testResultsToDelete.size(); i++) {
				TestResult aTestResult = (TestResult)testResultsToDelete.get(i);
				testResultIdsToDelete.add(aTestResult.getId());
			}
			while (it.hasNext()) {
				//bugzilla 2289: fix delete error because dup test results in array testResultsToDelete
				TestResult aTestResult = (TestResult) it.next();
				if (!testResultIdsToDelete.contains(aTestResult.getId())) {
				  testResultsToDelete.add(aTestResult);
				}
			}

			testResultDAO.deleteData(testResultsToDelete);

			it = oldTestAnalytesMap.values().iterator();
			while (it.hasNext()) {
				testAnalytesToDelete.add((TestAnalyte) it.next());
			}

			testAnalyteDAO.deleteData(testAnalytesToDelete);
			
			//bugzilla 2236 make sure we delete any analyses for tests not fully setup
			if (!testDAO.isTestFullySetup(test)) {
				AnalysisDAO analysisDAO = new AnalysisDAOImpl();
				//bugzilla 2300 cancel tests instead of deleting them
				List analyses = analysisDAO.getAllAnalysesPerTest(test);
				if (analyses != null && analyses.size() > 0) {
					for (int i = 0; i < analyses.size(); i++) {
						Analysis analysis = (Analysis)analyses.get(i);
						analysis.setSysUserId(sysUserId);
						analysis.setStatus(SystemConfiguration.getInstance().getAnalysisStatusCanceled());
						analysisDAO.updateData(analysis);
					}
				}

			}
			
			tx.commit();
		} catch (LIMSRuntimeException lre) {
    		//bugzilla 2154
			LogEvent.logError("TestAnalyteTestResultUpdateAction","performAction()",lre.toString());
			tx.rollback();
			errors = new ActionMessages();
			ActionError error = null;
			if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
				error = new ActionError("errors.OptimisticLockException", null,
						null);

			} else {
				if (isTestLockedByResult(testResultIdList)
						&& isTestLockedByReflex(testResultIdList)) {
					error = new ActionError(
							"testanalytetestresult.UpdateException.testLockedByResultAndReflex",
							null, null);
				} else if (isTestLockedByResult(testResultIdList)) {
					error = new ActionError(
							"testanalytetestresult.UpdateException.testLockedByResult",
							null, null);
				} else if (isTestLockedByReflex(testResultIdList)) {
					error = new ActionError(
							"testanalytetestresult.UpdateException.testLockedByReflex",
							null, null);
				} else {
					error = new ActionError("errors.UpdateException", null,
							null);
				}
			}

			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			//bugzilla 1485: allow change and try updating again (enable save button)
			//request.setAttribute(IActionConstants.ALLOW_EDITS_KEY, "false");
			// disable previous and next
			request.setAttribute(PREVIOUS_DISABLED, "true");
			request.setAttribute(NEXT_DISABLED, "true");
			forward = FWD_FAIL;

		} finally {
			HibernateUtil.closeSession();
		}
		if (forward.equals(FWD_FAIL))
			return mapping.findForward(forward);

		// initialize the form
		dynaForm.initialize(mapping);
		// repopulate the form from valueholder
		// PropertyUtils.copyProperties(dynaForm, testAnalyte);

		if ("true".equalsIgnoreCase(request.getParameter("close"))) {
			forward = FWD_CLOSE;
		}

		return getForward(mapping.findForward(forward), id, start);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.mn.state.health.lims.common.action.BaseAction#getPageTitleKey()
	 */
	protected String getPageTitleKey() {
		if (isNew) {
			return "testanalyte.add.title";
		} else {
			return "testanalyte.edit.title";
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.mn.state.health.lims.common.action.BaseAction#getPageSubtitleKey()
	 */
	protected String getPageSubtitleKey() {
		if (isNew) {
			return "testanalyte.add.title";
		} else {
			return "testanalyte.edit.title";
		}
	}

	/**
	 * @param testResultsByRG
	 * @param testResult
	 * @return
	 */
	private Hashtable addTestResultToTestResultsByRG(Hashtable testResultsByRG,
			TestResult testResult) {
		if (testResultsByRG.containsKey(testResult.getResultGroup())) {
			List testResults = (List) testResultsByRG.get(testResult
					.getResultGroup());
			testResults.add(testResult);
		} else {
			List list = new ArrayList();
			list.add(testResult);
			testResultsByRG.put(testResult.getResultGroup(), list);
		}

		return testResultsByRG;
	}

}