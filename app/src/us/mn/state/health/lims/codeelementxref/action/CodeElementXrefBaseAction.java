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
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionRedirect;

import us.mn.state.health.lims.codeelementtype.valueholder.CodeElementType;
import us.mn.state.health.lims.codeelementxref.dao.CodeElementXrefDAO;
import us.mn.state.health.lims.codeelementxref.daoimpl.CodeElementXrefDAOImpl;
import us.mn.state.health.lims.codeelementxref.valueholder.CodeElementXref;
import us.mn.state.health.lims.codeelementxref.valueholder.CodeElementXrefLocalCodeElementNameComparator;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.dao.EnumDAO;
import us.mn.state.health.lims.common.daoimpl.EnumDAOImpl;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.valueholder.EnumValueItem;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
/**
 * @author Benzd1
 * 
 */
public class CodeElementXrefBaseAction extends BaseAction {

	private static final String SORTED_CODE_ELEMENT_XREF_LIST_KEY = "sortedCodeElementXrefs";

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.mn.state.health.lims.common.action.BaseAction#performAction(org.apache.struts.action.ActionMapping,
	 *      org.apache.struts.action.ActionForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		return mapping.findForward("");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.mn.state.health.lims.common.action.BaseAction#getPageTitleKey()
	 */
	protected String getPageTitleKey() {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.mn.state.health.lims.common.action.BaseAction#getPageTitleKeyParameter(javax.servlet.http.HttpServletRequest,
	 *      org.apache.struts.action.ActionForm)
	 */
	protected String getPageTitleKeyParameter(HttpServletRequest request,
			ActionForm form) {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.mn.state.health.lims.common.action.BaseAction#getPageSubtitleKey()
	 */
	protected String getPageSubtitleKey() {
		return "";
	}

	/**
	 * @param session
	 * @return
	 */
	protected void setSessionCodeElementXrefSortedList(HttpSession session) {
		// add a sorted collection of codeElementXrefs to session for previous
		// and next functionality
		// this is done in a different way because of the nature of
		// local_code_element (refers to multiple tables)
		CodeElementXrefDAO codeElementXrefDAO = new CodeElementXrefDAOImpl();
		List allCodeElementXrefs = codeElementXrefDAO.getAllCodeElementXrefs();
		setLocalCodeElementXrefEnumName(allCodeElementXrefs);

		// need to sort by local code element name (within message org name and
		// code element type text already sorted in sql)
		List codeElementXrefsSortedByLocalCodeElNameWithinOrgAndType = new ArrayList();
		List tempList = new ArrayList();
		// create sub lists of code element types to sort within on local code
		// element name
		String savedCodeElementType = "";
		for (int i = 0; i < allCodeElementXrefs.size(); i++) {
			CodeElementXref cex = (CodeElementXref) allCodeElementXrefs.get(i);
			if (savedCodeElementType != cex.getCodeElementType().getText()) {
				savedCodeElementType = cex.getCodeElementType().getText();
				if (tempList.size() > 0) {
					Collections
							.sort(
									tempList,
									CodeElementXrefLocalCodeElementNameComparator.LOCAL_CODE_ELEMENT_NAME_COMPARATOR);
					// put sorted list into main list
					codeElementXrefsSortedByLocalCodeElNameWithinOrgAndType
							.addAll(tempList);
					tempList = new ArrayList();
				}

			}
			tempList.add(allCodeElementXrefs.get(i));
		}
		// process the rest
		if (tempList.size() > 0) {
			Collections
					.sort(
							tempList,
							CodeElementXrefLocalCodeElementNameComparator.LOCAL_CODE_ELEMENT_NAME_COMPARATOR);
			// put sorted list into main list
			codeElementXrefsSortedByLocalCodeElNameWithinOrgAndType
					.addAll(tempList);
		}

		session.setAttribute(SORTED_CODE_ELEMENT_XREF_LIST_KEY,
				codeElementXrefsSortedByLocalCodeElNameWithinOrgAndType);

	}

	/**
	 * @param session
	 * @param codeElementXref
	 * @return
	 */
	protected List getSessionNextCodeElementXref(HttpSession session,
			CodeElementXref codeElementXref) {
		// look for the list of next code element xrefs in an object stored in
		// the session

		List cexSessionList = (ArrayList) session
				.getAttribute(SORTED_CODE_ELEMENT_XREF_LIST_KEY);
		if (cexSessionList == null) {
			setSessionCodeElementXrefSortedList(session);
			cexSessionList = (ArrayList) session
					.getAttribute(SORTED_CODE_ELEMENT_XREF_LIST_KEY);
		}
		List codeElementXrefs = new ArrayList();
		
		for (int i = 0; i < cexSessionList.size(); i++) {
			CodeElementXref cexSessionObj = (CodeElementXref) cexSessionList
					.get(i);
			if (cexSessionObj.getId().equals(codeElementXref.getId())) {
				// FOUND IT
				if (i < cexSessionList.size())
				codeElementXrefs.addAll(cexSessionList.subList(i+1, cexSessionList.size()));
				break;
			}

		}
		return codeElementXrefs;
	}

	/**
	 * @param session
	 * @param codeElementXref
	 * @return
	 */
	protected List getSessionPreviousCodeElementXref(
			HttpSession session, CodeElementXref codeElementXref) {
		// look for the list of next code element xrefs in an object stored in
		// the session

		List cexSessionList = (ArrayList) session
				.getAttribute(SORTED_CODE_ELEMENT_XREF_LIST_KEY);
		if (cexSessionList == null) {
			setSessionCodeElementXrefSortedList(session);
			cexSessionList = (ArrayList) session
					.getAttribute(SORTED_CODE_ELEMENT_XREF_LIST_KEY);
		}
		List codeElementXrefs = new ArrayList();
		
		for (int i = 0; i < cexSessionList.size(); i++) {
			CodeElementXref cexSessionObj = (CodeElementXref) cexSessionList
					.get(i);
			if (cexSessionObj.getId().equals(codeElementXref.getId())) {
				// FOUND IT
				if (i > 0)
				codeElementXrefs.addAll(cexSessionList.subList(i-1, cexSessionList.size()));
				
				break;
			}

		}
		return codeElementXrefs;
	}
	
	protected void setLocalCodeElementXrefEnumName(List codeElementXrefs) {
		EnumDAO enumDAO = new EnumDAOImpl();
		for (int i = 0; i < codeElementXrefs.size(); i++) {
			CodeElementXref cex = (CodeElementXref) codeElementXrefs.get(i);
			CodeElementType cet = (CodeElementType) cex.getCodeElementType();
			if (cet != null && !StringUtil.isNullorNill(cet.getId())) {

				EnumValueItem evi = null;
				if (cet != null && !StringUtil.isNullorNill(cet.getId())) {
					evi = enumDAO.getEnumValueItem(EnumDAOImpl
							//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
							.getTableValueholderName(cet.getReferenceTables()
									.getName()), cex
							.getSelectedLocalCodeElementId());
					cex.setSelectedLocalCodeElementName(evi.getName());
				}

			}

		}
		
	}
	
	protected ActionForward getForward(ActionForward forward, String messageOrganizationId, String codeElementTypeId) {
		ActionRedirect redirect = new ActionRedirect(forward);
		//System.out.println("This is forward " + forward.getRedirect() + " "
		//		+ forward.getPath());

		//these are parameters needed by org.efs.openreports.actions.LimsReportDetailAction
		if (messageOrganizationId != null)
			redirect.addParameter("messageOrganizationId", messageOrganizationId);
		
		if (codeElementTypeId != null)
			//redirect.addParameter("group", "Developer Reports");
			redirect.addParameter("codeElementTypeId", codeElementTypeId);
		

		//System.out.println("This is redirect " + redirect.getPath());
		
		return redirect;
	}
	
}
