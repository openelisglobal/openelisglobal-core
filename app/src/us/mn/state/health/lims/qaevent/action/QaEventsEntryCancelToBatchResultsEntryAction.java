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
import org.apache.struts.action.DynaActionForm;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.qaevent.valueholder.QaEventRoutingSwitchSessionHandler;

/**
 * @author diane benz
 * modified 06/2008 for bugzilla 2053
 */
public class QaEventsEntryCancelToBatchResultsEntryAction extends BaseAction {

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		DynaActionForm dynaForm = (DynaActionForm) form;
		
		HttpSession session = request.getSession();
		
		String forward = FWD_SUCCESS;
		
		String selectedTestId = (String)session.getAttribute(QA_EVENTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_ENTRY_PARAM_TEST_ID);
		
		List accessionNumbersWithUnsatisfactoryResults = (ArrayList)session.getAttribute(QA_EVENTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_ENTRY_PARAM_ACCESSION_NUMBERS);

		if (accessionNumbersWithUnsatisfactoryResults != null && accessionNumbersWithUnsatisfactoryResults.size() > 0) {
 			//remove this accession number so we don't use it again
 			accessionNumbersWithUnsatisfactoryResults.remove(0);
 		} 
		
		if (accessionNumbersWithUnsatisfactoryResults != null && accessionNumbersWithUnsatisfactoryResults.size() > 0) {
 			forward = FWD_SUCCESS_QA_EVENTS_ENTRY;
 			String currentAccessionNumber = (String)accessionNumbersWithUnsatisfactoryResults.get(0);
  			session.setAttribute(QA_EVENTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_ENTRY_PARAM_ACCESSION_NUMBERS, accessionNumbersWithUnsatisfactoryResults);
 			return getForwardReturnToQaEvents(mapping.findForward(forward), currentAccessionNumber);
		} else {
			//flip switch back off since we don't need to route to QA Events Entry anymore (all access numbers have been processed)
			QaEventRoutingSwitchSessionHandler.switchOff(QA_EVENTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_ENTRY, session);
		}

		//pass back the test id
        return getForward(mapping.findForward(forward), selectedTestId);

	}

	protected String getPageTitleKey() {
		return "qaeventsentry.browse.title";
	}

	protected String getPageSubtitleKey() {
		return "qaeventsentry.browse.title";
	}
	
	protected ActionForward getForward(ActionForward forward, String selectedTestId) {
		ActionRedirect redirect = new ActionRedirect(forward);

		if (selectedTestId != null)
			redirect.addParameter(SELECTED_TEST_ID, selectedTestId);

		return redirect;
	}
	
	protected ActionForward getForwardReturnToQaEvents(ActionForward forward, String accessionNumber) {
		ActionRedirect redirect = new ActionRedirect(forward);

		if (accessionNumber != null)
			redirect.addParameter(ACCESSION_NUMBER, accessionNumber);

		return redirect;
	}
	

}