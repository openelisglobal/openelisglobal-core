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
package us.mn.state.health.lims.reports.action;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import us.mn.state.health.lims.audittrail.dao.AuditTrailDAO;
import us.mn.state.health.lims.audittrail.daoimpl.AuditTrailDAOImpl;
import us.mn.state.health.lims.audittrail.valueholder.History;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.organization.valueholder.Organization;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.project.valueholder.Project;
import us.mn.state.health.lims.provider.dao.ProviderDAO;
import us.mn.state.health.lims.provider.daoimpl.ProviderDAOImpl;
import us.mn.state.health.lims.provider.valueholder.Provider;
import us.mn.state.health.lims.referencetables.dao.ReferenceTablesDAO;
import us.mn.state.health.lims.referencetables.daoimpl.ReferenceTablesDAOImpl;
import us.mn.state.health.lims.referencetables.valueholder.ReferenceTables;
import us.mn.state.health.lims.reports.valueholder.audittrail.HistoryComparator;
import us.mn.state.health.lims.reports.valueholder.audittrail.HistoryXmlHelper;
import us.mn.state.health.lims.reports.valueholder.audittrail.SampleXmlHelper;
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
import us.mn.state.health.lims.sampleproject.valueholder.SampleProject;
import us.mn.state.health.lims.sourceofsample.valueholder.SourceOfSample;
import us.mn.state.health.lims.systemuser.dao.SystemUserDAO;
import us.mn.state.health.lims.systemuser.daoimpl.SystemUserDAOImpl;
import us.mn.state.health.lims.systemuser.valueholder.SystemUser;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;


/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 * bugzilla 2569
 */
public class AuditTrailReportBySampleProcessAction extends BaseAction {

	Properties transmissionMap = null;
	InputStream propertyStream = null;


	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String forward = FWD_SUCCESS;
		BaseActionForm dynaForm = (BaseActionForm) form;
		ActionMessages errors = null;

		request.setAttribute(ALLOW_EDITS_KEY, "false");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		String accessionNumber = (String)dynaForm.getString("accessionNumber");
		SampleXmlHelper sampleXmlHelper = new SampleXmlHelper();

