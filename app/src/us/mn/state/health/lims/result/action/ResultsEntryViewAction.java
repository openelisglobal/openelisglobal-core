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
import java.util.HashMap;
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
import us.mn.state.health.lims.analysisqaevent.dao.AnalysisQaEventDAO;
import us.mn.state.health.lims.analysisqaevent.daoimpl.AnalysisQaEventDAOImpl;
import us.mn.state.health.lims.analysisqaevent.valueholder.AnalysisQaEvent;
import us.mn.state.health.lims.common.action.BaseActionForm;
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
import us.mn.state.health.lims.organization.valueholder.Organization;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.project.valueholder.Project;
import us.mn.state.health.lims.referencetables.valueholder.ReferenceTables;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.result.valueholder.ResultsEntryTestResultComparator;
import us.mn.state.health.lims.result.valueholder.TestAnalyte_TestResults;
import us.mn.state.health.lims.result.valueholder.TestResult_AddedReflexTests;
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
import us.mn.state.health.lims.sampleproject.valueholder.SampleProject;
import us.mn.state.health.lims.sourceofsample.valueholder.SourceOfSample;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.testanalyte.dao.TestAnalyteDAO;
import us.mn.state.health.lims.testanalyte.daoimpl.TestAnalyteDAOImpl;
import us.mn.state.health.lims.testanalyte.valueholder.TestAnalyte;
import us.mn.state.health.lims.testreflex.dao.TestReflexDAO;
import us.mn.state.health.lims.testreflex.daoimpl.TestReflexDAOImpl;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;

/**
 * @author diane benz
 * AIS - bugzilla 1891
 * bugzilla 2053
 * bugzilla 2614 - fix to work for NB samples
 */
public class ResultsEntryViewAction extends ResultsEntryBaseAction {

	private boolean isNew = false;
	private TestReflexDAO reflexDAO;
	private ResultDAO resultDAO;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		BaseActionForm dynaForm = (BaseActionForm) form;

		int pageResultCounter = 0;
		int pageResultIndex = 0;

		String accessionNumber = (String) request
				.getAttribute(ACCESSION_NUMBER);

		// server side validation of accessionNumber in PreViewAction
		ActionMessages errors = null;

