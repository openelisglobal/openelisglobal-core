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
package us.mn.state.health.lims.analysisqaevent.dao;

import java.util.List;

import us.mn.state.health.lims.analysisqaevent.valueholder.AnalysisQaEvent;
import us.mn.state.health.lims.common.dao.BaseDAO;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;

/**
 *  $Header$
 *
 *  @author         Diane Benz
 *  @date created   08/24/2007
 *  @version        $Revision$
 *  bugzilla 2028
 */
public interface AnalysisQaEventDAO extends BaseDAO {

	public boolean insertData(AnalysisQaEvent analysisQaEvent) throws LIMSRuntimeException;

	public void deleteData(List analysisQaEvents) throws LIMSRuntimeException;

	public void getData(AnalysisQaEvent analysisQaEvent) throws LIMSRuntimeException;

	public void updateData(AnalysisQaEvent analysisQaEvent) throws LIMSRuntimeException;
	
	public List getAnalysisQaEventsByAnalysis(AnalysisQaEvent analysisQaEvent) throws LIMSRuntimeException;

	public AnalysisQaEvent getAnalysisQaEventByAnalysisAndQaEvent(AnalysisQaEvent analysisQaEvent) throws LIMSRuntimeException;
	
}
