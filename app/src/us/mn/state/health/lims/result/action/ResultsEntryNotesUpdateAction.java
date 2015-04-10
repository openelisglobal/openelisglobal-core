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

import org.apache.struts.Globals;
import org.apache.struts.action.*;
import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSDuplicateRecordException;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.util.resources.ResourceLocator;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.valueholder.UserSessionData;
import us.mn.state.health.lims.note.dao.NoteDAO;
import us.mn.state.health.lims.note.daoimpl.NoteDAOImpl;
import us.mn.state.health.lims.note.valueholder.Note;
import us.mn.state.health.lims.referencetables.dao.ReferenceTablesDAO;
import us.mn.state.health.lims.referencetables.daoimpl.ReferenceTablesDAOImpl;
import us.mn.state.health.lims.referencetables.valueholder.ReferenceTables;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import us.mn.state.health.lims.systemuser.dao.SystemUserDAO;
import us.mn.state.health.lims.systemuser.daoimpl.SystemUserDAOImpl;
import us.mn.state.health.lims.systemuser.valueholder.SystemUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 * bugzilla 2614 - fix to work for NB samples
 */
public class ResultsEntryNotesUpdateAction extends BaseAction {

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");

		BaseActionForm dynaForm = (BaseActionForm) form;
		// server-side validation (validation.xml)
		ActionMessages errors = dynaForm.validate(mapping, request);

        //bugzilla 2614 allow for NB domain samples
		String selectedTestId = "";

		String referenceTableId = SystemConfiguration.getInstance()
				.getResultReferenceTableId();
		String refId = (String) dynaForm.get("noteRefId");

		String noteIds = (String) dynaForm.get("noteIds");
		String noteSubjects = (String) dynaForm.get("noteSubjects");
		String noteTexts = (String) dynaForm.get("noteTexts");
		String noteTypes = (String) dynaForm.get("noteTypes");
		String noteLastupdateds = (String) dynaForm.get("noteLastupdateds");

		List noteIdList = new ArrayList();
		List noteSubjectList = new ArrayList();
		List noteTextList = new ArrayList();
		List noteTypeList = new ArrayList();
		List noteLastupdatedList = new ArrayList();

		String textSeparator = SystemConfiguration.getInstance()
				.getDefaultTextSeparator();

		NoteDAO noteDAO = new NoteDAOImpl();
		SystemUserDAO systemUserDAO = new SystemUserDAOImpl();
		//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
		ReferenceTablesDAO referenceTablesDAO = new ReferenceTablesDAOImpl();

		// get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());
		org.hibernate.Transaction tx = HibernateUtil.getSession()
				.beginTransaction();

