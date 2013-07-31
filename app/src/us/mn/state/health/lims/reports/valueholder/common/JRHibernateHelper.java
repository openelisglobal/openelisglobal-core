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
package us.mn.state.health.lims.reports.valueholder.common;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
/**
 * @author benzd1
 * bugzilla 2264
 */
public class JRHibernateHelper  { 


	private JRHibernateDataSource subReportDataSource;

	public JRHibernateHelper(JRHibernateDataSource ds) {
		this.subReportDataSource = ds;
	}
	public JRHibernateDataSource getSubReportDataSource() throws JRException{
		Object obj = this.subReportDataSource.getPreviousValue();
		List listOfObjects = new ArrayList();
		listOfObjects.add(obj);
		JRHibernateDataSource ds = new JRHibernateDataSource(listOfObjects);
		return ds; 
	}

	public void setSubReportDataSource(JRHibernateDataSource subReportDataSource) {
		this.subReportDataSource = subReportDataSource;
	}

}
