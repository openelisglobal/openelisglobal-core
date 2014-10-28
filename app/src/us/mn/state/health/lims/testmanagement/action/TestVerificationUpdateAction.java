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
package us.mn.state.health.lims.testmanagement.action;

import org.apache.struts.Globals;
import org.apache.struts.action.*;
import org.hibernate.Transaction;
import us.mn.state.health.lims.action.dao.ActionDAO;
import us.mn.state.health.lims.action.daoimpl.ActionDAOImpl;
import us.mn.state.health.lims.action.valueholder.Action;
import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.analysisqaevent.dao.AnalysisQaEventDAO;
import us.mn.state.health.lims.analysisqaevent.daoimpl.AnalysisQaEventDAOImpl;
import us.mn.state.health.lims.analysisqaevent.valueholder.AnalysisQaEvent;
import us.mn.state.health.lims.analysisqaeventaction.dao.AnalysisQaEventActionDAO;
import us.mn.state.health.lims.analysisqaeventaction.daoimpl.AnalysisQaEventActionDAOImpl;
import us.mn.state.health.lims.analysisqaeventaction.valueholder.AnalysisQaEventAction;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSDuplicateRecordException;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.util.resources.ResourceLocator;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.valueholder.UserSessionData;
import us.mn.state.health.lims.qaevent.dao.QaEventDAO;
import us.mn.state.health.lims.qaevent.daoimpl.QaEventDAOImpl;
import us.mn.state.health.lims.qaevent.valueholder.QaEvent;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import us.mn.state.health.lims.sampleorganization.dao.SampleOrganizationDAO;
import us.mn.state.health.lims.sampleorganization.daoimpl.SampleOrganizationDAOImpl;
import us.mn.state.health.lims.sampleorganization.valueholder.SampleOrganization;
import us.mn.state.health.lims.systemuser.dao.SystemUserDAO;
import us.mn.state.health.lims.systemuser.daoimpl.SystemUserDAOImpl;
import us.mn.state.health.lims.systemuser.valueholder.SystemUser;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author aiswarya raman
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 * bugzilla 2028: added database transaction handling
 *                added qa event logic
 */
public class TestVerificationUpdateAction extends BaseAction {
	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		BaseActionForm dynaForm = (BaseActionForm) form;
		Sample sample = new Sample();
		SampleItem sampleItem = new SampleItem();
		TypeOfSample typeOfSample = new TypeOfSample();
		SampleOrganization sampleOrganization = new SampleOrganization();

		String accessionNumber = (String) dynaForm.get("accessionNumber");
		String stringOfTestIds = (String) dynaForm.get("selectedTestIds");

		// set current date for validation of dates
		Date today = Calendar.getInstance().getTime();
		String dateAsText = DateUtil.formatDateAsText(today);

