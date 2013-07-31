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
package us.mn.state.health.lims.testresult.action;

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
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.valueholder.UserSessionData;
import us.mn.state.health.lims.scriptlet.dao.ScriptletDAO;
import us.mn.state.health.lims.scriptlet.daoimpl.ScriptletDAOImpl;
import us.mn.state.health.lims.scriptlet.valueholder.Scriptlet;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class TestResultUpdateAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new TestResult.
		// If there is a parameter present, we should bring up an existing
		// TestResult to edit.

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
			LogEvent.logError("TestResultUpdateAction","performAction()",e.toString());
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
		TestResult testResult = new TestResult();
		//get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());	
		testResult.setSysUserId(sysUserId);			
		org.hibernate.Transaction tx = HibernateUtil.getSession().beginTransaction();
		
		Test test = new Test();
		String testName = (String) dynaForm.get("testName");
		test.setTestName(testName);

		TestDAO testDAO = new TestDAOImpl();
		test = testDAO.getTestByName(test);

		Scriptlet scriptlet = new Scriptlet();
		String scriptletName = (String) dynaForm.get("scriptletName");
		scriptlet.setScriptletName(scriptletName);

		ScriptletDAO scriptletDAO = new ScriptletDAOImpl();
		Scriptlet s = scriptletDAO.getScriptletByName(scriptlet);

		// populate valueholder from form
		PropertyUtils.copyProperties(testResult, dynaForm);

		testResult.setTest(test);
		testResult.setScriptlet(s);

		try {

			TestResultDAO testResultDAO = new TestResultDAOImpl();

			if (!isNew) {
				// UPDATE

				testResultDAO.updateData(testResult);

				if (FWD_NEXT.equals(direction)) {
					List testResults = testResultDAO
							.getNextTestResultRecord(testResult.getId());
					if (testResults != null && testResults.size() > 0) {
						testResult = (TestResult) testResults.get(0);
						testResultDAO.getData(testResult);
						if (testResults.size() < 2) {
							// disable next button
							request.setAttribute(NEXT_DISABLED, "true");
						}
						id = testResult.getId();
					} else {
						// just disable next button
						request.setAttribute(NEXT_DISABLED, "true");
					}
					forward = FWD_NEXT;
				}

				if (FWD_PREVIOUS.equals(direction)) {
					List testResults = testResultDAO
							.getPreviousTestResultRecord(testResult.getId());
					if (testResults != null && testResults.size() > 0) {
						testResult = (TestResult) testResults.get(0);
						testResultDAO.getData(testResult);
						if (testResults.size() < 2) {
							// disable previous button
							request.setAttribute(PREVIOUS_DISABLED, "true");
						}
						id = testResult.getId();
					} else {
						// just disable next button
						request.setAttribute(PREVIOUS_DISABLED, "true");
					}
					forward = FWD_PREVIOUS;
				}
			} else {
				// INSERT

				testResultDAO.insertData(testResult);
			}
			tx.commit();
		} catch (LIMSRuntimeException lre) {
    		//bugzilla 2154
			LogEvent.logError("TestResultUpdateAction","performAction()",lre.toString());
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
		PropertyUtils.copyProperties(dynaForm, testResult);

		if ("true".equalsIgnoreCase(request.getParameter("close"))) {
			forward = FWD_CLOSE;
		}

		if (testResult.getId() != null && !testResult.getId().equals("0")) {
			request.setAttribute(ID, testResult.getId());

		}

		//bugzilla 1400
		if (isNew) forward = FWD_SUCCESS_INSERT;
		return getForward(mapping.findForward(forward), id, start);

	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "testresult.add.title";
		} else {
			return "testresult.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "testresult.add.title";
		} else {
			return "testresult.edit.title";
		}
	}

	protected ActionMessages validateAll(HttpServletRequest request,
			ActionMessages errors, BaseActionForm dynaForm) throws Exception {

		// test validation against database
		String testNameSelected = (String) dynaForm.get("testName");

		if (!StringUtil.isNullorNill(testNameSelected)) {
			Test test = new Test();
			test.setTestName(testNameSelected);
			TestDAO testDAO = new TestDAOImpl();
			test = testDAO.getTestByName(test);

			String messageKey = "testresult.testName";

			if (test == null) {
				// the test is not in database - not valid
				// testName
				ActionError error = new ActionError("errors.invalid",
						getMessageForKey(messageKey), null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			}
		}

		// scriptlet validation against database
		String scriptletSelected = (String) dynaForm.get("scriptletName");

		if (!StringUtil.isNullorNill(scriptletSelected)) {
			Scriptlet scriptlet = new Scriptlet();
			scriptlet.setScriptletName(scriptletSelected);
			ScriptletDAO scriptletDAO = new ScriptletDAOImpl();
			scriptlet = scriptletDAO.getScriptletByName(scriptlet);

			String messageKey = "testresult.scriptletName";

			if (scriptlet == null) {
				// the scriptlet is not in database - not valid
				// parentScriptlet
				ActionError error = new ActionError("errors.invalid",
						getMessageForKey(messageKey), null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			}
		}

		// validate for testResult D -> value must be dictionary ID
		String testResultType = (String) dynaForm.get("testResultType");

		if (testResultType.equals("D")) {
			String val = (String) dynaForm.get("value");
			String messageKey = "testresult.value";
			try {
				Integer.parseInt(val);

				Dictionary dictionary = new Dictionary();
				dictionary.setId(val);
				DictionaryDAO dictDAO = new DictionaryDAOImpl();
				List dictionarys = dictDAO.getAllDictionarys();

				boolean found = false;
				for (int i = 0; i < dictionarys.size(); i++) {
					Dictionary d = (Dictionary) dictionarys.get(i);
					if (dictionary.getId().equals(d.getId())) {
						found = true;
					}
				}

				if (!found) {
					ActionError error = new ActionError("errors.invalid",
							getMessageForKey(messageKey), null);
					errors.add(ActionMessages.GLOBAL_MESSAGE, error);
				}
			} catch (NumberFormatException nfe) {
    			//bugzilla 2154
			    LogEvent.logError("TestResultUpdateAction","validateAll()",nfe.toString());
				ActionError error = new ActionError("errors.invalid",
						getMessageForKey(messageKey), null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			}
		}

		return errors;
	}

}