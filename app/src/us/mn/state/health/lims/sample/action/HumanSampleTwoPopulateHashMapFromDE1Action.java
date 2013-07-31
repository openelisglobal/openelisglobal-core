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
import us.mn.state.health.lims.common.provider.validation.ProjectIdValidationProvider;
import us.mn.state.health.lims.common.provider.validation.StateValidationProvider;
import us.mn.state.health.lims.common.provider.validation.ZipValidationProvider;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.login.dao.UserTestSectionDAO;
import us.mn.state.health.lims.login.daoimpl.UserTestSectionDAOImpl;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.person.dao.PersonDAO;
import us.mn.state.health.lims.person.daoimpl.PersonDAOImpl;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.provider.dao.ProviderDAO;
import us.mn.state.health.lims.provider.daoimpl.ProviderDAOImpl;
import us.mn.state.health.lims.provider.valueholder.Provider;
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
import us.mn.state.health.lims.sourceofsample.dao.SourceOfSampleDAO;
import us.mn.state.health.lims.sourceofsample.daoimpl.SourceOfSampleDAOImpl;
import us.mn.state.health.lims.sourceofsample.valueholder.SourceOfSample;
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
public class HumanSampleTwoPopulateHashMapFromDE1Action extends BaseAction {

	private boolean isNew = false;

	protected static final String INVALID = "invalid";

	protected static final String VALID = "valid";

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
		//bugzilla 2154
		LogEvent.logDebug("HumanSampleTwoPopulateHashMapFromDE1Action","performAction()","accessionNumber coming in: " + accessionNumber);
		String start = (String) request.getParameter("startingRecNo");

