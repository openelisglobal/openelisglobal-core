/*
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
*
* Contributor(s): CIRG, University of Washington, Seattle WA.
*/
package us.mn.state.health.lims.reports.action.implementation.reportBeans;


import static us.mn.state.health.lims.reports.action.implementation.reportBeans.CSVColumnBuilder.Strategy.NONE;
import us.mn.state.health.lims.reports.action.implementation.Report.DateRange;

/**
 * @author pahill (pahill@uw.edu)
 * @since May 16, 2011
 */
public class EIDColumnBuilder extends CIColumnBuilder {

    /**
     * @param dateRange
     * @param projectStr
     */
    public EIDColumnBuilder(DateRange dateRange, String projectStr) {
        super(dateRange, projectStr);
    }
    
    /**
     * This is the order we want them in the CSV file.
     */
    protected void defineAllReportColumns() {
        defineBasicColumns();
        
        add("nameOfSampler"     ,"NOMPREV", NONE);
        add("nameOfRequestor"   ,"NOMMED",  NONE);
        add("whichPCR"                ,"whichPCR"                       );     
        add("reasonForSecondPCRTest"  ,"reasonForSecondPCRTest"         );     
        add("eidInfantPTME"           ,"eidInfantPTME"                  );     
        add("eidTypeOfClinic"         ,"eidTypeOfClinic", new EIDTypeOfClinicStrategy()  );     
        add("eidInfantSymptomatic"    ,"eidInfantSymptomatic"           );     
        add("eidMothersHIVStatus"     ,"eidMothersHIVStatus"            );     
        add("eidMothersARV"           ,"eidMothersARV"                  );     
        add("eidInfantsARV"           ,"eidInfantsARV"                  );     
        add("eidInfantCotrimoxazole"  ,"eidInfantCotrimoxazole"         );     
        add("eidHowChildFed"          ,"eidHowChildFed"                 );     
        add("eidStoppedBreastfeeding" ,"eidStoppedBreastfeeding"        );   
        addAllResultsColumns();
    }
    
    /**
     * @return the SQL for (nearly) one big row for each sample in the date range for the particular project.
     */
    public void makeSQL() {
        query = new StringBuilder();
        String lowDatePostgres =  postgresDateFormat.format(dateRange.getLowDate());
        String highDatePostgres = postgresDateFormat.format(dateRange.getHighDate());
        query.append(SELECT_SAMPLE_PATIENT_ORGANIZATION );
        // all crosstab generated tables need to be listed in the SELECT column list and in the WHERE clause at the bottom
        query.append(SELECT_ALL_DEMOGRAPHIC_AND_RESULTS);
        // more cross tabulation of other columns goes where

        // ordinary lab (sample and patient) tables
        query.append(FROM_SAMPLE_PATIENT_ORGANIZATION );

        // all observation history from expressions
        appendObservationHistoryCrosstab(lowDatePostgres, highDatePostgres);
        appendResultCrosstab(lowDatePostgres, highDatePostgres);

        // and finally the join that puts these all together. Each cross table should be listed here otherwise it's not in the result and you'll get a full join
        query.append(buildWhereSamplePatienOrgSQL(lowDatePostgres, highDatePostgres)
                        // insert joining of any other crosstab here.
                        // insert joining of any other crosstab here.
                        + "\n AND s.id = demo.samp_id "
                        + "\n AND s.id = result.samp_id "                        
                        + "\n ORDER BY s.accession_number "
                        );
        // no don't insert another crosstab or table here, go up before the main WHERE clause
        return;
    }    
}
