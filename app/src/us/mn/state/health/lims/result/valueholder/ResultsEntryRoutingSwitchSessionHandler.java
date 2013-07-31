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

import javax.servlet.http.HttpSession;

import us.mn.state.health.lims.common.action.IActionConstants;


/**
 * @author benzd1
 * bugzilla 2053
 */
public class ResultsEntryRoutingSwitchSessionHandler implements IActionConstants {

public static void switchOn(int routingSwitch, HttpSession session) {
	ResultsEntryRoutingSwitch resultsEntryRoutingSwitch = null;
	if (session.getAttribute(RESULTS_ENTRY_ROUTING_SWITCH) != null) {
		resultsEntryRoutingSwitch = (ResultsEntryRoutingSwitch)session.getAttribute(RESULTS_ENTRY_ROUTING_SWITCH);
	} else {
		resultsEntryRoutingSwitch = new ResultsEntryRoutingSwitch();
	}
	switch (routingSwitch) { 
	case RESULTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_VERIFICATION: 
		resultsEntryRoutingSwitch.setBatchResultsVerificationSwitch(true);
		resultsEntryRoutingSwitch.setQaEntryEntrySwitch(false);
		//bugzilla 2504
		resultsEntryRoutingSwitch.setQaEntryEntryLineListingSwitch(false);
		break;
	case RESULTS_ENTRY_ROUTING_FROM_QAEVENTS_ENTRY:  
		resultsEntryRoutingSwitch.setBatchResultsVerificationSwitch(false);
		resultsEntryRoutingSwitch.setQaEntryEntrySwitch(true);
		resultsEntryRoutingSwitch.setQaEntryEntryLineListingSwitch(false);
		break;
	case RESULTS_ENTRY_ROUTING_FROM_QAEVENTS_ENTRY_LINELISTING:  
		resultsEntryRoutingSwitch.setBatchResultsVerificationSwitch(false);
		resultsEntryRoutingSwitch.setQaEntryEntrySwitch(false);
		resultsEntryRoutingSwitch.setQaEntryEntryLineListingSwitch(true);
		break;
     default: //System.out.println("An error occurred with QaEventRoutingSwitchSessionHandler switchOn(" + routingSwitch + ")"); 
	}
	
	session.setAttribute(RESULTS_ENTRY_ROUTING_SWITCH, resultsEntryRoutingSwitch);
}

public static void switchOff(int routingSwitch, HttpSession session) {
	ResultsEntryRoutingSwitch resultsEntryRoutingSwitch = null;
	if (session.getAttribute(RESULTS_ENTRY_ROUTING_SWITCH) != null) {
		resultsEntryRoutingSwitch = (ResultsEntryRoutingSwitch)session.getAttribute(RESULTS_ENTRY_ROUTING_SWITCH);
	} else {
		resultsEntryRoutingSwitch = new ResultsEntryRoutingSwitch();
	}
	switch (routingSwitch) { 
	case RESULTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_VERIFICATION: 
		resultsEntryRoutingSwitch.setBatchResultsVerificationSwitch(false);
		//also remove session attributes
		session.setAttribute(RESULTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_VERIFICATION_PARAM_ACCESSION_NUMBER, null);
		session.setAttribute(RESULTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_VERIFICATION_PARAM_TEST_ID, null);
		session.setAttribute(RESULTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_VERIFICATION_PARAM_TEST_SECTION_ID, null);

		break;
	case RESULTS_ENTRY_ROUTING_FROM_QAEVENTS_ENTRY:  
		resultsEntryRoutingSwitch.setQaEntryEntrySwitch(false);
		break;
	case RESULTS_ENTRY_ROUTING_FROM_QAEVENTS_ENTRY_LINELISTING:  
		resultsEntryRoutingSwitch.setQaEntryEntryLineListingSwitch(false);
		break;
   	default: //System.out.println("An error occurred with QaEventRoutingSwitchSessionHandler switchOff(" + routingSwitch + ")"); 
	}
	
	session.setAttribute(RESULTS_ENTRY_ROUTING_SWITCH, resultsEntryRoutingSwitch);
}

public static void switchAllOff(HttpSession session) {
	ResultsEntryRoutingSwitch resultsEntryRoutingSwitch = null;
	if (session.getAttribute(RESULTS_ENTRY_ROUTING_SWITCH) != null) {
		resultsEntryRoutingSwitch = (ResultsEntryRoutingSwitch)session.getAttribute(RESULTS_ENTRY_ROUTING_SWITCH);
	} else {
		resultsEntryRoutingSwitch = new ResultsEntryRoutingSwitch();
	}
	resultsEntryRoutingSwitch.setBatchResultsVerificationSwitch(false);
	resultsEntryRoutingSwitch.setQaEntryEntrySwitch(false);
	resultsEntryRoutingSwitch.setQaEntryEntryLineListingSwitch(false);
	
	session.setAttribute(RESULTS_ENTRY_ROUTING_SWITCH, resultsEntryRoutingSwitch);
}

public static boolean isSwitchOn(int routingSwitch, HttpSession session){
	ResultsEntryRoutingSwitch resultsEntryRoutingSwitch = null;
	if (session.getAttribute(RESULTS_ENTRY_ROUTING_SWITCH) != null) {
		resultsEntryRoutingSwitch = (ResultsEntryRoutingSwitch)session.getAttribute(RESULTS_ENTRY_ROUTING_SWITCH);
	} else {
		return false;
	}
	switch (routingSwitch) { 
	case RESULTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_VERIFICATION: 
		if (resultsEntryRoutingSwitch.isBatchResultsVerificationSwitch()) return true;
		else return false;
	case RESULTS_ENTRY_ROUTING_FROM_QAEVENTS_ENTRY:  
		if (resultsEntryRoutingSwitch.isQaEntryEntrySwitch()) return true;
		else return false;
	case RESULTS_ENTRY_ROUTING_FROM_QAEVENTS_ENTRY_LINELISTING:  
		if (resultsEntryRoutingSwitch.isQaEntryEntryLineListingSwitch()) return true;
		else return false;
 	default: //System.out.println("An error occurred with QaEventRoutingSwitchSessionHandler isSwitchOn(" + routingSwitch + ")");
	         return false;
	}
	
	
}

}
