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
import us.mn.state.health.lims.analysisqaevent.dao.AnalysisQaEventDAO;
import us.mn.state.health.lims.analysisqaevent.daoimpl.AnalysisQaEventDAOImpl;
import us.mn.state.health.lims.analysisqaevent.valueholder.AnalysisQaEvent;
import us.mn.state.health.lims.analysisqaevent.valueholder.AnalysisQaEventComparator;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;

/**
 * @author diane benz
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class QaEventsEntryAddActionsToQaEventsPopupAction extends BaseAction {	

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		
		// bugzilla 2503
		String filterString=(String) request
       .getParameter("filterString");
		 
		String doingSearch=(String)request
       .getParameter("search");
		
		BaseActionForm dynaForm = (BaseActionForm) form;

		// initialize the form
		dynaForm.initialize(mapping);

    	Sample sample = new Sample();			
		SampleItem sampleItem = new SampleItem();
		List analyses = new ArrayList();		
	    SampleDAO sampleDAO = new SampleDAOImpl();			
		SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();	
		AnalysisDAO analysisDAO = new AnalysisDAOImpl();
		AnalysisQaEventDAO analysisQaEventDAO = new AnalysisQaEventDAOImpl();
		
		List analysisQaEvents = new ArrayList();
	    List totalAnalysisQaEvents = new ArrayList();
		
		//standardize request parameter naming
		String accessionNumber = request.getParameter(ACCESSION_NUMBER);
		
		sample.setAccessionNumber(accessionNumber);		
		sampleDAO.getSampleByAccessionNumber(sample);		
			
		if (!StringUtil.isNullorNill(sample.getId())) {			
			sampleItem.setSample(sample);
			sampleItemDAO.getDataBySample(sampleItem);	
			if (sampleItem.getId() != null ){
				//bugzilla 2227
				analyses = analysisDAO.getMaxRevisionAnalysesBySample(sampleItem);				
			}
		}
		
		if (analyses != null) {
			// there is one Analysis per Test			
			for (int i = 0; i < analyses.size(); i++) {				
				Analysis analysis = (Analysis) analyses.get(i);	
				AnalysisQaEvent analysisQaEvent = new AnalysisQaEvent();
				analysisQaEvent.setAnalysis(analysis);
				analysisQaEvents = analysisQaEventDAO.getAnalysisQaEventsByAnalysis(analysisQaEvent);
				
				for (int j = 0; j < analysisQaEvents.size(); j++) {
					AnalysisQaEvent analQaEvent = (AnalysisQaEvent)analysisQaEvents.get(j);
					if (analQaEvent.getCompletedDate() == null) {
						//pick ones with null completed date
						totalAnalysisQaEvents.add(analQaEvent);
					}
				}

			}
		}
		
		ActionDAO actionDAO = new ActionDAOImpl();
		List actions = new ArrayList();
		//bugzilla 2503
		if (!StringUtil.isNullorNill(doingSearch) && doingSearch.equals(YES))
			actions = actionDAO.getAllActionsByFilter (filterString);
		else
		    actions = actionDAO.getAllActions();
		
    	Collections.sort(totalAnalysisQaEvents, AnalysisQaEventComparator.NAME_COMPARATOR);
		Collections.sort(actions, ActionComparator.NAME_COMPARATOR);

				
		PropertyUtils.setProperty(dynaForm, "SelectList", actions);
		PropertyUtils.setProperty(dynaForm, "PickList", totalAnalysisQaEvents);
		
		 //bugzilla 2503
		request.setAttribute(POPUPFORM_FILTER_BY_TABLE_COLUMN, "action.description");

		if (!StringUtil.isNullorNill(doingSearch) && doingSearch.equals(YES) ) {
			 
			   request.setAttribute(IN_POPUP_FORM_SEARCH, "true");
			   
			   request.setAttribute(POPUP_FORM_SEARCH_STRING, filterString );
		}
			
		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		return "qaeventsentry.addActionToQaEventsPopup.title";
	}

	protected String getPageSubtitleKey() {
		return "qaeventsentry.addActionToQaEventsPopup.subtitle";
	}

}
