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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.common.action.BaseMenuAction;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.valueholder.EnumValue;
import us.mn.state.health.lims.common.valueholder.EnumValueImpl;
import us.mn.state.health.lims.method.dao.MethodDAO;
import us.mn.state.health.lims.method.daoimpl.MethodDAOImpl;
import us.mn.state.health.lims.systemuser.dao.SystemUserDAO;
import us.mn.state.health.lims.systemuser.daoimpl.SystemUserDAOImpl;
import us.mn.state.health.lims.systemuser.valueholder.SystemUser;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class MethodMenuAction extends BaseMenuAction {

	protected List createMenuList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		//System.out.println("I am in ProjectMenuAction createMenuList()");

		List methods = new ArrayList();

		String stringStartingRecNo = (String) request
				.getAttribute("startingRecNo");
		int startingRecNo = Integer.parseInt(stringStartingRecNo);

		MethodDAO methodDAO = new MethodDAOImpl();
		// methods = (Vector)methodDAO.getAllMethods();
		methods = methodDAO.getPageOfMethods(startingRecNo);

		SystemUserDAO sysUserDAO = new SystemUserDAOImpl();
		List sysUsers = sysUserDAO.getAllSystemUsers();
		EnumValue ev = new EnumValueImpl();
		ev.setEnumName("SystemUser");
		for (int i = 0; i < sysUsers.size(); i++) {
			SystemUser su = (SystemUser) sysUsers.get(i);
			ev.putValue(su.getId(), su);
		}

		HttpSession session = request.getSession();
		session.setAttribute("SystemUser", ev);

		// Now create Hashtable to get SystemUser names on menu - store in
		// request scope
		/*
		 * Hashtable ht = new Hashtable(); for (int i = 0; i < sysUsers.size();
		 * i++) { String key = ((SystemUser)sysUsers.get(i)).getId(); SystemUser
		 * value = (SystemUser)sysUsers.get(i); ht.put(key, value); }
		 * request.setAttribute("sysUsers", ht);
		 */

		request.setAttribute("menuDefinition", "MethodMenuDefinition");
		
		// bugzilla 1411 set pagination variables 
		request.setAttribute(MENU_TOTAL_RECORDS, String.valueOf(methodDAO
				.getTotalMethodCount()));
		request.setAttribute(MENU_FROM_RECORD, String.valueOf(startingRecNo));
		int numOfRecs = 0;
		if (methods != null) {
			if (methods.size() > SystemConfiguration.getInstance()
					.getDefaultPageSize()) {
				numOfRecs = SystemConfiguration.getInstance()
						.getDefaultPageSize();
			} else {
				numOfRecs = methods.size();
			}
			numOfRecs--;
		}
		int endingRecNo = startingRecNo + numOfRecs;
		request.setAttribute(MENU_TO_RECORD, String.valueOf(endingRecNo));
		//end bugzilla 1411
		
		return methods;
	}

	protected String getPageTitleKey() {
		return "method.browse.title";
	}

	protected String getPageSubtitleKey() {
		return "method.browse.title";
	}

	protected int getPageSize() {
		return SystemConfiguration.getInstance().getDefaultPageSize();
	}

	protected String getDeactivateDisabled() {
		return "false";
	}

}
