package us.mn.state.health.lims.common.servlet.barcode;

import javax.servlet.ServletException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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


public class LabelMakerServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws IOException, ServletException {
		String barcode = request.getParameter("barcode");
		String patientId = request.getParameter("patientId");
		int numCopies = Integer.parseInt(request.getParameter("copies"));
		//must have barcode number to generate barcode
		if (StringUtils.isNotEmpty(barcode)) {
			//if patientId present, generate barcode from patient info
			if (StringUtils.isNotEmpty(patientId)) {
				Label label = new SpecimenLabel(getPatientForID(patientId), barcode);
				BarcodeLabelMaker labelMaker = new BarcodeLabelMaker(label);
				ByteArrayOutputStream labelAsOutputStream = labelMaker.createLabelAsStream();
				response.setContentType("application/pdf");
				response.addHeader("Content-Disposition", "inline; filename=" + "sample.pdf");
				response.setContentLength((int) labelAsOutputStream.size()); 
				labelAsOutputStream.writeTo(response.getOutputStream());
				response.getOutputStream().flush();
				response.getOutputStream().close();
			} else {
			
				BarcodeLabelMaker labelMaker = new BarcodeLabelMaker(barcode);
				ByteArrayOutputStream labelAsOutputStream = labelMaker.createLabelAsStream();
				response.setContentType("application/pdf");
				response.addHeader("Content-Disposition", "inline; filename=" + "sample.pdf");
				response.setContentLength((int) labelAsOutputStream.size()); 
				labelAsOutputStream.writeTo(response.getOutputStream());
				response.getOutputStream().flush();
				response.getOutputStream().close();
			}
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		
	}
	
	private Patient getPatientForID(String personKey) {

		Patient patient = new Patient();
		patient.setId(personKey);

		PatientDAO dao = new PatientDAOImpl();

		dao.getData(patient);
		if (patient.getId() == null)  {
		    return null;
		} else {
		    return patient;
		}
	}
	
}
