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
import us.mn.state.health.lims.sourceofsample.dao.SourceOfSampleDAO;
import us.mn.state.health.lims.sourceofsample.daoimpl.SourceOfSampleDAOImpl;
import us.mn.state.health.lims.typeofsample.dao.TypeOfSampleDAO;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleDAOImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class HumanSampleTwoAction extends BaseAction {

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		// this is a new sample
		String forward = "success";
		request.setAttribute(ALLOW_EDITS_KEY, "false");

		BaseActionForm dynaForm = (BaseActionForm) form;

		// initialize the form
		dynaForm.initialize(mapping);


		// this is a new sample
		// default received date and entered date to today's date
		Date today = Calendar.getInstance().getTime();
		String dateAsText = DateUtil.formatDateAsText(today);


		GenderDAO genderDAO = new GenderDAOImpl();
		TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
		SourceOfSampleDAO sourceOfSampleDAO = new SourceOfSampleDAOImpl();

		List genders = genderDAO.getAllGenders();
		List typeOfSamples = typeOfSampleDAO.getAllTypeOfSamples();
		List sourceOfSamples = sourceOfSampleDAO.getAllSourceOfSamples();


		PropertyUtils.setProperty(form, "genders", genders);
		PropertyUtils.setProperty(form, "typeOfSamples", typeOfSamples);
		PropertyUtils.setProperty(form, "sourceOfSamples", sourceOfSamples);
		PropertyUtils.setProperty(form, "currentDate", dateAsText);
		PropertyUtils.setProperty(form, "humanSampleOneMap", new HashMap());
		PropertyUtils.setProperty(form, "blankscreen", "true");
		
		//pdf
		if ( SystemConfiguration.getInstance().getEnabledSamplePdf().equals(YES) ) {				
			String status = SystemConfiguration.getInstance().getSampleStatusEntry1Complete(); //status = 2
			String humanDomain = SystemConfiguration.getInstance().getHumanDomain(); 
			UserTestSectionDAO userTestSectionDAO = new UserTestSectionDAOImpl();
            Locale locale = (Locale) request.getSession().getAttribute("org.apache.struts.action.LOCALE");
			List accessionNumberListTwo = userTestSectionDAO.getSamplePdfList(request, locale, status, humanDomain);
			PropertyUtils.setProperty(form, "accessionNumberListTwo", accessionNumberListTwo);	
		}		
		
		//we need to initialize these!!
		PropertyUtils.setProperty(form, "lastupdated", new Timestamp(System.currentTimeMillis()));
		PropertyUtils.setProperty(form, "personLastupdated", new Timestamp(System.currentTimeMillis()));
		PropertyUtils.setProperty(form, "patientLastupdated", new Timestamp(System.currentTimeMillis()));
		PropertyUtils.setProperty(form, "providerPersonLastupdated", new Timestamp(System.currentTimeMillis()));
		PropertyUtils.setProperty(form, "providerLastupdated", new Timestamp(System.currentTimeMillis()));
		PropertyUtils.setProperty(form, "sampleItemLastupdated", new Timestamp(System.currentTimeMillis()));
		PropertyUtils.setProperty(form, "sampleHumanLastupdated", new Timestamp(System.currentTimeMillis()));
		PropertyUtils.setProperty(form, "sampleOrganizationLastupdated", new Timestamp(System.currentTimeMillis()));
		PropertyUtils.setProperty(form, "sampleProject1Lastupdated", new Timestamp(System.currentTimeMillis()));
		PropertyUtils.setProperty(form, "sampleProject2Lastupdated", new Timestamp(System.currentTimeMillis()));

		
		request.setAttribute("menuDefinition", "HumanSampleTwoDefinition");
		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		return "human.sample.two.edit.title";
	}

	protected String getPageSubtitleKey() {
		return "human.sample.two.edit.title";
	}

}
