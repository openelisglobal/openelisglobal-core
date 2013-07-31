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
package us.mn.state.health.lims.common.provider.validation;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import us.mn.state.health.lims.common.provider.validation.IAccessionNumberValidator.ValidationResults;
import us.mn.state.health.lims.common.servlet.validation.AjaxServlet;
import us.mn.state.health.lims.sample.util.AccessionNumberUtil;

/**
 * The QuickEntryAccessionNumberValidationProvider class is used to validate,
 * via AJAX.
 *
 */
public class SampleEntryAccessionNumberValidationProvider extends	BaseValidationProvider {

	public SampleEntryAccessionNumberValidationProvider(){
		super();
	}

	public SampleEntryAccessionNumberValidationProvider(AjaxServlet ajaxServlet) {
		this.ajaxServlet = ajaxServlet;
	}


	@Override
	public void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String accessionNumber = request.getParameter("accessionNumber");
		String field = request.getParameter("field");
		String recordType = request.getParameter("recordType");
		String isRequired = request.getParameter("isRequired");
		String projectFormName = request.getParameter("projectFormName");
		String checkOnlyFormatAndIsUsed = request.getParameter("checkFormatAndUsed");

		ValidationResults result = ValidationResults.SUCCESS;
		
		if( "true".equals(checkOnlyFormatAndIsUsed)){
			result = AccessionNumberUtil.correctFormat(accessionNumber);
			
			if( result == ValidationResults.SUCCESS){
				result = AccessionNumberUtil.isUsed(accessionNumber) ? ValidationResults.SAMPLE_FOUND : ValidationResults.SAMPLE_NOT_FOUND;
			}
		}else{
			result = AccessionNumberUtil.checkAccessionNumberValidity(accessionNumber, recordType, isRequired, projectFormName);	
		}
		

		String returnData = accessionNumber;

		switch( result ) {
			case SUCCESS:
			    returnData = VALID;
			    break;
			case SAMPLE_FOUND:
			case SAMPLE_NOT_FOUND:
			    returnData = result.name();
			    break;
			default:
			    returnData = AccessionNumberUtil.getInvalidMessage(result);
		}

		response.setCharacterEncoding("UTF-8");
		ajaxServlet.sendData(field, returnData, request, response);
	}
}
