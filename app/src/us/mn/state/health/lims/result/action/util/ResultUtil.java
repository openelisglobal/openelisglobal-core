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
* Copyright (C) CIRG, University of Washington, Seattle WA.  All Rights Reserved.
*
*/
package us.mn.state.health.lims.result.action.util;

import org.apache.commons.validator.GenericValidator;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.test.beanItems.TestResultItem;
import us.mn.state.health.lims.testanalyte.dao.TestAnalyteDAO;
import us.mn.state.health.lims.testanalyte.daoimpl.TestAnalyteDAOImpl;
import us.mn.state.health.lims.testanalyte.valueholder.TestAnalyte;
import us.mn.state.health.lims.typeoftestresult.valueholder.TypeOfTestResult.ResultType;

import java.util.List;

public class ResultUtil {
	private static final DictionaryDAO dictionaryDAO = new DictionaryDAOImpl();
	private static final TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();
	
	public static String getStringValueOfResult( Result result){
		if( ResultType.isDictionaryType(result.getResultType())){
			return dictionaryDAO.getDictionaryById(result.getValue()).getLocalizedName();
		}else{
			return result.getValue();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static TestAnalyte getTestAnalyteForResult(Result result) {

		if (result.getTestResult() != null) {
			List<TestAnalyte> testAnalyteList = testAnalyteDAO.getAllTestAnalytesPerTest(result.getTestResult().getTest());

			if (testAnalyteList.size() > 0) {
				int distanceFromRoot = 0;

				Analysis parentAnalysis = result.getAnalysis().getParentAnalysis();

				while (parentAnalysis != null) {
					distanceFromRoot++;
					parentAnalysis = parentAnalysis.getParentAnalysis();
				}

				int index = Math.min(distanceFromRoot, testAnalyteList.size() - 1);

				return testAnalyteList.get(index);
			}
		}
		return null;
	}
	public static boolean areNotes(TestResultItem item) {
		return !GenericValidator.isBlankOrNull(item.getNote());
	}
	
	public static boolean isReferred(TestResultItem testResultItem) {
		return testResultItem.isShadowReferredOut();
	}
	
    public static boolean isRejected(TestResultItem testResultItem) {
        return testResultItem.isShadowRejected();
    }
	public static boolean areResults(TestResultItem item) {
		return !(GenericValidator.isBlankOrNull(item.getResultValue()) || 
				(ResultType.DICTIONARY.matches(item.getResultType()) && "0".equals(item.getResultValue()))) ||
				(ResultType.isMultiSelectVariant(item.getResultType()) && !GenericValidator.isBlankOrNull(item.getMultiSelectResultValues()));
	}

	public static boolean isForcedToAcceptance(TestResultItem item){
		return !GenericValidator.isBlankOrNull(item.getForceTechApproval());
	}
}
