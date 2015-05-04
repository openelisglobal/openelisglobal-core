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
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
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
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.qaevent.valueholder.QaEventLineListingViewData;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.samplehuman.dao.SampleHumanDAO;
import us.mn.state.health.lims.samplehuman.daoimpl.SampleHumanDAOImpl;
import us.mn.state.health.lims.samplehuman.valueholder.SampleHuman;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import us.mn.state.health.lims.sampleorganization.dao.SampleOrganizationDAO;
import us.mn.state.health.lims.sampleorganization.daoimpl.SampleOrganizationDAOImpl;
import us.mn.state.health.lims.sampleqaevent.dao.SampleQaEventDAO;
import us.mn.state.health.lims.sampleqaevent.daoimpl.SampleQaEventDAOImpl;
import us.mn.state.health.lims.sampleqaevent.valueholder.SampleQaEvent;

/**
 * @author diane benz 
 * bugzilla 2504
 * bugzilla 2566 removed Provider from logic since not needed and causes problems with NB (no provider)
 */
public class QaEventsEntryViewLineListingAction extends BaseAction {

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		BaseActionForm dynaForm = (BaseActionForm) form;
		ActionMessages errors = null;


		HttpSession session = request.getSession();

		String selectedCategoryIdFromRouting = (String) session.getAttribute(QAEVENTS_ENTRY_LINELISTING_PARAM_QAEVENT_CATEGORY_ID);
		String viewModeFromRouting = (String) session.getAttribute(QAEVENTS_ENTRY_PARAM_VIEW_MODE);
		//bugzilla 2502
		String fullScreenSectionFromRouting = (String) session.getAttribute(QAEVENTS_ENTRY_FULL_SCREEN_VIEW_SECTION);

		String qaEventCategoryId = "";
		//initialize to NORMAL MODE
		String viewMode = QAEVENTS_ENTRY_NORMAL_VIEW;
		//initialize to SAMPLE SECTION MAXIMIZED for full screen view
		String fullScreenSection = QAEVENTS_ENTRY_FULL_SCREEN_VIEW_SAMPLE_SECTION;
		if (!StringUtil.isNullorNill(selectedCategoryIdFromRouting)) {
			qaEventCategoryId = selectedCategoryIdFromRouting;
			//bugzilla 2504
			if (!StringUtil.isNullorNill(viewModeFromRouting))
				viewMode = viewModeFromRouting;
			if (!StringUtil.isNullorNill(fullScreenSectionFromRouting))
				fullScreenSection = fullScreenSectionFromRouting;
			session.setAttribute(QAEVENTS_ENTRY_LINELISTING_PARAM_QAEVENT_CATEGORY_ID, null);
			//bugzilla 2504
			session.setAttribute(QAEVENTS_ENTRY_PARAM_VIEW_MODE, null);
			//bugzilla 2502
			session.setAttribute(QAEVENTS_ENTRY_FULL_SCREEN_VIEW_SECTION, null);
		} else {
			qaEventCategoryId = (String) dynaForm.get("selectedQaEventsCategoryId");
			//bugzila 2504
			if (request.getParameter(QAEVENTS_ENTRY_PARAM_VIEW_MODE) != null){
				viewMode = (String)request.getParameter(QAEVENTS_ENTRY_PARAM_VIEW_MODE);
			}
			if (request.getParameter(QAEVENTS_ENTRY_FULL_SCREEN_VIEW_SECTION) != null){
				fullScreenSection = (String)request.getParameter(QAEVENTS_ENTRY_FULL_SCREEN_VIEW_SECTION);
			}
		}


		//bugzilla 2566 for now qa events is for human and newborn
		String accessionNumber = request.getParameter(ACCESSION_NUMBER);
		String totalRecords = "0";

		// initialize the form
		dynaForm.initialize(mapping);

		List samples = new ArrayList();
		List samplesWithPendingQaEvents = new ArrayList();
		List categoryDictionaries = new ArrayList();

