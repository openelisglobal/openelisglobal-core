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
package us.mn.state.health.lims.sampletracking.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.analysisqaevent.dao.AnalysisQaEventDAO;
import us.mn.state.health.lims.analysisqaevent.daoimpl.AnalysisQaEventDAOImpl;
import us.mn.state.health.lims.analysisqaevent.valueholder.AnalysisQaEvent;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.note.dao.NoteDAO;
import us.mn.state.health.lims.note.daoimpl.NoteDAOImpl;
import us.mn.state.health.lims.note.valueholder.Note;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.qaevent.valueholder.QaEventRoutingSwitchSessionHandler;
import us.mn.state.health.lims.referencetables.valueholder.ReferenceTables;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
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
import us.mn.state.health.lims.sampletracking.dao.SampleTrackingDAO;
import us.mn.state.health.lims.sampletracking.daoimpl.SampleTrackingDAOImpl;
import us.mn.state.health.lims.sampletracking.valueholder.SampleTracking;
import us.mn.state.health.lims.sampletracking.valueholder.SampleTrackingCriteria;
import us.mn.state.health.lims.sourceofsample.valueholder.SourceOfSample;
import us.mn.state.health.lims.statusofsample.dao.StatusOfSampleDAO;
import us.mn.state.health.lims.statusofsample.daoimpl.StatusOfSampleDAOImpl;
import us.mn.state.health.lims.statusofsample.valueholder.StatusOfSample;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.testanalyte.dao.TestAnalyteDAO;
import us.mn.state.health.lims.testanalyte.daoimpl.TestAnalyteDAOImpl;
import us.mn.state.health.lims.testanalyte.valueholder.TestAnalyte;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;

/**
 * @author aiswarya raman
 * //AIS - bugzilla 1851/1850/1853
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 * bugzilla 1920 - standards
 * bugzilla 2028 (sub bugzilla 2037)
 */

public class SearchAction extends BaseAction {	

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

	request.setAttribute(ALLOW_EDITS_KEY, "true");
	request.setAttribute(PREVIOUS_DISABLED, "true");
	request.setAttribute(NEXT_DISABLED, "true");
		

	BaseActionForm dynaForm = (BaseActionForm)form;
	
	//bugzilla 2028 sub bugzilla 2038 if coming back from Qa Events accessionNumber will be request parameter
	String accessionNumber = (String)request.getParameter(ACCESSION_NUMBER);
	if (StringUtil.isNullorNill(accessionNumber)) {
		accessionNumber = (String) dynaForm.get("accessionNumber");
	}

	
	Sample sample = new Sample();
	Patient patient = new Patient();
	Person person = new Person();	
	SampleHuman sampleHuman = new SampleHuman();	
	SampleItem sampleItem = new SampleItem();
	List analyses = new ArrayList();	
	StatusOfSample statusOfSample = new StatusOfSample();
    SampleDAO sampleDAO = new SampleDAOImpl();   
	PatientDAO patientDAO = new PatientDAOImpl();	
	SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
	SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();	
	AnalysisDAO analysisDAO = new AnalysisDAOImpl();
	SampleTrackingDAO sampleTrackingDAO = new SampleTrackingDAOImpl();
	StatusOfSampleDAO statusOfSampleDAO = new StatusOfSampleDAOImpl();
	SampleOrganization sampleOrganization = new SampleOrganization();		
	SampleOrganizationDAO sampleOrganizationDAO = new SampleOrganizationDAOImpl();
	AnalysisQaEventDAO analysisQaEventDAO = new AnalysisQaEventDAOImpl();
	List sampleProjects = new ArrayList();	
	
	SampleTrackingCriteria sampleTrackingCriteria = new SampleTrackingCriteria();			

	sampleTrackingCriteria.setAccessionNumberPartial(accessionNumber);
	
