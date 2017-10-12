package us.mn.state.health.lims.barcode.dao;

import us.mn.state.health.lims.barcode.valueholder.BarcodeLabelInfo;
import us.mn.state.health.lims.common.dao.BaseDAO;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;

public interface BarcodeLabelInfoDAO extends BaseDAO {

	public boolean insertData(BarcodeLabelInfo barcodeLabelInfo) throws LIMSRuntimeException;
	
	public void updateData(BarcodeLabelInfo barcodeLabelInfo) throws LIMSRuntimeException;
	
	public BarcodeLabelInfo getDataByCode(String code) throws LIMSRuntimeException;
}
