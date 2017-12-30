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
package us.mn.state.health.lims.codeelementtype.daoimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.beanutils.PropertyUtils;

import us.mn.state.health.lims.audittrail.dao.AuditTrailDAO;
import us.mn.state.health.lims.audittrail.daoimpl.AuditTrailDAOImpl;
import us.mn.state.health.lims.codeelementtype.dao.CodeElementTypeDAO;
import us.mn.state.health.lims.codeelementtype.valueholder.CodeElementType;
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
public class CodeElementTypeDAOImpl extends BaseDAOImpl implements CodeElementTypeDAO {

	public void deleteData(List codeElementTypes) throws LIMSRuntimeException {
		//add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			for (int i = 0; i < codeElementTypes.size(); i++) {
				CodeElementType data = (CodeElementType)codeElementTypes.get(i);
			
				CodeElementType oldData = (CodeElementType)readCodeElementType(data.getId());
				CodeElementType newData = new CodeElementType();

				String sysUserId = data.getSysUserId();
				String event = IActionConstants.AUDIT_TRAIL_DELETE;
				String tableName = "CODE_ELEMENT_TYPE";
				auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
			}
		}  catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("CodeElementType","AuditTrail deleteData()",e.toString());
			throw new LIMSRuntimeException("Error in CodeElementType AuditTrail deleteData()", e);
		}  
		
		try {		
			for (int i = 0; i < codeElementTypes.size(); i++) {
				CodeElementType data = (CodeElementType) codeElementTypes.get(i);	
				//bugzilla 2206
				data = (CodeElementType)readCodeElementType(data.getId());
				HibernateUtil.getSession().delete(data);
				HibernateUtil.getSession().flush();
				HibernateUtil.getSession().clear();				
			}			
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("CodeElementType","deleteData()",e.toString());
			throw new LIMSRuntimeException("Error in CodeElementType deleteData()", e);
		} 
	}

	public boolean insertData(CodeElementType codeElementType) throws LIMSRuntimeException {
		try {
			// bugzilla 1482 throw Exception if record already exists
			if (duplicateCodeElementTypeExists(codeElementType)) {
				throw new LIMSDuplicateRecordException(
						"Duplicate record exists for "
								+ codeElementType.getText());
			}
			
			String id = (String)HibernateUtil.getSession().save(codeElementType);
			codeElementType.setId(id);
			
			//bugzilla 1824 inserts will be logged in history table
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = codeElementType.getSysUserId();
			String tableName = "CODE_ELEMENT_TYPE";
			auditDAO.saveNewHistory(codeElementType,sysUserId,tableName);
			
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();		
										
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("CodeElementType","insertData()",e.toString());
			throw new LIMSRuntimeException("Error in CodeElementType insertData()", e);
		}
		
		return true;
	}

	public void updateData(CodeElementType codeElementType) throws LIMSRuntimeException {
		// bugzilla 1482 throw Exception if record already exists
		try {
			if (duplicateCodeElementTypeExists(codeElementType)) {
				throw new LIMSDuplicateRecordException(
						"Duplicate record exists for "
								+ codeElementType.getText());
			}
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("CodeElementType","updateData()",e.toString());
			throw new LIMSRuntimeException("Error in CodeElementType updateData()",
					e);
		}
		
		CodeElementType oldData = (CodeElementType)readCodeElementType(codeElementType.getId());
		CodeElementType newData = codeElementType;

		//add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = codeElementType.getSysUserId();
			String event = IActionConstants.AUDIT_TRAIL_UPDATE;
			String tableName = "CODE_ELEMENT_TYPE";
			auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
		}  catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("CodeElementType","AuditTrail updateData()",e.toString());
			throw new LIMSRuntimeException("Error in CodeElementType AuditTrail updateData()", e);
		}  
			
		try {
			HibernateUtil.getSession().merge(codeElementType);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			HibernateUtil.getSession().evict(codeElementType);
			HibernateUtil.getSession().refresh(codeElementType);			
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("CodeElementType","updateData()",e.toString());
			throw new LIMSRuntimeException("Error in CodeElementType updateData()", e);
		}
	}

	public void getData(CodeElementType codeElementType) throws LIMSRuntimeException {
		try {
			CodeElementType pan = (CodeElementType)HibernateUtil.getSession().get(CodeElementType.class, codeElementType.getId());
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			if (pan != null) {
			  PropertyUtils.copyProperties(codeElementType, pan);
			} else {
				codeElementType.setId(null);
			}
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("CodeElementType","getData()",e.toString());
			throw new LIMSRuntimeException("Error in CodeElementType getData()", e);
		}

	}

	public List getAllCodeElementTypes() throws LIMSRuntimeException {
		List list = new Vector();
		try {
			String sql = "from CodeElementType";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			//query.setMaxResults(10);
			//query.setFirstResult(3);				
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("CodeElementType","getAllCodeElementTypes()",e.toString());
			throw new LIMSRuntimeException("Error in CodeElementType getAllCodeElementTypes()", e);
		}

		return list;
	}

	public List getPageOfCodeElementTypes(int startingRecNo) throws LIMSRuntimeException {
		List list = new Vector();
		try {
			// calculate maxRow to be one more than the page size
			int endingRecNo = startingRecNo + (SystemConfiguration.getInstance().getDefaultPageSize() + 1);
			
			//bugzilla 1399
			String sql = "from CodeElementType cet order by cet.text";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setFirstResult(startingRecNo-1);
			query.setMaxResults(endingRecNo-1); 
					
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("CodeElementType","getPageOfCodeElementTypes()",e.toString());
			throw new LIMSRuntimeException("Error in CodeElementType getPageOfCodeElementTypes()", e);
		}

		return list;
	}

	public CodeElementType readCodeElementType(String idString) {
		CodeElementType codeElementType = null;
		try {
			codeElementType = (CodeElementType)HibernateUtil.getSession().get(CodeElementType.class, idString);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("CodeElementType","readCodeElementType()",e.toString());
			throw new LIMSRuntimeException("Error in CodeElementType readCodeElementType()", e);
		}			
		
		return codeElementType;		
	}
	
	// this is for autocomplete
	public List getCodeElementTypes(String filter) throws LIMSRuntimeException {
		List list = new Vector(); 
		try {
			String sql = "from CodeElementType p where upper(p.text) like upper(:param) order by upper(p.text)";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setParameter("param", filter+"%");	

			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("CodeElementType","getCodeElementTypes()",e.toString());
			throw new LIMSRuntimeException( "Error in CodeElementType getCodeElementTypes()", e);
		}
		return list;	
		
	}

	public List getNextCodeElementTypeRecord(String id) throws LIMSRuntimeException {

		return getNextRecord(id, "CodeElementType", CodeElementType.class);

	}

	public List getPreviousCodeElementTypeRecord(String id) throws LIMSRuntimeException {

		return getPreviousRecord(id, "CodeElementType", CodeElementType.class);
	}

	public CodeElementType getCodeElementTypeByText(CodeElementType codeElementType) throws LIMSRuntimeException {
		try {
			String sql = "from CodeElementType cet where cet.text = :param";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setParameter("param", codeElementType.getText());

			List list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			CodeElementType pan = null;
			if ( list.size() > 0 )
				pan = (CodeElementType)list.get(0);
			
			return pan;

		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("CodeElementType","getCodeElementTypeByText()",e.toString());
			throw new LIMSRuntimeException("Error in CodeElementType getCodeElementTypeByText()", e);
		}
	}
	
	//bugzilla 1411
	public Integer getTotalCodeElementTypeCount() throws LIMSRuntimeException {
		return getTotalCount("CodeElementType", CodeElementType.class);
	}
	
	//overriding BaseDAOImpl bugzilla 1427 pass in name not id
	public List getNextRecord(String id, String table, Class clazz) throws LIMSRuntimeException {	
				
		List list = new Vector();
		try {			
			String sql = "from "+table+" t where text >= "+ enquote(id) + " order by t.text";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setFirstResult(1);
			query.setMaxResults(2); 	
			
			list = query.list();		
			
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("CodeElementType","getNextRecord()",e.toString());
			throw new LIMSRuntimeException("Error in getNextRecord() for " + table, e);
		}
		
		return list;		
	}

	//overriding BaseDAOImpl bugzilla 1427 pass in name not id
	public List getPreviousRecord(String id, String table, Class clazz) throws LIMSRuntimeException {		
		
		List list = new Vector();
		try {			
			String sql = "from "+table+" t order by t.text desc where text <= "+ enquote(id);
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setFirstResult(1);
			query.setMaxResults(2); 	
			
			list = query.list();					
		} catch (Exception e) {
			//buzilla 2154
			LogEvent.logError("CodeElementType","getPreviousRecord()",e.toString());
			throw new LIMSRuntimeException("Error in getPreviousRecord() for " + table, e);
		} 

		return list;
	}
	
	// bugzilla 1482
	private boolean duplicateCodeElementTypeExists(CodeElementType codeElementType) throws LIMSRuntimeException {
		try {

			List list = new ArrayList();

			// not case sensitive hemolysis and Hemolysis are considered
			// duplicates
			//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
			String sql = "from CodeElementType t where trim(lower(t.referenceTables.id)) = :param and t.id != :param2";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
			//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
			query.setParameter("param", codeElementType.getReferenceTables().getId().toLowerCase().trim());
	
			// initialize with 0 (for new records where no id has been generated
			// yet
			String codeElementTypeId = "0";
			if (!StringUtil.isNullorNill(codeElementType.getId())) {
				codeElementTypeId = codeElementType.getId();
			}
			query.setParameter("param2", codeElementTypeId);

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
			LogEvent.logError("CodeElementType","duplicateCodeElementTypeExists()",e.toString());
			throw new LIMSRuntimeException(
					"Error in duplicateCodeElementTypeExists()", e);
		}
	}
}