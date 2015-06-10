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

package us.mn.state.health.lims.test.action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.DynaValidatorForm;
import org.hibernate.Transaction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.services.AnalysisService;
import us.mn.state.health.lims.common.services.StatusService;
import us.mn.state.health.lims.common.services.TestService;
import us.mn.state.health.lims.common.util.IdValuePair;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.test.valueholder.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


public class BatchTestReassignmentUpdateAction extends BaseAction {
    private AnalysisDAO analysisDAO = new AnalysisDAOImpl();
    @Override
    protected ActionForward performAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String currentUserId = getSysUserId(request);
        DynaValidatorForm dynaForm = (DynaValidatorForm)form;
        //dynaForm.initialize(mapping);
        //PropertyUtils.setProperty(dynaForm, "sampleList", DisplayListService.getList(DisplayListService.ListType.SAMPLE_TYPE_ACTIVE));
        String jsonString = dynaForm.getString("jsonWad");
        System.out.println(jsonString);

        List<Analysis> newAnalysis = new ArrayList<Analysis>();
        List<Analysis> cancelAnalysis = new ArrayList<Analysis>();
        manageAnalysis(jsonString, cancelAnalysis, newAnalysis);

        String cancelStatus = StatusService.getInstance().getStatusID(StatusService.AnalysisStatus.Canceled);

        Transaction tx = HibernateUtil.getSession().beginTransaction();
        try{
            for( Analysis analysis : cancelAnalysis){
                analysis.setStatusId(cancelStatus);
                analysis.setSysUserId(currentUserId);
                analysisDAO.updateData(analysis);
            }

            for( Analysis analysis: newAnalysis){
                analysis.setSysUserId(currentUserId);
                analysisDAO.insertData(analysis, false);
            }

            tx.commit();
        }catch (LIMSRuntimeException e){
            tx.rollback();
        }

