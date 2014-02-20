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
package us.mn.state.health.lims.result.action;

import org.apache.commons.validator.GenericValidator;
import org.apache.struts.Globals;
import org.apache.struts.action.*;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.formfields.FormFields;
import us.mn.state.health.lims.common.formfields.FormFields.Field;
import us.mn.state.health.lims.common.services.IResultSaveService;
import us.mn.state.health.lims.common.services.StatusService;
import us.mn.state.health.lims.common.services.StatusService.AnalysisStatus;
import us.mn.state.health.lims.common.services.StatusService.OrderStatus;
import us.mn.state.health.lims.common.services.registration.ResultUpdateRegister;
import us.mn.state.health.lims.common.services.registration.interfaces.IResultUpdate;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.ConfigurationProperties.Property;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.note.dao.NoteDAO;
import us.mn.state.health.lims.note.daoimpl.NoteDAOImpl;
import us.mn.state.health.lims.note.util.NoteUtil;
import us.mn.state.health.lims.note.valueholder.Note;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.referral.dao.ReferralDAO;
import us.mn.state.health.lims.referral.dao.ReferralResultDAO;
import us.mn.state.health.lims.referral.dao.ReferralTypeDAO;
import us.mn.state.health.lims.referral.daoimpl.ReferralDAOImpl;
import us.mn.state.health.lims.referral.daoimpl.ReferralResultDAOImpl;
import us.mn.state.health.lims.referral.daoimpl.ReferralTypeDAOImpl;
import us.mn.state.health.lims.referral.valueholder.Referral;
import us.mn.state.health.lims.referral.valueholder.ReferralResult;
import us.mn.state.health.lims.referral.valueholder.ReferralType;
import us.mn.state.health.lims.result.action.util.*;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.dao.ResultInventoryDAO;
import us.mn.state.health.lims.result.dao.ResultSignatureDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.daoimpl.ResultInventoryDAOImpl;
import us.mn.state.health.lims.result.daoimpl.ResultSignatureDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.result.valueholder.ResultInventory;
import us.mn.state.health.lims.result.valueholder.ResultSignature;
import us.mn.state.health.lims.resultlimits.dao.ResultLimitDAO;
import us.mn.state.health.lims.resultlimits.daoimpl.ResultLimitDAOImpl;
import us.mn.state.health.lims.resultlimits.valueholder.ResultLimit;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.samplehuman.dao.SampleHumanDAO;
import us.mn.state.health.lims.samplehuman.daoimpl.SampleHumanDAOImpl;
import us.mn.state.health.lims.samplehuman.valueholder.SampleHuman;
import us.mn.state.health.lims.test.beanItems.TestResultItem;
import us.mn.state.health.lims.testanalyte.valueholder.TestAnalyte;
import us.mn.state.health.lims.testreflex.action.util.TestReflexBean;
import us.mn.state.health.lims.testreflex.action.util.TestReflexUtil;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.*;

public class ResultsLogbookUpdateAction extends BaseAction implements IResultSaveService{

	private List<TestResultItem> modifiedItems;
	private List<ResultSet> modifiedResults;
	private List<ResultSet> newResults;
	private List<Analysis> modifiedAnalysis;
	private List<Result> deletableResults;
	private AnalysisDAO analysisDAO = new AnalysisDAOImpl();
	private ResultDAO resultDAO = new ResultDAOImpl();
	private TestResultDAO testResultDAO = new TestResultDAOImpl();
	private ResultSignatureDAO resultSigDAO = new ResultSignatureDAOImpl();
	private ResultInventoryDAO resultInventoryDAO = new ResultInventoryDAOImpl();
	private NoteDAO noteDAO = new NoteDAOImpl();
	private SampleDAO sampleDAO = new SampleDAOImpl();
	private SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
	private ReferralDAO referralDAO = new ReferralDAOImpl();
	private ReferralResultDAO referralResultDAO = new ReferralResultDAOImpl();
	private ResultLimitDAO resultLimitDAO = new ResultLimitDAOImpl();

	private static final String RESULT_SUBJECT = "Result Note";
	private static String REFERRAL_CONFORMATION_ID;

