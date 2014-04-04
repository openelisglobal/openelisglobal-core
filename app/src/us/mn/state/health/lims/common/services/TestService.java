/*
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
 */

package us.mn.state.health.lims.common.services;

import us.mn.state.health.lims.test.beanItems.TestResultItem;
import us.mn.state.health.lims.test.beanItems.TestResultItem.ResultDisplayType;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;
import us.mn.state.health.lims.typeoftestresult.valueholder.TypeOfTestResult;

import java.util.List;

/**
 */
public class TestService{

    public static final String HIV_TYPE = "HIV_TEST_KIT";
    public static final String SYPHILIS_TYPE = "SYPHILIS_TEST_KIT";
    private static final TestResultDAO testResultDAO = new TestResultDAOImpl();
    private static final TestDAO testDAO = new TestDAOImpl();
    private final Test test;

    public TestService(Test test){
        this.test = test;
    }

    public Test getTest(){
        return test;
    }

    public String getTestMethodName(){
        return test.getMethod() != null ? test.getMethod().getMethodName() : null;
    }

    @SuppressWarnings("unchecked")
    public List<TestResult> getPossibleTestResults( ) {
        return testResultDAO.getAllTestResultsPerTest(test);
    }

    public String getUOM(boolean isCD4Conclusion){
        if (!isCD4Conclusion) {
            if (test != null && test.getUnitOfMeasure() != null) {
                return test.getUnitOfMeasure().getName();
            }
        }

        return "";
    }

    public boolean isReportable(){
        return "Y".equals(test.getIsReportable());
    }

    public String getSortOrder(){
        return test.getSortOrder();
    }

    public ResultDisplayType getDisplayTypeForTestMethod() {
        String methodName = getTestMethodName();

        if (HIV_TYPE.equals(methodName)) {
            return ResultDisplayType.HIV;
        } else if (SYPHILIS_TYPE.equals(methodName)) {
            return ResultDisplayType.SYPHILIS;
        }

        return TestResultItem.ResultDisplayType.TEXT;
    }

    public String getResultType(){
        String testResultType = TypeOfTestResult.ResultType.NUMERIC.getDBValue();
        List<TestResult> testResults = getPossibleTestResults();

        if (testResults != null && !testResults.isEmpty()) {
            testResultType = testResults.get(0).getTestResultType();
        }

        return testResultType;
    }

    public static List<Test> getAllActiveTests(){
        return testDAO.getAllActiveTests(false);
    }
}
