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
package us.mn.state.health.lims.referral.util;

import java.util.ArrayList;
import java.util.List;

import us.mn.state.health.lims.common.util.IdValuePair;
import us.mn.state.health.lims.referral.dao.ReferralReasonDAO;
import us.mn.state.health.lims.referral.daoimpl.ReferralReasonDAOImpl;
import us.mn.state.health.lims.referral.valueholder.ReferralReason;

public class ReferralUtil {

	private static List<IdValuePair> referralReasons;

	public static List<IdValuePair> getReferralReasons() {
		if( referralReasons == null){
			referralReasons = new ArrayList<IdValuePair>();
			ReferralReasonDAO referralReasonDAO = new ReferralReasonDAOImpl();
			List<ReferralReason> reasonList = referralReasonDAO.getAllReferralReasons();

			for( ReferralReason reason : reasonList){
				referralReasons.add(new IdValuePair(reason.getId(), reason.getLocalizedName()));
			}
		}

		return referralReasons;
	}
}
