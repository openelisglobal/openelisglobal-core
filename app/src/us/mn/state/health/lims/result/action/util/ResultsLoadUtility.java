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
package us.mn.state.health.lims.result.action.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.DynaActionForm;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.analyte.dao.AnalyteDAO;
import us.mn.state.health.lims.analyte.daoimpl.AnalyteDAOImpl;
import us.mn.state.health.lims.analyte.valueholder.Analyte;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.formfields.FormFields;
import us.mn.state.health.lims.common.formfields.FormFields.Field;
import us.mn.state.health.lims.common.services.QAService;
import us.mn.state.health.lims.common.services.StatusService;
import us.mn.state.health.lims.common.services.StatusService.AnalysisStatus;
import us.mn.state.health.lims.common.services.StatusService.OrderStatus;
import us.mn.state.health.lims.common.services.TestIdentityService;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.ConfigurationProperties.Property;
import us.mn.state.health.lims.common.util.DAOImplFactory;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.IdValuePair;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.inventory.action.InventoryUtility;
import us.mn.state.health.lims.inventory.form.InventoryKitItem;
import us.mn.state.health.lims.note.util.NoteUtil;
import us.mn.state.health.lims.note.valueholder.Note;
import us.mn.state.health.lims.observationhistory.dao.ObservationHistoryDAO;
import us.mn.state.health.lims.observationhistory.daoimpl.ObservationHistoryDAOImpl;
import us.mn.state.health.lims.observationhistory.valueholder.ObservationHistory;
import us.mn.state.health.lims.patient.util.PatientUtil;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.patientidentity.valueholder.PatientIdentity;
import us.mn.state.health.lims.patientidentitytype.util.PatientIdentityTypeMap;
import us.mn.state.health.lims.referencetables.dao.ReferenceTablesDAO;
import us.mn.state.health.lims.referencetables.valueholder.ReferenceTables;
import us.mn.state.health.lims.referral.dao.ReferralDAO;
import us.mn.state.health.lims.referral.daoimpl.ReferralDAOImpl;
import us.mn.state.health.lims.referral.valueholder.Referral;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.dao.ResultInventoryDAO;
import us.mn.state.health.lims.result.dao.ResultSignatureDAO;
import us.mn.state.health.lims.result.daoimpl.ResultInventoryDAOImpl;
import us.mn.state.health.lims.result.daoimpl.ResultSignatureDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.result.valueholder.ResultInventory;
import us.mn.state.health.lims.result.valueholder.ResultSignature;
import us.mn.state.health.lims.resultlimits.dao.ResultLimitDAO;
import us.mn.state.health.lims.resultlimits.valueholder.ResultLimit;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.samplehuman.dao.SampleHumanDAO;
import us.mn.state.health.lims.samplehuman.daoimpl.SampleHumanDAOImpl;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import us.mn.state.health.lims.statusofsample.util.StatusRules;
import us.mn.state.health.lims.systemuser.dao.SystemUserDAO;
import us.mn.state.health.lims.systemuser.daoimpl.SystemUserDAOImpl;
import us.mn.state.health.lims.systemuser.valueholder.SystemUser;
import us.mn.state.health.lims.systemusermodule.dao.PermissionAgentModuleDAO;
import us.mn.state.health.lims.systemusermodule.daoimpl.PermissionAgentFactory;
import us.mn.state.health.lims.test.beanItems.TestResultItem;
import us.mn.state.health.lims.test.beanItems.TestResultItem.ResultDisplayType;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.testreflex.action.util.TestReflexUtil;
import us.mn.state.health.lims.testreflex.valueholder.TestReflex;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;
import us.mn.state.health.lims.typeofsample.util.TypeOfSampleUtil;

public class ResultsLoadUtility {

	private static final boolean SORT_FORWARD = true;

	public static final String TESTKIT = "TestKit";

	private static final double INVALID_PATIENT_AGE = Double.MIN_VALUE;
	private static final String NO_PATIENT_NAME = " ";
	private static final String NO_PATIENT_INFO = " ";

	public static final String HIV_TYPE = "HIV_TEST_KIT";
	public static final String SYPHILIS_TYPE = "SYPHILIS_TEST_KIT";

	private static String RESULT_REFERENCE_TABLE_ID = null;
	private static String ANALYZER_RESULT_REFERENCE_TABLE_ID = null;
	
	private List<Sample> samples;
	private String currentDate = "";
	private Patient currPatient;
	private double currPatientAge;
	private Sample currSample;

	private Set<Integer> excludedAnalysisStatus = new HashSet<Integer>();
	private List<Integer> analysisStatusList = new ArrayList<Integer>();
	private List<Integer> sampleStatusList = new ArrayList<Integer>();

	private List<InventoryKitItem> activeKits;

	private Map<String, String> techLoginNameIdMap;
	private Map<String, String> supervisorLoginNameIdMap;
	private Map<String, String> userIdLoginNameMap;

	private final ResultDAO resultDAO = DAOImplFactory.getInstance().getResultDAOImpl();
	private final TestResultDAO testResultDAO = new TestResultDAOImpl();
	private final DictionaryDAO dictionaryDAO = new DictionaryDAOImpl();
	private final ResultSignatureDAO resultSignatureDAO = new ResultSignatureDAOImpl();
	private final ResultInventoryDAO resultInventoryDAO = new ResultInventoryDAOImpl();
	private final ObservationHistoryDAO observationHistoryDAO = new ObservationHistoryDAOImpl();
	private AnalysisDAO analysisDAO = new AnalysisDAOImpl();

	private final StatusRules statusRules = new StatusRules();

	private boolean inventoryNeeded = false;

