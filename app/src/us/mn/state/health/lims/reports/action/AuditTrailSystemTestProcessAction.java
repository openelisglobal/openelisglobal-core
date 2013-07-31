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
package us.mn.state.health.lims.reports.action;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import us.mn.state.health.lims.audittrail.dao.AuditTrailDAO;
import us.mn.state.health.lims.audittrail.daoimpl.AuditTrailDAOImpl;
import us.mn.state.health.lims.audittrail.valueholder.History;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.referencetables.dao.ReferenceTablesDAO;
import us.mn.state.health.lims.referencetables.daoimpl.ReferenceTablesDAOImpl;
import us.mn.state.health.lims.referencetables.valueholder.ReferenceTables;
import us.mn.state.health.lims.reports.valueholder.audittrail.HistoryXmlHelper;
import us.mn.state.health.lims.systemuser.dao.SystemUserDAO;
import us.mn.state.health.lims.systemuser.daoimpl.SystemUserDAOImpl;
import us.mn.state.health.lims.systemuser.valueholder.SystemUser;


/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 * bugzilla 2599
 */
public class AuditTrailSystemTestProcessAction extends BaseAction {

	Properties transmissionMap = null;
	InputStream propertyStream = null;


	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String forward = FWD_SUCCESS;
		BaseActionForm dynaForm = (BaseActionForm) form;

		// server-side validation (validation.xml)
		ActionMessages errors = dynaForm.validate(mapping, request);	
		if (errors != null && errors.size() > 0) {
			saveErrors(request, errors);
			// since we forward to jsp - not Action we don't need to repopulate
			// the lists here
			return mapping.findForward(FWD_FAIL);
		}

		request.setAttribute(ALLOW_EDITS_KEY, "false");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		AuditTrailDAO auditTrailDAO = new AuditTrailDAOImpl();
		SystemUserDAO systemUserDAO = new SystemUserDAOImpl();
		//ReferenceTablesDAO referenceTablesDAO = new ReferenceTablesDAOImpl();


		String systemUserId = (String)dynaForm.getString("selectedSystemUserId");
		
		String referenceTableId = null;
		if (dynaForm.getString("selectedReferenceTableId") != null) {
		 referenceTableId    = (String)dynaForm.getString("selectedReferenceTableId");
		 //ReferenceTables referenceTables = new ReferenceTables();
		 //referenceTables.setId(referenceTableId);
		 //referenceTablesDAO.getData(referenceTables);
			
	    }

		String dateModified = (String)dynaForm.getString("dateModified");
		List systemUsers = (ArrayList)dynaForm.get("systemUsers");
		List referenceTableList = (ArrayList)dynaForm.get("referenceTableList");

		History history = new History();
		history.setSysUserId(systemUserId);
		if (!StringUtil.isNullorNill(referenceTableId)) {
			history.setReferenceTable(referenceTableId);
		}
		String locale = SystemConfiguration.getInstance().getDefaultLocale()
		.toString();
		history.setTimestamp(DateUtil.convertStringDateToTruncatedTimestamp(dateModified, locale));

		@SuppressWarnings("unchecked")
		List<History> historyRecords = auditTrailDAO.getHistoryBySystemUserAndDateAndRefTableId(history);
        ReferenceTablesDAO referenceTablesDAO = new ReferenceTablesDAOImpl();
        
        List<HistoryXmlHelper> historyRecordsForDisplay = new ArrayList<HistoryXmlHelper>();
        for (History h : historyRecords) {
        	HistoryXmlHelper historyXmlHelper = new HistoryXmlHelper();
        	historyXmlHelper.setActivity(h.getActivity());
        	ReferenceTables ref = new ReferenceTables();
        	ref.setId(h.getReferenceTable());
        	referenceTablesDAO.getData(ref);
        	historyXmlHelper.setReferenceTableName(ref.getTableName());
        	historyXmlHelper.setReferenceTableId(h.getReferenceId());
        	SystemUser systemUser = new SystemUser();
        	systemUser.setId(h.getSysUserId());
        	systemUserDAO.getData(systemUser);
        	
        	historyXmlHelper.setUserName(systemUser.getNameForDisplay());
        	historyXmlHelper.setDate(DateUtil.convertTimestampToStringDateAndTime(h.getTimestamp(), locale));
        	if (!historyXmlHelper.getActivity().equals(IActionConstants.AUDIT_TRAIL_INSERT))
        	  historyXmlHelper.setChange(auditTrailDAO.retrieveBlobData(h.getId()));
        	historyRecordsForDisplay.add(historyXmlHelper);
        }
       

		// initialize the form
		dynaForm.initialize(mapping);

		PropertyUtils.setProperty(dynaForm, "historyRecords", historyRecordsForDisplay);

		PropertyUtils.setProperty(form, "referenceTableList", referenceTableList);
		PropertyUtils.setProperty(form, "systemUsers", systemUsers);
		
        PropertyUtils.setProperty(dynaForm, "selectedSystemUserId", systemUserId);
        PropertyUtils.setProperty(dynaForm, "selectedReferenceTableId", referenceTableId);
        PropertyUtils.setProperty(dynaForm, "dateModified", dateModified);
        
		forward = FWD_SUCCESS;




		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		return "report.audit.trail.systemtest.title";
	}

	protected String getPageSubtitleKey() {
		return "report.audit.trail.systemtest.subtitle";
	}

	protected String convertToDisplayableXML(String xml) {
		if (!StringUtil.isNullorNill(xml)) {
			xml = xml.replaceAll("<", "&lt;");
			xml = xml.replaceAll(">", "&gt;");
			// the following 2 lines are for display on page (not for system
			// out)
			xml = xml.replaceAll("\n", "<br>");
			xml = xml.replaceAll(" ", "&nbsp;");
		}
		return xml;
	}



}
