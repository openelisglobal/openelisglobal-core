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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.util.resources.ResourceLocator;
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
import us.mn.state.health.lims.samplenewborn.dao.SampleNewbornDAO;
import us.mn.state.health.lims.samplenewborn.daoimpl.SampleNewbornDAOImpl;
import us.mn.state.health.lims.samplenewborn.valueholder.SampleNewborn;
import us.mn.state.health.lims.samplepdf.dao.SamplePdfDAO;
import us.mn.state.health.lims.samplepdf.daoimpl.SamplePdfDAOImpl;
import us.mn.state.health.lims.samplepdf.valueholder.SamplePdf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class NewbornSampleTwoPopulateData2Action extends BaseAction {

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");

		BaseActionForm dynaForm = (BaseActionForm) form;

		Date today = Calendar.getInstance().getTime();
		String dateAsText = DateUtil.formatDateAsText(today);
		PropertyUtils.setProperty(form, "currentDate", dateAsText);
		
		//populate barcode
		String accessionNumber = (String) dynaForm.get("accessionNumber"); 
		if ( (accessionNumber != null) && (accessionNumber.length()>0) ) {
			SamplePdfDAO samplePdfDAO = new SamplePdfDAOImpl();
			SamplePdf samplePdf = new SamplePdf();
			samplePdf.setAccessionNumber(accessionNumber);
			samplePdf = samplePdfDAO.getSamplePdfByAccessionNumber(samplePdf);
			dynaForm.set("accessionNumber", accessionNumber);
			dynaForm.set("barcode", samplePdf.getBarcode());
			request.setAttribute("preAccessionNumber", accessionNumber);
			SampleDAO sampleDAO = new SampleDAOImpl();
			Sample sample = sampleDAO.getSampleByAccessionNumber(accessionNumber);
			PropertyUtils.setProperty(form, "newbornDomain", sample.getDomain());			
			prepareNewbornData2(dynaForm);
		}
		
		prepareOptionList(dynaForm,request);

		request.setAttribute("menuDefinition", "NewbornSampleTwoDefinition");
		return mapping.findForward(forward);
	}

	private void prepareNewbornData2(BaseActionForm dynaForm) throws Exception {
		SampleDAO sampleDAO = new SampleDAOImpl();
		Sample sample = sampleDAO.getSampleByAccessionNumber(dynaForm.getString("accessionNumber"));
				
		SampleHuman sampleHuman = new SampleHuman();
		SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
		sampleHuman.setSampleId(sample.getId());
		sampleHumanDAO.getDataBySample(sampleHuman);
		
		Patient patient = new Patient();
		PatientDAO patientDAO = new PatientDAOImpl();
		patient.setId(sampleHuman.getPatientId());
		patientDAO.getData(patient);
		
		Person person = new Person();
		PersonDAO personDAO = new PersonDAOImpl();
		person = patient.getPerson();
		personDAO.getData(person);
		
		SampleNewborn sampleNewborn = new SampleNewborn();
		SampleNewbornDAO sampleNewbornDAO = new SampleNewbornDAOImpl();
		sampleNewborn.setId(sampleHuman.getId());
		sampleNewbornDAO.getData(sampleNewborn);
		
		HashMap newbornSampleTwoMap = new HashMap(); 
		if (dynaForm.get("newbornSampleTwoMap") != null) {
			newbornSampleTwoMap = (HashMap) dynaForm.get("newbornSampleTwoMap");
		}

		newbornSampleTwoMap = populateNewbornSampleTwoMap(sample,sampleNewborn,sampleHuman,patient,
															person,dynaForm);
		PropertyUtils.setProperty(dynaForm, "newbornSampleTwoMap", newbornSampleTwoMap);
		
		// set lastupdated fields
		dynaForm.set("lastupdated", sample.getLastupdated());
		dynaForm.set("personLastupdated", person.getLastupdated());
		dynaForm.set("patientLastupdated", patient.getLastupdated());
		dynaForm.set("sampleHumanLastupdated", sampleHuman.getLastupdated());
		dynaForm.set("sampleNewbornLastupdated", sampleNewborn.getLastupdated());

	}
	
	private HashMap populateNewbornSampleTwoMap(Sample sample, SampleNewborn sampleNewborn, 
												SampleHuman sampleHuman,
												Patient patient, Person person,	BaseActionForm dynaForm) {
		HashMap newbornSampleTwoMap = new HashMap();
		
		if (person.getFirstName() != null) {
			newbornSampleTwoMap.put("firstName", person.getFirstName());
		} else {
			newbornSampleTwoMap.put("firstName", "");
		}

		if (person.getLastName() != null) {
			newbornSampleTwoMap.put("lastName", person.getLastName());
		} else {
			newbornSampleTwoMap.put("lastName", "");
		}

		if (patient.getBirthDateForDisplay() != null) {
			newbornSampleTwoMap.put("birthDateForDisplay", patient.getBirthDateForDisplay());
			String locale = SystemConfiguration.getInstance().getDefaultLocale().toString();
			newbornSampleTwoMap.put("birthTimeForDisplay", DateUtil.convertTimestampToStringTime(patient.getBirthDate(), locale));
		} else {
			newbornSampleTwoMap.put("birthDateForDisplay", "");
		}
		
		if (sample.getCollectionDateForDisplay() != null) {
			newbornSampleTwoMap.put("collectionDateForDisplay", sample.getCollectionDateForDisplay());
			String locale = SystemConfiguration.getInstance().getDefaultLocale().toString();
			newbornSampleTwoMap.put("collectionTimeForDisplay", DateUtil.convertTimestampToStringTime(sample.getCollectionDate(), locale));
		} else {
			newbornSampleTwoMap.put("birthDateForDisplay", "");
		}
		
		newbornSampleTwoMap.put("birthWeight", sampleNewborn.getWeight());
				
		return newbornSampleTwoMap;
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
		return "newborn.sample.two.edit.title";
	}

	protected String getPageSubtitleKey() {
		return "newborn.sample.two.edit.title";
	}

}
