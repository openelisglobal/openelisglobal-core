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
* Copyright (C) CIRG, University of Washington, Seattle WA.  All Rights Reserved.
*
*/
package us.mn.state.health.lims.requester.daoimpl;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import us.mn.state.health.lims.audittrail.daoimpl.AuditTrailDAOImpl;
import us.mn.state.health.lims.common.daoimpl.BaseDAOImpl;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.requester.dao.SampleRequesterDAO;
import us.mn.state.health.lims.requester.valueholder.SampleRequester;

/*
 */
public class SampleRequesterDAOImpl extends BaseDAOImpl implements SampleRequesterDAO {

	public boolean insertData(SampleRequester sampleRequester) throws LIMSRuntimeException {
		try {
			HibernateUtil.getSession().save(sampleRequester);

			new AuditTrailDAOImpl().saveNewHistory(sampleRequester, sampleRequester.getSysUserId(), "SAMPLE_REQUESTER");
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();

		} catch (Exception e) {
			LogEvent.logError("SampleRequesterDAOImpl","insertData()",e.toString());
			throw new LIMSRuntimeException("Error in SampleRequester insertData()", e);
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SampleRequester> getRequestersForSampleId(String sampleId) throws LIMSRuntimeException {
		String sql = "From SampleRequester sr where sr.sampleId = :sampleId";

		try{
			Query query = HibernateUtil.getSession().createQuery(sql);
			query.setLong("sampleId", Long.parseLong(sampleId));
			List<SampleRequester> requester = query.list();

			closeSession();

			return requester;

		}catch(HibernateException e){
			handleException(e, "getRequesterForSampleId");
		}
		return null;
	}


}