		String typeOfSample = (String) dynaForm.get("typeOfSampleDesc");
		String sourceOfSample = (String) dynaForm.get("sourceOfSampleDesc");

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
			    LogEvent.logError("HumanSampleTwoPopulateHashMapFromDE1Action","performAction()",nfe.toString());
				projectId = projectNameOrId;
			}

		}

		if (project2IdOrName != null && project2NameOrId != null) {
			try {
				Integer i = Integer.valueOf(project2IdOrName);
				project2Id = project2IdOrName;

			} catch (NumberFormatException nfe) {
    			//bugzilla 2154
			    LogEvent.logError("HumanSampleTwoPopulateHashMapFromDE1Action","performAction()",nfe.toString());
				project2Id = project2NameOrId;
			}

		}
		
		//bugzilla 2154
		LogEvent.logDebug("HumanSampleTwoPopulateHashMapFromDE1Action","performAction()","ProjectId is: " + projectId + " Project2Id is: " + project2Id);
		
		// set current date for validation of dates
		Date today = Calendar.getInstance().getTime();
		Locale locale = (Locale) request.getSession().getAttribute(
				"org.apache.struts.action.LOCALE");
		String dateAsText = DateUtil.formatDateAsText(today, locale);

		Patient patient = new Patient();
		Person person = new Person();
		Provider provider = new Provider();
		Person providerPerson = new Person();
		Sample sample = new Sample();
		SampleHuman sampleHuman = new SampleHuman();
		SampleOrganization sampleOrganization = new SampleOrganization();
		List sampleProjects = new ArrayList();
		List updatedSampleProjects = new ArrayList();
		SampleItem sampleItem = new SampleItem();
		// TODO need to populate this with tests entered in HSE I
		List analyses = new ArrayList();

		// tests are not handled in HSE II
		/*
		 * String stringOfTestIds = (String) dynaForm.get("selectedTestIds");
		 * 
		 * String[] listOfTestIds = stringOfTestIds.split(SystemConfiguration
		 * .getInstance().getDefaultIdSeparator(), -1);
		 * 
		 * List analyses = new ArrayList(); for (int i = 0; i <
		 * listOfTestIds.length; i++) { if
		 * (!StringUtil.isNullorNill(listOfTestIds[i])) { Analysis analysis =
		 * new Analysis(); analysis.setTestId(listOfTestIds[i]); // TODO: need
		 * to populate this with actual data!!!
		 * analysis.setAnalysisType("TEST"); analyses.add(analysis); } }
		 */

		ActionMessages errors = null;

		// validate on server-side sample accession number

		try {
			errors = new ActionMessages();
			errors = validateAccessionNumber(request, errors, dynaForm);
			// System.out.println("Just validated accessionNumber");
		} catch (Exception e) {
    		//bugzilla 2154
			LogEvent.logError("HumanSampleTwoPopulateHashMapFromDE1Action","performAction()",e.toString());
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
			request.setAttribute(ALLOW_EDITS_KEY, "false");

			return mapping.findForward(FWD_FAIL);
		}
		// System.out.println("Now try to get data for accession number ");
		try {

			PatientDAO patientDAO = new PatientDAOImpl();
			PersonDAO personDAO = new PersonDAOImpl();
			ProviderDAO providerDAO = new ProviderDAOImpl();
			SampleDAO sampleDAO = new SampleDAOImpl();
			SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
			SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
			SampleOrganizationDAO sampleOrganizationDAO = new SampleOrganizationDAOImpl();
			AnalysisDAO analysisDAO = new AnalysisDAOImpl();

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

				humanSampleOneMap = populateHumanSampleOneMap(patient, person,
						provider, providerPerson, sample, sampleHuman,
						sampleOrganization, sampleItem, analyses);

			}

		} catch (LIMSRuntimeException lre) {
			// if error then forward to fail and don't update to blank page
			// = false
			//bugzilla 2154
			LogEvent.logError("HumanSampleTwoPopulateHashMapFromDE1Action","performAction()",lre.toString());
			errors = new ActionMessages();
			ActionError error = null;
			if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
				// how can I get popup instead of struts error at the top of
				// page?
				// ActionMessages errors = dynaForm.validate(mapping,
				// request);
				error = new ActionError("errors.OptimisticLockException", null,
						null);
				//bugzilla 2154
				LogEvent.logError("HumanSampleTwoPopulateHashMapFromDE1Action","performAction()","errors.OptimisticLockException");		
			} else {
				error = new ActionError("errors.GetException", null, null);
				//bugzilla 2154
				LogEvent.logError("HumanSampleTwoPopulateHashMapFromDE1Action","performAction()","errors.GetException");
			}
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute(ALLOW_EDITS_KEY, "false");
			return mapping.findForward(FWD_FAIL);

		}

		// initialize the form
		dynaForm.initialize(mapping);

		// set lastupdated fields
		dynaForm.set("lastupdated", sample.getLastupdated());
		dynaForm.set("personLastupdated", person.getLastupdated());
		dynaForm.set("patientLastupdated", patient.getLastupdated());
		dynaForm.set("providerPersonLastupdated", providerPerson
				.getLastupdated());
		dynaForm.set("providerLastupdated", provider.getLastupdated());
		dynaForm.set("sampleItemLastupdated", sampleItem.getLastupdated());
		dynaForm.set("sampleHumanLastupdated", sampleHuman.getLastupdated());
		dynaForm.set("sampleOrganizationLastupdated", sampleOrganization
				.getLastupdated());

		if (updatedSampleProjects != null && updatedSampleProjects.size() > 0) {
			if (updatedSampleProjects.size() == 1) {
				SampleProject sp = (SampleProject) updatedSampleProjects.get(0);
				dynaForm.set("sampleProject1Lastupdated", sp.getLastupdated());
				// bugzilla 1857 deprecated stuff
				//System.out.println("This is sp ts "
				//		+ StringUtil.formatDateAsText(sp.getLastupdated(),
				//				SystemConfiguration.getInstance()
				//						.getDefaultLocale()));
			}
			if (updatedSampleProjects.size() == 2) {
				SampleProject sp2 = (SampleProject) updatedSampleProjects
						.get(1);
				dynaForm.set("sampleProject2Lastupdated", sp2.getLastupdated());
				// bugzilla 1857 deprecated stuff
				//System.out.println("This is sp2 ts "
				//		+ StringUtil.formatDateAsText(sp2.getLastupdated(),
				//				SystemConfiguration.getInstance()
				//						.getDefaultLocale()));
			}
		}

		if (dynaForm.get("sampleProject1Lastupdated") == null) {
			PropertyUtils.setProperty(form, "sampleProject1Lastupdated",
					new Timestamp(System.currentTimeMillis()));
		}
		if (dynaForm.get("sampleProject2Lastupdated") == null) {
			PropertyUtils.setProperty(form, "sampleProject2Lastupdated",
					new Timestamp(System.currentTimeMillis()));
		}

		PropertyUtils.setProperty(dynaForm, "currentDate", dateAsText);
		PropertyUtils.setProperty(dynaForm, "accessionNumber", sample
				.getAccessionNumber());
		// set receivedDate
		PropertyUtils.setProperty(dynaForm, "receivedDateForDisplay",
				(String) sample.getReceivedDateForDisplay());

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

		//bugzilla 2154
		LogEvent.logDebug("HumanSampleTwoPopulateHashMapFromDE1Action","performAction()","forwarding to: " + forward);
		
		//pdf - get accession number List
		if ( SystemConfiguration.getInstance().getEnabledSamplePdf().equals(YES) ) {				
			String status = SystemConfiguration.getInstance().getSampleStatusEntry1Complete(); //status = 2
			String humanDomain = SystemConfiguration.getInstance().getHumanDomain(); 
			UserTestSectionDAO userTestSectionDAO = new UserTestSectionDAOImpl();
			List accessionNumberListTwo = userTestSectionDAO.getSamplePdfList(request, locale, status, humanDomain);
			PropertyUtils.setProperty(form, "accessionNumberListTwo", accessionNumberListTwo);	
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


	// compares values against HashMap of HSE1 values before going to dbase to
	// revalidate submitted values
	//bugzilla 1765 changes to  city state zip validation
	protected ActionMessages validateAll(HttpServletRequest request,
			ActionMessages errors, BaseActionForm dynaForm,
			HashMap humanSampleOneMap) throws Exception {

		String result;
		String messageKey;
		ProjectIdValidationProvider projIdValidator = new ProjectIdValidationProvider();

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
		String organizationLocalAbbreviation = (String) dynaForm.get("organizationLocalAbbreviation");
		if (!StringUtil.isNullorNill(organizationLocalAbbreviation)) {
			result = compareAgainstHSE1HashMap(humanSampleOneMap, organizationLocalAbbreviation,
					"organizationLocalAbbreviation");
			if (result.equals(INVALID)) {
				//bugzilla 2531
				OrganizationLocalAbbreviationValidationProvider organizationLocalAbbreviationValidator = new OrganizationLocalAbbreviationValidationProvider();
				result = organizationLocalAbbreviationValidator.validate((String) dynaForm
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
				result = zipValidator
						.validate((String) dynaForm.get("zipCode"));
				messageKey = "person.zipCode";
				if (result.equals(INVALID)) {
					zipValid = false;
					ActionError error = new ActionError("errors.invalid",
							getMessageForKey(messageKey), null);
					errors.add(ActionMessages.GLOBAL_MESSAGE, error);
				}
			}
		}

		if (stateValid && cityValid && zipValid) {
			String messageKey1 = "person.city";
			String messageKey2 = "person.zipCode";
			String messageKey3 = "person.state";
			CityStateZipComboValidationProvider cityStateZipComboValidator = new CityStateZipComboValidationProvider();
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

	private HashMap populateHumanSampleOneMap(Patient patient, Person person,
			Provider provider, Person providerPerson, Sample sample,
			SampleHuman sampleHuman, SampleOrganization sampleOrganization,
			SampleItem sampleItem, List analyses) {
		HashMap humanSampleOneMap = new HashMap();

		List sampleProjects = new ArrayList();
		SampleProject sampleProject = null;
		SampleProject sampleProject2 = null;

		sampleProjects = sample.getSampleProjects();

		if (sampleProjects != null && sampleProjects.size() > 0) {
			sampleProject = (SampleProject) sampleProjects.get(0);
			if (sampleProjects.size() > 1) {
				sampleProject2 = (SampleProject) sampleProjects.get(1);
			}

		}

		if (sample.getAccessionNumber() != null) {
			humanSampleOneMap.put("accessionNumber", sample
					.getAccessionNumber());
		} else {
			humanSampleOneMap.put("accessionNumber", "");
		}

		if (patient.getBirthDateForDisplay() != null) {
			humanSampleOneMap.put("birthDateForDisplay", patient
					.getBirthDateForDisplay());
		} else {
			humanSampleOneMap.put("birthDateForDisplay", "");
		}

		if (sample.getCollectionDateForDisplay() != null) {
			humanSampleOneMap.put("collectionDateForDisplay", sample
					.getCollectionDateForDisplay());
		} else {
			humanSampleOneMap.put("collectionDateForDisplay", "");
		}

		if (sample.getCollectionTimeForDisplay() != null) {
			humanSampleOneMap.put("collectionTimeForDisplay", sample
					.getCollectionTimeForDisplay());
		} else {
		    //bugzilla 1894
			humanSampleOneMap.put("collectionTimeForDisplay", SystemConfiguration.getInstance()
					.getHumanSampleOneDefaultCollectionTimeForDisplay());
		}
		
		//bugzilla 1904
		if (patient.getChartNumber() != null) {
			humanSampleOneMap.put("chartNumber", patient.getChartNumber());
		} else {
			humanSampleOneMap.put("chartNumber", "");
		}
		
		if (person.getCity() != null) {
			// bugzilla 1766
			humanSampleOneMap
					.put("city", person.getCity().toUpperCase().trim());
		} else {
			humanSampleOneMap.put("city", "");
		}

		if (sample.getClientReference() != null) {
			humanSampleOneMap.put("clientReference", sample
					.getClientReference());
		} else {
			humanSampleOneMap.put("clientReference", "");
		}

		if (patient.getExternalId() != null) {
			humanSampleOneMap.put("externalId", patient.getExternalId());
		} else {
			humanSampleOneMap.put("externalId", "");
		}

		if (person.getFirstName() != null) {
			humanSampleOneMap.put("firstName", person.getFirstName());
		} else {
			humanSampleOneMap.put("firstName", "");
		}

		if (patient.getGender() != null) {
			humanSampleOneMap.put("gender", patient.getGender());
		} else {
			humanSampleOneMap.put("gender", "");
		}

		if (person.getLastName() != null) {
			humanSampleOneMap.put("lastName", person.getLastName());
		} else {
			humanSampleOneMap.put("lastName", "");
		}

		if (person.getMiddleName() != null) {
			humanSampleOneMap.put("middleName", person.getMiddleName());
		} else {
			humanSampleOneMap.put("middleName", "");
		}

		if (person.getMultipleUnit() != null) {
			humanSampleOneMap.put("multipleUnit", person.getMultipleUnit());
		} else {
			humanSampleOneMap.put("multipleUnit", "");
		}

		if (sampleOrganization != null
				&& sampleOrganization.getOrganization() != null) {
			//bugzilla 2069
			humanSampleOneMap.put("organizationLocalAbbreviation", sampleOrganization
					.getOrganization().getOrganizationLocalAbbreviation());
			humanSampleOneMap.put("organizationNameForDisplay",
					sampleOrganization.getOrganization().getOrganizationName());
		} else {
			humanSampleOneMap.put("organizationLocalAbbreviation", "");
			humanSampleOneMap.put("organizationNameForDisplay", "");
		}
		if (sampleProject != null && sampleProject.getProject() != null) {
			//bugzilla 2438
			humanSampleOneMap.put("projectId", sampleProject.getProject()
					.getLocalAbbreviation());
			// bugzilla 1766 since we capitolize project name do this here
			// for comparison purposes
			humanSampleOneMap.put("projectName", sampleProject.getProject()
					.getProjectName().toUpperCase().trim());
		} else {
			humanSampleOneMap.put("projectId", "");
			humanSampleOneMap.put("projectName", "");
		}

		if (sampleProject2 != null && sampleProject2.getProject() != null) {
			//bugzilla 2438
			humanSampleOneMap.put("projectId2", sampleProject2.getProject()
					.getLocalAbbreviation());
			// bugzilla 1766 since we capitolize project name do this here
			// for comparison purposes
			humanSampleOneMap.put("projectName2", sampleProject2.getProject()
					.getProjectName().toUpperCase().trim());
		} else {
			humanSampleOneMap.put("projectId2", "");
			humanSampleOneMap.put("projectName2", "");
		}

		if (providerPerson.getFirstName() != null) {
			humanSampleOneMap.put("providerFirstName", providerPerson
					.getFirstName());
		} else {
			humanSampleOneMap.put("providerFirstName", "");
		}

		if (providerPerson.getLastName() != null) {
			humanSampleOneMap.put("providerLastName", providerPerson
					.getLastName());
		} else {
			humanSampleOneMap.put("providerLastName", "");
		}

		if (providerPerson.getWorkPhone() != null) {
			String storedResult = providerPerson.getWorkPhone();
			humanSampleOneMap.put("providerWorkPhone", StringUtil
					.formatPhoneForDisplay(storedResult));
		} else {
			humanSampleOneMap.put("providerWorkPhone", "");
		}

		if (providerPerson.getWorkPhone() != null) {
			String storedResult = providerPerson.getWorkPhone();
			//bugzilla 2442
			String extension = StringUtil.formatExtensionForDisplay(storedResult);
			humanSampleOneMap.put("providerWorkPhoneExtension", StringUtil.trim(extension));
		} else {
			humanSampleOneMap.put("providerWorkPhoneExtension", "");
		}

		if (sample.getReceivedDateForDisplay() != null) {
			humanSampleOneMap.put("receivedDateForDisplay", sample
					.getReceivedDateForDisplay());
		} else {
			humanSampleOneMap.put("receivedDateForDisplay", "");
		}

		if (sample.getReferredCultureFlag() != null) {
			humanSampleOneMap.put("referredCultureFlag", sample
					.getReferredCultureFlag());
		} else {
			humanSampleOneMap.put("referredCultureFlag", "");
		}

		if (sampleItem.getSourceOfSample() != null) {
			SourceOfSample sos = (SourceOfSample) sampleItem
					.getSourceOfSample();
			// bugzilla 1766
			humanSampleOneMap.put("sourceOfSampleDesc", sos.getDescription()
					.toUpperCase().trim());
			// bugzilla 1465
			humanSampleOneMap.put("sourceOfSampleId", sos.getId());
		} else {
			humanSampleOneMap.put("sourceOfSampleDesc", "");
			// bugzilla 1465
			humanSampleOneMap.put("sourceOfSampleId", "");
		}

		if (sampleItem.getSourceOther() != null) {
			humanSampleOneMap.put("sourceOther", sampleItem.getSourceOther()
					.trim());
		} else {
			humanSampleOneMap.put("sourceOther", "");
		}

		if (person.getState() != null) {
			humanSampleOneMap.put("state", person.getState());
		} else {
			humanSampleOneMap.put("state", "");
		}

		if (sample.getStickerReceivedFlag() != null) {
			humanSampleOneMap.put("stickerReceivedFlag", sample
					.getStickerReceivedFlag());
		} else {
			humanSampleOneMap.put("stickerReceivedFlag", "");
		}

		if (person.getStreetAddress() != null) {
			humanSampleOneMap.put("streetAddress", person.getStreetAddress()
					.trim());
		} else {
			humanSampleOneMap.put("streetAddress", "");
		}

		if (sampleItem.getTypeOfSample() != null) {
			TypeOfSample tos = (TypeOfSample) sampleItem.getTypeOfSample();
			// bugzilla 1766
			humanSampleOneMap.put("typeOfSampleDesc", tos.getDescription()
					.toUpperCase().trim());
			// bugzilla 1465
			humanSampleOneMap.put("typeOfSampleId", tos.getId());
		} else {
			humanSampleOneMap.put("typeOfSampleDesc", "");
			// bugzilla 1465
			humanSampleOneMap.put("typeOfSampleId", "");
		}

		if (person.getZipCode() != null) {
			humanSampleOneMap.put("zipCode", person.getZipCode().trim());
		} else {
			humanSampleOneMap.put("zipCode", "");
		}

		return humanSampleOneMap;
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