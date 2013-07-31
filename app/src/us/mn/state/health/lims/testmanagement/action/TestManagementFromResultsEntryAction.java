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
package us.mn.state.health.lims.testmanagement.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.testmanagement.valueholder.TestManagementRoutingSwitchSessionHandler;

/**
 * @author benzd1
 * bugzilla 2053
 * bugzilla 2614 - fix to work for NB samples
 */
public class TestManagementFromResultsEntryAction extends BaseAction {	

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String forward = FWD_SUCCESS;
		HttpSession session = request.getSession();
		TestManagementRoutingSwitchSessionHandler.switchOn(TEST_MANAGEMENT_ROUTING_FROM_RESULTS_ENTRY, session);

		//bugzilla 2614
		String accessionNumber = null;
		accessionNumber = (String)request.getParameter(ACCESSION_NUMBER);

		SampleDAO sampleDAO = new SampleDAOImpl();
		Sample sample = new Sample();
		sample.setAccessionNumber(accessionNumber);
		sampleDAO.getSampleByAccessionNumber(sample);
		
		if (sample.getDomain().equals(SystemConfiguration.getInstance().getNewbornDomain())) {
			forward = FWD_SUCCESS_NEWBORN;
		}
		
		return mapping.findForward(forward);	
	}
	
	protected String getPageTitleKey() {
		return "testmanagement.title";
	}

	protected String getPageSubtitleKey() {
		return "testmanagement.title";
	}
}