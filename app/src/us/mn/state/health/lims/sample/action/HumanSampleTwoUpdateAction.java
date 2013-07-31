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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.analysisqaevent.dao.AnalysisQaEventDAO;
import us.mn.state.health.lims.analysisqaevent.daoimpl.AnalysisQaEventDAOImpl;
import us.mn.state.health.lims.analysisqaevent.valueholder.AnalysisQaEvent;
import us.mn.state.health.lims.citystatezip.dao.CityStateZipDAO;
import us.mn.state.health.lims.citystatezip.daoimpl.CityStateZipDAOImpl;
import us.mn.state.health.lims.citystatezip.valueholder.CityStateZip;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.provider.validation.CityStateZipComboValidationProvider;
import us.mn.state.health.lims.common.provider.validation.CityValidationProvider;
import us.mn.state.health.lims.common.provider.validation.HumanSampleSourceValidationProvider;
import us.mn.state.health.lims.common.provider.validation.HumanSampleTypeValidationProvider;
import us.mn.state.health.lims.common.provider.validation.OrganizationLocalAbbreviationValidationProvider;
import us.mn.state.health.lims.common.provider.validation.ProjectIdOrNameValidationProvider;
import us.mn.state.health.lims.common.provider.validation.StateValidationProvider;
import us.mn.state.health.lims.common.provider.validation.ZipValidationProvider;
import us.mn.state.health.lims.common.services.StatusService;
import us.mn.state.health.lims.common.services.StatusService.SampleStatus;
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
public class HumanSampleTwoUpdateAction extends BaseAction {

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

		// first get the accessionNumber and whether we are on blank page or not
		String accessionNumber = (String) dynaForm.get("accessionNumber");
		String blankscreen = (String) dynaForm.get("blankscreen");

		String start = (String) request.getParameter("startingRecNo");

		String typeOfSample = (String) dynaForm.get("typeOfSampleDesc");
		//bugzilla 2470, unused
		//String sourceOfSample = (String) dynaForm.get("sourceOfSampleDesc");

		List typeOfSamples = new ArrayList();
		List sourceOfSamples = new ArrayList();

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

		HashMap humanSampleOneMap = new HashMap();
		if (dynaForm.get("humanSampleOneMap") != null) {
			humanSampleOneMap = (HashMap) dynaForm.get("humanSampleOneMap");
		}

		String projectIdOrName = (String) dynaForm.get("projectIdOrName");
		String project2IdOrName = (String) dynaForm.get("project2IdOrName");

		String projectNameOrId = (String) dynaForm.get("projectNameOrId");
		String project2NameOrId = (String) dynaForm.get("project2NameOrId");

		String projectId = null;
		String project2Id = null;
		if (projectIdOrName != null && projectNameOrId != null) {
			try {
				Integer i = Integer.valueOf(projectIdOrName);
				projectId = projectIdOrName;
				
			} catch (NumberFormatException nfe) {
    			//bugzilla 2154
			    LogEvent.logError("HumanSampleTwoUpdateAction","performAction()",nfe.toString());
				projectId = projectNameOrId;
			}

		}

		if (project2IdOrName != null && project2NameOrId != null) {
			try {
				Integer i = Integer.valueOf(project2IdOrName);
				project2Id = project2IdOrName;				

			} catch (NumberFormatException nfe) {
    			//bugzilla 2154
			    LogEvent.logError("HumanSampleTwoUpdateAction","performAction()",nfe.toString());
				project2Id = project2NameOrId;				
			}

		}
		
		//bugzilla 2028
		//bugzilla 2069
		String submitterNumber = (String) dynaForm.get("organizationLocalAbbreviation");

		// set current date for validation of dates
		Date today = Calendar.getInstance().getTime();
		Locale locale = (Locale) request.getSession().getAttribute(
				"org.apache.struts.action.LOCALE");
		String dateAsText = DateUtil.formatDateAsText(today, locale);

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
		QaEventDAO qaEventDAO = new QaEventDAOImpl();

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
		sampleItem.setStatusId(StatusService.getInstance().getStatusID(SampleStatus.Entered));
		// TODO need to populate this with tests entered in HSE I
		List analyses = new ArrayList();

		ActionMessages errors = null;

		// validate on server-side sample accession number

