package us.mn.state.health.lims.barcode.labeltype;

import java.util.ArrayList;

import us.mn.state.health.lims.barcode.BarcodeLabelField;

public class BlankLabel extends Label {
	
	
	public BlankLabel(String code) {
		aboveFields = new ArrayList<BarcodeLabelField>();
		aboveFields.add(new BarcodeLabelField("Patient Id", "", 5));
		aboveFields.add(new BarcodeLabelField("Site", "", 5));
		aboveFields.add(new BarcodeLabelField("Patient Name", "", 6));
		aboveFields.add(new BarcodeLabelField("DOB", "", 4));
		
		setCode(code);
	}

	@Override
	public int getNumTextRowsBefore() {
		Iterable<BarcodeLabelField> fields = getAboveFields();
		return getNumRows(fields);
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
