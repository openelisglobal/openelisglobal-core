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
package us.mn.state.health.lims.reports.valueholder.audittrail;


//bugzilla 2569
public class HistoryXmlHelper extends
		us.mn.state.health.lims.audittrail.valueholder.History {

	private String change;

    private String userName;
    
	private String date;
	
	private String activity;
	
	private String referenceTableName;
	
	private String referenceTableId;

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}


	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getChange() {
		return change;
	}

	public void setChange(String change) {
		this.change = change;
	}

	public String getReferenceTableId() {
		return referenceTableId;
	}

	public void setReferenceTableId(String referenceTableId) {
		this.referenceTableId = referenceTableId;
	}

	public String getReferenceTableName() {
		return referenceTableName;
	}

	public void setReferenceTableName(String referenceTableName) {
		this.referenceTableName = referenceTableName;
	}

}