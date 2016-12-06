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
package us.mn.state.health.lims.reports.action.implementation.reportBeans;

import static us.mn.state.health.lims.reports.action.implementation.reportBeans.CSVColumnBuilder.Strategy.*;

//import org.apache.commons.validator.GenericValidator;

//import us.mn.state.health.lims.common.services.TestService;
import us.mn.state.health.lims.observationhistorytype.valueholder.ObservationHistoryType;
import us.mn.state.health.lims.reports.action.implementation.Report.DateRange;
//import us.mn.state.health.lims.test.valueholder.Test;

public class VLColumnBuilder extends CIColumnBuilder {

	public VLColumnBuilder(DateRange dateRange, String projectStr) {
		super(dateRange, projectStr);
	}

	@Override
	protected void defineAllReportColumns() {
        defineBasicColumns();
        add("Viral Load", "Viral Load", NONE );
        add("Viral Load", "Viral Load log", LOG );
        add("started_date"     ,"STARTED_DATE", NONE);
        add("completed_date"     ,"COMPLETED_DATE", NONE);
        add("released_date"     ,"RELEASED_DATE", NONE);
      //  add("patient_oe_id"     ,"PATIENT_OE_ID", NONE);
             
        add("hivStatus"            , "STATVIH", DICT_RAW );
        add("nameOfDoctor"         , "NOMMED", NONE );
        add("nameOfSampler"         , "NOMPRELEV", NONE );
        add("arvTreatmentInitDate"         , "ARV_INIT_DATE", NONE );
        add("arvTreatmentRegime"         , "ARVREG" );
        
        add("currentARVTreatmentINNs1", "CURRENT1",NONE );
        add("currentARVTreatmentINNs2", "CURRENT2",NONE );
        add("currentARVTreatmentINNs3", "CURRENT3",NONE );
        add("currentARVTreatmentINNs4", "CURRENT4",NONE );
        
        add("vlReasonForRequest"         , "VL_REASON" );
        add("vlOtherReasonForRequest"         , "REASON_OTHER", NONE );
        
        add("initcd4Count"         , "INITCD4_COUNT", NONE );
        add("initcd4Percent"         , "INITCD4_PERCENT", NONE );
        add("initcd4Date"         , "INITCD4_DATE", NONE );
        
        add("demandcd4Count"         , "DEMANDCD4_COUNT", NONE );
        add("demandcd4Percent"         , "DEMANDCD4_PERCENT", NONE );
        add("demandcd4Date"         , "DEMANDCD4_DATE", NONE );
         
        add("vlBenefit"         , "PRIOR_VL_BENEFIT",NONE );
        add("priorVLLab"         , "PRIOR_VL_Lab",NONE );
        add("priorVLValue"         , "PRIOR_VL_Value",NONE );
        add("priorVLDate"         , "PRIOR_VL_Date",NONE );
          
        
       // addAllResultsColumns();
        
        
	}

