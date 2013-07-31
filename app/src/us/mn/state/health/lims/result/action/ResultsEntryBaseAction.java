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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.testanalyte.valueholder.TestAnalyte;
import us.mn.state.health.lims.testreflex.dao.TestReflexDAO;
import us.mn.state.health.lims.testreflex.daoimpl.TestReflexDAOImpl;
import us.mn.state.health.lims.testreflex.valueholder.TestReflex;
import us.mn.state.health.lims.testresult.valueholder.TestResult;

/**
 * @author Benzd1
 * removed validateAll method (moved to ResultsEntryUpdateAction)
 */
public class ResultsEntryBaseAction extends BaseAction {

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.mn.state.health.lims.common.action.BaseAction#performAction(org.apache.struts.action.ActionMapping,
	 *      org.apache.struts.action.ActionForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		return mapping.findForward("");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.mn.state.health.lims.common.action.BaseAction#getPageTitleKey()
	 */
	protected String getPageTitleKey() {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.mn.state.health.lims.common.action.BaseAction#getPageTitleKeyParameter(javax.servlet.http.HttpServletRequest,
	 *      org.apache.struts.action.ActionForm)
	 */
	protected String getPageTitleKeyParameter(HttpServletRequest request,
			ActionForm form) {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.mn.state.health.lims.common.action.BaseAction#getPageSubtitleKey()
	 */
	protected String getPageSubtitleKey() {
		return "";
	}

	/**
	 * @param testResultIdList
	 * @return
	 */
	 //bugzilla 1684: added testAnalyte to criteria
	protected List getReflexTestsForTestResultAndTestAnalyte(TestResult testResult, TestAnalyte testAnalyte) {
		TestReflexDAO reflexDAO = new TestReflexDAOImpl();

		List reflexes = new ArrayList();
		List addedTests = new ArrayList();

		reflexes = reflexDAO.getTestReflexesByTestResultAndTestAnalyte(testResult, testAnalyte);

		if (reflexes != null) {
			for (int i = 0; i < reflexes.size(); i++) {
				TestReflex testReflex = (TestReflex) reflexes.get(i);
				Test test = (Test) testReflex.getAddedTest();
				if (test != null && test.getIsActive().equals(YES)) {
					addedTests.add(test);
				}
			}
		}

		if (addedTests != null && addedTests.size() > 0) {
			return addedTests;
		} else {
			return null;
		}
	}

}
