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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.common.action.BaseMenuAction;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.systemusersection.dao.SystemUserSectionDAO;
import us.mn.state.health.lims.systemusersection.daoimpl.SystemUserSectionDAOImpl;

/**
 *  @author     Hung Nguyen (Hung.Nguyen@health.state.mn.us)
 */
public class SystemUserSectionMenuAction extends BaseMenuAction {

	protected List createMenuList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
				
    	//bugzilla 2154
    	LogEvent.logDebug("SystemUserSectionMenuAction","createMenuList()","I am in SystemUserSectionMenuAction createMenuList()");		
		List systemUserSections = new ArrayList();

		String stringStartingRecNo = (String) request.getAttribute("startingRecNo");
		int startingRecNo = Integer.parseInt(stringStartingRecNo);

		SystemUserSectionDAO systemUserSectionDAO = new SystemUserSectionDAOImpl();
		systemUserSections = systemUserSectionDAO.getPageOfSystemUserSections(startingRecNo);
	
		request.setAttribute("menuDefinition", "SystemUserSectionMenuDefinition");
		
		request.setAttribute(MENU_TOTAL_RECORDS, String.valueOf(systemUserSectionDAO.getTotalSystemUserSectionCount()));
		request.setAttribute(MENU_FROM_RECORD, String.valueOf(startingRecNo));
		int numOfRecs = 0;
		if (systemUserSections != null) {
			if (systemUserSections.size() > SystemConfiguration.getInstance().getDefaultPageSize()) {
				numOfRecs = SystemConfiguration.getInstance().getDefaultPageSize();
			} else {
				numOfRecs = systemUserSections.size();
			}
			numOfRecs--;
		}
		int endingRecNo = startingRecNo + numOfRecs;
		request.setAttribute(MENU_TO_RECORD, String.valueOf(endingRecNo));

		return systemUserSections;
	}

	protected String getPageTitleKey() {
		return "systemusersection.browse.title";
	}

	protected String getPageSubtitleKey() {
		return "systemusersection.browse.title";
	}

	protected int getPageSize() {
		return SystemConfiguration.getInstance().getDefaultPageSize();
	}

	protected String getDeactivateDisabled() {
	//bugzilla 2206
		return "true";
	}

}
