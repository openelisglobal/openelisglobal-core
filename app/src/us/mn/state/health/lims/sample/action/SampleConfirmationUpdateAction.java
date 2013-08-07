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
 * Copyright (C) CIRG, University of Washington, Seattle WA.  All Rights Reserved.
 *
 */
package us.mn.state.health.lims.sample.action;

import org.apache.commons.validator.GenericValidator;
import org.apache.struts.Globals;
import org.apache.struts.action.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.Transaction;
import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.formfields.FormFields;
import us.mn.state.health.lims.common.formfields.FormFields.Field;
import us.mn.state.health.lims.common.provider.validation.IAccessionNumberValidator;
import us.mn.state.health.lims.common.provider.validation.IAccessionNumberValidator.ValidationResults;
import us.mn.state.health.lims.common.services.StatusService;
import us.mn.state.health.lims.common.services.StatusService.AnalysisStatus;
import us.mn.state.health.lims.common.services.StatusService.OrderStatus;
import us.mn.state.health.lims.common.services.StatusService.SampleStatus;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.note.dao.NoteDAO;
import us.mn.state.health.lims.note.daoimpl.NoteDAOImpl;
import us.mn.state.health.lims.note.util.NoteUtil;
import us.mn.state.health.lims.note.valueholder.Note;
import us.mn.state.health.lims.observationhistory.dao.ObservationHistoryDAO;
import us.mn.state.health.lims.observationhistory.daoimpl.ObservationHistoryDAOImpl;
import us.mn.state.health.lims.observationhistory.valueholder.ObservationHistory;
import us.mn.state.health.lims.observationhistorytype.dao.ObservationHistoryTypeDAO;
import us.mn.state.health.lims.observationhistorytype.daoImpl.ObservationHistoryTypeDAOImpl;
import us.mn.state.health.lims.observationhistorytype.valueholder.ObservationHistoryType;
import us.mn.state.health.lims.organization.dao.OrganizationContactDAO;
import us.mn.state.health.lims.organization.daoimpl.OrganizationContactDAOImpl;
import us.mn.state.health.lims.organization.valueholder.OrganizationContact;
import us.mn.state.health.lims.patient.action.IPatientUpdate;
import us.mn.state.health.lims.patient.action.PatientManagementUpdateAction;
import us.mn.state.health.lims.patient.action.bean.PatientManagmentInfo;
import us.mn.state.health.lims.person.dao.PersonDAO;
import us.mn.state.health.lims.person.daoimpl.PersonDAOImpl;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.referencetables.dao.ReferenceTablesDAO;
import us.mn.state.health.lims.referencetables.daoimpl.ReferenceTablesDAOImpl;
import us.mn.state.health.lims.referencetables.valueholder.ReferenceTables;
import us.mn.state.health.lims.requester.dao.RequesterTypeDAO;
import us.mn.state.health.lims.requester.dao.SampleRequesterDAO;
import us.mn.state.health.lims.requester.daoimpl.RequesterTypeDAOImpl;
import us.mn.state.health.lims.requester.daoimpl.SampleRequesterDAOImpl;
import us.mn.state.health.lims.requester.valueholder.SampleRequester;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.util.AccessionNumberUtil;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.samplehuman.dao.SampleHumanDAO;
import us.mn.state.health.lims.samplehuman.daoimpl.SampleHumanDAOImpl;
import us.mn.state.health.lims.samplehuman.valueholder.SampleHuman;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.typeofsample.dao.TypeOfSampleDAO;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleDAOImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * The SampleEntryAction class represents the initial Action for the SampleEntry
 * form of the application
 *
 */
public class SampleConfirmationUpdateAction extends BaseSampleEntryAction {
	private static String SAMPLE_ITEM_TABLE_ID = null;
	private static String ORG_REQUESTER_TYPE_ID;
	private static String PERSON_REQUESTER_TYPE_ID;
	private Sample sample;
	private SampleHuman sampleHuman;
	private List<SampleItemSet> sampleItemSetList;
	private boolean savePatient = false;
	private ActionMessages patientErrors;
	private String patientId;
	private SampleRequester personSampleRequester;
	private SampleRequester orgSampleRequester;
	private Person personRequester;
	private OrganizationContact organizationContact;
	private static boolean useInitialSampleCondition;

