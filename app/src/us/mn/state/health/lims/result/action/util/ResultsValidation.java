package us.mn.state.health.lims.result.action.util;

import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.ActionErrors;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.provider.validation.DateValidationProvider;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.ConfigurationProperties.Property;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.test.beanItems.TestResultItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResultsValidation {

	private static final String SPECIAL_CASE = "XXXX";
	private boolean supportReferrals = false;
	private boolean useTechnicianName = false;
	private boolean noteRequiredForChangedResults = false;
	
	private static ResultDAO resultDAO = new ResultDAOImpl();
	
	public  List<ActionError> validateItem(TestResultItem item ) {
		List<ActionError> errors = new ArrayList<ActionError>();

		validateTestDate(item, errors);

		validateResult(item, errors);

		if( noteRequiredForChangedResults){
			validateRequiredNote( item, errors);
		}
		
		if (supportReferrals) {
			validateReferral(item, errors);
		}
		if (useTechnicianName) {
			validateTesterSignature(item, errors);
		}

		return errors;
	}
	

	public ActionErrors validateModifiedItems(List<TestResultItem> modifiedItems) {
		noteRequiredForChangedResults = "true".equals(ConfigurationProperties.getInstance().getPropertyValue(Property.notesRequiredForModifyResults));
		
		ActionErrors errors = new ActionErrors();

		for (TestResultItem item : modifiedItems) {

			List<ActionError> itemErrors = validateItem(item );

			if (itemErrors.size() > 0) {
				StringBuilder augmentedAccession = new StringBuilder(item.getSequenceAccessionNumber());
				augmentedAccession.append(" : ");
				augmentedAccession.append(item.getTestName());
				ActionError accessionError = new ActionError("errors.followingAccession", augmentedAccession);
				errors.add(ActionErrors.GLOBAL_MESSAGE, accessionError);

				for (ActionError error : itemErrors) {
					errors.add(ActionErrors.GLOBAL_MESSAGE, error);
				}

			}
		}

		return errors;
	}

	
	private void validateTestDate(TestResultItem item, List<ActionError> errors) {

		DateValidationProvider dateValidator = new DateValidationProvider();
		Date date = dateValidator.getDate(item.getTestDate());

		if (date == null) {
			errors.add(new ActionError("errors.date", new StringBuilder(item.getTestDate())));
		} else if (!IActionConstants.VALID.equals(dateValidator.validateDate(date, DateValidationProvider.PAST))) {
			errors.add(new ActionError("error.date.inFuture"));
		}
	}
	
	private void validateResult(TestResultItem testResultItem, List<ActionError> errors) {

		if (!(ResultUtil.areNotes(testResultItem) || 
				(supportReferrals && ResultUtil.isReferred(testResultItem)) || 
				ResultUtil.areResults(testResultItem) || 
				ResultUtil.isForcedToAcceptance(testResultItem))) { 
			errors.add(new ActionError("errors.result.required"));
		}
		
		if (!GenericValidator.isBlankOrNull(testResultItem.getResultValue()) && "N".equals(testResultItem.getResultType())) {
			if( testResultItem.getResultValue().equals(SPECIAL_CASE)){
				return;
			}
			try {
				Double.parseDouble(testResultItem.getResultValue());
			} catch (NumberFormatException e) {
				errors.add(new ActionError("errors.number.format", new StringBuilder("Result")));
			}
		}
		
		if( testResultItem.isHasQualifiedResult() && GenericValidator.isBlankOrNull(testResultItem.getQualifiedResultValue())){
			errors.add(new ActionError("errors.missing.result.details", new StringBuilder("Result")));
		}
	}
	
	private void validateReferral(TestResultItem item, List<ActionError> errors) {
		if (item.isReferredOut() && "0".equals(item.getReferralReasonId())) {
			errors.add(new ActionError("error.referral.noReason"));
		}
	}
	
	private void validateRequiredNote(TestResultItem item, List<ActionError> errors) {
		if( GenericValidator.isBlankOrNull(item.getNote())&&
			!GenericValidator.isBlankOrNull(item.getResultId())){
			
			Result dbResult = resultDAO.getResultById(item.getResultId());
			if( !item.getResultValue().equals(dbResult.getValue()) && !GenericValidator.isBlankOrNull(dbResult.getValue())){
				errors.add(new ActionError("error.requiredNote.missing"));
			}
		}
		
	}
	private void validateTesterSignature(TestResultItem item, List<ActionError> errors) {
		// Conclusions may not need a signature. If the user changed the value
		// and then changed it back it will be
		// marked as dirty but the signature may still be blank.
		if ("0".equals(item.getResultId())) {
			return;
		}

		Result result = resultDAO.getResultById(item.getResultId());

		if (result != null && result.getAnalyte() != null && "Conclusion".equals(result.getAnalyte().getAnalyteName())) {
			if (result.getValue().equals(item.getResultValue())) {
				return;
			}
		}

		if (GenericValidator.isBlankOrNull(item.getTechnician())) {
			errors.add(new ActionError("errors.signature.required"));
		}
	}


	public void setSupportReferrals(boolean supportReferrals) {
		this.supportReferrals = supportReferrals;
	}

	public void setUseTechnicianName(boolean useTechnicianName) {
		this.useTechnicianName = useTechnicianName;
	}

}
