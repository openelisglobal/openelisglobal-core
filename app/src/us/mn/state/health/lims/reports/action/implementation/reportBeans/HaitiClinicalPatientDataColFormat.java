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
package us.mn.state.health.lims.reports.action.implementation.reportBeans;

import us.mn.state.health.lims.sample.util.AccessionNumberUtil;


public class HaitiClinicalPatientDataColFormat {

	private String sectionName = null;
	private String nationalId = "";
	private String gender = "";
	private String stNumber = "";
	private String subjectNumber = "";
	private String contactInfo;
	private String siteInfo;
	private String testName;
	private String testRefRange;
	private String result;
	private String note;
    private String conclusion;
	private String accessionNumber;
	private String receivedDate;
    private String age = "";
    private String firstName = "";
    private String lastName = "";
    private String dept;
    private String commune;
    private String med;
    private String col1testName;
	private String col2testName;
	private String col1result;
	private String col2result;
	private String col1range;
	private String col2range;
	private Boolean col1noUOM = false;
	private Boolean col2noUOM = true;
	private String col1Note;
	private String col2Note;
	private String areNotes;
	private String completeFlag = null;
	private String healthDistrict;
    private String healthRegion; 
    private String labOrderType;
	
 
    public HaitiClinicalPatientDataColFormat(HaitiClinicalPatientData data){
    	setNationalId(data.getNationalId());
    	setGender(data.getGender());
    	setStNumber(data.getStNumber());
    	setContactInfo(data.getContactInfo());
    	setSiteInfo( data.getSiteInfo());
    	setAccessionNumber( AccessionNumberUtil.getAccessionNumberFromSampleItemAccessionNumber(data.getAccessionNumber()));
    	setReceivedDate(data.getReceivedDate());
    	setFirstName(data.getFirstName());
    	setLastName( data.getLastName());
    	setSectionName(data.getTestSection());
    	setDept(data.getDept());
    	setCommune(data.getCommune());
    	setSubjectNumber(data.getSubjectNumber());
    	setHealthDistrict(data.getHealthDistrict());
    	setHealthRegion( data.getHealthRegion());
    	setLabOrderType(data.getLabOrderType());
    	//private String testName;
    	//private String testRefRange;
    	//private String result;
    	//private String note;
        //private String conclusion;
    	
    	//private String dob;	
    }
    

    private String uom;

	public String getTestRefRange() {
		return testRefRange;
	}

	public void setTestRefRange(String testRefRange) {
		this.testRefRange = testRefRange;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}


	public String getAccessionNumber() {
		return accessionNumber;
	}

	public void setAccessionNumber(String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}

	public String getNationalId() {
		return nationalId;
	}

	public void setNationalId(String nationalId) {
		this.nationalId = nationalId;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getStNumber() {
		return stNumber;
	}

	public void setStNumber(String stNumber) {
		this.stNumber = stNumber;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getTestName() {
		return testName;
	}

	public void setReceivedDate(String recievedDate) {
		this.receivedDate = recievedDate;
	}

	// in case of typo
	public String getRecievedDate() {
	    return getReceivedDate();
	}

	public String getReceivedDate() {
		return receivedDate;
	}

	public void setConclusion(String conclusioned) {
		conclusion = conclusioned;
	}

	public String getConclusion() {
		return conclusion;
	}

	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}

	public String getContactInfo() {
		return contactInfo;
	}

	public void setSiteInfo(String siteInfo) {
		this.siteInfo = siteInfo;
	}

	public String getSiteInfo() {
		return siteInfo;
	}


	public void setUom(String uom) {
		this.uom = uom;
	}

	public String getUom() {
		return uom;
	}
	
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
    
	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
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

	public void setLastName(String secondName) {
		this.lastName = secondName;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getCol1testName() {
		return col1testName;
	}

	public void setCol1testName(String col1testName) {
		this.col1testName = col1testName;
	}

	public String getCol2testName() {
		return col2testName;
	}

	public void setCol2testName(String col2testName) {
		this.col2testName = col2testName;
	}

	public String getCol1result() {
		return col1result;
	}

	public void setCol1result(String col1result) {
		this.col1result = col1result;
	}

	public String getCol2result() {
		return col2result;
	}

	public void setCol2result(String col2result) {
		this.col2result = col2result;
	}

	public String getCol1range() {
		return col1range;
	}

	public void setCol1range(String col1range) {
		this.col1range = col1range;
	}

	public String getCol2range() {
		return col2range;
	}

	public void setCol2range(String col2range) {
		this.col2range = col2range;
	}

	public Boolean getCol2noUOM() {
		return col2noUOM;
	}

	public void setCol2noUOM(Boolean col2noUOM) {
		this.col2noUOM = col2noUOM;
	}

	public Boolean getCol1noUOM() {
		return col1noUOM;
	}

	public void setCol1noUOM(Boolean col1noUOM) {
		this.col1noUOM = col1noUOM;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public String getCommune() {
		return commune;
	}

	public void setCommune(String commune) {
		this.commune = commune;
	}

	public String getMed() {
		return med;
	}

	public void setMed(String med) {
		this.med = med;
	}

	public String getCol1Note() {
		return col1Note;
	}

	public void setCol1Note(String col1Note) {
		this.col1Note = col1Note;
	}

	public String getCol2Note() {
		return col2Note;
	}

	public void setCol2Note(String col2Note) {
		this.col2Note = col2Note;
	}

	public String getAreNotes() {
		return areNotes;
	}

	public void setAreNotes(String areNotes) {
		this.areNotes = areNotes;
	}

	public void turnOnNotes(){
		setAreNotes(".");
	}

	public String getCompleteFlag() {
		return completeFlag;
	}

	public void setCompleteFlag(String completeFlag) {
		this.completeFlag = completeFlag;
	}

	public String getSubjectNumber() {
		return subjectNumber;
	}

	public void setSubjectNumber(String subjectNumber) {
		this.subjectNumber = subjectNumber;
	}

	public String getHealthDistrict() {
		return healthDistrict;
	}

	public void setHealthDistrict(String healthDistrict) {
		this.healthDistrict = healthDistrict;
	}

	public String getHealthRegion() {
		return healthRegion;
	}

	public void setHealthRegion(String healthRegion) {
		this.healthRegion = healthRegion;
	}

	public String getLabOrderType() {
		return labOrderType;
	}

	public void setLabOrderType(String labOrderType) {
		this.labOrderType = labOrderType;
	}
}
