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
package us.mn.state.health.lims.sourceofsample.daoimpl;

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
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.sourceofsample.dao.SourceOfSampleDAO;
import us.mn.state.health.lims.sourceofsample.valueholder.SourceOfSample;

/**
 * @author diane benz
 */
public class SourceOfSampleDAOImpl extends BaseDAOImpl implements
		SourceOfSampleDAO {

	public void deleteData(List sourceOfSamples) throws LIMSRuntimeException {
		// add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			for (int i = 0; i < sourceOfSamples.size(); i++) {
				SourceOfSample data = (SourceOfSample) sourceOfSamples.get(i);

				SourceOfSample oldData = (SourceOfSample) readSourceOfSample(data
						.getId());
				SourceOfSample newData = new SourceOfSample();

				String sysUserId = data.getSysUserId();
				String event = IActionConstants.AUDIT_TRAIL_DELETE;
				String tableName = "SOURCE_OF_SAMPLE";
				auditDAO.saveHistory(newData, oldData, sysUserId, event,
						tableName);
			}
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SourceOfSampleDAOImpl","AuditTrail deleteData()",e.toString());	
			throw new LIMSRuntimeException(
					"Error in SourceOfSample AuditTrail deleteData()", e);
		}

		try {
			for (int i = 0; i < sourceOfSamples.size(); i++) {
				SourceOfSample data = (SourceOfSample) sourceOfSamples.get(i);
				//bugzilla 2206
				data = (SourceOfSample)readSourceOfSample(data.getId());
				HibernateUtil.getSession().delete(data);
				HibernateUtil.getSession().flush();
				HibernateUtil.getSession().clear();
			}
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SourceOfSampleDAOImpl","deleteData()",e.toString());	
			throw new LIMSRuntimeException(
					"Error in SourceOfSample deleteData()", e);
		}
	}

	public boolean insertData(SourceOfSample sourceOfSample)
			throws LIMSRuntimeException {

		try {
			// bugzilla 1482 throw Exception if record already exists
			if (duplicateSourceOfSampleExists(sourceOfSample)) {
				throw new LIMSDuplicateRecordException(
						"Duplicate record exists for "
								+ sourceOfSample.getDescription());
			}
			
			String id = (String) HibernateUtil.getSession()
					.save(sourceOfSample);
			sourceOfSample.setId(id);
			
			//bugzilla 1824 inserts will be logged in history table
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = sourceOfSample.getSysUserId();
			String tableName = "SOURCE_OF_SAMPLE";
			auditDAO.saveNewHistory(sourceOfSample,sysUserId,tableName);
			
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();

		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SourceOfSampleDAOImpl","insertData()",e.toString());	
			throw new LIMSRuntimeException(
					"Error in SourceOfSample insertData()", e);
		}

		return true;
	}

	public void updateData(SourceOfSample sourceOfSample)
			throws LIMSRuntimeException {
		// bugzilla 1482 throw Exception if record already exists
		try {
			if (duplicateSourceOfSampleExists(sourceOfSample)) {
				throw new LIMSDuplicateRecordException(
						"Duplicate record exists for "
								+ sourceOfSample.getDescription());
			}
		} catch (Exception e) {
    		//bugzilla 2154
			LogEvent.logError("SourceOfSampleDAOImpl","updateData()",e.toString());	
			throw new LIMSRuntimeException("Error in SourceOfSample updateData()",
					e);
		}
		
		SourceOfSample oldData = (SourceOfSample) readSourceOfSample(sourceOfSample
				.getId());
		SourceOfSample newData = sourceOfSample;

		// add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = sourceOfSample.getSysUserId();
			String event = IActionConstants.AUDIT_TRAIL_UPDATE;
			String tableName = "SOURCE_OF_SAMPLE";
			auditDAO.saveHistory(newData, oldData, sysUserId, event, tableName);
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SourceOfSampleDAOImpl","AuditTrail updateData()",e.toString());
			throw new LIMSRuntimeException(
					"Error in SourceOfSample AuditTrail updateData()", e);
		}

		try {
			HibernateUtil.getSession().merge(sourceOfSample);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			HibernateUtil.getSession().evict(sourceOfSample);
			HibernateUtil.getSession().refresh(sourceOfSample);
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SourceOfSampleDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException(
					"Error in SourceOfSample updateData()", e);
		}
	}

	public void getData(SourceOfSample sourceOfSample)
			throws LIMSRuntimeException {
		try {
			SourceOfSample sos = (SourceOfSample) HibernateUtil.getSession()
					.get(SourceOfSample.class, sourceOfSample.getId());
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			if (sos != null) {
				PropertyUtils.copyProperties(sourceOfSample, sos);
			} else {
				sourceOfSample.setId(null);
			}
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SourceOfSampleDAOImpl","getData()",e.toString());
			throw new LIMSRuntimeException("Error in SourceOfSample getData()",
					e);
		}
	}

	public List getAllSourceOfSamples() throws LIMSRuntimeException {
		List list = new Vector();
		try {
			String sql = "from SourceOfSample";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SourceOfSampleDAOImpl","getAllSourceOfSamples()",e.toString());
			throw new LIMSRuntimeException(
					"Error in SourceOfSample getAllSourceOfSamples()", e);
		}

		return list;
	}

	public List getPageOfSourceOfSamples(int startingRecNo)
			throws LIMSRuntimeException {
		List list = new Vector();
		try {
			// calculate maxRow to be one more than the page size
			int endingRecNo = startingRecNo
					+ (SystemConfiguration.getInstance().getDefaultPageSize() + 1);

			// bugzilla 1399
			String sql = "from SourceOfSample s order by s.domain, s.description";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
			query.setFirstResult(startingRecNo - 1);
			query.setMaxResults(endingRecNo - 1);

			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SourceOfSampleDAOImpl","getPageOfSourceOfSamples()",e.toString());
			throw new LIMSRuntimeException(
					"Error in SourceOfSample getPageOfSourceOfSamples()", e);
		}

		return list;
	}

	public SourceOfSample readSourceOfSample(String idString) {
		SourceOfSample sos = null;
		try {
			sos = (SourceOfSample) HibernateUtil.getSession().get(
					SourceOfSample.class, idString);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SourceOfSampleDAOImpl","readSourceOfSample()",e.toString());
			throw new LIMSRuntimeException(
					"Error in SourceOfSample readSourceOfSample()", e);
		}

		return sos;
	}

	// this is for autocomplete
	// bugzilla 1474 added domain parm
	public List getSources(String filter, String domain)
			throws LIMSRuntimeException {
		List list = new Vector();
		try {

			String sql = "";
			if (!StringUtil.isNullorNill(domain)) {
				sql = "from SourceOfSample s where upper(s.description) like upper(:param) and s.domain = :param2 order by upper(s.description)";
			} else {
				sql = "from SourceOfSample s where upper(s.description) like upper(:param) order by upper(s.description)";

			}
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
			query.setParameter("param", filter + "%");
			// bugzilla 1474 added domain parm
			if (!StringUtil.isNullorNill(domain)) {
				query.setParameter("param2", domain);
			}

			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SourceOfSampleDAOImpl","getSources()",e.toString());
			throw new LIMSRuntimeException(
					"Error in SourceOfSample getSources(String filter)", e);
		}
		return list;
	}

	public List getNextSourceOfSampleRecord(String id)
			throws LIMSRuntimeException {

		return getNextRecord(id, "SourceOfSample", SourceOfSample.class);

	}

	public List getPreviousSourceOfSampleRecord(String id)
			throws LIMSRuntimeException {

		return getPreviousRecord(id, "SourceOfSample", SourceOfSample.class);
	}

	// bugzilla 1411
	public Integer getTotalSourceOfSampleCount() throws LIMSRuntimeException {
		return getTotalCount("SourceOfSample", SourceOfSample.class);
	}

	// bugzilla 1427
	public List getNextRecord(String id, String table, Class clazz)
			throws LIMSRuntimeException {
		int currentId = (Integer.valueOf(id)).intValue();
		String tablePrefix = getTablePrefix(table);

		List list = new Vector();
		//bugzilla 1908
		int rrn = 0;
		try {
			//bugzilla 1908 cannot use named query for postgres because of oracle ROWNUM
			//instead get the list in this sortorder and determine the index of record with id = currentId
    		String sql = "select sos.id from SourceOfSample sos " +
					" order by sos.domain, sos.description";
     		
 			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			rrn = list.indexOf(String.valueOf(currentId));

			list = HibernateUtil.getSession().getNamedQuery(
					tablePrefix + "getNext").setFirstResult(
					rrn + 1).setMaxResults(2).list();

		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SourceOfSampleDAOImpl","getNextRecord()",e.toString());
			throw new LIMSRuntimeException("Error in getNextRecord() for "
					+ table, e);
		}

		return list;
	}

	// bugzilla 1427
	public List getPreviousRecord(String id, String table, Class clazz)
			throws LIMSRuntimeException {
		int currentId = (Integer.valueOf(id)).intValue();
		String tablePrefix = getTablePrefix(table);

		List list = new Vector();
		//bugzilla 1908
		int rrn = 0;
		try {
			//bugzilla 1908 cannot use named query for postgres because of oracle ROWNUM
			//instead get the list in this sortorder and determine the index of record with id = currentId
    		String sql = "select sos.id from SourceOfSample sos " +
					" order by sos.domain desc, sos.description desc";
     		
 			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			rrn = list.indexOf(String.valueOf(currentId));

			list = HibernateUtil.getSession().getNamedQuery(
					tablePrefix + "getPrevious").setFirstResult(
					rrn + 1).setMaxResults(2).list();

		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SourceOfSampleDAOImpl","getPreviousRecord()",e.toString());
			throw new LIMSRuntimeException("Error in getPreviousRecord() for "
					+ table, e);
		}

		return list;
	}

	// bugzilla 1367 also handles NO domain (then all domains are retrieved)
	public SourceOfSample getSourceOfSampleByDescriptionAndDomain(
			SourceOfSample sos, boolean ignoreCase) throws LIMSRuntimeException {
		try {
			String sql = null;

			if (!StringUtil.isNullorNill(sos.getDomain())) {
				if (ignoreCase) {
					sql = "from SourceOfSample sos where trim(lower(sos.description)) = :param and sos.domain = :param2";
				} else {
					sql = "from SourceOfSample sos where trim(sos.description) = :param and sos.domain = :param2";
				}
			} else {
				if (ignoreCase) {
					sql = "from SourceOfSample sos where trim(lower(sos.description)) = :param";
				} else {
					sql = "from SourceOfSample sos where trim(sos.description) = :param";
				}
			}
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);

			if (ignoreCase) {
				query.setParameter("param", sos.getDescription().toLowerCase()
						.trim());
			} else {
				query.setParameter("param", sos.getDescription().trim());
			}

			if (!StringUtil.isNullorNill(sos.getDomain())) {
				query.setParameter("param2", sos.getDomain());
			}

			List list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			SourceOfSample sourceOfSample = null;
			if (list.size() > 0)
				sourceOfSample = (SourceOfSample) list.get(0);

			return sourceOfSample;

		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SourceOfSampleDAOImpl","getSourceOfSampleByDescriptionAndDomain()",e.toString());
			throw new LIMSRuntimeException(
					"Error in Test getSourceOfSampleByDescriptionAndDomain()",
					e);
		}
	}
	
	// bugzilla 1482
	private boolean duplicateSourceOfSampleExists(SourceOfSample sourceOfSample) throws LIMSRuntimeException {
		try {

			List list = new ArrayList();

			// not case sensitive hemolysis and Hemolysis are considered
			// duplicates
			String sql = "from SourceOfSample t where trim(lower(t.description)) = :param and trim(lower(t.domain)) = :param2 and t.id != :param3";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
			query.setParameter("param", sourceOfSample.getDescription().toLowerCase().trim());
			query.setParameter("param2", sourceOfSample.getDomain().toLowerCase().trim());

	
			// initialize with 0 (for new records where no id has been generated
			// yet
			String sourceOfSampleId = "0";
			if (!StringUtil.isNullorNill(sourceOfSample.getId())) {
				sourceOfSampleId = sourceOfSample.getId();
			}
			query.setParameter("param3", sourceOfSampleId);

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
			LogEvent.logError("SourceOfSampleDAOImpl","duplicateSourceOfSampleExists()",e.toString());
			throw new LIMSRuntimeException(
					"Error in duplicateSourceOfSampleExists()", e);
		}
	}
}