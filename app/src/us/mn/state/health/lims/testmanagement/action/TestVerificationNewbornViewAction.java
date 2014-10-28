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
import us.mn.state.health.lims.organization.valueholder.Organization;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.patientrelation.dao.PatientRelationDAO;
import us.mn.state.health.lims.patientrelation.daoimpl.PatientRelationDAOImpl;
import us.mn.state.health.lims.patientrelation.valueholder.PatientRelation;
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
import us.mn.state.health.lims.samplenewborn.dao.SampleNewbornDAO;
import us.mn.state.health.lims.samplenewborn.daoimpl.SampleNewbornDAOImpl;
import us.mn.state.health.lims.samplenewborn.valueholder.SampleNewborn;
import us.mn.state.health.lims.sampleorganization.dao.SampleOrganizationDAO;
import us.mn.state.health.lims.sampleorganization.daoimpl.SampleOrganizationDAOImpl;
import us.mn.state.health.lims.sampleorganization.valueholder.SampleOrganization;
import us.mn.state.health.lims.statusofsample.dao.StatusOfSampleDAO;
import us.mn.state.health.lims.statusofsample.daoimpl.StatusOfSampleDAOImpl;
import us.mn.state.health.lims.statusofsample.valueholder.StatusOfSample;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.test.valueholder.TestComparator;
import us.mn.state.health.lims.testmanagement.valueholder.TestManagementRoutingSwitchSessionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

public class TestVerificationNewbornViewAction extends BaseAction {

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		BaseActionForm testManagementNewbornForm = (BaseActionForm) form;

		if (request.getParameter(ID) == null) {
			testManagementNewbornForm.initialize(mapping);
		}

		String accessionNumber = null;

		accessionNumber = (String)request.getParameter(ACCESSION_NUMBER);
		if (StringUtil.isNullorNill(accessionNumber)) {
				accessionNumber = (String)testManagementNewbornForm.get("accessionNumber");
		}
	
		testManagementNewbornForm.initialize(mapping);

		Date today = Calendar.getInstance().getTime();
		String dateAsText = DateUtil.formatDateAsText(today);

		Sample sample = new Sample();
		SampleItem sampleItem = new SampleItem();
		List analyses = new ArrayList();
		SampleDAO sampleDAO = new SampleDAOImpl();
		SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
		AnalysisDAO analysisDAO = new AnalysisDAOImpl();
		List tests = new ArrayList();

		StatusOfSample statusOfSample = new StatusOfSample();
		StatusOfSampleDAO statusOfSampleDAO = new StatusOfSampleDAOImpl();
		sample.setAccessionNumber(accessionNumber);
		sampleDAO.getSampleByAccessionNumber(sample);
		
		if (!StringUtil.isNullorNill(sample.getId())) {
			PropertyUtils.setProperty(form, "accessionNumber", accessionNumber);
			sampleItem.setSample(sample);
			sampleItemDAO.getDataBySample(sampleItem);
			boolean isAnalysisReported = false;

			if (sampleItem.getId() != null) {
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

		}

		//bugzilla 2227
		String sampleHasTestRevisions = "false";
		if (analyses != null) {
			// there is one Analysis per Test
			for (int i = 0; i < analyses.size(); i++) {
				Analysis analysis = (Analysis) analyses.get(i);
				Test t = (Test) analysis.getTest();
				//bugzilla 2227
			    if (!analysis.getRevision().equals("0")) {
			    	sampleHasTestRevisions = "true";
			    }
				// System.out.println("This is TEST ID " + t.getId());
				// System.out.println("This is TEST DESC " +
				// t.getDescription());
				tests.add(t);
			}
		}
		//bugzilla 1856
		Collections.sort(tests, TestComparator.DESCRIPTION_COMPARATOR);
		PropertyUtils.setProperty(form, "tests", tests);
		//bugzilla 2227
		testManagementNewbornForm.set("sampleHasTestRevisions",	sampleHasTestRevisions);

		// **** for validating the date fields
		PropertyUtils.setProperty(form, "currentDate", dateAsText);

		request.setAttribute(ACCESSION_NUMBER, accessionNumber);

		prepareNewbornFullData(testManagementNewbornForm);
		
		return mapping.findForward("success");
	}

