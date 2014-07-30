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
package us.mn.state.health.lims.reports.send.sample.action;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Marshaller;
import org.xml.sax.InputSource;
import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.analyte.valueholder.Analyte;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.services.TestService;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.util.resources.ResourceLocator;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.organization.dao.OrganizationDAO;
import us.mn.state.health.lims.organization.daoimpl.OrganizationDAOImpl;
import us.mn.state.health.lims.organization.valueholder.Organization;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.person.dao.PersonDAO;
import us.mn.state.health.lims.person.daoimpl.PersonDAOImpl;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.provider.dao.ProviderDAO;
import us.mn.state.health.lims.provider.daoimpl.ProviderDAOImpl;
import us.mn.state.health.lims.reports.send.sample.valueholder.*;
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
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;
import us.mn.state.health.lims.testtrailer.dao.TestTrailerDAO;
import us.mn.state.health.lims.testtrailer.daoimpl.TestTrailerDAOImpl;
import us.mn.state.health.lims.testtrailer.valueholder.TestTrailer;
import us.mn.state.health.lims.typeofsample.dao.TypeOfSampleDAO;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleDAOImpl;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;
import us.mn.state.health.lims.typeoftestresult.dao.TypeOfTestResultDAO;
import us.mn.state.health.lims.typeoftestresult.daoimpl.TypeOfTestResultDAOImpl;
import us.mn.state.health.lims.typeoftestresult.valueholder.TypeOfTestResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class SampleXMLBySampleProcessAction extends BaseAction {

	InputStream propertyStream = null;

	Properties transmissionMap = null;
	//bugzilla 2393
	private static final String FILENAME_PREFIX = "PHLIP_vIOWA_";
	
	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String forward = FWD_SUCCESS;
		BaseActionForm dynaForm = (BaseActionForm) form;
		ActionMessages errors = null;

		request.setAttribute(ALLOW_EDITS_KEY, "false");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		// CHANGED

		String xmlString = null;
		// get transmission resources properties
		ResourceLocator rl = ResourceLocator.getInstance();
		// Now load a java.util.Properties object with the
		// properties
		transmissionMap = new Properties();
		try {
			propertyStream = rl
					.getNamedResourceAsInputStream(ResourceLocator.XMIT_PROPERTIES);

			transmissionMap.load(propertyStream);
		} catch (IOException e) {
    		//bugzilla 2154
			LogEvent.logError("SampleXMLBySampleProcessAction","performAction()",e.toString());
			throw new LIMSRuntimeException(
					"Unable to load transmission resource mappings.", e);
		} finally {
			if (null != propertyStream) {
				try {
					propertyStream.close();
					propertyStream = null;
				} catch (Exception e) {
    				//bugzilla 2154
			        LogEvent.logError("SampleXMLBySampleProcessAction","performAction()",e.toString());
				}
			}
		}

		String accessionNumber = (String) dynaForm.get("accessionNumber");

		// PROCESS

		String humanDomain = SystemConfiguration.getInstance().getHumanDomain();
		String animalDomain = SystemConfiguration.getInstance()
				.getAnimalDomain();

		try {

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
			OrganizationDAO organizationDAO = new OrganizationDAOImpl();
			TestTrailerDAO testTrailerDAO = new TestTrailerDAOImpl();
			TypeOfTestResultDAO typeOfTestResultDAO = new TypeOfTestResultDAOImpl();

			// for overall message portion of message
			MessageXmit message = new MessageXmit();

			// for UHL portion of message
			UHLXmit uhl = new UHLXmit();
			Organization organization = new Organization();
			TestingFacilityXmit uhlFacility = new TestingFacilityXmit();
			//bugzilla 2069
			organization.setOrganizationLocalAbbreviation(SystemConfiguration.getInstance()
					.getMdhOrganizationIdForXMLTransmission());
			organization = organizationDAO.getOrganizationByLocalAbbreviation(organization, true);

			StringBuffer orgName = new StringBuffer();
			orgName.append(organization.getOrganizationName());
			orgName.append(SystemConfiguration.getInstance()
					.getDefaultTransmissionTextSeparator());
			orgName.append(organization.getStreetAddress());
			orgName.append(SystemConfiguration.getInstance()
					.getDefaultTransmissionTextSeparator());
			orgName.append(organization.getCity());
			orgName.append(SystemConfiguration.getInstance()
					.getDefaultTransmissionTextSeparator());
			orgName.append(organization.getState());
			orgName.append(SystemConfiguration.getInstance()
					.getDefaultTransmissionTextSeparator());
			orgName.append(organization.getZipCode());
			orgName.append(SystemConfiguration.getInstance()
					.getDefaultTransmissionTextSeparator());
			orgName.append(SystemConfiguration.getInstance()
					.getMdhPhoneNumberForXMLTransmission());

			uhl.setId(SystemConfiguration.getInstance()
					.getMdhUhlIdForXMLTransmission());
			uhlFacility.setOrganizationName(orgName.toString());
			uhlFacility.setUniversalId(SystemConfiguration.getInstance()
					.getMdhUniversalIdForXMLTransmission());
			uhlFacility.setUniversalIdType(SystemConfiguration.getInstance()
					.getMdhUniversalIdTypeForXMLTransmission());

			uhl.setFacility(uhlFacility);
			uhl.setApplicationName(SystemConfiguration.getInstance()
					.getDefaultApplicationName());
			uhl.setMessageTime((new Timestamp(System.currentTimeMillis()))
					.toString());
			uhl.setProcessingId(SystemConfiguration.getInstance()
					.getDefaultProcessingIdForXMLTransmission());
			uhl.setTransportMethod(SystemConfiguration.getInstance()
					.getDefaultTransportMethodForXMLTransmission());

			PatientXmit patient = new PatientXmit();
			Person person = new Person();
			ProviderXmit provider = new ProviderXmit();
			FacilityXmit facility = new FacilityXmit();
			Person providerPerson = new Person();
			SampleHuman sampleHuman = new SampleHuman();
			SampleOrganization sampleOrganization = new SampleOrganization();
			List analyses = null;
			SourceOfSample sourceOfSample = new SourceOfSample();
			TypeOfSample typeOfSample = new TypeOfSample();

			Sample sample = new Sample();
			SampleItem sampleItem = new SampleItem();
			sample.setAccessionNumber(accessionNumber);
			sampleDAO.getSampleByAccessionNumber(sample);

			sampleHuman.setSampleId(sample.getId());
			sampleHumanDAO.getDataBySample(sampleHuman);
			sampleOrganization.setSampleId(sample.getId());
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
			//bugzilla 2227
			analyses = analysisDAO.getMaxRevisionAnalysesBySample(sampleItem);

			for (int i = 0; i < analyses.size(); i++) {
				Analysis analysis = (Analysis) analyses.get(i);
				sampleItemDAO.getData(sampleItem);
				sourceOfSample.setId(sampleItem.getSourceOfSampleId());
				if (!StringUtil.isNullorNill(sourceOfSample.getId())) {
					sourceOfSampleDAO.getData(sourceOfSample);
				}
				typeOfSample.setId(sampleItem.getTypeOfSampleId());
				if (!StringUtil.isNullorNill(typeOfSample.getId())) {
					typeOfSampleDAO.getData(typeOfSample);
				}

				// System.out.println("This is sampleItem " + sampleItem);
				if (sampleItem != null) {
				    //bugzilla 1773 need to store sample not sampleId for use in sorting
				    String sampleId = sampleItem.getSample().getId();
					SampleXmit sampleXmit = new SampleXmit();

					sampleXmit.setId(sampleId);
					sampleDAO.getData(sampleXmit);

					// marshall to XML
					Mapping castorMapping = new Mapping();

					String castorMappingName = transmissionMap
							.getProperty("SampleToXmlMapping");

					InputSource source = getSource(castorMappingName);

					// bugzilla #1346 add ability to hover over accession
					// number and
					// view patient/person information (first and last name
					// and external id)

					String domain = sampleXmit.getDomain();

					if (domain != null && domain.equals(humanDomain)) {
						// go to human view

						// bugzilla #1346 add ability to hover over
						// accession number and
						// view patient/person information (first and last
						// name and external id)

						if (!StringUtil.isNullorNill(sampleXmit.getId())) {

							sampleHuman.setSampleId(sampleXmit.getId());
							sampleHumanDAO.getDataBySample(sampleHuman);
							sampleOrganization.setSampleId(sampleXmit.getId());
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
							patient.setLastName(person.getLastName());
							patient.setFirstName(person.getFirstName());
							patient.setStreetAddress(person.getState());
							patient.setCity(person.getCity());
							patient.setState(person.getState());
							patient.setZipCode(person.getZipCode());
							patient.setHomePhone(person.getHomePhone());

							provider.setId(sampleHuman.getProviderId());
							providerDAO.getData(provider);
							providerPerson = provider.getPerson();
							personDAO.getData(providerPerson);
							Organization o = sampleOrganization
									.getOrganization();
							if (o != null) {
								PropertyUtils
										.copyProperties(facility, provider);
								// per Nancy 01/12/2007
								// have added null check
								if (!StringUtil.isNullorNill(o.getCliaNum())) {
									facility.setId(o.getCliaNum());
								}
								facility.setOrganizationName(o
										.getOrganizationName());
								facility.setDepartment(o.getOrganizationName());
								facility.setStreetAddress(o.getStreetAddress());
								facility.setCity(o.getCity());
								facility.setState(o.getState());
								facility.setZipCode(o.getZipCode());
							}

							provider
									.setWorkPhone(providerPerson.getWorkPhone());
							provider.setLastName(providerPerson.getLastName());
							provider
									.setFirstName(providerPerson.getFirstName());

							if (StringUtil.isNullorNill(sampleXmit
									.getRevision())) {
								sampleXmit.setRevision("0");
							}
							sampleXmit.setExternalId(patient.getExternalId());
							if (StringUtil.isNullorNill(sampleXmit.getStatus())) {
								sampleXmit
										.setStatus("THIS IS SAMPLE STATUS - IF BLANK SHOULD WE SEND DEFAULT");
							}
							sampleXmit.setPatient(patient);
							sampleXmit.setProvider(provider);
							sampleXmit.setFacility(facility);

							// get all tests for this sample
							//bugzilla 2227
							analyses = analysisDAO
									.getMaxRevisionAnalysesBySample(sampleItem);
							ArrayList sampleTests = new ArrayList();
							// assemble Test Elements
							for (int j = 0; j < analyses.size(); j++) {
								TestXmit sampleTest = new TestXmit();
								Analysis a = (Analysis) analyses.get(j);
								Test t = a.getTest();
								sampleTest.setMethod(t.getMethodName());
								sampleTest.setReleasedDate(a.getReleasedDate());

								if (sourceOfSample != null
										&& !StringUtil
												.isNullorNill(sourceOfSample
														.getDescription())) {
									sampleTest.setSourceOfSample(sourceOfSample
											.getDescription());
								}
								if (typeOfSample != null
										&& !StringUtil
												.isNullorNill(typeOfSample
														.getDescription())) {
									sampleTest.setTypeOfSample(typeOfSample
											.getDescription());
								}
								CodeElementXmit testName = new CodeElementXmit();
								// do we need go to receiver_xref to get
								// their test name? identifier, codesystem
								// type,
								testName.setIdentifier( TestService.getLocalizedTestName( a.getTest()) );
								testName.setCodeSystemType("L");
								testName.setText("This is some kind of text");
								sampleTest.setName(testName);

								TestTrailer testTrailer = t.getTestTrailer();

								CommentXmit testComment = new CommentXmit();
								if (testTrailer != null) {
									testComment.setComment(testTrailer
											.getText());
									testComment.setCommentSource("");
								}
								sampleTest.setComment(testComment);

								sampleTest
										.setStatus("This could be analysis status");

								// NOW GET THE RESULTS FOR THIS TEST
								TestResultDAO testResultDAO = new TestResultDAOImpl();
								DictionaryDAO dictDAO = new DictionaryDAOImpl();

								// load collection of
								// TestAnalyte_TestResults for the
								// test
								TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();
								List testAnalytes = testAnalyteDAO
										.getAllTestAnalytesPerTest(t);
								ArrayList testResults = new ArrayList();

								for (int k = 0; k < testAnalytes.size(); k++) {
									TestAnalyte testAnalyte = (TestAnalyte) testAnalytes
											.get(k);
									Result result = new Result();

									resultDAO.getResultByAnalysisAndAnalyte(
											result, analysis, testAnalyte);
									ResultXmit resultXmit = new ResultXmit();
									if (result != null
											&& !StringUtil.isNullorNill(result
													.getId())) {

										// we have at least one result so
										// add this
										TestResult testResult = result
												.getTestResult();
										String value = null;
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
										} else {
											value = testResult.getValue();
										}

										// now create other objects
										// within the result
										ObservationXmit observationXmit = new ObservationXmit();
										CodeElementXmit observationIdentifier = new CodeElementXmit();
										Analyte analyte = testAnalyte
												.getAnalyte();
										if (!StringUtil.isNullorNill(analyte
												.getExternalId())) {
											observationIdentifier
													.setIdentifier(analyte
															.getExternalId());
										} else {
											observationIdentifier
													.setIdentifier(analyte
															.getId());
										}
										observationIdentifier.setText(analyte
												.getAnalyteName());
										observationIdentifier
												.setCodeSystemType(SystemConfiguration
														.getInstance()
														.getDefaultTransmissionCodeSystemType());
										observationXmit
												.setIdentifier(observationIdentifier);
										observationXmit.setValue(value);
										//bugzilla 1866
										TypeOfTestResult totr = new TypeOfTestResult();
										totr.setTestResultType(testResult.getTestResultType());
										totr = typeOfTestResultDAO.getTypeOfTestResultByType(totr);
										observationXmit.setValueType(totr.getHl7Value());
										//end bugzilla 1866
										resultXmit
												.setObservation(observationXmit);

										//bugzilla 1867 remove empty tags
										//resultXmit.setReferenceRange("UNKNOWN");
										resultXmit.setReferenceRange(null);
										/*CodeElementXmit unitCodeElement = new CodeElementXmit();
										unitCodeElement
												.setIdentifier("UNKNOWN");
										unitCodeElement.setText("UNKNOWN");
										unitCodeElement
												.setCodeSystemType(SystemConfiguration
														.getInstance()
														.getDefaultTransmissionCodeSystemType());*/
										//resultXmit.setUnit(unitCodeElement);
										resultXmit.setUnit(null);
	                                    //end bugzilla 1867

									}
									testResults.add(resultXmit);

								}

								// END RESULTS FOR TEST

								sampleTest.setResults(testResults);
								sampleTest.setReleasedDate(analysis
										.getReleasedDate());

								// there is a requirement that there is at least
								// one result for a test
								if (testResults.size() > 0) {
									sampleTests.add(sampleTest);
								}

							}
							sampleXmit.setTests(sampleTests);

							message.setSample(sampleXmit);
							message.setUhl(uhl);
							try {
								// castorMapping.loadMapping(url);
								castorMapping.loadMapping(source);

								// Marshaller marshaller = new Marshaller(
								// new OutputStreamWriter(System.out));
								//bugzilla 2393
								String fileName = FILENAME_PREFIX + accessionNumber + ".xml";
								Marshaller marshaller = new Marshaller();
								marshaller.setMapping(castorMapping);
								Writer writer = new StringWriter();
								marshaller.setWriter(writer);
								marshaller.marshal(message);
								xmlString = writer.toString();
								
								//bugzilla 2393 allow for download of file
								response.reset();
								response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
								//response.setContentType("application/xml");
								response.setContentType("multipart/xml");
								response.setContentLength((int)xmlString.length());


								  try {
					                    OutputStream os = response.getOutputStream();
					                    byte[] xmlBytes = xmlString.getBytes();
					                    ByteArrayInputStream bais = new ByteArrayInputStream(xmlBytes);
					                    InputStream is = (InputStream)bais;
					                    int count;
					                    byte buf[] = new byte[4096];
					                    while ((count = is.read(buf)) > -1)
					                    os.write(buf, 0, count);
					                    is.close();
					                    os.close();
					                } catch (Exception ex) {
					                  ex.printStackTrace();
					                }//end try/catch

								//no need to display xml - we are saving a file instead
								xmlString = "";
								//xmlString = convertToDisplayableXML(xmlString);
							} catch (Exception e) {
								//bugzilla 2154
			                    LogEvent.logError("SampleXMLBySampleProcessAction","performAction()",e.toString());
							}

							// this writes a default mapping to oc4j's
							// j2ee/home directory
							/*
							 * try { MappingTool tool = new MappingTool();
							 * tool.setForceIntrospection(false);
							 * tool.addClass("us.mn.state.health.lims.patient.valueholder.Patient");
							 * tool.write(new FileWriter("XMLTestMapping.xml")); }
							 * catch(MappingException ex){
							 * System.out.println("Error" +
							 * ex.getLocalizedMessage());} catch(IOException
							 * ex){ System.out.println("Error" +
							 * ex.getLocalizedMessage());}
							 */

						}

					} else if (domain != null && domain.equals(animalDomain)) {
						// go to animal view
						// System.out.println("Going to animal view");
					} else {
						// go toother view
					}
				}
			}
		} catch (LIMSRuntimeException lre) {
			// if error then forward to fail and don't update to blank
			// page
			// = false

			//bugzilla 2154
			LogEvent.logError("SampleXMLBySampleProcessAction","performAction()",lre.toString());
			errors = new ActionMessages();
			ActionError error = null;
			error = new ActionError("errors.GetException", null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute(ALLOW_EDITS_KEY, "false");
			return mapping.findForward(FWD_FAIL);

		}

		// END PROCESS
		// initialize the form
		dynaForm.initialize(mapping);

		PropertyUtils.setProperty(dynaForm, "accessionNumber", accessionNumber);
		// PropertyUtils.setProperty(dynaForm, "xmlString",
		// "&lt;ajdf&gt;asdfasdf&lt;/ajdf&gt;");
		PropertyUtils.setProperty(dynaForm, "xmlString", xmlString);

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		return "report.sample.xml.by.test.title";
	}

	protected String getPageSubtitleKey() {
		return "report.sample.xml.by.test.subtitle";
	}

	protected String convertToDisplayableXML(String xml) {
		if (!StringUtil.isNullorNill(xml)) {
			xml = xml.replaceAll("<", "&lt;");
			xml = xml.replaceAll(">", "&gt;");
			// the following 2 lines are for display on page (not for system
			// out)
			xml = xml.replaceAll("\n", "<br>");
			xml = xml.replaceAll(" ", "&nbsp;");
		}
		return xml;
	}

	protected InputSource getSource(String castorMappingName) {
		InputStream mappingStream = null;
		// System.out.println("This is classpath " +
		// System.getProperty("java.class.path"));
		InputStream mappingXml = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(castorMappingName);
		// System.out.println("this is mappingXml " + mappingXml);
		InputSource source = new InputSource(mappingXml);
		return source;
	}
	
}
