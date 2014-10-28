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
package us.mn.state.health.lims.method.action;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.method.dao.MethodDAO;
import us.mn.state.health.lims.method.daoimpl.MethodDAOImpl;
import us.mn.state.health.lims.method.valueholder.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class MethodAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new Method.
		// If there is a parameter present, we should bring up an existing
		// Method to edit.

		String id = request.getParameter(ID);

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		DynaActionForm dynaForm = (DynaActionForm) form;

		// initialize the form
		dynaForm.initialize(mapping);

		Method method = new Method();
		//System.out.println("I am in MethodAction and this is id " + id);
		if ((id != null) && (!"0".equals(id))) { // this is an existing
			// method

			method.setId(id);
			MethodDAO methodDAO = new MethodDAOImpl();
			methodDAO.getData(method);

			isNew = false; // this is to set correct page title

			// do we need to enable next or previous?
			//bugzilla 1427 pass in name not id
			List methods = methodDAO.getNextMethodRecord(method.getMethodName());
			if (methods.size() > 0) {
				// enable next button
				request.setAttribute(NEXT_DISABLED, "false");
			}
			//bugzilla 1427 pass in name not id
			methods = methodDAO.getPreviousMethodRecord(method.getMethodName());
			if (methods.size() > 0) {
				// enable next button
				request.setAttribute(PREVIOUS_DISABLED, "false");
			}
			// end of logic to enable next or previous button

		} else { // this is a new method
			// default started date to today's date
			Date today = Calendar.getInstance().getTime();

			String dateAsText = DateUtil.formatDateAsText(today);

			method.setActiveBeginDateForDisplay(dateAsText);

			// default isActive to 'Y'
			method.setIsActive(YES);

			isNew = true; // this is to set correct page title

		}

		if (method.getId() != null && !method.getId().equals("0")) {
			request.setAttribute(ID, method.getId());
		}

		// populate form from valueholder
		PropertyUtils.copyProperties(form, method);

		//System.out.println("I am in MethodAction this is forward " + forward);
		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "method.add.title";
		} else {
			return "method.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "method.add.title";
		} else {
			return "method.edit.title";
		}
	}

}
