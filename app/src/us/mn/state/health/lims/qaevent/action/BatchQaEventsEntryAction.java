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
package us.mn.state.health.lims.qaevent.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.qaevent.dao.QaEventDAO;
import us.mn.state.health.lims.qaevent.daoimpl.QaEventDAOImpl;
import us.mn.state.health.lims.qaevent.valueholder.QaEventComparator;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class BatchQaEventsEntryAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");

		BaseActionForm dynaForm = (BaseActionForm) form;

		// initialize the form
		dynaForm.initialize(mapping);

		String fromAccessionNumber = "";
		String toAccessionNumber = "";
		String skipAccessionNumber = "";
		
		
		QaEventDAO qaEventDAO = new QaEventDAOImpl();
		List qaEvents = new ArrayList();
		qaEvents = qaEventDAO.getAllQaEvents();

				
		
		Collections.sort(qaEvents, QaEventComparator.NAME_COMPARATOR);
				
		PropertyUtils.setProperty(dynaForm, "SelectList", qaEvents);
		PropertyUtils.setProperty(dynaForm, "pickIds", "");
		PropertyUtils.setProperty(dynaForm, "fromAccessionNumber", fromAccessionNumber);
		PropertyUtils.setProperty(dynaForm, "toAccessionNumber", toAccessionNumber);
		PropertyUtils.setProperty(dynaForm, "skipAccessionNumber", skipAccessionNumber);

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "batchqaeventsentry.add.title";
		} else {
			return "batchqaeventsentry.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "batchqaeventsentry.add.subtitle";
		} else {
			return "batchqaeventsentry.edit.subtitle";
		}
	}

}