	private static String ANALYTE_CONCLUSION_ID;
	private static String ANALYTE_CD4_CNT_CONCLUSION_ID;
	private static boolean depersonalize = FormFields.getInstance().useField(Field.DepersonalizedResults);
	private boolean useTechSignature =  ConfigurationProperties.getInstance().isPropertyValueEqual(Property.resultTechnicianName, "true");
	private static boolean supportReferrals = FormFields.getInstance().useField(Field.ResultsReferral);
	private static boolean useInitialSampleCondition = FormFields.getInstance().useField(Field.InitialSampleCondition);
	private boolean useCurrentUserAsTechDefault = ConfigurationProperties.getInstance().isPropertyValueEqual(Property.autoFillTechNameUser, "true"); 
	private String currentUserName = "";
	private ReferralDAO referralDAO = new ReferralDAOImpl();
	private int reflexGroup = 1;
	private boolean lockCurrentResults = false;

	static {
		ReferenceTablesDAO rtDAO = DAOImplFactory.getInstance().getReferenceTablesDAOImpl();
		ReferenceTables referenceTable = new ReferenceTables();
		referenceTable.setTableName("RESULT");
		referenceTable = rtDAO.getReferenceTableByName(referenceTable);
		RESULT_REFERENCE_TABLE_ID = referenceTable.getId();

		referenceTable = new ReferenceTables();
		referenceTable.setTableName("ANALYZER_RESULTS");
		referenceTable = rtDAO.getReferenceTableByName(referenceTable);
		ANALYZER_RESULT_REFERENCE_TABLE_ID = referenceTable.getId();

		AnalyteDAO analyteDAO = new AnalyteDAOImpl();
		Analyte analyte = new Analyte();
		analyte.setAnalyteName("Conclusion");
		analyte = analyteDAO.getAnalyteByName(analyte, false);
		ANALYTE_CONCLUSION_ID = analyte == null ? "" : analyte.getId();
		analyte.setAnalyteName("generated CD4 Count");
		analyte = analyteDAO.getAnalyteByName(analyte, false);
		ANALYTE_CD4_CNT_CONCLUSION_ID = analyte == null ? "" : analyte.getId();

	}

	public ResultsLoadUtility(String currentUserId) {
		if (useCurrentUserAsTechDefault) {
			SystemUserDAO systemUserDAO = new SystemUserDAOImpl();
			SystemUser systemUser = new SystemUser();
			systemUser.setId(currentUserId);
			systemUserDAO.getData(systemUser);

			if (systemUser.getId() != null) {
				currentUserName = systemUser.getFirstName() + " " + systemUser.getLastName();
			}
		}
	}

	public ResultsLoadUtility( ){}
	/*
	 * N.B. The patient info is used to determine the limits for the results,
	 * not for including patient information
	 */
	public List<TestResultItem> getGroupedTestsForSample(Sample sample, Patient patient) {

		reflexGroup = 1;
		activeKits = null;
		samples = new ArrayList<Sample>();

		if (sample != null) {
			samples.add(sample);
		}

		currPatient = patient;
		currPatientAge = INVALID_PATIENT_AGE;

		return getGroupedTestsForSamples();
	}

	public List<TestResultItem> getGroupedTestsForPatient(Patient patient) {
		reflexGroup = 1;
		activeKits = null;
		inventoryNeeded = false;

		currPatient = patient;
		currPatientAge = INVALID_PATIENT_AGE;

		SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
		samples = sampleHumanDAO.getSamplesForPatient(patient.getId());

		return getGroupedTestsForSamples();
	}

	/*
	 * @deprecated -- unsafe to use outside of beans with firstName, lastName,
	 * dob, gender, st, nationalId
	 */
	@Deprecated
	public void addIdentifingPatientInfo(Patient patient, DynaActionForm dynaForm) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {

		PatientIdentityTypeMap identityMap = PatientIdentityTypeMap.getInstance();
		List<PatientIdentity> identityList = PatientUtil.getIdentityListForPatient(patient);

		if (!depersonalize) {
			PropertyUtils.setProperty(dynaForm, "firstName", patient.getPerson().getFirstName());
			PropertyUtils.setProperty(dynaForm, "lastName", patient.getPerson().getLastName());
			PropertyUtils.setProperty(dynaForm, "dob", patient.getBirthDateForDisplay());
			PropertyUtils.setProperty(dynaForm, "gender", patient.getGender());
		}

		PropertyUtils.setProperty(dynaForm, "st", identityMap.getIdentityValue(identityList, "ST"));
		PropertyUtils.setProperty(dynaForm, "nationalId", GenericValidator.isBlankOrNull(patient.getNationalId()) ? patient.getExternalId()
				: patient.getNationalId());
	}

	public List<TestResultItem> getUnfinishedTestResultItemsInTestSection(String testSectionId) {

		List<Analysis> analysisList = analysisDAO.getAllAnalysisByTestSectionAndStatus(testSectionId, analysisStatusList, sampleStatusList);

		List<TestResultItem> selectedTest = getGroupedTestsForAnalysisList(analysisList, SORT_FORWARD);

		return selectedTest;
	}

	public List<TestResultItem> getGroupedTestsForAnalysisList(List<Analysis> filteredAnalysisList, boolean forwardSort)
			throws LIMSRuntimeException {

		activeKits = null;
		inventoryNeeded = false;
		reflexGroup = 1;

		List<TestResultItem> selectedTestList = new ArrayList<TestResultItem>();

		for (Analysis analysis : filteredAnalysisList) {

			currPatient = getPatientForSampleItem(analysis.getSampleItem());
			currPatientAge = INVALID_PATIENT_AGE; // force recalculation of age

			String patientName = "";
			String patientInfo = "";
			if (depersonalize) {
				patientInfo = GenericValidator.isBlankOrNull(currPatient.getNationalId()) ? currPatient.getExternalId() : currPatient
						.getNationalId();
			} else {
				patientName = getDisplayNameForCurrentPatient();
				patientInfo = currPatient.getNationalId() + ", " + currPatient.getGender() + ", " + currPatient.getBirthDateForDisplay();
			}

			currSample = analysis.getSampleItem().getSample();
			List<TestResultItem> testResultItemList = getTestResultItemFromAnalysis(analysis, patientName, patientInfo);

			for (TestResultItem selectionItem : testResultItemList) {
				selectedTestList.add(selectionItem);
			}
		}

		if (forwardSort) {
			sortByAccessionAndSequence(selectedTestList);
		} else {
			reverseSortByAccessionAndSequence(selectedTestList);
		}

		setSampleGroupingNumbers(selectedTestList);
		addUserSelectionReflexes(selectedTestList);

		return selectedTestList;
	}

