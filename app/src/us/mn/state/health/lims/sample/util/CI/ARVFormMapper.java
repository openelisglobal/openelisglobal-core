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
package us.mn.state.health.lims.sample.util.CI;

import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.test.valueholder.Test;

import java.util.ArrayList;
import java.util.List;


public class ARVFormMapper extends BaseProjectFormMapper implements IProjectFormMapper {
		
	private final String projectCode = "LART";

    public ARVFormMapper(String projectFormId, BaseActionForm dynaForm) {	
		super(projectFormId, dynaForm);
	}
		
	public String getProjectCode() {
		return this.projectCode;
	}
			
	public List<Test> getDryTubeTests(BaseActionForm dynaForm){
		List<Test> testList = new ArrayList<Test>();
				
		if (projectData.getSerologyHIVTest()){
			testList.add(createTest("Murex"));
			testList.add(createTest("Integral"));
		}	
		if (projectData.getCreatinineTest()){
			testList.add(createTest("Créatininémie"));
		}
		if (projectData.getGlycemiaTest()){
			testList.add(createTest("Glycémie"));
		}
		
		if (projectData.getTransaminaseTest()){
			testList.add(createTest("Transaminases ALTL")); 
			testList.add(createTest("Transaminases ASTL")); 
		}
		return testList;
	}
	
	public List<Test> getEDTATubeTests(BaseActionForm dynaForm){
		List<Test> testList = new ArrayList<Test>();
				
		if (projectData.getNfsTest()){
			testList.add(createTest("GB"));
			testList.add(createTest("Neut %"));
			testList.add(createTest("Lymph %"));
			testList.add(createTest("Mono %"));
			testList.add(createTest("Eo %"));
			testList.add(createTest("Baso %"));
			testList.add(createTest("GR"));
			testList.add(createTest("Hb"));
			testList.add(createTest("HCT"));
			testList.add(createTest("VGM"));
			testList.add(createTest("TCMH"));
			testList.add(createTest("CCMH"));
			testList.add(createTest("PLQ"));
		}
		if (projectData.getCd4cd8Test()){
			testList.add(createTest("CD3 percentage count"));
			testList.add(createTest("CD4 percentage count"));	
		}
		if (projectData.getViralLoadTest()){
		    testList.add(createTest("Viral Load"));
		}		
		if (projectData.getGenotypingTest()){
		    testList.add(createTest("Génotypage"));
		}
		
		return testList;
	}
		
	public ArrayList<TypeOfSampleTests> getTypeOfSampleTests(){
		ArrayList<TypeOfSampleTests> sItemTests = new ArrayList<TypeOfSampleTests>();
		List<Test> testList = new ArrayList<Test>();
		
		//Check for Dry Tube Tests
	    if ( projectData.getDryTubeTaken() ) { 
			testList = getDryTubeTests(dynaForm);	
			sItemTests.add( new TypeOfSampleTests(getTypeOfSample("Dry Tube"), testList));
	    }
		
		//Check for EDTA Tubes Tests
	    if ( projectData.getEdtaTubeTaken()) { 
			testList = new ArrayList<Test>();
			testList = getEDTATubeTests(dynaForm);		
			sItemTests.add( new TypeOfSampleTests(getTypeOfSample("EDTA Tube"), testList));
        }			
		
		if (projectData.getDbsTaken()) {
		    if (projectData.getDnaPCR()) {
                testList = new ArrayList<Test>();
                testList = getDBSTests(dynaForm);
                sItemTests.add( new TypeOfSampleTests(getTypeOfSample("DBS"), testList));
            }
        }		
		return sItemTests;
	}	

	   public List<Test> getDBSTests(BaseActionForm dynaForm){
	        List<Test> testList = new ArrayList<Test>();

	        if (projectData.getDnaPCR()){
	            testList.add(createTest("DNA PCR"));
	        }
	        return testList;
	    }

    /**
     * @see us.mn.state.health.lims.sample.util.CI.BaseProjectFormMapper#getSampleCenterCode()
     */
    @Override
    public String getSampleCenterCode() {
        return projectData.getARVcenterCode();
    }
}
