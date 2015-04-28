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
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSDuplicateRecordException;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.util.resources.ResourceLocator;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.dao.UserTestSectionDAO;
import us.mn.state.health.lims.login.daoimpl.UserTestSectionDAOImpl;
import us.mn.state.health.lims.login.valueholder.UserSessionData;
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
public class QaEventUpdateAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "false");
		request.setAttribute(NEXT_DISABLED, "false");

		String id = request.getParameter(ID);

		if (StringUtil.isNullorNill(id) || "0".equals(id)) {
			isNew = true;
		} else {
			isNew = false;
		}

		BaseActionForm dynaForm = (BaseActionForm) form;

		// server-side validation (validation.xml)
		ActionMessages errors = dynaForm.validate(mapping, request);
		if (errors != null && errors.size() > 0) {
			saveErrors(request, errors);
			// since we forward to jsp - not Action we don't need to repopulate
			// the lists here
			return mapping.findForward(FWD_FAIL);
		}

		String start = (String) request.getParameter("startingRecNo");
		String direction = (String) request.getParameter("direction");

		QaEvent qaEvent = new QaEvent();
		// get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());
		qaEvent.setSysUserId(sysUserId);
		org.hibernate.Transaction tx = HibernateUtil.getSession()
				.beginTransaction();

		// populate valueholder from form
		PropertyUtils.copyProperties(qaEvent, dynaForm);
		String selectedTestId = dynaForm.getString("selectedTestId");
		// Bugzilla 2246 added variable selectedTypeId
		String selectedTypeId = dynaForm.getString("selectedTypeId");
        //bugzilla 2506		
		String selectedCategoryId = dynaForm.getString("selectedCategoryId");

		
		QaEventDAO qaEventDAO = new QaEventDAOImpl();
		TestDAO testDAO = new TestDAOImpl();
		DictionaryDAO dictionaryDAO = new DictionaryDAOImpl();


		try {

			if (!StringUtil.isNullorNill(selectedTestId)) {
				Test selectedTest = new Test();
				selectedTest.setId(selectedTestId);
				testDAO.getData(selectedTest);
				qaEvent.setTest(selectedTest);
			}
			// Bugzilla 2246 set the type value for the valueholder 
			if (!StringUtil.isNullorNill(selectedTypeId)) {
				Dictionary selectedDictionary = new Dictionary();
				selectedDictionary.setId(selectedTypeId);
				dictionaryDAO.getData(selectedDictionary);
				qaEvent.setType(selectedDictionary);
			}
			
			// Bugzilla 2506 set the category value for the valueholder 
			if (!StringUtil.isNullorNill(selectedCategoryId)) {
				Dictionary selectedDictionary = new Dictionary();
				selectedDictionary.setId(selectedCategoryId);
				dictionaryDAO.getData(selectedDictionary);
				qaEvent.setCategory(selectedDictionary);
			}


			if (!isNew) {
				// UPDATE
				qaEventDAO.updateData(qaEvent);
			} else {
				// INSERT
				qaEventDAO.insertData(qaEvent);
			}
			tx.commit();
		} catch (LIMSRuntimeException lre) {
    		//bugzilla 2154
			LogEvent.logError("QaEventUpdateAction","performAction()",lre.toString());
			tx.rollback();
			errors = new ActionMessages();
			ActionError error = null;
			if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
				error = new ActionError("errors.OptimisticLockException", null,
						null);
			} else {
				if (lre.getException() instanceof LIMSDuplicateRecordException) {
					java.util.Locale locale = (java.util.Locale) request
					.getSession().getAttribute(
							"org.apache.struts.action.LOCALE");
		         	String messageKey = "qaevent.name";
		         	String msg = ResourceLocator.getInstance()
					.getMessageResources().getMessage(locale,
							messageKey);
					error = new ActionError("errors.DuplicateRecord",
							msg, null);

				} else {
					error = new ActionError("errors.UpdateException", null,
							null);
				}
    		}
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);

			// disable previous and next
			request.setAttribute(PREVIOUS_DISABLED, "true");
			request.setAttribute(NEXT_DISABLED, "true");
			forward = FWD_FAIL;

		} finally {
			HibernateUtil.closeSession();
		}
		if (forward.equals(FWD_FAIL))
			return mapping.findForward(forward);

		// initialize the form
		dynaForm.initialize(mapping);

		//Get tests by user system id
		//bugzilla 2160
		UserTestSectionDAO userTestSectionDAO = new UserTestSectionDAOImpl();
		//bugzilla 2291
		List tests = userTestSectionDAO.getAllUserTests(request, true);
		//bugzilla 1856
		Collections.sort(tests, TestComparator.DESCRIPTION_COMPARATOR);
		List dictionaries = dictionaryDAO.getDictionaryEntrysByCategoryAbbreviation(SystemConfiguration.getInstance().getQaEventDictionaryCategoryType());
		//bugzilla 2506
		List dictionaries2 = dictionaryDAO.getDictionaryEntrysByCategoryAbbreviation(SystemConfiguration.getInstance().getQaEventDictionaryCategoryCategory());


		
		// repopulate the form from valueholder
		PropertyUtils.copyProperties(dynaForm, qaEvent);
		PropertyUtils.setProperty(form, "tests", tests);
		PropertyUtils.setProperty(form, "dictionaries", dictionaries);
		//bugzilla 2506
		PropertyUtils.setProperty(form, "dictionaries2", dictionaries2);

		if ("true".equalsIgnoreCase(request.getParameter("close"))) {
			forward = FWD_CLOSE;
		}

		if (qaEvent.getId() != null && !qaEvent.getId().equals("0")) {
			request.setAttribute(ID, qaEvent.getId());

		}

		if (isNew)
			forward = FWD_SUCCESS_INSERT;
		return getForward(mapping.findForward(forward), id, start, direction);
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