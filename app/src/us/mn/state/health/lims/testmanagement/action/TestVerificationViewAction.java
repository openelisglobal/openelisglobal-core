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
package us.mn.state.health.lims.testmanagement.action;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.person.dao.PersonDAO;
import us.mn.state.health.lims.person.daoimpl.PersonDAOImpl;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.provider.dao.ProviderDAO;
import us.mn.state.health.lims.provider.daoimpl.ProviderDAOImpl;
import us.mn.state.health.lims.provider.valueholder.Provider;
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
import us.mn.state.health.lims.statusofsample.dao.StatusOfSampleDAO;
import us.mn.state.health.lims.statusofsample.daoimpl.StatusOfSampleDAOImpl;
import us.mn.state.health.lims.statusofsample.valueholder.StatusOfSample;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.testmanagement.valueholder.TestManagementRoutingSwitchSessionHandler;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author aiswarya raman
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 * 
 * bugzilla 1774 route from results entry to test management (verification) passing accession number
 * added this action in order to use TestVerificationAction as an initializing action that
 * sets the session variable IActionConstants.TEST_MANAGEMENT_ROUTING_FROM_RESULTS_ENTRY to false
 * bugzilla 1942: status changes
 * bugzilla 2053
 */
public class TestVerificationViewAction extends BaseAction {

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		// System.out.println("I am in TestVerificationViewAction ");

		BaseActionForm testManagementForm = (BaseActionForm) form;

		if (request.getParameter(ID) == null) {
			testManagementForm.initialize(mapping);
		}

		String accessionNumber = null;

		accessionNumber = (String)request.getParameter(ACCESSION_NUMBER);
		if (StringUtil.isNullorNill(accessionNumber)) {
				accessionNumber = (String)testManagementForm.get("accessionNumber");
		}
	


		testManagementForm.initialize(mapping);

		Date today = Calendar.getInstance().getTime();
		String dateAsText = DateUtil.formatDateAsText(today);

		Sample sample = new Sample();
		SampleItem sampleItem = new SampleItem();
		List analyses = new ArrayList();
		SampleDAO sampleDAO = new SampleDAOImpl();
		SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
		AnalysisDAO analysisDAO = new AnalysisDAOImpl();
		//bugzilla 2532 removed line
		Patient patient = new Patient();
		Person person = new Person();
		SampleHuman sampleHuman = new SampleHuman();
		PatientDAO patientDAO = new PatientDAOImpl();
		SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
		ProviderDAO providerDAO = new ProviderDAOImpl();
		SampleOrganization sampleOrganization = new SampleOrganization();
		PersonDAO personDAO = new PersonDAOImpl();
		SampleOrganizationDAO sampleOrganizationDAO = new SampleOrganizationDAOImpl();
		Provider provider = new Provider();
		Person providerPerson = new Person();
		List sampleProjects = new ArrayList();
		StatusOfSample statusOfSample = new StatusOfSample();
		StatusOfSampleDAO statusOfSampleDAO = new StatusOfSampleDAOImpl();

		sample.setAccessionNumber(accessionNumber);
		sampleDAO.getSampleByAccessionNumber(sample);

