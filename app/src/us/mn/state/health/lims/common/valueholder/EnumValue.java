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
package us.mn.state.health.lims.common.valueholder;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface EnumValue extends Serializable, Collection {
	// get/set the name of the enumeration
	public String getEnumName();

	public void setEnumName(String name);

	// Provide the number of EnumValueItem objects currently stored
	public int getSize();

	// to add an EnumValueItem - provide a key for the object
	public void putValue(String key, EnumValueItem enumvalue);

	// Find an EnumValueItem by key
	public EnumValueItem getValue(String key);

	// Several ways to get the EnumValueItems as a Collection
	// Return all EnumValueItems
	public List getValues();

	// Return Only those considered active
	public List getActiveValues();

	// Return only those inactive
	public List getInActiveValues();
}