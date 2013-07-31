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
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.util.StringUtil;
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
public class PersonAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new Person.
		// If there is a parameter present, we should bring up an existing
		// Person to edit.

		String id = request.getParameter(ID);

		String forward = "success";
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		DynaActionForm dynaForm = (DynaActionForm) form;

		// initialize the form
		dynaForm.initialize(mapping);

		Person person = new Person();
		
		String hPhoneForDisplay = "";
		String wPhoneForDisplay = "";
		String cPhoneForDisplay = "";
		String faxForDisplay = "";

		String wPhoneExtForDisplay ="";

		//System.out.println("I am in PersonAction and this is id " + id);
		if ((id != null) && (!"0".equals(id))) { // this is an existing
			// person

			person.setId(id);
			PersonDAO personDAO = new PersonDAOImpl();
			personDAO.getData(person);

			// format phone numbers for display
			String wPhone = person.getWorkPhone();
			wPhoneForDisplay = StringUtil.formatPhoneForDisplay(wPhone);
			wPhoneExtForDisplay = StringUtil.formatExtensionForDisplay(wPhone);

			String hPhone = person.getHomePhone();
			hPhoneForDisplay = StringUtil.formatPhoneForDisplay(hPhone);
			
			String cPhone = person.getCellPhone();
			cPhoneForDisplay = StringUtil.formatPhoneForDisplay(cPhone);
			
			String fax = person.getFax();
			faxForDisplay = StringUtil.formatPhoneForDisplay(fax);


			

			isNew = false; // this is to set correct page title

			// do we need to enable next or previous?
			List persons = personDAO.getNextPersonRecord(person.getId());
			if (persons.size() > 0) {
				// enable next button
				request.setAttribute(NEXT_DISABLED, "false");
			}
			persons = personDAO.getPreviousPersonRecord(person.getId());
			if (persons.size() > 0) {
				// enable next button
				request.setAttribute(PREVIOUS_DISABLED, "false");
			}
			// end of logic to enable next or previous button
		} else { // this is a new person

			isNew = true; // this is to set correct page title
		}

		if (person.getId() != null && !person.getId().equals("0")) {
			request.setAttribute(ID, person.getId());
		}

		// populate form from valueholder
		PropertyUtils.copyProperties(form, person);
		
		PropertyUtils.setProperty(dynaForm, "homePhone", hPhoneForDisplay);
		PropertyUtils.setProperty(dynaForm, "workPhone", wPhoneForDisplay);
		PropertyUtils.setProperty(dynaForm, "cellPhone", cPhoneForDisplay);
		PropertyUtils.setProperty(dynaForm, "fax", faxForDisplay);

		PropertyUtils.setProperty(dynaForm, "workPhoneExtension", wPhoneExtForDisplay);
		

		return mapping.findForward(forward);
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
