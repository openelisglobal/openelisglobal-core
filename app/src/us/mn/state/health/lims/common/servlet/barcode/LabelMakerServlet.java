package us.mn.state.health.lims.common.servlet.barcode;

import javax.servlet.ServletException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.*;

import org.apache.commons.lang.StringUtils;

import us.mn.state.health.lims.barcode.BarcodeLabelMaker;
import us.mn.state.health.lims.barcode.labeltype.OrderLabel;
import us.mn.state.health.lims.barcode.labeltype.BlankLabel;
import us.mn.state.health.lims.barcode.labeltype.Label;
import us.mn.state.health.lims.barcode.labeltype.SpecimenLabel;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.services.StatusService;
import us.mn.state.health.lims.common.services.StatusService.SampleStatus;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.dao.UserModuleDAO;
import us.mn.state.health.lims.login.daoimpl.UserModuleDAOImpl;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import us.mn.state.health.lims.siteinformation.dao.SiteInformationDAO;
import us.mn.state.health.lims.siteinformation.daoimpl.SiteInformationDAOImpl;
import us.mn.state.health.lims.siteinformation.valueholder.SiteInformation;

public class LabelMakerServlet extends HttpServlet {

private static final long serialVersionUID = 4756240897909804141L;
private static final Set<Integer> ENTERED_STATUS_SAMPLE_LIST = new HashSet<Integer>();

static {
	ENTERED_STATUS_SAMPLE_LIST.add( Integer.parseInt( StatusService.getInstance().getStatusID( SampleStatus.Entered ) ) );
}

private boolean validate(String labNo, String patientId, String type, String quantity) {
	boolean valid = true;
	if (!StringUtil.isInteger(quantity)) 
		valid = false;
	else if (!"default".equals(type) && !"order".equals(type) && !"specimen".equals(type) 
			&& !"blank".equals(type) && !"aliquot".equals(type))
		valid = false;
	//validate patientID
	else if (StringUtil.isNullorNill(patientId)) 
		valid = false;
	else if (StringUtil.isNullorNill(labNo))
		valid = false;
	return valid;
}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws IOException, ServletException {
		//check for authentication
		UserModuleDAO userModuleDAO = new UserModuleDAOImpl();
		if (userModuleDAO.isSessionExpired(request)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			System.out.println("Invalid request - no active session found");
			return;
		}
		//get parameters
		String labNo = request.getParameter("labNo");
		String patientId = request.getParameter("patientId");
		String type = request.getParameter("type");
		String quantity = request.getParameter("quantity");
		if (StringUtils.isEmpty(labNo))
			labNo = (String) request.getSession().getAttribute("lastAccessionNumber");
		if (StringUtils.isEmpty(patientId))
			patientId = (String) request.getSession().getAttribute("lastPatientId");
		if (StringUtils.isEmpty(type))
			type = "default";
		if (StringUtils.isEmpty(quantity))
			quantity = "1";
		if (!validate(labNo, patientId, type, quantity)) {
			//reject request
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;			
		}
		//assemble requested labels
		ArrayList<Label> labels = new ArrayList<Label>();
		createLabels(labels, labNo, patientId, type, quantity);
		
		BarcodeLabelMaker labelMaker = new BarcodeLabelMaker(labels);
		ByteArrayOutputStream labelAsOutputStream = labelMaker.createLabelsAsStream();
		
		if (labelAsOutputStream.size() == 0) {
			response.getWriter().println(StringUtil.getMessageForKey("barcode.message.maxreached"));
		} else {
			response.setContentType("application/pdf");
			response.addHeader("Content-Disposition", "inline; filename=" + "sample.pdf");
			response.setContentLength((int) labelAsOutputStream.size()); 
			labelAsOutputStream.writeTo(response.getOutputStream());
			response.getOutputStream().flush();
			response.getOutputStream().close();
		}
	}
	
	private void createLabels(ArrayList<Label> labels, String labNo, String patientId, String type, String quantity) {
		if ("default".equals(type)) {
			//add 2 order label per default
			SampleDAO sampleDAO = new SampleDAOImpl();
			Sample sample = sampleDAO.getSampleByAccessionNumber(labNo);
			OrderLabel orderLabel = new OrderLabel(getPatientForID(patientId), sample, labNo);
			orderLabel.setNumLabels(2);
			orderLabel.linkBarcodeLabelInfo();
			if (orderLabel.checkIfPrintable()) {
				labels.add(orderLabel);
			}
			//1 specimen label per sampleitem
			SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
			List<SampleItem> sampleItemList = sampleItemDAO.getSampleItemsBySampleIdAndStatus(sample.getId(), ENTERED_STATUS_SAMPLE_LIST);
			for (SampleItem sampleItem : sampleItemList) {
				SpecimenLabel specLabel = new SpecimenLabel(getPatientForID(patientId), sample, sampleItem, labNo);
				specLabel.setNumLabels(1);
				specLabel.linkBarcodeLabelInfo();
				if (specLabel.checkIfPrintable()) {
					labels.add(specLabel);
				}
			}
		} else if ("order".equals(type)) {
			SampleDAO sampleDAO = new SampleDAOImpl();
			Sample sample = sampleDAO.getSampleByAccessionNumber(labNo);
			OrderLabel orderLabel = new OrderLabel(getPatientForID(patientId), sample, labNo);
			orderLabel.setNumLabels(Integer.parseInt(quantity));
			orderLabel.linkBarcodeLabelInfo();
			if (orderLabel.checkIfPrintable()) {
				labels.add(orderLabel);
			}
		} else if ("specimen".equals(type)) {
			SampleDAO sampleDAO = new SampleDAOImpl();
			SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
			Sample sample = sampleDAO.getSampleByAccessionNumber(labNo);
			List<SampleItem> sampleItemList = sampleItemDAO.getSampleItemsBySampleIdAndStatus(sample.getId(), ENTERED_STATUS_SAMPLE_LIST);
			for (SampleItem sampleItem : sampleItemList) {
				SpecimenLabel specLabel = new SpecimenLabel(getPatientForID(patientId), sample, sampleItem, labNo);
				specLabel.setNumLabels(Integer.parseInt(quantity));
				specLabel.linkBarcodeLabelInfo();
				if (specLabel.checkIfPrintable()) {
					labels.add(specLabel);
				}
			}
		} else if ("blank".equals(type)) {
			BlankLabel blankLabel = new BlankLabel(labNo);
			blankLabel.linkBarcodeLabelInfo();
			if (blankLabel.checkIfPrintable()) {
				labels.add(blankLabel);
			}
		} else if ("aliquot".equals(type)) {
			//logic for aliquots here
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
