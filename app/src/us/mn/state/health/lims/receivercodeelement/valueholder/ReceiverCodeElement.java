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
package us.mn.state.health.lims.receivercodeelement.valueholder;

import us.mn.state.health.lims.codeelementtype.valueholder.CodeElementType;
import us.mn.state.health.lims.common.valueholder.BaseObject;
import us.mn.state.health.lims.common.valueholder.ValueHolder;
import us.mn.state.health.lims.common.valueholder.ValueHolderInterface;
import us.mn.state.health.lims.messageorganization.valueholder.MessageOrganization;

public class ReceiverCodeElement extends BaseObject {

	private String id;
	
	private String identifier;

	private String text;
	
	private String codeSystem;
	
	private String messageOrganizationName;

	private String selectedMessageOrganizationId;

	private ValueHolderInterface messageOrganization;
	
	private String codeElementTypeName;

	private String selectedCodeElementTypeId;

	private ValueHolderInterface codeElementType;

	
	public ReceiverCodeElement() {
		super();
		this.messageOrganization = new ValueHolder();
		this.codeElementType = new ValueHolder();
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

	public String getCodeSystem() {
		return codeSystem;
	}

	public void setCodeSystem(String codeSystem) {
		this.codeSystem = codeSystem;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
    //messageOrganzation
	public MessageOrganization getMessageOrganization() {
		return (MessageOrganization) this.messageOrganization.getValue();
	}

	public void setMessageOrganization(MessageOrganization messageOrganization) {
		this.messageOrganization.setValue(messageOrganization);
	}

	protected ValueHolderInterface getMessageOrganizationHolder() {
		return this.messageOrganization;
	}

	public String getMessageOrganizationName() {
		return this.messageOrganizationName;
	}

	protected void setMessageOrganizationHolder(ValueHolderInterface messageOrganization) {
		this.messageOrganization = messageOrganization;
	}

	public void setMessageOrganizationName(String messageOrganizationName) {
		this.messageOrganizationName = messageOrganizationName;
	}

	public String selectedMessageOrganizationId() {
		return this.selectedMessageOrganizationId;
	}

	public void setSelectedMessageOrganizationId(String selectedMessageOrganizationId) {
		this.selectedMessageOrganizationId = selectedMessageOrganizationId;
	}

	public String getSelectedMessageOrganizationId() {
		return this.selectedMessageOrganizationId;
	}

	//codeElementType
	public CodeElementType getCodeElementType() {
		return (CodeElementType) this.codeElementType.getValue();
	}

	public void setCodeElementType(CodeElementType codeElementType) {
		this.codeElementType.setValue(codeElementType);
	}

	protected ValueHolderInterface getCodeElementTypeHolder() {
		return this.codeElementType;
	}

	public String getCodeElementTypeName() {
		return this.codeElementTypeName;
	}

	protected void setCodeElementTypeHolder(ValueHolderInterface codeElementType) {
		this.codeElementType = codeElementType;
	}

	public void setCodeElementTypeName(String codeElementTypeName) {
		this.codeElementTypeName = codeElementTypeName;
	}

	public String selectedCodeElementTypeId() {
		return this.selectedCodeElementTypeId;
	}

	public void setSelectedCodeElementTypeId(String selectedCodeElementTypeId) {
		this.selectedCodeElementTypeId = selectedCodeElementTypeId;
	}

	public String getSelectedCodeElementTypeId() {
		return this.selectedCodeElementTypeId;
	}
}