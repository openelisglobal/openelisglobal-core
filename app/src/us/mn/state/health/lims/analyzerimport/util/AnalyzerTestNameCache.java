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
package us.mn.state.health.lims.analyzerimport.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import us.mn.state.health.lims.analyzer.dao.AnalyzerDAO;
import us.mn.state.health.lims.analyzer.daoimpl.AnalyzerDAOImpl;
import us.mn.state.health.lims.analyzer.valueholder.Analyzer;
import us.mn.state.health.lims.analyzerimport.dao.AnalyzerTestMappingDAO;
import us.mn.state.health.lims.analyzerimport.daoimpl.AnalyzerTestMappingDAOImpl;
import us.mn.state.health.lims.analyzerimport.valueholder.AnalyzerTestMapping;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;

public class AnalyzerTestNameCache {

	private static AnalyzerTestNameCache instance;
	
	private static final String COBAS_INTEGRA400_NAME = "Cobas Integra";
	private static final String SYSMEX_XT2000_NAME = "Sysmex XT 2000";
	private static final String FACSCALIBUR = "Facscalibur";
	private static final String FACSCANTO = "FacsCanto";
	private static final String EVOLIS = "Evolis";
	private static final String COBAS_TAQMAN = "Cobas Taqman";
	private static final String COBAS_DBS = "CobasDBS";
	private static final String COBAS_C311 = "Cobas C311";

	public static enum AnalyzerType {
		COBAS_INTEGRA400,
		SYSMEX_XT_2000,
		FACSCALIBUR,
		EVOLIS,
		COBAS_TAQMAN,
		FACSCANTO,
		COBAS_DBS,
		COBAS_C311
	}

	private static Map<String, MappedTestName> CobasIntegraTestNameMap;
	private static Map<String, MappedTestName> CobasTaqmanTestNameMap;
	private static Map<String, MappedTestName> SysmexXT2000TestNameMap;
	private static Map<String, MappedTestName> FacscaliburTestNameMap;
	private static Map<String, MappedTestName> EvolisTestNameMap;
	private static Map<String, MappedTestName> FacscantoTestNameMap;
	private static Map<String, MappedTestName> CobasDBStoTestNameMap;
	private static Map<String, MappedTestName> CobasC311toTestNameMap;

	private static Map<String, String> analyzerNameToIdMap;
	private static Map<AnalyzerType, String> analyzerIDMap;
	private static boolean isMapped = false;

	public static AnalyzerTestNameCache instance(){
		if( instance == null){
			instance = new AnalyzerTestNameCache();
		}
		
		return instance;
	}
	
	public MappedTestName getMappedTest( AnalyzerType analyzerType, String analyzerTestName){
		insureMapsLoaded();

		switch (analyzerType){
		case COBAS_INTEGRA400:
			return CobasIntegraTestNameMap.get(analyzerTestName);
		case SYSMEX_XT_2000:
			return SysmexXT2000TestNameMap.get(analyzerTestName);
		case FACSCALIBUR:
			return FacscaliburTestNameMap.get(analyzerTestName);
		case EVOLIS:
			return EvolisTestNameMap.get(analyzerTestName);
		case COBAS_TAQMAN:
			return CobasTaqmanTestNameMap.get(analyzerTestName);
		case FACSCANTO:
			return FacscantoTestNameMap.get(analyzerTestName);
		case COBAS_DBS:
			return CobasDBStoTestNameMap.get(analyzerTestName);	
		case COBAS_C311:
			return CobasC311toTestNameMap.get(analyzerTestName);		
		default:
			return null;
		}
	}

	private synchronized void insureMapsLoaded() {
		if( !isMapped){
			loadMaps();
			isMapped = true;
		}
	}

	public Map<String, MappedTestName> getMappedTestsForAnalyzer( AnalyzerType analyzerType){
		insureMapsLoaded();

		switch (analyzerType){
		case COBAS_INTEGRA400:
			return CobasIntegraTestNameMap;
		case SYSMEX_XT_2000:
			return SysmexXT2000TestNameMap;
		case FACSCALIBUR:
			return FacscaliburTestNameMap;
		case EVOLIS:
			return EvolisTestNameMap;
		case COBAS_TAQMAN:
			return CobasTaqmanTestNameMap;
		case FACSCANTO:
			return FacscantoTestNameMap;
		case COBAS_DBS:
			return CobasDBStoTestNameMap;	
		case COBAS_C311:
			return CobasC311toTestNameMap;		
		default:
			return null;
		}
	}

	public synchronized void reloadCache(){
		isMapped = false;
	}

