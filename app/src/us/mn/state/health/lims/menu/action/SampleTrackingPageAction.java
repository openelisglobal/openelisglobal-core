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
package us.mn.state.health.lims.menu.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.organization.dao.OrganizationDAO;
import us.mn.state.health.lims.organization.daoimpl.OrganizationDAOImpl;
import us.mn.state.health.lims.organization.valueholder.OrganizationComparator;
import us.mn.state.health.lims.project.dao.ProjectDAO;
import us.mn.state.health.lims.project.daoimpl.ProjectDAOImpl;
import us.mn.state.health.lims.sampletracking.dao.SampleTrackingDAO;
import us.mn.state.health.lims.sampletracking.daoimpl.SampleTrackingDAOImpl;
import us.mn.state.health.lims.sourceofsample.dao.SourceOfSampleDAO;
import us.mn.state.health.lims.sourceofsample.daoimpl.SourceOfSampleDAOImpl;
import us.mn.state.health.lims.sourceofsample.valueholder.SourceOfSample;
import us.mn.state.health.lims.sourceofsample.valueholder.SourceOfSampleComparator;
import us.mn.state.health.lims.typeofsample.dao.TypeOfSampleDAO;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleDAOImpl;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSampleComparator;
/**
 * @author aiswarya raman
 * //AIS - bugzilla 1851/1853
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class SampleTrackingPageAction extends BaseAction {

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		
		BaseActionForm searchForm = (BaseActionForm)form;
		searchForm.initialize(mapping);
		
		//System.out.println("I am in SampleTrackingPageAction ");	

		List submitters = new ArrayList();		
		
		OrganizationDAO organizationDAO = new OrganizationDAOImpl();		
		submitters = organizationDAO.getAllOrganizations();				
		
		List types = new ArrayList();
		TypeOfSampleDAO typeofsampleDAO = new TypeOfSampleDAOImpl();		
		types = typeofsampleDAO.getAllTypeOfSamples();
		
		List sources = new ArrayList();
		SourceOfSampleDAO sourceOfSampleDAO = new SourceOfSampleDAOImpl();
		sources = sourceOfSampleDAO.getAllSourceOfSamples();
		
		List projectdetails = new ArrayList();		
		
		ProjectDAO projectDAO = new ProjectDAOImpl();		
		projectdetails = projectDAO.getAllProjects();
		
		List sortby = new ArrayList();	
		SampleTrackingDAO sampletrackingDAO = new SampleTrackingDAOImpl();
	
		sortby = getAllSortByList();		
		
		Collections.sort(submitters, OrganizationComparator.NAME_COMPARATOR);
		Collections.sort(types, TypeOfSampleComparator.NAME_COMPARATOR);
		Collections.sort(sources, SourceOfSampleComparator.NAME_COMPARATOR);
		
		PropertyUtils.setProperty(form, "submitters", submitters);		
		PropertyUtils.setProperty(form, "types", types);
		PropertyUtils.setProperty(form, "sources", sources);
		PropertyUtils.setProperty(form, "projectdetails", projectdetails);
		PropertyUtils.setProperty(form, "sortby", sortby);

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		return "sampletracking.title";
	}

	protected String getPageSubtitleKey() {
		return "sampletracking.title";
	}

	private List getAllSortByList() throws LIMSRuntimeException{		
		
		List<Object> sortobj = new ArrayList<Object>();
		List<String> sortlist = new ArrayList<String>();	
		
		sortlist.add("Accession Number");
		sortlist.add("Submitter");
		sortlist.add("Received Date");
		sortlist.add("Collection Date");
		sortlist.add("Sample Type");
		sortlist.add("Sample Source ");
		sortlist.add("Last Name");
		sortlist.add("First Name");
		sortlist.add("Submitting Lab ID");
		sortlist.add("Date of Birth");	
	
		for (int i=0; i < 10; i++){	
			//Pick any object with 'id' and 'description' to be used in optionsCollection
			SourceOfSample sourceOfSample = new SourceOfSample();
			String idval = Integer.toString(i);
			sourceOfSample.setId(idval);			
			sourceOfSample.setDescription(sortlist.get(i));	
			sortobj.add(sourceOfSample);
		}
	
		return sortobj;
		
	}
}
