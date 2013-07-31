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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.codeelementtype.dao.CodeElementTypeDAO;
import us.mn.state.health.lims.codeelementtype.daoimpl.CodeElementTypeDAOImpl;
import us.mn.state.health.lims.common.action.BaseMenuAction;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.valueholder.EnumValue;
import us.mn.state.health.lims.common.valueholder.EnumValueImpl;
import us.mn.state.health.lims.referencetables.dao.ReferenceTablesDAO;
import us.mn.state.health.lims.referencetables.daoimpl.ReferenceTablesDAOImpl;
import us.mn.state.health.lims.referencetables.valueholder.ReferenceTables;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class CodeElementTypeMenuAction extends BaseMenuAction {

	protected List createMenuList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

 		//System.out.println("I am in CodeElementTypeMenuAction createMenuList()");

		List codeElementTypes = new ArrayList();

		String stringStartingRecNo = (String) request
				.getAttribute("startingRecNo");
		int startingRecNo = Integer.parseInt(stringStartingRecNo);

		CodeElementTypeDAO codeElementTypeDAO = new CodeElementTypeDAOImpl();
		codeElementTypes = codeElementTypeDAO.getPageOfCodeElementTypes(startingRecNo);

    	//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
		ReferenceTablesDAO referenceTablesDAO = new ReferenceTablesDAOImpl();
		List referenceTableList = referenceTablesDAO.getAllReferenceTablesForHl7Encoding();
		EnumValue ev = new EnumValueImpl();
		ev.setEnumName("ReferenceTable");
		//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
		for (int i = 0; i < referenceTableList.size(); i++) {
			ReferenceTables refTables = (ReferenceTables) referenceTableList.get(i);
			ev.putValue(refTables.getId(), refTables);
		}

		HttpSession session = request.getSession();
		session.setAttribute("ReferenceTable", ev);
		
		request.setAttribute("menuDefinition", "CodeElementTypeMenuDefinition");

		// bugzilla 1411 set pagination variables 
		request.setAttribute(MENU_TOTAL_RECORDS, String.valueOf(codeElementTypeDAO
				.getTotalCodeElementTypeCount()));
		request.setAttribute(MENU_FROM_RECORD, String.valueOf(startingRecNo));
		int numOfRecs = 0;
		if (codeElementTypes != null) {
			if (codeElementTypes.size() > SystemConfiguration.getInstance()
					.getDefaultPageSize()) {
				numOfRecs = SystemConfiguration.getInstance()
						.getDefaultPageSize();
			} else {
				numOfRecs = codeElementTypes.size();
			}
			numOfRecs--;
		}
		int endingRecNo = startingRecNo + numOfRecs;
		request.setAttribute(MENU_TO_RECORD, String.valueOf(endingRecNo));
		//end bugzilla 1411
		
		return codeElementTypes;
	}

	protected String getPageTitleKey() {
		return "codeElementType.browse.title";
	}

	protected String getPageSubtitleKey() {
		return "codeElementType.browse.title";
	}

	protected int getPageSize() {
		return SystemConfiguration.getInstance().getDefaultPageSize();
	}

	protected String getDeactivateDisabled() {
		return "true";
	}
}
