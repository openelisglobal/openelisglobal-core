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
 * Copyright (C) CIRG, University of Washington, Seattle WA.  All Rights Reserved.
 *
 */
package us.mn.state.health.lims.common.provider.query;

import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SampleEntryPossibleResultsForTest extends BaseQueryProvider {

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String testId = request.getParameter("testId");
		String callerIndex = request.getParameter("index");

		StringBuilder xml = new StringBuilder();

		String result = createSearchResultXML(testId, callerIndex, xml);

		ajaxServlet.sendData(xml.toString(), result, request, response);

	}

	private String createSearchResultXML(String testId, String callerIndex, StringBuilder xml) {

		String success = VALID;

		TestResultDAO testResultDAO = new TestResultDAOImpl();
		List<TestResult> testResultList = testResultDAO.getActiveTestResultsByTest( testId );

		if (testResultList.isEmpty() || 
			"N".equals(testResultList.get(0).getTestResultType()) || 
			"A".equals(testResultList.get(0).getTestResultType())) 
		{	
			xml.append("<resultType value='N' />");
		} else if( "R".equals(testResultList.get(0).getTestResultType())){
			xml.append("<resultType value='R' />");
		}else {
			DictionaryDAO dictionaryDAO = new DictionaryDAOImpl();
			List<Dictionary> dictionaryList = new ArrayList<Dictionary>();
			for (TestResult testResult : testResultList) {
				dictionaryList.add(dictionaryDAO.getDictionaryById(testResult.getValue()));
			}

			Collections.sort(dictionaryList, new Comparator<Dictionary>() {
				@Override
				public int compare(Dictionary t1, Dictionary t2) {
					return t1.getDictEntry().compareTo(t2.getDictEntry());
				}
		    	});

			xml.append("<resultType value='D' />");
			xml.append("<values>");
			for (Dictionary dictionary : dictionaryList) {
				addDictionaryValueToXml(dictionary, xml);
			}
			xml.append("</values>");
		}

		xml.append("<callerIndex  value='");
		xml.append(callerIndex);
		xml.append("' />");

		return success;
	}

	private void addDictionaryValueToXml(Dictionary dictionary, StringBuilder xml) {
		xml.append("<value");
		xml.append(" id='");
		xml.append(dictionary.getId());
		xml.append("' name='");
		xml.append(dictionary.getDictEntry());
		xml.append("' />");
	}
}
