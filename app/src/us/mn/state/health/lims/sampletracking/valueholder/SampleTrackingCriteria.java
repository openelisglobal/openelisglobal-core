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
package us.mn.state.health.lims.sampletracking.valueholder;

import java.io.Serializable;

import us.mn.state.health.lims.common.valueholder.BaseObject;

/**
 * @author benzd1
 * bugzilla 1920 Diane - added this to use valueobject instead of passing
 * bugzilla 1920 Diane - added this to use valueobject instead of passing
 */
public class SampleTrackingCriteria extends BaseObject implements Serializable {
	
	
	
	private String clientRef;
	
	private String lastName;
	
	private String firstName;
	
	private String submitter;
	
	private String receivedDate;
	
	private String sampleType;
	
	private String sampleSource;
	
	private String externalId;
	
	private String collectionDate;
	
	private String accessionNumberPartial;	
	
	private String projectId;
	
	private String sortBy;
	
	//bugzilla 2455
	private String specimenOrIsolate;
	
	public SampleTrackingCriteria () {
	}
	
	//bugzilla 2455
	public String getSpecimenOrIsolate() {
		return specimenOrIsolate;
	}
	public void setSpecimenOrIsolate(String specimenOrIsolate) {
		this.specimenOrIsolate = specimenOrIsolate;
	}
	
	public String getAccessionNumberPartial() {
		return accessionNumberPartial;
	}

	public void setAccessionNumberPartial(String accessionNumberPartial) {
		this.accessionNumberPartial = accessionNumberPartial;
	}

	public String getClientRef() {
		return clientRef;
	}

	public void setClientRef(String clientRef) {
		this.clientRef = clientRef;
	}

	public String getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(String collectionDate) {
		this.collectionDate = collectionDate;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(String receivedDate) {
		this.receivedDate = receivedDate;
	}

	public String getSampleSource() {
		return sampleSource;
	}

	public void setSampleSource(String sampleSource) {
		this.sampleSource = sampleSource;
	}

	public String getSampleType() {
		return sampleType;
	}

	public void setSampleType(String sampleType) {
		this.sampleType = sampleType;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public String getSubmitter() {
		return submitter;
	}

	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}
	

}
