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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.services.StatusService;
import us.mn.state.health.lims.common.services.StatusService.AnalysisStatus;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.referral.valueholder.Referral;
import us.mn.state.health.lims.referral.valueholder.ReferralResult;
import us.mn.state.health.lims.reports.action.implementation.reportBeans.HaitiClinicalPatientData;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.sample.util.AccessionNumberUtil;
import us.mn.state.health.lims.test.valueholder.Test;

public class PatientHaitiClinical extends HaitiPatientReport implements IReportCreator, IReportParameterSetter{

	private AnalysisDAO analysisDAO = new AnalysisDAOImpl();
	private static Set<Integer> analysisStatusIds;
	private boolean isLNSP = false;
	protected List<HaitiClinicalPatientData> clinicalReportItems;

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

	public PatientHaitiClinical(){
		super();
	}

	public PatientHaitiClinical(boolean isLNSP){
		super();
		this.isLNSP = isLNSP;
	}

	@Override
	protected String reportFileName(){
		return noAlertColumn ? "PatientReportHaitiNoAlerts" : "PatientReportHaitiClinical";
	}

	@Override
	protected void createReportItems(){
		List<Analysis> analysisList = analysisDAO.getAnalysesBySampleIdAndStatusId(reportSample.getId(), analysisStatusIds);

		currentConclusion = null;
		for(Analysis analysis : analysisList){
			// case if there was a confirmation sample with no test specified
			if(analysis.getTest() != null && !analysis.getStatusId().equals(StatusService.getInstance().getStatusID(AnalysisStatus.ReferredIn))){
				reportAnalysis = analysis;
				HaitiClinicalPatientData resultsData = reportAnalysisResults();
				

				if(reportAnalysis.isReferredOut()){
					Referral referral = referralDao.getReferralByAnalysisId(reportAnalysis.getId());
					if(referral != null){
						addReferredTests(referral, resultsData);
					}
				}else{
					reportItems.add(resultsData);	
				}
			}
		}
	}

