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
package us.mn.state.health.lims.common.provider.validation;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.servlet.validation.AjaxServlet;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.organization.dao.OrganizationDAO;
import us.mn.state.health.lims.organization.daoimpl.OrganizationDAOImpl;
import us.mn.state.health.lims.organization.valueholder.Organization;

//bugzilla 2069 renamed from OrganizaitonIdValidationProvider
public class OrganizationLocalAbbreviationValidationProvider extends BaseValidationProvider {

	public OrganizationLocalAbbreviationValidationProvider() {
		super();
	}

	public OrganizationLocalAbbreviationValidationProvider(AjaxServlet ajaxServlet) {
		this.ajaxServlet = ajaxServlet;
	}

	public void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// get id from request
		String targetId = (String) request.getParameter("id");
		String formField = (String) request.getParameter("field");
		//bugzilla 2531
		String form = (String) request.getParameter("form");
		String result = validate(targetId,form);
		//System.out.println("This is field being validated " + formField);
		ajaxServlet.sendData(formField, result, request, response);
	}

	public String validate(String targetId, String form) throws LIMSRuntimeException {

		StringBuffer s = new StringBuffer();

		OrganizationDAO organizationDAO = new OrganizationDAOImpl();

		//bugzilla 1366 modified to get the one organization that is 
		//request instead of looping through all valid ones...
		//bugzilla 2589 check for null or nill
		if ((!StringUtil.isNullorNill(targetId))) {
			Organization organization = new Organization();
			//bugzilla 2069
			organization.setOrganizationLocalAbbreviation(targetId.trim());
									
			organization = organizationDAO.getOrganizationByLocalAbbreviation(organization, true);
			            
			if (organization != null && !StringUtil.isNullorNill(organization.getId())) {
				s.append(VALID);
				// This is particular to organizationLocalAbbreviation validation for HSE1 and HSE2:
				// the message appended to VALID is the organizationName that
				// can then be displayed when User enters valid Organization Id
				// (see humanSampleOne.jsp, humanSampleTwo.jsp)
				String orgName = organization.getOrganizationName();
				s.append(orgName);
				
				//bugzilla 2531
				if ( form != null ) {
					if ( form.equalsIgnoreCase("newbornSampleFullForm") ) {
						String cityName = organization.getCity();
						s.append(", ");
						s.append(cityName);
					}
				}	
			} else {
				s.append(INVALID);
			}
		} else {
			//bugzilla 2589
			s.append(VALID);
		}

		return s.toString();
	}

}
