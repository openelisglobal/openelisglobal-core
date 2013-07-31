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
package us.mn.state.health.lims.result.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.result.valueholder.TestAnalyte_TestResults;
import us.mn.state.health.lims.result.valueholder.Test_TestAnalyte;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.testanalyte.dao.TestAnalyteDAO;
import us.mn.state.health.lims.testanalyte.daoimpl.TestAnalyteDAOImpl;
import us.mn.state.health.lims.testanalyte.valueholder.TestAnalyte;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;

/**
 * @author diane benz
 * bugzilla 2227
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation. 
 * bugzilla 1802 modified to edit results on main page (remove test edit buttons)
 */
public class ResultsEntryHistoryPopupAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		BaseActionForm dynaForm = (BaseActionForm) form;

		String accessionNumber = null;
		String analysisId = null;

		//we may to through here at the SAMPLE level OR at the ANALYSIS level
		if (request.getParameter(ANALYSIS_ID) != null) {
		  analysisId = (String) request.getParameter(ANALYSIS_ID);
		}
		
		if (request.getParameter(ACCESSION_NUMBER) != null) {
		 accessionNumber = (String) request.getParameter(ACCESSION_NUMBER);
	    }


		ActionMessages errors = new ActionMessages();

		// initialize the form
		dynaForm.initialize(mapping);
		
		List testTestAnalytes = new ArrayList();

		if (!StringUtil.isNullorNill(analysisId) || !StringUtil.isNullorNill(accessionNumber)) {
			try {
				
				List listOfRevisions = new ArrayList();
				
				AnalysisDAO analysisDAO = new AnalysisDAOImpl();
				SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
				ResultDAO resultDAO = new ResultDAOImpl();
				TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();
				TestResultDAO testResultDAO = new TestResultDAOImpl();
				DictionaryDAO dictDAO = new DictionaryDAOImpl();

				
				if (!StringUtil.isNullorNill(analysisId)) {
					Analysis analysis = new Analysis();
					analysis.setId(analysisId);
					analysisDAO.getData(analysis);

					Test test = analysis.getTest();

					SampleItem sampleItem = new SampleItem();
					sampleItem.setId(analysis.getSampleItem().getId());
					sampleItemDAO.getData(sampleItem);
					if (sampleItem != null) {
						listOfRevisions = analysisDAO.getRevisionHistoryOfAnalysesBySampleAndTest(sampleItem, test, false);
					}
				} else if (!StringUtil.isNullorNill(accessionNumber)) {
                    Sample sample = new Sample();
                    SampleDAO sampleDAO = new SampleDAOImpl();
                    sample.setAccessionNumber(accessionNumber);
                    sampleDAO.getSampleByAccessionNumber(sample);
					SampleItem sampleItem = new SampleItem();
    				sampleItem.setSample(sample);
    				sampleItemDAO.getDataBySample(sampleItem);
                    listOfRevisions = analysisDAO.getRevisionHistoryOfAnalysesBySample(sampleItem);
				}
				if (listOfRevisions != null) {
					for (int i = 0; i < listOfRevisions.size(); i++) {
						Analysis revision = (Analysis) listOfRevisions.get(i);
						Test t = (Test) revision.getTest();
						Test_TestAnalyte tta = new Test_TestAnalyte();
						tta.setTest(t);
						tta.setAnalysis(revision);
						List tAs = new ArrayList();
						tAs = testAnalyteDAO.getAllTestAnalytesPerTest(t);
						TestAnalyte_TestResults[] taTrs = new TestAnalyte_TestResults[tAs.size()];
						Result[] results = new Result[tAs.size()];
						String resultId;
						String resultValue = null;
						String selectedTestResultId = null;
						for (int j = 0; j < tAs.size(); j++) {
							TestAnalyte_TestResults taTr = new TestAnalyte_TestResults();
							TestAnalyte ta = (TestAnalyte) tAs.get(j);
							taTr.setTestAnalyte(ta);

							Result result = new Result();
							resultDAO.getResultByAnalysisAndAnalyte(result,
									revision, ta);
							resultValue = null;
							selectedTestResultId = null;

							if (result != null) {
								if (result.getId() != null) {
									// fill in dictionary values
									if (result.getResultType().equals(
											SystemConfiguration
											.getInstance()
											.getDictionaryType())) {
										// get from dictionary
										Dictionary dictionary = new Dictionary();
								        //bugzilla 2312
										dictionary.setId(result.getTestResult().getValue());
										dictDAO.getData(dictionary);
										result
										.setValue(dictionary
												.getDictEntryDisplayValue());

									}if (result.getResultType().equals(
											SystemConfiguration
											.getInstance()
											.getTiterType())){

										resultValue = result.getValue();
										resultValue = resultValue.substring(2,resultValue.length());
										result.setValue(resultValue);
									}else{
										resultValue = result.getValue();	

									}
									results[j] = result;
									TestResult tr = (TestResult) result
									.getTestResult();
									selectedTestResultId = tr.getId();
								} else {
									results[j] = new Result();
								}
							} else {
								results[j] = new Result();
							}
							List listOfTestResults = testResultDAO
							.getTestResultsByTestAndResultGroup(ta);
							taTr.setTestResults(listOfTestResults);
							taTr
							.setSelectedTestResultId(selectedTestResultId);
							taTr.setResultId(results[j].getId());
							taTr.setResultValue(resultValue);
							taTrs[j] = taTr;
						}

						tta.setTestAnalytes(tAs);
						tta.setResults(results);
						tta.setTestAnalyteTestResults(taTrs);

						testTestAnalytes.add(tta);

					}

				}

			} catch (LIMSRuntimeException lre) {
				// if error then forward to fail and don't update to blank
				// page
				// = false
                //bugzilla 2154
			    LogEvent.logError("ResultsEntryHistoryPopupAction","performAction()",lre.toString());
				errors = new ActionMessages();
				ActionError error = null;
				error = new ActionError("errors.GetException", null, null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
				saveErrors(request, errors);
				request.setAttribute(Globals.ERROR_KEY, errors);
				request.setAttribute(ALLOW_EDITS_KEY,
						"false");
				return mapping.findForward(FWD_FAIL);

			}
			
			PropertyUtils.setProperty(dynaForm, "historyTestTestAnalytes", testTestAnalytes);

			forward = FWD_SUCCESS;

		}

		return mapping.findForward(forward);
	}


	protected String getPageTitleKeyParameter(HttpServletRequest request,
			ActionForm form) {
		BaseActionForm dynaForm = (BaseActionForm) form;
		String analysisId = "";
		if (request.getParameter(ANALYSIS_ID) != null) {
			analysisId = (String) request.getParameter(ANALYSIS_ID);
		}
		Analysis analysis = new Analysis();
		analysis.setId(analysisId);
		AnalysisDAO analysisDAO = new AnalysisDAOImpl();
		analysisDAO.getData(analysis);
		
		return analysis.getTest().getTestDisplayValue();
	}
	
	protected String getPageSubtitleKeyParameter(HttpServletRequest request,
			ActionForm form) {
		BaseActionForm dynaForm = (BaseActionForm) form;
		String analysisId = "";
		if (request.getParameter(ANALYSIS_ID) != null) {
			analysisId = (String) request.getParameter(ANALYSIS_ID);
		}
		Analysis analysis = new Analysis();
		analysis.setId(analysisId);
		AnalysisDAO analysisDAO = new AnalysisDAOImpl();
		analysisDAO.getData(analysis);
		
		return analysis.getTest().getTestDisplayValue();
	}

	protected String getPageTitleKey() {
		return "resultsentry.history.popup.title";
	}

	protected String getPageSubtitleKey() {
		return "resultsentry.history.popup.subtitle";
	}

}
