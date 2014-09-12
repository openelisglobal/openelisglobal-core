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
 * Copyright (C) ITECH, University of Washington, Seattle WA.  All Rights Reserved.
 *
 */
package us.mn.state.health.lims.reports.action.implementation;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.validator.GenericValidator;
import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.services.NoteService;
import us.mn.state.health.lims.common.services.StatusService;
import us.mn.state.health.lims.common.services.StatusService.AnalysisStatus;
import us.mn.state.health.lims.common.services.TestService;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.ConfigurationProperties.Property;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.referral.valueholder.Referral;
import us.mn.state.health.lims.referral.valueholder.ReferralResult;
import us.mn.state.health.lims.reports.action.implementation.reportBeans.ClinicalPatientData;
import us.mn.state.health.lims.reports.action.implementation.reportBeans.HaitiClinicalPatientDataColFormat;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.sample.util.AccessionNumberUtil;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.test.valueholder.TestSection;

import java.sql.Date;
import java.util.*;

public class PatientCILNSPClinical extends PatientReport implements IReportCreator, IReportParameterSetter{

	private AnalysisDAO analysisDAO = new AnalysisDAOImpl();
	private static Set<Integer> analysisStatusIds;
	private boolean isLNSP = false;
	protected List<HaitiClinicalPatientDataColFormat> clinicalReportItems;

	static{
		analysisStatusIds = new HashSet<Integer>();
		analysisStatusIds.add(Integer.parseInt(StatusService.getInstance().getStatusID(AnalysisStatus.BiologistRejected)));
		analysisStatusIds.add(Integer.parseInt(StatusService.getInstance().getStatusID(AnalysisStatus.Finalized)));
		analysisStatusIds.add(Integer.parseInt(StatusService.getInstance().getStatusID(AnalysisStatus.NonConforming_depricated)));
		analysisStatusIds.add(Integer.parseInt(StatusService.getInstance().getStatusID(AnalysisStatus.NotStarted)));
		analysisStatusIds.add(Integer.parseInt(StatusService.getInstance().getStatusID(AnalysisStatus.ReferredIn)));
		analysisStatusIds.add(Integer.parseInt(StatusService.getInstance().getStatusID(AnalysisStatus.TechnicalAcceptance)));
		analysisStatusIds.add(Integer.parseInt(StatusService.getInstance().getStatusID(AnalysisStatus.Canceled)));

	}
	
	static final String configName = ConfigurationProperties.getInstance()
			.getPropertyValue(Property.configurationName);

	public PatientCILNSPClinical(){
		super();
	}

	@Override
	protected String reportFileName(){
		if (configName.equals("CI IPCI")) {
			return "PatientReportCIIPCI";
		} else {
			return "PatientReportCILNSP";
		}
	}

	@Override
	protected void createReportItems(){
		List<Analysis> analysisList = analysisDAO.getAnalysesBySampleIdAndStatusId(currentSampleService.getId(), analysisStatusIds);

		currentConclusion = null;
		for(Analysis analysis : analysisList){
			// case if there was a confirmation sample with no test specified
			if(analysis.getTest() != null){
				reportAnalysis = analysis;
				ClinicalPatientData resultsData = reportAnalysisResults( false);
				reportItems.add(resultsData);

				Referral referral = referralDao.getReferralByAnalysisId(reportAnalysis.getId());
				if(referral != null){
					addReferredTests(referral, resultsData, analysis.isReferredOut());
				}
			}
		}
	}

