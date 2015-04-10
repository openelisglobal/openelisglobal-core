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
package us.mn.state.health.lims.sample.action;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;
import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.analysisqaevent.dao.AnalysisQaEventDAO;
import us.mn.state.health.lims.analysisqaevent.daoimpl.AnalysisQaEventDAOImpl;
import us.mn.state.health.lims.analysisqaevent.valueholder.AnalysisQaEvent;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.provider.validation.QuickEntryAccessionNumberValidationProvider;
import us.mn.state.health.lims.common.provider.validation.QuickEntrySampleSourceValidationProvider;
import us.mn.state.health.lims.common.provider.validation.QuickEntrySampleTypeValidationProvider;
import us.mn.state.health.lims.common.services.StatusService;
import us.mn.state.health.lims.common.services.StatusService.SampleStatus;
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
import us.mn.state.health.lims.sourceofsample.dao.SourceOfSampleDAO;
import us.mn.state.health.lims.sourceofsample.daoimpl.SourceOfSampleDAOImpl;
import us.mn.state.health.lims.sourceofsample.valueholder.SourceOfSample;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.typeofsample.dao.TypeOfSampleDAO;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleDAOImpl;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * The QuickEntryUpdateAction class represents the Update Action for the
 * QuickEntry form of the application.
 * 
 * @author - Ken Rosha 08/29/2006 02/21/2007 - bugzilla 1757: clean up overly
 *         complex code and fix bug with received date 08/02/2007 - bugzilla
 *         1813 add batch functionality
 */
public class QuickEntryUpdateAction extends BatchSampleProcessingBaseAction {

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");

		BaseActionForm dynaForm = (BaseActionForm) form;

