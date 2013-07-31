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
package us.mn.state.health.lims.note.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.note.dao.NoteDAO;
import us.mn.state.health.lims.note.daoimpl.NoteDAOImpl;
import us.mn.state.health.lims.note.valueholder.Note;
import us.mn.state.health.lims.referencetables.valueholder.ReferenceTables;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class NotesPopupAction extends BaseAction {

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		String refId = request.getParameter(NOTES_REFID);
		String referenceTableId = request.getParameter(NOTES_REFTABLE);
		//bugzilla 1942
		String disableExternalNotes = request
				.getParameter(NOTES_EXTERNAL_NOTES_DISABLED);

		BaseActionForm dynaForm = (BaseActionForm) form;

		// initialize the form
		dynaForm.initialize(mapping);

		NoteDAO noteDAO = new NoteDAOImpl();

		// now get the Notes for this result if exist
		Note note = new Note();
		List notes = new ArrayList();
		note.setReferenceId(refId);
		// bugzilla 1922
		//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
		ReferenceTables referenceTables = new ReferenceTables();
		referenceTables.setId(referenceTableId);
		note.setReferenceTables(referenceTables);
		notes = noteDAO.getAllNotesByRefIdRefTable(note);
		if (notes != null && notes.size() > 0) {
			PropertyUtils.setProperty(dynaForm, "notes", notes);
		} else {
			PropertyUtils.setProperty(dynaForm, "notes", new ArrayList());
		}

		if (!StringUtil.isNullorNill(disableExternalNotes)) {
			PropertyUtils.setProperty(dynaForm, "disableExternalNotes",
					disableExternalNotes);
		} else {
			PropertyUtils
					.setProperty(dynaForm, "disableExternalNotes", "false");
		}
		request.setAttribute("id", refId);
		request.setAttribute("tableId", referenceTableId);

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		return "note.notespopup.title";
	}

	protected String getPageSubtitleKey() {
		return "note.notespopup.subtitle";
	}

}