	private void prepareNewbornFullData(BaseActionForm dynaForm) throws Exception {
		String locale = SystemConfiguration.getInstance().getDefaultLocale().toString();
		
		SampleDAO sampleDAO = new SampleDAOImpl();
		Sample sample = sampleDAO.getSampleByAccessionNumber(dynaForm.getString("accessionNumber"));
		dynaForm.set("barcode",sample.getBarCode());
		PropertyUtils.setProperty(dynaForm,"collectionDateForDisplay",sample.getCollectionDateForDisplay());
		PropertyUtils.setProperty(dynaForm,"collectionTimeForDisplay",DateUtil.convertTimestampToStringTime(sample.getCollectionDate(), locale));
		
		SampleHuman sampleHuman = new SampleHuman();
		SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
		sampleHuman.setSampleId(sample.getId());
		sampleHumanDAO.getDataBySample(sampleHuman);
		
		Patient childPatient = new Patient();
		PatientDAO patientDAO = new PatientDAOImpl();
		childPatient.setId(sampleHuman.getPatientId());
		patientDAO.getData(childPatient);
		PropertyUtils.setProperty(dynaForm,"birthDateForDisplay",childPatient.getBirthDateForDisplay());
		PropertyUtils.setProperty(dynaForm,"birthTimeForDisplay",DateUtil.convertTimestampToStringTime(childPatient.getBirthDate(), locale));
		PropertyUtils.setProperty(dynaForm,"gender",childPatient.getGender());
		
		Person childPperson = new Person();
		PersonDAO personDAO = new PersonDAOImpl();
		childPperson = childPatient.getPerson();
		personDAO.getData(childPperson);
		PropertyUtils.setProperty(dynaForm,"lastName",childPperson.getLastName());
		PropertyUtils.setProperty(dynaForm,"firstName",childPperson.getFirstName());
		
		PatientRelation patientRelation = new PatientRelation();
		PatientRelationDAO patientRelationDAO = new PatientRelationDAOImpl(); 
		patientRelation.setPatientIdSource(childPatient.getId());
		patientRelation = patientRelationDAO.getPatientRelationByChildId(patientRelation);
		if (!StringUtil.isNullorNill(patientRelation.getId())) {		
			Patient motherPatient = new Patient();
			motherPatient.setId(patientRelation.getPatientId());
			patientDAO.getData(motherPatient);
			Person motherPerson = new Person();
			motherPerson = motherPatient.getPerson();
			personDAO.getData(motherPerson);
			PropertyUtils.setProperty(dynaForm,"motherLastName",motherPerson.getLastName());
			PropertyUtils.setProperty(dynaForm,"motherFirstName",motherPerson.getFirstName());
			PropertyUtils.setProperty(dynaForm,"motherBirthDateForDisplay",motherPatient.getBirthDateForDisplay());
			PropertyUtils.setProperty(dynaForm,"motherPhoneNumber",StringUtil.formatPhoneForDisplay(motherPerson.getHomePhone()));
			PropertyUtils.setProperty(dynaForm,"motherStreetAddress",motherPerson.getStreetAddress());
			PropertyUtils.setProperty(dynaForm,"city",motherPerson.getCity());
			PropertyUtils.setProperty(dynaForm,"state",motherPerson.getState());
			PropertyUtils.setProperty(dynaForm,"zipCode",motherPerson.getZipCode());
		} else {
			PropertyUtils.setProperty(dynaForm,"motherLastName","");
			PropertyUtils.setProperty(dynaForm,"motherFirstName","");
			PropertyUtils.setProperty(dynaForm,"motherBirthDateForDisplay","");
			PropertyUtils.setProperty(dynaForm,"motherPhoneNumber","");
			PropertyUtils.setProperty(dynaForm,"motherStreetAddress","");
			PropertyUtils.setProperty(dynaForm,"city","");
			PropertyUtils.setProperty(dynaForm,"state","");
			PropertyUtils.setProperty(dynaForm,"zipCode","");
		}
		
		SampleNewborn sampleNewborn = new SampleNewborn();
		SampleNewbornDAO sampleNewbornDAO = new SampleNewbornDAOImpl();
		sampleNewborn.setId(sampleHuman.getId());
		sampleNewbornDAO.getData(sampleNewborn);
		PropertyUtils.setProperty(dynaForm,"medicalRecordNumber",sampleNewborn.getMedicalRecordNumber());
		PropertyUtils.setProperty(dynaForm,"ynumber",sampleNewborn.getYnumber());
		
		String barcode = (String)dynaForm.get("barcode");
		if ( (barcode != null) && (barcode.startsWith("9")) )
			PropertyUtils.setProperty(dynaForm,"yellowCard",YES);
		else
			PropertyUtils.setProperty(dynaForm,"yellowCard",sampleNewborn.getYellowCard());
		
		PropertyUtils.setProperty(dynaForm,"birthWeight",sampleNewborn.getWeight());	
		PropertyUtils.setProperty(dynaForm,"multipleBirth",sampleNewborn.getMultiBirth());
		PropertyUtils.setProperty(dynaForm,"birthOrder",sampleNewborn.getBirthOrder());		
		PropertyUtils.setProperty(dynaForm,"gestationalWeek",sampleNewborn.getGestationalWeek());
		PropertyUtils.setProperty(dynaForm,"dateFirstFeedingForDisplay",sampleNewborn.getDateFirstFeedingForDisplay());
		PropertyUtils.setProperty(dynaForm,"timeFirstFeedingForDisplay",DateUtil.convertTimestampToStringTime(sampleNewborn.getDateFirstFeeding(), locale));
		PropertyUtils.setProperty(dynaForm,"breast",sampleNewborn.getBreast());
		PropertyUtils.setProperty(dynaForm,"tpn",sampleNewborn.getTpn());
		PropertyUtils.setProperty(dynaForm,"formula",sampleNewborn.getFormula());
		PropertyUtils.setProperty(dynaForm,"milk",sampleNewborn.getMilk());
		PropertyUtils.setProperty(dynaForm,"soy",sampleNewborn.getSoy());
		PropertyUtils.setProperty(dynaForm,"jaundice",sampleNewborn.getJaundice());
		PropertyUtils.setProperty(dynaForm,"antibiotic",sampleNewborn.getAntibiotic());
		PropertyUtils.setProperty(dynaForm,"transfused",sampleNewborn.getTransfused());
		PropertyUtils.setProperty(dynaForm,"dateTransfutionForDisplay",sampleNewborn.getDateTransfutionForDisplay());
		PropertyUtils.setProperty(dynaForm,"nicuPatient",sampleNewborn.getNicu());
		PropertyUtils.setProperty(dynaForm,"birthDefect",sampleNewborn.getBirthDefect());
		PropertyUtils.setProperty(dynaForm,"pregnancyComplication",sampleNewborn.getPregnancyComplication());
		PropertyUtils.setProperty(dynaForm,"deceasedSibling",sampleNewborn.getDeceasedSibling());
		PropertyUtils.setProperty(dynaForm,"causeOfDeath",sampleNewborn.getCauseOfDeath());
		PropertyUtils.setProperty(dynaForm,"familyHistory",sampleNewborn.getFamilyHistory());
		PropertyUtils.setProperty(dynaForm,"other",sampleNewborn.getOther());
					
		if ( !StringUtil.isNullorNill(sampleHuman.getProviderId()) ) {
			Provider provider = new Provider();
			ProviderDAO providerDAO = new ProviderDAOImpl();
			provider.setId(sampleHuman.getProviderId());
			providerDAO.getData(provider);
			if (!StringUtil.isNullorNill(provider.getId())) {
				Person providerPerson = provider.getPerson();			
				PropertyUtils.setProperty(dynaForm,"physicianLastName",providerPerson.getLastName());
				PropertyUtils.setProperty(dynaForm,"physicianFirstName",providerPerson.getFirstName());
				PropertyUtils.setProperty(dynaForm,"physicianPhoneNumber",StringUtil.formatPhoneForDisplay(providerPerson.getHomePhone()));
			} else {		
				PropertyUtils.setProperty(dynaForm,"physicianLastName","");
				PropertyUtils.setProperty(dynaForm,"physicianFirstName","");
				PropertyUtils.setProperty(dynaForm,"physicianPhoneNumber","");
			}
		} else {		
			PropertyUtils.setProperty(dynaForm,"physicianLastName","");
			PropertyUtils.setProperty(dynaForm,"physicianFirstName","");
			PropertyUtils.setProperty(dynaForm,"physicianPhoneNumber","");
		}	
		
		SampleOrganization sampleOrganization = new SampleOrganization();
		SampleOrganizationDAO sampleOrganizationDAO = new SampleOrganizationDAOImpl();
		sampleOrganization.setSampleId(sample.getId());
		sampleOrganizationDAO.getDataBySample(sampleOrganization);
		if ( !StringUtil.isNullorNill(sampleOrganization.getId()) ) {
			Organization o = sampleOrganization.getOrganization();
			PropertyUtils.setProperty(dynaForm,"submitterNumber",o.getId());		
		} else
			PropertyUtils.setProperty(dynaForm,"submitterNumber","");
		// set lastupdated fields
		dynaForm.set("lastupdated",sample.getLastupdated());
		dynaForm.set("personLastupdated",childPperson.getLastupdated());
		dynaForm.set("patientLastupdated",childPatient.getLastupdated());
		dynaForm.set("sampleHumanLastupdated",sampleHuman.getLastupdated());
		dynaForm.set("sampleNewbornLastupdated",sampleNewborn.getLastupdated());

	}
 
	
	protected String getPageTitleKey() {
		return "testmanagement.newborn.title";
	}

	protected String getPageSubtitleKey() {
		return "testmanagement.newborn.title";
	}
}