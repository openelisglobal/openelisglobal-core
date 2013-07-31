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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.analyte.dao.AnalyteDAO;
import us.mn.state.health.lims.analyte.daoimpl.AnalyteDAOImpl;
import us.mn.state.health.lims.analyte.valueholder.Analyte;
import us.mn.state.health.lims.common.action.BaseMenuAction;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.valueholder.EnumValue;
import us.mn.state.health.lims.common.valueholder.EnumValueImpl;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class AnalyteMenuAction extends BaseMenuAction {

	protected List createMenuList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		//System.out.println("I am in AnalyteMenuAction createMenuList()");

		List analytes = new ArrayList();

		String stringStartingRecNo = (String) request
				.getAttribute("startingRecNo");
		int startingRecNo = Integer.parseInt(stringStartingRecNo);
		
		// bugzilla 2370
		String searchString=(String) request
       .getParameter("searchString");
		 
		String doingSearch=(String)request
       .getParameter("search");
	
		
		AnalyteDAO analyteDAO = new AnalyteDAOImpl();
		
		if (!StringUtil.isNullorNill(doingSearch) && doingSearch.equals(YES))
	       analytes = analyteDAO.getPagesOfSearchedAnalytes(startingRecNo, searchString);
	    else
		   analytes = analyteDAO.getPageOfAnalytes(startingRecNo);

		List parentAnalytes = analyteDAO.getAllAnalytes();

		EnumValue ev = new EnumValueImpl();
		ev.setEnumName("Analyte");
		for (int i = 0; i < parentAnalytes.size(); i++) {
			Analyte anal = (Analyte) parentAnalytes.get(i);
			ev.putValue(anal.getId(), anal);
		}

		HttpSession session = request.getSession();
		session.setAttribute("Analyte", ev);
		//System.out.println("I am in AnalyteMenuAction setting menuDefinition");
		request.setAttribute("menuDefinition", "AnalyteMenuDefinition");

		// bugzilla 1411 set pagination variables 
		if (!StringUtil.isNullorNill(doingSearch) && doingSearch.equals(YES))
			request.setAttribute(MENU_TOTAL_RECORDS, String.valueOf(analyteDAO
					.getTotalSearchedAnalyteCount(searchString)));
		else
		    request.setAttribute(MENU_TOTAL_RECORDS, String.valueOf(analyteDAO
				.getTotalAnalyteCount()));
		request.setAttribute(MENU_FROM_RECORD, String.valueOf(startingRecNo));
		int numOfRecs = 0;
		if (analytes != null) {
			if (analytes.size() > SystemConfiguration.getInstance()
					.getDefaultPageSize()) {
				numOfRecs = SystemConfiguration.getInstance()
						.getDefaultPageSize();
			} else {
				numOfRecs = analytes.size();
			}
			numOfRecs--;
		}
		int endingRecNo = startingRecNo + numOfRecs;
		request.setAttribute(MENU_TO_RECORD, String.valueOf(endingRecNo));
		//end bugzilla 1411
		
		//bugzilla 2370
		request.setAttribute(MENU_SEARCH_BY_TABLE_COLUMN, "analyte.analyteName");
		// bugzilla 2370 set up a seraching mode so the next and previous action will know 
		// what to do
			
		if (!StringUtil.isNullorNill(doingSearch) && doingSearch.equals(YES) ) {
		 
		   request.setAttribute(IN_MENU_SELECT_LIST_HEADER_SEARCH, "true");
		   
		   request.setAttribute(MENU_SELECT_LIST_HEADER_SEARCH_STRING, searchString );
		}
	
		return analytes;
	}

	protected String getPageTitleKey() {
		return "analyte.browse.title";
	}

	protected String getPageSubtitleKey() {
		return "analyte.browse.title";
	}

	protected int getPageSize() {
		return SystemConfiguration.getInstance().getDefaultPageSize();
	}

	protected String getDeactivateDisabled() {
		return "false";
	}
}
