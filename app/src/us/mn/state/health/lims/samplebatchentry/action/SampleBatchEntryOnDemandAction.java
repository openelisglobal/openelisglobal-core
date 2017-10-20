package us.mn.state.health.lims.samplebatchentry.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.formfields.FormFields;
import us.mn.state.health.lims.common.services.DisplayListService;
import us.mn.state.health.lims.common.services.SampleOrderService;
import us.mn.state.health.lims.common.services.TestService;
import us.mn.state.health.lims.common.services.DisplayListService.ListType;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.patient.action.bean.PatientManagementInfo;
import us.mn.state.health.lims.patient.action.bean.PatientSearch;
import us.mn.state.health.lims.sample.action.BaseSampleEntryAction;
import us.mn.state.health.lims.sample.bean.SampleOrderItem;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.typeofsample.dao.TypeOfSampleDAO;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleDAOImpl;

public class SampleBatchEntryOnDemandAction extends BaseSampleEntryAction {
	
	@Override
	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String forward = "success";
		BaseActionForm dynaForm = (BaseActionForm) form;
		dynaForm.initialize(mapping);
		
		String sampleXML = request.getParameter("sampleXML");

		PropertyUtils.setProperty(dynaForm, "sampleTypes", DisplayListService.getList(ListType.SAMPLE_TYPE_ACTIVE));
		PropertyUtils.setProperty(dynaForm, "testSectionList", DisplayListService.getList(ListType.TEST_SECTION));
        PropertyUtils.setProperty( dynaForm, "currentDate", request.getParameter("currentDate"));
        PropertyUtils.setProperty( dynaForm, "currentTime", request.getParameter("currentTime"));
        PropertyUtils.setProperty( dynaForm, "sampleOrderItems.receivedTime", request.getParameter("sampleOrderItems.receivedTime"));
        PropertyUtils.setProperty( dynaForm, "sampleOrderItems.receivedDateForDisplay", request.getParameter("sampleOrderItems.receivedDateForDisplay"));
        PropertyUtils.setProperty( dynaForm, "sampleXML", sampleXML);
        
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
        
		String facilityIDString = request.getParameter("facilityID"); 
		String facilityIDNoString = request.getParameter("facilityIDNo"); 
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName"); 
		
		if (facilityIDNoString != null && !StringUtils.isNumeric(facilityIDNoString)) {
			return mapping.findForward("fail");
		} 
		

		return mapping.findForward(forward);
	}
	
	@Override
	protected String getPageTitleKey() {
		return "sample.batchentry.ondemand.title";
	}

	@Override
	protected String getPageSubtitleKey() {
		return "sample.batchentry.ondemand.title";
	}

}
