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
package us.mn.state.health.lims.systemusersection.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.systemusersection.dao.SystemUserSectionDAO;
import us.mn.state.health.lims.systemusersection.daoimpl.SystemUserSectionDAOImpl;
import us.mn.state.health.lims.systemusersection.valueholder.SystemUserSection;

/**
 *  @author     Hung Nguyen (Hung.Nguyen@health.state.mn.us)
 */
public class SystemUserSectionNextPreviousAction extends BaseAction {
	
	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(IActionConstants.ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "false");
		request.setAttribute(NEXT_DISABLED, "false");
		
		String id = request.getParameter("ID");
		
	
		if (StringUtil.isNullorNill(id) || "0".equals(id)) {
			isNew = true;
		} else {
			isNew = false;
		}
		
	
		BaseActionForm dynaForm = (BaseActionForm) form;

		String start = (String) request.getParameter("startingRecNo");
		String direction = (String) request.getParameter("direction");

		SystemUserSection systemUserSection = new SystemUserSection();

		systemUserSection.setId(id);
		try {

			SystemUserSectionDAO systemUserSectionDAO = new SystemUserSectionDAOImpl();
			systemUserSectionDAO.getData(systemUserSection);

			if (FWD_NEXT.equals(direction)) {
					List systemUserSections = systemUserSectionDAO.getNextSystemUserSectionRecord(systemUserSection.getId());
					if (systemUserSections != null && systemUserSections.size() > 0) {
						systemUserSection = (SystemUserSection) systemUserSections.get(0);
						systemUserSectionDAO.getData(systemUserSection);
						if (systemUserSections.size() < 2) {
							request.setAttribute(NEXT_DISABLED, "true");
						}
						id = systemUserSection.getId();
					} else {
						request.setAttribute(NEXT_DISABLED, "true");
					}
				}

				if (FWD_PREVIOUS.equals(direction)) {
					List systemUserSections = systemUserSectionDAO.getPreviousSystemUserSectionRecord(systemUserSection.getId());
					if (systemUserSections != null && systemUserSections.size() > 0) {
						systemUserSection = (SystemUserSection) systemUserSections.get(0);
						systemUserSectionDAO.getData(systemUserSection);
						if (systemUserSections.size() < 2) {
							request.setAttribute(PREVIOUS_DISABLED, "true");
						}
						id = systemUserSection.getId();
					} else {
						request.setAttribute(PREVIOUS_DISABLED, "true");
					}
				}

		} catch (LIMSRuntimeException lre) {
    		//bugzilla 2154
			LogEvent.logError("SystemUserSectionNextPreviousAction","performAction()",lre.toString());
			request.setAttribute(IActionConstants.ALLOW_EDITS_KEY, "false");
			request.setAttribute(PREVIOUS_DISABLED, "true");
			request.setAttribute(NEXT_DISABLED, "true");
			forward = FWD_FAIL;
		} 
		if (forward.equals(FWD_FAIL))
			return mapping.findForward(forward);


		if (systemUserSection.getId() != null && !systemUserSection.getId().equals("0")) {
			request.setAttribute("ID", systemUserSection.getId());

		}

		return getForward(mapping.findForward(forward), id, start);

	}

	protected String getPageTitleKey() {
       return null;
	}

	protected String getPageSubtitleKey() {
       return null;
	}

}