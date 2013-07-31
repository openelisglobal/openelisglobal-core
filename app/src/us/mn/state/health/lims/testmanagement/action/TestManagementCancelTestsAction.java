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
package us.mn.state.health.lims.testmanagement.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.hibernate.Transaction;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.analysisqaevent.dao.AnalysisQaEventDAO;
import us.mn.state.health.lims.analysisqaevent.daoimpl.AnalysisQaEventDAOImpl;
import us.mn.state.health.lims.analysisqaeventaction.dao.AnalysisQaEventActionDAO;
import us.mn.state.health.lims.analysisqaeventaction.daoimpl.AnalysisQaEventActionDAOImpl;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.valueholder.UserSessionData;
import us.mn.state.health.lims.note.dao.NoteDAO;
import us.mn.state.health.lims.note.daoimpl.NoteDAOImpl;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;

/**
 * @author Diane Benz
 * bugzilla 2300
 */
public class TestManagementCancelTestsAction extends BaseAction {
	
	private static final String HAS_AMENDED_TEST = "hasAmendedTest";
	private static final String INVALID_STATUS_RESULTS_COMPLETE = "invalidStatusResultsComplete";
	private static final String INVALID_STATUS_RESULTS_VERIFIED = "invalidStatusResultsVerified";

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		String selectedTestsString = (String)request.getParameter("selectedTests");

		BaseActionForm dynaForm = (BaseActionForm) form;
		String accessionNumber = (String)dynaForm.get("accessionNumber");

		// initialize the form
		dynaForm.initialize(mapping);

		ActionMessages errors = null;
		Map errorMap = new HashMap();
		errorMap.put(HAS_AMENDED_TEST, new ArrayList());
		errorMap.put(INVALID_STATUS_RESULTS_COMPLETE, new ArrayList());
		errorMap.put(INVALID_STATUS_RESULTS_VERIFIED, new ArrayList());
		
