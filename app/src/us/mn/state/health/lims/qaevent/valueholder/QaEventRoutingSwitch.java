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

import us.mn.state.health.lims.common.action.IActionConstants;


/**
 * @author benzd1
 * bugzilla 2501, 2504, 2502
 */
public class QaEventRoutingSwitch implements IActionConstants {

	private boolean testManagementSwitch = false;
	private boolean sampleTrackingSwitch = false;
	private boolean resultsEntrySwitch = false;
	private boolean batchResultsEntrySwitch = false;
	private boolean qaEventsEntryLineListingSwitch = false;
	
	public void QaEventRoutingSwitch() {
		testManagementSwitch = false;
		sampleTrackingSwitch = false;
		resultsEntrySwitch = false;
		batchResultsEntrySwitch = false;
		qaEventsEntryLineListingSwitch = false;
	}

	public boolean isResultsEntrySwitch() {
		return resultsEntrySwitch;
	}
	public void setResultsEntrySwitch(boolean resultsEntrySwitch) {
		this.resultsEntrySwitch = resultsEntrySwitch;
	}
	public boolean isSampleTrackingSwitch() {
		return sampleTrackingSwitch;
	}
	public void setSampleTrackingSwitch(boolean sampleTrackingSwitch) {
		this.sampleTrackingSwitch = sampleTrackingSwitch;
	}
	public boolean isTestManagementSwitch() {
		return testManagementSwitch;
	}
	public void setTestManagementSwitch(boolean testManagementSwitch) {
		this.testManagementSwitch = testManagementSwitch;
	}
	public boolean isBatchResultsEntrySwitch() {
		return batchResultsEntrySwitch;
	}
	public void setBatchResultsEntrySwitch(boolean batchResultsEntrySwitch) {
		this.batchResultsEntrySwitch = batchResultsEntrySwitch;
	}

	public boolean isQaEventsEntryLineListingSwitch() {
		return qaEventsEntryLineListingSwitch;
	}

	public void setQaEventsEntryLineListingSwitch(
			boolean qaEventsEntryLineListingSwitch) {
		this.qaEventsEntryLineListingSwitch = qaEventsEntryLineListingSwitch;
	}



}
