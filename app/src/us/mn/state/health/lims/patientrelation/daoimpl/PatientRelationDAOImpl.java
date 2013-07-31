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
package us.mn.state.health.lims.patientrelation.daoimpl;

import java.util.List;
import java.util.ArrayList;

import org.apache.commons.beanutils.PropertyUtils;

import us.mn.state.health.lims.audittrail.dao.AuditTrailDAO;
import us.mn.state.health.lims.audittrail.daoimpl.AuditTrailDAOImpl;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.daoimpl.BaseDAOImpl;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.patientrelation.dao.PatientRelationDAO;
import us.mn.state.health.lims.patientrelation.valueholder.PatientRelation;

public class PatientRelationDAOImpl extends BaseDAOImpl implements PatientRelationDAO {

	public void deleteData(List patientRelations) throws LIMSRuntimeException {
		
		//add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			for (int i = 0; i < patientRelations.size(); i++) {
				PatientRelation data = (PatientRelation)patientRelations.get(i);
			
				PatientRelation oldData = (PatientRelation)readPatientRelation(data.getId());
				PatientRelation newData = new PatientRelation();

				String sysUserId = data.getSysUserId();
				String event = IActionConstants.AUDIT_TRAIL_DELETE;
				String tableName = "PATIENT_RELATIONS";
				auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
			}
		}  catch (Exception e) {
			LogEvent.logError("PatientRelationDAOImpl","AuditTrail deleteData()",e.toString());	
			throw new LIMSRuntimeException("Error in PatientRelation AuditTrail deleteData()", e);
		}  
		
		try {		
			for (int i = 0; i < patientRelations.size(); i++) {
				PatientRelation data = (PatientRelation) patientRelations.get(i);
				data = (PatientRelation)readPatientRelation(data.getId());
				HibernateUtil.getSession().delete(data);
				HibernateUtil.getSession().flush();
				HibernateUtil.getSession().clear();
			}			
		} catch (Exception e) {
			LogEvent.logError("PatientRelationDAOImpl","deleteData()",e.toString());	
			throw new LIMSRuntimeException("Error in PatientRelation deleteData()", e);
		}
	}

	public boolean insertData(PatientRelation patientRelation) throws LIMSRuntimeException {
		
		try {
			String id = (String)HibernateUtil.getSession().save(patientRelation);
			patientRelation.setId(id);
			
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = patientRelation.getSysUserId();
			String tableName = "PATIENT_RELATIONS";
			auditDAO.saveNewHistory(patientRelation,sysUserId,tableName);
			
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
							
		} catch (Exception e) {
			LogEvent.logError("PatientRelationDAOImpl","insertData()",e.toString());	
			throw new LIMSRuntimeException("Error in PatientRelation insertData()", e);
		}
		
		return true;
	}

	public void updateData(PatientRelation patientRelation) throws LIMSRuntimeException {
		
		PatientRelation oldData = (PatientRelation)readPatientRelation(patientRelation.getId());
		PatientRelation newData = patientRelation;

		//add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = patientRelation.getSysUserId();
			String event = IActionConstants.AUDIT_TRAIL_UPDATE;
			String tableName = "PATIENT_RELATIONS";
			auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
		}  catch (Exception e) {
			LogEvent.logError("PatientRelationDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException("Error in PatientRelation AuditTrail updateData()", e);
		}  
						
		try {
			HibernateUtil.getSession().merge(patientRelation);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			HibernateUtil.getSession().evict(patientRelation);
			HibernateUtil.getSession().refresh(patientRelation);
		} catch (Exception e) {
			LogEvent.logError("PatientRelationDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException("Error in PatientRelation updateData()", e);
		} 
	}

	public void getData(PatientRelation patientRelation) throws LIMSRuntimeException {
		try {
			PatientRelation pat = (PatientRelation)HibernateUtil.getSession().get(PatientRelation.class, patientRelation.getId());
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			if (pat != null) {
			  PropertyUtils.copyProperties(patientRelation, pat);
			} else {
				patientRelation.setId(null);
			}
		} catch (Exception e) {
			LogEvent.logError("PatientRelationDAOImpl","getData()",e.toString());
			throw new LIMSRuntimeException("Error in PatientRelation getData()", e);
		}
	}

	public PatientRelation readPatientRelation(String idString) {
		PatientRelation pr = null;
		try {
			pr = (PatientRelation)HibernateUtil.getSession().get(PatientRelation.class, idString);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			LogEvent.logError("PatientRelationDAOImpl","readPatientRelation()",e.toString());
			throw new LIMSRuntimeException("Error in PatientRelation readPatientRelation()", e);
		}			
		
		return pr;
	}
	
	public PatientRelation getPatientRelationByChildId(PatientRelation patientRelation) {	
		try {
			List list = new ArrayList();
			String sql = "from PatientRelation p where p.patientIdSource = :param";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setParameter("param", patientRelation.getPatientIdSource());
			
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();

			if (list.size() > 0) {
				patientRelation = (PatientRelation)list.get(0);
			}	

		} catch (Exception e) {
			LogEvent.logError("PatientRelationDAOImpl","getPatientRelationByChildId()",e.toString());
			throw new LIMSRuntimeException("Error in PatientRelation getPatientRelationByChildId()", e);		
		}	
		return patientRelation;
	}
}