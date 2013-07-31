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
package us.mn.state.health.lims.projectorganization.dao;

import java.util.List;

import us.mn.state.health.lims.common.dao.BaseDAO;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.projectorganization.valueholder.ProjectOrganization;


public interface ProjectOrganizationDAO extends BaseDAO {

	public boolean insertData(ProjectOrganization projectOrg) throws LIMSRuntimeException;

	public void deleteData(List projectOrgs) throws LIMSRuntimeException;

	public void getData(ProjectOrganization projectOrg) throws LIMSRuntimeException;

	public void updateData(ProjectOrganization projectOrg) throws LIMSRuntimeException;
	
	public void getDataByProject(ProjectOrganization projectOrg) throws LIMSRuntimeException;

	
}