		try {
			SampleDAO sampleDAO = new SampleDAOImpl();
			Sample samp = new Sample();
			//bugzilla 2566 for now qa events is for human and newborn
            //added filterbyDomain parameter
			if (!StringUtil.isNullorNill(qaEventCategoryId)) {
				samples = sampleDAO.getSamplesWithPendingQaEvents(samp, true,
						qaEventCategoryId, false);
			} else {
				samples = sampleDAO.getSamplesWithPendingQaEvents(samp, false,
						null, false);
			}

			DictionaryDAO dictionaryDAO = new DictionaryDAOImpl();
			categoryDictionaries = dictionaryDAO
			.getDictionaryEntrysByCategoryAbbreviation(SystemConfiguration
					.getInstance()
					.getQaEventDictionaryCategoryCategory());

			PatientDAO patientDAO = new PatientDAOImpl();
			SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
			SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
			SampleOrganizationDAO sampleOrganizationDAO = new SampleOrganizationDAOImpl();
			AnalysisDAO analysisDAO = new AnalysisDAOImpl();
			AnalysisQaEventDAO analysisQaEventDAO = new AnalysisQaEventDAOImpl();
			SampleQaEventDAO sampleQaEventDAO = new SampleQaEventDAOImpl();

			if (samples != null && samples.size() > 0) {
				for (int x = 0; x < samples.size(); x++) {
					Sample sample = (Sample) samples.get(x);

					List sampleQaEvents = new ArrayList();
					List testQaEvents = new ArrayList();

					QaEventLineListingViewData record = new QaEventLineListingViewData();

					Patient patient = new Patient();
					Person person = new Person();
					SampleHuman sampleHuman = new SampleHuman();
					SampleItem sampleItem = new SampleItem();
					List analyses = new ArrayList();

					if (!StringUtil.isNullorNill(sample.getId())) {
						sampleHuman.setSampleId(sample.getId());
						sampleHumanDAO.getDataBySample(sampleHuman);
						sampleItem.setSample(sample);
						sampleItemDAO.getDataBySample(sampleItem);

						if (sampleHuman != null) {
							if (sampleHuman.getPatientId() != null) {
								patient.setId(sampleHuman.getPatientId());
								patientDAO.getData(patient);
								person = patient.getPerson();
							}
						}
						analyses = analysisDAO
						.getMaxRevisionAnalysesBySample(sampleItem);
					}

					SampleQaEvent sampleQaEvent = new SampleQaEvent();
					sampleQaEvent.setSample(sample);

					List allQaEventsForSample = sampleQaEventDAO
					.getSampleQaEventsBySample(sampleQaEvent);

					for (int i = 0; i < analyses.size(); i++) {
						Analysis analysis = (Analysis) analyses.get(i);
						AnalysisQaEvent analysisQaEvent = new AnalysisQaEvent();
						analysisQaEvent.setAnalysis(analysis);
						List allQaEventsForTest = analysisQaEventDAO
						.getAnalysisQaEventsByAnalysis(analysisQaEvent);
						testQaEvents.addAll(allQaEventsForTest);
					}

					record.setSample(sample);
					record.setPatient(patient);
					record.setTestQaEvents(testQaEvents);
					record.setSampleQaEvents(allQaEventsForSample);
					samplesWithPendingQaEvents.add(record);
				}

			}
		} catch (LIMSRuntimeException lre) {
			// bugzilla 2154
			LogEvent.logError("QaEventsEntryViewLineListingAction",
					"performAction()", lre.toString());
			errors = new ActionMessages();
			ActionError error = null;
			error = new ActionError("errors.GetException", null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute(ALLOW_EDITS_KEY, "false");
			return mapping.findForward(FWD_FAIL);

		}

		PropertyUtils.setProperty(dynaForm, "categoryDictionaries",
				categoryDictionaries);
		PropertyUtils.setProperty(dynaForm, "samplesWithPendingQaEvents",
				samplesWithPendingQaEvents);
		if (samplesWithPendingQaEvents != null
				&& samplesWithPendingQaEvents.size() > 0) {
			PropertyUtils.setProperty(dynaForm, "totalCount",
					String.valueOf(samplesWithPendingQaEvents.size()));
		} else {
			PropertyUtils.setProperty(dynaForm, "totalCount", "0");
		}
		PropertyUtils.setProperty(dynaForm, "selectedQaEventsCategoryId",
				qaEventCategoryId);

		forward = FWD_SUCCESS;

		return mapping.findForward(forward);
	}

	protected String getPageTitleKeyParameter(HttpServletRequest request,
			ActionForm form) {
		return null;
	}

	protected String getPageTitleKey() {
		return "qaevents.entry.linelisting.title";
	}

	protected String getPageSubtitleKey() {
		return "qaevents.entry.linelisting.title";
	}

}
