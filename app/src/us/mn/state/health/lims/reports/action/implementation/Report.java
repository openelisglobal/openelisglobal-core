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
* Copyright (C) CIRG, University of Washington, Seattle WA.  All Rights Reserved.
*
*/
package us.mn.state.health.lims.reports.action.implementation;

import static org.apache.commons.validator.GenericValidator.isBlankOrNull;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.xml.ws.Response;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;

import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.ConfigurationProperties.Property;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.organization.dao.OrganizationDAO;
import us.mn.state.health.lims.organization.daoimpl.OrganizationDAOImpl;
import us.mn.state.health.lims.organization.valueholder.Organization;
import us.mn.state.health.lims.reports.action.implementation.reportBeans.ErrorMessages;


public abstract class Report implements IReportCreator {
    
    public static final String CI_ERROR_REPORT = "NoticeOfReportError";
    public static final String HAITI_ERROR_REPORT = "HaitiNoticeOfReportError";
    
	protected static final String CSV = "csv";
	protected static final String EXCEL = "excel";
	protected static final String PDF = "pdf";

	protected boolean initialized = false;
    protected boolean errorFound = false;
    protected List<ErrorMessages> errorMsgs = new ArrayList<ErrorMessages>();
    protected HashMap<String, Object> reportParameters = null;
    protected boolean useLogo = ConfigurationProperties.getInstance().isPropertyValueEqual(Property.useLogoInReport, "true");
    private String fullReportFilename;
    
	protected void initializeReport() {
		initialized = true;
	}

	public String getResponseHeaderName(){
		return null;
	}
	public String getResponseHeaderContent(){
		return null;
	}
	
	protected Collection<String> getExportOptions() {
		List<String> exportList = new ArrayList<String>();

		exportList.add(PDF);
		exportList.add(EXCEL);
		exportList.add(CSV);

		return exportList;
	}

    /**
     * @see us.mn.state.health.lims.reports.action.implementation.IReportCreator#getContentType()
     */
    public String getContentType() {
        return "application/pdf; charset=UTF-8";
    }

    /**
     * Make sure we have a reportParameters map and make sure there is lab director in that map (for any possible error report).
     * All reports need a director name either in their header including or on their error report page."
     */
    protected void createReportParameters() {
        reportParameters = (reportParameters != null) ? reportParameters : new HashMap<String, Object>();
        reportParameters.put("directorName", ConfigurationProperties.getInstance().getPropertyValue(Property.labDirectorName));
        reportParameters.put("siteName", ConfigurationProperties.getInstance().getPropertyValue(Property.SiteName));
        reportParameters.put("additionalSiteInfo", ConfigurationProperties.getInstance().getPropertyValue(Property.ADDITIONAL_SITE_INFO));
        reportParameters.put("usePageNumbers", ConfigurationProperties.getInstance().getPropertyValue(Property.USE_PAGE_NUMBERS_ON_REPORTS));
    }

    /**
     * Nearly every report does PDF this routine does that, but some do other things like export to CSV which involves messing with the response and the headers
     * @param response response, so that appropriate things can be set into it.
     * @param fullReportFilename - valid full path to write the report
     * @throws JRException
     * @throws IllegalStateException
     * @see us.mn.state.health.lims.reports.action.implementation.IReportCreator#runReport(java.lang.String, Response)
     */
    @Override
    public byte[] runReport( ) throws Exception {
        return JasperRunManager.runReportToPdf(fullReportFilename, getReportParameters(), getReportDataSource()); 
    }


    /**
     * @see us.mn.state.health.lims.reports.action.implementation.IReportCreator#getReportDataSource()
     */
    public abstract JRDataSource getReportDataSource() throws IllegalStateException;

    public HashMap<String, ?> getReportParameters() throws IllegalStateException {
        if (!initialized) {
            throw new IllegalStateException("initializeReport not called first");
        }
        return reportParameters;
    }

    /**
     * Utility routine for a sequence done in many places.
     * Adds a message to the errorMsgs
     * @param messageId - name of resource
     */
    protected void add1LineErrorMessage(String messageId) {
        errorFound = true;
        ErrorMessages msgs = new ErrorMessages();
        msgs.setMsgLine1(StringUtil.getMessageForKey(messageId));
        errorMsgs.add(msgs);
    }

