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
package us.mn.state.health.lims.testanalyte.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import us.mn.state.health.lims.analyte.dao.AnalyteDAO;
import us.mn.state.health.lims.analyte.daoimpl.AnalyteDAOImpl;
import us.mn.state.health.lims.analyte.valueholder.Analyte;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.valueholder.UserSessionData;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.testanalyte.dao.TestAnalyteDAO;
import us.mn.state.health.lims.testanalyte.daoimpl.TestAnalyteDAOImpl;
import us.mn.state.health.lims.testanalyte.valueholder.TestAnalyte;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class TestAnalyteUpdateAction extends BaseAction {

	private boolean isNew = false;

	/* (non-Javadoc)
	 * @see us.mn.state.health.lims.common.action.BaseAction#performAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new TestAnalyte.
		// If there is a parameter present, we should bring up an existing
		// TestAnalyte to edit.

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
			LogEvent.logError("TestAnalyteUpdateAction","performAction()",e.toString());
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
		TestAnalyte testAnalyte = new TestAnalyte();
		//get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());	
		testAnalyte.setSysUserId(sysUserId);				
		org.hibernate.Transaction tx = HibernateUtil.getSession().beginTransaction();

		// set test object
		String testName = (String) dynaForm.get("testName");
		TestDAO testDAO = new TestDAOImpl();
		Test test = testDAO.getTestByName(testName);

		// set analyte object
		Analyte analyte = new Analyte();
		String analyteName = (String) dynaForm.get("analyteName");
		analyte.setAnalyteName(analyteName);

		AnalyteDAO analyteDAO = new AnalyteDAOImpl();
		//bugzilla 1367
		analyte = analyteDAO.getAnalyteByName(analyte, false);

		// populate valueholder from form
		PropertyUtils.copyProperties(testAnalyte, dynaForm);

		testAnalyte.setTest(test);
		testAnalyte.setAnalyte(analyte);

		try {

			TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();
			if (!isNew) {
				// UPDATE
    			testAnalyteDAO.updateData(testAnalyte);
				if (FWD_NEXT.equals(direction)) {
					List testAnalytes = testAnalyteDAO
							.getNextTestAnalyteRecord(testAnalyte.getId());
					if (testAnalytes != null && testAnalytes.size() > 0) {
						testAnalyte = (TestAnalyte) testAnalytes.get(0);
						testAnalyteDAO.getData(testAnalyte);
						if (testAnalytes.size() < 2) {
							// disable next button
							request.setAttribute(NEXT_DISABLED, "true");
						}
						id = testAnalyte.getId();
					} else {
						// just disable next button
						request.setAttribute(NEXT_DISABLED, "true");
					}
					forward = FWD_NEXT;
				}

				if (FWD_PREVIOUS.equals(direction)) {
					List testAnalytes = testAnalyteDAO
							.getPreviousTestAnalyteRecord(testAnalyte.getId());
					if (testAnalytes != null && testAnalytes.size() > 0) {
						testAnalyte = (TestAnalyte) testAnalytes.get(0);
						testAnalyteDAO.getData(testAnalyte);
						if (testAnalytes.size() < 2) {
							// disable previous button
							request.setAttribute(PREVIOUS_DISABLED, "true");
						}
						id = testAnalyte.getId();
					} else {
						// just disable next button
						request.setAttribute(PREVIOUS_DISABLED, "true");
					}
					forward = FWD_PREVIOUS;
				}
			} else {
				// INSERT

				testAnalyteDAO.insertData(testAnalyte);
			}
			tx.commit();
		} catch (LIMSRuntimeException lre) {
    		//bugzilla 2154
			LogEvent.logError("TestAnalyteUpdateAction","performAction()",lre.toString());
			tx.rollback();
			errors = new ActionMessages();
			ActionError error = null;
			if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
				// how can I get popup instead of struts error at the top of
				// page?
				// ActionMessages errors = dynaForm.validate(mapping, request);
				error = new ActionError("errors.OptimisticLockException", null,
						null);

			} else {
				error = new ActionError("errors.UpdateException", null, null);
			}

			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute(ALLOW_EDITS_KEY, "false");
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
		PropertyUtils.copyProperties(dynaForm, testAnalyte);

		if ("true".equalsIgnoreCase(request.getParameter("close"))) {
			forward = FWD_CLOSE;
		}

		if (testAnalyte.getId() != null && !testAnalyte.getId().equals("0")) {
			request.setAttribute(ID, testAnalyte.getId());

		}

		//bugzilla 1400
		if (isNew) forward = FWD_SUCCESS_INSERT;
		return getForward(mapping.findForward(forward), id, start);

	}

	/* (non-Javadoc)
	 * @see us.mn.state.health.lims.common.action.BaseAction#getPageTitleKey()
	 */
	protected String getPageTitleKey() {
		if (isNew) {
			return "testanalyte.add.title";
		} else {
			return "testanalyte.edit.title";
		}
	}

	/* (non-Javadoc)
	 * @see us.mn.state.health.lims.common.action.BaseAction#getPageSubtitleKey()
	 */
	protected String getPageSubtitleKey() {
		if (isNew) {
			return "testanalyte.add.title";
		} else {
			return "testanalyte.edit.title";
		}
	}

	/**
	 * @param request
	 * @param errors
	 * @param dynaForm
	 * @return
	 * @throws Exception
	 */
	protected ActionMessages validateAll(HttpServletRequest request,
			ActionMessages errors, BaseActionForm dynaForm) throws Exception {

		// test validation against database
		String testNameSelected = (String) dynaForm.get("testName");

		if (!StringUtil.isNullorNill(testNameSelected)) {
			Test test = new Test();
			test.setTestName(testNameSelected);
			TestDAO testDAO = new TestDAOImpl();
			test = testDAO.getTestByName(test);

			String messageKey = "testanalyte.testName";

			if (test == null) {
				// the test is not in database - not valid
				// testName
				ActionError error = new ActionError("errors.invalid",
						getMessageForKey(messageKey), null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			}
		}

		// analyte validation against database
		String analyteNameSelected = (String) dynaForm.get("analyteName");

		if (!StringUtil.isNullorNill(testNameSelected)) {
			Analyte analyte = new Analyte();
			analyte.setAnalyteName(analyteNameSelected);
			AnalyteDAO analyteDAO = new AnalyteDAOImpl();
			analyte = analyteDAO.getAnalyteByName(analyte, false);

			String messageKey = "testanalyte.analyteName";

			if (analyte == null) {
				// the test is not in database - not valid
				// testName
				ActionError error = new ActionError("errors.invalid",
						getMessageForKey(messageKey), null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			}
		}

		return errors;
	}

}