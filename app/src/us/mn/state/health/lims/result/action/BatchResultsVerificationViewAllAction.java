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
import us.mn.state.health.lims.note.dao.NoteDAO;
import us.mn.state.health.lims.note.daoimpl.NoteDAOImpl;
import us.mn.state.health.lims.note.valueholder.Note;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.referencetables.valueholder.ReferenceTables;
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
import us.mn.state.health.lims.sample.valueholder.SampleComparator;
import us.mn.state.health.lims.samplehuman.dao.SampleHumanDAO;
import us.mn.state.health.lims.samplehuman.daoimpl.SampleHumanDAOImpl;
import us.mn.state.health.lims.samplehuman.valueholder.SampleHuman;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.test.valueholder.TestSection;
import us.mn.state.health.lims.testanalyte.dao.TestAnalyteDAO;
import us.mn.state.health.lims.testanalyte.daoimpl.TestAnalyteDAOImpl;
import us.mn.state.health.lims.testanalyte.valueholder.TestAnalyte;

/**
 * @author aiswarya raman
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation. bugzilla 1802
 * modified to edit results on main page (remove test edit buttons) bugzilla
 * bugzilla 1942 cleanup
 * bugzilla 1992 - cleanup (for batchresultsverification: view all)
 *               - one instance of Sample_TestAnalyte per Analysis
 *               - one instance of Test_TestAnalyte per Sample_TestAnalyte
 * bugzilla 2614 - fix to work for NB samples
 */
