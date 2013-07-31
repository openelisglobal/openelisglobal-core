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

import java.sql.Timestamp;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class HumanSampleTwoResetAction extends BaseAction {
	static private String FWD_CLOSE = "close";

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseActionForm dynaForm = (BaseActionForm) form;

		// first get the accessionNumber and whether we are on blank page or not
		String accessionNumber = (String) dynaForm.get("accessionNumber");
		List typeOfSamples = (List) dynaForm.get("typeOfSamples");
		List sourceOfSamples = (List) dynaForm.get("sourceOfSamples");

		// initialize the form but retain the invalid accessionNumber
		dynaForm.initialize(mapping);
		dynaForm.set("accessionNumber", accessionNumber);
		//repopulate lists
		PropertyUtils.setProperty(dynaForm, "typeOfSamples", typeOfSamples);
		PropertyUtils.setProperty(dynaForm, "sourceOfSamples", sourceOfSamples);
		PropertyUtils.setProperty(dynaForm, "blankscreen", "true");
		
		PropertyUtils.setProperty(form, "lastupdated", new Timestamp(System.currentTimeMillis()));
		PropertyUtils.setProperty(form, "personLastupdated", new Timestamp(System.currentTimeMillis()));
		PropertyUtils.setProperty(form, "patientLastupdated", new Timestamp(System.currentTimeMillis()));
		PropertyUtils.setProperty(form, "providerPersonLastupdated", new Timestamp(System.currentTimeMillis()));
		PropertyUtils.setProperty(form, "providerLastupdated", new Timestamp(System.currentTimeMillis()));
		PropertyUtils.setProperty(form, "sampleItemLastupdated", new Timestamp(System.currentTimeMillis()));
		PropertyUtils.setProperty(form, "sampleHumanLastupdated", new Timestamp(System.currentTimeMillis()));
		PropertyUtils.setProperty(form, "sampleOrganizationLastupdated", new Timestamp(System.currentTimeMillis()));
		PropertyUtils.setProperty(form, "sampleProject1Lastupdated", new Timestamp(System.currentTimeMillis()));
		PropertyUtils.setProperty(form, "sampleProject2Lastupdated", new Timestamp(System.currentTimeMillis()));

		
		request.setAttribute(ALLOW_EDITS_KEY, "false");
		//request.setAttribute("menuDefinition", "default");

		return mapping.findForward(FWD_SUCCESS);

	}

	protected String getPageTitleKey() {
		return "human.sample.two.edit.title";
	}

	protected String getPageSubtitleKey() {
		return "human.sample.two.edit.title";
	}
}