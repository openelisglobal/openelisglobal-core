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
* Copyright (C) ITECH, University of Washington, Seattle WA.  All Rights Reserved.
*
*/
package us.mn.state.health.lims.reports.action.implementation;

import net.sf.jasperreports.engine.JRDataSource;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.ConfigurationProperties.Property;


public class HaitiLNSPFollowupRequiredByLocation extends HaitiIndicatorReport {



	@Override
	protected String errorReportFileName() {
		return HAITI_ERROR_REPORT;
	}

	@Override
	public void initializeReport(BaseActionForm dynaForm) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public JRDataSource getReportDataSource() throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String reportFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getNameForReportRequest() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getNameForReport() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getLabNameLine1() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getLabNameLine2() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getSiteLogo(){
		return ConfigurationProperties.getInstance().isPropertyValueEqual(Property.configurationName, "Haiti LNSP") ? "HaitiLNSP.jpg" : "labLogo.jpg";
	}

}
