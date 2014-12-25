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
 * Copyright (C) I-TECH, University of Washington, Seattle WA.  All Rights Reserved.
 *
 */
package us.mn.state.health.lims.resultvalidation.util;

import org.apache.commons.validator.GenericValidator;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.tools.StopWatch;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.ConfigurationProperties.Property;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.result.action.util.ResultsLoadUtility;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.resultvalidation.action.util.ResultValidationItem;
import us.mn.state.health.lims.resultvalidation.bean.AnalysisItem;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.typeoftestresult.valueholder.TypeOfTestResult.ResultType;

import java.util.*;

public class ResultsValidationRetroCUtility extends ResultsValidationUtility {



    //	private static String VIRAL_LOAD_ID = "";
	private static String ANALYTE_CD4_CT_GENERATED_ID;

	private final ResultDAO resultDAO = new ResultDAOImpl();

	private ResultsLoadUtility resultsLoadUtility = new ResultsLoadUtility();
	private String totalTestName = "";

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

            return result == null || result.getAnalyte() == null || !ANALYTE_CD4_CT_GENERATED_ID.equals( result.getAnalyte().getId() );
		} else {
			return name.equals("Neut %") || name.equals("Mono %") || name.equals("Eo %") || name.equals("Baso %");
		}
	}

	private ResultValidationItem createTotalItem(ResultValidationItem resultItem) {
		ResultValidationItem item = new ResultValidationItem();

		item.setTestName(totalTestName);
		item.setUnitsOfMeasure( "%" );
		item.setAccessionNumber( resultItem.getAccessionNumber() );
		item.setResult( new Result() );
		item.getResult().setValue( "0" );
		item.setResultType( ResultType.NUMERIC.getDBValue() );
		item.setTestSortNumber( "0" );
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
				int separatorIndex = total.indexOf('.');

				if (separatorIndex > 0) {
					totalItem.getResult().setValue(total.substring(0, separatorIndex + 2));
				}
			}
		}
	}
	
	public List<ResultValidationItem> getUnValidatedElisaResultItemsInTestSection(String id) {

		List<Analysis> analysisList = new ArrayList<Analysis>();

		List<Test> tests = resultsLoadUtility.getTestsInSection(id);

		for (Test test : tests) {
			List<Analysis> analysisTestList = analysisDAO.getAllAnalysisByTestAndExcludedStatus(test.getId(), notValidStatus);
			analysisList.addAll(analysisTestList);
		}

		return getGroupedTestsForAnalysisList(analysisList, false);
	}

	public List<ResultValidationItem> getUnValidatedTestResultItemsByTest(String testName, List<Integer> statusList) {

		List<Analysis> analysisList = analysisDAO.getAllAnalysisByTestAndStatus(getTestId(testName), statusList);

		return getGroupedTestsForAnalysisList(analysisList, false);
	}
}
