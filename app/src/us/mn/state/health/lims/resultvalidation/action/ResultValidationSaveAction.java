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
package us.mn.state.health.lims.resultvalidation.action;

import org.apache.commons.validator.GenericValidator;
import org.apache.struts.Globals;
import org.apache.struts.action.*;
import org.hibernate.Transaction;
import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.services.IResultSaveService;
import us.mn.state.health.lims.common.services.StatusService;
import us.mn.state.health.lims.common.services.StatusService.AnalysisStatus;
import us.mn.state.health.lims.common.services.StatusService.OrderStatus;
import us.mn.state.health.lims.common.services.registration.ValidationUpdateRegister;
import us.mn.state.health.lims.common.services.registration.interfaces.IResultUpdate;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.note.dao.NoteDAO;
import us.mn.state.health.lims.note.daoimpl.NoteDAOImpl;
import us.mn.state.health.lims.note.util.NoteUtil;
import us.mn.state.health.lims.note.valueholder.Note;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.referencetables.daoimpl.ReferenceTablesDAOImpl;
import us.mn.state.health.lims.reports.dao.DocumentTrackDAO;
import us.mn.state.health.lims.reports.daoimpl.DocumentTrackDAOImpl;
import us.mn.state.health.lims.reports.daoimpl.DocumentTypeDAOImpl;
import us.mn.state.health.lims.reports.valueholder.DocumentTrack;
import us.mn.state.health.lims.result.action.util.ResultSet;
import us.mn.state.health.lims.result.action.util.ResultsLoadUtility;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.resultvalidation.action.util.ResultValidationPaging;
import us.mn.state.health.lims.resultvalidation.bean.AnalysisItem;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.samplehuman.dao.SampleHumanDAO;
import us.mn.state.health.lims.samplehuman.daoimpl.SampleHumanDAOImpl;
import us.mn.state.health.lims.systemuser.dao.SystemUserDAO;
import us.mn.state.health.lims.systemuser.daoimpl.SystemUserDAOImpl;
import us.mn.state.health.lims.systemuser.valueholder.SystemUser;
import us.mn.state.health.lims.testanalyte.dao.TestAnalyteDAO;
import us.mn.state.health.lims.testanalyte.daoimpl.TestAnalyteDAOImpl;
import us.mn.state.health.lims.testanalyte.valueholder.TestAnalyte;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.*;

public class ResultValidationSaveAction extends BaseResultValidationAction implements IResultSaveService  {

	private static final DecimalFormat TWO_DECIMAL_FORMAT = new DecimalFormat("##.##");
	// DAOs
	private static final AnalysisDAO analysisDAO = new AnalysisDAOImpl();
	private static final SampleDAO sampleDAO = new SampleDAOImpl();
	private static final TestResultDAO testResultDAO = new TestResultDAOImpl();
	private static final TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();
	private static final ResultDAO resultDAO = new ResultDAOImpl();
	private static final NoteDAO noteDAO = new NoteDAOImpl();
	private static final SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
	private static final DocumentTrackDAO documentTrackDAO =  new DocumentTrackDAOImpl();
	
	// Update Lists
	private List<Analysis> analysisUpdateList;
	private ArrayList<Sample> sampleUpdateList;
	private ArrayList<Note> noteUpdateList;
	private ArrayList<Result> resultUpdateList;

	private SystemUser systemUser;
	private ArrayList<Integer> sampleFinishedStatus = new ArrayList<Integer>();
	private List<ResultSet> modifiedResultSet;
	private List<ResultSet> newResultSet;

	private static final String RESULT_SUBJECT = "Result Note";
	private static final String RESULT_TABLE_ID;
	private static final String RESULT_REPORT_ID;
	
	
	static{
		RESULT_TABLE_ID = new ReferenceTablesDAOImpl().getReferenceTableByName("RESULT").getId();		
		RESULT_REPORT_ID = new DocumentTypeDAOImpl().getDocumentTypeByName("resultExport").getId();
	}
	
