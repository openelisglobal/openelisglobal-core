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

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.valueholder.UserSessionData;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.person.dao.PersonDAO;
import us.mn.state.health.lims.person.daoimpl.PersonDAOImpl;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.samplehuman.dao.SampleHumanDAO;
import us.mn.state.health.lims.samplehuman.daoimpl.SampleHumanDAOImpl;
import us.mn.state.health.lims.samplehuman.valueholder.SampleHuman;
import us.mn.state.health.lims.samplenewborn.valueholder.SampleNewborn;
import us.mn.state.health.lims.samplenewborn.dao.SampleNewbornDAO;
import us.mn.state.health.lims.samplenewborn.daoimpl.SampleNewbornDAOImpl;
import us.mn.state.health.lims.sampleorganization.dao.SampleOrganizationDAO;
import us.mn.state.health.lims.sampleorganization.daoimpl.SampleOrganizationDAOImpl;
import us.mn.state.health.lims.sampleorganization.valueholder.SampleOrganization;
import us.mn.state.health.lims.provider.dao.ProviderDAO;
import us.mn.state.health.lims.provider.daoimpl.ProviderDAOImpl;
import us.mn.state.health.lims.provider.valueholder.Provider;
import us.mn.state.health.lims.patientrelation.dao.PatientRelationDAO;
import us.mn.state.health.lims.patientrelation.daoimpl.PatientRelationDAOImpl;
import us.mn.state.health.lims.patientrelation.valueholder.PatientRelation;
import us.mn.state.health.lims.organization.dao.OrganizationDAO;
import us.mn.state.health.lims.organization.daoimpl.OrganizationDAOImpl;
import us.mn.state.health.lims.organization.valueholder.Organization;

