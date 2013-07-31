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
package us.mn.state.health.lims.test.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.label.dao.LabelDAO;
import us.mn.state.health.lims.label.daoimpl.LabelDAOImpl;
import us.mn.state.health.lims.label.valueholder.LabelComparator;
import us.mn.state.health.lims.scriptlet.dao.ScriptletDAO;
import us.mn.state.health.lims.scriptlet.daoimpl.ScriptletDAOImpl;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class TestAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new Test.
		// If there is a parameter present, we should bring up an existing
		// Test to edit.

		String id = request.getParameter(ID);

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		BaseActionForm dynaForm = (BaseActionForm) form;
		//AIS - bugzilla 1562
		List scriptlets = new ArrayList();
		ScriptletDAO scriptletDAO = new ScriptletDAOImpl();
		scriptlets = scriptletDAO.getAllScriptlets();	
		
		List labels = new ArrayList();
		LabelDAO labelDAO = new LabelDAOImpl();
		labels = labelDAO.getAllLabels();

		// initialize the form
		dynaForm.initialize(mapping);

		Test test = new Test();

		if ((id != null) && (!"0".equals(id))) { // this is an existing
			// test

			test.setId(id);
			TestDAO testDAO = new TestDAOImpl();
			testDAO.getData(test);

			// initialize methodName
			try {
				if (test.getMethod() != null) {
					test.setMethodName(test.getMethod().getMethodName());
				}
			} catch(org.hibernate.ObjectNotFoundException onfe) {
    			//bugzilla 2154
			    LogEvent.logError("TestAction","performAction()",onfe.toString());
				//Null object doesn't work
			}	
			
			// initialize labelName
			try {
				if ( test.getLabel() != null ) {
					test.setLabelName(test.getLabel().getLabelName());
				}
			} catch(org.hibernate.ObjectNotFoundException onfe) {
    			//bugzilla 2154
			    LogEvent.logError("TestAction","performAction()",onfe.toString());
				//Null object doesn't work
			}
				
			// initialize testTrailerName
			try {
				if (test.getTestTrailer() != null) {
					test.setTestTrailerName(test.getTestTrailer().getTestTrailerName());
				}
			} catch(org.hibernate.ObjectNotFoundException onfe) {
    			//bugzilla 2154
			    LogEvent.logError("TestAction","performAction()",onfe.toString());
				//Null object doesn't work
			}
			
			// initialize testSectionName
			try {
				if (test.getTestSection() != null) {
					test.setTestSectionName(test.getTestSection().getTestSectionName());
				}
			} catch(org.hibernate.ObjectNotFoundException onfe) {
    			//bugzilla 2154
			    LogEvent.logError("TestAction","performAction()",onfe.toString());
				//Null object doesn't work
			}
			
			// initialize scriptletName
			try {
				if (test.getScriptlet() != null) {
					test.setScriptletName(test.getScriptlet().getScriptletName());
				}
			} catch(org.hibernate.ObjectNotFoundException onfe) {
    			//bugzilla 2154
			    LogEvent.logError("TestAction","performAction()",onfe.toString());
				//Null object doesn't work
			}
			
			isNew = false; // this is to set correct page title

			// do we need to enable next or previous?
			List tests = testDAO.getNextTestRecord(test.getId());
			if (tests.size() > 0) {
				// enable next button
				request.setAttribute(NEXT_DISABLED, "false");
			}
			tests = testDAO.getPreviousTestRecord(test.getId());
			if (tests.size() > 0) {
				// enable next button
				request.setAttribute(PREVIOUS_DISABLED, "false");
			}
			// end of logic to enable next or previous button

		} else { // this is a new test

			// bugzilla 1401 default isActive to 'Y'
			test.setIsActive(YES);
			//bugzilla 1784
			test.setIsReportable(YES);
			isNew = true; // this is to set correct page title

		}

		if (test.getId() != null && !test.getId().equals("0")) {
			request.setAttribute(ID, test.getId());
		}

		// populate form from valueholder
		//AIS - bugzilla 1562		
		Collections.sort(labels, LabelComparator.NAME_COMPARATOR);		
		PropertyUtils.setProperty(form, "scriptlets", scriptlets);
		PropertyUtils.setProperty(form, "labels", labels);
		
		PropertyUtils.copyProperties(dynaForm, test);

		//System.out.println("I am in TestAction this is forward " + forward);
		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "test.add.title";
		} else {
			return "test.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "test.add.title";
		} else {
			return "test.edit.title";
		}
	}

}