public class BatchResultsVerificationViewAllAction extends
BatchResultsVerificationBaseAction {

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String forward = FWD_SUCCESS;
		BaseActionForm dynaForm = (BaseActionForm) form;
		String selectedTestId = "";
		String selectedTestSectionId = "";
		String accessionNumber = "";
		List testSections = null;
		List tests = null;

		HttpSession session = request.getSession();

		//bugzilla 2378
		UserTestSectionDAO userTestSectionDAO = new UserTestSectionDAOImpl();
		PatientDAO patientDAO = new PatientDAOImpl();
		SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
		SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
		AnalysisDAO analysisDAO = new AnalysisDAOImpl();
		TestDAO testDAO = new TestDAOImpl();
		SampleDAO sampleDAO = new SampleDAOImpl();
		TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();
		ResultDAO resultDAO = new ResultDAOImpl();
		DictionaryDAO dictDAO = new DictionaryDAOImpl();
		NoteDAO noteDAO = new NoteDAOImpl();

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

		//bugzilla 2513
		List<Sample> samplesToProcess = new ArrayList<Sample>();

		if ((accessionNumber.equalsIgnoreCase(""))
				&& (!selectedTestSectionId.equalsIgnoreCase(""))) {

			List testsByTestSection = testDAO
			.getTestsByTestSection(selectedTestSectionId);

			List analyses = new ArrayList();


			for (int ii = 0; ii < testsByTestSection.size(); ii++) {

				Test test = (Test) testsByTestSection.get(ii);
				//bugzilla 2227
				analyses = analysisDAO.getAllMaxRevisionAnalysesPerTest(test);

				for (int jj = 0; jj < analyses.size(); jj++) {
					Analysis analysis = (Analysis) analyses.get(jj);


					// only show the ones ready to verify that have a status ==
					// results entered/completed only.
					if (!StringUtil.isNullorNill(analysis.getStatus())
							&& analysis
							.getStatus()
							.equals(
									SystemConfiguration
									.getInstance()
									.getAnalysisStatusResultCompleted())) {
						SampleItem sampleItem = (SampleItem)analysis.getSampleItem();
						Sample sample = new Sample();
						//bugzilla 2513
						//for Human domain (clinical) only allow for results verification: sample status HSE2 completed
						//for other domains (i.e. newborn) don't allow for results verification: sample status labels printed
						sample.setAccessionNumber(sampleItem.getSample().getAccessionNumber());
						sampleDAO.getSampleByAccessionNumber(sample);
						String domain = sample.getDomain();
						boolean processSample = true;
						if (sample.getDomain().equals(SystemConfiguration.getInstance().getHumanDomain())) {
							if (!sample.getStatus().equals(SystemConfiguration.getInstance().getSampleStatusEntry2Complete())) {
								processSample = false;
							}
						} else {
							if (sample.getStatus().equals(SystemConfiguration.getInstance().getSampleStatusLabelPrinted())) {
								processSample = false;
							}
						}

						if (processSample)
							samplesToProcess.add(sample);


					}

				}

			}


			//bugzilla 2375
			//bugzilla 2513
			if (samplesToProcess != null && samplesToProcess.size() > 0)
				Collections.sort(samplesToProcess, SampleComparator.ACCESSION_NUMBER_COMPARATOR);

		} else {
			//bugzilla 2513
			Sample sample = new Sample();
			sample.setAccessionNumber(accessionNumber);
			sampleDAO.getSampleByAccessionNumber(sample);

			boolean processSample = true;
			if (sample.getDomain().equals(SystemConfiguration.getInstance().getHumanDomain())) {
				if (!sample.getStatus().equals(SystemConfiguration.getInstance().getSampleStatusEntry2Complete())) {
					processSample = false;
				}
			} else {
				if (sample.getStatus().equals(SystemConfiguration.getInstance().getSampleStatusLabelPrinted())) {
					processSample = false;
				}
			}

			if (processSample)
				samplesToProcess.add(sample);
		}

		List<Object> sample_Tas = new ArrayList<Object>();
		//bugzilla 1856
		List<Object> sample_Tas_Sorted = new ArrayList<Object>();

		//bugzilla 2513
		for (int kk = 0; kk < samplesToProcess.size(); kk++) {

			Sample sample = (Sample)samplesToProcess.get(kk);
			//bugzilla 2227 
			//1900 - this needs to be initialized at sample level
			String sampleHasTestRevisions = "false";


			Patient patient = new Patient();
			Person person = new Person();
			SampleHuman sampleHuman = new SampleHuman();

			SampleItem sampleItem = new SampleItem();
			List analyses = new ArrayList();

			try {

				if (!StringUtil.isNullorNill(sample.getId())) {

					sampleHuman.setSampleId(sample.getId());
					sampleHumanDAO.getDataBySample(sampleHuman);
					sampleItem.setSample(sample);
					sampleItemDAO.getDataBySample(sampleItem);

					if (sampleHuman != null) {
						if (sampleHuman.getPatientId() != null) {
							patient.setId(sampleHuman.getPatientId());
							patientDAO.getData(patient);
							person = patient.getPerson();
						}
					}

					if (sampleItem.getId() != null) {
						//bugzilla 2227
						analyses = analysisDAO
						.getMaxRevisionAnalysesBySample(sampleItem);

					}

				}// if sample ID exists ~~~~

				if (analyses != null) {



					for (int i = 0; i < analyses.size(); i++) {
						Analysis analysis = (Analysis) analyses.get(i);
						//bugzilla 2227
						if (!analysis.getRevision().equals("0")) {
							sampleHasTestRevisions = "true";
						}


						Test t = (Test) analysis.getTest();

						String tsid = analysis.getTestSection().getId();

						Sample_TestAnalyte sample_Ta = new Sample_TestAnalyte();

						// only show the ones ready to verify that have
						// a
						// status == results entered/completed only.
						if (!StringUtil.isNullorNill(analysis
								.getStatus())
								&& analysis
								.getStatus()
								.equals(
										SystemConfiguration
										.getInstance()
										.getAnalysisStatusResultCompleted())) {

							if ((selectedTestSectionId
									.equalsIgnoreCase(""))
									|| (!selectedTestSectionId
											.equalsIgnoreCase("") && selectedTestSectionId
											.equalsIgnoreCase(tsid))) {

								Test_TestAnalyte test = new Test_TestAnalyte();
								test.setTest(t);
								test.setAnalysis(analysis);

								// before setting analysis make sure
								// that
								// parentResult is populated with
								// dictionary
								// values
								// where needed
								// this is for tooltip text
								if (analysis.getParentResult() != null) {
									Result result = analysis
									.getParentResult();

									if (result
											.getResultType()
											.equals(
													SystemConfiguration
													.getInstance()
													.getDictionaryType())) {

										Dictionary dictionary = new Dictionary();
										//bugzilla 2312
										dictionary.setId(result.getTestResult()
												.getValue());
										dictDAO.getData(dictionary);

										result
										.setValue(dictionary
												.getDictEntryDisplayValue());

									}
									analysis.setParentResult(result);
								}

								test.setAnalysis(analysis);
								if (StringUtil.isNullorNill(analysis
										.getIsReportable())) {

									test.setTestIsReportable(t
											.getIsReportable());

								} else {

									test.setTestIsReportable(analysis
											.getIsReportable());

								}

								List tAs = new ArrayList();
								tAs = testAnalyteDAO
								.getAllTestAnalytesPerTest(t);
								TestAnalyte_TestResults[] taTrs = new TestAnalyte_TestResults[tAs
								                                                              .size()];
								Result[] results = new Result[tAs
								                              .size()];
								List[] notes = new ArrayList[tAs.size()];
								String resultValue = null;

								String selectedResultIsReportableFlag = null;

								for (int j = 0; j < tAs.size(); j++) {
									TestAnalyte_TestResults taTr = new TestAnalyte_TestResults();
									TestAnalyte ta = (TestAnalyte) tAs
									.get(j);
									taTr.setTestAnalyte(ta);

									Result result = new Result();
									resultDAO
									.getResultByAnalysisAndAnalyte(
											result, analysis,
											ta);
									resultValue = null;
									selectedResultIsReportableFlag = null;

									if (result != null) {
										if (result.getId() != null) {
											// fill in dictionary values
											if (result
													.getResultType()
													.equals(
															SystemConfiguration
															.getInstance()
															.getDictionaryType())) {
												// get from dictionary
												Dictionary dictionary = new Dictionary();
												dictionary.setId(result
														.getValue());
												dictDAO
												.getData(dictionary);
												result
												.setValue(dictionary
														.getDictEntryDisplayValue());

											}
											// we are now using
											// resultValue
											// for all types N, T, D
											resultValue = result
											.getValue();

											results[j] = result;

											// now get the Notes for
											// this
											// result if
											// exist
											Note note = new Note();
											List notesByResult = new ArrayList();
											note.setReferenceId(result
													.getId());

											// bugzilla 1922
											//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
											ReferenceTables referenceTables = new ReferenceTables();
											referenceTables
											.setId(SystemConfiguration
													.getInstance()
													.getResultReferenceTableId());
											//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
											note
											.setReferenceTables(referenceTables);
											notesByResult = noteDAO
											.getAllNotesByRefIdRefTable(note);
											if (notesByResult != null
													&& notesByResult
													.size() > 0) {
												notes[j] = notesByResult;
											} else {
												notes[j] = new ArrayList();
											}
											selectedResultIsReportableFlag = result
											.getIsReportable();

										} else {
											results[j] = new Result();
											notes[j] = new ArrayList();
											selectedResultIsReportableFlag = ta
											.getIsReportable();
										}
									} else {
										results[j] = new Result();
										notes[j] = new ArrayList();
										selectedResultIsReportableFlag = ta
										.getIsReportable();
									}

									taTr.setResultValue(resultValue);
									taTr
									.setResultIsReportable(selectedResultIsReportableFlag);
									taTr.setResultNotes(notes[j]);
									taTrs[j] = taTr;

								} // for end all components ~~~~
								test.setTestAnalytes(tAs);
								test.setResults(results);
								test.setNotes(notes);
								test.setTestAnalyteTestResults(taTrs);


								sample_Ta
								.setTestTestAnalyte(test);

								sample_Ta.setSample(sample);
								sample_Ta.setAnalysis(analysis);
								//bugzilla 2227
								sample_Ta.setSampleHasTestRevisions(sampleHasTestRevisions);


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

								if (patient.getExternalId() == null) {
									patient.setExternalId(NOT_APPLICABLE);
								}
								sample_Ta.setPatient(patient);

								sample_Tas.add(sample_Ta);


							}// if Test Section Match the test ~~~~

						}// if Status ok ~~~~


					}// for end all analyses list ~~~~

				} // if (analyses != null)~~~~

			} catch (LIMSRuntimeException lre) {
				//bugzilla 2154
				LogEvent.logError("BatchResultsVerificationViewAllAction","performAction()",lre.toString());
				errors = new ActionMessages();
				ActionError error = null;
				error = new ActionError("errors.GetException", null,
						null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
				saveErrors(request, errors);
				request.setAttribute(Globals.ERROR_KEY, errors);
				request.setAttribute(IActionConstants.ALLOW_EDITS_KEY,
						"false");
				return mapping.findForward(FWD_FAIL);

			}

			forward = FWD_SUCCESS;



		}// for end all samplesToProcess list~~~~

		PropertyUtils.setProperty(form, "selectedTestSectionId",
				selectedTestSectionId);
		PropertyUtils.setProperty(form, "accessionNumber", accessionNumber);
		PropertyUtils.setProperty(form, "selectedTestId", selectedTestId);
		PropertyUtils.setProperty(form, "tests", tests);
		PropertyUtils.setProperty(form, "testSections", testSections);

		//show the test results for specific user bases on test_section id (full to admin user)
		//bugzilla 2160
		sample_Tas = userTestSectionDAO.getSampleTestAnalytes(request, sample_Tas, testSections);

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


		PropertyUtils.setProperty(form, "sample_TestAnalytes", sample_Tas_Sorted);

		return mapping.findForward(forward);

	}// Perform function end~~~~

	protected String getPageTitleKey() {
		return "batchresultsverification.edit.title";
	}

	protected String getPageSubtitleKey() {
		return "batchresultsverification.edit.subtitle";
	}

}
