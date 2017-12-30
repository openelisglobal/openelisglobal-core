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
package us.mn.state.health.lims.analysisqaeventaction.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Query;

import us.mn.state.health.lims.analysisqaeventaction.dao.AnalysisQaEventActionDAO;
import us.mn.state.health.lims.analysisqaeventaction.valueholder.AnalysisQaEventAction;
import us.mn.state.health.lims.audittrail.dao.AuditTrailDAO;
import us.mn.state.health.lims.audittrail.daoimpl.AuditTrailDAOImpl;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.daoimpl.BaseDAOImpl;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.hibernate.HibernateUtil;

/**
 *  $Header$
 *
 *  @author         Diane Benz
 *  @date created   08/24/2007
 *  @version        $Revision$
 *  bugzilla 2028
 */
public class AnalysisQaEventActionDAOImpl extends BaseDAOImpl implements AnalysisQaEventActionDAO {

	public void deleteData(List analysisQaEventActions) throws LIMSRuntimeException {
		//add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			for (int i = 0; i < analysisQaEventActions.size(); i++) {
				AnalysisQaEventAction data = (AnalysisQaEventAction)analysisQaEventActions.get(i);
			
				AnalysisQaEventAction oldData = (AnalysisQaEventAction)readAnalysisQaEventAction(data.getId());
				AnalysisQaEventAction newData = new AnalysisQaEventAction();

				String sysUserId = data.getSysUserId();
				String event = IActionConstants.AUDIT_TRAIL_DELETE;
				String tableName = "ANALYSIS_QAEVENT_ACTION";
				auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
			}
		}  catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("AnalysisQaEventActionDAOImpl","AuditTrail deleteData()",e.toString());
			throw new LIMSRuntimeException("Error in AnalysisQaEventAction AuditTrail deleteData()", e);
		}  
		
		try {		
			for (int i = 0; i < analysisQaEventActions.size(); i++) {
				AnalysisQaEventAction data = (AnalysisQaEventAction) analysisQaEventActions.get(i);
				//bugzilla 2206
				data = (AnalysisQaEventAction)readAnalysisQaEventAction(data.getId());
				HibernateUtil.getSession().delete(data);
				HibernateUtil.getSession().flush();
				HibernateUtil.getSession().clear();
			}			
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("AnalysisQaEventActionDAOImpl","deleteData()",e.toString());
			throw new LIMSRuntimeException("Error in AnalysisQaEventAction deleteData()", e);
		}
	}

	public boolean insertData(AnalysisQaEventAction analysisQaEventAction) throws LIMSRuntimeException {
		
		try {
			String id = (String)HibernateUtil.getSession().save(analysisQaEventAction);
			analysisQaEventAction.setId(id);
			
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = analysisQaEventAction.getSysUserId();
			String tableName = "ANALYSIS_QAEVENT_ACTION";
			auditDAO.saveNewHistory(analysisQaEventAction,sysUserId,tableName);
			
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
						
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("AnalysisQaEventActionDAOImpl","insertData()",e.toString());
			throw new LIMSRuntimeException("Error in AnalysisQaEventAction insertData()", e);
		}
		
		return true;
	}

	public void updateData(AnalysisQaEventAction analysisQaEventAction) throws LIMSRuntimeException {
		
		AnalysisQaEventAction oldData = (AnalysisQaEventAction)readAnalysisQaEventAction(analysisQaEventAction.getId());
		AnalysisQaEventAction newData = analysisQaEventAction;

		//add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = analysisQaEventAction.getSysUserId();
			String event = IActionConstants.AUDIT_TRAIL_UPDATE;
			String tableName = "ANALYSIS_QAEVENT_ACTION";
			auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
		}  catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("AnalysisQaEventActionDAOImpl","AuditTrail updateData()",e.toString());
			throw new LIMSRuntimeException("Error in AnalysisQaEventAction AuditTrail updateData()", e);
		}  
						
		try {
			HibernateUtil.getSession().merge(analysisQaEventAction);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			HibernateUtil.getSession().evict(analysisQaEventAction);
			HibernateUtil.getSession().refresh(analysisQaEventAction);
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("AnalysisQaEventActionDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException("Error in AnalysisQaEventAction updateData()", e);
		} 
	}

	public void getData(AnalysisQaEventAction analysisQaEventAction) throws LIMSRuntimeException {
		try {
			AnalysisQaEventAction data = (AnalysisQaEventAction)HibernateUtil.getSession().get(AnalysisQaEventAction.class, analysisQaEventAction.getId());
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			if (data != null) {
				PropertyUtils.copyProperties(analysisQaEventAction, data);
			} else {
				analysisQaEventAction.setId(null);
			}
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("AnalysisQaEventActionDAOImpl","getData()",e.toString());
			throw new LIMSRuntimeException("Error in AnalysisQaEventAction getData()", e);
		}
	}

	public AnalysisQaEventAction readAnalysisQaEventAction(String idString) {
		AnalysisQaEventAction sp = null;
		try {
			sp = (AnalysisQaEventAction)HibernateUtil.getSession().get(AnalysisQaEventAction.class, idString);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("AnalysisQaEventActionDAOImpl","readAnalysisQaEventAction()",e.toString());
			throw new LIMSRuntimeException("Error in AnalysisQaEventAction readAnalysisQaEventAction()", e);
		}			
		
		return sp;
	}
	
	public List getAnalysisQaEventActionsByActionId(String actionId) throws LIMSRuntimeException {
		List analysisQaEventActions = new ArrayList();
		
		try {
			String sql = "from AnalysisQaEventAction aqea where aqea.action = :param";
			Query query = HibernateUtil.getSession().createQuery(sql);			
			query.setParameter("param", actionId);		
		
			analysisQaEventActions = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();	
			
			return analysisQaEventActions;
			
			
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("AnalysisQaEventActionDAOImpl","getAnalysisQaEventActionsByActionId()",e.toString());
			throw new LIMSRuntimeException(
					"Error in AnalysisQaEventAction getAnalysisQaEventActionsByActionId()", e);
		}
	}
	
	public List getAnalysisQaEventActionsByAnalysisQaEvent(AnalysisQaEventAction analysisQaEventAction) throws LIMSRuntimeException {
		List analysisQaEventActions = new ArrayList();
		
		try {
			String sql = "from AnalysisQaEventAction aqea where aqea.analysisQaEvent = :param";
			Query query = HibernateUtil.getSession().createQuery(sql);			
			query.setParameter("param", analysisQaEventAction.getAnalysisQaEvent().getId());		
		
			analysisQaEventActions = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();	
			
			return analysisQaEventActions;
			
			
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("AnalysisQaEventActionDAOImpl","getAnalysisQaEventActionsByAnalysisQaEventId()",e.toString());
			throw new LIMSRuntimeException(
					"Error in AnalysisQaEventAction getAnalysisQaEventActionsByAnalysisQaEventId()", e);
		}
	}
	
	public AnalysisQaEventAction getAnalysisQaEventActionByAnalysisQaEventAndAction(AnalysisQaEventAction analysisQaEventAction) throws LIMSRuntimeException {
		AnalysisQaEventAction analQaEventAction = null;
		try
		{
			// Use an expression to read in the AnalysisQaEvent whose 
			// analysis and qaevent is given		
			String sql = "from AnalysisQaEventAction aqea where aqea.analysisQaEvent = :param and aqea.action = :param2";
			Query query = HibernateUtil.getSession().createQuery(sql);
			query.setParameter("param", analysisQaEventAction.getAnalysisQaEvent().getId());
			query.setParameter("param2", analysisQaEventAction.getAction().getId());
			List list = query.list();
			if ((list != null) &&
				!list.isEmpty())
			{
				analQaEventAction = (AnalysisQaEventAction)list.get(0);
			}
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();	

		}
		catch (Exception e)
		{
			//buzilla 2154
			LogEvent.logError("AnalysisQaEventActionDAOImpl","getAnalysisQaEventActionByAnalysisQaEventAndAction()",e.toString());			
			throw new LIMSRuntimeException("Exception occurred in getAnalysisQaEventActionByAnalysisQaEventAndAction", e);
		}
		return analQaEventAction;
		

	}
}