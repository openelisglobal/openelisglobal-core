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
package us.mn.state.health.lims.analysis.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.valueholder.UserSessionData;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.dao.TestSectionDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.daoimpl.TestSectionDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.test.valueholder.TestSection;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class AnalysisUpdateAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new Analysis.
		// If there is a parameter present, we should bring up an existing
		// Analysis to edit.
		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "false");
		request.setAttribute(NEXT_DISABLED, "false");

		String id = request.getParameter(ID);

        isNew = StringUtil.isNullorNill(id) || "0".equals(id);
		
		BaseActionForm dynaForm = (BaseActionForm) form;

		// server-side validation (validation.xml)
		ActionMessages errors = dynaForm.validate(mapping, request);
		
		try {
			errors = validateAll(errors, dynaForm);
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("AnalysisUpdateAction","performAction()",e.toString());	
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

		String start = request.getParameter("startingRecNo");
		String direction = request.getParameter("direction");

		Analysis analysis = new Analysis();
		//get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());	
		analysis.setSysUserId(sysUserId);
		org.hibernate.Transaction tx = HibernateUtil.getSession().beginTransaction();

		// create TestSection and Test valueholder
		TestSection testSection = new TestSection();
		String testSectionName = (String) dynaForm.get("testSectionName");
		testSection.setTestSectionName(testSectionName);

		TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
		TestSection ts = testSectionDAO.getTestSectionByName(testSection);

		String testName = (String) dynaForm.get("testName");

		TestDAO testDAO = new TestDAOImpl();
		Test t = testDAO.getTestByName(testName);

		// populate valueholder from form
		PropertyUtils.copyProperties(analysis, dynaForm);

		analysis.setTestSection(ts);
		analysis.setTest(t);
		//bugzilla 1942
		analysis.setIsReportable(t.getIsReportable());

		try {

			AnalysisDAO analysisDAO = new AnalysisDAOImpl();

			if (!isNew) {
				// UPDATE
				analysisDAO.updateData(analysis);
				if (FWD_NEXT.equals(direction)) {
					List analyses = analysisDAO.getNextAnalysisRecord(analysis
							.getId());
					if (analyses != null && analyses.size() > 0) {
						analysis = (Analysis) analyses.get(0);
						analysisDAO.getData(analysis);
						if (analyses.size() < 2) {
							// disable next button
							request.setAttribute(NEXT_DISABLED, "true");
						}
						id = analysis.getId();
					} else {
						// disable next button
						request.setAttribute(NEXT_DISABLED, "true");
					}
					forward = FWD_NEXT;
				}

				if (FWD_PREVIOUS.equals(direction)) {
					List analyses = analysisDAO
							.getPreviousAnalysisRecord(analysis.getId());
					if (analyses != null && analyses.size() > 0) {
						analysis = (Analysis) analyses.get(0);
						analysisDAO.getData(analysis);
						if (analyses.size() < 2) {
							// disable previous button
							request.setAttribute(PREVIOUS_DISABLED, "true");
						}
						id = analysis.getId();
					} else {
						// disable previous button
						request.setAttribute(PREVIOUS_DISABLED, "true");
					}
					forward = FWD_PREVIOUS;
				}
			} else {
				// INSERT
				//bugzilla 2013 added duplicateCheck parameter
				analysisDAO.insertData(analysis, false);
			}
			tx.commit();
		} catch (LIMSRuntimeException lre) {
			//bugzilla 2154
			LogEvent.logError("AnalysisUpdateAction","performAction()",lre.toString());			
			tx.rollback();
			errors = new ActionMessages();
			ActionError error;
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
		PropertyUtils.copyProperties(dynaForm, analysis);

		if ("true".equalsIgnoreCase(request.getParameter("close"))) {
			forward = FWD_CLOSE;
		}

		if (analysis.getId() != null && !analysis.getId().equals("0")) {
			request.setAttribute(ID, analysis.getId());

		}

		//bugzilla 1400
		if (isNew) forward = FWD_SUCCESS_INSERT;
		return getForward(mapping.findForward(forward), id, start);

	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "analysis.add.title";
		} else {
			return "analysis.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "analysis.add.title";
		} else {
			return "analysis.edit.title";
		}
	}

	protected ActionMessages validateAll(ActionMessages errors, BaseActionForm dynaForm) throws Exception {

		// testsection name validation against database
		String testSectionNameSelected = (String) dynaForm
				.get("testSectionName");

		if (!StringUtil.isNullorNill(testSectionNameSelected)) {
			TestSection testSection = new TestSection();
			//System.out.println("This is testSection name selected " + testSectionNameSelected);
			testSection.setTestSectionName(testSectionNameSelected);
			TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
			testSection = testSectionDAO.getTestSectionByName(testSection);

			String messageKey = "analysis.testSectionName";

			if (testSection == null) {
				// the panelItem is not in database - not valid parentPanelItem
				ActionError error = new ActionError("errors.invalid",
						getMessageForKey(messageKey), null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			}
		}

		// test name validation against database
		String testNameSelected = (String) dynaForm.get("testName");

		if (!StringUtil.isNullorNill(testSectionNameSelected)) {
			TestDAO testDAO = new TestDAOImpl();
			Test test = testDAO.getTestByName(testNameSelected);

			String messageKey = "analysis.testName";

			if (test == null) {
				// the panelItem is not in database - not valid parentPanelItem
				ActionError error = new ActionError("errors.invalid",
						getMessageForKey(messageKey), null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			}
		}

		return errors;
	}
}