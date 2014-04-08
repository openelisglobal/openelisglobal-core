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

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.sample.valueholder.Sample;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SampleService {
	private static final AnalysisDAO analysisDAO = new AnalysisDAOImpl();
    private static final Set<Integer> CONFIRMATION_STATUS_SET = new HashSet<Integer>(  );

	private Sample sample;
	
	public SampleService( Sample sample){
		this.sample = sample;
	}

    static {
        CONFIRMATION_STATUS_SET.add( Integer.parseInt( StatusService.getInstance().getStatusID( StatusService.AnalysisStatus.ReferredIn ) ) );
    }
	/**
	 * Gets the date of when the order was completed
	 * @return The date of when it was completed, null if it was not yet completed
	 */
	public Date getCompletedDate(){
		Date date = null;
		List<Analysis> analysisList = analysisDAO.getAnalysesBySampleId(sample.getId());

        for( Analysis analysis : analysisList ){
            if( !isCanceled( analysis ) ){
                if( analysis.getCompletedDate() == null ){
                    return null;
                }else if( date == null ){
                    date = analysis.getCompletedDate();
                }else if( analysis.getCompletedDate().after( date ) ){
                    date = analysis.getCompletedDate();
                }
            }
        }
        return date;
	}

    private boolean isCanceled( Analysis analysis ){
        return StatusService.getInstance().getStatusID( StatusService.AnalysisStatus.Canceled ).equals( analysis.getStatusId() );
    }

    public Date getOrderedDate(){
		return sample.getReceivedDate();
	}

    public String getAccessionNumber(){
        return sample.getAccessionNumber();
    }

    public String getReceivedDateForDisplay(){
        return sample.getReceivedDateForDisplay();
    }

    public String getReceivedTimeForDisplay(){
        return sample.getReceivedTimeForDisplay();
    }

    public boolean isConfirmationSample(){
        return !analysisDAO.getAnalysesBySampleIdAndStatusId( sample.getId(), CONFIRMATION_STATUS_SET ).isEmpty();
    }

    public Sample getSample(){
        return sample;
    }

    public String getId(){
        return sample.getId();
    }
}
