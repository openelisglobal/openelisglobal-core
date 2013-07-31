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
import org.apache.struts.action.ActionRedirect;
import org.apache.struts.action.DynaActionForm;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.qaevent.valueholder.QaEventLineListingRoutingSwitchSessionHandler;

/**
 * @author diane benz
 * bugzilla 2504
 */
public class QaEventsEntryLineListingCancelToQaEventsEntryAction extends BaseAction {

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		DynaActionForm dynaForm = (DynaActionForm) form;
		
		HttpSession session = request.getSession();
		
		String forward = FWD_SUCCESS;
		
		String accessionNumber = "";
		String qaEventCategoryId = (String)dynaForm.get("selectedQaEventsCategoryId");
		String multipleSampleMode = (String)dynaForm.get("multipleSampleMode");

		QaEventLineListingRoutingSwitchSessionHandler.switchOff(QA_EVENTS_ENTRY_LINELISTING_ROUTING_FROM_QAEVENTS_ENTRY, session);

		//THIS IS AN EXCEPTION WHERE ACCESSION NUMBER IS STORED IN SESSION (INSTEAD OF REQUEST) SINCE LINE LISTING IS
		//MULTIPLE SAMPLE MODE ONLY (NO ACCESSION NUMBER IN FORM)
		if (session.getAttribute(ACCESSION_NUMBER) != null) {
            accessionNumber = (String)session.getAttribute(ACCESSION_NUMBER);
			session.setAttribute(ACCESSION_NUMBER, null);
		}
		
		//reset this session attribute from line listing view
		session.setAttribute(QAEVENTS_ENTRY_PARAM_QAEVENT_CATEGORY_ID, qaEventCategoryId);

		if (!StringUtil.isNullorNill(multipleSampleMode)) {
            forward = FWD_SUCCESS_MULTIPLE_SAMPLE_MODE;
		} 

		//pass back the accession number
        return getForward(mapping.findForward(forward), accessionNumber);

	}

	protected String getPageTitleKey() {
		return "qaevents.entry.linelisting.title";
	}

	protected String getPageSubtitleKey() {
		return "qaevents.entry.linelisting.title";
	}
	
	protected ActionForward getForward(ActionForward forward, String accessionNumber) {
		ActionRedirect redirect = new ActionRedirect(forward);

		if (accessionNumber != null)
			redirect.addParameter(ACCESSION_NUMBER, accessionNumber);

		return redirect;
	}
	
}