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
package us.mn.state.health.lims.sampleqaeventaction.daoimpl;

import java.util.ArrayList;
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
import us.mn.state.health.lims.sampleqaeventaction.dao.SampleQaEventActionDAO;
import us.mn.state.health.lims.sampleqaeventaction.valueholder.SampleQaEventAction;

/**
 *  $Header$
 *
 *  @author         Diane Benz
 *  @date created   06/12/2008
 *  @version        $Revision$
 *  bugzilla 2510
 */
public class SampleQaEventActionDAOImpl extends BaseDAOImpl implements SampleQaEventActionDAO {

	public void deleteData(List sampleQaEventActions) throws LIMSRuntimeException {
		//add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			for (int i = 0; i < sampleQaEventActions.size(); i++) {
				SampleQaEventAction data = (SampleQaEventAction)sampleQaEventActions.get(i);
			
				SampleQaEventAction oldData = (SampleQaEventAction)readSampleQaEventAction(data.getId());
				SampleQaEventAction newData = new SampleQaEventAction();

				String sysUserId = data.getSysUserId();
				String event = IActionConstants.AUDIT_TRAIL_DELETE;
				String tableName = "SAMPLE_QAEVENT_ACTION";
				auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
			}
		}  catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("SampleQaEventActionDAOImpl","AuditTrail deleteData()",e.toString());
			throw new LIMSRuntimeException("Error in SampleQaEventAction AuditTrail deleteData()", e);
		}  
		
		try {		
			for (int i = 0; i < sampleQaEventActions.size(); i++) {
				SampleQaEventAction data = (SampleQaEventAction) sampleQaEventActions.get(i);
				//bugzilla 2206
				data = (SampleQaEventAction)readSampleQaEventAction(data.getId());
				HibernateUtil.getSession().delete(data);
				HibernateUtil.getSession().flush();
				HibernateUtil.getSession().clear();
			}			
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("SampleQaEventActionDAOImpl","deleteData()",e.toString());
			throw new LIMSRuntimeException("Error in SampleQaEventAction deleteData()", e);
		}
	}

	public boolean insertData(SampleQaEventAction sampleQaEventAction) throws LIMSRuntimeException {
		
		try {
			String id = (String)HibernateUtil.getSession().save(sampleQaEventAction);
			sampleQaEventAction.setId(id);
			
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = sampleQaEventAction.getSysUserId();
			String tableName = "SAMPLE_QAEVENT_ACTION";
			auditDAO.saveNewHistory(sampleQaEventAction,sysUserId,tableName);
			
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
						
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("SampleQaEventActionDAOImpl","insertData()",e.toString());
			throw new LIMSRuntimeException("Error in SampleQaEventAction insertData()", e);
		}
		
		return true;
	}

	public void updateData(SampleQaEventAction sampleQaEventAction) throws LIMSRuntimeException {
		
		SampleQaEventAction oldData = (SampleQaEventAction)readSampleQaEventAction(sampleQaEventAction.getId());
		SampleQaEventAction newData = sampleQaEventAction;

		//add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = sampleQaEventAction.getSysUserId();
			String event = IActionConstants.AUDIT_TRAIL_UPDATE;
			String tableName = "SAMPLE_QAEVENT_ACTION";
			auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
		}  catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("SampleQaEventActionDAOImpl","AuditTrail updateData()",e.toString());
			throw new LIMSRuntimeException("Error in SampleQaEventAction AuditTrail updateData()", e);
		}  
						
		try {
			HibernateUtil.getSession().merge(sampleQaEventAction);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			HibernateUtil.getSession().evict(sampleQaEventAction);
			HibernateUtil.getSession().refresh(sampleQaEventAction);
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("SampleQaEventActionDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException("Error in SampleQaEventAction updateData()", e);
		} 
	}

	public void getData(SampleQaEventAction sampleQaEventAction) throws LIMSRuntimeException {
		try {
			SampleQaEventAction data = (SampleQaEventAction)HibernateUtil.getSession().get(SampleQaEventAction.class, sampleQaEventAction.getId());
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			if (data != null) {
				PropertyUtils.copyProperties(sampleQaEventAction, data);
			} else {
				sampleQaEventAction.setId(null);
			}
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("SampleQaEventActionDAOImpl","getData()",e.toString());
			throw new LIMSRuntimeException("Error in SampleQaEventAction getData()", e);
		}
	}

	public SampleQaEventAction readSampleQaEventAction(String idString) {
		SampleQaEventAction sp = null;
		try {
			sp = (SampleQaEventAction)HibernateUtil.getSession().get(SampleQaEventAction.class, idString);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("SampleQaEventActionDAOImpl","readSampleQaEventAction()",e.toString());
			throw new LIMSRuntimeException("Error in SampleQaEventAction readSampleQaEventAction()", e);
		}			
		
		return sp;
	}
	
	public List getSampleQaEventActionsByActionId(String actionId) throws LIMSRuntimeException {
		List sampleQaEventActions = new ArrayList();
		
		try {
			String sql = "from SampleQaEventAction aqea where aqea.action = :param";
			Query query = HibernateUtil.getSession().createQuery(sql);			
			query.setParameter("param", actionId);		
		
			sampleQaEventActions = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();	
			
			return sampleQaEventActions;
			
			
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("SampleQaEventActionDAOImpl","getSampleQaEventActionsByActionId()",e.toString());
			throw new LIMSRuntimeException(
					"Error in SampleQaEventAction getSampleQaEventActionsByActionId()", e);
		}
	}
	
	public List getSampleQaEventActionsBySampleQaEvent(SampleQaEventAction sampleQaEventAction) throws LIMSRuntimeException {
		List sampleQaEventActions = new ArrayList();
		
		try {
			String sql = "from SampleQaEventAction aqea where aqea.sampleQaEvent = :param";
			Query query = HibernateUtil.getSession().createQuery(sql);			
			query.setParameter("param", sampleQaEventAction.getSampleQaEvent().getId());		
		
			sampleQaEventActions = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();	
			
			return sampleQaEventActions;
			
			
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("SampleQaEventActionDAOImpl","getSampleQaEventActionsBySampleQaEventId()",e.toString());
			throw new LIMSRuntimeException(
					"Error in SampleQaEventAction getSampleQaEventActionsBySampleQaEventId()", e);
		}
	}
	
	public SampleQaEventAction getSampleQaEventActionBySampleQaEventAndAction(SampleQaEventAction sampleQaEventAction) throws LIMSRuntimeException {
		SampleQaEventAction analQaEventAction = null;
		try
		{
			// Use an expression to read in the SampleQaEvent whose 
			// sample and qaevent is given		
			String sql = "from SampleQaEventAction aqea where aqea.sampleQaEvent = :param and aqea.action = :param2";
			Query query = HibernateUtil.getSession().createQuery(sql);
			query.setParameter("param", sampleQaEventAction.getSampleQaEvent().getId());
			query.setParameter("param2", sampleQaEventAction.getAction().getId());
			List list = query.list();
			if ((list != null) &&
				!list.isEmpty())
			{
				analQaEventAction = (SampleQaEventAction)list.get(0);
			}
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();	

		}
		catch (Exception e)
		{
			//buzilla 2154
			LogEvent.logError("SampleQaEventActionDAOImpl","getSampleQaEventActionBySampleQaEventAndAction()",e.toString());			
			throw new LIMSRuntimeException("Exception occurred in getSampleQaEventActionBySampleQaEventAndAction", e);
		}
		return analQaEventAction;
		

	}
}