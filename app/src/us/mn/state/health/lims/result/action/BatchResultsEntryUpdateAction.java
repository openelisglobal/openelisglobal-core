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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.action.BaseAction;
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
import us.mn.state.health.lims.result.valueholder.Sample_TestAnalyte;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.test.valueholder.TestComparator;
import us.mn.state.health.lims.test.valueholder.TestSectionComparator;
import us.mn.state.health.lims.testanalyte.valueholder.TestAnalyte;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;

/**
 * @author diane benz
 * //AIS - bugzilla 1863
 * //AIS - bugzilla 1891
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 * bugzilla 1942 - status changes
 * bugzilla 1992 - cleanup (remove counter definitions to stay consistent)
 */
public class BatchResultsEntryUpdateAction extends BaseAction {

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

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		BaseActionForm dynaForm = (BaseActionForm) form;

		String selectedTestSectionId = (String) dynaForm
				.get("selectedTestSectionId");
		String selectedTestId = (String) dynaForm.get("selectedTestId");

		List testAnalyteTestResults = (List) dynaForm
				.get("testAnalyte_TestResults");

		List sampleTestAnalytes = (List) dynaForm.get("sample_TestAnalytes");
		List resultValueN = (List) dynaForm.get("resultValueN");
		List resultValueT = (List) dynaForm.get("resultValueT");

		List tests = (List) dynaForm.get("tests");
		List testSections = (List) dynaForm.get("testSections");

		String receivedDateForDisplay = (String) dynaForm
				.get("receivedDateForDisplay");

		//bugzilla 2028 (sub bugzilla 2036)
		List accessionNumbersWithUnsatisfactoryResults = new ArrayList();
		//bugzilla 2254
		String stringOfUnsatisfactoryResults = (String)dynaForm.get("stringOfUnsatisfactoryResults");
		
		//bugzilla 2254
		String textSeparator = SystemConfiguration.getInstance()
		.getDefaultTextSeparator();
		textSeparator = StringUtil.convertStringToRegEx(textSeparator);

		List newUnsatisfactoryResultsList = StringUtil.loadListFromStringOfElements(stringOfUnsatisfactoryResults,
				textSeparator, false);
		
		for (int i = 0; i < newUnsatisfactoryResultsList.size(); i++) {
			String nameOfTestAnalyteElement = (String)newUnsatisfactoryResultsList.get(i);
			int index = getIndexFromName(nameOfTestAnalyteElement);
			
			if (index != -1) {
				Sample_TestAnalyte sampleWithUnsatisfactoryResult = (Sample_TestAnalyte)sampleTestAnalytes.get(index);

				if (!accessionNumbersWithUnsatisfactoryResults.contains(sampleWithUnsatisfactoryResult.getSample().getAccessionNumber())) {
					accessionNumbersWithUnsatisfactoryResults.add(sampleWithUnsatisfactoryResult.getSample().getAccessionNumber());
				}
			}
		}
		
		ActionMessages errors = null;

		try {
			errors = new ActionMessages();
			errors = validateAll(request, errors, dynaForm);
		} catch (Exception e) {
    		//bugzilla 2154
			LogEvent.logError("BatchResultsEntryUpdateAction","performAction()",e.toString());
			ActionError error = new ActionError("errors.ValidationException",
					null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}

		if (errors != null && errors.size() > 0) {
			saveErrors(request, errors);
			request.setAttribute(ALLOW_EDITS_KEY, "false");

			return mapping.findForward(FWD_FAIL);
		}

		String localeString = SystemConfiguration.getInstance().getDefaultLocale()
				.toString();
		Date today = Calendar.getInstance().getTime();
		Locale locale = (Locale) request.getSession().getAttribute(
				"org.apache.struts.action.LOCALE");

		String dateAsText = DateUtil.formatDateAsText(today, locale);

		// initialize the form
		dynaForm.initialize(mapping);

		// bugzilla 1926
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());

		org.hibernate.Transaction tx = HibernateUtil.getSession()
				.beginTransaction();

