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
package us.mn.state.health.lims.label.dao;

import java.util.List;

import us.mn.state.health.lims.common.dao.BaseDAO;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.label.valueholder.Label;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public interface LabelDAO extends BaseDAO {

	public boolean insertData(Label label) throws LIMSRuntimeException;

	public void deleteData(List labels) throws LIMSRuntimeException;

	public List getAllLabels() throws LIMSRuntimeException;

	public List getPageOfLabels(int startingRecNo) throws LIMSRuntimeException;

	public void getData(Label label) throws LIMSRuntimeException;

	public void updateData(Label label) throws LIMSRuntimeException;

	public List getLabels(String filter) throws LIMSRuntimeException;

	public List getNextLabelRecord(String id) throws LIMSRuntimeException;

	public List getPreviousLabelRecord(String id) throws LIMSRuntimeException;

	public Label getLabelByName(Label label) throws LIMSRuntimeException;
	
	//bugzilla 1411
	public Integer getTotalLabelCount() throws LIMSRuntimeException; 


}
