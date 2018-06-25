package us.mn.state.health.lims.datasubmission.valueholder;

import java.util.ArrayList;
import java.util.List;

import us.mn.state.health.lims.common.util.validator.GenericValidator;
import us.mn.state.health.lims.common.valueholder.BaseObject;

public class DataIndicator extends BaseObject {
	public static String SENT = "sent";
	public static String RECEIVED = "received";
	public static String FAILED = "failed";
	public static String UNSAVED = "unsaved";
	
	private String id;
	private DataValue dataValue;
	private int year;
	private int month;
	private List<DataValue> dataValues = new ArrayList<DataValue>();
	private TypeOfDataIndicator typeOfIndicator;
	private String status;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public DataValue getDataValue() {
		return dataValue;
	}
	public void setDataValue(DataValue dataValue) {
		this.dataValue = dataValue;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public List<DataValue> getDataValues() {
		return dataValues;
	}
	public void setDataValues(List<DataValue> dataValues) {
		this.dataValues = dataValues;
	}
	public DataValue getDataValue(int index) {
		return dataValues.get(index);
	}
	public void setDataValue(int index, DataValue dataValue) {
		this.dataValues.set(index, dataValue);
	}
	public TypeOfDataIndicator getTypeOfIndicator() {
		return typeOfIndicator;
	}
	public void setTypeOfIndicator(TypeOfDataIndicator typeOfIndicator) {
		this.typeOfIndicator = typeOfIndicator;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public List<DataValue> getAllDataValues() {
		List<DataValue> allValues = new ArrayList<DataValue>();
		allValues.addAll(getDataValues());
		allValues.add(dataValue);
		return allValues;
	}
	
	public List<String> getDataValueTableNames() {
		List<String> tableNames = new ArrayList<String>();
		for (DataValue value : getAllDataValues()) {
			if (!GenericValidator.isBlankOrNull(value.getForeignTableName()) && !tableNames.contains(value.getForeignTableName())) {
				tableNames.add(value.getForeignTableName());
			}
		}
		return tableNames;
	}
	
	public List<DataValue> getDataValuesByTable(String tableName) {
		List<DataValue> values = new ArrayList<DataValue>();
		for (DataValue value : getAllDataValues()) {
			if (tableName.equals(value.getForeignTableName())) {
				values.add(value);
			}
		}
		return values;
	}

}
