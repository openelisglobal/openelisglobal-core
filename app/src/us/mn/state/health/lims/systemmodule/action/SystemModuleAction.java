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
package us.mn.state.health.lims.systemmodule.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.systemmodule.dao.SystemModuleDAO;
import us.mn.state.health.lims.systemmodule.daoimpl.SystemModuleDAOImpl;
import us.mn.state.health.lims.systemmodule.valueholder.SystemModule;

/**
 *  @author     Hung Nguyen (Hung.Nguyen@health.state.mn.us)
 */
public class SystemModuleAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String id = request.getParameter(ID);

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		DynaActionForm dynaForm = (DynaActionForm) form;

		// initialize the form
		dynaForm.initialize(mapping);

		SystemModule systemModule = new SystemModule();
		if ((id != null) && (!"0".equals(id))) {
			systemModule.setId(id);
			SystemModuleDAO systemModuleDAO = new SystemModuleDAOImpl();
			systemModuleDAO.getData(systemModule);

			isNew = false;
			
			List systemModules = systemModuleDAO.getNextSystemModuleRecord(systemModule.getId());
			if (systemModules.size() > 0) {
				// enable next button
				request.setAttribute(NEXT_DISABLED, "false");
			}
			systemModules = systemModuleDAO.getPreviousSystemModuleRecord(systemModule.getId());
			if (systemModules.size() > 0) {
				// enable next button
				request.setAttribute(PREVIOUS_DISABLED, "false");
			}
			// end of logic to enable next or previous button


		} else {

			isNew = true; // this is to set correct page title

		}

		if (systemModule.getId() != null && !systemModule.getId().equals("0")) {
			request.setAttribute(ID, systemModule.getId());
		}

		// populate form from valueholder
		PropertyUtils.copyProperties(form, systemModule);

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "systemmodule.add.title";
		} else {
			return "systemmodule.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "systemmodule.add.title";
		} else {
			return "systemmodule.edit.title";
		}
	}


}
