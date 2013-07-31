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
package us.mn.state.health.lims.patient.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.gender.dao.GenderDAO;
import us.mn.state.health.lims.gender.daoimpl.GenderDAOImpl;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.person.dao.PersonDAO;
import us.mn.state.health.lims.person.daoimpl.PersonDAOImpl;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class PatientAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new Patient.
		// If there is a parameter present, we should bring up an existing
		// Patient to edit.

		String id = request.getParameter(ID);

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		DynaActionForm dynaForm = (DynaActionForm) form;

		// initialize the form
		dynaForm.initialize(mapping);

		Patient patient = new Patient();

		//System.out.println("I am in PatientAction and this is id " + id);
		if ((id != null) && (!"0".equals(id))) { // this is an existing
			// patient

			patient.setId(id);
			PatientDAO patientDAO = new PatientDAOImpl();
			patientDAO.getData(patient);
			// initialize selectedPersonId
			if (patient.getPerson() != null) {
				patient.setSelectedPersonId(patient.getPerson().getId());
			}
			isNew = false; // this is to set correct page title

			// do we need to enable next or previous?
			List patients = patientDAO.getNextPatientRecord(patient.getId());
			if (patients.size() > 0) {
				// enable next button
				request.setAttribute(NEXT_DISABLED, "false");
			}
			patients = patientDAO.getPreviousPatientRecord(patient.getId());
			if (patients.size() > 0) {
				// enable next button
				request.setAttribute(PREVIOUS_DISABLED, "false");
			}
			// end of logic to enable next or previous button

		} else { // this is a new patient

			isNew = true; // this is to set correct page title
		}

		if (patient.getId() != null && !patient.getId().equals("0")) {
			request.setAttribute(ID, patient.getId());
		}

		// populate form from valueholder
		PropertyUtils.copyProperties(form, patient);

		PersonDAO personDAO = new PersonDAOImpl();
		GenderDAO genderDAO = new GenderDAOImpl();

		List persons = personDAO.getAllPersons();
		List genders = genderDAO.getAllGenders();

		PropertyUtils.setProperty(form, "persons", persons);
		PropertyUtils.setProperty(form, "genders", genders);

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "patient.add.title";
		} else {
			return "patient.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "patient.add.title";
		} else {
			return "patient.edit.title";
		}
	}

}
