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
package us.mn.state.health.lims.program.daoimpl;

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
import us.mn.state.health.lims.program.dao.ProgramDAO;
import us.mn.state.health.lims.program.valueholder.Program;

/**
 * @author diane benz
 */
public class ProgramDAOImpl extends BaseDAOImpl implements ProgramDAO {

	public void deleteData(List programs) throws LIMSRuntimeException {
		//add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			for (int i = 0; i < programs.size(); i++) {
				Program data = (Program)programs.get(i);
			
				Program oldData = (Program)readProgram(data.getId());
				Program newData = new Program();

				String sysUserId = data.getSysUserId();
				String event = IActionConstants.AUDIT_TRAIL_DELETE;
				String tableName = "PROGRAM";
				auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
			}
		}  catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ProgramDAOImpl","AuditTrail deleteData()",e.toString());
			throw new LIMSRuntimeException("Error in Program AuditTrail deleteData()", e);
		}  
		
		try {		
			for (int i = 0; i < programs.size(); i++) {
				Program data = (Program) programs.get(i);
				//bugzilla 2206
				data = (Program)readProgram(data.getId());
				HibernateUtil.getSession().delete(data);
				HibernateUtil.getSession().flush();
				HibernateUtil.getSession().clear();			
			}			
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ProgramDAOImpl","deleteData()",e.toString());
			throw new LIMSRuntimeException("Error in Program deleteData()", e);
		}
	}

	public boolean insertData(Program program) throws LIMSRuntimeException {
		try {
			// bugzilla 1482 throw Exception if record already exists
			if (duplicateProgramExists(program)) {
				throw new LIMSDuplicateRecordException(
						"Duplicate record exists for "
								+ program.getProgramName());
			}
			
			String id = (String)HibernateUtil.getSession().save(program);
			program.setId(id);
			
			//bugzilla 1824 inserts will be logged in history table
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = program.getSysUserId();
			String tableName = "PROGRAM";
			auditDAO.saveNewHistory(program,sysUserId,tableName);
			
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ProgramDAOImpl","insertData()",e.toString());
			throw new LIMSRuntimeException("Error in Program insertData()", e);
		}
		
		return true;
	}

	public void updateData(Program program) throws LIMSRuntimeException {
		// bugzilla 1482 throw Exception if record already exists
		try {
			if (duplicateProgramExists(program)) {
				throw new LIMSDuplicateRecordException(
						"Duplicate record exists for "
								+ program.getProgramName());
			}
		} catch (Exception e) {
    		//bugzilla 2154
			LogEvent.logError("ProgramDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException("Error in Panel updateData()",
					e);
		}
		
		Program oldData = (Program)readProgram(program.getId());
		Program newData = program;

		//add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = program.getSysUserId();
			String event = IActionConstants.AUDIT_TRAIL_UPDATE;
			String tableName = "PROGRAM";
			auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
		}  catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ProgramDAOImpl","AuditTrail updateData()",e.toString());
			throw new LIMSRuntimeException("Error in Program AuditTrail updateData()", e);
		}  
			
		try {
			HibernateUtil.getSession().merge(program);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			HibernateUtil.getSession().evict(program);
			HibernateUtil.getSession().refresh(program);			
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ProgramDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException("Error in Program updateData()", e);
		}
	}

	public void getData(Program program) throws LIMSRuntimeException {
		try {
			Program pro = (Program)HibernateUtil.getSession().get(Program.class, program.getId());
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			if (pro != null) {
			  PropertyUtils.copyProperties(program, pro);
			} else {
				program.setId(null);
			}
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ProgramDAOImpl","getData()",e.toString());
			throw new LIMSRuntimeException("Error in Program getData()", e);
		}
	}

	public List getAllPrograms() throws LIMSRuntimeException {
		List list = new Vector();
		try {
			String sql = "from Program";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			//query.setMaxResults(10);
			//query.setFirstResult(3);				
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ProgramDAOImpl","getAllPrograms()",e.toString());
			throw new LIMSRuntimeException("Error in Program getAllPrograms()",e);
		}

		return list;
	}

	public List getPageOfPrograms(int startingRecNo) throws LIMSRuntimeException {
		List list = new Vector();
		try {
			// calculate maxRow to be one more than the page size
			int endingRecNo = startingRecNo + (SystemConfiguration.getInstance().getDefaultPageSize() + 1);
			
			//bugzilla 1399
			String sql = "from Program p order by p.programName";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setFirstResult(startingRecNo-1);
			query.setMaxResults(endingRecNo-1); 
					
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ProgramDAOImpl","getPageOfPrograms()",e.toString());
			throw new LIMSRuntimeException("Error in Program getPageOfPrograms()", e);
		}

		return list;
	}

	public Program readProgram(String idString) {
		Program program = null;
		try {
			program = (Program)HibernateUtil.getSession().get(Program.class, idString);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ProgramDAOImpl","readProgram()",e.toString());
			throw new LIMSRuntimeException("Error in Program readProgram()", e);
		}			
		
		return program;
	}
	
	public List getNextProgramRecord(String id) throws LIMSRuntimeException {

		return getNextRecord(id, "Program", Program.class);

	}

	public List getPreviousProgramRecord(String id) throws LIMSRuntimeException {

		return getPreviousRecord(id, "Program", Program.class);
	}
	
	//bugzilla 1411
	public Integer getTotalProgramCount() throws LIMSRuntimeException {
		return getTotalCount("Program", Program.class);
	}
	
	//overriding BaseDAOImpl bugzilla 1427 pass in name not id
	public List getNextRecord(String id, String table, Class clazz) throws LIMSRuntimeException {	
				
		List list = new Vector();
		try {			
			String sql = "from "+table+" t where name >= "+ enquote(id) + " order by t.programName";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setFirstResult(1);
			query.setMaxResults(2); 	
			
			list = query.list();		
			
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ProgramDAOImpl","getNextRecord()",e.toString());
			throw new LIMSRuntimeException("Error in getNextRecord() for " + table, e);
		}
		
		return list;		
	}

	//overriding BaseDAOImpl bugzilla 1427 pass in name not id
	public List getPreviousRecord(String id, String table, Class clazz) throws LIMSRuntimeException {		
		
		List list = new Vector();
		try {			
			String sql = "from "+table+" t order by t.programName desc where name <= "+ enquote(id);
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setFirstResult(1);
			query.setMaxResults(2); 	
			
			list = query.list();					
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ProgramDAOImpl","getPreviousRecord()",e.toString());
			throw new LIMSRuntimeException("Error in getPreviousRecord() for " + table, e);
		} 

		return list;
	}
	
	// bugzilla 1482
	private boolean duplicateProgramExists(Program program) throws LIMSRuntimeException {
		try {

			List list = new ArrayList();

			// not case sensitive hemolysis and Hemolysis are considered
			// duplicates
			String sql = "from Program t where (trim(lower(t.programName)) = :param and t.id != :param2) or (trim(lower(t.code)) = :param3 and t.id != :param2)";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
			query.setParameter("param", program.getProgramName().toLowerCase().trim());
	
			// initialize with 0 (for new records where no id has been generated
			// yet
			String programId = "0";
			if (!StringUtil.isNullorNill(program.getId())) {
				programId = program.getId();
			}
			String programCode = "";
			if (!StringUtil.isNullorNill(program.getCode())) {
				programCode = program.getCode().trim().toLowerCase();
			}
			query.setParameter("param2", programId);
			query.setParameter("param3", programCode);

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
			LogEvent.logError("ProgramDAOImpl","duplicateProgramExists()",e.toString());
			throw new LIMSRuntimeException(
					"Error in duplicateProgramExists()", e);
		}
	}

}