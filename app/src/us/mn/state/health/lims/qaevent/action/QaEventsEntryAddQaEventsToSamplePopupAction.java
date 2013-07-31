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
package us.mn.state.health.lims.qaevent.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.action.dao.ActionDAO;
import us.mn.state.health.lims.action.daoimpl.ActionDAOImpl;
import us.mn.state.health.lims.action.valueholder.ActionComparator;
import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.qaevent.dao.QaEventDAO;
import us.mn.state.health.lims.qaevent.daoimpl.QaEventDAOImpl;
import us.mn.state.health.lims.qaevent.valueholder.QaEventComparator;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.test.valueholder.TestComparator;

/**
 * @author diane benz
 * bugzilla 2501
 */
public class QaEventsEntryAddQaEventsToSamplePopupAction extends BaseAction {	

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");

		BaseActionForm dynaForm = (BaseActionForm) form;

		// initialize the form
		dynaForm.initialize(mapping);

		Sample sample = new Sample();			
		SampleItem sampleItem = new SampleItem();
		List analyses = new ArrayList();		
	    SampleDAO sampleDAO = new SampleDAOImpl();			
		SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();	
		AnalysisDAO analysisDAO = new AnalysisDAOImpl();
		List tests = new ArrayList();
	
    	//standardize request parameter naming
		String accessionNumber = request.getParameter(ACCESSION_NUMBER);
		
		sample.setAccessionNumber(accessionNumber);		
		sampleDAO.getSampleByAccessionNumber(sample);		
			
		
		QaEventDAO qaEventDAO = new QaEventDAOImpl();
		List qaEvents = new ArrayList();
		qaEvents = qaEventDAO.getAllQaEvents();

				
		Collections.sort(qaEvents, QaEventComparator.NAME_COMPARATOR);
				
		PropertyUtils.setProperty(dynaForm, "SelectList", qaEvents);


		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		return "qaeventsentry.addQaEventToSamplePopup.title";
	}

	protected String getPageSubtitleKey() {
		return "qaeventsentry.addQaEventToSamplePopup.subtitle";
	}

}
