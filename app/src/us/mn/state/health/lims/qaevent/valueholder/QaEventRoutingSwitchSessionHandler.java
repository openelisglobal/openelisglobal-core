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

import javax.servlet.http.HttpSession;

import us.mn.state.health.lims.common.action.IActionConstants;


/**
 * @author benzd1
 * modified 06/2008 for bugzilla 2053, 2502, 2501, 2504
 */
public class QaEventRoutingSwitchSessionHandler implements IActionConstants {

	public static void switchOn(int routingSwitch, HttpSession session) {
	QaEventRoutingSwitch qaEventRoutingSwitch = null;
		if (session.getAttribute(QA_EVENTS_ENTRY_ROUTING_SWITCH) != null) {
			qaEventRoutingSwitch = (QaEventRoutingSwitch)session.getAttribute(QA_EVENTS_ENTRY_ROUTING_SWITCH);
		} else {
			qaEventRoutingSwitch = new QaEventRoutingSwitch();
		}
		switch (routingSwitch) { 
		case QA_EVENTS_ENTRY_ROUTING_FROM_TEST_MANAGEMENT: 
			qaEventRoutingSwitch.setTestManagementSwitch(true);
			qaEventRoutingSwitch.setSampleTrackingSwitch(false);
			qaEventRoutingSwitch.setResultsEntrySwitch(false);
			qaEventRoutingSwitch.setBatchResultsEntrySwitch(false);
			qaEventRoutingSwitch.setQaEventsEntryLineListingSwitch(false);
			break;
		//bugzilla 2053 should not switch off other switches when RE and BRE are on since this is auto routing
		//and we may need to use previous routing to qa events to return to original page
		case QA_EVENTS_ENTRY_ROUTING_FROM_RESULTS_ENTRY:  
			//qaEventRoutingSwitch.setTestManagementSwitch(false);
			//qaEventRoutingSwitch.setSampleTrackingSwitch(false);
			qaEventRoutingSwitch.setResultsEntrySwitch(true);
			//qaEventRoutingSwitch.setBatchResultsEntrySwitch(false);
			//qaEventRoutingSwitch.setQaEventsEntryLineListingSwitch(false);
			break;
			//should not switch off other swiches since this is auto routing
		case QA_EVENTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_ENTRY:  
			//qaEventRoutingSwitch.setTestManagementSwitch(false);
			//qaEventRoutingSwitch.setSampleTrackingSwitch(false);
			//qaEventRoutingSwitch.setResultsEntrySwitch(false);
			//qaEventRoutingSwitch.setQaEventsEntryLineListingSwitch(false);
			qaEventRoutingSwitch.setBatchResultsEntrySwitch(true);
			break;
		case QA_EVENTS_ENTRY_ROUTING_FROM_SAMPLE_TRACKING:  
			qaEventRoutingSwitch.setTestManagementSwitch(false);
			qaEventRoutingSwitch.setSampleTrackingSwitch(true);
			qaEventRoutingSwitch.setResultsEntrySwitch(false);
			qaEventRoutingSwitch.setBatchResultsEntrySwitch(false);
			qaEventRoutingSwitch.setQaEventsEntryLineListingSwitch(false);
			break;
		case QA_EVENTS_ENTRY_ROUTING_FROM_QAEVENTS_ENTRY_LINELISTING:  
			qaEventRoutingSwitch.setTestManagementSwitch(false);
			qaEventRoutingSwitch.setSampleTrackingSwitch(false);
			qaEventRoutingSwitch.setResultsEntrySwitch(false);
			qaEventRoutingSwitch.setBatchResultsEntrySwitch(false);
			qaEventRoutingSwitch.setQaEventsEntryLineListingSwitch(true);
			break;
		default: //System.out.println("An error occurred with QaEventRoutingSwitchSessionHandler switchOn(" + routingSwitch + ")"); 
		}

		session.setAttribute(QA_EVENTS_ENTRY_ROUTING_SWITCH, qaEventRoutingSwitch);
	}

