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
package us.mn.state.health.lims.common.provider.validation;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.servlet.validation.AjaxServlet;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;

public class SampleDomainValidationProvider extends BaseValidationProvider {

	public SampleDomainValidationProvider() {
		super();
	}

	public SampleDomainValidationProvider(AjaxServlet ajaxServlet) {
		this.ajaxServlet = ajaxServlet;
	}

	public void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// get id from request
		String targetId = (String) request.getParameter("id");
		String formField = (String) request.getParameter("field");
		String expectedDomain = (String) request.getParameter("expectedDomain");
		String form = (String) request.getParameter("form");
		String result = validate(targetId, expectedDomain, form);
		ajaxServlet.sendData(formField, result, request, response);
	}

	/**
	 * validate() - for AccessionNumberValidationProvider
	 * 
	 * @param targetId - String
	 * @return String - valid or invalid
	 */
	public String validate(String targetId, String expectedDomain, String form) throws LIMSRuntimeException {
		String retVal = VALID;

		if (!StringUtil.isNullorNill(targetId)) {
			try {
				SampleDAO sampleDAO = new SampleDAOImpl();
				Sample sample = sampleDAO.getSampleByAccessionNumber(targetId.trim());

				if (sample == null) {
					retVal = INVALID;
					
				} else if (sample != null && form != null) {
					if (form.equalsIgnoreCase("newbornSampleOneForm") ||
						form.equalsIgnoreCase("newbornSampleTwoForm") ||
						form.equalsIgnoreCase("newbornSampleFullForm") || 
						form.equalsIgnoreCase("testManagementNewbornForm") ) {
						if (!StringUtil.isNullorNill(sample.getDomain())) {
							if(!sample.getDomain().equals(expectedDomain))
								retVal = INVALID;
						} else {
							retVal = INVALID;
						}					
					} else
						retVal = VALID;
				}

			} catch (NumberFormatException nFE) {
			    LogEvent.logError("SampleDomainValidationProvider","validate()",nFE.toString());
				retVal = INVALID;
			}

		} else {
			retVal = INVALID;
		}

		return retVal;
	}
}
