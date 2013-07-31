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
package us.mn.state.health.lims.result.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.provider.reports.BaseReportsProvider;
import us.mn.state.health.lims.common.provider.reports.ReportsProviderFactory;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.result.valueholder.Sample_TestAnalyte;

/**
 * @author diane benz
 * bugzilla 1900
 */
public class BatchResultsVerificationPreviewReportAction extends BaseAction {

	private Log log = LogFactory.getLog(BatchResultsVerificationPreviewReportAction.class);

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;

		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");
        String selectedAccessionNumber = null;
        if (request.getParameter("selectedAccessionNumber") != null) {
        	selectedAccessionNumber = (String)request.getParameter("selectedAccessionNumber");
        } 
		
		BaseActionForm dynaForm = (BaseActionForm) form;

		String selectedTestSectionId = (String) dynaForm
		.get("selectedTestSectionId");
        String selectedTestId = (String) dynaForm.get("selectedTestId");
        String enteredAccessionNumber = (String) dynaForm.get("accessionNumber");
        List sampleTestAnalytes = (List) dynaForm.get("sample_TestAnalytes");

        List accessionNumbers = new ArrayList();
        if (StringUtil.isNullorNill(enteredAccessionNumber)) {
          if (StringUtil.isNullorNill(selectedAccessionNumber)) {
        	HashMap accessionNumberHash = new HashMap();
        	if (sampleTestAnalytes != null && sampleTestAnalytes.size() > 0) {
         		for (int i = 0; i < sampleTestAnalytes.size(); i++) {
        			Sample_TestAnalyte sta = (Sample_TestAnalyte)sampleTestAnalytes.get(i);
        			accessionNumberHash.put(sta.getSample().getAccessionNumber(), "");
        		}
        	}

        	for (Iterator entryIter = accessionNumberHash.entrySet().iterator(); entryIter.hasNext();) {
        		Map.Entry entry = (Map.Entry) entryIter.next();

        		String accessionNumb = (String) entry
        		.getKey();
        		accessionNumbers.add(accessionNumb);
        	}
          } else {
        	  accessionNumbers.add(selectedAccessionNumber);
          }
        } else {
        	accessionNumbers.add(enteredAccessionNumber);
        }


		ActionMessages errors = null;

		//bugzilla 2375 don't initialize we are not changing anything on this page
		//dynaForm.initialize(mapping);
		
        //set information needed by ResultsReportProvider to run the preview report
		request.setAttribute(ACCESSION_NUMBERS, accessionNumbers);
		request.setAttribute(RESULTS_REPORT_TYPE_PARAM, RESULTS_REPORT_TYPE_PREVIEW);

		HashMap parameters = new HashMap();
		String reportsProvider = "ResultsReportProvider";
        BaseReportsProvider reportProvider = (BaseReportsProvider)ReportsProviderFactory
				.getInstance().getReportsProvider(reportsProvider);

		reportProvider.setServlet(null);
		//bugzilla 2274: added error handling
		boolean success = reportProvider.processRequest(parameters, request, response);

        //if unsuccessful route back to reports menu
		if (!success) {
				forward = FWD_FAIL;
		
		}


		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
       return null;
	}

	protected String getPageSubtitleKey() {
       return null;
	}

}
