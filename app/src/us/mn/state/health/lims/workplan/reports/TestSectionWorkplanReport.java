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
package us.mn.state.health.lims.workplan.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.ConfigurationProperties.Property;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.test.beanItems.TestResultItem;

public class TestSectionWorkplanReport implements IWorkplanReport {
	
	private static final String BASE_FILE_NAME = "WorkplanByTestSection";
	private static final String FILE_NAME_WITH_RESULTS = "WorkplanResultsByTestSection";
	private final HashMap<String, Object> parameterMap = new HashMap<String, Object>();
	private String testSection = "";
	private String messageKey = "banner.menu.workplan.";
	
	public TestSectionWorkplanReport(String testSection) {
		messageKey = messageKey + testSection;
		this.testSection = StringUtil.getContextualMessageForKey(messageKey);
		
		if(this.testSection == null){
			this.testSection = testSection;
		}
		
	}
	
	public String getFileName() {
		return  ConfigurationProperties.getInstance().isPropertyValueEqual(Property.RESULTS_ON_WORKPLAN, "false") ? BASE_FILE_NAME : FILE_NAME_WITH_RESULTS;
	}
	
	public HashMap<String, Object> getParameters() {
		parameterMap.put("testSection", testSection);
		parameterMap.put("printSubjectNo", ConfigurationProperties.getInstance().isPropertyValueEqual(Property.SUBJECT_ON_WORKPLAN, "true"));
		parameterMap.put("printNextVisit", ConfigurationProperties.getInstance().isPropertyValueEqual(Property.NEXT_VISIT_DATE_ON_WORKPLAN, "true"));
		parameterMap.put("labNumberTitle", StringUtil.getContextualMessageForKey("quick.entry.accession.number"));
		parameterMap.put("subjectNoTitle", StringUtil.getContextualMessageForKey("patient.subject.number"));
		parameterMap.put("labName", ConfigurationProperties.getInstance().getPropertyValue(Property.SiteName));

		return parameterMap;	
	
	}
	
	public List<?> prepareRows(BaseActionForm dynaForm) {
		
		@SuppressWarnings("unchecked")
		List<TestResultItem> workplanTests  = (List<TestResultItem>) dynaForm.get("workplanTests");
		
		//remove unwanted tests from workplan
		List<TestResultItem> includedTests = new ArrayList<TestResultItem>();
		for(TestResultItem test : workplanTests){
			if (!test.isNotIncludedInWorkplan()){
				includedTests.add(test);
			}else{
				//handles the case that the checkbox is unchecked
				test.setNotIncludedInWorkplan(false);
			}
		}
		return includedTests;
	}

}