        return mapping.findForward("success");
    }

    private void manageAnalysis(String jsonString, List<Analysis> cancelAnalysis, List<Analysis> newAnalysis) {
        JSONParser parser=new JSONParser();

        try {
            JSONObject obj = (JSONObject)parser.parse(jsonString);
            List<Test> newTests = getNewTestsFromJson(obj, parser);
            List<Analysis> changedNotStarted = getAnalysisFromJson((String)obj.get("changeNotStarted"), parser);
            List<Analysis> noChangedNotStarted = getAnalysisFromJson((String)obj.get("noChangeNotStarted"), parser);
            List<Analysis> changeTechReject = getAnalysisFromJson((String)obj.get("changeTechReject"), parser);
            List<Analysis> noChangeTechReject = getAnalysisFromJson((String)obj.get("noChangeTechReject"), parser);
            List<Analysis> changeBioReject = getAnalysisFromJson((String)obj.get("changeBioReject"), parser);
            List<Analysis> noChangeBioReject = getAnalysisFromJson((String)obj.get("noChangeBioReject"), parser);
            List<Analysis> changeNotValidated = getAnalysisFromJson((String)obj.get("changeNotValidated"), parser);
            List<Analysis> noChangeNotValidated = getAnalysisFromJson((String)obj.get("noChangeNotValidated"), parser);

            Map<StatusService.AnalysisStatus, List<IdValuePair>> statusChangedMap = getAnalysisStatusListtMap();
            verifyStatusNotChanged(changedNotStarted, noChangedNotStarted, StatusService.AnalysisStatus.NotStarted, statusChangedMap);
            verifyStatusNotChanged(changeNotValidated, noChangeNotValidated, StatusService.AnalysisStatus.TechnicalAcceptance, statusChangedMap);
            verifyStatusNotChanged(changeTechReject, noChangeTechReject, StatusService.AnalysisStatus.TechnicalRejected, statusChangedMap);
            verifyStatusNotChanged(changeBioReject, noChangeBioReject, StatusService.AnalysisStatus.BiologistRejected, statusChangedMap);

            cancelAnalysis.addAll(changedNotStarted);
            cancelAnalysis.addAll(changeBioReject);
            cancelAnalysis.addAll(changeNotValidated);
            cancelAnalysis.addAll(changeTechReject);

            if( !newTests.isEmpty()){
                newAnalysis.addAll(createNewAnalysis( newTests, changedNotStarted));
                newAnalysis.addAll(createNewAnalysis( newTests, changeBioReject));
                newAnalysis.addAll(createNewAnalysis( newTests, changeNotValidated));
                newAnalysis.addAll(createNewAnalysis( newTests, changeTechReject));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    private List<Analysis> createNewAnalysis(List<Test> newTests, List<Analysis> changeAnalysis) {
        List<Analysis> newAnalysis = new ArrayList<Analysis>();
        for( Test test:newTests){
            for(Analysis analysis: changeAnalysis){
                newAnalysis.add(AnalysisService.buildAnalysis(test, analysis.getSampleItem()));
            }
        }

        return newAnalysis;
    }

    private void verifyStatusNotChanged(List<Analysis> changed, List<Analysis> noChanged, StatusService.AnalysisStatus status, Map<StatusService.AnalysisStatus, List<IdValuePair>> statusChangedMap) {
        String statusId = StatusService.getInstance().getStatusID(status);

        List<Analysis> changedAnalysis = new ArrayList<Analysis>();

        for(Analysis analysis: changed){
            if( !statusId.equals(analysis.getStatusId())){
                changedAnalysis.add(analysis);
            }
        }

        if(!changedAnalysis.isEmpty()){
            changed.removeAll(changedAnalysis);
        }

        for(Analysis analysis: noChanged){
            if( !statusId.equals(analysis.getStatusId())){
                changedAnalysis.add(analysis);
            }
        }

        if(!changedAnalysis.isEmpty()){
            List<IdValuePair> pairList = statusChangedMap.get(status);
            for( Analysis analysis: changedAnalysis){
                pairList.add(new IdValuePair(analysis.getId(), analysis.getSampleItem().getSample().getAccessionNumber()));
            }
        }
    }

    private HashMap<StatusService.AnalysisStatus, List<IdValuePair>> getAnalysisStatusListtMap() {
        HashMap<StatusService.AnalysisStatus, List<IdValuePair>> map = new HashMap<StatusService.AnalysisStatus, List<IdValuePair>>();
        map.put(StatusService.AnalysisStatus.NotStarted, new ArrayList<IdValuePair>());
        map.put(StatusService.AnalysisStatus.TechnicalRejected, new ArrayList<IdValuePair>());
        map.put(StatusService.AnalysisStatus.BiologistRejected, new ArrayList<IdValuePair>());
        map.put(StatusService.AnalysisStatus.TechnicalAcceptance, new ArrayList<IdValuePair>());
        return map;
    }


    private List<Test> getNewTestsFromJson(JSONObject obj, JSONParser parser) {
        List<Test> replacementTestList = new ArrayList<Test>();

        String replacementTests = (String)obj.get("replace");
        if( replacementTests == null){
            return replacementTestList;
        }

        JSONArray replacementTestArray;
        try {
            replacementTestArray = (JSONArray) parser.parse(replacementTests);
        } catch (ParseException e) {
            e.printStackTrace();
            return replacementTestList;
        }

        for(Object testIdObject : replacementTestArray){
            replacementTestList.add(new TestService((String)testIdObject).getTest());
        }

        return replacementTestList;
    }

    private List<Analysis> getAnalysisFromJson(String sampleIdList, JSONParser parser) {
        List<Analysis> analysisList = new ArrayList<Analysis>();

        if(sampleIdList == null){
            return analysisList;
        }

        JSONArray modifyAnalysisArray;
        try {
            modifyAnalysisArray = (JSONArray) parser.parse(sampleIdList);
        } catch (ParseException e) {
            e.printStackTrace();
            return analysisList;
        }

        for (Object analysisId : modifyAnalysisArray){
            analysisList.add(analysisDAO.getAnalysisById((String)analysisId));
        }

        return analysisList;
    }

    @Override
    protected String getPageTitleKey() {
        return "configuration.batch.test.reassignment";
    }

    @Override
    protected String getPageSubtitleKey() {
        return "configuration.batch.test.reassignment";
    }
}
