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
package us.mn.state.health.lims.receivercodeelement.daoimpl;

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
import us.mn.state.health.lims.receivercodeelement.dao.ReceiverCodeElementDAO;
import us.mn.state.health.lims.receivercodeelement.valueholder.ReceiverCodeElement;

/**
 * @author diane benz
 */
public class ReceiverCodeElementDAOImpl extends BaseDAOImpl implements
		ReceiverCodeElementDAO {

	public void deleteData(List receiverCodeElements)
			throws LIMSRuntimeException {
		// add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			for (int i = 0; i < receiverCodeElements.size(); i++) {
				ReceiverCodeElement data = (ReceiverCodeElement) receiverCodeElements
						.get(i);

				ReceiverCodeElement oldData = (ReceiverCodeElement) readReceiverCodeElement(data
						.getId());
				ReceiverCodeElement newData = new ReceiverCodeElement();

				String sysUserId = data.getSysUserId();
				String event = IActionConstants.AUDIT_TRAIL_DELETE;
				String tableName = "RECEIVER_CODE_ELEMENT";
				auditDAO.saveHistory(newData, oldData, sysUserId, event,
						tableName);
			}
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ReceiverCodeElementDAOImpl","AuditTrail deleteData()",e.toString());
			throw new LIMSRuntimeException(
					"Error in ReceiverCodeElement AuditTrail deleteData()", e);
		}

		try {
			for (int i = 0; i < receiverCodeElements.size(); i++) {
				ReceiverCodeElement data = (ReceiverCodeElement) receiverCodeElements
						.get(i);
				//bugzilla 2206
				data = (ReceiverCodeElement)readReceiverCodeElement(data.getId());
				HibernateUtil.getSession().delete(data);
				HibernateUtil.getSession().flush();
				HibernateUtil.getSession().clear();
			}
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ReceiverCodeElementDAOImpl","deleteData()",e.toString());
			throw new LIMSRuntimeException(
					"Error in ReceiverCodeElement deleteData()", e);
		}
	}

	public boolean insertData(ReceiverCodeElement receiverCodeElement)
			throws LIMSRuntimeException {
		try {
			// bugzilla 1482 throw Exception if record already exists
			if (duplicateReceiverCodeElementExists(receiverCodeElement)) {
				throw new LIMSDuplicateRecordException(
						"Duplicate record exists for "
								+ receiverCodeElement.getText());
			}

			String id = (String) HibernateUtil.getSession().save(
					receiverCodeElement);
			receiverCodeElement.setId(id);
			
			//bugzilla 1824 inserts will be logged in history table
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = receiverCodeElement.getSysUserId();
			String tableName = "RECEIVER_CODE_ELEMENT";
			auditDAO.saveNewHistory(receiverCodeElement,sysUserId,tableName);
			
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();

		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ReceiverCodeElementDAOImpl","insertData()",e.toString());
			throw new LIMSRuntimeException(
					"Error in ReceiverCodeElement insertData()", e);
		}

		return true;
	}

	public void updateData(ReceiverCodeElement receiverCodeElement)
			throws LIMSRuntimeException {
		// bugzilla 1482 throw Exception if record already exists
		try {
			if (duplicateReceiverCodeElementExists(receiverCodeElement)) {
				throw new LIMSDuplicateRecordException(
						"Duplicate record exists for "
								+ receiverCodeElement.getText());
			}
		} catch (Exception e) {
    		//bugzilla 2154
			LogEvent.logError("ReceiverCodeElementDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException(
					"Error in ReceiverCodeElement updateData()", e);
		}

		ReceiverCodeElement oldData = (ReceiverCodeElement) readReceiverCodeElement(receiverCodeElement
				.getId());
		ReceiverCodeElement newData = receiverCodeElement;

		// add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = receiverCodeElement.getSysUserId();
			String event = IActionConstants.AUDIT_TRAIL_UPDATE;
			String tableName = "RECEIVER_CODE_ELEMENT";
			auditDAO.saveHistory(newData, oldData, sysUserId, event, tableName);
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ReceiverCodeElementDAOImpl","AuditTrail updateData()",e.toString());
			throw new LIMSRuntimeException(
					"Error in ReceiverCodeElement AuditTrail updateData()", e);
		}

		try {
			HibernateUtil.getSession().merge(receiverCodeElement);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			HibernateUtil.getSession().evict(receiverCodeElement);
			HibernateUtil.getSession().refresh(receiverCodeElement);
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ReceiverCodeElementDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException(
					"Error in ReceiverCodeElement updateData()", e);
		}
	}

	public void getData(ReceiverCodeElement receiverCodeElement)
			throws LIMSRuntimeException {
		try {
			ReceiverCodeElement pan = (ReceiverCodeElement) HibernateUtil
					.getSession().get(ReceiverCodeElement.class,
							receiverCodeElement.getId());
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			if (pan != null) {
				PropertyUtils.copyProperties(receiverCodeElement, pan);
			} else {
				receiverCodeElement.setId(null);
			}
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ReceiverCodeElementDAOImpl","getData()",e.toString());
			throw new LIMSRuntimeException(
					"Error in ReceiverCodeElement getData()", e);
		}

	}

	public List getAllReceiverCodeElements() throws LIMSRuntimeException {
		List list = new Vector();
		try {
			String sql = "from ReceiverCodeElement";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
			// query.setMaxResults(10);
			// query.setFirstResult(3);
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ReceiverCodeElementDAOImpl","getAllReceiverCodeElements()",e.toString());
			throw new LIMSRuntimeException(
					"Error in ReceiverCodeElement getAllReceiverCodeElements()",
					e);
		}

		return list;
	}

	public List getReceiverCodeElementsByMessageOrganizationAndCodeElementType(
			ReceiverCodeElement receiverCodeElement, boolean linked)
			throws LIMSRuntimeException {
		List list = new Vector();
		try {
			String sql = null;
			org.hibernate.Query query = null;
			if (!StringUtil.isNullorNill(receiverCodeElement
					.getMessageOrganization().getId())
					&& !StringUtil.isNullorNill(receiverCodeElement
							.getCodeElementType().getId())) {
				if (linked) {
     				sql = "from ReceiverCodeElement t where (t.messageOrganization.organization.id = :param and t.codeElementType.id = :param2 and t.id in (select x.receiverCodeElement.id from CodeElementXref x))";
				} else {
					sql = "from ReceiverCodeElement t where (t.messageOrganization.organization.id = :param and t.codeElementType.id = :param2 and t.id not in (select x.receiverCodeElement.id from CodeElementXref x))";
				}
				query = HibernateUtil.getSession().createQuery(sql);
				query.setParameter("param", receiverCodeElement
						.getMessageOrganization().getOrganization().getId());
				query.setParameter("param2", receiverCodeElement
						.getCodeElementType().getId());
				list = query.list();
			}


			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ReceiverCodeElementDAOImpl","getReceiverCodeElementsByMessageOrganizationAndCodeElementType()",e.toString());
			throw new LIMSRuntimeException(
					"Error in ReceiverCodeElement getReceiverCodeElementsByMessageOrganizationAndCodeElementType()",
					e);
		}

		return list;
	}

	public List getPageOfReceiverCodeElements(int startingRecNo)
			throws LIMSRuntimeException {
		List list = new Vector();
		try {
			// calculate maxRow to be one more than the page size
			int endingRecNo = startingRecNo
					+ (SystemConfiguration.getInstance().getDefaultPageSize() + 1);

			// bugzilla 1399
			String sql = "from ReceiverCodeElement cet order by cet.messageOrganization.organization.organizationName, cet.codeElementType.text, cet.text";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
			query.setFirstResult(startingRecNo - 1);
			query.setMaxResults(endingRecNo - 1);

			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
		    LogEvent.logError("ReceiverCodeElementDAOImpl","getPageOfReceiverCodeElements()",e.toString());
			throw new LIMSRuntimeException(
					"Error in ReceiverCodeElement getPageOfReceiverCodeElements()",
					e);
		}

		return list;
	}

	public ReceiverCodeElement readReceiverCodeElement(String idString) {
		ReceiverCodeElement receiverCodeElement = null;
		try {
			receiverCodeElement = (ReceiverCodeElement) HibernateUtil
					.getSession().get(ReceiverCodeElement.class, idString);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
		    LogEvent.logError("ReceiverCodeElementDAOImpl","readReceiverCodeElement()",e.toString());
			throw new LIMSRuntimeException(
					"Error in ReceiverCodeElement readReceiverCodeElement()", e);
		}

		return receiverCodeElement;
	}

	// this is for autocomplete
	public List getReceiverCodeElements(String filter)
			throws LIMSRuntimeException {
		List list = new Vector();
		try {
			String sql = "from ReceiverCodeElement p where upper(p.text) like upper(:param) order by upper(p.text)";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
			query.setParameter("param", filter + "%");

			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
		    LogEvent.logError("ReceiverCodeElementDAOImpl","getReceiverCodeElements()",e.toString());
			throw new LIMSRuntimeException(
					"Error in ReceiverCodeElement getReceiverCodeElements()", e);
		}
		return list;

	}

	public List getNextReceiverCodeElementRecord(String id)
			throws LIMSRuntimeException {

		return getNextRecord(id, "ReceiverCodeElement",
				ReceiverCodeElement.class);

	}

	public List getPreviousReceiverCodeElementRecord(String id)
			throws LIMSRuntimeException {

		return getPreviousRecord(id, "ReceiverCodeElement",
				ReceiverCodeElement.class);
	}

	public ReceiverCodeElement getReceiverCodeElementByText(
			ReceiverCodeElement receiverCodeElement)
			throws LIMSRuntimeException {
		try {
			String sql = "from ReceiverCodeElement cet where cet.text = :param";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
			query.setParameter("param", receiverCodeElement.getText());

			List list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			ReceiverCodeElement pan = null;
			if (list.size() > 0)
				pan = (ReceiverCodeElement) list.get(0);

			return pan;

		} catch (Exception e) {
			//bugzilla 2154
		    LogEvent.logError("ReceiverCodeElementDAOImpl","getReceiverCodeElementByText()",e.toString());
			throw new LIMSRuntimeException(
					"Error in ReceiverCodeElement getReceiverCodeElementByText()",
					e);
		}
	}

	// bugzilla 1411
	public Integer getTotalReceiverCodeElementCount()
			throws LIMSRuntimeException {
		return getTotalCount("ReceiverCodeElement", ReceiverCodeElement.class);
	}

	// overriding BaseDAOImpl bugzilla 1427 pass in name not id
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
    		String sql = "select rce.id from ReceiverCodeElement rce " +
					" order by rce.messageOrganization.organization.organizationName, rce.codeElementType.text, rce.text";
    		
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
		    LogEvent.logError("ReceiverCodeElementDAOImpl","getNextRecord()",e.toString());
			throw new LIMSRuntimeException("Error in getNextRecord() for "
					+ table, e);
		}

		return list;
	}

	// overriding BaseDAOImpl bugzilla 1427 pass in name not id
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
    		String sql = "select rce.id from ReceiverCodeElement rce " +
			" order by rce.messageOrganization.organization.organizationName desc, rce.codeElementType.text desc, rce.text desc";
    		
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
		    LogEvent.logError("ReceiverCodeElementDAOImpl","getPreviousRecord()",e.toString());
			throw new LIMSRuntimeException("Error in getPreviousRecord() for "
					+ table, e);
		}

		return list;
	}

	// bugzilla 1482
	private boolean duplicateReceiverCodeElementExists(
			ReceiverCodeElement receiverCodeElement)
			throws LIMSRuntimeException {
		try {

			List list = new ArrayList();

			// not case sensitive hemolysis and Hemolysis are considered
			// duplicates
			String sql = "from ReceiverCodeElement t where trim(lower(t.messageOrganization.organization.organizationName)) = :param and trim(lower(t.codeElementType.text)) = :param2 and trim(lower(t.identifier)) = :param3 and t.id != :param4";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
			query.setParameter("param", receiverCodeElement
					.getMessageOrganization().getOrganization()
					.getOrganizationName().toLowerCase().trim());
			query.setParameter("param2", receiverCodeElement
					.getCodeElementType().getText().toLowerCase().trim());
			query.setParameter("param3", receiverCodeElement.getIdentifier()
					.toLowerCase().trim());

			// initialize with 0 (for new records where no id has been generated
			// yet
			String receiverCodeElementId = "0";
			if (!StringUtil.isNullorNill(receiverCodeElement.getId())) {
				receiverCodeElementId = receiverCodeElement.getId();
			}
			query.setParameter("param4", receiverCodeElementId);

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
		    LogEvent.logError("ReceiverCodeElementDAOImpl","duplicateReceiverCodeElementExists()",e.toString());
			throw new LIMSRuntimeException(
					"Error in duplicateReceiverCodeElementExists()", e);
		}
	}
}