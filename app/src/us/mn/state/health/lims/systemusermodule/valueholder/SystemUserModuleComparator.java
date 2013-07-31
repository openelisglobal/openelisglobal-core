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
package us.mn.state.health.lims.systemusermodule.valueholder;

import java.util.Comparator;

public class SystemUserModuleComparator implements Comparable {
	String lastName;
	String systemUserId;

	public int compareTo(Object obj) {
		SystemUserModule sum = (SystemUserModule)obj;
		return this.lastName.compareTo(sum.getSystemUser().getLastName());
	}
	
	public static final Comparator LASTNAME_COMPARATOR =
		new Comparator() {
		public int compare(Object a, Object b) {
			SystemUserModule c_a = (SystemUserModule)a;
			SystemUserModule c_b = (SystemUserModule)b;
 
			return ((c_a.getSystemUser().getLastName().toLowerCase()).compareTo((c_b.getSystemUser().getLastName().toLowerCase())));
		}
	};
	
	public static final Comparator SYS_USER_ID_COMPARATOR =
		new Comparator() {
		public int compare(Object a, Object b) {
			SystemUserModule c_a = (SystemUserModule)a;
			SystemUserModule c_b = (SystemUserModule)b;
 
			return ((c_a.getSystemUser().getId()).compareTo((c_b.getSystemUser().getId())));
		}
	};	
}