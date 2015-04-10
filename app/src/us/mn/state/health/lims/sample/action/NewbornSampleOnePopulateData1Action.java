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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.resources.ResourceLocator;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.samplepdf.dao.SamplePdfDAO;
import us.mn.state.health.lims.samplepdf.daoimpl.SamplePdfDAOImpl;
import us.mn.state.health.lims.samplepdf.valueholder.SamplePdf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

public class NewbornSampleOnePopulateData1Action extends BaseAction {

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");

		BaseActionForm dynaForm = (BaseActionForm) form;

		Date today = Calendar.getInstance().getTime();
		String dateAsText = DateUtil.formatDateAsText(today);
		PropertyUtils.setProperty(form, "currentDate", dateAsText);
		
		//populate barcode
		String accessionNumber = (String) dynaForm.get("accessionNumber"); 
		if ( (accessionNumber != null) && (accessionNumber.length()>0) ) {
			SamplePdfDAO samplePdfDAO = new SamplePdfDAOImpl();
			SamplePdf samplePdf = new SamplePdf();
			samplePdf.setAccessionNumber(accessionNumber);
			samplePdf = samplePdfDAO.getSamplePdfByAccessionNumber(samplePdf);
			dynaForm.set("accessionNumber", accessionNumber);
			dynaForm.set("barcode", samplePdf.getBarcode());
			request.setAttribute("preAccessionNumber", accessionNumber);
			SampleDAO sampleDAO = new SampleDAOImpl();
			Sample sample = sampleDAO.getSampleByAccessionNumber(accessionNumber);
			//dynaForm.set("newbornDomain", sample.getDomain());
			PropertyUtils.setProperty(form, "newbornDomain", sample.getDomain());
		}
		
		prepareOptionList(dynaForm,request);
		
		request.setAttribute("menuDefinition", "NewbornSampleOneDefinition");
		return mapping.findForward(forward);
	}

    private void prepareOptionList( BaseActionForm dynaForm, HttpServletRequest request ) throws Exception {
		Locale locale = (Locale) request.getSession().getAttribute("org.apache.struts.action.LOCALE");
    	Vector optionList = new Vector();
    	
    	String gram = ResourceLocator.getInstance().getMessageResources().getMessage(locale,"page.default.gram.option");	
    	String pound = ResourceLocator.getInstance().getMessageResources().getMessage(locale,"page.default.pound.option");
        optionList.add(new LabelValueBean(gram,gram));
        optionList.add(new LabelValueBean(pound,pound));
        dynaForm.set("selectedBirthWeight",gram);
        PropertyUtils.setProperty(dynaForm,"birthWeightList", optionList);       
    }
	
	protected String getPageTitleKey() {
		return "newborn.sample.one.edit.title";
	}

	protected String getPageSubtitleKey() {
		return "newborn.sample.one.edit.title";
	}

}