	private static SampleDAO sampleDAO = new SampleDAOImpl();
	private static SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
	private static TestDAO testDAO = new TestDAOImpl();
	private static SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
	private static ResultDAO resultDAO = new ResultDAOImpl();
	private static AnalysisDAO analysisDAO = new AnalysisDAOImpl();
	private static NoteDAO noteDAO = new NoteDAOImpl();
	private static SampleRequesterDAO sampleRequesterDAO = new SampleRequesterDAOImpl();
	private static PersonDAO personDAO = new PersonDAOImpl();
	private static OrganizationContactDAO orgContactDAO = new OrganizationContactDAOImpl();
	private static ObservationHistoryDAO ohDAO = new ObservationHistoryDAOImpl();
	private static TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();

	private static String INITIAL_CONDITION_OBSERVATION_ID;

	static {
		ObservationHistoryTypeDAO ohtDAO = new ObservationHistoryTypeDAOImpl();
		ObservationHistoryType oht = ohtDAO.getByName("initialSampleCondition");
		if (oht != null) {
			INITIAL_CONDITION_OBSERVATION_ID = oht.getId();
		}
	
		ReferenceTablesDAO referenceTableDAO = new ReferenceTablesDAOImpl();
		ReferenceTables referenceTable = new ReferenceTables();
		referenceTable.setTableName("SAMPLE_ITEM");
		referenceTable = referenceTableDAO.getReferenceTableByName(referenceTable);

		SAMPLE_ITEM_TABLE_ID = referenceTable.getId();

		RequesterTypeDAO requesterTypeDAO = new RequesterTypeDAOImpl();
		ORG_REQUESTER_TYPE_ID = requesterTypeDAO.getRequesterTypeByName("organization").getId();
		PERSON_REQUESTER_TYPE_ID = requesterTypeDAO.getRequesterTypeByName("provider").getId();
		
		useInitialSampleCondition = FormFields.getInstance().useField(Field.InitialSampleCondition);
	}

	protected ActionForward performAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String forward = "success";

		request.getSession().setAttribute(IActionConstants.SAVE_DISABLED, IActionConstants.TRUE);

		BaseActionForm dynaForm = (BaseActionForm) form;
		String accessionNumber = dynaForm.getString("labno");
		
		ActionMessages errors = new ActionMessages();
		validateSample(errors, accessionNumber);

		if (errors.size() > 0) {
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			return mapping.findForward(FWD_FAIL);
		}
		
		PatientManagmentInfo patientInfo = (PatientManagmentInfo) dynaForm.get("patientProperties");
		IPatientUpdate patientUpdate = new PatientManagementUpdateAction();
		testAndInitializePatientForSaving(mapping, request, patientInfo, patientUpdate);

		createSample(dynaForm, accessionNumber);
		createSampleItemSets(dynaForm);
		createRequesters(dynaForm);

		Transaction tx = HibernateUtil.getSession().beginTransaction();

		try {
			if (savePatient) {
				patientUpdate.persistPatientData(patientInfo);
			}

			patientId = patientUpdate.getPatientId(dynaForm);

			sampleDAO.insertDataWithAccessionNumber(sample);
			sampleHuman.setSampleId(sample.getId());
			sampleHuman.setPatientId(patientId);
			sampleHumanDAO.insertData(sampleHuman);

			if (orgSampleRequester != null) {
				orgSampleRequester.setSampleId(sample.getId());
				sampleRequesterDAO.insertData(orgSampleRequester);
			}

			if (personRequester != null) {
				if (personRequester.getId() != null) {
					personDAO.updateData(personRequester);
				} else {
					personDAO.insertData(personRequester);
				}
			}

			if (personSampleRequester != null) {
				personSampleRequester.setRequesterId(personRequester.getId());
				personSampleRequester.setSampleId(sample.getId());
				sampleRequesterDAO.insertData(personSampleRequester);
			}

			if (organizationContact != null) {
				organizationContact.setPerson(personRequester);
				orgContactDAO.insert(organizationContact);
			}

			for (SampleItemSet sampleItemSet : sampleItemSetList) {
				sampleItemDAO.insertData(sampleItemSet.sampleItem);

				if (sampleItemSet.note != null) {
					sampleItemSet.note.setReferenceId(sampleItemSet.sampleItem.getId());
					noteDAO.insertData(sampleItemSet.note);
				}

				for (Analysis analysis : sampleItemSet.requestedAnalysisList) {
					analysisDAO.insertData(analysis, false);
				}

				for (ReferrerAnalysisSet analysisSet : sampleItemSet.referrerAnalysisSet) {
					analysisDAO.insertData(analysisSet.analysis, false);
					resultDAO.insertData(analysisSet.result);
				}

				if (useInitialSampleCondition) {
					persistInitialSampleConditions(sampleItemSet);
				}
			}

			tx.commit();
		} catch (Exception e) {
			tx.rollback();
		}

