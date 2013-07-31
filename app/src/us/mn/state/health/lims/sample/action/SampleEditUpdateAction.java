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
package us.mn.state.health.lims.sample.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.GenericValidator;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.DynaActionForm;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.formfields.FormFields;
import us.mn.state.health.lims.common.formfields.FormFields.Field;
import us.mn.state.health.lims.common.provider.validation.IAccessionNumberValidator.ValidationResults;
import us.mn.state.health.lims.common.services.SampleAddService;
import us.mn.state.health.lims.common.services.SampleAddService.SampleTestCollection;
import us.mn.state.health.lims.common.services.StatusService;
import us.mn.state.health.lims.common.services.StatusService.AnalysisStatus;
import us.mn.state.health.lims.common.services.StatusService.SampleStatus;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.ConfigurationProperties.Property;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.observationhistory.dao.ObservationHistoryDAO;
import us.mn.state.health.lims.observationhistory.daoimpl.ObservationHistoryDAOImpl;
import us.mn.state.health.lims.observationhistory.valueholder.ObservationHistory;
import us.mn.state.health.lims.observationhistory.valueholder.ObservationHistory.ValueType;
import us.mn.state.health.lims.observationhistorytype.dao.ObservationHistoryTypeDAO;
import us.mn.state.health.lims.observationhistorytype.daoImpl.ObservationHistoryTypeDAOImpl;
import us.mn.state.health.lims.observationhistorytype.valueholder.ObservationHistoryType;
import us.mn.state.health.lims.panel.valueholder.Panel;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.sample.bean.SampleEditItem;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.util.AccessionNumberUtil;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.samplehuman.daoimpl.SampleHumanDAOImpl;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.dao.TestSectionDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.daoimpl.TestSectionDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.test.valueholder.TestSection;

public class SampleEditUpdateAction extends BaseAction {

	private static final String DEFAULT_ANALYSIS_TYPE = "MANUAL";
	private AnalysisDAO analysisDAO = new AnalysisDAOImpl();
	private SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
	private SampleDAO sampleDAO = new SampleDAOImpl();
	private TestDAO testDAO = new TestDAOImpl();
	private static String PAYMENT_STATUS_OBSERVATION_ID = null;
	private static String CANCELED_TEST_STATUS_ID = null;
	private static String CANCELED_SAMPLE_STATUS_ID = null;
	private boolean deletePaymentHistory = false;
	private ObservationHistory paymentObservation = null;
	private ObservationHistoryDAO observationDAO = new ObservationHistoryDAOImpl();
	private TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
	private SampleAddService sampleAddService; 

	static {
		ObservationHistoryTypeDAO ohtDAO = new ObservationHistoryTypeDAOImpl();
		ObservationHistoryType observationType = ohtDAO.getByName("paymentStatus");
		if (observationType != null) {
			PAYMENT_STATUS_OBSERVATION_ID = observationType.getId();
		}


		CANCELED_TEST_STATUS_ID = StatusService.getInstance().getStatusID(AnalysisStatus.Canceled);
		CANCELED_SAMPLE_STATUS_ID = StatusService.getInstance().getStatusID(SampleStatus.Canceled);
	}

	@SuppressWarnings("unchecked")
	protected ActionForward performAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String forward = "success";

		ActionMessages errors = null;
		request.getSession().setAttribute(SAVE_DISABLED, TRUE);

		DynaActionForm dynaForm = (DynaActionForm) form;

		boolean accessionNumberChanged = accessionNumberChanged(dynaForm);
		Sample updatedSample = null;
		if (accessionNumberChanged) {
			errors = validateNewAccessionNumber(dynaForm.getString("newAccessionNumber"));
			if (!errors.isEmpty()) {
				saveErrors(request, errors);
				request.setAttribute(Globals.ERROR_KEY, errors);
				forward = FWD_FAIL;
				return mapping.findForward(FWD_FAIL);
			} else {
				updatedSample = updateAccessionNumberInSample(dynaForm);
			}
		}

		if (ConfigurationProperties.getInstance().isPropertyValueEqual(Property.trackPatientPayment, "true")) {
			setPaymentObservation(dynaForm, updatedSample);
		}

