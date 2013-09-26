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
 *
 * Contributor(s): CIRG, University of Washington, Seattle WA.
 */
package us.mn.state.health.lims.workplan.action;

import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.formfields.FormFields;
import us.mn.state.health.lims.common.formfields.FormFields.Field;
import us.mn.state.health.lims.common.services.IPatientService;
import us.mn.state.health.lims.common.services.PatientService;
import us.mn.state.health.lims.common.services.StatusService;
import us.mn.state.health.lims.common.services.StatusService.AnalysisStatus;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.ConfigurationProperties.Property;
import us.mn.state.health.lims.result.action.util.ResultsLoadUtility;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseWorkplanAction extends BaseAction {

	public enum WorkplanType {
		UNKNOWN, 
		TEST, 
		PANEL,
		IMMUNOLOGY, 
		HEMATO_IMMUNOLOGY,
		HEMATOLOGY, 
		BIOCHEMISTRY, 
		SEROLOGY, 
		VIROLOGY,
		CHEM, 
		BACTERIOLOGY, 
		PARASITOLOGY, 
		IMMUNO, 
		ECBU, 
		HIV, 
		MYCROBACTERIOLOGY, 
		MOLECULAR_BIOLOGY, 
		LIQUID_BIOLOGY, 
		ENDOCRINOLOGY, 
		CYTOBACTERIOLOGY,
		MYCOLOGY,
		SEROLOGY_IMMUNOLOGY,
        MALARIA
	}

	protected WorkplanType workplanType = WorkplanType.UNKNOWN;
	private TypeNameGroup typeNameGroup;
	private static Map<String, TypeNameGroup> workplanGroupMap = new HashMap<String, TypeNameGroup>();
	
	protected final TestDAO testDAO = new TestDAOImpl();

	protected ResultsLoadUtility resultsLoadUtility = new ResultsLoadUtility();

	protected static List<Integer> statusList;
	protected static boolean useReceptionTime = FormFields.getInstance().useField(Field.SampleEntryUseReceptionHour);
	protected List<String> nfsTestIdList = new ArrayList<String>();

	static{
		workplanGroupMap.put("test", new TypeNameGroup(null, "workplan.test.title", WorkplanType.TEST));
		workplanGroupMap.put("panel", new TypeNameGroup(null, "workplan.panel.title", WorkplanType.TEST));
		workplanGroupMap.put("immunology", new TypeNameGroup("Immunology", "workplan.immunology.title", WorkplanType.IMMUNOLOGY));
		workplanGroupMap.put("immuno", new TypeNameGroup("Immunology", "workplan.immunology.title", WorkplanType.IMMUNOLOGY));
		workplanGroupMap.put("hematology", new TypeNameGroup("Hematology", "workplan.hematology.title", WorkplanType.HEMATOLOGY));
		workplanGroupMap.put("biochemistry", new TypeNameGroup("Biochemistry", "workplan.biochemistry.title", WorkplanType.BIOCHEMISTRY));
		workplanGroupMap.put("chem", new TypeNameGroup("Biochemistry", "workplan.biochemistry.title", WorkplanType.CHEM));
		workplanGroupMap.put("serology", new TypeNameGroup("Serology", "workplan.serology.title", WorkplanType.SEROLOGY));
		workplanGroupMap.put("serologie", new TypeNameGroup("Serologie", "workplan.serology.title", WorkplanType.SEROLOGY));
		workplanGroupMap.put("virology", new TypeNameGroup("Virology", "workplan.virology.title", WorkplanType.VIROLOGY));
		workplanGroupMap.put("virologie", new TypeNameGroup("Virologie", "workplan.virology.title", WorkplanType.VIROLOGY));
		workplanGroupMap.put("bacteriology", new TypeNameGroup("Bacteria", "workplan.bacteriology.title", WorkplanType.BACTERIOLOGY));
		workplanGroupMap.put("parasitology", new TypeNameGroup("Parasitology", "workplan.parasitology.title", WorkplanType.PARASITOLOGY));
		workplanGroupMap.put("ECBU", new TypeNameGroup("ECBU", "workplan.ebcu.title", WorkplanType.ECBU));
		workplanGroupMap.put("cytobacteriology", new TypeNameGroup("Cytobacteriologie", "workplan.cytobacteriology.title", WorkplanType.CYTOBACTERIOLOGY));
		workplanGroupMap.put("molecularBio", new TypeNameGroup("Biologie Moleculaire", "workplan.molecularBio.title", WorkplanType.MOLECULAR_BIOLOGY));
		workplanGroupMap.put("liquidBio", new TypeNameGroup("Liquides biologique", "workplan.liquidBio.title", WorkplanType.LIQUID_BIOLOGY));
		workplanGroupMap.put("mycrobacteriology", new TypeNameGroup("Mycobacteriology", "workplan.mycrobacteriology.title", WorkplanType.MYCROBACTERIOLOGY));
		workplanGroupMap.put("mycology", new TypeNameGroup("mycology", "workplan.mycology.title", WorkplanType.MYCOLOGY));
		workplanGroupMap.put("endocrin", new TypeNameGroup("Endocrinologie", "workplan.endocrin.title", WorkplanType.ENDOCRINOLOGY));
		workplanGroupMap.put("HIV", new TypeNameGroup("VCT", "workplan.vct.title", WorkplanType.HIV));
		workplanGroupMap.put("hemato-immunology", new TypeNameGroup("Hemto-Immunology", "workplan.hemato.imunology.title", WorkplanType.HEMATO_IMMUNOLOGY));
        workplanGroupMap.put("serology-immunology", new TypeNameGroup("Serology-Immunology", "workplan.serology.immunology.title", WorkplanType.SEROLOGY_IMMUNOLOGY));
        workplanGroupMap.put("malaria", new TypeNameGroup("Malaria", "workplan.malaria.title", WorkplanType.MALARIA));
		
		
		statusList = new ArrayList<Integer>();
		statusList.add(Integer.parseInt(StatusService.getInstance().getStatusID(AnalysisStatus.NotStarted)));
		statusList.add(Integer.parseInt(StatusService.getInstance().getStatusID(AnalysisStatus.BiologistRejected)));
		statusList.add(Integer.parseInt(StatusService.getInstance().getStatusID(AnalysisStatus.TechnicalRejected)));
		statusList.add(Integer.parseInt(StatusService.getInstance().getStatusID(AnalysisStatus.NonConforming_depricated)));
		
	}



	@Override
	protected ActionForward performAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String forward = FWD_SUCCESS;

		return mapping.findForward(forward);
	}

	@Override
	protected String getPageTitleKey() {
		return typeNameGroup.getKey();
	}

	@Override
	protected String getPageSubtitleKey() {
		return getPageTitleKey();
	}

	protected void setRequestType(String workplan) {
		if (!GenericValidator.isBlankOrNull(workplan)) {
			typeNameGroup = workplanGroupMap.get(workplan);
			workplanType = typeNameGroup.getWorkplanType();
		}
	}


	protected void setNFSTestIdList() {
		nfsTestIdList = new ArrayList<String>();
		nfsTestIdList.add(getTestId("GB"));
		nfsTestIdList.add(getTestId("Neut %"));
		nfsTestIdList.add(getTestId("Lymph %"));
		nfsTestIdList.add(getTestId("Mono %"));
		nfsTestIdList.add(getTestId("Eo %"));
		nfsTestIdList.add(getTestId("Baso %"));
		nfsTestIdList.add(getTestId("GR"));
		nfsTestIdList.add(getTestId("Hb"));
		nfsTestIdList.add(getTestId("HCT"));
		nfsTestIdList.add(getTestId("VGM"));
		nfsTestIdList.add(getTestId("TCMH"));
		nfsTestIdList.add(getTestId("CCMH"));
		nfsTestIdList.add(getTestId("PLQ"));
	}

	protected boolean allNFSTestsRequested(List<String> testIdList) {
		return (testIdList.containsAll(nfsTestIdList));

	}

	protected String getTestId(String testName) {
		Test test = new Test();
		test.setTestName(testName);
		test = testDAO.getTestByName(test);
		return test.getId();

	}

	protected String getSubjectNumber( Analysis analysis){
		if( ConfigurationProperties.getInstance().isPropertyValueEqual(Property.SUBJECT_ON_WORKPLAN, "true")){
		  IPatientService patientService = new PatientService(analysis.getSampleItem().getSample());
		  return patientService.getSubjectNumber();
		}else{
			return "";
		}
	}
	
	protected String getTestSectionName() {
		return typeNameGroup.getName();
	}

	protected String getReceivedDateDisplay(Sample sample){
		String receptionTime = useReceptionTime ? " " + sample.getReceivedTimeForDisplay() : "";
		return sample.getReceivedDateForDisplay() + receptionTime;
	}
	
	static class TypeNameGroup{
		private String name;
		private String key;
		private WorkplanType workplanType;
		
		TypeNameGroup( String name, String key, WorkplanType workplanType){
			this.name = name;
			this.key = key;
			this.workplanType = workplanType;
		}

		public String getName() {
			return name;
		}


		public String getKey() {
			return key;
		}

		public WorkplanType getWorkplanType() {
			return workplanType;
		}

	}
}
