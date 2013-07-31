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
import java.util.Collections;
import java.util.List;

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
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.login.dao.UserTestSectionDAO;
import us.mn.state.health.lims.login.daoimpl.UserTestSectionDAOImpl;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.person.dao.PersonDAO;
import us.mn.state.health.lims.person.daoimpl.PersonDAOImpl;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.result.valueholder.ResultsVerificationTestComparator;
import us.mn.state.health.lims.result.valueholder.Sample_TestAnalyte;
import us.mn.state.health.lims.result.valueholder.TestAnalyte_TestResults;
import us.mn.state.health.lims.result.valueholder.Test_TestAnalyte;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.samplehuman.dao.SampleHumanDAO;
import us.mn.state.health.lims.samplehuman.daoimpl.SampleHumanDAOImpl;
import us.mn.state.health.lims.samplehuman.valueholder.SampleHuman;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import us.mn.state.health.lims.sampleorganization.dao.SampleOrganizationDAO;
import us.mn.state.health.lims.sampleorganization.daoimpl.SampleOrganizationDAOImpl;
import us.mn.state.health.lims.sampleorganization.valueholder.SampleOrganization;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.test.valueholder.TestComparator;
import us.mn.state.health.lims.test.valueholder.TestSectionComparator;
import us.mn.state.health.lims.testanalyte.dao.TestAnalyteDAO;
import us.mn.state.health.lims.testanalyte.daoimpl.TestAnalyteDAOImpl;
import us.mn.state.health.lims.testanalyte.valueholder.TestAnalyte;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;

/**
 * @author diane benz //AIS - bugzilla 1872 bugzilla 1348 To change this
 *         generated comment edit the template variable Ais-bugzilla 1838 = To
 *         display numeric result on Result Verification screen //AIS - bugzilla
 *         1891"typecomment": Window>Preferences>Java>Templates. To enable and
 *         disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 *  bugzilla 2614 - fix to work for NB samples
 */
public class BatchResultsVerificationViewAction extends BatchResultsVerificationBaseAction {

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new Result.
		// If there is a parameter present, we should bring up an existing
		// Result to edit.
		String id = request.getParameter(ID);

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		BaseActionForm dynaForm = (BaseActionForm) form;

		String selectedTestId = "";
		String selectedTestSectionId = "";
		String accessionNumber = "";
		List testSections = null;
		List tests = null;
		// String currentDate = null;

		HttpSession session = request.getSession();
		//bugzilla 2378
		UserTestSectionDAO userTestSectionDAO = new UserTestSectionDAOImpl();
		DictionaryDAO dictDAO = new DictionaryDAOImpl();
		TestDAO testDAO = new TestDAOImpl();
		TestResultDAO testResultDAO = new TestResultDAOImpl();
		AnalysisDAO analysisDAO = new AnalysisDAOImpl();
		SampleDAO sampleDAO = new SampleDAOImpl();
		ResultDAO resultDAO = new ResultDAOImpl();
		PatientDAO patientDAO = new PatientDAOImpl();
		PersonDAO personDAO = new PersonDAOImpl();
		SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
		SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
		SampleOrganizationDAO sampleOrganizationDAO = new SampleOrganizationDAOImpl();

