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
package us.mn.state.health.lims.common.daoimpl;

import java.util.List;
import java.util.Vector;

import us.mn.state.health.lims.common.dao.EnumDAO;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.valueholder.EnumValueItem;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.hibernate.HibernateUtil;

public class EnumDAOImpl extends BaseDAOImpl implements EnumDAO {

	public List getEnumObj(String enumName) {

		List list = new Vector();
		try {
			String sql = "from " + enumName + " t order by t.id desc";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();

			list = query.list();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("EnumDAOImpl","getEnumObj()",e.toString());			
			throw new LIMSRuntimeException(
					"Error in getEnumObj() for " + enumName, e);
		}

		return list;

	}
	
	public List getEnumObjForHL7(String enumName, boolean linked) {

		List list = new Vector();
		try {
			String sql = "";
			if (!linked) {
			  sql = "from " + enumName + " t where t.id not in (select cex.selectedLocalCodeElementId from CodeElementXref cex) order by t.id desc";
			} else {
			  sql = "from " + enumName + " t where t.id in (select cex.selectedLocalCodeElementId from CodeElementXref cex) order by t.id desc";
			}
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();

			list = query.list();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("EnumDAOImpl","getEnumObjForHL7()",e.toString());
			throw new LIMSRuntimeException(
					"Error in getEnumObjForHL7() for " + enumName, e);
		}

		return list;

	}

	public EnumValueItem getEnumValueItem(String enumName, String key) {
		List list = new Vector();

		try {
			String sql = "from " + enumName
					+ " t where id = :param";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(
					sql);

			query.setParameter("param", key);
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();

			EnumValueItem evi = null;
			if (list.size() > 0)
				evi = (EnumValueItem) list.get(0);

			return evi;
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("EnumDAOImpl","getEnumValueItem()",e.toString());
			throw new LIMSRuntimeException("Error in getEnumValueItem() for "
					+ enumName, e);
		}

	}
	

	public static String getTableValueholderName(String tableName) {
		StringBuffer valueholderName = new StringBuffer();
		String tName = tableName.trim().toLowerCase();
		char token = tName.charAt(0);
		if (Character.isLowerCase(token)) {
			valueholderName.append(Character.toUpperCase(token));
		}

		for (int i = 1; i < tName.length(); i++) {
			char c = tName.charAt(i);
			if (c == '_') {
				i++;
				valueholderName.append(Character.toUpperCase(tName.charAt(i)));
			} else {
				valueholderName.append(tName.charAt(i));
			}
		}

		return valueholderName.toString();
	}
}