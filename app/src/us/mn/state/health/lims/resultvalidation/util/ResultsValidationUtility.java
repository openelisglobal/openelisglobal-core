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
package us.mn.state.health.lims.resultvalidation.util;

import org.apache.commons.validator.GenericValidator;
import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.analyte.dao.AnalyteDAO;
import us.mn.state.health.lims.analyte.daoimpl.AnalyteDAOImpl;
import us.mn.state.health.lims.analyte.valueholder.Analyte;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.services.QAService;
import us.mn.state.health.lims.common.services.StatusService;
import us.mn.state.health.lims.common.services.StatusService.AnalysisStatus;
import us.mn.state.health.lims.common.services.StatusService.RecordStatus;
import us.mn.state.health.lims.common.services.TestIdentityService;
import us.mn.state.health.lims.common.tools.StopWatch;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.ConfigurationProperties.Property;
import us.mn.state.health.lims.common.util.IdValuePair;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.note.util.NoteUtil;
import us.mn.state.health.lims.note.valueholder.Note;
import us.mn.state.health.lims.observationhistory.dao.ObservationHistoryDAO;
import us.mn.state.health.lims.observationhistory.daoimpl.ObservationHistoryDAOImpl;
import us.mn.state.health.lims.observationhistory.valueholder.ObservationHistory;
import us.mn.state.health.lims.observationhistorytype.dao.ObservationHistoryTypeDAO;
import us.mn.state.health.lims.observationhistorytype.daoImpl.ObservationHistoryTypeDAOImpl;
import us.mn.state.health.lims.observationhistorytype.valueholder.ObservationHistoryType;
import us.mn.state.health.lims.referencetables.dao.ReferenceTablesDAO;
import us.mn.state.health.lims.referencetables.daoimpl.ReferenceTablesDAOImpl;
import us.mn.state.health.lims.referencetables.valueholder.ReferenceTables;
import us.mn.state.health.lims.result.action.util.ResultsLoadUtility;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.resultvalidation.action.util.ResultValidationItem;
import us.mn.state.health.lims.resultvalidation.bean.AnalysisItem;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.statusofsample.util.StatusRules;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.dao.TestSectionDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.daoimpl.TestSectionDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.test.valueholder.TestSection;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;

import java.util.*;

public class ResultsValidationUtility {

	public enum TestSectionType {
		UNKNOWN, IMMUNOLOGY, HEMATOLOGY, BIOCHEMISTRY, SEROLOGY, VIROLOGY
	}

//	private static String VIRAL_LOAD_ID = "";
	private static String ANALYTE_CD4_CT_GENERATED_ID;

	private static String CONCLUSION_ID;

	private final DictionaryDAO dictionaryDAO = new DictionaryDAOImpl();
	private final AnalysisDAO analysisDAO = new AnalysisDAOImpl();
	private final TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
	private final ResultDAO resultDAO = new ResultDAOImpl();
	private final TestResultDAO testResultDAO = new TestResultDAOImpl();
	private final TestDAO testDAO = new TestDAOImpl();
	private final SampleDAO sampleDAO = new SampleDAOImpl();
	private final ObservationHistoryDAO observationHistoryDAO = new ObservationHistoryDAOImpl();
	private static String RESULT_TABLE_ID;
	private static String SAMPLE_STATUS_OBSERVATION_HISTORY_TYPE_ID;
	private static String CD4_COUNT_SORT_NUMBER;

	private ResultsLoadUtility resultsLoadUtility = new ResultsLoadUtility();
	private static List<Integer> notValidStatus = new ArrayList<Integer>();
	private Map<String, String> testIdToUnits = new HashMap<String, String>();
	private Map<String, Boolean> accessionToValidMap;
	private String totalTestName = "";