public class NewbornSampleDemographicsUpdateAction extends BaseAction {

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, TRUE);

		BaseActionForm dynaForm = (BaseActionForm) form;

		ActionMessages errors = dynaForm.validate(mapping, request);
			
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());
		
		String birthDateForDisplay = dynaForm.getString("birthDateForDisplay");
		String birthTimeForDisplay = dynaForm.getString("birthTimeForDisplay");
		String format = "MM/dd/yyyy";
		java.sql.Timestamp dob = null;
		if ( (birthDateForDisplay != null) && (birthDateForDisplay.length() > 0) ) {
			java.text.SimpleDateFormat f = new java.text.SimpleDateFormat(format) ;
			dob = new java.sql.Timestamp(f.parse(birthDateForDisplay).getTime());
			
			if ( (birthTimeForDisplay != null) && (birthTimeForDisplay.length() > 0) ) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dob); 
				cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(birthTimeForDisplay.substring(0, 2)).intValue());
				cal.set(Calendar.MINUTE, Integer.valueOf(birthTimeForDisplay.substring(3, 5)).intValue());
				dob = new java.sql.Timestamp(cal.getTimeInMillis());
			}		
		}
		
		String dateFirstFeedingForDisplay = dynaForm.getString("dateFirstFeedingForDisplay");
		String timeFirstFeedingForDisplay = dynaForm.getString("timeFirstFeedingForDisplay");
		java.sql.Timestamp dateFirstFeeding = null;
		if ( (dateFirstFeedingForDisplay != null) && (dateFirstFeedingForDisplay.length() > 0) ) {
			java.text.SimpleDateFormat f = new java.text.SimpleDateFormat(format) ;
			dateFirstFeeding = new java.sql.Timestamp(f.parse(dateFirstFeedingForDisplay).getTime());
			
			if ( (timeFirstFeedingForDisplay != null) && (timeFirstFeedingForDisplay.length() > 0) ) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dob); 
				cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(timeFirstFeedingForDisplay.substring(0, 2)).intValue());
				cal.set(Calendar.MINUTE, Integer.valueOf(timeFirstFeedingForDisplay.substring(3, 5)).intValue());
				dateFirstFeeding = new java.sql.Timestamp(cal.getTimeInMillis());
			}		
		}		
				
		String collectionDateForDisplay = dynaForm.getString("collectionDateForDisplay");
		String collectionTimeForDisplay = dynaForm.getString("collectionTimeForDisplay");
		java.sql.Timestamp collDate = null;
		if ( (collectionDateForDisplay != null) && (collectionDateForDisplay.length() > 0) ) {
			java.text.SimpleDateFormat f = new java.text.SimpleDateFormat(format) ;
			collDate = new java.sql.Timestamp(f.parse(collectionDateForDisplay).getTime());
			
			if ( (collectionTimeForDisplay != null) && (collectionTimeForDisplay.length() > 0) ) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(collDate); 
				cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(collectionTimeForDisplay.substring(0, 2)).intValue());
				cal.set(Calendar.MINUTE, Integer.valueOf(collectionTimeForDisplay.substring(3, 5)).intValue());
				collDate = new java.sql.Timestamp(cal.getTimeInMillis());
			}		
		}
		
		String dateTransfutionForDisplay = (String) dynaForm.getString("dateTransfutionForDisplay");
		java.sql.Timestamp dateTransfution = null;
		if ( (dateTransfutionForDisplay != null) && (dateTransfutionForDisplay.length() > 0) ) {
			java.text.SimpleDateFormat f = new java.text.SimpleDateFormat(format) ;
			dateTransfution = new java.sql.Timestamp(f.parse(dateTransfutionForDisplay).getTime());
		}
		
		String motherBirthDateForDisplay = (String)dynaForm.get("motherBirthDateForDisplay");
		java.sql.Timestamp motherBirthDate = null;
		if ( (motherBirthDateForDisplay != null) && (motherBirthDateForDisplay.length() > 0) ) {
			java.text.SimpleDateFormat f = new java.text.SimpleDateFormat(format) ;
			motherBirthDate = new java.sql.Timestamp(f.parse(motherBirthDateForDisplay).getTime());
		}
		
		org.hibernate.Transaction tx = HibernateUtil.getSession().beginTransaction();
		try {
			//Sample
			Sample sample = new Sample();
			SampleDAO sampleDAO = new SampleDAOImpl();
			sample.setSysUserId(sysUserId);
			sample.setAccessionNumber((String)dynaForm.get("accessionNumber"));
			sampleDAO.getSampleByAccessionNumber(sample);		
			sample.setBarCode((String)dynaForm.get("barcode"));
			sample.setStatus(SystemConfiguration.getInstance().getSampleStatusEntry2Complete());	
			sample.setCollectionDate(collDate);
			sampleDAO.updateData(sample);
		
			//SampleHuman
			SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
			SampleHuman sampleHuman = new SampleHuman();			
			sampleHuman.setSampleId(sample.getId());			
			sampleHumanDAO.getDataBySample(sampleHuman);
			
			//Patient - child
			Patient childPatient = new Patient();
			PatientDAO patientDAO = new PatientDAOImpl();
			Person childPerson = new Person();
			childPatient.setId(sampleHuman.getPatientId());
			patientDAO.getData(childPatient);
			
			//Person - child
			PersonDAO personDAO = new PersonDAOImpl();
			childPerson = childPatient.getPerson();
			personDAO.getData(childPerson);
			childPerson.setSysUserId(sysUserId);
			childPerson.setLastName((String)dynaForm.get("lastName"));
			childPerson.setFirstName((String)dynaForm.get("firstName"));
			personDAO.updateData(childPerson);
			
			childPatient.setSysUserId(sysUserId);			
			childPatient.setBirthDate(dob);
			childPatient.setGender((String) dynaForm.get("gender"));
			patientDAO.updateData(childPatient);
			
			//PatientRelation
			PatientRelation patientRelation = new PatientRelation();
			PatientRelationDAO patientRelationDAO = new PatientRelationDAOImpl(); 
			patientRelation.setPatientIdSource(childPatient.getId());
			patientRelation = patientRelationDAO.getPatientRelationByChildId(patientRelation);
			Person motherPerson = new Person();
			Patient motherPatient = new Patient();
			if (!StringUtil.isNullorNill(patientRelation.getId())) {
				motherPatient.setId(patientRelation.getPatientId());
				patientDAO.getData(motherPatient);
				motherPerson = motherPatient.getPerson();
				personDAO.getData(motherPerson);
			} 			
			motherPerson.setLastName((String)dynaForm.get("motherLastName"));
			motherPerson.setFirstName((String)dynaForm.get("motherFirstName"));
			String motherHomePhone = (String)dynaForm.get("motherPhoneNumber");
			if ( (motherHomePhone!=null) && (motherHomePhone.length()>0) ) 
				motherHomePhone = StringUtil.formatPhone(motherHomePhone, null);
			motherPerson.setHomePhone(motherHomePhone);
			motherPerson.setStreetAddress((String)dynaForm.get("motherStreetAddress"));
			motherPerson.setCity((String)dynaForm.get("city"));
			motherPerson.setState((String)dynaForm.get("state"));
			motherPerson.setZipCode((String)dynaForm.get("zipCode"));
			motherPerson.setSysUserId(sysUserId);
			if ( !StringUtil.isNullorNill(motherPerson.getId()) )
				personDAO.updateData(motherPerson);
			else
				personDAO.insertData(motherPerson);
			
			motherPatient.setBirthDate(motherBirthDate);
			motherPatient.setPerson(motherPerson);
			motherPatient.setSysUserId(sysUserId);
			if ( !StringUtil.isNullorNill(motherPatient.getId()) )
				patientDAO.updateData(motherPatient);
			else
				patientDAO.insertData(motherPatient);
			
			patientRelation.setSysUserId(sysUserId);
			patientRelation.setPatientId(motherPatient.getId());
			
			String motherRelation = SystemConfiguration.getInstance().getNewbornPatientRelation();
			patientRelation.setRelation(motherRelation);
			if ( !StringUtil.isNullorNill(patientRelation.getId()) )
				patientRelationDAO.updateData(patientRelation);
			else
				patientRelationDAO.insertData(patientRelation);
			
			//SampleNewborn
			SampleNewborn sampleNewborn = new SampleNewborn();
			SampleNewbornDAO sampleNewbornDAO = new SampleNewbornDAOImpl();
			sampleNewborn.setId(sampleHuman.getId());
			sampleNewbornDAO.getData(sampleNewborn);
			sampleNewborn.setSysUserId(sysUserId);
			sampleNewborn.setMedicalRecordNumber((String)dynaForm.get("medicalRecordNumber"));
			sampleNewborn.setYnumber((String) dynaForm.get("ynumber"));
			sampleNewborn.setYellowCard((String)dynaForm.get("yellowCard"));
			sampleNewborn.setWeight((String)dynaForm.get("birthWeight"));
			sampleNewborn.setMultiBirth((String)dynaForm.get("multipleBirth"));
			sampleNewborn.setBirthOrder((String)dynaForm.get("birthOrder"));
			sampleNewborn.setGestationalWeek((Double)dynaForm.get("gestationalWeek"));
			sampleNewborn.setDateFirstFeeding(dateFirstFeeding);
			sampleNewborn.setBreast((String)dynaForm.get("breast"));
			sampleNewborn.setTpn((String)dynaForm.get("tpn"));
			sampleNewborn.setFormula((String)dynaForm.get("formula"));
			sampleNewborn.setMilk((String)dynaForm.get("milk"));
			sampleNewborn.setSoy((String)dynaForm.get("soy"));
			sampleNewborn.setJaundice((String)dynaForm.get("jaundice"));
			sampleNewborn.setAntibiotic((String)dynaForm.get("antibiotic"));
			sampleNewborn.setTransfused((String)dynaForm.get("transfused"));
			sampleNewborn.setNicu((String)dynaForm.get("nicuPatient"));
			sampleNewborn.setBirthDefect((String)dynaForm.get("birthDefect"));
			sampleNewborn.setPregnancyComplication((String)dynaForm.get("pregnancyComplication"));
			sampleNewborn.setDeceasedSibling((String)dynaForm.get("deceasedSibling"));
			sampleNewborn.setCauseOfDeath((String)dynaForm.get("causeOfDeath"));
			sampleNewborn.setFamilyHistory((String)dynaForm.get("familyHistory"));
			sampleNewborn.setOther((String)dynaForm.get("other"));
			sampleNewborn.setDateTransfution(dateTransfution);
			if ( !StringUtil.isNullorNill(sampleNewborn.getId()) )
				sampleNewbornDAO.updateData(sampleNewborn);
			else {
				sampleNewborn.setId(sampleHuman.getId());
				sampleNewbornDAO.insertData(sampleNewborn);
			}
			
			//Provider
			Provider provider = new Provider();
			Person providerPerson = new Person();
			ProviderDAO providerDAO = new ProviderDAOImpl();		
			if ( !StringUtil.isNullorNill(sampleHuman.getProviderId()) ) {
				provider.setId(sampleHuman.getProviderId());
				providerDAO.getData(provider);
			}
			
			if ( !StringUtil.isNullorNill(sampleHuman.getProviderId()) ) {
				provider.setId(sampleHuman.getProviderId());
				providerDAO.getData(provider);
				providerPerson = (Person)provider.getPerson();
			}
			
			//SampleOrganization
			Organization o = new Organization();
			OrganizationDAO organizationDAO = new OrganizationDAOImpl();
			o.setOrganizationLocalAbbreviation((String) dynaForm.get("submitterNumber"));
			o = organizationDAO.getOrganizationByLocalAbbreviation(o, true);
			SampleOrganization sampleOrganization = new SampleOrganization();
			SampleOrganizationDAO sampleOrganizationDAO = new SampleOrganizationDAOImpl();
			sampleOrganization.setSampleId(sample.getId());
			sampleOrganizationDAO.getDataBySample(sampleOrganization);
			sampleOrganization.setSample(sample);			
			sampleOrganization.setOrganization(o);
			sampleOrganization.setSysUserId(sysUserId);
			if ( !StringUtil.isNullorNill(sampleOrganization.getId()) )
				sampleOrganizationDAO.updateData(sampleOrganization);
			else
				sampleOrganizationDAO.insertData(sampleOrganization);
			
			//Person - provider
			providerPerson.setSysUserId(sysUserId);
			providerPerson.setFirstName((String)dynaForm.get("physicianFirstName"));
			providerPerson.setLastName((String)dynaForm.get("physicianLastName"));
			String providerHomePhone = (String)dynaForm.get("physicianPhoneNumber");
			if ( (providerHomePhone!=null) && (providerHomePhone.length()>0) ) 
				providerHomePhone = StringUtil.formatPhone(providerHomePhone, null);
			providerPerson.setHomePhone(providerHomePhone);	
			if ( !StringUtil.isNullorNill(providerPerson.getId()) )
				personDAO.updateData(providerPerson);
			else
				personDAO.insertData(providerPerson);
			
			provider.setPerson(providerPerson);
			provider.setExternalId(BLANK);
			provider.setSysUserId(sysUserId);
			if (!StringUtil.isNullorNill(provider.getId()))
				providerDAO.updateData(provider);
			else
				providerDAO.insertData(provider);
			
			sampleHuman.setProviderId(provider.getId());
			sampleHuman.setSysUserId(sysUserId);
			sampleHumanDAO.updateData(sampleHuman);

			tx.commit();
		} catch (LIMSRuntimeException lre) {
		    LogEvent.logError("NewbornSampleFullUpdateAction","performAction()",lre.toString());
			tx.rollback();
			errors = new ActionMessages();
			ActionError error = null;
			if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
				error = new ActionError("errors.OptimisticLockException", null,	null);
			} else {
				error = new ActionError("errors.UpdateException", null, null);
			}
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute(ALLOW_EDITS_KEY, "false");
			forward = FWD_FAIL;

		} finally {
			HibernateUtil.closeSession();
		}
		if (forward.equals(FWD_FAIL))
			return mapping.findForward(forward);
	
			
		return mapping.findForward(FWD_CLOSE);

	}

	protected String getPageTitleKey() {
		return "newborn.sample.two.edit.title";
	}

	protected String getPageSubtitleKey() {
		return "newborn.sample.two.edit.title";
	}
}