	private String getDisplayNameForCurrentPatient() {
		StringBuilder nameBuilder = new StringBuilder();
		if (!GenericValidator.isBlankOrNull(currPatient.getPerson().getLastName())) {
			nameBuilder.append(currPatient.getPerson().getLastName());
		}

		if (!GenericValidator.isBlankOrNull(currPatient.getPerson().getFirstName())) {
			if (nameBuilder.length() > 0) {
				nameBuilder.append(", ");
			}

			nameBuilder.append(currPatient.getPerson().getFirstName());
		}

		return nameBuilder.toString();
	}

	private void reverseSortByAccessionAndSequence(List<? extends ResultItem> selectedTest) {
		Collections.sort(selectedTest, new Comparator<ResultItem>() {
			public int compare(ResultItem a, ResultItem b) {
				int accessionSort = b.getSequenceAccessionNumber().compareTo(a.getSequenceAccessionNumber());

				if( accessionSort == 0){ //only the accession number sorting is reversed
					if( !GenericValidator.isBlankOrNull(a.getTestSortOrder()) && !GenericValidator.isBlankOrNull(b.getTestSortOrder())){
						try {
							return Integer.parseInt(a.getTestSortOrder()) - Integer.parseInt(b.getTestSortOrder());
						} catch (NumberFormatException e) {
							return a.getTestName().compareTo(b.getTestName());
						}

					} else {
						return a.getTestName().compareTo(b.getTestName());
					}
				}

				return accessionSort;
			}
		});
	}

	public void sortByAccessionAndSequence(List<? extends ResultItem> selectedTest) {
		Collections.sort(selectedTest, new Comparator<ResultItem>() {
			public int compare(ResultItem a, ResultItem b) {
				int accessionSort = a.getSequenceAccessionNumber().compareTo(b.getSequenceAccessionNumber());

				if (accessionSort == 0) {
					if( !GenericValidator.isBlankOrNull(a.getTestSortOrder()) && !GenericValidator.isBlankOrNull(b.getTestSortOrder())){
						try {
							return Integer.parseInt(a.getTestSortOrder()) - Integer.parseInt(b.getTestSortOrder());
						} catch (NumberFormatException e) {
							return a.getTestName().compareTo(b.getTestName());
						}

					}else if( !GenericValidator.isBlankOrNull(a.getTestName()) && !GenericValidator.isBlankOrNull(b.getTestName())){
						return a.getTestName().compareTo(b.getTestName());
					}
				}

				return accessionSort;
			}
		});
	}

	public void setSampleGroupingNumbers(List<? extends ResultItem> selectedTests) {
		int groupingNumber = 1; // the header is always going to be 0

		String currentSequenceAccession = "";

		for (ResultItem item : selectedTests) {
			if (!currentSequenceAccession.equals(item.getSequenceAccessionNumber()) || item.getIsGroupSeparator()) {
				groupingNumber++;
				currentSequenceAccession = item.getSequenceAccessionNumber();
				item.setShowSampleDetails(true);
			} else {
				item.setShowSampleDetails(false);
			}

			item.setSampleGroupingNumber(groupingNumber);

		}
	}

	@SuppressWarnings("unchecked")
	public List<Test> getTestsInSection(String id) {

		TestDAO testDAO = new TestDAOImpl();
		return testDAO.getTestsByTestSection(id);
	}


	private List<TestResultItem> getTestResultItemFromAnalysis(Analysis analysis, String patientName, String patientInfo)
			throws LIMSRuntimeException {
		List<TestResultItem> testResultList = new ArrayList<TestResultItem>();

		Test test = analysis.getTest();
		SampleItem sampleItem = analysis.getSampleItem();
		List<Result> resultList = resultDAO.getResultsByAnalysis(analysis);

		ResultInventory testKit = null;

		String techSignature = "";
		String techSignatureId = "";
		List<Note> notes = null;

		if (resultList == null) {
			return testResultList;
		}

		// For historical reasons we add a null member to the collection if it
		// is empty
		// this should be refactored.
		// The result list are results associated with the analysis, if there is
		// none we want
		// to present the user with a blank one
		if (resultList.isEmpty()) {
			resultList.add(null);
		}

		boolean multiSelectionResult = false;
		for (Result result : resultList) {
			// If the parentResult has a value then this result was handled with
			// the parent
			if (result != null && result.getParentResult() != null) {
				continue;
			}

			if (result != null) {
				notes = getNotesForAnalysis(result, resultList);
				if (useTechSignature) {
					List<ResultSignature> signatures = resultSignatureDAO.getResultSignaturesByResults(resultList);

					for (ResultSignature signature : signatures) {
						//we no longer use supervisor signature but there may be some in db
						if (!signature.getIsSupervisor()) {
							techSignature = signature.getNonUserName();
							techSignatureId = signature.getId();
						}
					}
				}

				testKit = getInventoryForResult(result);

				multiSelectionResult = "M".equals(result.getResultType());
			}

			ResultLimit resultLimit = getResultLimitForTestAndPatient(test, currPatient);

			String initialConditions = getInitialSampleConditionString(sampleItem);

			TestResultItem resultItem = createTestResultItem(resultLimit, analysis, test, testKit, notes, sampleItem.getSortOrder(), result,
					sampleItem.getSample().getAccessionNumber(), patientName, patientInfo, techSignature, techSignatureId,
					multiSelectionResult, initialConditions,  TypeOfSampleUtil.getTypeOfSampleNameForId(sampleItem.getTypeOfSampleId()));

			testResultList.add(resultItem);

			if (multiSelectionResult) {
				break;
			}
		}

		return testResultList;
	}

