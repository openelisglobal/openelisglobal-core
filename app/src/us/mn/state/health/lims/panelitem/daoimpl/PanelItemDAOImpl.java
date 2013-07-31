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
package us.mn.state.health.lims.panelitem.daoimpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;

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
import us.mn.state.health.lims.panel.valueholder.Panel;
import us.mn.state.health.lims.panelitem.dao.PanelItemDAO;
import us.mn.state.health.lims.panelitem.valueholder.PanelItem;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;

/**
 * @author diane benz
 */
public class PanelItemDAOImpl extends BaseDAOImpl implements PanelItemDAO {

	public void deleteData(List panelItems) throws LIMSRuntimeException {
		//add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			for (int i = 0; i < panelItems.size(); i++) {
				PanelItem data = (PanelItem)panelItems.get(i);
				
				PanelItem oldData = (PanelItem)readPanelItem(data.getId());
				PanelItem newData = new PanelItem();

				String sysUserId = data.getSysUserId();
				String event = IActionConstants.AUDIT_TRAIL_DELETE;
				String tableName = "PANEL_ITEM";
				auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
			}
		}  catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("PanelItemDAOImpl","AuditTrail deleteData()",e.toString());
			throw new LIMSRuntimeException("Error in PanelItem AuditTrail deleteData()", e);
		}  
		
		try {		
			for (int i = 0; i < panelItems.size(); i++) {
				PanelItem data = (PanelItem) panelItems.get(i);	
				//bugzilla 2206
				data = (PanelItem)readPanelItem(data.getId());
				HibernateUtil.getSession().delete(data);
				HibernateUtil.getSession().flush();
				HibernateUtil.getSession().clear();	

			}			
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("PanelItemDAOImpl","deleteData()",e.toString());
			throw new LIMSRuntimeException("Error in PanelItem deleteData()", e);
		} 
	}

	public boolean insertData(PanelItem panelItem) throws LIMSRuntimeException {
		try {	
			// bugzilla 1482 throw Exception if record already exists
			if (duplicatePanelItemExists(panelItem)) {
				throw new LIMSDuplicateRecordException(
						"Duplicate record exists for "
								+ panelItem.getPanelName());
			}
			
			String id = (String)HibernateUtil.getSession().save(panelItem);
			panelItem.setId(id);
			
			//bugzilla 1824 inserts will be logged in history table
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = panelItem.getSysUserId();
			String tableName = "PANEL_ITEM";
			auditDAO.saveNewHistory(panelItem,sysUserId,tableName);
			
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();										
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("PanelItemDAOImpl","insertData()",e.toString());
			throw new LIMSRuntimeException("Error in PanelItem insertData()", e);
		}
		
		return true;
	}

	public void updateData(PanelItem panelItem) throws LIMSRuntimeException {
		// bugzilla 1482 throw Exception if record already exists
		try {
			if (duplicatePanelItemExists(panelItem)) {
				throw new LIMSDuplicateRecordException(
						"Duplicate record exists for "
								+ panelItem.getPanel().getPanelName());
			}
		} catch (Exception e) {
    		//bugzilla 2154
			LogEvent.logError("PanelItemDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException("Error in PanelItem updateData()",
					e);
		}
		
		PanelItem oldData = (PanelItem)readPanelItem(panelItem.getId());
		PanelItem newData = panelItem;

		//add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = panelItem.getSysUserId();
			String event = IActionConstants.AUDIT_TRAIL_UPDATE;
			String tableName = "PANEL_ITEM";
			auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
		}  catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("PanelItemDAOImpl","AuditTrail updateData()",e.toString());
			throw new LIMSRuntimeException("Error in PanelItem AuditTrail updateData()", e);
		}  
			
		try {
			HibernateUtil.getSession().merge(panelItem);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			HibernateUtil.getSession().evict(panelItem);
			HibernateUtil.getSession().refresh(panelItem);			
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("PanelItemDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException("Error in PanelItem updateData()", e);
		}
	}

	public void getData(PanelItem panelItem) throws LIMSRuntimeException {
		try {
			PanelItem data = (PanelItem)HibernateUtil.getSession().get(PanelItem.class, panelItem.getId());
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			if (data != null) {
			  PropertyUtils.copyProperties(panelItem, data);
			} else {
				panelItem.setId(null);
			}
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("PanelItemDAOImpl","getData()",e.toString());
			throw new LIMSRuntimeException("Error in PanelItem getData()", e);
		}
	}

	public List getAllPanelItems() throws LIMSRuntimeException {
		List list = new Vector();
		try {
			//AIS - bugzilla 1776
			String sql = "from PanelItem P order by P.panel.id ";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			//query.setMaxResults(10);
			//query.setFirstResult(3);				
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("PanelItemDAOImpl","getAllPanelItems()",e.toString());
			throw new LIMSRuntimeException("Error in PanelItem getAllPanelItems()", e);
		}

		return list;
	}

	public List getPageOfPanelItems(int startingRecNo) throws LIMSRuntimeException {
		List list = new Vector();
		try {
			// calculate maxRow to be one more than the page size
			int endingRecNo = startingRecNo + (SystemConfiguration.getInstance().getDefaultPageSize() + 1);
			
			//bugzilla 1399
			String sql = "from PanelItem p order by p.panel.panelName, p.testName";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setFirstResult(startingRecNo-1);
			query.setMaxResults(endingRecNo-1); 
					
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("PanelItemDAOImpl","getPageOfPanelItems()",e.toString());
			throw new LIMSRuntimeException("Error in PanelItem getPageOfPanelItems()", e);
		}

		return list;
	}

	public PanelItem readPanelItem(String idString) {
		PanelItem pi = null;
		try {
			pi = (PanelItem)HibernateUtil.getSession().get(PanelItem.class, idString);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("PanelItemDAOImpl","readPanelItem()",e.toString());
			throw new LIMSRuntimeException("Error in PanelItem readPanelItem()", e);
		}			
		
		return pi;
	}

	// this is for autocomplete
	public List getPanelItems(String filter) throws LIMSRuntimeException {
		List list = new Vector(); 
		try {
			String sql = "from PanelItem p where upper(p.methodName) like upper(:param) order by upper(p.methodName)";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setParameter("param", filter+"%");	

			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("PanelItemDAOImpl","getPanelItems()",e.toString());
			throw new LIMSRuntimeException( "Error in PanelItem getPanelItems(String filter)", e);
		}
		return list;	
		
	}

	public List getPanelItemsForPanel(String panelId) throws LIMSRuntimeException {
		List list = new Vector(); 
		try {
			String sql = "from PanelItem p where p.panel.id = :panelId";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setInteger("panelId", Integer.parseInt(panelId));	

			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			LogEvent.logError("PanelItemDAOImpl","getPanelItemsForPanel()",e.toString());
			throw new LIMSRuntimeException( "Error in PanelItem getPanelItemsForPanel(String panelId)", e);
		}
		
		return list;	
		
	}

	
	public List getNextPanelItemRecord(String id) throws LIMSRuntimeException {

		return getNextRecord(id, "PanelItem", PanelItem.class);

	}

	public List getPreviousPanelItemRecord(String id) throws LIMSRuntimeException {

		return getPreviousRecord(id, "PanelItem", PanelItem.class);
	}
	
	//bugzilla 1411
	public Integer getTotalPanelItemCount() throws LIMSRuntimeException {
		return getTotalCount("PanelItem", PanelItem.class);
	}
	
