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
import us.mn.state.health.lims.laborder.dao.LabOrderTypeDAO;
import us.mn.state.health.lims.laborder.valueholder.LabOrderType;

public class LabOrderTypeDAOImpl extends BaseDAOImpl implements LabOrderTypeDAO {

	@Override
	public List<LabOrderType> getLabOrderTypesByDomainAndContext(String domain, String context) throws LIMSRuntimeException {
		String sql = "From LabOrderType lot where lot.domain = :domain and context = :context order by lot.sortOrder";

		try {
			Query query = HibernateUtil.getSession().createQuery(sql);
			query.setString("domain", domain);
			query.setString("context", context);
			@SuppressWarnings("unchecked")
			List<LabOrderType> orderTypes = query.list();
			closeSession();
			return orderTypes;
		} catch (HibernateException e) {
			handleException(e, "getLabOrderTypesByDomainAndContext");
		}

		return null;
	}

	@Override
	public LabOrderType getLabOrderTypeById(String labOrderTypeId) throws LIMSRuntimeException {
		String sql = "From LabOrderType lot where lot.id = :id";

		try {
			Query query = HibernateUtil.getSession().createQuery(sql);
			query.setInteger("id", Integer.parseInt(labOrderTypeId));

			LabOrderType orderType = (LabOrderType) query.uniqueResult();
			closeSession();
			return orderType;
		} catch (HibernateException e) {
			handleException(e, "getLabOrderTypeById");
		}

		return null;
	}

	@Override
	public LabOrderType getLabOrderTypeByType(String type) throws LIMSRuntimeException {
		String sql = "From LabOrderType lot where lot.type = :type";

		try {
			Query query = HibernateUtil.getSession().createQuery(sql);
			query.setString("type", type);

			LabOrderType orderType = (LabOrderType) query.uniqueResult();
			closeSession();
			return orderType;
		} catch (HibernateException e) {
			handleException(e, "getLabOrderTypeByType");
		}

		return null;
	}

}