	protected List<Note> getNotesForAnalysis(Result result, List<Result> resultList) {
		if ("M".equals(result.getResultType()) && resultList.size() > 1) {
			List<Note> notes = new ArrayList<Note>();
			for (Result resultItem : resultList) {
				notes.addAll(NoteUtil.getNotesForObjectAndTable(resultItem.getId(), RESULT_REFERENCE_TABLE_ID));
			}

			return notes;
		} else {
			return NoteUtil.getNotesForObjectAndTable(result.getId(), RESULT_REFERENCE_TABLE_ID);
		}
	}

	private String getInitialSampleConditionString(SampleItem sampleItem) {
		if (useInitialSampleCondition) {
			List<ObservationHistory> observationList = observationHistoryDAO.getObservationHistoriesBySampleItemId(sampleItem.getId());
			StringBuffer conditions = new StringBuffer();

			for (ObservationHistory observation : observationList) {
				Dictionary dictionary = dictionaryDAO.getDictionaryById(observation.getValue());
				if (dictionary != null) {
					conditions.append(dictionary.getLocalizedName());
					conditions.append(", ");
				}
			}

			if (conditions.length() > 2) {
				return conditions.substring(0, conditions.length() - 2);
			}
		}

		return null;
	}

	private ResultInventory getInventoryForResult(Result result) throws LIMSRuntimeException {
		List<ResultInventory> inventoryList = resultInventoryDAO.getResultInventorysByResult(result);

		return inventoryList.size() > 0 ? inventoryList.get(0) : null;
	}

	private String getTestResultType(List<TestResult> testResults) {
		String testResultType = "N";

		if (testResults != null && testResults.size() > 0) {
			testResultType = testResults.get(0).getTestResultType();
		}

		return testResultType;
	}

	private Patient getPatientForSampleItem(SampleItem sampleItem) {
		SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
		return sampleHumanDAO.getPatientForSample(sampleItem.getSample());
	}

	private List<TestResultItem> getGroupedTestsForSamples() {

		List<TestResultItem> testList = new ArrayList<TestResultItem>();

		TestResultItem[] tests = getSortedTestsFromSamples();

		String currentAccessionNumber = "";

		for (int i = 0; i < tests.length; i++) {
			if (!currentAccessionNumber.equals(tests[i].getAccessionNumber())) {

				TestResultItem seperatorItem = new TestResultItem();
				seperatorItem.setIsGroupSeparator(true);
				seperatorItem.setAccessionNumber(tests[i].getAccessionNumber());
				seperatorItem.setReceivedDate(tests[i].getReceivedDate());
				testList.add(seperatorItem);

				currentAccessionNumber = tests[i].getAccessionNumber();
				reflexGroup++;

			}

			testList.add(tests[i]);
		}

		return testList;
	}

	private TestResultItem[] getSortedTestsFromSamples() {

		List<TestResultItem> testList = new ArrayList<TestResultItem>();

		for (Sample sample : samples) {
			currSample = sample;
			List<SampleItem> sampleItems = getSampleItemsForSample(sample);

			for (SampleItem item : sampleItems) {
				List<Analysis> analysisList = getAnalysisForSampleItem(item);

				for (Analysis analysis : analysisList) {

					List<TestResultItem> selectedItemList = getTestResultItemFromAnalysis(analysis, NO_PATIENT_NAME, NO_PATIENT_INFO);

					for (TestResultItem selectedItem : selectedItemList) {
						testList.add(selectedItem);
					}
				}
			}
		}

		reverseSortByAccessionAndSequence(testList);
		setSampleGroupingNumbers(testList);
		addUserSelectionReflexes(testList);

		TestResultItem[] testArray = new TestResultItem[testList.size()];
		testList.toArray(testArray);

		return testArray;
	}

	private void addUserSelectionReflexes(List<TestResultItem> testList) {
		TestReflexUtil reflexUtil = new TestReflexUtil();

		Map<String, TestResultItem> groupedSibReflexMapping = new HashMap<String, TestResultItem>();

		for (TestResultItem resultItem : testList) {
			//N.B. showSampleDetails should be renamed.  It means that it is the first result for that group of accession numbers
			if (resultItem.isShowSampleDetails()) {
				groupedSibReflexMapping = new HashMap<String, TestResultItem>();
				reflexGroup++;
			}

			if (resultItem.isReflexGroup()) {
				resultItem.setReflexParentGroup(reflexGroup);
			}

			List<TestReflex> reflexList = reflexUtil.getPossibleUserChoiceTestReflexsForTest(resultItem.getTestId());
			resultItem.setUserChoiceReflex(reflexList.size() > 0);

			boolean possibleSibs = !groupedSibReflexMapping.isEmpty();

			for (TestReflex testReflex : reflexList) {
				if (!GenericValidator.isBlankOrNull(testReflex.getSiblingReflexId())) {
					if (possibleSibs) {
						TestResultItem sibTestResultItem = groupedSibReflexMapping.get(testReflex.getSiblingReflexId());
						if (sibTestResultItem != null) {
							Random r = new Random();
							String key1 = Long.toString(Math.abs(r.nextLong()), 36);
							String key2 = Long.toString(Math.abs(r.nextLong()), 36);

							sibTestResultItem.setThisReflexKey(key1);
							sibTestResultItem.setSiblingReflexKey(key2);

							resultItem.setThisReflexKey(key2);
							resultItem.setSiblingReflexKey(key1);

							break;
						}
					}
					groupedSibReflexMapping.put(testReflex.getId(), resultItem);
				}

			}

		}

	}

