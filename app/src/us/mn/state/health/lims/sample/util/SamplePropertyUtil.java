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
*
* Contributor(s): CIRG, University of Washington, Seattle WA.
*/
package us.mn.state.health.lims.sample.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.DynaActionForm;

import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.typeofsample.dao.TypeOfSampleDAO;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleDAOImpl;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;

public class SamplePropertyUtil {

	public static void addDefaultEntryDate(DynaActionForm dynaForm, String name, Locale locale) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Date today = Calendar.getInstance().getTime();
		String dateAsText = DateUtil.formatDateAsText(today, locale);

		PropertyUtils.setProperty(dynaForm, name, dateAsText);
	}

	public static void loadSampleTypes(DynaActionForm dynaForm, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
		List<TypeOfSample> list = typeOfSampleDAO.getTypesForDomainBySortOrder(TypeOfSampleDAO.SampleDomain.HUMAN);

		List<TypeOfSample> filteredList = new ArrayList<TypeOfSample>();
		
		for( TypeOfSample type : list){
			if( type.isActive()){
				filteredList.add(type);
			}
		}
		PropertyUtils.setProperty(dynaForm, name, filteredList);
	}
}
