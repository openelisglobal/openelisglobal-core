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
package us.mn.state.health.lims.referencetables.action;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.referencetables.dao.ReferenceTablesDAO;
import us.mn.state.health.lims.referencetables.daoimpl.ReferenceTablesDAOImpl;
import us.mn.state.health.lims.referencetables.valueholder.ReferenceTables;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;



/**
 * @author Yi Chen
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class ReferenceTablesAction extends BaseAction {

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

		ReferenceTables referenceTables = new ReferenceTables();

		if ((id != null) && (!"0".equals(id))) {

			referenceTables.setId(id);
			ReferenceTablesDAO referenceTablesDAO = new ReferenceTablesDAOImpl();
			referenceTablesDAO.getData(referenceTables);

			isNew = false; // this is to set correct page title
			
			// do we need to enable next or previous?
			List referenceTableses = referenceTablesDAO.getNextReferenceTablesRecord(referenceTables.getId());
			if (referenceTableses.size() > 0) {
				// enable next button
				request.setAttribute(NEXT_DISABLED, "false");
			}
			referenceTableses = referenceTablesDAO.getPreviousReferenceTablesRecord(referenceTables.getId());
			if (referenceTableses.size() > 0) {
				// enable next button
				request.setAttribute(PREVIOUS_DISABLED, "false");
			}
			// end of logic to enable next or previous button


		} else {
			isNew = true; // this is to set correct page title
		}

		if (referenceTables.getId() != null && !referenceTables.getId().equals("0")) {
			request.setAttribute(ID, referenceTables.getId());
		}

		// populate form from valueholder
		PropertyUtils.copyProperties(form, referenceTables);

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "referencetables.add.title";
		} else {
			return "referencetables.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "referencetables.add.title";
		} else {
			return "referencetables.edit.title";
		}
	}

}


