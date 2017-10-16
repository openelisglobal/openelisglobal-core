package us.mn.state.health.lims.barcode.labeltype;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.barcode.BarcodeLabelField;
import us.mn.state.health.lims.common.services.PatientService;
import us.mn.state.health.lims.common.services.TestService;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.ConfigurationProperties.Property;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;

public class SpecimenLabel extends Label {
	
	
	public SpecimenLabel(Patient patient, Sample sample, SampleItem sampleItem, String labNo) {
		//set dimensions
		String strWidth = ConfigurationProperties.getInstance().getPropertyValue(Property.SPECIMEN_BARCODE_WIDTH);
		String strHeight = ConfigurationProperties.getInstance().getPropertyValue(Property.SPECIMEN_BARCODE_HEIGHT);
		width = Float.parseFloat(strWidth);
		height = Float.parseFloat(strHeight);
		
		//getting fields for above barcode
		Person person = patient.getPerson();
		BarcodeLabelField patientIdField = getAvailableId(patient);
		String referringFacility = StringUtil.replaceNullWithEmptyString(sample.getReferringId()); 
		String patientName = StringUtil.replaceNullWithEmptyString(person.getFirstName()) + " " 
				+ StringUtil.replaceNullWithEmptyString(person.getMiddleName()) + " " 
				+ StringUtil.replaceNullWithEmptyString(person.getLastName());
		patientName = patientName.replaceAll("( )+", " ");
		String dob = StringUtil.replaceNullWithEmptyString(patient.getBirthDateForDisplay());
		
		//adding fields above barcode
		aboveFields = new ArrayList<BarcodeLabelField>();
		aboveFields.add(patientIdField);
		aboveFields.add(new BarcodeLabelField(StringUtil.getMessageForKey("barcode.label.info.site"), StringUtils.substring(referringFacility, 0, 20), 4));
		aboveFields.add(new BarcodeLabelField(StringUtil.getMessageForKey("barcode.label.info.patientname"), StringUtils.substring(patientName, 0, 30), 7));
		aboveFields.add(new BarcodeLabelField(StringUtil.getMessageForKey("barcode.label.info.patientdob"), dob, 3));
		
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
		belowFields.add(new BarcodeLabelField(StringUtil.getMessageForKey("barcode.label.info.collectiondate"), collectionDate, 3));
		belowFields.add(new BarcodeLabelField(StringUtil.getMessageForKey("barcode.label.info.collectiontime"), StringUtil.replaceNullWithEmptyString(collectionTime), 2));
		belowFields.add(new BarcodeLabelField(StringUtil.getMessageForKey("barcode.label.info.patientsex"), StringUtil.replaceNullWithEmptyString(patient.getGender()), 1));
		belowFields.add(new BarcodeLabelField(StringUtil.getMessageForKey("barcode.label.info.collectorid"), StringUtils.substring(StringUtil.replaceNullWithEmptyString(collector), 0, 15), 4));
		belowFields.add(new BarcodeLabelField(StringUtil.getMessageForKey("barcode.label.info.tests"), StringUtil.replaceNullWithEmptyString(tests), 10));
		
		//making code
		String sampleCode = sampleItem.getSortOrder();
		setCode(labNo + "." + sampleCode);
	}

	//get first available id for patient
	private BarcodeLabelField getAvailableId(Patient patient) {
		PatientService service = new PatientService(patient);
		String patientId = service.getSTNumber();
		if (!StringUtil.isNullorNill(patientId))
			return new BarcodeLabelField(StringUtil.getMessageForKey("barcode.label.info.patientid"), StringUtils.substring(patientId, 0, 25), 6);
		patientId = patient.getNationalId();
		if (!StringUtil.isNullorNill(patientId))
			return new BarcodeLabelField(StringUtil.getMessageForKey("barcode.label.info.patientid"), StringUtils.substring(patientId, 0, 25), 6);
		return new BarcodeLabelField(StringUtil.getMessageForKey("barcode.label.info.patientid"), "", 6);
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
		String max = ConfigurationProperties.getInstance().getPropertyValue(Property.MAX_SPECIMEN_PRINTED);
		return Integer.parseInt(max);
	}
	
}
