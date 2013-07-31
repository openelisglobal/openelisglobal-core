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
 * @author diane benz 
 * bugzilla 2504
 */
public class QaEventLineListingRoutingSwitchSessionHandler implements IActionConstants {

	public static void switchOn(int routingSwitch, HttpSession session) {
	QaEventLineListingRoutingSwitch qaEventLineListingRoutingSwitch = null;
		if (session.getAttribute(QA_EVENTS_ENTRY_LINELISTING_ROUTING_SWITCH) != null) {
			qaEventLineListingRoutingSwitch = (QaEventLineListingRoutingSwitch)session.getAttribute(QA_EVENTS_ENTRY_LINELISTING_ROUTING_SWITCH);
		} else {
			qaEventLineListingRoutingSwitch = new QaEventLineListingRoutingSwitch();
		}
		switch (routingSwitch) { 
		case QA_EVENTS_ENTRY_LINELISTING_ROUTING_FROM_QAEVENTS_ENTRY:  
			qaEventLineListingRoutingSwitch.setQaEventsEntrySwitch(true);
			break;
		default: //System.out.println("An error occurred with QaEventLineListingRoutingSwitchSessionHandler switchOn(" + routingSwitch + ")"); 
		}

		session.setAttribute(QA_EVENTS_ENTRY_LINELISTING_ROUTING_SWITCH, qaEventLineListingRoutingSwitch);
	}

	public static void switchOff(int routingSwitch, HttpSession session) {
	QaEventLineListingRoutingSwitch qaEventLineListingRoutingSwitch = null;
		if (session.getAttribute(QA_EVENTS_ENTRY_LINELISTING_ROUTING_SWITCH) != null) {
			qaEventLineListingRoutingSwitch = (QaEventLineListingRoutingSwitch)session.getAttribute(QA_EVENTS_ENTRY_LINELISTING_ROUTING_SWITCH);
		} else {
			qaEventLineListingRoutingSwitch = new QaEventLineListingRoutingSwitch();
		}
		switch (routingSwitch) { 
		case QA_EVENTS_ENTRY_LINELISTING_ROUTING_FROM_QAEVENTS_ENTRY: 
			qaEventLineListingRoutingSwitch.setQaEventsEntrySwitch(false);
			break;
		default: //System.out.println("An error occurred with QaEventLineListingRoutingSwitchSessionHandler switchOff(" + routingSwitch + ")"); 
		}

		session.setAttribute(QA_EVENTS_ENTRY_LINELISTING_ROUTING_SWITCH, qaEventLineListingRoutingSwitch);
	}

	public static void switchAllOff(HttpSession session) {
	QaEventLineListingRoutingSwitch qaEventLineListingRoutingSwitch = null;
		if (session.getAttribute(QA_EVENTS_ENTRY_LINELISTING_ROUTING_SWITCH) != null) {
			qaEventLineListingRoutingSwitch = (QaEventLineListingRoutingSwitch)session.getAttribute(QA_EVENTS_ENTRY_LINELISTING_ROUTING_SWITCH);
		} else {
			qaEventLineListingRoutingSwitch = new QaEventLineListingRoutingSwitch();
		}
		qaEventLineListingRoutingSwitch.setQaEventsEntrySwitch(false);

		session.setAttribute(QA_EVENTS_ENTRY_LINELISTING_ROUTING_SWITCH, qaEventLineListingRoutingSwitch);
	}

	public static boolean isSwitchOn(int routingSwitch, HttpSession session){
	QaEventLineListingRoutingSwitch qaEventLineListingRoutingSwitch = null;
		if (session.getAttribute(QA_EVENTS_ENTRY_LINELISTING_ROUTING_SWITCH) != null) {
			qaEventLineListingRoutingSwitch = (QaEventLineListingRoutingSwitch)session.getAttribute(QA_EVENTS_ENTRY_LINELISTING_ROUTING_SWITCH);
		} else {
			return false;
		}
		switch (routingSwitch) { 
		case QA_EVENTS_ENTRY_LINELISTING_ROUTING_FROM_QAEVENTS_ENTRY: 
			if (qaEventLineListingRoutingSwitch.isQaEventsEntrySwitch()) return true;
			else return false;
		default: //System.out.println("An error occurred with QaEventLineListingRoutingSwitchSessionHandler isSwitchOn(" + routingSwitch + ")");
			return false;
		}


	}

}
