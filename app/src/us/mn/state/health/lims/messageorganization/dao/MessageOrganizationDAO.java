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
package us.mn.state.health.lims.messageorganization.dao;

import java.util.List;

import us.mn.state.health.lims.common.dao.BaseDAO;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.messageorganization.valueholder.MessageOrganization;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public interface MessageOrganizationDAO extends BaseDAO {

	public boolean insertData(MessageOrganization messageOrganization)
			throws LIMSRuntimeException;

	public void deleteData(List messageOrganizations)
			throws LIMSRuntimeException;

	public List getAllMessageOrganizations() throws LIMSRuntimeException;

	public List getPageOfMessageOrganizations(int startingRecNo)
			throws LIMSRuntimeException;

	public void getData(MessageOrganization messageOrganization)
			throws LIMSRuntimeException;

	public void updateData(MessageOrganization messageOrganization)
			throws LIMSRuntimeException;

	public List getMessageOrganizations(String filter)
			throws LIMSRuntimeException;

	public List getNextMessageOrganizationRecord(String id)
			throws LIMSRuntimeException;

	public List getPreviousMessageOrganizationRecord(String id)
			throws LIMSRuntimeException;

	public MessageOrganization getMessageOrganizationByOrganization(
			MessageOrganization messageOrganization)
			throws LIMSRuntimeException;

	public Integer getTotalMessageOrganizationCount()
			throws LIMSRuntimeException;

}
