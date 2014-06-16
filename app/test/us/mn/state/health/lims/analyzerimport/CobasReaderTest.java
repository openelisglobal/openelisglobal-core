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
package us.mn.state.health.lims.analyzerimport;


import org.junit.*;
import us.mn.state.health.lims.analyzerimport.analyzerreaders.CobasReader;
import us.mn.state.health.lims.analyzerimport.util.AnalyzerTestNameCache;
import us.mn.state.health.lims.analyzerimport.util.MappedTestName;
import us.mn.state.health.lims.analyzerresults.dao.AnalyzerResultsDAO;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.ConfigurationProperties.Property;
import us.mn.state.health.lims.common.util.DAOImplFactory;
import us.mn.state.health.lims.common.util.HibernateProxy;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.test.dao.TestDAO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CobasReaderTest {

	private CobasReader reader;
	private List<String> testList;
	private static final String sysUserId = "1";
	private static final String FILE_HEADER = "0	2009-06-23 18:13:30	#ARC-FILE#	1.1a	2009-06-23	2009-06-23	COBAS INTEGRA400	39-5404	3.4.4.0809 (0809.4)	COUMBA";
	private static final String CONTROL = "40	2009-06-23 13:03:40	ALTL		PNU1 23/06/09	10	SER	U/L	 	  45	  0.00721	R	X";
	private static final String FULL_RESULT = "40	2009-06-23 13:04:26	ALTL		LART05136	11	SER	U/L	 	  19	  0.00248	R	X";
	private static final String NO_ACCESSION_RESULT = "40	2009-06-23 13:04:26	ALTL				11	SER	U/L	 	  19	  0.00248	R	X";
	private static final String NO_TEST_RESULT = "40	2009-06-23 13:04:26			LART05136	11	SER	U/L	 	  19	  0.00248	R	X";
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void buildBeforeClass() throws Exception {
		HibernateProxy.useTestImpl(true);
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DAOImplFactory.getInstance().revertAll();
		HibernateProxy.useTestImpl(false);
	}

	@Before
	public void setUp() throws Exception {
		ConfigurationProperties config = mock(ConfigurationProperties.class);
		ConfigurationProperties.setActiveConcreteInstance(config);
		config.setPropertyValue(Property.DEFAULT_DATE_LOCALE, "fr_FR");
		config.setPropertyValue(Property.DEFAULT_LANG_LOCALE, "fr_FR");
		
		DAOImplFactory factory = DAOImplFactory.getInstance();
		TestDAO testDAO = mock(TestDAO.class);
		factory.setTestDAOImpl(testDAO);
	
		when(testDAO.getActiveTestByName("Transaminases ASTL")).thenReturn(createTest("1"));
		when(testDAO.getActiveTestByName("Transaminases ALTL")).thenReturn(createTest("2"));
		when(testDAO.getActiveTestByName("Créatininémie")).thenReturn(createTest("3"));
		when(testDAO.getActiveTestByName("Glycémie")).thenReturn(createTest("4"));
	
		SampleDAO sampleDAO = mock(SampleDAO.class);
		factory.setSampleDAOImp(sampleDAO);
		
		when( sampleDAO.getSampleByAccessionNumber(anyString())).thenReturn((Sample)null);
		
		AnalyzerTestNameCache testNameCache = mock(AnalyzerTestNameCache.class);
		AnalyzerTestNameCache.setTestInstance(testNameCache);
		when(testNameCache.getMappedTest(AnalyzerTestNameCache.COBAS_INTEGRA400_NAME, "ASTL")).thenReturn(createMappedTestName("1"));
		when(testNameCache.getMappedTest(AnalyzerTestNameCache.COBAS_INTEGRA400_NAME, "ALTL")).thenReturn(createMappedTestName("2"));
		when(testNameCache.getEmptyMappedTestName(eq(AnalyzerTestNameCache.COBAS_INTEGRA400_NAME), anyString())).thenReturn(createMappedTestName("3"));
		
		AnalyzerResultsDAO analyzerResultDAO = mock( AnalyzerResultsDAO.class);
		CobasReader.setAnalyzerResultDAO(analyzerResultDAO);
		reader = new CobasReader();
		testList = new ArrayList<String>();
//		reader.insertTestLines(testList);

	}

	private List<us.mn.state.health.lims.test.valueholder.Test> createTest( String id) {
		us.mn.state.health.lims.test.valueholder.Test test = new us.mn.state.health.lims.test.valueholder.Test();
		test.setId(id);
		List<us.mn.state.health.lims.test.valueholder.Test> tests = new ArrayList<us.mn.state.health.lims.test.valueholder.Test>();
		tests.add(test);
		return tests;
	}

	@After
	public void tearDown() throws Exception {
		CobasReader.setAnalyzerResultDAO(null);
	}

	@Test
	public void testEmptyFile() {
		reader.insert((List<String>)null, sysUserId);
	}

	@Test
	public void testHeaderOnly() {
		//expected not to throw an exception
		testList.add(FILE_HEADER);
		reader.insert(testList, sysUserId);
	}

	@Test
	public void testControl() {
		//expected not to throw an exception
		testList.add(FILE_HEADER);
		testList.add(CONTROL);

		reader.insert(testList, sysUserId);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleMatch() {
		testList.add(FILE_HEADER);
		testList.add(FULL_RESULT);

		Object[] response = new Object[6];
		response[0] = "774324";
		response[1] = "test";
		response[2] = new BigDecimal(2);
		response[3] = new BigDecimal(3);
		response[4] = new BigDecimal(4);
		response[5] = "5";

		List<Object> responseList = new ArrayList<Object>();
		responseList.add(response);
		HibernateProxy.TestQuery.setQueryListResponse(responseList);
//		ANALYZER_RESULT_IMP.testStatus( 0, AnalyzerResults.NOT_REVIEWED );
//		ANALYZER_RESULT_IMP.testIsNotControl( 0 );

		reader.insert(testList, sysUserId);
	}

	@Test
	public void testMissingAccessionNumber() {
		testList.add(FILE_HEADER);
		testList.add(NO_ACCESSION_RESULT);

//		ANALYZER_RESULT_IMP.testStatus( 0, AnalyzerResults.MATCHING_ACCESSION_NOT_FOUND );
//		ANALYZER_RESULT_IMP.testIsNotControl( 0 );

//		reader.insertAnalyzerData(sysUserId);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMissingTest() {
		testList.add(FILE_HEADER);
		testList.add(NO_TEST_RESULT);

		List responseList = new ArrayList();
		responseList.add(new Object());
		HibernateProxy.TestQuery.setQueryListResponse(responseList);
//		ANALYZER_RESULT_IMP.testStatus( 0, AnalyzerResults.TEST_MAPPING_NOT_FOUND );
//		ANALYZER_RESULT_IMP.testIsNotControl( 0 );

//		reader.insertAnalyzerData(sysUserId);
	}

	@Test
	public void testNoAccessionMatch() {
		testList.add(FILE_HEADER);
		testList.add(FULL_RESULT);

//		ANALYZER_RESULT_IMP.testStatus( 0, AnalyzerResults.MATCHING_ACCESSION_NOT_FOUND );
//		ANALYZER_RESULT_IMP.testIsNotControl( 0 );

//		reader.insertAnalyzerData(sysUserId);
	}

	private MappedTestName createMappedTestName(String id) {
		MappedTestName mapped = new MappedTestName();
		mapped.setAnalyzerId(id);
		mapped.setTestId(id);
		mapped.setAnalyzerTestName("analyzer" + id);
		mapped.setOpenElisTestName("test" + id);
		return mapped;
	}

}
