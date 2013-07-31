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

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.person.valueholder.Person;
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
import us.mn.state.health.lims.sampletracking.dao.SampleTrackingDAO;
import us.mn.state.health.lims.sampletracking.daoimpl.SampleTrackingDAOImpl;
import us.mn.state.health.lims.sampletracking.valueholder.SampleTracking;
import us.mn.state.health.lims.sampletracking.valueholder.SampleTrackingCriteria;
import us.mn.state.health.lims.sourceofsample.valueholder.SourceOfSample;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;

/**
 * @author aiswarya raman
 * //AIS - bugzilla 1851/1853
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 * bugzilla 1920 - standards
 */

public class SampleTrackingPopupAction extends BaseAction {	

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

	
	request.setAttribute(ALLOW_EDITS_KEY, "true");
	request.setAttribute(PREVIOUS_DISABLED, "true");
	request.setAttribute(NEXT_DISABLED, "true");

	
	//System.out.println("I am here in SampleTrackingPopup Action");
	
	BaseActionForm searchForm = (BaseActionForm)form;
	
	SampleTrackingDAO sampleTrackingDAO = new SampleTrackingDAOImpl();
	
	//Ais: Modified for Type safety warning 
	List<String> lnamea = new ArrayList<String>();
	List<String> fnamea = new ArrayList<String>();
	List<String> lnameparta = new ArrayList<String>();
	List<String> fnameparta = new ArrayList<String>();
	List<String> aIda = new ArrayList<String>();
	List<String> cReferencea = new ArrayList<String>();
	List<String> cReferenceparta = new ArrayList<String>();	
	List<String> dOba = new ArrayList<String>();
	List<String> oRga= new ArrayList<String>();
	List<String> oRgnamea= new ArrayList<String>();
	List<String> receivedDatea= new ArrayList<String>();
	List<String> collectionDatea= new ArrayList<String>();
	List<String> collectionTimea= new ArrayList<String>();
	List<String> sampleTypea= new ArrayList<String>();
	List<String> sampleSourcea= new ArrayList<String>();
	List<String> sampleTypeparta= new ArrayList<String>();
	List<String> sampleSourceparta= new ArrayList<String>();
	//bugzilla 2455
	List<String> specimenOrIsolatea = new ArrayList<String>();
	
	SampleTrackingCriteria sampleTrackingCriteria = new SampleTrackingCriteria();			

