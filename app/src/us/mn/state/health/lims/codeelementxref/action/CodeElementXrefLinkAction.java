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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import us.mn.state.health.lims.common.exception.LIMSDuplicateRecordException;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.resources.ResourceLocator;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.valueholder.UserSessionData;
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
public class CodeElementXrefLinkAction extends CodeElementXrefBaseAction {

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");

		DynaActionForm dynaForm = (DynaActionForm) form;
		
		// server-side validation (validation.xml)
		ActionMessages errors = dynaForm.validate(mapping, request);
		if (errors != null && errors.size() > 0) {
			// System.out.println("Server side validation errors "
			// + errors.toString());
			saveErrors(request, errors);
			// since we forward to jsp - not Action we don't need to repopulate
			// the lists here
			return mapping.findForward(FWD_FAIL);
		}
		
		String selectedMessageOrganizationId = (String)dynaForm.get("selectedMessageOrganizationId");
		String selectedCodeElementTypeId = (String)dynaForm.getString("selectedCodeElementTypeId");

		String selectedReceiverCodeElementId = (String)dynaForm.get("selectedReceiverCodeElementId");
		String selectedLocalCodeElementId = (String)dynaForm.get("selectedLocalCodeElementId");


		// initialize the form
		dynaForm.initialize(mapping);

		ReceiverCodeElementDAO receiverCodeElementDAO = new ReceiverCodeElementDAOImpl();
		CodeElementTypeDAO codeElementTypeDAO = new CodeElementTypeDAOImpl();
		MessageOrganizationDAO messageOrganizationDAO = new MessageOrganizationDAOImpl();
		CodeElementXrefDAO codeElementXrefDAO = new CodeElementXrefDAOImpl();
		
		
		ReceiverCodeElement rce = new ReceiverCodeElement();
		rce.setId(selectedReceiverCodeElementId);
		receiverCodeElementDAO.getData(rce);
		
		CodeElementType cet = new CodeElementType();
		cet.setId(selectedCodeElementTypeId);
		codeElementTypeDAO.getData(cet);
				
		MessageOrganization mo = new MessageOrganization();
		mo.setId(selectedMessageOrganizationId);
		messageOrganizationDAO.getData(mo);
		
	
        CodeElementXref codeElementXref = new CodeElementXref();
        codeElementXref.setMessageOrganization(mo);
        codeElementXref.setCodeElementType(cet);
        codeElementXref.setReceiverCodeElement(rce);
        codeElementXref.setSelectedLocalCodeElementId(selectedLocalCodeElementId);
        
		//bugzilla 1819 get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());	
		codeElementXref.setSysUserId(sysUserId);
		
		org.hibernate.Transaction tx = HibernateUtil.getSession()
		.beginTransaction();
		try {
				// INSERT

			codeElementXrefDAO.insertData(codeElementXref);
			tx.commit();
		} catch (LIMSRuntimeException lre) {
			//bugzilla 2154
			LogEvent.logError("CodeElementXrefLinkAction","performAction()",lre.toString());
			tx.rollback();
			errors = new ActionMessages();
			java.util.Locale locale = (java.util.Locale) request.getSession()
					.getAttribute("org.apache.struts.action.LOCALE");
			ActionError error = null;
			if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
				// how can I get popup instead of struts error at the top of
				// page?
				// ActionMessages errors = dynaForm.validate(mapping, request);
				error = new ActionError("errors.OptimisticLockException", null,
						null);

			} else {
				// bugzilla 1482
				if (lre.getException() instanceof LIMSDuplicateRecordException) {
					String messageKey = "codeelementxref.codeElementXref";
					String msg = ResourceLocator.getInstance()
							.getMessageResources().getMessage(locale,
									messageKey);
					error = new ActionError("errors.DuplicateRecord", msg, null);

				} else {
					error = new ActionError("errors.UpdateException", null,
							null);
				}
			}

			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			// bugzilla 1485: allow change and try updating again (enable save
			// button)
			// request.setAttribute(IActionConstants.ALLOW_EDITS_KEY, "false");
			// disable previous and next
			request.setAttribute(PREVIOUS_DISABLED, "true");
			request.setAttribute(NEXT_DISABLED, "true");
			forward = FWD_FAIL;

		} finally {
			HibernateUtil.closeSession();
		}
		return getForward(mapping.findForward(forward), selectedMessageOrganizationId, selectedCodeElementTypeId);

	}

	protected String getPageTitleKey() {
		return "codeelementxref.edit.title";
	}

	protected String getPageSubtitleKey() {
		return "codeelementxref.edit.title";
	}

}
