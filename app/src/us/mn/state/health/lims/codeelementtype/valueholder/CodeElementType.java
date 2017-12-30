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
package us.mn.state.health.lims.codeelementtype.valueholder;

import us.mn.state.health.lims.common.valueholder.EnumValueItemImpl;
import us.mn.state.health.lims.common.valueholder.ValueHolder;
import us.mn.state.health.lims.common.valueholder.ValueHolderInterface;
import us.mn.state.health.lims.referencetables.valueholder.ReferenceTables;

public class CodeElementType extends EnumValueItemImpl {

	private String id;

	private String text;
	
	//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
	private ValueHolderInterface referenceTables;
	
	private String referenceTableId;

	
	public CodeElementType() {
		super();
		//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
		this.referenceTables = new ValueHolder();
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

    //referenceTable
	public void setReferenceTableId(String referenceTableId) {
		this.referenceTableId = referenceTableId;
	}

	public String getReferenceTableId() {
		return referenceTableId;
	}

	//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
	public ReferenceTables getReferenceTables() {
		return (ReferenceTables) this.referenceTables.getValue();
	}

	//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
	protected ValueHolderInterface getReferenceTablesHolder() {
		return this.referenceTables;
	}

	//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
	public void setReferenceTables(ReferenceTables referenceTables) {
		this.referenceTables.setValue(referenceTables);
	}

	//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
	protected void setReferenceTablesHolder(ValueHolderInterface referenceTables) {
		this.referenceTables = referenceTables;
	}

}