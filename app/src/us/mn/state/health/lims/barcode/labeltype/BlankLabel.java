package us.mn.state.health.lims.barcode.labeltype;

import java.util.ArrayList;

import com.lowagie.text.Font;

import us.mn.state.health.lims.barcode.valueholder.BarcodeLabelField;

public class BlankLabel implements Label {

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
	
	public BlankLabel(String code) {
		fields = new ArrayList<BarcodeLabelField>();
		fields.add(new BarcodeLabelField("Patient Id", "", 5));
		fields.add(new BarcodeLabelField("Site", "", 5));
		fields.add(new BarcodeLabelField("Patient Name", "", 6));
		fields.add(new BarcodeLabelField("DOB", "", 4));
		
		this.code = code;
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
