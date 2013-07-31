<%@ page import="us.mn.state.health.lims.common.action.IActionConstants"%>
<%@ page import="java.io.*"%>
<%@ page import="phl.util.*"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>

<%

	String fileName = (String)request.getAttribute(IActionConstants.TEMP_PDF_FILE);
	String accessionNumber = (String)request.getAttribute(IActionConstants.ACCESSION_NUMBER);	
	File tempFile = new File(fileName); 
	
	//bugzilla 2131
    response.setContentType ("application/pdf");
    response.setDateHeader ("Expires", 0);
    response.setHeader("Content-disposition","inline; filename="+accessionNumber+".pdf");

	InputStream in = new FileInputStream(tempFile);
	int bit = in.read();
	try {
		while ((bit) >= 0) {
			//bit = in.read();
			out.write(bit);
			bit = in.read();
		}
	    out.flush();
	    out.close();
	    in.close();	    
	    tempFile.delete();
	} catch (IOException ioe) {}
%>