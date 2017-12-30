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
package us.mn.state.health.lims.messageorganization.daoimpl;

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
import us.mn.state.health.lims.messageorganization.dao.MessageOrganizationDAO;
import us.mn.state.health.lims.messageorganization.valueholder.MessageOrganization;

/**
 * @author diane benz
 */
public class MessageOrganizationDAOImpl extends BaseDAOImpl implements
		MessageOrganizationDAO {

	public void deleteData(List messageOrganizations)
			throws LIMSRuntimeException {
	
    	// add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			for (int i = 0; i < messageOrganizations.size(); i++) {
				MessageOrganization data = (MessageOrganization) messageOrganizations
						.get(i);

				MessageOrganization oldData = (MessageOrganization) readMessageOrganization(data
						.getId());
				MessageOrganization newData = new MessageOrganization();

				String sysUserId = data.getSysUserId();
				String event = IActionConstants.AUDIT_TRAIL_DELETE;
				String tableName = "MESSAGE_ORG";
				auditDAO.saveHistory(newData, oldData, sysUserId, event,
						tableName);
			}
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("MessageOrganizationDAOImpl","AuditTrail deleteData()",e.toString());
			throw new LIMSRuntimeException(
					"Error in MessageOrganization AuditTrail deleteData()", e);
		}

		try {
			for (int i = 0; i < messageOrganizations.size(); i++) {
				MessageOrganization data = (MessageOrganization) messageOrganizations.get(i);
				MessageOrganization cloneData = (MessageOrganization) readMessageOrganization(data.getId());

				// Make the change to the object.
				cloneData.setIsActive(IActionConstants.NO);
				HibernateUtil.getSession().merge(cloneData);
				HibernateUtil.getSession().flush();
				HibernateUtil.getSession().clear();
				HibernateUtil.getSession().evict(cloneData);
				HibernateUtil.getSession().refresh(cloneData);
			}
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("MessageOrganizationDAOImpl","deleteData()",e.toString());
			throw new LIMSRuntimeException("Error in MessageOrganization deleteData()", e);
		}
	}

	public boolean insertData(MessageOrganization messageOrganization)
			throws LIMSRuntimeException {
		try {
			// bugzilla 1482 throw Exception if record already exists
			if (duplicateMessageOrganizationExists(messageOrganization)) {
				throw new LIMSDuplicateRecordException(
						"Duplicate record exists for "
								+ messageOrganization.getDescription());
			}

			String id = (String) HibernateUtil.getSession().save(
					messageOrganization);
			messageOrganization.setId(id);
			
			//bugzilla 1824 inserts will be logged in history table
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = messageOrganization.getSysUserId();
			String tableName = "MESSAGE_ORG";
			auditDAO.saveNewHistory(messageOrganization,sysUserId,tableName);
			
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();

		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("MessageOrganizationDAOImpl","insertData()",e.toString());
			throw new LIMSRuntimeException(
					"Error in MessageOrganization insertData()", e);
		}

		return true;
	}

	public void updateData(MessageOrganization messageOrganization)
			throws LIMSRuntimeException {
		// bugzilla 1482 throw Exception if record already exists
		try {
			if (duplicateMessageOrganizationExists(messageOrganization)) {
				throw new LIMSDuplicateRecordException(
						"Duplicate record exists for "
								+ messageOrganization.getDescription());
			}
		} catch (Exception e) {
    		//bugzilla 2154
			LogEvent.logError("MessageOrganizationDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException(
					"Error in MessageOrganization updateData()", e);
		}

		MessageOrganization oldData = (MessageOrganization) readMessageOrganization(messageOrganization
				.getId());
		MessageOrganization newData = messageOrganization;

		// add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = messageOrganization.getSysUserId();
			String event = IActionConstants.AUDIT_TRAIL_UPDATE;
			String tableName = "MESSAGE_ORG";
			auditDAO.saveHistory(newData, oldData, sysUserId, event, tableName);
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("MessageOrganizationDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException(
					"Error in MessageOrganization AuditTrail updateData()", e);
		}

		try {
			HibernateUtil.getSession().merge(messageOrganization);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			HibernateUtil.getSession().evict(messageOrganization);
			HibernateUtil.getSession().refresh(messageOrganization);
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("MessageOrganizationDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException(
					"Error in MessageOrganization updateData()", e);
		}
	}

	public void getData(MessageOrganization messageOrganization)
			throws LIMSRuntimeException {
		try {
			MessageOrganization pan = (MessageOrganization) HibernateUtil
					.getSession().get(MessageOrganization.class,
							messageOrganization.getId());
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			if (pan != null) {
				PropertyUtils.copyProperties(messageOrganization, pan);
			} else {
				messageOrganization.setId(null);
			}
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("MessageOrganizationDAOImpl","getData()",e.toString());
			throw new LIMSRuntimeException(
					"Error in MessageOrganization getData()", e);
		}

	}

	public List getAllMessageOrganizations() throws LIMSRuntimeException {
		List list = new Vector();
		try {
			String sql = "from MessageOrganization";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
			// query.setMaxResults(10);
			// query.setFirstResult(3);
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("MessageOrganizationDAOImpl","getAllMessageOrganizations()",e.toString());
			throw new LIMSRuntimeException(
					"Error in MessageOrganization getAllMessageOrganizations()",
					e);
		}

		return list;
	}

	public List getPageOfMessageOrganizations(int startingRecNo)
			throws LIMSRuntimeException {
		List list = new Vector();
		try {
			// calculate maxRow to be one more than the page size
			int endingRecNo = startingRecNo
					+ (SystemConfiguration.getInstance().getDefaultPageSize() + 1);

			// bugzilla 1399
			String sql = "from MessageOrganization mo order by mo.organization.organizationName, mo.description";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
			query.setFirstResult(startingRecNo - 1);
			query.setMaxResults(endingRecNo - 1);

			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("MessageOrganizationDAOImpl","getPageOfMessageOrganizations()",e.toString());
			throw new LIMSRuntimeException(
					"Error in MessageOrganization getPageOfMessageOrganizations()",
					e);
		}

		return list;
	}

	public MessageOrganization readMessageOrganization(String idString) {
		MessageOrganization messageOrganization = null;
		try {
			messageOrganization = (MessageOrganization) HibernateUtil
					.getSession().get(MessageOrganization.class, idString);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("MessageOrganizationDAOImpl","readMessageOrganization()",e.toString());
			throw new LIMSRuntimeException(
					"Error in MessageOrganization readMessageOrganization()", e);
		}

		return messageOrganization;
	}

	// this is for autocomplete
	public List getMessageOrganizations(String filter)
			throws LIMSRuntimeException {
		List list = new Vector();
		try {
			String sql = "from MessageOrganization p where upper(p.description) like upper(:param) order by upper(p.description)";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
			query.setParameter("param", filter + "%");

			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("MessageOrganizationDAOImpl","getMessageOrganizations()",e.toString());
			throw new LIMSRuntimeException(
					"Error in MessageOrganization getMessageOrganizations()", e);
		}
		return list;

	}

	public List getNextMessageOrganizationRecord(String id)
			throws LIMSRuntimeException {

		return getNextRecord(id, "MessageOrganization",
				MessageOrganization.class);

	}

	public List getPreviousMessageOrganizationRecord(String id)
			throws LIMSRuntimeException {

		return getPreviousRecord(id, "MessageOrganization",
				MessageOrganization.class);
	}

	public MessageOrganization getMessageOrganizationByOrganization(
			MessageOrganization messageOrganization)
			throws LIMSRuntimeException {
		try {
			String sql = "from MessageOrganization mo where trim(lower(mo.organizationName)) = :param";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
		    query.setParameter("param", messageOrganization.getOrganizationName().toLowerCase().trim());

			List list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			MessageOrganization pan = null;
			if (list.size() > 0)
				pan = (MessageOrganization) list.get(0);

			return pan;

		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("MessageOrganizationDAOImpl","getMessageOrganizationByText()",e.toString());
			throw new LIMSRuntimeException(
					"Error in MessageOrganization getMessageOrganizationByText()",
					e);
		}
	}

	// bugzilla 1411
	public Integer getTotalMessageOrganizationCount()
			throws LIMSRuntimeException {
		return getTotalCount("MessageOrganization", MessageOrganization.class);
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
			String sql = "select mo.id from MessageOrganization mo " +
					" order by mo.organization.organizationName, mo.description";
			
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
			LogEvent.logError("MessageOrganizationDAOImpl","getNextRecord()",e.toString());
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
			String sql = "select mo.id from MessageOrganization mo" +
					" order by mo.organization.organizationName desc, mo.description desc";
			
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
			LogEvent.logError("MessageOrganizationDAOImpl","getPreviousRecord()",e.toString());
			throw new LIMSRuntimeException("Error in getPreviousRecord() for " + table, e);
		} 

		return list;
	}

	// bugzilla 1482
	private boolean duplicateMessageOrganizationExists(
			MessageOrganization messageOrganization)
			throws LIMSRuntimeException {
		try {

			List list = new ArrayList();

			//only check if the test to be inserted/updated is active 
			if (messageOrganization.getIsActive().equalsIgnoreCase(IActionConstants.YES)) {
			// not case sensitive hemolysis and Hemolysis are considered
			// duplicates
			String sql = "from MessageOrganization t where trim(lower(t.organization.id)) = :param and trim(lower(t.description)) = :param2 and t.isActive='Y'  and t.id != :param3";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
			query.setParameter("param", messageOrganization.getOrganization().getId()
					.toLowerCase().trim());
			query.setParameter("param2", messageOrganization.getDescription()
					.toLowerCase().trim());

			// initialize with 0 (for new records where no id has been generated
			// yet
			String messageOrganizationId = "0";
			if (!StringUtil.isNullorNill(messageOrganization.getId())) {
				messageOrganizationId = messageOrganization.getId();
			}
			query.setParameter("param3", messageOrganizationId);

			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();

			}
			
			if (list.size() > 0) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("MessageOrganizationDAOImpl","duplicateMessageOrganizationExists()",e.toString());
			throw new LIMSRuntimeException(
					"Error in duplicateMessageOrganizationExists()", e);
		}
	}
}