	static {
		notValidStatus.add(Integer.parseInt(StatusService.getInstance().getStatusID(AnalysisStatus.Finalized)));
		notValidStatus.add(Integer.parseInt(StatusService.getInstance().getStatusID(AnalysisStatus.TechnicalRejected)));
		AnalyteDAO analyteDAO = new AnalyteDAOImpl();
		Analyte analyte = new Analyte();
		analyte.setAnalyteName("Conclusion");
		analyte = analyteDAO.getAnalyteByName(analyte, false);
		CONCLUSION_ID = analyte.getId();
		analyte.setAnalyteName("generated CD4 Count");
		analyte = analyteDAO.getAnalyteByName(analyte, false);
		ANALYTE_CD4_CT_GENERATED_ID = analyte == null ? "" : analyte.getId();

		TestDAO testDAO = new TestDAOImpl();
		
		Test test = testDAO.getTestByName("CD4 absolute count");
		if( test != null){
			CD4_COUNT_SORT_NUMBER = test.getSortOrder();
		}
		
		ReferenceTablesDAO refTablesDAO = new ReferenceTablesDAOImpl();
		ReferenceTables refTable = new ReferenceTables();
		refTable.setTableName("RESULT");
		RESULT_TABLE_ID = refTablesDAO.getReferenceTableByName(refTable).getId();

		ObservationHistoryTypeDAO ohTypeDAO = new ObservationHistoryTypeDAOImpl();
		ObservationHistoryType oht = ohTypeDAO.getByName("SampleRecordStatus");
		if( oht != null){
			SAMPLE_STATUS_OBSERVATION_HISTORY_TYPE_ID = oht.getId();
		}
	}

	StopWatch sw;

	public List<AnalysisItem> getResultValidationList(String testSectionName, String testName, List<Integer> statusList) {
		accessionToValidMap = new HashMap<String, Boolean>();
		sw = new StopWatch();
		sw.disable(true);

		List<ResultValidationItem> testList = new ArrayList<ResultValidationItem>();
		List<AnalysisItem> resultList = new ArrayList<AnalysisItem>();

		if (!(GenericValidator.isBlankOrNull(testSectionName) || testSectionName.equals("0"))) {
			sw.start("Result Validation " + testSectionName);
			String testSectionId;

			// unique serology department format for RetroCI
			if (testSectionName.equals("Serology")) {
				testSectionId = getTestSectionId(testSectionName);
				testList = getUnValidatedElisaResultItemsInTestSection(testSectionId);

				Collections.sort(testList, new Comparator<ResultValidationItem>() {
					@Override
					public int compare(ResultValidationItem o1, ResultValidationItem o2) {
						return o1.getAccessionNumber().compareTo(o2.getAccessionNumber());
					}

				});
				resultList = testResultListToELISAAnalysisList(testList, statusList);

				// default department format
			} else {

				// unique virology department format
				if ((!GenericValidator.isBlankOrNull(testName) && testSectionName.equals("Virology"))) {
					if (testName.equals("Genotyping")) {
						testName = "Génotypage";
					}

					testList.addAll(getUnValidatedTestResultItemsByTest(testName, statusList));

				} else {
					testSectionId = getTestSectionId(testSectionName);
					testList = getUnValidatedTestResultItemsInTestSection(testSectionId, statusList);
					// Immunology and Hematology are together
					//Not sure if this is the correct way to judge this business rule
					if (ConfigurationProperties.getInstance().isPropertyValueEqual(Property.configurationName, "CI RetroCI") &&
							testSectionName.equals("Immunology")) {
						sw.setMark("Immuno time");
						// add Hematology tests to list
						totalTestName = StringUtil.getMessageForKey("test.validation.total.percent");
						List<ResultValidationItem> hematologyResults = getUnValidatedTestResultItemsInTestSection(getTestSectionId("Hematology"), statusList);
						addPrecentageResultsTotal(hematologyResults);
						testList.addAll(hematologyResults);
						sw.setMark("Hemo time");
					}
				}

				resultList = testResultListToAnalysisItemList(testList);
				sw.setMark("conversion done for " + resultList.size());
			}

			sortByAccessionNumberAndOrder(resultList);
			sw.setMark("sorting done");
			setGroupingNumbers(resultList);
			sw.setMark("Grouping done");
		}

		sw.setMark("end");
		sw.report();

		return resultList;

	}

