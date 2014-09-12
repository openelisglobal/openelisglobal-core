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
package us.mn.state.health.lims.reports.send.sample.action.influenza;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Marshaller;
import org.xml.sax.InputSource;
import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSCannotCreateXMLException;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.provider.validation.AccessionNumberValidationProvider;
import us.mn.state.health.lims.common.services.TestService;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.util.resources.ResourceLocator;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.valueholder.UserSessionData;
import us.mn.state.health.lims.reports.send.sample.valueholder.influenza.BatchMessageXmit;
import us.mn.state.health.lims.reports.send.sample.valueholder.influenza.InfluenzaSampleXMLBySampleHelper;
import us.mn.state.health.lims.reports.send.sample.valueholder.influenza.MessageXmit;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;


/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 * bugzilla 2393
 * bugzilla 2437 modified to handle batch xml (using InfluenzaSampleXMLBySampleHelper)
 * This validates input, and filters samples down to meet selection criteria, 
 * The Helper program generates XML for each sample eligible for reporting
 */
public class InfluenzaSampleXMLBySampleProcessAction extends BaseAction {
	
	InputStream propertyStream = null;
	
	Properties transmissionMap = null;
	
	String xmlString = null;
	
	private static final String FILENAME_PREFIX = "PHLIP_FLU_v1.0_";
	private static final String HL7_INFLUENZA_TEST_DESCRIPTION = "influenza";
	private static final String HL7_INFLUENZA_TEST_NAME = "5300";
	
	
	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String forward = FWD_SUCCESS;
		BaseActionForm dynaForm = (BaseActionForm) form;
		ActionMessages errors = null;
		
		request.setAttribute(ALLOW_EDITS_KEY, "false");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");
		
		// get transmission resources properties
		ResourceLocator rl = ResourceLocator.getInstance();
		// Now load a java.util.Properties object with the
		// properties
		transmissionMap = new Properties();
		try {
			propertyStream = rl
			.getNamedResourceAsInputStream(ResourceLocator.XMIT_PROPERTIES);
			
			transmissionMap.load(propertyStream);
		} catch (IOException e) {
			//bugzilla 2154
			LogEvent.logError("InfluenzaSampleXMLBySampleProcessAction","performAction()",e.toString());
			throw new LIMSRuntimeException(
					"Unable to load transmission resource mappings.", e);
		} finally {
			if (null != propertyStream) {
				try {
					propertyStream.close();
					propertyStream = null;
				} catch (Exception e) {
					//bugzilla 2154
					LogEvent.logError("InfluenzaSampleXMLBySampleProcessAction","performAction()",e.toString());
				}
			}
		}
		
		String byDateRange = (String) dynaForm.get("byDateRange");
		String bySampleRange = (String) dynaForm.get("bySampleRange");
		String bySample = (String) dynaForm.get("bySample");
		
		String fromAccessionNumber = "";
		String toAccessionNumber = "";
		
		String accessionNumber1 = "";
		String accessionNumber2 = "";
		String accessionNumber3 = "";
		String accessionNumber4 = "";
		String accessionNumber5 = "";
		String accessionNumber6 = "";
		String accessionNumber7 = "";
		String accessionNumber8 = "";
		String accessionNumber9 = "";
		
		String fromReleasedDateForDisplay = "";
		String toReleasedDateForDisplay = "";
		
		
		String formName = dynaForm.getDynaClass().getName().toString();
		
		SampleDAO sampleDAO = new SampleDAOImpl();
		SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
		AnalysisDAO analysisDAO = new AnalysisDAOImpl();
		String humanDomain = SystemConfiguration.getInstance().getHumanDomain();
		List sampleStatuses = new ArrayList();
		
		List samplesUnfiltered = new ArrayList();
		List samples = new ArrayList();
		boolean eligibleForReporting = false;
		List analyses = null;
		
		Map testLevelCriteriaMap = null;
		
