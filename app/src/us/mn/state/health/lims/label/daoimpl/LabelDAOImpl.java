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
package us.mn.state.health.lims.label.daoimpl;

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
import us.mn.state.health.lims.label.dao.LabelDAO;
import us.mn.state.health.lims.label.valueholder.Label;

/**
 * @author diane benz
 */
public class LabelDAOImpl extends BaseDAOImpl implements LabelDAO {

	public void deleteData(List labels) throws LIMSRuntimeException {
		//add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			for (int i = 0; i < labels.size(); i++) {
				Label data = (Label)labels.get(i);
			
				Label oldData = (Label)readLabel(data.getId());
				Label newData = new Label();

				String sysUserId = data.getSysUserId();
				String event = IActionConstants.AUDIT_TRAIL_DELETE;
				String tableName = "LABEL";
				auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
			}
		}  catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("LabelDAOImpl","AuditTrail deleteData()",e.toString());
			throw new LIMSRuntimeException("Error in Label AuditTrail deleteData()", e);
		}  
		
		try {					
			for (int i = 0; i < labels.size(); i++) {
				Label data = (Label) labels.get(i);		
				//bugzilla 2206
				data = (Label)readLabel(data.getId());
				HibernateUtil.getSession().delete(data);
				HibernateUtil.getSession().flush();
				HibernateUtil.getSession().clear();				
			}			
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("LabelDAOImpl","deleteData()",e.toString());
			throw new LIMSRuntimeException("Error in Label deleteData()", e);
		} 
	}

	public boolean insertData(Label label) throws LIMSRuntimeException {
		try {		
			// bugzilla 1482 throw Exception if record already exists
			if (duplicateLabelExists(label)) {
				throw new LIMSDuplicateRecordException(
						"Duplicate record exists for "
								+ label.getLabelName());
			}
			
			String id = (String)HibernateUtil.getSession().save(label);
			label.setId(id);
			
			//bugzilla 1824 inserts will be logged in history table
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = label.getSysUserId();
			String tableName = "LABEL";
			auditDAO.saveNewHistory(label,sysUserId,tableName);
			
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();					
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("LabelDAOImpl","insertData()",e.toString());
			throw new LIMSRuntimeException("Error in Label insertData()", e);
		}
		
		return true;
	}

	public void updateData(Label label) throws LIMSRuntimeException {
		// bugzilla 1482 throw Exception if record already exists
		try {
			if (duplicateLabelExists(label)) {
				throw new LIMSDuplicateRecordException(
						"Duplicate record exists for "
								+ label.getLabelName());
			}
		} catch (Exception e) {
    		//bugzilla 2154
			LogEvent.logError("LabelDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException("Error in Label updateData()",
					e);
		}
		
		Label oldData = (Label)readLabel(label.getId());
		Label newData = label;

		//add to audit trail
		try {			
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = label.getSysUserId();
			String event = IActionConstants.AUDIT_TRAIL_UPDATE;
			String tableName = "LABEL";
			auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
		}  catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("LabelDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException("Error in Label AuditTrail updateData()", e);
		}  
						
		try {			
			HibernateUtil.getSession().merge(label);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			HibernateUtil.getSession().evict(label);
			HibernateUtil.getSession().refresh(label);			
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("LabelDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException("Error in Label updateData()", e);
		}
	}

	public void getData(Label label) throws LIMSRuntimeException {		
		try {			
			Label labl = (Label)HibernateUtil.getSession().get(Label.class, label.getId());
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			if (labl != null) {
				PropertyUtils.copyProperties(label, labl);
			} else {
				label.setId(null);
			}
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("LabelDAOImpl","getData()",e.toString());
			throw new LIMSRuntimeException("Error in Label getData()", e);
		} 
	}

	public List getAllLabels() throws LIMSRuntimeException {		
		List list = new Vector();
		try {			
			String sql = "from Label";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			//query.setMaxResults(10);
			//query.setFirstResult(3);
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("LabelDAOImpl","getAllLabels()",e.toString());
			throw new LIMSRuntimeException("Error in Label getAllLabels()", e);
		} 

		return list;
	}

	public List getPageOfLabels(int startingRecNo) throws LIMSRuntimeException {
		List list = new Vector();
		try {			
			// calculate maxRow to be one more than the page size
			int endingRecNo = startingRecNo + (SystemConfiguration.getInstance().getDefaultPageSize() + 1);
			
			//bugzilla 1399
			String sql = "from Label l order by l.labelName";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setFirstResult(startingRecNo-1);
			query.setMaxResults(endingRecNo-1); 					

			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("LabelDAOImpl","getPageOfLabels()",e.toString());
			throw new LIMSRuntimeException("Error in Label getPageOfLabels()",e);
		} 

		return list;
	}

	public Label readLabel(String idString) {
		Label label = null;
		try {			
			label = (Label)HibernateUtil.getSession().get(Label.class, idString);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("LabelDAOImpl","readLabel()",e.toString());
			throw new LIMSRuntimeException("Error in City readLabel()", e);
		}			
		
		return label;
	}
	
	// this is for autocomplete
	public List getLabels(String filter) throws LIMSRuntimeException {		
		List list = new Vector(); 	
		try {			
			String sql = "from Label l where upper(l.labelName) like upper(:param) order by upper(l.labelName)";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setParameter("param", filter+"%");		
		
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("LabelDAOImpl","getLabels()",e.toString());
			throw new LIMSRuntimeException("Error in Label getLabels(String filter)", e);
		}
		
		return list;
	}

	public List getNextLabelRecord(String id) throws LIMSRuntimeException {

		return getNextRecord(id, "Label", Label.class);

	}

	public List getPreviousLabelRecord(String id) throws LIMSRuntimeException {

		return getPreviousRecord(id, "Label", Label.class);
	}

	public Label getLabelByName(Label label) throws LIMSRuntimeException {		
		try {			
			String sql = "from Label l where l.labelName = :param";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setParameter("param", label.getLabelName());
			
			List list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			Label labl = null;
			if ( list.size() > 0 )
				labl = (Label)list.get(0);
			
			return labl;
			
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("LabelDAOImpl","getLabelByName()",e.toString());
			throw new LIMSRuntimeException("Error in Label getLabelByName()", e);
		}
	}
	
	//bugzilla 1411
	public Integer getTotalLabelCount() throws LIMSRuntimeException {
		return getTotalCount("Label", Label.class);
	}
	
	//overriding BaseDAOImpl bugzilla 1427 pass in name not id
	public List getNextRecord(String id, String table, Class clazz) throws LIMSRuntimeException {	
				
		List list = new Vector();
		try {			
			String sql = "from "+table+" t where name >= "+ enquote(id) + " order by t.labelName";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setFirstResult(1);
			query.setMaxResults(2); 	
			
			list = query.list();		
			
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("LabelDAOImpl","getNextRecord()",e.toString());
			throw new LIMSRuntimeException("Error in getNextRecord() for " + table, e);
		}
		
		return list;		
	}

	//overriding BaseDAOImpl bugzilla 1427 pass in name not id
	public List getPreviousRecord(String id, String table, Class clazz) throws LIMSRuntimeException {		
		
		List list = new Vector();
		try {			
			String sql = "from "+table+" t order by t.labelName desc where name <= "+ enquote(id);
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setFirstResult(1);
			query.setMaxResults(2); 	
			
			list = query.list();					
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("LabelDAOImpl","getPreviousRecord()",e.toString());
			throw new LIMSRuntimeException("Error in getPreviousRecord() for " + table, e);
		} 

		return list;
	}
	
	// bugzilla 1482
	private boolean duplicateLabelExists(Label label) throws LIMSRuntimeException {
		try {

			List list = new ArrayList();

			String sql = "from Label t where trim(lower(t.labelName)) = :param and t.id != :param2";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
			query.setParameter("param", label.getLabelName().toLowerCase().trim());

			// initialize with 0 (for new records where no id has been generated
			// yet
			String labelId = "0";
			if (!StringUtil.isNullorNill(label.getId())) {
				labelId = label.getId();
			}
			query.setParameter("param2", labelId);

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
			LogEvent.logError("LabelDAOImpl","duplicateLabelExists()",e.toString());
			throw new LIMSRuntimeException(
					"Error in duplicateLabelExists()", e);
		}
	}
}