	private void addReferredTests(Referral referral, HaitiClinicalPatientData parentData){
		List<ReferralResult> referralResults = referralResultDAO.getReferralResultsForReferral(referral.getId());


		if( noAlertColumn && !referralResults.isEmpty()){
		
			boolean referralTestAssigned = false;
			for( ReferralResult referralResult : referralResults){
				if( referralResult.getTestId() != null){
					referralTestAssigned = true;
				}
			}
			
			if( !referralTestAssigned){
				reportItems.add(parentData);	
			}
		}else{
			reportItems.add(parentData);	
		}
		
		
		for(int i = 0; i < referralResults.size(); i++){
			if( referralResults.get(i).getResult() == null ){
				sampleCompleteMap.put(reportSample.getAccessionNumber(), Boolean.FALSE);
			}else{
				// pick up note from 1st of possible multiple results
				String referralNote = getResultNote(referralResults.get(i).getResult());
				i = reportReferralResultValue(referralResults, i);
				ReferralResult referralResult = referralResults.get(i);

				HaitiClinicalPatientData data = new HaitiClinicalPatientData();
				copyParentData(data, parentData);

				data.setResult(reportReferralResultValue);
				data.setNote(referralNote);
				String testId = referralResult.getTestId();
				if(!GenericValidator.isBlankOrNull(testId)){
					Test test = new Test();
					test.setId(testId);
					testDAO.getData(test);
					data.setTestName(test.getReportingDescription());

					String uom = getUnitOfMeasure(referralResult.getResult(), test);
					if(reportReferralResultValue != null){
						data.setReferralResult(addIfNotEmpty(reportReferralResultValue, uom));
					}
					data.setTestRefRange(addIfNotEmpty(getRange(referralResult.getResult()), uom));
					data.setTestSortOrder(GenericValidator.isBlankOrNull(test.getSortOrder()) ? Integer.MAX_VALUE : Integer.parseInt(test
							.getSortOrder()));
					data.setSectionSortOrder(reportAnalysis.getTestSection().getSortOrderInt());
					data.setTestSection(reportAnalysis.getTestSection().getLocalizedName());
				}

				if(GenericValidator.isBlankOrNull(reportReferralResultValue)){
					sampleCompleteMap.put(reportSample.getAccessionNumber(), Boolean.FALSE);
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

	private void copyParentData(HaitiClinicalPatientData data, HaitiClinicalPatientData parentData){
		data.setContactInfo(parentData.getContactInfo());
		data.setSiteInfo(parentData.getSiteInfo());
		data.setReceivedDate(parentData.getReceivedDate());
		data.setDob(parentData.getDob());
		data.setAge(parentData.getAge());
		data.setGender(parentData.getGender());
		data.setNationalId(parentData.getNationalId());
		data.setPatientName(parentData.getPatientName());
		data.setFirstName(parentData.getFirstName());
		data.setLastName(parentData.getLastName());
		data.setDept(parentData.getDept());
		data.setCommune(parentData.getCommune());
		data.setStNumber(parentData.getStNumber());
		data.setAccessionNumber(parentData.getAccessionNumber());
	}

	@Override
	protected void postSampleBuild(){
		if(reportItems.isEmpty()){
			HaitiClinicalPatientData reportItem = reportAnalysisResults();
			reportItem.setTestSection(StringUtil.getMessageForKey("report.no.results"));
			clinicalReportItems.add(reportItem);
		}else{
			buildReport();
		}

	}

	private void buildReport(){
		Collections.sort(reportItems, new Comparator<HaitiClinicalPatientData>(){
			@Override
			public int compare(HaitiClinicalPatientData o1, HaitiClinicalPatientData o2){

				String o1AccessionNumber = AccessionNumberUtil.getAccessionNumberFromSampleItemAccessionNumber(o1.getAccessionNumber());
				String o2AccessionNumber = AccessionNumberUtil.getAccessionNumberFromSampleItemAccessionNumber(o2.getAccessionNumber());
				int accessionSort = o1AccessionNumber.compareTo(o2AccessionNumber);

				if(accessionSort != 0){
					return accessionSort;
				}

				if(o1.getSectionSortOrder() > o2.getSectionSortOrder()){
					return 1;
				}else if(o1.getSectionSortOrder() < o2.getSectionSortOrder()){
					return -1;
				}

				int o1Panel = Integer.MAX_VALUE;
				int o2Panel = Integer.MAX_VALUE;
				if(o1.getPanel() != null){
					o1Panel = o1.getPanel().getSortOrderInt();
				}
				if(o2.getPanel() != null){
					o2Panel = o2.getPanel().getSortOrderInt();
				}

				int panelSort = o1Panel - o2Panel;

				if(panelSort != 0){
					return panelSort;
				}

				return o1.getTestSortOrder() - o2.getTestSortOrder();
			}
		});

		String currentPanelId = null;
		for(HaitiClinicalPatientData reportItem : reportItems){
			if(reportItem.getPanel() != null && !reportItem.getPanel().getId().equals(currentPanelId)){
				currentPanelId = reportItem.getPanel().getId();
				reportItem.setSeparator(true);
			}else if(reportItem.getPanel() == null && currentPanelId != null){
				currentPanelId = null;
				reportItem.setSeparator(true);
			}

			reportItem.setAccessionNumber(reportItem.getAccessionNumber().split("-")[0]);
			reportItem.setCompleteFlag(StringUtil.getMessageForKey(sampleCompleteMap.get(reportItem.getAccessionNumber()) ? "report.status.complete" : "report.status.partial"));
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

		return errorFound ? new JRBeanCollectionDataSource(errorMsgs) : new JRBeanCollectionDataSource(reportItems);
	}

	@Override
	protected String getSiteLogo(){
		return isLNSP ? "HaitiLNSP.jpg" : "labLogo.jpg";
	}

	@Override
	protected void initializeReportItems(){
		super.initializeReportItems();
		clinicalReportItems = new ArrayList<HaitiClinicalPatientData>();
	}

	@Override
	protected void setReferredResult(HaitiClinicalPatientData data, Result result){
		data.setResult(data.getResult() + (augmentResultWithFlag() ? getResultFlag(result, "R") : ""));
		data.setAlerts(getResultFlag(result, "R"));
	}

	@Override
	protected boolean appendUOMToRange(){
		return false;
	}

	@Override
	protected boolean augmentResultWithFlag(){
		return false;
	}

	@Override
	protected boolean useReportingDescription(){
		return true;
	}

}
