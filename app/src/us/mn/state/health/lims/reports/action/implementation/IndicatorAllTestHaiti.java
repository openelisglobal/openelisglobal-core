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
package us.mn.state.health.lims.reports.action.implementation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.services.StatusService;
import us.mn.state.health.lims.common.services.StatusService.AnalysisStatus;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.reports.action.implementation.reportBeans.HaitiAggregateReportData;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.daoimpl.TestSectionDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;

/**
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * Copyright (C) CIRG, University of Washington, Seattle WA. All Rights
 * Reserved.
 * 
 */
public abstract class IndicatorAllTestHaiti extends HaitiIndicatorReport implements IReportCreator,
		IReportParameterSetter {

	private List<HaitiAggregateReportData> reportItems;
	private Map<String, TestBucket> testNameToBucketList;
	private Map<String, TestBucket> concatSection_TestToBucketMap;
	private List<TestBucket> testBucketList;
	private static String NOT_STARTED_STATUS_ID;
	private static String FINALIZED_STATUS_ID;
	private static String TECH_ACCEPT_ID;
	private static String TECH_REJECT_ID;
	private static String BIOLOGIST_REJECT_ID;
	private static String USER_TEST_SECTION_ID;

	static {
		NOT_STARTED_STATUS_ID = StatusService.getInstance().getStatusID(AnalysisStatus.NotStarted);
		FINALIZED_STATUS_ID = StatusService.getInstance().getStatusID(AnalysisStatus.Finalized);
		TECH_ACCEPT_ID = StatusService.getInstance().getStatusID(AnalysisStatus.TechnicalAcceptance);
		TECH_REJECT_ID = StatusService.getInstance().getStatusID(AnalysisStatus.TechnicalRejected);
		BIOLOGIST_REJECT_ID = StatusService.getInstance().getStatusID(AnalysisStatus.BiologistRejected);
		USER_TEST_SECTION_ID = new TestSectionDAOImpl().getTestSectionByName("user").getId();
	}

	@Override
	protected String reportFileName() {
		return "HaitiLabAggregate";
	}

	public JRDataSource getReportDataSource() throws IllegalStateException {
		return errorFound ? new JRBeanCollectionDataSource(errorMsgs) : new JRBeanCollectionDataSource(reportItems);
	}

	public void initializeReport(BaseActionForm dynaForm) {
		super.initializeReport();
		setDateRange(dynaForm);

		createReportParameters();

		setTestMapForAllTests();

		setAnalysisForDateRange();

		mergeLists();

		setTestAggregates();

	}

	private void setTestMapForAllTests() {
		testNameToBucketList = new HashMap<String, TestBucket>();
		concatSection_TestToBucketMap = new HashMap<String, TestBucket>();
		testBucketList = new ArrayList<TestBucket>();

		TestDAO testDAO = new TestDAOImpl();
		List<Test> testList = testDAO.getAllActiveTests(false);

		for (Test test : testList) {

			TestBucket bucket = new TestBucket();

			bucket.testName = test.getReportingDescription();
			bucket.testSort = Integer.parseInt(test.getSortOrder());
			bucket.testSection = test.getTestSection().getLocalizedName();
			bucket.sectionSort = test.getTestSection().getSortOrderInt();
			
			testNameToBucketList.put(test.getReportingDescription(), bucket);
			testBucketList.add(bucket);
		}

	}

	private void setAnalysisForDateRange() {
		AnalysisDAO analysisDAO = new AnalysisDAOImpl();
		List<Analysis> analysisList = analysisDAO.getAnalysisStartedOrCompletedInDateRange(lowDate, highDate);
		
		for (Analysis analysis : analysisList) {
			Test test = analysis.getTest();
			
			if (test != null) {
				TestBucket testBucket = null;
				//N.B. We need to look at the test->test section because the analysis test section reflects the user selection for the test section
				//that entry will not be in the test to test section map
				if (USER_TEST_SECTION_ID.equals(analysis.getTest().getTestSection().getId())) {
					String concatedName = analysis.getTestSection().getLocalizedName()
							+ analysis.getTest().getLocalizedName();
					testBucket = concatSection_TestToBucketMap.get(concatedName);
					if (testBucket == null) {
						testBucket = new TestBucket();
						testBucket.testName = test.getReportingDescription();
						testBucket.testSort = Integer.parseInt(test.getSortOrder());
						testBucket.testSection = analysis.getTestSection().getLocalizedName();
						testBucket.sectionSort = analysis.getTestSection().getSortOrderInt();
						concatSection_TestToBucketMap.put(concatedName, testBucket);
					}
				} else {
					testBucket = testNameToBucketList.get(test.getReportingDescription());
				}

				if (testBucket != null) {
					if (NOT_STARTED_STATUS_ID.equals(analysis.getStatusId())) {
						testBucket.notStartedCount++;
					} else if (inProgress(analysis)) {
						testBucket.inProgressCount++;
					} else if (FINALIZED_STATUS_ID.equals(analysis.getStatusId())) {
						testBucket.finishedCount++;
					}
				}
			}
		}
	}

	private boolean inProgress(Analysis analysis) {
		return TECH_ACCEPT_ID.equals(analysis.getStatusId()) ||
			   TECH_REJECT_ID.equals(analysis.getStatusId()) ||
			   BIOLOGIST_REJECT_ID.equals(analysis.getStatusId());
	}
	
	private void mergeLists() {

		for (TestBucket bucket : concatSection_TestToBucketMap.values()) {
			testBucketList.add(bucket);
		}

		Collections.sort(testBucketList, new Comparator<TestBucket>() {
			@Override
			public int compare(TestBucket o1, TestBucket o2) {
				int order = o1.sectionSort - o2.sectionSort;
				
				if( order == 0){
					order = o1.testSort - o2.testSort;
				}
				
				return order;
			}

		});

	}

	@Override
	protected String getNameForReportRequest() {
		return StringUtil.getMessageForKey("openreports.all.tests.aggregate");
	}

	private void setTestAggregates() {
		reportItems = new ArrayList<HaitiAggregateReportData>();

		for (TestBucket bucket : testBucketList) {
			if ((bucket.finishedCount + bucket.notStartedCount + bucket.inProgressCount) > 0) {
				HaitiAggregateReportData data = new HaitiAggregateReportData();

				data.setFinished(bucket.finishedCount);
				data.setNotStarted(bucket.notStartedCount);
				data.setInProgress(bucket.inProgressCount);
				data.setTestName(bucket.testName);
				data.setSectionName(bucket.testSection);

				reportItems.add(data);
			}
		}
	}

	private class TestBucket {
		public String testName = "";
		public int testSort = 0;
		public String testSection = "";
		public int sectionSort = 0;
		public int notStartedCount = 0;
		public int inProgressCount = 0;
		public int finishedCount = 0;
	}

	@Override
	protected String getNameForReport() {
		return StringUtil.getContextualMessageForKey("openreports.all.tests.aggregate");
	}

    
}
