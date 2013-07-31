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
import java.util.ArrayList;
import java.util.List;

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

/**
 * @author aiswarya raman
 * bugzilla 1765 changed name of this provider from SampleStateValidationProvider to SampleStatusValidationProvider
 * in order not to conflict with naming of StateValidationProvider (US state)
 * also changed to make more generic - this validation provider will validate any status based on expectedStatus
 * bugzilla 1979 modified to allow for validation of list of samples for a particular status
 */
public class SampleStatusValidationProvider extends BaseValidationProvider {

	public SampleStatusValidationProvider() {
		super();
	}

	public SampleStatusValidationProvider(AjaxServlet ajaxServlet) {
		this.ajaxServlet = ajaxServlet;
	}

	public void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// get id from request
		String targetId = (String) request.getParameter("id");
		String formField = (String) request.getParameter("field");
		String expectedStatus = (String) request.getParameter("expectedStatus");
		// bugzilla 1979 this is for batch quick entry to verify sample statuses
		// in a given range
		String toTargetId = null;
		if (request.getParameter("toId") != null) {
			toTargetId = (String) request.getParameter("toId");
		}
		String result = validate(targetId, toTargetId, expectedStatus);
		ajaxServlet.sendData(formField, result, request, response);
	}

	public String validate(String targetId, String toTargetId,
			String expectedStatus) throws LIMSRuntimeException {

    	//bugzilla 2154
    	LogEvent.logDebug("SampleStatusValidationProvider","validate()"," In validate() targetId in: " + targetId);

		StringBuffer s = new StringBuffer();

		if (StringUtil.isNullorNill(toTargetId)) {
			if ((targetId != null)) {

				Sample sample = new Sample();
				SampleDAO sampleDAO = new SampleDAOImpl();

				sample.setAccessionNumber(targetId);
				sampleDAO.getSampleByAccessionNumber(sample);

				if (!sample.getStatus().equalsIgnoreCase(expectedStatus)) {
					s.append(INVALID);
				} else {
					s.append(VALIDSTATUS);
				}

			} else {
				s.append(INVALID);
			}
		} else {
			List accessionNumbers = populateAccessionNumberList(targetId,
					toTargetId);

			boolean validStatuses = true;
			for (int i = 0; i < accessionNumbers.size(); i++) {
				Sample sample = new Sample();
				SampleDAO sampleDAO = new SampleDAOImpl();

				sample.setAccessionNumber((String)accessionNumbers.get(i));
				sampleDAO.getSampleByAccessionNumber(sample);

				//if even one sample in range has bad status then send back INVALID message
				if (!sample.getStatus().equalsIgnoreCase(expectedStatus)) {
					validStatuses = false;
					break;
				} 

			}
			
			if (validStatuses){
				s.append(VALID);
			} else {
				s.append(INVALID);
			}

		}

		return s.toString();
	}

    //bugzilla 1979
	private List populateAccessionNumberList(String fromAccessionNumber,
			String thruAccessionNumber) {
		List accessionNumbers = new ArrayList();
		if (!StringUtil.isNullorNill(thruAccessionNumber)) {
			int fromInt = Integer.parseInt(fromAccessionNumber);
			int thruInt = Integer.parseInt(thruAccessionNumber);
			for (int i = fromInt; i <= thruInt; i++) {
				accessionNumbers.add(String.valueOf(i));
			}
		} else {
			accessionNumbers.add(fromAccessionNumber);
		}
		return accessionNumbers;
	}

}
