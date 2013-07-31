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
 *
 * Contributor(s): CIRG, University of Washington, Seattle WA.
 */
package us.mn.state.health.lims.common.provider.query;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.servlet.validation.AjaxServlet;
import us.mn.state.health.lims.common.util.XMLUtil;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.testreflex.action.util.TestReflexUtil;
import us.mn.state.health.lims.testreflex.dao.TestReflexDAO;
import us.mn.state.health.lims.testreflex.daoimpl.TestReflexDAOImpl;
import us.mn.state.health.lims.testreflex.valueholder.TestReflex;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;

public class TestReflexUserChoiceProvider extends BaseQueryProvider {

	private static final String ID_SEPERATOR = ",";
	protected AjaxServlet ajaxServlet = null;

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String resultIds = request.getParameter("resultIds");
		String analysisIds = request.getParameter("analysisIds");
		String testIds = request.getParameter("testIds");
		String rowIndex = request.getParameter("rowIndex");
		String accessionNumber = request.getParameter("accessionNumber");

		StringBuilder xml = new StringBuilder();

		String result = VALID;

		if (GenericValidator.isBlankOrNull(resultIds) || 
				GenericValidator.isBlankOrNull(testIds) || 
				GenericValidator.isBlankOrNull(rowIndex) ||
				(GenericValidator.isBlankOrNull(analysisIds) && GenericValidator.isBlankOrNull(accessionNumber))) {
			result = INVALID;
			xml.append("Internal error, please contact Admin and file bug report");
		} else {
			result = createTestReflexXML(resultIds, analysisIds, testIds, accessionNumber, rowIndex, xml);
		}

