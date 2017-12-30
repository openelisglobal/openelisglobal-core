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
package us.mn.state.health.lims.common.provider.validation;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.servlet.validation.AjaxServlet;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;

/**
 * The QuickEntryAccessionNumberValidationProvider class is used to 
 * validate, via AJAX, the Sample Type entered on the Quick Entry view.
 * 
 * @author	Ken Rosha	08/30/2006
 */
public class QuickEntryAccessionNumberValidationProvider
	extends BaseValidationProvider 
{
	public QuickEntryAccessionNumberValidationProvider()
	{
		super();
	}

	//==============================================================

	public QuickEntryAccessionNumberValidationProvider(AjaxServlet ajaxServlet)
	{
		this.ajaxServlet = ajaxServlet;
	}
	//==============================================================

	public void processRequest(HttpServletRequest request,
							   HttpServletResponse response) 
		throws ServletException, IOException 
	{
		String targetId = (String) request.getParameter("id");
		String formField = (String) request.getParameter("field");
		String result = validate(targetId);
		ajaxServlet.sendData(formField, result, request, response);
	}
	//==============================================================

	/**
	 * Determine if the accession number exists in the database.
	 * 
	 * @param	String		The accession number to be sought.
	 * 
	 * @return	String		"valid" if the accession number does not exist,
	 * 						"invalid" if the accession number does exist.
	 */
	public String validate(String accessionNumber)
		throws LIMSRuntimeException 
	{
		String valid = VALID;
		SampleDAO sampleDAO = new SampleDAOImpl();
		Sample sample = sampleDAO.getSampleByAccessionNumber(accessionNumber);
		//bugzilla 2154
		LogEvent.logDebug("QuickEntryAccessionNumberValidationProvider","validate()","Sample is: " + sample);
		
		/* was this 12-1-06
		if (sample != null)
		{
			valid = INVALID;
		}*/
		
		//bgm - bugzilla 1551 added for if accessionNumber was created from being scanned in */ 
		if(sample !=null){

			
			if(sample.getStatus() !=null){
				if(Integer.parseInt(sample.getStatus()) != Integer.parseInt((SystemConfiguration.getInstance().getSampleStatusLabelPrinted())) ){
					valid = INVALID;				
				}
			}else{
				valid = INVALID;
			}
				
		} else {
			//bugzilla 1813
			valid = INVALID;
		}
																	
			
		return valid;
	}
	//==============================================================
}
