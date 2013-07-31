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
package us.mn.state.health.lims.testmanagement.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.testmanagement.valueholder.TestManagementRoutingSwitchSessionHandler;

/**
 * @author benzd1
 * 
 * bugzilla 2053 route from qa events entry to test management (pass accession number)
 */
public class TestManagementFromQaEventsEntryAction extends BaseAction {	

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String forward = FWD_SUCCESS;
		HttpSession session = request.getSession();
		DynaActionForm dynaForm = (DynaActionForm) form;
		String qaEventCategoryId = (String)dynaForm.get("selectedQaEventsCategoryId");
		String multipleSampleMode = (String)dynaForm.get("multipleSampleMode");
		//bugzilla 2502
		String viewMode = (String)dynaForm.get("viewMode");
		String fullScreenSection = (String)dynaForm.get("fullScreenSection");
		
		
		TestManagementRoutingSwitchSessionHandler.switchOn(TEST_MANAGEMENT_ROUTING_FROM_QAEVENTS_ENTRY, session);
		session.setAttribute(QAEVENTS_ENTRY_PARAM_MULTIPLE_SAMPLE_MODE, multipleSampleMode);
		session.setAttribute(QAEVENTS_ENTRY_PARAM_QAEVENT_CATEGORY_ID, qaEventCategoryId);
		//bugzilla 2502
		session.setAttribute(QAEVENTS_ENTRY_PARAM_VIEW_MODE, viewMode);
		session.setAttribute(QAEVENTS_ENTRY_FULL_SCREEN_VIEW_SECTION, fullScreenSection);

		//bugzilla 2566
		String accessionNumber = null;
		accessionNumber = (String)request.getParameter(ACCESSION_NUMBER);

		SampleDAO sampleDAO = new SampleDAOImpl();
		Sample sample = new Sample();
		sample.setAccessionNumber(accessionNumber);
		sampleDAO.getSampleByAccessionNumber(sample);
		
		if (sample.getDomain().equals(SystemConfiguration.getInstance().getNewbornDomain())) {
			forward = FWD_SUCCESS_NEWBORN;
		}
		
		return mapping.findForward(forward);	
	}
	
	protected String getPageTitleKey() {
		return "testmanagement.title";
	}

	protected String getPageSubtitleKey() {
		return "testmanagement.title";
	}
}