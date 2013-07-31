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
package us.mn.state.health.lims.codeelementtype.action;

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
import us.mn.state.health.lims.codeelementtype.valueholder.CodeElementType;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.referencetables.dao.ReferenceTablesDAO;
import us.mn.state.health.lims.referencetables.daoimpl.ReferenceTablesDAOImpl;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class CodeElementTypeAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new CodeElementType.
		// If there is a parameter present, we should bring up an existing
		// CodeElementType to edit.
		String id = request.getParameter(ID);

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		DynaActionForm dynaForm = (DynaActionForm) form;

		// initialize the form
		dynaForm.initialize(mapping);

		CodeElementType codeElementType = new CodeElementType();

		if ((id != null) && (!"0".equals(id))) { // this is an existing
			// codeElementType

			codeElementType.setId(id);
			CodeElementTypeDAO codeElementTypeDAO = new CodeElementTypeDAOImpl();
			codeElementTypeDAO.getData(codeElementType);

			// initialize referenceTableId
			//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
			if (codeElementType.getReferenceTables() != null) {
				codeElementType.setReferenceTableId(codeElementType.getReferenceTables().getId());
			}
			isNew = false; // this is to set correct page title

			// do we need to enable next or previous?
			//bugzilla 1427 pass in name not id
			List codeElementTypes = codeElementTypeDAO.getNextCodeElementTypeRecord(codeElementType.getText());
			if (codeElementTypes.size() > 0) {
				// enable next button
				request.setAttribute(NEXT_DISABLED, "false");
			}
			//bugzilla 1427 pass in name not id
			codeElementTypes = codeElementTypeDAO.getPreviousCodeElementTypeRecord(codeElementType.getText());
			if (codeElementTypes.size() > 0) {
				// enable next button
				request.setAttribute(PREVIOUS_DISABLED, "false");
			}
			// end of logic to enable next or previous button
		} else { // this is a new codeElementType

			isNew = true; // this is to set correct page title
		}

		if (codeElementType.getId() != null && !codeElementType.getId().equals("0")) {
			request.setAttribute(ID, codeElementType.getId());
		}

		// populate form from valueholder
		PropertyUtils.copyProperties(form, codeElementType);
		
		//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
		ReferenceTablesDAO referenceTablesDAO = new ReferenceTablesDAOImpl();
		List referenceTableList = referenceTablesDAO.getAllReferenceTablesForHl7Encoding();
		PropertyUtils.setProperty(form, "referenceTableList", referenceTableList);

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "codeelementtype.add.title";
		} else {
			return "codeelementtype.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "codeelementtype.add.title";
		} else {
			return "codeelementtype.edit.title";
		}
	}

}