		Transaction tx = HibernateUtil.getSession().beginTransaction();
		try {

			SampleDAO sampleDAO = new SampleDAOImpl();
			SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
			TestDAO testDAO = new TestDAOImpl();
			AnalysisDAO analysisDAO = new AnalysisDAOImpl();
			ResultDAO resultDAO = new ResultDAOImpl();
			NoteDAO noteDAO = new NoteDAOImpl();
			AnalysisQaEventDAO analysisQaEventDAO = new AnalysisQaEventDAOImpl();
			AnalysisQaEventActionDAO analysisQaEventActionDAO = new AnalysisQaEventActionDAOImpl();
			List listOfIds = new ArrayList();
			
			// bugzilla 1926 insert logging - get sysUserId from login module
			UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
			String sysUserId = String.valueOf(usd.getSystemUserId());

			if (!StringUtil.isNullorNill(selectedTestsString)) {

				String idSeparator = SystemConfiguration.getInstance()
				.getDefaultIdSeparator();
				StringTokenizer st = new StringTokenizer(selectedTestsString, idSeparator);
				while (st.hasMoreElements()) {
					String id = (String) st.nextElement();
					listOfIds.add(id);
				}
				//now set analysis status to canceled for these analysis ids

				for (int i = 0; i < listOfIds.size(); i++) {
					String id = (String)listOfIds.get(i);
					//bug 2532 (the ids are now analysis ids - not test ids)
					Analysis analysis = new Analysis();
					analysis.setId(id);
					analysisDAO.getData(analysis);
					if (analysis != null && !StringUtil.isNullorNill(analysis.getId())) {

						if (analysis.getStatus().equals(SystemConfiguration.getInstance().getAnalysisStatusAssigned())) {
							if (!analysis.getRevision().equals("0")) {
								List listOfAmendedTests = (ArrayList)errorMap.get(HAS_AMENDED_TEST);
								listOfAmendedTests.add(analysis);
								errorMap.put(HAS_AMENDED_TEST, listOfAmendedTests);
							}

						} else if (analysis.getStatus().equals(SystemConfiguration.getInstance().getAnalysisStatusResultCompleted())) {
							List listOfCompletedTests = (ArrayList)errorMap.get(INVALID_STATUS_RESULTS_COMPLETE);
							listOfCompletedTests.add(analysis);
							errorMap.put(INVALID_STATUS_RESULTS_COMPLETE, listOfCompletedTests);
							continue;
						} else if (analysis.getStatus().equals(SystemConfiguration.getInstance().getAnalysisStatusReleased())) {
							List listOfVerifiedTests = (ArrayList)errorMap.get(INVALID_STATUS_RESULTS_VERIFIED);
							listOfVerifiedTests.add(analysis);
							errorMap.put(INVALID_STATUS_RESULTS_VERIFIED, listOfVerifiedTests);
							continue;
						}

						analysis.setSysUserId(sysUserId);
						analysis.setStatus(SystemConfiguration.getInstance().getAnalysisStatusCanceled());
						analysisDAO.updateData(analysis);

					}

				}
			}
			

			PropertyUtils.setProperty(dynaForm, ACCESSION_NUMBER,
					accessionNumber);
			tx.commit();
			
			// introducing a confirmation message 
			
			if (errorMap != null && errorMap.size() > 0) { 
				
			  
			  //1) amended message
			  List amendedTests = (ArrayList)errorMap.get(HAS_AMENDED_TEST);
			  List completedTests = (ArrayList)errorMap.get(INVALID_STATUS_RESULTS_COMPLETE);
			  List verifiedTests = (ArrayList)errorMap.get(INVALID_STATUS_RESULTS_VERIFIED);
			  if ((amendedTests != null && amendedTests.size() > 0) || (completedTests != null && completedTests.size() > 0) || (verifiedTests != null && verifiedTests.size() > 0)) {
				  errors = new ActionMessages(); 
				  
				  if (amendedTests != null && amendedTests.size() > 0) {
					  ActionError error = null;
					  if (amendedTests.size() > 1) {
						  StringBuffer stringOfTests = new StringBuffer();
						  for (int i = 0; i < amendedTests.size(); i++) {
							  Analysis analysis = (Analysis)amendedTests.get(i);
							  stringOfTests.append("\\n    " + analysis.getTest().getTestDisplayValue());
						  }
						  error = new ActionError("testsmanagement.message.multiple.test.not.canceled.amended",
								  stringOfTests.toString(), null);
					  } else {
						  Analysis analysis = (Analysis)amendedTests.get(0);
    					  error = new ActionError("testsmanagement.message.one.test.not.canceled.amended",
								"\\n    " + analysis.getTest().getTestDisplayValue(), null);
					  }
					  errors.add(ActionMessages.GLOBAL_MESSAGE, error);

				  }
				  
				  if (completedTests != null && completedTests.size() > 0) {
					  ActionError error = null;
					  if (completedTests.size() > 1) {
						  StringBuffer stringOfTests = new StringBuffer();
						  for (int i = 0; i < completedTests.size(); i++) {
							  Analysis analysis = (Analysis)completedTests.get(i);
							  stringOfTests.append("\\n    " + analysis.getTest().getTestDisplayValue());
						  }
						  error = new ActionError("testsmanagement.message.multiple.test.not.canceled.completed",
								  stringOfTests.toString(), null);
					  } else {
						  Analysis analysis = (Analysis)completedTests.get(0);
    					  error = new ActionError("testsmanagement.message.one.test.not.canceled.completed",
								"\\n    " + analysis.getTest().getTestDisplayValue(), null);
					  }
					  errors.add(ActionMessages.GLOBAL_MESSAGE, error);

				  }
				  
				  if (verifiedTests != null && verifiedTests.size() > 0) {
					  ActionError error = null;
					  if (verifiedTests.size() > 1) {
						  StringBuffer stringOfTests = new StringBuffer();
						  for (int i = 0; i < verifiedTests.size(); i++) {
							  Analysis analysis = (Analysis)verifiedTests.get(i);
							  stringOfTests.append("\\n    " + analysis.getTest().getTestDisplayValue());
						  }
						  error = new ActionError("testsmanagement.message.multiple.test.not.canceled.verified",
								  stringOfTests.toString(), null);
					  } else {
						  Analysis analysis = (Analysis)verifiedTests.get(0);
    					  error = new ActionError("testsmanagement.message.one.test.not.canceled.verified",
								"\\n    " + analysis.getTest().getTestDisplayValue(), null);
					  }
					  errors.add(ActionMessages.GLOBAL_MESSAGE, error);

				  }
				  
				  saveErrors(request, errors);
				  request.setAttribute(Globals.ERROR_KEY, errors);
			  }

			}

		} catch (LIMSRuntimeException lre) {
			//bugzilla 2154
			LogEvent.logError("TestManagementCancelTests","performAction()",lre.toString());
			tx.rollback();
			errors = new ActionMessages();
			ActionError error = null;

			if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
				error = new ActionError("errors.OptimisticLockException", null,
						null);
			} else {

				error = new ActionError("errors.UpdateException", null,
						null);
			}

			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute(ALLOW_EDITS_KEY, "false");
			return mapping.findForward(FWD_FAIL);

		} finally {
			HibernateUtil.closeSession();
		}
		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		return "testmanagement.title";
	}

	protected String getPageSubtitleKey() {
		return "testmanagement.title";
	}

}
