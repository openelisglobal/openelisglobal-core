package us.mn.state.health.lims.common.servlet.barcode;

import javax.servlet.ServletException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.barcode.BarcodeLabelMaker;
import us.mn.state.health.lims.barcode.labeltype.OrderLabel;
import us.mn.state.health.lims.barcode.labeltype.BlankLabel;
import us.mn.state.health.lims.barcode.labeltype.Label;
import us.mn.state.health.lims.barcode.labeltype.SpecimenLabel;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.samplehuman.dao.SampleHumanDAO;
import us.mn.state.health.lims.samplehuman.daoimpl.SampleHumanDAOImpl;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;


public class LabelMakerServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws IOException, ServletException {
		//get parameters
		String barcode = request.getParameter("barcode");
		String patientId = request.getParameter("patientId");
		String type = request.getParameter("type");
		if (StringUtils.isEmpty(barcode))
			barcode = (String) request.getSession().getAttribute("lastAccessionNumber");
		if (StringUtils.isEmpty(patientId))
			patientId = (String) request.getSession().getAttribute("lastPatientId");
		if (StringUtils.isEmpty(type))
			type = "default";
		
		ArrayList<Label> labels = new ArrayList<Label>();
		if ("default".equals(type)) {
			//add 2 order label per default
			OrderLabel orderLabel = new OrderLabel(getPatientForID(patientId), barcode);
			orderLabel.setNumLabels(2);
			labels.add(orderLabel);
			//1 specimen label per sampleitem
			SampleDAO sampleDAO = new SampleDAOImpl();
			SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
			Sample sample = sampleDAO.getSampleByAccessionNumber(barcode);
			List<SampleItem> sampleItemList = sampleItemDAO.getSampleItemsBySampleId(sample.getId());
			for (SampleItem sampleItem : sampleItemList) {
				SpecimenLabel specLabel = new SpecimenLabel(getPatientForID(patientId), sampleItem, barcode);
				specLabel.setNumLabels(1);
				labels.add(specLabel);
			}
		} else if ("blank".equals(type)) {
			//logic for printing blank labels here
		} else if ("aliquot".equals(type)) {
			//logic for aliquots here
		}
		BarcodeLabelMaker labelMaker = new BarcodeLabelMaker(labels);
		ByteArrayOutputStream labelAsOutputStream = labelMaker.createLabelAsStream();
		response.setContentType("application/pdf");
		response.addHeader("Content-Disposition", "inline; filename=" + "sample.pdf");
		response.setContentLength((int) labelAsOutputStream.size()); 
		labelAsOutputStream.writeTo(response.getOutputStream());
		response.getOutputStream().flush();
		response.getOutputStream().close();
				
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
