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
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.login.dao.UserTestSectionDAO;
import us.mn.state.health.lims.login.daoimpl.UserTestSectionDAOImpl;
import us.mn.state.health.lims.note.dao.NoteDAO;
import us.mn.state.health.lims.note.daoimpl.NoteDAOImpl;
import us.mn.state.health.lims.note.valueholder.Note;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.person.dao.PersonDAO;
import us.mn.state.health.lims.person.daoimpl.PersonDAOImpl;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.referencetables.valueholder.ReferenceTables;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.result.valueholder.ResultsEntryTestResultComparator;
import us.mn.state.health.lims.result.valueholder.Sample_TestAnalyte;
import us.mn.state.health.lims.result.valueholder.TestAnalyte_TestResults;
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
 * @author diane benz
 * //AIS - bugzilla 1863
 * //AIS - bugzilla 1891
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 * bugzilla 1992 - cleanup  (remove counter definitions to stay consistent)
 * bugzilla 2614 - fix to work for NB samples
 */
public class BatchResultsEntryViewAction extends BaseAction {

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

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		BaseActionForm dynaForm = (BaseActionForm) form;

		//bugzilla 2028 sub bugzilla 2036 if coming back from Qa Events selectedTestId will be request parameter
		//bugzilla 2053 naming of static variable changed
		String selectedTestId = (String)request.getParameter("QA_EVENTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_ENTRY_PARAM_TEST_ID");;
		if (StringUtil.isNullorNill(selectedTestId)) {
			selectedTestId = (String) dynaForm.get("selectedTestId");
		}

		String receivedDateForDisplay = (String) dynaForm
		.get("receivedDateForDisplay");
		String currentDate = (String) dynaForm.get("currentDate");

		List testSections = (List) dynaForm.get("testSections");
		List tests = (List) dynaForm.get("tests");
		//bugzilla 2379
		String selectedTestSectionId = (String)dynaForm.get("selectedTestSectionId");

		//bugzilla 2379
		TestDAO testDAO = new TestDAOImpl();
		UserTestSectionDAO userTestSectionDAO = new UserTestSectionDAOImpl();
		if (!StringUtil.isNullorNill(selectedTestSectionId)) {
			tests = testDAO.getTestsByTestSection(selectedTestSectionId);
		} else {
			testSections = userTestSectionDAO.getAllUserTestSections(request);
			tests = userTestSectionDAO.getAllUserTests(request, true);
		}

		ActionMessages errors = null;

		// bugzilla #1346 add ability to hover over accession number and
		// view patient/person information (first and last name and external id)
		//bugzilla 1387 renamed so more generic
		//bugzilla 2614 allow for NB domain samples

		// initialize the form
		dynaForm.initialize(mapping);

		List sample_Tas = new ArrayList();
		List sample_Tasv = new ArrayList();
		List sample_Tasvt = new ArrayList();

		List testAnalyte_TestResults = new ArrayList();