		// Perform server-side validation (validation.xml)
		ActionMessages errors = dynaForm.validate(mapping, request);
		try {
			errors = validateAll(request, errors, dynaForm);
		} catch (Exception e) {
    		//bugzilla 2154
			LogEvent.logError("QuickEntryUpdateAction","performAction()",e.toString());
			ActionError error = new ActionError("errors.ValidationException",
					null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		if ((errors != null) && (errors.size() > 0)) {
			saveErrors(request, errors);
			return mapping.findForward(FWD_FAIL);
		}

		String accessionNumber = (String) dynaForm.get("accessionNumber");
		String accessionNumber2 = (String) dynaForm.get("accessionNumber2");
		String receivedDate = (String) dynaForm.get("receivedDateForDisplay");
		String typeOfSample = (String) dynaForm.get("typeOfSampleDesc");
		String sourceOfSample = (String) dynaForm.get("sourceOfSampleDesc");
		//bugzilla 1778
		String sourceOther = (String) dynaForm.get("sourceOther");
		String stringOfTestIds = (String) dynaForm.get("selectedTestIds");

		Sample sample = null;
		List accessionNumbers = populateAccessionNumberList(accessionNumber,
				accessionNumber2, null);

		SampleItem sampleItem = new SampleItem();
		sampleItem.setStatusId(StatusService.getInstance().getStatusID(SampleStatus.Entered));
		// 1926 get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());
		Transaction tx = HibernateUtil.getSession().beginTransaction();
		try {
			SampleDAO sampleDAO = new SampleDAOImpl();
			SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
			AnalysisDAO analysisDAO = new AnalysisDAOImpl();
			TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
			SourceOfSampleDAO sourceOfSampleDAO = new SourceOfSampleDAOImpl();
			AnalysisQaEventDAO analysisQaEventDAO = new AnalysisQaEventDAOImpl();
			QaEventDAO qaEventDAO = new QaEventDAOImpl();

			for (int j = 0; j < accessionNumbers.size(); j++) {

				sample = new Sample();
				sample.setAccessionNumber((String) accessionNumbers.get(j));
				sampleDAO.getSampleByAccessionNumber(sample);

				// if we are in batch mode we need to make
				// sure that the samples being updated are
				// in the correct status
				if (SystemConfiguration.getInstance()
						.getSampleStatusLabelPrinted().equals(
								sample.getStatus())) {

					String[] listOfTestIds = stringOfTestIds.split(
							SystemConfiguration.getInstance()
									.getDefaultIdSeparator(), -1);
					List analyses = new ArrayList();
					for (int i = 0; i < listOfTestIds.length; i++) {
						if (!StringUtil.isNullorNill(listOfTestIds[i])) {
							Analysis analysis = new Analysis();
							Test test = new Test();
							String testId = (String) listOfTestIds[i];
							test.setId(testId);

							TestDAO testDAO = new TestDAOImpl();
							testDAO.getData(test);
							analysis.setTest(test);
							// bgm - bugzilla 1495 setting analysis status here
							// from
							// above
							analysis.setStatus(SystemConfiguration
									.getInstance().getAnalysisStatusAssigned());
							//bugzilla 1942
							analysis.setIsReportable(test.getIsReportable());

							/** TODO: need to populate this with actual data!!! */
							analysis.setAnalysisType("TEST");
							analyses.add(analysis);
						}
					}

					sample.setAccessionNumber((String) accessionNumbers.get(j));
					sample.setStatus(SystemConfiguration.getInstance()
							.getSampleStatusQuickEntryComplete());
					sample.setReceivedDateForDisplay(receivedDate);
					sample.setCollectionTimeForDisplay("00:00");
					// Set entered date to today's date
					Date today = Calendar.getInstance().getTime();
					String dateAsText = DateUtil.formatDateAsText(today);
					sample.setEnteredDateForDisplay(dateAsText);
					
					//bugzilla 2528
					String newbornTypeOfSample = SystemConfiguration.getInstance().getNewbornTypeOfSample();
					if ( typeOfSample.equals(newbornTypeOfSample) )
						sample.setDomain(SystemConfiguration.getInstance().getNewbornDomain());
					else
						sample.setDomain(SystemConfiguration.getInstance().getHumanDomain());
			
					sampleItem.setSortOrder("1");
					if (!StringUtil.isNullorNill(typeOfSample)) {
						TypeOfSample typeOfSamp = new TypeOfSample();
						typeOfSamp.setDescription(typeOfSample);
						typeOfSamp.setDomain(SystemConfiguration.getInstance()
								.getHumanDomain());
						typeOfSamp = typeOfSampleDAO
								.getTypeOfSampleByDescriptionAndDomain(
										typeOfSamp, true);
						sampleItem.setTypeOfSample(typeOfSamp);
					}

					if (!StringUtil.isNullorNill(sourceOfSample)) {
						SourceOfSample sourceOfSamp = new SourceOfSample();
						sourceOfSamp.setDescription(sourceOfSample);
						
						//bugzilla 2528
						if ( typeOfSample.equals(newbornTypeOfSample) )
							sourceOfSamp.setDomain(SystemConfiguration.getInstance().getNewbornDomain());
						else
							sourceOfSamp.setDomain(SystemConfiguration.getInstance().getHumanDomain());
						
						String sourceOfSampleId = dynaForm.getString("sourceOfSampleId");						
						sourceOfSamp = sourceOfSampleDAO.getSourceOfSampleByDescriptionAndDomain(sourceOfSamp, true);
						sampleItem.setSourceOfSampleId(sourceOfSampleId);
					}
					
					//bugzilla 1778
					if (!StringUtil.isNullorNill(sourceOther)) {
						sampleItem.setSourceOther(sourceOther);
					}

					// bugzilla 1926
					sample.setSysUserId(sysUserId);
					sampleItem.setSysUserId(sysUserId);
					if (!StringUtil.isNullorNill(sample.getId())) {
						// Now update
						sampleDAO.updateData(sample);
						// bugzilla 1773 need to store sample not sampleId for
						// use in sorting
						sampleItem.setSample(sample);
					} else {

						sampleDAO.insertDataWithAccessionNumber(sample);
						// bugzilla 1773 need to store sample not sampleId for
						// use in sorting
						sampleItem.setSample(sample);
					}

					sampleItemDAO.insertData(sampleItem);

					// Analysis table
					for (int i = 0; i < analyses.size(); i++) {
						Analysis analysis = (Analysis) analyses.get(i);
						analysis.setSampleItem(sampleItem);
						// bugizlla 1926
						analysis.setSysUserId(sysUserId);
						//bugzilla 2064
						analysis.setRevision(SystemConfiguration.getInstance().getAnalysisDefaultRevision());

        				//bugzilla 2013 added duplicateCheck parameter
     					analysisDAO.insertData(analysis, false);
						
						//bugzilla 2028: If Sample Type NOT GIVE then create ANALYSIS_QA_EVENT for each added test
						if (typeOfSample.equals(SAMPLE_TYPE_NOT_GIVEN)) {
							QaEvent qaEvent = new QaEvent();
							qaEvent.setQaEventName(SystemConfiguration.getInstance().getQaEventCodeForRequestNoSampleType());
							qaEvent = qaEventDAO.getQaEventByName(qaEvent);
							AnalysisQaEvent analysisQaEvent = new AnalysisQaEvent();
							analysisQaEvent.setAnalysis(analysis);
							analysisQaEvent.setQaEvent(qaEvent);
							analysisQaEvent.setCompletedDate(null);
							analysisQaEvent.setSysUserId(sysUserId);
							analysisQaEventDAO.insertData(analysisQaEvent);
						}
					}
				}
			}

			tx.commit();

		} catch (LIMSRuntimeException lre) {
    		//bugzilla 2154
			LogEvent.logError("QuickEntryUpdateAction","performAction()",lre.toString());
			tx.rollback();
			errors = new ActionMessages();
			ActionError error = null;
			if (lre.getException() instanceof StaleObjectStateException) {
				error = new ActionError("errors.OptimisticLockException", null,
						null);
			} else {
				//bugzilla 2154
			    LogEvent.logError("QuickEntryUpdateAction","performAction()",lre.toString());
				error = new ActionError("errors.UpdateException", null, null);
			}
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute(ALLOW_EDITS_KEY, "false");
			return mapping.findForward(FWD_FAIL);

		} finally {
			HibernateUtil.closeSession();
		}

		// initialize the form
		dynaForm.initialize(mapping);
		PropertyUtils.copyProperties(dynaForm, sample);
		dynaForm.set("accessionNumber", accessionNumber);
		dynaForm.set("accessionNumber2", accessionNumber2);
		// PropertyUtils.setProperty(dynaForm, "sysUsers", sysUsers);

		if ("true".equalsIgnoreCase(request.getParameter("close"))) {
			forward = FWD_CLOSE;
		}
		if (forward.equals(FWD_SUCCESS)) {
			request.setAttribute("menuDefinition", "default");
		}

		return mapping.findForward(forward);
	}

	// ==============================================================

	protected String getPageTitleKey() {
		return "quick.entry.add.title";
	}

	// ==============================================================

	protected String getPageSubtitleKey() {
		return "quick.entry.add.title";
	}

	// ==============================================================

	protected ActionMessages validateAll(HttpServletRequest request,
			ActionMessages errors, BaseActionForm dynaForm) throws Exception {
		String result;
		String messageKey;

		// Accession number validation against database (reusing ajax validation
		// logic)
		// The specified accession number SHOULD NOT exist in the database.
		QuickEntryAccessionNumberValidationProvider accessionValidator = new QuickEntryAccessionNumberValidationProvider();
		result = accessionValidator.validate((String) dynaForm
				.get("accessionNumber"));
		messageKey = "quick.entry.accession.number";
		if (result.equals("invalid")) {
			ActionError error = new ActionError("errors.invalid",
					getMessageForKey(messageKey), null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}

		//only validate if not null or blank (this is not a required field)
		if (!StringUtil.isNullorNill((String) dynaForm.get("accessionNumber2"))) {
			result = accessionValidator.validate((String) dynaForm
					.get("accessionNumber2"));
			messageKey = "quick.entry.accession.number.2";
			if (result.equals("invalid")
					|| !fromAccessionLessThanThruAccession((String) dynaForm
							.get("accessionNumber"), (String) dynaForm
							.get("accessionNumber2"))) {
				ActionError error = new ActionError("errors.invalid",
						getMessageForKey(messageKey), null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			}
		}

		// Sample type validation against database (reusing ajax validation
		// logic)
		QuickEntrySampleTypeValidationProvider typeValidator = new QuickEntrySampleTypeValidationProvider();
		result = typeValidator.validate((String) dynaForm
				.get("typeOfSampleDesc"));
		messageKey = "quick.entry.sample.type";
		if (result.equals("invalid")) {
			ActionError error = new ActionError("errors.invalid",
					getMessageForKey(messageKey), null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}

		/*
		 * AIS - bugzilla 1396 Added if constraint-- to check if it is invalid,
		 * only when it is filled in
		 */
		if (!dynaForm.get("sourceOfSampleDesc").equals("")) {

			// Sample source validation against database (reusing ajax
			// validation logic)
			QuickEntrySampleSourceValidationProvider sourceValidator = new QuickEntrySampleSourceValidationProvider();
			result = sourceValidator.validate((String) dynaForm
					.get("sourceOfSampleDesc"));
			messageKey = "quick.entry.sample.source";
			if (result.equals("invalid")) {
				ActionError error = new ActionError("errors.invalid",
						getMessageForKey(messageKey), null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			}
		}
		return errors;
	}

	private boolean fromAccessionLessThanThruAccession(String from, String thru) {
		int fromInt = Integer.parseInt(from);
		int thruInt = Integer.parseInt(thru);
		if (fromInt < thruInt) {
			return true;
		}
		return false;
	}



}