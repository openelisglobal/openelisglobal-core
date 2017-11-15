package us.mn.state.health.lims.barcode.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.hibernate.Transaction;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.common.util.validator.GenericValidator;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.siteinformation.dao.SiteInformationDAO;
import us.mn.state.health.lims.siteinformation.daoimpl.SiteInformationDAOImpl;
import us.mn.state.health.lims.siteinformation.daoimpl.SiteInformationDomainDAOImpl;
import us.mn.state.health.lims.siteinformation.valueholder.SiteInformation;

public class BarcodeConfigurationSaveAction extends BaseAction {

	@Override
	protected ActionForward performAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String forward = FWD_SUCCESS;

		ActionMessages errors = validate(request);
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			request.setAttribute(IActionConstants.FWD_SUCCESS, false);
			forward = FWD_FAIL;
			return mapping.findForward(forward);
		}
		
		BaseActionForm dynaForm = (BaseActionForm) form;
		dynaForm.initialize(mapping);
		updateLabelSizing(request);
		updateLabelMaximums(request);
		updateFields(request);
		if (errors.isEmpty()) {
			request.setAttribute(IActionConstants.FWD_SUCCESS, true);
		}

 		return mapping.findForward(forward);
	}
	
	private ActionMessages validate(HttpServletRequest request) {
		ActionMessages errors = new ActionMessages();

		//check dimensions
		if (!GenericValidator.isFloat(request.getParameter("heightOrderLabels"))) {
			ActionError error = new ActionError("barcode.config.error.dimension.invalid", "Order Height");
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		if (!GenericValidator.isFloat(request.getParameter("widthOrderLabels"))) {
			ActionError error = new ActionError("barcode.config.error.dimension.invalid", "Order Width");
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		if (!GenericValidator.isFloat(request.getParameter("heightSpecimenLabels"))) {
			ActionError error = new ActionError("barcode.config.error.dimension.invalid", "Specimen Height");
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		if (!GenericValidator.isFloat(request.getParameter("widthSpecimenLabels"))) {
			ActionError error = new ActionError("barcode.config.error.dimension.invalid", "Specimen Width");
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}

		//check number of labels
		if (!GenericValidator.isInt(request.getParameter("numOrderLabels"))) {
			ActionError error = new ActionError("barcode.config.error.number.invalid", "Order");
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		if (!GenericValidator.isInt(request.getParameter("numSpecimenLabels"))) {
			ActionError error = new ActionError("barcode.config.error.number.invalid", "Specimen");
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}

		//check optional fields
		if (!GenericValidator.isBool(request.getParameter("collectionDateCheck")) 
				&& !GenericValidator.isBlankOrNull(request.getParameter("collectionDateCheck"))) {
			ActionError error = new ActionError("barcode.config.error.field.invalid", "Collection Date");
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		if (!GenericValidator.isBool(request.getParameter("patientSexCheck")) 
				&& !GenericValidator.isBlankOrNull(request.getParameter("patientSexCheck"))) {
			ActionError error = new ActionError("barcode.config.error.field.invalid", "Patient Sex");
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		if (!GenericValidator.isBool(request.getParameter("testCheck")) 
				&& !GenericValidator.isBlankOrNull(request.getParameter("testCheck"))) {
			ActionError error = new ActionError("barcode.config.error.field.invalid", "Tests");
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}

		return errors;
	}

	public void updateLabelSizing(HttpServletRequest request) {
		setValue("heightOrderLabels", request.getParameter("heightOrderLabels"), "text");
		setValue("widthOrderLabels", request.getParameter("widthOrderLabels"), "text");
		setValue("heightSpecimenLabels", request.getParameter("heightSpecimenLabels"), "text");
		setValue("widthSpecimenLabels", request.getParameter("widthSpecimenLabels"), "text");
	}
	
	public void updateLabelMaximums(HttpServletRequest request) {
		setValue("numOrderLabels", request.getParameter("numOrderLabels"), "text");
		setValue("numSpecimenLabels", request.getParameter("numSpecimenLabels"), "text");
	}
	
	public void updateFields(HttpServletRequest request) {
		String collectionDateCheck = request.getParameter("collectionDateCheck");
		String patientSexCheck = request.getParameter("patientSexCheck");
		String testsCheck = request.getParameter("testsCheck");
		collectionDateCheck = null == collectionDateCheck ? "false" : collectionDateCheck;
		patientSexCheck = null == patientSexCheck ? "false" : patientSexCheck;
		testsCheck = null == testsCheck ? "false" : testsCheck;
		setValue("collectionDateCheck", collectionDateCheck, "boolean");
		setValue("patientSexCheck", patientSexCheck, "boolean");
		setValue("testsCheck", testsCheck, "boolean");
	}
	
	public void setValue(String name, String value, String valueType) {
		SiteInformation siteInformation;
		SiteInformationDAO siteInformationDAO = new SiteInformationDAOImpl();
		
		siteInformation = siteInformationDAO.getSiteInformationByName(name);
        Transaction tx = HibernateUtil.getSession().beginTransaction();
        try {
			if (siteInformation == null) {
				siteInformation = new SiteInformation();
				siteInformation.setName(name);
				siteInformation.setValue(value);
				siteInformation.setValueType(valueType);
				siteInformation.setSysUserId(currentUserId);
				siteInformationDAO.insertData(siteInformation);
			} else {
				siteInformation.setValue(value);
				siteInformation.setSysUserId(currentUserId);
				siteInformationDAO.updateData(siteInformation);
			}
			tx.commit();
        } catch(LIMSRuntimeException lre) {
			tx.rollback();
        } finally {
			HibernateUtil.closeSession();
		}
        ConfigurationProperties.forceReload();
	}

	@Override
	protected String getPageTitleKey() {
		return "barcodeconfiguration.browse.title";
	}

	@Override
	protected String getPageSubtitleKey() {
		return "barcodeconfiguration.browse.title";
	}

}
