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
package us.mn.state.health.lims.sampledomain.daoimpl;

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
import us.mn.state.health.lims.sampledomain.dao.SampleDomainDAO;
import us.mn.state.health.lims.sampledomain.valueholder.SampleDomain;

/**
 * @author diane benz
 */
public class SampleDomainDAOImpl extends BaseDAOImpl implements SampleDomainDAO {

	public void deleteData(List sampleDomains) throws LIMSRuntimeException {
		//add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			for (int i = 0; i < sampleDomains.size(); i++) {
				SampleDomain data = (SampleDomain)sampleDomains.get(i);
			
				SampleDomain oldData = (SampleDomain)readSampleDomain(data.getId());
				SampleDomain newData = new SampleDomain();

				String sysUserId = data.getSysUserId();
				String event = IActionConstants.AUDIT_TRAIL_DELETE;
				String tableName = "SAMPLE_DOMAIN";
				auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
			}
		}  catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SampleDomainDAOImpl","AuditTrail deleteData()",e.toString());	
			throw new LIMSRuntimeException("Error in SampleDomain AuditTrail deleteData()", e);
		}  
		
		try {		
			for (int i = 0; i < sampleDomains.size(); i++) {
				SampleDomain data = (SampleDomain) sampleDomains.get(i);
				//bugzilla 2206
				data = (SampleDomain)readSampleDomain(data.getId());
				HibernateUtil.getSession().delete(data);
				HibernateUtil.getSession().flush();
				HibernateUtil.getSession().clear();			
			}			
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SampleDomainDAOImpl","deleteData()",e.toString());	
			throw new LIMSRuntimeException("Error in SampleDomain deleteData()", e);
		}
	}

	public boolean insertData(SampleDomain sampleDomain) throws LIMSRuntimeException {
		try {
			// bugzilla 1482 throw Exception if record already exists
			if (duplicateSampleDomainExists(sampleDomain)) {
				throw new LIMSDuplicateRecordException(
						"Duplicate record exists for "
								+ sampleDomain.getDescription());
			}
			
			String id = (String)HibernateUtil.getSession().save(sampleDomain);
			sampleDomain.setId(id);
			
			//bugzilla 1824 inserts will be logged in history table
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = sampleDomain.getSysUserId();
			String tableName = "SAMPLE_DOMAIN";
			auditDAO.saveNewHistory(sampleDomain,sysUserId,tableName);
			
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();					
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SampleDomainDAOImpl","insertData()",e.toString());	
			throw new LIMSRuntimeException("Error in SampleDomain insertData()", e);
		}
		
		return true;
	}

	public void updateData(SampleDomain sampleDomain) throws LIMSRuntimeException {
		// bugzilla 1482 throw Exception if record already exists
		try {
			if (duplicateSampleDomainExists(sampleDomain)) {
				throw new LIMSDuplicateRecordException(
						"Duplicate record exists for "
								+ sampleDomain.getDescription());
			}
		} catch (Exception e) {
    		//bugzilla 2154
			LogEvent.logError("SampleDomainDAOImpl","updateData()",e.toString());	
			throw new LIMSRuntimeException("Error in SampleDomain updateData()",
					e);
		}
		
		SampleDomain oldData = (SampleDomain)readSampleDomain(sampleDomain.getId());
		SampleDomain newData = sampleDomain;

		//add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = sampleDomain.getSysUserId();
			String event = IActionConstants.AUDIT_TRAIL_UPDATE;
			String tableName = "SAMPLE_DOMAIN";
			auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
		}  catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SampleDomainDAOImpl","AuditTrail updateData()",e.toString());
			throw new LIMSRuntimeException("Error in SampleDomain AuditTrail updateData()", e);
		}  
				
		try {
			HibernateUtil.getSession().merge(sampleDomain);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			HibernateUtil.getSession().evict(sampleDomain);
			HibernateUtil.getSession().refresh(sampleDomain);			
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SampleDomainDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException("Error in SampleDomain updateData()", e);
		} 
	}

	public void getData(SampleDomain sampleDomain) throws LIMSRuntimeException {
		try {
			SampleDomain sampleDom = (SampleDomain)HibernateUtil.getSession().get(SampleDomain.class, sampleDomain.getId());
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			if (sampleDom != null) {
 				//System.out.println("Just read prog " + sampleDomain.getId() + " "
				//		+ sampleDom.getDescription());
				PropertyUtils.copyProperties(sampleDomain, sampleDom);
			} else {
				sampleDomain.setId(null);
			}
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SampleDomainDAOImpl","getData()",e.toString());
			throw new LIMSRuntimeException("Error in SampleDomain getData()", e);
		}
	}

	public List getAllSampleDomains() throws LIMSRuntimeException {
		List list = new Vector();
		try {
			String sql = "from SampleDomain";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			//query.setMaxResults(10);
			//query.setFirstResult(3);
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SampleDomainDAOImpl","getAllSampleDomains()",e.toString());
			throw new LIMSRuntimeException("Error in SampleDomain getAllSampleDomains()", e);
		}

		return list;
	}

	public List getPageOfSampleDomains(int startingRecNo) throws LIMSRuntimeException {
		List list = new Vector();
		try {
			// calculate maxRow to be one more than the page size
			int endingRecNo = startingRecNo + (SystemConfiguration.getInstance().getDefaultPageSize() + 1);
			
			//bugzilla 1399
			String sql = "from SampleDomain s order by s.description";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setFirstResult(startingRecNo-1);
			query.setMaxResults(endingRecNo-1); 					
			
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SampleDomainDAOImpl","getPageOfSampleDomains()",e.toString());
			throw new LIMSRuntimeException("Error in SampleDomain getPageOfSampleDomains()", e);
		}

		return list;
	}

	public SampleDomain readSampleDomain(String idString) {
		SampleDomain sd = null;
		try {
			sd = (SampleDomain)HibernateUtil.getSession().get(SampleDomain.class, idString);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SampleDomainDAOImpl","readSampleDomain()",e.toString());
			throw new LIMSRuntimeException("Error in SampleDomain readSampleDomain()", e);
		}			
		
		return sd;
	}

	public List getNextSampleDomainRecord(String id) throws LIMSRuntimeException {

		return getNextRecord(id, "SampleDomain", SampleDomain.class);

	}

	public List getPreviousSampleDomainRecord(String id) throws LIMSRuntimeException {

		return getPreviousRecord(id, "SampleDomain", SampleDomain.class);
	}
	
	//bugzilla 1411
	public Integer getTotalSampleDomainCount() throws LIMSRuntimeException {
		return getTotalCount("SampleDomain", SampleDomain.class);
	}
	
	//overriding BaseDAOImpl bugzilla 1427 pass in name not id
	public List getNextRecord(String id, String table, Class clazz) throws LIMSRuntimeException {	
				
		List list = new Vector();
		try {			
			String sql = "from "+table+" t where domain_description >= "+ enquote(id) + " order by t.description";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setFirstResult(1);
			query.setMaxResults(2); 	
			
			list = query.list();		
			
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SampleDomainDAOImpl","getNextRecord()",e.toString());
			throw new LIMSRuntimeException("Error in getNextRecord() for " + table, e);
		}
		
		return list;		
	}

	//overriding BaseDAOImpl bugzilla 1427 pass in name not id
	public List getPreviousRecord(String id, String table, Class clazz) throws LIMSRuntimeException {		
		
		List list = new Vector();
		try {			
			String sql = "from "+table+" t order by t.description desc where domain_description <= "+ enquote(id);
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setFirstResult(1);
			query.setMaxResults(2); 	
			
			list = query.list();					
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SampleDomainDAOImpl","getPreviousRecord()",e.toString());
			throw new LIMSRuntimeException("Error in getPreviousRecord() for " + table, e);
		} 

		return list;
	}
	
	// bugzilla 1482
	private boolean duplicateSampleDomainExists(SampleDomain sampleDomain) throws LIMSRuntimeException {
		try {

			List list = new ArrayList();

			// not case sensitive hemolysis and Hemolysis are considered
			// duplicates
			String sql = "from SampleDomain t where (trim(lower(t.description)) = :param and t.id != :param2) or (trim(lower(t.code)) = :param3 and t.id != :param2)";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
			query.setParameter("param", sampleDomain.getDescription().toLowerCase().trim());
			query.setParameter("param3", sampleDomain.getCode().toLowerCase().trim());
			
			// initialize with 0 (for new records where no id has been generated
			// yet
			String sampleDomainId = "0";
			if (!StringUtil.isNullorNill(sampleDomain.getId())) {
				sampleDomainId = sampleDomain.getId();
			}
			query.setParameter("param2", sampleDomainId);

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
			LogEvent.logError("SampleDomainDAOImpl","duplicateSampleDomainExists()",e.toString());
			throw new LIMSRuntimeException(
					"Error in duplicateSampleDomainExists()", e);
		}
	}
}