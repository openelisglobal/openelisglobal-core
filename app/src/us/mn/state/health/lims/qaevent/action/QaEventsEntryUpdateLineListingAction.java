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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import us.mn.state.health.lims.action.dao.ActionDAO;
import us.mn.state.health.lims.action.daoimpl.ActionDAOImpl;
import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysisqaevent.dao.AnalysisQaEventDAO;
import us.mn.state.health.lims.analysisqaevent.daoimpl.AnalysisQaEventDAOImpl;
import us.mn.state.health.lims.analysisqaevent.valueholder.AnalysisQaEvent;
import us.mn.state.health.lims.analysisqaeventaction.dao.AnalysisQaEventActionDAO;
import us.mn.state.health.lims.analysisqaeventaction.daoimpl.AnalysisQaEventActionDAOImpl;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.valueholder.UserSessionData;
import us.mn.state.health.lims.qaevent.dao.QaEventDAO;
import us.mn.state.health.lims.qaevent.daoimpl.QaEventDAOImpl;
import us.mn.state.health.lims.qaevent.valueholder.QaEventLineListingViewData;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleqaevent.dao.SampleQaEventDAO;
import us.mn.state.health.lims.sampleqaevent.daoimpl.SampleQaEventDAOImpl;
import us.mn.state.health.lims.sampleqaevent.valueholder.SampleQaEvent;
import us.mn.state.health.lims.sampleqaeventaction.dao.SampleQaEventActionDAO;
import us.mn.state.health.lims.sampleqaeventaction.daoimpl.SampleQaEventActionDAOImpl;
import us.mn.state.health.lims.systemuser.dao.SystemUserDAO;
import us.mn.state.health.lims.systemuser.daoimpl.SystemUserDAOImpl;
import us.mn.state.health.lims.systemuser.valueholder.SystemUser;

/**
 * @author diane benz
 * bugzilla 2504
 */
public class QaEventsEntryUpdateLineListingAction extends BaseAction {


	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		BaseActionForm dynaForm = (BaseActionForm) form;

		String accessionNumber = (String) request.getParameter(ACCESSION_NUMBER);
		List samplesWithPendingQaEvents = (List) dynaForm.get("samplesWithPendingQaEvents");

		String qaEventCategoryId = (String)dynaForm.get("selectedQaEventsCategoryId");

		// server side validation of accessionNumber
		// validate on server-side sample accession number

		ActionMessages errors = null;

		try {
			errors = new ActionMessages();
			errors = validateAccessionNumber(request, errors, dynaForm);
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("QaEventsEntryUpdateLineListingAction","performAction()",e.toString());
			ActionError error = new ActionError("errors.ValidationException",
					null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		if (errors != null && errors.size() > 0) {
			saveErrors(request, errors);
			return mapping.findForward(FWD_FAIL);
		}

		// 1926 get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());

		//bugzilla 2481 Action Owner
		SystemUser systemUser = new SystemUser();
		systemUser.setId(sysUserId);
		SystemUserDAO systemUserDAO = new SystemUserDAOImpl();
		systemUserDAO.getData(systemUser);

		Date today = Calendar.getInstance().getTime();
		Locale locale = (Locale) request.getSession().getAttribute(
				"org.apache.struts.action.LOCALE");

		String dateAsText = DateUtil.formatDateAsText(today, locale);

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

				//get the sample qa events from the form
				if (samplesWithPendingQaEvents != null && samplesWithPendingQaEvents.size() >0) {

					QaEventLineListingViewData sampleWithQaEvents = null;

					for (int i = 0; i < samplesWithPendingQaEvents.size(); i++) {
						sampleWithQaEvents = (QaEventLineListingViewData)samplesWithPendingQaEvents.get(i);
						if (sampleWithQaEvents.getSample().getAccessionNumber().equals(accessionNumber)) {
							break;	 
						}
					}

					if (sampleWithQaEvents != null) {
						//get list of pending qa events for sample
						List sampleQaEvents = sampleWithQaEvents.getSampleQaEvents();

						//get List of pending qa events for tests for sample
						List testQaEvents = sampleWithQaEvents.getTestQaEvents();

						//first update completed dates for sample
						if (sampleQaEvents != null
								&& sampleQaEvents.size() > 0) {
							for (int i = 0; i < sampleQaEvents.size(); i++) {
								SampleQaEvent sampleQaEvent = (SampleQaEvent)sampleQaEvents.get(i);
								//sampleQaEventDAO.getData(sampleQaEvent);
								if (sampleQaEvent.getCompletedDate() == null) {
									sampleQaEvent.setCompletedDateForDisplay(dateAsText);
									sampleQaEvent.setSysUserId(sysUserId);
									sampleQaEventDAO.updateData(sampleQaEvent);
								}

							}
						} 


						//now update test completed dates
						if (testQaEvents != null
								&& testQaEvents.size() > 0) {
							for (int i = 0; i < testQaEvents.size(); i++) {
								//for each sample there is a list of tests
								AnalysisQaEvent analysisQaEvent = (AnalysisQaEvent)testQaEvents.get(i);
								//analysisQaEventDAO.getData(analysisQaEvent);
								if (analysisQaEvent.getCompletedDate() == null) {
									analysisQaEvent.setCompletedDateForDisplay(dateAsText);
									analysisQaEvent.setSysUserId(sysUserId);
									analysisQaEventDAO.updateData(analysisQaEvent);
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

		request.setAttribute(ACCESSION_NUMBER, accessionNumber);
		request.setAttribute("selectedQaEventsCategoryId", qaEventCategoryId);


		return mapping.findForward(forward);
	}

	protected String getPageTitleKeyParameter(HttpServletRequest request,
			ActionForm form) {
		return null;
	}

	protected String getPageTitleKey() {
		return null;
	}

	protected String getPageSubtitleKey() {
		return null;
	}

}
