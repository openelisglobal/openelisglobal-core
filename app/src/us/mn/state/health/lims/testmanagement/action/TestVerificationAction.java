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

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.testmanagement.valueholder.TestManagementRoutingSwitchSessionHandler;

/**
 * @author aiswarya raman
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 * 
 * bugzilla 1774: this action is now an intializing action that sets
 * the session variable IActionConstants.TEST_MANAGEMENT_ROUTING_FROM_RESULTS_ENTRY to false
 * bugzilla 1942: removed major portion of the code because it is not needed: most
 *    of the logic isn't needed until TestVerificationViewAction and 
 *    we don't need to duplicate it here..
 
 */
public class TestVerificationAction extends BaseAction {	

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		
		BaseActionForm testManagementForm = (BaseActionForm)form;
		
		if (request.getParameter(ID) == null) {		
			testManagementForm.initialize(mapping);		
		}
		
	
		// bugzilla 1774 Initialize this session variable to false!!!!
		HttpSession session = request.getSession();
		//bugzila 2053
		TestManagementRoutingSwitchSessionHandler.switchAllOff(session);
		
		testManagementForm.initialize(mapping);	
		


		PropertyUtils.setProperty(form, "tests", new ArrayList());		
		
		
		return mapping.findForward(FWD_SUCCESS);	
	}
	
	protected String getPageTitleKey() {
		return "testmanagement.title";
	}

	protected String getPageSubtitleKey() {
		return "testmanagement.title";
	}
}