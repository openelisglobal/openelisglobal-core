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
package us.mn.state.health.lims.analysis.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class AnalysisAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new Analysis.
		// If there is a parameter present, we should bring up an existing
		// Analysis to edit.

		String id = request.getParameter(ID);

		String forward = "success";
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		BaseActionForm dynaForm = (BaseActionForm) form;

		// initialize the form
		dynaForm.initialize(mapping);

		Analysis analysis = new Analysis();

		if ((id != null) && (!"0".equals(id))) { // this is an existing
			// analysis

			analysis.setId(id);
			AnalysisDAO analysisDAO = new AnalysisDAOImpl();
			analysisDAO.getData(analysis);

			// initialize testSectionName
			if (analysis.getTestSection() != null) {
				analysis.setTestSectionName(analysis.getTestSection()
						.getTestSectionName());
			}

			// initialize testName
			if (analysis.getTest() != null) {
				analysis.setTestName(analysis.getTest().getTestName());
			}

			isNew = false; // this is to set correct page title

			// do we need to enable next or previous?
			List analyses = analysisDAO.getNextAnalysisRecord(analysis.getId());
			if (analyses.size() > 0) {
				// enable next button
				request.setAttribute(NEXT_DISABLED, "false");
			}
			analyses = analysisDAO.getPreviousAnalysisRecord(analysis.getId());
			if (analyses.size() > 0) {
				// enable next button
				request.setAttribute(PREVIOUS_DISABLED, "false");
			}
			// end of logic to enable next or previous button

		} else { // this is a new analysis

			isNew = true; // this is to set correct page title

		}

		if (analysis.getId() != null && !analysis.getId().equals("0")) {
			request.setAttribute(ID, analysis.getId());
		}

		// populate form from valueholder
		PropertyUtils.copyProperties(dynaForm, analysis);

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "analysis.add.title";
		} else {
			return "analysis.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "analysis.add.title";
		} else {
			return "analysis.edit.title";
		}
	}

}
