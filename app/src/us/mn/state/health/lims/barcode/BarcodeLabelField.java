package us.mn.state.health.lims.barcode;

public class BarcodeLabelField {
	
	private String name;
	private String value;
	private int colspan = 5;
	private boolean startNewline = false;

	public BarcodeLabelField(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public BarcodeLabelField(String name, String value, int colspan) {
		this.name = name;
		this.value = value;
		this.colspan = colspan;
	}

	public boolean isStartNewline() {
		return startNewline;
	}

	public void setStartNewline(boolean startNewline) {
		this.startNewline = startNewline;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getColspan() {
		return colspan;
	}

	public void setColspan(int colspan) {
		this.colspan = colspan;
	}

}
