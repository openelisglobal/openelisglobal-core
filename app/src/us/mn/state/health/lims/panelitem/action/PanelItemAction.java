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
package us.mn.state.health.lims.panelitem.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.login.dao.UserTestSectionDAO;
import us.mn.state.health.lims.login.daoimpl.UserTestSectionDAOImpl;
import us.mn.state.health.lims.method.dao.MethodDAO;
import us.mn.state.health.lims.method.daoimpl.MethodDAOImpl;
import us.mn.state.health.lims.panel.dao.PanelDAO;
import us.mn.state.health.lims.panel.daoimpl.PanelDAOImpl;
import us.mn.state.health.lims.panel.valueholder.Panel;
import us.mn.state.health.lims.panelitem.dao.PanelItemDAO;
import us.mn.state.health.lims.panelitem.daoimpl.PanelItemDAOImpl;
import us.mn.state.health.lims.panelitem.valueholder.PanelItem;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class PanelItemAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new PanelItem.
		// If there is a parameter present, we should bring up an existing
		// PanelItem to edit.
		String id = request.getParameter(ID);

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		DynaActionForm dynaForm = (DynaActionForm) form;

		// initialize the form
		dynaForm.initialize(mapping);

		PanelItem panelItem = new PanelItem();

		if ((id != null) && (!"0".equals(id))) { // this is an existing
			// panelItem

			panelItem.setId(id);
			PanelItemDAO panelItemDAO = new PanelItemDAOImpl();
			panelItemDAO.getData(panelItem);
			// initialize selectedPanelItemId
			if (panelItem.getPanel() != null) {
				panelItem.setSelectedPanelId(panelItem.getPanel().getId());
			}
			isNew = false; // this is to set correct page title

			// do we need to enable next or previous?
			List panelItems = panelItemDAO.getNextPanelItemRecord(panelItem
					.getId());
			if (panelItems.size() > 0) {
				// enable next button
				request.setAttribute(NEXT_DISABLED, "false");
			}
			panelItems = panelItemDAO.getPreviousPanelItemRecord(panelItem
					.getId());
			if (panelItems.size() > 0) {
				// enable next button
				request.setAttribute(PREVIOUS_DISABLED, "false");
			}
			// end of logic to enable next or previous button
		} else { // this is a new panelItem

			isNew = true; // this is to set correct page title
		}

		if (panelItem.getId() != null && !panelItem.getId().equals("0")) {
			request.setAttribute(ID, panelItem.getId());
		}

		// populate form from valueholder
		PropertyUtils.copyProperties(form, panelItem);

		PanelDAO panelDAO = new PanelDAOImpl();
		MethodDAO methodDAO = new MethodDAOImpl();
		TestDAO testDAO = new TestDAOImpl();

		List parentPanels = panelDAO.getAllActivePanels();
		List methods = methodDAO.getAllMethods();

		//Get tests by user system id
		//bugzilla 2160
		UserTestSectionDAO userTestSectionDAO = new UserTestSectionDAOImpl();
		//bugzilla 2291
		List tests = userTestSectionDAO.getAllUserTests(request, true);
	
	
		// set parentPanelName
		String parentPanelName = null;
		for (int i = 0; i < parentPanels.size(); i++) {
			Panel parentPanel = (Panel) parentPanels.get(i);
			if (parentPanel.getId().equals(panelItem.getSelectedPanelId())) {
				parentPanelName = parentPanel.getPanelName();
			}
		}

		PropertyUtils.setProperty(form, "parentPanels", parentPanels);
		PropertyUtils.setProperty(form, "parentPanelName", parentPanelName);
		PropertyUtils.setProperty(form, "methods", methods);
		PropertyUtils.setProperty(form, "tests", tests);

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "panelitem.add.title";
		} else {
			return "panelitem.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "panelitem.add.title";
		} else {
			return "panelitem.edit.title";
		}
	}

}
