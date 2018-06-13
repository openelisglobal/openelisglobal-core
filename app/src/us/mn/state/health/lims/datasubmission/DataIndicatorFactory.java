package us.mn.state.health.lims.datasubmission;

import us.mn.state.health.lims.datasubmission.valueholder.DataIndicator;
import us.mn.state.health.lims.datasubmission.valueholder.DataValue;
import us.mn.state.health.lims.datasubmission.valueholder.TypeOfDataIndicator;

public class DataIndicatorFactory {
	
	public static DataIndicator createBlankDataIndicatorForType(TypeOfDataIndicator typeOfIndicator) {

		DataIndicator indicator = new DataIndicator();
		
		switch (typeOfIndicator.getName()) {
		case "Turnaround Time": 
			indicator = createTurnaroundTime();
			break;
		case "VL Coverage": 
			indicator = createVLCoverage();
			break;
		case "Testing Trends":
			indicator = createTestingTrends();
			break;
		case "Gender Trends":
			indicator = createGenderTrends();
			break;
		case "Age Trends":
			indicator = createAgeTrends();
			break;
		case "Justification":
			indicator = createJustification();
			break;
		default:
			break;
		}
		indicator.setTypeOfIndicator(typeOfIndicator);
		indicator.setStatus(DataIndicator.UNSAVED);
		
		return indicator;
	}

	private static DataIndicator createTurnaroundTime() {
		DataIndicator indicator = new DataIndicator();
		DataValue mainValue = new DataValue();
		
		mainValue.setValue("");
		mainValue.setName("");
		mainValue.setIndicator(indicator);
		mainValue.setForeignColumnName("tat4");
		mainValue.setForeignTableName("vl_national_summary");
		indicator.setDataValue(mainValue);
		
		return indicator;
	}

	private static DataIndicator createVLCoverage() {
		DataIndicator indicator = new DataIndicator();
		DataValue mainValue = new DataValue();
		DataValue subValue = new DataValue();
		
		mainValue.setValue("");
		mainValue.setName("Percentage");
		mainValue.setNameKey("datasubmission.percentage");
		mainValue.setIndicator(indicator);
		indicator.setDataValue(mainValue);

		subValue = new DataValue();
		subValue.setValue("");
		subValue.setName("Suppressed");
		subValue.setNameKey("datasubmission.suppressed");
		subValue.setParentValue(mainValue);
		subValue.setForeignColumnName("suppressed");
		subValue.setForeignTableName("vl_site_suppression");
		indicator.getDataValues().add(subValue);

		subValue = new DataValue();
		subValue.setValue("");
		subValue.setName("Non-Suppressed");
		subValue.setNameKey("datasubmission.nonsuppressed");
		subValue.setParentValue(mainValue);
		subValue.setForeignColumnName("nonsuppressed");
		subValue.setForeignTableName("vl_site_suppression");
		indicator.getDataValues().add(subValue);

		subValue = new DataValue();
		subValue.setValue("");
		subValue.setName("Total Artmar");
		subValue.setNameKey("datasubmission.totartmar");
		subValue.setParentValue(mainValue);
		subValue.setForeignColumnName("totalartmar");
		subValue.setForeignTableName("facilitys");
		indicator.getDataValues().add(subValue);
		
		return indicator;
	}

	private static DataIndicator createTestingTrends() {
		DataIndicator indicator = new DataIndicator();
		DataValue mainValue = new DataValue();
		
		mainValue.setValue("");
		mainValue.setName("");
		mainValue.setIndicator(indicator);
		indicator.setDataValue(mainValue);
		
		createAllSampleTypesDataValues("default", "vl_national_summary", indicator);
		createSuppressedUnsuppressedBreakdownDataValues("default", "vl_national_summary", indicator);
		
		return indicator;
	}

	private static DataIndicator createGenderTrends() {
		DataIndicator indicator = new DataIndicator();
		DataValue mainValue = new DataValue();
		
		mainValue.setValue("");
		mainValue.setName("");
		mainValue.setGroupKey("");
		mainValue.setIndicator(indicator);
		indicator.setDataValue(mainValue);

		createSuppressedUnsuppressedBreakdownDataValues("datasubmission.women", "vl_national_gender", indicator);
		createSuppressedUnsuppressedBreakdownDataValues("datasubmission.men", "vl_national_gender", indicator);
		
		return indicator;
	}

	private static DataIndicator createAgeTrends() {
		DataIndicator indicator = new DataIndicator();
		DataValue mainValue = new DataValue();
		
		mainValue.setValue("");
		mainValue.setName("");
		mainValue.setGroupKey("");
		mainValue.setIndicator(indicator);
		indicator.setDataValue(mainValue);

		createSuppressedUnsuppressedBreakdownDataValues("label.less9", "vl_national_age", indicator);
		createSuppressedUnsuppressedBreakdownDataValues("label.less19", "vl_national_age", indicator);
		createSuppressedUnsuppressedBreakdownDataValues("label.less24", "vl_national_age", indicator);
		createSuppressedUnsuppressedBreakdownDataValues("label.over25", "vl_national_age", indicator);
				
		return indicator;
	}