//	bugzilla 1427
	public List getNextRecord(String id, String table, Class clazz) throws LIMSRuntimeException {	
		int currentId= (Integer.valueOf(id)).intValue();
		String tablePrefix = getTablePrefix(table);
		
		List list = new Vector();
		//bugzilla 1908
		int rrn = 0;
		try {			
			//bugzilla 1908 cannot use named query for postgres because of oracle ROWNUM
			//instead get the list in this sortorder and determine the index of record with id = currentId
			String sql = "select pi.id from PanelItem pi " +
					" order by pi.panel.panelName, pi.testName";

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
			LogEvent.logError("PanelItemDAOImpl","getNextRecord()",e.toString());
			throw new LIMSRuntimeException("Error in getNextRecord() for " + table, e);
		} 


		return list;		
	}

	//bugzilla 1427
	public List getPreviousRecord(String id, String table, Class clazz) throws LIMSRuntimeException {		
		int currentId= (Integer.valueOf(id)).intValue();
		String tablePrefix = getTablePrefix(table);
		
		List list = new Vector();
		//bugzilla 1908
		int rrn = 0;
		try {			
			//bugzilla 1908 cannot use named query for postgres because of oracle ROWNUM
			//instead get the list in this sortorder and determine the index of record with id = currentId
			String sql = "select pi.id from PanelItem pi " +
					" order by pi.panel.panelName desc, pi.testName desc";

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
			LogEvent.logError("PanelItemDAOImpl","getPreviousRecord()",e.toString());
			throw new LIMSRuntimeException("Error in getPreviousRecord() for " + table, e);
		} 

		return list;
	}
	
	// bugzilla 1482
	private boolean duplicatePanelItemExists(PanelItem panelItem) throws LIMSRuntimeException {
		try {
			List list = new ArrayList();

			// not case sensitive hemolysis and Hemolysis are considered
			// duplicates
			String sql = "from PanelItem t where trim(lower(t.panel.panelName)) = :param and trim(lower(t.testName)) = :param2 and t.id != :panelItemId";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
			query.setParameter("param", panelItem.getPanel().getPanelName().toLowerCase().trim());
			query.setParameter("param2", panelItem.getTestName().toLowerCase().trim());

	
			// initialize with 0 (for new records where no id has been generated
			// yet
			String panelItemId = "0";
			if (!StringUtil.isNullorNill(panelItem.getId())) {
				panelItemId = panelItem.getId();
			}
			query.setInteger("panelItemId", Integer.parseInt(panelItemId));

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
			LogEvent.logError("PanelItemDAOImpl","duplicatePanelItemExists()",e.toString());
			throw new LIMSRuntimeException(
					"Error in duplicatePanelItemExists()", e);
		}
	}
	
	//bugzilla 2207
	public boolean getDuplicateSortOrderForPanel(PanelItem panelItem) throws LIMSRuntimeException {
		try {
			List list = new ArrayList();

			// not case sensitive hemolysis and Hemolysis are considered
			// duplicates
			String sql = "from PanelItem t where trim(lower(t.panel.panelName)) = :param and t.sortOrder = :sortOrder and t.id != :panelItemId";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
			
			query.setParameter("param", panelItem.getPanelName().toLowerCase().trim());
			query.setInteger("sortOrder", Integer.parseInt( panelItem.getSortOrder()));
			
	      
			// initialize with 0 (for new records where no id has been generated
			// yet
			String panelItemId = "0";
			if (!StringUtil.isNullorNill(panelItem.getId())) {
				panelItemId = panelItem.getId();
			}

			query.setInteger("panelItemId", Integer.parseInt(panelItemId));

			
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();

			if (list.size() > 0) {
				return true;
			} else { 
				return false;
			}

		} catch (Exception e) {
			LogEvent.logError("PanelItemDAOImpl","getDuplicateSortOrderForPanel()",e.toString());
			throw new LIMSRuntimeException(
					"Error in getDuplicateSortOrderForPanel()", e);
	    }
	}
	
	public List getPanelItemByPanel(Panel panel, boolean onlyTestsFullySetup) throws LIMSRuntimeException {
		try {
			String sql = "from PanelItem pi where pi.panel = :param";
			Query query = HibernateUtil.getSession().createQuery(sql);
			query.setParameter("param", panel);

			List list = query.list();
			
			TestDAO testDAO = new TestDAOImpl();
			
			if (onlyTestsFullySetup && list != null && list.size() > 0) {
				Iterator panelItemIterator = list.iterator();
				list = new Vector();
				while(panelItemIterator.hasNext())	{		
					PanelItem panelItem = (PanelItem) panelItemIterator.next();
					String testName = (String)panelItem.getTestName();
					Test test = new Test();
					test.setTestName(testName);
					test = testDAO.getTestByName(test);
					if (test != null && !StringUtil.isNullorNill(test.getId()) && testDAO.isTestFullySetup(test)) {
						list.add(panelItem);
					}
					
				}
			}
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			return list;

		} catch (Exception e) {
			LogEvent.logError("PanelItemDAOImpl","getPanelItemByPanel()",e.toString());
			throw new LIMSRuntimeException("Error in Method getPanelItemByPanel(String filter)",e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PanelItem> getPanelItemByTestId(String testId) throws LIMSRuntimeException {
		String sql = "From PanelItem pi where pi.testId= :testId";
		
		try {
			Query query = HibernateUtil.getSession().createQuery(sql);
			query.setInteger("testId", Integer.parseInt(testId));
			List<PanelItem> panelItems = query.list();
			closeSession();
			return panelItems;
			
		} catch (HibernateException e) {
			handleException(e, "getPanelItemByTestId");
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PanelItem> getPanelItemsForPanelAndItemList(String panelId, List<Integer> testList) throws LIMSRuntimeException{
		String sql = "From PanelItem pi where pi.panel.id = :panelId and pi.test.id in (:testList)";
		try{
			Query query = HibernateUtil.getSession().createQuery(sql);
			query.setInteger("panelId", Integer.parseInt(panelId));
			query.setParameterList("testList", testList);
			List<PanelItem> items = query.list();
			closeSession();
			return items;
		}catch(HibernateException e){
			handleException(e, "getPanelItemsFromPanelAndItemList");
		}
		return null;
	}
}