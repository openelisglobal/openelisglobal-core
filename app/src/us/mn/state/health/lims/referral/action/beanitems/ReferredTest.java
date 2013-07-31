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
package us.mn.state.health.lims.referral.action.beanitems;

import java.util.List;

import us.mn.state.health.lims.common.util.IdValuePair;
import us.mn.state.health.lims.referral.valueholder.ReferralResult;


public class ReferredTest implements IReferralResultTest {

    private String referralId;

    private String referredTestId;
	private String referredResult = "";
	private String referredDictionaryResult;
	private List<IdValuePair> dictionaryResults;
	private String referredResultType = "";
	private String referredReportDate;
	private String referralResultId;
	private boolean remove = false;
    private String referredMultiDictionaryResult;

    public String getReferralId() {
        return referralId;
    }
    
    public void setReferralId(String referralId) {
        this.referralId = referralId;
    }
    
    public void setReferredTestId(String referredTestId) {
		this.referredTestId = referredTestId;
	}

	public String getReferredTestId() {
		return referredTestId;
	}

	public String getReferredResult() {
		return referredResult;
	}

	public void setReferredResult(String referredResult) {
		this.referredResult = referredResult;
	}

	public String getReferredResultType() {
		return referredResultType;
	}

	public void setReferredResultType(String referredResultType) {
		this.referredResultType = referredResultType;
	}

	public String getReferredReportDate() {
		return referredReportDate;
	}

	public void setReferredReportDate(String referredReportDate) {
		this.referredReportDate = referredReportDate == null ? "" : referredReportDate;
	}

	public void setRemove(boolean remove) {
		this.remove = remove;
	}
	public boolean isRemove() {
		return remove;
	}
	public void setReferralResultId(String referralResultId) {
		this.referralResultId = referralResultId;
	}

	public String getReferralResultId() {
		return referralResultId;
	}

	public void setReferredDictionaryResult(String referredDictionaryResult) {
		this.referredDictionaryResult = referredDictionaryResult;
	}

	public String getReferredDictionaryResult() {
		return referredDictionaryResult;
	}

	public void setDictionaryResults(List<IdValuePair> dictionaryResults) {
		this.dictionaryResults = dictionaryResults;
	}

	public List<IdValuePair> getDictionaryResults() {
		return dictionaryResults;
	}

    public String getReferredMultiDictionaryResult() {
        return referredMultiDictionaryResult;
    }

    public void setReferredMultiDictionaryResult(String referredMultiDictionaryResult) {
        this.referredMultiDictionaryResult = referredMultiDictionaryResult;
    }
}
