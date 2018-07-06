package us.mn.state.health.lims.datasubmission;

import us.mn.state.health.lims.datasubmission.valueholder.DataIndicator;
import us.mn.state.health.lims.datasubmission.valueholder.DataValue;
import us.mn.state.health.lims.datasubmission.valueholder.TypeOfDataIndicator;

public class DataIndicatorFactory {
	
	public static DataIndicator createBlankDataIndicatorForType(TypeOfDataIndicator typeOfIndicator) {

		DataIndicator indicator = new DataIndicator();
		DataValue mainValue = new DataValue();
		DataValue subValue = new DataValue();
		
		String indicatorName = typeOfIndicator.getName();
		//switch (typeOfIndicator.getName()) {
		//case "Turnaround Time": 
		if (indicatorName.equals("Turnaround Time")) {
			indicator.setTypeOfIndicator(typeOfIndicator);
			indicator.setStatus(DataIndicator.UNSAVED);
			
			mainValue.setValue("");
			mainValue.setName("");
			mainValue.setIndicator(indicator);
			mainValue.setForeignColumnName("tat4");
			mainValue.setForeignTableName("vl_national_summary");
			indicator.setDataValue(mainValue);
		} else if (indicatorName.equals("VL Coverage")) {
		//	break;
		//case "VL Coverage": 
			indicator.setTypeOfIndicator(typeOfIndicator);
			indicator.setStatus(DataIndicator.UNSAVED);
			
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
		} else if (indicatorName.equals("Testing Trends")) {
		//	break;
		//case "Testing Trends": 
			indicator.setTypeOfIndicator(typeOfIndicator);
			indicator.setStatus(DataIndicator.UNSAVED);
			
			mainValue.setValue("");
			mainValue.setName("");
			mainValue.setIndicator(indicator);
			indicator.setDataValue(mainValue);
			
			subValue.setValue("");
			subValue.setName("DBS");
			subValue.setNameKey("datasubmission.sample.dbs");
			subValue.setParentValue(mainValue);
			subValue.setForeignColumnName("dbs");
			subValue.setForeignTableName("vl_national_summary");
			indicator.getDataValues().add(subValue);

			subValue = new DataValue();
			subValue.setValue("");
			subValue.setName("Plasma");
			subValue.setNameKey("datasubmission.sample.plasma");
			subValue.setParentValue(mainValue);
			subValue.setForeignColumnName("plasma");
			subValue.setForeignTableName("vl_national_summary");
			indicator.getDataValues().add(subValue);

			subValue = new DataValue();
			subValue.setValue("");
			subValue.setName("EDTA");
			subValue.setNameKey("datasubmission.sample.edta");
			subValue.setParentValue(mainValue);
			subValue.setForeignColumnName("edta");
			subValue.setForeignTableName("vl_national_summary");
			indicator.getDataValues().add(subValue);

			subValue = new DataValue();
			subValue.setValue("");
			subValue.setName("All DBS");
			subValue.setNameKey("datasubmission.sample.alldbs");
			subValue.setParentValue(mainValue);
			subValue.setForeignColumnName("alldbs");
			subValue.setForeignTableName("vl_national_summary");
			indicator.getDataValues().add(subValue);

			subValue = new DataValue();
			subValue.setValue("");
			subValue.setName("All Plasma");
			subValue.setNameKey("datasubmission.sample.allplasma");
			subValue.setParentValue(mainValue);
			subValue.setForeignColumnName("allplasma");
			subValue.setForeignTableName("vl_national_summary");
			indicator.getDataValues().add(subValue);

			subValue = new DataValue();
			subValue.setValue("");
			subValue.setName("All EDTA");
			subValue.setNameKey("datasubmission.sample.alledta");
			subValue.setParentValue(mainValue);
			subValue.setForeignColumnName("alledta");
			subValue.setForeignTableName("vl_national_summary");
			indicator.getDataValues().add(subValue);

			subValue = new DataValue();
			subValue.setValue("");
			subValue.setName("Undetected");
			subValue.setNameKey("datasubmission.undetected");
			subValue.setParentValue(mainValue);
			subValue.setForeignColumnName("Undetected");
			subValue.setForeignTableName("vl_national_summary");
			indicator.getDataValues().add(subValue);

			subValue = new DataValue();
			subValue.setValue("");
			subValue.setName(" < 1000");
			subValue.setNameKey("datasubmission.less1000");
			subValue.setParentValue(mainValue);
			subValue.setForeignColumnName("less1000");
			subValue.setForeignTableName("vl_national_summary");
			indicator.getDataValues().add(subValue);

			subValue = new DataValue();
			subValue.setValue("");
			subValue.setName("< 5000");
			subValue.setNameKey("datasubmission.less5000");
			subValue.setParentValue(mainValue);
			subValue.setForeignColumnName("less5000");
			subValue.setForeignTableName("vl_national_summary");
			indicator.getDataValues().add(subValue);

			subValue = new DataValue();
			subValue.setValue("");
			subValue.setName("> 5000");
			subValue.setNameKey("datasubmission.more5000");
			subValue.setParentValue(mainValue);
			subValue.setForeignColumnName("above5000");
			subValue.setForeignTableName("vl_national_summary");
			indicator.getDataValues().add(subValue);
			
		}
		//	break;
		//default:
		//	break;
		//}
		return indicator;
	}

}