	public ResultLimit getResultLimitForTestAndPatient(Test test, Patient patient) {
		currPatient = patient;
		List<ResultLimit> resultLimits = getAllResultLimitsForTest(test);

		if (resultLimits == null || resultLimits.isEmpty()) {
			return null;
		} else if (patient == null) {
			return defaultResultLimit(resultLimits);
		} else if (patient.getBirthDate() == null && GenericValidator.isBlankOrNull(patient.getGender())) {
			return defaultResultLimit(resultLimits);
		} else if (GenericValidator.isBlankOrNull(patient.getGender())) {
			return ageBasedResultLimit(resultLimits);
		} else if (patient.getBirthDate() == null) {
			return genderBasedResultLimit(resultLimits);
		} else {
			return ageAndGenderBasedResultLimit(resultLimits);
		}
	}

	private ResultLimit defaultResultLimit(List<ResultLimit> resultLimits) {

		for (ResultLimit limit : resultLimits) {
			if (GenericValidator.isBlankOrNull(limit.getGender()) && limit.ageLimitsAreDefault()) {
				return limit;
			}
		}
		return new ResultLimit();
	}

	private ResultLimit ageBasedResultLimit(List<ResultLimit> resultLimits) {

		ResultLimit resultLimit = null;

		// First we look for a limit with no gender
		for (ResultLimit limit : resultLimits) {
			if (GenericValidator.isBlankOrNull(limit.getGender()) && !limit.ageLimitsAreDefault()
					&& getCurrPatientAge() >= limit.getMinAge() && getCurrPatientAge() <= limit.getMaxAge()) {

				resultLimit = limit;
				break;
			}
		}

		// if none is found then drop the no gender requirement
		if (resultLimit == null) {
			for (ResultLimit limit : resultLimits) {
				if (!limit.ageLimitsAreDefault() && getCurrPatientAge() >= limit.getMinAge() && getCurrPatientAge() <= limit.getMaxAge()) {

					resultLimit = limit;
					break;
				}
			}
		}

		return resultLimit == null ? defaultResultLimit(resultLimits) : resultLimit;
	}

	private ResultLimit genderBasedResultLimit(List<ResultLimit> resultLimits) {

		ResultLimit resultLimit = null;

		// First we look for a limit with no age
		for (ResultLimit limit : resultLimits) {
			if (limit.ageLimitsAreDefault() && currPatient.getGender().equals(limit.getGender())) {

				resultLimit = limit;
				break;
			}
		}

		// drop the age limit
		if (resultLimit == null) {
			for (ResultLimit limit : resultLimits) {
				if (currPatient.getGender().equals(limit.getGender())) {
					resultLimit = limit;
					break;
				}
			}
		}
		return resultLimit == null ? defaultResultLimit(resultLimits) : resultLimit;
	}

	/*
	 * We only get here if patient has age and gender
	 */
	private ResultLimit ageAndGenderBasedResultLimit(List<ResultLimit> resultLimits) {

		ResultLimit resultLimit = null;

		List<ResultLimit> fullySpecifiedLimits = new ArrayList<ResultLimit>();
		// first age and gender matter
		for (ResultLimit limit : resultLimits) {
			if (currPatient.getGender().equals(limit.getGender()) && !limit.ageLimitsAreDefault()) {

				// if fully qualified don't retest for only part of the
				// qualification
				fullySpecifiedLimits.add(limit);

				if (getCurrPatientAge() >= limit.getMinAge() && getCurrPatientAge() <= limit.getMaxAge()) {

					resultLimit = limit;
					break;
				}
			}
		}

		resultLimits.removeAll(fullySpecifiedLimits);

		// second only age matters
		if (resultLimit == null) {
			for (ResultLimit limit : resultLimits) {
				if (!limit.ageLimitsAreDefault() && getCurrPatientAge() >= limit.getMinAge() && getCurrPatientAge() <= limit.getMaxAge()) {

					resultLimit = limit;
					break;
				}
			}
		}

		// third only gender matters
		return resultLimit == null ? genderBasedResultLimit(resultLimits) : resultLimit;
	}

	@SuppressWarnings("unchecked")
	private List<ResultLimit> getAllResultLimitsForTest(Test test) {
		ResultLimitDAO resultLimitDAO = DAOImplFactory.getInstance().getResultLimitsDAOImpl();
		return resultLimitDAO.getAllResultLimitsForTest(test);
	}

	private List<SampleItem> getSampleItemsForSample(Sample sample) {
		SampleItemDAO sampleItemDAO = DAOImplFactory.getInstance().getSampleItemDAOImpl();
		return sampleItemDAO.getSampleItemsBySampleId(sample.getId());
	}

	private List<Analysis> getAnalysisForSampleItem(SampleItem item) {
		return analysisDAO.getAnalysesBySampleItemsExcludingByStatusIds(item, excludedAnalysisStatus);
	}

