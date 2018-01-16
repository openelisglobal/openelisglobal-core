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
package us.mn.state.health.lims.common.provider.query;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.common.servlet.validation.AjaxServlet;
import us.mn.state.health.lims.sample.util.AccessionNumberUtil;


public class ScanGeneratorProvider extends BaseQueryProvider {

	public ScanGeneratorProvider() {
		super();
	}

	public ScanGeneratorProvider(AjaxServlet ajaxServlet) {
		this.ajaxServlet = ajaxServlet;
	}

	public void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String nextNumber = null;
		try {
			nextNumber = getNextScanNumber("");	
		} catch (IllegalStateException e) {
			// 
		}
		

		String result = GenericValidator.isBlankOrNull(nextNumber) ? INVALID : VALID;

		String returnData = GenericValidator.isBlankOrNull(nextNumber) ? " " : nextNumber;

		ajaxServlet.sendData(returnData, result, request, response);
	}

	private String getNextScanNumber(String optionalPrefix) throws IllegalStateException {

		return AccessionNumberUtil.getNextAccessionNumber(optionalPrefix);
	}

}
