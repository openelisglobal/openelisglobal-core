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
package us.mn.state.health.lims.sample.valueholder;

import java.util.Comparator;

/**
 * @author diane benz 
 * bugzilla 2513
 */
public class SampleComparator implements Comparable {
	String accessionNumber;


	// You can put the default sorting capability here
	public int compareTo(Object obj) {
		Sample s = (Sample)obj;
		return this.accessionNumber.compareTo(s.getAccessionNumber());
	} 




	public static final Comparator ACCESSION_NUMBER_COMPARATOR =
		new Comparator() {
		public int compare(Object a, Object b) {
			Sample s_a = (Sample)a;
			Sample s_b = (Sample)b;

			String aValue = getLongVersionOfNumber(s_a.getAccessionNumber());
			String bValue = getLongVersionOfNumber(s_b.getAccessionNumber());
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
