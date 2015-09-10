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
import us.mn.state.health.lims.common.services.DisplayListService;
import us.mn.state.health.lims.common.services.TestService;
import us.mn.state.health.lims.common.util.IdValuePair;
import us.mn.state.health.lims.panelitem.dao.PanelItemDAO;
import us.mn.state.health.lims.panelitem.daoimpl.PanelItemDAOImpl;
import us.mn.state.health.lims.panelitem.valueholder.PanelItem;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.test.valueholder.TestComparator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class PanelTestAssignAction extends BaseAction {
    @Override  
    protected ActionForward performAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ((DynaValidatorForm)form).initialize(mapping);
        List<IdValuePair> panels = DisplayListService.getListWithLeadingBlank(DisplayListService.ListType.PANELS);
        LinkedHashMap<IdValuePair, List<IdValuePair>> panelTestsMap = new LinkedHashMap<IdValuePair, List<IdValuePair>>(panels.size());

        for( IdValuePair panelPair : panels){
            List<IdValuePair> tests = new ArrayList<IdValuePair>();
            panelTestsMap.put(panelPair, tests );
            List<Test> testList = getAllTestsByPanelId(panelPair.getId());

            for( Test test : testList){
                if( test.isActive()) {
                    tests.add(new IdValuePair(test.getId(), TestService.getLocalizedTestNameWithType(test)));
                }
            }
        }
        
        //we can't just append the original list because that list is in the cache
        List<IdValuePair> joinedList = new ArrayList<IdValuePair>(panels);
        joinedList.addAll(DisplayListService.getList(DisplayListService.ListType.PANELS_INACTIVE));
        PropertyUtils.setProperty(form, "panelList", joinedList);
        PropertyUtils.setProperty(form, "panelTestList", panelTestsMap);

        return mapping.findForward(FWD_SUCCESS);
    }

    public static List<Test> getAllTestsByPanelId(String panelId){
        List<Test> testList = new ArrayList<Test>();
        PanelItemDAO panelItemDAO = new PanelItemDAOImpl();

        @SuppressWarnings("unchecked")
		List<PanelItem> testLinks = panelItemDAO.getPanelItemsForPanel(panelId);

        for (PanelItem link : testLinks) {
            testList.add(link.getTest());
        }

        Collections.sort(testList, TestComparator.NAME_COMPARATOR);
        return testList;
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
