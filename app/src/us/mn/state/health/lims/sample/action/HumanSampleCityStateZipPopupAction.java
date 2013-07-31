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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.citystatezip.dao.CityStateZipDAO;
import us.mn.state.health.lims.citystatezip.daoimpl.CityStateZipDAOImpl;
import us.mn.state.health.lims.citystatezip.valueholder.CityStateZip;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;

/**
 * @author diane benz
 * 
 * bugzilla 1765
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class HumanSampleCityStateZipPopupAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		String city = request.getParameter("city");
		String state = request.getParameter("state");
		//bugzilla 1895 changed request parameter to zipCode for consistancy
		String zip = request.getParameter("zipCode");
		
		BaseActionForm dynaForm = (BaseActionForm) form;
		
		// initialize the form
		dynaForm.initialize(mapping);

		CityStateZipDAO cityStateZipDAO = new CityStateZipDAOImpl();
		CityStateZip cityStateZip = new CityStateZip();
		cityStateZip.setState(state);
		cityStateZip.setCity(city);
		cityStateZip.setZipCode(zip);
		List validCombos = cityStateZipDAO.getValidCityStateZipCombosForHumanSampleEntry(cityStateZip);
		String selectedCombo = "0";

		PropertyUtils.setProperty(dynaForm, "validCombos", validCombos);
		PropertyUtils.setProperty(dynaForm, "selectedCombo", selectedCombo);

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		return "humansampleone.cityStateZipPopup.title";
	}

	protected String getPageSubtitleKey() {
		return "humansampleone.cityStateZipPopup.subtitle";
	}

}