	@Override
	protected ActionForward performAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception{

		
		String forward = FWD_SUCCESS;

		List<IResultUpdate> updaters = ValidationUpdateRegister.getRegisteredUpdaters();
		boolean areListeners = updaters != null && !updaters.isEmpty(); 
		
		request.getSession().setAttribute(SAVE_DISABLED, "true");

		BaseActionForm dynaForm = (BaseActionForm)form;

		ResultValidationPaging paging = new ResultValidationPaging();
		paging.updatePagedResults(request, dynaForm);
		List<AnalysisItem> resultItemList = paging.getResults(request);

		String testSectionName = (String)dynaForm.get("testSection");
		String testName = (String)dynaForm.get("testName");
		setRequestType(testSectionName);
		
		ActionMessages errors = validateModifiedItems(resultItemList);

		if(errors.size() > 0){
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			return mapping.findForward(FWD_VALIDATION_ERROR);
		}
		
		createSystemUser();
		setSampleFinishedStatuses();

		noteUpdateList = new ArrayList<Note>();
		resultUpdateList = new ArrayList<Result>();
		analysisUpdateList = new ArrayList<Analysis>();
		modifiedResultSet = new ArrayList<ResultSet>();
		newResultSet = new ArrayList<ResultSet>();
		
		if(testSectionName.equals("serology")){
			createUpdateElisaList(resultItemList);
		}else{
			createUpdateList(resultItemList, areListeners);
		}

		Transaction tx = HibernateUtil.getSession().beginTransaction();

		try{

			// update analysis
			for(Analysis analysis : analysisUpdateList){
				analysisDAO.updateData(analysis);
			}

			for(Result result : resultUpdateList){
				if( result.getId() != null){
					resultDAO.updateData(result);
				}else{
					resultDAO.insertData(result);
				}
			}

			checkIfSamplesFinished(resultItemList);

			// update finished samples
			for(Sample sample : sampleUpdateList){
				sampleDAO.updateData(sample);
			}

			// create or update notes
			for(Note note : noteUpdateList){
				if(note != null){
					if(note.getId() == null){
						noteDAO.insertData(note);
					}else{
						noteDAO.updateData(note);
					}
				}
			}

			for(IResultUpdate updater : updaters){
				updater.transactionalUpdate(this);
			}
			
			tx.commit();

		}catch(LIMSRuntimeException lre){
			tx.rollback();
		}
		
		for(IResultUpdate updater : updaters){
			updater.postTransactionalCommitUpdate(this);
		}

		if(GenericValidator.isBlankOrNull(testSectionName)){
			return mapping.findForward(forward);
		}else{
			Map<String, String> params = new HashMap<String, String>();
			params.put("type", testSectionName);
			params.put("test", testName);
			params.put("forward", forward);

			return getForwardWithParameters(mapping.findForward(forward), params);
		}

	}

	private ActionMessages validateModifiedItems(List<AnalysisItem> resultItemList){
		ActionErrors errors = new ActionErrors();
		
		
		for (AnalysisItem item : resultItemList) {
			List<ActionError> errorList = new ArrayList<ActionError>();
			validateQuantifiableItems(item, errorList);
			

			if (errorList.size() > 0) {
				StringBuilder augmentedAccession = new StringBuilder(item.getAccessionNumber());
				augmentedAccession.append(" : ");
				augmentedAccession.append(item.getTestName());
				ActionError accessionError = new ActionError("errors.followingAccession", augmentedAccession);
				errors.add(ActionErrors.GLOBAL_MESSAGE, accessionError);

				for (ActionError error : errorList) {
					errors.add(ActionErrors.GLOBAL_MESSAGE, error);
				}

			}
		}

		return errors;
	}

