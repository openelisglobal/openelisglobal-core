/*
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
*
* Contributor(s): CIRG, University of Washington, Seattle WA.
*/
package us.mn.state.health.lims.patient.action;


import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;

import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.util.DateUtil;


/**
 * Action for Côte d'Ivoire study based patient edit.
 * @author pahill
 * @since 2010-04-16
 */
public class PatientEditByProjectAction extends BasePatientEntryByProject {

	private String todayAsText;
	
	@Override
	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		todayAsText = DateUtil.formatDateAsText(new Date());
		
		String forward = "success";
		
		BaseActionForm dynaForm = (BaseActionForm) form;

		request.getSession().setAttribute(IActionConstants.SAVE_DISABLED, IActionConstants.TRUE);

		// Initialize the form.
		dynaForm.initialize(mapping);
				
		updateRequestType(request);
		
		// Set current date and entered date to today's date		
		PropertyUtils.setProperty(form, "currentDate", todayAsText);	// TODO Needed?
		
		addAllPatientFormLists(dynaForm);
		
		return mapping.findForward(forward);
	}
	
	protected String getPageTitleKey() {
		return "patient.project.title";
	}


	protected String getPageSubtitleKey() {
		String key = null;

		switch (requestType) {
			case READWRITE: {
				key = "banner.menu.editPatient.ReadWrite";
				break;
			}
			case READONLY: {
				key = "banner.menu.editPatient.ReadOnly";
				break;
			}
			
			default: {
				key = "banner.menu.editPatient.ReadOnly";
			}
		}

		return key;
	}
	
	public String getProject() {
	    return null;
	}
}
