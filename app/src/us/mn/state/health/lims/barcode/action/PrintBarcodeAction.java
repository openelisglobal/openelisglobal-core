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
*/
package us.mn.state.health.lims.barcode.action;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.DynaActionForm;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.provider.validation.AccessionNumberValidatorFactory;
import us.mn.state.health.lims.common.provider.validation.IAccessionNumberValidator;
import us.mn.state.health.lims.common.services.IPatientService;
import us.mn.state.health.lims.common.services.PatientService;
import us.mn.state.health.lims.common.services.SampleService;
import us.mn.state.health.lims.common.services.StatusService;
import us.mn.state.health.lims.common.services.TestService;
import us.mn.state.health.lims.common.services.StatusService.AnalysisStatus;
import us.mn.state.health.lims.common.services.StatusService.SampleStatus;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.login.dao.UserModuleDAO;
import us.mn.state.health.lims.login.daoimpl.UserModuleDAOImpl;
import us.mn.state.health.lims.patient.action.bean.PatientSearch;
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
import us.mn.state.health.lims.typeofsample.dao.TypeOfSampleDAO;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleDAOImpl;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;

public class PrintBarcodeAction extends BaseAction {

    private static final TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
    private static final AnalysisDAO analysisDAO = new AnalysisDAOImpl();
    private static final SampleEditItemComparator testComparator = new SampleEditItemComparator();
    private static final Set<Integer> excludedAnalysisStatusList;
    private static final Set<Integer> ENTERED_STATUS_SAMPLE_LIST = new HashSet<Integer>();
    private static final Collection<String> ABLE_TO_CANCEL_ROLE_NAMES = new ArrayList<String>(  );

	static {
		excludedAnalysisStatusList = new HashSet<Integer>();
		excludedAnalysisStatusList.add(Integer.parseInt(StatusService.getInstance().getStatusID(AnalysisStatus.Canceled)));

		ENTERED_STATUS_SAMPLE_LIST.add( Integer.parseInt( StatusService.getInstance().getStatusID( SampleStatus.Entered ) ) );
        ABLE_TO_CANCEL_ROLE_NAMES.add( "Validator" );
        ABLE_TO_CANCEL_ROLE_NAMES.add( "Validation");
        ABLE_TO_CANCEL_ROLE_NAMES.add( "Biologist" );
	}
	
