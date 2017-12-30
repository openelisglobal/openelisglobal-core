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
package us.mn.state.health.lims.action.daoimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Query;

import us.mn.state.health.lims.action.dao.ActionDAO;
import us.mn.state.health.lims.action.valueholder.Action;
import us.mn.state.health.lims.audittrail.dao.AuditTrailDAO;
import us.mn.state.health.lims.audittrail.daoimpl.AuditTrailDAOImpl;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.daoimpl.BaseDAOImpl;
import us.mn.state.health.lims.common.exception.LIMSDuplicateRecordException;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.hibernate.HibernateUtil;

/**
 * @author diane benz
 */
public class ActionDAOImpl extends BaseDAOImpl implements ActionDAO {

	public void deleteData(List actions) throws LIMSRuntimeException {
		//add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			for (int i = 0; i < actions.size(); i++) {
				Action data = (Action)actions.get(i);
			
				Action oldData = (Action)readAction(data.getId());
				Action newData = new Action();

				String sysUserId = data.getSysUserId();
				String event = IActionConstants.AUDIT_TRAIL_DELETE;
				String tableName = "ACTION";
				auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
			}
		}  catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ActionDAOImpl","AuditTrail deleteData()",e.toString());
			throw new LIMSRuntimeException("Error in Action AuditTrail deleteData()", e);
		}  
		
		try {		
			for (int i = 0; i < actions.size(); i++) {
				Action data = (Action) actions.get(i);	
				//bugzilla 2206
				data = (Action)readAction(data.getId());
				HibernateUtil.getSession().delete(data);
				HibernateUtil.getSession().flush();
				HibernateUtil.getSession().clear();			
			}			
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ActionDAOImpl","deleteData()",e.toString());
			throw new LIMSRuntimeException("Error in Action deleteData()", e);
		}
	}

	public boolean insertData(Action action) throws LIMSRuntimeException {
		try {
			// bugzilla 1482 throw Exception if record already exists
			if (duplicateActionExists(action)) {
				throw new LIMSDuplicateRecordException(
						"Duplicate record exists for action type, code"
								+ action.getType() + " " + action.getCode());
			}
			
			String id = (String)HibernateUtil.getSession().save(action);
			action.setId(id);
			
			//bugzilla 1824 inserts will be logged in history table
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = action.getSysUserId();
			String tableName = "ACTION";
			auditDAO.saveNewHistory(action,sysUserId,tableName);
			
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ActionDAOImpl","insertData()",e.toString());
			throw new LIMSRuntimeException("Error in Action insertData()", e);
		}
		
		return true;
	}

	public void updateData(Action action) throws LIMSRuntimeException {
		// bugzilla 1482 throw Exception if record already exists
		try {
			if (duplicateActionExists(action)) {
				throw new LIMSDuplicateRecordException(
						"Duplicate record exists for action type, code"
								+ action.getType() + action.getCode());
			}
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ActionDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException("Error in Action updateData()",
					e);
		}
		
		Action oldData = (Action)readAction(action.getId());
		Action newData = action;

		//add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = action.getSysUserId();
			String event = IActionConstants.AUDIT_TRAIL_UPDATE;
			String tableName = "ACTION";
			auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
		}  catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ActionDAOImpl","AuditTrail updateData()",e.toString());
			throw new LIMSRuntimeException("Error in Action AuditTrail updateData()", e);
		}  
			
		try {
			HibernateUtil.getSession().merge(action);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			HibernateUtil.getSession().evict(action);
			HibernateUtil.getSession().refresh(action);			
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ActionDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException("Error in Action updateData()", e);
		}
	}

	public void getData(Action action) throws LIMSRuntimeException {
		try {
			Action act = (Action)HibernateUtil.getSession().get(Action.class, action.getId());
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			if (act != null) {
			  PropertyUtils.copyProperties(action, act);
			} else {
				action.setId(null);
			}
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ActionDAOImpl","getData()",e.toString());
			throw new LIMSRuntimeException("Error in Action getData()", e);
		}
	}

	public List getAllActions() throws LIMSRuntimeException {
		List list = new Vector();
		try {
			String sql = "from Action";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
				
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ActionDAOImpl","getAllActions()",e.toString());
			throw new LIMSRuntimeException("Error in Action getAllActions()",e);
		}

		return list;
	}
	// bugzilla 2503
	public List getAllActionsByFilter ( String filterString ) throws LIMSRuntimeException {
		List list = new Vector();
        String wildCard = "*";
        String newSearchStr;
        String sql;
        
        try {  
       	     int wCdPosition = filterString.indexOf (wildCard);
      
             if (wCdPosition == -1)  // no wild card looking for exact match
             {
           	     newSearchStr = filterString.toLowerCase().trim();
                 sql = "from Action a where trim(lower (a.description)) = :param  order by a.description";
             }
	          else
	          {
	             newSearchStr = filterString.replace(wildCard, "%").toLowerCase().trim();
	             sql = "from Action a where trim(lower (a.description)) like :param  order by a.description";
	          }
	          org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
	          query.setParameter("param", newSearchStr);

	          list = query.list();
	          HibernateUtil.getSession().flush();
	          HibernateUtil.getSession().clear();
              }       catch (Exception e) {
	                  e.printStackTrace();
	                  throw new LIMSRuntimeException(
			             "Error in Action getAllActionsByFilter()", e);
           }

		return list;
	}

	public List getPageOfActions(int startingRecNo) throws LIMSRuntimeException {
		List list = new Vector();
		try {
			// calculate maxRow to be one more than the page size
			int endingRecNo = startingRecNo + (SystemConfiguration.getInstance().getDefaultPageSize() + 1);
			
			//bugzilla 1399
			String sql = "from Action a order by a.type, a.code";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setFirstResult(startingRecNo-1);
			query.setMaxResults(endingRecNo-1); 
					
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ActionDAOImpl","getPageOfActions()",e.toString());
			throw new LIMSRuntimeException("Error in Action getPageOfActions()", e);
		}

		return list;
	}

	public Action readAction(String idString) {
		Action action = null;
		try {
			action = (Action)HibernateUtil.getSession().get(Action.class, idString);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ActionDAOImpl","readAction()",e.toString());
			throw new LIMSRuntimeException("Error in Action readAction()", e);
		}			
		
		return action;
	}
	
	public List getNextActionRecord(String id) throws LIMSRuntimeException {

		return getNextRecord(id, "Action", Action.class);

	}

	public List getPreviousActionRecord(String id) throws LIMSRuntimeException {

		return getPreviousRecord(id, "Action", Action.class);
	}
	
	//bugzilla 1411
	public Integer getTotalActionCount() throws LIMSRuntimeException {
		return getTotalCount("Action", Action.class);
	}
	
	//overriding BaseDAOImpl bugzilla 1427 pass in name not id
	public List getNextRecord(String id, String table, Class clazz) throws LIMSRuntimeException {	
		int currentId= (Integer.valueOf(id)).intValue();
		String tablePrefix = getTablePrefix(table);
		
		List list = new Vector();
		//bugzilla 1908
		int rrn = 0;
		try {			
			//bugzilla 1908 cannot use named query for postgres because of oracle ROWNUM
			//instead get the list in this sortorder and determine the index of record with id = currentId
			String sql = "select act.id from Action act " +
			          " order by act.type, act.code";


 			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			rrn = list.indexOf(String.valueOf(currentId));

			list = HibernateUtil.getSession().getNamedQuery(tablePrefix + "getNext")
			.setFirstResult(rrn + 1)
			.setMaxResults(2)
			.list(); 		
			
							
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ActionDAOImpl","getNextRecord()",e.toString());
			throw new LIMSRuntimeException("Error in getNextRecord() for " + table, e);
		} 


		return list;		
	}

	//overriding BaseDAOImpl bugzilla 1427 pass in name not id
	public List getPreviousRecord(String id, String table, Class clazz) throws LIMSRuntimeException {		
		int currentId= (Integer.valueOf(id)).intValue();
		String tablePrefix = getTablePrefix(table);
		
		List list = new Vector();
		//bugzilla 1908
		int rrn = 0;
		try {			
			//bugzilla 1908 cannot use named query for postgres because of oracle ROWNUM
			//instead get the list in this sortorder and determine the index of record with id = currentId
			String sql = "select act.id from Action act " +
					" order by act.type desc, act.code desc";

 			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			rrn = list.indexOf(String.valueOf(currentId));

			list = HibernateUtil.getSession().getNamedQuery(tablePrefix + "getPrevious")
			.setFirstResult(rrn + 1)
			.setMaxResults(2)
			.list(); 		
			
							
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ActionDAOImpl","getPreviousRecord()",e.toString());
			throw new LIMSRuntimeException("Error in getPreviousRecord() for " + table, e);
		} 

		return list;
	}
	
	// bugzilla 1482
	private boolean duplicateActionExists(Action action) throws LIMSRuntimeException {
		try {

			List list = new ArrayList();

			// not case sensitive hemolysis and Hemolysis are considered
			// duplicates
			String sql = "from Action t where (trim(lower(t.type)) = :param and trim(lower(t.code)) = :param2 and t.id != :param4) or (trim(lower(t.type)) = :param and trim(lower(t.description)) = :param3 and t.id != :param4)";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
			query.setParameter("param", action.getType().toLowerCase().trim());
			query.setParameter("param2", action.getCode().toLowerCase().trim());
			query.setParameter("param3", action.getDescription().toLowerCase().trim());
			
	
			// initialize with 0 (for new records where no id has been generated
			// yet
			String actionId = "0";
			if (!StringUtil.isNullorNill(action.getId())) {
				actionId = action.getId();
			}
			query.setParameter("param4", actionId);

			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();

			if (list.size() > 0) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ActionDAOImpl","duplicateActionExists()",e.toString());
			throw new LIMSRuntimeException(
					"Error in duplicateActionExists()", e);
		}
	}
	
	public Action getActionByCode(Action action) throws LIMSRuntimeException {
		Action act = null;
		try
		{
			// Use an expression to read in the AnalysisQaEvent whose 
			// analysis and qaevent is given		
			String sql = "from Action act where act.code = :param";
			Query query = HibernateUtil.getSession().createQuery(sql);
			query.setParameter("param", action.getCode());
			List list = query.list();
			if ((list != null) &&
				!list.isEmpty())
			{
				act = (Action)list.get(0);
			}
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();	

		}
		catch (Exception e)
		{
			//bugzilla 2154
			LogEvent.logError("ActionDAOImpl","getActionByCode()",e.toString());
			throw new LIMSRuntimeException("Exception occurred in getActionByCode", e);
		}
		return act;
		

	}

}