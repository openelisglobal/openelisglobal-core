package us.mn.state.health.lims.datasubmission.valueholder;

import us.mn.state.health.lims.common.valueholder.BaseObject;

public class DataValue extends BaseObject {
	private String id;
	private String name;
	private String value;
	private DataValue parentValue;
	private DataIndicator indicator;
	private String foreignColumnName;
	private String foreignTableName;
	private String foreignId;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public DataValue getParentValue() {
		return parentValue;
	}
	public void setParentValue(DataValue parentValue) {
		this.parentValue = parentValue;
	}
	public DataIndicator getIndicator() {
		return indicator;
	}
	public void setIndicator(DataIndicator indicator) {
		this.indicator = indicator;
	}
	public String getForeignColumnName() {
		return foreignColumnName;
	}
	public void setForeignColumnName(String foreignColumnName) {
		this.foreignColumnName = foreignColumnName;
	}
	public String getForeignTableName() {
		return foreignTableName;
	}
	public void setForeignTableName(String foreignTableName) {
		this.foreignTableName = foreignTableName;
	}
	public String getForeignId() {
		return foreignId;
	}
	public void setForeignId(String foreignId) {
		this.foreignId = foreignId;
	}
}
