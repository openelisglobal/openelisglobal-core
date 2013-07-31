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
* Copyright (C) The Minnesota Department of Health.  All Rights Reserved.
*/
package us.mn.state.health.lims.samplepdf.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import phl.util.Crypto;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.log.LogEvent;

/**
 *  @author     Hung Nguyen (Hung.Nguyen@health.state.mn.us)
 */
public class ViewPDFAction extends BaseAction {

    protected ActionForward performAction( ActionMapping mapping, ActionForm form,
    							  HttpServletRequest request, HttpServletResponse response )
        throws IOException, ServletException {
    	
    	String fileSampleID = request.getParameter(ACCESSION_NUMBER);
    	File tempPDFFile = null; 
    	String tempFile = null;
    	String tempFileName = fileSampleID;
    	ActionErrors errors = new ActionErrors();
		String page = FWD_SUCCESS;

		try {			
			String folderName = getFileFolder(request,fileSampleID,errors);
			if ( folderName != null  ) {
				String PDF_DIR = SystemConfiguration.getInstance().getEncryptedPdfPath() + "/" + folderName;
				fileSampleID = PDF_DIR+fileSampleID+".PDF.encrypted";
				tempFile = fileSampleID + ".temp.pdf";
				tempPDFFile = new File(fileSampleID + ".temp.pdf"); 
				
				Crypto crypto = new Crypto();
			    crypto.decryptFile(new FileInputStream(fileSampleID),new FileOutputStream(tempPDFFile)); 
			    
				request.setAttribute(TEMP_PDF_FILE, tempFile);
				request.setAttribute(ACCESSION_NUMBER, tempFileName);
			}
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("ViewPDFAction","performAction()",e.toString());	
		} 
		return (mapping.findForward(page)); 
    }
    
    /**
     * Parsing the filename to get the corrected folder (last 3 digits)
     * example: 200090004 will return the string "004/00/4/"
     * @param fileName the name of the file
     * @param errors the ActionErrors
     * @return folder
     */
    private String getFileFolder(HttpServletRequest request, String fileName, ActionErrors errors) {		
        String firstFolder = fileName.substring(fileName.length()-3);
        String secondFolder = firstFolder.substring(0,firstFolder.length()-1);
        String thirdFolder = firstFolder.substring(2,firstFolder.length());

        try {
        	Integer.parseInt(firstFolder);
        	Integer.parseInt(secondFolder);
        	Integer.parseInt(thirdFolder);
        } catch (Exception e) {
        	//bugzilla 2154
			LogEvent.logError("ViewPDFAction","getFileFolder()",e.toString());
            return null;
        }
        return firstFolder + "/" + secondFolder + "/" + thirdFolder + "/";
    }    
   
	protected String getPageTitleKey() {
		return "human.sample.pdf.link";
	}

	protected String getPageSubtitleKey() {
		return "human.sample.pdf.link";
	}
 }