	/**
	 * @return the SQL for (nearly) one big row for each sample in the date range for the particular project.
	 */
	public void makeSQL_2() {/* export by analysis.released_date*/
	
	    query = new StringBuilder();
	    String lowDatePostgres =  postgresDateFormat.format(dateRange.getLowDate());
	    String highDatePostgres = postgresDateFormat.format(dateRange.getHighDate());
	    query.append( "SELECT DISTINCT s.id as sample_id, s.accession_number, s.entered_date, s.received_date, s.collection_date, s.status_id" 
	    + "\n , pat.national_id, pat.external_id, pat.birth_date, per.first_name, per.last_name, pat.gender, pat.id AS patient_oe_id" 
	    + "\n , o.short_name as organization_code, o.name AS organization_name, sp.proj_id as project_id"
	    + "\n , a.sampitem_id,a.started_date,a.completed_date,a.released_date,a.printed_date"
	    + "\n ,a.type_of_sample_name, r.value AS \"Viral Load\",r.analysis_id, currentARVTreatmentINNs.*, demo.*"  
	    + "\n FROM clinlims.analysis AS a,clinlims.result AS r,clinlims.sample_item AS si, clinlims.sample AS s,clinlims.sample_human AS sh, clinlims.patient AS pat"
	    + "\n ,clinlims.person AS per, clinlims.sample_projects AS sp" 
	    + "\n ,clinlims.sample_organization AS so, clinlims.organization AS o,"
	    + "\n  ( SELECT s.id AS samp_id, currentARVTreatmentINNs.*  FROM clinlims.sample AS s LEFT JOIN" 
	    + "\n  crosstab( ' SELECT s.id as s_id, type, value FROM clinlims.Sample AS s"
	    + "\n  LEFT JOIN ( SELECT DISTINCT s.id as s_id , oh.observation_history_type_id AS type, oh.value"
	    + "\n  AS value, oh.id  FROM clinlims.Sample as s, clinlims.Observation_History AS oh"
	    + "\n  WHERE oh.sample_id = s.id AND oh.observation_history_type_id = (select id FROM clinlims.observation_history_type"
	    + "\n  WHERE type_name = ''currentARVTreatmentINNs'')  ORDER by 1,2, oh.id desc ) AS repeatCols ON s.id = repeatCols.s_id"
	    + "\n  ' )"
	    + "\n  AS currentARVTreatmentINNs ( s_id NUMERIC(10) , \"currentARVTreatmentINNs1\" VARCHAR(100), \"currentARVTreatmentINNs2\" VARCHAR(100), \"currentARVTreatmentINNs3\" VARCHAR(100), \"currentARVTreatmentINNs4\" VARCHAR(100) )"
	    + "\n  ON s.id = currentARVTreatmentINNs.s_id ORDER BY 1" 
	    + "\n  ) AS currentARVTreatmentINNs,"
	    + "\n  ( SELECT s.id AS samp_id, demo.*  FROM clinlims.sample AS s LEFT JOIN" 
	    
	    + "\n  crosstab('SELECT DISTINCT oh.sample_id as samp_id, oht.type_name, value" 
	    + "\n  FROM clinlims.observation_history AS oh, clinlims.sample AS s, clinlims.observation_history_type AS oht" 
	    + "\n  WHERE s.id = oh.sample_id AND oh.observation_history_type_id = oht.id order by 1;'" 
	    + "\n  , 'SELECT DISTINCT oht.type_name FROM clinlims.observation_history_type AS oht ORDER BY 1;'" 
	    + "\n  ) " );
	    
	    query.append(" as demo ( " + " \"s_id\"                           numeric(10) ");
		for (ObservationHistoryType oht : allObHistoryTypes) {
			query.append("\n," + oht.getTypeName() + " varchar(100) ");
		}
		query.append(" ) \n");  

		query.append("  ON s.id = demo.s_id  ORDER BY 1" 
	    + "\n  ) AS demo"
	    + "\n WHERE "
	    + "\n a.released_date >= date('" + lowDatePostgres + "')" 
	    + "\n AND a.released_date <= date('" + highDatePostgres + "')" 
	    + "\n AND a.test_id=174 "
	    + "\n AND a.status_id = 18 "
	    + "\n AND a.id=r.analysis_id "
	    + "\n AND a.sampitem_id=si.id "
	    + "\n AND si.samp_id=s.id "
	    + "\n AND s.id=sh.samp_id "
	    + "\n AND sh.patient_id=pat.id "
	    + "\n  AND pat.person_id = per.id"
	    + "\n AND s.id=so.samp_id "
	    + "\n AND so.org_id=o.id "
	    + "\n  AND s.id = sp.samp_id"
	    + "\n  AND s.id = currentARVTreatmentINNs.samp_id"
	    + "\n  AND s.id=demo.s_id"

	    
	    + "\n ORDER BY s.accession_number;");
		

	    return;
	}

	public void makeSQL() {
	    query = new StringBuilder();
	    String lowDatePostgres =  postgresDateFormat.format(dateRange.getLowDate());
	    String highDatePostgres = postgresDateFormat.format(dateRange.getHighDate());
	    query.append( SELECT_SAMPLE_PATIENT_ORGANIZATION );
	    // all crosstab generated tables need to be listed in the following list and in the WHERE clause at the bottom
	    query.append("\n, pat.id AS patient_oe_id, a.started_date,a.completed_date,a.released_date,a.printed_date, r.value as \"Viral Load\", demo.*, currentARVTreatmentINNs.* ");
	
	    // ordinary lab (sample and patient) tables
	    query.append( FROM_SAMPLE_PATIENT_ORGANIZATION +
	    		", clinlims.sample_item as si, clinlims.analysis as a, clinlims.result as r ");
	
	    // all observation history values
	    appendObservationHistoryCrosstab(lowDatePostgres, highDatePostgres);
	    // current ARV treatments
	    appendRepeatingObservation("currentARVTreatmentINNs", 4,  lowDatePostgres, highDatePostgres);
	    //result
	  //  appendResultCrosstab(lowDatePostgres, highDatePostgres );
	
	    // and finally the join that puts these all together. Each cross table should be listed here otherwise it's not in the result and you'll get a full join
	    query.append( " WHERE "             
	    + "\n a.test_id = 174" 
	    + "\n AND a.status_id = 18" 
	    + "\n AND a.id=r.analysis_id"
	    + "\n AND a.sampitem_id = si.id" 
	    + "\n AND s.id = si.samp_id"
	    + "\n AND s.id=sh.samp_id" 
	    + "\n AND sh.patient_id=pat.id" 
	    + "\n AND pat.person_id = per.id"
	    + "\n AND s.id=so.samp_id" 
	    + "\n AND so.org_id=o.id" 
	    + "\n AND s.id = sp.samp_id" 
	    + "\n AND s.id=demo.s_id"
	    + "\n AND s.id = currentARVTreatmentINNs.samp_id"
	    + "\n AND s.collection_date >= date('" + lowDatePostgres + "')" 
	    + "\n AND s.collection_date <= date('" + highDatePostgres + "')"
	    
	    + "\n ORDER BY s.accession_number;");
	    /////////
	    // no don't insert another crosstab or table here, go up before the main WHERE clause

	    return;
	}

}
