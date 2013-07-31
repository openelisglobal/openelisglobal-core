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
package us.mn.state.health.lims.systemmodule.valueholder;

import java.util.Comparator;

public class SystemModuleComparator implements Comparable {
	String description;

	public int compareTo(Object obj) {
		SystemModule sm = (SystemModule)obj;
		return this.description.compareTo(sm.getDescription());
	}
	
	public static final Comparator DESC_COMPARATOR =
		new Comparator() {
		public int compare(Object a, Object b) {
			SystemModule c_a = (SystemModule)a;
			SystemModule c_b = (SystemModule)b;
 
			return ((c_a.getDescription().toLowerCase()).compareTo((c_b.getDescription().toLowerCase())));
		}
	};
}