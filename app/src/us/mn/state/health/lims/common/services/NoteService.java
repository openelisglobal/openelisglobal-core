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
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.note.dao.NoteDAO;
import us.mn.state.health.lims.note.daoimpl.NoteDAOImpl;
import us.mn.state.health.lims.note.valueholder.Note;
import us.mn.state.health.lims.referencetables.dao.ReferenceTablesDAO;
import us.mn.state.health.lims.referencetables.daoimpl.ReferenceTablesDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import us.mn.state.health.lims.sampleqaevent.valueholder.SampleQaEvent;
import us.mn.state.health.lims.systemuser.dao.SystemUserDAO;
import us.mn.state.health.lims.systemuser.daoimpl.SystemUserDAOImpl;
import us.mn.state.health.lims.systemuser.valueholder.SystemUser;

import java.util.ArrayList;
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
        ANALYSIS, QA_EVENT, ORDER, SAMPLE
    }

    public enum NoteSource {
        VALIDATION,
        OTHER
    }

    private BoundTo binding;
    private final String tableId;
    private final String objectId;

    private static final String ANALYSIS_TABLE_ID;
    private static final String SAMPLE_TABLE_ID;
    private static final String SAMPLE_QAEVENT_TABLE_ID;
    private static final String SAMPLE_ITEM_TABLE_ID;

    static{
        ReferenceTablesDAO refTableDAO = new ReferenceTablesDAOImpl();
        ANALYSIS_TABLE_ID = refTableDAO.getReferenceTableByName( "ANALYSIS" ).getId();
        SAMPLE_TABLE_ID = refTableDAO.getReferenceTableByName( "SAMPLE" ).getId();
        SAMPLE_QAEVENT_TABLE_ID = refTableDAO.getReferenceTableByName( "SAMPLE_QAEVENT" ).getId();
        SAMPLE_ITEM_TABLE_ID = refTableDAO.getReferenceTableByName( "SAMPLE_ITEM" ).getId();
    }

    public NoteService(Analysis analysis){
        tableId = ANALYSIS_TABLE_ID;
        objectId = analysis.getId();
        binding = BoundTo.ANALYSIS;
    }

    public NoteService(Sample sample){
        tableId = SAMPLE_TABLE_ID;
        objectId = sample.getId();
        binding = BoundTo.ORDER;
    }

    public NoteService(SampleQaEvent sampleQaEvent){
        tableId = SAMPLE_QAEVENT_TABLE_ID;
        objectId = sampleQaEvent.getId();
        binding = BoundTo.QA_EVENT;
    }

    public NoteService( SampleItem sampleItem){
        tableId = SAMPLE_ITEM_TABLE_ID;
        objectId = sampleItem.getId();
        binding = BoundTo.SAMPLE;
    }

    public String getNotesAsString( boolean prefixType, boolean prefixTimestamp, String noteSeparator, NoteType[] filter ){
        List<String> dbFilter = new ArrayList<String>( filter.length );
        for( NoteType type : filter){
            dbFilter.add( type.getDBCode() );
        }

        List<Note> noteList = noteDAO.getNotesChronologicallyByRefIdAndRefTableAndType( objectId, tableId, dbFilter );

        return notesToString( prefixType, prefixTimestamp, noteSeparator, noteList );
    }

    public String getNotesAsString( boolean prefixType, boolean prefixTimestamp, String noteSeparator ){
         List<Note> noteList = noteDAO.getNotesChronologicallyByRefIdAndRefTable( objectId, tableId );

        return notesToString( prefixType, prefixTimestamp, noteSeparator, noteList );
    }

    private String notesToString( boolean prefixType, boolean prefixTimestamp, String noteSeparator, List<Note> noteList ){
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

            builder.append( StringUtil.blankIfNull( noteSeparator ) );
        }

        if(!GenericValidator.isBlankOrNull( noteSeparator )){
            builder.setLength(builder.lastIndexOf( noteSeparator ));
        }

        return builder.toString();
    }

    public String getNotesAsString( String prefix, String noteSeparator ){
        List<Note> noteList = noteDAO.getNotesChronologicallyByRefIdAndRefTable( objectId, tableId );

        if(noteList.isEmpty()){
            return null;
        }

        StringBuilder builder = new StringBuilder();

        for( Note note : noteList ){
            builder.append( StringUtil.blankIfNull( prefix ) );
            builder.append( note.getText() );
            builder.append( StringUtil.blankIfNull( noteSeparator ) );
        }

        if( !GenericValidator.isBlankOrNull( noteSeparator )){
            builder.setLength(builder.lastIndexOf( noteSeparator ));
        }

        return builder.toString();
    }


    public Note getMostRecentNoteFilteredBySubject(String filter){
        List<Note> noteList;
        if( GenericValidator.isBlankOrNull( filter )){
            noteList = noteDAO.getNotesChronologicallyByRefIdAndRefTable( objectId, tableId );
            if(!noteList.isEmpty()){
                return noteList.get(noteList.size() - 1);
            }
        }else{
            noteList = noteDAO.getNoteByRefIAndRefTableAndSubject(objectId, tableId, filter );
            if(!noteList.isEmpty()){
                return noteList.get(0);
            }
        }

        return null;
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
        note.setReferenceId( objectId );
        note.setReferenceTableId( tableId );
        note.setNoteType( type.getDBCode() );
        note.setSubject( subject );
        note.setText( text );
        note.setSysUserId( currentUserId );
        note.setSystemUser( createSystemUser(currentUserId) );

        return note;
    }

    public static SystemUser createSystemUser(String currentUserId) {
        SystemUser systemUser = new SystemUser();
        systemUser.setId(currentUserId);
        SystemUserDAO systemUserDAO = new SystemUserDAOImpl();
        systemUserDAO.getData(systemUser);
        return systemUser;
    }

    public static String getReferenceTableIdForNoteBinding( BoundTo binding){
        switch( binding ){
            case ANALYSIS:{
                return ANALYSIS_TABLE_ID;
            }
            case QA_EVENT:{
                return SAMPLE_QAEVENT_TABLE_ID;
            }
            case ORDER:{
                return SAMPLE_TABLE_ID;
            }
            case SAMPLE:{
                return SAMPLE_ITEM_TABLE_ID;
            }
            default:{
                return null;
            }
        }
    }

    private String getNotePrefix(Note note) {
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
