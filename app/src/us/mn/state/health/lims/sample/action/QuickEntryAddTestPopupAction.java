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
package us.mn.state.health.lims.sample.action;

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

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.login.dao.UserTestSectionDAO;
import us.mn.state.health.lims.login.daoimpl.UserTestSectionDAOImpl;
import us.mn.state.health.lims.panel.dao.PanelDAO;
import us.mn.state.health.lims.panel.daoimpl.PanelDAOImpl;
import us.mn.state.health.lims.panel.valueholder.Panel;
import us.mn.state.health.lims.panelitem.dao.PanelItemDAO;
import us.mn.state.health.lims.panelitem.daoimpl.PanelItemDAOImpl;
import us.mn.state.health.lims.panelitem.valueholder.PanelItem;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.AssignableTest;
import us.mn.state.health.lims.test.valueholder.AssignableTestComparator;
import us.mn.state.health.lims.test.valueholder.Test;

/**
 * The QuickEntryAddTestPopupAction class represents the Add Test
 * Popup Action for the QuickEntry form of the application.
 * 
 * @author	- Ken Rosha		08/29/2006
 * benzd1 - bugzilla 2223
 * benzd1 - replaced 1776 with 1844, 2293
 * also cleaned up to conform to naming standards
 * also made more efficient (added method to PanelItemDAO to get panelItems by panel instead of looping through all panelItems)
 */
public class QuickEntryAddTestPopupAction extends BaseAction {
	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseActionForm dynaForm = (BaseActionForm) form;
		dynaForm.initialize(mapping);

		PanelDAO panelDAO = new PanelDAOImpl();		
		TestDAO testDAO = new TestDAOImpl();
		PanelItemDAO panelItemDAO = new PanelItemDAOImpl();

		List testTypeAssignableTests = new ArrayList();
		List panelTypeAssignableTests = new ArrayList();
		List assignableTests = new ArrayList();
		AssignableTest assignableTest = null;

		
		//Get tests by user system id
		//bugzilla 2160		
		UserTestSectionDAO userTestSectionDAO = new UserTestSectionDAOImpl();
		//bugzilla 2291
		List tests = userTestSectionDAO.getAllUserTests(request, true);

		Iterator testIterator = tests.iterator();
		
		//bugzilla 2223 need to filter out (remove) tests that are not setup yet!
		while (testIterator.hasNext()) {
			Test aTest = (Test)testIterator.next();
			//bugzilla 2291 
            if (aTest.getIsActive().equals(IActionConstants.YES) ) {
              assignableTest = new AssignableTest(aTest);
              testTypeAssignableTests.add(assignableTest);
            }
		}

        //bugzilla 1844
		Collections.sort(testTypeAssignableTests, AssignableTestComparator.TEST_TYPE_DESCRIPTION_COMPARATOR);
		
		List panels = panelDAO.getAllActivePanels();

		Iterator panelIterator = panels.iterator();
		
		while(panelIterator.hasNext())			
		{			
			Panel currentPanel = (Panel) panelIterator.next();

			String panelTooltipString = "";
			String tooltipString = "";

			//bugzilla 2223 - need to remove panelItems pointing to tests not set up
			//bugzilla 2291
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

				if (null != panelItemTest && panelItemTest.getIsActive().equals(IActionConstants.YES)) {
					atLeastOnePanelItemWithSetupTestFound = true;

					assignableTest = new AssignableTest(panelItemTest);
                    listOfAssignableTests.add(assignableTest);
 					//bugzilla 2293
					panelTooltipString = panelTooltipString + assignableTest.getDisplayValue() + "&#013;";
					
				}

				

			}

			if ( panelTooltipString.length() > 6 ){				
				tooltipString = panelTooltipString.substring(0,panelTooltipString.length()-6);			
			}else{
				tooltipString = panelTooltipString;				
			}
			//System.out.println("panelToolltipString " + tooltipString);
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

		return mapping.findForward(FWD_SUCCESS);
	}

	// ==============================================================

	protected String getPageTitleKey() {
		return "quickentry.addTestPopup.title";
	}

	// ==============================================================

	protected String getPageSubtitleKey() {
		return "quickentry.addTestPopup.subtitle";
	}
	// ==============================================================
}
