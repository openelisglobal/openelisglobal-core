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

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.servlet.validation.AjaxServlet;
import us.mn.state.health.lims.test.valueholder.Test;
/**
 * bugzilla 2236
 * The TestAnalyteTestResultValidationProvider class is used to 
 * validate, via AJAX, the Test Information entered on the  
 * Test Analyte/Test Result Setup View.
 * 
 * @author	benzd1	01/11/2008
 */
public class TestAnalyteTestResultValidationProvider extends BaseValidationProvider {

	public TestAnalyteTestResultValidationProvider() {
		super();
	}

	public TestAnalyteTestResultValidationProvider(AjaxServlet ajaxServlet) {
		this.ajaxServlet = ajaxServlet;
	}

	public void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	
		//field is test id
		String formField = (String) request.getParameter("field");
		String targetId = (String) request.getParameter("id");
		
		String result = validate(targetId);
		ajaxServlet.sendData(formField, result, request, response);
	}

	public String validate(String targetId) throws LIMSRuntimeException {

        String result = VALID;

        //at least one component with one result group defined must exist if a sample is attached to this test
        //else give option to cancel out of save
        //if they choose to continue - delete analyses associated with this test
        String testId = targetId;
        Test test = new Test();
        test.setId(testId);
        
        //get all analyses for this test
        AnalysisDAO analysisDAO = new AnalysisDAOImpl();
        List analyses = analysisDAO.getAllAnalysesPerTest(test);
        
        if (analyses != null && analyses.size() > 0) {
        	result = INVALID;
        }
        
		return result;
	}
}
