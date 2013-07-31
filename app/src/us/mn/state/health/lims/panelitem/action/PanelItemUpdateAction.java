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
package us.mn.state.health.lims.panelitem.action;

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

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSDuplicateRecordException;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.resources.ResourceLocator;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.dao.UserTestSectionDAO;
import us.mn.state.health.lims.login.daoimpl.UserTestSectionDAOImpl;
import us.mn.state.health.lims.login.valueholder.UserSessionData;
import us.mn.state.health.lims.method.dao.MethodDAO;
import us.mn.state.health.lims.method.daoimpl.MethodDAOImpl;
import us.mn.state.health.lims.method.valueholder.Method;
import us.mn.state.health.lims.panel.dao.PanelDAO;
import us.mn.state.health.lims.panel.daoimpl.PanelDAOImpl;
import us.mn.state.health.lims.panel.valueholder.Panel;
import us.mn.state.health.lims.panelitem.dao.PanelItemDAO;
import us.mn.state.health.lims.panelitem.daoimpl.PanelItemDAOImpl;
import us.mn.state.health.lims.panelitem.valueholder.PanelItem;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class PanelItemUpdateAction extends BaseAction {
	
	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new PanelItem.
		// If there is a parameter present, we should bring up an existing
		// PanelItem to edit.

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
		try {
			errors = validateAll(request, errors, dynaForm);
		} catch (Exception e) {
    		//bugzilla 2154
			LogEvent.logError("PanelItemUpdateAction","performAction()",e.toString());
			ActionError error = new ActionError("errors.ValidationException",
					null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}

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
		PanelItem panelItem = new PanelItem();
		//get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());	
		panelItem.setSysUserId(sysUserId);				
		org.hibernate.Transaction tx = HibernateUtil.getSession().beginTransaction();

		// String selectedPanelItemId = (String)
		// dynaForm.get("selectedPanelItemId");
		String parentPanelName = (String) dynaForm.get("parentPanelName");

		List methods = new ArrayList();
		List tests = new ArrayList();

		if (dynaForm.get("methods") != null) {
			methods = (List) dynaForm.get("methods");
		} else {
			MethodDAO methodDAO = new MethodDAOImpl();
			methods = methodDAO.getAllMethods();
		}
		if (dynaForm.get("tests") != null) {
			tests = (List) dynaForm.get("tests");
		} else {
			//Get tests by user system id
			//bugzilla 2160
			UserTestSectionDAO userTestSectionDAO = new UserTestSectionDAOImpl();
			//bugzilla 2291
			tests = userTestSectionDAO.getAllUserTests(request, true);
		}

		// System.out.println("This is selectedPanelItemId "
		// + dynaForm.get("selectedPanelItemId"));
		List pans = new ArrayList();
		if (dynaForm.get("parentPanels") != null) {
			pans = (List) dynaForm.get("parentPanels");
		} else {
			PanelDAO panDAO = new PanelDAOImpl();
			pans = panDAO.getAllActivePanels();
		}

		Panel parentPanel = null;
		//System.out.println("Try to find parentPanelName " + parentPanelName
		//		+ " in this list ");
		// get the right parentPanel to update panelItem with
		for (int i = 0; i < pans.size(); i++) {
			Panel o = (Panel) pans.get(i);
			// if (o.getId().equals(selectedPanelItemId)) {
 			//System.out.println("This " + o.getPanelName());
			if (o.getPanelName().equals(parentPanelName)) {
				parentPanel = o;
				break;
			}
		}

		// populate valueholder from form
		PropertyUtils.copyProperties(panelItem, dynaForm);
		//System.out.println("Setting parent panel teo " + parentPanel);
		if (parentPanel != null) {
			//System.out.println("This is id " + parentPanel.getId());
		}
		panelItem.setPanel(parentPanel);

		try {

			PanelItemDAO panelItemDAO = new PanelItemDAOImpl();

			if (!isNew) {
				// UPDATE

				panelItemDAO.updateData(panelItem);

			} else {
				// INSERT

				panelItemDAO.insertData(panelItem);
			}
			tx.commit();
		} catch (LIMSRuntimeException lre) {
            //bugzilla 2154
			LogEvent.logError("PanelItemUpdateAction","performAction()",lre.toString()); 
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
					String messageKey = "panelitem.panelitem";
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
			//request.setAttribute(IActionConstants.ALLOW_EDITS_KEY, "false");
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
		PropertyUtils.copyProperties(dynaForm, panelItem);

		// need to repopulate in case of FWD_FAIL?
		PropertyUtils.setProperty(form, "parentPanels", pans);

		if ("true".equalsIgnoreCase(request.getParameter("close"))) {
			forward = FWD_CLOSE;
		}

		if (panelItem.getId() != null && !panelItem.getId().equals("0")) {
			request.setAttribute(ID, panelItem.getId());

		}

		//bugzilla 1400
		if (isNew) forward = FWD_SUCCESS_INSERT;
		//bugzilla 1467 added direction for redirect to NextPreviousAction
		return getForward(mapping.findForward(forward), id, start, direction);

	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "panelitem.add.title";
		} else {
			return "panelitem.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "panelitem.add.title";
		} else {
			return "panelitem.edit.title";
		}
	}

	protected ActionMessages validateAll(HttpServletRequest request,
			ActionMessages errors, BaseActionForm dynaForm) throws Exception {

		// parent panelItem validation against database
		String parentPanelSelected = (String) dynaForm.get("parentPanelName");

		if (!StringUtil.isNullorNill(parentPanelSelected)) {
			Panel panel = new Panel();
			panel.setPanelName(parentPanelSelected);
			PanelDAO panelDAO = new PanelDAOImpl();
			panel = panelDAO.getPanelByName(panel);

			String messageKey = "panelitem.panelParent";

			if (panel == null) {
				// the panelItem is not in database - not valid parentPanelItem
				ActionError error = new ActionError("errors.invalid",
						getMessageForKey(messageKey), null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			}
		}

		// method name validation against database
		String methodNameSelected = (String) dynaForm.get("methodName");

		if (!StringUtil.isNullorNill(methodNameSelected)) {
			Method method = new Method();
			//System.out.println("This is method name selected "
			//		+ methodNameSelected);
			method.setMethodName(methodNameSelected);
			MethodDAO methodDAO = new MethodDAOImpl();
			method = methodDAO.getMethodByName(method);

			String messageKey = "panelitem.methodName";

			if (method == null) {
				// the panelItem is not in database - not valid parentPanelItem
				ActionError error = new ActionError("errors.invalid",
						getMessageForKey(messageKey), null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			}
		}

		// test name validation against database
		String testNameSelected = (String) dynaForm.get("testName");

		if (!StringUtil.isNullorNill(testNameSelected)) {
			Test test = new Test();
			
			test.setTestName(testNameSelected);
			TestDAO testDAO = new TestDAOImpl();
			test = testDAO.getTestByName(test);

			String messageKey = "panelitem.testName";
			
			if (test == null) {
				// the panelItem is not in database - not valid parentPanelItem
				ActionError error = new ActionError("errors.invalid",
						getMessageForKey(messageKey), null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			}
		}
		
	    // Bugzilla 2207 check for duplicate sort order item
	    String panelName = (String) dynaForm.get("parentPanelName");
	    String sortOrder = (String) dynaForm.get("sortOrder");
        String id = (String) dynaForm.get("id");
    
	    if (!StringUtil.isNullorNill(panelName) && !StringUtil.isNullorNill (sortOrder)) {
		    PanelItem panelItem = new PanelItem();
		    panelItem.setPanelName(panelName);
		    panelItem.setSortOrder(sortOrder);
		
		    if (!StringUtil.isNullorNill(id)) {
		        panelItem.setId(id);
		     }
		    PanelItemDAOImpl panelItemDAO = new PanelItemDAOImpl();
		
		    String messageKey = "panelitem.sortOrder";

		    if (panelItemDAO.getDuplicateSortOrderForPanel(panelItem)) {
			// There is already one with the same panel name and sort order id in the database
		    	
			    ActionError error = new ActionError("errors.DuplicateItem",
					        getMessageForKey(messageKey), null);
			    errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		    }
	    }
	
	    return errors;
	}
}