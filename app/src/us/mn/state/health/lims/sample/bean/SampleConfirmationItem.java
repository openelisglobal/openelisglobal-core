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
package us.mn.state.health.lims.sample.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SampleConfirmationItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private String requesterSampleId;
	private String sampleType;
	private String collectionDate;
	private List<SampleConfirmationTest> requesterTests;

	public String getRequesterSampleId() {
		return requesterSampleId;
	}
	public void setRequesterSampleId(String requesterSampleId) {
		this.requesterSampleId = requesterSampleId;
	}
	public String getSampleType() {
		return sampleType;
	}
	public void setSampleType(String sampleType) {
		this.sampleType = sampleType;
	}
	public String getCollectionDate() {
		return collectionDate;
	}
	public void setCollectionDate(String collectionDate) {
		this.collectionDate = collectionDate;
	}
	public List<SampleConfirmationTest> getRequesterTests() {
		return requesterTests;
	}
	public void setRequesterTests(List<SampleConfirmationTest> requesterTests) {
		this.requesterTests = requesterTests;
	}

	public void addRequesterTest( SampleConfirmationTest test){
		if( requesterTests == null){
			requesterTests = new ArrayList<SampleConfirmationTest>();
		}

		requesterTests.add(test);
	}
}
