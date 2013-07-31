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
package us.mn.state.health.lims.result.valueholder;

import java.util.Comparator;

import us.mn.state.health.lims.common.util.StringUtil;

/**
 * @author diane benz bugzilla 1856
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation. 
 */
public class ResultsVerificationTestComparator implements Comparable {
	String name;


	// You can put the default sorting capability here
	public int compareTo(Object obj) {
		Sample_TestAnalyte sta= (Sample_TestAnalyte)obj;
		return this.name.compareTo(sta.getAnalysis().getTest().getSortOrder());
	} 




	public static final Comparator SORT_ORDER_COMPARATOR =
		new Comparator() {
		public int compare(Object a, Object b) {
			Sample_TestAnalyte sta_a = (Sample_TestAnalyte)a;
			Sample_TestAnalyte sta_b = (Sample_TestAnalyte)b;

			String aValue = sta_a.getAnalysis().getTest().getSortOrder();
			String bValue = sta_b.getAnalysis().getTest().getSortOrder();
			if (StringUtil.isNullorNill(aValue)) {
				aValue = "0";
			}

			if (StringUtil.isNullorNill(bValue)) {
				bValue = "0";
			}
			return (aValue.compareTo(bValue));

		}
	};

	public static final Comparator ACCESSION_NUMBER_COMPARATOR =
		new Comparator() {
		public int compare(Object a, Object b) {
			Sample_TestAnalyte sta_a = (Sample_TestAnalyte)a;
			Sample_TestAnalyte sta_b = (Sample_TestAnalyte)b;

			String aValue = getLongVersionOfNumber(sta_a.getSample().getAccessionNumber());
			String bValue = getLongVersionOfNumber(sta_b.getSample().getAccessionNumber());

			return (aValue.compareTo(bValue));

		}
	};

	private static String getLongVersionOfNumber(String number) {
		String longVersion = "";
		if (number.length() < 10) {
			int zeroPaddingLength = 10 - number.length();
			StringBuffer zeros = new StringBuffer();
			for (int i = 0; i < zeroPaddingLength; i++) {
				zeros.append("0");
			}
			longVersion = zeros + number;
		}
		if (number.length() == 10) {
			longVersion = number;
		}

		return longVersion;
	}   
}
