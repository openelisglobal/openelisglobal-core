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
package us.mn.state.health.lims.person.action;

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
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.valueholder.UserSessionData;
import us.mn.state.health.lims.person.dao.PersonDAO;
import us.mn.state.health.lims.person.daoimpl.PersonDAOImpl;
import us.mn.state.health.lims.person.valueholder.Person;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class PersonUpdateAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new Person.
		// If there is a parameter present, we should bring up an existing
		// Person to edit.
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

		// System.out.println("This is ID from request " + id);
		Person person = new Person();
		//get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());	
		person.setSysUserId(sysUserId);		
		org.hibernate.Transaction tx = HibernateUtil.getSession().beginTransaction();
		
		// populate valueholder from form
		PropertyUtils.copyProperties(person, dynaForm);

		// format workPhone and homePhone for storage
		String workPhone = (String) dynaForm.get("workPhone");
		String ext = (String) dynaForm.get("workPhoneExtension");
		String formattedPhone = StringUtil.formatPhone(workPhone, ext);
		// phone is stored as 999/999-9999.9999
		// area code/phone - number.extension
		person.setWorkPhone(formattedPhone);

		String homePhone = (String) dynaForm.get("homePhone");
		ext = "";
		formattedPhone = StringUtil.formatPhone(homePhone, ext);
		// phone is stored as 999/999-9999.9999
		// area code/phone - number.extension
		person.setHomePhone(formattedPhone);

		String cellPhone = (String) dynaForm.get("cellPhone");
		ext = "";
		formattedPhone = StringUtil.formatPhone(cellPhone, ext);
		// phone is stored as 999/999-9999.9999
		// area code/phone - number.extension
		person.setCellPhone(formattedPhone);

		String fax = (String) dynaForm.get("fax");
		ext = "";
		formattedPhone = StringUtil.formatPhone(fax, ext);
		// phone is stored as 999/999-9999.9999
		// area code/phone - number.extension
		person.setFax(formattedPhone);

		try {

			PersonDAO personDAO = new PersonDAOImpl();

			if (!isNew) {
				// UPDATE

				personDAO.updateData(person);
				if (FWD_NEXT.equals(direction)) {
					List persons = personDAO
							.getNextPersonRecord(person.getId());
					if (persons != null && persons.size() > 0) {
						person = (Person) persons.get(0);
						personDAO.getData(person);
						if (persons.size() < 2) {
							// disable next button
							request.setAttribute(NEXT_DISABLED, "true");
						}
						id = person.getId();
					} else {
						// disable next button
						request.setAttribute(NEXT_DISABLED, "true");
					}
					forward = FWD_NEXT;
				}

				if (FWD_PREVIOUS.equals(direction)) {
					List persons = personDAO.getPreviousPersonRecord(person
							.getId());
					if (persons != null && persons.size() > 0) {
						person = (Person) persons.get(0);
						personDAO.getData(person);
						if (persons.size() < 2) {
							// disable previous button
							request.setAttribute(PREVIOUS_DISABLED, "true");
						}
						id = person.getId();
					} else {
						// disable previous button
						request.setAttribute(PREVIOUS_DISABLED, "true");
					}
					forward = FWD_PREVIOUS;
				}
			} else {
				// INSERT

				personDAO.insertData(person);
			}
			tx.commit();
		} catch (LIMSRuntimeException lre) {
            //bugzilla 2154
			LogEvent.logError("PersonUpdateAction","performAction()",lre.toString()); 
			tx.rollback();
			errors = new ActionMessages();
			ActionError error = null;
			if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
				// how can I get popup instead of struts error at the top of
				// page?
				// ActionMessages errors = dynaForm.validate(mapping, request);
				error = new ActionError("errors.OptimisticLockException", null,
						null);
			} else {
				error = new ActionError("errors.UpdateException", null, null);
			}
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute(ALLOW_EDITS_KEY, "false");
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
		// repopulate the form from valueholder
		PropertyUtils.copyProperties(dynaForm, person);

		if ("true".equalsIgnoreCase(request.getParameter("close"))) {
			forward = FWD_CLOSE;
		}

		if (person.getId() != null && !person.getId().equals("0")) {
			request.setAttribute(ID, person.getId());

		}

		//bugzilla 1400
		if (isNew) forward = FWD_SUCCESS_INSERT;
		return getForward(mapping.findForward(forward), id, start);

	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "person.add.title";
		} else {
			return "person.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "person.add.title";
		} else {
			return "person.edit.title";
		}
	}

}