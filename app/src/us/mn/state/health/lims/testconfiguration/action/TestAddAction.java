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
import us.mn.state.health.lims.common.services.DisplayListService.ListType;
import us.mn.state.health.lims.common.services.DisplayListService;
import us.mn.state.health.lims.common.services.ResultLimitService;
import us.mn.state.health.lims.common.util.IdValuePair;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class TestAddAction extends BaseAction {
    @Override
    protected ActionForward performAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ((DynaValidatorForm)form).initialize(mapping);
        List<IdValuePair> allSampleTypesList = new ArrayList<IdValuePair>();
        allSampleTypesList.addAll(DisplayListService.getList(ListType.SAMPLE_TYPE_ACTIVE));
        allSampleTypesList.addAll(DisplayListService.getList(ListType.SAMPLE_TYPE_INACTIVE));
        PropertyUtils.setProperty(form, "sampleTypeList", allSampleTypesList);
        PropertyUtils.setProperty(form, "panelList", DisplayListService.getList(ListType.PANELS));
        PropertyUtils.setProperty(form, "resultTypeList", DisplayListService.getList(ListType.RESULT_TYPE_LOCALIZED));
        PropertyUtils.setProperty(form, "uomList", DisplayListService.getList(ListType.UNIT_OF_MEASURE));
        PropertyUtils.setProperty(form, "labUnitList", DisplayListService.getList(ListType.TEST_SECTION));
        PropertyUtils.setProperty(form, "ageRangeList", ResultLimitService.getPredefinedAgeRanges());

        return mapping.findForward(FWD_SUCCESS);
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
