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

import org.apache.commons.validator.GenericValidator;
import org.apache.struts.Globals;
import org.apache.struts.action.*;
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
import us.mn.state.health.lims.common.services.RequesterService;
import us.mn.state.health.lims.common.services.SampleAddService;
import us.mn.state.health.lims.common.services.SampleAddService.SampleTestCollection;
import us.mn.state.health.lims.common.services.SampleOrderService;
import us.mn.state.health.lims.common.services.StatusService;
import us.mn.state.health.lims.common.services.StatusService.AnalysisStatus;
import us.mn.state.health.lims.common.services.StatusService.SampleStatus;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.observationhistory.dao.ObservationHistoryDAO;
import us.mn.state.health.lims.observationhistory.daoimpl.ObservationHistoryDAOImpl;
import us.mn.state.health.lims.observationhistory.valueholder.ObservationHistory;
import us.mn.state.health.lims.organization.dao.OrganizationDAO;
import us.mn.state.health.lims.organization.dao.OrganizationOrganizationTypeDAO;
import us.mn.state.health.lims.organization.daoimpl.OrganizationDAOImpl;
import us.mn.state.health.lims.organization.daoimpl.OrganizationOrganizationTypeDAOImpl;
import us.mn.state.health.lims.panel.valueholder.Panel;
import us.mn.state.health.lims.person.dao.PersonDAO;
import us.mn.state.health.lims.person.daoimpl.PersonDAOImpl;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.requester.dao.SampleRequesterDAO;
import us.mn.state.health.lims.requester.daoimpl.SampleRequesterDAOImpl;
import us.mn.state.health.lims.requester.valueholder.SampleRequester;
import us.mn.state.health.lims.sample.bean.SampleEditItem;
import us.mn.state.health.lims.sample.bean.SampleOrderItem;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.util.AccessionNumberUtil;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.dao.TestSectionDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.daoimpl.TestSectionDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.test.valueholder.TestSection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SampleEditUpdateAction extends BaseAction {

	private static final String DEFAULT_ANALYSIS_TYPE = "MANUAL";
	private AnalysisDAO analysisDAO = new AnalysisDAOImpl();
	private SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
	private SampleDAO sampleDAO = new SampleDAOImpl();
	private TestDAO testDAO = new TestDAOImpl();
	private static String CANCELED_TEST_STATUS_ID = null;
	private static String CANCELED_SAMPLE_STATUS_ID = null;
	private boolean deletePaymentHistory = false;
	private ObservationHistory paymentObservation = null;
	private ObservationHistoryDAO observationDAO = new ObservationHistoryDAOImpl();
	private TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
    private PersonDAO personDAO = new PersonDAOImpl();
    private SampleRequesterDAO sampleRequesterDAO = new SampleRequesterDAOImpl();
    private OrganizationDAO organizationDAO = new OrganizationDAOImpl();
    private OrganizationOrganizationTypeDAO orgOrgTypeDAO = new OrganizationOrganizationTypeDAOImpl();
	private SampleAddService sampleAddService; 

	static {
		CANCELED_TEST_STATUS_ID = StatusService.getInstance().getStatusID(AnalysisStatus.Canceled);
		CANCELED_SAMPLE_STATUS_ID = StatusService.getInstance().getStatusID(SampleStatus.Canceled);
	}

	@SuppressWarnings("unchecked")
	protected ActionForward performAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionMessages errors;
		request.getSession().setAttribute(SAVE_DISABLED, TRUE);

		DynaActionForm dynaForm = (DynaActionForm) form;

		boolean sampleChanged = accessionNumberChanged(dynaForm);
		Sample updatedSample = null;

		if (sampleChanged) {
			errors = validateNewAccessionNumber(dynaForm.getString("newAccessionNumber"));
			if (!errors.isEmpty()) {
				saveErrors(request, errors);
				request.setAttribute(Globals.ERROR_KEY, errors);
				return mapping.findForward(FWD_FAIL);
			} else {
				updatedSample = updateAccessionNumberInSample(dynaForm);
			}
		}

        List<Analysis> cancelAnalysisList = createRemoveList((List<SampleEditItem>) dynaForm.get("existingTests"));
        List<SampleItem> cancelSampleItemList = createCancelSampleList((List<SampleEditItem>) dynaForm.get("existingTests"),
                cancelAnalysisList);
        List<Analysis> addAnalysisList = createAddAanlysisList((List<SampleEditItem>) dynaForm.get("possibleTests"));


        List<SampleTestCollection> addedSamples = createAddSampleList(dynaForm, updatedSample);

        SampleOrderService sampleOrderService = new SampleOrderService( (SampleOrderItem )dynaForm.get("sampleOrderItems") );
        SampleOrderService.SampleOrderPersistenceArtifacts orderArtifacts = sampleOrderService.getPersistenceArtifacts( updatedSample, currentUserId);

        if( orderArtifacts.getSample() != null){
            sampleChanged = true;
            updatedSample = orderArtifacts.getSample();
        }

        Person referringPerson = orderArtifacts.getProviderPerson();

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

            if (sampleChanged ) {
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

            if( referringPerson != null){
                if(referringPerson.getId() == null){
                    personDAO.insertData( referringPerson );
                }else{
                    personDAO.updateData( referringPerson );
                }
            }

            for(ObservationHistory observation : orderArtifacts.getObservations()){
                observationDAO.insertOrUpdateData( observation );
            }

            if( orderArtifacts.getSamplePersonRequester() != null){
                SampleRequester samplePersonRequester = orderArtifacts.getSamplePersonRequester();
                samplePersonRequester.setRequesterId( orderArtifacts.getProviderPerson().getId() );
                sampleRequesterDAO.insertOrUpdateData( samplePersonRequester );
            }

            if( orderArtifacts.getProviderOrganization() != null ){
                boolean link = orderArtifacts.getProviderOrganization().getId() == null;
                organizationDAO.insertOrUpdateData( orderArtifacts.getProviderOrganization() );
                if( link){
                    orgOrgTypeDAO.linkOrganizationAndType( orderArtifacts.getProviderOrganization(), RequesterService.REFERRAL_ORG_TYPE_ID );
                }
            }

            if( orderArtifacts.getSampleOrganizationRequester() != null){
                if(orderArtifacts.getProviderOrganization() != null ){
                    orderArtifacts.getSampleOrganizationRequester().setRequesterId( orderArtifacts.getProviderOrganization().getId() );
                }
                sampleRequesterDAO.insertOrUpdateData( orderArtifacts.getSampleOrganizationRequester());
            }

            if( orderArtifacts.getDeletableSampleOrganizationRequester() != null){
                sampleRequesterDAO.delete(orderArtifacts.getDeletableSampleOrganizationRequester());
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


		String sampleEditWritable = (String) request.getSession().getAttribute(IActionConstants.SAMPLE_EDIT_WRITABLE);

		if (GenericValidator.isBlankOrNull(sampleEditWritable)) {
			return mapping.findForward(FWD_SUCCESS);
		} else {
			Map<String, String> params = new HashMap<String, String>();
			params.put("type", sampleEditWritable);
			return getForwardWithParameters(mapping.findForward(FWD_SUCCESS), params);
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
	
	private List<SampleTestCollection> createAddSampleList(DynaActionForm dynaForm, Sample sample) {
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

        return !GenericValidator.isBlankOrNull( newAccessionNumber ) && !newAccessionNumber.equals( dynaForm.getString( "accessionNumber" ) );

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