		try {
			errors = new ActionMessages();
			errors = validateAccessionNumber(request, errors, dynaForm);
			// System.out.println("Just validated accessionNumber");
		} catch (Exception e) {
    		//bugzilla 2154
			LogEvent.logError("ResultsEntryViewAction","performAction()",e.toString());
			ActionError error = new ActionError("errors.ValidationException",
					null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}

		//bugzilla 2028		
		
		//if passing in accession number from another module:
		accessionNumber = (String)request.getParameter(ACCESSION_NUMBER);
		if (StringUtil.isNullorNill(accessionNumber)) {
				accessionNumber = (String)dynaForm.get("accessionNumber");
		}

		// initialize the form
		dynaForm.initialize(mapping);

		if (!StringUtil.isNullorNill(accessionNumber)) {
			Sample sample = new Sample();
			SampleDAO sampleDAO = new SampleDAOImpl();
			sample.setAccessionNumber(accessionNumber);

			List testTestAnalytes = new ArrayList();
			try {
				sampleDAO.getSampleByAccessionNumber(sample);

				if (!StringUtil.isNullorNill(sample.getStatus()) && sample.getStatus().equals(SystemConfiguration.getInstance()
								.getSampleStatusLabelPrinted())) {
					dynaForm.set("accessionNumber", accessionNumber);
					request.setAttribute(ALLOW_EDITS_KEY,
							"false");
					return mapping.findForward(FWD_FAIL);
				}

			} catch (LIMSRuntimeException lre) {
    			//bugzilla 2154
			    LogEvent.logError("ResultsEntryViewAction","performAction()",lre.toString());
				errors = new ActionMessages();
				ActionError error = null;
				error = new ActionError("errors.GetException", null, null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
				saveErrors(request, errors);
				request.setAttribute(Globals.ERROR_KEY, errors);
				request.setAttribute(ALLOW_EDITS_KEY, "false");
				return mapping.findForward(FWD_FAIL);

			}

              //bugzilla 2614 allow for NB domain samples

				Patient patient = new Patient();
				Person person = new Person();
				SampleHuman sampleHuman = new SampleHuman();
				SampleOrganization sampleOrganization = new SampleOrganization();
				Organization organization = new Organization();
				List sampleProjects = new ArrayList();
				Project project = new Project();
				Project project2 = new Project();
				SampleItem sampleItem = new SampleItem();
				List analyses = new ArrayList();
				String[] selectedTestIsReportableFlags = null;
				String[] selectedResultIsReportableFlags = null;
				String[] selectedTestResultIds = null;
				String[] resultValueN = null;

				// System.out.println("Now try to get data for accession number
				// ");
				try {

					PatientDAO patientDAO = new PatientDAOImpl();
					SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
					SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
					SampleOrganizationDAO sampleOrganizationDAO = new SampleOrganizationDAOImpl();
					AnalysisDAO analysisDAO = new AnalysisDAOImpl();
					TestResultDAO testResultDAO = new TestResultDAOImpl();
					AnalysisQaEventDAO analysisQaEventDAO = new AnalysisQaEventDAOImpl();
					reflexDAO = new TestReflexDAOImpl();

					if (!StringUtil.isNullorNill(sample.getId())) {
						sampleHuman.setSampleId(sample.getId());
						sampleHumanDAO.getDataBySample(sampleHuman);
						sampleOrganization.setSample(sample);
						sampleOrganizationDAO
								.getDataBySample(sampleOrganization);
						// bugzilla 1773 need to store sample not sampleId for
						// use in sorting
						sampleItem.setSample(sample);
						sampleItemDAO.getDataBySample(sampleItem);

						// bgm - bugzilla 1584 check for sampleHuman, person, &
						// patient info first before getting info.
						if (sampleHuman != null) {
							if (sampleHuman.getPatientId() != null) {
								patient.setId(sampleHuman.getPatientId());
								patientDAO.getData(patient);
								person = patient.getPerson();
							}
						}
                        //bugzilla 2227
						analyses = analysisDAO.getMaxRevisionAnalysesBySample(sampleItem);
						
						//bugzilla 2433, remove the analysis test section that does not assigned to the user
						UserTestSectionDAO userTestSectionDAO = new UserTestSectionDAOImpl();
						List testSections = userTestSectionDAO.getAllUserTestSections(request);
						analyses = userTestSectionDAO.getAnalyses(request,analyses,testSections);	

					}
					organization = (Organization) sampleOrganization
							.getOrganization();
					sampleProjects = sample.getSampleProjects();

					if (sampleProjects != null && sampleProjects.size() > 0) {
						SampleProject sampleProject = (SampleProject) sampleProjects
								.get(0);
						project = sampleProject.getProject();
						if (sampleProjects.size() > 1) {
							SampleProject sampleProject2 = (SampleProject) sampleProjects
									.get(1);
							project2 = sampleProject2.getProject();
						}
					}

					if (analyses != null) {
						
						//bugzilla 1856
						analyses = sortTests(analyses);


						TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();
						resultDAO = new ResultDAOImpl();
						DictionaryDAO dictDAO = new DictionaryDAOImpl();
						NoteDAO noteDAO = new NoteDAOImpl();


						// there is one Analysis per Test
						// bugzilla 1780 added this for oracle invalid number
						// bug (if result already processed don't process again
						// using dictionary value instead of dictionary id)
						List resultsAlreadyProcessed = new ArrayList();
						selectedTestIsReportableFlags = new String[analyses
								.size()];

						List tAs = new ArrayList();
						//bugzilla 2532/2627
						List parents = new ArrayList();
						// do initial count and other preprocessing
						for (int i = 0; i < analyses.size(); i++) {
							Analysis analysis = (Analysis) analyses.get(i);
							Test t = (Test) analysis.getTest();
							tAs = new ArrayList();
							tAs = testAnalyteDAO.getAllTestAnalytesPerTest(t);
							pageResultCounter += tAs.size();
							//bugzilla 2532/2627 have list of tests already at level 0 (= parents) to determine whether a child test can be unlinked
							if (analysis.getParentAnalysis() == null) {
								parents.add(analysis.getTest().getId());
							}
						}

						selectedResultIsReportableFlags = new String[pageResultCounter];
						resultValueN = new String[pageResultCounter];
						selectedTestResultIds = new String[pageResultCounter];

						tAs = new ArrayList();

						for (int i = 0; i < analyses.size(); i++) {
							Analysis analysis = (Analysis) analyses.get(i);
							Test t = (Test) analysis.getTest();
							Test_TestAnalyte test = new Test_TestAnalyte();
							test.setTest(t);

							// before setting analysis make sure that
							// parentResult is populated with dictionary values
							// where needed
							// this is for tooltip text
							if (analysis.getParentResult() != null) {
								Result result = analysis.getParentResult();
								if (result.getResultType().equals(
										SystemConfiguration.getInstance()
												.getDictionaryType())
										&& !resultsAlreadyProcessed
												.contains(new Integer(result
														.getId()))) {
									// bugzilla 1780 added this for oracle
									// invalid number bug (if result already
									// processed don't process again using
									// dictionary value instead of dictionary
									// id)
									// get from dictionary
									Dictionary dictionary = new Dictionary();
									//bugzilla 2312
									dictionary.setId(result.getTestResult().getValue());
									dictDAO.getData(dictionary);
									// bugzilla 1780 added this for oracle
									// invalid number bug (if result already
									// processed don't process again using
									// dictionary value instead of dictionary
									// id)
									resultsAlreadyProcessed.add(new Integer(
											result.getId()));

									result.setValue(dictionary
											.getDictEntryDisplayValue());

								}
								analysis.setParentResult(result);
							}

                            //bugzilla 2028 populated test with analysisQaEvents
							List analysisQaEvents = new ArrayList();
							AnalysisQaEvent analysisQaEvent = new AnalysisQaEvent();
							analysisQaEvent.setAnalysis(analysis);
							analysisQaEvents = analysisQaEventDAO.getAnalysisQaEventsByAnalysis(analysisQaEvent);
							test.setAnalysisQaEvents(analysisQaEvents);
							
							test.setAnalysis(analysis);
							if (StringUtil.isNullorNill(analysis
									.getIsReportable())) {
								selectedTestIsReportableFlags[i] = t
										.getIsReportable();
							} else {
								selectedTestIsReportableFlags[i] = analysis
										.getIsReportable();
							}

							tAs = new ArrayList();
							tAs = testAnalyteDAO.getAllTestAnalytesPerTest(t);
							TestAnalyte_TestResults[] taTrs = new TestAnalyte_TestResults[tAs
									.size()];
							Result[] results = new Result[tAs.size()];
							List[] notes = new ArrayList[tAs.size()];

							String resultId;
							List notesList = new ArrayList();
							// corresponding
							// list
							// of
							// results
							String resultValue = null;
							String selectedTestResultId = null;
							String selectedResultIsReportableFlag = null;
							for (int j = 0; j < tAs.size(); j++) {
								TestAnalyte_TestResults taTr = new TestAnalyte_TestResults();
								TestAnalyte ta = (TestAnalyte) tAs.get(j);
								taTr.setTestAnalyte(ta);

								Result result = new Result();
								resultDAO.getResultByAnalysisAndAnalyte(result,
										analysis, ta);
								resultValue = null;
								selectedTestResultId = null;
								selectedResultIsReportableFlag = null;

								if (result != null) {
									if (result.getId() != null) {
										// fill in dictionary values
										if (result.getResultType().equals(
												SystemConfiguration
														.getInstance()
														.getDictionaryType())) {
											// get from dictionary
											Dictionary dictionary = new Dictionary();
											//bugzilla 2312
											dictionary.setId(result.getTestResult().getValue());
											dictDAO.getData(dictionary);
											result
													.setValue(dictionary
															.getDictEntryDisplayValue());

										}if (result.getResultType().equals(
												SystemConfiguration
												.getInstance()
												.getTiterType())){
											
											resultValue = result.getValue();
											resultValue = resultValue.substring(2,resultValue.length());
										}else{
											resultValue = result.getValue();	
											
										}
										
										
                                        //we are now using resultValue for all types N, T, D									
										
										results[j] = result;

										// now get the Notes for this result if
										// exist
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
											notes[j] = notesByResult;
										} else {
											notes[j] = new ArrayList();
										}
										selectedResultIsReportableFlag = result
												.getIsReportable();
										TestResult tr = (TestResult) result
												.getTestResult();
										selectedTestResultId = tr.getId();
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
								List listOfTestResults = testResultDAO
										.getTestResultsByTestAndResultGroup(ta);
								List newListOfTestResults = new ArrayList();
								List totalAddedTests = new ArrayList();
								
								List listOfComparableTestResultsAndReflexTests = new ArrayList();
								// fill in dictionary values

								for (int k = 0; k < listOfTestResults.size(); k++) {
									TestResult tr = new TestResult();
									tr = (TestResult) listOfTestResults.get(k);

									//bugzilla 2184
									String sortTestResultValue = "";
									// bugzilla 1684: added testAnalyte to
									// criteria
									List addedTests = getReflexTestsForTestResultAndTestAnalyte(
											tr, ta);
									if (tr.getTestResultType().equals(
											SystemConfiguration.getInstance()
													.getDictionaryType())) {
										// get from dictionary
										Dictionary dictionary = new Dictionary();
										dictionary.setId(tr.getValue());
										dictDAO.getData(dictionary);

										tr.setValue(dictionary
												.getDictEntryDisplayValue());
										sortTestResultValue = dictionary.getDictEntry();

									} else {
										sortTestResultValue = tr.getValue();
									}
									//this is used for sorting testResults (dropdown) and corresponding list of possible added reflex tests)
									TestResult_AddedReflexTests tr_Arf = new TestResult_AddedReflexTests();
									tr_Arf.setSortTestResultValue(sortTestResultValue);
									tr_Arf.setTestResult(tr);
									tr_Arf.setAddedReflexTests(addedTests);
									listOfComparableTestResultsAndReflexTests.add(tr_Arf);
								}
								resultValueN[pageResultIndex] = resultValue;
								selectedTestResultIds[pageResultIndex] = selectedTestResultId;
								selectedResultIsReportableFlags[pageResultIndex] = selectedResultIsReportableFlag;
											
								//bugzilla 1845
								Collections.sort(listOfComparableTestResultsAndReflexTests, ResultsEntryTestResultComparator.SORTORDER_VALUE_COMPARATOR);
								for (int x = 0; x < listOfComparableTestResultsAndReflexTests.size(); x++) {
									TestResult_AddedReflexTests trart = (TestResult_AddedReflexTests)listOfComparableTestResultsAndReflexTests.get(x);
									newListOfTestResults.add(trart.getTestResult());
									totalAddedTests.add(trart.getAddedReflexTests());
								}
								taTr.setTestResults(newListOfTestResults);
 								taTr.setTestResultReflexTests(totalAddedTests);
								taTr
										.setSelectedTestResultId(selectedTestResultId);
								taTr.setResultId(results[j].getId());
								taTr.setResultValue(resultValue);
								taTr.setResultNotes(notes[j]);
								
								//bugzilla 1798
								taTr.setChildType(getChildType(analysis));
								taTr.setCanBeLinked(canTestBeLinkedAsChild(analyses, analysis));
								
								//bugzilla 2532/2627
								String canBeUnlinked = TRUE;
								if (parents.contains(analysis.getTest().getId())) {
									canBeUnlinked = FALSE;
								}
								taTr.setCanBeUnlinked(canBeUnlinked);
								
								taTrs[j] = taTr;
								pageResultIndex++;
							}

							test.setTestAnalytes(tAs);
							test.setResults(results);
							test.setNotes(notes);
							test.setTestAnalyteTestResults(taTrs);
 							
							testTestAnalytes.add(test);
			
						}

					}
					
					
				
				} catch (LIMSRuntimeException lre) {
					// if error then forward to fail and don't update to blank
					// page
					// = false
                    //bugzilla 2154
			        LogEvent.logError("ResultsEntryViewAction","performAction()",lre.toString());
					errors = new ActionMessages();
					ActionError error = null;
					error = new ActionError("errors.GetException", null, null);
					errors.add(ActionMessages.GLOBAL_MESSAGE, error);
					saveErrors(request, errors);
					request.setAttribute(Globals.ERROR_KEY, errors);
					request.setAttribute(ALLOW_EDITS_KEY,
							"false");
					return mapping.findForward(FWD_FAIL);

				}

				// populate form from valueholder
				//bugzilla 2227
				PropertyUtils.setProperty(dynaForm, "sampleLastupdated", sample
						.getLastupdated());
				PropertyUtils.setProperty(dynaForm, "patientFirstName", person
						.getFirstName());
				PropertyUtils.setProperty(dynaForm, "patientLastName", person
						.getLastName());
				PropertyUtils.setProperty(dynaForm, "patientId", patient
						.getExternalId());
				PropertyUtils.setProperty(dynaForm, "birthDateForDisplay",
						(String) patient.getBirthDateForDisplay());
				TypeOfSample typeOfSample = sampleItem.getTypeOfSample();
				SourceOfSample sourceOfSample = sampleItem.getSourceOfSample();
				if (typeOfSample == null) {
					PropertyUtils.setProperty(dynaForm, "typeOfSample",
							new TypeOfSample());
				} else {
					PropertyUtils.setProperty(dynaForm, "typeOfSample",
							typeOfSample);
				}
				if (sourceOfSample == null) {
					PropertyUtils.setProperty(dynaForm, "sourceOfSample",
							new SourceOfSample());
				} else {
					PropertyUtils.setProperty(dynaForm, "sourceOfSample",
							sourceOfSample);
				}

				PropertyUtils.setProperty(dynaForm, "sourceOther", sampleItem
						.getSourceOther());
				PropertyUtils.setProperty(dynaForm, "receivedDateForDisplay",
						(String) sample.getReceivedDateForDisplay());
				PropertyUtils.setProperty(dynaForm, "collectionDateForDisplay",
						(String) sample.getCollectionDateForDisplay());
				// bugzilla 1855
				PropertyUtils.setProperty(dynaForm, "referredCultureFlag",
						(String) sample.getReferredCultureFlag());

				if (organization == null) {
					PropertyUtils.setProperty(dynaForm, "organization",
							new Organization());
				} else {
					PropertyUtils.setProperty(dynaForm, "organization",
							organization);
				}

				if (project == null) {
					PropertyUtils.setProperty(dynaForm, "project",
							new Project());
				} else {
					PropertyUtils.setProperty(dynaForm, "project", project);
				}

				if (project2 == null) {
					PropertyUtils.setProperty(dynaForm, "project2",
							new Project());
				} else {
					PropertyUtils.setProperty(dynaForm, "project2", project2);
				}
				// System.out.println("Setting testTestAnalytes in form " +
				// testTestAnalytes.size());
				PropertyUtils.setProperty(dynaForm, "testTestAnalytes",
						testTestAnalytes);

				// reload accession number
				PropertyUtils.setProperty(dynaForm, "accessionNumber",
						accessionNumber);
				//bugzilla 2614 currently not domain specific
				//PropertyUtils.setProperty(dynaForm, "domain", domain);
	
				// bugzilla 1802
				PropertyUtils.setProperty(dynaForm,
						"selectedTestIsReportableFlags",
						selectedTestIsReportableFlags);
				PropertyUtils.setProperty(dynaForm,
						"selectedResultIsReportableFlags",
						selectedResultIsReportableFlags);
				PropertyUtils.setProperty(dynaForm, "selectedTestResultIds",
						selectedTestResultIds);
				PropertyUtils.setProperty(dynaForm, "resultValueN",
						resultValueN);

				forward = FWD_SUCCESS;

		}

		return mapping.findForward(forward);
	}

	//bugzilla 1798
	private String getChildType(Analysis analysis) {
		String childType = CHILD_TYPE_NONE;
		
		if (analysis.getParentAnalysis()!= null && analysis.getParentResult() != null) {
			if (reflexDAO.isReflexedTest(analysis)) {
				childType = CHILD_TYPE_REFLEX;
			} else {
				childType = CHILD_TYPE_LINK;
			}
		} 
		
		return childType;
	}
	
	private String canTestBeLinkedAsChild(List analyses, Analysis analysis) {
		String canBeLinked = TRUE;
		
		//it can't be linked 1) if no other tests on sample
		//                   2) if only other tests on sample have no results entered
		//                   3) if only other test on sample with result entered is already linked as parent OR child
		//                   4) if already linked
		
		//there is no other test besides this one (no linking possible)
		if (analyses.size() == 1) canBeLinked = FALSE;
		
		//this test is already a child of another test - no linking possible
		if (analysis.getParentAnalysis() != null) canBeLinked = FALSE;
		
		if (canBeLinked.equals(TRUE)) {
			
			int countPossibleParentAnalyses =0;
			//bugzilla 2532 for all analyses: these can be potential parents to select from-> get a list of already linked child test ids (not analysis ids) -> don't allow to link same test twice to a parent
			HashMap parentToListOfLinkedTestIdsMap = new HashMap();
			for (int i = 0; i < analyses.size(); i++) {
				Analysis currentAnalysis = (Analysis)analyses.get(i);
				if (currentAnalysis.getParentResult() != null) {
					if (!parentToListOfLinkedTestIdsMap.containsKey(currentAnalysis.getParentResult().getId())) {
						parentToListOfLinkedTestIdsMap.put(currentAnalysis.getParentResult().getId(), new ArrayList());
					}
				}
			}
			//bugzilla 2532 attach list of already linked child test ids to each potential parent result
			for (int i = 0; i < analyses.size(); i++) {
				Analysis currentAnalysis = (Analysis)analyses.get(i);
				if (currentAnalysis.getParentResult() != null) {
					List list = (ArrayList)parentToListOfLinkedTestIdsMap.get(currentAnalysis.getParentResult().getId());
					list.add(currentAnalysis.getTest().getId());
					parentToListOfLinkedTestIdsMap.put(currentAnalysis.getParentAnalysis().getId(), list);
				}
			}
			for (int i = 0; i < analyses.size(); i++) {
				Analysis currentAnalysis = (Analysis)analyses.get(i);
				Analysis parentAnalysisOfCurrentAnalysis = currentAnalysis.getParentAnalysis();
				//IF THIS FOLLOWING LOGIC IS CHANGED - ALSO CHANGE SIMILAR LOGIC IN ResultsEntryLinkChildTestToParentTestResultsPopupAction
				//this test cannot be the same as the test to be linked and it can not be parent of a test to be linked && test to be linked must have at least one result
				//bugzilla 2532 AND test cannot be linked to a parent result that already has that same test linked to it
				List results = resultDAO.getResultsByAnalysis(currentAnalysis);
				String testIdOfAnalysis = (String)analysis.getTest().getId();
				boolean resultIsPossibleParent = false;
				if ((! currentAnalysis.getId().equals(analysis.getId()) && (parentAnalysisOfCurrentAnalysis == null || !(parentAnalysisOfCurrentAnalysis.getId().equals(analysis.getId())))) && (results != null && results.size() > 0)) {
					    for (int j = 0; j < results.size(); j++) {
					    	Result result = (Result)results.get(j);
					    	if (parentToListOfLinkedTestIdsMap.containsKey(result.getId())) {
					    		List listOfTestIds = (ArrayList)parentToListOfLinkedTestIdsMap.get(result.getId());
					    		if (listOfTestIds != null && listOfTestIds.size() > 0) {
					    			if (!listOfTestIds.contains(testIdOfAnalysis)) {
					    				resultIsPossibleParent = true;
					    			}
					    		} else {
					    			resultIsPossibleParent = true;
					    		}
					    	} else {
					    		resultIsPossibleParent = true;
					    	}
					    }
					//if at least one of the results on this analysis qualifies as parent then we need to count this analysis as possible parent
					if (resultIsPossibleParent)
                    countPossibleParentAnalyses++;
				}

			}
			
			if (countPossibleParentAnalyses == 0) {
				canBeLinked = FALSE;
			}
		}
		return canBeLinked;
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
	

}