	private void loadMaps() {
		AnalyzerDAO analyzerDAO = new AnalyzerDAOImpl();
		List<Analyzer> analyzerList = analyzerDAO.getAllAnalyzers();
		Map<String, Map<String,MappedTestName>> analyzerMapList = new HashMap<String, Map<String,MappedTestName>>();

		CobasIntegraTestNameMap = new HashMap<String,MappedTestName>();
		SysmexXT2000TestNameMap = new HashMap<String,MappedTestName>();
		FacscaliburTestNameMap = new HashMap<String,MappedTestName>();
		EvolisTestNameMap = new HashMap<String,MappedTestName>();
		CobasTaqmanTestNameMap = new HashMap<String, MappedTestName>();
		FacscantoTestNameMap = new HashMap<String, MappedTestName>();
		CobasDBStoTestNameMap = new HashMap<String, MappedTestName>();
		CobasC311toTestNameMap = new HashMap<String, MappedTestName>();
		
		analyzerIDMap = new HashMap<AnalyzerType, String>();
		analyzerNameToIdMap = new HashMap<String, String>();

		for( Analyzer analyzer : analyzerList){
			analyzerNameToIdMap.put(analyzer.getName(), analyzer.getId());

			if( COBAS_INTEGRA400_NAME.equals( analyzer.getName())){
				analyzerMapList.put(analyzer.getId(), CobasIntegraTestNameMap );
				analyzerIDMap.put(AnalyzerType.COBAS_INTEGRA400, analyzer.getId());
			}else if( SYSMEX_XT2000_NAME.equals(analyzer.getName())){
				analyzerMapList.put(analyzer.getId(), SysmexXT2000TestNameMap);
				analyzerIDMap.put(AnalyzerType.SYSMEX_XT_2000, analyzer.getId());
			}else if( FACSCALIBUR.equals(analyzer.getName())){
				analyzerMapList.put(analyzer.getId(), FacscaliburTestNameMap);
				analyzerIDMap.put(AnalyzerType.FACSCALIBUR, analyzer.getId());
			}else if(EVOLIS.equals(analyzer.getName())){
				analyzerMapList.put(analyzer.getId(), EvolisTestNameMap);
				analyzerIDMap.put(AnalyzerType.EVOLIS, analyzer.getId());
			}else if(COBAS_TAQMAN.equals(analyzer.getName())){
				analyzerMapList.put(analyzer.getId(), CobasTaqmanTestNameMap);
				analyzerIDMap.put(AnalyzerType.COBAS_TAQMAN, analyzer.getId());
			}else if( FACSCANTO.equals(analyzer.getName())){
				analyzerMapList.put(analyzer.getId(), FacscantoTestNameMap);
				analyzerIDMap.put(AnalyzerType.FACSCANTO, analyzer.getId());
			}else if( COBAS_DBS.equals(analyzer.getName())){
				analyzerMapList.put(analyzer.getId(), CobasDBStoTestNameMap);
				analyzerIDMap.put(AnalyzerType.COBAS_DBS, analyzer.getId());
			}else if( COBAS_C311.equals(analyzer.getName())){
				analyzerMapList.put(analyzer.getId(), CobasC311toTestNameMap);
				analyzerIDMap.put(AnalyzerType.COBAS_C311, analyzer.getId());
			}
		}

		AnalyzerTestMappingDAO analyzerTestMappingDAO = new AnalyzerTestMappingDAOImpl();
		List<AnalyzerTestMapping> mappingList = analyzerTestMappingDAO.getAllAnalyzerTestMappings();

		TestDAO testDAO = new TestDAOImpl();
		for( AnalyzerTestMapping mapping : mappingList){
			MappedTestName mappedTestName = createMappedTestName( testDAO, mapping );

			Map<String,MappedTestName> testMap = analyzerMapList.get(mapping.getAnalyzerId());
			if( testMap != null){
				testMap.put(mapping.getAnalyzerTestName(), mappedTestName);
			}
		}

	}

	private MappedTestName createMappedTestName(TestDAO testDAO, AnalyzerTestMapping mapping) {
		Test test = new Test();
		test.setId(mapping.getTestId());
		testDAO.getData(test);


		MappedTestName mappedTest = new MappedTestName();
		mappedTest.setAnalyzerTestName(mapping.getAnalyzerTestName());
		mappedTest.setTestId(mapping.getTestId());
		mappedTest.setOpenElisTestName(test.getLocalizedName());
		mappedTest.setAnalyzerId(mapping.getAnalyzerId());

		return mappedTest;
	}

	public MappedTestName getEmptyMappedTestName(AnalyzerType analyzerType, String analyzerTestName) {
		MappedTestName mappedTest = new MappedTestName();
		mappedTest.setAnalyzerTestName(analyzerTestName);
		mappedTest.setTestId(null);
		mappedTest.setOpenElisTestName(analyzerTestName);
		mappedTest.setAnalyzerId(analyzerIDMap.get(analyzerType));

		return mappedTest;
	}

	public String getAnalyzerId(AnalyzerType analyzerType) {
		insureMapsLoaded();

		return analyzerIDMap.get(analyzerType);
	}

	public String getAnalyzerIdForName(String analyzerName) {
		insureMapsLoaded();

		return analyzerNameToIdMap.get(analyzerName);
	}
	
	public static void setTestInstance( AnalyzerTestNameCache cache){
		instance = cache;
	}
}
