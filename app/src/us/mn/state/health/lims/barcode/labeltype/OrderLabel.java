package us.mn.state.health.lims.barcode.labeltype;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import us.mn.state.health.lims.barcode.BarcodeLabelField;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.person.valueholder.Person;

public class OrderLabel extends Label {
	
	
	public OrderLabel(Patient patient, String labNo) {
		//get information for above barcode
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
		
		//adding code
		setCode(labNo);
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
		int numRows = 0;
		int curColumns = 0;
		boolean completeRow = true;
		Iterable<BarcodeLabelField> fields = getAboveFields();
		for (BarcodeLabelField field : fields) {
			//add to num row if start on newline
			if (field.isStartNewline() && !completeRow) {
				++numRows;
				curColumns = 0;
			}
			curColumns += field.getColspan();
			if (curColumns > 10) {
				//throw error
				//row is completed, add to num row
			} else if (curColumns == 10) {
				completeRow = true;
				curColumns = 0;
				++numRows;
			} else {
				completeRow = false;
			}
		}
		//add to num row if last row was incomplete
		if (!completeRow) {
			++numRows;
		}
		
		return numRows;
	}

	@Override
	public int getNumTextRowsAfter() {
		return 0;
	}

	@Override
	public int getMaxNumLabels() {
		return 10;
	}

}
