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
package us.mn.state.health.lims.county.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.common.action.BaseMenuAction;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.valueholder.EnumValue;
import us.mn.state.health.lims.common.valueholder.EnumValueImpl;
import us.mn.state.health.lims.county.dao.CountyDAO;
import us.mn.state.health.lims.county.daoimpl.CountyDAOImpl;
import us.mn.state.health.lims.region.dao.RegionDAO;
import us.mn.state.health.lims.region.daoimpl.RegionDAOImpl;
import us.mn.state.health.lims.region.valueholder.Region;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class CountyMenuAction extends BaseMenuAction {

	protected List createMenuList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		//System.out.println("I am in CountyMenuAction createMenuList()");

		List countys = new ArrayList();

		String stringStartingRecNo = (String) request
				.getAttribute("startingRecNo");
		int startingRecNo = Integer.parseInt(stringStartingRecNo);

		CountyDAO countyDAO = new CountyDAOImpl();
		// countys = (Vector)countyDAO.getAllCountys();
		countys = countyDAO.getPageOfCountys(startingRecNo);

		RegionDAO regionDAO = new RegionDAOImpl();
		List regions = regionDAO.getAllRegions();
		EnumValue ev = new EnumValueImpl();
		ev.setEnumName("Region");
		for (int i = 0; i < regions.size(); i++) {
			Region reg = (Region) regions.get(i);
			ev.putValue(reg.getId(), reg);
		}

		HttpSession session = request.getSession();
		session.setAttribute("Region", ev);

		request.setAttribute("menuDefinition", "CountyMenuDefinition");

		// bugzilla 1411 set pagination variables 
		request.setAttribute(MENU_TOTAL_RECORDS, String.valueOf(countyDAO
				.getTotalCountyCount()));
		request.setAttribute(MENU_FROM_RECORD, String.valueOf(startingRecNo));
		int numOfRecs = 0;
		if (countys != null) {
			if (countys.size() > SystemConfiguration.getInstance()
					.getDefaultPageSize()) {
				numOfRecs = SystemConfiguration.getInstance()
						.getDefaultPageSize();
			} else {
				numOfRecs = countys.size();
			}
			numOfRecs--;
		}
		int endingRecNo = startingRecNo + numOfRecs;
		request.setAttribute(MENU_TO_RECORD, String.valueOf(endingRecNo));
		//end bugzilla 1411
		
		return countys;
	}

	protected String getPageTitleKey() {
		return "county.browse.title";
	}

	protected String getPageSubtitleKey() {
		return "county.browse.title";
	}

	protected int getPageSize() {
		return SystemConfiguration.getInstance().getDefaultPageSize();
	}

	protected String getDeactivateDisabled() {
		return "true";
	}

}
