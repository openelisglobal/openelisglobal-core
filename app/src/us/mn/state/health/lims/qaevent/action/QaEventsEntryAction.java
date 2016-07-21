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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.organization.valueholder.Organization;
import us.mn.state.health.lims.project.valueholder.Project;
import us.mn.state.health.lims.qaevent.valueholder.QaEventRoutingSwitchSessionHandler;
import us.mn.state.health.lims.sourceofsample.valueholder.SourceOfSample;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;

/**
 * @author diane benz
 * 
 * bugzilla 2028
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class QaEventsEntryAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String id = request.getParameter(ID);

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");
		//bugzilla 2501
		request.setAttribute(MULTIPLE_SAMPLE_MODE, "false");
		
		HttpSession session = request.getSession();
		//bugzila 2053
		QaEventRoutingSwitchSessionHandler.switchAllOff(session);


		DynaActionForm dynaForm = (DynaActionForm) form;

		// initialize the form
		dynaForm.initialize(mapping);

		DictionaryDAO dictionaryDAO = new DictionaryDAOImpl();
		List categoryDictionaries = dictionaryDAO.getDictionaryEntrysByCategoryAbbreviation(SystemConfiguration.getInstance().getQaEventDictionaryCategoryCategory());


		PropertyUtils.setProperty(form, "testQaEvents", new ArrayList());
		PropertyUtils.setProperty(form, "organization", new Organization());
		PropertyUtils.setProperty(form, "project", new Project());
		PropertyUtils.setProperty(form, "typeOfSample", new TypeOfSample());
		PropertyUtils.setProperty(form, "sourceOfSample", new SourceOfSample());
		PropertyUtils.setProperty(form, "categoryDictionaries", categoryDictionaries);
		PropertyUtils.setProperty(form, "currentCount", "0");
		PropertyUtils.setProperty(form, "totalCount", "0");

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "qaeventsentry.add.title";
		} else {
			return "qaeventsentry.edit.title";
		}
	}

	protected String getPageTitleKeyParameter(HttpServletRequest request,
			ActionForm form) {
		BaseActionForm dynaForm = (BaseActionForm) form;
		String accn = "";
		if (dynaForm.get("accessionNumber") != null) {
			accn = (String) dynaForm.get("accessionNumber");
		}
		return accn;
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "qaeventsentry.add.subtitle";
		} else {
			return "qaeventsentry.edit.subtitle";
		}
	}

}
