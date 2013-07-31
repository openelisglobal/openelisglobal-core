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

import java.util.ArrayList;

import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.provider.valueholder.Provider;
import us.mn.state.health.lims.sourceofsample.valueholder.SourceOfSample;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;

//bugzilla 2569
public class SampleXmlHelper extends
		us.mn.state.health.lims.sample.valueholder.Sample {

	private Patient patient;

    private SourceOfSample sourceOfSample;
	
	private TypeOfSample typeOfSample;

	private Provider provider;

	//the tests need their own helper class to refer to child compnents/results
	private ArrayList tests;
	
	private ArrayList historyRecords;

	public SampleXmlHelper() {
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public SourceOfSample getSourceOfSample() {
		return sourceOfSample;
	}

	public void setSourceOfSample(SourceOfSample sourceOfSample) {
		this.sourceOfSample = sourceOfSample;
	}

	public ArrayList getTests() {
		return tests;
	}

	public void setTests(ArrayList tests) {
		this.tests = tests;
	}

	public TypeOfSample getTypeOfSample() {
		return typeOfSample;
	}

	public void setTypeOfSample(TypeOfSample typeOfSample) {
		this.typeOfSample = typeOfSample;
	}

	public ArrayList getHistoryRecords() {
		return historyRecords;
	}

	public void setHistoryRecords(ArrayList historyRecords) {
		this.historyRecords = historyRecords;
	}
	
	public void addHistoryRecords(ArrayList historyRecords) {
		this.historyRecords.addAll(historyRecords);
	}

}