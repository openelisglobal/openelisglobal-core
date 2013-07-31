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
 * Copyright (C) ITECH, University of Washington, Seattle WA.  All Rights Reserved.
 *
 */
package us.mn.state.health.lims.common.services;

import java.sql.Date;
import java.util.List;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.sample.valueholder.Sample;


public class SampleService {
	private static final AnalysisDAO analysisDAO = new AnalysisDAOImpl();
	private Sample sample;
	
	public SampleService( Sample sample){
		this.sample = sample;
	}

	/**
	 * Gets the date of when the order was completed
	 * @return The date of when it was completed, null if it was not yet completed
	 */
	public Date getCompletedDate(){
		Date date = null;
		List<Analysis> analysisList = analysisDAO.getAnalysesBySampleId(sample.getId());
		
		for( Analysis analysis : analysisList){
			if( analysis.getCompletedDate() == null ){
				return null;
			}else if(date == null){
				date = analysis.getCompletedDate();
			}else if( analysis.getCompletedDate().after(date)){
				date = analysis.getCompletedDate();
			}
		}
		
		return date;
	}
	
	public Date getOrderedDate(){
		return sample.getReceivedDate();
	}
}
