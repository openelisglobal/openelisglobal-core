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
package us.mn.state.health.lims.codeelementxref.dao;

import java.util.List;

import us.mn.state.health.lims.codeelementxref.valueholder.CodeElementXref;
import us.mn.state.health.lims.common.dao.BaseDAO;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public interface CodeElementXrefDAO extends BaseDAO {

	public boolean insertData(CodeElementXref codeElementXref)
			throws LIMSRuntimeException;

	public void deleteData(List codeElementXrefs)
			throws LIMSRuntimeException;

	public List getAllCodeElementXrefs() throws LIMSRuntimeException;

	public List getPageOfCodeElementXrefs(int startingRecNo)
			throws LIMSRuntimeException;

	public void getData(CodeElementXref codeElementXref)
			throws LIMSRuntimeException;

	public void updateData(CodeElementXref codeElementXref)
			throws LIMSRuntimeException;

    public List getNextCodeElementXrefRecord(String id)
			throws LIMSRuntimeException;

	public List getPreviousCodeElementXrefRecord(String id)
			throws LIMSRuntimeException;

	public Integer getTotalCodeElementXrefCount()
			throws LIMSRuntimeException;
	
	public List getCodeElementXrefsByReceiverOrganizationAndCodeElementType(CodeElementXref codeElementXref)
            throws LIMSRuntimeException;
}
