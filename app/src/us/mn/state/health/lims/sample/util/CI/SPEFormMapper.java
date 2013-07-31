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

import java.util.ArrayList;
import java.util.List;

import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.test.valueholder.Test;

public class SPEFormMapper extends ARVFormMapper implements IProjectFormMapper {

	private final String projectCode = "LSPE";

	public SPEFormMapper(String projectFormId, BaseActionForm dynaForm) {
		super(projectFormId, dynaForm);
	}

	public String getProjectCode() {
		return this.projectCode;
	}

	public String getOrganizationId() {
		// no organization id for Special Request
		return null;
	}

	@Override
	public List<Test> getDryTubeTests(BaseActionForm dynaForm) {
		List<Test> testList = new ArrayList<Test>();

		if (projectData.getSerologyHIVTest()) {
			testList.add(createTest("Murex"));
			testList.add(createTest("Integral"));
		}

		if (projectData.getMurexTest()) {
			testList.add(createTest("Murex"));
		}
		if (projectData.getIntegralTest()) {
			testList.add(createTest("Integral"));
		}
		if (projectData.getVironostikaTest()) {
			testList.add(createTest("Vironostika"));
		}
		if (projectData.getGenieIITest()) {
			testList.add(createTest("Genie II"));
		}
		if (projectData.getGenieII100Test()) {
			testList.add(createTest("Genie II 100"));
		}
		if (projectData.getGenieII10Test()) {
			testList.add(createTest("Genie II 10"));
		}
		if (projectData.getWB1Test()) {
			testList.add(createTest("Western Blot 1"));
		}
		if (projectData.getWB2Test()) {
			testList.add(createTest("Western Blot 2"));
		}
		if (projectData.getP24AgTest()) {
			testList.add(createTest("p24 Ag"));
		}
		if (projectData.getCreatinineTest()) {
			testList.add(createTest("Créatininémie"));
		}
		if (projectData.getGlycemiaTest()) {
			testList.add(createTest("Glycémie"));
		}

		if (projectData.getTransaminaseTest()) {
			testList.add(createTest("Transaminases ALTL"));
			testList.add(createTest("Transaminases ASTL"));
		} else {
			if (projectData.getTransaminaseALTLTest()) {
				testList.add(createTest("Transaminases ALTL"));
			}
			if (projectData.getTransaminaseASTLTest()) {
				testList.add(createTest("Transaminases ASTL"));
			}
		}
		return testList;
	}

	@Override
	public List<Test> getEDTATubeTests(BaseActionForm dynaForm) {
		List<Test> testList = new ArrayList<Test>();

		if (projectData.getNfsTest()) {
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

		if (projectData.getGbTest()) {
			testList.add(createTest("GB"));
		}
		if (projectData.getNeutTest()) {
			testList.add(createTest("Neut %"));
		}
		if (projectData.getLymphTest()) {
			testList.add(createTest("Lymph %"));
		}
		if (projectData.getMonoTest()) {
			testList.add(createTest("Mono %"));
		}
		if (projectData.getEoTest()) {
			testList.add(createTest("Eo %"));
		}
		if (projectData.getBasoTest()) {
			testList.add(createTest("Baso %"));
		}
		if (projectData.getGrTest()) {
			testList.add(createTest("GR"));
		}
		if (projectData.getHbTest()) {
			testList.add(createTest("Hb"));
		}
		if (projectData.getHctTest()) {
			testList.add(createTest("HCT"));
		}
		if (projectData.getVgmTest()) {
			testList.add(createTest("VGM"));
		}
		if (projectData.getTcmhTest()) {
			testList.add(createTest("TCMH"));
		}
		if (projectData.getCcmhTest()) {
			testList.add(createTest("CCMH"));
		}
		if (projectData.getPlqTest()) {
			testList.add(createTest("PLQ"));
		}
		if (projectData.getCd4cd8Test()) {
			testList.add(createTest("CD3 percentage count"));
			testList.add(createTest("CD4 percentage count"));
		}
		if (projectData.getCd3CountTest()) {
			testList.add(createTest("CD3 percentage count"));
		}
		if (projectData.getCd4CountTest()) {
			testList.add(createTest("CD4 percentage count"));
		}

		if (projectData.getViralLoadTest()) {
			testList.add(createTest("Viral Load"));
		}
		if (projectData.getGenotypingTest()) {
			testList.add(createTest("Génotypage"));
		}

		return testList;
	}

}