	sampleTrackingCriteria.setClientRef((String) dynaForm.get("clientReference"));
	sampleTrackingCriteria.setLastName((String) dynaForm.get("lastname"));
	sampleTrackingCriteria.setFirstName((String) dynaForm.get("firstname"));
	sampleTrackingCriteria.setSubmitter((String) dynaForm.get("selectedOrgId"));
	sampleTrackingCriteria.setReceivedDate((String) dynaForm.get("selectedReceivedDate"));
	sampleTrackingCriteria.setSampleType((String) dynaForm.get("selectedSampleType"));
	sampleTrackingCriteria.setSampleSource((String) dynaForm.get("selectedSampleSource"));
	sampleTrackingCriteria.setExternalId((String) dynaForm.get("externalId"));
	sampleTrackingCriteria.setCollectionDate((String) dynaForm.get("selectedCollectionDate"));
	sampleTrackingCriteria.setProjectId((String) dynaForm.get("selectedProjId"));
	sampleTrackingCriteria.setSortBy((String) dynaForm.get("selectedSortBy"));
	//bugzilla 2455
	sampleTrackingCriteria.setSpecimenOrIsolate((String) dynaForm.getString("selectedSpecimenOrIsolate"));
	
	//bugzilla 2028		
	HttpSession session = (HttpSession)request.getSession();
	boolean comingFromQaEventsEntry = false;
	if (QaEventRoutingSwitchSessionHandler.isSwitchOn(QA_EVENTS_ENTRY_ROUTING_FROM_SAMPLE_TRACKING, session)) {
		QaEventRoutingSwitchSessionHandler.switchOff(QA_EVENTS_ENTRY_ROUTING_FROM_SAMPLE_TRACKING, session);
		comingFromQaEventsEntry = true;
	}

	
	if (StringUtil.isNullorNill((String)dynaForm.get("accessionNumber"))){
	
		List listOfSamples = 
    		sampleTrackingDAO.getAccessionByPatientAndOtherCriteria(sampleTrackingCriteria);
    		
		if (listOfSamples != null && listOfSamples.size() > 0) {
		  sample.setAccessionNumber(((SampleTracking)listOfSamples.get(0)).getAccNum());
		}
		
	} else if ( ((String)dynaForm.get("accessionNumber")).length() == 10 ){		   	    		
		sample.setAccessionNumber((String)dynaForm.get("accessionNumber"));
	}else{
		String accNum = request.getParameter(ACCESSION_NUMBER);
		sample.setAccessionNumber(accNum);		
	}
    
	List testTestAnalytes = new ArrayList();
	
	sampleDAO.getSampleByAccessionNumber(sample);
	
	accessionNumber  = sample.getAccessionNumber();
	
	if (null != sample.getStatus()){	
	
		String status = sample.getStatus();		
		statusOfSample.setCode(status);
		//AIS - bugzilla 1546 and 1649
		statusOfSample.setStatusType(SystemConfiguration.getInstance().getSampleStatusType());		
		
		statusOfSample = statusOfSampleDAO.getDataByStatusTypeAndStatusCode(statusOfSample);
		//bugzilla 2073 display name - not description
		if (null != statusOfSample && !StringUtil.isNullorNill(statusOfSample.getId())){
			
			String sampleStatusName = statusOfSample.getStatusOfSampleName();
			dynaForm.set("status", sampleStatusName);
			
		}else{
			dynaForm.set("status", status);
		}	
		
	}else {
		
		dynaForm.set("status", "");
	}
	
	String clientReference = sample.getClientReference();
	String selectedReceivedDate = sample.getReceivedDateForDisplay();
	String collectionDateForDisplay = sample.getCollectionDateForDisplay();
	//bugzilla 2455
	String specimenOrIsolate = sample.getReferredCultureFlag();
	dynaForm.set("selectedSpecimenOrIsolate", specimenOrIsolate);
		
	dynaForm.set("accessionNumber", accessionNumber);	
	dynaForm.set("clientReference", clientReference);
	dynaForm.set("selectedReceivedDate", selectedReceivedDate);
	dynaForm.set("selectedCollectionDate", collectionDateForDisplay);


