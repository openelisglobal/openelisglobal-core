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
package us.mn.state.health.lims.testresult.action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import us.mn.state.health.lims.common.action.BaseMenuAction;
import us.mn.state.health.lims.common.services.TestService;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class TestResultMenuAction extends BaseMenuAction {

	@SuppressWarnings("unchecked")
	protected List createMenuList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		List<TestResult> testResults = new ArrayList<TestResult>();

		String stringStartingRecNo = (String) request.getAttribute("startingRecNo");
		int startingRecNo = Integer.parseInt(stringStartingRecNo);

		TestResultDAO testResultDAO = new TestResultDAOImpl();
		testResults = testResultDAO.getPageOfTestResults(startingRecNo);

		request.setAttribute("menuDefinition", "TestResultMenuDefinition");

		request.setAttribute(MENU_TOTAL_RECORDS, String.valueOf(testResultDAO.getTotalCount("TestResult", TestResult.class)));
		request.setAttribute(MENU_FROM_RECORD, String.valueOf(startingRecNo));
		
		int numOfRecs = 0;
		if( !testResults.isEmpty()){
			numOfRecs = Math.min(testResults.size(),SystemConfiguration.getInstance().getDefaultPageSize() ) - 1;
			
			java.util.Collections.sort(testResults, new Comparator<TestResult>(){
				public int compare(TestResult a, TestResult b){
					return TestService.getUserLocalizedTestName( a.getTest() ).compareTo(TestService.getUserLocalizedTestName( b.getTest() ));
				}
			});
		}
				
		int endingRecNo = startingRecNo + numOfRecs;
		request.setAttribute(MENU_TO_RECORD, String.valueOf(endingRecNo));
		
		return testResults;
				
	}

	protected String getPageTitleKey() {
		return "testresult.browse.title";
	}

	protected String getPageSubtitleKey() {
		return "testresult.browse.title";
	}

	protected int getPageSize() {
		return SystemConfiguration.getInstance().getDefaultPageSize();
	}

	protected String getDeactivateDisabled() {
		return "true";
	}
}
