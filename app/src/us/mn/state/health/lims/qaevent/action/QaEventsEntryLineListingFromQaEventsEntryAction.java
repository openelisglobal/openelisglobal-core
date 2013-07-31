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
package us.mn.state.health.lims.qaevent.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.qaevent.valueholder.QaEventLineListingRoutingSwitchSessionHandler;

/**
 * @author diane benz
 * bugzilla 2504, 2502
 */
public class QaEventsEntryLineListingFromQaEventsEntryAction extends BaseAction {

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;


		BaseActionForm dynaForm = (BaseActionForm) form;
		HttpSession session = request.getSession();


		//initialize test management routing switch to on/initializes other switches to off
		QaEventLineListingRoutingSwitchSessionHandler.switchOn(QA_EVENTS_ENTRY_LINELISTING_ROUTING_FROM_QAEVENTS_ENTRY, session);

		// depending on where we are coming from get accn numb differently
		String accessionNumber = "";

		if (!StringUtil.isNullorNill((String) request
				.getParameter(ACCESSION_NUMBER))) {
			accessionNumber = (String) request.getParameter("accessionNumber");
		} else {
			accessionNumber = (String) dynaForm.get("accessionNumber");
		}
		
		String qaEventCategoryId = (String)dynaForm.get("selectedQaEventsCategoryId");
		String multipleSampleMode = (String)dynaForm.get("multipleSampleMode");
		String viewMode = (String)dynaForm.get("viewMode");
        String fullScreenSection = "";
        if (viewMode != null && viewMode.equals(QAEVENTS_ENTRY_FULL_SCREEN_VIEW)) {
        	fullScreenSection = (String)dynaForm.get("fullScreenSection");
        }
		
	
		session.setAttribute(ACCESSION_NUMBER, accessionNumber);
		session.setAttribute(QAEVENTS_ENTRY_PARAM_MULTIPLE_SAMPLE_MODE, multipleSampleMode);
		session.setAttribute(QAEVENTS_ENTRY_PARAM_QAEVENT_CATEGORY_ID, qaEventCategoryId);
		session.setAttribute(QAEVENTS_ENTRY_PARAM_VIEW_MODE, viewMode);
		session.setAttribute(QAEVENTS_ENTRY_FULL_SCREEN_VIEW_SECTION, fullScreenSection);
		

		return mapping.findForward(forward);

	}

	protected String getPageTitleKeyParameter(HttpServletRequest request,
			ActionForm form) {
		BaseActionForm dynaForm = (BaseActionForm) form;
		String accn = "";
		if (dynaForm.get("accessionNumber") != null) {
			accn = (String) dynaForm.get("accessionNumber");
		}
		return accn;
	}

	protected String getPageTitleKey() {
        return "qaevents.entry.linelisting.title";
	}

	protected String getPageSubtitleKey() {
        return "qaevents.entry.linelisting.title";
	}

}
