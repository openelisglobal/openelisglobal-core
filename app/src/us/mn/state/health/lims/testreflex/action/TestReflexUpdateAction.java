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
 *
 * Contributor(s): CIRG, University of Washington, Seattle WA.
 */
package us.mn.state.health.lims.testreflex.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSDuplicateRecordException;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.resources.ResourceLocator;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.scriptlet.dao.ScriptletDAO;
import us.mn.state.health.lims.scriptlet.daoimpl.ScriptletDAOImpl;
import us.mn.state.health.lims.scriptlet.valueholder.Scriptlet;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.testanalyte.dao.TestAnalyteDAO;
import us.mn.state.health.lims.testanalyte.daoimpl.TestAnalyteDAOImpl;
import us.mn.state.health.lims.testanalyte.valueholder.TestAnalyte;
import us.mn.state.health.lims.testreflex.dao.TestReflexDAO;
import us.mn.state.health.lims.testreflex.daoimpl.TestReflexDAOImpl;
import us.mn.state.health.lims.testreflex.valueholder.TestReflex;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;

/**
 * @author diane benz
 *
 *         To change this generated comment edit the template variable
 *         "typecomment": Window>Preferences>Java>Templates. To enable and
 *         disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
public class TestReflexUpdateAction extends BaseAction {

	private boolean isNew = false;
	private boolean useSecondTest = false;

	protected ActionForward performAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new TestReflex.
		// If there is a parameter present, we should bring up an existing
		// TestReflex to edit.

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "false");
		request.setAttribute(NEXT_DISABLED, "false");
		TestDAO testDAO = new TestDAOImpl();
		ScriptletDAO scriptletDAO = new ScriptletDAOImpl();

		BaseActionForm dynaForm = (BaseActionForm) form;

		String id = request.getParameter(ID);
		isNew = StringUtil.isNullorNill(id) || "0".equals(id);

		useSecondTest = (Boolean)dynaForm.get("useSecondTest");


		// server-side validation (validation.xml)
		ActionMessages errors = dynaForm.validate(mapping, request);

		try {
			errors = validateAll(request, errors, dynaForm);
		} catch (Exception e) {
			// bugzilla 2154
			LogEvent.logError("TestReflexUpdateAction", "performAction()", e.toString());
			ActionError error = new ActionError("errors.ValidationException", null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}

		if (errors != null && errors.size() > 0) {
			saveErrors(request, errors);
			return mapping.findForward(FWD_FAIL);
		}

		String start = (String) request.getParameter("startingRecNo");
		String direction = (String) request.getParameter("direction");

		TestReflex testReflex = new TestReflex();
		TestReflex secondTestReflex = null;

		String loggedOnUserId = getSysUserId(request);

		org.hibernate.Transaction tx = HibernateUtil.getSession().beginTransaction();

		Test reflexTest = loadTest(testDAO, dynaForm.getString("addedTestId"));
		Scriptlet reflexScriptlet = loadScriptlet( scriptletDAO, dynaForm.getString("actionScriptletId"));

		loadPrimaryTestReflex(testDAO, dynaForm, testReflex, loggedOnUserId, reflexTest, reflexScriptlet);

		if( useSecondTest ){
			secondTestReflex = new TestReflex();
			loadSecondaryTestReflex(testDAO, dynaForm, secondTestReflex, loggedOnUserId, reflexTest);
		}


		try {

			TestReflexDAO testReflexDAO = new TestReflexDAOImpl();

			if (!isNew) {
				testReflexDAO.updateData(testReflex);
			} else {
				testReflexDAO.insertData(testReflex);

				if( secondTestReflex != null){

					secondTestReflex.setSiblingReflexId(testReflex.getId());
					testReflexDAO.insertData(secondTestReflex);

					testReflex.setSiblingReflexId(secondTestReflex.getId());
					testReflexDAO.updateData(testReflex);
				}
			}

			tx.commit();
		} catch (LIMSRuntimeException lre) {
			// bugzilla 2154
			LogEvent.logError("TestReflexUpdateAction", "performAction()", lre.toString());
			tx.rollback();
			errors = new ActionMessages();
			java.util.Locale locale = (java.util.Locale) request.getSession().getAttribute("org.apache.struts.action.LOCALE");
			ActionError error = null;
			if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
				error = new ActionError("errors.OptimisticLockException", null, null);
			} else {
				if (lre.getException() instanceof LIMSDuplicateRecordException) {
					String messageKey = "testreflex.testreflex";
					String msg = ResourceLocator.getInstance().getMessageResources().getMessage(locale, messageKey);
					error = new ActionError("errors.DuplicateRecord", msg, null);

				} else {
					error = new ActionError("errors.UpdateException", null, null);
				}
			}

			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);

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
		PropertyUtils.copyProperties(dynaForm, testReflex);

		if ("true".equalsIgnoreCase(request.getParameter("close"))) {
			forward = FWD_CLOSE;
		}

		if (testReflex.getId() != null && !testReflex.getId().equals("0")) {
			request.setAttribute(ID, testReflex.getId());

		}

		if (isNew) {
			forward = FWD_SUCCESS_INSERT;
		}

		return getForward(mapping.findForward(forward), id, start, direction);

	}

	private void loadSecondaryTestReflex(TestDAO testDAO, BaseActionForm dynaForm, TestReflex secondTestReflex, String loggedOnUserId,
			Test reflexTest) {
		secondTestReflex.setSysUserId(loggedOnUserId);

		TestResult secondTestResult = loadTestResult(dynaForm.getString("secondTestResultId"));
		TestAnalyte secondTestAnalyte = loadTestAnalyte(dynaForm.getString("secondTestAnalyteId"));
		Test secondTest = loadTest(testDAO, dynaForm.getString("secondTestId"));

		secondTestReflex.setTestResult(secondTestResult);
		secondTestReflex.setTestAnalyte(secondTestAnalyte);
		secondTestReflex.setTest(secondTest);
		secondTestReflex.setAddedTest(reflexTest);
	}

	private void loadPrimaryTestReflex(TestDAO testDAO, BaseActionForm dynaForm, TestReflex testReflex, String loggedOnUserId,
			Test reflexTest, Scriptlet reflexScriptlet) {
		testReflex.setSysUserId(loggedOnUserId);
		TestResult testResult = loadTestResult(dynaForm.getString("testResultId"));
		TestAnalyte testAnalyte = loadTestAnalyte(dynaForm.getString("testAnalyteId"));

		Test test = loadTest(testDAO, dynaForm.getString("testId"));

		testReflex.setTestResult(testResult);
		testReflex.setTestAnalyte(testAnalyte);
		testReflex.setTest(test);
		testReflex.setAddedTest(reflexTest);
		testReflex.setActionScriptlet(reflexScriptlet);
	}

	private Test loadTest(TestDAO testDAO, String id) {
		if( GenericValidator.isBlankOrNull(id)){
			return null;
		}

		Test test = new Test();

		test.setId(id);
		testDAO.getData(test);
		return test;
	}

	private Scriptlet loadScriptlet(ScriptletDAO scriptletDAO, String id) {
		if( GenericValidator.isBlankOrNull(id)){
			return null;
		}

		Scriptlet scriptlet = new Scriptlet();

		scriptlet.setId(id);
		scriptletDAO.getData(scriptlet);
		return scriptlet;
	}
	private TestResult loadTestResult(String testResultId) {
		TestResult testResult = new TestResult();
		TestResultDAO testResultDAO = new TestResultDAOImpl();

		testResult.setId(testResultId);
		testResultDAO.getData(testResult);

		return testResult;
	}

	private TestAnalyte loadTestAnalyte(String testAnalyteId) {
		TestAnalyteDAO teatAnalyteDAO = new TestAnalyteDAOImpl();
		TestAnalyte testAnalyte = new TestAnalyte();

		testAnalyte.setId(testAnalyteId);

		return teatAnalyteDAO.getData(testAnalyte);
	}

	protected String getPageTitleKey() {
		return isNew ? "testreflex.add.title" : "testreflex.edit.title";
	}

	protected String getPageSubtitleKey() {
		return isNew ? "testreflex.add.title" : "testreflex.edit.title";
	}

	protected ActionMessages validateAll(HttpServletRequest request, ActionMessages errors, BaseActionForm dynaForm) throws Exception {
		TestDAO testDAO = new TestDAOImpl();
		TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();
		TestResultDAO testResultDAO = new TestResultDAOImpl();

		String testResultSelected = (String) dynaForm.get("testResultId");
		validateTestResultExists(errors, testResultDAO, testResultSelected);

		String testAnalyteSelected = (String) dynaForm.get("testAnalyteId");
		validateTestAnalyteExists(errors, testAnalyteDAO, testAnalyteSelected);

		String testSelected = (String) dynaForm.get("testId");
		validateTestExists(errors, testDAO, testSelected, "testreflex.testId");

		String addedTestSelected = (String) dynaForm.get("addedTestId");
		validateTestExists(errors, testDAO, addedTestSelected, "testreflex.addedTestId");

		if( useSecondTest){
			testResultSelected = (String) dynaForm.get("secondTestResultId");
			validateTestResultExists(errors, testResultDAO, testResultSelected);

			testAnalyteSelected = (String) dynaForm.get("secondTestAnalyteId");
			validateTestAnalyteExists(errors, testAnalyteDAO, testAnalyteSelected);

			testSelected = (String) dynaForm.get("secondTestId");
			validateTestExists(errors, testDAO, testSelected, "testreflex.testId");
		}

		return errors;
	}

	private void validateTestExists(ActionMessages errors, TestDAO testDAO, String testSelected, String errorMsgKey) throws Exception {
		if (!StringUtil.isNullorNill(testSelected)) {
			Test test = new Test();
			test.setId(testSelected);
			test = testDAO.getTestById(test);

			if (test == null) {
				ActionError error = new ActionError("errors.invalid", getMessageForKey(errorMsgKey), null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			}
		}
	}

	private void validateTestAnalyteExists(ActionMessages errors, TestAnalyteDAO testAnalyteDAO, String testAnalyteSelected)
			throws Exception {
		if (!StringUtil.isNullorNill(testAnalyteSelected)) {
			TestAnalyte testAnalyte = new TestAnalyte();

			testAnalyte.setId(testAnalyteSelected);
			testAnalyte = testAnalyteDAO.getTestAnalyteById(testAnalyte);

			if (testAnalyte == null) {
				ActionError error = new ActionError("errors.invalid", getMessageForKey("testreflex.testAnalyteId"), null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			}
		}
	}

	private void validateTestResultExists(ActionMessages errors, TestResultDAO testResultDAO, String testResultSelected) throws Exception {
		if (!StringUtil.isNullorNill(testResultSelected)) {
			TestResult testResult = new TestResult();

			testResult.setId(testResultSelected);
			testResult = testResultDAO.getTestResultById(testResult);

			if (testResult == null) {
				ActionError error = new ActionError("errors.invalid", getMessageForKey("testreflex.testResultId"), null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			}
		}
	}

}