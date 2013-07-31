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

import java.sql.Timestamp;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.ActionRedirect;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.analysisqaevent.dao.AnalysisQaEventDAO;
import us.mn.state.health.lims.analysisqaevent.daoimpl.AnalysisQaEventDAOImpl;
import us.mn.state.health.lims.analysisqaevent.valueholder.AnalysisQaEvent;
import us.mn.state.health.lims.analysisqaeventaction.dao.AnalysisQaEventActionDAO;
import us.mn.state.health.lims.analysisqaeventaction.daoimpl.AnalysisQaEventActionDAOImpl;
import us.mn.state.health.lims.analysisqaeventaction.valueholder.AnalysisQaEventAction;
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
import us.mn.state.health.lims.note.valueholder.Note;
import us.mn.state.health.lims.referencetables.valueholder.ReferenceTables;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.result.valueholder.Test_TestAnalyte;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;

/**
 * @author diane benz 
 *         bugzilla 2227 Amended Results
 *         To change this generated comment
 *         edit the template variable "typecomment":
 *         Window>Preferences>Java>Templates. To enable and disable the creation
 *         of type comments go to Window>Preferences>Java>Code Generation.
 */
public class AmendedResultsEntryUpdateAction extends ResultsEntryBaseAction {

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {



		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");
		String amendedAnalysisId = null;
		

    	if (request.getParameter(ANALYSIS_ID) != null) {
			amendedAnalysisId = (String) request.getParameter(ANALYSIS_ID);
	      }
		
    	BaseActionForm dynaForm = (BaseActionForm) form;
		
		List testTestAnalytes = (List) dynaForm.get("testTestAnalytes");
		String accessionNumber = (String) dynaForm.get("accessionNumber");
		Timestamp sampleLastupdated = (Timestamp) dynaForm
						.get("sampleLastupdated");

		ActionMessages errors = null;

		// initialize the form
		dynaForm.initialize(mapping);

		// 1926 get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());
		
		org.hibernate.Transaction tx = HibernateUtil.getSession()
				.beginTransaction();

		if (!StringUtil.isNullorNill(amendedAnalysisId)) {

			ResultDAO resultDAO = new ResultDAOImpl();
			AnalysisDAO analysisDAO = new AnalysisDAOImpl();
			NoteDAO noteDAO = new NoteDAOImpl();
			SampleDAO sampleDAO = new SampleDAOImpl();
			AnalysisQaEventDAO analysisQaEventDAO = new AnalysisQaEventDAOImpl();
			AnalysisQaEventActionDAO analysisQaEventActionDAO = new AnalysisQaEventActionDAOImpl();

			try {

				// get old analysis and copy to new one
				Analysis analysisToAmend = new Analysis();
				analysisToAmend.setId(amendedAnalysisId);
				analysisDAO.getData(analysisToAmend);
				Analysis amendedAnalysis = new Analysis();
				
				PropertyUtils.copyProperties(amendedAnalysis, analysisToAmend);
				amendedAnalysis.setId(null);
				amendedAnalysis.setLastupdated(null);
				amendedAnalysis.setSysUserId(sysUserId);
				
				String revisionString = analysisToAmend.getRevision();
				int revision = 0;
				if (!StringUtil.isNullorNill(revisionString)) {
					try {
						revision =Integer.parseInt(revisionString);
					} catch (NumberFormatException nfe) {
						//bugzilla 2154
			            LogEvent.logError("AmendedResultsEntryUpdateAction","performAction()",nfe.toString());
					}
				}
				amendedAnalysis.setRevision(String.valueOf(++revision));
				amendedAnalysis.setStatus(SystemConfiguration.getInstance().getAnalysisStatusResultCompleted());
				amendedAnalysis.setPrintedDate(null);
				//bugzilla 2013 added duplicateCheck parameter
				analysisDAO.insertData(amendedAnalysis, false);
				
				
				//find qa events attached to analysisToAmend and clone them to the amenedAnalysis
				AnalysisQaEvent analysisQaEventToAmend = new AnalysisQaEvent();
				analysisQaEventToAmend.setAnalysis(analysisToAmend);
				List qaEvents = analysisQaEventDAO.getAnalysisQaEventsByAnalysis(analysisQaEventToAmend);
				if (qaEvents != null && qaEvents.size() > 0) {
				  for (int y = 0; y < qaEvents.size(); y++) {
					analysisQaEventToAmend = (AnalysisQaEvent)qaEvents.get(y);
					AnalysisQaEvent amendedAnalysisQaEvent = new AnalysisQaEvent();
								
					AnalysisQaEventAction analysisQaEventActionToAmend = new AnalysisQaEventAction();
					analysisQaEventActionToAmend.setAnalysisQaEvent(analysisQaEventToAmend);
					List qaEventActionsToAmend = analysisQaEventActionDAO.getAnalysisQaEventActionsByAnalysisQaEvent(analysisQaEventActionToAmend);
					PropertyUtils.copyProperties(amendedAnalysisQaEvent, analysisQaEventToAmend);
								
					amendedAnalysisQaEvent.setId(null);
					amendedAnalysisQaEvent.setAnalysis(amendedAnalysis);
					amendedAnalysisQaEvent.setLastupdated(null);
					amendedAnalysisQaEvent.setSysUserId(sysUserId);
					analysisQaEventDAO.insertData(amendedAnalysisQaEvent);
								
					//get actions also
					if (qaEventActionsToAmend != null && qaEventActionsToAmend.size() > 0) {
					  for (int z = 0; z < qaEventActionsToAmend.size(); z++) {
					    analysisQaEventActionToAmend = (AnalysisQaEventAction)qaEventActionsToAmend.get(z);
						AnalysisQaEventAction amendedAnalysisQaEventAction = new AnalysisQaEventAction();
						PropertyUtils.copyProperties(amendedAnalysisQaEventAction, analysisQaEventActionToAmend);

						amendedAnalysisQaEventAction.setId(null);
						amendedAnalysisQaEventAction.setAnalysisQaEvent(amendedAnalysisQaEvent);
						amendedAnalysisQaEventAction.setLastupdated(null);
						amendedAnalysisQaEventAction.setSysUserId(sysUserId);
						analysisQaEventActionDAO.insertData(amendedAnalysisQaEventAction);
					  }
		 		    }
		 		}
		       }


				
				//create copies of results
				List resultsToAmend = resultDAO.getResultsByAnalysis(analysisToAmend);
				
				for (int i = 0; i < resultsToAmend.size(); i++) {
					Result resultToAmend = (Result)resultsToAmend.get(i);
					Result amendedResult = new Result();
					
					PropertyUtils.copyProperties(amendedResult, resultToAmend);
					amendedResult.setId(null);
					amendedResult.setAnalysis(amendedAnalysis);
					amendedResult.setLastupdated(null);
					amendedResult.setSysUserId(sysUserId);
					resultDAO.insertData(amendedResult);
					
				   //find other analyses on this sample that have this result/analysis as a parent and unlink/link to this one
					List childAnalyses = analysisDAO.getAllChildAnalysesByResult(resultToAmend);
					for (int j = 0; j < childAnalyses.size(); j++) {
						Analysis childAnalysis = (Analysis)childAnalyses.get(j);
						childAnalysis.setParentAnalysis(amendedAnalysis);
						childAnalysis.setParentResult(amendedResult);
						childAnalysis.setSysUserId(sysUserId);
						
						//this is for optimistic locking - we need the correct lastupdated 
						for (int x = 0; x < testTestAnalytes.size(); x++) {
							Test_TestAnalyte test_testAnalyte = (Test_TestAnalyte) testTestAnalytes
									.get(x);
							Analysis analysis = test_testAnalyte.getAnalysis();
							
							if (analysis.getId().equals(childAnalysis.getId())) {
								childAnalysis.setLastupdated(analysis.getLastupdated());
								break;
							}
							
						}

						analysisDAO.updateData(childAnalysis);
					}
					
					//create copies of notes (attached to new results)
			        Note note = new Note();
	        		//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
					ReferenceTables referenceTables = new ReferenceTables();
					referenceTables.setId(SystemConfiguration.getInstance().getResultReferenceTableId());
					note.setReferenceTables(referenceTables);
					note.setReferenceId(resultToAmend.getId());
					List notesByResult = noteDAO.getAllNotesByRefIdRefTable(note);

	                for (int x = 0 ; x < notesByResult.size(); x++) {
	                	Note noteToAmend = (Note)notesByResult.get(x);
	                	Note amendedNote = new Note();
	                	PropertyUtils.copyProperties(amendedNote, noteToAmend);
	                	amendedNote.setId(null);
	                	amendedNote.setReferenceId(amendedResult.getId());
	                	amendedNote.setLastupdated(null);
	                	//per Nancy retain user id of original author of note
	                	// Also!!!! Note needs to have systemUser (for Note mapping) AND sysUserId (for audit trail) set!!!!
	                	amendedNote.setSystemUser(noteToAmend.getSystemUser());
						amendedNote.setSysUserId(sysUserId);
                        noteDAO.insertData(amendedNote);
	                }
				   
				}
				
				//update sample status
				Sample sample = sampleDAO.getSampleByAccessionNumber(accessionNumber);
				//this is for optimistic locking...(sampleLastupdated was stored in form from initial read)
				sample.setLastupdated(sampleLastupdated);
				sample.setSysUserId(sysUserId);
				sample.setStatus(SystemConfiguration.getInstance().getSampleStatusEntry2Complete());
				sampleDAO.updateData(sample);


				tx.commit();
			} catch (LIMSRuntimeException lre) {
    			//bugzilla 2154
                LogEvent.logError("AmendedResultsEntryUpdateAction","performAction()",lre.toString());	            
				tx.rollback();

				errors = new ActionMessages();
				ActionError error = null;
				if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {

					error = new ActionError("errors.OptimisticLockException",
							null, null);
				} else {
					error = new ActionError("errors.InsertException", null,
							null);
				}
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
				saveErrors(request, errors);
				request.setAttribute(Globals.ERROR_KEY, errors);

				forward = FWD_FAIL;

			} finally {
				HibernateUtil.closeSession();
			}

		} else {
			forward = FWD_FAIL;
		}

		return getForward(mapping.findForward(forward), accessionNumber);
	
	}

	protected String getPageTitleKey() {
          return null;
	}

	protected String getPageSubtitleKey() {
          return null;
	}
	
	protected ActionForward getForward(ActionForward forward, String accessionNumber) {
		ActionRedirect redirect = new ActionRedirect(forward);
		if (!StringUtil.isNullorNill(accessionNumber))
			redirect.addParameter(ACCESSION_NUMBER, accessionNumber);
	
		return redirect;

	}

}
