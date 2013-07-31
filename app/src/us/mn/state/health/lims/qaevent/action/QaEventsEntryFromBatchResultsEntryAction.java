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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionRedirect;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.qaevent.valueholder.QaEventRoutingSwitchSessionHandler;

/**
 * @author diane benz
 * modified 06/2008 for bugzilla 2053
 * 
 */
public class QaEventsEntryFromBatchResultsEntryAction extends BaseAction {

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
		// bugzilla 2555 initialize the form 
		dynaForm.initialize(mapping);

		//initialize test management routing switch to on/initializes other switches to off
		QaEventRoutingSwitchSessionHandler.switchOn(QA_EVENTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_ENTRY, session);

		List accessionNumbersWithUnsatisfactoryResults = (ArrayList)session.getAttribute(QA_EVENTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_ENTRY_PARAM_ACCESSION_NUMBERS);
		String selectedTestId = (String)session.getAttribute(QA_EVENTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_ENTRY_PARAM_TEST_ID);

		if (accessionNumbersWithUnsatisfactoryResults != null && accessionNumbersWithUnsatisfactoryResults.size() > 0) {
 			forward = FWD_SUCCESS;
 			String currentAccessionNumber = (String)accessionNumbersWithUnsatisfactoryResults.get(0);
  			session.setAttribute(QA_EVENTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_ENTRY_PARAM_ACCESSION_NUMBERS, accessionNumbersWithUnsatisfactoryResults);
 			return getForward(mapping.findForward(forward), currentAccessionNumber);
		} else {
			forward = FWD_FAIL;
		}

		
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
		if (isNew) {
			return "qaeventsentry.add.title";
		} else {
			return "qaeventsentry.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "qaeventsentry.add.subtitle";
		} else {
			return "qaeventsentry.edit.subtitle";
		}
	}
	
	protected ActionForward getForward(ActionForward forward, String accessionNumber) {
		ActionRedirect redirect = new ActionRedirect(forward);

		if (accessionNumber != null)
			redirect.addParameter(ACCESSION_NUMBER, accessionNumber);

		return redirect;
	}

}
