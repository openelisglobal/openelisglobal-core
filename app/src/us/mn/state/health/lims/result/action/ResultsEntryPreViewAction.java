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
package us.mn.state.health.lims.result.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;

/**
 * @author diane benz
 * bugzilla 2053
 */
public class ResultsEntryPreViewAction extends ResultsEntryBaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		BaseActionForm dynaForm = (BaseActionForm) form;
		HttpSession session = request.getSession();

		// bugzilla 1348 depending on where we are coming from (menu or from
		// verification) get accn numb differently
		String accessionNumber = "";
		String testIdFromVerification = "";

		accessionNumber = (String) request
					.getAttribute(ACCESSION_NUMBER);
		if (StringUtil.isNullorNill(accessionNumber)) {
			accessionNumber = (String) dynaForm.get("accessionNumber");
		}

		// server side validation of accessionNumber
		// validate on server-side sample accession number

		ActionMessages errors = null;

		try {
			errors = new ActionMessages();
			errors = validateAccessionNumber(request, errors, dynaForm);
			// System.out.println("Just validated accessionNumber");
		} catch (Exception e) {
    		//bugzilla 2154
			LogEvent.logError("ResultsEntryPreViewAction","performAction()",e.toString());
			ActionError error = new ActionError("errors.ValidationException",
					null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		// System.out.println("This is errors after validation of accn Number "
		// + errors);
		if (errors != null && errors.size() > 0) {
			saveErrors(request, errors);
			// initialize the form but retain the invalid accessionNumber
			dynaForm.initialize(mapping);
			dynaForm.set("accessionNumber", accessionNumber);

			request.setAttribute(ALLOW_EDITS_KEY, "false");

			return mapping.findForward(FWD_FAIL);
		}

		request
				.setAttribute(ACCESSION_NUMBER,
						accessionNumber);

		if (!StringUtil.isNullorNill(accessionNumber)) {
			Sample sample = new Sample();
			SampleDAO sampleDAO = new SampleDAOImpl();
			sample.setAccessionNumber(accessionNumber);

			try {
				sampleDAO.getSampleByAccessionNumber(sample);


				if (!StringUtil.isNullorNill(sample.getStatus()) && sample.getStatus().equals(SystemConfiguration.getInstance()
								.getSampleStatusLabelPrinted())) {
					dynaForm.set("accessionNumber", accessionNumber);
					request.setAttribute(ALLOW_EDITS_KEY,
							"false");
					return mapping.findForward(FWD_FAIL);
				}

			} catch (LIMSRuntimeException lre) {
				// if error then forward to fail and don't update to blank page
				// = false
                //bugzilla 2154
			    LogEvent.logError("ResultsEntryPreViewAction","performAction()",lre.toString());
				errors = new ActionMessages();
				ActionError error = null;
				error = new ActionError("errors.GetException", null, null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
				saveErrors(request, errors);
				request.setAttribute(Globals.ERROR_KEY, errors);
				request.setAttribute(ALLOW_EDITS_KEY, "false");
				return mapping.findForward(FWD_FAIL);

			}

			forward = FWD_SUCCESS;
		}

		return mapping.findForward(forward);
	}

	protected String getPageTitleKeyParameter(HttpServletRequest request,
			ActionForm form) {
		BaseActionForm dynaForm = (BaseActionForm) form;
		String accn = "";
		if (dynaForm.get("accessionNumber") != null) {
			accn = (String) dynaForm.get("accessionNumber");
		}
		return accn;
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "resultsentry.add.title";
		} else {
			return "resultsentry.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "resultsentry.add.subtitle";
		} else {
			return "resultsentry.edit.subtitle";
		}
	}

}