		if (!StringUtil.isNullorNill(sample.getId())) {

			String clientReference = sample.getClientReference();
			String collectionDateForDisplay = sample.getCollectionDateForDisplay();
			String collectionTimeForDisplay = sample.getCollectionTimeForDisplay();
			String receivedDateForDisplay = sample.getReceivedDateForDisplay();
			String stickerReceivedFlag = sample.getStickerReceivedFlag();
			String referredCultureFlag = sample.getReferredCultureFlag();

			PropertyUtils.setProperty(form, "accessionNumber", accessionNumber);

			PropertyUtils.setProperty(form, "clientReference", clientReference);
			PropertyUtils.setProperty(form, "collectionDateForDisplay", collectionDateForDisplay);		
			PropertyUtils.setProperty(form, "collectionTimeForDisplay", collectionTimeForDisplay);	
			PropertyUtils.setProperty(form, "receivedDateForDisplay", receivedDateForDisplay);	
			PropertyUtils.setProperty(form, "stickerReceivedFlag", stickerReceivedFlag);	
			PropertyUtils.setProperty(form, "referredCultureFlag", referredCultureFlag);				

			//bugzilla 1773 need to store sample not sampleId for use in sorting
			sampleItem.setSample(sample);
			sampleItemDAO.getDataBySample(sampleItem);

			
			//bugzilla 1942 is at least one linked analysis already reported (printed date NOT null)?
			boolean isAnalysisReported = false;

			if (sampleItem.getId() != null) {
				//bugzilla 2227
				analyses = analysisDAO.getMaxRevisionAnalysesBySample(sampleItem);
			}

			for (int i = 0; i < analyses.size(); i++) {
				Analysis analysis = (Analysis) analyses.get(i);
				if (analysis.getPrintedDate() != null) {
					isAnalysisReported = true;
					break;
				}
			}
			//bugzilla 1942: change logic to disable "add tests" button and to disable "edit sample demographics" button	
			String editOption = "";
			//bugzilla 2028: added disable property for QA Events
			String qaEventsAllowEdit = "";
			if (null != sample.getStatus()) {

				if ((sample.getStatus().equals(SystemConfiguration.getInstance().getSampleStatusReleased()) && !isAnalysisReported)
						|| (sample.getStatus().equals(SystemConfiguration.getInstance().getSampleStatusEntry2Complete()))) {
					editOption = "true";
				}

				//bugzilla 2501
				HttpSession session = request.getSession();
				boolean qaEventsForCircularReferenceDisabled = false;
				if (TestManagementRoutingSwitchSessionHandler.isSwitchOn(IActionConstants.TEST_MANAGEMENT_ROUTING_FROM_QAEVENTS_ENTRY, session)) {
				    qaEventsForCircularReferenceDisabled = true;
				}
				if (!qaEventsForCircularReferenceDisabled) {
				  qaEventsAllowEdit = "true";
				}

				/*
				 * FOR lable printed special status.
				 */
				
				//bugzilla 2227 we now allow amending
				//testsAddOption is currently always true (but I am leaving this functionality commented out
				//if we need to add restrictions later
				String testsAddOption = "";
				/*if (sample.getStatus().equals(
						SystemConfiguration.getInstance()
								.getSampleStatusReleased())
						&& isAnalysisReported) {
					testsAddOption = "false";
				} else {
					testsAddOption = "true";
				}*/
				testsAddOption = "true";
				PropertyUtils.setProperty(form, "testsAddOption",
						testsAddOption);
				
				//bugzilla 2300
				String testsCancelOption = "true";
				PropertyUtils.setProperty(form, "testsCancelOption",
						testsCancelOption);

				//bugzilla 2227
				String amendMode = "false";
				if (sample.getStatus().equals(
						SystemConfiguration.getInstance()
								.getSampleStatusReleased())
						&& isAnalysisReported) {
					amendMode = "true";
				} 
				
				PropertyUtils.setProperty(form, "amendMode",
						amendMode);

				// bugzilla 1942 display sample status (name) upper right
				statusOfSample.setCode(sample.getStatus());
				statusOfSample.setStatusType(SystemConfiguration.getInstance()
						.getSampleStatusType());

				statusOfSample = statusOfSampleDAO
						.getDataByStatusTypeAndStatusCode(statusOfSample);
				if (null != statusOfSample) {
					PropertyUtils.setProperty(form, "sampleStatus",
							statusOfSample.getStatusOfSampleName());
				} else {
					PropertyUtils.setProperty(form, "sampleStatus", BLANK);
				}

			}

			PropertyUtils.setProperty(form, "editOption", editOption);
			PropertyUtils.setProperty(form, "qaEventsAllowEdit", qaEventsAllowEdit);

			sampleHuman.setSampleId(sample.getId());
			sampleHumanDAO.getDataBySample(sampleHuman);
			// sampleItem.setSampleId(sample.getId());
			// sampleItemDAO.getDataBySample(sampleItem);
			patient.setId(sampleHuman.getPatientId());

			sampleHuman.setSampleId(sample.getId());
			sampleHumanDAO.getDataBySample(sampleHuman);
			sampleOrganization.setSampleId(sample.getId());
			sampleOrganizationDAO.getDataBySample(sampleOrganization);

			if (sampleHuman.getPatientId() != null) {
				patient.setId(sampleHuman.getPatientId());
				patientDAO.getData(patient);
				person = patient.getPerson();
				personDAO.getData(person);

			}

			if (sampleHuman.getProviderId() != null) {
				provider.setId(sampleHuman.getProviderId());
				providerDAO.getData(provider);
				providerPerson = provider.getPerson();
				personDAO.getData(providerPerson);

				String provider_personId = providerPerson.getId();
				PropertyUtils.setProperty(form, "provider_personId", provider_personId);

				String providerFirstName = providerPerson.getFirstName();
				String providerLastName = providerPerson.getLastName();
				String providerWorkPhoneFull = providerPerson.getWorkPhone();
				String providerWorkPhone = StringUtil.formatPhoneForDisplay(providerWorkPhoneFull);
				String providerWorkPhoneExtension = StringUtil.formatExtensionForDisplay(providerWorkPhoneFull);

				PropertyUtils.setProperty(form, "providerFirstName", providerFirstName);
				PropertyUtils.setProperty(form, "providerLastName", providerLastName);				
				PropertyUtils.setProperty(form, "providerWorkPhone", providerWorkPhone);
				PropertyUtils.setProperty(form, "providerWorkPhoneExtension", providerWorkPhoneExtension);
			}

			if (null != sampleOrganization.getOrganization()) {
			    //bugzilla 2069
				String organizationLocalAbbreviation = sampleOrganization.getOrganization().getOrganizationLocalAbbreviation();
				String organizationName = sampleOrganization.getOrganization().getOrganizationName();
				String organizationBoth =  organizationLocalAbbreviation+ " / "+ organizationName;				

                //bugzilla 2069
				PropertyUtils.setProperty(form, "organizationLocalAbbreviation", organizationLocalAbbreviation);
				PropertyUtils.setProperty(form, "organizationBoth", organizationBoth);				

			}

			if (sampleItem.getId() != null) {
				TypeOfSample typeOfSample = sampleItem.getTypeOfSample();
				SourceOfSample sourceOfSample = sampleItem.getSourceOfSample();

				if (typeOfSample != null) {
					PropertyUtils.setProperty(form, "typeOfSample",
							typeOfSample);

					String typeOfSampleDesc = typeOfSample.getDescription();
					PropertyUtils.setProperty(form, "typeOfSampleDesc", typeOfSampleDesc);

					String typeOfSampleId = typeOfSample.getId();
					PropertyUtils.setProperty(form, "typeOfSampleId", typeOfSampleId);
				}

				if (sourceOfSample != null) {

					PropertyUtils.setProperty(form, "sourceOfSample",
							sourceOfSample);

					String sourceOfSampleDesc = sourceOfSample.getDescription();
					PropertyUtils.setProperty(form, "sourceOfSampleDesc", sourceOfSampleDesc);

					String sourceOfSampleId = sourceOfSample.getId();
					PropertyUtils.setProperty(form, "sourceOfSampleId", sourceOfSampleId);	

				}

				String sourceOther = sampleItem.getSourceOther();
				PropertyUtils.setProperty(form, "sourceOther", sourceOther);
			}

			sampleProjects = sample.getSampleProjects();

			if (sampleProjects != null && sampleProjects.size() > 0) {

				SampleProject sampleProject = (SampleProject) sampleProjects
						.get(0);

				//bugzilla 2438
				String projectid = sampleProject.getProject().getLocalAbbreviation();
				String projectname = sampleProject.getProject().getProjectName();						
				String projectIdAndName = projectid + " / " + projectname;
				PropertyUtils.setProperty(form, "projectIdAndName", projectIdAndName);
				PropertyUtils.setProperty(form, "projectIdOrName", projectid);
				PropertyUtils.setProperty(form, "projectNameOrId", projectname);

				if (sampleProjects.size() > 1) {
					SampleProject sampleProject2 = (SampleProject) sampleProjects
							.get(1);

					//bugzilla 2438
					String projectId2 =  sampleProject2.getProject().getLocalAbbreviation();
					String projectName2 = sampleProject2.getProject().getProjectName();
					String project2IdAndName = projectId2 + " / " + projectName2;

					PropertyUtils.setProperty(form, "project2IdAndName", project2IdAndName);
					PropertyUtils.setProperty(form, "project2IdOrName", projectId2);
					PropertyUtils.setProperty(form, "project2NameOrId", projectName2);
				}
			}

			if (null != patient.getId()) {

				patientDAO.getData(patient);
				person = patient.getPerson();
				//bugzilla 2227
				analyses = analysisDAO.getMaxRevisionAnalysesBySample(sampleItem);

				String firstName = person.getFirstName();
				String lastName = person.getLastName();
				String middleName = person.getMiddleName();
				String multipleUnit = person.getMultipleUnit();
				String streetAddress = person.getStreetAddress();
				String city = person.getCity();
				String state = person.getState();
				String zipCode = person.getZipCode();

				String birthDateForDisplay = patient.getBirthDateForDisplay();
				String gender = patient.getGender();
				String externalId = patient.getExternalId();
				//bugzilla 1904
				String chartNumber = patient.getChartNumber();

				PropertyUtils.setProperty(form, "firstName", firstName);
				PropertyUtils.setProperty(form, "lastName", lastName);
				PropertyUtils.setProperty(form, "middleName", middleName);
				PropertyUtils.setProperty(form, "multipleUnit", multipleUnit);
				PropertyUtils.setProperty(form, "streetAddress", streetAddress);
				//bugzilla 2136: (due to change to tiles-def.xml to use popupTemplate to fix popup losing focus)
				// these validations were failing if values not trimmed
				if (city != null) city = city.trim();
				if (state != null) state = state.trim();
				if (zipCode != null) zipCode = zipCode.trim();
				PropertyUtils.setProperty(form, "city", city);
				PropertyUtils.setProperty(form, "zipCode", zipCode);
				PropertyUtils.setProperty(form, "state", state);

				PropertyUtils.setProperty(form, "birthDateForDisplay", birthDateForDisplay);	
				PropertyUtils.setProperty(form, "gender", gender);
				PropertyUtils.setProperty(form, "externalId", externalId);
				//bugzilla 1904
				PropertyUtils.setProperty(form, "chartNumber", chartNumber);

			}
		}

