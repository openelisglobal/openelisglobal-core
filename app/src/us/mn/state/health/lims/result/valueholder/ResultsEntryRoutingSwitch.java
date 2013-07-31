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
package us.mn.state.health.lims.result.valueholder;

import us.mn.state.health.lims.common.action.IActionConstants;


/**
 * @author benzd1
 * bugzilla 2053
 */
public class ResultsEntryRoutingSwitch implements IActionConstants {

	private boolean batchResultsVerificationSwitch = false;
	private boolean qaEntrySwitch = false;
	//bugzilla 2504
	private boolean qaEntryLineListingSwitch = false;
	
	public void ResultsEntryRoutingSwitch() {
		batchResultsVerificationSwitch = false;
		qaEntrySwitch = false;
		qaEntryLineListingSwitch = false;
    }

	public boolean isBatchResultsVerificationSwitch() {
		return batchResultsVerificationSwitch;
	}
	public void setBatchResultsVerificationSwitch(boolean batchResultsVerificationSwitch) {
		this.batchResultsVerificationSwitch = batchResultsVerificationSwitch;
	}

	public boolean isQaEntryEntrySwitch() {
		return qaEntrySwitch;
	}
	public void setQaEntryEntrySwitch(boolean qaEntrySwitch) {
		this.qaEntrySwitch = qaEntrySwitch;
	}

	public boolean isQaEntryEntryLineListingSwitch() {
		return qaEntryLineListingSwitch;
	}
	public void setQaEntryEntryLineListingSwitch(boolean qaEntryLineListingSwitch) {
		this.qaEntryLineListingSwitch = qaEntryLineListingSwitch;
	}

}
