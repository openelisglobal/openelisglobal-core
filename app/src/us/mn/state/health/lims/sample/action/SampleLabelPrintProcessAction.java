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
package us.mn.state.health.lims.sample.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSInvalidPrinterException;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.provider.reports.SampleLabelPrintProvider;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.valueholder.UserSessionData;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 *
 * bugzilla 2167: added message indicating which accession numbers had their label printed
 */
public class SampleLabelPrintProcessAction extends BaseAction {


	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new Sample.
		// If there is a parameter present, we should bring up an existing
		// Sample to edit.
		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");


		BaseActionForm dynaForm = (BaseActionForm) form;


		String numberOfSamples	= (String)dynaForm.get("numberOfSamples");


		int count = 0;
		try {
			count = Integer.parseInt(numberOfSamples);
		} catch (NumberFormatException nfe) {
			//bugzilla 2154
			LogEvent.logError("SampleLabelPrintProcessAction","performAction()",nfe.toString());			
		}

//		server-side validation (validation.xml)
		ActionMessages errors = dynaForm.validate(mapping, request);

		//bugzilla 2374 limit number of labels
		int maxNumberOfLabels = Integer.parseInt(SystemConfiguration.getInstance().getMaxNumberOfLabels());
		if (count > maxNumberOfLabels) {
			ActionError error = new ActionError("errors.labelprint.exceeded.maxnumber",
					SystemConfiguration.getInstance().getMaxNumberOfLabels(), null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}

		if (errors != null && errors.size() > 0) {
			saveErrors(request, errors);
			// since we forward to jsp - not Action we don't need to repopulate
			// the lists here
			return mapping.findForward(FWD_FAIL);
		}

		// initialize the form
		dynaForm.initialize(mapping);

		SampleLabelPrintProvider printProvider = new SampleLabelPrintProvider();
		Map parms = new HashMap();
		List listOfAccessionNumbers = new ArrayList();
		SampleDAO sampleDAO = new SampleDAOImpl();
		Sample sample = new Sample();
		Date today = Calendar.getInstance().getTime();
		Locale locale = (Locale) request.getSession().getAttribute(
		"org.apache.struts.action.LOCALE");

		String dateAsText = DateUtil.formatDateAsText(today, locale);

		sample.setReceivedDateForDisplay(dateAsText);
		sample.setEnteredDateForDisplay(dateAsText);
//		bgm - bugzilla 1586 remove setting collection date here in quick entry.
		// this will be set in HSE1.
		//sample.setCollectionDateForDisplay(dateAsText);

		//1926 get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());
		//bgm added to see if this will fix the error for a record lock when this Sample is added here and
		// then updated later,within same thread, on another screen.... like in QuickEntry.
		Transaction tx = HibernateUtil.getSession().beginTransaction();

		try{
//			bgm - bugzilla 1568 added sample.setStatus for lable printed.
			for (int i = 0; i < count; i++) {
				sample.setStatus(SystemConfiguration.getInstance().getSampleStatusLabelPrinted());
				//bugzilla 1926
				sample.setSysUserId(sysUserId);
				sampleDAO.insertData(sample);
				sampleDAO.getData(sample);

				//String stringToPrint = "^XA^FO5,40^BY3^BCN,100,Y,N,N^FD" + sample.getAccessionNumber() + "^FS^XZ\n";

				String accessionNumber = sample.getAccessionNumber();
				parms.put("Accession_Number", accessionNumber);
				listOfAccessionNumbers.add(accessionNumber);
				//parms.put("Accession_Number", stringToPrint);

                //bugzilla 2380
				if (!SystemConfiguration.getInstance().getLabelPrinterName().equalsIgnoreCase(NO_LABEL_PRINTING) && !SystemConfiguration.getInstance().getLabelPrinterName().equals(BLANK)) {
					printProvider.processRequest(parms, request, response);
				} 
			}

			tx.commit();

			//if it didn't fail then populate the accessionNumbersPrinted form field
			String accessionNumbersPrinted = "";
			String accessionNumberPrinted = "";
			//bugzilla 2380
			String accessionNumbersGenerated = "";
			String accessionNumberGenerated = "";

			if (!SystemConfiguration.getInstance().getLabelPrinterName().equalsIgnoreCase(NO_LABEL_PRINTING) && !SystemConfiguration.getInstance().getLabelPrinterName().equals(BLANK)) {
				if (listOfAccessionNumbers != null && listOfAccessionNumbers.size() > 0) {
					if (listOfAccessionNumbers.size() > 1) {
						accessionNumbersPrinted = (String)listOfAccessionNumbers.get(0) + " - " + (String)listOfAccessionNumbers.get(listOfAccessionNumbers.size()-1);
					} else {
						accessionNumberPrinted = (String)listOfAccessionNumbers.get(0);
					}

				}
			//bugzilla 2380
			} else {
				if (listOfAccessionNumbers != null && listOfAccessionNumbers.size() > 0) {
					if (listOfAccessionNumbers.size() > 1) {
						accessionNumbersGenerated = (String)listOfAccessionNumbers.get(0) + " - " + (String)listOfAccessionNumbers.get(listOfAccessionNumbers.size()-1);
					} else {
						accessionNumberGenerated = (String)listOfAccessionNumbers.get(0);
					}

				}	
			}

			PropertyUtils.setProperty(form, "accessionNumbersPrinted", accessionNumbersPrinted);
			PropertyUtils.setProperty(form, "accessionNumberPrinted", accessionNumberPrinted);
			//bugzilla 2380
			PropertyUtils.setProperty(form, "accessionNumbersGenerated", accessionNumbersGenerated);
			PropertyUtils.setProperty(form, "accessionNumberGenerated", accessionNumberGenerated);


		}catch(LIMSRuntimeException lre){
			//bugzilla 2154
			LogEvent.logError("SampleLabelPrintProcessAction","performAction()",lre.toString());	
			tx.rollback();



			errors = new ActionMessages();
			ActionError error = null;
			if (lre.getException() instanceof StaleObjectStateException)
			{
				error = new ActionError("errors.OptimisticLockException", null,	null);
			}
			//bugzilla 2380
			else if (lre.getException() instanceof LIMSInvalidPrinterException)
			{
				error = new ActionError("errors.labelprint.invalidprinter", SystemConfiguration.getInstance().getLabelPrinterName(),lre.getException().getMessage(), null);
			}
			else 
			{
				//bugzilla 2154
				LogEvent.logError("SampleLabelPrintProcessAction","performAction()",lre.toString());	
				error = new ActionError("errors.labelprint.general", null, null);
			}
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute(ALLOW_EDITS_KEY, "false");
			return mapping.findForward(FWD_FAIL);

		}finally{
			HibernateUtil.closeSession();
		}

		return mapping.findForward(forward);


	}

	protected String getPageTitleKey() {
		return "sample.label.print.title";
	}

	protected String getPageSubtitleKey() {
		return "sample.label.print.title";
	}

}