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
package us.mn.state.health.lims.messageorganization.valueholder;

import java.sql.Date;

import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.valueholder.EnumValueItemImpl;
import us.mn.state.health.lims.common.valueholder.ValueHolder;
import us.mn.state.health.lims.common.valueholder.ValueHolderInterface;
import us.mn.state.health.lims.organization.valueholder.Organization;

public class MessageOrganization extends EnumValueItemImpl {

	private String id;

	private String description;

	private String organizationName;

	private String selectedOrganizationId;

	private ValueHolderInterface organization;

	private String isActive;

	private Date activeBeginDate = null;

	private String activeBeginDateForDisplay;

	private Date activeEndDate = null;

	private String activeEndDateForDisplay;

	public MessageOrganization() {
		super();
		this.organization = new ValueHolder();
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setActiveBeginDate(Date activeBeginDate) {
		this.activeBeginDate = activeBeginDate;
		this.activeBeginDateForDisplay = DateUtil.convertSqlDateToStringDate(activeBeginDate);
	}

	public Date getActiveBeginDate() {
		return activeBeginDate;
	}

	public void setActiveEndDate(Date activeEndDate) {
		this.activeEndDate = activeEndDate;
		this.activeEndDateForDisplay = DateUtil.convertSqlDateToStringDate(	activeEndDate);
	}

	public Date getActiveEndDate() {
		return activeEndDate;
	}
	
	public void setActiveBeginDateForDisplay(String activeBeginDateForDisplay) {
		this.activeBeginDateForDisplay = activeBeginDateForDisplay;
		// also update the java.sql.Date
		String locale = SystemConfiguration.getInstance().getDefaultLocale()
				.toString();
		this.activeBeginDate = DateUtil.convertStringDateToSqlDate(
				this.activeBeginDateForDisplay, locale);
	}

	public String getActiveBeginDateForDisplay() {
		return activeBeginDateForDisplay;
	}

	public void setActiveEndDateForDisplay(String activeEndDateForDisplay) {
		this.activeEndDateForDisplay = activeEndDateForDisplay;
		// also update the java.sql.Date
		String locale = SystemConfiguration.getInstance().getDefaultLocale()
				.toString();
		this.activeEndDate = DateUtil.convertStringDateToSqlDate(
				activeEndDateForDisplay, locale);
	}

	public String getActiveEndDateForDisplay() {
		return activeEndDateForDisplay;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public Organization getOrganization() {
		return (Organization) this.organization.getValue();
	}

	public void setOrganization(Organization organization) {
		this.organization.setValue(organization);
	}

	protected ValueHolderInterface getOrganizationHolder() {
		return this.organization;
	}

	public String getOrganizationName() {
		return this.organizationName;
	}

	protected void setOrganizationHolder(ValueHolderInterface organization) {
		this.organization = organization;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String selectedOrganizationId() {
		return this.selectedOrganizationId;
	}

	public void setSelectedOrganizationId(String selectedOrganizationId) {
		this.selectedOrganizationId = selectedOrganizationId;
	}

	public String getSelectedOrganizationId() {
		return this.selectedOrganizationId;
	}

}