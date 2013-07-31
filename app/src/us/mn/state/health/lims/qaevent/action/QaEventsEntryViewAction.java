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
import java.util.Collections;
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
import us.mn.state.health.lims.analysisqaeventaction.dao.AnalysisQaEventActionDAO;
import us.mn.state.health.lims.analysisqaeventaction.daoimpl.AnalysisQaEventActionDAOImpl;
import us.mn.state.health.lims.analysisqaeventaction.valueholder.AnalysisQaEventAction;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.provider.validation.AccessionNumberValidationProvider;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.note.dao.NoteDAO;
import us.mn.state.health.lims.note.daoimpl.NoteDAOImpl;
import us.mn.state.health.lims.note.valueholder.Note;
import us.mn.state.health.lims.organization.valueholder.Organization;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.project.valueholder.Project;
import us.mn.state.health.lims.qaevent.valueholder.Sample_QaEvent_Actions;
import us.mn.state.health.lims.qaevent.valueholder.Test_QaEventComparator;
import us.mn.state.health.lims.qaevent.valueholder.Test_QaEvent_Actions;
import us.mn.state.health.lims.qaevent.valueholder.Test_QaEvents;
import us.mn.state.health.lims.referencetables.valueholder.ReferenceTables;
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
import us.mn.state.health.lims.sampleorganization.valueholder.SampleOrganization;
import us.mn.state.health.lims.sampleproject.valueholder.SampleProject;
import us.mn.state.health.lims.sampleqaevent.dao.SampleQaEventDAO;
import us.mn.state.health.lims.sampleqaevent.daoimpl.SampleQaEventDAOImpl;
import us.mn.state.health.lims.sampleqaevent.valueholder.SampleQaEvent;
import us.mn.state.health.lims.sampleqaeventaction.dao.SampleQaEventActionDAO;
import us.mn.state.health.lims.sampleqaeventaction.daoimpl.SampleQaEventActionDAOImpl;
import us.mn.state.health.lims.sampleqaeventaction.valueholder.SampleQaEventAction;
import us.mn.state.health.lims.sourceofsample.valueholder.SourceOfSample;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;

/**
 * @author diane benz
 * modified 06/2008 for bugzilla 2053/2501
 * bugzilla 2566 removed Provider logic since not needed and causes problems with NB
 */
