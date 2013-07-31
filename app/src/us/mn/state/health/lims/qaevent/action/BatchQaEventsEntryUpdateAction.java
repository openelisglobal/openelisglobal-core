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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.analysisqaevent.dao.AnalysisQaEventDAO;
import us.mn.state.health.lims.analysisqaevent.daoimpl.AnalysisQaEventDAOImpl;
import us.mn.state.health.lims.analysisqaevent.valueholder.AnalysisQaEvent;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.valueholder.UserSessionData;
import us.mn.state.health.lims.qaevent.dao.QaEventDAO;
import us.mn.state.health.lims.qaevent.daoimpl.QaEventDAOImpl;
import us.mn.state.health.lims.qaevent.valueholder.QaEvent;
import us.mn.state.health.lims.sample.action.BatchSampleProcessingBaseAction;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;

/**
 * @author diane benz
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 * bugzilla 1942 - status changes
 * bugzilla 1992 - cleanup (remove counter definitions to stay consistent)
 */
public class BatchQaEventsEntryUpdateAction extends BatchSampleProcessingBaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		
		BaseActionForm dynaForm = (BaseActionForm) form;

		String fromAccessionNumber = (String) dynaForm
				.get("fromAccessionNumber");
		String toAccessionNumber = (String) dynaForm.get("toAccessionNumber");

		String skipAccessionNumber = (String) dynaForm
				.get("skipAccessionNumber");

		String[] skipAccessionNumbers = null;
		
		if (!StringUtil.isNullorNill(toAccessionNumber)) {
		  skipAccessionNumbers = skipAccessionNumber.split("[\\s]+"); //splits on whitespace?200
		}
		
		String stringOfPickIds = (String)dynaForm.get("pickIds");
		String[] arrayOfPickIds = stringOfPickIds.split(SystemConfiguration
				.getInstance().getDefaultIdSeparator(), -1);

	
		ActionMessages errors = null;

		try {
			//validate from-, to-,skip accession numbers
			errors = new ActionMessages();
			errors = validateAccessionNumber(fromAccessionNumber, "batchqaeventsentry.from.accession", request, errors, dynaForm);
			if (!StringUtil.isNullorNill(toAccessionNumber)) { 
		      errors = validateAccessionNumber(toAccessionNumber, "batchqaeventsentry.to.accession", request, errors, dynaForm);
		      if (errors == null || errors.size() == 0) {
                 errors = isFromAccessionLessThanToAccession(request, errors, fromAccessionNumber, toAccessionNumber);
		      }
			}
			//bugzilla 2180 - need to check if 1st element is null or nill
			if (skipAccessionNumbers != null && skipAccessionNumbers.length > 0 && !StringUtil.isNullorNill(skipAccessionNumbers[0])) {
				for (int i = 0; i < skipAccessionNumbers.length; i++) {
					errors = validateAccessionNumber(skipAccessionNumbers[i], "batchqaeventsentry.skip.accession", request, errors, dynaForm);
				}
			}
		} catch (Exception e) {
    		//bugzilla 2154
			LogEvent.logError("BatchQaEventsEntryUpdateAction","performAction()",e.toString());
			ActionError error = new ActionError("errors.ValidationException",
					null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		
		if (errors != null && errors.size() > 0) {
			saveErrors(request, errors);
			request.setAttribute(ALLOW_EDITS_KEY, "false");

			return mapping.findForward(FWD_FAIL);
		}

		// initialize the form
		dynaForm.initialize(mapping);
		
		List accessionNumbers = populateAccessionNumberList(fromAccessionNumber,
				toAccessionNumber, skipAccessionNumbers);
		

		// 1926 get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());
		
		org.hibernate.Transaction tx = HibernateUtil.getSession().beginTransaction();


			try {

				AnalysisDAO analysisDAO = new AnalysisDAOImpl();
				QaEventDAO qaEventDAO = new QaEventDAOImpl();
				AnalysisQaEventDAO analysisQaEventDAO = new AnalysisQaEventDAOImpl();
				SampleDAO sampleDAO = new SampleDAOImpl();
				SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
				
				SampleItem sampleItem = new SampleItem();
				List analyses = new ArrayList();
				
				//populate a list of qaEvents selected
				List listOfSelectedQaEvents = new ArrayList();
				for (int i = 0; i < arrayOfPickIds.length; i++) {
				    String pickId = arrayOfPickIds[i];
				    if (!StringUtil.isNullorNill(pickId)) {
				    	QaEvent qaEvent = new QaEvent();
				    	qaEvent.setId(pickId);
				    	qaEventDAO.getData(qaEvent);
				    	listOfSelectedQaEvents.add(qaEvent);
				    }
				}
				
				for (int i = 0; i < accessionNumbers.size(); i++) {
					String accessionNumber = (String)accessionNumbers.get(i);
					
					if (!StringUtil.isNullorNill(accessionNumber)) {
						Sample sample = new Sample();
						sample.setAccessionNumber(accessionNumber);
						sampleDAO.getSampleByAccessionNumber(sample);
						
						sampleItem.setSample(sample);
						sampleItemDAO.getDataBySample(sampleItem);
						
						//bugzilla 2227
						analyses = analysisDAO.getMaxRevisionAnalysesBySample(sampleItem);
						
						if (analyses != null && analyses.size() > 0) {
							for (int j = 0; j < analyses.size(); j++) {
								Analysis analysis = (Analysis) analyses.get(j);
								
								//for each qa event insert a record into ANALYSIS_QAEVENT
								for (int k = 0; k < listOfSelectedQaEvents.size(); k++) {
									QaEvent qaEvent = (QaEvent)listOfSelectedQaEvents.get(k);
									
									AnalysisQaEvent analysisQaEvent = new AnalysisQaEvent();
									analysisQaEvent.setAnalysis(analysis);
									analysisQaEvent.setQaEvent(qaEvent);
									analysisQaEvent = analysisQaEventDAO.getAnalysisQaEventByAnalysisAndQaEvent(analysisQaEvent);
									
									if (analysisQaEvent == null) {
										analysisQaEvent = new AnalysisQaEvent();
										analysisQaEvent.setAnalysis(analysis);
										analysisQaEvent.setQaEvent(qaEvent);
										analysisQaEvent.setCompletedDate(null);
										analysisQaEvent.setSysUserId(sysUserId);
										analysisQaEventDAO.insertData(analysisQaEvent);
									} 
								}
							}
						}

					}
				}
				
				tx.commit();

			} catch (LIMSRuntimeException lre) {
    			//bugzilla 2154
			    LogEvent.logError("BatchQaEventsEntryUpdateAction","performAction()",lre.toString());
				tx.rollback();

				errors = new ActionMessages();
				ActionError error = null;
				if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
					error = new ActionError("errors.OptimisticLockException",
							null, null);
				} else {
					error = new ActionError("errors.GetException", null, null);

				}
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
				saveErrors(request, errors);
				request.setAttribute(Globals.ERROR_KEY, errors);
				request.setAttribute(ALLOW_EDITS_KEY, "false");
				forward = FWD_FAIL;

			} finally {
				HibernateUtil.closeSession();
			}




		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "batchqaeventsentry.add.title";
		} else {
			return "batchqaeventsentry.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "batchqaeventsentry.add.subtitle";
		} else {
			return "batchqaeventsentry.edit.subtitle";
		}
	}


}