	private void addPrecentageResultsTotal(List<ResultValidationItem> hematologyResults) {
		Map<String, ResultValidationItem> accessionToTotalMap = new HashMap<String, ResultValidationItem>();

		for (ResultValidationItem resultItem : hematologyResults) {
			if (isItemToBeTotaled(resultItem)) {
				ResultValidationItem totalItem = accessionToTotalMap.get(resultItem.getAccessionNumber());

				if (totalItem == null) {
					totalItem = createTotalItem(resultItem);
					accessionToTotalMap.put(resultItem.getAccessionNumber(), totalItem);
				}

				totalItem.getResult().setValue(totalValues(totalItem, resultItem));
				totalItem.setTestSortNumber(greaterSortNumber(totalItem, resultItem));
			}
		}

		roundTotalItemValue(accessionToTotalMap);

		hematologyResults.addAll(accessionToTotalMap.values());
	}

	private String greaterSortNumber(ResultValidationItem totalItem, ResultValidationItem resultItem) {
		return String.valueOf(Math.max(Integer.parseInt(totalItem.getTestSortNumber()), Integer.parseInt(resultItem.getTestSortNumber())));
	}

	private boolean isItemToBeTotaled(ResultValidationItem resultItem) {
		String name = resultItem.getTestName();
		// This is totally un-wholesome it is to specific to RetroCI
		if (name.equals("Lymph %")) {
			Result result = resultDAO.getResultById(resultItem.getResultId());

			if (result == null || result.getAnalyte() == null) {
				return true;
			} else {
				return !ANALYTE_CD4_CT_GENERATED_ID.equals(result.getAnalyte().getId());
			}

		} else {
			return name.equals("Neut %") || name.equals("Mono %") || name.equals("Eo %") || name.equals("Baso %");
		}
	}

	private ResultValidationItem createTotalItem(ResultValidationItem resultItem) {
		ResultValidationItem item = new ResultValidationItem();

		item.setTestName(totalTestName);
		item.setUnitsOfMeasure("%");
		item.setAccessionNumber(resultItem.getAccessionNumber());
		item.setResult(new Result());
		item.getResult().setValue("0");
		item.setResultType("N");
		item.setTestSortNumber("0");
		return item;
	}

	private String totalValues(ResultValidationItem totalItem, ResultValidationItem additionalItem) {
		try {
			return String.valueOf(Double.parseDouble(totalItem.getResult().getValue())
					+ Double.parseDouble(additionalItem.getResult().getValue()));
		} catch (NumberFormatException e) {
			return totalItem.getResult().getValue();
		}
	}

	private void roundTotalItemValue(Map<String, ResultValidationItem> accessionToTotalMap) {
		for (ResultValidationItem totalItem : accessionToTotalMap.values()) {
			String total = totalItem.getResult().getValue();

			if (total.startsWith("99.9999")) {
				totalItem.getResult().setValue("100.0");
			} else {
				int seperatorIndex = total.indexOf('.');

				if (seperatorIndex > 0) {
					totalItem.getResult().setValue(total.substring(0, seperatorIndex + 2));
				}
			}
		}
	}

	private void sortByAccessionNumberAndOrder(List<AnalysisItem> resultItemList) {
		Collections.sort(resultItemList, new Comparator<AnalysisItem>() {
			public int compare(AnalysisItem a, AnalysisItem b) {
				int accessionComp = a.getAccessionNumber().compareTo(b.getAccessionNumber());
				return ((accessionComp == 0) ? Integer.parseInt(a.getTestSortNumber()) - Integer.parseInt(b.getTestSortNumber())
						: accessionComp);
			}
		});

	}

	private void setGroupingNumbers(List<AnalysisItem> resultList) {
		String currentAccessionNumber = null;
		AnalysisItem headItem = null;
		int groupingCount = 1;

		for (AnalysisItem analysisResultItem : resultList) {
			if (!analysisResultItem.getAccessionNumber().equals(currentAccessionNumber)) {
				currentAccessionNumber = analysisResultItem.getAccessionNumber();
				headItem = analysisResultItem;
				groupingCount++;
			} else {
				headItem.setMultipleResultForSample(true);
				analysisResultItem.setMultipleResultForSample(true);
			}

			analysisResultItem.setSampleGroupingNumber(groupingCount);
		}
	}

