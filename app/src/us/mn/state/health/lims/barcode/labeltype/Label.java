package us.mn.state.health.lims.barcode.labeltype;

import com.lowagie.text.Font;

import us.mn.state.health.lims.barcode.valueholder.BarcodeLabelField;

public interface Label {
	
	int SMALL_BARCODE = 3;
	int MED_BARCODE = 4;
	int LARGE_BARCODE = 5;

	Iterable<BarcodeLabelField> getFields();
	String getCode();
	Font getNameFont();
	Font getValueFont();
	int getHeight();
	int getWidth();
	int getMargin();
	int getBarcodeSpace();
	int getNumLabels();
	void setNumLabels(int num);
	Iterable<BarcodeLabelField> getBelowFields();
	
	default int getNumTextRowsBefore() {
		int numRows = 0;
		int curColumns = 0;
		boolean completeRow = true;
		Iterable<BarcodeLabelField> fields = getFields();
		for (BarcodeLabelField field : fields) {
			
			if (field.isStartNewline() && !completeRow) {
				++numRows;
				curColumns = 0;
			}
			curColumns += field.getColspan();
			if (curColumns > 10) {
				//throw error
			} else if (curColumns == 10) {
				completeRow = true;
				curColumns = 0;
				++numRows;
			} else {
				completeRow = false;
			}
		}
		if (!completeRow) {
			++numRows;
		}
		
		return numRows;
	}
	
	default int getNumTextRowsAfter() {
		int numRows = 0;
		int curColumns = 0;
		boolean completeRow = true;
		Iterable<BarcodeLabelField> fields = getBelowFields();
		if (fields == null) 
			return 0;
		for (BarcodeLabelField field : fields) {
			
			if (field.isStartNewline() && !completeRow) {
				++numRows;
				curColumns = 0;
			}
			curColumns += field.getColspan();
			if (curColumns > 10) {
				//throw error
			} else if (curColumns == 10) {
				completeRow = true;
				curColumns = 0;
				++numRows;
			} else {
				completeRow = false;
			}
		}
		if (!completeRow) {
			++numRows;
		}
		
		return numRows;
	}
	
}
