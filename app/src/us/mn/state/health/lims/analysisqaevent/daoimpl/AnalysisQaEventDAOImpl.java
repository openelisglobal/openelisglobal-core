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
package us.mn.state.health.lims.analysisqaevent.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Query;

import us.mn.state.health.lims.analysisqaevent.dao.AnalysisQaEventDAO;
import us.mn.state.health.lims.analysisqaevent.valueholder.AnalysisQaEvent;
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
public class AnalysisQaEventDAOImpl extends BaseDAOImpl implements AnalysisQaEventDAO {

	public void deleteData(List analysisQaEvents) throws LIMSRuntimeException {
		//add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			for (int i = 0; i < analysisQaEvents.size(); i++) {
				AnalysisQaEvent data = (AnalysisQaEvent)analysisQaEvents.get(i);
			
				AnalysisQaEvent oldData = (AnalysisQaEvent)readAnalysisQaEvent(data.getId());
				AnalysisQaEvent newData = new AnalysisQaEvent();

				String sysUserId = data.getSysUserId();
				String event = IActionConstants.AUDIT_TRAIL_DELETE;
				String tableName = "ANALYSIS_QAEVENT";
				auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
			}
		}  catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("AnalysisQaEventDAOImpl","AuditTrail deleteData()",e.toString());
			throw new LIMSRuntimeException("Error in AnalysisQaEvent AuditTrail deleteData()", e);
		}  
		
		try {		
			for (int i = 0; i < analysisQaEvents.size(); i++) {
				AnalysisQaEvent data = (AnalysisQaEvent) analysisQaEvents.get(i);
				//bugzilla 2206
				data = (AnalysisQaEvent)readAnalysisQaEvent(data.getId());
				HibernateUtil.getSession().delete(data);
				HibernateUtil.getSession().flush();
				HibernateUtil.getSession().clear();
			}			
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("AnalysisQaEventDAOImpl","deleteData()",e.toString());
			throw new LIMSRuntimeException("Error in AnalysisQaEvent deleteData()", e);
		}
	}

	public boolean insertData(AnalysisQaEvent analysisQaEvent) throws LIMSRuntimeException {
		
		try {
			String id = (String)HibernateUtil.getSession().save(analysisQaEvent);
			analysisQaEvent.setId(id);
			
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = analysisQaEvent.getSysUserId();
			String tableName = "ANALYSIS_QAEVENT";
			auditDAO.saveNewHistory(analysisQaEvent,sysUserId,tableName);
			
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
						
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("AnalysisQaEventDAOImpl","insertData()",e.toString());
			throw new LIMSRuntimeException("Error in AnalysisQaEvent insertData()", e);
		}
		
		return true;
	}

	public void updateData(AnalysisQaEvent analysisQaEvent) throws LIMSRuntimeException {
		
		AnalysisQaEvent oldData = (AnalysisQaEvent)readAnalysisQaEvent(analysisQaEvent.getId());
		AnalysisQaEvent newData = analysisQaEvent;

		//add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = analysisQaEvent.getSysUserId();
			String event = IActionConstants.AUDIT_TRAIL_UPDATE;
			String tableName = "ANALYSIS_QAEVENT";
			auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
		}  catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("AnalysisQaEventDAOImpl","AuditTrail insertData()",e.toString());
			throw new LIMSRuntimeException("Error in AnalysisQaEvent AuditTrail updateData()", e);
		}  
						
		try {
			HibernateUtil.getSession().merge(analysisQaEvent);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			HibernateUtil.getSession().evict(analysisQaEvent);
			HibernateUtil.getSession().refresh(analysisQaEvent);
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("AnalysisQaEventDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException("Error in AnalysisQaEvent updateData()", e);
		} 
	}

	public void getData(AnalysisQaEvent analysisQaEvent) throws LIMSRuntimeException {
		try {
			AnalysisQaEvent data = (AnalysisQaEvent)HibernateUtil.getSession().get(AnalysisQaEvent.class, analysisQaEvent.getId());
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			if (data != null) {
				PropertyUtils.copyProperties(analysisQaEvent, data);
			} else {
				analysisQaEvent.setId(null);
			}
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("AnalysisQaEventDAOImpl","getData()",e.toString());
			throw new LIMSRuntimeException("Error in AnalysisQaEvent getData()", e);
		}
	}

	public AnalysisQaEvent readAnalysisQaEvent(String idString) {
		AnalysisQaEvent sp = null;
		try {
			sp = (AnalysisQaEvent)HibernateUtil.getSession().get(AnalysisQaEvent.class, idString);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("AnalysisQaEventDAOImpl","readAnalysisQaEvent()",e.toString());
			throw new LIMSRuntimeException("Error in AnalysisQaEvent readAnalysisQaEvent()", e);
		}			
		
		return sp;
	}
	
	public List getAnalysisQaEventsByAnalysis(AnalysisQaEvent analysisQaEvent) throws LIMSRuntimeException {
		List analysisQaEvents = new ArrayList();
		
		try {
			String sql = "from AnalysisQaEvent aqe where aqe.analysis = :param";
			Query query = HibernateUtil.getSession().createQuery(sql);			
			query.setParameter("param", analysisQaEvent.getAnalysis().getId());		
		
			analysisQaEvents = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();	
			
			return analysisQaEvents;
			
			
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("AnalysisQaEventDAOImpl","getAnalysisQaEventsByAnalysisId()",e.toString());
			throw new LIMSRuntimeException(
					"Error in AnalysisQaEventDAO getAnalysisQaEventsByAnalysisId()", e);
		}
	}
	
	public AnalysisQaEvent getAnalysisQaEventByAnalysisAndQaEvent(AnalysisQaEvent analysisQaEvent) throws LIMSRuntimeException {
		AnalysisQaEvent analQaEvent = null;
		try
		{
			// Use an expression to read in the AnalysisQaEvent whose 
			// analysis and qaevent is given		
			String sql = "from AnalysisQaEvent aqe where aqe.analysis = :param and aqe.qaEvent = :param2";
			Query query = HibernateUtil.getSession().createQuery(sql);
			query.setParameter("param", analysisQaEvent.getAnalysis().getId());
			query.setParameter("param2", analysisQaEvent.getQaEvent().getId());
			List list = query.list();
			if ((list != null) &&
				!list.isEmpty())
			{
				analQaEvent = (AnalysisQaEvent)list.get(0);
			}
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();	

		}
		catch (Exception e)
		{
			//buzilla 2154
			LogEvent.logError("AnalysisQaEventDAOImpl","getAnalysisQaEventByAnalysisAndQaEvent()",e.toString());
			throw new LIMSRuntimeException("Exception occurred in getAnalysisQaEventByAnalysisAndQaEvent", e);
		}
		return analQaEvent;
		

	}

}