	public void validateQuantifiableItems(AnalysisItem analysisItem, List<ActionError> errors){
		if( "Q".equals(analysisItem.getResultType()) && 
				GenericValidator.isBlankOrNull(analysisItem.getQualifiedResultValue()) &&
				analysisItemWillBeUpdated(analysisItem)){
			errors.add(new ActionError("errors.missing.result.details", new StringBuilder("Result")));
		}
		// verify that qualifiedResultValue has been entered if required
		if (!GenericValidator.isBlankOrNull(analysisItem.getQualifiedDictionaryId())) {
		    String[] qualifiedDictionaryIds = analysisItem.getQualifiedDictionaryId().replace("[", "").replace("]", "").split(",");
		    Set<String> qualifiedDictIdsSet = new HashSet<String>(Arrays.asList(qualifiedDictionaryIds));
		    
		    
		    if (qualifiedDictIdsSet.contains(analysisItem.getResult()) &&
		            GenericValidator.isBlankOrNull(analysisItem.getQualifiedResultValue())) {
		        errors.add(new ActionError("errors.missing.result.details", new StringBuilder("Result")));
		      
		    }

		}
				
		
	}


	
	private void createUpdateList(List<AnalysisItem> analysisItems, boolean areListeners){

		List<String> analysisIdList = new ArrayList<String>();

		for(AnalysisItem analysisItem : analysisItems){
			if(!analysisItem.isReadOnly() && analysisItemWillBeUpdated(analysisItem)){
				String analysisId = analysisItem.getAnalysisId();

				Analysis analysis = new Analysis();
				analysis.setId(analysisId);
				analysisDAO.getData(analysis);
				analysis.setSysUserId(currentUserId);

				if(!analysisIdList.contains(analysisId)){

					if(analysisItem.getIsAccepted()){
						analysis.setStatusId(StatusService.getInstance().getStatusID(AnalysisStatus.Finalized));
						analysis.setReleasedDate(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
						analysisIdList.add(analysisId);
						analysisUpdateList.add(analysis);
					}

					if(analysisItem.getIsRejected()){
						analysis.setStatusId(StatusService.getInstance().getStatusID(AnalysisStatus.BiologistRejected));
						analysisIdList.add(analysisId);
						analysisUpdateList.add(analysis);
					}
				}

				createNote(analysisItem);

				if(areResults(analysisItem)){
					Result result = createResultFromAnalysisItem(analysisItem, analysis);
					resultUpdateList.add(result);
					
					if(areListeners){
						addResultSets(analysis, result);
					}
				}
			}
		}
	}

	private void addResultSets(Analysis analysis, Result result){
		Sample sample = analysis.getSampleItem().getSample();
		Patient patient = sampleHumanDAO.getPatientForSample(sample);
		List<DocumentTrack> documents =  documentTrackDAO.getByTypeRecordAndTable(RESULT_REPORT_ID, RESULT_TABLE_ID, result.getId());
		if( documents.isEmpty()){
			newResultSet.add(new ResultSet(result, null,null,null, patient, sample, null, null, null));
		}else{
			modifiedResultSet.add(new ResultSet(result, null,null,null, patient, sample, null, null, null));
		}
	}

	private boolean analysisItemWillBeUpdated(AnalysisItem analysisItem){
		return analysisItem.getIsAccepted() || analysisItem.getIsRejected();
	}

	private void createUpdateElisaList(List<AnalysisItem> resultItems){

		for(AnalysisItem resultItem : resultItems){

			if(resultItem.getIsAccepted()){

				List<Analysis> acceptedAnalysisList = createAnalysisFromElisaAnalysisItem(resultItem);

				for(Analysis analysis : acceptedAnalysisList){
					analysis.setStatusId(StatusService.getInstance().getStatusID(AnalysisStatus.Finalized));
					analysisUpdateList.add(analysis);
				}
			}

			if(resultItem.getIsRejected()){
				List<Analysis> rejectedAnalysisList = createAnalysisFromElisaAnalysisItem(resultItem);

				for(Analysis analysis : rejectedAnalysisList){
					analysis.setStatusId(StatusService.getInstance().getStatusID(AnalysisStatus.BiologistRejected));
					analysisUpdateList.add(analysis);
				}

			}
		}
	}

	private List<Analysis> createAnalysisFromElisaAnalysisItem(AnalysisItem analysisItem){

		List<Analysis> analysisList = new ArrayList<Analysis>();

		Analysis analysis = new Analysis();

		if(!GenericValidator.isBlankOrNull(analysisItem.getMurexResult())){
			analysis = getAnalysisFromId(analysisItem.getMurexAnalysisId());
			analysisList.add(analysis);
		}
		if(!GenericValidator.isBlankOrNull(analysisItem.getBiolineResult())){
			analysis = getAnalysisFromId(analysisItem.getBiolineAnalysisId());
			analysisList.add(analysis);
		}
		if(!GenericValidator.isBlankOrNull(analysisItem.getIntegralResult())){
			analysis = getAnalysisFromId(analysisItem.getIntegralAnalysisId());
			analysisList.add(analysis);
		}
		if(!GenericValidator.isBlankOrNull(analysisItem.getVironostikaResult())){
			analysis = getAnalysisFromId(analysisItem.getVironostikaAnalysisId());
			analysisList.add(analysis);
		}
		if(!GenericValidator.isBlankOrNull(analysisItem.getGenieIIResult())){
			analysis = getAnalysisFromId(analysisItem.getGenieIIAnalysisId());
			analysisList.add(analysis);
		}
		if(!GenericValidator.isBlankOrNull(analysisItem.getGenieII10Result())){
			analysis = getAnalysisFromId(analysisItem.getGenieII10AnalysisId());
			analysisList.add(analysis);
		}
		if(!GenericValidator.isBlankOrNull(analysisItem.getGenieII100Result())){
			analysis = getAnalysisFromId(analysisItem.getGenieII100AnalysisId());
			analysisList.add(analysis);
		}
		if(!GenericValidator.isBlankOrNull(analysisItem.getWesternBlot1Result())){
			analysis = getAnalysisFromId(analysisItem.getWesternBlot1AnalysisId());
			analysisList.add(analysis);
		}
		if(!GenericValidator.isBlankOrNull(analysisItem.getWesternBlot2Result())){
			analysis = getAnalysisFromId(analysisItem.getWesternBlot2AnalysisId());
			analysisList.add(analysis);
		}
		if(!GenericValidator.isBlankOrNull(analysisItem.getP24AgResult())){
			analysis = getAnalysisFromId(analysisItem.getP24AgAnalysisId());
			analysisList.add(analysis);
		}
		if(!GenericValidator.isBlankOrNull(analysisItem.getInnoliaResult())){
			analysis = getAnalysisFromId(analysisItem.getInnoliaAnalysisId());
			analysisList.add(analysis);
		}

		analysisList.add(analysis);

		return analysisList;
	}

	private void checkIfSamplesFinished(List<AnalysisItem> resultItemList){
		sampleUpdateList = new ArrayList<Sample>();

		String currentSampleId = "";
		boolean sampleFinished = true;
		List<Analysis> analysisList = new ArrayList<Analysis>();

		for(AnalysisItem analysisItem : resultItemList){

			String analysisSampleId = sampleDAO.getSampleByAccessionNumber(analysisItem.getAccessionNumber()).getId();
			if(!analysisSampleId.equals(currentSampleId)){

				currentSampleId = analysisSampleId;

				analysisList = analysisDAO.getAnalysesBySampleId(currentSampleId);

				for(Analysis analysis : analysisList){
					if(!sampleFinishedStatus.contains(Integer.parseInt(analysis.getStatusId()))){
						sampleFinished = false;
						break;
					}
				}

				if(sampleFinished){
					Sample sample = new Sample();
					sample.setId(currentSampleId);
					sampleDAO.getData(sample);
					sample.setStatusId(StatusService.getInstance().getStatusID(OrderStatus.Finished));
					sampleUpdateList.add(sample);
				}

				sampleFinished = true;

			}

		}
	}

	private Analysis getAnalysisFromId(String id){
		Analysis analysis = new Analysis();
		analysis.setId(id);
		analysisDAO.getData(analysis);
		analysis.setSysUserId(currentUserId);

		return analysis;
	}

	private void createNote(AnalysisItem testResult){
		Note note = null;

		if(!GenericValidator.isBlankOrNull(testResult.getNoteId())){
			note = new Note();
			note.setId(testResult.getNoteId());
			noteDAO.getData(note);
		}else if(areNotes(testResult)){
			note = new Note();
			note.setReferenceId(testResult.getResultId());
			note.setReferenceTableId(ResultsLoadUtility.getResultReferenceTableId());
			note.setNoteType(NoteUtil.getDefaultNoteType(NoteUtil.NoteSource.VALIDATION));
			note.setSubject(RESULT_SUBJECT);
		}

		if(note != null){
			note.setText(testResult.getNote());
			note.setSysUserId(currentUserId);
			note.setSystemUser(systemUser);
			note.setSystemUserId(currentUserId);
			noteUpdateList.add(note);
		}

	}

	private Result createResultFromAnalysisItem(AnalysisItem analysisItem, Analysis analysis){

		Result result = new Result();

		if(GenericValidator.isBlankOrNull(analysisItem.getResultId())){
			result.setAnalysis(analysis);
			result.setAnalysisId(analysisItem.getAnalysisId());
			// alphanumeric is the only supported resultType currently
			result.setResultType("A");
			TestAnalyte testAnalyte = getTestAnalyteForResult(result);

			if(testAnalyte != null){
				result.setAnalyte(testAnalyte.getAnalyte());
			}

		}else{
			result.setId(analysisItem.getResultId());
			resultDAO.getData(result);
		}

		TestResult testResult = getTestResult(analysisItem);
		// changing from quantifiable
		if("Q".equals(result.getTestResult().getTestResultType())){
			String quanifiedValue = "";
			if("Q".equals(testResult.getTestResultType())){
				// just the qualifier value has changed
				quanifiedValue = analysisItem.getQualifiedResultValue();
			}
			
			List<Result> children = resultDAO.getChildResults(result.getId());
			if(!children.isEmpty()){
				updateExitingQuntifieableResult(quanifiedValue, children);
			}
			//changing to quantifiable from non-quantifiable
		}else if("Q".equals(testResult.getTestResultType())){
			List<Result> children = resultDAO.getChildResults(result.getId());
			if(children.isEmpty()){
				Result quantifiedResult = new Result();
				quantifiedResult.setAnalysis(analysis);
				quantifiedResult.setAnalysisId(analysisItem.getAnalysisId());
				// alphanumeric is the only supported resultType currently
				quantifiedResult.setResultType("A");
				TestAnalyte testAnalyte = getTestAnalyteForResult(quantifiedResult);

				if(testAnalyte != null){
					quantifiedResult.setAnalyte(testAnalyte.getAnalyte());
				}
				quantifiedResult.setValue(analysisItem.getQualifiedResultValue());
				quantifiedResult.setSysUserId(currentUserId);
				quantifiedResult.setSortOrder("0");
				quantifiedResult.setParentResult(result);
				resultUpdateList.add(quantifiedResult);
			}else{
				updateExitingQuntifieableResult(analysisItem.getQualifiedResultValue(), children);
			}
		}

		if(testResult != null){
			result.setTestResult(testResult);
		}

		if(analysisItem.getResult() != null && !analysisItem.getResult().equals(result.getValue())){
			String analysisResult = analysisItem.getResult();
			if(analysisItem.isDisplayResultAsLog()){
				try{
					Double value = Math.log10(Double.parseDouble(analysisResult));
					analysisResult += "(" + String.valueOf(Double.valueOf(TWO_DECIMAL_FORMAT.format(value))) + ")";
				}catch(NumberFormatException e){
					// no-op use original number
				}
			}

			result.setValue(analysisResult);
			analysis.setRevision(String.valueOf(Integer.parseInt(analysis.getRevision()) + 1));
			analysis.setEnteredDate(DateUtil.getNowAsTimestamp());
		}
		result.setSysUserId(currentUserId);
		result.setSortOrder("0");

		return result;
	}

	private void updateExitingQuntifieableResult(String quanifiedValue, List<Result> children){
		Result quantifiedResult = children.get(0);
		quantifiedResult.setValue(quanifiedValue);
		quantifiedResult.setSysUserId(currentUserId);
		quantifiedResult.setSortOrder("0");
		resultUpdateList.add(quantifiedResult);
	}

	protected TestResult getTestResult(AnalysisItem analysisItem){
		TestResult testResult = null;
		if("DQ".contains(analysisItem.getResultType())){
			testResult = testResultDAO.getTestResultsByTestAndDictonaryResult(analysisItem.getTestId(), analysisItem.getResult());
		}else{
			List<TestResult> testResultList = testResultDAO.getTestResultsByTest(analysisItem.getTestId());
			// we are assuming there is only one testResult for a numeric type
			// result
			if(!testResultList.isEmpty()){
				testResult = testResultList.get(0);
			}
		}
		return testResult;
	}

	private TestAnalyte getTestAnalyteForResult(Result result){

		if(result.getTestResult() != null){
			@SuppressWarnings("unchecked")
			List<TestAnalyte> testAnalyteList = testAnalyteDAO.getAllTestAnalytesPerTest(result.getTestResult().getTest());

			if(testAnalyteList.size() > 0){
				int distanceFromRoot = 0;

				Analysis parentAnalysis = result.getAnalysis().getParentAnalysis();

				while(parentAnalysis != null){
					distanceFromRoot++;
					parentAnalysis = parentAnalysis.getParentAnalysis();
				}

				int index = Math.min(distanceFromRoot, testAnalyteList.size() - 1);

				return testAnalyteList.get(index);
			}
		}
		return null;
	}

	private boolean areNotes(AnalysisItem item){
		return !GenericValidator.isBlankOrNull(item.getNote());
	}

	private boolean areResults(AnalysisItem item){
		return !(GenericValidator.isBlankOrNull(item.getResult()) || ("D".equals(item.getResultType()) && "0".equals(item.getResult())));
	}

	private void createSystemUser(){
		systemUser = new SystemUser();
		systemUser.setId(currentUserId);
		SystemUserDAO systemUserDAO = new SystemUserDAOImpl();
		systemUserDAO.getData(systemUser);
	}

	private void setSampleFinishedStatuses(){
		sampleFinishedStatus = new ArrayList<Integer>();
		sampleFinishedStatus.add(Integer.parseInt(StatusService.getInstance().getStatusID(AnalysisStatus.Finalized)));
		sampleFinishedStatus.add(Integer.parseInt(StatusService.getInstance().getStatusID(AnalysisStatus.Canceled)));
		sampleFinishedStatus.add(Integer.parseInt(StatusService.getInstance().getStatusID(AnalysisStatus.NonConforming_depricated)));
	}

	@Override
	public String getCurrentUserId(){
		return currentUserId;
	}

	@Override
	public List<ResultSet> getNewResults(){
		return newResultSet;
	}

	@Override
	public List<ResultSet> getModifiedResults(){
		return modifiedResultSet;
	}

}
