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
package us.mn.state.health.lims.common.provider.autocomplete;

/**
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import us.mn.state.health.lims.analyte.dao.AnalyteDAO;
import us.mn.state.health.lims.analyte.daoimpl.AnalyteDAOImpl;

public class AnalyteAutocompleteProvider extends BaseAutocompleteProvider {

	/**
	 * @see org.ajaxtags.demo.servlet.BaseAjaxServlet#getXmlContent(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public List processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		//System.out.println("I am in AnalyteAutocompleteProvider "
				//+ request.getParameter("parentAnalyteName"));
		String analyteName = request.getParameter("parentAnalyteName");
		//System.out
				//.println("AnalyteAutocompleteProvider analyteName "
						//+ analyteName);
		AnalyteDAO analyteDAO = new AnalyteDAOImpl();
		List list = analyteDAO.getAnalytes(analyteName);
		//System.out.println("AnalyteAutocompleteProvider list "
				//+ list.size());

		return list;
	}

}
