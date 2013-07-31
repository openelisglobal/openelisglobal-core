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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.ActionRedirect;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.analyte.valueholder.Analyte;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSCannotDeleteDependentRecordExistsException;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.provider.validation.ResultsValueValidationProvider;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.valueholder.UserSessionData;
import us.mn.state.health.lims.note.dao.NoteDAO;
import us.mn.state.health.lims.note.daoimpl.NoteDAOImpl;
import us.mn.state.health.lims.note.valueholder.Note;
import us.mn.state.health.lims.referencetables.valueholder.ReferenceTables;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.result.valueholder.TestAnalyte_TestResults;
import us.mn.state.health.lims.result.valueholder.Test_TestAnalyte;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.testanalyte.dao.TestAnalyteDAO;
import us.mn.state.health.lims.testanalyte.daoimpl.TestAnalyteDAOImpl;
import us.mn.state.health.lims.testanalyte.valueholder.TestAnalyte;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;

/**
 * @author diane benz 
 *         //AIS - bugzilla 1797 
		   //AIS - bugzilla 1891
 *         //bugzilla 1802 - results entry redesign
 *         bugzilla 1942 status changes
 *         To change this generated comment
 *         edit the template variable "typecomment":
 *         Window>Preferences>Java>Templates. To enable and disable the creation
 *         of type comments go to Window>Preferences>Java>Code Generation.
 */
