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

package us.mn.state.health.lims.testconfiguration.action;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.DynaValidatorForm;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.services.LocalizationService;
import us.mn.state.health.lims.common.services.TestService;
import us.mn.state.health.lims.panel.valueholder.Panel;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.testconfiguration.beans.TestCatalogBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TestCatalogAction extends BaseAction {
    @Override
    protected ActionForward performAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DynaValidatorForm dynaForm = (DynaValidatorForm)form;
        PropertyUtils.setProperty(dynaForm,"testList", createTestList());



        return mapping.findForward(FWD_SUCCESS);
    }

    private List<TestCatalogBean> createTestList() {
        List<TestCatalogBean> beanList = new ArrayList<TestCatalogBean>();

        List<Test> testList = new TestDAOImpl().getAllTests(false);

        for( Test test : testList){
            TestCatalogBean bean = new TestCatalogBean();
            TestService testService = new TestService(test);
            bean.setEnglishName(TestService.getUserLocalizedTestName(test));
            bean.setFrenchName(TestService.getUserLocalizedTestName(test));
            bean.setEnglishReportName(TestService.getUserLocalizedReportingTestName(test));
            bean.setFrenchReportName(TestService.getUserLocalizedReportingTestName(test));

            bean.setTestUnit(testService.getTestSectionName());
            bean.setPanel(creatPanelList(testService));
            bean.setResultType(testService.getResultType());
            bean.setSampleType(testService.getTypeOfSample().getLocalizedName());
            bean.setOrderable( test.getOrderable() ? "Orderable" : "Not orderable");
            bean.setActive(test.isActive() ? "Active" : "Not active");
            beanList.add(bean);
        }

        Collections.sort(beanList, new Comparator<TestCatalogBean>() {
            @Override
            public int compare(TestCatalogBean o1, TestCatalogBean o2) {
                return o1.getTestUnit().compareTo(o2.getTestUnit());
            }
        });

        return beanList;
    }

    private String creatPanelList(TestService testService) {
        StringBuilder builder = new StringBuilder();

        List<Panel> panelList = testService.getPanels();
        for(Panel panel : panelList){
            builder.append(LocalizationService.getLocalizedValueById(panel.getLocalization().getId()));
            builder.append(", ");
        }

        String panelString = builder.toString();
        if( panelString.isEmpty()){
            panelString = "None";
        }else{
            panelString = panelString.substring(0, panelString.length() - 3 );
        }

        return panelString;
    }


    @Override
    protected String getPageTitleKey() {
        return null;
    }

    @Override
    protected String getPageSubtitleKey() {
        return null;
    }
}
