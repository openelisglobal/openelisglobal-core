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
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.sampledomain.dao.SampleDomainDAO;
import us.mn.state.health.lims.sampledomain.daoimpl.SampleDomainDAOImpl;
import us.mn.state.health.lims.systemuser.dao.SystemUserDAO;
import us.mn.state.health.lims.systemuser.daoimpl.SystemUserDAOImpl;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class SampleAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new Sample.
		// If there is a parameter present, we should bring up an existing
		// Sample to edit.

		String id = request.getParameter(ID);

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		DynaActionForm dynaForm = (DynaActionForm) form;

		// initialize the form
		dynaForm.initialize(mapping);

		Sample sample = new Sample();
		//System.out.println("I am in SampleAction and this is id " + id);
		if ((id != null) && (!"0".equals(id))) { // this is an existing
			// sample

			sample.setId(id);
			SampleDAO sampleDAO = new SampleDAOImpl();
			sampleDAO.getData(sample);

			// initialize sysUserId
			if (sample.getSystemUser() != null) {
				sample.setSysUserId(sample.getSystemUser().getId());
			}

			isNew = false; // this is to set correct page title

			// do we need to enable next or previous?
			List samples = sampleDAO.getNextSampleRecord(sample.getId());
			if (samples.size() > 0) {
				// enable next button
				request.setAttribute(NEXT_DISABLED, "false");
			}
			samples = sampleDAO.getPreviousSampleRecord(sample.getId());
			if (samples.size() > 0) {
				// enable next button
				request.setAttribute(PREVIOUS_DISABLED, "false");
			}
			// end of logic to enable next or previous button

		} else { // this is a new sample
			// default received date and entered date to today's date
			Date today = Calendar.getInstance().getTime();
			Locale locale = (Locale) request.getSession().getAttribute(
					"org.apache.struts.action.LOCALE");

			String dateAsText = DateUtil.formatDateAsText(today, locale);

			sample.setReceivedDateForDisplay(dateAsText);
			sample.setEnteredDateForDisplay(dateAsText);

			// default referredCultureFlag to 'N'
			sample.setReferredCultureFlag("S");

			// default stickerReceivedFlag to 'N'
			sample.setStickerReceivedFlag(NO);

			// default nextItemSequence to 1 (for clinical - always 1)
			sample.setNextItemSequence("1");

			// revision is set to 0 on insert
			sample.setRevision("0");

			isNew = true; // this is to set correct page title

		}

		if (sample.getId() != null && !sample.getId().equals("0")) {
			request.setAttribute(ID, sample.getId());
		}

		// populate form from valueholder
		PropertyUtils.copyProperties(form, sample);

		//SampleDAO sampleDAO = new SampleDAOImpl();
		SystemUserDAO sysUserDAO = new SystemUserDAOImpl();
		SampleDomainDAO sampleDomainDAO = new SampleDomainDAOImpl();

		//List samples = sampleDAO.getAllSamples();
		List sysUsers = sysUserDAO.getAllSystemUsers();
		List sampleDomains = sampleDomainDAO.getAllSampleDomains();

		//PropertyUtils.setProperty(form, "parentSamples", samples);
		PropertyUtils.setProperty(form, "sysUsers", sysUsers);
		PropertyUtils.setProperty(form, "sampleDomains", sampleDomains);

		//System.out.println("I am in SampleAction this is forward " + forward);

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "sample.add.title";
		} else {
			return "sample.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "sample.add.title";
		} else {
			return "sample.edit.title";
		}
	}

}