	//TO DO currently hardcoded, possibly made procedural
	private static DataIndicator createJustification() {
		DataIndicator indicator = new DataIndicator();
		DataValue mainValue = new DataValue();
		DataValue subValue = new DataValue();
		
		mainValue.setValue("");
		mainValue.setName("");
		mainValue.setNameKey("datasubmission.reason");
		mainValue.setIndicator(indicator);
		indicator.setDataValue(mainValue);
		
		subValue.setValue("");
		subValue.setName("under ARV control");
		subValue.setNameKey("");
		subValue.setGroupKey("datasubmission.reason.arv");
		subValue.setParentValue(mainValue);
		subValue.setForeignColumnName("tests");
		subValue.setForeignTableName("vl_national_justification");
		indicator.getDataValues().add(subValue);

		subValue = new DataValue();
		subValue.setValue("");
		subValue.setName("virological failure");
		subValue.setNameKey("");
		subValue.setGroupKey("datasubmission.reason.virologicalfail");
		subValue.setParentValue(mainValue);
		subValue.setForeignColumnName("tests");
		subValue.setForeignTableName("vl_national_justification");
		indicator.getDataValues().add(subValue);

		subValue = new DataValue();
		subValue.setValue("");
		subValue.setName("clinical failure");
		subValue.setNameKey("");
		subValue.setGroupKey("datasubmission.reason.clinicalfail");
		subValue.setParentValue(mainValue);
		subValue.setForeignColumnName("tests");
		subValue.setForeignTableName("vl_national_justification");
		indicator.getDataValues().add(subValue);

		subValue = new DataValue();
		subValue.setValue("");
		subValue.setName("immunological failure");
		subValue.setNameKey("");
		subValue.setGroupKey("datasubmission.reason.immunologicalfail");
		subValue.setParentValue(mainValue);
		subValue.setForeignColumnName("tests");
		subValue.setForeignTableName("vl_national_justification");
		indicator.getDataValues().add(subValue);
		
		return indicator;
	}
	
	
	
	
	private static void createAllSampleTypesDataValues(String groupKey, String tableName, DataIndicator indicator) {
		DataValue mainValue = indicator.getDataValue();
		DataValue subValue;

		subValue = new DataValue();
		subValue.setValue("");
		subValue.setName("DBS");
		subValue.setNameKey("datasubmission.sample.dbs");
		subValue.setGroupKey(groupKey);
		subValue.setParentValue(mainValue);
		subValue.setForeignColumnName("dbs");
		subValue.setForeignTableName(tableName);
		indicator.getDataValues().add(subValue);

		subValue = new DataValue();
		subValue.setValue("");
		subValue.setName("Plasma");
		subValue.setNameKey("datasubmission.sample.plasma");
		subValue.setGroupKey(groupKey);
		subValue.setParentValue(mainValue);
		subValue.setForeignColumnName("plasma");
		subValue.setForeignTableName(tableName);
		indicator.getDataValues().add(subValue);

		subValue = new DataValue();
		subValue.setValue("");
		subValue.setName("EDTA");
		subValue.setNameKey("datasubmission.sample.edta");
		subValue.setGroupKey(groupKey);
		subValue.setParentValue(mainValue);
		subValue.setForeignColumnName("edta");
		subValue.setForeignTableName(tableName);
		indicator.getDataValues().add(subValue);

		subValue = new DataValue();
		subValue.setValue("");
		subValue.setName("All DBS");
		subValue.setNameKey("datasubmission.sample.alldbs");
		subValue.setGroupKey(groupKey);
		subValue.setParentValue(mainValue);
		subValue.setForeignColumnName("alldbs");
		subValue.setForeignTableName(tableName);
		indicator.getDataValues().add(subValue);

		subValue = new DataValue();
		subValue.setValue("");
		subValue.setName("All Plasma");
		subValue.setNameKey("datasubmission.sample.allplasma");
		subValue.setGroupKey(groupKey);
		subValue.setParentValue(mainValue);
		subValue.setForeignColumnName("allplasma");
		subValue.setForeignTableName(tableName);
		indicator.getDataValues().add(subValue);

		subValue = new DataValue();
		subValue.setValue("");
		subValue.setName("All EDTA");
		subValue.setNameKey("datasubmission.sample.alledta");
		subValue.setGroupKey(groupKey);
		subValue.setParentValue(mainValue);
		subValue.setForeignColumnName("alledta");
		subValue.setForeignTableName(tableName);
		indicator.getDataValues().add(subValue);
	}
	
	//Creates 4 data values that are commonly used for data indicators
	private static void createSuppressedUnsuppressedBreakdownDataValues(String groupKey, String tableName, DataIndicator indicator) {
		DataValue mainValue = indicator.getDataValue();
		DataValue subValue;
		
		subValue = new DataValue();
		subValue.setValue("");
		subValue.setName("Undetected");
		subValue.setNameKey("datasubmission.undetected");
		subValue.setGroupKey(groupKey);
		subValue.setParentValue(mainValue);
		subValue.setForeignColumnName("Undetected");
		subValue.setForeignTableName(tableName);
		indicator.getDataValues().add(subValue);

		subValue = new DataValue();
		subValue.setValue("");
		subValue.setName(" < 1000");
		subValue.setNameKey("datasubmission.less1000");
		subValue.setGroupKey(groupKey);
		subValue.setParentValue(mainValue);
		subValue.setForeignColumnName("less1000");
		subValue.setForeignTableName(tableName);
		indicator.getDataValues().add(subValue);

		subValue = new DataValue();
		subValue.setValue("");
		subValue.setName("< 5000");
		subValue.setNameKey("datasubmission.less5000");
		subValue.setGroupKey(groupKey);
		subValue.setParentValue(mainValue);
		subValue.setForeignColumnName("less5000");
		subValue.setForeignTableName(tableName);
		indicator.getDataValues().add(subValue);

		subValue = new DataValue();
		subValue.setValue("");
		subValue.setName("> 5000");
		subValue.setNameKey("datasubmission.more5000");
		subValue.setGroupKey(groupKey);
		subValue.setParentValue(mainValue);
		subValue.setForeignColumnName("above5000");
		subValue.setForeignTableName(tableName);
		indicator.getDataValues().add(subValue);
	}
}