		if (!StringUtil.isNullorNill(selectedTestId)) {

			ResultDAO resultDAO = new ResultDAOImpl();
			TestResultDAO testResultDAO = new TestResultDAOImpl();
			AnalysisDAO analysisDAO = new AnalysisDAOImpl();
			NoteDAO noteDAO = new NoteDAOImpl();
			DictionaryDAO dictDAO = new DictionaryDAOImpl();
			List resultsToDelete = new ArrayList();
			
			Sample sample = null;
			Analysis analysis = null;
			Result result = null;

			try {
				for (int i = 0; i < sampleTestAnalytes.size(); i++) {
					Sample_TestAnalyte sTa = (Sample_TestAnalyte) sampleTestAnalytes.get(i);

					sample = sTa.getSample();
					analysis = sTa.getAnalysis();

					List testResultIds = sTa.getSampleTestResultIds();
					List testResultValues = sTa.getTestResultValues();
					List resultLastupdatedList = sTa.getResultLastupdatedList();
					List tas = (List) sTa.getTestAnalytes();
					//bugzilla 1942 (if results for all REQUIRED test analytes have been entered then results entry is considered complete)
					boolean areResultsForRequiredTestAnalytesEntered = true;
					
					//bugzilla 1942 completedDate on analysis should only be update if 
					//              results entry is completed = areResultsForRequiredTestAnalytesEntered is true
					//              AND if at least one result has changed
					boolean atLeastOneRequiredResultHasChanged = false;

                   
					for (int j = 0; j < tas.size(); j++) {
						TestAnalyte ta = (TestAnalyte) tas.get(j);
						
				
						result = new Result();
						resultDAO.getResultByAnalysisAndAnalyte(result,
								analysis, ta);

						String testResultId = (String) testResultIds.get(j);
						String testResultValue = (String) testResultValues.get(j);
						// bugzilla 1926
						result.setSysUserId(sysUserId);
						boolean noResult = true;
						if ((!StringUtil.isNullorNill(testResultId))
								&& (!testResultValue.equalsIgnoreCase(""))) {
                            noResult = false;
							TestResult tr = new TestResult();
							tr.setId(testResultId);
							testResultDAO.getData(tr);
							
							if (!StringUtil.isNullorNill(result.getId())) {
								//bugzilla 1942: find out if results have changed on this test
								if (result.getIsReportable() != null && result.getIsReportable().equals(YES)) {
									if (result.getTestResult() != null && !StringUtil.isNullorNill(result.getTestResult().getValue())) {
										String oldResult = result.getTestResult().getValue();
										if (!oldResult.equals(tr.getValue())) {
											atLeastOneRequiredResultHasChanged = true;
										}
									}
								}
							} else {
								//bugzilla 1942: find out if required results have changed on this test (needed to determine whether to update completed date)
								//this result has changed for sure
								if (ta.getIsReportable() != null && ta.getIsReportable().equals(YES)) {
									atLeastOneRequiredResultHasChanged = true;
								}
							}
							
						
							result.setTestResult(tr);
							

							result.setAnalysis(analysis);
							result.setAnalyte(ta.getAnalyte());
							if (tr.getTestResultType().equalsIgnoreCase(SystemConfiguration.getInstance().getNumericType())){								
								result.setValue(testResultValue);
							}else if (tr.getTestResultType().equalsIgnoreCase(SystemConfiguration.getInstance().getTiterType())){

								result.setValue("1:" + testResultValue);
							} else {
								result.setValue(tr.getValue());
								//bugzilla 2028 check for UNSATISFACTORY dictionary type results
								// get from dictionary
								Dictionary dictionary = new Dictionary();
								dictionary.setId(result.getValue());
								dictDAO.getData(dictionary);
							}
							result.setResultType(tr.getTestResultType());
							result.setSortOrder(ta.getSortOrder());
							// bugzilla 1942
							result.setIsReportable(ta.getIsReportable());


							// optimistic locking use timestamp from getData()
							Timestamp reslastupdated = null;
							if (resultLastupdatedList != null
									&& resultLastupdatedList.get(j) != null) {
								if (resultLastupdatedList.get(j) instanceof java.lang.String
										&& !StringUtil
												.isNullorNill((String) resultLastupdatedList
														.get(j))) {

									reslastupdated = DateUtil
											.formatStringToTimestamp(
													(String) resultLastupdatedList
															.get(j), localeString);
								} else {
									if (resultLastupdatedList.get(j) instanceof java.sql.Timestamp) {
										reslastupdated = (Timestamp) resultLastupdatedList
												.get(j);
									}
								}
								result.setLastupdated(reslastupdated);

							}


								if (!StringUtil.isNullorNill(result.getId())) {
									resultDAO.updateData(result);
								} else {
									resultDAO.insertData(result);
								}
						
						} else if ((!StringUtil.isNullorNill(result.getId()))
								&& (!result.getId().equalsIgnoreCase("0"))) {
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

							if (notesByResult != null && notesByResult.size() > 0) {
								Exception e = new LIMSCannotDeleteDependentRecordExistsException(
										"Cannot delete - dependent record exists for " + result.getId());

								throw new LIMSRuntimeException("Error in BatchResult updateData()", e);			
							} else {
							   resultsToDelete.add(result);
							}
						}

						//bugzilla 1942 (if results for all REQUIRED test analytes (if they are reportable) have been entered then results entry is considered complete)
						//  per Christina - all reportable results (test_analyte.is_reportable) must have a value for a test otherwise results entry is not completed
						if (!StringUtil.isNullorNill(ta.getIsReportable()) && ta.getIsReportable().equals(YES)) {
							if (noResult) {
								areResultsForRequiredTestAnalytesEntered = false;
							}
						}
					}// after inner for loop

					if (analysis != null) {
						analysis.setSysUserId(sysUserId);
						
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
							analysis.setStatus(SystemConfiguration
									.getInstance().getAnalysisStatusAssigned());
							analysis.setCompletedDateForDisplay(null);
						}
						analysisDAO.updateData(analysis);
					}

				}// outter for loop

				if (resultsToDelete.size() > 0) {
					resultDAO.deleteData(resultsToDelete);
				}

				tx.commit();

			} catch (LIMSRuntimeException lre) {
    			//bugzilla 2154
			    LogEvent.logError("BatchResultsEntryUpdateAction","performAction()",lre.toString());
				tx.rollback();

				errors = new ActionMessages();
				ActionError error = null;
				if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
					error = new ActionError("errors.OptimisticLockException",
							null, null);
				} else if (lre.getException() instanceof LIMSCannotDeleteDependentRecordExistsException) {
					error = new ActionError("resultsentry.changetonoresult.error", null, null);
		
				} else {
					error = new ActionError("errors.GetException", null, null);

				}
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
				saveErrors(request, errors);
				request.setAttribute(Globals.ERROR_KEY, errors);
				request.setAttribute(ALLOW_EDITS_KEY, "false");
				forward = FWD_FAIL;

			} finally {
				HibernateUtil.closeSession();
			}

		} else {
			return mapping.findForward(FWD_FAIL);
		}

		// #1347 sort dropdown values
		Collections.sort(testSections, TestSectionComparator.NAME_COMPARATOR);
		Collections.sort(tests, TestComparator.NAME_COMPARATOR);

		PropertyUtils.setProperty(dynaForm, "selectedTestId", selectedTestId);
		PropertyUtils.setProperty(dynaForm, "selectedTestSectionId",
				selectedTestSectionId);
		PropertyUtils.setProperty(dynaForm, "tests", tests);
		PropertyUtils.setProperty(dynaForm, "testSections", testSections);
		PropertyUtils.setProperty(dynaForm, "sample_TestAnalytes",
				sampleTestAnalytes);
		PropertyUtils.setProperty(dynaForm, "resultValueN", resultValueN);
		PropertyUtils.setProperty(dynaForm, "resultValueT", resultValueT);
		PropertyUtils.setProperty(dynaForm, "testAnalyte_TestResults",
				testAnalyteTestResults);
		PropertyUtils.setProperty(form, "receivedDateForDisplay",
				receivedDateForDisplay);

		//bugzilla 2028 Qa Events - if any of the results are UNSATISFACTORY then route to QAEvents Entry
		if (accessionNumbersWithUnsatisfactoryResults.size() > 0) {
			forward = FWD_SUCCESS_QA_EVENTS_ENTRY;
			HttpSession session = (HttpSession)request.getSession();
			//modified naming of static variable for bugzilla 2053
			session.setAttribute(QA_EVENTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_ENTRY_PARAM_ACCESSION_NUMBERS, accessionNumbersWithUnsatisfactoryResults);
			session.setAttribute(QA_EVENTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_ENTRY_PARAM_TEST_ID, selectedTestId);
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

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "resultsentry.add.subtitle";
		} else {
			return "resultsentry.edit.subtitle";
		}
	}

	protected ActionMessages validateAll(HttpServletRequest request,
			ActionMessages errors, BaseActionForm dynaForm) throws Exception {

		// verify if "N/T" type values are validated
		List sampleTestAnalytes = (List) dynaForm.get("sample_TestAnalytes");

		ResultsValueValidationProvider resultsValueValidator = new ResultsValueValidationProvider();

		for (int i = 0; i < sampleTestAnalytes.size(); i++) {
			Sample_TestAnalyte sTa = (Sample_TestAnalyte) sampleTestAnalytes.get(i);
			List testResultIds = sTa.getSampleTestResultIds();
			List testResultValues = sTa.getTestResultValues();
			List tas = (List) sTa.getTestAnalytes();
			for (int j = 0; j < tas.size(); j++) {
				String testResultId = (String) testResultIds.get(j);
				String testResultValue = (String) testResultValues.get(j);

    			//bugzilla 2016: added bug fix where we were not checking to make sure selectedTestResultIds[i] is not null before validating
				//bugzilla 2347, 2361 fix error message handling
				if ((!testResultValue.equalsIgnoreCase("-1"))
						&& (!testResultValue.equalsIgnoreCase("")) && !StringUtil.isNullorNill(testResultId)) {

					String message = resultsValueValidator.validate( testResultValue,testResultId, null);				
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
		}

		return errors;
	}
	
	//bugizlla 2254
	private int getIndexFromName(String name) throws LIMSRuntimeException{

		int index = -1;
		if (!StringUtil.isNullorNill(name)) {
		int start = name.indexOf("[");
		int end = name.indexOf("]");
		String indexString = name.substring(start + 1, end);
		try {
			index = Integer.parseInt(indexString);
		} catch (NumberFormatException nfe){
    		//bugzilla 2154
			LogEvent.logError("BatchResultsEntryUpdateAction","getIndexFromName()",nfe.toString());
			throw new LIMSRuntimeException(nfe);
		}

		}
		return index;
	}
	
}