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
* Copyright (C) ITECH, University of Washington, Seattle WA.  All Rights Reserved.
*
*/
package us.mn.state.health.lims.laborder.daoimpl;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import us.mn.state.health.lims.common.daoimpl.BaseDAOImpl;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.laborder.dao.LabOrderItemDAO;
import us.mn.state.health.lims.laborder.valueholder.LabOrderItem;

public class LabOrderItemDAOImpl extends BaseDAOImpl implements LabOrderItemDAO {

	@Override
	public List<LabOrderItem> getLabOrderItemsByTableAndAction(String tableId, String action) throws LIMSRuntimeException {
		String sql = "FROM LabOrderItem loi where loi.tableRef = :tableId and loi.action = :action";
		
		try {
			Query query = HibernateUtil.getSession().createQuery(sql);
			query.setInteger("tableId", Integer.parseInt(tableId));
			query.setString("action", action);
			@SuppressWarnings("unchecked")
			List<LabOrderItem> items = query.list();
			closeSession();
			return items;
		} catch (HibernateException e) {
			handleException(e, "getLabOrderItemsByTableAndAction");
		}
		return null;
	}

	
}
