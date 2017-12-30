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
package us.mn.state.health.lims.common.provider.reports.toplink;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import us.mn.state.health.lims.common.log.LogEvent;

/**
 * @author benzd1
 *
 */
public class QueryResultDataSource implements JRDataSource {

	private Iterator iterator;

	private Object currentValue;

	/**
	 * @param list
	 */
	public QueryResultDataSource(List list) {
		this.iterator = list.iterator();
	}

	/* (non-Javadoc)
	 * @see net.sf.jasperreports.engine.JRDataSource#getFieldValue(net.sf.jasperreports.engine.JRField)
	 */
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;

		try {
			Method getter = PropertyUtils.getReadMethod(PropertyUtils
					.getPropertyDescriptor(currentValue, field.getName()));
			value = getter.invoke(currentValue, (Object[])null);

		} catch (Exception ex) {
			//bugzilla 2154
			LogEvent.logError("QueryResultDataSource","getFieldValue()",ex.toString());
		}
		return value;
	}

	/* (non-Javadoc)
	 * @see net.sf.jasperreports.engine.JRDataSource#next()
	 */
	public boolean next() throws JRException {
		currentValue = iterator.hasNext() ? iterator.next() : null;
		return (currentValue != null);
	}
}