	private String maxAccessionNumber;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String forward = FWD_SUCCESS;
		ActionMessages errors = validate(request);
		
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			request.setAttribute(IActionConstants.FWD_SUCCESS, false);
			forward = FWD_FAIL;
			return mapping.findForward(forward);
		}

		String accessionNumber = request.getParameter("accessionNumber");
		String patientId = request.getParameter("patientId");
		BaseActionForm dynaForm = (BaseActionForm) form;
		dynaForm.initialize(mapping);
		if (!GenericValidator.isBlankOrNull(accessionNumber)) {
			PropertyUtils.setProperty(dynaForm, "accessionNumber", accessionNumber);
			PropertyUtils.setProperty(dynaForm, "patientId", patientId);
		//	PropertyUtils.setProperty(dynaForm, "searchFinished", Boolean.TRUE);
			Sample sample = getSample(accessionNumber);
			
			if (sample != null && !GenericValidator.isBlankOrNull(sample.getId())) {

				List<SampleItem> sampleItemList = getSampleItems(sample);
				setPatientInfo(dynaForm, sample);
				List<SampleEditItem> currentTestList = getCurrentTestInfo( sampleItemList, accessionNumber, false);
                PropertyUtils.setProperty(dynaForm, "existingTests", currentTestList);
			} else {
			//	PropertyUtils.setProperty(dynaForm, "noSampleFound", Boolean.TRUE);
			}
		}
		PatientSearch patientSearch = new PatientSearch();
        patientSearch.setLoadFromServerWithPatient( true );
        patientSearch.setSelectedPatientActionButtonText( StringUtil.getMessageForKey( "label.patient.search.select" ) );
        PropertyUtils.setProperty( form, "patientSearch", patientSearch );

 		return mapping.findForward(forward);
	}
	
	private ActionMessages validate(HttpServletRequest request) {
		ActionMessages errors = new ActionMessages();
		
		IAccessionNumberValidator accessionNumberValidator = AccessionNumberUtil.getAccessionNumberValidator();
		String accessionNumber = request.getParameter("accessionNumber");
		String patientId = request.getParameter("patientId");
		if (accessionNumber == null && patientId == null) {
			return errors;
		}
		if (!(IAccessionNumberValidator.ValidationResults.SUCCESS == accessionNumberValidator.validFormat(accessionNumber, false))) {
			ActionError error = new ActionError("barcode.print.error.accession.invalid");
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		if (!GenericValidator.isInt(patientId)) {
			ActionError error = new ActionError("barcode.print.error.patientid.invalid");
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		
		return errors;
	}

	protected String getPageTitleKey() {
		return "barcode.print.title";
	}

	protected String getPageSubtitleKey() {
		return "barcode.print.title";
	}

	private Sample getSample(String accessionNumber) {
		SampleDAO sampleDAO = new SampleDAOImpl();
		return sampleDAO.getSampleByAccessionNumber(accessionNumber);
	}

	private List<SampleItem> getSampleItems(Sample sample) {
		SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();

		return sampleItemDAO.getSampleItemsBySampleIdAndStatus(sample.getId(), ENTERED_STATUS_SAMPLE_LIST );
	}

	private List<SampleEditItem> getCurrentTestInfo(  List<SampleItem> sampleItemList, String accessionNumber, boolean allowedToCancelAll ) throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		List<SampleEditItem> currentTestList = new ArrayList<SampleEditItem>();

		for (SampleItem sampleItem : sampleItemList) {
			addCurrentTestsToList(sampleItem, currentTestList, accessionNumber, allowedToCancelAll);
		}

        return currentTestList;
	}

	private void addCurrentTestsToList(SampleItem sampleItem, List<SampleEditItem> currentTestList, String accessionNumber, boolean allowedToCancelAll) {

		TypeOfSample typeOfSample = new TypeOfSample();
		typeOfSample.setId(sampleItem.getTypeOfSampleId());
		typeOfSampleDAO.getData(typeOfSample);

		List<Analysis> analysisList = analysisDAO.getAnalysesBySampleItemsExcludingByStatusIds(sampleItem, excludedAnalysisStatusList);

		List<SampleEditItem> analysisSampleItemList = new ArrayList<SampleEditItem>();

        String collectionDate = DateUtil.convertTimestampToStringDate( sampleItem.getCollectionDate() );
        String collectionTime = DateUtil.convertTimestampToStringTime( sampleItem.getCollectionDate() );
		boolean canRemove = true;
		for (Analysis analysis : analysisList) {
			SampleEditItem sampleEditItem = new SampleEditItem();

			sampleEditItem.setTestId(analysis.getTest().getId());
			sampleEditItem.setTestName(TestService.getUserLocalizedTestName( analysis.getTest() ));
			sampleEditItem.setSampleItemId(sampleItem.getId());

			boolean canCancel = allowedToCancelAll ||
                    (!StatusService.getInstance().matches( analysis.getStatusId(), AnalysisStatus.Canceled ) &&
					StatusService.getInstance().matches( analysis.getStatusId(), AnalysisStatus.NotStarted ));

			if( !canCancel){
				canRemove = false;
			}
			sampleEditItem.setCanCancel(canCancel);
			sampleEditItem.setAnalysisId(analysis.getId());
			sampleEditItem.setStatus(StatusService.getInstance().getStatusNameFromId(analysis.getStatusId()));
			sampleEditItem.setSortOrder(analysis.getTest().getSortOrder());
            sampleEditItem.setHasResults( !StatusService.getInstance().matches( analysis.getStatusId(), AnalysisStatus.NotStarted ) );

			analysisSampleItemList.add(sampleEditItem);
			break;
		}

		if (!analysisSampleItemList.isEmpty()) {
			Collections.sort(analysisSampleItemList, testComparator);
            SampleEditItem firstItem = analysisSampleItemList.get( 0 );

            firstItem.setAccessionNumber(accessionNumber + "-" + sampleItem.getSortOrder());
            firstItem.setSampleType(typeOfSample.getLocalizedName());
            firstItem.setCanRemoveSample(canRemove);
            firstItem.setCollectionDate( collectionDate == null ? "" : collectionDate );
            firstItem.setCollectionTime( collectionTime );
			maxAccessionNumber = analysisSampleItemList.get(0).getAccessionNumber();
			currentTestList.addAll(analysisSampleItemList);
		}
	}

	private void setPatientInfo(DynaActionForm dynaForm, Sample sample) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {

		Patient patient = new SampleHumanDAOImpl().getPatientForSample(sample);
		IPatientService patientService = new PatientService(patient);

        PropertyUtils.setProperty( dynaForm, "patientName", patientService.getLastFirstName() );
		PropertyUtils.setProperty(dynaForm, "dob", patientService.getEnteredDOB());
		PropertyUtils.setProperty(dynaForm, "gender", patientService.getGender());
		PropertyUtils.setProperty(dynaForm, "nationalId", patientService.getNationalId());
	}
	
	private static class SampleEditItemComparator implements Comparator<SampleEditItem> {

		public int compare(SampleEditItem o1, SampleEditItem o2) {
			if (GenericValidator.isBlankOrNull(o1.getSortOrder()) || GenericValidator.isBlankOrNull(o2.getSortOrder())) {
				return o1.getTestName().compareTo(o2.getTestName());
			}

			try {
				return Integer.parseInt(o1.getSortOrder()) - Integer.parseInt(o2.getSortOrder());
			} catch (NumberFormatException e) {
				return o1.getTestName().compareTo(o2.getTestName());
			}
		}

	}
}
