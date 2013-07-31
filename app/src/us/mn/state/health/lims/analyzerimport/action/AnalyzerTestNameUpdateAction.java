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
package us.mn.state.health.lims.analyzerimport.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.GenericValidator;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.hibernate.Transaction;

import us.mn.state.health.lims.analyzerimport.dao.AnalyzerTestMappingDAO;
import us.mn.state.health.lims.analyzerimport.daoimpl.AnalyzerTestMappingDAOImpl;
import us.mn.state.health.lims.analyzerimport.util.AnalyzerTestNameCache;
import us.mn.state.health.lims.analyzerimport.valueholder.AnalyzerTestMapping;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.hibernate.HibernateUtil;

public class AnalyzerTestNameUpdateAction extends BaseAction {

	protected ActionForward performAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "false");
		request.setAttribute(NEXT_DISABLED, "false");

		String forward = FWD_SUCCESS;

		BaseActionForm dynaForm = (BaseActionForm) form;

		forward = validateAndUpdateAnalyzerTestName(mapping, request, dynaForm);

		return mapping.findForward(forward);

	}

	public String validateAndUpdateAnalyzerTestName(ActionMapping mapping, HttpServletRequest request, BaseActionForm dynaForm) {
		String forward = FWD_SUCCESS_INSERT;
		String analyzerId = dynaForm.getString("analyzerId");
		String testId = dynaForm.getString("testId");
		String analyzerTestName = dynaForm.getString("analyzerTestName");

		ActionMessages errors = new ActionMessages();

		validateAnalyzerAndTestName(analyzerId, analyzerTestName, testId, errors);

		if (errors.size() > 0) {
			saveErrors(request, errors);
			return FWD_FAIL;
		}

		AnalyzerTestMapping analyzerTestNameMapping = new AnalyzerTestMapping();
		analyzerTestNameMapping.setAnalyzerId(analyzerId);
		analyzerTestNameMapping.setAnalyzerTestName(analyzerTestName);
		analyzerTestNameMapping.setTestId(testId);

		AnalyzerTestMappingDAO mappingDAO = new AnalyzerTestMappingDAOImpl();

		Transaction tx = HibernateUtil.getSession().beginTransaction();

		try {
			mappingDAO.insertData(analyzerTestNameMapping, currentUserId);
		} catch (LIMSRuntimeException lre) {
			tx.rollback();

			ActionError error = null;
			if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
				error = new ActionError("errors.OptimisticLockException", null, null);
			} else {
				error = new ActionError("errors.UpdateException", null, null);
			}

			persisteError(request, error);

			disableNavigationButtons(request);
			forward = FWD_FAIL;
		} finally {
			if (!tx.wasRolledBack()) {
				tx.commit();
			}
			HibernateUtil.closeSession();
		}

		AnalyzerTestNameCache.instance().reloadCache();

		return forward;
	}

	private void validateAnalyzerAndTestName(String analyzerId, String analyzerTestName, String testId, ActionMessages errors) {
		//This is not very efficient but this is a very low usage action
		if( GenericValidator.isBlankOrNull(analyzerId) ||
			GenericValidator.isBlankOrNull(analyzerTestName) ||
			GenericValidator.isBlankOrNull(testId)){
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionError("error.all.required"));
			return;
		}

		AnalyzerTestMappingDAO mappingDAO = new AnalyzerTestMappingDAOImpl();
		List<AnalyzerTestMapping> testMappingList = mappingDAO.getAllAnalyzerTestMappings();

		for( AnalyzerTestMapping testMapping : testMappingList){
			if( analyzerId.equals(testMapping.getAnalyzerId()) &&
				analyzerTestName.equals(testMapping.getAnalyzerTestName())){
				errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionError("error.analyzer.test.name.duplicate"));
			}
		}
	}

	private void persisteError(HttpServletRequest request, ActionError error) {
		ActionMessages errors;
		errors = new ActionMessages();

		errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		saveErrors(request, errors);
		request.setAttribute(Globals.ERROR_KEY, errors);
	}

	private void disableNavigationButtons(HttpServletRequest request) {
		request.setAttribute(PREVIOUS_DISABLED, TRUE);
		request.setAttribute(NEXT_DISABLED, TRUE);
	}

	protected String getPageTitleKey() {
		return "analyzerTestName.browse.title";
	}

	protected String getPageSubtitleKey() {
		return "analyzerTestName.browse.title";
	}
}