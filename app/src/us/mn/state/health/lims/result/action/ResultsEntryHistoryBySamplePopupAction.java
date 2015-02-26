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
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.organization.valueholder.Organization;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.project.valueholder.Project;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
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
import us.mn.state.health.lims.sampleproject.valueholder.SampleProject;
import us.mn.state.health.lims.sourceofsample.valueholder.SourceOfSample;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.testanalyte.dao.TestAnalyteDAO;
import us.mn.state.health.lims.testanalyte.daoimpl.TestAnalyteDAOImpl;
import us.mn.state.health.lims.testanalyte.valueholder.TestAnalyte;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;

/**
 * @author diane benz
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation. 
 * bugzilla 2227 Amended Results
 * bugzilla 2614 - fix to work for NB samples
 */
public class ResultsEntryHistoryBySamplePopupAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		BaseActionForm dynaForm = (BaseActionForm) form;

		String accessionNumber = null;
		
		if (request.getParameter(ACCESSION_NUMBER) != null) {
		 accessionNumber = (String) request.getParameter(ACCESSION_NUMBER);
	    }


		ActionMessages errors = new ActionMessages();

		// initialize the form
		dynaForm.initialize(mapping);
		
		List testTestAnalytes = new ArrayList();
		
        //bugzilla 2614 allow for NB domain samples

		Patient patient = new Patient();
		Person person = new Person();
        Sample sample = new Sample();
		SampleItem sampleItem = new SampleItem();
		SampleHuman sampleHuman = new SampleHuman();
		SampleOrganization sampleOrganization = new SampleOrganization();
		Organization organization = new Organization();
		List sampleProjects = new ArrayList();
		Project project = new Project();
		Project project2 = new Project();


		if (!StringUtil.isNullorNill(accessionNumber)) {
			try {
				
				List listOfRevisions = new ArrayList();
				
				AnalysisDAO analysisDAO = new AnalysisDAOImpl();
				SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
				ResultDAO resultDAO = new ResultDAOImpl();
				TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();
				TestResultDAO testResultDAO = new TestResultDAOImpl();
				DictionaryDAO dictDAO = new DictionaryDAOImpl();

				
                if (!StringUtil.isNullorNill(accessionNumber)) {
 
                    SampleDAO sampleDAO = new SampleDAOImpl();
                    sample.setAccessionNumber(accessionNumber);
                    sampleDAO.getSampleByAccessionNumber(sample);
    				sampleItem.setSample(sample);
    				sampleItemDAO.getDataBySample(sampleItem);
                    listOfRevisions = analysisDAO.getRevisionHistoryOfAnalysesBySample(sampleItem);
                    //bugzilla 2614 allow for NB domain samples         				
    					PatientDAO patientDAO = new PatientDAOImpl();
    					SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
    					SampleOrganizationDAO sampleOrganizationDAO = new SampleOrganizationDAOImpl();
                    	
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

                    }
                
                String savedTestId = null;
                String totalNumberOfRevisionsToDisplayForTest = "0";
				if (listOfRevisions != null) {
					for (int i = 0; i < listOfRevisions.size(); i++) {
						Analysis revision = (Analysis) listOfRevisions.get(i);
						Test t = (Test) revision.getTest();
						if (!t.getId().equals(savedTestId)) {
							//this is latest revision of a particular test
							totalNumberOfRevisionsToDisplayForTest = String.valueOf(Integer.parseInt(revision.getRevision()) + 1);
							savedTestId = t.getId();
						}
						Test_TestAnalyte tta = new Test_TestAnalyte();
						tta.setTest(t);
						tta.setAnalysis(revision);
						tta.setTotalNumberOfRevisionsToDisplayInHistoryForATest(totalNumberOfRevisionsToDisplayForTest);
						List tAs = new ArrayList();
						tAs = testAnalyteDAO.getAllTestAnalytesPerTest(t);
						TestAnalyte_TestResults[] taTrs = new TestAnalyte_TestResults[tAs.size()];
						Result[] results = new Result[tAs.size()];
						String resultId;
						String resultValue = null;
						String selectedTestResultId = null;
						for (int j = 0; j < tAs.size(); j++) {
							TestAnalyte_TestResults taTr = new TestAnalyte_TestResults();
							TestAnalyte ta = (TestAnalyte) tAs.get(j);
							taTr.setTestAnalyte(ta);

							Result result = new Result();
							resultDAO.getResultByAnalysisAndAnalyte(result,
									revision, ta);
							resultValue = null;
							selectedTestResultId = null;

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
										result.setValue(resultValue);
									}else{
										resultValue = result.getValue();	

									}
									results[j] = result;
									TestResult tr = (TestResult) result
									.getTestResult();
									selectedTestResultId = tr.getId();
								} else {
									results[j] = new Result();
								}
							} else {
								results[j] = new Result();
							}
							List listOfTestResults = testResultDAO
							.getTestResultsByTestAndResultGroup(ta);
							taTr.setTestResults(listOfTestResults);
							taTr
							.setSelectedTestResultId(selectedTestResultId);
							taTr.setResultId(results[j].getId());
							taTr.setResultValue(resultValue);
							taTrs[j] = taTr;
						}

						tta.setTestAnalytes(tAs);
						tta.setResults(results);
						tta.setTestAnalyteTestResults(taTrs);

						testTestAnalytes.add(tta);

					}

				}

			} catch (LIMSRuntimeException lre) {
				// if error then forward to fail and don't update to blank
				// page
				// = false
                //bugzilla 2154
			    LogEvent.logError("ResultsEntryHistoryBySamplePopupAction","performAction()",lre.toString());
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
			
			PropertyUtils.setProperty(dynaForm, "historyTestTestAnalytes", testTestAnalytes);
			
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

            //bug 2614 currently not domain specific
			//PropertyUtils.setProperty(dynaForm, "domain", domain);
			PropertyUtils.setProperty(dynaForm, "accessionNumber", accessionNumber);

			//if (domain != null && domain.equals(humanDomain)) {
			  //forward = FWD_SUCCESS_HUMAN;
			//} else if (domain != null && domain.equals(animalDomain)) {
				// go to animal view
				// System.out.println("Going to animal view");
				//forward = FWD_SUCCESS_ANIMAL;
			//} else {
				forward = FWD_SUCCESS;
			//}

		}

		return mapping.findForward(forward);
	}


	protected String getPageTitleKeyParameter(HttpServletRequest request,
			ActionForm form) {
		String accessionNumber = "";
		if (request.getParameter(ACCESSION_NUMBER) != null) {
			accessionNumber = (String) request.getParameter(ACCESSION_NUMBER);
		}
	
		return accessionNumber;
	}
	
	protected String getPageSubtitleKeyParameter(HttpServletRequest request,
			ActionForm form) {
		String accessionNumber = "";
		if (request.getParameter(ACCESSION_NUMBER) != null) {
			accessionNumber = (String) request.getParameter(ACCESSION_NUMBER);
		}
        return accessionNumber;
	}

	protected String getPageTitleKey() {
		return "resultsentry.history.by.sample.title";
	}

	protected String getPageSubtitleKey() {
		return "resultsentry.history.by.sample.subtitle";
	}

}
