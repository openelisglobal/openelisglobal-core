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
package us.mn.state.health.lims.sourceofsample.dao;

import java.util.List;

import us.mn.state.health.lims.common.dao.BaseDAO;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.sourceofsample.valueholder.SourceOfSample;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public interface SourceOfSampleDAO extends BaseDAO {

	public boolean insertData(SourceOfSample sourceOfSample)
			throws LIMSRuntimeException;

	public void deleteData(List sourceOfSamples) throws LIMSRuntimeException;

	public List getAllSourceOfSamples() throws LIMSRuntimeException;

	public List getPageOfSourceOfSamples(int startingRecNo)
			throws LIMSRuntimeException;

	public void getData(SourceOfSample sourceOfSample)
			throws LIMSRuntimeException;

	public void updateData(SourceOfSample sourceOfSample)
			throws LIMSRuntimeException;
	
	//bugzilla 1474 added domain parm
	public List getSources(String filter, String domain) throws LIMSRuntimeException;
	
	public List getNextSourceOfSampleRecord(String id) throws LIMSRuntimeException;

	public List getPreviousSourceOfSampleRecord(String id) throws LIMSRuntimeException;

	//bugzilla 1411
	public Integer getTotalSourceOfSampleCount() throws LIMSRuntimeException; 
	
	//bugzilla 1367
	public SourceOfSample getSourceOfSampleByDescriptionAndDomain(SourceOfSample sos, boolean ignoreCase) throws LIMSRuntimeException;
}
