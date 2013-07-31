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
package us.mn.state.health.lims.scriptlet.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.scriptlet.dao.ScriptletDAO;
import us.mn.state.health.lims.scriptlet.daoimpl.ScriptletDAOImpl;
import us.mn.state.health.lims.scriptlet.valueholder.Scriptlet;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class ScriptletAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new Scriptlet.
		// If there is a parameter present, we should bring up an existing
		// Scriptlet to edit.

		String id = request.getParameter(ID);

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		BaseActionForm dynaForm = (BaseActionForm) form;

		// initialize the form
		dynaForm.initialize(mapping);

		Scriptlet scriptlet = new Scriptlet();

		if ((id != null) && (!"0".equals(id))) { // this is an existing
			// scriptlet

			scriptlet.setId(id);
			ScriptletDAO scriptletDAO = new ScriptletDAOImpl();
			scriptletDAO.getData(scriptlet);

			isNew = false; // this is to set correct page title

			// do we need to enable next or previous?
			//bugzilla 1427 pass in name not id
			List scriptlets = scriptletDAO.getNextScriptletRecord(scriptlet
					.getScriptletName());
			if (scriptlets.size() > 0) {
				// enable next button
				request.setAttribute(NEXT_DISABLED, "false");
			}
			//bugzilla 1427 pass in name not id
			scriptlets = scriptletDAO.getPreviousScriptletRecord(scriptlet
					.getScriptletName());
			if (scriptlets.size() > 0) {
				// enable next button
				request.setAttribute(PREVIOUS_DISABLED, "false");
			}
			// end of logic to enable next or previous button

		} else { // this is a new scriptlet

			isNew = true; // this is to set correct page title

		}

		if (scriptlet.getId() != null && !scriptlet.getId().equals("0")) {
			request.setAttribute(ID, scriptlet.getId());
		}

		// populate form from valueholder
		PropertyUtils.copyProperties(dynaForm, scriptlet);

		//System.out
		//		.println("I am in ScriptletAction this is forward " + forward);
		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "scriptlet.add.title";
		} else {
			return "scriptlet.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "scriptlet.add.title";
		} else {
			return "scriptlet.edit.title";
		}
	}

}
