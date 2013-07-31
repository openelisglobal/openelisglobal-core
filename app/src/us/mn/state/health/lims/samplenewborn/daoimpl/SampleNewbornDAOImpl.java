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
package us.mn.state.health.lims.samplenewborn.daoimpl;

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

import us.mn.state.health.lims.audittrail.dao.AuditTrailDAO;
import us.mn.state.health.lims.audittrail.daoimpl.AuditTrailDAOImpl;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.daoimpl.BaseDAOImpl;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.samplenewborn.dao.SampleNewbornDAO;
import us.mn.state.health.lims.samplenewborn.valueholder.SampleNewborn;

public class SampleNewbornDAOImpl extends BaseDAOImpl implements SampleNewbornDAO {

	public void deleteData(List sampleNewborns) throws LIMSRuntimeException {
		
		//add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			for (int i = 0; i < sampleNewborns.size(); i++) {
				SampleNewborn data = (SampleNewborn)sampleNewborns.get(i);
			
				SampleNewborn oldData = (SampleNewborn)readSampleNewborn(data.getId());
				SampleNewborn newData = new SampleNewborn();

				String sysUserId = data.getSysUserId();
				String event = IActionConstants.AUDIT_TRAIL_DELETE;
				String tableName = "SAMPLE_NEWBORN";
				auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
			}
		}  catch (Exception e) {
			LogEvent.logError("SampleNewbornDAOImpl","AuditTrail deleteData()",e.toString());	
			throw new LIMSRuntimeException("Error in SampleNewborn AuditTrail deleteData()", e);
		}  
		
		try {		
			for (int i = 0; i < sampleNewborns.size(); i++) {
				SampleNewborn data = (SampleNewborn) sampleNewborns.get(i);
				data = (SampleNewborn)readSampleNewborn(data.getId());
				HibernateUtil.getSession().delete(data);
				HibernateUtil.getSession().flush();
				HibernateUtil.getSession().clear();
			}			
		} catch (Exception e) {
			LogEvent.logError("SampleNewbornDAOImpl","deleteData()",e.toString());	
			throw new LIMSRuntimeException("Error in SampleNewborn deleteData()", e);
		}
	}

	public boolean insertData(SampleNewborn sampleNewborn) throws LIMSRuntimeException {
		
		try {
			String id = (String)HibernateUtil.getSession().save(sampleNewborn);
			sampleNewborn.setId(id);
			
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = sampleNewborn.getSysUserId();
			String tableName = "SAMPLE_NEWBORN";
			auditDAO.saveNewHistory(sampleNewborn,sysUserId,tableName);
			
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
							
		} catch (Exception e) {
			LogEvent.logError("SampleNewbornDAOImpl","insertData()",e.toString());	
			throw new LIMSRuntimeException("Error in SampleNewborn insertData()", e);
		}
		
		return true;
	}

	public void updateData(SampleNewborn sampleNewborn) throws LIMSRuntimeException {
		
		SampleNewborn oldData = (SampleNewborn)readSampleNewborn(sampleNewborn.getId());
		SampleNewborn newData = sampleNewborn;

		//add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = sampleNewborn.getSysUserId();
			String event = IActionConstants.AUDIT_TRAIL_UPDATE;
			String tableName = "SAMPLE_NEWBORN";
			auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
		}  catch (Exception e) {
			LogEvent.logError("SampleNewbornDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException("Error in SampleNewborn AuditTrail updateData()", e);
		}  
						
		try {
			HibernateUtil.getSession().merge(sampleNewborn);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			HibernateUtil.getSession().evict(sampleNewborn);
			HibernateUtil.getSession().refresh(sampleNewborn);
		} catch (Exception e) {
			LogEvent.logError("SampleNewbornDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException("Error in SampleNewborn updateData()", e);
		} 
	}

	public void getData(SampleNewborn sampleNewborn) throws LIMSRuntimeException {
		try {
			SampleNewborn sampNewborn = (SampleNewborn)HibernateUtil.getSession().get(SampleNewborn.class, sampleNewborn.getId());
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			if (sampNewborn != null) {
				String locale = SystemConfiguration.getInstance().getDefaultLocale().toString();
				sampNewborn.setDateFirstFeedingForDisplay(DateUtil.convertTimestampToStringDate(sampNewborn.getDateFirstFeeding(), locale));
				sampNewborn.setDateTransfutionForDisplay(DateUtil.convertTimestampToStringDate(sampNewborn.getDateTransfution(), locale));
				PropertyUtils.copyProperties(sampleNewborn, sampNewborn);
			} else {
				sampleNewborn.setId(null);
			}
		} catch (Exception e) {
			LogEvent.logError("SampleNewbornDAOImpl","getData()",e.toString());
			throw new LIMSRuntimeException("Error in SampleNewborn getData()", e);
		}
	}

	public SampleNewborn readSampleNewborn(String idString) {
		SampleNewborn sh = null;
		try {
			sh = (SampleNewborn)HibernateUtil.getSession().get(SampleNewborn.class, idString);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			LogEvent.logError("SampleNewbornDAOImpl","readSampleNewborn()",e.toString());
			throw new LIMSRuntimeException("Error in SampleNewborn readSampleNewborn()", e);
		}			
		
		return sh;
	}
}