		try {

			textSeparator = StringUtil.convertStringToRegEx(textSeparator);

			SystemUser systemUser = new SystemUser();
			systemUser.setId(sysUserId);
			systemUserDAO.getData(systemUser);

			// get all the data required to forward to correct page

			// get analysis id from result
			ResultDAO resultDAO = new ResultDAOImpl();
			Result result = new Result();
			result.setId(refId);
			resultDAO.getData(result);

			String analysisId = result.getAnalysis().getId();

			AnalysisDAO analysisDAO = new AnalysisDAOImpl();
			Analysis analysis = new Analysis();
			analysis.setId(analysisId);
			analysisDAO.getData(analysis);

			// get test id from analysis
			selectedTestId = analysis.getTest().getId();

			// get domain from sample
			SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
			SampleItem sampleItem = new SampleItem();
			sampleItem.setId(analysis.getSampleItem().getId());
			sampleItemDAO.getData(sampleItem);

			SampleDAO sampleDAO = new SampleDAOImpl();
			Sample sample = new Sample();
			//bugzilla 1773 need to store sample not sampleId for use in sorting
			sample.setId(sampleItem.getSample().getId());
			sampleDAO.getData(sample);

            //bugzilla 2614 allow for NB domain samples
			// now that we have the domain (for forwarding to correct fail page)
			// validate note popup form data!
			try {
			    //bugzilla 2254 moved loadListFromStringOfElements to StringUtil
				noteIdList = StringUtil.loadListFromStringOfElements(noteIds,
						textSeparator, false);
				noteLastupdatedList = StringUtil.loadListFromStringOfElements(
						noteLastupdateds, textSeparator, false);
				// these three need to be validated for empty strings
				noteSubjectList = StringUtil.loadListFromStringOfElements(noteSubjects,
						textSeparator, true);
				noteTextList = StringUtil.loadListFromStringOfElements(noteTexts,
						textSeparator, true);
				noteTypeList = StringUtil.loadListFromStringOfElements(noteTypes,
						textSeparator, true);

			} catch (Exception e) {
    			//bugzilla 2154
			    LogEvent.logError("ResultsEntryNotesUpdateAction","performAction()",e.toString());
				String messageKey = "note.note";
				ActionError error = new ActionError("errors.invalid",
						getMessageForKey(messageKey), null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
				saveErrors(request, errors);
				forward = FWD_FAIL;

				return mapping.findForward(forward);
			}

			for (int i = 0; i < noteIdList.size(); i++) {
				Note note = new Note();

				String noteId = (String) noteIdList.get(i);
				note.setReferenceId(refId);
				//bugzilla 1922
				//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
				ReferenceTables referenceTables = new ReferenceTables();
				referenceTables.setId(referenceTableId);
				//bugzilla 2571
				referenceTablesDAO.getData(referenceTables);
				note.setReferenceTables(referenceTables);
				note.setSystemUser(systemUser);
				note.setSystemUserId(sysUserId);
				//1926 for audit trail
				note.setSysUserId(sysUserId);

				if (noteId != null && !noteId.equals("0")) {
					note.setId((String) noteIdList.get(i));
					note.setSubject((String) noteSubjectList.get(i));
					note.setText((String) noteTextList.get(i));
					note.setNoteType((String) noteTypeList.get(i));

					Timestamp noteTimestamp = null;
					if (!StringUtil.isNullorNill((String) noteLastupdatedList
							.get(i))) {

						noteTimestamp = DateUtil.formatStringToTimestamp((String) noteLastupdatedList.get(i));
					}

					note.setLastupdated(noteTimestamp);

					// UPDATE
					noteDAO.updateData(note);
					// }

				} else {
					// this is a new note
					note.setSubject((String) noteSubjectList.get(i));
					note.setText((String) noteTextList.get(i));
					note.setNoteType((String) noteTypeList.get(i));
					// INSERT
					noteDAO.insertData(note);
				}
			}

			tx.commit();

			return getForward(mapping.findForward(forward), selectedTestId,
					analysisId);

		} catch (LIMSRuntimeException lre) {
			//bugzilla 2154
			LogEvent.logError("ResultsEntryNotesUpdateAction","performAction()",lre.toString());
    		tx.rollback();
			errors = new ActionMessages();
			ActionError error = null;
			if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
				error = new ActionError("errors.OptimisticLockException", null,
						null);
			} else {
				if (lre.getException() instanceof LIMSDuplicateRecordException) {
					java.util.Locale locale = (java.util.Locale) request
							.getSession().getAttribute(
									"org.apache.struts.action.LOCALE");
					String messageKey = "note.note";
					String msg = ResourceLocator.getInstance()
							.getMessageResources().getMessage(locale,
									messageKey);
					error = new ActionError("errors.DuplicateRecord", msg, null);

				} else {
					error = new ActionError("errors.UpdateException", null,
							null);
				}
			}
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);

			// disable previous and next
			request.setAttribute(PREVIOUS_DISABLED, "true");
			request.setAttribute(NEXT_DISABLED, "true");

			// default forward fail
			forward = FWD_FAIL;

		} finally {
			HibernateUtil.closeSession();
		}
		if (forward.equals(FWD_FAIL) || forward.equals(FWD_FAIL_HUMAN) || forward.equals(FWD_FAIL_ANIMAL))
			return mapping.findForward(forward);

		// initialize the form
		dynaForm.initialize(mapping);

		// we need this for subsequent actions to
		// get data related to note parent for forwarding to next page
		request.setAttribute("refId", refId);
		request.setAttribute(SELECTED_TEST_ID, selectedTestId);

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		return "resultsentry.edit.subtitle";
	}

	protected String getPageSubtitleKey() {
		return "resultsentry.edit.subtitle";
	}

	protected ActionForward getForward(ActionForward forward, String testId,
			String analysisId) {
		ActionRedirect redirect = new ActionRedirect(forward);

		if (testId != null)
			redirect.addParameter(SELECTED_TEST_ID, testId);

		if (analysisId != null)
			redirect.addParameter(ANALYSIS_ID, analysisId);

		// System.out.println("This is redirect " + redirect.getPath());
		return redirect;
	}

}
