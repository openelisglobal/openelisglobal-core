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
 *
 * Contributor(s): CIRG, University of Washington, Seattle WA.
 */
package us.mn.state.health.lims.testreflex.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.common.action.BaseMenuAction;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.testreflex.dao.TestReflexDAO;
import us.mn.state.health.lims.testreflex.daoimpl.TestReflexDAOImpl;
import us.mn.state.health.lims.testreflex.valueholder.TestReflex;
import us.mn.state.health.lims.testresult.valueholder.TestResult;

/**
 * @author diane benz
 *
 *         To change this generated comment edit the template variable
 *         "typecomment": Window>Preferences>Java>Templates. To enable and
 *         disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
public class TestReflexMenuAction extends BaseMenuAction {


	@SuppressWarnings("unchecked")
	protected List createMenuList(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String stringStartingRecNo = (String) request.getAttribute("startingRecNo");
		int startingRecNo = Integer.parseInt(stringStartingRecNo);

		List<TestReflex> testReflexs = getPageOfGroupedTestReflexs(startingRecNo);

		DictionaryDAO dictDAO = new DictionaryDAOImpl();

		List<String> listOfProcessedTestResultsForDisplay = new ArrayList<String>();

		for (TestReflex testReflex : testReflexs) {
			TestResult testResult = testReflex.getTestResult();

			if (testResult != null && !listOfProcessedTestResultsForDisplay.contains(testResult.getId())) {

				if (testResult.getTestResultType().equals(SystemConfiguration.getInstance().getDictionaryType())) {

					Dictionary dictionary = new Dictionary();
					dictionary.setId(testResult.getValue());
					dictDAO.getData(dictionary);

					testResult.setValue(dictionary.getDictEntryDisplayValue());
					listOfProcessedTestResultsForDisplay.add(testResult.getId());

				}

			}
		}

		request.setAttribute("menuDefinition", "TestReflexMenuDefinition");

		setDisplayPageBounds(request, testReflexs.size(), startingRecNo, new TestReflexDAOImpl(), TestReflex.class);

		return testReflexs;
	}

	@SuppressWarnings("unchecked")
	private List<TestReflex> getPageOfGroupedTestReflexs(int startingRecNo) {

		TestReflexDAO testReflexDAO = new TestReflexDAOImpl();
		List<TestReflex> allTestReflexs = testReflexDAO.getAllTestReflexs();
		List<TestReflex> pagedTestReflex = new ArrayList<TestReflex>();

		int startingIndexNo = Math.max(0, startingRecNo - 1);
		int virtualPagedIndex = 0;

		for (int i = 0; i < allTestReflexs.size(); i++) {
			TestReflex reflex = allTestReflexs.get(i);

			if (reflex != null) { // sibling reflexes will be set to null

				if (virtualPagedIndex >= startingIndexNo) {
					pagedTestReflex.add(reflex);
				}

				virtualPagedIndex++;

				if (reflex.getSiblingReflexId() != null) {
					String startingReflexId = reflex.getId();

					boolean allSiblingsFound = false;
					while( !allSiblingsFound ){
						int siblingIndex = getIndexOfSibling(reflex.getSiblingReflexId(), allTestReflexs, i);

						TestReflex siblingReflex = allTestReflexs.get(siblingIndex);
						if( siblingReflex == null ||
						    siblingReflex.getSiblingReflexId() == null ||
						    siblingReflex.getSiblingReflexId().equals(startingReflexId) ){
							allSiblingsFound = true;
						}

						if (virtualPagedIndex >= startingIndexNo) {
							siblingReflex.setPassiveSibling(true);
							pagedTestReflex.add(siblingReflex);
						}

						virtualPagedIndex++;

						allTestReflexs.set(siblingIndex, null);

						reflex = siblingReflex;
					}
				}

				//we need to go one beyond the page size to get next previous to work
				if (pagedTestReflex.size() > getPageSize()) {
					break;
				}
			}

		}

		return pagedTestReflex;
	}

	private int getIndexOfSibling(String siblingReflex, List<TestReflex> totalTestReflex, int i) {
		for( int index = i; index < totalTestReflex.size(); index++ ){

			if( (totalTestReflex.get(index) !=  null)) {

				if( siblingReflex.equals( totalTestReflex.get(index).getId())){
					return index;
				}
			}
		}
		return i;
	}

	protected String getPageTitleKey() {
		return "testreflex.browse.title";
	}

	protected String getPageSubtitleKey() {
		return "testreflex.browse.title";
	}

	@Override
	protected String getDeactivateDisabled() {
		return "false";
	}

	@Override
	protected String getEditDisabled() {
		return "true";
	}

}