	private void addReferredTests(Referral referral, ClinicalPatientData parentData, boolean parentStillReferred){
		List<ReferralResult> referralResults = referralResultDAO.getReferralResultsForReferral(referral.getId());
        String note = new NoteService( reportAnalysis ).getNotesAsString( false, true, "<br/>", true );

		for(int i = 0; i < referralResults.size(); i++){
			if(referralResults.get(i).getResult() != null &&
			// if referral has been canceled then don't show referred tests with
			// no results
					(parentStillReferred || !GenericValidator.isBlankOrNull(referralResults.get(i).getResult().getValue()))){

				i = reportReferralResultValue(referralResults, i);
				ReferralResult referralResult = referralResults.get(i);

				ClinicalPatientData data = new ClinicalPatientData();
				copyParentData(data, parentData);

				data.setResult(reportReferralResultValue);
                if( note != null){
                    data.setNote(note);
                }
				String testId = referralResult.getTestId();
				if(!GenericValidator.isBlankOrNull(testId)){
					Test test = new Test();
					test.setId(testId);
					testDAO.getData(test);
					data.setTestName( TestService.getUserLocalizedReportingTestName( test ) );

					String uom = getUnitOfMeasure( test);
					if(reportReferralResultValue != null){
						data.setReferralResult(addIfNotEmpty(reportReferralResultValue, uom));
					}
					data.setTestRefRange(addIfNotEmpty(getRange(referralResult.getResult()), uom));
					data.setTestSortOrder(GenericValidator.isBlankOrNull(test.getSortOrder()) ? Integer.MAX_VALUE : Integer.parseInt(test
							.getSortOrder()));
					TestSection resultTestSection = getTestSection(referralResult);
					if(resultTestSection != null){
						data.setSectionSortOrder(resultTestSection.getSortOrderInt());
						data.setTestSection(resultTestSection.getLocalizedName());
					}
				}

				if(GenericValidator.isBlankOrNull(reportReferralResultValue)){
					sampleCompleteMap.put(currentSampleService.getAccessionNumber(), Boolean.FALSE);
					data.setResult(StringUtil.getMessageForKey("report.test.status.inProgress")
							+ (augmentResultWithFlag() ? getResultFlag(referralResult.getResult(), "A") : ""));
				}else{
					data.setResult(reportReferralResultValue + (augmentResultWithFlag() ? getResultFlag(referralResult.getResult(), "A") : ""));
				}

				data.setAlerts(getResultFlag(referralResult.getResult(), "A"));
				data.setHasRangeAndUOM(referralResult.getResult() != null && "N".equals(referralResult.getResult().getResultType()));

				reportItems.add(data);
			}
		}
	}

	private TestSection getTestSection(ReferralResult referralResult){
		if(referralResult.getResult().getAnalysis() != null){
			return referralResult.getResult().getAnalysis().getTestSection();
		}

		Test test = testDAO.getTestById(referralResult.getTestId());

		if(test != null){
			return test.getTestSection();
		}

		return null;
	}

	private void copyParentData(ClinicalPatientData data, ClinicalPatientData parentData){
		data.setContactInfo(parentData.getContactInfo());
		data.setSiteInfo(parentData.getSiteInfo());
		data.setReceivedDate(parentData.getReceivedDate());
		data.setDob(parentData.getDob());
		data.setGender(parentData.getGender());
		data.setNationalId(parentData.getNationalId());
		data.setPatientName(parentData.getPatientName());
		data.setFirstName(parentData.getFirstName());
		data.setLastName(parentData.getLastName());
		data.setDept(parentData.getDept());
		data.setCommune(parentData.getCommune());
		data.setStNumber(parentData.getStNumber());
		data.setAccessionNumber(parentData.getAccessionNumber());
		data.setLabOrderType(parentData.getLabOrderType());
	}

	@Override
	protected void postSampleBuild(){
		if(reportItems.isEmpty()){
			ClinicalPatientData reportItem = reportAnalysisResults( false);
			HaitiClinicalPatientDataColFormat colData = new HaitiClinicalPatientDataColFormat(reportItem);
			colData.setSectionName(StringUtil.getMessageForKey("report.no.results"));
			colData.setAge(createReadableAge(reportItem.getDob()));
			clinicalReportItems.add(colData);
		}else{
			buildReport();
		}

	}

	private void buildReport(){
		Collections.sort(reportItems, new Comparator<ClinicalPatientData>(){
			@Override
			public int compare(ClinicalPatientData o1, ClinicalPatientData o2){
				String o1AccessionNumber = AccessionNumberUtil.getAccessionNumberFromSampleItemAccessionNumber(o1.getAccessionNumber());
				String o2AccessionNumber = AccessionNumberUtil.getAccessionNumberFromSampleItemAccessionNumber(o2.getAccessionNumber());
				int accessionSort = o1AccessionNumber.compareTo(o2AccessionNumber);

				if(accessionSort != 0){
					return accessionSort;
				}

				int sectionSort = o1.getSectionSortOrder() - o2.getSectionSortOrder(); 
				
				if(sectionSort != 0){
					return sectionSort;
				}

				return o1.getTestSortOrder() - o2.getTestSortOrder();
			}
		});

		HaitiClinicalPatientDataColFormat colData = null;
		String section = null;
		String accessionNumber = null;
		List<ClinicalPatientData> testSection = new ArrayList<ClinicalPatientData>();
		for(ClinicalPatientData reportItem : reportItems){
			//This handles the case where two orders in a row have a single identical section
			String itemAccessionNumber = getRootAccessionNumber(reportItem);
			if( !itemAccessionNumber.equals(accessionNumber)){
				section = null;
				accessionNumber = itemAccessionNumber;
			}
			
			// if the current test section is the same as the previous, add it
			// to the list of report Items within this test section

			if(reportItem.getTestSection().equals(section)){
				testSection.add(reportItem);

				// if the test section is different from the last, then print
				// out the previous test section and start a new list for the
				// new test section
			}else{
				buildTestSection(testSection, colData);

				// add first report item of the new test set into a new list for
				// that section
				section = reportItem.getTestSection();
				testSection = new ArrayList<ClinicalPatientData>();
				testSection.add(reportItem);
			}
		}

		// add last test section
		if(!testSection.isEmpty()){
			buildTestSection(testSection, colData);
		}
	}

