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
package us.mn.state.health.lims.result.action.util;

import us.mn.state.health.lims.note.valueholder.Note;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.referral.valueholder.Referral;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.result.valueholder.ResultInventory;
import us.mn.state.health.lims.result.valueholder.ResultSignature;
import us.mn.state.health.lims.sample.valueholder.Sample;

import java.util.List;
import java.util.Map;

public class ResultSet {
	public Result result;
	public ResultSignature signature;
	public ResultInventory testKit;
	public Note note;
	public Patient patient;
	public Sample sample;
	public Map<String,List<String>> triggersToSelectedReflexesMap;
	public Referral newReferral;
	public Referral existingReferral;
	public boolean alwaysInsertSignature = false;


	public ResultSet(Result result, ResultSignature signature, ResultInventory testKit, Note note, Patient patient, Sample sample,
                     Map<String,List<String>> triggersToSelectedReflexesMap , Referral newReferral, Referral existingReferral) {
		this.result = result;
		this.signature = signature;
		this.testKit = testKit;
		this.note = note;
		this.patient = patient;
		this.sample = sample;
		this.triggersToSelectedReflexesMap = triggersToSelectedReflexesMap;
		this.newReferral = newReferral;
		this.existingReferral = existingReferral;
		

		alwaysInsertSignature = signature != null && signature.getId() == null;
	}
}

