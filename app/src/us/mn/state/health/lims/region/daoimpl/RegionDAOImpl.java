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
package us.mn.state.health.lims.region.daoimpl;

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
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.region.dao.RegionDAO;
import us.mn.state.health.lims.region.valueholder.Region;

/**
 * @author diane benz
 */
public class RegionDAOImpl extends BaseDAOImpl implements RegionDAO {

	public void deleteData(List regions) throws LIMSRuntimeException {
		//add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			for (int i = 0; i < regions.size(); i++) {
				Region data = (Region)regions.get(i);
			
				Region oldData = (Region)readRegion(data.getId());
				Region newData = new Region();

				String sysUserId = data.getSysUserId();
				String event = IActionConstants.AUDIT_TRAIL_DELETE;
				String tableName = "REGION";
				auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
			}
		}  catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("RegionDAOImpl","AuditTrail deleteData()",e.toString());
			throw new LIMSRuntimeException("Error in Region AuditTrail deleteData()", e);
		}  
		
		try {				
			for (int i = 0; i < regions.size(); i++) {
				Region data = (Region) regions.get(i);
				//bugzilla 2206
				data = (Region)readRegion(data.getId());
				HibernateUtil.getSession().delete(data);
				HibernateUtil.getSession().flush();
				HibernateUtil.getSession().clear();			
			}			
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("RegionDAOImpl","deleteData()",e.toString());
			throw new LIMSRuntimeException("Error in Region deleteData()", e);
		} 
	}

	public boolean insertData(Region region) throws LIMSRuntimeException {
		try {
			// bugzilla 1482 throw Exception if record already exists
			if (duplicateRegionExists(region)) {
				throw new LIMSDuplicateRecordException(
						"Duplicate record exists for "
								+ region.getRegion());
			}
			
			String id = (String)HibernateUtil.getSession().save(region);
			region.setId(id);
			
			//bugzilla 1824 inserts will be logged in history table
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = region.getSysUserId();
			String tableName = "REGION";
			auditDAO.saveNewHistory(region,sysUserId,tableName);
			
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();						
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("RegionDAOImpl","insertData()",e.toString());
			throw new LIMSRuntimeException("Error in Region insertData()", e);
		}
		
		return true;
	}

	public void updateData(Region region) throws LIMSRuntimeException {
		// bugzilla 1482 throw Exception if record already exists
		try {
			if (duplicateRegionExists(region)) {
				throw new LIMSDuplicateRecordException(
						"Duplicate record exists for "
								+ region.getRegion());
			}
		} catch (Exception e) {
    		//bugzilla 2154
			LogEvent.logError("RegionDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException("Error in Region updateData()",
					e);
		}
		
		Region oldData = (Region)readRegion(region.getId());
		Region newData = region;

		//add to audit trail
		try {			
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = region.getSysUserId();
			String event = IActionConstants.AUDIT_TRAIL_UPDATE;
			String tableName = "REGION";
			auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
		}  catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("RegionDAOImpl","AuditTrail updateData()",e.toString());
			throw new LIMSRuntimeException("Error in Region AuditTrail updateData()", e);
		}  
					
		try {			
			HibernateUtil.getSession().merge(region);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			HibernateUtil.getSession().evict(region);
			HibernateUtil.getSession().refresh(region);			
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("RegionDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException("Error in Region updateData()", e);
		}
	}

	public void getData(Region region) throws LIMSRuntimeException {
		try {
			Region reg = (Region)HibernateUtil.getSession().get(Region.class, region.getId());
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			if (reg != null) {
			  PropertyUtils.copyProperties(region, reg);
			} else {
				region.setId(null);
			}
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("RegionDAOImpl","getData()",e.toString());
			throw new LIMSRuntimeException("Error in Region getData()", e);
		} 
	}

	public List getAllRegions() throws LIMSRuntimeException {
		List list = new Vector();
		try {
			String sql = "from Region";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			//query.setMaxResults(10);
			//query.setFirstResult(3);				
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("RegionDAOImpl","getAllRegions()",e.toString());
			throw new LIMSRuntimeException("Error in Region getAllRegions()", e);
		}
		return list;
	}

	public List getPageOfRegions(int startingRecNo) throws LIMSRuntimeException {
		List list = new Vector();
		try {
			int endingRecNo = startingRecNo 
			+ (SystemConfiguration.getInstance().getDefaultPageSize() + 1);	
			
			//bugzilla 1399
			String sql = "from Region r order by r.region";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setFirstResult(startingRecNo-1);
			query.setMaxResults(endingRecNo-1); 
					
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("RegionDAOImpl","getPageOfRegions()",e.toString());
			throw new LIMSRuntimeException("Error in Region getPageOfRegions()", e);
		}

		return list;
	}

	public Region readRegion(String idString) {
		Region region = null;
		try {
			region = (Region)HibernateUtil.getSession().get(Region.class, idString);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("RegionDAOImpl","readRegion()",e.toString());
			throw new LIMSRuntimeException("Error in Region readRegion()", e);
		}		
		return region;
	}

	public List getNextRegionRecord(String id) throws LIMSRuntimeException {

		return getNextRecord(id, "Region", Region.class);
	}

	public List getPreviousRegionRecord(String id) throws LIMSRuntimeException {

		return getPreviousRecord(id, "Region", Region.class);
	}

	//bugzilla 1411
	public Integer getTotalRegionCount() throws LIMSRuntimeException {
		return getTotalCount("Region", Region.class);
	}
	
	//overriding BaseDAOImpl bugzilla 1427 pass in name not id
	public List getNextRecord(String id, String table, Class clazz) throws LIMSRuntimeException {	
				
		List list = new Vector();
		try {			
			String sql = "from "+table+" t where region >= "+ enquote(id) + " order by t.region";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setFirstResult(1);
			query.setMaxResults(2); 	
			
			list = query.list();		
			
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("RegionDAOImpl","getNextRecord()",e.toString());
			throw new LIMSRuntimeException("Error in getNextRecord() for " + table, e);
		}
		
		return list;		
	}

	//overriding BaseDAOImpl bugzilla 1427 pass in name not id
	public List getPreviousRecord(String id, String table, Class clazz) throws LIMSRuntimeException {		
		
		List list = new Vector();
		try {			
			String sql = "from "+table+" t order by t.region desc where region <= "+ enquote(id);
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setFirstResult(1);
			query.setMaxResults(2); 	
			
			list = query.list();					
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("RegionDAOImpl","getPreviousRecord()",e.toString());
			throw new LIMSRuntimeException("Error in getPreviousRecord() for " + table, e);
		} 

		return list;
	}
	
	// bugzilla 1482
	private boolean duplicateRegionExists(Region region) throws LIMSRuntimeException {
		try {

			List list = new ArrayList();

			// not case sensitive hemolysis and Hemolysis are considered
			// duplicates
			String sql = "from Region t where trim(lower(t.region)) = :param and t.id != :param2";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
			query.setParameter("param", region.getRegion().toLowerCase().trim());
	
			// initialize with 0 (for new records where no id has been generated
			// yet
			String regionId = "0";
			if (!StringUtil.isNullorNill(region.getId())) {
				regionId = region.getId();
			}
			query.setParameter("param2", regionId);

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
			LogEvent.logError("RegionDAOImpl","duplicateRegionExists()",e.toString());
			throw new LIMSRuntimeException(
					"Error in duplicateRegionExists()", e);
		}
	}
}