	private boolean useTechnicianName = ConfigurationProperties.getInstance().isPropertyValueEqual(Property.resultTechnicianName, "true");
	private boolean alwaysValidate = ConfigurationProperties.getInstance().isPropertyValueEqual(Property.ALWAYS_VALIDATE_RESULTS, "true");
	private boolean supportReferrals = FormFields.getInstance().useField(Field.ResultsReferral);
	private String statusRuleSet = ConfigurationProperties.getInstance().getPropertyValueUpperCase(Property.StatusRules);
	private Analysis previousAnalysis;
	private ResultsValidation resultValidation = new ResultsValidation();

	static{
		ReferralTypeDAO referralTypeDAO = new ReferralTypeDAOImpl();
		ReferralType referralType = referralTypeDAO.getReferralTypeByName("Confirmation");
		if(referralType != null){
			REFERRAL_CONFORMATION_ID = referralType.getId();
		}
	}

	protected ActionForward performAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception{

		String forward = FWD_SUCCESS;
		List<IResultUpdate> updaters = ResultUpdateRegister.getRegisteredUpdaters();

		BaseActionForm dynaForm = (BaseActionForm)form;

		resultValidation.setSupportReferrals(supportReferrals);
		resultValidation.setUseTechnicianName(useTechnicianName);

		ResultsPaging paging = new ResultsPaging();
		paging.updatePagedResults(request, dynaForm);
		List<TestResultItem> tests = paging.getResults(request);

		setModifiedItems(tests);

		ActionMessages errors = resultValidation.validateModifiedItems(modifiedItems);

		if(errors.size() > 0){
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);

			return mapping.findForward(FWD_VALIDATION_ERROR);
		}

		initializeLists();
		createResultsFromItems();

		Transaction tx = HibernateUtil.getSession().beginTransaction();

		try{
			for(ResultSet resultSet : newResults){

				resultDAO.insertData(resultSet.result);

				if(resultSet.signature != null){
					resultSet.signature.setResultId(resultSet.result.getId());
					resultSigDAO.insertData(resultSet.signature);
				}

				if(resultSet.testKit != null && resultSet.testKit.getInventoryLocationId() != null){
					resultSet.testKit.setResultId(resultSet.result.getId());
					resultInventoryDAO.insertData(resultSet.testKit);
				}

				if(resultSet.note != null){
					resultSet.note.setReferenceId(resultSet.result.getId());
					noteDAO.insertData(resultSet.note);
				}

				if(resultSet.newReferral != null){
					insertNewReferralAndReferralResult(resultSet);
				}
			}

			for(ResultSet resultSet : modifiedResults){
				resultDAO.updateData(resultSet.result);

				if(resultSet.signature != null){
					resultSet.signature.setResultId(resultSet.result.getId());
					if(resultSet.alwaysInsertSignature){
						resultSigDAO.insertData(resultSet.signature);
					}else{
						resultSigDAO.updateData(resultSet.signature);
					}
				}

				if(resultSet.testKit != null && resultSet.testKit.getInventoryLocationId() != null){
					resultSet.testKit.setResultId(resultSet.result.getId());
					if(resultSet.testKit.getId() == null){
						resultInventoryDAO.insertData(resultSet.testKit);
					}else{
						resultInventoryDAO.updateData(resultSet.testKit);
					}
				}

				if(resultSet.note != null){
					resultSet.note.setReferenceId(resultSet.result.getId());
					if(resultSet.note.getId() == null){
						noteDAO.insertData(resultSet.note);
					}else{
						noteDAO.updateData(resultSet.note);
					}
				}

				if(resultSet.newReferral != null){
					// we can't just create a referral with a blank result,
					// because referral page assumes a referralResult and a
					// result.
					insertNewReferralAndReferralResult(resultSet);
				}

				if(resultSet.existingReferral != null){
					referralDAO.updateData(resultSet.existingReferral);
				}
			}

			for(Analysis analysis : modifiedAnalysis){
				analysisDAO.updateData(analysis);
			}

			removeDeletedResults();

			setTestReflexes();

			setSampleStatus();

			for(IResultUpdate updater : updaters){
				updater.transactionalUpdate(this);
			}

			tx.commit();

		}catch(LIMSRuntimeException lre){
			tx.rollback();

			ActionError error;
			if(lre.getException() instanceof StaleObjectStateException){
				error = new ActionError("errors.OptimisticLockException", null, null);
			}else{
				lre.printStackTrace();
				error = new ActionError("errors.UpdateException", null, null);
			}

			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);

