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
package us.mn.state.health.lims.common.provider.selectdropdown;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.login.dao.UserTestSectionDAO;
import us.mn.state.health.lims.login.daoimpl.UserTestSectionDAOImpl;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.TestComparator;

/**
 * An example servlet that responds to an ajax:autocomplete tag action. This
 * servlet would be referenced by the baseUrl attribute of the JSP tag.
 * <p>
 * This servlet should generate XML in the following format:
 * </p>
 * <code><![CDATA[<?xml version="1.0"?>
 * <list>
 *   <item value="Item1">First Item</item>
 *   <item value="Item2">Second Item</item>
 *   <item value="Item3">Third Item</item>
 * </list>]]></code>
 * 
 * @author Darren L. Spurgeon
 */
public class TestSectionTestSelectDropDownProvider extends
		BaseSelectDropDownProvider {

	/**
	 * @see org.ajaxtags.demo.servlet.BaseAjaxServlet#getXmlContent(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public List processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		//System.out.println("I am in TestSectionTestSelectDropDownProvider ");

		String testSectionId = request.getParameter("selectedTestSectionId");

		List list = new ArrayList();

		TestDAO testDAO = new TestDAOImpl();

		//bgm - bugzilla 1583 adding else check for null testSectionId to then get all test names.
		if (!StringUtil.isNullorNill(testSectionId)) {
			list = testDAO.getTestsByTestSection(testSectionId);
		}else{
			//get all tests by sys user id
			//bugzilla 2160 
			UserTestSectionDAO userTestSectionDAO = new UserTestSectionDAOImpl();
			//bugzilla 2291
			list = userTestSectionDAO.getAllUserTests(request, true);
		}

		//bugzilla 1844
		Collections.sort(list, TestComparator.DESCRIPTION_COMPARATOR);

	
		return list;
	}

}
