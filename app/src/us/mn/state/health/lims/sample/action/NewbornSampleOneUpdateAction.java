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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
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

public class NewbornSampleOneUpdateAction extends BaseAction {

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");

		BaseActionForm dynaForm = (BaseActionForm) form;

		ActionMessages errors = dynaForm.validate(mapping, request);

		String accessionNumber = (String) dynaForm.get("accessionNumber"); 
		String birthWeight = (String) dynaForm.get("birthWeight");
		
		Patient patient = new Patient();
		Person person = new Person();
		Sample sample = new Sample();
		SampleHuman sampleHuman = new SampleHuman();
		SampleNewborn sampleNewborn = new SampleNewborn();
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());

		// populate valueholder from form
		PropertyUtils.copyProperties(sample, dynaForm);
		PropertyUtils.copyProperties(person, dynaForm);
		PropertyUtils.copyProperties(sampleHuman, dynaForm);
		PropertyUtils.copyProperties(sampleNewborn, dynaForm);
			
		String birthDate = dynaForm.getString("birthDateForDisplay");
		String birthTime = dynaForm.getString("birthTimeForDisplay");
		String format = "MM/dd/yyyy";
		if ( (birthDate != null) && (birthDate.length() > 0) ) {
			java.text.SimpleDateFormat f = new java.text.SimpleDateFormat(format) ;
			java.sql.Timestamp dob = new java.sql.Timestamp(f.parse(birthDate).getTime());
			
			if ( (birthTime != null) && (birthTime.length() > 0) ) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dob); 
				cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(birthTime.substring(0, 2)).intValue());
				cal.set(Calendar.MINUTE, Integer.valueOf(birthTime.substring(3, 5)).intValue());
				dob = new java.sql.Timestamp(cal.getTimeInMillis());
			}	
			patient.setBirthDate(dob);		
		}
		
		String collectionDate = dynaForm.getString("collectionDateForDisplay");
		String collectionTime = dynaForm.getString("collectionTimeForDisplay");
		java.sql.Timestamp collDate = null;
		if ( (collectionDate != null) && (collectionDate.length() > 0) ) {
			java.text.SimpleDateFormat f = new java.text.SimpleDateFormat(format) ;
			collDate = new java.sql.Timestamp(f.parse(collectionDate).getTime());
			
			if ( (collectionTime != null) && (collectionTime.length() > 0) ) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(collDate); 
				cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(collectionTime.substring(0, 2)).intValue());
				cal.set(Calendar.MINUTE, Integer.valueOf(collectionTime.substring(3, 5)).intValue());
				collDate = new java.sql.Timestamp(cal.getTimeInMillis());
			}		
		}

		SampleDAO sampleDAO = new SampleDAOImpl();
		PersonDAO personDAO = new PersonDAOImpl();
		PatientDAO patientDAO = new PatientDAOImpl();
		SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
		SampleNewbornDAO sampleNewbornDAO = new SampleNewbornDAOImpl();
		
		org.hibernate.Transaction tx = HibernateUtil.getSession().beginTransaction();
		try {
			sample.setSysUserId(sysUserId);
			sample.setAccessionNumber(accessionNumber);
			sampleDAO.getSampleByAccessionNumber(sample);		
			sample.setStatus(SystemConfiguration.getInstance().getSampleStatusEntry1Complete());
			sample.setCollectionDate(collDate);
			sampleDAO.updateData(sample);
			
			person.setSysUserId(sysUserId);
			patient.setSysUserId(sysUserId);
			personDAO.insertData(person);
			patient.setPerson(person);
			patientDAO.insertData(patient);
			
			sampleHuman.setSysUserId(sysUserId);
			sampleHuman.setSampleId(sample.getId());
			sampleHuman.setPatientId(patient.getId()); 
			sampleHumanDAO.insertData(sampleHuman);
			
			sampleNewborn.setSysUserId(sysUserId);
			sampleNewborn.setId(sampleHuman.getId());
			sampleNewborn.setWeight(birthWeight);
			sampleNewbornDAO.insertData(sampleNewborn);
			
			tx.commit();
		} catch (LIMSRuntimeException lre) {
		    LogEvent.logError("NewbornSampleOneUpdateAction","performAction()",lre.toString());
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
	
		return mapping.findForward(FWD_SUCCESS);

	}

	protected String getPageTitleKey() {
		return "newborn.sample.one.edit.title";
	}

	protected String getPageSubtitleKey() {
		return "newborn.sample.one.edit.title";
	}
}