		// server-side validation (validation.xml)
		//this should take care of released date validation
		//only do this if released date range download is selected since nothing else is validated
		//through validation.xml
		errors = dynaForm.validate(mapping, request);		
		if (errors != null && errors.size() > 0) {
			saveErrors(request, errors);
			return mapping.findForward(FWD_FAIL);
		}

		//BY DATE RANGE - get samples that meet selection criteria
		if (byDateRange.equals(TRUE)) {
			
			
			fromReleasedDateForDisplay = (String)dynaForm.getString("fromReleasedDateForDisplay");
			toReleasedDateForDisplay = (String)dynaForm.getString("toReleasedDateForDisplay");
			
			String messageKey = "error.sample.xml.by.sample.flu.begindate.lessthan.enddate";
			if (StringUtil.isNullorNill(fromReleasedDateForDisplay) || StringUtil.isNullorNill(toReleasedDateForDisplay)) {
				ActionError error = new ActionError("error.sample.xml.by.sample.flu.begindate.lessthan.enddate",
						null, null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			}
			
			String locale = SystemConfiguration.getInstance()
			.getDefaultLocale().toString();
			java.sql.Date convertedFromReleasedDate = DateUtil
			.convertStringDateToSqlDate(fromReleasedDateForDisplay, locale);	
			java.sql.Date convertedToReleasedDate = DateUtil
			.convertStringDateToSqlDate(toReleasedDateForDisplay, locale);		        
			
			
			sampleStatuses.add(SystemConfiguration.getInstance().getSampleStatusEntry2Complete());
			sampleStatuses.add(SystemConfiguration.getInstance().getSampleStatusReleased());
			samplesUnfiltered = sampleDAO.getSamplesByStatusAndDomain(sampleStatuses, humanDomain);
			
			analyses = null;
			
			for (int j = 0; j < samplesUnfiltered.size(); j++) {
				Sample sample = (Sample)samplesUnfiltered.get(j);
				SampleItem sampleItem = new SampleItem();
				sampleItem.setSample(sample);
				sampleItemDAO.getDataBySample(sampleItem);
				analyses = analysisDAO.getMaxRevisionAnalysesBySample(sampleItem);
				
				eligibleForReporting = false;
				//find at least one eligible test to report on
				for (int i = 0; i < analyses.size(); i++) {
					Analysis analysis = (Analysis) analyses.get(i);
					
					//only process influenza type samples
					if (!TestService.getLocalizedTestNameWithType( analysis.getTest() ).toLowerCase().startsWith(HL7_INFLUENZA_TEST_DESCRIPTION) && !TestService.getUserLocalizedTestName( analysis.getTest() ).equals( HL7_INFLUENZA_TEST_NAME ) ) {
						continue;
					}
					
					//analysis must be released
					if (!analysis.getStatus().equals(SystemConfiguration.getInstance().getAnalysisStatusReleased())) {
						continue;
					}
					//NOW FILTER ON ANALYSIS RELEASED DATE
					if (!analysis.getReleasedDate().before(convertedFromReleasedDate) && !analysis.getReleasedDate().after(convertedToReleasedDate)) {
						eligibleForReporting = true;
					}
				}
				if (eligibleForReporting) 
					samples.add(sample);
			}
			testLevelCriteriaMap = new HashMap();
			testLevelCriteriaMap.put("fromReleasedDate", convertedFromReleasedDate);
			testLevelCriteriaMap.put("toReleasedDate", convertedToReleasedDate);
			
		}
		
		//BY SAMPLE RANGE - get samples that meet selection criteria
		if (bySampleRange.equals(TRUE)) {
			fromAccessionNumber = (String) dynaForm.get("fromAccessionNumber");
			toAccessionNumber = (String) dynaForm.get("toAccessionNumber");
			
			
			if (!StringUtil.isNullorNill(fromAccessionNumber)) {
				errors = validateAccessionNumber(request, errors, fromAccessionNumber, formName);
			}
			if (!StringUtil.isNullorNill(toAccessionNumber)) {
				errors = validateAccessionNumber(request, errors, fromAccessionNumber, formName);
			}
			
			
			String messageKey = "sample.accessionNumber";
			if (!StringUtil.isNullorNill(fromAccessionNumber) && !StringUtil.isNullorNill(toAccessionNumber)) {
				int fromInt = Integer.parseInt(fromAccessionNumber);
				int thruInt = Integer.parseInt(toAccessionNumber);
				
				
				if (fromInt > thruInt) {
					ActionError error = new ActionError("errors.range.accessionnumber.from.less.to",
							getMessageForKey(messageKey), null);
					errors.add(ActionMessages.GLOBAL_MESSAGE, error);
				}
			} else {
				ActionError error = new ActionError("errors.invalid",
						getMessageForKey(messageKey), null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			}
			
			if (errors != null && errors.size() > 0) {
				saveErrors(request, errors);
				request.setAttribute(ALLOW_EDITS_KEY, "false");
				
				return mapping.findForward(FWD_FAIL);
			}
			
			List accessionNumbers = populateAccessionNumberList(fromAccessionNumber,
					toAccessionNumber);
			samplesUnfiltered = createSampleObjectsFromAccessionNumbers(accessionNumbers);
			
			analyses = null;
			
			for (int j = 0; j < samplesUnfiltered.size(); j++) {
				Sample sample = (Sample)samplesUnfiltered.get(j);
				sampleDAO.getSampleByAccessionNumber(sample);
				if (sample.getDomain().equals(humanDomain) && sample.getStatus().equals(SystemConfiguration.getInstance().getSampleStatusEntry2Complete()) || sample.getStatus().equals(SystemConfiguration.getInstance().getSampleStatusReleased())) {
					SampleItem sampleItem = new SampleItem();
					sampleItem.setSample(sample);
					sampleItemDAO.getDataBySample(sampleItem);
					analyses = analysisDAO.getMaxRevisionAnalysesBySample(sampleItem);
					
					eligibleForReporting = false;
					//find at least one eligible test to report on
					for (int i = 0; i < analyses.size(); i++) {
						Analysis analysis = (Analysis) analyses.get(i);
						
						//only process influenza type samples
						if (!TestService.getLocalizedTestNameWithType( analysis.getTest() ).toLowerCase().startsWith(HL7_INFLUENZA_TEST_DESCRIPTION) && !TestService.getUserLocalizedTestName( analysis.getTest() ).equals(HL7_INFLUENZA_TEST_NAME)) {
							continue;
						}
						
						//analysis must be released
						if (!analysis.getStatus().equals(SystemConfiguration.getInstance().getAnalysisStatusReleased())) {
							continue;
						}
						eligibleForReporting = true;
					}
				}
				if (eligibleForReporting) 
					samples.add(sample);
			}
			
		}
		
		//BY SAMPLE - get samples that meet selection criteria
		if (bySample.equals(TRUE)) {
			accessionNumber1 = (String) dynaForm.get("accessionNumber1");
			accessionNumber2 = (String) dynaForm.get("accessionNumber2");
			accessionNumber3 = (String) dynaForm.get("accessionNumber3");
			accessionNumber4 = (String) dynaForm.get("accessionNumber4");
			accessionNumber5 = (String) dynaForm.get("accessionNumber5");
			accessionNumber6 = (String) dynaForm.get("accessionNumber6");
			accessionNumber7 = (String) dynaForm.get("accessionNumber7");
			accessionNumber8 = (String) dynaForm.get("accessionNumber8");
			accessionNumber9 = (String) dynaForm.get("accessionNumber9");
			
			List accessionNumbers = new ArrayList();
			if (!StringUtil.isNullorNill(accessionNumber1)) {
				errors = validateAccessionNumber(request, errors, accessionNumber1, formName);
				accessionNumbers.add(accessionNumber1);
			}
			if (!StringUtil.isNullorNill(accessionNumber2)) {
				errors = validateAccessionNumber(request, errors, accessionNumber2, formName);
				accessionNumbers.add(accessionNumber2);
			}
			if (!StringUtil.isNullorNill(accessionNumber3)) {
				errors = validateAccessionNumber(request, errors, accessionNumber3, formName);
				accessionNumbers.add(accessionNumber3);
			}
			if (!StringUtil.isNullorNill(accessionNumber4)) {
				errors = validateAccessionNumber(request, errors, accessionNumber4, formName);
				accessionNumbers.add(accessionNumber4);
			}			
			if (!StringUtil.isNullorNill(accessionNumber5)) {
				errors = validateAccessionNumber(request, errors, accessionNumber5, formName);
				accessionNumbers.add(accessionNumber5);
			}
			if (!StringUtil.isNullorNill(accessionNumber6)) {
				errors = validateAccessionNumber(request, errors, accessionNumber6, formName);
				accessionNumbers.add(accessionNumber6);
			}
			if (!StringUtil.isNullorNill(accessionNumber7)) {
				errors = validateAccessionNumber(request, errors, accessionNumber7, formName);
				accessionNumbers.add(accessionNumber7);
			}
			if (!StringUtil.isNullorNill(accessionNumber8)) {
				errors = validateAccessionNumber(request, errors, accessionNumber8, formName);
				accessionNumbers.add(accessionNumber8);
			}
			if (!StringUtil.isNullorNill(accessionNumber9)) {
				errors = validateAccessionNumber(request, errors, accessionNumber9, formName);
				accessionNumbers.add(accessionNumber9);
			}
			
			if (errors != null && errors.size() > 0) {
				saveErrors(request, errors);
				request.setAttribute(ALLOW_EDITS_KEY, "false");
				
				return mapping.findForward(FWD_FAIL);
			}
			samplesUnfiltered = createSampleObjectsFromAccessionNumbers(accessionNumbers);
			
			analyses = null;
			
			for (int j = 0; j < samplesUnfiltered.size(); j++) {
				Sample sample = (Sample)samplesUnfiltered.get(j);
				sampleDAO.getSampleByAccessionNumber(sample);
				if (sample.getDomain().equals(humanDomain) && sample.getStatus().equals(SystemConfiguration.getInstance().getSampleStatusEntry2Complete()) || sample.getStatus().equals(SystemConfiguration.getInstance().getSampleStatusReleased())) {
					SampleItem sampleItem = new SampleItem();
					sampleItem.setSample(sample);
					sampleItemDAO.getDataBySample(sampleItem);
					analyses = analysisDAO.getMaxRevisionAnalysesBySample(sampleItem);
					
					eligibleForReporting = false;
					//find at least one eligible test to report on
					for (int i = 0; i < analyses.size(); i++) {
						Analysis analysis = (Analysis) analyses.get(i);
						
						//only process influenza type samples
						if (!TestService.getLocalizedTestNameWithType( analysis.getTest() ).toLowerCase().startsWith(HL7_INFLUENZA_TEST_DESCRIPTION) && !TestService.getUserLocalizedTestName( analysis.getTest() ).equals(HL7_INFLUENZA_TEST_NAME)) {
							continue;
						}
						
						//analysis must be released
						if (!analysis.getStatus().equals(SystemConfiguration.getInstance().getAnalysisStatusReleased())) {
							continue;
						}
						eligibleForReporting = true;
					}
				}
				if (eligibleForReporting) 
					samples.add(sample);
			}

		}
		
		
		// initialize the form
		dynaForm.initialize(mapping);
		
		// 1926 get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());
		
		org.hibernate.Transaction tx = HibernateUtil.getSession()
		.beginTransaction();
		
		try {
			
			
			// marshall to XML
			Mapping castorMapping = new Mapping();
			
			String castorMappingName = transmissionMap
			.getProperty("SampleToXmlInfluenzaMapping");
			
			InputSource source = getSource(castorMappingName);
			
			InfluenzaSampleXMLBySampleHelper helper = new InfluenzaSampleXMLBySampleHelper();
			List messages = new ArrayList();
			BatchMessageXmit batchMessage = new BatchMessageXmit();
			for (int i = 0; i < samples.size(); i++) {
				Sample sample = (Sample)samples.get(i);
				MessageXmit message = helper.getXMLMessage(sample, testLevelCriteriaMap);
				if (message != null) {
					messages.add(message);
				}
			}
			
			if (messages != null && messages.size() > 1) {
				batchMessage.setMessages((ArrayList)messages);
			} else {
				batchMessage = null;
			}
			
			//if there is no message or batchmessage then create Exception
			if (batchMessage == null && (messages == null || messages.size() == 0 || (messages.get(0) == null))) {
				Exception e = new LIMSCannotCreateXMLException(
				"Cannot generate XML for selection");
				
				throw new LIMSRuntimeException("Error in InfluenzaSampleXMLBySampleProcessAction ", e);			
				
			}
			// castorMapping.loadMapping(url);
			castorMapping.loadMapping(source);
			
			// Marshaller marshaller = new Marshaller(
			// new OutputStreamWriter(System.out));
			String fileName = "";
			if (batchMessage != null) {
				if (byDateRange.equals(TRUE)) {
					fileName = FILENAME_PREFIX + fromReleasedDateForDisplay.replaceAll("/", "") +  "_" + toReleasedDateForDisplay.replaceAll("/", "") + ".xml";
				}
				else if (bySample.equals(TRUE)) {
					fileName = FILENAME_PREFIX + "multiple_samples" + ".xml";
				}
				else if (bySampleRange.equals(TRUE)) {
					fileName = FILENAME_PREFIX + fromAccessionNumber + "_" + toAccessionNumber + ".xml";
				}
			} else {
				MessageXmit msg = (MessageXmit)messages.get(0);
				Sample samp = msg.getSample().getSample();
				String accessionNumber = samp.getAccessionNumber();
				fileName = FILENAME_PREFIX + accessionNumber + ".xml";
			}
			Marshaller marshaller = new Marshaller();
			marshaller.setMapping(castorMapping);
			Writer writer = new StringWriter();
			marshaller.setWriter(writer);
			if (batchMessage != null) {
				marshaller.marshal(batchMessage);
			} else {
				if (messages != null && messages.size() > 0) {
					marshaller.marshal((MessageXmit)messages.get(0));
				}
			}
			xmlString = writer.toString();
			
			response.reset();
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
			//response.setContentType("application/xml");
			response.setContentType("multipart/xml");
			response.setContentLength((int)xmlString.length());
			
			OutputStream os = response.getOutputStream();
			byte[] xmlBytes = xmlString.getBytes();
			ByteArrayInputStream bais = new ByteArrayInputStream(xmlBytes);
			InputStream is = (InputStream)bais;
			int count;
			byte buf[] = new byte[4096];
			while ((count = is.read(buf)) > -1)
				os.write(buf, 0, count);
			is.close();
			os.close();
			
			xmlString = "";
			
			tx.commit();
		} catch (LIMSRuntimeException lre) {
			LogEvent.logError("InfluenzaSampleXMLBySampleProcessAction","performAction()",lre.toString());
			tx.rollback();
			
			errors = new ActionMessages();
			ActionError error = null;
			
			if (lre.getException() instanceof LIMSCannotCreateXMLException) {
				error = new ActionError("errors.CannotCreateXMLException",
						null, null);
				
			} else {
				error = new ActionError("errors.GetException", null, null);
				
			}
			
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute(ALLOW_EDITS_KEY, "false");
			return mapping.findForward(FWD_FAIL);
			
		} catch (Exception e) {
			LogEvent.logError("InfluenzaSampleXMLBySampleProcessAction","performAction()",e.toString());
			tx.rollback();
			
			errors = new ActionMessages();
			ActionError error = null;
			
			error = new ActionError("errors.CannotCreateXMLException",
					null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute(ALLOW_EDITS_KEY, "false");
			return mapping.findForward(FWD_FAIL);	
		} finally {
			HibernateUtil.closeSession();
		}
		
		PropertyUtils.setProperty(dynaForm, "fromAccessionNumber", fromAccessionNumber);
		PropertyUtils.setProperty(dynaForm, "toAccessionNumber", toAccessionNumber);
		
		PropertyUtils.setProperty(dynaForm, "accessionNumber1", accessionNumber1);
		PropertyUtils.setProperty(dynaForm, "accessionNumber2", accessionNumber2);
		PropertyUtils.setProperty(dynaForm, "accessionNumber3", accessionNumber3);
		PropertyUtils.setProperty(dynaForm, "accessionNumber4", accessionNumber4);
		PropertyUtils.setProperty(dynaForm, "accessionNumber5", accessionNumber5);
		PropertyUtils.setProperty(dynaForm, "accessionNumber6", accessionNumber6);
		PropertyUtils.setProperty(dynaForm, "accessionNumber7", accessionNumber7);
		PropertyUtils.setProperty(dynaForm, "accessionNumber8", accessionNumber8);
		PropertyUtils.setProperty(dynaForm, "accessionNumber9", accessionNumber9);
		
		PropertyUtils.setProperty(dynaForm, "fromReleasedDateForDisplay", fromReleasedDateForDisplay);
		PropertyUtils.setProperty(dynaForm, "toReleasedDateForDisplay", toReleasedDateForDisplay);
		
		
		
		return mapping.findForward(forward);
	}
	
	protected String getPageTitleKey() {
		return "report.sample.xml.by.test.title";
	}
	
	protected String getPageSubtitleKey() {
		return "report.sample.xml.by.test.subtitle";
	}
	
	protected String convertToDisplayableXML(String xml) {
		if (!StringUtil.isNullorNill(xml)) {
			xml = xml.replaceAll("<", "&lt;");
			xml = xml.replaceAll(">", "&gt;");
			// the following 2 lines are for display on page (not for system
			// out)
			xml = xml.replaceAll("\n", "<br>");
			xml = xml.replaceAll(" ", "&nbsp;");
		}
		return xml;
	}
	
	protected InputSource getSource(String castorMappingName) {
		InputStream mappingStream = null;
		// System.out.println("This is classpath " +
		// System.getProperty("java.class.path"));
		InputStream mappingXml = Thread.currentThread().getContextClassLoader()
		.getResourceAsStream(castorMappingName);
		// System.out.println("this is mappingXml " + mappingXml);
		InputSource source = new InputSource(mappingXml);
		return source;
	}
	
	protected ActionMessages validateAccessionNumber(HttpServletRequest request, ActionMessages errors, String accessionNumber, String formName) throws Exception {
		
		// accession number validation against database (reusing ajax
		// validation logic)
		AccessionNumberValidationProvider accessionNumberValidator = new AccessionNumberValidationProvider();
		
		String result = "";
		result = accessionNumberValidator.validate(accessionNumber, formName);
		
		String messageKey = "sample.accessionNumber";
		if (result.equals(INVALID)) {
			ActionError error = new ActionError("errors.invalid",
					getMessageForKey(messageKey), null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		if (result.equals(INVALIDSTATUS)) {
			ActionError error = new ActionError("error.invalid.sample.status",
					getMessageForKey(messageKey), null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		return errors;
	}	
	
	//used for batch accession number processing (QE, QA Events)
	protected List populateAccessionNumberList(String fromAccessionNumber,
			String thruAccessionNumber) {
		List accessionNumbers = new ArrayList();
		
		
		if (!StringUtil.isNullorNill(thruAccessionNumber)) {
			int fromInt = Integer.parseInt(fromAccessionNumber);
			int thruInt = Integer.parseInt(thruAccessionNumber);
			for (int i = fromInt; i <= thruInt; i++) {
				accessionNumbers.add(String.valueOf(i));
			}
			
		} else {
			accessionNumbers.add(fromAccessionNumber);
		}
		return accessionNumbers;
	}
	
	protected List createSampleObjectsFromAccessionNumbers(List accessionNumbers) {
		List samples = new ArrayList();
		for (int i = 0; i < accessionNumbers.size(); i++) {
			String accessionNumber = (String)accessionNumbers.get(i);
			Sample sample = new Sample();
			sample.setAccessionNumber(accessionNumber);
			samples.add(sample);
		}
		return samples;
	}
}
