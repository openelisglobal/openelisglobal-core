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
package us.mn.state.health.lims.sample.action;

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
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
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
public class SampleMenuAction extends BaseMenuAction {

	protected List createMenuList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

 		//System.out.println("I am in SampleMenuAction createMenuList()");

		List samples = new ArrayList();

		String stringStartingRecNo = (String) request
				.getAttribute("startingRecNo");
		int startingRecNo = Integer.parseInt(stringStartingRecNo);

		SampleDAO sampleDAO = new SampleDAOImpl();
		// samples = (Vector)sampleDAO.getAllSamples();
		samples = sampleDAO.getPageOfSamples(startingRecNo);

	/*	List parentSamples = sampleDAO.getAllSamples();

		EnumValue ev = new EnumValueImpl();
		ev.setEnumName("Sample");
		for (int i = 0; i < parentSamples.size(); i++) {
			Sample samp = (Sample) parentSamples.get(i);
			ev.putValue(samp.getId(), samp);
		}*/

		HttpSession session = request.getSession();
		//session.setAttribute("Sample", ev);

		SystemUserDAO sysUserDAO = new SystemUserDAOImpl();
		List sysUsers = sysUserDAO.getAllSystemUsers();
		EnumValue ev = new EnumValueImpl();
		ev.setEnumName("SystemUser");
		for (int i = 0; i < sysUsers.size(); i++) {
			SystemUser su = (SystemUser) sysUsers.get(i);
			ev.putValue(su.getId(), su);
		}

		session.setAttribute("SystemUser", ev);

		// Now create Hashtable to get SystemUser names on menu - store in
		// request scope
		/*
		 * Hashtable ht = new Hashtable(); for (int i = 0; i < sysUsers.size();
		 * i++) { String key = ((SystemUser)sysUsers.get(i)).getId(); SystemUser
		 * value = (SystemUser)sysUsers.get(i); ht.put(key, value);
		 *  } request.setAttribute("sysUsers", ht);
		 */

		request.setAttribute("menuDefinition", "SampleMenuDefinition");

		return samples;
	}

	protected String getPageTitleKey() {
		return "sample.browse.title";
	}

	protected String getPageSubtitleKey() {
		return "sample.browse.title";
	}

	protected int getPageSize() {
		return SystemConfiguration.getInstance().getDefaultPageSize();
	}

	protected String getDeactivateDisabled() {
		return "true";
	}

}