	if (!StringUtil.isNullorNill(sample.getId())) {
		
		sampleHuman.setSampleId(sample.getId());
		sampleHumanDAO.getDataBySample(sampleHuman);
		//bugzilla 1773 need to store sample not sampleId for use in sorting
		sampleItem.setSample(sample);
		sampleItemDAO.getDataBySample(sampleItem);
		patient.setId(sampleHuman.getPatientId());			
	
		if (null != patient.getId()){
			
			patientDAO.getData(patient);
			person = patient.getPerson();
			String externalId = patient.getExternalId();
			
			String firstname = person.getFirstName();
			String lastname = person.getLastName();
			
			dynaForm.set("externalId", externalId);
			dynaForm.set("firstname", firstname);
			dynaForm.set("lastname", lastname);
		}else {
			dynaForm.set("externalId", "");
			dynaForm.set("firstname", "");
			dynaForm.set("lastname", "");	
			
		}
		
		
		
		if (sampleItem.getId() != null ){
			TypeOfSample typeOfSample = sampleItem.getTypeOfSample();			
			SourceOfSample sourceOfSample = sampleItem.getSourceOfSample();	
			
			if (typeOfSample != null) {				
				String selectedSampleType = typeOfSample.getId();			
				PropertyUtils.setProperty(form, "selectedSampleType", selectedSampleType);
			}
			
			if (sourceOfSample != null) {				
				String selectedSampleSource = sourceOfSample.getId();
				PropertyUtils.setProperty(form, "selectedSampleSource", selectedSampleSource);				
			}	
			
			
			sampleProjects = sample.getSampleProjects();		

			if (sampleProjects != null && sampleProjects.size() > 0) {
				SampleProject sampleProject = (SampleProject) sampleProjects
						.get(0);	
				//bugzilla 2438			
				String projectid = sampleProject.getProject().getLocalAbbreviation();				
				PropertyUtils.setProperty(form, "selectedProjIdOne", projectid);
				
				if (sampleProjects.size() > 1) {
					SampleProject sampleProject2 = (SampleProject) sampleProjects
							.get(1);
					//bugzilla 2438
					String projectid2 =  sampleProject2.getProject().getLocalAbbreviation();
					PropertyUtils.setProperty(form, "selectedProjIdTwo", projectid2);					
				}
			}			
			
			//Ais - Bugzilla#1711
			//bugzilla 2227 (revisions)
			//bugzilla 2300
			analyses = analysisDAO.getMaxRevisionAnalysesBySampleIncludeCanceled(sampleItem);
			
		}	
		sampleOrganization.setSampleId(sample.getId());
		sampleOrganizationDAO.getDataBySample(sampleOrganization);
		
		if (null != sampleOrganization.getOrganization()){	
			String selectedOrgId = sampleOrganization.getOrganization().getId();				
			PropertyUtils.setProperty(form, "selectedOrgId", selectedOrgId);			
		}	
		
	}

