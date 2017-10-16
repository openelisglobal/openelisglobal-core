package us.mn.state.health.lims.barcode.labeltype;

import java.util.ArrayList;

import us.mn.state.health.lims.barcode.BarcodeLabelField;
import us.mn.state.health.lims.common.util.StringUtil;

public class BlankLabel extends Label {
	
	
	public BlankLabel(String code) {
		aboveFields = new ArrayList<BarcodeLabelField>();
		aboveFields.add(new BarcodeLabelField(StringUtil.getMessageForKey("barcode.label.info.patientid"), "", 5));
		aboveFields.add(new BarcodeLabelField(StringUtil.getMessageForKey("barcode.label.info.site"), "", 5));
		aboveFields.add(new BarcodeLabelField(StringUtil.getMessageForKey("barcode.label.info.patientname"), "", 6));
		aboveFields.add(new BarcodeLabelField(StringUtil.getMessageForKey("barcode.label.info.patientdob"), "", 4));
		
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
