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
import us.mn.state.health.lims.login.dao.LoginDAO;
import us.mn.state.health.lims.login.daoimpl.LoginDAOImpl;
import us.mn.state.health.lims.login.valueholder.Login;

/**
 *  @author     Hung Nguyen (Hung.Nguyen@health.state.mn.us)
 */
public class LoginUserNextPreviousAction extends BaseAction {
	
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

		Login login = new Login();

		login.setId(id);
		try {

			LoginDAO loginDAO = new LoginDAOImpl();
			loginDAO.getData(login);

			if (FWD_NEXT.equals(direction)) {
					List logins = loginDAO.getNextLoginUserRecord(login.getId());
					if (logins != null && logins.size() > 0) {
						login = (Login) logins.get(0);
						loginDAO.getData(login);
						if (logins.size() < 2) {
							request.setAttribute(NEXT_DISABLED, "true");
						}
						id = login.getId();
					} else {
						request.setAttribute(NEXT_DISABLED, "true");
					}
				}

				if (FWD_PREVIOUS.equals(direction)) {
					List logins = loginDAO.getPreviousLoginUserRecord(login.getId());
					if (logins != null && logins.size() > 0) {
						login = (Login) logins.get(0);
						loginDAO.getData(login);
						if (logins.size() < 2) {
							request.setAttribute(PREVIOUS_DISABLED, "true");
						}
						id = login.getId();
					} else {
						request.setAttribute(PREVIOUS_DISABLED, "true");
					}
				}

		} catch (LIMSRuntimeException lre) {
    		//bugzilla 2154
			LogEvent.logError("LoginUserNextPreviousAction","performAction()",lre.toString());  
			request.setAttribute(IActionConstants.ALLOW_EDITS_KEY, "false");
			request.setAttribute(PREVIOUS_DISABLED, "true");
			request.setAttribute(NEXT_DISABLED, "true");
			forward = FWD_FAIL;
		} 
		if (forward.equals(FWD_FAIL))
			return mapping.findForward(forward);


		if (login.getId() != null && !login.getId().equals("0")) {
			request.setAttribute("ID", login.getId());

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