package us.mn.state.health.lims.barcode.labeltype;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import us.mn.state.health.lims.barcode.BarcodeLabelField;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.services.PatientService;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.ConfigurationProperties.Property;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.sample.valueholder.Sample;

public class OrderLabel extends Label {
	
	
	public OrderLabel(Patient patient, Sample sample, String labNo) {
		//set dimensions
		String strWidth = ConfigurationProperties.getInstance().getPropertyValue(Property.ORDER_BARCODE_WIDTH);
		String strHeight = ConfigurationProperties.getInstance().getPropertyValue(Property.ORDER_BARCODE_HEIGHT);
		try {
			width = Float.parseFloat(strWidth);
			height = Float.parseFloat(strHeight);
		} catch (Exception e) {
			LogEvent.logError("OrderLabel","OrderLabel OrderLabel()",e.toString());
		}
		
		//get information for above barcode
		Person person = patient.getPerson();
		String referringFacility = ConfigurationProperties.getInstance().getPropertyValue(Property.SiteCode); 
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
		//adding code
		setCode(labNo);
	}
	
	//get first available id for patient
	private BarcodeLabelField getAvailableIdField(Patient patient) {
		PatientService service = new PatientService(patient);
		String patientId = service.getSTNumber();
		if (!StringUtil.isNullorNill(patientId))
			return new BarcodeLabelField(StringUtil.getMessageForKey("barcode.label.info.patientid"), StringUtils.substring(patientId, 0, 25), 6);
		patientId = service.getNationalId();
		if (!StringUtil.isNullorNill(patientId))
			return new BarcodeLabelField(StringUtil.getMessageForKey("barcode.label.info.patientid"), StringUtils.substring(patientId, 0, 25), 6);
		return new BarcodeLabelField(StringUtil.getMessageForKey("barcode.label.info.patientid"), "", 6);
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
		String strMax = ConfigurationProperties.getInstance().getPropertyValue(Property.MAX_ORDER_PRINTED);
		int max = 0;
		try {
			max = Integer.parseInt(strMax);
		} catch (Exception e) {
			LogEvent.logError("OrderLabel","OrderLabel getMaxNumLabels()",e.toString());
		}
		return max;
	}

}
