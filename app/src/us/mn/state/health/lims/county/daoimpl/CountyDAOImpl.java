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
package us.mn.state.health.lims.county.daoimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.beanutils.PropertyUtils;

import us.mn.state.health.lims.audittrail.dao.AuditTrailDAO;
import us.mn.state.health.lims.audittrail.daoimpl.AuditTrailDAOImpl;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.daoimpl.BaseDAOImpl;
import us.mn.state.health.lims.common.exception.LIMSDuplicateRecordException;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.county.dao.CountyDAO;
import us.mn.state.health.lims.county.valueholder.County;
import us.mn.state.health.lims.hibernate.HibernateUtil;

/**
 * @author diane benz
 */
public class CountyDAOImpl extends BaseDAOImpl implements CountyDAO {

	public void deleteData(List counties) throws LIMSRuntimeException {
		//add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			for (int i = 0; i < counties.size(); i++) {
				County data = (County)counties.get(i);
			
				County oldData = (County)readCounty(data.getId());
				County newData = new County();

				String sysUserId = data.getSysUserId();
				String event = IActionConstants.AUDIT_TRAIL_DELETE;
				String tableName = "COUNTY";
				auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
			}
		}  catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("CountyDAOImpl","AuditTrail deleteData()",e.toString());
			throw new LIMSRuntimeException("Error in County AuditTrail deleteData()", e);
		}  
		
		try {		
			for (int i = 0; i < counties.size(); i++) {
				County data = (County) counties.get(i);
				//bugzilla 2206
				data = (County)readCounty(data.getId());
				HibernateUtil.getSession().delete(data);	
				HibernateUtil.getSession().flush();
				HibernateUtil.getSession().clear();				
			}			
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("CountyDAOImpl","deleteData()",e.toString());
			throw new LIMSRuntimeException("Error in County deleteData()", e);
		} 
	}

	public boolean insertData(County county) throws LIMSRuntimeException {
		try {
			// bugzilla 1482 throw Exception if record already exists
			if (duplicateCountyExists(county)) {
				throw new LIMSDuplicateRecordException(
						"Duplicate record exists for "
								+ county.getCounty());
			}
			
			String id = (String)HibernateUtil.getSession().save(county);
			county.setId(id);
			
			//bugzilla 1824 inserts will be logged in history table
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = county.getSysUserId();
			String tableName = "COUNTY";
			auditDAO.saveNewHistory(county,sysUserId,tableName);
			
			HibernateUtil.getSession().flush();	
			HibernateUtil.getSession().clear();						
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("CountyDAOImpl","insertData()",e.toString());
			throw new LIMSRuntimeException("Error in County insertData()", e);
		} 
		
		return true;
	}

	public void updateData(County county) throws LIMSRuntimeException {
		// bugzilla 1482 throw Exception if record already exists
		try {
			if (duplicateCountyExists(county)) {
				throw new LIMSDuplicateRecordException(
						"Duplicate record exists for "
								+ county.getCounty());
			}
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("CountyDAOImpl","updateData()",e.toString());    		
			throw new LIMSRuntimeException("Error in County updateData()",
					e);
		}
		County oldData = (County)readCounty(county.getId());
		County newData = county;

		//add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = county.getSysUserId();
			String event = IActionConstants.AUDIT_TRAIL_UPDATE;
			String tableName = "COUNTY";
			auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
		}  catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("CountyDAOImpl","AuditTrail updateData()",e.toString());
			throw new LIMSRuntimeException("Error in County AuditTrail updateData()", e);
		}  
		
		try {
			HibernateUtil.getSession().merge(county);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			HibernateUtil.getSession().evict(county);
			HibernateUtil.getSession().refresh(county);			
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("CountyDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException("Error in County updateData()", e);
		}
	}

	public void getData(County county) throws LIMSRuntimeException {
		try {
			County co = (County)HibernateUtil.getSession().get(County.class, county.getId());
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();			
			if (co != null) {
			  PropertyUtils.copyProperties(county, co);
			} else {
				county.setId(null);
			}
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("CountyDAOImpl","getData()",e.toString());
			throw new LIMSRuntimeException("Error in County getData()", e);
		}
	}

	public List getAllCountys() throws LIMSRuntimeException {
		List list = new Vector();
		try {
			String sql = "from County";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			//query.setMaxResults(10);
			//query.setFirstResult(3);

			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();			
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("CountyDAOImpl","getAllCountys()",e.toString());
			throw new LIMSRuntimeException("Error in County getAllCountys()", e);
		}

		return list;
	}

	public List getPageOfCountys(int startingRecNo) throws LIMSRuntimeException {
		List list = new Vector();
		try {			
			// calculate maxRow to be one more than the page size
			int endingRecNo = startingRecNo 
			+ (SystemConfiguration.getInstance().getDefaultPageSize() + 1);
			
			//bugzilla 1399
			String sql = "from County c order by c.county";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setFirstResult(startingRecNo-1);
			query.setMaxResults(endingRecNo-1); 
			
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("CountyDAOImpl","getPageOfCountys()",e.toString());
			throw new LIMSRuntimeException("Error in County getPageOfCountys()", e);
		}

		return list;
	}

	public County readCounty(String idString) {
		County county = null;
		try {
			county = (County)HibernateUtil.getSession().get(County.class, idString);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("CountyDAOImpl","readCounty()",e.toString());
			throw new LIMSRuntimeException("Error in County readCounty()", e);
		}				
	
		return county;
	}
	
	public List getNextCountyRecord(String id) throws LIMSRuntimeException {

		return getNextRecord(id, "County", County.class);
	}

	public List getPreviousCountyRecord(String id) throws LIMSRuntimeException {

		return getPreviousRecord(id, "County", County.class);

	}
	
	//bugzilla 1411
	public Integer getTotalCountyCount() throws LIMSRuntimeException {
		return getTotalCount("County", County.class);
	}
	
	//overriding BaseDAOImpl bugzilla 1427 pass in name not id
	public List getNextRecord(String id, String table, Class clazz) throws LIMSRuntimeException {	
				
		List list = new Vector();
		try {			
			String sql = "from "+table+" t where county >= "+ enquote(id) + " order by t.county";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setFirstResult(1);
			query.setMaxResults(2); 	
			
			list = query.list();		
			
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("CountyDAOImpl","getNextRecord()",e.toString());
			throw new LIMSRuntimeException("Error in getNextRecord() for " + table, e);
		}
		
		return list;		
	}

	//overriding BaseDAOImpl bugzilla 1427 pass in name not id
	public List getPreviousRecord(String id, String table, Class clazz) throws LIMSRuntimeException {		
		
		List list = new Vector();
		try {			
			String sql = "from "+table+" t order by t.county desc where county <= "+ enquote(id);
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setFirstResult(1);
			query.setMaxResults(2); 	
			
			list = query.list();					
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("CountyDAOImpl","getPreviousRecord()",e.toString());
			throw new LIMSRuntimeException("Error in getPreviousRecord() for " + table, e);
		} 

		return list;
	}
	
	// bugzilla 1482
	private boolean duplicateCountyExists(County county) throws LIMSRuntimeException {
		try {

			List list = new ArrayList();

			// not case sensitive hemolysis and Hemolysis are considered
			// duplicates
			String sql = "from County t where trim(lower(t.county)) = :param and t.region.id = :param2 and t.id != :param3";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
			query.setParameter("param", county.getCounty().toLowerCase().trim());
			query.setParameter("param2", county.getRegion().getId());

			// initialize with 0 (for new records where no id has been generated
			// yet
			String countyId = "0";
			if (!StringUtil.isNullorNill(county.getId())) {
				countyId = county.getId();
			}
			query.setParameter("param3", countyId);

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
			LogEvent.logError("CountyDAOImpl","duplicateCountyExists()",e.toString());
			throw new LIMSRuntimeException(
					"Error in duplicateCountyExists()", e);
		}
	}
}