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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import us.mn.state.health.lims.common.action.IActionConstants;

//import org.apache.commons.lang.builder.EqualsBuilder;
//import org.apache.commons.lang.builder.HashCodeBuilder;

public class EnumValueImpl implements EnumValue, Collection {
	private String enumName = null;

	private Map map = null;

	private List list = null;

	public EnumValueImpl() {

	}

	public EnumValueImpl(String name) {
		setEnumName(name);
	}

	public String getEnumName() {
		return enumName;
	}

	public void setEnumName(String enumName) {
		this.enumName = enumName;
	}

	public int getSize() {
		return getMap().size();
	}

	public void putValue(String key, EnumValueItem enumValueItem) {
		// Add enumValueItem to both the List and Map maintained by this class
		if (null != enumValueItem)
			enumValueItem.setEnumName(this.getEnumName());

		getList().add(enumValueItem);
		getMap().put(key, enumValueItem);
	}

	public EnumValueItem getValue(String key) {
		return (EnumValueItem) getMap().get(key);
	}

	public List getValues() {
		return getList();
	}

	public List getActiveValues() {
		Iterator iterator = getList().iterator();
		List list = new ArrayList();
		EnumValueItem value = null;
		while (iterator.hasNext()) {
			value = (EnumValueItem) iterator.next();
			if (value.getIsActive().equals(IActionConstants.YES)) {
				list.add(value);
			}
		}
		return list;
	}

	public List getInActiveValues() {
		Iterator iterator = getList().iterator();
		List list = new ArrayList();
		EnumValueItem value = null;
		while (iterator.hasNext()) {
			value = (EnumValueItem) iterator.next();
			if (!value.getIsActive().equals(IActionConstants.YES)) {
				list.add(value);
			}
		}
		return list;
	}

	protected List getList() {
		if (list == null) {
			list = new ArrayList();
		}
		return list;
	}

	protected void setList(List list) {
		this.list = list;
	}

	protected Map getMap() {
		if (map == null) {
			map = new HashMap();
		}
		return map;
	}

	protected void setMap(Map map) {
		this.map = map;
	}

	//
	// Following methods implement the Collection interface
	//

	/**
	 * @see java.util.Collection#add(Object)
	 */
	public boolean add(Object o) {
		return false;
	}

	/**
	 * @see java.util.Collection#addAll(Collection)
	 */
	public boolean addAll(Collection c) {
		return false;
	}

	/**
	 * @see java.util.Collection#clear()
	 */
	public void clear() {
		getList().clear();
		getMap().clear();
	}

	/**
	 * @see java.util.Collection#contains(Object)
	 */
	public boolean contains(Object o) {
		return getMap().containsValue(o);
	}

	/**
	 * @see java.util.Collection#containsAll(Collection)
	 */
	public boolean containsAll(Collection c) {
		Iterator all = c.iterator();
		while (all.hasNext()) {
			if (!getMap().containsValue(all.next())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @see java.util.Collection#isEmpty()
	 */
	public boolean isEmpty() {
		return getMap().isEmpty();
	}

	/**
	 * @see java.util.Collection#iterator()
	 */
	public Iterator iterator() {
		return getValues().iterator();
	}

	/**
	 * @see java.util.Collection#remove(Object)
	 */
	public boolean remove(Object o) {
		return false;
	}

	/**
	 * @see java.util.Collection#removeAll(Collection)
	 */
	public boolean removeAll(Collection c) {
		return false;
	}

	/**
	 * @see java.util.Collection#retainAll(Collection)
	 */
	public boolean retainAll(Collection c) {
		return false;
	}

	/**
	 * @see java.util.Collection#size()
	 */
	public int size() {
		return getMap().size();
	}

	/**
	 * @see java.util.Collection#toArray()
	 */
	public Object[] toArray() {
		return getValues().toArray();
	}

	/**
	 * @see java.util.Collection#toArray(Object[])
	 */
	public Object[] toArray(Object[] a) {
		return getValues().toArray(a);
	}

}