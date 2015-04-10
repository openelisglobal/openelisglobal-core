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
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.gender.dao.GenderDAO;
import us.mn.state.health.lims.gender.daoimpl.GenderDAOImpl;
import us.mn.state.health.lims.login.dao.UserTestSectionDAO;
import us.mn.state.health.lims.login.daoimpl.UserTestSectionDAOImpl;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.sampledomain.dao.SampleDomainDAO;
import us.mn.state.health.lims.sampledomain.daoimpl.SampleDomainDAOImpl;
import us.mn.state.health.lims.sourceofsample.dao.SourceOfSampleDAO;
import us.mn.state.health.lims.sourceofsample.daoimpl.SourceOfSampleDAOImpl;
import us.mn.state.health.lims.systemuser.dao.SystemUserDAO;
import us.mn.state.health.lims.systemuser.daoimpl.SystemUserDAOImpl;
import us.mn.state.health.lims.typeofsample.dao.TypeOfSampleDAO;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleDAOImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class HumanSampleOneAction extends BaseAction {

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		// this is a new sample
		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "false");

		BaseActionForm dynaForm = (BaseActionForm) form;

		// initialize the form
		dynaForm.initialize(mapping);

		Sample sample = new Sample();
		Patient patient = new Patient();
		Person person = new Person();

		// this is a new sample
		// default received date and entered date to today's date
		Date today = Calendar.getInstance().getTime();
		String dateAsText = DateUtil.formatDateAsText(today);

		sample.setReceivedDateForDisplay(dateAsText);
		sample.setEnteredDateForDisplay(dateAsText);

		sample.setReferredCultureFlag(SystemConfiguration.getInstance()
				.getHumanSampleOneDefaultReferredCultureFlag());

		sample.setStickerReceivedFlag(SystemConfiguration.getInstance()
				.getHumanSampleOneDefaultStickerReceivedFlag());

		// default nextItemSequence to 1 (for clinical - always 1)
		sample.setNextItemSequence(SystemConfiguration.getInstance()
				.getHumanSampleOneDefaultNextItemSequence());

		// revision is set to 0 on insert
		sample.setRevision(SystemConfiguration.getInstance()
				.getHumanSampleOneDefaultRevision());

		sample.setCollectionTimeForDisplay(SystemConfiguration.getInstance()
				.getHumanSampleOneDefaultCollectionTimeForDisplay());

		if (sample.getId() != null && !sample.getId().equals("0")) {
			request.setAttribute(ID, sample.getId());
		}

		patient.setGender(SystemConfiguration.getInstance()
				.getHumanSampleOneDefaultPatientGender());

		// populate form from valueholder
		PropertyUtils.copyProperties(form, sample);
		PropertyUtils.copyProperties(form, person);
		PropertyUtils.copyProperties(form, patient);

		SystemUserDAO sysUserDAO = new SystemUserDAOImpl();
		SampleDomainDAO sampleDomainDAO = new SampleDomainDAOImpl();
		GenderDAO genderDAO = new GenderDAOImpl();
		TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
		SourceOfSampleDAO sourceOfSampleDAO = new SourceOfSampleDAOImpl();

		List sysUsers = sysUserDAO.getAllSystemUsers();
		List sampleDomains = sampleDomainDAO.getAllSampleDomains();
		List genders = genderDAO.getAllGenders();
		List typeOfSamples = typeOfSampleDAO.getAllTypeOfSamples();
		List sourceOfSamples = sourceOfSampleDAO.getAllSourceOfSamples();

		//pdf
		if ( SystemConfiguration.getInstance().getEnabledSamplePdf().equals(YES) ) {
			String status = SystemConfiguration.getInstance().getSampleStatusQuickEntryComplete(); //status = 1
			String humanDomain = SystemConfiguration.getInstance().getHumanDomain(); 
			UserTestSectionDAO userTestSectionDAO = new UserTestSectionDAOImpl();
            Locale locale = (Locale) request.getSession().getAttribute("org.apache.struts.action.LOCALE");
			List accessionNumberListOne = userTestSectionDAO.getSamplePdfList(request, locale, status, humanDomain);
			PropertyUtils.setProperty(form, "accessionNumberListOne", accessionNumberListOne);	
		}
		
		PropertyUtils.setProperty(form, "sysUsers", sysUsers);
		PropertyUtils.setProperty(form, "sampleDomains", sampleDomains);
		PropertyUtils.setProperty(form, "genders", genders);
		PropertyUtils.setProperty(form, "typeOfSamples", typeOfSamples);
		PropertyUtils.setProperty(form, "sourceOfSamples", sourceOfSamples);
		PropertyUtils.setProperty(form, "currentDate", dateAsText);
		request.setAttribute("menuDefinition", "HumanSampleOneDefinition");
		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		return "human.sample.one.edit.title";
	}

	protected String getPageSubtitleKey() {
		return "human.sample.one.edit.title";
	}

}
