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
* Copyright (C) ITECH, University of Washington, Seattle WA.  All Rights Reserved.
*
*/
package us.mn.state.health.lims.common.services.historyservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import us.mn.state.health.lims.audittrail.action.workers.AuditTrailItem;
import us.mn.state.health.lims.audittrail.valueholder.History;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.note.dao.NoteDAO;
import us.mn.state.health.lims.note.daoimpl.NoteDAOImpl;
import us.mn.state.health.lims.note.valueholder.Note;
import us.mn.state.health.lims.referencetables.dao.ReferenceTablesDAO;
import us.mn.state.health.lims.referencetables.daoimpl.ReferenceTablesDAOImpl;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.sample.valueholder.Sample;

public class NoteHistoryService extends HistoryService {

	private static String NOTE_TABLE_ID;
	private static String RESULT_TABLE_ID;
	
	private static NoteDAO noteDAO = new NoteDAOImpl();
	private static ResultDAO resultDAO = new ResultDAOImpl();
	private Map<String, String> noteIdToIndicatorMap;	
	
	static {
		ReferenceTablesDAO tableDAO = new ReferenceTablesDAOImpl();

		NOTE_TABLE_ID = tableDAO.getReferenceTableByName("NOTE").getId();
		RESULT_TABLE_ID = tableDAO.getReferenceTableByName("RESULT").getId();
	}
	
	public NoteHistoryService(Sample sample) {
		setUpForNotes( sample );
	}
	
	@SuppressWarnings("unchecked")
	private void setUpForNotes(Sample sample) {
		noteIdToIndicatorMap = new HashMap<String, String>();
		
		List<Result> results = resultDAO.getResultsForSample(sample);
		History searchHistory = new History();
		searchHistory.setReferenceTable(NOTE_TABLE_ID);
		historyList = new ArrayList<History>();
		
		Note searchNote = new Note();
		searchNote.setReferenceTableId(RESULT_TABLE_ID);
		for( Result result : results){
			searchNote.setReferenceId(result.getId());

			List<Note> notes = noteDAO.getAllNotesByRefIdRefTable(searchNote);
			
			for(Note note : notes){
				searchHistory.setReferenceId(note.getId());
				noteIdToIndicatorMap.put(note.getId(), result.getAnalysis().getTest().getTestName() );
				historyList.addAll(auditTrailDAO.getHistoryByRefIdAndRefTableId(searchHistory));
			}	
		}
		
		newValueMap = new HashMap<String, String>();
	}

	@Override
	protected void addInsertion(History history, List<AuditTrailItem> items) {
		Note note = noteDAO.getData(history.getReferenceId());
		identifier = noteIdToIndicatorMap.get(history.getReferenceId());
		AuditTrailItem audit = getCoreTrail(history);
		audit.setNewValue( note.getText());
		items.add(audit);
	}

	@Override
	protected void getObservableChanges(History history, Map<String, String> changeMap, String changes) {
		
	}

	@Override
	protected String getObjectName() {
		return StringUtil.getMessageForKey("note.note");
	}
}
