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
package us.mn.state.health.lims.test.action;

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
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.resources.ResourceLocator;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.label.dao.LabelDAO;
import us.mn.state.health.lims.label.daoimpl.LabelDAOImpl;
import us.mn.state.health.lims.label.valueholder.Label;
import us.mn.state.health.lims.login.valueholder.UserSessionData;
import us.mn.state.health.lims.method.dao.MethodDAO;
import us.mn.state.health.lims.method.daoimpl.MethodDAOImpl;
import us.mn.state.health.lims.method.valueholder.Method;
import us.mn.state.health.lims.scriptlet.dao.ScriptletDAO;
import us.mn.state.health.lims.scriptlet.daoimpl.ScriptletDAOImpl;
import us.mn.state.health.lims.scriptlet.valueholder.Scriptlet;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.dao.TestSectionDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.daoimpl.TestSectionDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.test.valueholder.TestSection;
import us.mn.state.health.lims.testtrailer.dao.TestTrailerDAO;
import us.mn.state.health.lims.testtrailer.daoimpl.TestTrailerDAOImpl;
import us.mn.state.health.lims.testtrailer.valueholder.TestTrailer;
import us.mn.state.health.lims.typeofsample.util.TypeOfSampleUtil;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class TestUpdateAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new Test.
		// If there is a parameter present, we should bring up an existing
		// Test to edit.
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
			LogEvent.logError("TestUpdateAction","performAction()",e.toString());
			ActionError error = new ActionError("errors.ValidationException",
					null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}

		if (errors != null && errors.size() > 0) {
			saveErrors(request, errors);
			// since we forward to jsp - not Action we don't need to repopulate
			// the lists here
			return mapping.findForward(FWD_FAIL);
		}

		String start = (String) request.getParameter("startingRecNo");
		String direction = (String) request.getParameter("direction");

		Test test = new Test();
		// get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());
		test.setSysUserId(sysUserId);
		org.hibernate.Transaction tx = HibernateUtil.getSession()
				.beginTransaction();

		Method method = new Method();
		String methodName = (String) dynaForm.get("methodName");
		method.setMethodName((methodName == null)?"":methodName);

		MethodDAO methodDAO = new MethodDAOImpl();
	    Method meth = methodDAO.getMethodByName(method);

		Label label = new Label();
		String labelName = (String) dynaForm.get("labelName");
		label.setLabelName((labelName == null)?"":labelName);

		LabelDAO labelDAO = new LabelDAOImpl();
		Label lab = labelDAO.getLabelByName(label);

		TestTrailer testTrailer = new TestTrailer();
		String testTrailerName = (String) dynaForm.get("testTrailerName");
		testTrailer.setTestTrailerName((testTrailerName == null)?"":testTrailerName);

		TestTrailerDAO testTrailerDAO = new TestTrailerDAOImpl();
		TestTrailer tt = testTrailerDAO.getTestTrailerByName(testTrailer);

		TestSection testSection = new TestSection();
		String testSectionName = (String) dynaForm.get("testSectionName");
		testSection.setTestSectionName(testSectionName);

		TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
		TestSection ts = testSectionDAO.getTestSectionByName(testSection);

		Scriptlet scriptlet = new Scriptlet();
		String scriptletName = (String) dynaForm.get("scriptletName");
		scriptlet.setScriptletName((scriptletName == null)?"":scriptletName);

		ScriptletDAO scriptletDAO = new ScriptletDAOImpl();
		Scriptlet s = scriptletDAO.getScriptletByName(scriptlet);

		// populate valueholder from form
		PropertyUtils.copyProperties(test, dynaForm);

		test.setMethod(meth);
		test.setLabel(lab);
		test.setTestTrailer(tt);
		test.setTestSection(ts);
		test.setScriptlet(s);

		try {

			TestDAO testDAO = new TestDAOImpl();

			if (!isNew) {
				// bugzilla 1401 removed system.out that caused nullpointer
				// UPDATE
				testDAO.updateData(test);
				// if we rewrite a test, we can't rely on the cache of tests for a type of sample (used in sample entry).
			} else {
				// INSERT
				testDAO.insertData(test);
			}
			TypeOfSampleUtil.clearTestCache();
			tx.commit();
		} catch (LIMSRuntimeException lre) {
    		//bugzilla 2154
			LogEvent.logError("TestUpdateAction","performAction()",lre.toString());
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
				if (lre.getException() instanceof LIMSDuplicateRecordException) {
					java.util.Locale locale = (java.util.Locale) request.getSession()
					.getAttribute("org.apache.struts.action.LOCALE");
					//bugzilla 2459 (added dup check for description)
					String messageKey = "test.testNameOrDescription";
					String msg =  ResourceLocator.getInstance().getMessageResources().getMessage(
							locale, messageKey);
					error = new ActionError("errors.DuplicateRecord.activeonly", msg,
							null);

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
		PropertyUtils.copyProperties(dynaForm, test);

		if ("true".equalsIgnoreCase(request.getParameter("close"))) {
			forward = FWD_CLOSE;
		}

		if (test.getId() != null && !test.getId().equals("0")) {
			request.setAttribute(ID, test.getId());

		}

		// bugzilla 1400
		if (isNew)
			forward = FWD_SUCCESS_INSERT;
		//bugzilla 1467 added direction for redirect to NextPreviousAction
		return getForward(mapping.findForward(forward), id, start, direction);

	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "test.add.title";
		} else {
			return "test.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "test.add.title";
		} else {
			return "test.edit.title";
		}
	}

	protected ActionMessages validateAll(HttpServletRequest request,
			ActionMessages errors, BaseActionForm dynaForm) throws Exception {

		// method name validation against database
		String methodNameSelected = (String) dynaForm.get("methodName");

		if (!StringUtil.isNullorNill(methodNameSelected)) {
			Method method = new Method();
			//System.out.println("This is method name selected "
			//		+ methodNameSelected);
			method.setMethodName(methodNameSelected);
			MethodDAO methodDAO = new MethodDAOImpl();
			method = methodDAO.getMethodByName(method);

			String messageKey = "test.methodName";

			if (method == null) {
				// the panelItem is not in database - not valid parentPanelItem
				ActionError error = new ActionError("errors.invalid",
						getMessageForKey(messageKey), null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			}
		}
        //AIS - bugzilla 1562 ( removed validation for Label, as it is now drop-down) 
		// test trailer name validation against database
		String ttNameSelected = (String) dynaForm.get("testTrailerName");

		if (!StringUtil.isNullorNill(ttNameSelected)) {
			TestTrailer testTrailer = new TestTrailer();
			//System.out.println("This is testTrailer name selected "
			//		+ ttNameSelected);
			testTrailer.setTestTrailerName(ttNameSelected);
			TestTrailerDAO testTrailerDAO = new TestTrailerDAOImpl();
			testTrailer = testTrailerDAO.getTestTrailerByName(testTrailer);

			String messageKey = "test.testTrailerName";

			if (testTrailer == null) {
				// the panelItem is not in database - not valid parentPanelItem
				ActionError error = new ActionError("errors.invalid",
						getMessageForKey(messageKey), null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			}
		}

		// testSection validation against database
		String testSectionSelected = (String) dynaForm.get("testSectionName");

		if (!StringUtil.isNullorNill(testSectionSelected)) {
			TestSection testSection = new TestSection();
			testSection.setTestSectionName(testSectionSelected);
			TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
			testSection = testSectionDAO.getTestSectionByName(testSection);

			String messageKey = "test.testSectionName";

			if (testSection == null) {
				// the testSection is not in database - not valid
				// parentTestSection
				ActionError error = new ActionError("errors.invalid",
						getMessageForKey(messageKey), null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			}
		}
        //AIS - bugzilla 1562 ( removed scriptlet validation, as it is now drop-down )
		return errors;
	}
}