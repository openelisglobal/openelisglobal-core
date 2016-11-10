package us.mn.state.health.lims.reports.action.implementation;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.validator.GenericValidator;
import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.services.ReportTrackingService;
import us.mn.state.health.lims.common.services.StatusService;
import us.mn.state.health.lims.common.services.TestService;
import us.mn.state.health.lims.common.services.StatusService.AnalysisStatus;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.reports.action.implementation.reportBeans.EIDReportData;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.sampleorganization.dao.SampleOrganizationDAO;
import us.mn.state.health.lims.sampleorganization.daoimpl.SampleOrganizationDAOImpl;
import us.mn.state.health.lims.sampleorganization.valueholder.SampleOrganization;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public abstract class PatientEIDReport extends RetroCIPatientReport {


    protected static final long YEAR = 1000L * 60L * 60L * 24L * 365L;
	protected static final long THREE_YEARS = YEAR * 3L;
	protected static final long WEEK = YEAR / 52L;
	protected static final long MONTH = YEAR / 12L;

	protected List<EIDReportData> reportItems;
	private String invalidValue = StringUtil.getMessageForKey("report.test.status.inProgress");

	protected void initializeReportItems() {
		reportItems = new ArrayList<EIDReportData>();
	}

	protected String getReportNameForReport() {
		return StringUtil.getMessageForKey("reports.label.patient.EID");
	}

	public JRDataSource getReportDataSource() throws IllegalStateException {
		if (!initialized) {
			throw new IllegalStateException("initializeReport not called first");
		}

		return errorFound ? new JRBeanCollectionDataSource(errorMsgs) : new JRBeanCollectionDataSource(reportItems);
	}

	protected void createReportItems() {
		EIDReportData data = new EIDReportData();

		setPatientInfo(data);
		setTestInfo(data);
		setPreviousTestInfo(data);//System.out.println("previousResultMap="+data.getPreviousResultMap());

		reportItems.add(data);

	}

	protected void setTestInfo(EIDReportData data) {
		boolean atLeastOneAnalysisNotValidated = false;
		AnalysisDAO analysisDAO = new AnalysisDAOImpl();
		List<Analysis> analysisList = analysisDAO.getAnalysesBySampleId(reportSample.getId());
		DictionaryDAO dictionaryDAO = new DictionaryDAOImpl();
		Timestamp lastReport = new ReportTrackingService().getTimeOfLastNamedReport(reportSample, ReportTrackingService.ReportType.PATIENT, requestedReport);
		Boolean mayBeDuplicate = lastReport != null;
		ResultDAO resultDAO = new ResultDAOImpl();

		Date maxCompleationDate = null;
		long maxCompleationTime = 0L;
		String invalidValue = StringUtil.getMessageForKey("report.test.status.inProgress");

		for (Analysis analysis : analysisList) {

			if (analysis.getCompletedDate() != null) {
				if (analysis.getCompletedDate().getTime() > maxCompleationTime) {
					maxCompleationDate = analysis.getCompletedDate();
					maxCompleationTime = maxCompleationDate.getTime();
				}

			}

			String testName = TestService.getUserLocalizedTestName( analysis.getTest() );

			List<Result> resultList = resultDAO.getResultsByAnalysis(analysis);
			

			boolean valid = ANALYSIS_FINALIZED_STATUS_ID.equals(analysis.getStatusId());
			if (!valid) {
				atLeastOneAnalysisNotValidated = true;
			}

			if (testName.equals("DNA PCR")) {
				if (valid) {
					String resultValue = "";
					if( resultList.size() > 0){
						resultValue = resultList.get( resultList.size() - 1).getValue();
					}
					Dictionary dictionary = new Dictionary();
					dictionary.setId(resultValue);
					dictionaryDAO.getData(dictionary);
					data.setHiv_status(dictionary.getDictEntry());
				} else {
					data.setHiv_status(invalidValue);
				}
			}
			if( mayBeDuplicate &&
					StatusService.getInstance().matches( analysis.getStatusId(), AnalysisStatus.Finalized) &&
					lastReport.before(analysis.getLastupdated())){
				mayBeDuplicate = false;
			}
		}
		if (maxCompleationDate != null) {
			data.setCompleationdate(DateUtil.convertSqlDateToStringDate(maxCompleationDate));
		}

		String observation = getObservationValues(OBSERVATION_WHICH_PCR_ID);

		if (!GenericValidator.isBlankOrNull(observation)) {
			Dictionary dictionary = new Dictionary();
			dictionary.setId(observation);
			dictionaryDAO.getData(dictionary);
			data.setPcr_type(dictionary.getDictEntry());
		}
		data.setDuplicateReport(mayBeDuplicate);
		data.setStatus(atLeastOneAnalysisNotValidated ? StringUtil.getMessageForKey("report.status.partial") : StringUtil
				.getMessageForKey("report.status.complete"));
	}

	protected void setPatientInfo(EIDReportData data) {

		SampleOrganizationDAO orgDAO = new SampleOrganizationDAOImpl();

		data.setSubjectno(reportPatient.getNationalId());
		data.setSitesubjectno(reportPatient.getExternalId());
		data.setBirth_date(reportPatient.getBirthDateForDisplay());
		data.setGender(reportPatient.getGender());
		data.setCollectiondate( DateUtil.convertTimestampToStringDateAndTime(reportSample.getCollectionDate()));
		SampleOrganization sampleOrg = new SampleOrganization();
		sampleOrg.setSample(reportSample);
		orgDAO.getDataBySample(sampleOrg);
		data.setServicename(sampleOrg.getId() == null ? "" : sampleOrg.getOrganization().getOrganizationName());
		data.setAccession_number(reportSample.getAccessionNumber());
		data.setReceptiondate( DateUtil.convertTimestampToStringDateAndTime(reportSample.getReceivedTimestamp()));           

		Timestamp collectionDate = reportSample.getCollectionDate();

		if (collectionDate != null) {
			long collectionTime = collectionDate.getTime() - reportPatient.getBirthDate().getTime();

			if (collectionTime < THREE_YEARS) {
				data.setAgeWeek(String.valueOf((int) Math.floor(collectionTime / WEEK)));
			} else {
				data.setAgeMonth(String.valueOf((int) Math.floor(collectionTime / MONTH)));
			}

		}
		data.getSampleQaEventItems(reportSample);
	}

	protected String getProjectId() {
		return EID_STUDY_ID;
	}

	protected void setPreviousTestInfo(EIDReportData data){
		AnalysisDAO analysisDAO = new AnalysisDAOImpl();
		DictionaryDAO dictionaryDAO = new DictionaryDAOImpl();
		ResultDAO resultDAO = new ResultDAOImpl();
	
		if( GenericValidator.isBlankOrNull(StringUtil.getMessageForKey("previous.test.to.report")))
		return;
		
		String[] testList=StringUtil.getMessageForKey("previous.test.to.report").split(",");
		
		for(int i=0;i<testList.length;i++){
		Analysis analysis=analysisDAO.getPatientPreviousAnalysisForTestName(reportPatient, reportSample, testList[i].trim());
			if(analysis!=null && !analysis.getStatusId().equals(StatusService.getInstance().getStatusID(AnalysisStatus.Canceled))){
				String testName = TestService.getUserLocalizedTestName( analysis.getTest() );
	
				List<Result> resultList = resultDAO.getResultsByAnalysis(analysis);
				String resultValue = null;
	
				boolean valid = ANALYSIS_FINALIZED_STATUS_ID.equals(analysis.getStatusId());
			//	if(!valid){
			//		atLeastOneAnalysisNotValidated = true;
			//	}
				// there may be more than one result for an analysis if one of
				// them
				// is a conclusion
				if(resultList.size() > 1){
					for(Result result : resultList){
						if(result.getAnalyte() != null && result.getAnalyte().getId().equals(CONCLUSION_ID)){
							Dictionary dictionary = new Dictionary();
							dictionary.setId(result.getValue());
							dictionaryDAO.getData(dictionary);
							data.getPreviousResultMap().put("Vih", valid ? dictionary.getDictEntry() : invalidValue);//data.setVih(valid ? dictionary.getDictEntry() : invalidValue);
							//data.setShowSerologie(Boolean.TRUE);
						}else if(result.getAnalyte() != null && result.getAnalyte().getId().equals(CD4_CNT_CONCLUSION)){
							//data.setCd4(valid ? result.getValue() : invalidValue);
							data.getPreviousResultMap().put("CD4 absolute count",valid ? result.getValue() : invalidValue);
						}else{
							resultValue = result.getValue();
						}
					}
				}
	
				if(resultList.size() > 0){
					if(resultValue == null){
						resultValue = resultList.get(resultList.size() - 1).getValue();
					}
				}
	
				if(resultValue != null || !valid){
					assignPreviousResultsToAVRReportData(data, testName, valid ? resultValue : invalidValue);
				}
			}
	
			
		}
	
	}

	private void assignPreviousResultsToAVRReportData(EIDReportData data, String testName, String resultValue){
	
		if(testName.equalsIgnoreCase("Viral Load") || testName.equalsIgnoreCase("Charge Virale")){
			//data.setShowVirologie(Boolean.TRUE);
			// Results entered via analyzer have log value, results entered
			// manually may not
			String baseValue = resultValue;
			if(!GenericValidator.isBlankOrNull(resultValue) && resultValue.contains("(")){
				String[] splitValue = resultValue.split("\\(");
				data.getPreviousResultMap().put("Ampli2", splitValue[0]);//data.setAmpli2(splitValue[0]);
				baseValue = splitValue[0];
			}else{
				data.getPreviousResultMap().put("Ampli2", resultValue);//data.setAmpli2(resultValue);
			}
			if(!GenericValidator.isBlankOrNull(baseValue) && !"0".equals(baseValue)){
				try{
					double viralLoad = Double.parseDouble(baseValue);
					data.getPreviousResultMap().put("Ampli2lo", String.format("%.3g%n", Math.log10(viralLoad)));//data.setAmpli2lo(String.format("%.3g%n", Math.log10(viralLoad)));
				}catch(NumberFormatException nfe){
					data.getPreviousResultMap().put("Ampli2lo","");//data.setAmpli2lo("");
				}
			}
	
		}else if(testName.equals("Murex") || testName.equals("Intgral") || testName.equals("Integral")){ //Serology must have one of these but not necessarily both
		//	data.setShowSerologie(Boolean.TRUE);
		//	if(GenericValidator.isBlankOrNull(data.getVih())){
			//	data.setVih(invalidValue);
		//	}
		}else {
			data.getPreviousResultMap().put(testName, resultValue);
			
		}
	}

}
