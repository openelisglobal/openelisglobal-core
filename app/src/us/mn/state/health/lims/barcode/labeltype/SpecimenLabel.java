package us.mn.state.health.lims.barcode.labeltype;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.barcode.BarcodeLabelField;
import us.mn.state.health.lims.common.services.TestService;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;

public class SpecimenLabel extends Label {
	
	
	public SpecimenLabel(Patient patient, SampleItem sampleItem, String labNo) {
		//getting fields for above barcode
		Person person = patient.getPerson();
		BarcodeLabelField patientIdField = getAvailableId(patient);
		String referringFacility = ""; //unsure where info is stored or how to get
		String patientName = StringUtil.replaceNullWithEmptyString(person.getFirstName()) + " " 
				+ StringUtil.replaceNullWithEmptyString(person.getMiddleName()) + " " 
				+ StringUtil.replaceNullWithEmptyString(person.getLastName());
		patientName = patientName.replaceAll("( )+", " ");
		String dob = StringUtil.replaceNullWithEmptyString(patient.getBirthDateForDisplay());
		//adding fields above barcode
		aboveFields = new ArrayList<BarcodeLabelField>();
		aboveFields.add(patientIdField);
		aboveFields.add(new BarcodeLabelField("Site", StringUtils.substring(referringFacility, 0, 20), 4));
		aboveFields.add(new BarcodeLabelField("Patient Name", StringUtils.substring(patientName, 0, 30), 7));
		aboveFields.add(new BarcodeLabelField("DOB", dob, 3));
		
		//getting fields for below barcode
		Timestamp timestamp = sampleItem.getCollectionDate();
		String collectionDate = DateUtil.convertTimestampToStringDate(timestamp);
		String collectionTime = DateUtil.convertTimestampToStringTime(timestamp);
		AnalysisDAO analysisDAO = new AnalysisDAOImpl();
		String collector = sampleItem.getCollector();
		String tests = "";
		List<Analysis> analysisList = analysisDAO.getAnalysesBySampleItem(sampleItem);
		for (Analysis analysis : analysisList) {
			tests += TestService.getUserLocalizedTestName(analysis.getTest()) + " ";
		}
		//adding fields below barcode
		belowFields = new ArrayList<BarcodeLabelField>();
		belowFields.add(new BarcodeLabelField("Date", collectionDate, 3));
		belowFields.add(new BarcodeLabelField("Time", StringUtil.replaceNullWithEmptyString(collectionTime), 2));
		belowFields.add(new BarcodeLabelField("Sex", StringUtil.replaceNullWithEmptyString(patient.getGender()), 1));
		belowFields.add(new BarcodeLabelField("Collector Id", StringUtils.substring(StringUtil.replaceNullWithEmptyString(collector), 0, 15), 4));
		belowFields.add(new BarcodeLabelField("Tests", StringUtil.replaceNullWithEmptyString(tests), 10));
		
		//making code
		String sampleCode = sampleItem.getId();
		setCode(labNo + "." + sampleCode);
	}

	//get first available id for patient
	private BarcodeLabelField getAvailableId(Patient patient) {
		String patientId = patient.getId();
		if (!StringUtil.isNullorNill(patientId))
			return new BarcodeLabelField("Patient Id", StringUtils.substring(patientId, 0, 25), 6);
		patientId = patient.getNationalId();
		if (!StringUtil.isNullorNill(patientId))
			return new BarcodeLabelField("Patient Id", StringUtils.substring(patientId, 0, 25), 6);
		return new BarcodeLabelField("Patient Id", "", 6);
	}
	
	@Override
	public int getNumTextRowsBefore() {
		Iterable<BarcodeLabelField> fields = getAboveFields();
		return getNumRows(fields);
	}

	@Override
	public int getNumTextRowsAfter() {
		Iterable<BarcodeLabelField> fields = getBelowFields();
		return getNumRows(fields);
	}

	@Override
	public int getMaxNumLabels() {
		return 1;
	}
	
}
