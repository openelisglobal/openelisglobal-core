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
package us.mn.state.health.lims.sample.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMessages;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.provider.validation.AccessionNumberValidationProvider;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.validator.ActionError;

public abstract class BatchSampleProcessingBaseAction extends BaseAction {

	public BatchSampleProcessingBaseAction() {

	}


	
	//overloading validateAccessionNumber() method in order to pass in accessionNumber as parameter
	//instead of getting accessionNumber from request or from form
	protected ActionMessages validateAccessionNumber(
			String accessionNumber, String invalidMessageKey, HttpServletRequest request, ActionMessages errors,
			BaseActionForm dynaForm) throws Exception {
		
		
		String formName = dynaForm.getDynaClass().getName().toString();

		// accession number validation against database (reusing ajax
		// validation logic)
		AccessionNumberValidationProvider accessionNumberValidator = new AccessionNumberValidationProvider();

		String result = "";

		result = accessionNumberValidator.validate(accessionNumber, formName);

		String messageKey = invalidMessageKey;
		if (result.equals(INVALID)) {
			ActionError error = new ActionError("errors.invalid",
					getMessageForKey(messageKey), null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		if (result.equals(INVALIDSTATUS)) {
			ActionError error = new ActionError("error.invalid.sample.status",
					getMessageForKey(messageKey), null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		return errors;
	}	
	
	//used for batch accession number processing (QE, QA Events)
	protected List populateAccessionNumberList(String fromAccessionNumber,
			String thruAccessionNumber, String[] excludedAccessionNumbers) {
		List accessionNumbers = new ArrayList();
		
		//populate a list for ease of processing
		List excludedAccessionNumbersList = new ArrayList();
		if (excludedAccessionNumbers != null && excludedAccessionNumbers.length > 0) {
			for (int i = 0; i < excludedAccessionNumbers.length; i++) {
				excludedAccessionNumbersList.add(excludedAccessionNumbers[i]);
			}
		}
			
		if (!StringUtil.isNullorNill(thruAccessionNumber)) {
			int fromInt = Integer.parseInt(fromAccessionNumber);
			int thruInt = Integer.parseInt(thruAccessionNumber);
			for (int i = fromInt; i <= thruInt; i++) {
				
              if (!excludedAccessionNumbersList.contains(String.valueOf(i))) {
				accessionNumbers.add(String.valueOf(i));
              }
			}

		} else {
			accessionNumbers.add(fromAccessionNumber);
		}
		return accessionNumbers;
	}
	
	protected ActionMessages isFromAccessionLessThanToAccession(HttpServletRequest request, ActionMessages errors, String fromAccessionNumber, String thruAccessionNumber) throws Exception {
		boolean isLessThan = false;
		int fromInt = Integer.parseInt(fromAccessionNumber);
		int thruInt = Integer.parseInt(thruAccessionNumber);
		if (fromInt < thruInt) {
			isLessThan = true;
		}
		if (!isLessThan) {
			String messageKey = "errors.range.accessionnumber.from.less.to";
			ActionError error = new ActionError("errors.invalid",
					getMessageForKey(messageKey), null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		return errors;
	}

}