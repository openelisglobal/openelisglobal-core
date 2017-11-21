package us.mn.state.health.lims.barcode.labeltype;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.barcode.BarcodeLabelField;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.services.PatientService;
import us.mn.state.health.lims.common.services.SampleOrderService;
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
		SampleOrderService sampleOrderService = new SampleOrderService( sample );
		try {
			width = Float.parseFloat(strWidth);
			height = Float.parseFloat(strHeight);
		} catch (Exception e) {
			LogEvent.logError("SpecimenLabel","SpecimenLabel SpecimenLabel()",e.toString());
		}
		
		//getting fields for above barcode
		Person person = patient.getPerson();
		String referringFacility = StringUtil.replaceNullWithEmptyString(
				sampleOrderService.getSampleOrderItem().getReferringSiteName());
		String patientName = StringUtil.replaceNullWithEmptyString(person.getLastName()) + ", " 
				+ StringUtil.replaceNullWithEmptyString(person.getFirstName());
		if (patientName.trim().equals(",")) {
			patientName = " ";
		}
		patientName = patientName.replaceAll("( )+", " ");
		String dob = StringUtil.replaceNullWithEmptyString(patient.getBirthDateForDisplay());
		BarcodeLabelField field;
		
		//adding fields above barcode
		aboveFields = new ArrayList<BarcodeLabelField>();
		aboveFields.add(new BarcodeLabelField(StringUtil.getMessageForKey("barcode.label.info.patientname"), StringUtils.substring(patientName, 0, 30), 6));
		aboveFields.add(new BarcodeLabelField(StringUtil.getMessageForKey("barcode.label.info.patientdob"), dob, 4));
		aboveFields.add(getAvailableIdField(patient));
		field = new BarcodeLabelField(StringUtil.getMessageForKey("barcode.label.info.site"), StringUtils.substring(referringFacility, 0, 20), 4);
		field.setDisplayFieldName(true);
		aboveFields.add(field);
		
		//getting fields for below barcode
		Timestamp timestamp = sampleItem.getCollectionDate();
		String collectionDate = DateUtil.convertTimestampToStringDate(timestamp);
		String collectionTime = DateUtil.convertTimestampToStringTime(timestamp);
		AnalysisDAO analysisDAO = new AnalysisDAOImpl();
		String collector = sampleItem.getCollector();
		StringBuilder tests = new StringBuilder();
		String seperator = "";
		List<Analysis> analysisList = analysisDAO.getAnalysesBySampleItem(sampleItem);
		for (Analysis analysis : analysisList) {
			tests.append(seperator);
			tests.append(TestService.getUserLocalizedTestName(analysis.getTest()));
			seperator = ", ";
		}
		//adding fields below barcode (based on configuration)
		belowFields = new ArrayList<BarcodeLabelField>();
		String useDateTime = ConfigurationProperties.getInstance().getPropertyValue(Property.SPECIMEN_FIELD_DATE);
		String useSex = ConfigurationProperties.getInstance().getPropertyValue(Property.SPECIMEN_FIELD_SEX);
		String useTests = ConfigurationProperties.getInstance().getPropertyValue(Property.SPECIMEN_FIELD_TESTS);
		if ("true".equals(useSex)) {
			field = new BarcodeLabelField(StringUtil.getMessageForKey("barcode.label.info.patientsex"),
					StringUtil.replaceNullWithEmptyString(patient.getGender()), 2);
			field.setDisplayFieldName(true);
			belowFields.add(field);
		}
		if ("true".equals(useDateTime)) {
			field = new BarcodeLabelField(StringUtil.getMessageForKey("barcode.label.info.collectiondate"), collectionDate, 3);
			field.setDisplayFieldName(true);
			belowFields.add(field);
			field = new BarcodeLabelField(StringUtil.getMessageForKey("barcode.label.info.collectiontime"),
					StringUtil.replaceNullWithEmptyString(collectionTime), 2);
			belowFields.add(field);
		}
		field = new BarcodeLabelField(StringUtil.getMessageForKey("barcode.label.info.collectorid"),
				StringUtils.substring(StringUtil.replaceNullWithEmptyString(collector), 0, 15), 3);
		field.setDisplayFieldName(true);
		belowFields.add(field);
		if ("true".equals(useTests)) {
			BarcodeLabelField testsField = new BarcodeLabelField(StringUtil.getMessageForKey("barcode.label.info.tests"),
					StringUtil.replaceNullWithEmptyString(tests.toString()), 10);
			testsField.setStartNewline(true);
			belowFields.add(testsField);
		}
		
		//making code
		String sampleCode = sampleItem.getSortOrder();
		setCode(labNo + "." + sampleCode);
	}
	
	//get first available id for patient
	private BarcodeLabelField getAvailableIdField(Patient patient) {
		PatientService service = new PatientService(patient);
		String patientId = service.getSubjectNumber();
		if (!StringUtil.isNullorNill(patientId))
			return new BarcodeLabelField(StringUtil.getMessageForKey("barcode.label.info.patientid"), StringUtils.substring(patientId, 0, 25), 6);
		patientId = service.getNationalId();
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
		String strMax = ConfigurationProperties.getInstance().getPropertyValue(Property.MAX_SPECIMEN_PRINTED);
		int max = 0;
		try {
			max = Integer.parseInt(strMax);
		} catch (Exception e) {
			LogEvent.logError("SpecimenLabel","SpecimenLabel getMaxNumLabels()",e.toString());
		}
		return max;
	}
	
}
