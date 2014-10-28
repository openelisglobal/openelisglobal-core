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
package us.mn.state.health.lims.login.action;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.login.dao.LoginDAO;
import us.mn.state.health.lims.login.daoimpl.LoginDAOImpl;
import us.mn.state.health.lims.login.valueholder.Login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *  @author     Hung Nguyen (Hung.Nguyen@health.state.mn.us)
 */
public class LoginUserAction extends BaseAction {

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

		Date today = Calendar.getInstance().getTime();
		String dateAsText = DateUtil.formatDateAsText(today);
		PropertyUtils.setProperty(form, "currentDate", dateAsText);
		
		Login login = new Login();
		if ((id != null) && (!"0".equals(id))) {
			login.setId(id);
			LoginDAO loginDAO = new LoginDAOImpl();
			loginDAO.getData(login);

			isNew = false;
			
			List logins = loginDAO.getNextLoginUserRecord(login.getId());
			if (logins.size() > 0) {
				// enable next button
				request.setAttribute(NEXT_DISABLED, "false");
			}
			logins = loginDAO.getPreviousLoginUserRecord(login.getId());
			if (logins.size() > 0) {
				// enable next button
				request.setAttribute(PREVIOUS_DISABLED, "false");
			}
			// end of logic to enable next or previous button
			
		} else {
			//java.util.Date today = java.util.Calendar.getInstance().getTime();
			login.setPasswordExpiredDateForDisplay(dateAsText);
			isNew = true; // this is to set correct page title
		}

		if (login.getId() != null && !login.getId().equals("0")) {
			request.setAttribute(ID, login.getId());
		}

		// populate form from valueholder				
		PropertyUtils.copyProperties(form, login);

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "login.add.title";
		} else {
			return "login.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "login.add.title";
		} else {
			return "login.edit.title";
		}
	}


}
