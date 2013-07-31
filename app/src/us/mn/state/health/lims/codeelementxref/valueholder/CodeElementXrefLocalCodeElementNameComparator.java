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
package us.mn.state.health.lims.codeelementxref.valueholder;

import java.util.Comparator;

import us.mn.state.health.lims.codeelementtype.valueholder.CodeElementType;
import us.mn.state.health.lims.common.dao.EnumDAO;
import us.mn.state.health.lims.common.daoimpl.EnumDAOImpl;
import us.mn.state.health.lims.common.valueholder.EnumValueItem;

public class CodeElementXrefLocalCodeElementNameComparator implements
		Comparable {
	String localCodeElementName;

	EnumDAO enumDAO;

	// You can put the default sorting capability here
	public int compareTo(Object obj) {
		CodeElementXref cex = (CodeElementXref) obj;
		CodeElementType cet = (CodeElementType) cex.getCodeElementType();
		enumDAO = new EnumDAOImpl();
		EnumValueItem evii = enumDAO.getEnumValueItem(EnumDAOImpl
				//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
				.getTableValueholderName(cet.getReferenceTables().getName()),
				cex.getSelectedLocalCodeElementId());
		return this.localCodeElementName.compareTo(evii.getName());
	}
	

	public static final Comparator LOCAL_CODE_ELEMENT_NAME_COMPARATOR = new Comparator() {
	      public int compare(Object a, Object b) {
			CodeElementXref cex_a = (CodeElementXref) a;
			CodeElementXref cex_b = (CodeElementXref) b;

			CodeElementType cet_a = (CodeElementType) cex_a
					.getCodeElementType();
			EnumDAO enumDAO = new EnumDAOImpl();
			
			//bugzilla 2571 (fixed NullPointerException)
			String aValue = "";
			EnumValueItem evii_a = enumDAO.getEnumValueItem(EnumDAOImpl
					.getTableValueholderName(cet_a.getReferenceTables()
							.getName()), cex_a.getSelectedLocalCodeElementId());
			if (evii_a != null)
				aValue = evii_a.getName().toLowerCase();

			CodeElementType cet_b = (CodeElementType) cex_b
			.getCodeElementType();
			enumDAO = new EnumDAOImpl();
			
			String bValue = "";
			EnumValueItem evii_b = enumDAO.getEnumValueItem(EnumDAOImpl
					.getTableValueholderName(cet_b.getReferenceTables()
							.getName()), cex_b.getSelectedLocalCodeElementId());
			
			if (evii_b != null)
				bValue = evii_b.getName().toLowerCase();

			return ((aValue).compareTo((bValue)));

		}
	};


}