	sampleTrackingCriteria.setClientRef((String) request.getParameter("cr"));
	sampleTrackingCriteria.setLastName((String) request.getParameter("ln"));
	sampleTrackingCriteria.setFirstName((String) request.getParameter("fn"));
	sampleTrackingCriteria.setSubmitter((String) request.getParameter("sub"));
	sampleTrackingCriteria.setReceivedDate((String) request.getParameter("rd"));
	sampleTrackingCriteria.setSampleType((String) request.getParameter("st"));
	sampleTrackingCriteria.setSampleSource((String) request.getParameter("ss"));
	sampleTrackingCriteria.setExternalId(request.getParameter("ei"));
	sampleTrackingCriteria.setCollectionDate(request.getParameter("cd"));
	sampleTrackingCriteria.setAccessionNumberPartial(request.getParameter("an"));
	sampleTrackingCriteria.setProjectId(request.getParameter("pi"));
	sampleTrackingCriteria.setSortBy(request.getParameter("sb"));
	//bugzilla 2455
	sampleTrackingCriteria.setSpecimenOrIsolate((String) request.getParameter("si"));

	
    List listOfSamples = sampleTrackingDAO.getAccessionByPatientAndOtherCriteria(sampleTrackingCriteria);
       
	
		for (int i = 0; i < listOfSamples.size(); i++ ) {			
			//initialize
			Sample sample = new Sample();
			Patient patient = new Patient();
			Person person = new Person();
			Provider provider = new Provider();
			SampleHuman sampleHuman = new SampleHuman();
			SampleItem sampleItem = new SampleItem();
			
			PatientDAO patientDAO = new PatientDAOImpl();	
			SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
			SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();		
			SampleDAO sampleDAO = new SampleDAOImpl();
			
			SampleOrganization sampleOrganization = new SampleOrganization();
			SampleOrganizationDAO sampleOrganizationDAO = new SampleOrganizationDAOImpl();	
			
		    sample.setAccessionNumber(((SampleTracking)listOfSamples.get(i)).getAccNum()) ;		
			String accessionNumber = sample.getAccessionNumber();		
			sampleDAO.getSampleByAccessionNumber(sample);	
			String clientReference = sample.getClientReference();
			//bugzilla 2455
			String specimenOrIsolate = sample.getReferredCultureFlag();
			
			String collectionDateForDisplay = sample.getCollectionDateForDisplay();
			String collectionTimeForDisplay = sample.getCollectionTimeForDisplay();
			String receivedDateForDisplay = sample.getReceivedDateForDisplay();
			
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
				provider.setId(sampleHuman.getProviderId());	
				
				String firstname = person.getFirstName();		
				String lastname = person.getLastName();	
				
				String firstnamepart = "";
				if (null != firstname ) {
					int firstnamelen = firstname.length();	
					
					if (firstnamelen >15){
						firstnamepart = firstname.substring(0,15);					
					}else{
						firstnamepart = firstname;					
					}
			    }else{
			    	firstname ="";			    	
			    }
				
				String lastnamepart = "";
				if (null != lastname ) {
					int lastnamelen = lastname.length();					
					if (lastnamelen >15){
						lastnamepart = lastname.substring(0,15);					
					}else{
						lastnamepart = lastname;				
					}
				}else{
					lastname = "";					
				}
				
				String birthDateForDisplay = patient.getBirthDateForDisplay();
				if (null == birthDateForDisplay){
					birthDateForDisplay ="";				
				}
				
				lnamea.add(i,lastname);
			    fnamea.add(i,firstname);			    
			    lnameparta.add(i,lastnamepart);
			    fnameparta.add(i,firstnamepart);			    
			    dOba.add(i,birthDateForDisplay);
			    
			}else {
				lnamea.add(i,"");
			    fnamea.add(i,"");
			    lnameparta.add(i,"");
			    fnameparta.add(i,"");
			    dOba.add(i,"");
			}			
		}		
						
		aIda.add(i,accessionNumber);	
		
		String cReferencepart = "";
		if (null != clientReference ) {
			int cReferencelen = clientReference.length();					
			if (cReferencelen >10){
				cReferencepart = clientReference.substring(0,10);					
			}else{
				cReferencepart = clientReference;				
			}
		}else{
			clientReference = "";					
		}
		
		cReferencea.add(i,clientReference);
		cReferenceparta.add(i,cReferencepart);
		
		collectionDatea.add(i,collectionDateForDisplay);
		collectionTimea.add(i,collectionTimeForDisplay);
		receivedDatea.add(i,receivedDateForDisplay);		
		//bugzilla 2455
		specimenOrIsolatea.add(i,specimenOrIsolate);
		
		sampleOrganization.setSampleId(sample.getId());
		sampleOrganizationDAO.getDataBySample(sampleOrganization);
		
		if (null != sampleOrganization.getOrganization()){	
		//bugzilla 2069
			String organizationLocalAbbreviation = sampleOrganization.getOrganization().getOrganizationLocalAbbreviation();
			String organizationName =sampleOrganization.getOrganization().getOrganizationName();
			
			//bugzilla 2069
			String organizationtest =sampleOrganization.getOrganization().getConcatOrganizationLocalAbbreviationName();
			//System.out.println("organizationtest" + organizationtest);
			
			//bugzilla 2069
			oRga.add(i,organizationLocalAbbreviation);	
			oRgnamea.add(i,organizationName);	
			
		}else{			
			oRga.add(i,"");
			oRgnamea.add(i,"");
		}
		
		if (sampleItem.getId() != null ){
			TypeOfSample typeOfSample = sampleItem.getTypeOfSample();			
			SourceOfSample sourceOfSample = sampleItem.getSourceOfSample();	
			
			if (typeOfSample != null) {					
				String typeOfSampleDesc = typeOfSample.getDescription();	
				//System.out.println("current size of tos#:" +  sampleTypea.size());
				sampleTypea.add(i,typeOfSampleDesc);
				
				int typeOfSampleDesclen = typeOfSampleDesc.length();				
				String typeOfSampleDescpart = "";
				if (typeOfSampleDesclen >15){
					typeOfSampleDescpart = typeOfSampleDesc.substring(0,15);					
				}else{
					typeOfSampleDescpart = typeOfSampleDesc;					
				}
				
				sampleTypeparta.add(i,typeOfSampleDescpart);
				
				
			}else{
				sampleTypea.add(i,"");
				sampleTypeparta.add(i,"");
			}
			
			
			if (sourceOfSample != null) {				
				String sourceOfSampleDesc = sourceOfSample.getDescription();	
				
				
			
				sampleSourcea.add(i,sourceOfSampleDesc);					
				
				int sourceOfSampleDesclen = sourceOfSampleDesc.length();				
				String sourceOfSampleDescpart = "";
				if (sourceOfSampleDesclen >10){
					sourceOfSampleDescpart = sourceOfSampleDesc.substring(0,10);					
				}else{
					sourceOfSampleDescpart = sourceOfSampleDesc;					
				}	
				
				sampleSourceparta.add(i,sourceOfSampleDescpart);
				
				
			}else{				
				sampleSourcea.add(i,"");
				sampleSourceparta.add(i,"");				
			}
			
		}else{
			sampleTypea.add(i,"");
			sampleTypeparta.add(i,"");
			sampleSourcea.add(i,"");
			sampleSourceparta.add(i,"");
			
		}
    }	
    	
	String lname [] = (String []) lnamea.toArray (new String [lnamea.size ()]);      	
	String fname [] = (String []) fnamea.toArray (new String [fnamea.size ()]); 	
	String lnamepart [] = (String []) lnameparta.toArray (new String [lnameparta.size ()]);      	
	String fnamepart [] = (String []) fnameparta.toArray (new String [fnameparta.size ()]);	
	String aId [] = (String []) aIda.toArray (new String [aIda.size ()]);      
	String cReference [] = (String []) cReferencea.toArray (new String [cReferencea.size ()]);
	String cReferencepart [] = (String []) cReferenceparta.toArray (new String [cReferenceparta.size ()]); 
	String dOb [] = (String []) dOba.toArray (new String [dOba.size ()]); 	
	String oRg [] = (String []) oRga.toArray (new String [oRga.size ()]);
	String oRgname [] = (String []) oRgnamea.toArray (new String [oRgnamea.size ()]);
	String receivedDate [] = (String []) receivedDatea.toArray (new String [receivedDatea.size ()]); 
	String collectionDate [] = (String []) collectionDatea.toArray (new String [collectionDatea.size ()]); 
	String collectionTime [] = (String []) collectionTimea.toArray (new String [collectionTimea.size ()]); 
	String sampleType [] = (String []) sampleTypea.toArray (new String [sampleTypea.size ()]); 
	String sampleSource [] = (String []) sampleSourcea.toArray (new String [sampleSourcea.size ()]); 
	String sampleTypepart[] = (String []) sampleTypeparta.toArray (new String [sampleTypeparta.size ()]); 
	String sampleSourcepart [] = (String []) sampleSourceparta.toArray (new String [sampleSourceparta.size ()]); 
	//bugzilla 2455
	String specimenOrIsolate [] = (String []) specimenOrIsolatea.toArray (new String [specimenOrIsolatea.size ()]);

	
	searchForm.set("aId", aId);    		
	searchForm.set("cReference", cReference);
	searchForm.set("cReferencepart", cReferencepart);
	searchForm.set("fname", fname);
	searchForm.set("lname", lname);
	searchForm.set("fnamepart", fnamepart);
	searchForm.set("lnamepart", lnamepart);	
	searchForm.set("dOb", dOb);
	searchForm.set("oRg", oRg);
	searchForm.set("oRgname", oRgname);
	searchForm.set("receivedDate", receivedDate);
	searchForm.set("collectionDate", collectionDate);
	searchForm.set("collectionTime", collectionTime);
	searchForm.set("sampleType", sampleType);
	searchForm.set("sampleSource", sampleSource);
	searchForm.set("sampleTypepart", sampleTypepart);
	searchForm.set("sampleSourcepart", sampleSourcepart);
	//bugzilla 2455
	searchForm.set("specimenOrIsolate", specimenOrIsolate);
	
		
	return mapping.findForward("success");
	}

	protected String getPageTitleKey() {
		return "sampletracking.title";
	}
	
	protected String getPageSubtitleKey() {
		return "sampletracking.title";
	}
}


  