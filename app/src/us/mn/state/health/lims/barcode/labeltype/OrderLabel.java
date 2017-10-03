package us.mn.state.health.lims.barcode.labeltype;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import com.lowagie.text.Font;

import us.mn.state.health.lims.barcode.valueholder.BarcodeLabelField;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.person.valueholder.Person;

public class OrderLabel implements Label {

	private static Font VALUE_FONT =  new Font(Font.HELVETICA, 8, Font.NORMAL);
	private static Font NAME_FONT =  new Font(Font.HELVETICA, 8, Font.BOLD);
	//height width only define ratio
	private static int HEIGHT = 1;
	private static int WIDTH = 3;
	private static int MARGIN = 5;
	private static int BARCODE_SPACE = LARGE_BARCODE;
	
	private ArrayList<BarcodeLabelField> fields;
	private String code;
	
	private int numLabels = 1;
	
	public OrderLabel(String patientId, String patientName, String referringFacility, String dob, String code) {
		fields = new ArrayList<BarcodeLabelField>();
		fields.add(new BarcodeLabelField("Patient Id", StringUtils.substring(patientId, 0, 25), 6));
		fields.add(new BarcodeLabelField("Site", StringUtils.substring(referringFacility, 0, 20), 4));
		fields.add(new BarcodeLabelField("Patient Name", StringUtils.substring(patientName, 0, 30), 7));
		fields.add(new BarcodeLabelField("DOB", dob, 3));
		this.code = code;
	}
	
	public OrderLabel(Patient patient, String code) {
		Person person = patient.getPerson();
		BarcodeLabelField patientIdField = getAvailableId(patient);
		String referringFacility = ""; //unsure where info is stored or how to get
		String patientName = StringUtil.replaceNullWithEmptyString(person.getFirstName()) + " " 
				+ StringUtil.replaceNullWithEmptyString(person.getMiddleName()) + " " 
				+ StringUtil.replaceNullWithEmptyString(person.getLastName());
		patientName = patientName.replaceAll("( )+", " ");
		String dob = StringUtil.replaceNullWithEmptyString(patient.getBirthDateForDisplay());
		
		fields = new ArrayList<BarcodeLabelField>();
		fields.add(patientIdField);
		fields.add(new BarcodeLabelField("Site", StringUtils.substring(referringFacility, 0, 20), 4));
		fields.add(new BarcodeLabelField("Patient Name", StringUtils.substring(patientName, 0, 30), 7));
		fields.add(new BarcodeLabelField("DOB", dob, 3));
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
	
	public ArrayList<BarcodeLabelField> getBelowFields() {
		return null;
	}
	
	public ArrayList<BarcodeLabelField> getFields() {
		return fields;
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
