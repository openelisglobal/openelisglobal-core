/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/ 
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) ITECH, University of Washington, Seattle WA.  All Rights Reserved.
*
*/
package us.mn.state.health.lims.common.servlet.reports;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.validator.GenericValidator;


public class LogoUploadServlet extends HttpServlet {


    static final long serialVersionUID = 1L;
    
    private static final String FILE_PATH = File.separator + "WEB-INF" + File.separator + "reports" + File.separator + "images" + File.separator + "labLogo.jpg";
    private static final String PREVIEW_FILE_PATH = File.separator + "images" + File.separator + "labLogo.jpg";
    private static final int MAX_MEMORY_SIZE = 1024 * 1024 * 2;
    private static final int MAX_REQUEST_SIZE = 1024 * 1024;
 
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // Check that we have a file upload request
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
 
        if (!isMultipart) {
            return;
        }
 
        DiskFileItemFactory factory = new DiskFileItemFactory();
 
        factory.setSizeThreshold(MAX_MEMORY_SIZE);
 
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
        
        String uploadFullPath = getServletContext().getRealPath("") + FILE_PATH;
        String uploadPreviewPath = getServletContext().getRealPath("") + PREVIEW_FILE_PATH;
 
        ServletFileUpload upload = new ServletFileUpload(factory);
 
        upload.setSizeMax(MAX_REQUEST_SIZE);
        
        try {
            @SuppressWarnings("unchecked")
			List<FileItem> items = upload.parseRequest(request);
           
            for( FileItem item : items) {
 
                if (validToWrite(item)) {

                    File uploadedFile = new File(uploadFullPath);
                    
                    item.write(uploadedFile);
                    
                    File previewFile = new File(uploadPreviewPath);
                    
                    item.write(previewFile);
                    
                    break;
                }
            }
            
            getServletContext().getRequestDispatcher("/PrintedReportsConfigurationMenu.do").forward(request, response);
            
        } catch (FileUploadException ex) {
            throw new ServletException(ex);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }       
    }

	private boolean validToWrite(FileItem item) {
		return !item.isFormField() &&
				item.getSize() > 0 &&
				!GenericValidator.isBlankOrNull(item.getName()) &&
				( item.getName().contains("jpg") || item.getName().contains("png") || item.getName().contains("gif"));
	}
	
}
