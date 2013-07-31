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
package us.mn.state.health.lims.reports.action.implementation.reportBeans;

public class TestSegmentedExportBean{

	private String siteCode = "";
	private String nationalId = "";
	private String firstName = "";
	private String lastName = "";
	private String gender = "";
	private String DOB = "";
	private String age = "";
	private String accessionNumber = "";
	private String status = "";
	private String sampleType = "";
	private String receptionDate = "";
	private String referringSiteName = "";
	private String testBench = "";
	private String testName = "";
	private String result = "";
	private String resultDate = "";
	
	private static final String header = "CODSITE,LNSPID,FIRSTNAME,LASTNAME,SEX,DOB,AGE,SPECIMENID,STATUT,SPECIMEN,RECEIPTDATE,SPECIMEN_PROVENANCE,DEPARTEMENT,CATEGORIE,TEST,RESULTAT,RESULTDATE";

	public String getAsCSVString(){
		StringBuilder builder = new StringBuilder();
		builder.append(siteCode);
		builder.append(",");
		builder.append(nationalId);
		builder.append(",");
		builder.append(firstName);
		builder.append(",");
		builder.append(lastName);
		builder.append(",");
		builder.append(gender);
		builder.append(",");
		builder.append(DOB);
		builder.append(",");
		builder.append(age);
		builder.append(",");
		builder.append(accessionNumber);
		builder.append(",");
		builder.append(status);
		builder.append(",");
		builder.append(sampleType);
		builder.append(",");
		builder.append(receptionDate);
		builder.append(",");
		builder.append(referringSiteName);
		builder.append(",");
		builder.append(testBench);
		builder.append(",");
		builder.append(testBench);//department and category are the same
		builder.append(",");
		builder.append(testName);
		builder.append(",");
		builder.append(result);
		builder.append(",");
		builder.append(resultDate);
		return builder.toString();
	}
	
	public static String getHeader(){
		return header;
	}

	public void setSiteCode(String siteCode){
		this.siteCode = siteCode == null ? "" : siteCode;
	}

	public void setNationalId(String nationalId){
		this.nationalId = nationalId == null ? "" : nationalId;
	}

	public void setFirstName(String firstName){
		this.firstName = firstName == null ? "" : firstName;
	}

	public void setLastName(String lastName){
		this.lastName = lastName == null ? "" : lastName;
	}

	public void setGender(String gender){
		this.gender = gender == null ? "" : gender;
	}

	public void setDOB(String dOB){
		DOB = dOB == null ? "" : dOB;
	}

	public void setAge(String age){
		this.age = age == null ? "" : age;
	}

	public void setAccessionNumber(String accessionNumber){
		this.accessionNumber = accessionNumber == null ? "" : accessionNumber;
	}

	public void setStatus(String status){
		this.status = status == null ? "" : status;
	}

	public void setSampleType(String sampleType){
		this.sampleType = sampleType == null ? "" : sampleType;
	}

	public void setReceptionDate(String receptionDate){
		this.receptionDate = receptionDate == null ? "" : receptionDate;
	}

	public void setReferringSiteName(String referringSiteName){
		this.referringSiteName = referringSiteName == null ? "" : referringSiteName;
	}

	public void setTestBench(String testBench){
		this.testBench = testBench == null ? "" : testBench;
	}

	public void setTestName(String testName){
		this.testName = testName == null ? "" : testName;
	}

	public void setResult(String result){
		this.result = result == null ? "" : result;
	}

	public void setResultDate(String resultDate){
		this.resultDate = resultDate == null ? "" : resultDate;
	}
	
	
}