		//bugzilla 2053
		String testIdFromSessionRouting = (String) session.getAttribute(RESULTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_VERIFICATION_PARAM_TEST_ID);
		String testSectionIdFromSessionRouting = (String)session.getAttribute(RESULTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_VERIFICATION_PARAM_TEST_SECTION_ID);
		String accessionNumberFromSessionRouting = (String) session.getAttribute(RESULTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_VERIFICATION_PARAM_ACCESSION_NUMBER);
		boolean routingFromResultsEntry = false;
		if (!StringUtil.isNullorNill(testIdFromSessionRouting) || !StringUtil.isNullorNill(testSectionIdFromSessionRouting) || !StringUtil.isNullorNill(accessionNumberFromSessionRouting)) {
			routingFromResultsEntry = true;
		}
		if (routingFromResultsEntry) {
			selectedTestId = testIdFromSessionRouting;
			selectedTestSectionId = testSectionIdFromSessionRouting;
			accessionNumber = accessionNumberFromSessionRouting;
			session.setAttribute(RESULTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_VERIFICATION_PARAM_TEST_ID,
					null);
			session.setAttribute(RESULTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_VERIFICATION_PARAM_TEST_SECTION_ID, null);
			session.setAttribute(RESULTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_VERIFICATION_PARAM_ACCESSION_NUMBER,
					null);
			//Get tests/testsections by user system id
			//bugzilla 2160
			testSections = userTestSectionDAO.getAllUserTestSections(request);

		} else {
			testSections = (List) dynaForm.get("testSections");
			tests = (List)dynaForm.get("tests");
			accessionNumber = (String) dynaForm.get("accessionNumber");
			selectedTestSectionId = (String) dynaForm.get("selectedTestSectionId");
			selectedTestId = (String) dynaForm.get("selectedTestId");
		}

		if (!StringUtil.isNullorNill(selectedTestSectionId)) {
			tests = testDAO.getTestsByTestSection(selectedTestSectionId);
		} else {
			testSections = userTestSectionDAO.getAllUserTestSections(request);
			tests = userTestSectionDAO.getAllUserTests(request, true);
		}

		ActionMessages errors = null;

		// initialize the form
		dynaForm.initialize(mapping);

		List sample_Tas = new ArrayList();
		List sample_Tas_Sorted = new ArrayList();
		List testAnalyte_TestResults = new ArrayList();


