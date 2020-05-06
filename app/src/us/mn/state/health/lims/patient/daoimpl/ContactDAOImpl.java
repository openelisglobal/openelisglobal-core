package us.mn.state.health.lims.patient.daoimpl;

import us.mn.state.health.lims.audittrail.dao.AuditTrailDAO;
import us.mn.state.health.lims.audittrail.daoimpl.AuditTrailDAOImpl;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.patient.dao.ContactDAO;
import us.mn.state.health.lims.patient.valueholder.Contact;

public class ContactDAOImpl implements ContactDAO {

	public void insertData(Contact contact) {
		try {
			String id = (String) HibernateUtil.getSession().save(contact);
			contact.setId(id);

			// bugzilla 1824 inserts will be logged in history table
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = contact.getSysUserId();
			String tableName = "CONTACT";
			auditDAO.saveNewHistory(contact, sysUserId, tableName);

			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			// bugzilla 2154
			LogEvent.logError("ContactDAOImpl", "insertData()", e.toString());
			throw new LIMSRuntimeException("Error in ContactDAOImpl insertData()", e);
		}
	}

	public void updateData(Contact contact) {
		Contact oldData = readContact(contact.getId());

		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = contact.getSysUserId();
			String event = IActionConstants.AUDIT_TRAIL_UPDATE;
			String tableName = "CONTACT";
			auditDAO.saveHistory(contact, oldData, sysUserId, event, tableName);
		} catch (Exception e) {
			LogEvent.logError("ContactDAOImpl", "AuditTrail updateData()", e.toString());
			throw new LIMSRuntimeException("Error in Contact AuditTrail updateData()", e);
		}

		try {
			HibernateUtil.getSession().save(contact);
			HibernateUtil.getSession().merge(contact);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			HibernateUtil.getSession().evict(contact);
			HibernateUtil.getSession().refresh(contact);
		} catch (Exception e) {
			LogEvent.logError("ContactDAOImpl", "updateData()", e.toString());
			throw new LIMSRuntimeException("Error in Contact updateData()", e);
		}
	}

	public Contact readContact(String idString) {
		Contact contact = null;
		try {
			contact = (Contact) HibernateUtil.getSession().get(Contact.class, idString);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			// bugzilla 2154
			LogEvent.logError("ContactDAOImpl", "readContact()", e.toString());
			throw new LIMSRuntimeException("Error in Contact readContact()", e);
		}

		return contact;
	}

}
