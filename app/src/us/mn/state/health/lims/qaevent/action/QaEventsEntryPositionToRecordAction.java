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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;

/**
 * @author diane benz
 *  bugzilla 2501
 *  This action is executed for 4 different scenarios:
 *  1) NEXT button was pressed (direction = next)
 *  2) PREVIOUS button was pressed (direction = prev
 *  3) A sample within the list of samples with pending qa events was requested: position to this sample
 *  4) No sample was requested but we are in multiple sample mode - get first sample with pending qa events
 *  
 *  Also: set button disabling for NEXT and PREVIOUS
 *        get current record count and total record count
 *        pass accession number as request param to the ViewAction
 * bugzilla 2502
 * bugzilla 2566 adjust for newborn
 */
public class QaEventsEntryPositionToRecordAction extends BaseAction {


	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new Analyte.
		// If there is a parameter present, we should bring up an existing
		// Analyte to edit.
		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "false");
		request.setAttribute(NEXT_DISABLED, "false");

		BaseActionForm dynaForm = (BaseActionForm) form;

		// server side validation of accessionNumber
		ActionMessages errors = null;

		//bugzilla 2501
		HttpSession session = request.getSession();
		String multipleSampleModeFromSessionRouting = (String)session.getAttribute(QAEVENTS_ENTRY_PARAM_MULTIPLE_SAMPLE_MODE);
		String selectedCategoryIdFromRouting = (String)session.getAttribute(QAEVENTS_ENTRY_PARAM_QAEVENT_CATEGORY_ID);
		//bugzilla 2504
		String viewModeFromRouting = (String)session.getAttribute(QAEVENTS_ENTRY_PARAM_VIEW_MODE);
		//bugzilla 2502
	    String fullScreenSectionFromRouting = (String)session.getAttribute(QAEVENTS_ENTRY_FULL_SCREEN_VIEW_SECTION);
		String multipleSampleMode = "";
		String qaEventCategoryId = "";
		//bugzilla 2504
		String viewMode = QAEVENTS_ENTRY_NORMAL_VIEW;
		//bugzilla 2502
		String fullScreenSection = "";
		if (!StringUtil.isNullorNill(multipleSampleModeFromSessionRouting)) {
			multipleSampleMode = multipleSampleModeFromSessionRouting;
			qaEventCategoryId = selectedCategoryIdFromRouting;
			//bugzilla 2504
			if (!StringUtil.isNullorNill(viewModeFromRouting))
              viewMode = viewModeFromRouting;
			if (!StringUtil.isNullorNill(fullScreenSectionFromRouting))
				fullScreenSection = fullScreenSectionFromRouting;
			session.setAttribute(QAEVENTS_ENTRY_PARAM_MULTIPLE_SAMPLE_MODE,
					null);
			session.setAttribute(QAEVENTS_ENTRY_PARAM_QAEVENT_CATEGORY_ID, null);
			//bugzilla 2504
			session.setAttribute(QAEVENTS_ENTRY_PARAM_VIEW_MODE, null);
			session.setAttribute(QAEVENTS_ENTRY_FULL_SCREEN_VIEW_SECTION, null);
		} else {
			multipleSampleMode = (String)dynaForm.get("multipleSampleMode");
			qaEventCategoryId = (String)dynaForm.get("selectedQaEventsCategoryId");
			viewMode = (String)dynaForm.get("viewMode");
			fullScreenSection = (String)dynaForm.get("fullScreenSection");
			
		}

        //for now qa events is for both human and Newborn
		String accessionNumber = request.getParameter(ACCESSION_NUMBER);
		String requestedAccessionNumber = BLANK;
		//the direction is only used for NEXT and PREVIOUS functionality
		String direction = (String) request.getParameter("direction");
		String nextDisabled = FALSE;
		String previousDisabled = FALSE;
		String currentRecord = "0";
		String totalRecords = "0";


		try {
			SampleDAO sampleDAO = new SampleDAOImpl();
			List samples = new ArrayList();
			Sample sample = new Sample();
			sample.setAccessionNumber(accessionNumber);
			//bugzilla 2502 (in full screen mode after bad filter there is no accession number
			if (!StringUtil.isNullorNill(sample.getAccessionNumber())) {
				sampleDAO.getSampleByAccessionNumber(sample);
			}

			if (!StringUtil.isNullorNill(qaEventCategoryId)) {
				samples = sampleDAO.getSamplesWithPendingQaEvents(sample, true, qaEventCategoryId, false);
			} else {
				samples = sampleDAO.getSamplesWithPendingQaEvents(sample, false, null, false);
			}

			if (samples != null && samples.size() > 0) {
				//this is for next, previous and current
				if (sample != null && !StringUtil.isNullorNill(sample.getId())) {
					//NEXT button was pressed
					if (FWD_NEXT.equals(direction)) {
							sample = getNextSampleFromList(samples, sample);
							int position = getPosition(samples, sample);
							if (sample != null && position >= 0) {
								currentRecord = String.valueOf(position + 1);
								totalRecords = String.valueOf(samples.size());
							} else {
								//the sample was not found (why was NEXT not disabled?? Or has this sample
								//been modified to complete qa events?
								sample = (Sample)samples.get(samples.size() - 1);
								accessionNumber = sample.getAccessionNumber();
								position = samples.size();
								currentRecord = String.valueOf(samples.size());
								totalRecords = String.valueOf(samples.size());
							}
							if (position >= (samples.size() - 1)) {
								nextDisabled = TRUE;
							}
							if (position == 0) {
								previousDisabled = TRUE;
							}
							accessionNumber = sample.getAccessionNumber();
					//position to previous record
					} else if (FWD_PREVIOUS.equals(direction)) {
							sample = getPreviousSampleFromList(samples, sample);
							int position = getPosition(samples, sample);
							if (sample != null && position >= 0) {
								currentRecord = String.valueOf(position + 1);
								totalRecords = String.valueOf(samples.size());

							} else {
								//the sample was not found (why was PREVIOUS not disabled?? Or has this sample
								//been modified to complete qa events?
								sample = (Sample)samples.get(0);
								accessionNumber = sample.getAccessionNumber();
								position = 0;
								currentRecord = "1";
								totalRecords = String.valueOf(samples.size());
							}
							if (position >= (samples.size() - 1)) {
								nextDisabled = TRUE;
							}
							if (position == 0) {
								previousDisabled = TRUE;
							}
							accessionNumber = sample.getAccessionNumber();
					//position to current record
					} else if (StringUtil.isNullorNill(direction)) {
						boolean recordNotFound = false;
						requestedAccessionNumber = sample.getAccessionNumber();
						recordNotFound = getCurrentSampleFromList(samples, sample, true);
						int position = getPosition(samples, sample);
						if (sample != null && position >= 0) {
							currentRecord = String.valueOf(position + 1);
							totalRecords = String.valueOf(samples.size());
							if (position >= (samples.size() - 1)) {
								nextDisabled = TRUE;
							}
							if (position == 0) {
								previousDisabled = TRUE;
							}
							accessionNumber = sample.getAccessionNumber();
						}
						if (recordNotFound) {
							  //display a simple warning message
							   	ActionError error = new ActionError("qaeventsentry.message.error.record.not.found.position.to.next",
							   			requestedAccessionNumber, null);
								errors = new ActionMessages();
								errors.add(ActionMessages.GLOBAL_MESSAGE, error);
								saveErrors(request, errors);
								request.setAttribute(Globals.ERROR_KEY, errors);
						}
					}

				} else {
					//position to first record
					sample = (Sample)samples.get(0);
					int position = 0;
					if (sample != null && position >= 0) {
						currentRecord = String.valueOf(position + 1);
						totalRecords = String.valueOf(samples.size());
						if (position > (samples.size() - 1)) {
							nextDisabled = TRUE;
						}
						if (position == 0) {
							previousDisabled = TRUE;
						}
						accessionNumber = sample.getAccessionNumber();
					} 

				}

			} else {
				//samples is null (there are no pending qa events ---> go to empty page
				//display error
				ActionError error = new ActionError("qaeventsentry.message.error.no.data",
						null, null);
				errors = new ActionMessages();
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
				saveErrors(request, errors);
				request.setAttribute(Globals.ERROR_KEY, errors);
			}


			//get multipleSampleMode and selectedQaEventCategoryId from session (in case routing) 
			//and set as form variables for ViewAction
			PropertyUtils.setProperty(dynaForm, "currentCount", currentRecord);
			PropertyUtils.setProperty(dynaForm, "totalCount", totalRecords);
			PropertyUtils.setProperty(dynaForm, "selectedQaEventsCategoryId", qaEventCategoryId);
			PropertyUtils.setProperty(dynaForm, "multipleSampleMode", multipleSampleMode);
			//bugzilla 2502
			PropertyUtils.setProperty(dynaForm, "viewMode", viewMode);
			PropertyUtils.setProperty(dynaForm, "fullScreenSection", fullScreenSection);
			
			if (!StringUtil.isNullorNill(accessionNumber)) {
				request.setAttribute(ACCESSION_NUMBER, accessionNumber);
			}
			
			if (!StringUtil.isNullorNill(requestedAccessionNumber)) {
				request.setAttribute(ACCESSION_NUMBER_REQUESTED, requestedAccessionNumber);
			}
			
			if (!StringUtil.isNullorNill(nextDisabled)) {
				request.setAttribute(NEXT_DISABLED, nextDisabled);
	    	}
			
			if (!StringUtil.isNullorNill(previousDisabled)) {
				request.setAttribute(PREVIOUS_DISABLED, previousDisabled);
	    	}
			
			//bugzilla 2504
			if (!StringUtil.isNullorNill(viewMode)) {
				request.setAttribute(QAEVENTS_ENTRY_PARAM_VIEW_MODE, viewMode);
			}
			
			//bugzilla 2502
			if (!StringUtil.isNullorNill(fullScreenSection)) {
				request.setAttribute(QAEVENTS_ENTRY_FULL_SCREEN_VIEW_SECTION, fullScreenSection);
			}
		} catch (LIMSRuntimeException lre) {
			//bugzilla 2154
			LogEvent.logError("QaEventsEntryPositionToRecordAction","performAction()",lre.toString());				
			request.setAttribute(ALLOW_EDITS_KEY, "false");
			// disable previous and next
			request.setAttribute(PREVIOUS_DISABLED, "true");
			request.setAttribute(NEXT_DISABLED, "true");
			forward = FWD_FAIL;
		} 

		return mapping.findForward(forward);

	}

	protected String getPageTitleKey() {
		return null;
	}

	protected String getPageSubtitleKey() {
		return null;
	}

	protected Sample getNextSampleFromList(List samples, Sample sample) {
		if (samples != null && samples.size() > 0 && sample != null) {
			for (int i = 0; i < samples.size(); i++) {
				Sample sampleInList = (Sample)samples.get(i);
				if (sampleInList.getAccessionNumber().equals(sample.getAccessionNumber())) {
					if (i < samples.size() -1) {
						return (Sample)samples.get(i + 1);
					}
				}
			}
			
			//if we got to here and didn't return a sample then this sample for which we are trying to get NEXT
			//does not exist anymore in list of samples with pending qa events: get next higher accession number
			int accessionNumber = (Integer.valueOf(sample.getAccessionNumber())).intValue();
			for (int i = 0; i < samples.size(); i++) {
				Sample sampleInList = (Sample)samples.get(i);
				int accessionNumberFromList = (Integer.valueOf(sampleInList.getAccessionNumber())).intValue();
				if (accessionNumberFromList > accessionNumber) {
					return (Sample)samples.get(i);
				}
			}
			
			//if we got to here then we are beyond the last sample with qa events
			//now set to last record
			return (Sample)samples.get(samples.size() - 1);
		
		}

		return null;
	}

	protected Sample getPreviousSampleFromList(List samples, Sample sample) {
		if (samples != null && samples.size() > 0 && sample != null) {
			for (int i = 0; i < samples.size(); i++) {
				Sample sampleInList = (Sample)samples.get(i);
				if (sampleInList.getAccessionNumber().equals(sample.getAccessionNumber())) {
					if (i != 0) {
						return (Sample)samples.get(i - 1);
					} 
				}
			}
			
			//if we got to here and didn't return a sample then this sample for which we are trying to get PREVIOUS
			//does not exist anymore in list of samples with pending qa events: get next lower accession number
			int accessionNumber = (Integer.valueOf(sample.getAccessionNumber())).intValue();
			for (int i = samples.size() - 1; i >= 0; i--) {
				Sample sampleInList = (Sample)samples.get(i);
				int accessionNumberFromList = (Integer.valueOf(sampleInList.getAccessionNumber())).intValue();
				if (accessionNumberFromList < accessionNumber) {
					return (Sample)samples.get(i);
				}
			}
			
			//if we got to here then we are beyond the last sample with qa events
			//now set to first record
			return (Sample)samples.get(0);

		}

		return null;
	}

	protected boolean getCurrentSampleFromList(List samples, Sample sample, boolean returnNextInListIfRecordNotFound) {
		boolean recordNotFound = true;
		if (samples != null && samples.size() > 0 && sample != null) {
			for (int i = 0; i < samples.size(); i++) {
				Sample sampleInList = (Sample)samples.get(i);
				if (sampleInList.getAccessionNumber().equals(sample.getAccessionNumber())) {
					recordNotFound = false;
					return recordNotFound;
				}
			}
			//if we got to here then we didn't find the exact sample: get the  next higher accession number
			int accessionNumber = (Integer.valueOf(sample.getAccessionNumber())).intValue();
			for (int i = 0; i < samples.size(); i++) {
				Sample sampleInList = (Sample)samples.get(i);
				int accessionNumberFromList = (Integer.valueOf(sampleInList.getAccessionNumber())).intValue();
				if (accessionNumberFromList > accessionNumber) {
					Sample nextSample = (Sample)samples.get(i);
					try {
						PropertyUtils.copyProperties(sample, nextSample);
					} catch (Exception e) {
						//if this didn't work then say record not found
					}
					return recordNotFound;
				}
			}
			//if we got to here then we are beyond the last sample with qa events
			//now set to last record
			Sample lastSample = (Sample)samples.get(samples.size() - 1);
			try {
				PropertyUtils.copyProperties(sample, lastSample);
			} catch (Exception e) {
				//if this didn't work then say record not found
			}
		}

		return recordNotFound;
	}

	protected int getPosition(List samples, Sample sample) {
		int position = -1;
		if (samples != null && samples.size() > 0 && sample != null) {
			for (int i = 0; i < samples.size(); i++) {
				Sample sampleInList = (Sample)samples.get(i);
				if (sampleInList.getAccessionNumber().equals(sample.getAccessionNumber())) {
					position = i;
					break;
				}
			}
		}

		return position;
	}
	
}