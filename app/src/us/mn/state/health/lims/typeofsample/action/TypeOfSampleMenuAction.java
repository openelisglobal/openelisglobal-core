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
package us.mn.state.health.lims.typeofsample.action;

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
import us.mn.state.health.lims.sampledomain.dao.SampleDomainDAO;
import us.mn.state.health.lims.sampledomain.daoimpl.SampleDomainDAOImpl;
import us.mn.state.health.lims.sampledomain.valueholder.SampleDomain;
import us.mn.state.health.lims.typeofsample.dao.TypeOfSampleDAO;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleDAOImpl;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class TypeOfSampleMenuAction extends BaseMenuAction {

	protected List createMenuList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		//System.out.println("I am in TypeOfSampleMenuAction createMenuList()");

		List typeOfSamples = new ArrayList();

		String stringStartingRecNo = (String) request
				.getAttribute("startingRecNo");
		int startingRecNo = Integer.parseInt(stringStartingRecNo);

		TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
		// typeOfSamples = (Vector)typeOfSampleDAO.getAllTypeOfSamples();
		typeOfSamples = typeOfSampleDAO.getPageOfTypeOfSamples(startingRecNo);

		SampleDomainDAO sampleDomainDAO = new SampleDomainDAOImpl();
		List sampleDomains = sampleDomainDAO.getAllSampleDomains();
		EnumValue ev = new EnumValueImpl();
		ev.setEnumName("SampleDomain");
		for (int i = 0; i < sampleDomains.size(); i++) {
			SampleDomain sampleDomain = (SampleDomain) sampleDomains.get(i);
			ev.putValue(sampleDomain.getId(), sampleDomain);
		}

		HttpSession session = request.getSession();
		session.setAttribute("SampleDomain", ev);

		request.setAttribute("menuDefinition", "TypeOfSampleMenuDefinition");

		// bugzilla 1411 set pagination variables 
		request.setAttribute(MENU_TOTAL_RECORDS, String.valueOf(typeOfSampleDAO
				.getTotalTypeOfSampleCount()));
		request.setAttribute(MENU_FROM_RECORD, String.valueOf(startingRecNo));
		int numOfRecs = 0;
		if (typeOfSamples != null) {
			if (typeOfSamples.size() > SystemConfiguration.getInstance()
					.getDefaultPageSize()) {
				numOfRecs = SystemConfiguration.getInstance()
						.getDefaultPageSize();
			} else {
				numOfRecs = typeOfSamples.size();
			}
			numOfRecs--;
		}
		int endingRecNo = startingRecNo + numOfRecs;
		request.setAttribute(MENU_TO_RECORD, String.valueOf(endingRecNo));
		//end bugzilla 1411
		
		return typeOfSamples;
	}

	protected String getPageTitleKey() {
		return "typeofsample.browse.title";
	}

	protected String getPageSubtitleKey() {
		return "typeofsample.browse.title";
	}

	protected int getPageSize() {
		return SystemConfiguration.getInstance().getDefaultPageSize();
	}

	protected String getDeactivateDisabled() {
		return "true";
	}
}