	private TestResultItem createTestResultItem(ResultLimit resultLimit, Analysis analysis, Test test, ResultInventory testKit, List<Note> notes,
			String sequenceNumber, Result result, String accessionNumber, String patientName, String patientInfo, String techSignature,
			String techSignatureId,  boolean multiSelectionResult, String initialSampleConditions, String sampleType) {

		String receivedDate = currSample == null ? getCurrentDate() : currSample.getReceivedDateForDisplay();
		String testMethodName = test.getMethod() != null ? test.getMethod().getMethodName() : null;
		List<TestResult> testResults = getPossibleResultsForTest(test);

		String testKitId = null;
		String testKitInventoryId = null;
		Result testKitResult = new Result();
		boolean testKitInactive = false;

		if (testKit != null) {
			testKitId = testKit.getId();
			testKitInventoryId = testKit.getInventoryLocationId();
			testKitResult.setId(testKit.getResultId());
			resultDAO.getData(testKitResult);
			testKitInactive = kitNotInActiveKitList(testKitInventoryId);
		}

		String displayTestName = test.getDescription();

		boolean isConclusion = false;
		boolean isCD4Conclusion = false;

		String multiSelectResults = null;
		if (multiSelectionResult) {
			multiSelectResults = createMultiSelectResultsString(analysis);
		}

		if (result != null && result.getAnalyte() != null) {
			isConclusion = result.getAnalyte().getId().equals(ANALYTE_CONCLUSION_ID);
			isCD4Conclusion = result.getAnalyte().getId().equals(ANALYTE_CD4_CNT_CONCLUSION_ID);

			if (isConclusion) {
				displayTestName = StringUtil.getMessageForKey("result.conclusion");
			} else if (isCD4Conclusion) {
				displayTestName = StringUtil.getMessageForKey("result.conclusion.cd4");
			}
		}

		String referralId = null;
		String referralReasonId = null;
		boolean referralCanceled = false;
		if (supportReferrals) {
			if (analysis != null) {
				Referral referral = referralDAO.getReferralByAnalysisId(analysis.getId());
				if (referral != null) {
					referralCanceled = referral.isCanceled();
					referralId = referral.getId();
					if (!referral.isCanceled()) {
						referralReasonId = referral.getReferralReasonId();
					}
				}
			}
		}

		String uom = "";
		if (!isCD4Conclusion) {
			if (test != null && test.getUnitOfMeasure() != null) {
				uom = test.getUnitOfMeasure().getName();
			}
		}

		String testDate = GenericValidator.isBlankOrNull(analysis.getCompletedDateForDisplay()) ? getCurrentDate()
				: analysis.getCompletedDateForDisplay();

		TestResultItem testItem = new TestResultItem();

		testItem.setAccessionNumber(accessionNumber);
		testItem.setAnalysisId(analysis.getId());
		testItem.setSequenceNumber(sequenceNumber);
		testItem.setReceivedDate(receivedDate);
		testItem.setTestName(displayTestName);
		testItem.setTestId(test.getId());
		setResultLimitDependencies(resultLimit, testItem);
		testItem.setPatientName(patientName);
		testItem.setPatientInfo(patientInfo);
		testItem.setReportable("Y".equals(test.getIsReportable()));
		testItem.setUnitsOfMeasure(uom);
		testItem.setTestDate(testDate);
		testItem.setResultDisplayType(getDisplayTypeForTestMethod(testMethodName));
		testItem.setTestMethod(testMethodName);
		testItem.setAnalysisMethod(analysis.getAnalysisType());
		testItem.setResult(result);
		testItem.setResultValue(getFormatedResultValue(result));
		testItem.setMultiSelectResultValues(multiSelectResults);
		testItem.setAnalysisStatusId(analysis.getStatusId());
		//setDictionaryResults must come after setResultType, it may override it
		testItem.setResultType(getTestResultType(testResults));
		setDictionaryResults(testItem, isConclusion, result, testResults);

		testItem.setTechnician(techSignature);
		testItem.setTechnicianSignatureId(techSignatureId);
		testItem.setTestKitId(testKitId);
		testItem.setTestKitInventoryId(testKitInventoryId);
		testItem.setTestKitInactive(testKitInactive);
		testItem.setReflexStep((isConclusion || isCD4Conclusion) ? 0 : calculateReflexStep(analysis));
		testItem.setReadOnly(isLockCurrentResults() && result != null && result.getId() != null);
		testItem.setReferralId(referralId);
		testItem.setReferredOut(!GenericValidator.isBlankOrNull(referralId) && !referralCanceled);
		testItem.setReferralReasonId(referralReasonId);
		testItem.setReferralCanceled(referralCanceled);
		testItem.setInitialSampleCondition(initialSampleConditions);
		testItem.setSampleType(sampleType);
		testItem.setTestSortOrder(test.getSortOrder());
		testItem.setFailedValidation(statusRules.hasFailedValidation(analysis.getStatusId()));
		if (useCurrentUserAsTechDefault && GenericValidator.isBlankOrNull(testItem.getTechnician())) {
			testItem.setTechnician(currentUserName);
		}
		testItem.setReflexGroup(analysis.getTriggeredReflex());
		testItem.setChildReflex(analysis.getTriggeredReflex() && isConclusion(result, analysis));
		testItem.setUserChoicePending(false);
		addNotes(notes, testItem);

		testItem.setDisplayResultAsLog(hasLogValue(analysis, testItem.getResultValue()));
		testItem.setNonconforming(QAService.isAnalysisParentNonConforming(analysis));
		return testItem;
	}

	private void setResultLimitDependencies(ResultLimit resultLimit, TestResultItem testItem){
		if( resultLimit != null){
			testItem.setResultLimitId(resultLimit.getId());
			testItem.setLowerNormalRange(resultLimit.getLowNormal() == Double.NEGATIVE_INFINITY ? 0 : resultLimit.getLowNormal());
			testItem.setUpperNormalRange(resultLimit.getHighNormal() == Double.POSITIVE_INFINITY ? 0 : resultLimit.getHighNormal());
			testItem.setLowerAbnormalRange(resultLimit.getLowValid() == Double.NEGATIVE_INFINITY ? 0 : resultLimit.getLowValid());
			testItem.setUpperAbnormalRange(resultLimit.getHighValid() == Double.POSITIVE_INFINITY ? 0 : resultLimit.getHighValid());
			testItem.setValid(getIsValid(testItem.getResultValue(), resultLimit));
			testItem.setNormal(getIsNormal(testItem.getResultValue(), resultLimit));
		}
	}

	private void setDictionaryResults(TestResultItem testItem, boolean isConclusion, Result result,
			List<TestResult> testResults) {
		if (isConclusion) {
			testItem.setDictionaryResults(getAnyDictonaryValues(result));
		} else {
			setDictionaryResults(testItem, testResults, result);
		}
	}

