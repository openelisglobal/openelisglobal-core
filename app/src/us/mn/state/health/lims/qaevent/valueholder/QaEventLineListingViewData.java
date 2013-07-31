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
package us.mn.state.health.lims.qaevent.valueholder;

import java.util.List;

import us.mn.state.health.lims.common.valueholder.BaseObject;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.sample.valueholder.Sample;

/**
 * @author diane benz 
 * bugzilla 2504
 */
public class QaEventLineListingViewData extends BaseObject {

	private Sample sample;
	private List sampleQaEvents;
	private List testQaEvents;
	
	private Patient patient;
	
	//not yet know where these come from
	private String weight;
	private String yellowCard;


	public QaEventLineListingViewData() {
		super();
	}


	public Patient getPatient() {
		return patient;
	}


	public void setPatient(Patient patient) {
		this.patient = patient;
	}


	public Sample getSample() {
		return sample;
	}


	public void setSample(Sample sample) {
		this.sample = sample;
	}


	public List getSampleQaEvents() {
		return sampleQaEvents;
	}


	public void setSampleQaEvents(List sampleQaEvents) {
		this.sampleQaEvents = sampleQaEvents;
	}


	public List getTestQaEvents() {
		return testQaEvents;
	}


	public void setTestQaEvents(List testQaEvents) {
		this.testQaEvents = testQaEvents;
	}


	public String getWeight() {
		return weight;
	}


	public void setWeight(String weight) {
		this.weight = weight;
	}


	public String getYellowCard() {
		return yellowCard;
	}


	public void setYellowCard(String yellowCard) {
		this.yellowCard = yellowCard;
	}

}