		try {
			errors = new ActionMessages();
			errors = validateAccessionNumber(request, errors, dynaForm);
			// System.out.println("Just validated accessionNumber");
		} catch (Exception e) {
    		//bugzilla 2154
			LogEvent.logError("HumanSampleTwoUpdateAction","performAction()",e.toString());
			ActionError error = new ActionError("errors.ValidationException",
					null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		// System.out.println("This is errors after validation of accn Number "
		// + errors);
		if (errors != null && errors.size() > 0) {
			saveErrors(request, errors);
			// initialize the form but retain the invalid accessionNumber
			dynaForm.initialize(mapping);
			dynaForm.set("accessionNumber", accessionNumber);

			// repopulate lists
			PropertyUtils.setProperty(dynaForm, "typeOfSamples", typeOfSamples);
			PropertyUtils.setProperty(dynaForm, "sourceOfSamples",
					sourceOfSamples);
			PropertyUtils.setProperty(dynaForm, "blankscreen", "true");
			request.setAttribute(ALLOW_EDITS_KEY, "false");

			return mapping.findForward(FWD_FAIL);
		}
		// System.out.println("Now try to get data for accession number ");
		errors = dynaForm.validate(mapping, request);

		try {
			errors = validateAll(request, errors, dynaForm, humanSampleOneMap);
		} catch (Exception e) {
    		//bugzilla 2154
			LogEvent.logError("HumanSampleTwoUpdateAction","performAction()",e.toString());			 
			ActionError error = new ActionError("errors.ValidationException",
					null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		// end of zip/city combination check

		if (errors != null && errors.size() > 0) {
			saveErrors(request, errors);
			// since we forward to jsp - not Action we don't need to
			// repopulate
			// the lists here
			return mapping.findForward(FWD_FAIL);
		}


		// GET ORIGINAL DATA
		try {

			sample.setAccessionNumber(accessionNumber);
			sampleDAO.getSampleByAccessionNumber(sample);
			if (!StringUtil.isNullorNill(sample.getId())) {
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

				oldSampleProjects = sample.getSampleProjects();

			}

		} catch (LIMSRuntimeException lre) {
			// if error then forward to fail and don't update to blank page
			// = false
            //bugzilla 2154
			LogEvent.logError("HumanSampleTwoUpdateAction","performAction()",lre.toString());
			errors = new ActionMessages();
			ActionError error = null;
			error = new ActionError("errors.GetException", null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute(ALLOW_EDITS_KEY, "false");
			return mapping.findForward(FWD_FAIL);

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
		// System.out.println("This is entered date before update from form
		// "
		// + sample.getEnteredDate()
		// + sample.getEnteredDateForDisplay());

		// UPDATE DATA FROM FORM
		// populate valueholder from form
		PropertyUtils.copyProperties(sample, dynaForm);
		PropertyUtils.copyProperties(person, dynaForm);
		PropertyUtils.copyProperties(patient, dynaForm);
		PropertyUtils.copyProperties(provider, dynaForm);
		PropertyUtils.copyProperties(sampleHuman, dynaForm);
		PropertyUtils.copyProperties(sampleOrganization, dynaForm);
		PropertyUtils.copyProperties(sampleItem, dynaForm);

		Organization org = new Organization();
		org.setOrganizationLocalAbbreviation((String) dynaForm.get("organizationLocalAbbreviation"));
		OrganizationDAO organizationDAO = new OrganizationDAOImpl();
		org = organizationDAO.getOrganizationByLocalAbbreviation(org, true);
		sampleOrganization.setOrganization(org);

		// if there was a first sampleProject id entered
		// if there was a first sampleProject id entered
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

		// in case there was a second sampleProject id entered
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

		// set the provider person manually as we have two Person
		// valueholders
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

		Timestamp d = sample.getCollectionDate();
		//bgm - bugzilla 1586 check for null date
		if(null != d){
		if (time.indexOf(":") > 0) {
			//bugzilla 1857 deprecated stuff
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time.substring(0, 2)).intValue());
			cal.set(Calendar.MINUTE, Integer.valueOf(time.substring(3, 5)).intValue());
			//d.setHours(Integer.valueOf(time.substring(0, 2)).intValue());
			//d.setMinutes(Integer.valueOf(time.substring(3, 5)).intValue());
            d = new Timestamp(cal.getTimeInMillis());
			sample.setCollectionDate(d);
		}
		}


		// sampleItem
		sampleItem.setSortOrder("1");
		// set the typeOfSample
		sampleItem.setTypeOfSample(typeOfSamp);
		// set the sourceOfSample
		// fixed in bugzilla 2470 unused 
		//sampleItem.setSourceOfSample(sourceOfSamp);

   		sample.setSampleProjects(newSampleProjects);
		// get entered by through system (when we have login functionality)
		// removed per Christina 3/03/2006
		// sample.setEnteredBy("diane");

		//get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());			
		
		org.hibernate.Transaction tx = HibernateUtil.getSession().beginTransaction();

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
							"lastupdated", (Timestamp) dynaForm
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
							"lastupdated", (Timestamp) dynaForm
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
					(Timestamp) dynaForm.get("personLastupdated"));
			PropertyUtils.setProperty(patient, "lastupdated",
					(Timestamp) dynaForm.get("patientLastupdated"));
			PropertyUtils.setProperty(sample, "lastupdated",
					(Timestamp) dynaForm.get("lastupdated"));
			PropertyUtils.setProperty(providerPerson, "lastupdated",
					(Timestamp) dynaForm.get("providerPersonLastupdated"));
			PropertyUtils.setProperty(provider, "lastupdated",
					(Timestamp) dynaForm.get("providerLastupdated"));
			PropertyUtils.setProperty(sampleItem, "lastupdated",
					(Timestamp) dynaForm.get("sampleItemLastupdated"));
			PropertyUtils.setProperty(sampleHuman, "lastupdated",
					(Timestamp) dynaForm.get("sampleHumanLastupdated"));
			PropertyUtils.setProperty(sampleOrganization, "lastupdated",
					(Timestamp) dynaForm.get("sampleOrganizationLastupdated"));

			//System.out.println("This is person ts " + person.getLastupdated().toLocaleString());
			
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
			//System.out.println("This is patient ts " 	+ patient.getLastupdated().toLocaleString());

			patientDAO.updateData(patient);
			personDAO.updateData(providerPerson);
			provider.setPerson(providerPerson);
			providerDAO.updateData(provider);

			sampleHuman.setSampleId(sample.getId());
			sampleHuman.setPatientId(patient.getId());
			sampleHuman.setProviderId(provider.getId());
			sampleHumanDAO.updateData(sampleHuman);
			sampleOrganization.setSampleId(sample.getId());
			sampleOrganization.setSample(sample);
			sampleOrganizationDAO.updateData(sampleOrganization);
			
			//bugzilla 2470 
			SampleItem si = new SampleItem();
			si.setSample(sample);
			sampleItemDAO.getDataBySample(si);
			sampleItem.setId(si.getId());
			sampleItem.setSourceOfSampleId(si.getSourceOfSampleId());
			sampleItem.setSourceOther(si.getSourceOther());
			
			//bugzilla 1773 need to store sample not sampleId for use in sorting
			sampleItem.setSample(sample);
			sampleItemDAO.updateData(sampleItem);

			boolean allAnalysesReleased = true;
			
			//bugzilla 2028 get the possible qa events
			QaEvent qaEventForNoCollectionDate = new QaEvent();
			qaEventForNoCollectionDate.setQaEventName(SystemConfiguration.getInstance().getQaEventCodeForRequestNoCollectionDate());
			qaEventForNoCollectionDate = qaEventDAO.getQaEventByName(qaEventForNoCollectionDate);

			QaEvent qaEventForNoSampleType = new QaEvent();
			qaEventForNoSampleType.setQaEventName(SystemConfiguration.getInstance().getQaEventCodeForRequestNoSampleType());
			qaEventForNoSampleType = qaEventDAO.getQaEventByName(qaEventForNoSampleType);
			
			QaEvent qaEventForUnknownSubmitter = new QaEvent();
			qaEventForUnknownSubmitter.setQaEventName(SystemConfiguration.getInstance().getQaEventCodeForRequestUnknownSubmitter());
			qaEventForUnknownSubmitter = qaEventDAO.getQaEventByName(qaEventForUnknownSubmitter);
            //end bugzilla 2028

			// Analysis table
			for (int i = 0; i < analyses.size(); i++) {
				Analysis analysis = (Analysis) analyses.get(i);
				//bugzilla 1942: if all analyses for this sample have already gone through results verification and analysis.status is released
				//               then change sample.status to released also
				if (StringUtil.isNullorNill(analysis.getStatus()) || !analysis.getStatus().equals(SystemConfiguration.getInstance().getAnalysisStatusReleased())) {
						allAnalysesReleased = false;
				} 
				analysis.setSampleItem(sampleItem);
				analysis.setSysUserId(sysUserId);
				
				//bugzilla 2028 QA_EVENT COLLECTIONDATE
				if (sample.getCollectionDate() == null) {
					AnalysisQaEvent analysisQaEvent = new AnalysisQaEvent();
					analysisQaEvent.setAnalysis(analysis);
					analysisQaEvent.setQaEvent(qaEventForNoCollectionDate);
					analysisQaEvent = analysisQaEventDAO.getAnalysisQaEventByAnalysisAndQaEvent(analysisQaEvent);

					
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
				}
				
				//bugzilla 2028 QA_EVENT SAMPLETYPE
				if (typeOfSample.equals(SAMPLE_TYPE_NOT_GIVEN)) {
					AnalysisQaEvent analysisQaEvent = new AnalysisQaEvent();
					analysisQaEvent.setAnalysis(analysis);
					analysisQaEvent.setQaEvent(qaEventForNoSampleType);
					analysisQaEvent = analysisQaEventDAO.getAnalysisQaEventByAnalysisAndQaEvent(analysisQaEvent);


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
				}
				
				//bugzilla 2028 QA_EVENT UNKNOWN SUBMITTER
				if (submitterNumber.equals(SystemConfiguration.getInstance().getUnknownSubmitterNumberForQaEvent())) {
					AnalysisQaEvent analysisQaEvent = new AnalysisQaEvent();
					analysisQaEvent.setAnalysis(analysis);
					analysisQaEvent.setQaEvent(qaEventForUnknownSubmitter);
					analysisQaEvent = analysisQaEventDAO.getAnalysisQaEventByAnalysisAndQaEvent(analysisQaEvent);


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
				}
				analysisDAO.updateData(analysis);
			}
			
			//bugzilla 1942
			if (analyses.size() > 0 && allAnalysesReleased) {
				sample.setStatus(SystemConfiguration.getInstance().getSampleStatusReleased());
				sample.setReleasedDateForDisplay(dateAsText);
			} else {
				sample.setStatus(SystemConfiguration.getInstance().getSampleStatusEntry2Complete());
			}
            
			sampleDAO.updateData(sample);
			
			tx.commit();
			// done updating return to menu
			blankscreen = "false";
			forward = FWD_CLOSE;

		} catch (LIMSRuntimeException lre) {
            //bugzilla 2154
			LogEvent.logError("HumanSampleTwoUpdateAction","performAction()",lre.toString());
			tx.rollback();
			// if error then forward to fail and don't update to blank page
			// = false
			errors = new ActionMessages();
			ActionError error = null;
			if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
				// how can I get popup instead of struts error at the top of
				// page?
				// ActionMessages errors = dynaForm.validate(mapping,
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

		PropertyUtils.setProperty(dynaForm, "typeOfSamples", typeOfSamples);
		PropertyUtils.setProperty(dynaForm, "sourceOfSamples", sourceOfSamples);
		PropertyUtils.setProperty(dynaForm, "humanSampleOneMap",
				humanSampleOneMap);

		if ("true".equalsIgnoreCase(request.getParameter("close"))) {
			forward = FWD_CLOSE;
		}

		if (sample.getId() != null && !sample.getId().equals("0")) {
			request.setAttribute(ID, sample.getId());

		}

		if (forward.equals(FWD_SUCCESS)) {
			request.setAttribute("menuDefinition", "default");
		}

		// return getForward(mapping.findForward(forward), id, start);
		return mapping.findForward(forward);

	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "human.sample.two.add.title";
		} else {
			return "human.sample.two.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "human.sample.two.add.title";
		} else {
			return "human.sample.two.edit.title";
		}
	}

	protected ActionMessages validateZipCity(ActionMessages errors,
			String zipCode, String city) throws Exception {
		//bugzilla 1545
		CityStateZipDAO cityStateZipDAO = new CityStateZipDAOImpl();
        CityStateZip cityStateZip = new CityStateZip();

		// use 5-digit zipcode for validation
		String zc5Dig = null;
		zc5Dig = zipCode.substring(0, 5);
		cityStateZip.setZipCode(zc5Dig);
		cityStateZip.setCity(city);
		
		cityStateZip = cityStateZipDAO.getCityStateZipByCityAndZipCode(cityStateZip);
		
		if (cityStateZip == null) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionError(
						"humansampleone.validation.zipCity", null, null));
		}

