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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import us.mn.state.health.lims.codeelementtype.dao.CodeElementTypeDAO;
import us.mn.state.health.lims.codeelementtype.daoimpl.CodeElementTypeDAOImpl;
import us.mn.state.health.lims.codeelementtype.valueholder.CodeElementType;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSDuplicateRecordException;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.resources.ResourceLocator;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.valueholder.UserSessionData;
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
public class CodeElementTypeUpdateAction extends BaseAction {
	
	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new CodeElementType.
		// If there is a parameter present, we should bring up an existing
		// CodeElementType to edit.

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "false");
		request.setAttribute(NEXT_DISABLED, "false");
		
		String id = request.getParameter(ID);
		
		if (StringUtil.isNullorNill(id) || "0".equals(id)) {
			isNew = true;
		} else {
			isNew = false;
		}

		BaseActionForm dynaForm = (BaseActionForm) form;

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

		String start = (String) request.getParameter("startingRecNo");
		String direction = (String) request.getParameter("direction");

		// System.out.println("This is ID from request " + id);
		CodeElementType codeElementType = new CodeElementType();
		//get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());	
		codeElementType.setSysUserId(sysUserId);			
		org.hibernate.Transaction tx = HibernateUtil.getSession().beginTransaction();

		
		String referenceTableId = (String) dynaForm.get("referenceTableId");
		//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
		List refTables = new ArrayList();
		if (dynaForm.get("referenceTableList") != null) {
			refTables = (List) dynaForm.get("referenceTableList");
		} else {
			//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
			ReferenceTablesDAO referenceTablesDAO = new ReferenceTablesDAOImpl();
			refTables = referenceTablesDAO.getAllReferenceTablesForHl7Encoding();
		}

		//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
		ReferenceTables referenceTables = null;

		//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
		for (int i = 0; i < refTables.size(); i++) {
			ReferenceTables refTbl = (ReferenceTables) refTables.get(i);
			if (refTbl.getId().equals(referenceTableId)) {
				referenceTables = refTbl;
				break;
			}
		}
		
		// populate valueholder from form
		PropertyUtils.copyProperties(codeElementType, dynaForm);
		
		// set the referenceTable
		//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
		codeElementType.setReferenceTables(referenceTables);

		try {

			CodeElementTypeDAO codeElementTypeDAO = new CodeElementTypeDAOImpl();

			if (!isNew) {
				// UPDATE

				codeElementTypeDAO.updateData(codeElementType);

			} else {
				// INSERT

				codeElementTypeDAO.insertData(codeElementType);
			}
			tx.commit();
		} catch (LIMSRuntimeException lre) {
			//bugzilla 2154
			LogEvent.logError("CodeElementTypeUpdateAction","performAction()",lre.toString());		
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
					String messageKey = "codeelementtype.codeElementType";
					String msg = ResourceLocator.getInstance()
							.getMessageResources().getMessage(locale,
									messageKey);
					error = new ActionError("errors.DuplicateRecord",
							msg, null);

				} else {
					error = new ActionError("errors.UpdateException", null,
							null);
				}
			}

			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			//bugzilla 1485: allow change and try updating again (enable save button)
			//request.setAttribute(ALLOW_EDITS_KEY, "false");
			// disable previous and next
			request.setAttribute(PREVIOUS_DISABLED, "true");
			request.setAttribute(NEXT_DISABLED, "true");
			forward = FWD_FAIL;
			
		} finally {
            HibernateUtil.closeSession();
        }
		if (forward.equals(FWD_FAIL))
			return mapping.findForward(forward);

		// initialize the form
		dynaForm.initialize(mapping);
		// repopulate the form from valueholder
		PropertyUtils.copyProperties(dynaForm, codeElementType);
		
		// need to repopulate in case of FWD_FAIL?
		//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
		PropertyUtils.setProperty(dynaForm, "referenceTableList", refTables);


		if ("true".equalsIgnoreCase(request.getParameter("close"))) {
			forward = FWD_CLOSE;
		}

		if (codeElementType.getId() != null && !codeElementType.getId().equals("0")) {
			request.setAttribute(ID, codeElementType.getId());

		}

		//bugzilla 1400
		if (isNew) forward = FWD_SUCCESS_INSERT;
		//bugzilla 1467 added direction for redirect to NextPreviousAction
		return getForward(mapping.findForward(forward), id, start, direction);

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