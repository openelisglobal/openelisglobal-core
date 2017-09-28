package us.mn.state.health.lims.common.servlet.barcode;

import javax.servlet.ServletException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.*;
import javax.servlet.http.*;


import us.mn.state.health.lims.barcode.LabelMaker;


public class LabelMakerServlet extends HttpServlet {
	
	

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws IOException, ServletException {
		
		String barcode = request.getParameter("barcode");
		LabelMaker labelMaker = new LabelMaker();
		File pdfFile = labelMaker.createLabel(barcode);

		response.setContentType("application/pdf");
		response.addHeader("Content-Disposition", "inline; filename=" + "sample.pdf");
		response.setContentLength((int) pdfFile.length()); 
		FileInputStream fileInputStream = new FileInputStream(pdfFile);
		OutputStream responseOutputStream = response.getOutputStream();
		int bytes;
		while ((bytes = fileInputStream.read()) != -1) {
			responseOutputStream.write(bytes);
		}
	}
}