	@SuppressWarnings("unchecked")
	public List<ResultValidationItem> getUnValidatedElisaResultItemsInTestSection(String id) {

		List<Analysis> analysisList = new ArrayList<Analysis>();

		List<Test> tests = resultsLoadUtility.getTestsInSection(id);

		for (Test test : tests) {
			List<Analysis> analysisTestList = analysisDAO.getAllAnalysisByTestAndExcludedStatus(test.getId(), notValidStatus);
			analysisList.addAll(analysisTestList);
		}

		return getGroupedTestsForAnalysisList(analysisList, false);
	}

	@SuppressWarnings("unchecked")
	public List<ResultValidationItem> getUnValidatedTestResultItemsInTestSection(String sectionId, List<Integer> statusList) {

		List<Analysis> analysisList = analysisDAO.getAllAnalysisByTestSectionAndStatus(sectionId, statusList, false);
		sw.setMark("analysis returned " + analysisList.size());
		return getGroupedTestsForAnalysisList(analysisList, !StatusRules.useRecordStatusForValidation());
	}

	@SuppressWarnings("unchecked")
	public List<ResultValidationItem> getUnValidatedTestResultItemsByTest(String testName, List<Integer> statusList) {

		List<Analysis> analysisList = analysisDAO.getAllAnalysisByTestAndStatus(getTestId(testName), statusList);

		return getGroupedTestsForAnalysisList(analysisList, false);
	}

	/*
	 * N.B. The ignoreRecordStatus is an abomination and should be removed. It
	 * is a quick and dirty fix for workplan and validation using the same code
	 * but having different rules
	 */
	public List<ResultValidationItem> getGroupedTestsForAnalysisList(Collection<Analysis> filteredAnalysisList, boolean ignoreRecordStatus)
			throws LIMSRuntimeException {

		List<ResultValidationItem> selectedTestList = new ArrayList<ResultValidationItem>();
		Dictionary dictionary;

		for (Analysis analysis : filteredAnalysisList) {

			if (ignoreRecordStatus || sampleReadyForValidation(analysis.getSampleItem().getSample())) {
				List<ResultValidationItem> testResultItemList = getResultItemFromAnalysis(analysis);
				//NB.  The resultValue is filled in during getResultItemFromAnalysis as a side effect of setResult
				for (ResultValidationItem validationItem : testResultItemList) {
					if (isDictionaryType(validationItem.getResultType())) {
						dictionary = new Dictionary();
						String resultValue = null;
						try {
							dictionary.setId(validationItem.getResultValue());
							dictionaryDAO.getData(dictionary);
							resultValue = GenericValidator.isBlankOrNull(dictionary.getLocalAbbreviation()) ? dictionary.getDictEntry()
									: dictionary.getLocalAbbreviation();

						} catch (Exception e) {

						}

						validationItem.setResultValue(resultValue);

					}

					validationItem.setAnalysisStatusId(analysis.getStatusId());
					validationItem.setNonconforming(QAService.isAnalysisParentNonConforming(analysis));
					selectedTestList.add(validationItem);
				}
			}
		}

		return selectedTestList;
	}

	private boolean sampleReadyForValidation(Sample sample) {

		Boolean valid = accessionToValidMap.get(sample.getAccessionNumber());

		if (valid == null) {
			valid = new Boolean(getSampleRecordStatus(sample) == StatusService.RecordStatus.ValidationRegistration);
			accessionToValidMap.put(sample.getAccessionNumber(), valid);
		}

		return valid.booleanValue();
	}