		//bugzilla 2227
		String sampleHasTestRevisions = "false";
		//bugzilla 2532 sort tests like results report
		//indent child tests with dashes
		analyses = sortTests(analyses);
		int level = 0;
		String indent = "";
		HashMap parentToChildLevelMap = new HashMap();
		if (analyses != null) {
			// there is one Analysis per Test
			for (int i = 0; i < analyses.size(); i++) {
				Analysis analysis = (Analysis) analyses.get(i);
				Test t = (Test) analysis.getTest();
				
				level = 0;
				if (analysis.getParentAnalysis() != null) {
					//lookup in parentToChildLevelMap
					String parentAnalysisId = analysis.getParentAnalysis().getId();
					if (parentToChildLevelMap.containsKey(parentAnalysisId)) {
						//get the child level
						level = Integer.valueOf((String)parentToChildLevelMap.get(parentAnalysisId));
					}
				} 
				
				parentToChildLevelMap.put(analysis.getId(), String.valueOf(level + 1));
				
				//bugzilla 2227
			    if (!analysis.getRevision().equals("0")) {
			    	sampleHasTestRevisions = "true";
			    }

			    indent = "";
			    if (level > 0) {
			    	for (int j = 0; j < level; j++) {
		    		 indent = indent + "---";
			    	}
			    }
		    	analysis.setAssignedSortedTestTreeDisplayValue(indent + t.getTestDisplayValue());
			}
		}
		//bugzilla 1856
		//bugzilla 2532 (already sorted analyses above to have child tests below parent tests)
		//Collections.sort(tests, TestComparator.DESCRIPTION_COMPARATOR);
		PropertyUtils.setProperty(form, "tests", analyses);
		//bugzilla 2227
		testManagementForm.set("sampleHasTestRevisions",	sampleHasTestRevisions);

		// **** for optimistic locking
		testManagementForm.set("lastupdated", sample.getLastupdated());
		testManagementForm.set("personLastupdated", person.getLastupdated());
		testManagementForm.set("patientLastupdated", patient.getLastupdated());
		testManagementForm.set("providerPersonLastupdated", providerPerson
				.getLastupdated());
		testManagementForm.set("providerLastupdated", provider.getLastupdated());
		testManagementForm.set("sampleItemLastupdated", sampleItem.getLastupdated());
		testManagementForm.set("sampleHumanLastupdated", sampleHuman.getLastupdated());
		testManagementForm.set("sampleOrganizationLastupdated",	sampleOrganization.getLastupdated());

		// **** for validating the date fields
		PropertyUtils.setProperty(form, "currentDate", dateAsText);

		request.setAttribute(ACCESSION_NUMBER, accessionNumber);

		return mapping.findForward("success");
	}

	protected String getPageTitleKey() {
		return "testmanagement.title";
	}

	protected String getPageSubtitleKey() {
		return "testmanagement.title";
	}
}