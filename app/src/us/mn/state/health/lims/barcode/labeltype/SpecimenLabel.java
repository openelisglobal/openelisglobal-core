package us.mn.state.health.lims.barcode.labeltype;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.lowagie.text.Font;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.barcode.valueholder.BarcodeLabelField;
import us.mn.state.health.lims.common.services.TestService;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;

public class SpecimenLabel implements Label {

	private static Font VALUE_FONT =  new Font(Font.HELVETICA, 8, Font.NORMAL);
	private static Font NAME_FONT =  new Font(Font.HELVETICA, 8, Font.BOLD);
	//height width only define ratio
	private static int HEIGHT = 1;
	private static int WIDTH = 3;
	private static int MARGIN = 5;
	private static int BARCODE_SPACE = LARGE_BARCODE;

	private ArrayList<BarcodeLabelField> fields;
	private ArrayList<BarcodeLabelField> belowFields;
	private String code;
	
	private int numLabels = 1;
	
	public SpecimenLabel(String patientId, String patientName, String referringFacility, String dob, String code) {
		fields = new ArrayList<BarcodeLabelField>();
		fields.add(new BarcodeLabelField("Patient Id", StringUtils.substring(patientId, 0, 25), 6));
		fields.add(new BarcodeLabelField("Site", StringUtils.substring(referringFacility, 0, 20), 4));
		fields.add(new BarcodeLabelField("Patient Name", StringUtils.substring(patientName, 0, 30), 7));
		fields.add(new BarcodeLabelField("DOB", dob, 3));
		this.code = code;
		belowFields = new ArrayList<BarcodeLabelField>();
		belowFields.add(new BarcodeLabelField("Date", "", 2));
		belowFields.add(new BarcodeLabelField("Time", "", 2));
		belowFields.add(new BarcodeLabelField("Sex", "", 2));
		belowFields.add(new BarcodeLabelField("Collector Id:", "", 4));
		belowFields.add(new BarcodeLabelField("Tests", "", 10));
	}
	
	public SpecimenLabel(Patient patient, SampleItem sampleItem, String code) {
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
		fields = new ArrayList<BarcodeLabelField>();
		fields.add(patientIdField);
		fields.add(new BarcodeLabelField("Site", StringUtils.substring(referringFacility, 0, 20), 4));
		fields.add(new BarcodeLabelField("Patient Name", StringUtils.substring(patientName, 0, 30), 7));
		fields.add(new BarcodeLabelField("DOB", dob, 3));
		//getting fields for below barcode
		Timestamp timestamp = sampleItem.getCollectionDate();
		String collectionDate = DateUtil.convertTimestampToStringDate(timestamp);
		String collectionTime = DateUtil.convertTimestampToStringTime(timestamp);
		AnalysisDAO analysisDAO = new AnalysisDAOImpl();
		String collector = sampleItem.getCollector();
		String tests = "";
		List<Analysis> analysisList = analysisDAO.getAnalysesBySampleItem(sampleItem);
		for (Analysis analysis : analysisList) {
			tests += StringUtils.substring(TestService.getUserLocalizedTestName(analysis.getTest()), 0, 5) + " ";
		}
		//adding fields below barcode
		belowFields = new ArrayList<BarcodeLabelField>();
		belowFields.add(new BarcodeLabelField("Date", collectionDate, 3));
		belowFields.add(new BarcodeLabelField("Time", collectionTime, 2));
		belowFields.add(new BarcodeLabelField("Sex", StringUtil.replaceNullWithEmptyString(patient.getGender()), 1));
		belowFields.add(new BarcodeLabelField("Collector Id", StringUtils.substring(StringUtil.replaceNullWithEmptyString(collector), 0, 15), 4));
		belowFields.add(new BarcodeLabelField("Tests", StringUtil.replaceNullWithEmptyString(tests), 10));
		this.code = code;
	}
	
	public BarcodeLabelField getAvailableId(Patient patient) {
		String patientId = patient.getId();
		if (!StringUtil.isNullorNill(patientId))
			return new BarcodeLabelField("Patient Id", StringUtils.substring(patientId, 0, 25), 6);
		patientId = patient.getNationalId();
		if (!StringUtil.isNullorNill(patientId))
			return new BarcodeLabelField("Patient Id", StringUtils.substring(patientId, 0, 25), 6);
		return new BarcodeLabelField("Patient Id", "", 6);
	}

	public Iterable<BarcodeLabelField> getFields() {
		return fields;
	}
	
	public Iterable<BarcodeLabelField> getBelowFields() {
		return belowFields;
	}

	
	public String getCode() {
		return code;
	}

	public Font getValueFont() {
		return VALUE_FONT;
	}

	public Font getNameFont() {
		return NAME_FONT;
	}

	public int getHeight() {
		return HEIGHT;
	}

	public int getWidth() {
		return WIDTH;
	}

	public int getMargin() {
		return MARGIN;
	}
	
	public int getBarcodeSpace() {
		return BARCODE_SPACE * 2;
	}

	public int getNumLabels() {
		return numLabels;
	}

	public void setNumLabels(int num) {
		numLabels = num;
	}
	
}
