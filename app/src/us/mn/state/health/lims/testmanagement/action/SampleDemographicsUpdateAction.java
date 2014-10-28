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
package us.mn.state.health.lims.testmanagement.action;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import us.mn.state.health.lims.action.dao.ActionDAO;
import us.mn.state.health.lims.action.daoimpl.ActionDAOImpl;
import us.mn.state.health.lims.action.valueholder.Action;
import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.analysisqaevent.dao.AnalysisQaEventDAO;
import us.mn.state.health.lims.analysisqaevent.daoimpl.AnalysisQaEventDAOImpl;
import us.mn.state.health.lims.analysisqaevent.valueholder.AnalysisQaEvent;
import us.mn.state.health.lims.analysisqaeventaction.dao.AnalysisQaEventActionDAO;
import us.mn.state.health.lims.analysisqaeventaction.daoimpl.AnalysisQaEventActionDAOImpl;
import us.mn.state.health.lims.analysisqaeventaction.valueholder.AnalysisQaEventAction;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.provider.validation.*;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.valueholder.UserSessionData;
import us.mn.state.health.lims.organization.dao.OrganizationDAO;
import us.mn.state.health.lims.organization.daoimpl.OrganizationDAOImpl;
import us.mn.state.health.lims.organization.valueholder.Organization;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.person.dao.PersonDAO;
import us.mn.state.health.lims.person.daoimpl.PersonDAOImpl;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.project.dao.ProjectDAO;
import us.mn.state.health.lims.project.daoimpl.ProjectDAOImpl;
import us.mn.state.health.lims.project.valueholder.Project;
import us.mn.state.health.lims.provider.dao.ProviderDAO;
import us.mn.state.health.lims.provider.daoimpl.ProviderDAOImpl;
import us.mn.state.health.lims.provider.valueholder.Provider;
import us.mn.state.health.lims.qaevent.dao.QaEventDAO;
import us.mn.state.health.lims.qaevent.daoimpl.QaEventDAOImpl;
import us.mn.state.health.lims.qaevent.valueholder.QaEvent;
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
import us.mn.state.health.lims.sampleproject.dao.SampleProjectDAO;
import us.mn.state.health.lims.sampleproject.daoimpl.SampleProjectDAOImpl;
import us.mn.state.health.lims.sampleproject.valueholder.SampleProject;
import us.mn.state.health.lims.sourceofsample.dao.SourceOfSampleDAO;
import us.mn.state.health.lims.sourceofsample.daoimpl.SourceOfSampleDAOImpl;
import us.mn.state.health.lims.sourceofsample.valueholder.SourceOfSample;
import us.mn.state.health.lims.systemuser.dao.SystemUserDAO;
import us.mn.state.health.lims.systemuser.daoimpl.SystemUserDAOImpl;
import us.mn.state.health.lims.systemuser.valueholder.SystemUser;
import us.mn.state.health.lims.typeofsample.dao.TypeOfSampleDAO;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleDAOImpl;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author aiswarya raman
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class SampleDemographicsUpdateAction extends BaseAction {

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		//System.out.println("I am in SampleDemographicsUpdateAction ");

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		BaseActionForm testManagementForm = (BaseActionForm) form;

		// server-side validation (validation.xml)
		ActionMessages errors = testManagementForm.validate(mapping, request);
		try {
			errors = validateAll(request, errors, testManagementForm);
		} catch (Exception e) {
    		//bugzilla 2154
			LogEvent.logError("SampleDemographicsUpdateAction","performAction()",e.toString());
			ActionError error = new ActionError("errors.ValidationException",
					null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		// end of zip/city combination check

		if (errors != null && errors.size() > 0) {
			saveErrors(request, errors);
			// since we forward to jsp - not Action we don't need to repopulate
			// the lists here
			return mapping.findForward(FWD_FAIL);
		}

		String accessionNumber = (String) testManagementForm
				.get("accessionNumber");

		String typeOfSample = (String) testManagementForm
				.get("typeOfSampleDesc");
		String sourceOfSample = (String) testManagementForm
				.get("sourceOfSampleDesc");

		List typeOfSamples = new ArrayList();
		List sourceOfSamples = new ArrayList();

		if (testManagementForm.get("typeOfSamples") != null) {
			typeOfSamples = (List) testManagementForm.get("typeOfSamples");
		} else {
			TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
			typeOfSamples = typeOfSampleDAO.getAllTypeOfSamples();
		}
		if (testManagementForm.get("sourceOfSamples") != null) {
			sourceOfSamples = (List) testManagementForm.get("sourceOfSamples");
		} else {
			SourceOfSampleDAO sourceOfSampleDAO = new SourceOfSampleDAOImpl();
			sourceOfSamples = sourceOfSampleDAO.getAllSourceOfSamples();
		}

		String projectIdOrName = (String) testManagementForm
				.get("projectIdOrName");
		String project2IdOrName = (String) testManagementForm
				.get("project2IdOrName");

		String projectNameOrId = (String) testManagementForm
				.get("projectNameOrId");
		String project2NameOrId = (String) testManagementForm
				.get("project2NameOrId");

		String projectId = null;
		String project2Id = null;

		if (projectIdOrName != null && projectNameOrId != null) {
			try {
				Integer i = Integer.valueOf(projectIdOrName);
				projectId = projectIdOrName;
			} catch (NumberFormatException nfe) {
    			//bugzilla 2154
			    LogEvent.logError("SampleDemographicsUpdateAction","performAction()",nfe.toString());
				projectId = projectNameOrId;
			}
		}

		if (project2IdOrName != null && project2NameOrId != null) {
			try {
				Integer i = Integer.valueOf(project2IdOrName);
				project2Id = project2IdOrName;
			} catch (NumberFormatException nfe) {
    			//bugzilla 2154
			    LogEvent.logError("SampleDemographicsUpdateAction","performAction()",nfe.toString());
				project2Id = project2NameOrId;
			}
		}

		// set current date for validation of dates
		Date today = Calendar.getInstance().getTime();
		String dateAsText = DateUtil.formatDateAsText(today);

		PersonDAO personDAO = new PersonDAOImpl();
		PatientDAO patientDAO = new PatientDAOImpl();
		ProviderDAO providerDAO = new ProviderDAOImpl();
		SampleDAO sampleDAO = new SampleDAOImpl();
		SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
		SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
		SampleOrganizationDAO sampleOrganizationDAO = new SampleOrganizationDAOImpl();
		AnalysisDAO analysisDAO = new AnalysisDAOImpl();
		SampleProjectDAO sampleProjectDAO = new SampleProjectDAOImpl();
		ProjectDAO projectDAO = new ProjectDAOImpl();
		AnalysisQaEventDAO analysisQaEventDAO = new AnalysisQaEventDAOImpl();
		AnalysisQaEventActionDAO analysisQaEventActionDAO = new AnalysisQaEventActionDAOImpl();
		QaEventDAO qaEventDAO = new QaEventDAOImpl();
		ActionDAO actionDAO = new ActionDAOImpl();


		Patient patient = new Patient();
		Person person = new Person();
		Provider provider = new Provider();
		Person providerPerson = new Person();
		Sample sample = new Sample();
		SampleHuman sampleHuman = new SampleHuman();
		SampleOrganization sampleOrganization = new SampleOrganization();
		List oldSampleProjects = new ArrayList();
		List newSampleProjects = new ArrayList();
		SampleItem sampleItem = new SampleItem();

		List analyses = new ArrayList();

		// GET ORIGINAL DATA
		try {

			sample.setAccessionNumber(accessionNumber);
			sampleDAO.getSampleByAccessionNumber(sample);
			if (!StringUtil.isNullorNill(sample.getId())) {
				sampleHuman.setSampleId(sample.getId());
				sampleHumanDAO.getDataBySample(sampleHuman);
				sampleOrganization.setSampleId(sample.getId());
				sampleOrganizationDAO.getDataBySample(sampleOrganization);
				// bugzilla 1773 need to store sample not sampleId for use in
				// sorting
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

				oldSampleProjects = sample.getSampleProjects();

			}

		} catch (LIMSRuntimeException lre) {
			// if error then forward to fail and don't update to blank page
			// = false
            //bugzilla 2154
			LogEvent.logError("SampleDemographicsUpdateAction","performAction()",lre.toString());
			errors = new ActionMessages();
			ActionError error = null;
			error = new ActionError("errors.GetException", null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute(ALLOW_EDITS_KEY, "false");
			return mapping.findForward(FWD_FAIL);

		}

		// UPDATE DATA FROM FORM
		// populate valueholder from form
		PropertyUtils.copyProperties(sample, testManagementForm);
		PropertyUtils.copyProperties(person, testManagementForm);
		PropertyUtils.copyProperties(patient, testManagementForm);
		PropertyUtils.copyProperties(provider, testManagementForm);
		PropertyUtils.copyProperties(sampleHuman, testManagementForm);
		PropertyUtils.copyProperties(sampleOrganization, testManagementForm);
		PropertyUtils.copyProperties(sampleItem, testManagementForm);

		TypeOfSample typeOfSamp = null;

		// get the right typeOfSamp to update sampleitem with
		for (int i = 0; i < typeOfSamples.size(); i++) {
			TypeOfSample s = (TypeOfSample) typeOfSamples.get(i);
			if (s.getDescription().equalsIgnoreCase(typeOfSample)) {
				typeOfSamp = s;
				break;
			}
		}

		SourceOfSample sourceOfSamp = null;

		// get the right sourceOfSamp to update sampleitem with
		for (int i = 0; i < sourceOfSamples.size(); i++) {
			SourceOfSample s = (SourceOfSample) sourceOfSamples.get(i);
			if (s.getDescription().equalsIgnoreCase(sourceOfSample)) {
				sourceOfSamp = s;
				break;
			}
		}

		Organization org = new Organization();
		//bugzilla 2069
		org.setOrganizationLocalAbbreviation((String) testManagementForm.get("organizationLocalAbbreviation"));
		OrganizationDAO organizationDAO = new OrganizationDAOImpl();
		org = organizationDAO.getOrganizationByLocalAbbreviation(org, true);
		sampleOrganization.setOrganization(org);

		// if there was a first sampleProject id entered
		// ****Added a Try catch block to validate integer because..
		// ****When a project is deleted, the name of the project is passed in
		// as its id which causes error
		try {
			Integer i = Integer.valueOf(projectId);

			if (!StringUtil.isNullorNill(projectId)) {
				SampleProject sampleProject = new SampleProject();
				Project p = new Project();
				//bugzilla 2438
				p.setLocalAbbreviation(projectId);
				p = projectDAO.getProjectByLocalAbbreviation(p, true);
				sampleProject.setProject(p);
				sampleProject.setSample(sample);
				sampleProject.setIsPermanent(NO);
				newSampleProjects.add(sampleProject);

			}

		} catch (NumberFormatException nfe) {
    		//bugzilla 2154
			LogEvent.logError("SampleDemographicsUpdateAction","performAction()",nfe.toString());
		}

		// in case there was a second sampleProject id entered
		try {
			Integer i = Integer.valueOf(project2Id);

			if (!StringUtil.isNullorNill(project2Id)) {
				SampleProject sampleProject2 = new SampleProject();
				Project p2 = new Project();
				//bugzilla 2438
				p2.setLocalAbbreviation(project2Id);
				p2 = projectDAO.getProjectByLocalAbbreviation(p2, true);
				sampleProject2.setProject(p2);
				sampleProject2.setSample(sample);
				sampleProject2.setIsPermanent(NO);
				newSampleProjects.add(sampleProject2);
			}

		} catch (NumberFormatException nfe) {
    		//bugzilla 2154
			LogEvent.logError("SampleDemographicsUpdateAction","performAction()",nfe.toString());
		}

		// set the provider person manually as we have two Person
		// valueholders
		// to populate and copyProperties() can only handle one per form
		providerPerson.setFirstName((String) testManagementForm
				.get("providerFirstName"));
		providerPerson.setLastName((String) testManagementForm
				.get("providerLastName"));

		// format workPhone for storage
		String workPhone = (String) testManagementForm.get("providerWorkPhone");
		String ext = (String) testManagementForm
				.get("providerWorkPhoneExtension");
		String formattedPhone = StringUtil.formatPhone(workPhone, ext);
		// phone is stored as 999/999-9999.9999
		// area code/phone - number.extension
		providerPerson.setWorkPhone(formattedPhone);

		String date = (String) testManagementForm
				.get("collectionDateForDisplay");

		if (!StringUtil.isNullorNill(date)) {
 			//System.out.println("I am here");
			// set collection time
			String time = (String) testManagementForm
					.get("collectionTimeForDisplay");
			if (StringUtil.isNullorNill(time)) {
				time = "00:00";
			}
			sample.setCollectionTimeForDisplay(time);
			sample.setCollectionDateForDisplay(date);

			Timestamp d = sample.getCollectionDate();
			if (time.indexOf(":") > 0) {
				// bugzilla 1857 deprecated stuff
				Calendar cal = Calendar.getInstance();
				cal.setTime(d);
				cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(
						time.substring(0, 2)).intValue());
				cal.set(Calendar.MINUTE, Integer.valueOf(time.substring(3, 5))
						.intValue());
				// d.setHours(Integer.valueOf(time.substring(0, 2)).intValue());
				// d.setMinutes(Integer.valueOf(time.substring(3,
				// 5)).intValue());
				d = new Timestamp(cal.getTimeInMillis());
				sample.setCollectionDate(d);
			}
		}

		// sampleItem
		sampleItem.setSortOrder("1");
		// set the typeOfSample
		sampleItem.setTypeOfSample(typeOfSamp);
		// set the sourceOfSample
		sampleItem.setSourceOfSample(sourceOfSamp);
		sample.setSampleProjects(newSampleProjects);

		// get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());
		org.hibernate.Transaction tx = HibernateUtil.getSession()
				.beginTransaction();

		//bugzilla 2481, 2496 Action Owner
		SystemUser systemUser = new SystemUser();
		systemUser.setId(sysUserId);
		SystemUserDAO systemUserDAO = new SystemUserDAOImpl();
		systemUserDAO.getData(systemUser);
		
		List newIds = new ArrayList();
		List oldIds = new ArrayList();

		if (newSampleProjects != null) {
			for (int i = 0; i < newSampleProjects.size(); i++) {
				SampleProject sp = (SampleProject) newSampleProjects.get(i);
				newIds.add(sp.getId());
			}
		}

		if (oldSampleProjects != null) {

			List listOfOldOnesToRemove = new ArrayList();
			for (int i = 0; i < oldSampleProjects.size(); i++) {
				SampleProject sp = (SampleProject) oldSampleProjects.get(i);
				oldIds.add(sp.getId());
				if (!newIds.contains(sp.getId())) {
					// remove ones that are to be deleted
					listOfOldOnesToRemove.add(new Integer(i));
				}
			}

			int decreaseOSPIndexBy = 0;
			int decreaseOIIndexBy = 0;
			List listOfSampleProjectObjectsToDelete = new ArrayList();
			for (int i = 0; i < listOfOldOnesToRemove.size(); i++) {
				SampleProject sp = (SampleProject) oldSampleProjects
						.remove(((Integer) listOfOldOnesToRemove.get(i))
								.intValue()
								- decreaseOSPIndexBy++);
				//bugzilla 1926
				sp.setSysUserId(sysUserId);
				listOfSampleProjectObjectsToDelete.add(sp);
				oldIds.remove(((Integer) listOfOldOnesToRemove.get(i))
						.intValue()
						- decreaseOIIndexBy++);

			}
			sampleProjectDAO.deleteData(listOfSampleProjectObjectsToDelete);
		}

		if (newSampleProjects != null) {
			for (int j = 0; j < newSampleProjects.size(); j++) {
				SampleProject saPr = (SampleProject) newSampleProjects.get(j);

				int index = oldIds.indexOf(saPr.getId());
				if (index >= 0) {
					SampleProject sampleProjectClone = (SampleProject) oldSampleProjects
							.get(index);
					PropertyUtils.copyProperties(sampleProjectClone, saPr);
					Sample smplClone = (Sample) sampleProjectClone.getSample();
					sampleProjectClone.setSample(smplClone);
					Project pClone = (Project) sampleProjectClone.getProject();
					sampleProjectClone.setProject(pClone);
					PropertyUtils.setProperty(sampleProjectClone,
							"lastupdated", (Timestamp) testManagementForm
									.get("sampleProject1Lastupdated"));

					sampleProjectClone.setSysUserId(sysUserId);
					sampleProjectDAO.updateData(sampleProjectClone);
					oldSampleProjects.set(index, sampleProjectClone);
				} else {
					SampleProject sampleProjectClone = new SampleProject();
					PropertyUtils.copyProperties(sampleProjectClone, saPr);
					Sample smplClone = (Sample) sampleProjectClone.getSample();
					sampleProjectClone.setSample(smplClone);
					Project pClone = (Project) sampleProjectClone.getProject();
					sampleProjectClone.setProject(pClone);
					PropertyUtils.setProperty(sampleProjectClone,
							"lastupdated", (Timestamp) testManagementForm
									.get("sampleProject2Lastupdated"));
					//bugzilla 1926
					sampleProjectClone.setSysUserId(sysUserId);
					sampleProjectDAO.insertData(sampleProjectClone);
					oldSampleProjects.add(sampleProjectClone);
				}
			}
		}

		sample.setSampleProjects(oldSampleProjects);
		// END DIANE

		try {

			// set last updated from form
			PropertyUtils.setProperty(person, "lastupdated",
					(Timestamp) testManagementForm.get("personLastupdated"));
			PropertyUtils.setProperty(patient, "lastupdated",
					(Timestamp) testManagementForm.get("patientLastupdated"));
			PropertyUtils.setProperty(sample, "lastupdated",
					(Timestamp) testManagementForm.get("lastupdated"));
			PropertyUtils.setProperty(providerPerson, "lastupdated",
					(Timestamp) testManagementForm
							.get("providerPersonLastupdated"));
			PropertyUtils.setProperty(provider, "lastupdated",
					(Timestamp) testManagementForm.get("providerLastupdated"));
			PropertyUtils
					.setProperty(sampleItem, "lastupdated",
							(Timestamp) testManagementForm
									.get("sampleItemLastupdated"));
			PropertyUtils.setProperty(sampleHuman, "lastupdated",
					(Timestamp) testManagementForm
							.get("sampleHumanLastupdated"));
			PropertyUtils.setProperty(sampleOrganization, "lastupdated",
					(Timestamp) testManagementForm
							.get("sampleOrganizationLastupdated"));

			person.setSysUserId(sysUserId);
			patient.setSysUserId(sysUserId);
			providerPerson.setSysUserId(sysUserId);
			provider.setSysUserId(sysUserId);
			sample.setSysUserId(sysUserId);
			sampleHuman.setSysUserId(sysUserId);
			sampleOrganization.setSysUserId(sysUserId);
			sampleItem.setSysUserId(sysUserId);
			personDAO.updateData(person);
			patient.setPerson(person);

			patientDAO.updateData(patient);
			personDAO.updateData(providerPerson);
			provider.setPerson(providerPerson);
			providerDAO.updateData(provider);
			sampleDAO.updateData(sample);

			sampleHuman.setSampleId(sample.getId());
			sampleHuman.setPatientId(patient.getId());
			sampleHuman.setProviderId(provider.getId());
			sampleHumanDAO.updateData(sampleHuman);
			sampleOrganization.setSampleId(sample.getId());
			sampleOrganization.setSample(sample);
			sampleOrganizationDAO.updateData(sampleOrganization);
			// bugzilla 1773 need to store sample not sampleId for use in
			// sorting
			sampleItem.setSample(sample);

			sampleItemDAO.updateData(sampleItem);

			// Analysis table
			for (int i = 0; i < analyses.size(); i++) {
				Analysis analysis = (Analysis) analyses.get(i);
				analysis.setSampleItem(sampleItem);
				analysis.setSysUserId(sysUserId);
				analysisDAO.updateData(analysis);
			}
			
			// bugzilla 3032/2028 qa event logic needs to be executed for new and
			// existing analyses for this sample
			// ADDITIONAL REQUIREMENT ADDED 8/30: only for HSE2 Completed Status (see bugzilla 2032)
			boolean isSampleStatusReadyForQaEvent = false;
			if (!StringUtil.isNullorNill(sample.getStatus()) && (sample.getStatus().equals(SystemConfiguration.getInstance().getSampleStatusReleased()))
					                                  || sample.getStatus().equals(SystemConfiguration.getInstance().getSampleStatusEntry2Complete())) {
				isSampleStatusReadyForQaEvent = true;
			}
			if (isSampleStatusReadyForQaEvent) {
				
			
				// bugzilla 2028 need additional information for qa events
				typeOfSamp = sampleItem.getTypeOfSample();
				sampleOrganization.setSampleId(sample.getId());
				sampleOrganizationDAO.getDataBySample(sampleOrganization);
				//bugzilla 2589
				String submitterNumber = "";
				if (sampleOrganization != null && sampleOrganization.getOrganization() != null) {
					submitterNumber = sampleOrganization.getOrganization()
					.getId();
				}

				//bugzilla 2227
				List allAnalysesForSample = analysisDAO
				.getMaxRevisionAnalysesBySample(sampleItem);
				
				// bugzilla 2028 get the possible qa events
				QaEvent qaEventForNoCollectionDate = new QaEvent();
				qaEventForNoCollectionDate.setQaEventName(SystemConfiguration
						.getInstance().getQaEventCodeForRequestNoCollectionDate());
				qaEventForNoCollectionDate = qaEventDAO
				.getQaEventByName(qaEventForNoCollectionDate);
				
				QaEvent qaEventForNoSampleType = new QaEvent();
				qaEventForNoSampleType.setQaEventName(SystemConfiguration
						.getInstance().getQaEventCodeForRequestNoSampleType());
				qaEventForNoSampleType = qaEventDAO
				.getQaEventByName(qaEventForNoSampleType);
				
				QaEvent qaEventForUnknownSubmitter = new QaEvent();
				qaEventForUnknownSubmitter.setQaEventName(SystemConfiguration
						.getInstance().getQaEventCodeForRequestUnknownSubmitter());
				qaEventForUnknownSubmitter = qaEventDAO
				.getQaEventByName(qaEventForUnknownSubmitter);
				// end bugzilla 2028
				
				// bugzilla 2028 get the possible qa event actions
				Action actionForNoCollectionDate = new Action();
				actionForNoCollectionDate.setCode(SystemConfiguration.getInstance()
						.getQaEventActionCodeForRequestNoCollectionDate());
				actionForNoCollectionDate = actionDAO
				.getActionByCode(actionForNoCollectionDate);
				
				Action actionForNoSampleType = new Action();
				actionForNoSampleType.setCode(SystemConfiguration.getInstance()
						.getQaEventActionCodeForRequestNoSampleType());
				actionForNoSampleType = actionDAO
				.getActionByCode(actionForNoSampleType);
				
				Action actionForUnknownSubmitter = new Action();
				actionForUnknownSubmitter.setCode(SystemConfiguration.getInstance()
						.getQaEventActionCodeForRequestUnknownSubmitter());
				actionForUnknownSubmitter = actionDAO
				.getActionByCode(actionForUnknownSubmitter);
				// end bugzilla 2028
				
				for (int i = 0; i < allAnalysesForSample.size(); i++) {
					Analysis analysis = (Analysis) allAnalysesForSample.get(i);
					// bugzilla 2028 QA_EVENT COLLECTIONDATE
					if (sample.getCollectionDate() == null) {
						AnalysisQaEvent analysisQaEvent = new AnalysisQaEvent();
						analysisQaEvent.setAnalysis(analysis);
						analysisQaEvent.setQaEvent(qaEventForNoCollectionDate);
						analysisQaEvent = analysisQaEventDAO
						.getAnalysisQaEventByAnalysisAndQaEvent(analysisQaEvent);
						
						if (analysisQaEvent == null) {
							analysisQaEvent = new AnalysisQaEvent();
							analysisQaEvent.setAnalysis(analysis);
							analysisQaEvent.setQaEvent(qaEventForNoCollectionDate);
							analysisQaEvent.setCompletedDate(null);
							analysisQaEvent.setSysUserId(sysUserId);
							analysisQaEventDAO.insertData(analysisQaEvent);
						} else {
							if (analysisQaEvent.getCompletedDate() != null) {
								analysisQaEvent.setCompletedDate(null);
								analysisQaEvent.setSysUserId(sysUserId);
								analysisQaEventDAO.updateData(analysisQaEvent);
							}
							
						}
					} else {
						AnalysisQaEvent analysisQaEvent = new AnalysisQaEvent();
						analysisQaEvent.setAnalysis(analysis);
						analysisQaEvent.setQaEvent(qaEventForNoCollectionDate);
						analysisQaEvent = analysisQaEventDAO
						.getAnalysisQaEventByAnalysisAndQaEvent(analysisQaEvent);
						
						// if we don't find a record in ANALYSIS_QAEVENT (or
						// completed date is not null) then this is already
						// fixed
						if (analysisQaEvent != null
								&& analysisQaEvent.getCompletedDate() == null) {
							AnalysisQaEventAction analysisQaEventAction = new AnalysisQaEventAction();
							analysisQaEventAction
							.setAnalysisQaEvent(analysisQaEvent);
							analysisQaEventAction
							.setAction(actionForNoCollectionDate);
							analysisQaEventAction = analysisQaEventActionDAO
							.getAnalysisQaEventActionByAnalysisQaEventAndAction(analysisQaEventAction);
							
							// if we found a record in ANALYSIS_QAEVENT_ACTION
							// then this has been fixed
							if (analysisQaEventAction == null) {
								// insert a record in ANALYSIS_QAEVENT_ACTION
								AnalysisQaEventAction analQaEventAction = new AnalysisQaEventAction();
								analQaEventAction
								.setAnalysisQaEvent(analysisQaEvent);
								analQaEventAction
								.setAction(actionForNoCollectionDate);
								analQaEventAction
								.setCreatedDateForDisplay(dateAsText);
								analQaEventAction.setSysUserId(sysUserId);
								//bugzilla 2496
								analQaEventAction.setSystemUser(systemUser);
								analysisQaEventActionDAO
								.insertData(analQaEventAction);
							}
							// update the found
							// ANALYSIS_QAEVENT.COMPLETED_DATE with current
							// date stamp
							analysisQaEvent
							.setCompletedDateForDisplay(dateAsText);
							analysisQaEvent.setSysUserId(sysUserId);
							analysisQaEventDAO.updateData(analysisQaEvent);
						}
						
						
					}
					
					// bugzilla 2028 QA_EVENT SAMPLETYPE
					if (typeOfSamp.getDescription().equals(SAMPLE_TYPE_NOT_GIVEN)) {
						AnalysisQaEvent analysisQaEvent = new AnalysisQaEvent();
						analysisQaEvent.setAnalysis(analysis);
						analysisQaEvent.setQaEvent(qaEventForNoSampleType);
						analysisQaEvent = analysisQaEventDAO
						.getAnalysisQaEventByAnalysisAndQaEvent(analysisQaEvent);
						
						if (analysisQaEvent == null) {
							analysisQaEvent = new AnalysisQaEvent();
							analysisQaEvent.setAnalysis(analysis);
							analysisQaEvent.setQaEvent(qaEventForNoSampleType);
							analysisQaEvent.setCompletedDate(null);
							analysisQaEvent.setSysUserId(sysUserId);
							analysisQaEventDAO.insertData(analysisQaEvent);
						} else {
							if (analysisQaEvent.getCompletedDate() != null) {
								analysisQaEvent.setCompletedDate(null);
								analysisQaEvent.setSysUserId(sysUserId);
								analysisQaEventDAO.updateData(analysisQaEvent);
							}
							
						}
					} else {
						AnalysisQaEvent analysisQaEvent = new AnalysisQaEvent();
						analysisQaEvent.setAnalysis(analysis);
						analysisQaEvent.setQaEvent(qaEventForNoSampleType);
						analysisQaEvent = analysisQaEventDAO
						.getAnalysisQaEventByAnalysisAndQaEvent(analysisQaEvent);
						
						// if we don't find a record in ANALYSIS_QAEVENT (or
						// completed date is not null) then this is already
						// fixed
						if (analysisQaEvent != null
								&& analysisQaEvent.getCompletedDate() == null) {
							AnalysisQaEventAction analysisQaEventAction = new AnalysisQaEventAction();
							analysisQaEventAction
							.setAnalysisQaEvent(analysisQaEvent);
							analysisQaEventAction
							.setAction(actionForNoSampleType);
							analysisQaEventAction = analysisQaEventActionDAO
							.getAnalysisQaEventActionByAnalysisQaEventAndAction(analysisQaEventAction);
							
							// if we found a record in ANALYSIS_QAEVENT_ACTION
							// then this has been fixed
							if (analysisQaEventAction == null) {
								// insert a record in ANALYSIS_QAEVENT_ACTION
								AnalysisQaEventAction analQaEventAction = new AnalysisQaEventAction();
								analQaEventAction
								.setAnalysisQaEvent(analysisQaEvent);
								analQaEventAction
								.setAction(actionForNoSampleType);
								analQaEventAction
								.setCreatedDateForDisplay(dateAsText);
								analQaEventAction.setSysUserId(sysUserId);
								//bugzilla 2496
								analQaEventAction.setSystemUser(systemUser);
								analysisQaEventActionDAO
								.insertData(analQaEventAction);
							}
							// update the found
							// ANALYSIS_QAEVENT.COMPLETED_DATE with current
							// date stamp
							analysisQaEvent
							.setCompletedDateForDisplay(dateAsText);
							analysisQaEvent.setSysUserId(sysUserId);
							analysisQaEventDAO.updateData(analysisQaEvent);
						}
						
					}
					
					// bugzilla 2028 QA_EVENT UNKNOWN SUBMITTER
					//bugzilla 2589
					if (submitterNumber.equals(SystemConfiguration.getInstance()
							.getUnknownSubmitterNumberForQaEvent())) {
						AnalysisQaEvent analysisQaEvent = new AnalysisQaEvent();
						analysisQaEvent.setAnalysis(analysis);
						analysisQaEvent.setQaEvent(qaEventForUnknownSubmitter);
						analysisQaEvent = analysisQaEventDAO
						.getAnalysisQaEventByAnalysisAndQaEvent(analysisQaEvent);
						
						if (analysisQaEvent == null) {
							analysisQaEvent = new AnalysisQaEvent();
							analysisQaEvent.setAnalysis(analysis);
							analysisQaEvent.setQaEvent(qaEventForUnknownSubmitter);
							analysisQaEvent.setCompletedDate(null);
							analysisQaEvent.setSysUserId(sysUserId);
							analysisQaEventDAO.insertData(analysisQaEvent);
						} else {
							if (analysisQaEvent.getCompletedDate() != null) {
								analysisQaEvent.setCompletedDate(null);
								analysisQaEvent.setSysUserId(sysUserId);
								analysisQaEventDAO.updateData(analysisQaEvent);
							}
							
						}
					} else {
						AnalysisQaEvent analysisQaEvent = new AnalysisQaEvent();
						analysisQaEvent.setAnalysis(analysis);
						analysisQaEvent.setQaEvent(qaEventForUnknownSubmitter);
						analysisQaEvent = analysisQaEventDAO
						.getAnalysisQaEventByAnalysisAndQaEvent(analysisQaEvent);
						
						// if we don't find a record in ANALYSIS_QAEVENT (or
						// completed date is not null) then this is already
						// fixed
						if (analysisQaEvent != null
								&& analysisQaEvent.getCompletedDate() == null) {
							AnalysisQaEventAction analysisQaEventAction = new AnalysisQaEventAction();
							analysisQaEventAction
							.setAnalysisQaEvent(analysisQaEvent);
							analysisQaEventAction
							.setAction(actionForUnknownSubmitter);
							analysisQaEventAction = analysisQaEventActionDAO
							.getAnalysisQaEventActionByAnalysisQaEventAndAction(analysisQaEventAction);
							
							// if we found a record in ANALYSIS_QAEVENT_ACTION
							// then this has been fixed
							if (analysisQaEventAction == null) {
								// insert a record in ANALYSIS_QAEVENT_ACTION
								AnalysisQaEventAction analQaEventAction = new AnalysisQaEventAction();
								analQaEventAction
								.setAnalysisQaEvent(analysisQaEvent);
								analQaEventAction
								.setAction(actionForUnknownSubmitter);
								analQaEventAction
								.setCreatedDateForDisplay(dateAsText);
								analQaEventAction.setSysUserId(sysUserId);
								//bugzilla 2496
								analQaEventAction.setSystemUser(systemUser);
								analysisQaEventActionDAO
								.insertData(analQaEventAction);
							}
							// update the found
							// ANALYSIS_QAEVENT.COMPLETED_DATE with current
							// date stamp
							analysisQaEvent
							.setCompletedDateForDisplay(dateAsText);
							analysisQaEvent.setSysUserId(sysUserId);
							analysisQaEventDAO.updateData(analysisQaEvent);
						}
					}
					
				}
			}

			tx.commit();
			// done updating return to menu
			forward = FWD_CLOSE;

		} catch (LIMSRuntimeException lre) {
    		//bugzilla 2154
			LogEvent.logError("SampleDemographicsUpdateAction","performAction()",lre.toString());
			tx.rollback();
			// if error then forward to fail and don't update to blank page
			// = false
			errors = new ActionMessages();
			ActionError error = null;
			if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
				// how can I get popup instead of struts error at the top of
				// page?
				// ActionMessages errors = testManagementForm.validate(mapping,
				// request);
				error = new ActionError("errors.OptimisticLockException", null,
						null);
			} else {
				error = new ActionError("errors.UpdateException", null, null);
			}
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute(ALLOW_EDITS_KEY, "false");
			forward = FWD_FAIL;

		} finally {
			HibernateUtil.closeSession();
		}
		if (forward.equals(FWD_FAIL))
			return mapping.findForward(FWD_FAIL);

		if ("true".equalsIgnoreCase(request.getParameter("close"))) {
			forward = FWD_CLOSE;
		}

		if (sample.getId() != null && !sample.getId().equals("0")) {
			request.setAttribute(ID, sample.getId());

		}

		if (forward.equals(FWD_SUCCESS)) {
			request.setAttribute("menuDefinition", "default");
		}

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		return "testmanagement.sampledemographics.title";
	}

	protected String getPageSubtitleKey() {
		return "testmanagement.sampledemographics.subtitle";
	}

    //bugzilla 1765 changes to city state zip validation
	protected ActionMessages validateAll(HttpServletRequest request,
			ActionMessages errors, BaseActionForm dynaForm) throws Exception {

		String result;
		String messageKey;

	    //bugzilla 1978: changed to use Basic Provider which allows active and inactive projs
		BasicProjectIdOrNameValidationProvider projIdOrNameValidator = new BasicProjectIdOrNameValidationProvider();

		String projNum = (String) dynaForm.get("projectIdOrName");
		if (!StringUtil.isNullorNill(projNum)) {
			// project ID validation against database (reusing ajax
			// validation logic)
			result = projIdOrNameValidator.validate((String) dynaForm
					.get("projectIdOrName"));
			messageKey = "humansampleone.projectNumber";
			if (result.startsWith("valid")) {

				result = projIdOrNameValidator.validate((String) dynaForm
						.get("projectNameOrId"));
				if (result.equals("invalid")) {
					ActionError error = new ActionError("errors.invalid",
							getMessageForKey(messageKey), null);
					errors.add(ActionMessages.GLOBAL_MESSAGE, error);
				}
			} else {
				ActionError error = new ActionError("errors.invalid",
						getMessageForKey(messageKey), null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			}
		}

		String proj2Num = (String) dynaForm.get("project2IdOrName");
		if (!StringUtil.isNullorNill(proj2Num)) {
			result = projIdOrNameValidator.validate((String) dynaForm
					.get("project2IdOrName"));
			messageKey = "humansampleone.project2Number";
			if (result.startsWith("valid")) {

				result = projIdOrNameValidator.validate((String) dynaForm
						.get("project2NameOrId"));
				if (result.equals("invalid")) {
					ActionError error = new ActionError("errors.invalid",
							getMessageForKey(messageKey), null);
					errors.add(ActionMessages.GLOBAL_MESSAGE, error);
				}
			} else {
				ActionError error = new ActionError("errors.invalid",
						getMessageForKey(messageKey), null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);

			}
		}

		// organization ID (submitter) validation against database (reusing ajax
		// validation logic)
		//bugzilla 2069
		//bugzilla 2531
		OrganizationLocalAbbreviationValidationProvider organizationLocalAbbreviationValidator = new OrganizationLocalAbbreviationValidationProvider();
		result = organizationLocalAbbreviationValidator.validate((String) dynaForm
				.get("organizationLocalAbbreviation"),null);
		messageKey = "humansampleone.provider.organization.localAbbreviation";
		if (result.equals("invalid")) {
			ActionError error = new ActionError("errors.invalid",
					getMessageForKey(messageKey), null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}

		boolean cityValid = true;
		boolean zipValid = true;
		boolean stateValid = true;
		// state validation against database (reusing ajax validation logic
		StateValidationProvider stateValidator = new StateValidationProvider();
		result = stateValidator.validate((String) dynaForm.get("state"));
		messageKey = "person.state";
		if (result.equals("invalid")) {
			stateValid = false;
			ActionError error = new ActionError("errors.invalid",
					getMessageForKey(messageKey), null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}


		// city validation against database (reusing ajax validation logic
		CityValidationProvider cityValidator = new CityValidationProvider();
		result = cityValidator.validate((String) dynaForm.get("city"));
		messageKey = "person.city";
		if (result.equals("invalid")) {
			cityValid = false;
			ActionError error = new ActionError("errors.invalid",
					getMessageForKey(messageKey), null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}

		// zip validation against database (reusing ajax validation logic
		ZipValidationProvider zipValidator = new ZipValidationProvider();
		result = zipValidator.validate((String) dynaForm.get("zipCode"));
		messageKey = "person.zipCode";
		if (result.equals("invalid")) {
			zipValid = false;
			ActionError error = new ActionError("errors.invalid",
					getMessageForKey(messageKey), null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}

		if (cityValid && stateValid && zipValid) {
			String messageKey1 = "person.city";
			String messageKey2 = "person.zipCode";
			String messageKey3 = "person.state";
			// city validation against database (reusing ajax validation logic
			CityStateZipComboValidationProvider cityStateZipComboValidator = new CityStateZipComboValidationProvider();
			result = cityStateZipComboValidator.validate((String) dynaForm
					.get("city"), (String) dynaForm.get("state"),
					(String) dynaForm.get("zipCode"));
			// combination is invalid if result is invalid
			if ("invalid".equals(result)) {
				ActionError error = new ActionError("errors.combo.3.invalid",
						getMessageForKey(messageKey1),
						getMessageForKey(messageKey2), 
						getMessageForKey(messageKey3), null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			}
		}
		// sample type validation against database (reusing ajax validation
		// logic

		// do this "invalid" only when filled in. otherwise, "required" error
		String typeOfSample = (String) dynaForm.get("typeOfSampleDesc");
		if (!StringUtil.isNullorNill(typeOfSample)) {
			HumanSampleTypeValidationProvider typeValidator = new HumanSampleTypeValidationProvider();
			result = typeValidator.validate((String) dynaForm
					.get("typeOfSampleDesc"));
			messageKey = "sampleitem.typeOfSample";
			if (result.equals("invalid")) {
				ActionError error = new ActionError("errors.invalid",
						getMessageForKey(messageKey), null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			}
		} else {

			ActionError error = new ActionError("errors.required",
					getMessageForKey(messageKey), null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);

		}

		// sample source validation against database (reusing ajax validation
		// logic
		HumanSampleSourceValidationProvider sourceValidator = new HumanSampleSourceValidationProvider();
		result = sourceValidator.validate((String) dynaForm
				.get("sourceOfSampleDesc"));
		messageKey = "sampleitem.sourceOfSample";
		if (result.equals("invalid")) {
			ActionError error = new ActionError("errors.invalid",
					getMessageForKey(messageKey), null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}

		return errors;
	}

}