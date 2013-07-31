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
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.DynaActionForm;

import us.mn.state.health.lims.codeelementtype.dao.CodeElementTypeDAO;
import us.mn.state.health.lims.codeelementtype.daoimpl.CodeElementTypeDAOImpl;
import us.mn.state.health.lims.codeelementtype.valueholder.CodeElementType;
import us.mn.state.health.lims.codeelementxref.dao.CodeElementXrefDAO;
import us.mn.state.health.lims.codeelementxref.daoimpl.CodeElementXrefDAOImpl;
import us.mn.state.health.lims.codeelementxref.valueholder.CodeElementXref;
import us.mn.state.health.lims.codeelementxref.valueholder.CodeElementXrefLocalCodeElementNameComparator;
import us.mn.state.health.lims.common.dao.EnumDAO;
import us.mn.state.health.lims.common.daoimpl.EnumDAOImpl;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.common.valueholder.EnumComparator;
import us.mn.state.health.lims.common.valueholder.EnumValueItem;
import us.mn.state.health.lims.messageorganization.dao.MessageOrganizationDAO;
import us.mn.state.health.lims.messageorganization.daoimpl.MessageOrganizationDAOImpl;
import us.mn.state.health.lims.messageorganization.valueholder.MessageOrganization;
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
public class CodeElementXrefViewAction extends CodeElementXrefBaseAction {

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	
		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		DynaActionForm dynaForm = (DynaActionForm) form;
		
	
		String selectedMessageOrganizationId = null;
		String selectedCodeElementTypeId = null;
		selectedMessageOrganizationId = (String)dynaForm.get("selectedMessageOrganizationId");
		selectedCodeElementTypeId = (String)dynaForm.getString("selectedCodeElementTypeId");
		
		if (StringUtil.isNullorNill(selectedMessageOrganizationId)) {
			selectedMessageOrganizationId = (String)request.getParameter("messageOrganizationId");
		}
		
		if (StringUtil.isNullorNill(selectedCodeElementTypeId)) {
			selectedCodeElementTypeId = (String)request.getParameter("codeElementTypeId");
		}

		// initialize the form
		dynaForm.initialize(mapping);

	
		CodeElementXrefDAO codeElementXrefDAO = new CodeElementXrefDAOImpl();
		MessageOrganizationDAO messageOrganizationDAO = new MessageOrganizationDAOImpl();
		ReceiverCodeElementDAO receiverCodeElementDAO = new ReceiverCodeElementDAOImpl();
		EnumDAO enumDAO = new EnumDAOImpl();


		// populate form from valueholder

		List messageOrganizations = messageOrganizationDAO
				.getAllMessageOrganizations();
		PropertyUtils.setProperty(form, "messageOrganizations",
				messageOrganizations);

		CodeElementTypeDAO codeElementTypeDAO = new CodeElementTypeDAOImpl();
		List codeElementTypes = codeElementTypeDAO.getAllCodeElementTypes();
		PropertyUtils.setProperty(form, "codeElementTypes", codeElementTypes);
		
		PropertyUtils.setProperty(form, "selectedMessageOrganizationId", selectedMessageOrganizationId);
		PropertyUtils.setProperty(form, "selectedCodeElementTypeId", selectedCodeElementTypeId);

		ReceiverCodeElement receiverCodeElement = new ReceiverCodeElement();
		MessageOrganization mo = new MessageOrganization();
		if (!StringUtil.isNullorNill(selectedMessageOrganizationId)) {
			mo.setId(selectedMessageOrganizationId);
			messageOrganizationDAO.getData(mo);
		}

		CodeElementType cet = new CodeElementType();
		if (!StringUtil.isNullorNill(selectedCodeElementTypeId)) {
			cet.setId(selectedCodeElementTypeId);
			codeElementTypeDAO.getData(cet);
		}

		List linkedCodeElements = new ArrayList();
		CodeElementXref codeElementXref = new CodeElementXref();
		if (mo != null && !StringUtil.isNullorNill(mo.getId()) && cet != null
				&& !StringUtil.isNullorNill(cet.getId())) {
			codeElementXref.setMessageOrganization(mo);
			codeElementXref.setCodeElementType(cet);
			linkedCodeElements = codeElementXrefDAO.
			   getCodeElementXrefsByReceiverOrganizationAndCodeElementType(codeElementXref);
		}
		
		//for each codeElementXref fill in the vh for localCodeElement
		for (int i = 0; i < linkedCodeElements.size(); i++) {
			CodeElementXref codeElXref = (CodeElementXref)linkedCodeElements.get(i);
			String localID = codeElXref.getSelectedLocalCodeElementId();
			CodeElementType codeElType = (CodeElementType) codeElXref.getCodeElementType();
			if (codeElType != null && !StringUtil.isNullorNill(codeElType.getId())) {

				EnumValueItem evi = null;
				if (codeElType != null && !StringUtil.isNullorNill(codeElType.getId())) {
					evi = enumDAO.getEnumValueItem(EnumDAOImpl
							//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
							.getTableValueholderName(codeElType.getReferenceTables()
									.getName()), codeElXref
							.getSelectedLocalCodeElementId());
					codeElXref.setLocalCodeElement(evi);
				}

			}

			
		}
		Collections
		.sort(
				linkedCodeElements,
				CodeElementXrefLocalCodeElementNameComparator.LOCAL_CODE_ELEMENT_NAME_COMPARATOR);
		PropertyUtils.setProperty(form, "codeElementXrefs",
				linkedCodeElements);
		
		List receiverCodeElementsNotLinked = new ArrayList();
		if (mo != null && !StringUtil.isNullorNill(mo.getId()) && cet != null
				&& !StringUtil.isNullorNill(cet.getId())) {
			receiverCodeElement.setMessageOrganization(mo);
			receiverCodeElement.setCodeElementType(cet);
			receiverCodeElementsNotLinked = receiverCodeElementDAO
					.getReceiverCodeElementsByMessageOrganizationAndCodeElementType(receiverCodeElement, false);
		}
		PropertyUtils.setProperty(form, "receiverCodeElements",
				receiverCodeElementsNotLinked);

		List localCodeElementsNotLinked = new ArrayList();
		if (cet != null && !StringUtil.isNullorNill(cet.getId())) {

			localCodeElementsNotLinked = enumDAO
					.getEnumObjForHL7(EnumDAOImpl.getTableValueholderName(cet
							//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
							.getReferenceTables().getName()), false);
		}
		Collections.sort(localCodeElementsNotLinked, EnumComparator.NAME_COMPARATOR);
		PropertyUtils.setProperty(form, "localCodeElements", localCodeElementsNotLinked);
		
		
		ActionError error = null;
		ActionMessages errors = dynaForm.validate(mapping, request);

		if (localCodeElementsNotLinked.size() == 0 && linkedCodeElements.size() == 0) { 
		   error = new ActionError("codeelementxref.validation.nolocalcodes", null, null);
		   errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		
		if (receiverCodeElementsNotLinked.size() == 0 && linkedCodeElements.size() == 0) { 
			   error = new ActionError("codeelementxref.validation.noreceivercodes", null, null);
			   errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}

		if (errors != null && errors.size() > 0) {
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			return mapping.findForward(FWD_FAIL);
		}

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		return "codeelementxref.edit.title";
	}

	protected String getPageSubtitleKey() {
		return "codeelementxref.edit.title";
	}

}