	private void setDictionaryResults(TestResultItem testItem, List<TestResult> testResults, Result result) {

		List<IdValuePair> values = null;
		Dictionary dictionary;

		if (testResults != null && !testResults.isEmpty() && isDictionaryVarientType(testResults.get(0).getTestResultType())) {
			values = new ArrayList<IdValuePair>();

			Collections.sort(testResults, new Comparator<TestResult>() {
				@Override
				public int compare(TestResult o1, TestResult o2) {
					if (GenericValidator.isBlankOrNull(o1.getSortOrder())
							|| GenericValidator.isBlankOrNull(o2.getSortOrder())) {
						return 1;
					}

					return Integer.parseInt(o1.getSortOrder()) - Integer.parseInt(o2.getSortOrder());
				}
			});
			
			String qualifiedDictionaryIds = "";
			for (TestResult testResult : testResults) {
				if (isDictionaryVarientType(testResult.getTestResultType())) {
					dictionary = new Dictionary();
					dictionary.setId(testResult.getValue());
					dictionaryDAO.getData(dictionary);
					String displayValue = dictionary.getLocalizedName();

					if ("unknown".equals(displayValue)) {
						displayValue = GenericValidator.isBlankOrNull(dictionary.getLocalAbbreviation()) ? dictionary
								.getDictEntry() : dictionary.getLocalAbbreviation();
					}
					values.add(new IdValuePair(testResult.getValue(), displayValue));
					if (testResult.getTestResultType().equals("Q")) {
						if( qualifiedDictionaryIds != ""){
							qualifiedDictionaryIds += ",";
						}
						qualifiedDictionaryIds += testResult.getValue();
						setQualifedValues(testItem, testResult, result);
					}
				}
			}
			
			if( qualifiedDictionaryIds != ""){
				testItem.setQualifiedDictonaryId("[" + qualifiedDictionaryIds + "]");
			}
		}
		if( !GenericValidator.isBlankOrNull(testItem.getQualifiedResultValue())){
			testItem.setResultType("Q");
		}

		testItem.setDictionaryResults(values);
	}

	private void setQualifedValues(TestResultItem testItem, TestResult testResult, Result result) {
		if (result != null) {
			List<Result> results = resultDAO.getChildResults(result.getId());
			if (!results.isEmpty()) {
				Result childResult = results.get(0);
				testItem.setQualifiedResultId(childResult.getId());
				testItem.setQualifiedResultValue(childResult.getValue());
			}
		}
	}

	private String getFormatedResultValue(Result result) {
		if (result == null) {
			return "";
		}

		if ("A".equals(result.getResultType()) && !GenericValidator.isBlankOrNull(result.getValue())) {
			return result.getValue().split("\\(")[0].trim();
		}

		return result.getValue();
	}

	private boolean hasLogValue(Analysis analysis, String resultValue) {
		//TO-DO refactor
		if ( TestIdentityService.isTestNumericViralLoad(analysis.getTest())){
//			if (GenericValidator.isBlankOrNull(resultValue)) {
//				return true;
//			}
//			try {
//				Double.parseDouble(resultValue);
//				return true;
//			} catch (NumberFormatException e) {
//				return false;
//			}
			
			return true;
		}

		return false;
	}

	private boolean isConclusion(Result testResult, Analysis analysis) {
		List<Result> results = resultDAO.getResultsByAnalysis(analysis);
		if (results.size() == 1) {
			return false;
		}

		Long testResultId = Long.parseLong(testResult.getId());
		// This based on the fact that the conclusion is always added
		// after the shared result so if there is a result with a larger id
		// then this is not a conclusion
		for (Result result : results) {
			if (Long.parseLong(result.getId()) > testResultId) {
				return false;
			}
		}

		return true;
	}

	protected void addNotes(List<Note> notes, TestResultItem testItem) {
		if (!(notes == null || notes.isEmpty())) {
			Collections.sort(notes, new Comparator<Note>() {

				@Override
				public int compare(Note o1, Note o2) {
					return Integer.parseInt(o1.getId()) - Integer.parseInt(o2.getId());
				}
			});
			StringBuilder noteBuilder = new StringBuilder();
			for (Note note : notes) {
				noteBuilder.append(note.getText());
				noteBuilder.append("<br/>");
			}

			noteBuilder.setLength(noteBuilder.lastIndexOf("<br/>"));

			testItem.setPastNotes(noteBuilder.toString());
		}
	}

	private String createMultiSelectResultsString(Analysis analysis) {
		String multiSelectResults;
		List<Result> existingResults = resultDAO.getResultsByAnalysis(analysis);
		StringBuilder multiSelectBuffer = new StringBuilder();
		for (Result existingResult : existingResults) {
			multiSelectBuffer.append(existingResult.getValue());
			multiSelectBuffer.append(',');
		}

		multiSelectBuffer.setLength(multiSelectBuffer.length() - 1); // remove
		// last
		// ','
		multiSelectResults = multiSelectBuffer.toString();
		return multiSelectResults;
	}

	private List<IdValuePair> getAnyDictonaryValues(Result result) {
		List<IdValuePair> values = null;

		if (result != null && isDictionaryVarientType(result.getResultType())) {
			values = new ArrayList<IdValuePair>();

			Dictionary dictionaryValue = new Dictionary();
			dictionaryValue.setId(result.getValue());
			dictionaryDAO.getData(dictionaryValue);

			List<Dictionary> dictionaryList = dictionaryDAO.getDictionaryEntriesByCategoryId(dictionaryValue.getDictionaryCategory()
					.getId());

			for (Dictionary dictionary : dictionaryList) {
				String displayValue = dictionary.getLocalizedName();

				if ("unknown".equals(displayValue)) {
					displayValue = GenericValidator.isBlankOrNull(dictionary.getLocalAbbreviation()) ? dictionary.getDictEntry()
							: dictionary.getLocalAbbreviation();
				}
				values.add(new IdValuePair(dictionary.getId(), displayValue));
			}
		}

		return values;

	}

