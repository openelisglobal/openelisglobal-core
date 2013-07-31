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
package us.mn.state.health.lims.analysis.dao;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.dao.BaseDAO;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import us.mn.state.health.lims.test.valueholder.Test;

/**
 * @author diane benz
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public interface AnalysisDAO extends BaseDAO {

    public boolean insertData(Analysis analysis, boolean duplicateCheck) throws LIMSRuntimeException;

	@SuppressWarnings("rawtypes")
	public void deleteData(List analysiss) throws LIMSRuntimeException;

	@SuppressWarnings("rawtypes")
	public List getAllAnalyses() throws LIMSRuntimeException;

	@SuppressWarnings("rawtypes")
	public List getPageOfAnalyses(int startingRecNo)throws LIMSRuntimeException;

	public void getData(Analysis analysis) throws LIMSRuntimeException;

	public void updateData(Analysis analysis) throws LIMSRuntimeException;

	@SuppressWarnings("rawtypes")
	public List getAnalyses(String filter) throws LIMSRuntimeException;

	@SuppressWarnings("rawtypes")
	public List getNextAnalysisRecord(String id) throws LIMSRuntimeException;

	@SuppressWarnings("rawtypes")
	public List getPreviousAnalysisRecord(String id) throws LIMSRuntimeException;

	@SuppressWarnings("rawtypes")
	public List getAllAnalysesPerTest(Test test) throws LIMSRuntimeException;

	@SuppressWarnings("rawtypes")
	public List getAllAnalysisByTestAndStatus(String testId, List<Integer> statusIdList) throws LIMSRuntimeException;

	@SuppressWarnings("rawtypes")
	public List getAllAnalysisByTestsAndStatus(List<String> testIdList, List<Integer> statusIdList) throws LIMSRuntimeException;

	@SuppressWarnings("rawtypes")
	public List getAllAnalysisByTestAndExcludedStatus(String testId, List<Integer> statusIdList) throws LIMSRuntimeException;

	@SuppressWarnings("rawtypes")
	public List getAllAnalysisByTestSectionAndStatus(String testSectionId, List<Integer> statusIdList, boolean sortedByDateAndAccession) throws LIMSRuntimeException;

	@SuppressWarnings("rawtypes")
	public List getAllAnalysisByTestSectionAndExcludedStatus(String testSectionId, List<Integer> statusIdList) throws LIMSRuntimeException;

	public List<Analysis> getAnalysesBySampleItem(SampleItem sampleItem) throws LIMSRuntimeException;

	public List<Analysis> getAnalysesBySampleItemsExcludingByStatusIds(SampleItem sampleItem, Set<Integer> statusIds) throws LIMSRuntimeException;

	public List<Analysis> getAnalysesBySampleStatusId(String statusId) throws LIMSRuntimeException;

	public List<Analysis> getAnalysesBySampleStatusIdExcludingByStatusId(String statusId, Set<Integer> statusIds) throws LIMSRuntimeException;

	@SuppressWarnings("rawtypes")
	public List getAnalysesReadyToBeReported() throws LIMSRuntimeException;

	@SuppressWarnings("rawtypes")
	public List getAllChildAnalysesByResult(Result result) throws LIMSRuntimeException;

	@SuppressWarnings("rawtypes")
	public List getMaxRevisionAnalysesReadyToBeReported() throws LIMSRuntimeException;

	@SuppressWarnings("rawtypes")
	public List getMaxRevisionAnalysesReadyForReportPreviewBySample(List accessionNumbers) throws LIMSRuntimeException;

	@SuppressWarnings("rawtypes")
	public List getAnalysesAlreadyReportedBySample(Sample sample) throws LIMSRuntimeException;

	@SuppressWarnings("rawtypes")
	public List getMaxRevisionAnalysesBySample(SampleItem sampleItem) throws LIMSRuntimeException;

	@SuppressWarnings("rawtypes")
	public List getMaxRevisionAnalysesBySampleIncludeCanceled(SampleItem sampleItem) throws LIMSRuntimeException;

	@SuppressWarnings("rawtypes")
	public List getRevisionHistoryOfAnalysesBySample(SampleItem sampleItem) throws LIMSRuntimeException;

	@SuppressWarnings("rawtypes")
	public List getRevisionHistoryOfAnalysesBySampleAndTest(SampleItem sampleItem, Test test, boolean includeLatestRevision) throws LIMSRuntimeException;

	@SuppressWarnings("rawtypes")
	public List getAllMaxRevisionAnalysesPerTest(Test test) throws LIMSRuntimeException;

	@SuppressWarnings("rawtypes")
	public List getMaxRevisionPendingAnalysesReadyToBeReportedBySample(Sample sample) throws LIMSRuntimeException;

	@SuppressWarnings("rawtypes")
	public List getMaxRevisionPendingAnalysesReadyForReportPreviewBySample(Sample sample) throws LIMSRuntimeException;

	public Analysis getPreviousAnalysisForAmendedAnalysis(Analysis analysis) throws LIMSRuntimeException;

	public void getMaxRevisionAnalysisBySampleAndTest(Analysis analysis) throws LIMSRuntimeException;

	@SuppressWarnings("rawtypes")
	public List getMaxRevisionParentTestAnalysesBySample(SampleItem sampleItem) throws LIMSRuntimeException;

	public List<Analysis>  getAnalysesForStatusId(String statusId)throws LIMSRuntimeException;

	public List<Analysis> getAnalysisStartedOnExcludedByStatusId(Date collectionDate, Set<Integer> statusIds) throws LIMSRuntimeException;
	public List<Analysis> getAnalysisStartedOn(Date collectionDate) throws LIMSRuntimeException;

	public List<Analysis> getAnalysisCollectedOnExcludedByStatusId(Date collectionDate, Set<Integer> statusIds) throws LIMSRuntimeException;
	public List<Analysis> getAnalysisCollectedOn(Date collectionDate) throws LIMSRuntimeException;

	public List<Analysis> getAnalysesBySampleId(String id) throws LIMSRuntimeException;
	public List<Analysis> getAnalysesBySampleIdExcludedByStatusId(String id, Set<Integer> statusIds) throws LIMSRuntimeException;

    public List<Analysis> getAnalysisBySampleAndTestIds(String sampleKey, List<Integer> testIds);

	public List<Analysis> getAnalysisByTestSectionAndCompletedDateRange(String sectionID, Date lowDate, Date highDate) throws LIMSRuntimeException;

	public List<Analysis> getAnalysisStartedOrCompletedInDateRange(Date lowDate, Date highDate) throws LIMSRuntimeException;

	public List<Analysis> getAllAnalysisByTestSectionAndStatus(String testSectionId, List<Integer> analysisStatusList, List<Integer> sampleStatusList) throws LIMSRuntimeException;

	public List<Analysis> getAnalysisStartedOnRangeByStatusId(Date lowDate, Date highDate, String statusID) throws LIMSRuntimeException;
	
	public List<Analysis> getAnalysisCompleteInRange(Timestamp lowDate, Timestamp highDate) throws LIMSRuntimeException;

	public List<Analysis> getAnalysisEnteredAfterDate(Timestamp latestCollectionDate) throws LIMSRuntimeException;

	public List<Analysis> getAnalysisByAccessionAndTestId(String accessionNumber, String testId) throws LIMSRuntimeException;

	public List<Analysis> getAnalysesBySampleIdAndStatusId(String id, Set<Integer> analysisStatusIds) throws LIMSRuntimeException;

	public List<Analysis> getAnalysisByTestNamesAndCompletedDateRange(List<String> testNames, Date lowDate, Date highDate) throws LIMSRuntimeException;

	public List<Analysis> getAnalysesBySampleItemIdAndStatusId(String sampleItemId, String statusId) throws LIMSRuntimeException;

	public List<Analysis> getAnalysisByTestDescriptionAndCompletedDateRange(List<String> descriptions, Date sqlDayOne, Date sqlDayTwo) throws LIMSRuntimeException;

	public Analysis getAnalysisById(String analysisId) throws LIMSRuntimeException;


}
