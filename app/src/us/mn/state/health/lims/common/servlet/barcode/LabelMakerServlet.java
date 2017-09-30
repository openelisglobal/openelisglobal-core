package us.mn.state.health.lims.common.servlet.barcode;

import javax.servlet.ServletException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.http.*;

import org.apache.commons.lang.StringUtils;

import us.mn.state.health.lims.barcode.BarcodeLabelMaker;
import us.mn.state.health.lims.barcode.labeltype.OrderLabel;
import us.mn.state.health.lims.barcode.labeltype.BlankLabel;
import us.mn.state.health.lims.barcode.labeltype.SpecimenLabel;


public class LabelMakerServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws IOException, ServletException {
		String barcode = request.getParameter("barcode");
		if (StringUtils.isNotEmpty(barcode)) {
			BarcodeLabelMaker labelMaker = new BarcodeLabelMaker(barcode);
			ByteArrayOutputStream labelAsOutputStream = labelMaker.createLabelAsStream();
			response.setContentType("application/pdf");
			response.addHeader("Content-Disposition", "inline; filename=" + "sample.pdf");
			response.setContentLength((int) labelAsOutputStream.size()); 
			labelAsOutputStream.writeTo(response.getOutputStream());
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		
	}
	
}
