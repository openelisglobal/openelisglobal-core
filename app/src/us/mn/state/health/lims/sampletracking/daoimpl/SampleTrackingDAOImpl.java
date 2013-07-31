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
package us.mn.state.health.lims.sampletracking.daoimpl;

import java.util.List;

import org.hibernate.Query;

import us.mn.state.health.lims.common.daoimpl.BaseDAOImpl;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.sampleproject.dao.SampleProjectDAO;
import us.mn.state.health.lims.sampleproject.daoimpl.SampleProjectDAOImpl;
import us.mn.state.health.lims.sampleproject.valueholder.SampleProject;
import us.mn.state.health.lims.sampletracking.dao.SampleTrackingDAO;
import us.mn.state.health.lims.sampletracking.valueholder.SampleTracking;
import us.mn.state.health.lims.sampletracking.valueholder.SampleTrackingCriteria;

/**
 * @author aiswarya raman
 * //AIS - bugzilla 1851/1853
 *  bugzilla 1920 - standards
 */
public class SampleTrackingDAOImpl extends BaseDAOImpl implements SampleTrackingDAO {

	public List getAccessionByPatientAndOtherCriteria(SampleTrackingCriteria sampleTrackingCriteria) throws LIMSRuntimeException {
		
		List samples = null;
		
		try {
	
			StringBuffer sqlb = new StringBuffer();
			
			String accNumString = "";
			SampleTracking sample = null;	
					
			sqlb.append("Select ST from SampleTracking ST Where 1=1 ");		
			
			if (!StringUtil.isNullorNill(sampleTrackingCriteria.getClientRef())){ 				         		 
					sqlb.append("AND upper(ST.cliRef) LIKE upper(:param1) ");  
				}			
			if (!StringUtil.isNullorNill(sampleTrackingCriteria.getLastName())){				
					sqlb.append("AND upper(ST.patientLastName) LIKE upper(:param2) " ); 
				}				
			if (!StringUtil.isNullorNill(sampleTrackingCriteria.getFirstName())){ 				
				sqlb.append("AND upper (ST.patientFirstName) LIKE upper(:param3) ") ;
			}			
			if (!StringUtil.isNullorNill(sampleTrackingCriteria.getSubmitter())){ 				         		 
			//bugzilla 2069
				sqlb.append("AND ST.organizationLocalAbbreviation = (:param4) ");			
			}
			if (!StringUtil.isNullorNill(sampleTrackingCriteria.getReceivedDate())){			
				sqlb.append("AND ST.recdDate = (:param5) "); 			
			}	
			if (!StringUtil.isNullorNill(sampleTrackingCriteria.getSampleType())){				
				sqlb.append("AND ST.tosId = (:param6) "); 
				}			
			if (!StringUtil.isNullorNill(sampleTrackingCriteria.getSampleSource())){ 				
				sqlb.append("AND ST.sosId= (:param7) ");
			}
			if (!StringUtil.isNullorNill(sampleTrackingCriteria.getExternalId())){ 
				sqlb.append("AND upper(ST.patientId) LIKE upper(:param8) ");				
			}
			if (!StringUtil.isNullorNill(sampleTrackingCriteria.getCollectionDate())){
				sqlb.append("AND to_char(ST.collDate, 'mm/dd/YYYY') = (:param9) "); 			
			}	
			
			if (!StringUtil.isNullorNill(sampleTrackingCriteria.getAccessionNumberPartial())){			
				sqlb.append("AND ST.accNum LIKE (:param10) "); 			
			}
			
			//bugzilla 2455
			if (!StringUtil.isNullorNill(sampleTrackingCriteria.getSpecimenOrIsolate())){			
				sqlb.append("AND ST.specOrIsolate LIKE upper(:param11) "); 			
			}
			
			if (!StringUtil.isNullorNill(sampleTrackingCriteria.getProjectId())){			
				//do the list of accnums for this proj id				
				SampleProjectDAO sampleProjectDAO = new SampleProjectDAOImpl();
				List sampleProjects = sampleProjectDAO.getSampleProjectsByProjId(sampleTrackingCriteria.getProjectId());
				
				if (sampleProjects != null){					
					for (int i=0; i<sampleProjects.size(); i++) {
						SampleProject sampleProject = new SampleProject(); 
						sampleProject = (SampleProject)sampleProjects.get(i);
						if (i==0){
							accNumString = accNumString + sampleProject.getSample().getAccessionNumber();						
						}else{
							accNumString = accNumString + "," +sampleProject.getSample().getAccessionNumber();							
						}					
					}				
				}else{
					//there is no sample exists with this projid
					accNumString ="-1";					
				}
				sqlb.append("AND ST.accNum IN ("+accNumString+") " ); 
			}	

				
			if (!StringUtil.isNullorNill(sampleTrackingCriteria.getSortBy())){
				
				String SortByDetail ="";				
				if (sampleTrackingCriteria.getSortBy().equalsIgnoreCase("0")){
					SortByDetail = "accnum";					
				}else if(sampleTrackingCriteria.getSortBy().equalsIgnoreCase("1")){
				//bugzilla 2069
					SortByDetail = "orglocalabbrev";					
				}else if (sampleTrackingCriteria.getSortBy().equalsIgnoreCase("2")){					
					SortByDetail = "recddate";
				}else if (sampleTrackingCriteria.getSortBy().equalsIgnoreCase("3")){
					SortByDetail = "colldate";					
				}else if (sampleTrackingCriteria.getSortBy().equalsIgnoreCase("4")){
					SortByDetail = "tosdesc";					
				}else if (sampleTrackingCriteria.getSortBy().equalsIgnoreCase("5")){
					SortByDetail = "sosdesc";					
				}else if (sampleTrackingCriteria.getSortBy().equalsIgnoreCase("6")){
					SortByDetail = "patientlastname";										
				}else if (sampleTrackingCriteria.getSortBy().equalsIgnoreCase("7")){
					SortByDetail = "patientfirstname";					
				}else if (sampleTrackingCriteria.getSortBy().equalsIgnoreCase("8")){					
					SortByDetail = "cliref";
				}else if (sampleTrackingCriteria.getSortBy().equalsIgnoreCase("9")){
					SortByDetail = "dateofbirth";
				}				
				
				sqlb.append("ORDER BY "+SortByDetail);						
			}
			
		    String sql = sqlb.toString();  	    		
			Query query = HibernateUtil.getSession().createQuery(sql);	
						
			if (!StringUtil.isNullorNill(sampleTrackingCriteria.getClientRef())){ 
				query.setParameter("param1", sampleTrackingCriteria.getClientRef()+"%");
			}			
			if (!StringUtil.isNullorNill(sampleTrackingCriteria.getLastName())){ 
				query.setParameter("param2", sampleTrackingCriteria.getLastName()+"%");
			}			
			if (!StringUtil.isNullorNill(sampleTrackingCriteria.getFirstName())){ 
				query.setParameter("param3", sampleTrackingCriteria.getFirstName()+"%");		
			}			
			if (!StringUtil.isNullorNill(sampleTrackingCriteria.getSubmitter())){ 
				query.setParameter("param4", sampleTrackingCriteria.getSubmitter());
			}			
			if (!StringUtil.isNullorNill(sampleTrackingCriteria.getReceivedDate())){ 				
				String locale = SystemConfiguration.getInstance()
				.getDefaultLocale().toString();
		        java.sql.Date convertedReceivedDate = DateUtil
				.convertStringDateToSqlDate(sampleTrackingCriteria.getReceivedDate(), locale);		        
				query.setParameter("param5", convertedReceivedDate);				
			}			
			if (!StringUtil.isNullorNill(sampleTrackingCriteria.getSampleType())){ 
				query.setParameter("param6", sampleTrackingCriteria.getSampleType());
			}			
			if (!StringUtil.isNullorNill(sampleTrackingCriteria.getSampleSource())){ 
				query.setParameter("param7", sampleTrackingCriteria.getSampleSource());		
			}
			if (!StringUtil.isNullorNill(sampleTrackingCriteria.getExternalId())){ 			
				query.setParameter("param8", sampleTrackingCriteria.getExternalId()+"%");	
			}
			if (!StringUtil.isNullorNill(sampleTrackingCriteria.getCollectionDate())){ 						        
				query.setParameter("param9", sampleTrackingCriteria.getCollectionDate());				
			}
			
			if (!StringUtil.isNullorNill(sampleTrackingCriteria.getAccessionNumberPartial())){
				query.setParameter("param10", sampleTrackingCriteria.getAccessionNumberPartial()+"%");						
			}	
			
			//bugzilla 2455
			if (!StringUtil.isNullorNill(sampleTrackingCriteria.getSpecimenOrIsolate())){
				query.setParameter("param11", sampleTrackingCriteria.getSpecimenOrIsolate()+"%");						
			}
				
			
			//System.out.println("Search Query\n"+  query.getQueryString());
					
			samples = query.list();				
			
			return samples;
			//if ((list != null) &&
					//!list.isEmpty()){		
			
			//samples = new SampleTracking[list.size()]; 
			
			//for (int i = 0; i < list.size(); i++) {	
				//sample = (SampleTracking)list.get(i);
				//samples[i] = sample;				
			//}			
			//return samples;
			
			//}else {		
				//return samplesfornull;
			//}
			
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SampleTrackingDAOImpl","getAccessionByPatientAndOtherCriteria()",e.toString());	
			throw new LIMSRuntimeException("Error in SampleTracking getAccessionByPatientAndOtherCriteria()", e);
		}
		
	}



}
