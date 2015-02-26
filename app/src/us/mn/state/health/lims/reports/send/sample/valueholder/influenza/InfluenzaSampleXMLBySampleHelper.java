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
*/
package us.mn.state.health.lims.reports.send.sample.valueholder.influenza;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.analyte.valueholder.Analyte;
import us.mn.state.health.lims.citystatezip.dao.CityStateZipDAO;
import us.mn.state.health.lims.citystatezip.daoimpl.CityStateZipDAOImpl;
import us.mn.state.health.lims.citystatezip.valueholder.CityStateZip;
import us.mn.state.health.lims.common.services.TestService;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.note.dao.NoteDAO;
import us.mn.state.health.lims.note.daoimpl.NoteDAOImpl;
import us.mn.state.health.lims.note.valueholder.Note;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.person.dao.PersonDAO;
import us.mn.state.health.lims.person.daoimpl.PersonDAOImpl;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.provider.dao.ProviderDAO;
import us.mn.state.health.lims.provider.daoimpl.ProviderDAOImpl;
import us.mn.state.health.lims.referencetables.valueholder.ReferenceTables;
import us.mn.state.health.lims.reports.send.sample.dao.SampleTransmissionSequenceDAO;
import us.mn.state.health.lims.reports.send.sample.daoimpl.SampleTransmissionSequenceDAOImpl;
import us.mn.state.health.lims.reports.send.sample.valueholder.SampleTransmissionSequence;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.samplehuman.dao.SampleHumanDAO;
import us.mn.state.health.lims.samplehuman.daoimpl.SampleHumanDAOImpl;
import us.mn.state.health.lims.samplehuman.valueholder.SampleHuman;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import us.mn.state.health.lims.sampleorganization.dao.SampleOrganizationDAO;
import us.mn.state.health.lims.sampleorganization.daoimpl.SampleOrganizationDAOImpl;
import us.mn.state.health.lims.sampleorganization.valueholder.SampleOrganization;
import us.mn.state.health.lims.sourceofsample.dao.SourceOfSampleDAO;
import us.mn.state.health.lims.sourceofsample.daoimpl.SourceOfSampleDAOImpl;
import us.mn.state.health.lims.sourceofsample.valueholder.SourceOfSample;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.testanalyte.dao.TestAnalyteDAO;
import us.mn.state.health.lims.testanalyte.daoimpl.TestAnalyteDAOImpl;
import us.mn.state.health.lims.testanalyte.valueholder.TestAnalyte;
import us.mn.state.health.lims.testresult.valueholder.TestResult;
import us.mn.state.health.lims.testtrailer.valueholder.TestTrailer;
import us.mn.state.health.lims.typeofsample.dao.TypeOfSampleDAO;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleDAOImpl;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;

import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 * bugzilla 2437 added for batch xml
 */
public class InfluenzaSampleXMLBySampleHelper {
	
	private static final String HL7_CORRECTED_RESULT_STATUS_CODE = "C";
	private static final String HL7_FINAL_RESULT_STATUS_CODE = "F";
	private static final String HL7_ANALYTE_NAME_INTERPRETATION = "interpretation";
	private static final String HL7_ANALYTE_NAME_COMMENT = "comment";
	private static final String HL7_ANALYTE_NAME_FINAL_RESULT = "final result";
	private static final String HL7_RESULT_VALUE_TYPE = "CE";
	private static final String HL7_INFLUENZA_TEST_DESCRIPTION = "influenza";
	private static final String HL7_INFLUENZA_TEST_NAME = "5300";
	private static final String PERIOD = ".";
	private static final String PERIOD_SPACE = ". ";
	
