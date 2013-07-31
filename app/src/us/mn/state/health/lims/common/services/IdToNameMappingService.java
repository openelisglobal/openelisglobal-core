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
package us.mn.state.health.lims.common.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;

public class IdToNameMappingService {
	private static Map<Entity, Map<String, String>> entityToMap;
	
	public enum Entity{
		TEST_NAME,
		TEST_DESCRIPTION
	}
	
	static{
		entityToMap = new HashMap<Entity, Map<String, String>>();
		entityToMap.put(Entity.TEST_NAME, createTestIdToNameMap());
		entityToMap.put(Entity.TEST_DESCRIPTION, createTestIdToDescriptionMap());
	}
	public static Map<String, String> getMap( Entity entiy){
		return entityToMap.get(entiy);
	}
	
	private static Map<String, String> createTestIdToNameMap() {
		Map<String, String> testIdToNameMap = new HashMap<String, String>();

		List<Test> tests = new TestDAOImpl().getAllActiveTests(false);

		for (Test test : tests) {
			testIdToNameMap.put(test.getId(), test.getName().replace("\n", " "));
		}
		
		return testIdToNameMap;
	}

	private static Map<String, String> createTestIdToDescriptionMap() {
		Map<String, String> testIdToNameMap = new HashMap<String, String>();

		List<Test> tests = new TestDAOImpl().getAllActiveTests(false);

		for (Test test : tests) {
			testIdToNameMap.put(test.getId(), test.getDescription().replace("\n", " "));
		}
		
		return testIdToNameMap;
	}
}
