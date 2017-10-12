package us.mn.state.health.lims.barcode.valueholder;

import us.mn.state.health.lims.common.valueholder.BaseObject;

public class BarcodeLabelInfo extends BaseObject {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private int numPrinted;
	private String code;
	private String type;
	
	public BarcodeLabelInfo() {
		super();
		numPrinted = 0;
	}
	
	public BarcodeLabelInfo(String code) {
		super();
		this.code = code;
		numPrinted = 0;
		type = parseCodeForType();
	}
	
	public void incrementNumPrinted() {
		++numPrinted;
	}
	
	public String parseCodeForType() {
		if (code.contains("-")) {
			return "aliquot";
		} else if (code.contains(".")) {
			return "specimen";
		} else {
			return "order";
		}
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public int getNumPrinted() {
		return numPrinted;
	}

	public void setNumPrinted(int numPrinted) {
		this.numPrinted = numPrinted;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	

}
