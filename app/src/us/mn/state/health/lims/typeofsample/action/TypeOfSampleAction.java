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
package us.mn.state.health.lims.typeofsample.action;

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
import us.mn.state.health.lims.typeofsample.dao.TypeOfSampleDAO;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleDAOImpl;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class TypeOfSampleAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new TypeOfSample.
		// If there is a parameter present, we should bring up an existing
		// TypeOfSample to edit.

		String id = request.getParameter(ID);

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		DynaActionForm dynaForm = (DynaActionForm) form;

		// initialize the form
		dynaForm.initialize(mapping);

		TypeOfSample typeOfSample = new TypeOfSample();

		if ((id != null) && (!"0".equals(id))) { // this is an existing
			// typeOfSample

			typeOfSample.setId(id);
			TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
			typeOfSampleDAO.getData(typeOfSample);

			isNew = false; // this is to set correct page title

			// do we need to enable next or previous?
			List typeOfSamples = typeOfSampleDAO
					.getNextTypeOfSampleRecord(typeOfSample.getId());
			if (typeOfSamples.size() > 0) {
				// enable next button
				request.setAttribute(NEXT_DISABLED, "false");
			}
			typeOfSamples = typeOfSampleDAO
					.getPreviousTypeOfSampleRecord(typeOfSample.getId());
			if (typeOfSamples.size() > 0) {
				// enable next button
				request.setAttribute(PREVIOUS_DISABLED, "false");
			}
			// end of logic to enable next or previous button

		} else { // this is a new typeOfSample

			isNew = true; // this is to set correct page title

		}

		if (typeOfSample.getId() != null && !typeOfSample.getId().equals("0")) {
			request.setAttribute(ID, typeOfSample.getId());
		}

		// populate form from valueholder
		PropertyUtils.copyProperties(form, typeOfSample);

		SampleDomainDAO sampleDomainDAO = new SampleDomainDAOImpl();

		List domains = sampleDomainDAO.getAllSampleDomains();

		PropertyUtils.setProperty(form, "domains", domains);

		//System.out.println("I am in TypeOfSampleAction this is forward "
		//		+ forward);
		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "typeofsample.add.title";
		} else {
			return "typeofsample.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "typeofsample.add.title";
		} else {
			return "typeofsample.edit.title";
		}
	}

}