	private boolean isDictionaryVarientType(String testResultType) {
		return "D".equals(testResultType) || "M".equals(testResultType) || "Q".equals(testResultType);
	}

	@SuppressWarnings("unchecked")
	private List<TestResult> getPossibleResultsForTest(Test test) {
		return testResultDAO.getAllTestResultsPerTest(test);
	}

	private int calculateReflexStep(Analysis analysis) {
		int step = 0;

		Analysis parentAnalysis = analysis.getParentAnalysis();

		if (parentAnalysis != null) {
			step = 1;

			do {
				step++;
				parentAnalysis = parentAnalysis.getParentAnalysis();
			} while (parentAnalysis != null);
		} else {
			if (analysis.getTriggeredReflex()) {
				step = 1;
			}
		}

		return step;
	}

	private boolean getIsValid(String resultValue, ResultLimit resultLimit) {
		boolean valid = true;

		if (!GenericValidator.isBlankOrNull(resultValue) && resultLimit != null) {
			try {
				double value = Double.valueOf(resultValue);

				valid = value >= resultLimit.getLowValid() && value <= resultLimit.getHighValid();

			} catch (NumberFormatException nfe) {
				// no-op
			}
		}

		return valid;
	}

	private boolean getIsNormal(String resultValue, ResultLimit resultLimit) {
		boolean normal = true;

		if (!GenericValidator.isBlankOrNull(resultValue) && resultLimit != null) {
			try {
				double value = Double.valueOf(resultValue);

				normal = value >= resultLimit.getLowNormal() && value <= resultLimit.getHighNormal();

			} catch (NumberFormatException nfe) {
				// no-op
			}
		}

		return normal;
	}

	private boolean kitNotInActiveKitList(String testKitId) {
		List<InventoryKitItem> activeKits = getActiveKits();

		for (InventoryKitItem kit : activeKits) {
			// The locationID is the reference held in the DB
			if (testKitId.equals(kit.getInventoryLocationId())) {
				return false;
			}
		}

		return true;
	}

	private double getCurrPatientAge() {
		if (currPatientAge == INVALID_PATIENT_AGE && currPatient != null && currPatient.getBirthDate() != null) {

			Calendar dob = Calendar.getInstance();
			dob.setTime(currPatient.getBirthDate());

			currPatientAge = DateUtil.getAgeInMonths(currPatient.getBirthDate(), new Date());
		}

		return currPatientAge;
	}

	private String getCurrentDate() {
		if (GenericValidator.isBlankOrNull(currentDate)) {
			currentDate = DateUtil.getCurrentDateAsText();
		}

		return currentDate;
	}

	@SuppressWarnings("unchecked")
	private void createNameToIdMaps() throws LIMSRuntimeException {
		SystemUserDAO systemUserDAO = new SystemUserDAOImpl();
		PermissionAgentModuleDAO symDAO = PermissionAgentFactory.getPermissionAgentImpl();

		techLoginNameIdMap = new HashMap<String, String>();
		supervisorLoginNameIdMap = new HashMap<String, String>();
		userIdLoginNameMap = new HashMap<String, String>();

		List<SystemUser> users = systemUserDAO.getAllSystemUsers();

		for (SystemUser user : users) {
			techLoginNameIdMap.put(user.getLoginName(), user.getId());
			userIdLoginNameMap.put(user.getId(), user.getLoginName());

			if (symDAO.isAgentAllowedAccordingToName(user.getId(), "ResultSupervisor")) {
				supervisorLoginNameIdMap.put(user.getLoginName(), user.getId());
			}
		}
	}

	public String getIdForSupervisorsName(String supervisor) {

		if (supervisorLoginNameIdMap == null) {
			createNameToIdMaps();
		}

		return supervisorLoginNameIdMap.get(supervisor);
	}

	private ResultDisplayType getDisplayTypeForTestMethod(String methodName) {
		ResultDisplayType resultType = ResultDisplayType.TEXT;

		if (HIV_TYPE.equals(methodName)) {
			resultType = ResultDisplayType.HIV;
			inventoryNeeded = true;
		} else if (SYPHILIS_TYPE.equals(methodName)) {
			resultType = ResultDisplayType.SYPHILIS;
			inventoryNeeded = true;
		}

		return resultType;
	}

	public boolean inventoryNeeded() {
		return inventoryNeeded;
	}

	public void addExcludedAnalysisStatus(AnalysisStatus status) {
		excludedAnalysisStatus.add(Integer.parseInt(StatusService.getInstance().getStatusID(status)));
	}

	public void addIncludedSampleStatus(OrderStatus status) {
		sampleStatusList.add(Integer.parseInt(StatusService.getInstance().getStatusID(status)));
	}

	public void addIncludedAnalysisStatus(AnalysisStatus status) {
		analysisStatusList.add(Integer.parseInt(StatusService.getInstance().getStatusID(status)));
	}

	private List<InventoryKitItem> getActiveKits() {
		if (activeKits == null) {
			InventoryUtility inventoryUtil = new InventoryUtility();
			activeKits = inventoryUtil.getExistingActiveInventory();
		}

		return activeKits;
	}

	public static String getResultReferenceTableId() {
		return RESULT_REFERENCE_TABLE_ID;
	}

	public static String getAnalyzerResultsReferenceTableId() {
		return ANALYZER_RESULT_REFERENCE_TABLE_ID;
	}

	public void setLockCurrentResults(boolean lockCurrentResults) {
		this.lockCurrentResults = lockCurrentResults;
	}

	public boolean isLockCurrentResults() {
		return lockCurrentResults;
	}

}
