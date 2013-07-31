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
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.servlet.validation.AjaxServlet;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.sampletracking.dao.SampleTrackingDAO;
import us.mn.state.health.lims.sampletracking.daoimpl.SampleTrackingDAOImpl;
import us.mn.state.health.lims.sampletracking.valueholder.SampleTracking;
import us.mn.state.health.lims.sampletracking.valueholder.SampleTrackingCriteria;
//AIS - bugzilla 1851/1853
//bugzilla 1920 refactor to conform to standards
public class SampleTrackingValidationProvider extends BaseValidationProvider {

	public SampleTrackingValidationProvider() {
		super();
	}

	public SampleTrackingValidationProvider(AjaxServlet ajaxServlet) {
		this.ajaxServlet = ajaxServlet;
	}

	public void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	
		String formField = (String) request.getParameter("field");

		
		SampleTrackingCriteria sampleTrackingCriteria = new SampleTrackingCriteria();
		sampleTrackingCriteria.setClientRef((String) request.getParameter("cr"));
		sampleTrackingCriteria.setLastName((String) request.getParameter("ln"));
		sampleTrackingCriteria.setFirstName((String) request.getParameter("fn"));
		sampleTrackingCriteria.setSubmitter((String) request.getParameter("sub"));
		sampleTrackingCriteria.setReceivedDate((String) request.getParameter("rd"));
		sampleTrackingCriteria.setSampleType((String) request.getParameter("st"));
		sampleTrackingCriteria.setSampleSource((String) request.getParameter("ss"));
		sampleTrackingCriteria.setExternalId(request.getParameter("ei"));
		sampleTrackingCriteria.setCollectionDate(request.getParameter("cd"));
		sampleTrackingCriteria.setAccessionNumberPartial(request.getParameter("an"));
		sampleTrackingCriteria.setProjectId(request.getParameter("pi"));
		sampleTrackingCriteria.setSortBy(request.getParameter("sb"));
		//bugzilla 2455	
		sampleTrackingCriteria.setSpecimenOrIsolate((String)request.getParameter("si"));
		
		String result = validate(sampleTrackingCriteria);
		ajaxServlet.sendData(formField, result, request, response);
	}

	// AIS - bugzilla 1493
	public String validate(SampleTrackingCriteria sampleTrackingCriteria) throws LIMSRuntimeException {

		StringBuffer s = new StringBuffer();
		String accNum = "";

		SampleTrackingDAO sampleTrackingDAO = new SampleTrackingDAOImpl();

		List listOfSamples = sampleTrackingDAO
				.getAccessionByPatientAndOtherCriteria(sampleTrackingCriteria);

		//bugzilla 2189
		if ((listOfSamples != null) && (listOfSamples.size() != 0)) {
			if (listOfSamples.size() > 1) {
				s.append(MORE_THAN_ONE_ACCESSION_NUMBER);
			} else {
				s.append(VALID);
				SampleTracking st = (SampleTracking)listOfSamples.get(0);
				accNum = st.getAccNum(); 
			}
		} else {
			s.append(INVALIDOTHERS);
		}		
		
		String str = s.toString();
		str = str + SystemConfiguration.getInstance().getDefaultIdSeparator()
				+ accNum;
				

		return str;
	}
}
