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
package us.mn.state.health.lims.reports.send.sample.daoimpl;

import us.mn.state.health.lims.common.daoimpl.BaseDAOImpl;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.reports.send.sample.dao.SampleTransmissionSequenceDAO;
import us.mn.state.health.lims.reports.send.sample.valueholder.SampleTransmissionSequence;

/**
 * @author diane benz
 * bugzilla 2393
 */
public class SampleTransmissionSequenceDAOImpl extends BaseDAOImpl implements SampleTransmissionSequenceDAO {


	public String getNextSampleTransmissionSequenceNumber(SampleTransmissionSequence sampleTransmissionSequence) throws LIMSRuntimeException {
		String sequenceNumber = null;
		try {		

			sequenceNumber = (String)HibernateUtil.getSession().save(sampleTransmissionSequence);			
	
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();					
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SampleTransmissionSequenceDAOImpl","getNextSampleTransmissionSequenceNumber()",e.toString());
			throw new LIMSRuntimeException("Error in SampleTransmissionSequence getNextSampleTransmissionSequenceNumber()", e);
		}
		
		return sequenceNumber;

	}

}