	public List<ResultValidationItem> getResultItemFromAnalysis(Analysis analysis) throws LIMSRuntimeException {
		List<ResultValidationItem> testResultList = new ArrayList<ResultValidationItem>();

		List<Result> resultList = resultDAO.getResultsByAnalysis(analysis);

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

		ResultValidationItem parentItem = null;
		for (Result result : resultList) {
			if( parentItem != null && parentItem.getResultId().equals(result.getParentResult().getId())){
				parentItem.setQualifiedResultValue(result.getValue());
				parentItem.setResultType("Q");
				continue;
			}
			
			ResultValidationItem resultItem = createTestResultItem(analysis, analysis.getTest(), analysis.getSampleItem().getSortOrder(),
					result, analysis.getSampleItem().getSample().getAccessionNumber());

			if( resultItem.getQualifiedDictionaryId() != null){
				parentItem = resultItem;
			}
			
			testResultList.add(resultItem);
		}

		return testResultList;
	}

	private ResultValidationItem createTestResultItem(Analysis analysis, Test test, String sequenceNumber, Result result,
			String accessionNumber) {

		List<TestResult> testResults = getPossibleResultsForTest(test);

		String displayTestName = test.getDescription();
//		displayTestName = augmentTestNameWithRange(displayTestName, result);
		
		ResultValidationItem testItem = new ResultValidationItem();

		testItem.setAccessionNumber(accessionNumber);
		testItem.setAnalysisId(analysis.getId());
		testItem.setSequenceNumber(sequenceNumber);
		testItem.setTestName(displayTestName);
		testItem.setTestId(test.getId());
		testItem.setAnalysisMethod(analysis.getAnalysisType());
		testItem.setResult(result);
		testItem.setDictionaryResults(getAnyDictonaryValues(testResults));
		testItem.setResultType(getTestResultType(testResults ));
		testItem.setAnalysisStatusId(analysis.getStatusId());
		testItem.setTestSortNumber(test.getSortOrder());
		testItem.setReflexGroup(analysis.getTriggeredReflex());
		testItem.setChildReflex(analysis.getTriggeredReflex() && isConclusion(result, analysis));
		testItem.setQualifiedDictionaryId(getQualifiedDictionaryId(testResults));

		if (result != null && !GenericValidator.isBlankOrNull(result.getId())) {
			List<Note> noteList = NoteUtil.getNotesForObjectAndTable(result.getId(), RESULT_TABLE_ID);

			if (!(noteList == null || noteList.isEmpty())) {
				StringBuilder builder = new StringBuilder();
                builder.append(NoteUtil.getNotePrefix(noteList.get(noteList.size() - 1)));
                builder.append(noteList.get(noteList.size() - 1).getText());
				for(int i = noteList.size() - 2; i >= 0; i--){
					builder.append("<br>");
                    builder.append(NoteUtil.getNotePrefix(noteList.get(i)));
					builder.append(noteList.get(i).getText());
				}
				
				testItem.setPastNotes(builder.toString());
			}
		}

		return testItem;
	}

	private String getQualifiedDictionaryId(List<TestResult> testResults){
		for( TestResult testResult : testResults){
			if( "Q".equals(testResult.getTestResultType())){
				return "[" + testResult.getValue() + "]";
			}
		}
		
		return null;
	}