		return errors;
	}

	// compares values against HashMap of HSE1 values before going to dbase to
	// revalidate submitted values
	//bugzilla 1765 changes to city state zip validation
	protected ActionMessages validateAll(HttpServletRequest request,
			ActionMessages errors, BaseActionForm dynaForm,
			HashMap humanSampleOneMap) throws Exception {

		String result;
		String messageKey;
		ProjectIdOrNameValidationProvider projIdValidator = new ProjectIdOrNameValidationProvider();

		String projNum = (String) dynaForm.get("projectIdOrName");
		if (!StringUtil.isNullorNill(projNum)) {
			// project ID validation against database (reusing ajax
			// validation logic)
			result = compareAgainstHSE1HashMap(humanSampleOneMap, projNum, 
					"projectId");
			if (result.equals(INVALID)) {
				result = projIdValidator.validate(projNum);
				messageKey = "humansampleone.projectNumber";
				if (result.equals(INVALID)) {
					String projName = (String) dynaForm.get("projectNameOrId");
					result = compareAgainstHSE1HashMap(humanSampleOneMap,
							projName, "projectName");
					if (result.equals(INVALID)) {
						result = projIdValidator.validate(projName);
						if (result.equals(INVALID)) {
							ActionError error = new ActionError(
									"errors.invalid", getMessageForKey(messageKey), null);
							errors.add(ActionMessages.GLOBAL_MESSAGE, error);
						}
					}
				}
			}
		}

		String proj2Num = (String) dynaForm.get("project2IdOrName");
		if (!StringUtil.isNullorNill(proj2Num)) {
			result = compareAgainstHSE1HashMap(humanSampleOneMap, proj2Num,
					"projectId2");
			if (result.equals(INVALID)) {
				result = projIdValidator.validate(proj2Num);
				messageKey = "humansampleone.project2Number";
				if (result.equals(INVALID)) {
					String proj2Name = (String) dynaForm
							.get("project2NameOrId");
					result = compareAgainstHSE1HashMap(humanSampleOneMap,
							proj2Name, "projectName2");
					if (result.equals(INVALID)) {
						result = projIdValidator.validate(proj2Name);
						if (result.equals(INVALID)) {
							ActionError error = new ActionError(
									"errors.invalid", getMessageForKey(messageKey), null);
							errors.add(ActionMessages.GLOBAL_MESSAGE, error);
						}
					}
				}
			}
		}

		// accession number validation against database (reusing ajax
		// validation logic)
		String accnNum = (String) dynaForm.get("accessionNumber");
		if (!StringUtil.isNullorNill(accnNum)) {
			result = compareAgainstHSE1HashMap(humanSampleOneMap, accnNum,
					"accessionNumber");
			if (result.equals(INVALID)) {
				errors = validateAccessionNumber(request, errors, dynaForm);
			}
		}

		// organization ID (submitter) validation against database (reusing ajax
		// validation logic)
		//bugzilla 2069
		String orgLocalAbbreviation = (String) dynaForm.get("organizationLocalAbbreviation");
		if (!StringUtil.isNullorNill(orgLocalAbbreviation)) {
			result = compareAgainstHSE1HashMap(humanSampleOneMap, orgLocalAbbreviation,
					"organizationLocalAbbreviation");
			if (result.equals(INVALID)) {
				//bugzilla 2531
				OrganizationLocalAbbreviationValidationProvider organiationLocalAbbreviationValidator = new OrganizationLocalAbbreviationValidationProvider();
				result = organiationLocalAbbreviationValidator.validate((String) dynaForm
						.get("organizationLocalAbbreviation"),null);
				messageKey = "humansampleone.provider.organization.localAbbreviation";
				if (result.equals(INVALID)) {
					ActionError error = new ActionError("errors.invalid",
							getMessageForKey(messageKey), null);
					errors.add(ActionMessages.GLOBAL_MESSAGE, error);
				}
			}
		}

		boolean stateValid = true;
		String state = (String) dynaForm.get("state");
		if (!StringUtil.isNullorNill(state)) {
			result = compareAgainstHSE1HashMap(humanSampleOneMap, state,
					"state");
			if (result.equals(INVALID)) {
				// state validation against database (reusing ajax validation
				// logic
				StateValidationProvider stateValidator = new StateValidationProvider();
				result = stateValidator.validate(state);
				messageKey = "person.state";
				if (result.equals(INVALID)) {
					stateValid = false;
					ActionError error = new ActionError("errors.invalid",
							getMessageForKey(messageKey), null);
					errors.add(ActionMessages.GLOBAL_MESSAGE, error);
				}
			}
		}

		boolean cityValid = true;
		String city = (String) dynaForm.get("city");
		if (!StringUtil.isNullorNill(city)) {
			result = compareAgainstHSE1HashMap(humanSampleOneMap, city, "city");
			if (result.equals(INVALID)) {
				// city validation against database (reusing ajax validation
				// logic
				CityValidationProvider cityValidator = new CityValidationProvider();
				result = cityValidator.validate(city);
				messageKey = "person.city";
				if (result.equals(INVALID)) {
					cityValid = false;
					ActionError error = new ActionError("errors.invalid",
							getMessageForKey(messageKey), null);
					errors.add(ActionMessages.GLOBAL_MESSAGE, error);
				}
			}
		}

		boolean zipValid = true;
		String zip = (String) dynaForm.get("zipCode");
		if (!StringUtil.isNullorNill(zip)) {
			result = compareAgainstHSE1HashMap(humanSampleOneMap, zip,
					"zipCode");
			if (result.equals(INVALID)) {
				// zip validation against database (reusing ajax validation
				// logic
				ZipValidationProvider zipValidator = new ZipValidationProvider();
				result = zipValidator.validate((String) dynaForm.get("zipCode"));
				messageKey = "person.zipCode";
				if (result.equals(INVALID)) {
					zipValid = false;
					ActionError error = new ActionError("errors.invalid",
							getMessageForKey(messageKey), null);
					errors.add(ActionMessages.GLOBAL_MESSAGE, error);
				}
			}
		}
		
		if (cityValid && stateValid && zipValid) {
			String messageKey1 = "person.city";
			String messageKey2 = "person.zipCode";
			String messageKey3 = "person.state";			CityStateZipComboValidationProvider cityStateZipComboValidator = new CityStateZipComboValidationProvider();
			result = cityStateZipComboValidator.validate((String) dynaForm
					.get("city"), (String) dynaForm.get("state"),
					(String) dynaForm.get("zipCode"));
			// combination is invalid if result is invalid
			if ("invalid".equals(result)) {
				ActionError error = new ActionError("errors.combo.3.invalid",
						getMessageForKey(messageKey1),
						getMessageForKey(messageKey2),
						getMessageForKey(messageKey3),null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			}
		}

		// sample type validation against database (reusing ajax validation
		// logic
		String sampleType = (String) dynaForm.get("typeOfSampleDesc");
		if (!StringUtil.isNullorNill(sampleType)) {
			result = compareAgainstHSE1HashMap(humanSampleOneMap, sampleType,
					"typeOfSampleDesc");
			if (result.equals(INVALID)) {
				HumanSampleTypeValidationProvider typeValidator = new HumanSampleTypeValidationProvider();
				result = typeValidator.validate(sampleType);
				messageKey = "sampleitem.typeOfSample";
				if (result.equals(INVALID)) {
					ActionError error = new ActionError("errors.invalid",
							getMessageForKey(messageKey), null);
					errors.add(ActionMessages.GLOBAL_MESSAGE, error);
				}
			}
		}

		String sampleSource = (String) dynaForm.get("sourceOfSampleDesc");
		if (!StringUtil.isNullorNill(sampleSource)) {
			result = compareAgainstHSE1HashMap(humanSampleOneMap, sampleSource,
					"sourceOfSampleDesc");
			// sample source validation against database (reusing ajax
			// validation
			// logic
			if (result.equals(INVALID)) {
				HumanSampleSourceValidationProvider sourceValidator = new HumanSampleSourceValidationProvider();
				result = sourceValidator.validate(sampleSource);
				messageKey = "sampleitem.sourceOfSample";
				if (result.equals(INVALID)) {
					ActionError error = new ActionError("errors.invalid",
							getMessageForKey(messageKey), null);
					errors.add(ActionMessages.GLOBAL_MESSAGE, error);
				}
			}
		}

		return errors;
	}

	private String compareAgainstHSE1HashMap(HashMap humanSampleOneMap,
			String value, String key) {

		if (humanSampleOneMap.get(key).equals(value)) {
			return VALID;
		} else {
			return INVALID;
		}
	}
}