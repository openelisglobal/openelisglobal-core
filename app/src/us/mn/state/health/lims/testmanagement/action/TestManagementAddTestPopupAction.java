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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.login.dao.UserTestSectionDAO;
import us.mn.state.health.lims.login.daoimpl.UserTestSectionDAOImpl;
import us.mn.state.health.lims.panel.dao.PanelDAO;
import us.mn.state.health.lims.panel.daoimpl.PanelDAOImpl;
import us.mn.state.health.lims.panel.valueholder.Panel;
import us.mn.state.health.lims.panelitem.dao.PanelItemDAO;
import us.mn.state.health.lims.panelitem.daoimpl.PanelItemDAOImpl;
import us.mn.state.health.lims.panelitem.valueholder.PanelItem;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.AssignableTest;
import us.mn.state.health.lims.test.valueholder.AssignableTestComparator;
import us.mn.state.health.lims.test.valueholder.Test;

/**
 * @author aiswarya raman
 * benzd1 - bugzillas 2223, 2227
 * benzd1 - replaced bugzilla 1776 with 1844, 2293
 * also cleaned up to conform to naming standards
 * also made more efficient (added method to PanelItemDAO to get panelItems by panel instead of looping through all panelItems)
 */
public class TestManagementAddTestPopupAction extends BaseAction {	

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");

		BaseActionForm dynaForm = (BaseActionForm) form;

		// initialize the form
		dynaForm.initialize(mapping);
		
		//Get tests by user system id
		//bugzilla 2160		
		UserTestSectionDAO userTestSectionDAO = new UserTestSectionDAOImpl();
		//bugzilla 2291 added onlyTestsFullySetup
		List tests = userTestSectionDAO.getAllUserTests(request, true);
		
		//bugzilla 2223 need to filter out (remove) tests that are not setup yet!
		TestDAO testDAO = new TestDAOImpl();
				
		List testTypeAssignableTests = new ArrayList();
		List panelTypeAssignableTests = new ArrayList();
		List assignableTests = new ArrayList();
		AssignableTest assignableTest = null;
		
		SampleDAO sampleDAO = new SampleDAOImpl();			
		SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();	
		AnalysisDAO analysisDAO = new AnalysisDAOImpl();
		PanelDAO panelDAO = new PanelDAOImpl();		
		PanelItemDAO panelItemDAO = new PanelItemDAOImpl();
		
		Sample sample = new Sample();			
		SampleItem sampleItem = new SampleItem();
		List analyses = new ArrayList();		

		List selectedTestIds = new ArrayList();
		
		String accessionNumber = request.getParameter(ACCESSION_NUMBER);
		
		sample.setAccessionNumber(accessionNumber);		
		sampleDAO.getSampleByAccessionNumber(sample);		
			
		if (!StringUtil.isNullorNill(sample.getId())) {			
			//bugzilla 1773 need to store sample not sampleId for use in sorting
			sampleItem.setSample(sample);
			sampleItemDAO.getDataBySample(sampleItem);	
			if (sampleItem.getId() != null ){
				//bugzilla 2227
				//bugzilla 2532 (don't include child tests)
				analyses = analysisDAO.getMaxRevisionParentTestAnalysesBySample(sampleItem);				
			}
		}
		
		if (analyses != null) {
			// there is one Analysis per Test			
			for (int i = 0; i < analyses.size(); i++) {				
				Analysis analysis = (Analysis) analyses.get(i);				
				Test test = (Test) analysis.getTest();					
    			selectedTestIds.add(test.getId());							
			}			
		}
		
		//the tests to select from should not contain already selected tests
		for (int i = 0; i < tests.size(); i++) {	
			Test aTest = (Test)tests.get(i);
            if (!selectedTestIds.contains(aTest.getId())) {				
	            if (aTest.getIsActive().equals(IActionConstants.YES) ) {
	                assignableTest = new AssignableTest(aTest);
	                testTypeAssignableTests.add(assignableTest);
	             }

			}
			
		}		

		
		Collections.sort(testTypeAssignableTests, AssignableTestComparator.TEST_TYPE_DESCRIPTION_COMPARATOR);
		
		List panels = panelDAO.getAllActivePanels();
		
		Iterator panelIterator = panels.iterator();
		panels = new ArrayList();
		
		while(panelIterator.hasNext())			
		{			
			Panel currentPanel = (Panel) panelIterator.next();

			String panelTooltipString = "";
			String tooltipString = "";

			//bugzilla 2223 - need to remove panelItems pointing to tests not set up
			//bugzilla 2291 added onlyTestsFullySetup
			List panelItemsForPanel = panelItemDAO.getPanelItemByPanel(currentPanel, true);

			//bugzilla 2223 - remove a panel if all of associated tests are not set up yet
			if (panelItemsForPanel == null || panelItemsForPanel.size() == 0) {
				continue;
			}
			
			Iterator panelItemIterator = panelItemsForPanel.iterator();
			boolean atLeastOnePanelItemWithSetupTestFound = false;
			List listOfAssignableTests = new ArrayList();

			while(panelItemIterator.hasNext())	{		
				PanelItem currentPanelitem = (PanelItem) panelItemIterator.next();
				String testName = currentPanelitem.getTestName();
				Test panelItemTest = new Test();
				panelItemTest.setTestName(testName);
				panelItemTest = testDAO.getTestByName(panelItemTest);

                //bugzilla 2291
				if (null != panelItemTest) {
				     assignableTest = new AssignableTest(panelItemTest);
					
				     //if already selected then just add to tooltip
					if (!selectedTestIds.contains(panelItemTest.getId())) {
					 atLeastOnePanelItemWithSetupTestFound = true;
                     listOfAssignableTests.add(assignableTest);
					}
					
					panelTooltipString = panelTooltipString + assignableTest.getDisplayValue() + "&#013;";
				}
			}

			if ( panelTooltipString.length() > 6 ){				
				tooltipString = panelTooltipString.substring(0,panelTooltipString.length()-6);			
			}else{
				tooltipString = panelTooltipString;				
			}
			
			assignableTest = new AssignableTest(currentPanel);
			assignableTest.setTooltipText(tooltipString);
			Collections.sort(listOfAssignableTests, AssignableTestComparator.TEST_TYPE_NAME_COMPARATOR);
			assignableTest.setListOfAssignableTests(listOfAssignableTests);

			if (atLeastOnePanelItemWithSetupTestFound) {
			  panelTypeAssignableTests.add(assignableTest);
			}

		}
		Collections.sort(panelTypeAssignableTests, AssignableTestComparator.PANEL_TYPE_DESCRIPTION_COMPARATOR);

		//panel types should sort to the top
		assignableTests.addAll(panelTypeAssignableTests);
        assignableTests.addAll(testTypeAssignableTests);
				
		PropertyUtils.setProperty(dynaForm, "SelectList", assignableTests);

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		return "quickentry.addTestPopup.title";
	}

	protected String getPageSubtitleKey() {
		return "quickentry.addTestPopup.subtitle";
	}

}
