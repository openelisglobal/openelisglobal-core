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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.codeelementtype.dao.CodeElementTypeDAO;
import us.mn.state.health.lims.codeelementtype.daoimpl.CodeElementTypeDAOImpl;
import us.mn.state.health.lims.codeelementtype.valueholder.CodeElementType;
import us.mn.state.health.lims.common.action.BaseMenuAction;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.valueholder.EnumValue;
import us.mn.state.health.lims.common.valueholder.EnumValueImpl;
import us.mn.state.health.lims.messageorganization.dao.MessageOrganizationDAO;
import us.mn.state.health.lims.messageorganization.daoimpl.MessageOrganizationDAOImpl;
import us.mn.state.health.lims.messageorganization.valueholder.MessageOrganization;
import us.mn.state.health.lims.receivercodeelement.dao.ReceiverCodeElementDAO;
import us.mn.state.health.lims.receivercodeelement.daoimpl.ReceiverCodeElementDAOImpl;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class ReceiverCodeElementMenuAction extends BaseMenuAction {

	protected List createMenuList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		//System.out.println("I am in ReceiverCodeElementMenuAction createMenuList()");

		List receiverCodeElements = new ArrayList();

		String stringStartingRecNo = (String) request
				.getAttribute("startingRecNo");
		int startingRecNo = Integer.parseInt(stringStartingRecNo);

		ReceiverCodeElementDAO receiverCodeElementDAO = new ReceiverCodeElementDAOImpl();
		receiverCodeElements = receiverCodeElementDAO.getPageOfReceiverCodeElements(startingRecNo);

		
		MessageOrganizationDAO messageOrganizationDAO = new MessageOrganizationDAOImpl();
		List messageOrganizations = messageOrganizationDAO.getAllMessageOrganizations();
		EnumValue ev = new EnumValueImpl();
		ev.setEnumName("MessageOrganization");
		for (int i = 0; i < messageOrganizations.size(); i++) {
			MessageOrganization mo = (MessageOrganization) messageOrganizations.get(i);
			ev.putValue(mo.getId(), mo);
		}

		HttpSession session = request.getSession();
		session.setAttribute("MessageOrganization", ev);
		
		CodeElementTypeDAO codeElementTypeDAO = new CodeElementTypeDAOImpl();
		List codeElementTypes = codeElementTypeDAO.getAllCodeElementTypes();
		ev = new EnumValueImpl();
		ev.setEnumName("CodeElementType");
		for (int i = 0; i < codeElementTypes.size(); i++) {
			CodeElementType mo = (CodeElementType) codeElementTypes.get(i);
			ev.putValue(mo.getId(), mo);
		}

		session.setAttribute("CodeElementType", ev);
		request.setAttribute("menuDefinition", "ReceiverCodeElementMenuDefinition");

		// bugzilla 1411 set pagination variables 
		request.setAttribute(MENU_TOTAL_RECORDS, String.valueOf(receiverCodeElementDAO
				.getTotalReceiverCodeElementCount()));
		request.setAttribute(MENU_FROM_RECORD, String.valueOf(startingRecNo));
		int numOfRecs = 0;
		if (receiverCodeElements != null) {
			if (receiverCodeElements.size() > SystemConfiguration.getInstance()
					.getDefaultPageSize()) {
				numOfRecs = SystemConfiguration.getInstance()
						.getDefaultPageSize();
			} else {
				numOfRecs = receiverCodeElements.size();
			}
			numOfRecs--;
		}
		int endingRecNo = startingRecNo + numOfRecs;
		request.setAttribute(MENU_TO_RECORD, String.valueOf(endingRecNo));
		//end bugzilla 1411
		
		return receiverCodeElements;
	}

	protected String getPageTitleKey() {
		return "receivercodeelement.browse.title";
	}

	protected String getPageSubtitleKey() {
		return "receivercodeelement.browse.title";
	}

	protected int getPageSize() {
		return SystemConfiguration.getInstance().getDefaultPageSize();
	}

	protected String getDeactivateDisabled() {
		return "true";
	}
}
