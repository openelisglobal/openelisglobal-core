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
package us.mn.state.health.lims.sample.action;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.ActionRedirect;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.citystatezip.dao.CityStateZipDAO;
import us.mn.state.health.lims.citystatezip.daoimpl.CityStateZipDAOImpl;
import us.mn.state.health.lims.citystatezip.valueholder.CityStateZip;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.provider.validation.CityStateZipComboValidationProvider;
import us.mn.state.health.lims.common.provider.validation.CityValidationProvider;
import us.mn.state.health.lims.common.provider.validation.OrganizationLocalAbbreviationValidationProvider;
import us.mn.state.health.lims.common.provider.validation.ProjectIdOrNameValidationProvider;
import us.mn.state.health.lims.common.provider.validation.StateValidationProvider;
import us.mn.state.health.lims.common.provider.validation.ZipValidationProvider;
import us.mn.state.health.lims.common.services.StatusService;
import us.mn.state.health.lims.common.services.StatusService.SampleStatus;
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
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.sampledomain.dao.SampleDomainDAO;
import us.mn.state.health.lims.sampledomain.daoimpl.SampleDomainDAOImpl;
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
import us.mn.state.health.lims.systemuser.dao.SystemUserDAO;
import us.mn.state.health.lims.systemuser.daoimpl.SystemUserDAOImpl;
import us.mn.state.health.lims.systemuser.valueholder.SystemUser;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.typeofsample.dao.TypeOfSampleDAO;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleDAOImpl;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class HumanSampleOneUpdateAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new Sample.
		// If there is a parameter present, we should bring up an existing
		// Sample to edit.
		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");

		String id = request.getParameter(ID);

		if (StringUtil.isNullorNill(id) || "0".equals(id)) {
			isNew = true;
		} else {
			isNew = false;
		}

		BaseActionForm dynaForm = (BaseActionForm) form;

		// server-side validation (validation.xml)
		ActionMessages errors = dynaForm.validate(mapping, request);

		// validate on server-side patient city/zip combination
		/*
		 * String city = (String) dynaForm.get("city"); String zipCode =
		 * (String) dynaForm.get("zipCode"); if (!StringUtil.isNullorNill(city) &&
		 * !StringUtil.isNullorNill(zipCode)) { try { errors =
		 * validateZipCity(errors, zipCode, city); } catch (Exception e) {
		 * ActionError error = new ActionError( "errors.ValidationException",
		 * null, null); errors.add(ActionMessages.GLOBAL_MESSAGE, error); } }
		 */
		try {
			errors = validateAll(request, errors, dynaForm);
		} catch (Exception e) {
    		//bugzilla 2154
			LogEvent.logError("HumanSampleOneUpdateAction","performAction()",e.toString());
			ActionError error = new ActionError("errors.ValidationException",
					null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		// end of zip/city combination check

		if (errors != null && errors.size() > 0) {
			// System.out.println("saveing errors " + errors.size());
			saveErrors(request, errors);
			// since we forward to jsp - not Action we don't need to repopulate
			// the lists here
			return mapping.findForward(FWD_FAIL);
		}

		String start = (String) request.getParameter("startingRecNo");

		Patient patient = new Patient();
		Person person = new Person();
		Provider provider = new Provider();
		Person providerPerson = new Person();
		Sample sample = new Sample();
		SampleHuman sampleHuman = new SampleHuman();
		SampleOrganization sampleOrganization = new SampleOrganization();
		List sampleProjects = new ArrayList();
		SampleItem sampleItem = new SampleItem();
		sampleItem.setStatusId(StatusService.getInstance().getStatusID(SampleStatus.Entered));
		// bugzilla 1926 get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());
		// String typeOfSampleId = (String) dynaForm.get("typeOfSampleId");
		String typeOfSample = (String) dynaForm.get("typeOfSampleDesc");
		// String sourceOfSampleId = (String) dynaForm.get("sourceOfSampleId");
		//bugzilla 2470, unused
		// String sourceOfSample = (String) dynaForm.get("sourceOfSampleDesc");

		List sysUsers = new ArrayList();
		List sampleDomains = new ArrayList();
		List typeOfSamples = new ArrayList();
		List sourceOfSamples = new ArrayList();

		if (dynaForm.get("sysUsers") != null) {
			sysUsers = (List) dynaForm.get("sysUsers");
		} else {
			SystemUserDAO sysUserDAO = new SystemUserDAOImpl();
			sysUsers = sysUserDAO.getAllSystemUsers();
		}

		if (dynaForm.get("sampleDomains") != null) {
			sampleDomains = (List) dynaForm.get("sampleDomains");
		} else {
			SampleDomainDAO sampleDomainDAO = new SampleDomainDAOImpl();
			sampleDomains = sampleDomainDAO.getAllSampleDomains();
		}
		if (dynaForm.get("typeOfSamples") != null) {
			typeOfSamples = (List) dynaForm.get("typeOfSamples");
		} else {
			TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
			typeOfSamples = typeOfSampleDAO.getAllTypeOfSamples();
		}
		if (dynaForm.get("sourceOfSamples") != null) {
			sourceOfSamples = (List) dynaForm.get("sourceOfSamples");
		} else {
			SourceOfSampleDAO sourceOfSampleDAO = new SourceOfSampleDAOImpl();
			sourceOfSamples = sourceOfSampleDAO.getAllSourceOfSamples();
		}
		String stringOfTestIds = (String) dynaForm.get("selectedTestIds");

		String projectIdOrName = (String) dynaForm.get("projectIdOrName");
		String project2IdOrName = (String) dynaForm.get("project2IdOrName");

		String projectNameOrId = (String) dynaForm.get("projectNameOrId");
		String project2NameOrId = (String) dynaForm.get("project2NameOrId");

		// get the numeric projectId either from projectIdOrName or from
		// projectNameOrId
		String projectId = null;
		String project2Id = null;
		//bugzilla 2318 
		if (!StringUtil.isNullorNill(projectIdOrName) && !StringUtil.isNullorNill(projectNameOrId)) {
			try {
				Integer i = Integer.valueOf(projectIdOrName);
				projectId = projectIdOrName;
				//bugzilla 2154
				LogEvent.logDebug("HumanSampleOneUpdateAction","performAction()","Parsed integer value of projectIdOrName: " + projectIdOrName + " projectNameOrId: " + projectNameOrId);
			} catch (NumberFormatException nfe) {
				projectId = projectNameOrId;
				//bugzilla 2154
			    LogEvent.logError("HumanSampleOneUpdateAction","performAction()","Error parsing integer value of projectIdOrName: " + projectIdOrName + " " + nfe.toString());
			}

		}

		//bugzilla 2318
		if (!StringUtil.isNullorNill(project2IdOrName) && !StringUtil.isNullorNill(project2NameOrId)) {
			try {
				Integer i = Integer.valueOf(project2IdOrName);
				project2Id = project2IdOrName;
                //bugzilla 2154
				LogEvent.logDebug("HumanSampleOneUpdateAction","performAction()","Parsed integer value of project2IdOrName: " + project2IdOrName + " project2NameOrId: " + project2NameOrId);
			} catch (NumberFormatException nfe) {
				project2Id = project2NameOrId;
				//bugzilla 2154
			    LogEvent.logError("HumanSampleOneUpdateAction","performAction()","Error parsing integer value of project2IdOrName: " + project2IdOrName + " " + nfe.toString());
			}

		}

		String[] listOfTestIds = stringOfTestIds.split(SystemConfiguration
				.getInstance().getDefaultIdSeparator(), -1);

		List analyses = new ArrayList();
		for (int i = 0; i < listOfTestIds.length; i++) {
			if (!StringUtil.isNullorNill(listOfTestIds[i])) {
				Analysis analysis = new Analysis();

				Test test = new Test();
				String testId = (String) listOfTestIds[i];
				test.setId(testId);

				TestDAO testDAO = new TestDAOImpl();
				testDAO.getData(test);
				analysis.setTest(test);

				// TODO: need to populate this with actual data!!!
				analysis.setAnalysisType("TEST");
				analyses.add(analysis);
			}
		}

		SystemUser sysUser = null;

		for (int i = 0; i < sysUsers.size(); i++) {
			SystemUser su = (SystemUser) sysUsers.get(i);
			if (su.getId().equals(sysUserId)) {
				sysUser = su;
				break;
			}
		}

		TypeOfSample typeOfSamp = null;

		// get the right typeOfSamp to update sampleitem with
		for (int i = 0; i < typeOfSamples.size(); i++) {
			TypeOfSample s = (TypeOfSample) typeOfSamples.get(i);
			// if (s.getId().equals(typeOfSampleId)) {
			if (s.getDescription().equalsIgnoreCase(typeOfSample)) {
				typeOfSamp = s;
				break;
			}
		}

		// fixed in bugzilla 2470, unused
		//SourceOfSample sourceOfSamp = null;
/*		
		// get the right sourceOfSamp to update sampleitem with
		for (int i = 0; i < sourceOfSamples.size(); i++) {
			SourceOfSample s = (SourceOfSample) sourceOfSamples.get(i);
			// if (s.getId().equals(sourceOfSampleId)) {
			if (s.getDescription().equalsIgnoreCase(sourceOfSample)) {
				sourceOfSamp = s;
				break;
			}
		}
*/
		// populate valueholder from form
		PropertyUtils.copyProperties(sample, dynaForm);
		PropertyUtils.copyProperties(person, dynaForm);
		PropertyUtils.copyProperties(patient, dynaForm);
		PropertyUtils.copyProperties(provider, dynaForm);
		PropertyUtils.copyProperties(sampleHuman, dynaForm);
		PropertyUtils.copyProperties(sampleOrganization, dynaForm);
		PropertyUtils.copyProperties(sampleItem, dynaForm);

		Organization o = new Organization();
		//bugzilla 2069
		o.setOrganizationLocalAbbreviation((String) dynaForm.get("organizationLocalAbbreviation"));
		OrganizationDAO organizationDAO = new OrganizationDAOImpl();
		o = organizationDAO.getOrganizationByLocalAbbreviation(o, true);

		sampleOrganization.setOrganization(o);

		// if there was a first sampleProject id entered
		if (!StringUtil.isNullorNill(projectId)) {
			SampleProject sampleProject = new SampleProject();
			Project p = new Project();
			//bugzilla 2438
			p.setLocalAbbreviation(projectId);
			ProjectDAO projectDAO = new ProjectDAOImpl();
			p = projectDAO.getProjectByLocalAbbreviation(p, true);
			sampleProject.setProject(p);
			sampleProject.setIsPermanent(NO);
			sampleProjects.add(sampleProject);
		}

		// in case there was a second sampleProject id entered
		if (!StringUtil.isNullorNill(project2Id)) {
			SampleProject sampleProject2 = new SampleProject();
			Project p2 = new Project();
			//bugzilla 2438
			p2.setLocalAbbreviation(project2Id);
			ProjectDAO projectDAO = new ProjectDAOImpl();
			p2 = projectDAO.getProjectByLocalAbbreviation(p2, true);
			sampleProject2.setProject(p2);
			sampleProject2.setIsPermanent(NO);
			sampleProjects.add(sampleProject2);
		}

		// set the provider person manually as we have two Person valueholders
		// to populate and copyProperties() can only handle one per form
		providerPerson.setFirstName((String) dynaForm.get("providerFirstName"));
		providerPerson.setLastName((String) dynaForm.get("providerLastName"));

		// format workPhone for storage
		String workPhone = (String) dynaForm.get("providerWorkPhone");
		String ext = (String) dynaForm.get("providerWorkPhoneExtension");
		String formattedPhone = StringUtil.formatPhone(workPhone, ext);
		// phone is stored as 999/999-9999.9999
		// area code/phone - number.extension
		providerPerson.setWorkPhone(formattedPhone);
		//bugzilla 1701 blank out provider.externalId - this is copied from patient 
		//externalId which is not related...and we currently don't enter an externalId for
		//provider on this screen
        provider.setExternalId(BLANK);
		

		// set collection time
		String time = (String) dynaForm.get("collectionTimeForDisplay");

		if (StringUtil.isNullorNill(time)) {
			time = "00:00";
		}
		sample.setCollectionTimeForDisplay(time);
		// AIS - bugzilla 1408 - Start
		String accessionNumberOne = (String) dynaForm.get("accessionNumber");
		sample.setAccessionNumber(accessionNumberOne);
		SampleDAO sampleDAO = new SampleDAOImpl();
		sampleDAO.getSampleByAccessionNumber(sample);
		String stickerReceivedFlag = (String) dynaForm
				.get("stickerReceivedFlag");
		String referredCultureFlag = (String) dynaForm
				.get("referredCultureFlag");
		sample.setStickerReceivedFlag(stickerReceivedFlag);
		sample.setReferredCultureFlag(referredCultureFlag);
		String date = (String) dynaForm.get("collectionDateForDisplay");
		
		// bgm - bugzilla 1586 check for null collection date
		//db bugzilla 1765 - noticed that error occurs - need to check
		// also that date is not nill as well as not null
		if (!StringUtil.isNullorNill(date)) {
			sample.setCollectionDateForDisplay(date);
			// AIS - bugzilla 1408 - End

			Timestamp d = sample.getCollectionDate();
			//bugzilla 1857 deprecated date stuff
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			if (null != d)
				if (time.indexOf(":") > 0) {
					cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time.substring(0, 2)).intValue());
					//d.setHours(Integer.valueOf(time.substring(0, 2)).intValue());
					cal.set(Calendar.MINUTE, Integer.valueOf(time.substring(3, 5)).intValue());
					//d.setMinutes(Integer.valueOf(time.substring(3, 5)).intValue());
					d = new Timestamp(cal.getTimeInMillis());
					sample.setCollectionDate(d);
				}
		}
		// sampleItem
		sampleItem.setSortOrder("1");
		// set the typeOfSample
		sampleItem.setTypeOfSample(typeOfSamp);
		// sampleItem.setTypeOfSample(typeOfSample);
		// set the sourceOfSample
		// fixed in bugzilla 2470 unused
		//sampleItem.setSourceOfSample(sourceOfSamp);
		// sampleItem.setSourceOfSample(sourceOfSample);

		// set the system user
		sample.setSystemUser(sysUser);
		//bugzilla 2112
		sample.setSysUserId(sysUserId);
		//bugzilla 1926
		sampleItem.setSysUserId(sysUserId);
		sampleOrganization.setSysUserId(sysUserId);
		sampleHuman.setSysUserId(sysUserId);
		patient.setSysUserId(sysUserId);
		person.setSysUserId(sysUserId);
		provider.setSysUserId(sysUserId);
		providerPerson.setSysUserId(sysUserId);

		//bugzilla 2169 - undo bugzilla 1408 logic to copy externalId into sample client_reference if clientRef# is blank
		if (!StringUtil.isNullorNill((String) dynaForm.get("clientReference"))) {
		 sample.setClientReference((String) dynaForm.get("clientReference"));
		}
		

        //bugzilla 1761 (get status code and domain from SystemConfiguration)
        sample.setStatus(SystemConfiguration.getInstance().getSampleStatusEntry1Complete());
		sample.setDomain(SystemConfiguration.getInstance().getHumanDomain());
		sample.setSampleProjects(sampleProjects);

		org.hibernate.Transaction tx = HibernateUtil.getSession()
				.beginTransaction();

		try {

			// HumanSampleOneDAO humanSampleOneDAO = new
			// HumanSampleOneDAOImpl();
			PersonDAO personDAO = new PersonDAOImpl();
			PatientDAO patientDAO = new PatientDAOImpl();
			ProviderDAO providerDAO = new ProviderDAOImpl();

			// AIS - bugzilla 1408 ( already declared..)
			// SampleDAO sampleDAO = new SampleDAOImpl();

			SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
			SampleProjectDAO sampleProjectDAO = new SampleProjectDAOImpl();
			SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
			SampleOrganizationDAO sampleOrganizationDAO = new SampleOrganizationDAOImpl();
			AnalysisDAO analysisDAO = new AnalysisDAOImpl();

			// why does it only work if sample is the first insert (not when
			// person is first insert?)

			// AIS - bugzilla 1408
			// sampleDAO.insertData(sample);
			//bugzilla 2154
			LogEvent.logDebug("HumanSampleOneUpdateAction","performAction()","About to sampleDAO.updateData() status code: " + sample.getStatus());

			sampleDAO.updateData(sample);

			personDAO.insertData(person);
			patient.setPerson(person);
			patientDAO.insertData(patient);
			personDAO.insertData(providerPerson);
			provider.setPerson(providerPerson);
			providerDAO.insertData(provider);

			for (int i = 0; i < sampleProjects.size(); i++) {
				SampleProject sampleProject = (SampleProject) sampleProjects
						.get(i);
				sampleProject.setSample(sample);
				// sampleProject.setProject(prClone);
				
				//bugzilla 2112
				sampleProject.setSysUserId(sysUserId);
				sampleProjectDAO.insertData(sampleProject);
			}

			sampleHuman.setSampleId(sample.getId());
			sampleHuman.setPatientId(patient.getId());
			sampleHuman.setProviderId(provider.getId());
			sampleHumanDAO.insertData(sampleHuman);
			sampleOrganization.setSample(sample);
			sampleOrganization.setSample(sample);
			sampleOrganizationDAO.insertData(sampleOrganization);
    		//bugzilla 1773 need to store sample not sampleId for use in sorting
			sampleItem.setSample(sample);
			// AIS - bugzilla 1408
			// sampleItemDAO.insertData(sampleItem);

			//bugzilla 2113 
			SampleItem si = new SampleItem();
			si.setSample(sample);
			sampleItemDAO.getDataBySample(si);
			sampleItem.setId(si.getId());
		
			//bugzilla 2470
			sampleItem.setSourceOfSampleId(si.getSourceOfSampleId());
			sampleItem.setSourceOther(si.getSourceOther());
			
			sampleItem.setLastupdated(si.getLastupdated());
			sampleItemDAO.updateData(sampleItem);
			
			// Analysis table
			if (analyses != null) {
				for (int i = 0; i < analyses.size(); i++) {
					Analysis analysis = (Analysis) analyses.get(i);
					analysis.setSampleItem(sampleItem);
					//bugzilla 2064
					analysis.setRevision(SystemConfiguration.getInstance().getAnalysisDefaultRevision());
					//bugzilla 2013 added duplicateCheck parameter
					analysisDAO.insertData(analysis, false);
				}
			}

			// insert humanSampleOne
			// humanSampleOneDAO.insertData(patient, person, provider,
			// providerPerson, sample, sampleHuman, sampleOrganization,
			// sampleItem, analyses);
			tx.commit();
		} catch (LIMSRuntimeException lre) {
			//bugzilla 2154
		    LogEvent.logError("HumanSampleOneUpdateAction","performAction()",lre.toString());
			tx.rollback();
			errors = new ActionMessages();
			ActionError error = null;
			if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
				// how can I get popup instead of struts error at the top of
				// page?
				// ActionMessages errors = dynaForm.validate(mapping, request);
				error = new ActionError("errors.OptimisticLockException", null,
						null);
			} else {
				//lre.printStackTrace();
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
			return mapping.findForward(forward);

		// initialize the form
		dynaForm.initialize(mapping);
		// repopulate the form from valueholder
		PropertyUtils.copyProperties(dynaForm, sample);

		// PropertyUtils.setProperty(dynaForm, "parentSamples", samps);
		PropertyUtils.setProperty(dynaForm, "sysUsers", sysUsers);
		PropertyUtils.setProperty(dynaForm, "sampleDomains", sampleDomains);

		if ("true".equalsIgnoreCase(request.getParameter("close"))) {
			forward = FWD_CLOSE;
		}

		if (sample.getId() != null && !sample.getId().equals("0")) {
			request.setAttribute(ID, sample.getId());

		}

		if (forward.equals(FWD_SUCCESS)) {
			request.setAttribute("menuDefinition", "default");
		}

		// TODO: temporary code to forward with accessionNumber (remove the
		// overriding getForward in this class
		String accessionNumber = sample.getAccessionNumber();
		// return getForward(mapping.findForward(forward), id, start);
		return getForward(mapping.findForward(forward), id, start,
				accessionNumber);

	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "human.sample.one.add.title";
		} else {
			return "human.sample.one.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "human.sample.one.add.title";
		} else {
			return "human.sample.one.edit.title";
		}
	}

	protected ActionMessages validateZipCity(ActionMessages errors,
			String zipCode, String city) throws Exception {

		// bugzilla 1545
		CityStateZipDAO cityStateZipDAO = new CityStateZipDAOImpl();
		CityStateZip cityStateZip = new CityStateZip();

		// use 5-digit zipcode for validation
		String zc5Dig = null;
		zc5Dig = zipCode.substring(0, 5);
		cityStateZip.setZipCode(zc5Dig);
		cityStateZip.setCity(city);

		cityStateZip = cityStateZipDAO
				.getCityStateZipByCityAndZipCode(cityStateZip);

		if (cityStateZip == null) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionError(
					"humansampleone.validation.zipCity", null, null));
		}

		return errors;
	}

    //bugzilla 1765  changes for city/state/zip validation
	protected ActionMessages validateAll(HttpServletRequest request,
			ActionMessages errors, BaseActionForm dynaForm) throws Exception {

		String result;
		String messageKey;
		//bugzilla 2054 (missing validation)
		errors = validateAccessionNumber(request, errors, dynaForm);

		ProjectIdOrNameValidationProvider projIdValidator = new ProjectIdOrNameValidationProvider();

		String projNum = (String) dynaForm.get("projectIdOrName");
		if (!StringUtil.isNullorNill(projNum)) {
			// project ID validation against database (reusing ajax
			// validation logic)
			result = projIdValidator.validate((String) dynaForm
					.get("projectIdOrName"));
			messageKey = "humansampleone.projectNumber";
			if (result.startsWith("valid")) {

				result = projIdValidator.validate((String) dynaForm
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
			result = projIdValidator.validate((String) dynaForm
					.get("project2IdOrName"));
			messageKey = "humansampleone.project2Number";
			if (result.startsWith("valid")) {

				result = projIdValidator.validate((String) dynaForm
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
		OrganizationLocalAbbreviationValidationProvider orgLocalAbbreviationValidator = new OrganizationLocalAbbreviationValidationProvider();
		result = orgLocalAbbreviationValidator.validate((String) dynaForm
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

		String messageKey1 = "person.city";
		String messageKey2 = "person.zipCode";
		String messageKey3 = "person.state";
		// city validation against database (reusing ajax validation logic
		CityValidationProvider cityValidator = new CityValidationProvider();
		result = cityValidator.validate((String) dynaForm.get("city"));
		if (result.equals("invalid")) {
			cityValid = false;
			ActionError error = new ActionError("errors.invalid",
					getMessageForKey(messageKey1), null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}

		// zip validation against database (reusing ajax validation logic
		ZipValidationProvider zipValidator = new ZipValidationProvider();
		result = zipValidator.validate((String) dynaForm.get("zipCode"));
		if (result.equals("invalid")) {
			zipValid = false;
			ActionError error = new ActionError("errors.invalid",
					getMessageForKey(messageKey2), null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}

		if (cityValid && stateValid && zipValid) {
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
		return errors;
	}

	protected ActionForward getForward(ActionForward forward, String id,
			String startingRecNo, String accessionNumber) {
		ActionRedirect redirect = new ActionRedirect(forward);
		// System.out.println("This is forward " + forward.getRedirect() + " "
		// + forward.getPath());

		if (id != null)
			redirect.addParameter(ID, id);
		if (startingRecNo != null)
			redirect.addParameter("startingRecNo", startingRecNo);
		if (accessionNumber != null)
			redirect.addParameter("accessionNumber", accessionNumber);
		// System.out.println("This is redirect " + redirect.getPath());
		return redirect;
	}
}