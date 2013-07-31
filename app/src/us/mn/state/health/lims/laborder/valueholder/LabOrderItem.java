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
package us.mn.state.health.lims.laborder.valueholder;

import us.mn.state.health.lims.common.valueholder.BaseObject;

public class LabOrderItem extends BaseObject {

	private static final long serialVersionUID = -5599953728001797018L;

	private String id;
	private String labOrderTypeId;
	private String tableRef;
	private String recordId;
	private String identifier;
	private String action;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabOrderTypeId() {
		return labOrderTypeId;
	}

	public void setLabOrderTypeId(String labOrderTypeId) {
		this.labOrderTypeId = labOrderTypeId;
	}

	public String getTableRef() {
		return tableRef;
	}

	public void setTableRef(String tableRef) {
		this.tableRef = tableRef;
	}

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
