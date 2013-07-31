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
package us.mn.state.health.lims.receivercodeelement.action;

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
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.messageorganization.dao.MessageOrganizationDAO;
import us.mn.state.health.lims.messageorganization.daoimpl.MessageOrganizationDAOImpl;
import us.mn.state.health.lims.receivercodeelement.dao.ReceiverCodeElementDAO;
import us.mn.state.health.lims.receivercodeelement.daoimpl.ReceiverCodeElementDAOImpl;
import us.mn.state.health.lims.receivercodeelement.valueholder.ReceiverCodeElement;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class ReceiverCodeElementAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new ReceiverCodeElement.
		// If there is a parameter present, we should bring up an existing
		// ReceiverCodeElement to edit.
		String id = request.getParameter(ID);

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		DynaActionForm dynaForm = (DynaActionForm) form;

		// initialize the form
		dynaForm.initialize(mapping);

		ReceiverCodeElement receiverCodeElement = new ReceiverCodeElement();

		if ((id != null) && (!"0".equals(id))) { // this is an existing
			// receiverCodeElement

			receiverCodeElement.setId(id);
			ReceiverCodeElementDAO receiverCodeElementDAO = new ReceiverCodeElementDAOImpl();
			receiverCodeElementDAO.getData(receiverCodeElement);

			isNew = false; // this is to set correct page title
			
			// initialize selectedMessageOrganizationId
			if (receiverCodeElement.getMessageOrganization() != null) {
				receiverCodeElement
						.setSelectedMessageOrganizationId(receiverCodeElement
								.getMessageOrganization().getId());
			}
			if (receiverCodeElement.getCodeElementType() != null) {
				receiverCodeElement
						.setSelectedCodeElementTypeId(receiverCodeElement
								.getCodeElementType().getId());
			}

			// do we need to enable next or previous?
			//bugzilla 1427 pass in name not id
			List receiverCodeElements = receiverCodeElementDAO.getNextReceiverCodeElementRecord(receiverCodeElement.getId());
			if (receiverCodeElements.size() > 0) {
				// enable next button
				request.setAttribute(NEXT_DISABLED, "false");
			}
			//bugzilla 1427 pass in name not id
			receiverCodeElements = receiverCodeElementDAO.getPreviousReceiverCodeElementRecord(receiverCodeElement.getId());
			if (receiverCodeElements.size() > 0) {
				// enable next button
				request.setAttribute(PREVIOUS_DISABLED, "false");
			}
			// end of logic to enable next or previous button
		} else { // this is a new receiverCodeElement

			isNew = true; // this is to set correct page title

			//this is additional functionality to keep previously selected messageOrganizationId and codeElementTypeId for convenience
			String selectedMessageOrganizationId = (String)request.getParameter("selectedMessageOrganizationId");
			if (!StringUtil.isNullorNill(selectedMessageOrganizationId)) {
				receiverCodeElement.setSelectedMessageOrganizationId(selectedMessageOrganizationId);
			}
			String selectedCodeElementTypeId = (String)request.getParameter("selectedCodeElementTypeId");
			if (!StringUtil.isNullorNill(selectedCodeElementTypeId)) {
				receiverCodeElement.setSelectedCodeElementTypeId(selectedCodeElementTypeId);
			}
			
		}

		if (receiverCodeElement.getId() != null && !receiverCodeElement.getId().equals("0")) {
			request.setAttribute(ID, receiverCodeElement.getId());
		}

		// populate form from valueholder
		PropertyUtils.copyProperties(form, receiverCodeElement);

		MessageOrganizationDAO messageOrganizationDAO = new MessageOrganizationDAOImpl();
		List messageOrganizations = messageOrganizationDAO.getAllMessageOrganizations();
		PropertyUtils.setProperty(form, "messageOrganizations", messageOrganizations);
		
		CodeElementTypeDAO codeElementTypeDAO = new CodeElementTypeDAOImpl();
		List codeElementTypes = codeElementTypeDAO.getAllCodeElementTypes();
		PropertyUtils.setProperty(form, "codeElementTypes", codeElementTypes);


		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "receivercodeelement.add.title";
		} else {
			return "receivercodeelement.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "receivercodeelement.add.title";
		} else {
			return "receivercodeelement.edit.title";
		}
	}

}
