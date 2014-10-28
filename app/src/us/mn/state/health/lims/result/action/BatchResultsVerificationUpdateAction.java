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
package us.mn.state.health.lims.result.action;

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
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.valueholder.UserSessionData;
import us.mn.state.health.lims.result.valueholder.Sample_TestAnalyte;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import us.mn.state.health.lims.test.valueholder.TestComparator;
import us.mn.state.health.lims.test.valueholder.TestSectionComparator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author diane benz
 * //AIS - bugzilla 1872 
 * bugzilla 1348 To change this generated comment edit the template variable
 * "typecomment": Window>Preferences>Java>Templates. To enable and disable the
 * creation of type comments go to Window>Preferences>Java>Code Generation.
 * bugzilla 1942 - status changes
 * bugzilla 1992 - cleanup (for consistency between batchresultsverification: view all and view)
 *               - also to fix optimistic lock not working on view all (we must read analysis on the view/viewall action
 *                 not on update action
 */
public class BatchResultsVerificationUpdateAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new Result.
		// If there is a parameter present, we should bring up an existing
		// Result to edit.

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		HttpSession session = request.getSession();

		BaseActionForm dynaForm = (BaseActionForm) form;

		String selectedTestSectionId = (String) dynaForm
				.get("selectedTestSectionId");
		String selectedTestId = (String) dynaForm.get("selectedTestId");


		List sampleTestAnalytes = (List) dynaForm.get("sample_TestAnalytes");

		List tests = (List) dynaForm.get("tests");
		List testSections = (List) dynaForm.get("testSections");

		String[] selectedRows = (String[]) dynaForm.get("selectedRows");
		String accessionNumber = (String) dynaForm.get("accessionNumber");

		ActionMessages errors = null;

		try {
			errors = new ActionMessages();
		} catch (Exception e) {
    		//bugzilla 2154
			LogEvent.logError("BatchResultsVerificationUpdateAction","performAction()",e.toString());
			ActionError error = new ActionError("errors.ValidationException",
					null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		if (errors != null && errors.size() > 0) {
			saveErrors(request, errors);
			// initialize the form but retain the invalid accessionNumber
			dynaForm.initialize(mapping);

			request.setAttribute(IActionConstants.ALLOW_EDITS_KEY, "false");

			return mapping.findForward(FWD_FAIL);
		}

		// initialize the form
		dynaForm.initialize(mapping);

		// bugzilla 1926
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());

		org.hibernate.Transaction tx = HibernateUtil.getSession()
				.beginTransaction();

		Sample sample = null;
		Analysis analysis = null;

		if (selectedRows != null && selectedRows.length > 0) {

			AnalysisDAO analysisDAO = new AnalysisDAOImpl();
			SampleDAO sampleDAO = new SampleDAOImpl();
			Date today = Calendar.getInstance().getTime();
			String dateAsText = DateUtil.formatDateAsText(today);

			boolean sampleReadyToBeReleased = false;


			try {
				for (int i = 0; i < selectedRows.length; i++) {
				 
					Sample_TestAnalyte sTa = (Sample_TestAnalyte) sampleTestAnalytes
							.get(Integer.parseInt(selectedRows[i]));
					sample = sTa.getSample();
					analysis = sTa.getAnalysis();
					
					//bugzilla 2053 
					if(!StringUtil.isNullorNill((String) session
							.getAttribute(IActionConstants.RESULTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_VERIFICATION_PARAM_ACCESSION_NUMBER))) {
						 forward = FWD_SUCCESS_OTHER;
					}
					
					if (!StringUtil.isNullorNill(sample.getStatus()) && sample.getStatus().equals(
							SystemConfiguration.getInstance()
									.getSampleStatusEntry2Complete())) {
						sampleReadyToBeReleased = true;
					}
					
					if (sample != null && analysis != null) {
						// bugzilla 1926
						analysis.setSysUserId(sysUserId);

						// Determine what the status should be set to
						if (!StringUtil.isNullorNill(analysis.getStatus()) && analysis.getStatus().equals(
								SystemConfiguration.getInstance()
										.getAnalysisStatusResultCompleted())) {
							analysis.setStatus(SystemConfiguration
									.getInstance().getAnalysisStatusReleased());
							analysis.setReleasedDateForDisplay(dateAsText);

							analysisDAO.updateData(analysis);
						} else {
							sampleReadyToBeReleased = false;
						}
					} else {
						sampleReadyToBeReleased = false;
					}

				 }



				if (sampleReadyToBeReleased) {
					// verify that ALL analyses linked are in released status
					SampleItem sampleItem = new SampleItem();
					sampleItem.setSample(sample);
					SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
					sampleItemDAO.getDataBySample(sampleItem);
					//bugzilla 2227
					List linkedTests = analysisDAO
							.getMaxRevisionAnalysesBySample(sampleItem);

					if (linkedTests != null && linkedTests.size() > 0) {
						for (int i = 0; i < linkedTests.size(); i++) {
							Analysis linkedTest = (Analysis) linkedTests.get(i);
							if (linkedTest.getStatus() == null
									|| !linkedTest
											.getStatus()
											.equals(
													SystemConfiguration
															.getInstance()
															.getAnalysisStatusReleased())) {
								sampleReadyToBeReleased = false;
							}
						}
						if (sampleReadyToBeReleased) {
							sample.setStatus(SystemConfiguration.getInstance()
									.getSampleStatusReleased());
							sample.setReleasedDateForDisplay(dateAsText);
							sample.setSysUserId(sysUserId);
							sampleDAO.updateData(sample);
						}
					}
				}

				tx.commit();

			} catch (LIMSRuntimeException lre) {
    			//bugzilla 2154
			    LogEvent.logError("BatchResultsVerificationUpdateAction","performAction()",lre.toString());
			    tx.rollback();
				errors = new ActionMessages();
				ActionError error = null;
				if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
					error = new ActionError("errors.OptimisticLockException",
							null, null);

				} else {
					error = new ActionError("errors.UpdateException", null,
							null);

				}
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
				saveErrors(request, errors);
				request.setAttribute(Globals.ERROR_KEY, errors);
				request.setAttribute(ALLOW_EDITS_KEY, "false");
				// bugzilla 1992, 2053
			    if(!StringUtil.isNullorNill((String) session
						.getAttribute(IActionConstants.RESULTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_VERIFICATION_PARAM_ACCESSION_NUMBER))) {
										 forward = FWD_FAIL_OTHER;
				} else {
					forward = FWD_FAIL;
				}

			} finally {
				HibernateUtil.closeSession();
			}

		} else {
			return mapping.findForward(FWD_FAIL);
		}

		// #1347 sort dropdown values
		Collections.sort(testSections, TestSectionComparator.NAME_COMPARATOR);
		Collections.sort(tests, TestComparator.NAME_COMPARATOR);

		PropertyUtils.setProperty(dynaForm, "selectedTestId", selectedTestId);
		PropertyUtils.setProperty(dynaForm, "selectedTestSectionId",
				selectedTestSectionId);
		PropertyUtils.setProperty(dynaForm, "tests", 
				(tests == null? new ArrayList(): tests));
		PropertyUtils.setProperty(dynaForm, "testSections", 
				(testSections==null? new ArrayList(): testSections));
		PropertyUtils.setProperty(dynaForm, "sample_TestAnalytes",
				(sampleTestAnalytes==null? new ArrayList(): sampleTestAnalytes));
		PropertyUtils.setProperty(dynaForm, "selectedRows", selectedRows);

		PropertyUtils.setProperty(dynaForm, "accessionNumber", accessionNumber);

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "resultsentry.add.title";
		} else {
			return "resultsentry.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "resultsentry.add.subtitle";
		} else {
			return "resultsentry.edit.subtitle";
		}
	}

}
