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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.common.action.BaseMenuAction;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.log.LogEvent;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class AnalysisMenuAction extends BaseMenuAction {
	
	protected List createMenuList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

        //bugzilla 2154
        LogEvent.logDebug("AnalysisMenuAction","createMenuList()","I am in AnalysisMenuAction createMenuList()");

		List analyses = new ArrayList();

		String stringStartingRecNo = (String) request
				.getAttribute("startingRecNo");
		int startingRecNo = Integer.parseInt(stringStartingRecNo);

		AnalysisDAO analysisDAO = new AnalysisDAOImpl();
		// analyses = (Vector)analysisDAO.getAllAnalyses();

		analyses = analysisDAO.getPageOfAnalyses(startingRecNo);

		request.setAttribute("menuDefinition", "AnalysisMenuDefinition");

		return analyses;
	}

	protected String getPageTitleKey() {
		return "analysis.browse.title";
	}

	protected String getPageSubtitleKey() {
		return "analysis.browse.title";
	}

	protected int getPageSize() {
		return SystemConfiguration.getInstance().getDefaultPageSize();
	}

	protected String getDeactivateDisabled() {
		return "true";
	}

}
