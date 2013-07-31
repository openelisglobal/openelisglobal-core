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
import java.util.HashMap;
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
import us.mn.state.health.lims.analyte.valueholder.Analyte;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 * bugzilla 1802: redesign results entry - now all tests are displayed and edited in one page
 *               testAnalyteTestResults is now an array in Test_TestAnalyte rather than a form
 *               variable
 * bugzilla 1798: created 
 */
public class ResultsEntryLinkChildTestToParentTestResultPopupAction extends ResultsEntryBaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String analysisId = (String) request.getParameter("analysisId");
		String accessionNumber = (String)request.getParameter(ACCESSION_NUMBER);
		
		// server side validation of accessionNumber in PreViewAction
		ActionMessages errors = null;


		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		BaseActionForm dynaForm = (BaseActionForm) form;
		
		String[] selectedLinkedParentAnalysisParentResult = null;
		List listOfParentAnalyses = new ArrayList();
		List listOfParentAnalytes = new ArrayList();
		List listOfParentResults = new ArrayList();
		
		if (!StringUtil.isNullorNill(accessionNumber)) {
			Sample sample = new Sample();
			SampleDAO sampleDAO = new SampleDAOImpl();
			sample = sampleDAO.getSampleByAccessionNumber(accessionNumber);
			SampleItem sampleItem = new SampleItem();
			List analyses = new ArrayList();

			try {

				SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
				AnalysisDAO analysisDAO = new AnalysisDAOImpl();
				ResultDAO resultDAO = new ResultDAOImpl();
				DictionaryDAO dictDAO = new DictionaryDAOImpl();

				//get the valueholder for analysis we want to get possible parent analyses for
				Analysis analysis = new Analysis();
				analysis.setId(analysisId);
				analysisDAO.getData(analysis);

				if (!StringUtil.isNullorNill(sample.getId())) {
					sampleItem.setSample(sample);
					sampleItemDAO.getDataBySample(sampleItem);

                    //bugzilla 2227
					analyses = analysisDAO.getMaxRevisionAnalysesBySample(sampleItem);
				}
				if (analyses != null) {

					//bugzilla 2532 for all analyses: these can be potential parents to select from-> get a list of already linked child test ids (not analysis ids) -> don't allow to link same test twice to a parent
					HashMap parentToListOfLinkedTestIdsMap = new HashMap();
					for (int i = 0; i < analyses.size(); i++) {
						Analysis currentAnalysis = (Analysis)analyses.get(i);
						if (currentAnalysis.getParentResult() != null) {
							if (!parentToListOfLinkedTestIdsMap.containsKey(currentAnalysis.getParentResult().getId())) {
								parentToListOfLinkedTestIdsMap.put(currentAnalysis.getParentResult().getId(), new ArrayList());
							}
						}
					}
					//bugzilla 2532 attach list of already linked child test ids to each potential parent result
					for (int i = 0; i < analyses.size(); i++) {
						Analysis currentAnalysis = (Analysis)analyses.get(i);
						if (currentAnalysis.getParentResult() != null) {
							List list = (ArrayList)parentToListOfLinkedTestIdsMap.get(currentAnalysis.getParentResult().getId());
							list.add(currentAnalysis.getTest().getId());
							parentToListOfLinkedTestIdsMap.put(currentAnalysis.getParentAnalysis().getId(), list);
						}
					}
					for (int i = 0; i < analyses.size(); i++) {
						Analysis currentAnalysis = (Analysis)analyses.get(i);
						Analysis parentAnalysisOfCurrentAnalysis = currentAnalysis.getParentAnalysis();
						//IF THIS FOLLOWING LOGIC IS CHANGED - ALSO CHANGE SIMILAR LOGIC IN ResultsEntryViewAction (canTestBeLinkedAsChild())
						//this test cannot be the same as the test to be linked and it cannot be the parent of a test to be linked && test to be linked must have at least one result
						//bugzilla 2532 AND test cannot be linked to a parent result that already has that same test linked to it
						List results = resultDAO.getResultsByAnalysis(currentAnalysis);
						String testIdOfAnalysis = (String)analysis.getTest().getId();
						if ((! currentAnalysis.getId().equals(analysis.getId()) && (parentAnalysisOfCurrentAnalysis == null || !(parentAnalysisOfCurrentAnalysis.getId().equals(analysis.getId())))) && (results != null && results.size() > 0)) {
						    for (int j = 0; j < results.size(); j++) {
						    	Result parentResult = (Result)results.get(j);
								boolean resultIsPossibleParent = false;
						    	if (parentToListOfLinkedTestIdsMap.containsKey(parentResult.getId())) {
						    		List listOfTestIds = (ArrayList)parentToListOfLinkedTestIdsMap.get(parentResult.getId());
						    		if (listOfTestIds != null && listOfTestIds.size() > 0) {
						    			if (!listOfTestIds.contains(testIdOfAnalysis)) {
						    				resultIsPossibleParent = true;
						    			}
						    		} else {
						    			resultIsPossibleParent = true;
						    		}
						    	} else {
						    		resultIsPossibleParent = true;
						    	}
						    	if (resultIsPossibleParent) {
						    		Analyte parentAnalyte = parentResult.getAnalyte();
									listOfParentAnalyses.add(currentAnalysis);
									listOfParentAnalytes.add(parentAnalyte);
									if (parentResult.getResultType().equals(
											SystemConfiguration.getInstance()
											.getDictionaryType()))
									{
										Dictionary dictionary = new Dictionary();
										//bugzilla 2312
										dictionary.setId(parentResult.getTestResult().getValue());
										dictDAO.getData(dictionary);
										parentResult.setValue(dictionary
												.getDictEntryDisplayValue());

									}
									listOfParentResults.add(parentResult);
						    	}
						    }
						}
					}

					selectedLinkedParentAnalysisParentResult = new String[listOfParentResults.size()];
				}
			} catch (LIMSRuntimeException lre) {
                //bugzilla 2154
			    LogEvent.logError("ResultsEntryLinkChildTestToParentTestResultPopupAction","performAction()",lre.toString());
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

		}
        //initialize the form
		dynaForm.initialize(mapping);
		
        PropertyUtils.setProperty(dynaForm, "childAnalysisId", analysisId);
        PropertyUtils.setProperty(dynaForm, "listOfParentAnalyses", listOfParentAnalyses);	
        PropertyUtils.setProperty(dynaForm, "listOfParentAnalytes", listOfParentAnalytes);
        PropertyUtils.setProperty(dynaForm, "listOfParentResults", listOfParentResults);
		PropertyUtils.setProperty(dynaForm, "selectedLinkedParentAnalysisParentResult",
				selectedLinkedParentAnalysisParentResult);

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "resultsentry.linkChildTestToParentTestResultPopup.title";
		} else {
			return "resultsentry.linkChildTestToParentTestResultPopup.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "resultsentry.linkChildTestToParentTestResultPopup.subtitle";
		} else {
			return "resultsentry.linkChildTestToParentTestResultPopup.subtitle";
		}
	}

}
