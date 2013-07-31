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
package us.mn.state.health.lims.codeelementxref.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import us.mn.state.health.lims.codeelementtype.dao.CodeElementTypeDAO;
import us.mn.state.health.lims.codeelementtype.daoimpl.CodeElementTypeDAOImpl;
import us.mn.state.health.lims.messageorganization.dao.MessageOrganizationDAO;
import us.mn.state.health.lims.messageorganization.daoimpl.MessageOrganizationDAOImpl;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class CodeElementXrefAction extends CodeElementXrefBaseAction {

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new CodeElementXref.
		// If there is a parameter present, we should bring up an existing
		// CodeElementXref to edit.
		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		DynaActionForm dynaForm = (DynaActionForm) form;

		// initialize the form
		dynaForm.initialize(mapping);


		MessageOrganizationDAO messageOrganizationDAO = new MessageOrganizationDAOImpl();
		List messageOrganizations = messageOrganizationDAO
				.getAllMessageOrganizations();
		PropertyUtils.setProperty(form, "messageOrganizations",
				messageOrganizations);

		CodeElementTypeDAO codeElementTypeDAO = new CodeElementTypeDAOImpl();
		List codeElementTypes = codeElementTypeDAO.getAllCodeElementTypes();
		PropertyUtils.setProperty(form, "codeElementTypes", codeElementTypes);

		PropertyUtils.setProperty(form, "localCodeElements", new ArrayList());
		PropertyUtils.setProperty(form, "receiverCodeElements", new ArrayList());
		PropertyUtils.setProperty(form, "codeElementXrefs", new ArrayList());

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
			return "codeelementxref.edit.title";
	}

	protected String getPageSubtitleKey() {
			return "codeelementxref.edit.title";
	}

}