		if (!StringUtil.isNullorNill(accessionNumber)) {
			Sample sample = new Sample();
			SampleDAO sampleDAO = new SampleDAOImpl();
			sample.setAccessionNumber(accessionNumber);

			List testTestAnalytes = new ArrayList();
			try {
				sampleDAO.getSampleByAccessionNumber(sample);

				if (!StringUtil.isNullorNill(sample.getStatus()) && sample.getStatus().equals(SystemConfiguration.getInstance()
						.getSampleStatusLabelPrinted())) {
					dynaForm.set("accessionNumber", accessionNumber);
					request.setAttribute(ALLOW_EDITS_KEY,
					"false");
					return mapping.findForward(FWD_FAIL);
				}

			} catch (LIMSRuntimeException lre) {

				LogEvent.logError("AuditTrailReportBySampleProcessAction","performAction()",lre.toString());
				errors = new ActionMessages();
				ActionError error = null;
				error = new ActionError("errors.GetException", null, null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
				saveErrors(request, errors);
				request.setAttribute(Globals.ERROR_KEY, errors);
				request.setAttribute(ALLOW_EDITS_KEY, "false");
				return mapping.findForward(FWD_FAIL);

			}

			String domain = sample.getDomain();

			String humanDomain = SystemConfiguration.getInstance()
			.getHumanDomain();

			String animalDomain = SystemConfiguration.getInstance()
			.getAnimalDomain();

			if (domain != null && domain.equals(humanDomain)) {
				// go to human view

				Patient patient = new Patient();
				Person patientPerson = new Person();
				Provider provider = new Provider();
				Person providerPerson = new Person();
				SampleHuman sampleHuman = new SampleHuman();
				SampleOrganization sampleOrganization = new SampleOrganization();
				Organization organization = new Organization();
				List sampleProjects = new ArrayList();
				Project project = new Project();
				Project project2 = new Project();
				SampleItem sampleItem = new SampleItem();

				try {

					PatientDAO patientDAO = new PatientDAOImpl();
					ProviderDAO providerDAO = new ProviderDAOImpl();
					SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
					SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
					SampleOrganizationDAO sampleOrganizationDAO = new SampleOrganizationDAOImpl();
					AuditTrailDAO auditTrailDAO = new AuditTrailDAOImpl();
					SystemUserDAO systemUserDAO = new SystemUserDAOImpl();


					if (!StringUtil.isNullorNill(sample.getId())) {
						sampleHuman.setSampleId(sample.getId());
						sampleHumanDAO.getDataBySample(sampleHuman);
						sampleOrganization.setSample(sample);
						sampleOrganizationDAO
						.getDataBySample(sampleOrganization);
						sampleItem.setSample(sample);
						sampleItemDAO.getDataBySample(sampleItem);

						if (sampleHuman != null) {
							if (sampleHuman.getPatientId() != null) {
								patient.setId(sampleHuman.getPatientId());
								patientDAO.getData(patient);
								patientPerson = patient.getPerson();
								provider.setId(sampleHuman.getProviderId());
								providerDAO.getData(provider);
								providerPerson = provider.getPerson();
						
							}
						}

					}
					organization = (Organization) sampleOrganization
					.getOrganization();
					sampleProjects = sample.getSampleProjects();

					if (sampleProjects != null && sampleProjects.size() > 0) {
						SampleProject sampleProject = (SampleProject) sampleProjects
						.get(0);
						project = sampleProject.getProject();
						if (sampleProjects.size() > 1) {
							SampleProject sampleProject2 = (SampleProject) sampleProjects
							.get(1);
							project2 = sampleProject2.getProject();
						}
					}


					String tableName = "SAMPLE";
					ReferenceTablesDAO referenceTablesDAO = new ReferenceTablesDAOImpl();
					ReferenceTables referenceTables = new ReferenceTables();
					referenceTables.setTableName(tableName);
					ReferenceTables rt = referenceTablesDAO.getReferenceTableByName(referenceTables);


					PropertyUtils.copyProperties(sampleXmlHelper, sample);

					//String data = auditTrailDAO.retrieveBlobData(sample.getId());
					//String data = auditTrailDAO.retrieveBlobData("9446");
					History history = new History();
					history.setReferenceId(sample.getId());
					history.setReferenceTable(rt.getId());
					List historyRecords = auditTrailDAO.getHistoryByRefIdAndRefTableId(history);

					
					List sampleHistoryRecords = populateHistoryList(request, historyRecords, "sample", "sampleHistoryMapping.xsl");
					sampleXmlHelper.setHistoryRecords((ArrayList)sampleHistoryRecords);
					
					tableName = "SAMPLE_ITEM";
					referenceTables = new ReferenceTables();
					referenceTables.setTableName(tableName);
					rt = referenceTablesDAO.getReferenceTableByName(referenceTables);
					history = new History();
					history.setReferenceId(sampleItem.getId());
					history.setReferenceTable(rt.getId());
					historyRecords = auditTrailDAO.getHistoryByRefIdAndRefTableId(history);


					List sampleItemHistoryRecords = populateHistoryList(request, historyRecords, "sampleItem", "sampleItemHistoryMapping.xsl");
					sampleXmlHelper.addHistoryRecords((ArrayList)sampleItemHistoryRecords);
					
					tableName = "PATIENT";
					referenceTables = new ReferenceTables();
					referenceTables.setTableName(tableName);
					rt = referenceTablesDAO.getReferenceTableByName(referenceTables);
					history = new History();
					history.setReferenceId(patient.getId());
					history.setReferenceTable(rt.getId());
					historyRecords = auditTrailDAO.getHistoryByRefIdAndRefTableId(history);


					List patientHistoryRecords = populateHistoryList(request, historyRecords, "patient", "patientHistoryMapping.xsl");
					sampleXmlHelper.addHistoryRecords((ArrayList)patientHistoryRecords);
					
					
					tableName = "PERSON";
					referenceTables = new ReferenceTables();
					referenceTables.setTableName(tableName);
					rt = referenceTablesDAO.getReferenceTableByName(referenceTables);
					history = new History();
					history.setReferenceId(patientPerson.getId());
					history.setReferenceTable(rt.getId());
					historyRecords = auditTrailDAO.getHistoryByRefIdAndRefTableId(history);


					//List patientPersonHistoryRecords = populateHistoryList(request, historyRecords, "person", "personHistoryMapping.xsl");
					//sampleXmlHelper.addHistoryRecords((ArrayList)patientPersonHistoryRecords);
					
					tableName = "PERSON";
					referenceTables = new ReferenceTables();
					referenceTables.setTableName(tableName);
					rt = referenceTablesDAO.getReferenceTableByName(referenceTables);
					history = new History();
					history.setReferenceId(providerPerson.getId());
					history.setReferenceTable(rt.getId());
					historyRecords = auditTrailDAO.getHistoryByRefIdAndRefTableId(history);


					//List providerPersonHistoryRecords = populateHistoryList(request, historyRecords, "person", "personHistoryMapping.xsl");
					//sampleXmlHelper.addHistoryRecords((ArrayList)providerPersonHistoryRecords);
					
					
					
					tableName = "SAMPLE_PROJECTS";
					referenceTables = new ReferenceTables();
					referenceTables.setTableName(tableName);
					rt = referenceTablesDAO.getReferenceTableByName(referenceTables);
					history = new History();
					history.setReferenceId(providerPerson.getId());
					history.setReferenceTable(rt.getId());
					historyRecords = auditTrailDAO.getHistoryByRefIdAndRefTableId(history);


					List sampleProjectHistoryRecords = populateHistoryList(request, historyRecords, "sampleProject", "sampleProjectHistoryMapping.xsl");
					sampleXmlHelper.addHistoryRecords((ArrayList)sampleProjectHistoryRecords);

					// initialize the form
					dynaForm.initialize(mapping);
					
					tableName = "SAMPLE_ORGANIZATION";
					referenceTables = new ReferenceTables();
					referenceTables.setTableName(tableName);
					rt = referenceTablesDAO.getReferenceTableByName(referenceTables);
					history = new History();
					history.setReferenceId(sampleOrganization.getId());
					history.setReferenceTable(rt.getId());
					historyRecords = auditTrailDAO.getHistoryByRefIdAndRefTableId(history);


					List sampleOrganizationHistoryRecords = populateHistoryList(request, historyRecords, "sampleOrganization", "sampleOrganizationHistoryMapping.xsl");
					sampleXmlHelper.addHistoryRecords((ArrayList)sampleOrganizationHistoryRecords);
					
					List historyRecordsForSorting = sampleXmlHelper.getHistoryRecords();
					
					Collections.sort(historyRecordsForSorting, HistoryComparator.NAME_COMPARATOR);
					
					String savedUserName = "";
					List dateSortList = new ArrayList();
					List finalList = new ArrayList();
					//now within name sort by date desc
					for (int i = 0; i < historyRecordsForSorting.size(); i++) {
						//break down into chunks by name
						HistoryXmlHelper hist = (HistoryXmlHelper)historyRecordsForSorting.get(i);
						
						if (i > 0 && !hist.getUserName().equals(savedUserName)) {
							//now sort chunk so far
							Collections.sort(dateSortList, HistoryComparator.DATE_COMPARATOR);
							finalList.addAll(dateSortList);
							dateSortList.clear();
						} 
						dateSortList.add(hist);
						savedUserName = hist.getUserName();
					}
					
					if (dateSortList != null && dateSortList.size() > 0) {
						Collections.sort(dateSortList, HistoryComparator.DATE_COMPARATOR);
						finalList.addAll(dateSortList);
						dateSortList.clear();
					}
					
					sampleXmlHelper.setHistoryRecords((ArrayList)finalList);

					// initialize the form
					dynaForm.initialize(mapping);





				} catch (LIMSRuntimeException lre) {
					// if error then forward to fail and don't update to blank
					// page
					// = false
					//bugzilla 2154
					LogEvent.logError("ResultsEntryViewAction","performAction()",lre.toString());
					errors = new ActionMessages();
					ActionError error = null;
					error = new ActionError("errors.GetException", null, null);
					errors.add(ActionMessages.GLOBAL_MESSAGE, error);
					saveErrors(request, errors);
					request.setAttribute(Globals.ERROR_KEY, errors);
					request.setAttribute(ALLOW_EDITS_KEY,
					"false");
					return mapping.findForward(FWD_FAIL);

				}

				// populate form from valueholder
				//PropertyUtils.setProperty(dynaForm, "sampleLastupdated", sample
						//.getLastupdated());
				PropertyUtils.setProperty(dynaForm, "patientFirstName", patientPerson
						.getFirstName());
				PropertyUtils.setProperty(dynaForm, "patientLastName", patientPerson
						.getLastName());
				PropertyUtils.setProperty(dynaForm, "patientId", patient
						.getExternalId());
				
				PropertyUtils.setProperty(dynaForm, "gender", patient
						.getGender());
				
				PropertyUtils.setProperty(dynaForm, "chartNumber", patient
						.getChartNumber());
				
				PropertyUtils.setProperty(dynaForm, "birthDateForDisplay",
						(String) patient.getBirthDateForDisplay());
				TypeOfSample typeOfSample = sampleItem.getTypeOfSample();
				SourceOfSample sourceOfSample = sampleItem.getSourceOfSample();
				if (typeOfSample == null) {
					PropertyUtils.setProperty(dynaForm, "typeOfSample",
							new TypeOfSample());
				} else {
					PropertyUtils.setProperty(dynaForm, "typeOfSample",
							typeOfSample);
				}
				if (sourceOfSample == null) {
					PropertyUtils.setProperty(dynaForm, "sourceOfSample",
							new SourceOfSample());
				} else {
					PropertyUtils.setProperty(dynaForm, "sourceOfSample",
							sourceOfSample);
				}

				PropertyUtils.setProperty(dynaForm, "sourceOther", sampleItem
						.getSourceOther());
				PropertyUtils.setProperty(dynaForm, "receivedDateForDisplay",
						(String) sample.getReceivedDateForDisplay());
				PropertyUtils.setProperty(dynaForm, "collectionDateForDisplay",
						(String) sample.getCollectionDateForDisplay());
				
				PropertyUtils.setProperty(dynaForm, "collectionTimeForDisplay",
						(String) sample.getCollectionTimeForDisplay());
	
				PropertyUtils.setProperty(dynaForm, "referredCultureFlag",
						(String) sample.getReferredCultureFlag());
				
				PropertyUtils.setProperty(dynaForm, "stickerReceivedFlag",
						(String) sample.getStickerReceivedFlag());
				
				if (organization == null) {
					PropertyUtils.setProperty(dynaForm, "organization",
							new Organization());
				} else {
					PropertyUtils.setProperty(dynaForm, "organization",
							organization);
				}

				if (project == null) {
					PropertyUtils.setProperty(dynaForm, "project",
							new Project());
				} else {
					PropertyUtils.setProperty(dynaForm, "project", project);
				}

				if (project2 == null) {
					PropertyUtils.setProperty(dynaForm, "project2",
							new Project());
				} else {
					PropertyUtils.setProperty(dynaForm, "project2", project2);
				}

				// reload accession number
				PropertyUtils.setProperty(dynaForm, "accessionNumber",
						accessionNumber);
				PropertyUtils.setProperty(dynaForm, "domain", domain);

				PropertyUtils.setProperty(dynaForm, "sampleXmlHelper", sampleXmlHelper);

				forward = FWD_SUCCESS_HUMAN;
			} else if (domain != null && domain.equals(animalDomain)) {
				// go to animal view
				// System.out.println("Going to animal view");
				forward = FWD_SUCCESS_ANIMAL;
			} else {
				forward = FWD_SUCCESS;
			}

		}

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		return "report.audit.trail.sample.title";
	}

