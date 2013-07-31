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
package us.mn.state.health.lims.resultlimits.dao;

import java.util.List;

import us.mn.state.health.lims.common.dao.BaseDAO;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.resultlimits.valueholder.ResultLimit;
import us.mn.state.health.lims.test.valueholder.Test;

public interface ResultLimitDAO extends BaseDAO {

	public boolean insertData(ResultLimit resultLimit)
			throws LIMSRuntimeException;

	public void deleteData(List resultLimits) throws LIMSRuntimeException;

	public List getAllResultLimits() throws LIMSRuntimeException;

	public List getPageOfResultLimits(int startingRecNo)
			throws LIMSRuntimeException;

	public void getData(ResultLimit resultLimit) throws LIMSRuntimeException;

	public void updateData(ResultLimit resultLimit) throws LIMSRuntimeException;

	public List getNextResultLimitRecord(String id) throws LIMSRuntimeException;

	public List getPreviousResultLimitRecord(String id)
			throws LIMSRuntimeException;

	public List getAllResultLimitsForTest(Test test) throws LIMSRuntimeException;
	
	public ResultLimit getResultLimitById( String resultLimitId) throws LIMSRuntimeException;
}
