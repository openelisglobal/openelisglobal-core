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

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.login.dao.UserTestSectionDAO;
import us.mn.state.health.lims.login.daoimpl.UserTestSectionDAOImpl;
import us.mn.state.health.lims.qaevent.dao.QaEventDAO;
import us.mn.state.health.lims.qaevent.daoimpl.QaEventDAOImpl;
import us.mn.state.health.lims.qaevent.valueholder.QaEvent;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.test.valueholder.TestComparator;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class QaEventAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String id = request.getParameter(ID);

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		DynaActionForm dynaForm = (DynaActionForm) form;

		// initialize the form
		dynaForm.initialize(mapping);

		QaEvent qaEvent = new QaEvent();
		TestDAO testDAO = new TestDAOImpl();
		DictionaryDAO dictionaryDAO = new DictionaryDAOImpl();

		if ((id != null) && (!"0".equals(id))) { // this is an existing
			// qaEvent

			qaEvent.setId(id);
			QaEventDAO qaEventDAO = new QaEventDAOImpl();
			qaEventDAO.getData(qaEvent);

			isNew = false; // this is to set correct page title

			// do we need to enable next or previous?
			List qaEvents = qaEventDAO.getNextQaEventRecord(qaEvent
					.getQaEventName());
			if (qaEvents.size() > 0) {
				// enable next button
				request.setAttribute(NEXT_DISABLED, "false");
			}

			qaEvents = qaEventDAO.getPreviousQaEventRecord(qaEvent
					.getQaEventName());
			if (qaEvents.size() > 0) {
				// enable next button
				request.setAttribute(PREVIOUS_DISABLED, "false");
			}
			// end of logic to enable next or previous button

		} else { // this is a new qaEvent

			isNew = true; // this is to set correct page title

		}

		if (qaEvent.getId() != null && !qaEvent.getId().equals("0")) {
			request.setAttribute(ID, qaEvent.getId());
		}
		//Get tests by user system id
		//bugzilla 2160
		UserTestSectionDAO userTestSectionDAO = new UserTestSectionDAOImpl();
		//bugzilla 2291
		List tests = userTestSectionDAO.getAllUserTests(request, true);
		List dictionaries = dictionaryDAO.getDictionaryEntrysByCategory(SystemConfiguration.getInstance().getQaEventDictionaryCategoryType());
		//bugzilla 2506
		List dictionaries2 = dictionaryDAO.getDictionaryEntrysByCategory(SystemConfiguration.getInstance().getQaEventDictionaryCategoryCategory());
		
		//bugzilla 1856
		Collections.sort(tests, TestComparator.DESCRIPTION_COMPARATOR);
		// populate form from valueholder
		PropertyUtils.copyProperties(form, qaEvent);
		PropertyUtils.setProperty(form, "tests", tests);
		PropertyUtils.setProperty(form, "dictionaries", dictionaries);
		//bugzilla 2506
		PropertyUtils.setProperty(form, "dictionaries2", dictionaries2);
		
		Test qaEventTest = (Test)qaEvent.getTest();
		// bugzilla 2246 changed name selectedTest to selectedTestId
		String selectedTestId = null;
		if (qaEventTest != null)
			selectedTestId = qaEventTest.getId();
		PropertyUtils.setProperty(form, "selectedTestId", selectedTestId);
		
		Dictionary type = (Dictionary)qaEvent.getType();
		String selectedTypeId = null;
		if (type != null)
			selectedTypeId = type.getId();
		// bugzilla 2246
		PropertyUtils.setProperty(form, "selectedTypeId", selectedTypeId);

		//bugzilla 2506
		Dictionary category = (Dictionary)qaEvent.getCategory();
		String selectedCategoryId = null;
		if (category != null)
			selectedCategoryId = category.getId();
		PropertyUtils.setProperty(form, "selectedCategoryId", selectedCategoryId);


		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "qaevent.add.title";
		} else {
			return "qaevent.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "qaevent.add.title";
		} else {
			return "qaevent.edit.title";
		}
	}

}