	if (analyses != null) {

		//TestDAO testDAO = new TestDAOImpl();
		TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();
		ResultDAO resultDAO = new ResultDAOImpl();
		DictionaryDAO dictDAO = new DictionaryDAOImpl();
		NoteDAO noteDAO = new NoteDAOImpl();

		// there is one Analysis per Test
		
		String aStatus [] = new String [analyses.size()];
		
		//bugzilla 1856
		analyses = sortTests(analyses);
		
		for (int i = 0; i < analyses.size(); i++) {
			
			Analysis analysis = (Analysis) analyses.get(i);
			//System.out.println("This is ANALYSIS ID " + analysis.getId());
			Test t = (Test) analysis.getTest();
			//System.out.println("This is TEST ID " + t.getId());
			//testDAO.getData(t);			
			
			if (null != analysis.getStatus()){				
				String analysisStatus = analysis.getStatus();				
    			statusOfSample.setCode(analysisStatus);
    			//AIS - bugzilla 1546 and 1649 			
    			statusOfSample.setStatusType(SystemConfiguration.getInstance().getAnalysisStatusType());	
    			statusOfSample = statusOfSampleDAO.getDataByStatusTypeAndStatusCode(statusOfSample);
    			//bugzilla 2073 display name - not description
    			if (null != statusOfSample){
    			
    				String analysisStatusName = statusOfSample.getStatusOfSampleName();
    				aStatus[i]= analysisStatusName;
    				
    			}else{
    				aStatus[i]= analysisStatus;
				}	    				
				
			}else {					
				aStatus[i]= "";			
			}
								
			Test_TestAnalyte test = new Test_TestAnalyte();
			test.setTest(t);
						
            //bugzilla 2028 populated test with analysisQaEvents
			List analysisQaEvents = new ArrayList();
			AnalysisQaEvent analysisQaEvent = new AnalysisQaEvent();
			analysisQaEvent.setAnalysis(analysis);
			analysisQaEvents = analysisQaEventDAO.getAnalysisQaEventsByAnalysis(analysisQaEvent);
			test.setAnalysisQaEvents(analysisQaEvents);

			//AIS - bugzilla 1826 - Removed unwanted tooltip code

			test.setAnalysis(analysis);

			List tAs = testAnalyteDAO
					.getAllTestAnalytesPerTest(t);
			Result[] results = new Result[tAs.size()];
			List[] notes = new ArrayList[tAs.size()];
			// corresponding
			// list
			// of
			// results
			//String[] testResultIds = new String[tAs.size()];
			for (int j = 0; j < tAs.size(); j++) {
				TestAnalyte ta = (TestAnalyte) tAs.get(j);
				// System.out.println("This is TEST_ANALYTE ID " + ta.getId());
				Result result = new Result();
				resultDAO.getResultByAnalysisAndAnalyte(result,
						analysis, ta);
				// System.out.println("Adding result " + result.getId());
				if (result != null) {
					// System.out.println("This is RESULT ID " + result.getId());
					if (result.getId() != null) {
						// fill in dictionary values
						if (result.getResultType().equals(
								SystemConfiguration
										.getInstance()
										.getDictionaryType())) {
							// get from dictionary
							Dictionary dictionary = new Dictionary();
							dictionary.setId(result.getValue());
							dictDAO.getData(dictionary);
							//bugzilla 1847: use dictEntryDisplayValue
							result.setValue(dictionary
									.getDictEntryDisplayValue());

						}
						results[j] = result;
						
						//now get the Notes for this result if exist
						Note note = new Note();
						List notesByResult = new ArrayList();
						note.setReferenceId(result.getId());
						//bugzilla 1922
						//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
						ReferenceTables referenceTables = new ReferenceTables();
						referenceTables.setId(SystemConfiguration.getInstance().getResultReferenceTableId());
						note.setReferenceTables(referenceTables);
	 					notesByResult = noteDAO.getAllNotesByRefIdRefTable(note);
						if (notesByResult != null && notesByResult.size() > 0) {
							notes[j] = notesByResult;
						} else {
							notes[j] = new ArrayList();
						}						
						
					} else {
						results[j] = new Result();
						notes[j] = new ArrayList();
					}
				} else {
					results[j] = new Result();
					notes[j] = new ArrayList();
				}
			}

			test.setTestAnalytes(tAs);
			test.setResults(results);
			test.setNotes(notes);
			
			// Now load test data
			testTestAnalytes.add(test);
			dynaForm.set("aStatus", aStatus);
		}		
	}

	dynaForm.set("testTestAnalytes", testTestAnalytes);
	dynaForm.set("testAnalyteTestResults", new ArrayList());
				
	//System.out.println("testTestAnalytes-- :"+ testTestAnalytes);
	return mapping.findForward("success");     			
	}
	
	protected String getPageTitleKey() {
		return "sampletracking.title";
	}

	protected String getPageSubtitleKey() {
		return "sampletracking.title";
	}

}