	public MessageXmit getXMLMessage(Sample sample, Map testLevelCriteriaMap) throws Exception {
		
		String accessionNumber = sample.getAccessionNumber();
		
		NoteDAO noteDAO = new NoteDAOImpl();
		PatientDAO patientDAO = new PatientDAOImpl();
		PersonDAO personDAO = new PersonDAOImpl();
		ProviderDAO providerDAO = new ProviderDAOImpl();
		SampleDAO sampleDAO = new SampleDAOImpl();
		SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
		SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
		SampleOrganizationDAO sampleOrganizationDAO = new SampleOrganizationDAOImpl();
		AnalysisDAO analysisDAO = new AnalysisDAOImpl();
		ResultDAO resultDAO = new ResultDAOImpl();
		TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
		SourceOfSampleDAO sourceOfSampleDAO = new SourceOfSampleDAOImpl();
		CityStateZipDAO cityStateZipDAO = new CityStateZipDAOImpl();
		DictionaryDAO dictDAO = new DictionaryDAOImpl();
		TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();
		SampleTransmissionSequenceDAO sampleTransmissionSequenceDAO = new SampleTransmissionSequenceDAOImpl();
		
		// for overall message portion of message
		MessageXmit message = new MessageXmit();
		SampleTransmissionSequence sampleTransmissionSequence = new SampleTransmissionSequence();
		
		GregorianCalendar gc = new GregorianCalendar();
		Date now = gc.getTime();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String dateAsText = sdf.format(now);
		
		SampleXmit sampleXmit = new SampleXmit();
		PatientXmit patient = new PatientXmit();
		Person person = new Person();
		ProviderXmit provider = new ProviderXmit();
		Person providerPerson = new Person();
		SampleHuman sampleHuman = new SampleHuman();
		SampleOrganization sampleOrganization = new SampleOrganization();
		List analyses = null;
		SourceOfSample sourceOfSample = new SourceOfSample();
		TypeOfSample typeOfSample = new TypeOfSample();
		
		SampleItem sampleItem = new SampleItem();
		sample.setAccessionNumber(accessionNumber);
		sampleDAO.getSampleByAccessionNumber(sample);
		
		sampleHuman.setSampleId(sample.getId());
		sampleHumanDAO.getDataBySample(sampleHuman);
		sampleOrganization.setSample(sample);
		sampleOrganizationDAO.getDataBySample(sampleOrganization);
		//bugzilla 1773 need to store sample not sampleId for use in sorting
		sampleItem.setSample(sample);
		sampleItemDAO.getDataBySample(sampleItem);
		patient.setId(sampleHuman.getPatientId());
		patientDAO.getData(patient);
		person = patient.getPerson();
		personDAO.getData(person);
		
		provider.setId(sampleHuman.getProviderId());
		providerDAO.getData(provider);
		providerPerson = provider.getPerson();
		personDAO.getData(providerPerson);
		
		analyses = analysisDAO.getMaxRevisionAnalysesBySample(sampleItem);
		
		sampleItemDAO.getData(sampleItem);
		
		sourceOfSample.setId(sampleItem.getSourceOfSampleId());
		if (!StringUtil.isNullorNill(sourceOfSample.getId())) {
			sourceOfSampleDAO.getData(sourceOfSample);
		}
		typeOfSample.setId(sampleItem.getTypeOfSampleId());
		if (!StringUtil.isNullorNill(typeOfSample.getId())) {
			typeOfSampleDAO.getData(typeOfSample);
		}
		
		if (sampleItem != null) {
			//bugzilla 1773 need to store sample not sampleId for use in sorting
			String sampleId = sampleItem.getSample().getId();
			
			
			sampleXmit.setId(sampleId);
			sampleDAO.getData(sampleXmit);
			
		} 
		
		//the sample object in sampleXmit is not currently used in xml generation but has useful information
		sampleXmit.setSample(sample);
		
		if (typeOfSample != null
				&& !StringUtil
				.isNullorNill(typeOfSample
						.getDescription())) {
			sampleXmit.setTypeOfSample(typeOfSample.getDescription());
			sampleXmit.setTypeOfSampleCode(typeOfSample.getLocalAbbreviation());
		}
		if (sourceOfSample != null
				&& !StringUtil
				.isNullorNill(sourceOfSample
						.getDescription())) {
			sampleXmit.setSourceOfSample(sourceOfSample.getDescription() + " " + sampleItem.getSourceOther());
		}
		
		sampleHuman.setSampleId(sampleXmit.getId());
		sampleHumanDAO.getDataBySample(sampleHuman);
		sampleOrganization.setSample(sampleXmit);
		sampleOrganizationDAO
		.getDataBySample(sampleOrganization);
		//bugzilla 1827 set id = external id AFTER getting data
		patient.setId(sampleHuman.getPatientId());
		patientDAO.getData(patient);
		// per Nancy 01/12/2007
		// this should be external id (if none just patient
		// id because we can't send null
		if (!StringUtil.isNullorNill(patient
				.getExternalId())) {
			patient.setId(patient.getExternalId());
		} else {
			patient.setId(sampleHuman.getPatientId());
		}
		person.setId(patient.getPerson().getId());
		personDAO.getData(person);
		
		// do we need to set id on patient to be externalId?
		patient.setLastName(StringUtil.trim(person.getLastName()));
		patient.setFirstName(StringUtil.trim(person.getFirstName()));
		patient.setMiddleName(StringUtil.trim(person.getMiddleName()));
		patient.setStreetAddress(StringUtil.trim(person.getStreetAddress()));
		patient.setMultipleUnit(StringUtil.trim(person.getMultipleUnit()));
		patient.setCity(StringUtil.trim(person.getCity()));
		patient.setState(StringUtil.trim(person.getState()));
		patient.setZipCode(StringUtil.trim(person.getZipCode()));
		
		if (!StringUtil.isNullorNill(person.getState()) && !StringUtil.isNullorNill(person.getZipCode())) {
			CityStateZip csz = new CityStateZip();
			csz.setState(person.getState());
			csz.setZipCode(person.getZipCode());
			String countyCode = cityStateZipDAO.getCountyCodeByStateAndZipCode(csz);
			patient.setCounty(countyCode);
		}
		
		provider.setId(sampleHuman.getProviderId());
		providerDAO.getData(provider);
		providerPerson = provider.getPerson();
		personDAO.getData(providerPerson);
		provider
		.setWorkPhone(providerPerson.getWorkPhone());
		provider.setLastName(StringUtil.trim(providerPerson.getLastName()));
		provider
		.setFirstName(StringUtil.trim(providerPerson.getFirstName()));
		
		if (StringUtil.isNullorNill(sampleXmit
				.getRevision())) {
			sampleXmit.setRevision("0");
		}
		
		sampleXmit.setPatient(patient);
		sampleXmit.setProvider(provider);
		
		ArrayList sampleTests = new ArrayList();
		
		
		// assemble Test Elements
		for (int j = 0; j < analyses.size(); j++) {
			Analysis analysis = (Analysis) analyses.get(j);
			
			//only process influenza type samples else skip it
			if (!TestService.getLocalizedTestNameWithType( analysis.getTest() ).toLowerCase().startsWith(HL7_INFLUENZA_TEST_DESCRIPTION) && !TestService.getUserLocalizedTestName( analysis.getTest() ).equals( HL7_INFLUENZA_TEST_NAME ) ) {
				continue;
			}
			
			//analysis must be released else skip it
			if (!analysis.getStatus().equals(SystemConfiguration.getInstance().getAnalysisStatusReleased())) {
				continue;
			}
			
			//extract criteria from testLevelCriteriaMap 
			if (testLevelCriteriaMap != null) {
				Date fromReleasedDate = (Date)testLevelCriteriaMap.get("fromReleasedDate");
				Date toReleasedDate = (Date)testLevelCriteriaMap.get("toReleasedDate");
				
				if (analysis.getReleasedDate().before(fromReleasedDate) || analysis.getReleasedDate().after(toReleasedDate)) {
					continue;
				}
			}
			
			Test t = analysis.getTest();
			
			TestXmit sampleTest = null;
			TestNameXmit testName = null;
			
			//this returns analytes in correct sort order
			List testAnalytes = testAnalyteDAO
			.getAllTestAnalytesPerTest(t);
			ArrayList testResults = new ArrayList();
			StringBuffer comment = new StringBuffer();
			StringBuffer interpretationComment = new StringBuffer();
			StringBuffer externalNotesComment = new StringBuffer();
			StringBuffer testTrailerTextComment = new StringBuffer();
			ResultXmit resultXmit = null;
			String analyteTestCode = null;
			String analyteTestDescription = null;
			
			//determine here whether we are dealing with exceptions
			//a) IF TEST CONTAINS ONE Final Result type component then remove all other reportable components from the list (leave the comments pertaining to Final Result.
			boolean testHasFinalResultComponent = false;
			List tempTestAnalytes = new ArrayList();
			for (int z = 0; z < testAnalytes.size(); z++) {
				TestAnalyte ta = (TestAnalyte)testAnalytes.get(z);
				Analyte a = ta.getAnalyte();
				if (testHasFinalResultComponent && (a.getAnalyteName().equalsIgnoreCase(HL7_ANALYTE_NAME_INTERPRETATION) || a.getAnalyteName().equalsIgnoreCase(HL7_ANALYTE_NAME_COMMENT))) {
					tempTestAnalytes.add(ta);
				}
				if (testHasFinalResultComponent && (!a.getAnalyteName().equalsIgnoreCase(HL7_ANALYTE_NAME_INTERPRETATION) && !a.getAnalyteName().equalsIgnoreCase(HL7_ANALYTE_NAME_COMMENT))) {
					continue;
				}
				if (a.getAnalyteName().equalsIgnoreCase(HL7_ANALYTE_NAME_FINAL_RESULT)) {
					tempTestAnalytes.add(ta);
					testHasFinalResultComponent = true;
				}
			}
			
			if (testHasFinalResultComponent) {
				testAnalytes = new ArrayList();
				for (int z = 0; z < tempTestAnalytes.size(); z++) {
					testAnalytes.add(tempTestAnalytes.get(z));
				}
			}
			//end a)
			
			//b) IF TEST CONTAINS MORE THAN ONE NON-INTERPRETAION/NON-COMMENT type component to be included in the message
			//    THEN TREAT THESE ANALYTES AS TESTS
			boolean multipleAnalyteException = false;
			int countOfNonCommentTypeAnalytes = 0;
			for (int z = 0; z < testAnalytes.size(); z++) {
				TestAnalyte ta = (TestAnalyte)testAnalytes.get(z);
				Analyte a = ta.getAnalyte();
				if (!a.getAnalyteName().equalsIgnoreCase(HL7_ANALYTE_NAME_INTERPRETATION) && !a.getAnalyteName().equalsIgnoreCase(HL7_ANALYTE_NAME_COMMENT)) {
					countOfNonCommentTypeAnalytes++;
				}
			}
			
			if (countOfNonCommentTypeAnalytes > 1) {
				multipleAnalyteException = true;
			}
			//end b)
			
			//There should only be one result for influenza of dictionary type
			for (int k = 0; k < testAnalytes.size(); k++) {
				TestAnalyte testAnalyte = (TestAnalyte) testAnalytes
				.get(k);
				Analyte analyte = testAnalyte
				.getAnalyte();
				
				//if we have a new reportable analyte then save the last one as a test
				if (!analyte.getAnalyteName().equalsIgnoreCase(HL7_ANALYTE_NAME_INTERPRETATION) && !analyte.getAnalyteName().equalsIgnoreCase(HL7_ANALYTE_NAME_COMMENT)) {
					if (resultXmit != null) {
						sampleTest = new TestXmit();
						testName = new TestNameXmit();
						//Don't add empty result (some results like comments and interpretaion are reported in comment section
						testResults = new ArrayList();
						if (resultXmit.getDescription() != null)
							testResults.add(resultXmit);
						sampleTest.setResults(testResults);
						
						//rebuild test information from analyte if this is a multipleAnalyteException
						if (multipleAnalyteException) {
							testName.setCode(analyteTestCode);
							testName.setDescription(analyteTestDescription);
						} else {
							testName.setCode(TestService.getUserLocalizedTestName( analysis.getTest() ) );
							testName.setDescription(TestService.getLocalizedTestNameWithType( analysis.getTest() ));
						}
						sampleTest.setName(testName);
						
						//build test comment
						if (!StringUtil.isNullorNill(interpretationComment.toString())) {
							comment.append(interpretationComment.toString());
							if (!StringUtil.isNullorNill(comment.toString()) && !comment.toString().endsWith(PERIOD)) {
								comment.append(PERIOD_SPACE);
							}
						}
						if (!StringUtil.isNullorNill(externalNotesComment.toString())) {
							comment.append(externalNotesComment.toString());
							if (!StringUtil.isNullorNill(comment.toString()) && !comment.toString().endsWith(PERIOD)) {
								comment.append(PERIOD_SPACE);
							}
						}
						sampleTest.setComment(comment.toString().trim());
						//initialize comment variables
						comment = new StringBuffer();
						externalNotesComment = new StringBuffer();
						interpretationComment = new StringBuffer();
						
						sampleTest.setReleasedDate(analysis.getReleasedDate());
						
						boolean isCorrected = false;
						if (!analysis.getRevision().equals("0")) {
							Analysis previousAnalysis = analysisDAO.getPreviousAnalysisForAmendedAnalysis(analysis);
							if (previousAnalysis.getPrintedDate() != null) {
								isCorrected = true;
							} 
						}
						if (isCorrected) {
							sampleTest.setStatus(HL7_CORRECTED_RESULT_STATUS_CODE);
						} else {
							sampleTest.setStatus(HL7_FINAL_RESULT_STATUS_CODE);
						}
						
						// there is a requirement that there is at least
						// one result for a test
						if (testResults.size() > 0) {
							sampleTests.add(sampleTest);
						}
					} 
					//we are not ready to save this yet but store the analyte info
					analyteTestCode = analyte.getLocalAbbreviation();
					analyteTestDescription = analyte.getAnalyteName();
					//only create a new ResultXmit if we are not dealing with an interpretation/comment
					resultXmit = new ResultXmit();
				}
				
				
				Result result = new Result();
				resultDAO.getResultByAnalysisAndAnalyte(
						result, analysis, testAnalyte);
				if (result != null
						&& !StringUtil.isNullorNill(result
								.getId())) {
					
					// we have at least one result so
					// add this
					TestResult testResult = result
					.getTestResult();
					String value = null;
					String localAbbrev = null;
					if (testResult
							.getTestResultType()
							.equals(
									SystemConfiguration
									.getInstance()
									.getDictionaryType())) {
						// get from dictionary
						Dictionary dictionary = new Dictionary();
						dictionary.setId(testResult
								.getValue());
						dictDAO.getData(dictionary);
						// System.out.println("setting
						// dictEntry "
						// + dictionary.getDictEntry());
						value = dictionary.getDictEntry();
						localAbbrev = dictionary.getLocalAbbreviation();
						
					} else {
						value = testResult.getValue();
					}
					
					if (!analyte.getAnalyteName().equalsIgnoreCase(HL7_ANALYTE_NAME_INTERPRETATION) && !analyte.getAnalyteName().equalsIgnoreCase(HL7_ANALYTE_NAME_COMMENT)) {
						resultXmit.setCode(localAbbrev);
						resultXmit.setDescription(value);
						resultXmit.setValueType(HL7_RESULT_VALUE_TYPE);
					} else {
						if (!StringUtil.isNullorNill(value)) {
							if (!StringUtil.isNullorNill(interpretationComment.toString()) && !interpretationComment.toString().trim().endsWith(PERIOD)) {
								interpretationComment.append(PERIOD_SPACE);
							}
							interpretationComment.append(value);
						}
					}
					
					//construct rest of comment
					//now get the Notes for this result if exist
					Note note = new Note();
					List notesByResult = new ArrayList();
					note.setReferenceId(result.getId());
					//bugzilla 1922
					//bugzilla 2571 go through ReferenceTablesDAO to get reference tables info
					ReferenceTables referenceTables = new ReferenceTables();
					referenceTables.setId(SystemConfiguration.getInstance().getResultReferenceTableId());
					note.setReferenceTables(referenceTables);
					note.setNoteType(SystemConfiguration.getInstance().getNoteTypeExternal());
					notesByResult = noteDAO.getNotesByNoteTypeRefIdRefTable(note);
					
					for (int x = 0; x < notesByResult.size(); x++) {
						note = (Note)notesByResult.get(x);
						if (note != null && !StringUtil.isNullorNill(note.getText())) {
							String text = note.getText().trim();
							if (!StringUtil.isNullorNill(externalNotesComment.toString()) && !externalNotesComment.toString().trim().endsWith(PERIOD)) {
								externalNotesComment.append(PERIOD_SPACE);
							}
							externalNotesComment.append(text);
						}
					}
					
					
				}
				
			}
			
			// END RESULTS FOR TEST
			//Don't add empty result (some results like comments and interpretaion are reported in comment section
			testResults = new ArrayList();
			if (resultXmit.getDescription() != null)
				testResults.add(resultXmit);
			sampleTest = new TestXmit();
			testName = new TestNameXmit();
			sampleTest.setResults(testResults);
			
			
			
			//rebuild test information from analyte if this is a multipleAnalyteException
			if (multipleAnalyteException) {
				testName.setCode(analyteTestCode);
				testName.setDescription(analyteTestDescription);
			} else {
				testName.setCode(TestService.getUserLocalizedTestName( analysis.getTest() ));
				testName.setDescription(TestService.getLocalizedTestNameWithType( analysis.getTest() ));
			}
			sampleTest.setName(testName);
			
			//concatenate testtrailer text to comment if exists
			TestTrailer testTrailer = t.getTestTrailer();
			if (testTrailer != null && !StringUtil.isNullorNill(testTrailer.getText())) {
				if (!StringUtil.isNullorNill(testTrailerTextComment.toString()) && !testTrailerTextComment.toString().trim().endsWith(PERIOD)) {
					testTrailerTextComment.append(PERIOD_SPACE);
				}
				testTrailerTextComment.append(testTrailer.getText());
			}
			
			//build test comment
			if (!StringUtil.isNullorNill(interpretationComment.toString())) {
				comment.append(interpretationComment.toString());
				if (!StringUtil.isNullorNill(comment.toString()) && !comment.toString().endsWith(PERIOD)) {
					comment.append(PERIOD_SPACE);
				}
			}
			if (!StringUtil.isNullorNill(externalNotesComment.toString())) {
				comment.append(externalNotesComment.toString());
				if (!StringUtil.isNullorNill(comment.toString()) && !comment.toString().endsWith(PERIOD)) {
					comment.append(PERIOD_SPACE);
				}
			}
			if (!StringUtil.isNullorNill(testTrailerTextComment.toString())) {
				comment.append(testTrailerTextComment.toString());
				if (!StringUtil.isNullorNill(comment.toString()) && !comment.toString().endsWith(PERIOD)) {
					comment.append(PERIOD_SPACE);
				}
			}
			
			sampleTest.setComment(comment.toString().trim());
			sampleTest.setReleasedDate(analysis.getReleasedDate());
			
			boolean isCorrected = false;
			if (!analysis.getRevision().equals("0")) {
				Analysis previousAnalysis = analysisDAO.getPreviousAnalysisForAmendedAnalysis(analysis);
				if (previousAnalysis.getPrintedDate() != null) {
					isCorrected = true;
				} 
			}
			if (isCorrected) {
				sampleTest.setStatus(HL7_CORRECTED_RESULT_STATUS_CODE);
			} else {
				sampleTest.setStatus(HL7_FINAL_RESULT_STATUS_CODE);
			}
			
			
			// there is a requirement that there is at least
			// one result for a test
			if (testResults.size() > 0) {
				sampleTests.add(sampleTest);
			}
			
		}
		sampleXmit.setTests(sampleTests);
		
		message.setSample(sampleXmit);
		
		// for MNPHL portion of message (doing this last minute to not use up sequences if
		//       sample is not eligible for sending
		MNPHLXmit mnphl = new MNPHLXmit();
		
		mnphl.setId(accessionNumber + "_" + sampleTransmissionSequenceDAO.getNextSampleTransmissionSequenceNumber(sampleTransmissionSequence));
		mnphl.setSendingApplication(SystemConfiguration.getInstance()
				.getInfluenzaDefaultApplicationName());
		//mnphl.setMessageTime((new Timestamp(System.currentTimeMillis()))
		//.toString());
		mnphl.setMessageTime(dateAsText);
		mnphl.setProcessingId(SystemConfiguration.getInstance()
				.getInfluenzaDefaultProcessingIdForXMLTransmission());
		
		
		message.setMnphl(mnphl);
		return message;
		
	}
	
}
