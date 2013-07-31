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
package us.mn.state.health.lims.result.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionRedirect;
import org.apache.struts.action.DynaActionForm;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.result.valueholder.ResultsEntryRoutingSwitchSessionHandler;

/**
 * @author diane benz
 * bugzilla 2053
 */
public class ResultsEntryFromQaEventsEntryAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new Result.
		// If there is a parameter present, we should bring up an existing
		// Result to edit.
		String id = request.getParameter(ID);
		String accessionNumber = request.getParameter("accNum");

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		DynaActionForm dynaForm = (DynaActionForm) form;
		String qaEventCategoryId = (String)dynaForm.get("selectedQaEventsCategoryId");
		String multipleSampleMode = (String)dynaForm.get("multipleSampleMode");
		//bugzilla 2502
		String viewMode = (String)dynaForm.get("viewMode");
		String fullScreenSection = (String)dynaForm.get("fullScreenSection");

        //bugzilla 1774 modifications for going between modules
        HttpSession session = request.getSession();
        ResultsEntryRoutingSwitchSessionHandler.switchOn(RESULTS_ENTRY_ROUTING_FROM_QAEVENTS_ENTRY, session);
		session.setAttribute(RESULTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_VERIFICATION_PARAM_ACCESSION_NUMBER, accessionNumber);

		session.setAttribute(QAEVENTS_ENTRY_PARAM_MULTIPLE_SAMPLE_MODE, multipleSampleMode);
		session.setAttribute(QAEVENTS_ENTRY_PARAM_QAEVENT_CATEGORY_ID, qaEventCategoryId);
		//bugzilla 2502
		session.setAttribute(QAEVENTS_ENTRY_PARAM_VIEW_MODE, viewMode);
		session.setAttribute(QAEVENTS_ENTRY_FULL_SCREEN_VIEW_SECTION, fullScreenSection);
		
		return getForward(mapping.findForward(forward), accessionNumber);
	}

	protected String getPageTitleKey() {
		return "";
	}

	protected String getPageSubtitleKey() {
		return "";
	}
	
	protected ActionForward getForward(ActionForward forward, String accessionNumber) {
		ActionRedirect redirect = new ActionRedirect(forward);
		if (!StringUtil.isNullorNill(accessionNumber))
			redirect.addParameter(ACCESSION_NUMBER, accessionNumber);
	
		return redirect;

	}

}