	public static void switchOff(int routingSwitch, HttpSession session) {
	QaEventRoutingSwitch qaEventRoutingSwitch = null;
		if (session.getAttribute(QA_EVENTS_ENTRY_ROUTING_SWITCH) != null) {
			qaEventRoutingSwitch = (QaEventRoutingSwitch)session.getAttribute(QA_EVENTS_ENTRY_ROUTING_SWITCH);
		} else {
			qaEventRoutingSwitch = new QaEventRoutingSwitch();
		}
		switch (routingSwitch) { 
		case QA_EVENTS_ENTRY_ROUTING_FROM_TEST_MANAGEMENT: 
			qaEventRoutingSwitch.setTestManagementSwitch(false);
			break;
		case QA_EVENTS_ENTRY_ROUTING_FROM_RESULTS_ENTRY:  
			qaEventRoutingSwitch.setResultsEntrySwitch(false);
			break;
		case QA_EVENTS_ENTRY_ROUTING_FROM_SAMPLE_TRACKING:  
			qaEventRoutingSwitch.setSampleTrackingSwitch(false);
			break;
		case QA_EVENTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_ENTRY:  
			qaEventRoutingSwitch.setBatchResultsEntrySwitch(false);
			//also remove session attributes
			//bugzilla 2053
			session.setAttribute(QA_EVENTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_ENTRY_PARAM_TEST_ID, null);
			session.setAttribute(QA_EVENTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_ENTRY_PARAM_ACCESSION_NUMBERS, null);
			break;
		case QA_EVENTS_ENTRY_ROUTING_FROM_QAEVENTS_ENTRY_LINELISTING:  
			qaEventRoutingSwitch.setQaEventsEntryLineListingSwitch(false);
			break;
		default: //System.out.println("An error occurred with QaEventRoutingSwitchSessionHandler switchOff(" + routingSwitch + ")"); 
		}

		session.setAttribute(QA_EVENTS_ENTRY_ROUTING_SWITCH, qaEventRoutingSwitch);
	}

	public static void switchAllOff(HttpSession session) {
	QaEventRoutingSwitch qaEventRoutingSwitch = null;
		if (session.getAttribute(QA_EVENTS_ENTRY_ROUTING_SWITCH) != null) {
			qaEventRoutingSwitch = (QaEventRoutingSwitch)session.getAttribute(QA_EVENTS_ENTRY_ROUTING_SWITCH);
		} else {
			qaEventRoutingSwitch = new QaEventRoutingSwitch();
		}
		qaEventRoutingSwitch.setTestManagementSwitch(false);
		qaEventRoutingSwitch.setResultsEntrySwitch(false);
		qaEventRoutingSwitch.setSampleTrackingSwitch(false);
		qaEventRoutingSwitch.setBatchResultsEntrySwitch(false);
		qaEventRoutingSwitch.setQaEventsEntryLineListingSwitch(false);

		session.setAttribute(QA_EVENTS_ENTRY_ROUTING_SWITCH, qaEventRoutingSwitch);
	}

	public static boolean isSwitchOn(int routingSwitch, HttpSession session){
	QaEventRoutingSwitch qaEventRoutingSwitch = null;
		if (session.getAttribute(QA_EVENTS_ENTRY_ROUTING_SWITCH) != null) {
			qaEventRoutingSwitch = (QaEventRoutingSwitch)session.getAttribute(QA_EVENTS_ENTRY_ROUTING_SWITCH);
		} else {
			return false;
		}
		switch (routingSwitch) { 
		case QA_EVENTS_ENTRY_ROUTING_FROM_TEST_MANAGEMENT: 
			if (qaEventRoutingSwitch.isTestManagementSwitch()) return true;
			else return false;
		case QA_EVENTS_ENTRY_ROUTING_FROM_RESULTS_ENTRY:  
			if (qaEventRoutingSwitch.isResultsEntrySwitch()) return true;
			else return false;
		case QA_EVENTS_ENTRY_ROUTING_FROM_SAMPLE_TRACKING:  
			if (qaEventRoutingSwitch.isSampleTrackingSwitch()) return true;
			else return false;
		case QA_EVENTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_ENTRY:  
			if (qaEventRoutingSwitch.isBatchResultsEntrySwitch()) return true;
			else return false;
		case QA_EVENTS_ENTRY_ROUTING_FROM_QAEVENTS_ENTRY_LINELISTING:  
			if (qaEventRoutingSwitch.isQaEventsEntryLineListingSwitch()) return true;
			else return false;
		default: //System.out.println("An error occurred with QaEventRoutingSwitchSessionHandler isSwitchOn(" + routingSwitch + ")");
			return false;
		}


	}

}
