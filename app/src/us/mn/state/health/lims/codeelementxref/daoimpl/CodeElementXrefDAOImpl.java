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
package us.mn.state.health.lims.codeelementxref.daoimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.beanutils.PropertyUtils;

import us.mn.state.health.lims.audittrail.dao.AuditTrailDAO;
import us.mn.state.health.lims.audittrail.daoimpl.AuditTrailDAOImpl;
import us.mn.state.health.lims.codeelementxref.dao.CodeElementXrefDAO;
import us.mn.state.health.lims.codeelementxref.valueholder.CodeElementXref;
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
public class CodeElementXrefDAOImpl extends BaseDAOImpl implements
		CodeElementXrefDAO {

	public void deleteData(List codeElementXrefs) throws LIMSRuntimeException {
		// add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			for (int i = 0; i < codeElementXrefs.size(); i++) {
				CodeElementXref data = (CodeElementXref) codeElementXrefs
						.get(i);

				CodeElementXref oldData = (CodeElementXref) readCodeElementXref(data
						.getId());
				CodeElementXref newData = new CodeElementXref();

				String sysUserId = data.getSysUserId();
				String event = IActionConstants.AUDIT_TRAIL_DELETE;
				String tableName = "CODE_ELEMENT_XREF";
				auditDAO.saveHistory(newData, oldData, sysUserId, event,
						tableName);
			}
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("CodeElementXref","AuditTrail deleteData()",e.toString());
			throw new LIMSRuntimeException(
					"Error in CodeElementXref AuditTrail deleteData()", e);
		}

		try {
			for (int i = 0; i < codeElementXrefs.size(); i++) {
				CodeElementXref data = (CodeElementXref) codeElementXrefs
						.get(i);
				//bugzilla 2206
				data = (CodeElementXref)readCodeElementXref(data.getId());
				HibernateUtil.getSession().delete(data);
				HibernateUtil.getSession().flush();
				HibernateUtil.getSession().clear();
			}
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("CodeElementXref","deleteData()",e.toString());
			throw new LIMSRuntimeException(
					"Error in CodeElementXref deleteData()", e);
		}
	}

	public boolean insertData(CodeElementXref codeElementXref)
			throws LIMSRuntimeException {
		try {
			// bugzilla 1482 throw Exception if record already exists
			if (duplicateCodeElementXrefExists(codeElementXref)) {
				throw new LIMSDuplicateRecordException(
						"Duplicate record exists for "
								+ codeElementXref.getMessageOrganization()
										.getOrganizationName()
								+ " "
								+ codeElementXref.getCodeElementType()
										.getText()
								+ " "
								+ codeElementXref
										.getSelectedLocalCodeElementId());
			}

			String id = (String) HibernateUtil.getSession().save(
					codeElementXref);
			codeElementXref.setId(id);
			
			//bugzilla 1824 inserts will be logged in history table
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = codeElementXref.getSysUserId();
			String tableName = "CODE_ELEMENT_XREF";
			auditDAO.saveNewHistory(codeElementXref,sysUserId,tableName);
			
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();

		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("CodeElementXref","insertData()",e.toString());
			throw new LIMSRuntimeException(
					"Error in CodeElementXref insertData()", e);
		}

		return true;
	}

	public void updateData(CodeElementXref codeElementXref)
			throws LIMSRuntimeException {
		// bugzilla 1482 throw Exception if record already exists
		try {
			if (duplicateCodeElementXrefExists(codeElementXref)) {
				throw new LIMSDuplicateRecordException(
						"Duplicate record exists for "
								+ codeElementXref.getMessageOrganization()
										.getOrganizationName()
								+ " "
								+ codeElementXref.getCodeElementType()
										.getText()
								+ " "
								+ codeElementXref
										.getSelectedLocalCodeElementId());
			}
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("CodeElementXref","updateData()",e.toString());			
			throw new LIMSRuntimeException(
					"Error in CodeElementXref updateData()", e);
		}

		CodeElementXref oldData = (CodeElementXref) readCodeElementXref(codeElementXref
				.getId());
		CodeElementXref newData = codeElementXref;

		// add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = codeElementXref.getSysUserId();
			String event = IActionConstants.AUDIT_TRAIL_UPDATE;
			String tableName = "CODE_ELEMENT_XREF";
			auditDAO.saveHistory(newData, oldData, sysUserId, event, tableName);
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("CodeElementXref","AuditTrail updateData()",e.toString());
			throw new LIMSRuntimeException(
					"Error in CodeElementXref AuditTrail updateData()", e);
		}

		try {
			HibernateUtil.getSession().merge(codeElementXref);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			HibernateUtil.getSession().evict(codeElementXref);
			HibernateUtil.getSession().refresh(codeElementXref);
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("CodeElementXref","updateData()",e.toString());
			throw new LIMSRuntimeException(
					"Error in CodeElementXref updateData()", e);
		}
	}

	public void getData(CodeElementXref codeElementXref)
			throws LIMSRuntimeException {
		try {
			CodeElementXref pan = (CodeElementXref) HibernateUtil.getSession()
					.get(CodeElementXref.class, codeElementXref.getId());
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			if (pan != null) {
				PropertyUtils.copyProperties(codeElementXref, pan);
			} else {
				codeElementXref.setId(null);
			}
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("CodeElementXref","getData()",e.toString());
			throw new LIMSRuntimeException(
					"Error in CodeElementXref getData()", e);
		}

	}

	public List getAllCodeElementXrefs() throws LIMSRuntimeException {
		List list = new Vector();
		try {
			String sql = "from CodeElementXref cex order by cex.messageOrganization.organization.organizationName, cex.codeElementType.text, cex.selectedLocalCodeElementId";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
			// query.setMaxResults(10);
			// query.setFirstResult(3);
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("CodeElementXref","getAllCodeElementXrefs()",e.toString());
			throw new LIMSRuntimeException(
					"Error in CodeElementXref getAllCodeElementXrefs()", e);
		}

		return list;
	}

	public List getPageOfCodeElementXrefs(int startingRecNo)
			throws LIMSRuntimeException {
		List list = new Vector();
		try {
			// calculate maxRow to be one more than the page size
			int endingRecNo = startingRecNo
					+ (SystemConfiguration.getInstance().getDefaultPageSize() + 1);

			// bugzilla 1399
			String sql = "from CodeElementXref cex order by cex.messageOrganization.organization.organizationName, cex.codeElementType.text, cex.selectedLocalCodeElementId";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
			query.setFirstResult(startingRecNo - 1);
			query.setMaxResults(endingRecNo - 1);

			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("CodeElementXref","getPageOfCodeElementXrefs()",e.toString());
			throw new LIMSRuntimeException(
					"Error in CodeElementXref getPageOfCodeElementXrefs()", e);
		}

		return list;
	}

	public CodeElementXref readCodeElementXref(String idString) {
		CodeElementXref codeElementXref = null;
		try {
			codeElementXref = (CodeElementXref) HibernateUtil.getSession().get(
					CodeElementXref.class, idString);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("CodeElementXref","readCodeElementXref()",e.toString());
			throw new LIMSRuntimeException(
					"Error in CodeElementXref readCodeElementXref()", e);
		}

		return codeElementXref;
	}

	public List getNextCodeElementXrefRecord(String id)
			throws LIMSRuntimeException {

		return getNextRecord(id, "CodeElementXref", CodeElementXref.class);

	}

	public List getPreviousCodeElementXrefRecord(String id)
			throws LIMSRuntimeException {

		return getPreviousRecord(id, "CodeElementXref", CodeElementXref.class);
	}

	// bugzilla 1411
	public Integer getTotalCodeElementXrefCount() throws LIMSRuntimeException {
		return getTotalCount("CodeElementXref", CodeElementXref.class);
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
    		String sql = "select cex.id from CodeElementXref cex " +
					" inner join MessageOrg mo on mo.id = cex.messageOrgId" +
					" inner join Organization o on o.id = mo.orgId" +
					" inner join CodeElementType cet on cet.id = cex.codeElementTypeId" +
					" order by o.name, cet.text, cex.localCodeElementId";
 			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			rrn = list.indexOf(String.valueOf(currentId));

			list = HibernateUtil.getSession().getNamedQuery(
					tablePrefix + "getNext").setFirstResult(
					rrn + 1).setMaxResults(2).list();

		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("CodeElementXref","getNextRecord()",e.toString());
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
    		String sql = "select cex.id from CodeElementXref cex " +
					" inner join MessageOrg mo on mo.id = cex.messageOrgId" +
					" inner join Organization o on o.id = mo.orgId" +
					" inner join CodeElementType cet on cet.id = cex.codeElementTypeId" +
					" order by o.name desc, cet.text desc, cex.localCodeElementId desc";
 			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			rrn = list.indexOf(String.valueOf(currentId));

			list = HibernateUtil.getSession().getNamedQuery(
					tablePrefix + "getPrevious").setFirstResult(
					rrn + 1).setMaxResults(2).list();

		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("CodeElementXref","getPreviousRecord()",e.toString());
			throw new LIMSRuntimeException("Error in getPreviousRecord() for "
					+ table, e);
		}

		return list;
	}

	// bugzilla 1482
	private boolean duplicateCodeElementXrefExists(
			CodeElementXref codeElementXref) throws LIMSRuntimeException {
		try {

			List list = new ArrayList();

			// not case sensitive hemolysis and Hemolysis are considered
			// duplicates
			String sql = "from CodeElementXref t where trim(lower(t.messageOrganization.organization.organizationName)) = :param and trim(lower(t.codeElementType.text)) = :param2 and t.selectedLocalCodeElementId = :param3 and t.id != :param4";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
			query.setParameter("param", codeElementXref
					.getMessageOrganization().getOrganization()
					.getOrganizationName().trim().toLowerCase());

			// initialize with 0 (for new records where no id has been generated
			// yet
			String codeElementXrefId = "0";
			if (!StringUtil.isNullorNill(codeElementXref.getId())) {
				codeElementXrefId = codeElementXref.getId();
			}
			query.setParameter("param2", codeElementXref.getCodeElementType()
					.getText().trim().toLowerCase());
			query.setParameter("param3", codeElementXref
					.getSelectedLocalCodeElementId().trim());
			query.setParameter("param4", codeElementXrefId);

			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();

			if (list.size() > 0) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("CodeElementXref","duplicateCodeElementXrefExists()",e.toString());
			throw new LIMSRuntimeException(
					"Error in duplicateCodeElementXrefExists()", e);
		}
	}

	public List getCodeElementXrefsByReceiverOrganizationAndCodeElementType(
			CodeElementXref codeElementXref) throws LIMSRuntimeException {
		List list = new Vector();

		try {
			String sql = "from CodeElementXref cex where cex.messageOrganization = :param and cex.codeElementType = :param2";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
			query.setParameter("param", codeElementXref
					.getMessageOrganization());
			query.setParameter("param2", codeElementXref.getCodeElementType());

			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("CodeElementXref","getCodeElementXrefsByReceiverOrganizationAndCodeElementType()",e.toString());
			throw new LIMSRuntimeException(
					"Error in getCodeElementXrefsByReceiverOrganizationAndCodeElementType()",
					e);
		}

		return list;
	}
}