		if (forward == FWD_SUCCESS) {

			List<Analysis> cancelAnalysisList = createRemoveList((List<SampleEditItem>) dynaForm.get("existingTests"));
			List<SampleItem> cancelSampleItemList = createCancelSampleList((List<SampleEditItem>) dynaForm.get("existingTests"),
					cancelAnalysisList);
			List<Analysis> addAnalysisList = createAddAanlysisList((List<SampleEditItem>) dynaForm.get("possibleTests"));

			List<SampleTestCollection> addedSamples = createAddSampleList(dynaForm, addAnalysisList, updatedSample);
			Transaction tx = HibernateUtil.getSession().beginTransaction();

			try {

				for (Analysis analysis : cancelAnalysisList) {
					analysisDAO.updateData(analysis);
				}

				for (Analysis analysis : addAnalysisList) {
					if (analysis.getId() == null) {
						analysisDAO.insertData(analysis, false); // don't check
																	// for
																	// duplicates
					} else {
						analysisDAO.updateData(analysis);
					}
				}

				for (SampleItem sampleItem : cancelSampleItemList) {
					sampleItemDAO.updateData(sampleItem);
				}

				if (accessionNumberChanged) {
					sampleDAO.updateData(updatedSample);
				}

				if (paymentObservation != null) {
					if (deletePaymentHistory) {
						List<ObservationHistory> shortList = new ArrayList<ObservationHistory>();
						shortList.add(paymentObservation);
						observationDAO.delete(shortList);
					} else if (GenericValidator.isBlankOrNull(paymentObservation.getId())) {
						observationDAO.insertData(paymentObservation);
					} else {
						observationDAO.updateData(paymentObservation);
					}
				}

				for( SampleTestCollection sampleTestCollection : addedSamples){
					sampleItemDAO.insertData(sampleTestCollection.item);

					for (Test test : sampleTestCollection.tests) {
						testDAO.getData(test);

						Analysis analysis = populateAnalysis(sampleTestCollection, test, sampleTestCollection.testIdToUserSectionMap.get(test.getId()) );
						analysisDAO.insertData(analysis, false); // false--do not check
						// for duplicates
					}
					
					if( sampleTestCollection.initialSampleConditionIdList != null){
						for( ObservationHistory observation : sampleTestCollection.initialSampleConditionIdList){
							observation.setSampleItemId(sampleTestCollection.item.getId());
							observationDAO.insertData(observation);
						}
					}
				}
				
				tx.commit();
			} catch (LIMSRuntimeException lre) {
				tx.rollback();
				errors = new ActionMessages();
				if (lre.getException() instanceof StaleObjectStateException) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionError("errors.OptimisticLockException", null, null));
				} else {
					lre.printStackTrace();
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionError("errors.UpdateException", null, null));
				}

				saveErrors(request, errors);
				request.setAttribute(Globals.ERROR_KEY, errors);

				return mapping.findForward(FWD_FAIL);

			} finally {
				HibernateUtil.closeSession();
			}
		}

		String sampleEditWritability = (String) request.getSession().getAttribute(IActionConstants.SAMPLE_EDIT_WRITABLE);

		if (GenericValidator.isBlankOrNull(sampleEditWritability)) {
			return mapping.findForward(forward);
		} else {
			Map<String, String> params = new HashMap<String, String>();
			params.put("type", sampleEditWritability);
			return getForwardWithParameters(mapping.findForward(forward), params);
		}

	}

	private Analysis populateAnalysis(SampleTestCollection sampleTestCollection, Test test, String userSelectedTestSection) {
		java.sql.Date collectionDateTime = DateUtil.convertStringDateTimeToSqlDate(sampleTestCollection.collectionDate);
		TestSection testSection = test.getTestSection();
		if( !GenericValidator.isBlankOrNull(userSelectedTestSection)){
			testSection = testSectionDAO.getTestSectionById( userSelectedTestSection); //change
		}
		
		Panel panel = sampleAddService.getPanelForTest(test);
		
		Analysis analysis = new Analysis();
		analysis.setTest(test);
		analysis.setIsReportable(test.getIsReportable());
		analysis.setAnalysisType(DEFAULT_ANALYSIS_TYPE);
		analysis.setSampleItem(sampleTestCollection.item);
		analysis.setSysUserId(sampleTestCollection.item.getSysUserId());
		analysis.setRevision("0");
		analysis.setStartedDate(collectionDateTime  == null ? DateUtil.getNowAsSqlDate() : collectionDateTime );
		analysis.setStatusId(StatusService.getInstance().getStatusID(AnalysisStatus.NotStarted));
		analysis.setTestSection(testSection);
		analysis.setPanel(panel);
		return analysis;
	}
	
	private List<SampleTestCollection> createAddSampleList(DynaActionForm dynaForm, List<Analysis> addAnalysisList, Sample sample) {
		if( sample == null){
			sample = sampleDAO.getSampleByAccessionNumber(dynaForm.getString("accessionNumber"));
		}
		
		String receivedDateForDisplay = sample.getReceivedDateForDisplay();
		String collectionDateFromRecieveDate = null;
		boolean useReceiveDateForCollectionDate = !FormFields.getInstance().useField(Field.CollectionDate);
		
		if (useReceiveDateForCollectionDate) {
			collectionDateFromRecieveDate = receivedDateForDisplay + " 00:00:00";
		}
		
		sampleAddService = new SampleAddService(dynaForm.getString("sampleXML"), currentUserId, sample, collectionDateFromRecieveDate);
		
		String maxAccessionNumber = dynaForm.getString("maxAccessionNumber");
		if( !GenericValidator.isBlankOrNull(maxAccessionNumber)){		
			sampleAddService.setInitialSampleItemOrderValue(Integer.parseInt(maxAccessionNumber.split("-")[1]));
		}
	
		return sampleAddService.createSampleTestCollection();
	}

	private List<SampleItem> createCancelSampleList(List<SampleEditItem> list, List<Analysis> cancelAnalysisList) {
		List<SampleItem> cancelList = new ArrayList<SampleItem>();

		boolean cancelTest = false;

		for (SampleEditItem editItem : list) {
			if (editItem.getAccessionNumber() != null) {
				cancelTest = false;
			}
			if (cancelTest && !cancelAnalysisListContainsId(editItem.getAnalysisId(), cancelAnalysisList)) {
				Analysis analysis = getCancelableAnalysis(editItem);
				cancelAnalysisList.add(analysis);
			}

			if (editItem.isRemoveSample()) {
				cancelTest = true;
				SampleItem sampleItem = getCancelableSampleItem(editItem);
				if (sampleItem != null) {
					cancelList.add(sampleItem);
				}
				if (!cancelAnalysisListContainsId(editItem.getAnalysisId(), cancelAnalysisList)) {
					Analysis analysis = getCancelableAnalysis(editItem);
					cancelAnalysisList.add(analysis);
				}
			}
		}

		return cancelList;
	}

	private SampleItem getCancelableSampleItem(SampleEditItem editItem) {
		String sampleItemId = editItem.getSampleItemId();
		SampleItem item = new SampleItem();
		item.setId(sampleItemId);
		sampleItemDAO.getData(item);

		if (item.getId() != null) {
			item.setStatusId(CANCELED_SAMPLE_STATUS_ID);
			item.setSysUserId(currentUserId);
			return item;
		}

		return null;
	}

	private boolean cancelAnalysisListContainsId(String analysisId, List<Analysis> cancelAnalysisList) {

		for (Analysis analysis : cancelAnalysisList) {
			if (analysisId.equals(analysis.getId())) {
				return true;
			}
		}

		return false;
	}

	private void setPaymentObservation(DynaActionForm dynaForm, Sample updatedSample) {
		deletePaymentHistory = false;
		paymentObservation = null;
		String paymentValue = dynaForm.getString("paymentOptionSelection");

		/*
		 * Cases to handle 1. value in DB no selected value 2. value in DB
		 * selected value different 3. value in DB selected value same 4. no
		 * value in DB selected value 5. no value in DB no selected value
		 */

		if (updatedSample == null) {
			updatedSample = sampleDAO.getSampleByAccessionNumber(dynaForm.getString("accessionNumber"));
		}

		paymentObservation = observationDAO.getObservationHistoriesBySampleIdAndType(updatedSample.getId(), PAYMENT_STATUS_OBSERVATION_ID);
		if (paymentObservation != null) {
			if (GenericValidator.isBlankOrNull(paymentValue)) {
				deletePaymentHistory = true;
			} else {
				if (paymentObservation.getValue().equals(paymentValue)) {
					paymentObservation = null;
				} else {
					paymentObservation.setValue(paymentValue);
				}
			}
		} else if (!GenericValidator.isBlankOrNull(paymentValue)) {
			Patient patient = new SampleHumanDAOImpl().getPatientForSample(updatedSample);
			if (patient != null) {
				paymentObservation = new ObservationHistory();
				paymentObservation.setSampleId(updatedSample.getId());
				paymentObservation.setObservationHistoryTypeId(PAYMENT_STATUS_OBSERVATION_ID);
				paymentObservation.setValueType(ValueType.DICTIONARY);
				paymentObservation.setPatientId(patient.getId());
				paymentObservation.setValue(paymentValue);
			}
		}

		if (paymentObservation != null) {
			paymentObservation.setSysUserId(currentUserId);
		}

	}

	private ActionMessages validateNewAccessionNumber(String accessionNumber) {
		ActionMessages errors = new ActionMessages();
		ValidationResults results = AccessionNumberUtil.correctFormat(accessionNumber);

		if (results != ValidationResults.SUCCESS) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionError("sample.entry.invalid.accession.number.format", null, null));
		} else if (AccessionNumberUtil.isUsed(accessionNumber)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionError("sample.entry.invalid.accession.number.used", null, null));
		}

		return errors;
	}

	private Sample updateAccessionNumberInSample(DynaActionForm dynaForm) {
		Sample sample = sampleDAO.getSampleByAccessionNumber(dynaForm.getString("accessionNumber"));

		if (sample != null) {
			sample.setAccessionNumber(dynaForm.getString("newAccessionNumber"));
			sample.setSysUserId(currentUserId);
		}

		return sample;
	}

	private boolean accessionNumberChanged(DynaActionForm dynaForm) {
		String newAccessionNumber = dynaForm.getString("newAccessionNumber");

		if (GenericValidator.isBlankOrNull(newAccessionNumber)) {
			return false;
		}

		return !newAccessionNumber.equals(dynaForm.getString("accessionNumber"));
	}

	private List<Analysis> createRemoveList(List<SampleEditItem> tests) {
		List<Analysis> removeAnalysisList = new ArrayList<Analysis>();

		for (SampleEditItem sampleEditItem : tests) {
			if (sampleEditItem.isCanceled()) {
				Analysis analysis = getCancelableAnalysis(sampleEditItem);
				removeAnalysisList.add(analysis);
			}
		}

		return removeAnalysisList;
	}

	private Analysis getCancelableAnalysis(SampleEditItem sampleEditItem) {
		Analysis analysis = new Analysis();
		analysis.setId(sampleEditItem.getAnalysisId());
		analysisDAO.getData(analysis);
		analysis.setSysUserId(currentUserId);
		analysis.setStatusId(StatusService.getInstance().getStatusID(AnalysisStatus.Canceled));
		return analysis;
	}

	private List<Analysis> createAddAanlysisList(List<SampleEditItem> tests) {
		List<Analysis> addAnalysisList = new ArrayList<Analysis>();

		for (SampleEditItem sampleEditItem : tests) {
			if (sampleEditItem.isAdd()) {

				Analysis analysis = newOrExistingCanceledAnalysis(sampleEditItem);

				if (analysis.getId() == null) {
					SampleItem sampleItem = new SampleItem();
					sampleItem.setId(sampleEditItem.getSampleItemId());
					sampleItemDAO.getData(sampleItem);
					analysis.setSampleItem(sampleItem);

					Test test = new Test();
					test.setId(sampleEditItem.getTestId());
					testDAO.getData(test);

					analysis.setTest(test);
					analysis.setRevision("0");
					analysis.setTestSection(test.getTestSection());
					analysis.setEnteredDate(DateUtil.getNowAsTimestamp());
					analysis.setIsReportable(test.getIsReportable());
					analysis.setAnalysisType("MANUAL");
					analysis.setStartedDate(DateUtil.getNowAsSqlDate());
				}

				analysis.setStatusId(StatusService.getInstance().getStatusID(AnalysisStatus.NotStarted));
				analysis.setSysUserId(currentUserId);

				addAnalysisList.add(analysis);
			}
		}

		return addAnalysisList;
	}

	private Analysis newOrExistingCanceledAnalysis(SampleEditItem sampleEditItem) {
		List<Analysis> canceledAnalysis = analysisDAO.getAnalysesBySampleItemIdAndStatusId(sampleEditItem.getSampleItemId(),
				CANCELED_TEST_STATUS_ID);

		for (Analysis analysis : canceledAnalysis) {
			if (sampleEditItem.getTestId().equals(analysis.getTest().getId())) {
				return analysis;
			}
		}

		return new Analysis();
	}

	protected String getPageTitleKey() {
		return StringUtil.getContextualKeyForKey("sample.edit.title");
	}

	protected String getPageSubtitleKey() {
		return StringUtil.getContextualKeyForKey("sample.edit.subtitle");
	}
}
