package us.mn.state.health.lims.barcode.daoimpl;

import java.util.List;
import java.util.Vector;

import us.mn.state.health.lims.audittrail.dao.AuditTrailDAO;
import us.mn.state.health.lims.audittrail.daoimpl.AuditTrailDAOImpl;
import us.mn.state.health.lims.barcode.dao.BarcodeLabelInfoDAO;
import us.mn.state.health.lims.barcode.valueholder.BarcodeLabelInfo;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.daoimpl.BaseDAOImpl;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.hibernate.HibernateUtil;

public class BarcodeLabelInfoDAOImpl extends BaseDAOImpl implements BarcodeLabelInfoDAO{
	
	List list = new Vector();

	public boolean insertData(BarcodeLabelInfo barcodeLabelInfo) throws LIMSRuntimeException {
		try {
			String id = (String)HibernateUtil.getSession().save(barcodeLabelInfo);
			barcodeLabelInfo.setId(id);
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			//auditDAO.saveNewHistory(barcodeLabelInfo, barcodeLabelInfo.getSysUserId(), "BARCODE_LABEL_INFO");
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			LogEvent.logError("BarcodeLabelInfoDAOImpl","insertData()",e.toString());
			throw new LIMSRuntimeException("Error in BarcodeLabelInfo insertData()",e);
		} 
		return true;
	}
	
	public void updateData(BarcodeLabelInfo barcodeLabelInfo) throws LIMSRuntimeException {
		BarcodeLabelInfo oldData = readBarcodeLabelInfo(barcodeLabelInfo.getId());
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
		//	auditDAO.saveHistory(barcodeLabelInfo,oldData, barcodeLabelInfo.getSysUserId(), IActionConstants.AUDIT_TRAIL_UPDATE, "BARCODE_LABEL_INFO");
		}  catch (Exception e) {
			LogEvent.logError("BarcodeLabelInfoDAOImpl","AuditTrail updateData()",e.toString());
			throw new LIMSRuntimeException("Error in BarcodeLabelInfo AuditTrail updateData()", e);
		}  				
		try {
			HibernateUtil.getSession().merge(barcodeLabelInfo);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			HibernateUtil.getSession().evict(barcodeLabelInfo);
			HibernateUtil.getSession().refresh(barcodeLabelInfo);
		} catch (Exception e) {
			LogEvent.logError("BarcodeLabelInfoDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException("Error in BarcodeLabelInfo updateData()",e);
		}
	}

	public BarcodeLabelInfo getDataByCode(String code) throws LIMSRuntimeException {
		BarcodeLabelInfo bli = null;
		try {
			String sql = "From BarcodeLabelInfo b where b.code = :param";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setParameter("param", code.trim());
			list = query.list();
			if (list != null && list.size() > 0)
				bli = (BarcodeLabelInfo) list.get(0);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			LogEvent.logError("BarcodeLabelInfoDAOImpl","getBarcodeLabelInfoByCode()",e.toString());
			throw new LIMSRuntimeException("Error in getBarcodeLabelInfoByCode()", e);
		}
		return bli;
	}
	
	public BarcodeLabelInfo readBarcodeLabelInfo(String idString) {
		BarcodeLabelInfo recoveredBarcodeLabelInfo;
		try {
			recoveredBarcodeLabelInfo = (BarcodeLabelInfo)HibernateUtil.getSession().get(BarcodeLabelInfo.class, idString);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			LogEvent.logError("BarcodeLabelInfoDAOImpl", "readBarcodeLabelInfo()", e.toString());
			throw new LIMSRuntimeException("Error in BarcodeLabelInfo readBarcodeLabelInfo()", e);
		}			
		return recoveredBarcodeLabelInfo;
	}	

}
