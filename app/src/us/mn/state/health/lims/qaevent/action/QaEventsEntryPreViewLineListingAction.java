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

/**
 * @author diane benz
 * bugzilla 2504
 */
public class QaEventsEntryPreViewLineListingAction extends BaseAction {

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;

		HttpSession session = request.getSession();
		BaseActionForm dynaForm = (BaseActionForm) form;

		// initialize the form
		dynaForm.initialize(mapping);
		
		//also clear out session vars for routing
		session.setAttribute(QAEVENTS_ENTRY_LINELISTING_PARAM_QAEVENT_CATEGORY_ID, null);
		

		return mapping.findForward(forward);
	}

	protected String getPageTitleKeyParameter(HttpServletRequest request) {
          return null;
	}

	protected String getPageTitleKey() {
	      return "qaevents.entry.linelisting.title";
	}

	protected String getPageSubtitleKey() {
	      return "qaevents.entry.linelisting.title";
	}

}
