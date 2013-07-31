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
package us.mn.state.health.lims.note.valueholder;

import us.mn.state.health.lims.common.valueholder.BaseObject;
import us.mn.state.health.lims.referencetables.valueholder.ReferenceTables;
import us.mn.state.health.lims.systemuser.valueholder.SystemUser;

public class Note extends BaseObject {

	private String id;

    private SystemUser systemUser;
    
    private String systemUserId;
    
	private String referenceId;
	
	//bugzilla 1922
	//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
	//private ReferenceTables referenceTables;

	private String referenceTableId;

	//private Date timestamp = null;

	//private String timestampForDisplay = null;

	private String noteType;

	private String subject;

	private String text;

	public Note() {
		super();
		//this.referenceTables = new ReferenceTables();
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	/*public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
		// also update String date
		String locale = SystemConfiguration.getInstance().getDefaultLocale()
				.toString();
		this.timestampForDisplay = StringUtil.convertSqlDateToStringDate(
				timestamp, locale);
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestampForDisplay(String timestampForDisplay) {
		this.timestampForDisplay = timestampForDisplay;
		// also update the java.sql.Date
		String locale = SystemConfiguration.getInstance().getDefaultLocale()
				.toString();
		this.timestamp = StringUtil.convertStringDateToSqlDate(
				this.timestampForDisplay, locale);
	}

	public String getTimestampForDisplay() {
		return timestampForDisplay;
	}
*/
	public String getNoteType() {
		return noteType;
	}

	public void setNoteType(String noteType) {
		this.noteType = noteType;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getReferenceTableId() {
		return referenceTableId;
	}

	public void setReferenceTableId(String referenceTableId) {
		this.referenceTableId = referenceTableId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setSystemUser(SystemUser systemUser) {
		this.systemUser = systemUser;
	}
	
	public SystemUser getSystemUser() {
		return this.systemUser;
	}


	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getSystemUserId() {
		return systemUserId;
	}

	public void setSystemUserId(String systemUserId) {
		this.systemUserId = systemUserId;
	}

//	public ReferenceTables getReferenceTables() {
//		return referenceTables;
//	}
//
	public void setReferenceTables(ReferenceTables referenceTables) {
		if( referenceTables != null){
			setReferenceTableId(referenceTables.getId());
		}
			
// note: due to transient issues we're just using the id  Do not merge into main branch		this.referenceTables = referenceTables;
	}


}

