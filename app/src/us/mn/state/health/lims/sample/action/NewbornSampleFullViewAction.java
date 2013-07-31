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
package us.mn.state.health.lims.sample.action;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.util.resources.ResourceLocator;
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
import us.mn.state.health.lims.samplenewborn.dao.SampleNewbornDAO;
import us.mn.state.health.lims.samplenewborn.daoimpl.SampleNewbornDAOImpl;
import us.mn.state.health.lims.samplenewborn.valueholder.SampleNewborn;
import us.mn.state.health.lims.sampleorganization.dao.SampleOrganizationDAO;
import us.mn.state.health.lims.sampleorganization.daoimpl.SampleOrganizationDAOImpl;
import us.mn.state.health.lims.sampleorganization.valueholder.SampleOrganization;

public class NewbornSampleFullViewAction extends BaseAction {

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");

		BaseActionForm dynaForm = (BaseActionForm) form;

		Date today = Calendar.getInstance().getTime();
		Locale locale = (Locale) request.getSession().getAttribute("org.apache.struts.action.LOCALE");
		String dateAsText = DateUtil.formatDateAsText(today, locale);
		PropertyUtils.setProperty(form, "currentDate", dateAsText);
		
		//populate data
		String accessionNumber = (String) dynaForm.get("accessionNumber"); 
		if ( (accessionNumber != null) && (accessionNumber.length()>0) ) {
			SampleDAO sampleDAO = new SampleDAOImpl();
			Sample sample = sampleDAO.getSampleByAccessionNumber(accessionNumber);
			dynaForm.set("accessionNumber",accessionNumber);
			dynaForm.set("barcode",sample.getBarCode());
			PropertyUtils.setProperty(form, "newbornDomain", sample.getDomain());			
			prepareNewbornFullData(dynaForm);
		}
		
		prepareOptionList(dynaForm,request);

		request.setAttribute("menuDefinition", "NewbornSampleFullDefinition");
		return mapping.findForward(forward);		

	}

	private void prepareNewbornFullData(BaseActionForm dynaForm) throws Exception {
		String locale = SystemConfiguration.getInstance().getDefaultLocale().toString();
		
		SampleDAO sampleDAO = new SampleDAOImpl();
		Sample sample = sampleDAO.getSampleByAccessionNumber(dynaForm.getString("accessionNumber"));
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
	
    private void prepareOptionList( BaseActionForm dynaForm, HttpServletRequest request ) throws Exception {
		Locale locale = (Locale) request.getSession().getAttribute("org.apache.struts.action.LOCALE");
    	Vector optionList = new Vector();
    	
    	String gram = ResourceLocator.getInstance().getMessageResources().getMessage(locale,"page.default.gram.option");	
    	String pound = ResourceLocator.getInstance().getMessageResources().getMessage(locale,"page.default.pound.option");
        optionList.add(new LabelValueBean(gram,gram));
        optionList.add(new LabelValueBean(pound,pound));
        dynaForm.set("selectedBirthWeight",gram);
        PropertyUtils.setProperty(dynaForm,"birthWeightList", optionList);       
    }
    
	protected String getPageTitleKey() {
	    return "newborn.sample.full.edit.title";
	}

	protected String getPageSubtitleKey() {
	    return "newborn.sample.full.edit.title";
	}
}