    /**
     * Utility routine for a sequence done in many places.
     * Adds a message to the errorMsgs
     * @param messageId - name of resource
     */
    protected void add1LineErrorMessage(String messageId, String more) {
        errorFound = true;
        ErrorMessages msgs = new ErrorMessages();
        msgs.setMsgLine1(StringUtil.getMessageForKey(messageId) + more);
        errorMsgs.add(msgs);
    }
    
    /**
     * Checks a given date to make sure it is ok, filling in with a default if not found, logging a message, if there is a problem.
     * @param checkDateStr - date to check
     * @param defaultDateStr - will use this date if the 1st one is null or blank.
     * @param badDateMessage - message to report if the date is bad (blank or not valid form).
     * @return
     */
    protected Date validateDate(String checkDateStr, String defaultDateStr, String badDateMessage) {
        checkDateStr = (isBlankOrNull(checkDateStr))?defaultDateStr:checkDateStr;
        Date checkDate;
        if (isBlankOrNull(checkDateStr)) {
            add1LineErrorMessage(badDateMessage);
            return null;
        }

        try {
            checkDate = DateUtil.convertStringDateToSqlDate(checkDateStr);
        } catch (LIMSRuntimeException re) {
            add1LineErrorMessage("report.error.message.date.format", " " + checkDateStr);
            return null;
        }
        return checkDate;
    }

    /**
     * @return true, if location is not blank or "0" is is found in the DB; false otherwise
     */
    protected Organization getValidOrganization(String locationStr) {
        if (isBlankOrNull(locationStr) || "0".equals(Integer.decode(locationStr))) {
            add1LineErrorMessage("report.error.message.location.missing");
            return null;
        }
        OrganizationDAO dao = new OrganizationDAOImpl();
        Organization org = dao.getOrganizationById(locationStr);
        if (org == null) {
            add1LineErrorMessage("report.error.message.location.missing");
            return null;
        }
        return org;
    }

    /**
     * @see us.mn.state.health.lims.reports.action.implementation.IReportCreator#getReportFileName()
     */
    public String getReportFileName() {
        return errorFound ? errorReportFileName() : reportFileName();
    }

    public class DateRange {
        private String lowDateStr;
        private String highDateStr;
        private Date lowDate;
        private Date highDate;

        public Date getLowDate() {
            return lowDate;
        }
        public Date getHighDate() {
            return highDate;
        }
        
        /**
         * If you need to compare a Date which started as a date string to a bunch of timestamps, you should move it from 00:00 at the beginning of the day 
         * to the end of the day at 23:59:59.999.
         * @return the high date with time set to the end of the day.
         */
        public Date getHighDateAtEndOfDay() {
            // not perfect in areas with Daylight Savings Time. Will over shoot on the spring forward day and undershoot on the fall back day.
            Date newDate = new Date(highDate.getTime() + 24*60*60*1000);
            return newDate;
        }

        public DateRange(String lowDateStr, String highDateStr) {
            this.lowDateStr = lowDateStr;
            this.highDateStr = highDateStr;
        }
        /**
         * <ol>
         * <li>High date picks up low date if it ain't filled in,
         * <li>they can't both be empty
         * <li>they have to be well formed.
         *
         * @return
         */
        public boolean validateHighLowDate(String missingDateMessage) {
            lowDate = validateDate(lowDateStr,  null, missingDateMessage );
            highDate = validateDate(highDateStr, lowDateStr, missingDateMessage);
            if (lowDate == null || highDate == null ) {
                return false;
            }
            if (highDate.getTime() < lowDate.getTime()) {
                Date tmpDate = highDate;
                highDate = lowDate;
                lowDate = tmpDate;
                
                String tmpString = highDateStr;
                highDateStr = lowDateStr;
                lowDateStr = tmpString;
            }
            return true;
        }
        
        public String toString() {
            String range = lowDateStr;
            try {
                if ( !GenericValidator.isBlankOrNull(highDateStr)) {
                    range += "  -  " + highDateStr;
                }
            } catch (Exception e) {
            }
            return range;
        }
    }
    
    public void setReportPath( String path){
    	fullReportFilename =  path + getReportFileName() + ".jasper";
    }
    
    public List<String> getReportedOrders(){
    	return new ArrayList<String>();
    }
    protected abstract String errorReportFileName();
    protected abstract String reportFileName();
}
