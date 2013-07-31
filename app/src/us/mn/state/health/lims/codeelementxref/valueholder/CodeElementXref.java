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
package us.mn.state.health.lims.codeelementxref.valueholder;

import us.mn.state.health.lims.codeelementtype.valueholder.CodeElementType;
import us.mn.state.health.lims.common.valueholder.BaseObject;
import us.mn.state.health.lims.common.valueholder.EnumValueItem;
import us.mn.state.health.lims.common.valueholder.ValueHolder;
import us.mn.state.health.lims.common.valueholder.ValueHolderInterface;
import us.mn.state.health.lims.messageorganization.valueholder.MessageOrganization;
import us.mn.state.health.lims.receivercodeelement.valueholder.ReceiverCodeElement;

public class CodeElementXref extends BaseObject {

	private String id;

	private String messageOrganizationName;

	private String selectedMessageOrganizationId;

	private ValueHolderInterface messageOrganization;
	
	private String codeElementTypeName;

	private String selectedCodeElementTypeId;

	private ValueHolderInterface codeElementType;
	
	private String receiverCodeElementName;

	private String selectedReceiverCodeElementId;

	private ValueHolderInterface receiverCodeElement;
	
	private String selectedLocalCodeElementId;
	
	private String selectedLocalCodeElementName;
	
	private EnumValueItem localCodeElement;
	
	public String getSelectedLocalCodeElementName() {
		return selectedLocalCodeElementName;
	}

	public void setSelectedLocalCodeElementName(String selectedLocalCodeElementName) {
		this.selectedLocalCodeElementName = selectedLocalCodeElementName;
	}

	public CodeElementXref() {
		super();
		this.messageOrganization = new ValueHolder();
		this.codeElementType = new ValueHolder();
		this.receiverCodeElement = new ValueHolder();
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
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
	
    //receiverCodeElement
	public ReceiverCodeElement getReceiverCodeElement() {
		return (ReceiverCodeElement) this.receiverCodeElement.getValue();
	}

	public void setReceiverCodeElement(ReceiverCodeElement receiverCodeElement) {
		this.receiverCodeElement.setValue(receiverCodeElement);
	}

	protected ValueHolderInterface getReceiverCodeElementHolder() {
		return this.receiverCodeElement;
	}

	public String getReceiverCodeElementName() {
		return this.receiverCodeElementName;
	}

	protected void setReceiverCodeElementHolder(ValueHolderInterface receiverCodeElement) {
		this.receiverCodeElement = receiverCodeElement;
	}

	public void setReceiverCodeElementName(String receiverCodeElementName) {
		this.receiverCodeElementName = receiverCodeElementName;
	}

	public String selectedReceiverCodeElementId() {
		return this.selectedReceiverCodeElementId;
	}

	public void setSelectedReceiverCodeElementId(String selectedReceiverCodeElementId) {
		this.selectedReceiverCodeElementId = selectedReceiverCodeElementId;
	}

	public String getSelectedReceiverCodeElementId() {
		return this.selectedReceiverCodeElementId;
	}



    //localCodeElementId
	public String getSelectedLocalCodeElementId() {
		return selectedLocalCodeElementId;
	}

	public void setSelectedLocalCodeElementId(String selectedLocalCodeElementId) {
		this.selectedLocalCodeElementId = selectedLocalCodeElementId;
	}

	public EnumValueItem getLocalCodeElement() {
		return localCodeElement;
	}

	public void setLocalCodeElement(EnumValueItem localCodeElement) {
		this.localCodeElement = localCodeElement;
	}
}