public class QaEventsEntryViewAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		BaseActionForm dynaForm = (BaseActionForm) form;
		HttpSession session = request.getSession();

		//bugzilla 2501
		String multipleSampleMode = (String)dynaForm.get("multipleSampleMode");
		String qaEventCategoryId = (String)dynaForm.get("selectedQaEventsCategoryId");
		String currentCount = (String)dynaForm.get("currentCount");
		String totalCount = (String)dynaForm.get("totalCount");
		//bugzilla 2502
		String viewMode = (String)dynaForm.get("viewMode");
		String fullScreenSection = (String)dynaForm.get("fullScreenSection");
		//bugzilla 2501
		DictionaryDAO dictionaryDAO = new DictionaryDAOImpl();
		List categoryDictionaries = dictionaryDAO.getDictionaryEntrysByCategory(SystemConfiguration.getInstance().getQaEventDictionaryCategoryCategory());
		List testQaEvents = new ArrayList();
		//bugzila 2501
		List sampleQaEvents = new ArrayList();

		//get accession number from one of these 3 in this order
		//1) if from PositionToRecord: attribute
		//2) if passing in accession number from another module: parameter
		//3) else form

		//if accession number is an attribute -> then we are coming from PositionToRecord and also need the requestedAccessionNumber to display warning if needed
		String requestedAccessionNumber = (String)request.getAttribute(ACCESSION_NUMBER_REQUESTED);
		String accessionNumber = (String)request.getAttribute(ACCESSION_NUMBER);
		if (StringUtil.isNullorNill(accessionNumber)) {
			accessionNumber = (String)request.getParameter(ACCESSION_NUMBER);
		} 		
		if (StringUtil.isNullorNill(accessionNumber)) {
			accessionNumber = (String)dynaForm.get("accessionNumber");
		}

		//this was set in PositionToRecord action
		String nextDisabled = (String)request.getAttribute(NEXT_DISABLED);
		String previousDisabled = (String)request.getAttribute(PREVIOUS_DISABLED);

		// server side validation of accessionNumber
		//this may already have an error or warning from another action
		ActionMessages errors = (ActionMessages)request.getAttribute(Globals.ERROR_KEY);
		if (errors == null) {
			errors = new ActionMessages();
		}

		try {
			//only do this if we don't have an error already to display:
			if (errors == null || errors.size() <= 0) {
				errors = validateAccessionNumber(request, errors, dynaForm);
			}
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ActionUpdateAction","performAction()",e.toString());
			ActionError error = new ActionError("errors.ValidationException",
					null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		if (errors != null && errors.size() > 0) {
			saveErrors(request, errors);
			request.setAttribute(ALLOW_EDITS_KEY, "false");
			//load collection for fail page
			PropertyUtils.setProperty(dynaForm, "categoryDictionaries", categoryDictionaries);
			//bugzilla 2501
			if (!StringUtil.isNullorNill(requestedAccessionNumber) && !StringUtil.isNullorNill(accessionNumber) && !requestedAccessionNumber.equals(accessionNumber)) {
				forward = FWD_FAIL;
			} else {
				return mapping.findForward(FWD_FAIL);
			}
		}

		// initialize the form
		dynaForm.initialize(mapping);


		Sample sample = new Sample();
		SampleDAO sampleDAO = new SampleDAOImpl();
		sample.setAccessionNumber(accessionNumber);


		try {
			sampleDAO.getSampleByAccessionNumber(sample);

		} catch (LIMSRuntimeException lre) {
			//bugzilla 2154
			LogEvent.logError("ActionUpdateAction","performAction()",lre.toString());
			errors = new ActionMessages();
			ActionError error = null;
			error = new ActionError("errors.GetException", null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute(ALLOW_EDITS_KEY, "false");
			return mapping.findForward(FWD_FAIL);

		}

		//bugzilla 2566 for now qa events is for human and newborn
		Patient patient = new Patient();
		Person person = new Person();
		SampleHuman sampleHuman = new SampleHuman();
		SampleOrganization sampleOrganization = new SampleOrganization();
		Organization organization = new Organization();
		List sampleProjects = new ArrayList();
		Project project = new Project();
		Project project2 = new Project();
		SampleItem sampleItem = new SampleItem();
		List analyses = new ArrayList();
		String[] selectedAnalysisQaEventIdsForCompletion = null;
		//bugzilla 2501
		String[] selectedSampleQaEventIdsForCompletion = null;

		try {

			PatientDAO patientDAO = new PatientDAOImpl();
			SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
			SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
			SampleOrganizationDAO sampleOrganizationDAO = new SampleOrganizationDAOImpl();
			AnalysisDAO analysisDAO = new AnalysisDAOImpl();
			AnalysisQaEventDAO analysisQaEventDAO = new AnalysisQaEventDAOImpl();
			//bugzilla 2501
			SampleQaEventDAO sampleQaEventDAO = new SampleQaEventDAOImpl();
			AnalysisQaEventActionDAO analysisQaEventActionDAO = new AnalysisQaEventActionDAOImpl();
			SampleQaEventActionDAO sampleQaEventActionDAO = new SampleQaEventActionDAOImpl();
			NoteDAO noteDAO = new NoteDAOImpl();

			if (!StringUtil.isNullorNill(sample.getId())) {
				sampleHuman.setSampleId(sample.getId());
				sampleHumanDAO.getDataBySample(sampleHuman);
				sampleOrganization.setSampleId(sample.getId());
				sampleOrganizationDAO.getDataBySample(sampleOrganization);
				sampleItem.setSample(sample);
				sampleItemDAO.getDataBySample(sampleItem);

				if (sampleHuman != null) {
					if (sampleHuman.getPatientId() != null) {
						patient.setId(sampleHuman.getPatientId());
						patientDAO.getData(patient);
						person = patient.getPerson();
    				}
				}
				//bugzilla 2227
				analyses = analysisDAO.getMaxRevisionAnalysesBySample(sampleItem);
			}
			organization = (Organization) sampleOrganization
			.getOrganization();
			sampleProjects = sample.getSampleProjects();

			if (sampleProjects != null && sampleProjects.size() > 0) {
				SampleProject sampleProject = (SampleProject) sampleProjects
				.get(0);
				project = sampleProject.getProject();
				if (sampleProjects.size() > 1) {
					SampleProject sampleProject2 = (SampleProject) sampleProjects
					.get(1);
					project2 = sampleProject2.getProject();
				}
			}

			//bugzilla 2500 get sample qa events
			SampleQaEvent sampleQaEvent = new SampleQaEvent();
			sampleQaEvent.setSample(sample);

			List allQaEventsForSample = sampleQaEventDAO
			.getSampleQaEventsBySample(sampleQaEvent);

			List actionsForSampleQaEvent = new ArrayList();

			for (int j = 0; j < allQaEventsForSample.size(); j++) {
				SampleQaEvent sampQaEvent = (SampleQaEvent) allQaEventsForSample
				.get(j);
				// get action/note info for each qaEvent for this test
				SampleQaEventAction sampleQaEventAction = new SampleQaEventAction();
				sampleQaEventAction.setSampleQaEvent(sampQaEvent);
				actionsForSampleQaEvent = sampleQaEventActionDAO
				.getSampleQaEventActionsBySampleQaEvent(sampleQaEventAction);

				Sample_QaEvent_Actions sampleQaEventActions = new Sample_QaEvent_Actions();
				sampleQaEventActions.setQaEvent(sampQaEvent);
				// convert list to array
				SampleQaEventAction[] actionsForSampleQaEventArray = null;
				if (actionsForSampleQaEvent != null
						&& actionsForSampleQaEvent.size() > 0) {
					actionsForSampleQaEventArray = new SampleQaEventAction[actionsForSampleQaEvent
					                                                       .size()];
					for (int x = 0; x < actionsForSampleQaEvent.size(); x++) {
						actionsForSampleQaEventArray[x] = (SampleQaEventAction) actionsForSampleQaEvent
						.get(x);
					}

				}

				sampleQaEventActions.setActions(actionsForSampleQaEventArray);
				List[] notes = null;

				if (actionsForSampleQaEventArray != null) {
					// now get the notes for this action if exist
					Note note = new Note();
					List notesBySampleQaEventAction = new ArrayList();
					notes = new ArrayList[actionsForSampleQaEventArray.length];

					for (int x = 0; x < actionsForSampleQaEventArray.length; x++) {
						SampleQaEventAction act = (SampleQaEventAction) actionsForSampleQaEventArray[x];
						note.setReferenceId(act.getId());
						//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
						ReferenceTables referenceTables = new ReferenceTables();
						referenceTables
						.setId(SystemConfiguration
								.getInstance()
								.getSampleQaEventActionReferenceTableId());
						//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
						note.setReferenceTables(referenceTables);
						notesBySampleQaEventAction = noteDAO
						.getAllNotesByRefIdRefTable(note);
						if (notesBySampleQaEventAction != null
								&& notesBySampleQaEventAction.size() > 0) {
							notes[x] = notesBySampleQaEventAction;
						} else {
							notes[x] = new ArrayList();
						}

					}
				}
				sampleQaEventActions.setNotes(notes);
				sampleQaEvents.add(sampleQaEventActions);

			}


			//end bugzilla 2500


			for (int i = 0; i < analyses.size(); i++) {
				Analysis analysis = (Analysis) analyses.get(i);
				AnalysisQaEvent analysisQaEvent = new AnalysisQaEvent();
				analysisQaEvent.setAnalysis(analysis);
				List allQaEventsForTest = analysisQaEventDAO
				.getAnalysisQaEventsByAnalysis(analysisQaEvent);

				List qaEventsForTest = new ArrayList();
				List actionsForQaEvent = new ArrayList();

				for (int j = 0; j < allQaEventsForTest.size(); j++) {
					AnalysisQaEvent aQaEvent = (AnalysisQaEvent) allQaEventsForTest
					.get(j);
					// get action/note info for each qaEvent for this test
					AnalysisQaEventAction analysisQaEventAction = new AnalysisQaEventAction();
					analysisQaEventAction.setAnalysisQaEvent(aQaEvent);
					actionsForQaEvent = analysisQaEventActionDAO
					.getAnalysisQaEventActionsByAnalysisQaEvent(analysisQaEventAction);

					//bugzilla 2501
					Test_QaEvent_Actions analysisQaEventActions = new Test_QaEvent_Actions();
					analysisQaEventActions.setQaEvent(aQaEvent);
					// convert list to array
					AnalysisQaEventAction[] actionsForQaEventArray = null;
					if (actionsForQaEvent != null
							&& actionsForQaEvent.size() > 0) {
						actionsForQaEventArray = new AnalysisQaEventAction[actionsForQaEvent
						                                                   .size()];
						for (int x = 0; x < actionsForQaEvent.size(); x++) {
							actionsForQaEventArray[x] = (AnalysisQaEventAction) actionsForQaEvent
							.get(x);
						}

					}

					//bugzilla 2501
					analysisQaEventActions.setActions(actionsForQaEventArray);
					List[] notes = null;

					if (actionsForQaEventArray != null) {
						// now get the notes for this action if exist
						Note note = new Note();
						List notesByAnalysisQaEventAction = new ArrayList();
						notes = new ArrayList[actionsForQaEventArray.length];

						for (int x = 0; x < actionsForQaEventArray.length; x++) {
							AnalysisQaEventAction act = (AnalysisQaEventAction) actionsForQaEventArray[x];
							note.setReferenceId(act.getId());
							//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
							ReferenceTables referenceTables = new ReferenceTables();
							referenceTables
							.setId(SystemConfiguration
									.getInstance()
									.getInstance()
									.getAnalysisQaEventActionReferenceTableId());
							//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
							note.setReferenceTables(referenceTables);
							notesByAnalysisQaEventAction = noteDAO
							.getAllNotesByRefIdRefTable(note);
							if (notesByAnalysisQaEventAction != null
									&& notesByAnalysisQaEventAction.size() > 0) {
								notes[x] = notesByAnalysisQaEventAction;
							} else {
								notes[x] = new ArrayList();
							}

						}
					}
					//bugzilla 2501
					analysisQaEventActions.setNotes(notes);
					qaEventsForTest.add(analysisQaEventActions);

				}

				Test_QaEvents tQaEvents = new Test_QaEvents();
				tQaEvents.setAnalysis(analysis);
				tQaEvents.setQaEvents(qaEventsForTest);

				testQaEvents.add(tQaEvents);

			}
			//bugzilla 1856
			Collections.sort(testQaEvents, Test_QaEventComparator.DESCRIPTION_COMPARATOR);

		} catch (LIMSRuntimeException lre) {
			//bugzilla 2154
			LogEvent.logError("ActionUpdateAction","performAction()",lre.toString());
			errors = new ActionMessages();
			ActionError error = null;
			error = new ActionError("errors.GetException", null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute(ALLOW_EDITS_KEY, "false");
			return mapping.findForward(FWD_FAIL);

		}

		// populate form from valueholder
		PropertyUtils.setProperty(dynaForm, "patientFirstName", person
				.getFirstName());
		PropertyUtils.setProperty(dynaForm, "patientLastName", person
				.getLastName());
		PropertyUtils.setProperty(dynaForm, "patientId", patient
				.getExternalId());
		PropertyUtils.setProperty(dynaForm, "birthDateForDisplay",
				(String) patient.getBirthDateForDisplay());
		TypeOfSample typeOfSample = sampleItem.getTypeOfSample();
		SourceOfSample sourceOfSample = sampleItem.getSourceOfSample();
		if (typeOfSample == null) {
			PropertyUtils.setProperty(dynaForm, "typeOfSample",
					new TypeOfSample());
		} else {
			PropertyUtils.setProperty(dynaForm, "typeOfSample",
					typeOfSample);
		}
		if (sourceOfSample == null) {
			PropertyUtils.setProperty(dynaForm, "sourceOfSample",
					new SourceOfSample());
		} else {
			PropertyUtils.setProperty(dynaForm, "sourceOfSample",
					sourceOfSample);
		}

		PropertyUtils.setProperty(dynaForm, "sourceOther", sampleItem
				.getSourceOther());
		PropertyUtils.setProperty(dynaForm, "receivedDateForDisplay",
				(String) sample.getReceivedDateForDisplay());
		PropertyUtils.setProperty(dynaForm, "collectionDateForDisplay",
				(String) sample.getCollectionDateForDisplay());
		PropertyUtils.setProperty(dynaForm, "referredCultureFlag",
				(String) sample.getReferredCultureFlag());

		if (organization == null) {
			PropertyUtils.setProperty(dynaForm, "organization",
					new Organization());
		} else {
			PropertyUtils.setProperty(dynaForm, "organization",
					organization);
		}

		if (project == null) {
			PropertyUtils.setProperty(dynaForm, "project", new Project());
		} else {
			PropertyUtils.setProperty(dynaForm, "project", project);
		}

		if (project2 == null) {
			PropertyUtils.setProperty(dynaForm, "project2", new Project());
		} else {
			PropertyUtils.setProperty(dynaForm, "project2", project2);
		}
		PropertyUtils.setProperty(dynaForm, "testQaEvents", testQaEvents);
		//bugzilla 2501
		PropertyUtils.setProperty(dynaForm, "categoryDictionaries", categoryDictionaries);

		// reload accession number
		PropertyUtils.setProperty(dynaForm, "accessionNumber",
				accessionNumber);
		//bug 2566
		//PropertyUtils.setProperty(dynaForm, "domain", domain);

		//bugzilla 2501
		if (StringUtil.isNullorNill(multipleSampleMode)) {
			PropertyUtils.setProperty(dynaForm, "currentCount", "1");
			PropertyUtils.setProperty(dynaForm, "totalCount", "1");
			request.setAttribute(PREVIOUS_DISABLED, TRUE);
			request.setAttribute(NEXT_DISABLED, TRUE);
		} else {
			PropertyUtils.setProperty(dynaForm, "currentCount", currentCount);
			PropertyUtils.setProperty(dynaForm, "totalCount", totalCount);
			PropertyUtils.setProperty(dynaForm, "selectedQaEventsCategoryId", qaEventCategoryId);
			request.setAttribute(PREVIOUS_DISABLED, previousDisabled);
			request.setAttribute(NEXT_DISABLED, nextDisabled);
		}

		//bugzilla 2500
		PropertyUtils.setProperty(dynaForm, "sampleQaEvents", sampleQaEvents);
		PropertyUtils.setProperty(dynaForm, "multipleSampleMode", multipleSampleMode);
		//bugzilla 2502
		//set hidden form variables
		if (!StringUtil.isNullorNill(viewMode)) {
			PropertyUtils.setProperty(dynaForm, "viewMode", viewMode);
		} else {
			PropertyUtils.setProperty(dynaForm, "viewMode", QAEVENTS_ENTRY_NORMAL_VIEW);
		}

		if (!StringUtil.isNullorNill(fullScreenSection)) {
			PropertyUtils.setProperty(dynaForm, "fullScreenSection", fullScreenSection);
		} else {
			PropertyUtils.setProperty(dynaForm, "fullScreenSection", QAEVENTS_ENTRY_FULL_SCREEN_VIEW_SAMPLE_SECTION);
		}

		//this request attribute is used to find out if we need to disable category dropdown on the form
		if (!StringUtil.isNullorNill(multipleSampleMode)) {
			request.setAttribute(MULTIPLE_SAMPLE_MODE, TRUE);
		} else {
			request.setAttribute(MULTIPLE_SAMPLE_MODE, FALSE);	
		}

		//bugzilla 2502
		if (!StringUtil.isNullorNill(viewMode)) {
			request.setAttribute(QAEVENTS_ENTRY_PARAM_VIEW_MODE, viewMode);
		} else {
			request.setAttribute(QAEVENTS_ENTRY_PARAM_VIEW_MODE, QAEVENTS_ENTRY_NORMAL_VIEW);	
		}

		//if in full screen mode then default is sample section maximized
		if (!StringUtil.isNullorNill(fullScreenSection)) {
			request.setAttribute(QAEVENTS_ENTRY_FULL_SCREEN_VIEW_SECTION, fullScreenSection);
		} else {
			request.setAttribute(QAEVENTS_ENTRY_FULL_SCREEN_VIEW_SECTION, QAEVENTS_ENTRY_FULL_SCREEN_VIEW_SAMPLE_SECTION);	
		}


		//bugzilla 2504
		forward = FWD_SUCCESS;

		if (viewMode != null && viewMode.equals(QAEVENTS_ENTRY_FULL_SCREEN_VIEW)) {
			if (fullScreenSection != null && fullScreenSection.equals(QAEVENTS_ENTRY_FULL_SCREEN_VIEW_SAMPLE_SECTION)) {
				forward = FWD_SUCCESS_FULL_SCREEN_VIEW_SAMPLE_SECTION;					
			} else {
				forward = FWD_SUCCESS_FULL_SCREEN_VIEW_TEST_SECTION;
			}
		}


        //bugzilla 2622
		//forward = FWD_SUCCESS;
		return mapping.findForward(forward);
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

	//bugzilla 2501
	protected ActionMessages validateAccessionNumber(
			HttpServletRequest request, ActionMessages errors,
			BaseActionForm dynaForm) throws Exception {


		String formName = dynaForm.getDynaClass().getName().toString();

		// accession number validation against database (reusing ajax
		// validation logic)
		AccessionNumberValidationProvider accessionNumberValidator = new AccessionNumberValidationProvider();

		// this was not validating before...
		String accessionNumber = "";
		String result = "";
		// if routing from another module accessionNumber is not a form variable but a request parameter
		if (!StringUtil.isNullorNill((String) request
				.getParameter(ACCESSION_NUMBER))) {
			accessionNumber = (String) request.getParameter(ACCESSION_NUMBER);
		} else if (!StringUtil.isNullorNill((String)request.getAttribute(ACCESSION_NUMBER))) {
			accessionNumber = (String)request.getAttribute(ACCESSION_NUMBER);
		} else {
			accessionNumber = (String) dynaForm.get(ACCESSION_NUMBER);
		}
		result = accessionNumberValidator.validate(accessionNumber, formName);

		String messageKey = "sample.accessionNumber";
		if (result.equals(INVALID)) {
			ActionError error = new ActionError("errors.invalid",
					getMessageForKey(messageKey), null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		if (result.equals(INVALIDSTATUS)) {
			ActionError error = new ActionError("error.invalid.sample.status",
					getMessageForKey(messageKey), null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		return errors;
	}	

	protected String getPageTitleKey() {
		if (isNew) {
			return "qaeventsentry.add.title";
		} else {
			return "qaeventsentry.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "qaeventsentry.add.subtitle";
		} else {
			return "qaeventsentry.edit.subtitle";
		}
	}

}
