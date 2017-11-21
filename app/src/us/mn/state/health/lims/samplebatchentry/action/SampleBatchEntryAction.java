package us.mn.state.health.lims.samplebatchentry.action;

import java.util.Iterator;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.formfields.FormFields;
import us.mn.state.health.lims.common.provider.validation.DateValidationProvider;
import us.mn.state.health.lims.common.services.DisplayListService;
import us.mn.state.health.lims.common.services.SampleOrderService;
import us.mn.state.health.lims.common.services.TestService;
import us.mn.state.health.lims.common.services.DisplayListService.ListType;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.common.util.validator.GenericValidator;
import us.mn.state.health.lims.organization.dao.OrganizationDAO;
import us.mn.state.health.lims.organization.daoimpl.OrganizationDAOImpl;
import us.mn.state.health.lims.organization.valueholder.Organization;
import us.mn.state.health.lims.patient.action.bean.PatientManagementInfo;
import us.mn.state.health.lims.patient.action.bean.PatientSearch;
import us.mn.state.health.lims.sample.action.BaseSampleEntryAction;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.typeofsample.dao.TypeOfSampleDAO;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleDAOImpl;

public class SampleBatchEntryAction extends BaseSampleEntryAction {
	
	@Override
	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String forward = "success";
		BaseActionForm dynaForm = (BaseActionForm) form;
		dynaForm.initialize(mapping);
		
		String sampleXML = request.getParameter("sampleXML");
		SampleOrderService sampleOrderService = new SampleOrderService();
		
		ActionMessages errors = validate(request);
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			request.setAttribute(IActionConstants.FWD_SUCCESS, false);
			forward = FWD_FAIL;
			return mapping.findForward(forward);
		}
        
		//set properties given by previous (setup) page
		PropertyUtils.setProperty( dynaForm, "sampleOrderItems", sampleOrderService.getSampleOrderItem() );
		PropertyUtils.setProperty(dynaForm, "sampleTypes", DisplayListService.getList(ListType.SAMPLE_TYPE_ACTIVE));
		PropertyUtils.setProperty(dynaForm, "testSectionList", DisplayListService.getList(ListType.TEST_SECTION));
        PropertyUtils.setProperty( dynaForm, "currentDate", request.getParameter("currentDate"));
        PropertyUtils.setProperty( dynaForm, "currentTime", request.getParameter("currentTime"));
        PropertyUtils.setProperty( dynaForm, "sampleOrderItems.receivedTime", request.getParameter("sampleOrderItems.receivedTime"));
        PropertyUtils.setProperty( dynaForm, "sampleOrderItems.receivedDateForDisplay", request.getParameter("sampleOrderItems.receivedDateForDisplay"));
        PropertyUtils.setProperty( dynaForm, "sampleOrderItems.referringSiteId", request.getParameter("facilityID"));
        PropertyUtils.setProperty( dynaForm, "sampleXML", sampleXML);
        
        //get summary of tests selected to place in common fields section
        Document sampleDom = DocumentHelper.parseText(sampleXML);
		Element sampleItem = sampleDom.getRootElement().element("sample");
	    String testIDs = sampleItem.attributeValue("tests");
	    TestDAO testDAO = new TestDAOImpl();
		StringTokenizer tokenizer = new StringTokenizer(testIDs, ",");
		StringBuilder sBuilder = new StringBuilder();
		String seperator = "";
		while (tokenizer.hasMoreTokens()) {
			sBuilder.append(seperator);
			sBuilder.append(TestService.getUserLocalizedTestName(testDAO.getTestById(tokenizer.nextToken().trim())));
			seperator = "<br>";
		}
		TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
		String sampleType = typeOfSampleDAO.getTypeOfSampleById(sampleItem.attributeValue("sampleID")).getLocalAbbreviation();
		String testNames = sBuilder.toString();
	    request.setAttribute("sampleType", sampleType);
	    request.setAttribute("testNames", testNames);
	    
	    //get facility name from id
	    OrganizationDAO organizationDAO = new OrganizationDAOImpl();
	    Organization organization = new Organization();
	    organization.setId(request.getParameter("facilityID"));
	    organizationDAO.getData(organization);
	    request.setAttribute("facilityName", organization.getOrganizationName());
     
		return mapping.findForward(forward);
	}
	
	private ActionMessages validate(HttpServletRequest request) {
		ActionMessages errors = new ActionMessages();
		DateValidationProvider dateValidationProvider = new DateValidationProvider();
		String curDateValid = dateValidationProvider.validateDate(dateValidationProvider.getDate(request.getParameter("currentDate")), "past");
		String recDateValid = dateValidationProvider.validateDate(dateValidationProvider.getDate(request.getParameter("sampleOrderItems.receivedDateForDisplay")), "past");
		
		if (!(curDateValid.equals(IActionConstants.VALID))) {
			ActionError error = new ActionError("batchentry.error.curdate.invalid");
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		if (!GenericValidator.is24HourTime(request.getParameter("currentTime"))) {
			ActionError error = new ActionError("batchentry.error.curtime.invalid");
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		if (!(recDateValid.equals(IActionConstants.VALID))) {
			ActionError error = new ActionError("batchentry.error.recdate.invalid");
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		if (!GenericValidator.is24HourTime(request.getParameter("sampleOrderItems.receivedTime"))) {
			ActionError error = new ActionError("batchentry.error.rectime.invalid");
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		//TO DO validate facility id
		
		//TO DO validate sampleXML
			
		
		return errors;
	}
	
	@Override
	protected String getPageTitleKey() {
		return "sample.batchentry.title";
	}

	@Override
	protected String getPageSubtitleKey() {
		return "sample.batchentry.title";
	}

}