		if (!StringUtil.isNullorNill(selectedTestId)) {
			Test test = new Test();
			test.setId(selectedTestId);
			testDAO.getData(test);

			List testAnalytes = new ArrayList();
			TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();
			testAnalytes = testAnalyteDAO.getAllTestAnalytesPerTest(test);
			Patient patient = new Patient();
			Person person = new Person();
			SampleHuman sampleHuman = new SampleHuman();
			SampleOrganization sampleOrganization = new SampleOrganization();
			List analyses = new ArrayList();

			Analysis analysis = null;
			SampleItem sampleItem = null;
			Sample_TestAnalyte sample_Ta = null;
			TestAnalyte ta = null;

			try {
				//bugzilla 2227
				analyses = analysisDAO.getAllMaxRevisionAnalysesPerTest(test);
				//bugzilla 2227
				String sampleHasTestRevisions = "false";

				for (int i = 0; i < analyses.size(); i++) {
					analysis = (Analysis) analyses.get(i);
					//bugzilla 2227
					sampleHasTestRevisions = "false";

					Test t = analysis.getTest();
					sampleItem = (SampleItem) analysis.getSampleItem();

					// only show the ones ready to verify that have a status ==
					// results entered/completed only.
					// if
					// (status.equals(SystemConfiguration.getInstance().getAnalysisReadyToVerifyStatus()))
					// {
					if (!StringUtil.isNullorNill(analysis.getStatus()) && analysis.getStatus().equals(SystemConfiguration.getInstance()
							.getAnalysisStatusResultCompleted())) {
						// System.out.println("This is sampleItem " +
						// sampleItem);
						if (sampleItem != null) {
							// bugzilla 1773 need to store sample not sampleId
							// for use in sorting
							String sampleId = sampleItem.getSample().getId();
							String givenSampleId = sampleId;

							if (!StringUtil.isNullorNill(accessionNumber)) {
								Sample sample = new Sample();
								sample.setAccessionNumber(accessionNumber);
								sampleDAO.getSampleByAccessionNumber(sample);
								givenSampleId = sample.getId();

							}

							if (sampleId.equalsIgnoreCase(givenSampleId)) {

								Sample sample = new Sample();

								sample.setId(sampleId);
								sampleDAO.getData(sample);

								//bugzilla 2227
								if (!analysis.getRevision().equals("0")) {
									sampleHasTestRevisions = "true";
								}
								//bugzilla 2513
								if (sample.getStatus().equals(SystemConfiguration.getInstance().getSampleStatusEntry2Complete())) {

									// go to human view
									if (!StringUtil
											.isNullorNill(sample.getId())) {
										sampleHuman.setSampleId(sample.getId());
										sampleHumanDAO
										.getDataBySample(sampleHuman);
										sampleOrganization.setSampleId(sample
												.getId());
										sampleOrganizationDAO
										.getDataBySample(sampleOrganization);
										if (sampleHuman != null) {
											patient.setId(sampleHuman
													.getPatientId());
											if (patient.getId() != null) {
												patientDAO.getData(patient);
												person = patient.getPerson();
												personDAO.getData(person);
											}


										}

									}

									sample_Ta = new Sample_TestAnalyte();
									sample_Ta.setSample(sample);

									// display N/A if no first, last name
									if (person.getFirstName() == null
											&& person.getLastName() == null) {
										person.setFirstName(NOT_APPLICABLE);
										person.setLastName(BLANK);
									}
									if (person.getFirstName() != null
											&& person.getLastName() == null) {
										person.setLastName(BLANK);
									}
									if (person.getFirstName() == null
											&& person.getLastName() != null) {
										person.setFirstName(BLANK);
									}
									sample_Ta.setPerson(person);
									// display N/A if no externalId
									if (patient.getExternalId() == null) {
										patient.setExternalId(NOT_APPLICABLE);
									}
									sample_Ta.setPatient(patient);
									sample_Ta.setAnalysis(analysis);
									//bugzilla 2227
									sample_Ta.setSampleHasTestRevisions(sampleHasTestRevisions);

									sample_Ta.setTestAnalytes(testAnalytes);

									Test_TestAnalyte t_ta = new Test_TestAnalyte();
									t_ta.setTest(t);
									t_ta.setAnalysis(analysis);


									List results = new ArrayList();
									List resultValues = new ArrayList();
									List resultIds = new ArrayList();
									Result result = null;

									TestAnalyte_TestResults[] taTrs = new TestAnalyte_TestResults[testAnalytes
									                                                              .size()];
									TestResult testResult = null;
									List listOfTestResults = null;
									Dictionary dict = null;


									for (int j = 0; j < testAnalytes.size(); j++) {
										ta = (TestAnalyte) testAnalytes.get(j);

										//Begin 1992 store testAnalyte_TestResults within Test_TestAnalyte
										//           and Test_TestAnalyte within Sample_TestAnalyte instead of within
										//           the form for consistency
										TestAnalyte_TestResults taTr = new TestAnalyte_TestResults();
										taTr.setTestAnalyte(ta);
										listOfTestResults = new ArrayList();
										listOfTestResults = testResultDAO
										.getTestResultsByTestAndResultGroup(ta);
										// fill in dictionary values
										for (int k = 0; k < listOfTestResults.size(); k++) {
											testResult = (TestResult) listOfTestResults.get(k);
											if (testResult.getTestResultType().equals(
													SystemConfiguration.getInstance()
													.getDictionaryType())) {
												// get from dictionary
												dict = new Dictionary();
												dict.setId(testResult.getValue());
												dictDAO.getData(dict);
												//bugzilla 1847: use dictEntryDisplayValue
												testResult.setValue(dict.getDictEntryDisplayValue());
											}
										}
										taTr.setTestResults(listOfTestResults);
										taTrs[j] = taTr;
										//end 1992
										result = new Result();
										resultDAO
										.getResultByAnalysisAndAnalyte(
												result, analysis, ta);

										if (result != null) {
											if (result.getTestResult() != null) {
												TestResult tr = result
												.getTestResult();
												String trId = tr.getId();
												if (tr
														.getTestResultType()
														.equals(
																SystemConfiguration
																.getInstance()
																.getDictionaryType())) {
													// get from dictionary
													Dictionary dictionary = new Dictionary();
													dictionary.setId(tr
															.getValue());
													dictDAO.getData(dictionary);
													//bugzilla 1847: use dictEntryDisplayValue
													tr.setValue(dictionary
															.getDictEntryDisplayValue());
												}
												results.add(tr);
												// AIS - bugzilla 1838/1891
												if (tr
														.getTestResultType()
														.equalsIgnoreCase(
																SystemConfiguration
																.getInstance()
																.getDictionaryType())) {
													resultValues.add("");

												} else {
													resultValues.add(result
															.getValue());
												}
												resultIds.add(trId);
											} else {
												results.add(null);
												resultValues.add(null);
												resultIds.add("");
											}
										} else {
											results.add(null);
											resultValues.add(null);
											resultIds.add("");
										}
									}
									t_ta.setTestAnalyteTestResults(taTrs);
									sample_Ta.setSampleTestResultIds(resultIds);
									sample_Ta.setSampleTestResults(results);
									sample_Ta.setTestResultValues(resultValues);
									sample_Ta.setTestTestAnalyte(t_ta);
									sample_Tas.add(sample_Ta);
									//bugzilla 2513
								} else {
									//if human domain sample is not in correct status (HSE2 Completed) then don't process		
								}



							}

						}
					}
				}


				//bugzilla 1856 first sort by accession number
				if (sample_Tas != null && sample_Tas.size() > 0)
					Collections.sort(sample_Tas, ResultsVerificationTestComparator.ACCESSION_NUMBER_COMPARATOR);

				//bugzilla 1856 for each accession number: use recursive sort for parent/child and test.sort_order
				List testsForSample = new ArrayList();
				String currentAccessionNumber = "";
				for (int i = 0; i < sample_Tas.size(); i++) {
					Sample_TestAnalyte sta = (Sample_TestAnalyte)sample_Tas.get(i);
					if (!sta.getSample().getAccessionNumber().equals(currentAccessionNumber)) {
						if (!StringUtil.isNullorNill(currentAccessionNumber)) {
							//bugzilla 1856 sort testsForSample
							testsForSample = completeHierarchyOfTestsForSorting(testsForSample);
							testsForSample = sortTests(testsForSample);
							testsForSample = removePhantomTests(testsForSample);
							sample_Tas_Sorted.addAll(testsForSample);
						}
						testsForSample = new ArrayList();
						currentAccessionNumber = sta.getSample().getAccessionNumber();
					} 
					testsForSample.add(sta);
				}

				//bugzilla 1856 process last batch of tests (for an accession number)
				if (testsForSample.size() > 0) {
					testsForSample = completeHierarchyOfTestsForSorting(testsForSample);
					testsForSample = sortTests(testsForSample);
					testsForSample = removePhantomTests(testsForSample);
					sample_Tas_Sorted.addAll(testsForSample);
				}



			} catch (LIMSRuntimeException lre) {
				// if error then forward to fail and don't update to blank
				// page
				// = false
				//bugzilla 2154
				LogEvent.logError("BatchResultsVerificationViewAction","performAction()",lre.toString());
				errors = new ActionMessages();
				ActionError error = null;
				// bugzilla 1435 error was null
				error = new ActionError("errors.GetException", null, null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
				saveErrors(request, errors);
				request.setAttribute(Globals.ERROR_KEY, errors);
				request.setAttribute(IActionConstants.ALLOW_EDITS_KEY, "false");
				forward = FWD_FAIL;

			}

		}
		// #1347 sort dropdown values
		//bugzilla 2375
		if (testSections != null && testSections.size() > 0)
			Collections.sort(testSections, TestSectionComparator.NAME_COMPARATOR);

		//bugzilla 1844 change sort
		//bugzilla 2375
		if (tests != null && tests.size() > 0)
			Collections.sort(tests, TestComparator.DESCRIPTION_COMPARATOR);

		//bugzilla 1856
		PropertyUtils.setProperty(form, "sample_TestAnalytes", sample_Tas_Sorted);
		PropertyUtils.setProperty(form, "selectedTestSectionId",
				selectedTestSectionId);
		PropertyUtils.setProperty(form, "accessionNumber", accessionNumber);
		PropertyUtils.setProperty(form, "selectedTestId", selectedTestId);
		PropertyUtils.setProperty(form, "tests", tests);
		PropertyUtils.setProperty(form, "testSections", testSections);

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		return "batchresultsverification.edit.title";
	}

	protected String getPageSubtitleKey() {
		return "batchresultsverification.edit.subtitle";
	}

}
