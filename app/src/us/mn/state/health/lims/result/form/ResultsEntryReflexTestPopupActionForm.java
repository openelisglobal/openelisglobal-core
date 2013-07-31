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
package us.mn.state.health.lims.result.form;

import us.mn.state.health.lims.common.action.BaseActionForm;

//We have some dynamically (through javascript) created collections that need to be initialized
public class ResultsEntryReflexTestPopupActionForm extends BaseActionForm {

	private String[] selectedAddedTests;

	public String[] getSelectedAddedTests() {
		return selectedAddedTests;
	}

	public void setSelectedAddedTests(String[] selectedAddedTests) {
		this.selectedAddedTests = selectedAddedTests;
	}

	public void reset() {
		selectedAddedTests = new String[0];
	}

}