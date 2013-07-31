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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.panel.dao.PanelDAO;
import us.mn.state.health.lims.panel.daoimpl.PanelDAOImpl;
import us.mn.state.health.lims.panel.valueholder.Panel;
import us.mn.state.health.lims.panelitem.dao.PanelItemDAO;
import us.mn.state.health.lims.panelitem.daoimpl.PanelItemDAOImpl;
import us.mn.state.health.lims.panelitem.valueholder.PanelItem;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;

public class QuickEntryPopulateNewbornTestAction extends BaseAction {

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "false");
		
		HttpSession session = request.getSession();
		ArrayList selectedTestIds = new ArrayList();
		session.setAttribute("selectedTestIds", selectedTestIds);

		BaseActionForm dynaForm = (BaseActionForm)form;

		String accessionNumber = dynaForm.getString("accessionNumber");
		String accessionNumber2 = dynaForm.getString("accessionNumber2");
		PropertyUtils.setProperty(form, "accessionNumber2", accessionNumber2);
		String sourceOfSampleDesc = dynaForm.getString("sourceOfSampleDesc");
		PropertyUtils.setProperty(form, "sourceOfSampleDesc", sourceOfSampleDesc);
		String sourceOfSampleId = dynaForm.getString("sourceOfSampleId");
		PropertyUtils.setProperty(form, "sourceOfSampleId", sourceOfSampleId);
		String sourceOther = dynaForm.getString("sourceOther");
		PropertyUtils.setProperty(form, "sourceOther", sourceOther);
				
		TestDAO testDAO = new TestDAOImpl();
		String newbornTestPanelName = SystemConfiguration.getInstance().getNewbornTestPanelName();	
		
		PanelDAO panelDAO = new PanelDAOImpl();
		Panel panel = new Panel();
		panel.setPanelName(newbornTestPanelName);
		panel = panelDAO.getPanelByName(panel);

		PanelItemDAO panelItemDAO = new PanelItemDAOImpl();
		List panelItemsList = panelItemDAO.getPanelItemByPanel(panel, true);

 		String idSeparator = SystemConfiguration.getInstance().getDefaultIdSeparator();
 		String testIds = "";
 		String testNames = "";
		for ( int i=0; i<panelItemsList.size(); i++ ) {	
			PanelItem currentPanelitem = (PanelItem) panelItemsList.get(i);
			String testName = currentPanelitem.getTestName();
			Test panelItemTest = new Test();
			panelItemTest.setTestName(testName);
			panelItemTest = testDAO.getTestByName(panelItemTest);
 			testIds += idSeparator + panelItemTest.getId();
 			testNames += idSeparator + panelItemTest.getName(); 			
 		}
 		PropertyUtils.setProperty(form, "selectedTestIds", testIds);
 		PropertyUtils.setProperty(form, "selectedTestNames", testNames);
 		
		Sample	sample	= new Sample();

		// Set received date and entered date to today's date
		Date today = Calendar.getInstance().getTime();
		Locale locale = (Locale)request.getSession().getAttribute("org.apache.struts.action.LOCALE");
		String dateAsText = DateUtil.formatDateAsText(today, locale);

		SystemConfiguration sysConfig = SystemConfiguration.getInstance();
		
		sample.setReceivedDateForDisplay(dateAsText);
		sample.setEnteredDateForDisplay(dateAsText);
		sample.setReferredCultureFlag(sysConfig.getQuickEntryDefaultReferredCultureFlag());
		sample.setStickerReceivedFlag(sysConfig.getQuickEntryDefaultStickerReceivedFlag());
		sample.setAccessionNumber(accessionNumber);
		// default nextItemSequence to 1 (for clinical - always 1)
		sample.setNextItemSequence(sysConfig.getQuickEntryDefaultNextItemSequence());

		// revision is set to 0 on insert
		sample.setRevision(sysConfig.getQuickEntryDefaultRevision());

		sample.setCollectionTimeForDisplay(sysConfig.getQuickEntryDefaultCollectionTimeForDisplay());

		if (sample.getId() != null && !sample.getId().equals("0")) {
			request.setAttribute(ID, sample.getId());
		}

		// populate form from valueholder
		PropertyUtils.copyProperties(form, sample);

		PropertyUtils.setProperty(form, "currentDate", dateAsText);
		request.setAttribute("menuDefinition", "QuickEntryDefinition");
		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		return "quick.entry.edit.title";
	}

	protected String getPageSubtitleKey() {
		return "quick.entry.edit.title";
	}
}
