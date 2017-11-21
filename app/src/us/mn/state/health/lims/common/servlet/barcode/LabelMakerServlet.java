package us.mn.state.health.lims.common.servlet.barcode;

import javax.servlet.ServletException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.*;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import us.mn.state.health.lims.barcode.BarcodeLabelMaker;
import us.mn.state.health.lims.barcode.labeltype.OrderLabel;
import us.mn.state.health.lims.barcode.labeltype.BlankLabel;
import us.mn.state.health.lims.barcode.labeltype.Label;
import us.mn.state.health.lims.barcode.labeltype.SpecimenLabel;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.provider.validation.IAccessionNumberValidator;
import us.mn.state.health.lims.common.services.StatusService;
import us.mn.state.health.lims.common.services.StatusService.SampleStatus;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.Versioning;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.common.util.validator.GenericValidator;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.dao.UserModuleDAO;
import us.mn.state.health.lims.login.daoimpl.UserModuleDAOImpl;
import us.mn.state.health.lims.login.valueholder.UserSessionData;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.util.AccessionNumberUtil;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import us.mn.state.health.lims.siteinformation.dao.SiteInformationDAO;
import us.mn.state.health.lims.siteinformation.daoimpl.SiteInformationDAOImpl;
import us.mn.state.health.lims.siteinformation.valueholder.SiteInformation;

public class LabelMakerServlet extends HttpServlet implements IActionConstants {

private static final long serialVersionUID = 4756240897909804141L;
private static final Set<Integer> ENTERED_STATUS_SAMPLE_LIST = new HashSet<Integer>();

static {
	ENTERED_STATUS_SAMPLE_LIST.add( Integer.parseInt( StatusService.getInstance().getStatusID( SampleStatus.Entered ) ) );
}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws IOException, ServletException {
		//check for authentication
		UserModuleDAO userModuleDAO = new UserModuleDAOImpl();
		if (userModuleDAO.isSessionExpired(request)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("text/html; charset=utf-8");
			response.getWriter().println(StringUtil.getMessageForKey("message.error.unauthorized"));
			return;
		}
		//get parameters
		String labNo = request.getParameter("labNo");
		String patientId = request.getParameter("patientId");
		String type = request.getParameter("type");
		String quantity = request.getParameter("quantity");
		String override = request.getParameter("override");
		if (StringUtils.isEmpty(labNo)) {
			labNo = (String) request.getSession().getAttribute("lastAccessionNumber");
			labNo = StringUtil.replaceNullWithEmptyString(labNo);
		}
		if (StringUtils.isEmpty(patientId)) {
			patientId = (String) request.getSession().getAttribute("lastPatientId");
			patientId = StringUtil.replaceNullWithEmptyString(patientId);
		}
		if (StringUtils.isEmpty(type))
			type = "default";
		if (StringUtils.isEmpty(quantity))
			quantity = "1";
		if (StringUtils.isEmpty(override)) 
			override = "false";
		
		ActionMessages errors = validate(labNo, patientId, type, quantity, override);
		if (!errors.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.setContentType("text/html; charset=utf-8");
			response.getWriter().println("One or more fields are invalid");
			response.getWriter().println("<ul>");
			@SuppressWarnings("unchecked")
			Iterator<ActionMessage> errorIterator = errors.get();
			while (errorIterator.hasNext()) {
			    ActionMessage error = errorIterator.next();
				response.getWriter().println("<li>" + StringUtil.getMessageForKey(error.getKey()) + "</li>" );
			}
			response.getWriter().println("</ul>");
			return;			
		}
		//assemble requested labels
		ArrayList<Label> labels = new ArrayList<Label>();
		createLabels(labels, labNo, patientId, type, quantity, override, request);
		
		BarcodeLabelMaker labelMaker = new BarcodeLabelMaker(labels);
		labelMaker.setOverride(override);
		ByteArrayOutputStream labelAsOutputStream = labelMaker.createLabelsAsStream();
		
		if (labelAsOutputStream.size() == 0) {
			String path = request.getContextPath();
		 	String basePath = request.getScheme() + "://" + request.getServerName() + ":"	+ request.getServerPort() + path + "/";
		 	String version = Versioning.getBuildNumber();
			response.setContentType("text/html; charset=utf-8");
			response.getWriter().println(StringUtil.getMessageForKey("barcode.message.maxreached"));
			response.getWriter().println("</br>");
			response.getWriter().println("<input type='button' id='overrideButton' value='Override' onclick='override();'>");
			response.getWriter().println("<script type=\"text/javascript\" src=\"" + basePath 
					+ "scripts/labelMaker.js?ver=" + version + "\" ></script>");
		} else {
			response.setContentType("application/pdf");
			response.addHeader("Content-Disposition", "inline; filename=" + "sample.pdf");
			response.setContentLength((int) labelAsOutputStream.size()); 
			labelAsOutputStream.writeTo(response.getOutputStream());
			response.getOutputStream().flush();
			response.getOutputStream().close();
		}
	}