		ajaxServlet.sendData(xml.toString(), result, request, response);

	}

	private String createTestReflexXML(String resultIds, String analysisIds, String testIds, String accessionNumber, String rowIndex, StringBuilder xml) {
		TestReflexUtil reflexUtil = new TestReflexUtil();
		String[] resultIdSeries = resultIds.split(ID_SEPERATOR);

		/*
		 * Here's the deal. If the UC test reflex has both an add_test_id and a
		 * scriptlet_id then we are done. If it has only one Then we need to
		 * look for the other
		 */
		TestReflex testReflexOne = null;
		TestReflex testReflexTwo = null;
		// Both test given results on client
		if (resultIdSeries.length > 1) {
			String[] testIdSeries = testIds.split(ID_SEPERATOR);

			List<TestReflex> testReflexesForResultOne = reflexUtil.getTestReflexsForDictioanryResultTestId(resultIdSeries[0],
					testIdSeries[0], true);

			if (!testReflexesForResultOne.isEmpty()) {
				List<TestReflex> sibTestReflexList = reflexUtil.getTestReflexsForDictioanryResultTestId(resultIdSeries[1],
						testIdSeries[1], true);

				boolean allChoicesFound = false;
				for (TestReflex reflexFromResultOne : testReflexesForResultOne) {
					for (TestReflex sibReflex : sibTestReflexList) {
						if (areSibs(reflexFromResultOne, sibReflex)
								&& TestReflexUtil.USER_CHOOSE_FLAG.equals(reflexFromResultOne.getFlags())) {
							if (reflexFromResultOne.getActionScriptlet() != null) {
								testReflexOne = reflexFromResultOne;
								allChoicesFound = true;
								break;
							} else if (testReflexOne == null) {
								testReflexOne = reflexFromResultOne;
							} else {
								testReflexTwo = reflexFromResultOne;
								allChoicesFound = true;
								break;
							}
						}
						if (allChoicesFound) {
							break;
						}
					}
				}
			}
			// One test given results on client, the other is in the DB
		} else {
			// for each reflex we are going to try and find a sibling reflex
			// which is currently satisfied
			// get their common sample
			AnalysisDAO analysisDAO = new AnalysisDAOImpl();
			TestReflexDAO testReflexDAO = new TestReflexDAOImpl();
			TestResultDAO testResultDAO = new TestResultDAOImpl();
			ResultDAO resultDAO = new ResultDAOImpl();

			Sample sample = getSampleForKnownTest(analysisIds, accessionNumber, analysisDAO);
			
			List<Analysis> analysisList = analysisDAO.getAnalysesBySampleId(sample.getId());

			List<TestReflex> possibleTestReflexList = reflexUtil.getTestReflexsForDictioanryResultTestId(resultIds, testIds, true);

			for (TestReflex possibleTestReflex : possibleTestReflexList) {
				if (TestReflexUtil.USER_CHOOSE_FLAG.equals(possibleTestReflex.getFlags())) {
					if (GenericValidator.isBlankOrNull(possibleTestReflex.getSiblingReflexId())) {
						if (possibleTestReflex.getActionScriptlet() != null) {
							testReflexOne = possibleTestReflex;
							break;
						} else if (testReflexOne == null) {
							testReflexOne = possibleTestReflex;
						} else {
							testReflexTwo = possibleTestReflex;
							break;
						}
					} else {
						// find if the sibling reflex is satisfied
						TestReflex sibTestReflex = new TestReflex();
						sibTestReflex.setId(possibleTestReflex.getSiblingReflexId());

						testReflexDAO.getData(sibTestReflex);

						TestResult sibTestResult = new TestResult();
						sibTestResult.setId(sibTestReflex.getTestResultId());
						testResultDAO.getData(sibTestResult);

						for (Analysis analysis : analysisList) {
							List<Result> resultList = resultDAO.getResultsByAnalysis(analysis);
							Test test = analysis.getTest();

							for (Result result : resultList) {
								TestResult testResult = testResultDAO.getTestResultsByTestAndDictonaryResult(test.getId(),
										result.getValue());
								if (testResult != null && testResult.getId().equals(sibTestReflex.getTestResultId())) {	
									if (possibleTestReflex.getActionScriptlet() != null) {
										testReflexOne = possibleTestReflex;
										break;
									} else if (testReflexOne == null) {
										testReflexOne = possibleTestReflex;
									} else {
										testReflexTwo = possibleTestReflex;
										break;
									}
								}
							}
						}
					}
				}
			}
		}

		if (testReflexOne != null) {
			xml.append("<userchoice>");
			XMLUtil.appendKeyValue("rowIndex", rowIndex, xml);
			createChoiceElement(testReflexOne, testReflexTwo, xml);
			xml.append("</userchoice>");

			return VALID;
		}
		
		xml.append("<userchoice/>");
		return INVALID;
	}

	private Sample getSampleForKnownTest(String analysisIds, String accessionNumber, AnalysisDAO analysisDAO) {
		//We use the analysisId for logbook results and accessionNumber for analysis results, we should accessionNumber for both.
		if( GenericValidator.isBlankOrNull(analysisIds)){
			return new SampleDAOImpl().getSampleByAccessionNumber(accessionNumber);
		}else{
			Analysis knownAnalysis = new Analysis();
			knownAnalysis.setId(analysisIds);
			analysisDAO.getData(knownAnalysis);

			return knownAnalysis.getSampleItem().getSample();
		}
	}

	private boolean areSibs(TestReflex testReflex, TestReflex sibTestReflex) {
		return !GenericValidator.isBlankOrNull(testReflex.getSiblingReflexId())
				&& !GenericValidator.isBlankOrNull(sibTestReflex.getSiblingReflexId())
				&& testReflex.getSiblingReflexId().equals(sibTestReflex.getId())
				&& sibTestReflex.getSiblingReflexId().equals(testReflex.getId());

	}

	private void createChoiceElement(TestReflex testReflexOne, TestReflex testReflexTwo, StringBuilder xml) {
		if (testReflexTwo == null) { //one has both test and script
			XMLUtil.appendKeyValue("selectionOneText", TestReflexUtil.makeReflexScriptName(testReflexOne), xml);
			XMLUtil.appendKeyValue("selectionOneId", TestReflexUtil.makeReflexScriptValue(testReflexOne), xml);
			XMLUtil.appendKeyValue("selectionTwoText", TestReflexUtil.makeReflexTestName(testReflexOne), xml);
			XMLUtil.appendKeyValue("selectionTwoId", TestReflexUtil.makeReflexTestValue(testReflexOne), xml);
		}else if(testReflexOne.getActionScriptlet() == null && testReflexTwo.getActionScriptlet() == null){ //both tests
			XMLUtil.appendKeyValue("selectionOneText", TestReflexUtil.makeReflexTestName(testReflexTwo), xml);
			XMLUtil.appendKeyValue("selectionOneId", TestReflexUtil.makeReflexTestValue(testReflexTwo), xml);
			XMLUtil.appendKeyValue("selectionTwoText", TestReflexUtil.makeReflexTestName(testReflexOne), xml);
			XMLUtil.appendKeyValue("selectionTwoId", TestReflexUtil.makeReflexTestValue(testReflexOne), xml);
		}else if(testReflexOne.getAddedTest() == null && testReflexTwo.getAddedTest() == null ){ //both scripts
			XMLUtil.appendKeyValue("selectionOneText", TestReflexUtil.makeReflexScriptName(testReflexOne), xml);
			XMLUtil.appendKeyValue("selectionOneId", TestReflexUtil.makeReflexScriptValue(testReflexOne), xml);
			XMLUtil.appendKeyValue("selectionTwoText", TestReflexUtil.makeReflexScriptName(testReflexTwo), xml);
			XMLUtil.appendKeyValue("selectionTwoId", TestReflexUtil.makeReflexScriptValue(testReflexTwo), xml);
		}else if( testReflexOne.getAddedTest() == null ){ //these two are redundant if db has been set up correctly
			XMLUtil.appendKeyValue("selectionOneText", TestReflexUtil.makeReflexScriptName(testReflexOne), xml);
			XMLUtil.appendKeyValue("selectionOneId", TestReflexUtil.makeReflexScriptValue(testReflexOne), xml);
			XMLUtil.appendKeyValue("selectionTwoText", TestReflexUtil.makeReflexTestName(testReflexTwo), xml);
			XMLUtil.appendKeyValue("selectionTwoId", TestReflexUtil.makeReflexTestValue(testReflexTwo), xml);
		}else{
			XMLUtil.appendKeyValue("selectionOneText", TestReflexUtil.makeReflexScriptName(testReflexTwo), xml);
			XMLUtil.appendKeyValue("selectionOneId", TestReflexUtil.makeReflexScriptValue(testReflexTwo), xml);
			XMLUtil.appendKeyValue("selectionTwoText", TestReflexUtil.makeReflexTestName(testReflexOne), xml);
			XMLUtil.appendKeyValue("selectionTwoId", TestReflexUtil.makeReflexTestValue(testReflexOne), xml);			
		}
	}

	@Override
	public void setServlet(AjaxServlet as) {
		this.ajaxServlet = as;
	}

	@Override
	public AjaxServlet getServlet() {
		return this.ajaxServlet;
	}

}
