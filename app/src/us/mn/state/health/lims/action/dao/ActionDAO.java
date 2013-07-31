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
package us.mn.state.health.lims.action.dao;

import java.util.List;

import us.mn.state.health.lims.action.valueholder.Action;
import us.mn.state.health.lims.common.dao.BaseDAO;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public interface ActionDAO extends BaseDAO {

	public boolean insertData(Action action) throws LIMSRuntimeException;

	public void deleteData(List actions) throws LIMSRuntimeException;

	public List getAllActions() throws LIMSRuntimeException;

	public List getPageOfActions(int startingRecNo)
			throws LIMSRuntimeException;

	public void getData(Action action) throws LIMSRuntimeException;

	public void updateData(Action action) throws LIMSRuntimeException;
	
	public List getNextActionRecord(String id) throws LIMSRuntimeException;

	public List getPreviousActionRecord(String id) throws LIMSRuntimeException;
	
	public Integer getTotalActionCount() throws LIMSRuntimeException; 	
	
	public Action getActionByCode(Action action) throws LIMSRuntimeException;
    // bugzilla 2503
	public List getAllActionsByFilter ( String filterString ) throws LIMSRuntimeException;
}