		if (!StringUtil.isNullorNill(selectedTestId)) {
			Test test = new Test();
			test.setId(selectedTestId);
			testDAO.getData(test);

			List testAnalytes = new ArrayList();
			TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();
			testAnalytes = testAnalyteDAO.getAllTestAnalytesPerTest(test);
			try {
				List analyses = new ArrayList();

				AnalysisDAO analysisDAO = new AnalysisDAOImpl();
				//bugzilla 2227
				analyses = analysisDAO.getAllMaxRevisionAnalysesPerTest(test);

				SampleDAO sampleDAO = new SampleDAOImpl();
				ResultDAO resultDAO = new ResultDAOImpl();
				PatientDAO patientDAO = new PatientDAOImpl();
				PersonDAO personDAO = new PersonDAOImpl();
				SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
				SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
				SampleOrganizationDAO sampleOrganizationDAO = new SampleOrganizationDAOImpl();
				NoteDAO noteDAO = new NoteDAOImpl();

				Patient patient = new Patient();
				Person person = new Person();
				SampleHuman sampleHuman = new SampleHuman();
				SampleOrganization sampleOrganization = new SampleOrganization();
				Analysis analysis = null;
				SampleItem sampleItem = null;
				//bugzilla 2227
				String sampleHasTestRevisions = "false";


				for (int i = 0; i < analyses.size(); i++) {
					analysis = (Analysis) analyses.get(i);
					//bugzilla 2227
					sampleHasTestRevisions = "false";
					//bugzilla 1942 status results verified changed to released (logic is to say if analysis status is not yet released then go ahead)
					if(!StringUtil.isNullorNill(analysis.getStatus()) && !analysis.getStatus().equals(SystemConfiguration.getInstance()
							.getAnalysisStatusReleased()) ) {

						sampleItem = (SampleItem) analysis.getSampleItem();

						//System.out.println("This is sampleItem " + sampleItem);
						if (sampleItem != null) {
							//bugzilla 1773 need to store sample not sampleId for use in sorting
							String sampleId = sampleItem.getSample().getId();
							Sample sample = new Sample();


							//bgm - bugzilla 1639 check sampleId first before using...
							if(sampleId !=null){
								sample.setId(sampleId);
								sampleDAO.getData(sample);
								//bugzilla 2227
								if (!analysis.getRevision().equals("0")) {
									sampleHasTestRevisions = "true";
								}

								// bugzilla #1346 add ability to hover over accession
								// number and
								// view patient/person information (first and last name
								// and external id)
								//bugzilla 2614 allow for NB domain samples
								// bugzilla #1346 add ability to hover over
								// accession number and
								// view patient/person information (first and last
								// name and external id)

								if (!StringUtil.isNullorNill(sample.getId())) {
									//bugzilla 2252
									sampleHuman = new SampleHuman();
									patient = new Patient();
									person = new Person();

									sampleHuman.setSampleId(sample.getId());
									sampleHumanDAO.getDataBySample(sampleHuman);
									sampleOrganization.setSample(sample);
									sampleOrganizationDAO.getDataBySample(sampleOrganization);

									if(sampleHuman !=null){
										patient.setId(sampleHuman.getPatientId());
										if(patient.getId() !=null) {
											patientDAO.getData(patient);
											person = patient.getPerson();
											personDAO.getData(person);
										}
									}
									Sample_TestAnalyte sample_Ta = new Sample_TestAnalyte();
									Sample_TestAnalyte sample_Tav = new Sample_TestAnalyte();

									Sample_TestAnalyte sample_Tavt = new Sample_TestAnalyte();

									// System.out.println("This is
											// sample.getReceivedDate "
									// + sample.getReceivedDate());
									// System.out.println("This is
									// receivedDateForDisplay "
									// + receivedDateForDisplay);
									String locale = SystemConfiguration.getInstance()
									.getDefaultLocale().toString();
									java.sql.Date convertedReceivedDate = DateUtil.convertStringDateToSqlDate(
											receivedDateForDisplay, locale);

									if (sample.getReceivedDate().equals(convertedReceivedDate)
											|| StringUtil.isNullorNill(receivedDateForDisplay)) {
										// exclude samples for which results have been
										// entered
										// (status code?? in analysis or sample)
										sample_Ta.setSample(sample);


										// bugzilla #1346 add ability to hover over
										// accession number and
										// view patient/person information (first and
										// last name and external id)

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
										sample_Ta.setTestAnalytes(testAnalytes);
										List results = new ArrayList();
										List resultValues = new ArrayList();
										List resultValuesn = new ArrayList();
										List resultValuest = new ArrayList();

										List resultIds = new ArrayList();
										List resultLastupdatedList = new ArrayList();
										List sampleResultHasNotesList = new ArrayList();


										for (int j = 0; j < testAnalytes.size(); j++) {
											TestAnalyte ta = (TestAnalyte) testAnalytes
											.get(j);

											Result result = new Result();
											resultDAO.getResultByAnalysisAndAnalyte(
													result, analysis, ta);

											if (result != null) {
												if (result.getTestResult() != null) {


													resultIds.add(result.getId());
													TestResult tr = result
													.getTestResult();
													String trId = tr.getId();
													results.add(trId);
													if (tr.getTestResultType().equalsIgnoreCase(SystemConfiguration.getInstance().getNumericType())){															
														resultValues.add(result.getValue());
														resultValuesn.add(result.getValue());
														resultValuest.add("");

													}else if(tr.getTestResultType().equalsIgnoreCase(SystemConfiguration.getInstance().getTiterType())){

														String resultValue = result.getValue();
														resultValue = resultValue.substring(2,resultValue.length());

														resultValues.add(resultValue);
														resultValuest.add(resultValue);
														resultValuesn.add("");

													}else{
														resultValues.add("");
														resultValuesn.add("");	
														resultValuest.add("");	
													}
													resultLastupdatedList.add(result
															.getLastupdated());

												} else {
													results.add("");
													resultIds.add("0");
													resultValues.add("");
													resultValuesn.add("");													
													resultValuest.add("");
													resultLastupdatedList
													.add(new Timestamp(
															System.currentTimeMillis()));
												}
											} else {
												results.add("");
												resultIds.add("0");
												resultValues.add("");	
												resultValuesn.add("");

												resultValuest.add("");
												resultLastupdatedList
												.add(new Timestamp(
														System.currentTimeMillis()));
											}

											// bugzilla 1942 now get the Notes for this result if
											// exist/ we need to know about notes on batch result entry 
											// in order to determine whether an existing result is allowed to be deleted
											if (result != null && result.getTestResult() != null) {
												Note note = new Note();
												List notesByResult = new ArrayList();
												note.setReferenceId(result.getId());
												//bugzilla 1922
												//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
												ReferenceTables referenceTables = new ReferenceTables();
												referenceTables.setId(SystemConfiguration
														.getInstance()
														.getResultReferenceTableId());
												//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
												note
												.setReferenceTables(referenceTables);
												notesByResult = noteDAO
												.getAllNotesByRefIdRefTable(note);
												if (notesByResult != null
														&& notesByResult.size() > 0) {
													sampleResultHasNotesList.add(true);
												} else {
													sampleResultHasNotesList.add(false);
												}
											} else {//no result
												sampleResultHasNotesList.add(false);
											}


											//END 1942
										}

										sample_Ta.setSampleTestResultIds(results);
										sample_Ta.setResultLastupdatedList(resultLastupdatedList);
										sample_Ta.setTestResultValues(resultValues);
										sample_Ta.setResultIds(resultIds);	
										sample_Ta.setResultHasNotesList(sampleResultHasNotesList);
										//bugzilla 2227
										sample_Ta.setSampleHasTestRevisions(sampleHasTestRevisions);

										sample_Tav.setTestResultValues(resultValuesn);
										sample_Tavt.setTestResultValues(resultValuest);	

										sample_Tas.add(sample_Ta);
										sample_Tasv.add(sample_Tav);
										sample_Tasvt.add(sample_Tavt);

									}
								}
								// bugzilla #1346 add ability to hover over
								// accession number and
								// view patient/person information (first and last
								// name and external id)


							}
						}

					}//end if analysisStatus check


				}//end for loop

				// Load list of TestAnalyte_TestResults for display
				TestResultDAO testResultDAO = new TestResultDAOImpl();
				DictionaryDAO dictDAO = new DictionaryDAOImpl();
				TestAnalyte ta = null;
				TestAnalyte_TestResults ta_Trs = null;
				List listOfTestResults = null;

				for (int i = 0; i < testAnalytes.size(); i++) {
					ta = (TestAnalyte) testAnalytes.get(i);
					ta_Trs = new TestAnalyte_TestResults();
					ta_Trs.setTestAnalyte(ta);

					listOfTestResults = new ArrayList();
					listOfTestResults = testResultDAO
					.getTestResultsByTestAndResultGroup(ta);
					// fill in dictionary values
					for (int j = 0; j < listOfTestResults.size(); j++) {
						TestResult tr = (TestResult) listOfTestResults.get(j);
						if (tr.getTestResultType().equals(
								SystemConfiguration.getInstance()
								.getDictionaryType())) {
							// get from dictionary
							Dictionary dictionary = new Dictionary();
							dictionary.setId(tr.getValue());
							dictDAO.getData(dictionary);
							//bugzilla 1847: use dictEntryDisplayValue
							tr.setValue(dictionary.getDictEntryDisplayValue());
						}
					}
					//bugzilla 1845
					Collections.sort(listOfTestResults, ResultsEntryTestResultComparator.SORTORDER_VALUE_COMPARATOR);
					ta_Trs.setTestResults(listOfTestResults);
					testAnalyte_TestResults.add(ta_Trs);
				}

			} catch (LIMSRuntimeException lre) {
				// if error then forward to fail and don't update to blank
				// page
				// = false
				//bugzilla 2154
				LogEvent.logError("BatchResultsEntryViewAction","performAction()",lre.toString());
				errors = new ActionMessages();
				ActionError error = null;
				error = new ActionError("errors.GetException", null, null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
				saveErrors(request, errors);
				request.setAttribute(Globals.ERROR_KEY, errors);
				request.setAttribute(ALLOW_EDITS_KEY, "false");
				return mapping.findForward(FWD_FAIL);

			}

		}
		// #1347 sort dropdown values
		Collections.sort(testSections, TestSectionComparator.NAME_COMPARATOR);

		//bugzilla 1844
		Collections.sort(tests, TestComparator.DESCRIPTION_COMPARATOR);

		PropertyUtils.setProperty(form, "sample_TestAnalytes", sample_Tas);
		PropertyUtils.setProperty(form, "resultValueN", sample_Tasv);		
		PropertyUtils.setProperty(form, "resultValueT", sample_Tasvt);	

		PropertyUtils.setProperty(form, "testAnalyte_TestResults",
				testAnalyte_TestResults);

		PropertyUtils.setProperty(form, "selectedTestId", selectedTestId);
		//bugzilla 2379
		PropertyUtils.setProperty(form, "selectedTestSectionId", selectedTestSectionId);
		PropertyUtils.setProperty(form, "receivedDateForDisplay",
				receivedDateForDisplay);

		PropertyUtils.setProperty(form, "tests", tests);
		PropertyUtils.setProperty(form, "testSections", testSections);
		PropertyUtils.setProperty(form, "currentDate", currentDate);

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "batchresultsentry.add.title";
		} else {
			return "batchresultsentry.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "batchresultsentry.add.subtitle";
		} else {
			return "batchresultsentry.edit.subtitle";
		}
	}

}
