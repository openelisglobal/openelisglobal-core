package us.mn.state.health.lims.samplebatchentry.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.formfields.FormFields;
import us.mn.state.health.lims.common.services.DisplayListService;
import us.mn.state.health.lims.common.services.SampleOrderService;
import us.mn.state.health.lims.common.services.DisplayListService.ListType;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.patient.action.bean.PatientManagementInfo;
import us.mn.state.health.lims.patient.action.bean.PatientSearch;
import us.mn.state.health.lims.sample.action.BaseSampleEntryAction;

public class SampleBatchEntryOnDemandAction extends BaseSampleEntryAction {
	
	@Override
	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String forward = "success";

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