		return mapping.findForward(forward);
	}

	private void validateSample(ActionMessages errors, String accessionNumber) {
		// assure accession number
		ValidationResults result = AccessionNumberUtil.checkAccessionNumberValidity(accessionNumber, null, null, null);

		if (result != IAccessionNumberValidator.ValidationResults.SUCCESS) {
			String message = AccessionNumberUtil.getInvalidMessage(result);
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionError(message));
		}

//		// assure that there is at least 1 sample
//		if (sampleItemsTests.isEmpty()) {
//			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionError("errors.no.sample"));
//		}
//
//		// assure that all samples have tests
//		if (!allSamplesHaveTests()) {
//			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionError("errors.samples.with.no.tests"));
//		}

	}
	
	private void testAndInitializePatientForSaving(ActionMapping mapping, HttpServletRequest request, PatientManagmentInfo patientInfo,
			IPatientUpdate patientUpdate) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		patientUpdate.setPatientUpdateStatus(patientInfo);
		savePatient = patientUpdate.getPatientUpdateStatus() != PatientManagementUpdateAction.PatientUpdateStatus.NO_ACTION;

		if (savePatient) {
			patientErrors = patientUpdate.preparePatientData(mapping, request, patientInfo);
		}
	}

	private void createSample(BaseActionForm dynaForm, String accessionNumber) {
		
		String receivedDate = dynaForm.getString("receivedDate");
		String receivedTime = dynaForm.getString("recievedTime");

		receivedDate += GenericValidator.isBlankOrNull(receivedTime) ? " 00:00" : ( " " + receivedTime); 

		
		sample = new Sample();
		sample.setAccessionNumber(accessionNumber);
		sample.setReceivedTimestamp(DateUtil.convertStringDateToTimestamp(receivedDate));
		sample.setCollectionDate(sample.getReceivedTimestamp()); //note there really is no collection date but other code thinks there is
		sample.setSysUserId(currentUserId);
		sample.setDomain("H");
		sample.setEnteredDate(DateUtil.getNowAsSqlDate());
		sample.setStatusId(StatusService.getInstance().getStatusID(OrderStatus.Entered));

		sampleHuman = new SampleHuman();
		sampleHuman.setSysUserId(currentUserId);

	}

	private void createSampleItemSets(BaseActionForm dynaForm) throws DocumentException {
		sampleItemSetList = new ArrayList<SampleItemSet>();
		Document requestedTestsDOM = DocumentHelper.parseText(dynaForm.getString("requestAsXML"));

		int sampleItemSortOrder = 0;
		for (Object element : requestedTestsDOM.getRootElement().element("samples").elements("sample")) {
			SampleItemSet sampleItemSet = new SampleItemSet();

			Element sampleItemElement = (Element) element;

			SampleItem sampleItem = new SampleItem();
			sampleItem.setStatusId(StatusService.getInstance().getStatusID(SampleStatus.Entered));
			sampleItemSet.sampleItem = sampleItem;

			String externalId = sampleItemElement.attributeValue("requesterSampleId");
			sampleItem.setExternalId(GenericValidator.isBlankOrNull(externalId) ? null : externalId);

			String sampleTypeId = sampleItemElement.attributeValue("sampleType");
			sampleItem.setTypeOfSample(typeOfSampleDAO.getTypeOfSampleById(sampleTypeId));

			String collectionDate = sampleItemElement.attributeValue("collectionDate");
			String collectionTime = sampleItemElement.attributeValue("collectionTime");

            if (!GenericValidator.isBlankOrNull(collectionDate)) {
                collectionDate += GenericValidator.isBlankOrNull(collectionTime) ? " 00:00" : ( " " + collectionTime);
                sampleItem.setCollectionDate(DateUtil.convertStringDateToTimestamp(collectionDate));
            }

			sampleItem.setSortOrder( String.valueOf(sampleItemSortOrder));
			sampleItemSortOrder++;
			sampleItem.setSysUserId(currentUserId);
			sampleItem.setSample(sample);

			sampleItemSet.note = createNote(sampleItemElement, sampleItem);
			sampleItemSet.requestedAnalysisList = createRequestedAnalysisSet(sampleItemElement, sampleItem);

			List<ReferrerAnalysisSet> referrerSet = createReferrarAnalysisSets(sampleItemElement, sampleItem);

			sampleItemSet.referrerAnalysisSet = referrerSet;

			List<ObservationHistory> initialConditionList = null;
			if (useInitialSampleCondition) {
				String initialSampleConditionIdString = sampleItemElement.attributeValue("initialConditionIds");
				if ( !GenericValidator.isBlankOrNull(initialSampleConditionIdString)) {
					String[] initialSampleConditionIds = initialSampleConditionIdString.split(",");
					initialConditionList = new ArrayList<ObservationHistory>();

					for (int j = 0; j < initialSampleConditionIds.length; ++j) {
						ObservationHistory initialSampleConditions = new ObservationHistory();
						initialSampleConditions.setValue(initialSampleConditionIds[j]);
						initialSampleConditions.setValueType(ObservationHistory.ValueType.DICTIONARY);
						initialSampleConditions.setObservationHistoryTypeId(INITIAL_CONDITION_OBSERVATION_ID);
						initialConditionList.add(initialSampleConditions);
					}
				}
			}
			sampleItemSet.initialConditionList = initialConditionList;
			
			sampleItemSetList.add(sampleItemSet);
		}
	}

	private Note createNote(Element sampleItemElement, SampleItem sampleItem) {
		String noteText = sampleItemElement.attributeValue("note");

		if (!GenericValidator.isBlankOrNull(noteText)) {
			return NoteUtil.createSavableNote(null, noteText, null, SAMPLE_ITEM_TABLE_ID, "Confirmation Note", currentUserId, NoteUtil.getDefaultNoteType(NoteUtil.NoteSource.OTHER));
		}

		return null;
	}

	private List<ReferrerAnalysisSet> createReferrarAnalysisSets(Element sampleItemElement, SampleItem sampleItem) {
		List<ReferrerAnalysisSet> referrerSetList = new ArrayList<ReferrerAnalysisSet>();

		for (Object element : sampleItemElement.element("tests").elements("test")) {
			Element testElement = (Element) element;

			ReferrerAnalysisSet referrerSet = new ReferrerAnalysisSet();

			String testId = testElement.attributeValue("id");
			String resultType = testElement.attributeValue("resultType");
			String resultValue = testElement.attributeValue("value");

			Analysis analysis = new Analysis();
			Result result = new Result();

			Test test = testDAO.getTestById(testId);
			if ( test != null ) {
				analysis.setTest(test);
				analysis.setTestSection(test.getTestSection());
				analysis.setIsReportable(test.getIsReportable());
				result.setIsReportable(test.getIsReportable());
			}

			analysis.setAnalysisType("MANUAL");
			analysis.setSysUserId(currentUserId);
			analysis.setStatusId(StatusService.getInstance().getStatusID(AnalysisStatus.ReferredIn));

			analysis.setSampleItem(sampleItem);
			analysis.setRevision("0");
			analysis.setStartedDate(DateUtil.getNowAsSqlDate());

			result.setAnalysis(analysis);
			result.setResultType(resultType);
			result.setValue(resultValue);
			result.setSysUserId(currentUserId);
			result.setSortOrder("0");

			referrerSet.analysis = analysis;
			referrerSet.result = result;
			referrerSetList.add(referrerSet);
		}

		return referrerSetList;
	}

	private List<Analysis> createRequestedAnalysisSet(Element sampleItemElement, SampleItem sampleItem) {
		List<Analysis> analysisList = new ArrayList<Analysis>();
		String requestedAanlysisIds = sampleItemElement.attributeValue("requestedTests");

		if (!GenericValidator.isBlankOrNull(requestedAanlysisIds)) {
			String[] splitIds = requestedAanlysisIds.split(",");

			for (int i = 0; i < splitIds.length; ++i) {
				Analysis analysis = new Analysis();
				Test test = testDAO.getTestById(splitIds[i]);
				if (test != null) {
					analysis.setTest(test);
					analysis.setTestSection(test.getTestSection());
					analysis.setAnalysisType("MANUAL");
					analysis.setSysUserId(currentUserId);
					analysis.setStatusId(StatusService.getInstance().getStatusID(AnalysisStatus.NotStarted));
					analysis.setIsReportable(test.getIsReportable());
					analysis.setSampleItem(sampleItem);
					analysis.setRevision("0");
					analysis.setStartedDate(DateUtil.getNowAsSqlDate());
					analysisList.add(analysis);
				}
			}

		}

		return analysisList;
	}

	private void createRequesters(BaseActionForm dynaForm) {
		String orgId = dynaForm.getString("requestingOrganization");
		orgSampleRequester = null;
		personSampleRequester = null;
		organizationContact = null;

		if (!(GenericValidator.isBlankOrNull(orgId) || "0".equals(orgId))) {
			orgSampleRequester = new SampleRequester();
			orgSampleRequester.setRequesterId(orgId);
			orgSampleRequester.setRequesterTypeId(ORG_REQUESTER_TYPE_ID);
			orgSampleRequester.setSysUserId(currentUserId);
		}

		String personId = dynaForm.getString("personRequesterId");
		if (!(GenericValidator.isBlankOrNull(personId) || "0".equals(personId))) {
			personRequester = personDAO.getPersonById(personId);
		} else {
			personRequester = new Person();
			if (!GenericValidator.isBlankOrNull(orgId)) {
				organizationContact = new OrganizationContact();
				organizationContact.setSysUserId(currentUserId);
				organizationContact.setOrganizationId(orgId);
			}
		}

		personSampleRequester = new SampleRequester();
		personSampleRequester.setRequesterTypeId(PERSON_REQUESTER_TYPE_ID);
		personSampleRequester.setSysUserId(currentUserId);

		personRequester.setEmail(dynaForm.getString("e-mail"));
		personRequester.setFax(dynaForm.getString("fax"));
		personRequester.setWorkPhone(dynaForm.getString("phone"));
		personRequester.setFirstName(dynaForm.getString("firstName"));
		personRequester.setLastName(dynaForm.getString("lastName"));
		personRequester.setSysUserId(currentUserId);
	}

	private void persistInitialSampleConditions( SampleItemSet sampleItemSet) {
			if (sampleItemSet.initialConditionList != null) {
				for (ObservationHistory observation : sampleItemSet.initialConditionList) {
					observation.setSampleId(sampleItemSet.sampleItem.getSample().getId());
					observation.setSampleItemId(sampleItemSet.sampleItem.getId());
					observation.setPatientId(patientId);
					observation.setSysUserId(currentUserId);
					ohDAO.insertData(observation);
				}
			}

	}
	protected String getPageTitleKey() {
		return StringUtil.getContextualKeyForKey("banner.menu.sample.confirmation.add");
	}

	protected String getPageSubtitleKey() {
		return StringUtil.getContextualKeyForKey("banner.menu.sample.confirmation.add");
	}

	class SampleItemSet {
		public SampleItem sampleItem;
		public Note note;
		public List<ReferrerAnalysisSet> referrerAnalysisSet;
		public List<Analysis> requestedAnalysisList;
		public List<ObservationHistory> initialConditionList;

	}

	class ReferrerAnalysisSet {
		public Analysis analysis;
		public Result result;
	}
}
