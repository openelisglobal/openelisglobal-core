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
import us.mn.state.health.lims.qaevent.valueholder.QaEventRoutingSwitchSessionHandler;

/**
 * @author diane benz
 * bugzilla 2502
 * 
 */
public class QaEventsEntryFromQaEventsEntryLineListingAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		BaseActionForm dynaForm = (BaseActionForm) form;
		HttpSession session = request.getSession();

		String accessionNumber = (String)request.getParameter(ACCESSION_NUMBER);
        String viewMode = (String)dynaForm.get("viewMode");
        String fullScreenSection = (String)dynaForm.get("fullScreenSection");
        String multipleSampleMode = (String)dynaForm.get("multipleSampleMode");
		// bugzila 2555 initialize the form 
		dynaForm.initialize(mapping);
		//initialize test management routing switch to on/initializes other switches to off
		QaEventRoutingSwitchSessionHandler.switchOn(QA_EVENTS_ENTRY_ROUTING_FROM_QAEVENTS_ENTRY_LINELISTING, session);

		//PropertyUtils.setProperty(dynaForm, "viewMode", viewMode);
		//PropertyUtils.setProperty(dynaForm, "fullScreenSection", fullScreenSection);
		session.setAttribute(QAEVENTS_ENTRY_PARAM_VIEW_MODE, viewMode);
		session.setAttribute(QAEVENTS_ENTRY_FULL_SCREEN_VIEW_SECTION, fullScreenSection);
		request.setAttribute(ACCESSION_NUMBER, accessionNumber);

		if (!StringUtil.isNullorNill(multipleSampleMode)) {
			forward = FWD_SUCCESS_MULTIPLE_SAMPLE_MODE;
		}
		return mapping.findForward(forward);
	}

	protected String getPageTitleKeyParameter(HttpServletRequest request,
			ActionForm form) {
            return null;
	}

	protected String getPageTitleKey() {
  		return null;
	}

	protected String getPageSubtitleKey() {
        return null;
	}
	
}