public class ResultsEntryUpdateAction extends ResultsEntryBaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

    	// this is used if user has clicked notes icon and decides to update
		// (all results get updated, but we need to know which results to
		// display notes for)
		String notesAnalyteId = (String) request.getParameter("analyteId");
		String notesRefId = null;

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");
		String refId = null;
		
		if (request.getAttribute(NOTES_REFID) != null) {
			refId = (String) request.getAttribute(NOTES_REFID);
		}
		
		//bugzilla 2227
		String amendedAnalysisId = null;
		if (request.getParameter(ANALYSIS_ID) != null) {
			amendedAnalysisId = (String) request.getParameter(ANALYSIS_ID);
		}

		BaseActionForm dynaForm = (BaseActionForm) form;

		String accessionNumber = (String) dynaForm.get("accessionNumber");
		String[] selectedTestResultIds = (String[]) dynaForm
				.get("selectedTestResultIds");
		String[] resultValueN = (String[]) dynaForm.get("resultValueN");
		String[] selectedResultIsReportableFlags = (String[]) dynaForm
				.get("selectedResultIsReportableFlags");
		List testTestAnalytes = (List) dynaForm.get("testTestAnalytes");
		String addedReflexTestIds = (String) dynaForm.get("addedReflexTestIds");
		String addedReflexTestParentResultIds = (String) dynaForm
				.get("addedReflexTestParentResultIds");
		// bugzilla 1882
		String addedReflexTestParentAnalyteIds = (String) dynaForm
				.get("addedReflexTestParentAnalyteIds");
		String addedReflexTestParentAnalysisIds = (String) dynaForm
				.get("addedReflexTestParentAnalysisIds");
		String[] selectedTestIsReportableFlags = (String[]) dynaForm
				.get("selectedTestIsReportableFlags");

		String domain = (String) dynaForm.get("domain");
		//bugzilla 2254
		String hasNewUnsatisfactoryResult = (String) dynaForm.get("hasNewUnsatisfactoryResult");
		
		//bugzilla 1798: if coming from popup to link a parent test - this will have information
		String linkedParentInformationString = (String) dynaForm.get("linkedParentInformationString");
		
		//bugzilla 1798: if User has clicked UNLINK FROM PARENT this string will have information
		String unlinkedParentInformationString = (String) dynaForm.get("unlinkedParentInformationString");

		
		int pageResultCounter = 0;
		for (int j = 0; j < testTestAnalytes.size(); j++) {
			Test_TestAnalyte testTestAnalyte = (Test_TestAnalyte) testTestAnalytes
					.get(j);
			TestAnalyte_TestResults[] testAnalyteTestResults = (TestAnalyte_TestResults[]) testTestAnalyte
					.getTestAnalyteTestResults();

			// remove the testresultid added by default for those numeric types
			for (int i = 0; i < testAnalyteTestResults.length; i++) {
				TestAnalyte_TestResults taTr = (TestAnalyte_TestResults) testAnalyteTestResults[i];
				List testResults = taTr.getTestResults();
				//bugzila 1908 fixing a bug where null and size not checked (this was found while testing 1908)
				if (testResults != null && testResults.size() > 0) {
					TestResult testresult = (TestResult) testResults.get(0);
				 //bugzilla 2220 - Added titer type results to this logic (numeric and titer are in one category)
				 if (testresult.getTestResultType().equalsIgnoreCase(SystemConfiguration.getInstance().getNumericType()) ||
				     testresult.getTestResultType().equalsIgnoreCase(SystemConfiguration.getInstance().getTiterType())) {
						if (StringUtil.isNullorNill(resultValueN[pageResultCounter])) {
							selectedTestResultIds[pageResultCounter] = "";
						}

					}
				}
				pageResultCounter++;
			}
		}

		// server side validation of accessionNumber
		// validate on server-side sample accession number

		ActionMessages errors = null;

		try {
			errors = new ActionMessages();
			errors = validateAll(request, errors, dynaForm);
			// System.out.println("Just validated accessionNumber");
		} catch (Exception e) {
    		//bugzilla 2154
			LogEvent.logError("ResultsEntryUpdateAction","performAction()",e.toString());
			ActionError error = new ActionError("errors.ValidationException",
					null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		if (errors != null && errors.size() > 0) {
			saveErrors(request, errors);
			//bugzilla 2361
			if (domain.equals(SystemConfiguration.getInstance().getHumanDomain())) {
				return mapping.findForward(FWD_FAIL_HUMAN);
			} else {
				return mapping.findForward(FWD_FAIL);
			}
		}

		// initialize the form
		dynaForm.initialize(mapping);

		// 1926 get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());
		
		Date today = Calendar.getInstance().getTime();
		Locale locale = (Locale) request.getSession().getAttribute(
				"org.apache.struts.action.LOCALE");

		String dateAsText = DateUtil.formatDateAsText(today, locale);

		org.hibernate.Transaction tx = HibernateUtil.getSession()
				.beginTransaction();

		if (!StringUtil.isNullorNill(accessionNumber)) {

			ResultDAO resultDAO = new ResultDAOImpl();
			TestResultDAO testResultDAO = new TestResultDAOImpl();
			AnalysisDAO analysisDAO = new AnalysisDAOImpl();
			TestDAO testDAO = new TestDAOImpl();
			NoteDAO noteDAO = new NoteDAOImpl();
			TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();
			SampleDAO sampleDAO = new SampleDAOImpl();
			DictionaryDAO dictDAO = new DictionaryDAOImpl();

			try {
				//bugzilla 1798 (added functionality to link a parent test to child test)
				//parse data from popup (link test) for subsequent update of child analysis
				String childAnalysisId = "";
				Analysis linkedParentAnalysis = new Analysis();
				Result linkedParentResult = new Result();
				if (!StringUtil.isNullorNill(linkedParentInformationString)) {
					
					String idSeparator = SystemConfiguration.getInstance()
					.getDefaultIdSeparator();
					StringTokenizer st = new StringTokenizer(linkedParentInformationString, idSeparator);
					String parentAnalysisId = "";
					String parentResultId = "";

                    List listOfIds = new ArrayList();
					while (st.hasMoreElements()) {
						String id = (String) st.nextElement();
						listOfIds.add(id);
					}
					
					//see resultsEntryLinkChildTestToParentTestResultPopup.jsp
					childAnalysisId = (String)listOfIds.get(0);
					parentAnalysisId = (String)listOfIds.get(1);
					parentResultId = (String)listOfIds.get(2);
					
     				linkedParentAnalysis.setId(parentAnalysisId);
					linkedParentResult.setId(parentResultId);
					
					analysisDAO.getData(linkedParentAnalysis);
					resultDAO.getData(linkedParentResult);
					
				}
				//end 1798
				
				Sample sample = new Sample();
				sample.setAccessionNumber(accessionNumber);
				sample = sampleDAO.getSampleByAccessionNumber(accessionNumber);

				int pageResultIndex = 0;

				// pre-process the reflex tests
				List listOfTestsThatTriggeredReflex = new ArrayList();
				List listOfTestResultsThatTriggeredReflex = new ArrayList();
				// bugzilla 1882
				List listOfAnalytesThatTriggeredReflex = new ArrayList();
				List listOfAnalysesThatTriggeredReflex = new ArrayList();
				List listOfResultsThatTriggeredReflex = new ArrayList();
				List listOfAddedTests = new ArrayList();
				if (!StringUtil.isNullorNill(addedReflexTestIds)
						&& !StringUtil
								.isNullorNill(addedReflexTestParentResultIds)) {
					String idSeparator = SystemConfiguration.getInstance()
							.getDefaultIdSeparator();

					// populate list of parent results
					StringTokenizer parentResultTokenizer = new StringTokenizer(
							addedReflexTestParentResultIds, idSeparator);
					while (parentResultTokenizer.hasMoreElements()) {
						String testResultId = (String) parentResultTokenizer
								.nextElement();
						TestResult testResult = new TestResult();
						testResult.setId(testResultId);
						testResultDAO.getData(testResult);
						listOfTestResultsThatTriggeredReflex.add(testResult);
						String testId = testResult.getTest().getId();
						listOfTestsThatTriggeredReflex.add(testId);
					}

					StringTokenizer addedTestTokenizer = new StringTokenizer(
							addedReflexTestIds, idSeparator);

					while (addedTestTokenizer.hasMoreElements()) {
						String testId = (String) addedTestTokenizer
								.nextElement();
						// System.out
						// .println("This is a addedTestToken " + testId);
						Test test = new Test();
						test.setId(testId);
						testDAO.getData(test);
						listOfAddedTests.add(test);
					}

					// bugzilla 1882 populate list of parent analytes
					StringTokenizer parentAnalyteTokenizer = new StringTokenizer(
							addedReflexTestParentAnalyteIds, idSeparator);
					while (parentAnalyteTokenizer.hasMoreElements()) {
						String testAnalyteId = (String) parentAnalyteTokenizer
								.nextElement();
						TestAnalyte testAnalyte = new TestAnalyte();
						testAnalyte.setId(testAnalyteId);
						testAnalyteDAO.getData(testAnalyte);
						listOfAnalytesThatTriggeredReflex.add(testAnalyte);
					}

					// bugzilla 1882 populate list of parent analyses
					StringTokenizer parentAnalysisTokenizer = new StringTokenizer(
							addedReflexTestParentAnalysisIds, idSeparator);
					while (parentAnalysisTokenizer.hasMoreElements()) {
						String analysisId2 = (String) parentAnalysisTokenizer
								.nextElement();
						Analysis analysis2 = new Analysis();
						analysis2.setId(analysisId2);
						listOfAnalysesThatTriggeredReflex.add(analysis2);
					}

				}

				for (int x = 0; x < testTestAnalytes.size(); x++) {
					Test_TestAnalyte test_testAnalyte = (Test_TestAnalyte) testTestAnalytes
							.get(x);
					TestAnalyte_TestResults[] testAnalyteTestResults = (TestAnalyte_TestResults[]) test_testAnalyte
							.getTestAnalyteTestResults();

					Analysis analysis = test_testAnalyte.getAnalysis();
					

					//bugzilla 1942 (if results for all REQUIRED test analytes have been entered then results entry is considered complete)
					boolean areResultsForRequiredTestAnalytesEntered = true;
					//bugzilla 1942 completedDate on analysis should only be update if 
					//              results entry is completed = areResultsForRequiredTestAnalytesEntered is true
					//              AND if at least one result has changed
					boolean atLeastOneRequiredResultHasChanged = false;

					for (int i = 0; i < testAnalyteTestResults.length; i++) {
						
						TestAnalyte_TestResults taTr = (TestAnalyte_TestResults) testAnalyteTestResults[i];

						String selectedTestResultId = taTr
								.getSelectedTestResultId();
						TestAnalyte ta = taTr.getTestAnalyte();

						Result result = new Result();
						Result[] resultsFromTestTestAnalyte = test_testAnalyte
								.getResults();

						for (int j = 0; j < resultsFromTestTestAnalyte.length; j++) {
							Result res = (Result) resultsFromTestTestAnalyte[j];
							if (res != null
									&& !StringUtil.isNullorNill(res.getId())) {
								if (res.getAnalyte().getId().equals(
										ta.getAnalyte().getId())) {
									result = res;
									break;
								}
							}
						}
						// bugzilla 1926
						result.setSysUserId(sysUserId);
						
						//bugzilla 1942
						boolean noResult = true;
						if (!StringUtil.isNullorNill(selectedTestResultId)) {
							if (!StringUtil
									.isNullorNill(selectedTestResultIds[pageResultIndex])) {
								noResult = false;
								TestResult tr = new TestResult();
								tr.setId(selectedTestResultIds[pageResultIndex]);
								testResultDAO.getData(tr);

								//bugzilla 1942: find out if required results have changed on this test (needed to determine whether to update completed date)
								if (result.getIsReportable() != null && result.getIsReportable().equals(YES)) {
									if (result.getTestResult() != null && !StringUtil.isNullorNill(result.getTestResult().getValue())) {
										String oldResult = result.getTestResult().getValue();
										if (!oldResult.equals(tr.getValue())) {
											atLeastOneRequiredResultHasChanged = true;
										}
									}
								}
								
								// update existing selection
								result.setTestResult(tr);
								

								if (tr.getTestResultType()
										.equalsIgnoreCase(SystemConfiguration.getInstance().getNumericType())) {
									result.setValue(resultValueN[pageResultIndex]);
								} else if (tr.getTestResultType()
										.equalsIgnoreCase(SystemConfiguration.getInstance().getTiterType())){

									String setTiter = "1:"+ resultValueN[pageResultIndex];
									result.setValue(setTiter);
								} else {
									result.setValue(tr.getValue());
									//bugzilla 2028 check for UNSATISFACTORY dictionary type results
									// get from dictionary
									Dictionary dictionary = new Dictionary();
									dictionary.setId(result.getValue());
									dictDAO.getData(dictionary);
								}

								result.setAnalysis(analysis);

								result
										.setIsReportable(selectedResultIsReportableFlags[pageResultIndex]);
								if (!StringUtil.isNullorNill(notesAnalyteId)) {
									if (result.getAnalyte().getId().equals(
											notesAnalyteId)) {
										notesRefId = result.getId();
									}
								}
   
								resultDAO.updateData(result);

							} else {
								// bugzilla 1942: delete existing result
								List results = new ArrayList();
								//bugzilla 1942 check if result has notes - THEN DON'T ALLOW DELETE (per Christina/Nancy)
								Note note = new Note();
								List notesByResult = new ArrayList();
								//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
								ReferenceTables referenceTables = new ReferenceTables();
								referenceTables.setId(SystemConfiguration
												.getInstance()
												.getResultReferenceTableId());
     							//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
								note
										.setReferenceTables(referenceTables);
								note.setReferenceId(result.getId());
								notesByResult = noteDAO
										.getAllNotesByRefIdRefTable(note);
								
								//bugzilla 1798
								List childAnalysesByResult = new ArrayList();
								childAnalysesByResult = analysisDAO.getAllChildAnalysesByResult(result);

								if (notesByResult != null && notesByResult.size() > 0) {
																Exception e = new LIMSCannotDeleteDependentRecordExistsException(
										"Cannot delete - dependent record exists for "
												+ result.getId());

							 	throw new LIMSRuntimeException(
										"Error in Result updateData()", e);
								} 
								//bugzilla 1798
								else if (childAnalysesByResult != null && childAnalysesByResult.size() > 0) {
									Exception e = new LIMSCannotDeleteDependentRecordExistsException(
											"Cannot delete - dependent record exists for "
													+ result.getId());

								 	throw new LIMSRuntimeException(
											"Error in Result updateData()", e);
								} else {
								  results.add(result);
								  resultDAO.deleteData(results);
								}
							}
						} else {
							if (!StringUtil
									.isNullorNill(selectedTestResultIds[pageResultIndex])) {
								// insert new result
								noResult = false;
								TestResult tr = new TestResult();
								tr.setId(selectedTestResultIds[pageResultIndex]);
								testResultDAO.getData(tr);

								// insert
								Analyte analyte = ta.getAnalyte();
								result.setAnalyte(analyte);
								result.setAnalysis(analysis);
								
								result.setTestResult(tr);
								if (tr.getTestResultType()
										.equalsIgnoreCase(SystemConfiguration.getInstance().getNumericType())) {
									result.setValue(resultValueN[pageResultIndex]);
								} else if (tr.getTestResultType()
										.equalsIgnoreCase(SystemConfiguration.getInstance().getTiterType())) {

									String setTiter = "1:"+ resultValueN[pageResultIndex];
									result.setValue(setTiter);
								} else {
									result.setValue(tr.getValue());
									//bugzilla 2028 check for UNSATISFACTORY dictionary type results
									// get from dictionary
									Dictionary dictionary = new Dictionary();
									dictionary.setId(result.getValue());
									dictDAO.getData(dictionary);
								}

								result.setResultType(tr.getTestResultType());
								result
										.setIsReportable(selectedResultIsReportableFlags[pageResultIndex]);
								result.setSortOrder(ta.getSortOrder());

								//bugzilla 1942: find out if required results have changed on this test (needed to determine whether to update completed date)
								//this result has changed for sure
								if (ta.getIsReportable() != null && ta.getIsReportable().equals(YES)) {
									atLeastOneRequiredResultHasChanged = true;
								}
								resultDAO.insertData(result);

								// need to add this new result to
								// test_testAnalyte
								// for display on right side of screen after
								// update
								// on route to notes popup
								resultsFromTestTestAnalyte[i] = result;
								test_testAnalyte
										.setResults(resultsFromTestTestAnalyte);

								if (!StringUtil.isNullorNill(notesAnalyteId)) {
									if (result.getAnalyte().getId().equals(
											notesAnalyteId)) {
										notesRefId = result.getId();
									}
								}
							} else {
								// do nothing
							}
						}

						//bugzilla 1942 (if results for all REQUIRED test analytes have been entered then results entry is considered complete)
						//  per Christina - all reportable results must have a value for a test otherwise results entry is not completed
						//  need to check test_analyte for isReportable flag if there is NO result record
						if (noResult && !StringUtil.isNullorNill(ta.getIsReportable()) && ta.getIsReportable().equals(YES)){
							  areResultsForRequiredTestAnalytesEntered = false;
					    }
				
						// if this is analyte that notes icon was selected for
						// then
						// set the result id (IActionConstants.NOTES_REFID) in
						// the
						// request
						// this can be used in NotesPopupAction
						pageResultIndex++;
					}
					// bugzilla 1926
					analysis.setSysUserId(sysUserId);

					analysis.setIsReportable(selectedTestIsReportableFlags[x]);

					//bugzilla 1942 (if results for all REQUIRED test analytes have been entered then results entry is considered complete)
					if (areResultsForRequiredTestAnalytesEntered) {
						//bugzilla 1967 only if not already released
						//bugzilla 1942 AND if at least one result has changed
						if (!analysis.getStatus().equals(SystemConfiguration.getInstance().getAnalysisStatusReleased()) &&
								atLeastOneRequiredResultHasChanged) {
							analysis.setStatus(SystemConfiguration.getInstance()
									.getAnalysisStatusResultCompleted());
							analysis.setCompletedDateForDisplay(dateAsText);
						}
					} else {
						analysis.setStatus(SystemConfiguration.getInstance()
								.getAnalysisStatusAssigned());
						analysis.setCompletedDateForDisplay(null);
					}
					
					//bugzilla 1798 also update parent analysis/parent result if link was requested for this analysis
					//link
					if (!StringUtil.isNullorNill(linkedParentInformationString) && childAnalysisId.equals(analysis.getId())) {
                        analysis.setParentAnalysis(linkedParentAnalysis);
                        analysis.setParentResult(linkedParentResult);
					}
					//unlink
					if (!StringUtil.isNullorNill(unlinkedParentInformationString) && unlinkedParentInformationString.equals(analysis.getId())) {
                        analysis.setParentAnalysis(null);
                        analysis.setParentResult(null);
					}
					
					analysisDAO.updateData(analysis);

				}

				// bugzilla 1882
				if (listOfAnalytesThatTriggeredReflex.size() > 0) {
				    //create listOfResultsThatTriggeredReflex from analysis/analyte
					for (int i = 0; i < listOfAnalysesThatTriggeredReflex
							.size(); i++) {

						Analysis analysisThatTriggered = (Analysis) listOfAnalysesThatTriggeredReflex
								.get(i);
						TestAnalyte analyteThatTriggered = (TestAnalyte) listOfAnalytesThatTriggeredReflex
								.get(i);
						Result result = new Result();
						resultDAO.getResultByAnalysisAndAnalyte(result,
								analysisThatTriggered, analyteThatTriggered);
						listOfResultsThatTriggeredReflex.add(result);
					}
					// Are there any added tests (reflex tests)
					for (int i = 0; i < listOfAnalytesThatTriggeredReflex
							.size(); i++) {

						Analysis analysisThatTriggered = (Analysis) listOfAnalysesThatTriggeredReflex
								.get(i);
						analysisDAO.getData(analysisThatTriggered);
						TestAnalyte analyteThatTriggered = (TestAnalyte) listOfAnalytesThatTriggeredReflex
								.get(i);
						Result result = (Result) listOfResultsThatTriggeredReflex
								.get(i);
						Test test = (Test) listOfAddedTests.get(i);
						Analysis newAnalysis = new Analysis();
						// TODO: need to populate this with actual data!!!
						newAnalysis.setAnalysisType("TEST");
						newAnalysis.setSampleItem(analysisThatTriggered.getSampleItem());
						newAnalysis.setTest(test);
						newAnalysis.setTestSection(test.getTestSection());
						newAnalysis.setStatus(SystemConfiguration.getInstance()
								.getAnalysisStatusAssigned());

						newAnalysis.setParentAnalysis(analysisThatTriggered);
						newAnalysis.setParentResult(result);
						newAnalysis.setIsReportable(test.getIsReportable());
						// bugzilla 1926
						newAnalysis.setSysUserId(sysUserId);
						//bugzilla 2064
						newAnalysis.setRevision(SystemConfiguration.getInstance().getAnalysisDefaultRevision());
						//bugzilla 2013 added duplicateCheck parameter
						analysisDAO.insertData(newAnalysis, false);
					}
				}

				tx.commit();

				// bugzilla 1703: introducing a confirmation message after
				// updates and inserts have succeeded!
				errors = new ActionMessages();
				ActionError error = null;
				error = new ActionError("resultsentry.confirmupdate.message",
						null, null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
				saveErrors(request, errors);
				request.setAttribute(Globals.ERROR_KEY, errors);
				// end bugzilla 1703

			} catch (LIMSRuntimeException lre) {
    			//bugzilla 2154
			    LogEvent.logError("ResultsEntryUpdateAction","performAction()",lre.toString());
				tx.rollback();

				errors = new ActionMessages();
				ActionError error = null;
				if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {

					error = new ActionError("errors.OptimisticLockException",
							null, null);
				} else if (lre.getException() instanceof LIMSCannotDeleteDependentRecordExistsException) {
					error = new ActionError(
							"resultsentry.changetonoresult.error", null, null);
				}  else {
					error = new ActionError("errors.UpdateException", null,
							null);
				}
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
				saveErrors(request, errors);
				request.setAttribute(Globals.ERROR_KEY, errors);
				// bugzilla 1485
				// request.setAttribute(IActionConstants.ALLOW_EDITS_KEY,
				// "false");

                //bugzilla 2361
				if (domain.equals(SystemConfiguration.getInstance().getHumanDomain())) {
					forward = FWD_FAIL_HUMAN;
				} else {
					forward = FWD_FAIL;
				}

			} finally {
				HibernateUtil.closeSession();
			}

		} 

		PropertyUtils.setProperty(dynaForm, "accessionNumber", accessionNumber);
		PropertyUtils.setProperty(dynaForm, "selectedTestResultIds",
				selectedTestResultIds);
		PropertyUtils.setProperty(dynaForm, "resultValueN", resultValueN);
		PropertyUtils.setProperty(dynaForm, "selectedResultIsReportableFlags",
				selectedResultIsReportableFlags);
		PropertyUtils.setProperty(dynaForm, "selectedTestIsReportableFlags",
				selectedTestIsReportableFlags);

		PropertyUtils.setProperty(dynaForm, "domain", domain);
		PropertyUtils.setProperty(dynaForm, "testTestAnalytes",
				testTestAnalytes);

		if (!StringUtil.isNullorNill(notesAnalyteId)) {
			request.setAttribute(NOTES_REFID, notesRefId);
			request.setAttribute(NOTES_REFTABLE,
					SystemConfiguration.getInstance()
							.getResultReferenceTableId());
		} else {
			request.setAttribute(NOTES_REFID, refId);
			request.setAttribute(NOTES_REFTABLE,
					SystemConfiguration.getInstance()
							.getResultReferenceTableId());
		}
		
		//bugzilla 2311
		//bugzilla 2361
		if (!forward.equals(FWD_FAIL) && !forward.equals(FWD_FAIL_HUMAN)) {
		//bugzilla 2028 Qa Events - if any of the results are UNSATISFACTORY then route to QAEvents Entry
		//bugzilla 2227 - don't route to qa events if Note or Amend were clicked
		//bugzilla 2254
		if (hasNewUnsatisfactoryResult.equals(TRUE) && !mapping.getPath().contains("Note")  && !mapping.getPath().contains("Amend")) {
			forward = FWD_SUCCESS_QA_EVENTS_ENTRY;
			return getForward(mapping.findForward(forward), accessionNumber);
		}

		
		//bugzilla 2227
		if (!StringUtil.isNullorNill(amendedAnalysisId)) {
			return getForward(mapping.findForward(forward), accessionNumber, amendedAnalysisId);
		}
		}
		
		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "resultsentry.add.title";
		} else {
			return "resultsentry.edit.title";
		}
	}

	protected String getPageTitleKeyParameter(HttpServletRequest request,
			ActionForm form) {
		BaseActionForm dynaForm = (BaseActionForm) form;
		String accn = "";
		if (dynaForm.get("accessionNumber") != null) {
			accn = (String) dynaForm.get("accessionNumber");
		}
		return accn;
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "resultsentry.add.subtitle";
		} else {
			return "resultsentry.edit.subtitle";
		}
	}


	
	//bugzilla 2028
	protected ActionForward getForward(ActionForward forward, String accessionNumber) {
		ActionRedirect redirect = new ActionRedirect(forward);
		if (!StringUtil.isNullorNill(accessionNumber))
			redirect.addParameter(ACCESSION_NUMBER, accessionNumber);
	
		return redirect;

	}
	
	protected ActionForward getForward(ActionForward forward, String accessionNumber, String analysisId) {
		ActionRedirect redirect = new ActionRedirect(forward);
		if (!StringUtil.isNullorNill(accessionNumber))
			redirect.addParameter(ACCESSION_NUMBER, accessionNumber);
		
		if (!StringUtil.isNullorNill(analysisId))
			redirect.addParameter(ANALYSIS_ID, analysisId);
	
		return redirect;

    }
	
	//2227
	//1856: cleanup - this method does not need to be in BaseAction
	private ActionMessages validateAll(HttpServletRequest request,
			ActionMessages errors, BaseActionForm dynaForm) throws Exception {
		// accession number validation against database (reusing ajax
		// validation logic)
		errors = validateAccessionNumber(request, errors, dynaForm);
        String messageKey = "";
		// isReportableFlag must be set if a result was selected
		List testTestAnalytes = (List) dynaForm.get("testTestAnalytes");
		String[] selectedTestResultIds = (String[]) dynaForm
				.get("selectedTestResultIds");
		String[] selectedResultIsReportableFlags = (String[]) dynaForm
				.get("selectedResultIsReportableFlags");
		for (int j = 0; j < testTestAnalytes.size(); j++) {
			Test_TestAnalyte testTestAnalyte = (Test_TestAnalyte) testTestAnalytes
					.get(j);
			TestAnalyte_TestResults[] testAnalyteTestResults = (TestAnalyte_TestResults[]) testTestAnalyte
					.getTestAnalyteTestResults();

			for (int i = 0; i < testAnalyteTestResults.length; i++) {

				TestAnalyte_TestResults taTr = (TestAnalyte_TestResults) testAnalyteTestResults[i];
				String selectedTestResultId = taTr.getSelectedTestResultId();

				if (!StringUtil.isNullorNill(selectedTestResultId)) {
					if (!StringUtil.isNullorNill(selectedTestResultIds[i])) {
						if (StringUtil
								.isNullorNill(selectedResultIsReportableFlags[i])) {
							messageKey = "result.isReportable";
							ActionError error = new ActionError(
									"errors.invalid", getMessageForKey(messageKey), null);
							errors.add(ActionMessages.GLOBAL_MESSAGE, error);
						}
					}
				}
			}
		}

		// verify if "N" type values are validated
		String[] resultValueN = (String[]) dynaForm.get("resultValueN");
		ResultsValueValidationProvider resultsValueValidator = new ResultsValueValidationProvider();
		//bugzilla 2347, 2361 fix error message handling
		for (int i = 0; i < resultValueN.length; i++) {
			//bugzilla 2016: added bug fix where we were not checking to make sure selectedTestResultIds[i] is not null before validating
			//bugzilla 2439 need to check for null resultValueN[i] - best to use StringUtil.isNullorNill()
			if ((!StringUtil.isNullorNill(resultValueN[i]) && !resultValueN[i].equalsIgnoreCase("-1"))
					&& !StringUtil.isNullorNill(selectedTestResultIds[i])) {
				String message = resultsValueValidator.validate(
						resultValueN[i], selectedTestResultIds[i], null);
				String[] msgArray = message.split(SystemConfiguration.getInstance().getDefaultIdSeparator());
				String errorMess = msgArray[0];
				if (errorMess.equalsIgnoreCase("invalid")) {
					ActionError error = null;
					if (msgArray[1].equals(SystemConfiguration.getInstance().getTiterType())) {
						error = new ActionError("resultsentry.invalidresultvalue.titer.message", 
								msgArray[2], msgArray[3], null);	
					} else {
						error = new ActionError("resultsentry.invalidresultvalue.numeric.message", 
								msgArray[2], msgArray[3], null);
					}

					errors.add(ActionMessages.GLOBAL_MESSAGE, error);
    			}
			}
		}

		return errors;
	}
	
}
