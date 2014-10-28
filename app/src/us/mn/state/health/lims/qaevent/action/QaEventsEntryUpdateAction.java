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
package us.mn.state.health.lims.qaevent.action;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
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
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
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
import us.mn.state.health.lims.sampleqaevent.dao.SampleQaEventDAO;
import us.mn.state.health.lims.sampleqaevent.daoimpl.SampleQaEventDAOImpl;
import us.mn.state.health.lims.sampleqaevent.valueholder.SampleQaEvent;
import us.mn.state.health.lims.sampleqaeventaction.dao.SampleQaEventActionDAO;
import us.mn.state.health.lims.sampleqaeventaction.daoimpl.SampleQaEventActionDAOImpl;
import us.mn.state.health.lims.sampleqaeventaction.valueholder.SampleQaEventAction;
import us.mn.state.health.lims.systemuser.dao.SystemUserDAO;
import us.mn.state.health.lims.systemuser.daoimpl.SystemUserDAOImpl;
import us.mn.state.health.lims.systemuser.valueholder.SystemUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author diane benz
 */
public class QaEventsEntryUpdateAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");
		BaseActionForm dynaForm = (BaseActionForm) form;

		String accessionNumber = (String) dynaForm.get("accessionNumber");
		String[] selectedAnalysisQaEventIdsForCompletion = (String[]) dynaForm
				.get("selectedAnalysisQaEventIdsForCompletion");
		List testQaEvents = (List) dynaForm.get("testQaEvents");
		//bugzilla 2500
		String[] selectedSampleQaEventIdsForCompletion = (String[]) dynaForm
		.get("selectedSampleQaEventIdsForCompletion");
        List sampleQaEvents = (List) dynaForm.get("sampleQaEvents");

        //bugzilla 2566 for now qa events is for both humand and newborn
		//bugzilla 2501
		String multipleSampleMode = (String) dynaForm.get("multipleSampleMode");
    	String qaEventCategoryId = (String)dynaForm.get("selectedQaEventsCategoryId");
		
		//bugzilla 2500
		String addQaEventPopupSelectedQaEventIdsForSample = (String) dynaForm.get("addQaEventPopupSelectedQaEventIdsForSample");


	
		StringTokenizer qaEventIdTokenizerForSample = new StringTokenizer(
				addQaEventPopupSelectedQaEventIdsForSample, SystemConfiguration
				.getInstance().getDefaultIdSeparator());
		List listOfAddedQaEventIdsForSample = new ArrayList();
		while (qaEventIdTokenizerForSample.hasMoreElements()) {
			String qaEventIdForSample = (String) qaEventIdTokenizerForSample
					.nextElement();
			listOfAddedQaEventIdsForSample.add(qaEventIdForSample);
		}
		
		String addActionSelectedActionIdsForSample = (String) dynaForm.get("addActionPopupSelectedActionIdsForSample");
		String addActionSelectedSampleQaEventIds = (String) dynaForm.get("addActionPopupSelectedSampleQaEventIds");


	
		StringTokenizer actionIdTokenizerForSample = new StringTokenizer(
				addActionSelectedActionIdsForSample, SystemConfiguration
				.getInstance().getDefaultIdSeparator());
		List listOfAddedActionIdsForSample = new ArrayList();
		while (actionIdTokenizerForSample.hasMoreElements()) {
			String actionIdForSample = (String) actionIdTokenizerForSample
					.nextElement();
			listOfAddedActionIdsForSample.add(actionIdForSample);
		}
		
		StringTokenizer sampleQaEventIdTokenizer = new StringTokenizer(
				addActionSelectedSampleQaEventIds, SystemConfiguration
				.getInstance().getDefaultIdSeparator());
		List listOfSampleQaEventIds = new ArrayList();
		while (sampleQaEventIdTokenizer.hasMoreElements()) {
			String sampleQaEventId = (String) sampleQaEventIdTokenizer
					.nextElement();
			listOfSampleQaEventIds.add(sampleQaEventId);
		}

		//end bugzilla 2500
		
		String addQaEventSelectedTestIds = (String) dynaForm.get("addQaEventPopupSelectedTestIds");
		String addQaEventSelectedQaEventIds = (String) dynaForm.get("addQaEventPopupSelectedQaEventIds");


	
		StringTokenizer testIdTokenizer = new StringTokenizer(
				addQaEventSelectedTestIds, SystemConfiguration
				.getInstance().getDefaultIdSeparator());
		List listOfTestIds = new ArrayList();
		while (testIdTokenizer.hasMoreElements()) {
			String testId = (String) testIdTokenizer
					.nextElement();
			listOfTestIds.add(testId);
		}
		
		StringTokenizer qaEventIdTokenizer = new StringTokenizer(
				addQaEventSelectedQaEventIds, SystemConfiguration
				.getInstance().getDefaultIdSeparator());
		List listOfAddedQaEventIds = new ArrayList();
		while (qaEventIdTokenizer.hasMoreElements()) {
			String qaEventId = (String) qaEventIdTokenizer
					.nextElement();
			listOfAddedQaEventIds.add(qaEventId);
		}


		String addActionSelectedActionIds = (String) dynaForm.get("addActionPopupSelectedActionIds");
		String addActionSelectedAnalysisQaEventIds = (String) dynaForm.get("addActionPopupSelectedAnalysisQaEventIds");


	
		StringTokenizer actionIdTokenizer = new StringTokenizer(
				addActionSelectedActionIds, SystemConfiguration
				.getInstance().getDefaultIdSeparator());
		List listOfActionIds = new ArrayList();
		while (actionIdTokenizer.hasMoreElements()) {
			String actionId = (String) actionIdTokenizer
					.nextElement();
			listOfActionIds.add(actionId);
		}
		
		StringTokenizer analysisQaEventIdTokenizer = new StringTokenizer(
				addActionSelectedAnalysisQaEventIds, SystemConfiguration
				.getInstance().getDefaultIdSeparator());
		List listOfAnalysisQaEventIds = new ArrayList();
		while (analysisQaEventIdTokenizer.hasMoreElements()) {
			String analysisQaEventId = (String) analysisQaEventIdTokenizer
					.nextElement();
			listOfAnalysisQaEventIds.add(analysisQaEventId);
		}
		
		// server side validation of accessionNumber
		// validate on server-side sample accession number

		ActionMessages errors = null;

		try {
			errors = new ActionMessages();
			errors = validateAccessionNumber(request, errors, dynaForm);
		} catch (Exception e) {
    		//bugzilla 2154
			LogEvent.logError("QaEventsEntryUpdateAction","performAction()",e.toString());
			ActionError error = new ActionError("errors.ValidationException",
					null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		if (errors != null && errors.size() > 0) {
			saveErrors(request, errors);
			return mapping.findForward(FWD_FAIL);
		}

		// initialize the form
		dynaForm.initialize(mapping);

		// 1926 get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());
		
		//bugzilla 2481 Action Owner
		SystemUser systemUser = new SystemUser();
		systemUser.setId(sysUserId);
		SystemUserDAO systemUserDAO = new SystemUserDAOImpl();
		systemUserDAO.getData(systemUser);
		
		Date today = Calendar.getInstance().getTime();
		String dateAsText = DateUtil.formatDateAsText(today);

		org.hibernate.Transaction tx = HibernateUtil.getSession()
				.beginTransaction();

		AnalysisQaEventDAO analysisQaEventDAO = new AnalysisQaEventDAOImpl();
		SampleDAO sampleDAO = new SampleDAOImpl();
		SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
		AnalysisDAO analysisDAO = new AnalysisDAOImpl();
		QaEventDAO qaEventDAO = new QaEventDAOImpl();
		ActionDAO actionDAO = new ActionDAOImpl();
		AnalysisQaEventActionDAO analysisQaEventActionDAO = new AnalysisQaEventActionDAOImpl();
		//bugzilla 2501
		SampleQaEventDAO sampleQaEventDAO = new SampleQaEventDAOImpl();
		SampleQaEventActionDAO sampleQaEventActionDAO = new SampleQaEventActionDAOImpl();
		
		if (!StringUtil.isNullorNill(accessionNumber)) {


			
			try {
				
				//bugzilla 2500 first update completed dates for sample
				if (selectedSampleQaEventIdsForCompletion != null
						&& selectedSampleQaEventIdsForCompletion.length > 0) {
					for (int i = 0; i < selectedSampleQaEventIdsForCompletion.length; i++) {
						
						String sampleQaEventId = selectedSampleQaEventIdsForCompletion[i];
						SampleQaEvent sampleQaEvent = new SampleQaEvent();
						sampleQaEvent.setId(sampleQaEventId);
						sampleQaEventDAO.getData(sampleQaEvent);
						if (sampleQaEvent.getCompletedDate() == null) {
							sampleQaEvent.setCompletedDateForDisplay(dateAsText);
							sampleQaEvent.setSysUserId(sysUserId);
							sampleQaEventDAO.updateData(sampleQaEvent);
						}
						
					}
				} 

				
				//first update completed dates
				if (selectedAnalysisQaEventIdsForCompletion != null
						&& selectedAnalysisQaEventIdsForCompletion.length > 0) {
					for (int i = 0; i < selectedAnalysisQaEventIdsForCompletion.length; i++) {
						
						String analysisQaEventId = selectedAnalysisQaEventIdsForCompletion[i];
						AnalysisQaEvent analysisQaEvent = new AnalysisQaEvent();
						analysisQaEvent.setId(analysisQaEventId);
						analysisQaEventDAO.getData(analysisQaEvent);
						if (analysisQaEvent.getCompletedDate() == null) {
							analysisQaEvent.setCompletedDateForDisplay(dateAsText);
							analysisQaEvent.setSysUserId(sysUserId);
							analysisQaEventDAO.updateData(analysisQaEvent);
						}
						
					}
				} 
				
				//bug 2500 now check if we added new Qa Events for sample
				if (listOfAddedQaEventIdsForSample != null && listOfAddedQaEventIdsForSample.size() > 0) {

					Sample sample = new Sample();
					sample.setAccessionNumber(accessionNumber);		
					sampleDAO.getSampleByAccessionNumber(sample);	

					if (!StringUtil.isNullorNill(sample.getId())) {
						//insert all qaEvents selected for this sample if doesn't exist already
						for (int j = 0; j < listOfAddedQaEventIdsForSample.size(); j++) {
							//if this doesn't exist in in SAMPLE_QAEVENT already then insert it
							QaEvent qaEvent = new QaEvent();
							qaEvent.setId((String)listOfAddedQaEventIdsForSample.get(j));
							qaEventDAO.getData(qaEvent);
							if (qaEvent != null) {
								SampleQaEvent sampleQaEvent = new SampleQaEvent();
								sampleQaEvent.setSample(sample);
								sampleQaEvent.setQaEvent(qaEvent);
								sampleQaEvent = sampleQaEventDAO.getSampleQaEventBySampleAndQaEvent(sampleQaEvent);
								if (sampleQaEvent != null) {
									//do nothing
								} else {
									//insert this new analysis qa event
									sampleQaEvent = new SampleQaEvent();
									sampleQaEvent.setSample(sample);
									sampleQaEvent.setQaEvent(qaEvent);
									sampleQaEvent.setSysUserId(sysUserId);
									sampleQaEventDAO.insertData(sampleQaEvent);
								}
							}
						}
					}

				}


				//now check if we added new Actions
				if (listOfSampleQaEventIds != null && listOfSampleQaEventIds.size() > 0 && listOfAddedActionIdsForSample != null && listOfAddedActionIdsForSample.size() > 0) {

					Sample sample = new Sample();
					sample.setAccessionNumber(accessionNumber);		
					sampleDAO.getSampleByAccessionNumber(sample);	

					if (!StringUtil.isNullorNill(sample.getId())) {

						SampleQaEvent sampleQaEvent = new SampleQaEvent();
						sampleQaEvent.setSample(sample);
						sampleQaEvents = sampleQaEventDAO.getSampleQaEventsBySample(sampleQaEvent);

						for (int j = 0; j < sampleQaEvents.size(); j++) {
							SampleQaEvent sampQaEvent = (SampleQaEvent)sampleQaEvents.get(j);
							if (listOfSampleQaEventIds.contains(sampQaEvent.getId())) {

								for (int k = 0; k < listOfAddedActionIdsForSample.size(); k++) {
									Action action = new Action();
									action.setId((String)listOfAddedActionIdsForSample.get(k));
									actionDAO.getData(action);

									//if this analysis qa event action doesn't already exist in ANALYSIS_QA_EVENT_ACTION then insert it
									if (action != null) {
										SampleQaEventAction sampQaEventAction = new SampleQaEventAction();
										sampQaEventAction.setAction(action);
										sampQaEventAction.setSampleQaEvent(sampQaEvent);
										sampQaEventAction = sampleQaEventActionDAO.getSampleQaEventActionBySampleQaEventAndAction(sampQaEventAction);
										if (sampQaEventAction != null) {
											//do nothing if already exists
										} else {
											//insert this new analysis qa event action
											sampQaEventAction = new SampleQaEventAction();
											sampQaEventAction.setSampleQaEvent(sampQaEvent);
											sampQaEventAction.setAction(action);
											sampQaEventAction.setCreatedDateForDisplay(dateAsText);
											sampQaEventAction.setSysUserId(sysUserId);
											//bugzilla 2481
											sampQaEventAction.setSystemUser(systemUser);
											sampleQaEventActionDAO.insertData(sampQaEventAction);
										}
									}
								}

							}
						}
					}
				}
				//end 2500
				
				//now check if we added new Qa Events
				if (listOfAddedQaEventIds != null && listOfAddedQaEventIds.size() > 0 && listOfTestIds != null && listOfTestIds.size() > 0) {
					
					Sample sample = new Sample();
					sample.setAccessionNumber(accessionNumber);		
					sampleDAO.getSampleByAccessionNumber(sample);	
					List analyses = new ArrayList();
						
					if (!StringUtil.isNullorNill(sample.getId())) {
						
                        SampleItem sampleItem = new SampleItem();
						sampleItem.setSample(sample);
						sampleItemDAO.getDataBySample(sampleItem);	
						if (sampleItem.getId() != null ){
						    //bugzilla 2227
							analyses = analysisDAO.getMaxRevisionAnalysesBySample(sampleItem);				
						}
					}
					
					if (analyses != null) {
						for (int i = 0; i < analyses.size(); i++) {
							Analysis analysis = (Analysis)analyses.get(i);
							if (listOfTestIds.contains(analysis.getTest().getId())) {
								//insert all qaEvents selected for this test if doesn't exist already
								for (int j = 0; j < listOfAddedQaEventIds.size(); j++) {
									//if this doesn't exist in in ANALYSIS_QAEVENT already then insert it
									QaEvent qaEvent = new QaEvent();
									qaEvent.setId((String)listOfAddedQaEventIds.get(j));
									qaEventDAO.getData(qaEvent);
									if (qaEvent != null) {
										AnalysisQaEvent analysisQaEvent = new AnalysisQaEvent();
										analysisQaEvent.setAnalysis(analysis);
										analysisQaEvent.setQaEvent(qaEvent);
										analysisQaEvent = analysisQaEventDAO.getAnalysisQaEventByAnalysisAndQaEvent(analysisQaEvent);
										if (analysisQaEvent != null) {
											//do nothing
										} else {
											//insert this new analysis qa event
											analysisQaEvent = new AnalysisQaEvent();
											analysisQaEvent.setAnalysis(analysis);
											analysisQaEvent.setQaEvent(qaEvent);
											analysisQaEvent.setSysUserId(sysUserId);
											analysisQaEventDAO.insertData(analysisQaEvent);
										}
									}
								}
								
							}
						}
					}
						
				}
				
				
				//now check if we added new Actions
				if (listOfAnalysisQaEventIds != null && listOfAnalysisQaEventIds.size() > 0 && listOfActionIds != null && listOfActionIds.size() > 0) {
					
					Sample sample = new Sample();
					sample.setAccessionNumber(accessionNumber);		
					sampleDAO.getSampleByAccessionNumber(sample);	
					List analyses = new ArrayList();
						
					if (!StringUtil.isNullorNill(sample.getId())) {
						
                        SampleItem sampleItem = new SampleItem();
						sampleItem.setSample(sample);
						sampleItemDAO.getDataBySample(sampleItem);	
						if (sampleItem.getId() != null ){
						    //bugzilla 2227
							analyses = analysisDAO.getMaxRevisionAnalysesBySample(sampleItem);				
						}
					}
					
					if (analyses != null) {
						for (int i = 0; i < analyses.size(); i++) {
							Analysis analysis = (Analysis)analyses.get(i);
							
							AnalysisQaEvent analysisQaEvent = new AnalysisQaEvent();
							analysisQaEvent.setAnalysis(analysis);
							List analysisQaEvents = analysisQaEventDAO.getAnalysisQaEventsByAnalysis(analysisQaEvent);
							
							for (int j = 0; j < analysisQaEvents.size(); j++) {
								  AnalysisQaEvent analQaEvent = (AnalysisQaEvent)analysisQaEvents.get(j);
							      if (listOfAnalysisQaEventIds.contains(analQaEvent.getId())) {
							    	  
							    	  for (int k = 0; k < listOfActionIds.size(); k++) {
							    		  Action action = new Action();
							    		  action.setId((String)listOfActionIds.get(k));
							    		  actionDAO.getData(action);
							    		  
							    		  //if this analysis qa event action doesn't already exist in ANALYSIS_QA_EVENT_ACTION then insert it
							    		  if (action != null) {
							    			  AnalysisQaEventAction analQaEventAction = new AnalysisQaEventAction();
							    			  analQaEventAction.setAction(action);
							    			  analQaEventAction.setAnalysisQaEvent(analQaEvent);
							    			  analQaEventAction = analysisQaEventActionDAO.getAnalysisQaEventActionByAnalysisQaEventAndAction(analQaEventAction);
							    			  if (analQaEventAction != null) {
							    				  //do nothing if already exists
							    			  } else {
													//insert this new analysis qa event action
													analQaEventAction = new AnalysisQaEventAction();
													analQaEventAction.setAnalysisQaEvent(analQaEvent);
													analQaEventAction.setAction(action);
													analQaEventAction.setCreatedDateForDisplay(dateAsText);
													analQaEventAction.setSysUserId(sysUserId);
													//bugzilla 2481
													analQaEventAction.setSystemUser(systemUser);
													analysisQaEventActionDAO.insertData(analQaEventAction);
							    			  }
							    		  }
							    	  }
							    	  
							      }
							}
							
						}
					}
						
				}
				tx.commit();
				
			} catch (LIMSRuntimeException lre) {
    			//bugzilla 2154
			    LogEvent.logError("QaEventsEntryUpdateAction","performAction()",lre.toString());
				tx.rollback();
				
				errors = new ActionMessages();
				ActionError error = null;
				if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
					
					error = new ActionError(
							"errors.OptimisticLockException", null, null);
				} else {
					error = new ActionError("errors.UpdateException", null,
							null);
				}
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
				saveErrors(request, errors);
				request.setAttribute(Globals.ERROR_KEY, errors);
				
				forward = FWD_FAIL;
				
			} finally {
				HibernateUtil.closeSession();
			}

		}

		PropertyUtils.setProperty(dynaForm, "accessionNumber", accessionNumber);
		PropertyUtils.setProperty(dynaForm,
				"selectedAnalysisQaEventIdsForCompletion",
				selectedAnalysisQaEventIdsForCompletion);
		
		//bugzilla 2500
		PropertyUtils.setProperty(dynaForm,
				"selectedSampleQaEventIdsForCompletion",
				selectedSampleQaEventIdsForCompletion);

		//bugzilla 2566 for now qa events is for human and newborn
		//PropertyUtils.setProperty(dynaForm, "domain", domain);
		PropertyUtils.setProperty(dynaForm, "testQaEvents", testQaEvents);
		//bugzilla 2501
		PropertyUtils.setProperty(dynaForm, "multipleSampleMode", multipleSampleMode);
		PropertyUtils.setProperty(dynaForm, "selectedQaEventsCategoryId", qaEventCategoryId);
 
		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "qaeventsentry.add.title";
		} else {
			return "qaeventsentry.edit.title";
		}
	}

	protected String getPageTitleKeyParameter(HttpServletRequest request,
			ActionForm form) {
		BaseActionForm dynaForm = (BaseActionForm) form;
		String accn = "";
		if (dynaForm.get("accessionNumber") != null) {
			accn = (String) dynaForm.get("accessionNumber");
		}
		return accn;
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "qaeventsentry.add.subtitle";
		} else {
			return "qaeventsentry.edit.subtitle";
		}
	}

}
