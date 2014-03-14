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
package us.mn.state.health.lims.common.services;

import org.apache.commons.validator.GenericValidator;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.ConfigurationProperties.Property;
import us.mn.state.health.lims.common.util.DAOImplFactory;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.note.dao.NoteDAO;
import us.mn.state.health.lims.note.daoimpl.NoteDAOImpl;
import us.mn.state.health.lims.note.valueholder.Note;
import us.mn.state.health.lims.referencetables.dao.ReferenceTablesDAO;
import us.mn.state.health.lims.referencetables.daoimpl.ReferenceTablesDAOImpl;
import us.mn.state.health.lims.referencetables.valueholder.ReferenceTables;
import us.mn.state.health.lims.systemuser.dao.SystemUserDAO;
import us.mn.state.health.lims.systemuser.daoimpl.SystemUserDAOImpl;
import us.mn.state.health.lims.systemuser.valueholder.SystemUser;

import java.util.List;

public class NoteService{
    private static NoteDAO noteDAO = new NoteDAOImpl();
    private static boolean SUPPORT_INTERNAL_EXTERNAL = ConfigurationProperties.getInstance().isPropertyValueEqual(Property.NOTE_EXTERNAL_ONLY_FOR_VALIDATION,"true");

    public enum NoteType{
        EXTERNAL(Note.EXTERNAL),
        INTERNAL(Note.INTERNAL),
        REJECTION_REASON( Note.REJECT_REASON);

        String DBCode;

        NoteType(String dbCode){
            DBCode = dbCode;
        }

        public String getDBCode(){
            return DBCode;
        }
    }

    public enum BoundTo{
        ANALYSIS, NON_CONFORMITY, ORDER, SAMPLE
    }

    public enum NoteSource {
        VALIDATION,
        OTHER
    }

    private Analysis analysis;
    private BoundTo binding;

    private static final String ANALYSIS_TABLE_ID;

    static{
        ReferenceTablesDAO refTableDAO = new ReferenceTablesDAOImpl();
        ANALYSIS_TABLE_ID = refTableDAO.getReferenceTableByName( "ANALYSIS" ).getId();
    }

    public NoteService(Analysis analysis){
        this.analysis = analysis;
        binding = BoundTo.ANALYSIS;
    }

    public String getNotesAsString( boolean prefixType, boolean prefixTimestamp, String noteSeparater ){
        List<Note> noteList = null;

        switch( binding ){
            case ANALYSIS:{
                noteList = noteDAO.getNotesChronologicallyByRefIdAndRefTable( analysis.getId(), ANALYSIS_TABLE_ID );
                break;
            }
            default:{
                return "";
            }
        }

        if(noteList.isEmpty()){
            return null;
        }

        StringBuilder builder = new StringBuilder();

        for( Note note : noteList ){
            if( prefixType ){
                builder.append( getNotePrefix( note ) );
                builder.append( " " );
            }

            if( prefixTimestamp ){
                builder.append( getNoteTimestamp( note ) );
                builder.append( " " );
            }

            if( prefixType || prefixTimestamp){
                builder.append( ": " );
            }
            builder.append( note.getText() );

            builder.append( noteSeparater );
        }

        builder.setLength(builder.lastIndexOf(noteSeparater));

        return builder.toString();
    }

    private String getNoteTimestamp( Note note ){
        return DateUtil.convertTimestampToStringDateAndTime( note.getLastupdated() );
    }

    /**
     * @param type
     * @param text
     * @param subject
     * @param currentUserId
     * @return  Note
     */
    public Note createSavableNote( NoteType type, String text, String subject, String currentUserId){
        if( GenericValidator.isBlankOrNull( text )){
            return null;
        }

        Note note = new Note();

        switch( binding ){
            case ANALYSIS:{
                note.setReferenceId( analysis.getId() );
                note.setReferenceTableId( ANALYSIS_TABLE_ID );
            }
        }

        note.setNoteType( type.getDBCode() );
        note.setSubject( subject );
        note.setText( text );
        note.setSysUserId( currentUserId );
        note.setSystemUser( createSystemUser(currentUserId) );

        return note;
    }

	@SuppressWarnings("unchecked")
	public static List<Note> getNotesForObjectAndTable(String objectId, String tableId) {

		Note note = new Note();
		note.setReferenceTableId(tableId);
		note.setReferenceId(objectId);
		return noteDAO.getAllNotesByRefIdRefTable(note);
	}

    @SuppressWarnings("unchecked")
    public static List<Note> getExternalNotesForObjectAndTable(String objectId, String tableId) {

        Note note = new Note();
        note.setReferenceTableId(tableId);
        note.setReferenceId(objectId);
        note.setNoteType(Note.EXTERNAL);
        return noteDAO.getNotesByNoteTypeRefIdRefTable(note);
    }

    static public String getTableReferenceId(String tableName) {
        ReferenceTablesDAO rtDAO = DAOImplFactory.getInstance().getReferenceTablesDAOImpl();
        ReferenceTables referenceTable = new ReferenceTables();
        referenceTable.setTableName(tableName);
        referenceTable = rtDAO.getReferenceTableByName(referenceTable);
        return referenceTable.getId();
    }

    /**
     * @param noteId -- The id of the note if it exists, otherwise it may be null.  If it does exist then the Note will be loaded from the
     * database.  The id of the returned note should be checked to understand if the note should be inserted or updated
     * @param text -- What should go into the note.  May not be null or blank for new notes
     * @param objectId -- Notes are defined by some id and some table.  This is the id
     * @param tableId -- Notes are defined by some id and some table.  This is the id of the table from reference_table table
     * @param noteSubject -- This is the subject of the note.  May be some arbitary string which can then be used for searching
     * @param currentUserId -- The current user Id.  Also known as the sysUserId.  This is the same id as the one used to track changes in the history table.
     * @return The note if one could be created
     */
    @Deprecated
    public static Note createSavableNote(String noteId, String text, String objectId, String tableId, String noteSubject, String currentUserId, String noteType) {
        Note note = null;

        if (!GenericValidator.isBlankOrNull(noteId)) {
            note = new Note();
            note.setId(noteId);
            noteDAO.getData(note);
		} else if( !GenericValidator.isBlankOrNull(text)) {
            note = new Note();
            note.setReferenceId(objectId);
            note.setReferenceTableId(tableId);
            note.setNoteType(noteType);
            note.setSubject(noteSubject);
        }

        if (note != null) {
            note.setText(text);
            note.setSysUserId(currentUserId);
            note.setSystemUser(createSystemUser(currentUserId));
        }

        return note;
    }

    public static SystemUser createSystemUser(String currentUserId) {
        SystemUser systemUser = new SystemUser();
        systemUser.setId(currentUserId);
        SystemUserDAO systemUserDAO = new SystemUserDAOImpl();
        systemUserDAO.getData(systemUser);
        return systemUser;
    }

    public static String getDefaultNoteType(NoteSource source) {
        if(SUPPORT_INTERNAL_EXTERNAL) {
            return source == NoteSource.VALIDATION ? Note.EXTERNAL : Note.INTERNAL;
        }

        return Note.EXTERNAL;
    }

    public static String getNotePrefix(Note note) {
        if(SUPPORT_INTERNAL_EXTERNAL){
            if( "I".equals(note.getNoteType())){
                return StringUtil.getMessageForKey("note.type.internal");
            }else if( "E".equals(note.getNoteType())){
                return StringUtil.getMessageForKey("note.type.external");
            }else if( "R".equals( note.getNoteType() )){
                return StringUtil.getMessageForKey( "note.type.rejectReason" );
            }
        }

        return "";
    }

}
