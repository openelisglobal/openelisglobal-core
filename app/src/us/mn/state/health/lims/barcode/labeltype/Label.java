package us.mn.state.health.lims.barcode.labeltype;

import com.lowagie.text.Font;

import us.mn.state.health.lims.barcode.BarcodeLabelField;

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
	
}