	protected String getPageSubtitleKey() {
		return "report.audit.trail.sample.subtitle";
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

	private void parseXmlFile(String xml){
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			Document dom = db.parse(xml);

			System.out.println("This is parsed xml getting changes " + dom.getElementById("accessionNumber") );


		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
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

	protected List populateHistoryList(HttpServletRequest request, List historyRecords, String rootNodeName, String xslMappingFileName) throws LIMSRuntimeException {
		List list = new ArrayList();
		try {
			SystemUserDAO systemUserDAO = new SystemUserDAOImpl();
			AuditTrailDAO auditTrailDAO = new AuditTrailDAOImpl();
			

			for (int i = 0; i < historyRecords.size(); i++) {

				History historyRecord = (History)historyRecords.get(i);
				Timestamp date = historyRecord.getTimestamp();
				String dateForDisplay = DateUtil.convertTimestampToStringDateAndTime(date);

				SystemUser systemUser = new SystemUser();
				systemUser.setId(historyRecord.getSysUserId());
				systemUserDAO.getData(systemUser);
				String blob = null;
				if (!historyRecord.getActivity().equals(AUDIT_TRAIL_INSERT)) {
					blob = auditTrailDAO.retrieveBlobData(historyRecord.getId());
				}

				//this is temporary until 2593 has been completed

				if (historyRecord.getActivity().equals(IActionConstants.AUDIT_TRAIL_UPDATE)) {
                  blob = "<" + rootNodeName + ">" + blob + "</" + rootNodeName + ">";	
				}
                
				if (!StringUtil.isNullorNill(blob)) {
					HistoryXmlHelper historyXmlHelper = new HistoryXmlHelper();
					historyXmlHelper.setActivity(historyRecord.getActivity());
					historyXmlHelper.setUserName(systemUser.getNameForDisplay());

					String media= null , title = null, charset = null, xsldata = "";
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					try {

//						NOTE!!!!
//						in order to run this in oc4j I needed to do the following:
						// add this to OC4J startup: -Djavax.xml.transform.TransformerFactory=org.apache.xalan.processor.TransformerFactoryImpl
						//to make sure that oc4j uses correct XSL processor (else it could not handle "function extensions" used to bring in MessageResources labels
						//place xalan.jar, xml-apis.jar, XercesImpl.jar into the applib folder of the oc4j installation


						TransformerFactory tFactory = TransformerFactory.newInstance();

						HttpSession session = request.getSession();
						ServletContext context = session.getServletContext();

						File xslFile = new File(context
								.getRealPath("/WEB-INF/transformation/" + xslMappingFileName));

						Source stylesheet = new StreamSource(xslFile);

						Transformer transformer = tFactory.newTransformer(stylesheet);

						System.out.println("This is blob " + blob);
						transformer.transform(new StreamSource(new StringReader(blob)), 
								new StreamResult(outputStream));

					} catch (TransformerConfigurationException tce) {
						tce.printStackTrace();
					} catch (TransformerException te) {
						te.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}

					System.out.println("This is xml " + outputStream.toString());
					historyXmlHelper.setChange(outputStream.toString());

					historyXmlHelper.setDate(dateForDisplay);

					if (!StringUtil.isNullorNill(historyXmlHelper.getChange())) {
						historyXmlHelper.setChange(historyXmlHelper.getChange().trim());
					}
					
					if (!StringUtil.isNullorNill(historyXmlHelper.getChange())) {
						list.add(historyXmlHelper);
					}
				}
			}
		} catch (Exception e) {
			throw new LIMSRuntimeException(e);
		}
		return list;
	}
	
}
