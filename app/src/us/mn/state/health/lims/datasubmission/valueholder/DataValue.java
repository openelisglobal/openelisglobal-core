package us.mn.state.health.lims.datasubmission.valueholder;

import us.mn.state.health.lims.common.valueholder.BaseObject;

public class DataValue extends BaseObject {
	private String id;
	private String value;
	private String displayKey;
	private boolean visible;
	
	public DataValue() {
		this.value = "";
		visible = true;
	}
	public DataValue(String displayKey) {
		this.value = "";
		this.displayKey = displayKey;
		visible = true;
	}
	public DataValue(boolean visible) {
		this.value = "";
		this.visible = visible;
	}
	public DataValue(String value, boolean visible) {
		this.value = value;
		this.visible = visible;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDisplayKey() {
		return displayKey;
	}
	public void setDisplayKey(String displayKey) {
		this.displayKey = displayKey;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
