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
package us.mn.state.health.lims.analyte.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import us.mn.state.health.lims.analyte.dao.AnalyteDAO;
import us.mn.state.health.lims.analyte.daoimpl.AnalyteDAOImpl;
import us.mn.state.health.lims.analyte.valueholder.Analyte;
import us.mn.state.health.lims.common.action.BaseAction;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class AnalyteAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new Analyte.
		// If there is a parameter present, we should bring up an existing
		// Analyte to edit.
		String id = request.getParameter(ID);

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		DynaActionForm dynaForm = (DynaActionForm) form;

		// initialize the form
		dynaForm.initialize(mapping);

		Analyte analyte = new Analyte();

		if ((id != null) && (!"0".equals(id))) { // this is an existing
			// analyte

			analyte.setId(id);
			AnalyteDAO analyteDAO = new AnalyteDAOImpl();
			analyteDAO.getData(analyte);
			// initialize selectedAnalyteId
			if (analyte.getAnalyte() != null) {
				analyte.setSelectedAnalyteId(analyte.getAnalyte().getId());
			}
			isNew = false; // this is to set correct page title

			// do we need to enable next or previous?
			//bugzilla 1427 pass in name not id
			List analytes = analyteDAO.getNextAnalyteRecord(analyte.getAnalyteName());
			if (analytes.size() > 0) {
				// enable next button
				request.setAttribute(NEXT_DISABLED, "false");
			}
			//bugzilla 1427 pass in name not id
			analytes = analyteDAO.getPreviousAnalyteRecord(analyte.getAnalyteName());
			if (analytes.size() > 0) {
				// enable next button
				request.setAttribute(PREVIOUS_DISABLED, "false");
			}
			// end of logic to enable next or previous button
		} else { // this is a new analyte

			// default isActive to 'Y'
			analyte.setIsActive(YES);
			isNew = true; // this is to set correct page title
		}

		if (analyte.getId() != null && !analyte.getId().equals("0")) {
			request.setAttribute(ID, analyte.getId());
		}

		// populate form from valueholder
		PropertyUtils.copyProperties(form, analyte);

		AnalyteDAO analDAO = new AnalyteDAOImpl();

		List parentAnalytes = analDAO.getAllAnalytes();

		// set parentAnalyteName
		String parentAnalyteName = null;
		for (int i = 0; i < parentAnalytes.size(); i++) {
			Analyte parentAnalyte = (Analyte) parentAnalytes.get(i);
			if (parentAnalyte.getId().equals(analyte.getSelectedAnalyteId())) {
				parentAnalyteName = parentAnalyte.getAnalyteName();
			}
		}

		PropertyUtils.setProperty(form, "parentAnalytes", parentAnalytes);
		PropertyUtils.setProperty(form, "parentAnalyteName", parentAnalyteName);

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "analyte.add.title";
		} else {
			return "analyte.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "analyte.add.title";
		} else {
			return "analyte.edit.title";
		}
	}

}