	private ActionMessages validate(String labNo, String patientId, String type, String quantity, String override) {
		ActionMessages errors = new ActionMessages();
		//Validate quantity
		if (!GenericValidator.isInt(quantity)) {
			ActionError error = new ActionError("barcode.label.error.quantity.invalid");
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		//Validate type
		if (!"default".equals(type) && !"order".equals(type) && !"specimen".equals(type)
				&& !"blank".equals(type)) {
			ActionError error = new ActionError("barcode.label.error.type.invalid");
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		//Validate patientId
		if (!GenericValidator.isInt(patientId)) {
			ActionError error = new ActionError("barcode.label.error.patientid.invalid");
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		//Validate labNo
		IAccessionNumberValidator accessionNumberValidator = AccessionNumberUtil.getAccessionNumberValidator();
		String accessionNumber;
		String sampleItemNumber;
		if (labNo.indexOf(".") > 0) {
			accessionNumber = labNo.substring(0, labNo.indexOf("."));
			sampleItemNumber = labNo.substring(labNo.indexOf(".") + 1);
		} else if (labNo.indexOf("-") > 0) {
			accessionNumber = labNo.substring(0, labNo.indexOf("-"));
			sampleItemNumber = labNo.substring(labNo.indexOf("-") + 1);
		} else {
			accessionNumber = labNo;
			sampleItemNumber = "0";
		}
		if (!(IAccessionNumberValidator.ValidationResults.SUCCESS == 
				accessionNumberValidator.validFormat(accessionNumber, false)) || !GenericValidator.isInt(sampleItemNumber)) {
			ActionError error = new ActionError("barcode.label.error.accession.invalid");
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		if (!GenericValidator.isBool(override)) {
			//ActionError error = new ActionError("barcode.label.error.override.invalid");
			//errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			override = "false";
		}
		 
		return errors;
	}
	
	private void createLabels(ArrayList<Label> labels, String labNo, String patientId, String type, String quantity, String override, HttpServletRequest request) {
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		if ("default".equals(type)) {
			//add 2 order label per default
			SampleDAO sampleDAO = new SampleDAOImpl();
			Sample sample = sampleDAO.getSampleByAccessionNumber(labNo);
			OrderLabel orderLabel = new OrderLabel(getPatientForID(patientId), sample, labNo);
			orderLabel.setNumLabels(2);
			orderLabel.linkBarcodeLabelInfo();
			//get sysUserId from login module
			orderLabel.setSysUserId(String.valueOf(usd.getSystemUserId()));
			if (orderLabel.checkIfPrintable() || "true".equals(override)) {
				labels.add(orderLabel);
			}
			//1 specimen label per sampleitem
			SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
			List<SampleItem> sampleItemList = sampleItemDAO.getSampleItemsBySampleIdAndStatus(sample.getId(), ENTERED_STATUS_SAMPLE_LIST);
			for (SampleItem sampleItem : sampleItemList) {
				SpecimenLabel specLabel = new SpecimenLabel(getPatientForID(patientId), sample, sampleItem, labNo);
				specLabel.setNumLabels(1);
				specLabel.linkBarcodeLabelInfo();
				//get sysUserId from login module
				specLabel.setSysUserId(String.valueOf(usd.getSystemUserId()));
				if (specLabel.checkIfPrintable() || "true".equals(override)) {
					labels.add(specLabel);
				}
			}
		} else if ("order".equals(type)) {
			SampleDAO sampleDAO = new SampleDAOImpl();
			Sample sample = sampleDAO.getSampleByAccessionNumber(labNo);
			OrderLabel orderLabel = new OrderLabel(getPatientForID(patientId), sample, labNo);
			orderLabel.setNumLabels(Integer.parseInt(quantity));
			orderLabel.linkBarcodeLabelInfo();
			//get sysUserId from login module
			orderLabel.setSysUserId(String.valueOf(usd.getSystemUserId()));
			if (orderLabel.checkIfPrintable() || "true".equals(override)) {
				labels.add(orderLabel);
			}
		} else if ("specimen".equals(type)) {
			String specimenNumber = labNo.substring(labNo.lastIndexOf("-") + 1);
			labNo = labNo.substring(0, labNo.lastIndexOf("-"));
			SampleDAO sampleDAO = new SampleDAOImpl();
			SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
			Sample sample = sampleDAO.getSampleByAccessionNumber(labNo);
			List<SampleItem> sampleItemList = sampleItemDAO.getSampleItemsBySampleIdAndStatus(sample.getId(), ENTERED_STATUS_SAMPLE_LIST);
			for (SampleItem sampleItem : sampleItemList) {
				if (sampleItem.getSortOrder().equals(specimenNumber)) {
					SpecimenLabel specLabel = new SpecimenLabel(getPatientForID(patientId), sample, sampleItem, labNo);
					specLabel.setNumLabels(Integer.parseInt(quantity));
					specLabel.linkBarcodeLabelInfo();
					//get sysUserId from login module
					specLabel.setSysUserId(String.valueOf(usd.getSystemUserId()));
					if (specLabel.checkIfPrintable() || "true".equals(override)) {
						labels.add(specLabel);
					}
				}
			}
		} else if ("blank".equals(type)) {
			BlankLabel blankLabel = new BlankLabel(labNo);
			blankLabel.linkBarcodeLabelInfo();
			//get sysUserId from login module
			blankLabel.setSysUserId(String.valueOf(usd.getSystemUserId()));
			if (blankLabel.checkIfPrintable() || "true".equals(override)) {
				labels.add(blankLabel);
			}
		}
	}
	
	private Patient getPatientForID(String personKey) {
		Patient patient = new Patient();
		patient.setId(personKey);
		PatientDAO dao = new PatientDAOImpl();
		dao.getData(patient);
		if (patient.getId() == null) 
		    return null;
		else 
		    return patient;
	}
	
}
