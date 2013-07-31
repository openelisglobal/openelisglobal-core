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
package us.mn.state.health.lims.sourceofsample.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.sampledomain.dao.SampleDomainDAO;
import us.mn.state.health.lims.sampledomain.daoimpl.SampleDomainDAOImpl;
import us.mn.state.health.lims.sourceofsample.dao.SourceOfSampleDAO;
import us.mn.state.health.lims.sourceofsample.daoimpl.SourceOfSampleDAOImpl;
import us.mn.state.health.lims.sourceofsample.valueholder.SourceOfSample;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class SourceOfSampleAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new SourceOfSample.
		// If there is a parameter present, we should bring up an existing
		// SourceOfSample to edit.

		String id = request.getParameter(ID);

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		DynaActionForm dynaForm = (DynaActionForm) form;

		// initialize the form
		dynaForm.initialize(mapping);

		SourceOfSample sourceOfSample = new SourceOfSample();

		if ((id != null) && (!"0".equals(id))) { // this is an existing
			// sourceOfSample

			sourceOfSample.setId(id);
			SourceOfSampleDAO sourceOfSampleDAO = new SourceOfSampleDAOImpl();
			sourceOfSampleDAO.getData(sourceOfSample);

			isNew = false; // this is to set correct page title

			// do we need to enable next or previous?
			List sourceOfSamples = sourceOfSampleDAO
					.getNextSourceOfSampleRecord(sourceOfSample.getId());
			if (sourceOfSamples.size() > 0) {
				// enable next button
				request.setAttribute(NEXT_DISABLED, "false");
			}
			sourceOfSamples = sourceOfSampleDAO
					.getPreviousSourceOfSampleRecord(sourceOfSample.getId());
			if (sourceOfSamples.size() > 0) {
				// enable next button
				request.setAttribute(PREVIOUS_DISABLED, "false");
			}
			// end of logic to enable next or previous button

		} else { // this is a new sourceOfSample

			isNew = true; // this is to set correct page title

		}

		if (sourceOfSample.getId() != null
				&& !sourceOfSample.getId().equals("0")) {
			request.setAttribute(ID, sourceOfSample.getId());
		}

		// populate form from valueholder
		PropertyUtils.copyProperties(form, sourceOfSample);

		SampleDomainDAO sampleDomainDAO = new SampleDomainDAOImpl();

		List domains = sampleDomainDAO.getAllSampleDomains();

		PropertyUtils.setProperty(form, "domains", domains);

		//System.out.println("I am in SourceOfSampleAction this is forward "
		//		+ forward);
		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "sourceofsample.add.title";
		} else {
			return "sourceofsample.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "sourceofsample.add.title";
		} else {
			return "sourceofsample.edit.title";
		}
	}

}