		Transaction tx = HibernateUtil.getSession().beginTransaction();
		try {

			SampleDAO sampleDAO = new SampleDAOImpl();
			sample.setAccessionNumber(accessionNumber);
			sampleDAO.getSampleByAccessionNumber(sample);

			SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
			AnalysisDAO analysisDAO = new AnalysisDAOImpl();
			AnalysisQaEventDAO analysisQaEventDAO = new AnalysisQaEventDAOImpl();
			AnalysisQaEventActionDAO analysisQaEventActionDAO = new AnalysisQaEventActionDAOImpl();
			QaEventDAO qaEventDAO = new QaEventDAOImpl();
			ActionDAO actionDAO = new ActionDAOImpl();
			SampleOrganizationDAO sampleOrganizationDAO = new SampleOrganizationDAOImpl();

			// bugzilla 1773 need to store sample not sampleId for use in
			// sorting
			sampleItem.setSample(sample);
			sampleItemDAO.getDataBySample(sampleItem);


			String[] listOfTestIds = stringOfTestIds.split(SystemConfiguration
					.getInstance().getDefaultIdSeparator(), -1);
			// bugzilla 1926 insert logging - get sysUserId from login module
			UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
			String sysUserId = String.valueOf(usd.getSystemUserId());

			//bugzilla 2481, 2496 Action Owner
			SystemUser systemUser = new SystemUser();
			systemUser.setId(sysUserId);
			SystemUserDAO systemUserDAO = new SystemUserDAOImpl();
			systemUserDAO.getData(systemUser);

			for (int i = 0; i < listOfTestIds.length; i++) {
				if (!StringUtil.isNullorNill(listOfTestIds[i])) {
					Analysis analysis = new Analysis();
					Test test = new Test();
					String testId = (String) listOfTestIds[i];
					test.setId(testId);
					TestDAO testDAO = new TestDAOImpl();
					testDAO.getData(test);
					analysis.setTest(test);
					analysis.setSampleItem(sampleItem);
					analysis.setAnalysisType("TEST");
					analysis.setStatus(SystemConfiguration.getInstance()
							.getAnalysisStatusAssigned());
					// bugzilla 1942
					analysis.setIsReportable(test.getIsReportable());
					// bugzilla 1926 insert logging - get sysUserId from login
					// module
					analysis.setSysUserId(sysUserId);
					//bugzilla 2064
					analysis.setRevision(SystemConfiguration.getInstance().getAnalysisDefaultRevision());

    				//bugzilla 2013 added duplicateCheck parameter
					//bugzilla 2532
					analysisDAO.insertData(analysis, false);
					if (i == 1) {

						if (!StringUtil.isNullorNill(sample.getStatus())
								&& sample.getStatus().equals(
										SystemConfiguration.getInstance()
												.getSampleStatusReleased())) {
							// bugzilla 1942: if sample status WAS released set
							// it
							// back to HSE2 complete
							sample.setStatus(SystemConfiguration.getInstance()
									.getSampleStatusEntry2Complete());
							// bugzilla 1942 remove released date since we are
							// returning to HSE2 completed status
							sample.setReleasedDateForDisplay(null);
							// bugzilla 1926 insert audit logging - get
							// sysUserId
							// from login module
							sample.setSysUserId(sysUserId);
							sampleDAO.updateData(sample);
						}

					}
				}
			}

			// bugzilla 2028 qa event logic needs to be executed for new and
			// existing analyses for this sample
			// ADDITIONAL REQUIREMENT ADDED 8/30: only for HSE2 Completed Status (see bugzilla 2032)
			boolean isSampleStatusReadyForQaEvent = false;
			if (!StringUtil.isNullorNill(sample.getStatus()) && (sample.getStatus().equals(SystemConfiguration.getInstance().getSampleStatusReleased()))
					                                  || sample.getStatus().equals(SystemConfiguration.getInstance().getSampleStatusEntry2Complete())) {
				isSampleStatusReadyForQaEvent = true;
			}
			if (isSampleStatusReadyForQaEvent) {
				

				// bugzilla 2028 need additional information for qa events
				typeOfSample = sampleItem.getTypeOfSample();
				sampleOrganization.setSampleId(sample.getId());
				sampleOrganizationDAO.getDataBySample(sampleOrganization);
				//bugzilla 2589
				String submitterNumber = "";
				if (sampleOrganization != null && sampleOrganization.getOrganization() != null) {
					submitterNumber = sampleOrganization.getOrganization()
					.getId();
				}

				//bugzilla 2227
				List allAnalysesForSample = analysisDAO
				.getMaxRevisionAnalysesBySample(sampleItem);
				
				// bugzilla 2028 get the possible qa events
				QaEvent qaEventForNoCollectionDate = new QaEvent();
				qaEventForNoCollectionDate.setQaEventName(SystemConfiguration
						.getInstance().getQaEventCodeForRequestNoCollectionDate());
				qaEventForNoCollectionDate = qaEventDAO
				.getQaEventByName(qaEventForNoCollectionDate);
				
				QaEvent qaEventForNoSampleType = new QaEvent();
				qaEventForNoSampleType.setQaEventName(SystemConfiguration
						.getInstance().getQaEventCodeForRequestNoSampleType());
				qaEventForNoSampleType = qaEventDAO
				.getQaEventByName(qaEventForNoSampleType);
				
				QaEvent qaEventForUnknownSubmitter = new QaEvent();
				qaEventForUnknownSubmitter.setQaEventName(SystemConfiguration
						.getInstance().getQaEventCodeForRequestUnknownSubmitter());
				qaEventForUnknownSubmitter = qaEventDAO
				.getQaEventByName(qaEventForUnknownSubmitter);
				// end bugzilla 2028
				
				// bugzilla 2028 get the possible qa event actions
				Action actionForNoCollectionDate = new Action();
				actionForNoCollectionDate.setCode(SystemConfiguration.getInstance()
						.getQaEventActionCodeForRequestNoCollectionDate());
				actionForNoCollectionDate = actionDAO
				.getActionByCode(actionForNoCollectionDate);
				
				Action actionForNoSampleType = new Action();
				actionForNoSampleType.setCode(SystemConfiguration.getInstance()
						.getQaEventActionCodeForRequestNoSampleType());
				actionForNoSampleType = actionDAO
				.getActionByCode(actionForNoSampleType);
				
				Action actionForUnknownSubmitter = new Action();
				actionForUnknownSubmitter.setCode(SystemConfiguration.getInstance()
						.getQaEventActionCodeForRequestUnknownSubmitter());
				actionForUnknownSubmitter = actionDAO
				.getActionByCode(actionForUnknownSubmitter);
				// end bugzilla 2028
				
				for (int i = 0; i < allAnalysesForSample.size(); i++) {
					Analysis analysis = (Analysis) allAnalysesForSample.get(i);
					// bugzilla 2028 QA_EVENT COLLECTIONDATE
					if (sample.getCollectionDate() == null) {
						AnalysisQaEvent analysisQaEvent = new AnalysisQaEvent();
						analysisQaEvent.setAnalysis(analysis);
						analysisQaEvent.setQaEvent(qaEventForNoCollectionDate);
						analysisQaEvent = analysisQaEventDAO
						.getAnalysisQaEventByAnalysisAndQaEvent(analysisQaEvent);
						
						if (analysisQaEvent == null) {
							analysisQaEvent = new AnalysisQaEvent();
							analysisQaEvent.setAnalysis(analysis);
							analysisQaEvent.setQaEvent(qaEventForNoCollectionDate);
							analysisQaEvent.setCompletedDate(null);
							analysisQaEvent.setSysUserId(sysUserId);
							analysisQaEventDAO.insertData(analysisQaEvent);
						} else {
							if (analysisQaEvent.getCompletedDate() != null) {
								analysisQaEvent.setCompletedDate(null);
								analysisQaEvent.setSysUserId(sysUserId);
								analysisQaEventDAO.updateData(analysisQaEvent);
							}
							
						}
					} else {
						AnalysisQaEvent analysisQaEvent = new AnalysisQaEvent();
						analysisQaEvent.setAnalysis(analysis);
						analysisQaEvent.setQaEvent(qaEventForNoCollectionDate);
						analysisQaEvent = analysisQaEventDAO
						.getAnalysisQaEventByAnalysisAndQaEvent(analysisQaEvent);
						
						// if we don't find a record in ANALYSIS_QAEVENT (or
						// completed date is not null) then this is already
						// fixed
						if (analysisQaEvent != null
								&& analysisQaEvent.getCompletedDate() == null) {
							AnalysisQaEventAction analysisQaEventAction = new AnalysisQaEventAction();
							analysisQaEventAction
							.setAnalysisQaEvent(analysisQaEvent);
							analysisQaEventAction
							.setAction(actionForNoCollectionDate);
							analysisQaEventAction = analysisQaEventActionDAO
							.getAnalysisQaEventActionByAnalysisQaEventAndAction(analysisQaEventAction);
							
							// if we found a record in ANALYSIS_QAEVENT_ACTION
							// then this has been fixed
							if (analysisQaEventAction == null) {
								// insert a record in ANALYSIS_QAEVENT_ACTION
								AnalysisQaEventAction analQaEventAction = new AnalysisQaEventAction();
								analQaEventAction
								.setAnalysisQaEvent(analysisQaEvent);
								analQaEventAction
								.setAction(actionForNoCollectionDate);
								analQaEventAction
								.setCreatedDateForDisplay(dateAsText);
								analQaEventAction.setSysUserId(sysUserId);
								//bugzilla 2496
								analQaEventAction.setSystemUser(systemUser);
								analysisQaEventActionDAO
								.insertData(analQaEventAction);
							}
							// update the found
							// ANALYSIS_QAEVENT.COMPLETED_DATE with current
							// date stamp
							analysisQaEvent
							.setCompletedDateForDisplay(dateAsText);
							analysisQaEvent.setSysUserId(sysUserId);
							analysisQaEventDAO.updateData(analysisQaEvent);
						}
						
						
					}
					
					// bugzilla 2028 QA_EVENT SAMPLETYPE
					if (typeOfSample.getDescription().equals(SAMPLE_TYPE_NOT_GIVEN)) {
						AnalysisQaEvent analysisQaEvent = new AnalysisQaEvent();
						analysisQaEvent.setAnalysis(analysis);
						analysisQaEvent.setQaEvent(qaEventForNoSampleType);
						analysisQaEvent = analysisQaEventDAO
						.getAnalysisQaEventByAnalysisAndQaEvent(analysisQaEvent);
						
						if (analysisQaEvent == null) {
							analysisQaEvent = new AnalysisQaEvent();
							analysisQaEvent.setAnalysis(analysis);
							analysisQaEvent.setQaEvent(qaEventForNoSampleType);
							analysisQaEvent.setCompletedDate(null);
							analysisQaEvent.setSysUserId(sysUserId);
							analysisQaEventDAO.insertData(analysisQaEvent);
						} else {
							if (analysisQaEvent.getCompletedDate() != null) {
								analysisQaEvent.setCompletedDate(null);
								analysisQaEvent.setSysUserId(sysUserId);
								analysisQaEventDAO.updateData(analysisQaEvent);
							}
							
						}
					} else {
						AnalysisQaEvent analysisQaEvent = new AnalysisQaEvent();
						analysisQaEvent.setAnalysis(analysis);
						analysisQaEvent.setQaEvent(qaEventForNoSampleType);
						analysisQaEvent = analysisQaEventDAO
						.getAnalysisQaEventByAnalysisAndQaEvent(analysisQaEvent);
						
						// if we don't find a record in ANALYSIS_QAEVENT (or
						// completed date is not null) then this is already
						// fixed
						if (analysisQaEvent != null
								&& analysisQaEvent.getCompletedDate() == null) {
							AnalysisQaEventAction analysisQaEventAction = new AnalysisQaEventAction();
							analysisQaEventAction
							.setAnalysisQaEvent(analysisQaEvent);
							analysisQaEventAction
							.setAction(actionForNoSampleType);
							analysisQaEventAction = analysisQaEventActionDAO
							.getAnalysisQaEventActionByAnalysisQaEventAndAction(analysisQaEventAction);
							
							// if we found a record in ANALYSIS_QAEVENT_ACTION
							// then this has been fixed
							if (analysisQaEventAction == null) {
								// insert a record in ANALYSIS_QAEVENT_ACTION
								AnalysisQaEventAction analQaEventAction = new AnalysisQaEventAction();
								analQaEventAction
								.setAnalysisQaEvent(analysisQaEvent);
								analQaEventAction
								.setAction(actionForNoSampleType);
								analQaEventAction
								.setCreatedDateForDisplay(dateAsText);
								analQaEventAction.setSysUserId(sysUserId);
								//bugzilla 2496
								analQaEventAction.setSystemUser(systemUser);
								analysisQaEventActionDAO
								.insertData(analQaEventAction);
							}
							// update the found
							// ANALYSIS_QAEVENT.COMPLETED_DATE with current
							// date stamp
							analysisQaEvent
							.setCompletedDateForDisplay(dateAsText);
							analysisQaEvent.setSysUserId(sysUserId);
							analysisQaEventDAO.updateData(analysisQaEvent);
						}
						
					}
					
					// bugzilla 2028 QA_EVENT UNKNOWN SUBMITTER
					//bugzilla 2589 unknown submitter is now null/nill
					if (submitterNumber.equals(SystemConfiguration.getInstance()
							.getUnknownSubmitterNumberForQaEvent())) {
						AnalysisQaEvent analysisQaEvent = new AnalysisQaEvent();
						analysisQaEvent.setAnalysis(analysis);
						analysisQaEvent.setQaEvent(qaEventForUnknownSubmitter);
						analysisQaEvent = analysisQaEventDAO
						.getAnalysisQaEventByAnalysisAndQaEvent(analysisQaEvent);
						
						if (analysisQaEvent == null) {
							analysisQaEvent = new AnalysisQaEvent();
							analysisQaEvent.setAnalysis(analysis);
							analysisQaEvent.setQaEvent(qaEventForUnknownSubmitter);
							analysisQaEvent.setCompletedDate(null);
							analysisQaEvent.setSysUserId(sysUserId);
							analysisQaEventDAO.insertData(analysisQaEvent);
						} else {
							if (analysisQaEvent.getCompletedDate() != null) {
								analysisQaEvent.setCompletedDate(null);
								analysisQaEvent.setSysUserId(sysUserId);
								analysisQaEventDAO.updateData(analysisQaEvent);
							}
							
						}
					} else {
						AnalysisQaEvent analysisQaEvent = new AnalysisQaEvent();
						analysisQaEvent.setAnalysis(analysis);
						analysisQaEvent.setQaEvent(qaEventForUnknownSubmitter);
						analysisQaEvent = analysisQaEventDAO
						.getAnalysisQaEventByAnalysisAndQaEvent(analysisQaEvent);
						
						// if we don't find a record in ANALYSIS_QAEVENT (or
						// completed date is not null) then this is already
						// fixed
						if (analysisQaEvent != null
								&& analysisQaEvent.getCompletedDate() == null) {
							AnalysisQaEventAction analysisQaEventAction = new AnalysisQaEventAction();
							analysisQaEventAction
							.setAnalysisQaEvent(analysisQaEvent);
							analysisQaEventAction
							.setAction(actionForUnknownSubmitter);
							analysisQaEventAction = analysisQaEventActionDAO
							.getAnalysisQaEventActionByAnalysisQaEventAndAction(analysisQaEventAction);
							
							// if we found a record in ANALYSIS_QAEVENT_ACTION
							// then this has been fixed
							if (analysisQaEventAction == null) {
								// insert a record in ANALYSIS_QAEVENT_ACTION
								AnalysisQaEventAction analQaEventAction = new AnalysisQaEventAction();
								analQaEventAction
								.setAnalysisQaEvent(analysisQaEvent);
								analQaEventAction
								.setAction(actionForUnknownSubmitter);
								analQaEventAction
								.setCreatedDateForDisplay(dateAsText);
								analQaEventAction.setSysUserId(sysUserId);
								//bugzilla 2496
								analQaEventAction.setSystemUser(systemUser);
								analysisQaEventActionDAO
								.insertData(analQaEventAction);
							}
							// update the found
							// ANALYSIS_QAEVENT.COMPLETED_DATE with current
							// date stamp
							analysisQaEvent
							.setCompletedDateForDisplay(dateAsText);
							analysisQaEvent.setSysUserId(sysUserId);
							analysisQaEventDAO.updateData(analysisQaEvent);
						}
						
					}
					
				}
			}
			tx.commit();

		} catch (LIMSRuntimeException lre) {
    		//bugzilla 2154
			LogEvent.logError("TestVerificationUpdateAction","performAction()",lre.toString());
			tx.rollback();
			ActionMessages errors = new ActionMessages();
			ActionError error = null;
			
			if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
				error = new ActionError("errors.OptimisticLockException", null,
						null);
			} else {
			    //bugzilla 2013 added duplicateCheck parameter
				if (lre.getException() instanceof LIMSDuplicateRecordException) {
					String messageKey = "analysis.analysis";
                    Locale locale = (Locale) request.getSession().getAttribute(	"org.apache.struts.action.LOCALE");
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
			request.setAttribute(ALLOW_EDITS_KEY, "false");
			return mapping.findForward(FWD_FAIL);

		} finally {
			HibernateUtil.closeSession();
		}
		return getForward(mapping.findForward(forward));

	}

	protected String getPageTitleKey() {
		return "testmanagement.title";
	}

	protected String getPageSubtitleKey() {
		return "testmanagement.title";
	}

	protected ActionForward getForward(ActionForward forward) {
		ActionRedirect redirect = new ActionRedirect(forward);
		return redirect;
	}
}