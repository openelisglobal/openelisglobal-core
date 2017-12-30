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
package us.mn.state.health.lims.projectorganization.daoimpl;

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Query;

import us.mn.state.health.lims.audittrail.dao.AuditTrailDAO;
import us.mn.state.health.lims.audittrail.daoimpl.AuditTrailDAOImpl;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.daoimpl.BaseDAOImpl;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.projectorganization.dao.ProjectOrganizationDAO;
import us.mn.state.health.lims.projectorganization.valueholder.ProjectOrganization;

public class ProjectOrganizationDAOImpl extends BaseDAOImpl implements ProjectOrganizationDAO {

	public void deleteData(List projectOrgss) throws LIMSRuntimeException {
		//add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			for (int i = 0; i < projectOrgss.size(); i++) {
				ProjectOrganization data = (ProjectOrganization)projectOrgss.get(i);
			
				ProjectOrganization oldData = (ProjectOrganization)readProjectOrganization(data.getId());
				ProjectOrganization newData = new ProjectOrganization();

				String sysUserId = data.getSysUserId();
				String event = IActionConstants.AUDIT_TRAIL_DELETE;
				String tableName = "PROJECT_ORGANIZATION";
				auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
			}
		}  catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ProjectOrganizationDAOImpl","AuditTrail deleteData()",e.toString());	
			throw new LIMSRuntimeException("Error in ProjectOrganization AuditTrail deleteData()", e);
		}  
		
		try {		
			for (int i = 0; i < projectOrgss.size(); i++) {
				ProjectOrganization data = (ProjectOrganization) projectOrgss.get(i);
				//bugzilla 2206
				data = (ProjectOrganization)readProjectOrganization(data.getId());
				HibernateUtil.getSession().delete(data);
				HibernateUtil.getSession().flush();
				HibernateUtil.getSession().clear();
			}			
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ProjectOrganizationDAOImpl","deleteData()",e.toString());	
			throw new LIMSRuntimeException("Error in ProjectHuman deleteData()", e);
		}
	}

	public boolean insertData(ProjectOrganization projectOrg) throws LIMSRuntimeException {
		
		try {
			String id = (String)HibernateUtil.getSession().save(projectOrg);
			projectOrg.setId(id);
			
			//bugzilla 1824 inserts will be logged in history table
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = projectOrg.getSysUserId();
			String tableName = "PROJECT_ORGANIZATION";
			auditDAO.saveNewHistory(projectOrg,sysUserId,tableName);
			
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
							
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ProjectOrganizationDAOImpl","insertData()",e.toString());	
			throw new LIMSRuntimeException("Error in ProjectOrganization insertData()", e);
		}
		
		return true;
	}

	public void updateData(ProjectOrganization projectOrg) throws LIMSRuntimeException {
		
		ProjectOrganization oldData = (ProjectOrganization)readProjectOrganization(projectOrg.getId());
		ProjectOrganization newData = projectOrg;

		//add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = projectOrg.getSysUserId();
			String event = IActionConstants.AUDIT_TRAIL_UPDATE;
			String tableName = "PROJECT_ORGANIZATION";
			auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
		}  catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ProjectOrganizationDAOImpl","AuditTrail updateData()",e.toString());
			throw new LIMSRuntimeException("Error in ProjectOrganization AuditTrail updateData()", e);
		}  
						
		try {
			HibernateUtil.getSession().merge(projectOrg);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			HibernateUtil.getSession().evict(projectOrg);
			HibernateUtil.getSession().refresh(projectOrg);
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ProjectOrganizationDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException("Error in ProjectOrganization updateData()", e);
		} 
	}

	public void getData(ProjectOrganization projectOrg) throws LIMSRuntimeException {
		try {
			ProjectOrganization data = (ProjectOrganization)HibernateUtil.getSession().get(ProjectOrganization.class, projectOrg.getId());
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			if (data != null) {
				PropertyUtils.copyProperties(projectOrg, data);
			} else {
				projectOrg.setId(null);
			}
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ProjectOrganizationDAOImpl","getData()",e.toString());
			throw new LIMSRuntimeException("Error in ProjectOrganization getData()", e);
		}
	}

	public ProjectOrganization readProjectOrganization(String idString) {
		ProjectOrganization so = null;
		try {
			so = (ProjectOrganization)HibernateUtil.getSession().get(ProjectOrganization.class, idString);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ProjectOrganizationDAOImpl","readProjectOrganization()",e.toString());
			throw new LIMSRuntimeException("Error in ProjectOrganization readProjectOrganization()", e);
		}			
		
		return so;
	}
	
	public void getDataByProject(ProjectOrganization projectOrganization) throws LIMSRuntimeException {
		
		try {
		String sql = "from ProjectOrganization so where project_id = :param";
		Query query = HibernateUtil.getSession().createQuery(sql);
		query.setParameter("param", projectOrganization.getProjectId());
		List list = query.list();
		HibernateUtil.getSession().flush();
		HibernateUtil.getSession().clear();
		ProjectOrganization projOrgs = null;
		if ( list.size()> 0 ) {
			projOrgs = (ProjectOrganization)list.get(0);			
			PropertyUtils.copyProperties(projectOrganization, projOrgs);
		}
		}catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ProjectOrganizationDAOImpl","getData()",e.toString());
			throw new LIMSRuntimeException("Error in ProjectOrganization getData()", e);
		}

	}
}