	private String augmentUOMWithRange(String uom,	Result result) {
		Double max = result.getMaxNormal();
		Double min = result.getMinNormal();		
	
		if( min == null || max == null || min.equals(max)){
			return uom;
		}
				
		return uom + "  ( " + String.valueOf(min) + " - " + String.valueOf(max) + " )";
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

	@SuppressWarnings("unchecked")
	private List<TestResult> getPossibleResultsForTest(Test test) {
		return testResultDAO.getAllTestResultsPerTest(test);
	}

	private List<IdValuePair> getAnyDictonaryValues(List<TestResult> testResults) {
		List<IdValuePair> values = null;
		Dictionary dictionary;

		if (testResults != null && testResults.size() > 0 && isDictionaryType(testResults.get(0).getTestResultType())) {
			values = new ArrayList<IdValuePair>();
			values.add(new IdValuePair("0", ""));

			for (TestResult testResult : testResults) {
				// Note: result group use to be a criteria but was removed, if
				// results are not as expected investigate
				if (isDictionaryType(testResult.getTestResultType())) {
					dictionary = dictionaryDAO.getDataForId(testResult.getValue());
					String displayValue = dictionary.getLocalizedName();

					if ("unknown".equals(displayValue)) {
						displayValue = GenericValidator.isBlankOrNull(dictionary.getLocalAbbreviation()) ? dictionary.getDictEntry()
								: dictionary.getLocalAbbreviation();
					}
					values.add(new IdValuePair(testResult.getValue(), displayValue));
				}
			}
		}

		return values;
	}

	private boolean isDictionaryType(String resultType){
		return "DQM".contains(resultType);
	}

	private String getTestResultType(List<TestResult> testResults) {
		String testResultType = "N";

		if (testResults != null && testResults.size() > 0) {
			testResultType = testResults.get(0).getTestResultType();
		}

		return testResultType;
	}

	public List<AnalysisItem> testResultListToELISAAnalysisList(List<ResultValidationItem> testResultList, List<Integer> statusList) {
		List<AnalysisItem> analysisItemList = new ArrayList<AnalysisItem>();
		AnalysisItem analysisResultItem = new AnalysisItem();
		String currentAccessionNumber = "";
		String currentInvalidAccessionNumber = "";
		boolean readyForValidation = true;

		for (int i = 0; i < testResultList.size(); i++) {

			ResultValidationItem tResultItem = testResultList.get(i);

			// create new bean
			if (!tResultItem.getAccessionNumber().equals(currentAccessionNumber)) {
				if (tResultItem.getAccessionNumber().equals(currentInvalidAccessionNumber)) {
					continue;
				}
				Sample sample = sampleDAO.getSampleByAccessionNumber(tResultItem.getAccessionNumber());
				if (sampleReadyForValidation(sample)) {
					if (!GenericValidator.isBlankOrNull(analysisResultItem.getFinalResult()) && readyForValidation) {
						analysisItemList.add(analysisResultItem);
					}
					readyForValidation = true;

					analysisResultItem = new AnalysisItem();
					analysisResultItem = testResultItemToELISAAnalysisItem(tResultItem);

					currentAccessionNumber = analysisResultItem.getAccessionNumber();
					currentInvalidAccessionNumber = "";
				} else { // record status not valid
					currentInvalidAccessionNumber = tResultItem.getAccessionNumber();
					continue;
				}
				// or just add test result to elisaAlgorithm bean
			} else {
				analysisResultItem.setResult(tResultItem.getResultValue());
				analysisResultItem = addTestResultToELISAAnalysisItem(tResultItem, analysisResultItem);
			}

			if (!GenericValidator.isBlankOrNull(analysisResultItem.getStatusId())
					&& !statusList.contains(Integer.parseInt(analysisResultItem.getStatusId()))) {
				readyForValidation = false;
			}

			String finalResult = checkIfFinalResult(tResultItem.getAnalysisId());

			if (!GenericValidator.isBlankOrNull(finalResult)) {
				analysisResultItem.setFinalResult(finalResult);
			}

			// final time through
			if (i == (testResultList.size() - 1) && readyForValidation
					&& !GenericValidator.isBlankOrNull(analysisResultItem.getFinalResult())) {
				analysisItemList.add(analysisResultItem);
			}
		}

		return analysisItemList;
	}

	public String checkIfFinalResult(String analysisId) {
		String finalResult = null;
		Analysis analysis = new Analysis();
		analysis.setId(analysisId);

		List<Result> resultList = resultDAO.getResultsByAnalysis(analysis);
		String conclusion = null;

		if (resultList.size() > 1) {
			for (Result result : resultList) {
				if (result.getAnalyte() != null && result.getAnalyte().getId().equals(CONCLUSION_ID)) {
					conclusion = result.getValue();
				}
			}

		}

		if (conclusion != null) {
			Dictionary dictionary = new Dictionary();
			dictionary.setId(conclusion);
			dictionaryDAO.getData(dictionary);
			finalResult = (dictionary.getDictEntry());
		}

		return finalResult;

	}

	public AnalysisItem testResultItemToELISAAnalysisItem(ResultValidationItem testResultItem) {
		AnalysisItem elisaResultItem = new AnalysisItem();

		elisaResultItem.setAccessionNumber(testResultItem.getAccessionNumber());
		elisaResultItem.setTestName(testResultItem.getTestName());
		elisaResultItem.setResult(testResultItem.getResultValue());
		elisaResultItem.setSampleGroupingNumber(testResultItem.getSampleGroupingNumber());
		elisaResultItem.setAnalysisId(testResultItem.getAnalysisId());
		elisaResultItem.setStatusId(testResultItem.getAnalysisStatusId());
		elisaResultItem.setNote(testResultItem.getNote());
		elisaResultItem.setNoteId(testResultItem.getNoteId());
		elisaResultItem.setResultId(testResultItem.getResultId());
		elisaResultItem.setNonconforming(testResultItem.isNonconforming());

		// elisaResultItem.setResult(testResultItem.getResult().getValue());

		// set elisa test to result
		elisaResultItem = setElisaTestResult(testResultItem.getTestName(), elisaResultItem);

		return elisaResultItem;

	}

	public AnalysisItem addTestResultToELISAAnalysisItem(ResultValidationItem testResultItem, AnalysisItem eItem) {

		eItem.setAnalysisId(testResultItem.getAnalysisId());
		eItem.setStatusId(testResultItem.getAnalysisStatusId());
		if( testResultItem.isNonconforming()){
			eItem.setNonconforming(true);
		}
		// set elisa test to result
		eItem = setElisaTestResult(testResultItem.getTestName(), eItem);

		return eItem;

	}

	public AnalysisItem setElisaTestResult(String testName, AnalysisItem eItem) {
		String result = eItem.getResult();
		String analysisId = eItem.getAnalysisId();

		if (testName.equals("Murex")) {
			eItem.setMurexResult(result);
			eItem.setMurexAnalysisId(analysisId);
		} else if (testName.equals("Integral")) {
			eItem.setIntegralResult(result);
			eItem.setIntegralAnalysisId(analysisId);
		} else if (testName.equals("Vironostika")) {
			eItem.setVironostikaResult(result);
			eItem.setVironostikaAnalysisId(analysisId);
		} else if (testName.equals("Genie II")) {
			eItem.setGenieIIResult(result);
			eItem.setGenieIIAnalysisId(analysisId);
		} else if (testName.equals("Genie II 10")) {
			eItem.setGenieII10Result(result);
			eItem.setGenieII10AnalysisId(analysisId);
		} else if (testName.equals("Genie II 100")) {
			eItem.setGenieII100Result(result);
			eItem.setGenieII100AnalysisId(analysisId);
		} else if (testName.equals("Western Blot 1")) {
			eItem.setWesternBlot1Result(result);
			eItem.setWesternBlot1AnalysisId(analysisId);
		} else if (testName.equals("Western Blot 2")) {
			eItem.setWesternBlot2Result(result);
			eItem.setWesternBlot2AnalysisId(analysisId);
		} else if (testName.equals("p24 Ag")) {
			eItem.setP24AgResult(result);
			eItem.setP24AgAnalysisId(analysisId);
		} else if (testName.equals("Bioline")) {
			eItem.setBiolineResult(result);
			eItem.setBiolineAnalysisId(analysisId);
		} else if (testName.equals("Innolia")) {
			eItem.setInnoliaResult(result);
			eItem.setInnoliaAnalysisId(analysisId);
		}

		return eItem;
	}

	public List<AnalysisItem> testResultListToAnalysisItemList(List<ResultValidationItem> testResultList) {
		List<AnalysisItem> analysisResultList = new ArrayList<AnalysisItem>();

		for (ResultValidationItem tResultItem : testResultList) {
			analysisResultList.add(testResultItemToAnalysisItem(tResultItem));
		}

		return analysisResultList;
	}

	private RecordStatus getSampleRecordStatus(Sample sample) {

		List<ObservationHistory> ohList = observationHistoryDAO.getAll(null, sample, SAMPLE_STATUS_OBSERVATION_HISTORY_TYPE_ID);

		if (ohList.isEmpty()) {
			return null;
		}

		return StatusService.getInstance().getRecordStatusForID(ohList.get(0).getValue());
	}

	public AnalysisItem testResultItemToAnalysisItem(ResultValidationItem testResultItem) {
		AnalysisItem analysisResultItem = new AnalysisItem();
		String testUnits = getUnitsByTestId(testResultItem.getTestId());
		String testName = testResultItem.getTestName();
		String sortOrder = testResultItem.getTestSortNumber();
		if (testResultItem.getResult().getAnalyte() != null
				&& ANALYTE_CD4_CT_GENERATED_ID.equals(testResultItem.getResult().getAnalyte().getId())) {
			testUnits = "";
			testName = StringUtil.getMessageForKey("result.conclusion.cd4");
			analysisResultItem.setShowAcceptReject(false);
			sortOrder = CD4_COUNT_SORT_NUMBER;
		} else if (testResultItem.getTestName().equals(totalTestName)) {
			analysisResultItem.setShowAcceptReject(false);
			analysisResultItem.setReadOnly(true);
			testUnits = testResultItem.getUnitsOfMeasure();
			analysisResultItem.setIsHighlighted(!"100.0".equals(testResultItem.getResult().getValue()));
		}

		testUnits = augmentUOMWithRange(testUnits,	testResultItem.getResult());
		
		analysisResultItem.setAccessionNumber(testResultItem.getAccessionNumber());
		analysisResultItem.setTestName(testName);
		analysisResultItem.setUnits(testUnits);
		analysisResultItem.setAnalysisId(testResultItem.getAnalysisId());
		analysisResultItem.setPastNotes(testResultItem.getPastNotes());
		analysisResultItem.setResultId(testResultItem.getResultId());
		analysisResultItem.setResultType(testResultItem.getResultType());
		analysisResultItem.setTestId(testResultItem.getTestId());
		analysisResultItem.setTestSortNumber(sortOrder);
		analysisResultItem.setDictionaryResults(testResultItem.getDictionaryResults());
		analysisResultItem.setDisplayResultAsLog(TestIdentityService.isTestNumericViralLoad(testResultItem.getTestId()));
		analysisResultItem.setResult(getFormatedResult(testResultItem));
		analysisResultItem.setReflexGroup(testResultItem.isReflexGroup());
		analysisResultItem.setChildReflex(testResultItem.isChildReflex());
		analysisResultItem.setNonconforming(testResultItem.isNonconforming());
		analysisResultItem.setQualifiedDictionaryId(testResultItem.getQualifiedDictionaryId());
		analysisResultItem.setQualifiedResultValue(testResultItem.getQualifiedResultValue());

		return analysisResultItem;

	}

	private String getFormatedResult(ResultValidationItem testResultItem) {
		String result = testResultItem.getResult().getValue();
		if( TestIdentityService.isTestNumericViralLoad(testResultItem.getTestId()) && !GenericValidator.isBlankOrNull(result)){
			result = result.split("\\(")[0].trim();
		}
		
		return result;
	}

	public String getUnitsByTestId(String testId) {

		String uomName = null;

		if (testId != null) {
			uomName = testIdToUnits.get(testId);
			if (uomName == null) {
				Test test = new Test();
				test.setId(testId);
				test = testDAO.getTestById(test);

				if (test.getUnitOfMeasure() != null) {
					uomName = test.getUnitOfMeasure().getName();
					testIdToUnits.put(testId, uomName);
				} else {
					testIdToUnits.put(testId, "");
				}
			}
		}
		return uomName;

	}

	public String getTestSectionId(String testSectionName) {
		TestSection testSection = new TestSection();
		testSection.setTestSectionName(testSectionName);
		testSection = testSectionDAO.getTestSectionByName(testSection);

		return testSection.getId();
	}

	public String getTestId(String testName) {
		Test test = new Test();
		test.setTestName(testName);
		test = testDAO.getTestByName(test);

		return test.getId();
	}

}