			return mapping.findForward(FWD_FAIL);

		}

		for(IResultUpdate updater : updaters){
			updater.postTransactionalCommitUpdate(this);
		}

		setSuccessFlag(request, forward);

		if(GenericValidator.isBlankOrNull(dynaForm.getString("logbookType"))){
			return mapping.findForward(forward);
		}else{
			Map<String, String> params = new HashMap<String, String>();
			params.put("type", dynaForm.getString("logbookType"));
			params.put("forward", forward);
			return getForwardWithParameters(mapping.findForward(forward), params);
		}
	}

	private void insertNewReferralAndReferralResult(ResultSet resultSet){
		referralDAO.insertData(resultSet.newReferral);
		ReferralResult referralResult = new ReferralResult();
		referralResult.setReferralId(resultSet.newReferral.getId());
		referralResult.setSysUserId(currentUserId);
		referralResultDAO.insertData(referralResult);
	}

	private void removeDeletedResults(){
		for(Result result : deletableResults){
			List<ResultSignature> signatures = resultSigDAO.getResultSignaturesByResult(result);
			List<ReferralResult> referrals = referralResultDAO.getReferralsByResultId(result.getId());

			for(ResultSignature signature : signatures){
				signature.setSysUserId(currentUserId);
			}

			resultSigDAO.deleteData(signatures);

			for(ReferralResult referral : referrals){
				referral.setSysUserId(currentUserId);
				referralResultDAO.deleteData(referral);
			}

			result.setSysUserId(currentUserId);
			resultDAO.deleteData(result);
		}

	}

	protected void setTestReflexes(){
		TestReflexUtil testReflexUtil = new TestReflexUtil();
		testReflexUtil.setCurrentUserId(currentUserId);
		testReflexUtil.addNewTestsToDBForReflexTests(convertToTestReflexBeanList(newResults));
		testReflexUtil.updateModifiedReflexes(convertToTestReflexBeanList(modifiedResults));
	}

	private List<TestReflexBean> convertToTestReflexBeanList(List<ResultSet> resultSetList){
		List<TestReflexBean> reflexBeanList = new ArrayList<TestReflexBean>();

		for(ResultSet resultSet : resultSetList){
			TestReflexBean reflex = new TestReflexBean();
			reflex.setPatient(resultSet.patient);
            reflex.setTriggersToSelectedReflexesMap( resultSet.triggersToSelectedReflexesMap );
			reflex.setResult(resultSet.result);
			reflex.setSample(resultSet.sample);
			reflexBeanList.add(reflex);
		}

		return reflexBeanList;
	}

	private void setSampleStatus(){
		Set<Sample> sampleSet = new HashSet<Sample>();

		for(ResultSet resultSet : newResults){
			sampleSet.add(resultSet.sample);
		}

		String sampleTestingStartedId = StatusService.getInstance().getStatusID(OrderStatus.Started);
		String sampleNonConformingId = StatusService.getInstance().getStatusID(OrderStatus.NonConforming_depricated);

		for(Sample sample : sampleSet){
			if(!(sample.getStatusId().equals(sampleNonConformingId) || sample.getStatusId().equals(sampleTestingStartedId))){
				Sample newSample = new Sample();
				newSample.setId(sample.getId());
				sampleDAO.getData(newSample);

				newSample.setStatusId(sampleTestingStartedId);
				newSample.setSysUserId(currentUserId);
				sampleDAO.updateData(newSample);
			}
		}
	}

	private void setModifiedItems(List<TestResultItem> allItems){
		modifiedItems = new ArrayList<TestResultItem>();

		for(TestResultItem item : allItems){
			if(isModified(item)){
				modifiedItems.add(item);
			}
		}
	}

	private boolean isModified(TestResultItem item){
		return item.getIsModified()
				&& (ResultUtil.areResults(item) || ResultUtil.areNotes(item) || ResultUtil.isReferred(item) || ResultUtil.isForcedToAcceptance(item));
	}

	private void createResultsFromItems(){

		for(TestResultItem testResultItem : modifiedItems){

			Analysis analysis = analysisDAO.getAnalysisById(testResultItem.getAnalysisId());
			List<Result> results = createResultFromTestResultItem(testResultItem, analysis);

			for(Result result : results){
				addResult(result, testResultItem, analysis);

				if(analysisShouldBeUpdated(testResultItem, result)){
					updateAndAddAnalysisToModifiedList(testResultItem, testResultItem.getTestDate(), analysis);
				}
			}
		}
	}

	protected void initializeLists(){
		modifiedResults = new ArrayList<ResultSet>();
		newResults = new ArrayList<ResultSet>();
		modifiedAnalysis = new ArrayList<Analysis>();
		deletableResults = new ArrayList<Result>();
	}

	protected boolean analysisShouldBeUpdated(TestResultItem testResultItem, Result result){
		return result != null && !GenericValidator.isBlankOrNull(result.getValue())
				|| (supportReferrals && ResultUtil.isReferred(testResultItem))
				|| ResultUtil.isForcedToAcceptance(testResultItem);
	}

	private void addResult(Result result, TestResultItem testResultItem, Analysis analysis){
		boolean newResult = result.getId() == null;
		boolean newAnalysisInLoop = analysis != previousAnalysis;

		ResultSignature technicianResultSignature = null;

		if(useTechnicianName && newAnalysisInLoop){
			technicianResultSignature = createTechnicianSignatureFromResultItem(testResultItem);
		}

		ResultInventory testKit = createTestKitLinkIfNeeded(testResultItem, ResultsLoadUtility.TESTKIT);

		Note note = NoteUtil.createSavableNote(null, testResultItem.getNote(), testResultItem.getResultId(),
				ResultsLoadUtility.getResultReferenceTableId(), RESULT_SUBJECT, currentUserId, NoteUtil.getDefaultNoteType(NoteUtil.NoteSource.OTHER));

		analysis.setStatusId(getStatusForTestResult(testResultItem));
		analysis.setReferredOut(testResultItem.isReferredOut());
		analysis.setEnteredDate(DateUtil.getNowAsTimestamp());

		if(newResult){
			analysis.setEnteredDate(DateUtil.getNowAsTimestamp());
			analysis.setRevision("1");
		}else if(newAnalysisInLoop){
			analysis.setRevision(String.valueOf(Integer.parseInt(analysis.getRevision()) + 1));
		}

		Sample sample = sampleDAO.getSampleByAccessionNumber(testResultItem.getAccessionNumber());
		Patient patient = null;

		if("H".equals(sample.getDomain())){
			SampleHuman sampleHuman = new SampleHuman();
			sampleHuman.setSampleId(sample.getId());
			sampleHumanDAO.getDataBySample(sampleHuman);

			patient = new Patient();
			patient.setId(sampleHuman.getPatientId());
		}

		Referral referral = null;
		Referral existingReferral = null;

		if(supportReferrals){
			// referredOut means the referral checkbox was checked, repeating
			// analysis means that we have multi-select results, so only do one.
			if(testResultItem.isReferredOut() && newAnalysisInLoop){
				// If it is a new result or there is no referral ID that means
				// that a new referral has to be created if it was checked and
				// it was canceled then we are un-canceling a canceled referral
				if(newResult || GenericValidator.isBlankOrNull(testResultItem.getReferralId())){
					referral = new Referral();
					referral.setReferralTypeId(REFERRAL_CONFORMATION_ID);
					referral.setSysUserId(currentUserId);
					referral.setRequestDate(new Timestamp(new Date().getTime()));
					referral.setRequesterName(testResultItem.getTechnician());
					referral.setAnalysis(analysis);
					referral.setReferralReasonId(testResultItem.getReferralReasonId());
				}else if(testResultItem.isReferralCanceled()){
					existingReferral = referralDAO.getReferralById(testResultItem.getReferralId());
					existingReferral.setCanceled(false);
					existingReferral.setSysUserId(currentUserId);
					existingReferral.setRequesterName(testResultItem.getTechnician());
					existingReferral.setReferralReasonId(testResultItem.getReferralReasonId());
				}
			}
		}

        Map<String,List<String>> triggersToReflexesMap = new HashMap<String, List<String>>(  );

        getSelectedReflexes( testResultItem.getReflexJSONResult(), triggersToReflexesMap );

        if(newResult){
			newResults.add(new ResultSet(result, technicianResultSignature, testKit, note, patient, sample, triggersToReflexesMap, referral,
					existingReferral));
		}else{
			modifiedResults.add(new ResultSet(result, technicianResultSignature, testKit, note, patient, sample, triggersToReflexesMap,
					referral, existingReferral));
		}

		previousAnalysis = analysis;
	}

    private void getSelectedReflexes( String reflexJSONResult, Map<String, List<String>> triggersToReflexesMap ){
        if( !GenericValidator.isBlankOrNull( reflexJSONResult )){
            JSONParser parser=new JSONParser();
            try{
                JSONObject jsonResult = ( JSONObject ) parser.parse( reflexJSONResult.replaceAll( "'", "\"" ) );

                for(Object compoundReflexes : jsonResult.values()){
                    String triggerIds = (String)((JSONObject)compoundReflexes).get( "triggerIds" );
                    List<String> selectedReflexIds = new ArrayList<String>(  );
                    JSONArray selectedReflexes = (JSONArray)( ( JSONObject ) compoundReflexes ).get( "selected" );
                    for( Object selectedReflex : selectedReflexes){
                        selectedReflexIds.add (((String)selectedReflex));
                    }
                    triggersToReflexesMap.put( triggerIds.trim(), selectedReflexIds );
                }
            }catch( ParseException e ){
                e.printStackTrace();
            }
        }
    }

    private String getStatusForTestResult(TestResultItem testResult){
		if(alwaysValidate || !testResult.isValid() || ResultUtil.isForcedToAcceptance(testResult)){
			return StatusService.getInstance().getStatusID(AnalysisStatus.TechnicalAcceptance);
		}else if(noResults(testResult.getResultValue(), testResult.getMultiSelectResultValues(), testResult.getResultType())){
			return StatusService.getInstance().getStatusID(AnalysisStatus.NotStarted);
		}else{
			ResultLimit resultLimit = resultLimitDAO.getResultLimitById(testResult.getResultLimitId());
			if(resultLimit != null && resultLimit.isAlwaysValidate()){
				return StatusService.getInstance().getStatusID(AnalysisStatus.TechnicalAcceptance);
			}

			return StatusService.getInstance().getStatusID(AnalysisStatus.Finalized);
		}
	}

	private boolean noResults(String value, String multiSelectValue, String type){

		return (GenericValidator.isBlankOrNull(value) && GenericValidator.isBlankOrNull(multiSelectValue)) ||
				("D".equals(type) && "0".equals(value));
	}

	private ResultInventory createTestKitLinkIfNeeded(TestResultItem testResult, String testKitName){
		ResultInventory testKit = null;

		if((TestResultItem.ResultDisplayType.SYPHILIS == testResult.getRawResultDisplayType() || TestResultItem.ResultDisplayType.HIV == testResult
				.getRawResultDisplayType()) && ResultsLoadUtility.TESTKIT.equals(testKitName)){

			testKit = creatTestKit(testResult, testKitName, testResult.getTestKitId());
		}

		return testKit;
	}

	private ResultInventory creatTestKit(TestResultItem testResult, String testKitName, String testKitId) throws LIMSRuntimeException{
		ResultInventory testKit;
		testKit = new ResultInventory();

		if(!GenericValidator.isBlankOrNull(testKitId)){
			testKit.setId(testKitId);
			resultInventoryDAO.getData(testKit);
		}

		testKit.setInventoryLocationId(testResult.getTestKitInventoryId());
		testKit.setDescription(testKitName);
		testKit.setSysUserId(currentUserId);
		return testKit;
	}

	private void updateAndAddAnalysisToModifiedList(TestResultItem testResultItem, String testDate, Analysis analysis){
		String testMethod = testResultItem.getAnalysisMethod();
		analysis.setAnalysisType(testMethod);
		analysis.setStartedDateForDisplay(testDate);

		// This needs to be refactored -- part of the logic is in
		// getStatusForTestResult
		if(statusRuleSet.equals(IActionConstants.STATUS_RULES_RETROCI)){
			if( !StatusService.getInstance().getStatusID(AnalysisStatus.Canceled).equals(analysis.getStatusId() )){
				analysis.setCompletedDate(DateUtil.convertStringDateToSqlDate(testDate));
				analysis.setStatusId(StatusService.getInstance().getStatusID(AnalysisStatus.TechnicalAcceptance));
			}
		}else if(StatusService.getInstance().getStatusID(AnalysisStatus.Finalized).equals(analysis.getStatusId()) ||
				StatusService.getInstance().getStatusID(AnalysisStatus.TechnicalAcceptance).equals(analysis.getStatusId()) ||
				(analysis.isReferredOut() && !GenericValidator.isBlankOrNull(testResultItem.getResultValue()))){
			analysis.setCompletedDate(DateUtil.convertStringDateToSqlDate(testDate));
		}

		analysis.setSysUserId(currentUserId);
		modifiedAnalysis.add(analysis);
	}

	private List<Result> createResultFromTestResultItem(TestResultItem testResultItem, Analysis analysis){
		List<Result> results = new ArrayList<Result>();

		if("M".equals(testResultItem.getResultType())){
			String[] multiResults = testResultItem.getMultiSelectResultValues().split(",");
			List<Result> existingResults = resultDAO.getResultsByAnalysis(analysis);

            for( String resultAsString : multiResults){
				Result existingResultFromDB = null;
				for(Result existingResult : existingResults){
					if(resultAsString.equals(existingResult.getValue())){
						existingResultFromDB = existingResult;
						break;
					}
				}

				if(existingResultFromDB != null){
					existingResults.remove(existingResultFromDB);
					existingResultFromDB.setSysUserId(currentUserId);
					results.add(existingResultFromDB);
					continue;
				}
				Result result = new Result();

				setTestResultsForDictionaryResult(testResultItem.getTestId(), resultAsString, result);
				setNewResultValues(testResultItem, analysis, result);
				setStandardResultValues(resultAsString, result);
				result.setSortOrder(getResultSortOrder(analysis, result.getValue()));

				results.add(result);
			}
			deletableResults.addAll(existingResults);
		}else{
			Result result = new Result();
			Result qualifiedResult = null;

			boolean newResult = GenericValidator.isBlankOrNull(testResultItem.getResultId());
			boolean isQualifiedResult = testResultItem.isHasQualifiedResult();

			if(!newResult){
				result.setId(testResultItem.getResultId());
				resultDAO.getData(result);

				if(!GenericValidator.isBlankOrNull(testResultItem.getQualifiedResultId())){
					qualifiedResult = new Result();
					qualifiedResult.setId(testResultItem.getQualifiedResultId());
					resultDAO.getData(qualifiedResult);
				}else if(isQualifiedResult){
					qualifiedResult = new Result();
					setNewResultValues(testResultItem, analysis, qualifiedResult);
					qualifiedResult.setResultType("A");
					qualifiedResult.setParentResult(result);
				}
			}

			if("D".equals(testResultItem.getResultType()) || isQualifiedResult){
				setTestResultsForDictionaryResult(testResultItem.getTestId(), testResultItem.getResultValue(), result);  //support qualified result
			}else{
				List<TestResult> testResultList = testResultDAO.getTestResultsByTest(testResultItem.getTestId());
				// we are assuming there is only one testResult for a numeric
				// type result
				if(!testResultList.isEmpty()){
					result.setTestResult(testResultList.get(0));
				}
			}

			if(newResult){
				setNewResultValues(testResultItem, analysis, result);
				if(isQualifiedResult){
					qualifiedResult = new Result();
					setNewResultValues(testResultItem, analysis, qualifiedResult);
					qualifiedResult.setResultType("A");
					qualifiedResult.setParentResult(result);
				}
			}else{
				setAnalyteForResult(result);
			}

			setStandardResultValues(testResultItem.getResultValue(), result);
			results.add(result);

			if(isQualifiedResult){
				setStandardResultValues(testResultItem.getQualifiedResultValue(), qualifiedResult);
				results.add(qualifiedResult);
			}else if(qualifiedResult != null){ // covers the case where user
												// made change from qualified to
												// non-qualified
				setStandardResultValues("", qualifiedResult);
				results.add(qualifiedResult);
			}
		}

		return results;
	}

	private String getResultSortOrder(Analysis analysis, String resultValue){
		TestResult testResult = testResultDAO.getTestResultsByTestAndDictonaryResult(analysis.getTest().getId(), resultValue);
		return testResult == null ? "0" : testResult.getSortOrder();
	}

	private void setStandardResultValues(String value, Result result){
		result.setValue(value);
		result.setSysUserId(currentUserId);
		result.setSortOrder("0");
	}

	private void setNewResultValues(TestResultItem testResultItem, Analysis analysis, Result result){
		result.setAnalysis(analysis);
		result.setAnalysisId(testResultItem.getAnalysisId());
		result.setIsReportable(testResultItem.getReportable());
		result.setResultType(testResultItem.getResultType());
		result.setMinNormal(testResultItem.getLowerNormalRange());
		result.setMaxNormal(testResultItem.getUpperNormalRange());
        result.setSignificantDigits( testResultItem.getSignificantDigits() );

		setAnalyteForResult(result);
	}

	private void setAnalyteForResult(Result result){
		TestAnalyte testAnalyte = ResultUtil.getTestAnalyteForResult(result);

		if(testAnalyte != null){
			result.setAnalyte(testAnalyte.getAnalyte());
		}
	}

	private TestResult setTestResultsForDictionaryResult(String testId, String dictValue, Result result){
		TestResult testResult;
		testResult = testResultDAO.getTestResultsByTestAndDictonaryResult(testId, dictValue);

		if(testResult != null){
			result.setTestResult(testResult);
		}

		return testResult;
	}

	private ResultSignature createTechnicianSignatureFromResultItem(TestResultItem testResult){
		ResultSignature sig = null;

		// The technician signature may be blank if the user changed a
		// conclusion and then changed it back. It will be dirty
		// but will not need a signature
		if(!GenericValidator.isBlankOrNull(testResult.getTechnician())){
			sig = new ResultSignature();

			if(!GenericValidator.isBlankOrNull(testResult.getTechnicianSignatureId())){
				sig.setId(testResult.getTechnicianSignatureId());
				resultSigDAO.getData(sig);
			}

			sig.setIsSupervisor(false);
			sig.setNonUserName(testResult.getTechnician());

			sig.setSysUserId(currentUserId);
		}
		return sig;
	}

	protected ActionForward getForward(ActionForward forward, String accessionNumber){
		ActionRedirect redirect = new ActionRedirect(forward);
		if(!StringUtil.isNullorNill(accessionNumber))
			redirect.addParameter(ACCESSION_NUMBER, accessionNumber);

		return redirect;

	}

	protected ActionForward getForward(ActionForward forward, String accessionNumber, String analysisId){
		ActionRedirect redirect = new ActionRedirect(forward);
		if(!StringUtil.isNullorNill(accessionNumber))
			redirect.addParameter(ACCESSION_NUMBER, accessionNumber);

		if(!StringUtil.isNullorNill(analysisId))
			redirect.addParameter(ANALYSIS_ID, analysisId);

		return redirect;

	}

	@Override
	protected String getPageSubtitleKey(){
		return "banner.menu.results";
	}

	@Override
	protected String getPageTitleKey(){
		return "banner.menu.results";
	}

	@Override
	public String getCurrentUserId(){
		return currentUserId;
	}

	@Override
	public List<ResultSet> getNewResults(){
		return newResults;
	}

	@Override
	public List<ResultSet> getModifiedResults(){
		return modifiedResults;
	}
}