	private String getRootAccessionNumber(ClinicalPatientData reportItem){
		String itemAccessionNumber = reportItem.getAccessionNumber();
		int separatorIndex = reportItem.getAccessionNumber().lastIndexOf("-");
		if( separatorIndex > 0){
			itemAccessionNumber = itemAccessionNumber.substring(0, separatorIndex);
		}
		return itemAccessionNumber;
	}

	private HaitiClinicalPatientDataColFormat buildCol(HaitiClinicalPatientDataColFormat colData, ClinicalPatientData patientData, int column){

		if(column == 1){
			colData.setCol1testName(patientData.getTestName());
			colData.setCol1result(patientData.getResult());
			colData.setCol1range(patientData.getTestRefRange());
			colData.setCol1noUOM(!patientData.isHasRangeAndUOM());
			if(!GenericValidator.isBlankOrNull(patientData.getNote())){
				colData.setCol1Note(patientData.getNote());
				colData.turnOnNotes();
			}

			colData.setAge(createReadableAge(patientData.getDob()));

			String accessionNumber = AccessionNumberUtil.getAccessionNumberFromSampleItemAccessionNumber(patientData.getAccessionNumber());
			colData.setCompleteFlag(StringUtil.getMessageForKey(sampleCompleteMap.get(accessionNumber) ? "report.status.complete"
					: "report.status.partial"));
		}else{
			colData.setCol2testName(patientData.getTestName());
			colData.setCol2result(patientData.getResult());
			colData.setCol2range(patientData.getTestRefRange());
			colData.setCol2noUOM(!patientData.isHasRangeAndUOM());
			if(!GenericValidator.isBlankOrNull(patientData.getNote())){
				colData.setCol2Note(patientData.getNote());
				colData.turnOnNotes();
			}

		}

		return colData;
	}

	/*
	 * private helper method, factors out work to complete the insertion of a
	 * complete test section into the report
	 */
	private void buildTestSection(List<ClinicalPatientData> testSection, HaitiClinicalPatientDataColFormat colData){
		// calculates half # of report Items, if an odd number adds 1 to account
		// for truncated division
		int half = (testSection.size() % 2 == 0) ? testSection.size() / 2 : testSection.size() / 2 + 1;
		for(int i = 0; i < half; i++){
			ClinicalPatientData col1 = testSection.get(i);
			colData = new HaitiClinicalPatientDataColFormat(col1);

			colData = buildCol(colData, col1, 1);
			// protects us from running off the end of the list, incase there is
			// an odd number of report items
			if(i + half < testSection.size()){
				ClinicalPatientData col2 = testSection.get(i + half);
				colData = buildCol(colData, col2, 2);
			}
			clinicalReportItems.add(colData);
		}
	}

	private String createReadableAge(String dob){
		if(GenericValidator.isBlankOrNull(dob)){
			return "";
		}

		dob = dob.replaceAll("xx", "01");
		Date dobDate = DateUtil.convertStringDateToSqlDate(dob);
		int months = DateUtil.getAgeInMonths(dobDate, DateUtil.getNowAsSqlDate());
		if(months > 35){
			return (months / 12) + " A";
		}else if(months > 0){
			return months + " M";
		}else{
			int days = DateUtil.getAgeInDays(dobDate, DateUtil.getNowAsSqlDate());
			return days + " J";
		}

	}

	@Override
	protected String getReportNameForParameterPage(){
		return StringUtil.getMessageForKey("openreports.patientTestStatus");
	}

	public JRDataSource getReportDataSource() throws IllegalStateException{
		if(!initialized){
			throw new IllegalStateException("initializeReport not called first");
		}

		return errorFound ? new JRBeanCollectionDataSource(errorMsgs) : new JRBeanCollectionDataSource(clinicalReportItems);
	}

	@Override
	protected String getSiteLogo(){
		return "labLogo.jpg";
	}

	@Override
	protected void initializeReportItems(){
		super.initializeReportItems();
		clinicalReportItems = new ArrayList<HaitiClinicalPatientDataColFormat>();
	}

	@Override
	protected void setReferredResult(ClinicalPatientData data, Result result){
		data.setResult(data.getResult() + (augmentResultWithFlag() ? getResultFlag(result, "R") : ""));
		data.setAlerts(getResultFlag(result, "R"));
	}

}
