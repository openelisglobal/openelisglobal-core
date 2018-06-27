package us.mn.state.health.lims.datasubmission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import us.mn.state.health.lims.datasubmission.valueholder.DataIndicator;
import us.mn.state.health.lims.datasubmission.valueholder.DataResource;
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
		case "VL Outcomes":
			indicator = createVLOutcomes();
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
		case "Gender Suppression":
			indicator = createGenderSuppression();
			break;
		case "Age Suppression":
			indicator = createAgeSuppression();
			break;
		default:
			return null;
		}
		indicator.setTypeOfIndicator(typeOfIndicator);
		indicator.setStatus(DataIndicator.UNSAVED);
		
		return indicator;
	}

	private static DataIndicator createTurnaroundTime() {
		DataIndicator indicator = new DataIndicator();
		List<DataResource> resources = new ArrayList<DataResource>();
		DataResource resource;

		resource = new DataResource();
		resource.setName("summary");
		resource.setCollectionName("summaries");
		resource.setLevel(DataResource.ALL);
		Map<String,DataValue> columnValues = new HashMap<String,DataValue>();
		columnValues.put("tat4", new DataValue("datasubmission.tat"));
		
		resource.setColumnValues(columnValues);
		resources.add(resource);
		
		indicator.setResources(resources);
		
		return indicator;
	}

	private static DataIndicator createVLCoverage() {
		DataIndicator indicator = new DataIndicator();
		List<DataResource> resources = new ArrayList<DataResource>();
		DataResource resource = new DataResource();
		
		resource.setName("suppression");
		resource.setCollectionName("suppressions");
		resource.setLevel(DataResource.SITE);
		Map<String,DataValue> columnValues = new HashMap<String,DataValue>();
		columnValues.put("suppressed", new DataValue("datasubmission.suppressed"));
		columnValues.put("nonsuppressed", new DataValue("datasubmission.nonsuppressed"));
		resource.setColumnValues(columnValues);
		resources.add(resource);
		
		indicator.setResources(resources);
		
		return indicator;
	}

	private static DataIndicator createVLOutcomes() {
		DataIndicator indicator = new DataIndicator();
		List<DataResource> resources = new ArrayList<DataResource>();
		DataResource resource = new DataResource();
		
		resource.setName("summary");
		resource.setCollectionName("summaries");
		resource.setLevel(DataResource.ALL);
		Map<String,DataValue> columnValues = new HashMap<String,DataValue>();
		columnValues.put("confirm2vl", new DataValue("datasubmission.confirm2vl"));
		columnValues.put("confirmtx", new DataValue("datasubmission.confirmtx"));
		columnValues.put("baseline", new DataValue("datasubmission.baseline"));
		columnValues.put("baselinesustxfail", new DataValue("datasubmission.baselinesustxfail"));
		columnValues.put("rejected", new DataValue("datasubmission.rejected"));
		columnValues.put("received", new DataValue("datasubmission.received"));
		columnValues.put("sitessending", new DataValue("datasubmission.sitessending"));
		
		resource.setColumnValues(columnValues);
		resources.add(resource);
		
		indicator.setResources(resources);
		
		return indicator;
	}

	private static DataIndicator createTestingTrends() {
		DataIndicator indicator = new DataIndicator();
		List<DataResource> resources = new ArrayList<DataResource>();
		DataResource resource = new DataResource();
		
		resource.setName("summary");
		resource.setCollectionName("summaries");
		resource.setLevel(DataResource.ALL);
		Map<String,DataValue> columnValues = new HashMap<String,DataValue>();
		columnValues.put("dbs", new DataValue("datasubmission.sample.dbs"));
		columnValues.put("plasma", new DataValue("datasubmission.sample.plasma"));
		columnValues.put("edta", new DataValue("datasubmission.sample.edta"));
		columnValues.put("alldbs", new DataValue("datasubmission.sample.alldbs"));
		columnValues.put("allplasma", new DataValue("datasubmission.sample.allplasma"));
		columnValues.put("alledta", new DataValue("datasubmission.sample.alledta"));
		columnValues.put("Undetected", new DataValue("datasubmission.undetected"));
		columnValues.put("less1000", new DataValue("datasubmission.less1000"));
		columnValues.put("less5000", new DataValue("datasubmission.less5000"));
		columnValues.put("above5000", new DataValue("datasubmission.more5000"));
		
		resource.setColumnValues(columnValues);
		resources.add(resource);
		
		indicator.setResources(resources);
		
		return indicator;
	}

	private static DataIndicator createGenderTrends() {
		DataIndicator indicator = new DataIndicator();
		List<DataResource> resources = new ArrayList<DataResource>();
		DataResource resource = new DataResource();
		
		resource.setName("gender");
		resource.setCollectionName("genders");
		resource.setLevel(DataResource.ALL);
		resource.setHeaderKey("datasubmission.men");
		Map<String,DataValue> columnValues = new HashMap<String,DataValue>();
		columnValues.put("Undetected", new DataValue("datasubmission.undetected"));
		columnValues.put("less1000", new DataValue("datasubmission.less1000"));
		columnValues.put("less5000", new DataValue("datasubmission.less5000"));
		columnValues.put("above5000", new DataValue("datasubmission.more5000"));
		columnValues.put("gender", new DataValue("Men", false));
		
		resource.setColumnValues(columnValues);
		resources.add(resource);
		
		resource = new DataResource();
		
		resource.setName("gender");
		resource.setCollectionName("genders");
		resource.setLevel(DataResource.ALL);
		resource.setHeaderKey("datasubmission.women");
		columnValues = new HashMap<String,DataValue>();
		columnValues.put("Undetected", new DataValue("datasubmission.undetected"));
		columnValues.put("less1000", new DataValue("datasubmission.less1000"));
		columnValues.put("less5000", new DataValue("datasubmission.less5000"));
		columnValues.put("above5000", new DataValue("datasubmission.more5000"));
		columnValues.put("gender", new DataValue("Women", false));
		
		resource.setColumnValues(columnValues);
		resources.add(resource);
		
		indicator.setResources(resources);
		
		return indicator;
	}

	private static DataIndicator createAgeTrends() {
		DataIndicator indicator = new DataIndicator();
		List<DataResource> resources = new ArrayList<DataResource>();
		DataResource resource = new DataResource();
		
		resource.setName("age");
		resource.setCollectionName("ages");
		resource.setLevel(DataResource.ALL);
		resource.setHeaderKey("datasubmission.less2");
		Map<String,DataValue> columnValues = new HashMap<String,DataValue>();
		columnValues.put("Undetected", new DataValue("datasubmission.undetected"));
		columnValues.put("less1000", new DataValue("datasubmission.less1000"));
		columnValues.put("less5000", new DataValue("datasubmission.less5000"));
		columnValues.put("above5000", new DataValue("datasubmission.more5000"));
		columnValues.put("age", new DataValue("label.less2", false));
		
		resource.setColumnValues(columnValues);
		resources.add(resource);
		
		resource = new DataResource();
		resource.setName("age");
		resource.setCollectionName("ages");
		resource.setLevel(DataResource.ALL);
		resource.setHeaderKey("datasubmission.less9");
		columnValues = new HashMap<String,DataValue>();
		columnValues.put("Undetected", new DataValue("datasubmission.undetected"));
		columnValues.put("less1000", new DataValue("datasubmission.less1000"));
		columnValues.put("less5000", new DataValue("datasubmission.less5000"));
		columnValues.put("above5000", new DataValue("datasubmission.more5000"));
		columnValues.put("age", new DataValue("label.less9", false));
		
		resource.setColumnValues(columnValues);
		resources.add(resource);
		
		resource = new DataResource();
		resource.setName("age");
		resource.setCollectionName("ages");
		resource.setLevel(DataResource.ALL);
		resource.setHeaderKey("datasubmission.less14");
		columnValues = new HashMap<String,DataValue>();
		columnValues.put("Undetected", new DataValue("datasubmission.undetected"));
		columnValues.put("less1000", new DataValue("datasubmission.less1000"));
		columnValues.put("less5000", new DataValue("datasubmission.less5000"));
		columnValues.put("above5000", new DataValue("datasubmission.more5000"));
		columnValues.put("age", new DataValue("label.less14", false));
		
		resource.setColumnValues(columnValues);
		resources.add(resource);
		
		resource = new DataResource();
		resource.setName("age");
		resource.setCollectionName("ages");
		resource.setLevel(DataResource.ALL);
		resource.setHeaderKey("datasubmission.less19");
		columnValues = new HashMap<String,DataValue>();
		columnValues.put("Undetected", new DataValue("datasubmission.undetected"));
		columnValues.put("less1000", new DataValue("datasubmission.less1000"));
		columnValues.put("less5000", new DataValue("datasubmission.less5000"));
		columnValues.put("above5000", new DataValue("datasubmission.more5000"));
		columnValues.put("age", new DataValue("label.less19", false));
		
		resource.setColumnValues(columnValues);
		resources.add(resource);
		
		resource = new DataResource();
		resource.setName("age");
		resource.setCollectionName("ages");
		resource.setLevel(DataResource.ALL);
		resource.setHeaderKey("datasubmission.less24");
		columnValues = new HashMap<String,DataValue>();
		columnValues.put("Undetected", new DataValue("datasubmission.undetected"));
		columnValues.put("less1000", new DataValue("datasubmission.less1000"));
		columnValues.put("less5000", new DataValue("datasubmission.less5000"));
		columnValues.put("above5000", new DataValue("datasubmission.more5000"));
		columnValues.put("age", new DataValue("label.less24", false));
		
		resource.setColumnValues(columnValues);
		resources.add(resource);
		
		resource = new DataResource();
		resource.setName("age");
		resource.setCollectionName("ages");
		resource.setLevel(DataResource.ALL);
		resource.setHeaderKey("datasubmission.over25");
		columnValues = new HashMap<String,DataValue>();
		columnValues.put("Undetected", new DataValue("datasubmission.undetected"));
		columnValues.put("less1000", new DataValue("datasubmission.less1000"));
		columnValues.put("less5000", new DataValue("datasubmission.less5000"));
		columnValues.put("above5000", new DataValue("datasubmission.more5000"));
		columnValues.put("age", new DataValue("label.over25", false));
		
		resource.setColumnValues(columnValues);
		resources.add(resource);
		
		indicator.setResources(resources);
				
		return indicator;
	}

	//TO DO currently hardcoded, possibly made procedural
	private static DataIndicator createJustification() {
		DataIndicator indicator = new DataIndicator();
		List<DataResource> resources = new ArrayList<DataResource>();
		DataResource resource = new DataResource();
		
		resource.setName("justification");
		resource.setCollectionName("justifications");
		resource.setLevel(DataResource.ALL);
		Map<String,DataValue> columnValues = new HashMap<String,DataValue>();
		columnValues.put("tests", new DataValue("datasubmission.reason.arv"));
		columnValues.put("justification", new DataValue("arv", false));
		
		resource.setColumnValues(columnValues);
		resources.add(resource);
		
		resource = new DataResource();
		resource.setName("justification");
		resource.setCollectionName("justifications");
		resource.setLevel(DataResource.ALL);
		columnValues = new HashMap<String,DataValue>();
		columnValues.put("tests", new DataValue("datasubmission.reason.virologicalfail"));
		columnValues.put("justification", new DataValue("virological", false));
		
		resource.setColumnValues(columnValues);
		resources.add(resource);
		
		resource = new DataResource();
		resource.setName("justification");
		resource.setCollectionName("justifications");
		resource.setLevel(DataResource.ALL);
		columnValues = new HashMap<String,DataValue>();
		columnValues.put("tests", new DataValue("datasubmission.reason.clinicalfail"));
		columnValues.put("justification", new DataValue("clinical", false));
		
		resource.setColumnValues(columnValues);
		resources.add(resource);
		
		resource = new DataResource();
		resource.setName("justification");
		resource.setCollectionName("justifications");
		resource.setLevel(DataResource.ALL);
		columnValues = new HashMap<String,DataValue>();
		columnValues.put("tests", new DataValue("datasubmission.reason.immunologicalfail"));
		columnValues.put("justification", new DataValue("immunological", false));
		
		resource.setColumnValues(columnValues);
		resources.add(resource);
		
		indicator.setResources(resources);
		
		return indicator;
	}

	private static DataIndicator createGenderSuppression() {
		DataIndicator indicator = new DataIndicator();
		List<DataResource> resources = new ArrayList<DataResource>();
		DataResource resource = new DataResource();
		
		resource.setName("suppression");
		resource.setCollectionName("suppressions");
		resource.setLevel(DataResource.SITE);
		Map<String,DataValue> columnValues = new HashMap<String,DataValue>();
		columnValues.put("male_suppressed", new DataValue("datasubmission.malesuppressed"));
		columnValues.put("male_nonsuppressed", new DataValue("datasubmission.malenonsuppressed"));
		columnValues.put("female_suppressed", new DataValue("datasubmission.femalesuppressed"));
		columnValues.put("female_nonsuppressed", new DataValue("datasubmission.femalenonsuppressed"));
		resource.setColumnValues(columnValues);
		resources.add(resource);
		
		indicator.setResources(resources);
		
		return indicator;
	}

	private static DataIndicator createAgeSuppression() {
		DataIndicator indicator = new DataIndicator();
		List<DataResource> resources = new ArrayList<DataResource>();
		DataResource resource = new DataResource();
		
		resource.setName("suppression");
		resource.setCollectionName("suppressions");
		resource.setLevel(DataResource.SITE);
		Map<String,DataValue> columnValues = new HashMap<String,DataValue>();
		columnValues.put("less2_suppressed", new DataValue("datasubmission.less2suppressed"));
		columnValues.put("less2_nonsuppressed", new DataValue("datasubmission.less2nonsuppressed"));
		columnValues.put("less9_suppressed", new DataValue("datasubmission.less9suppressed"));
		columnValues.put("less9_nonsuppressed", new DataValue("datasubmission.less9nonsuppressed"));
		columnValues.put("less14_suppressed", new DataValue("datasubmission.less14suppressed"));
		columnValues.put("less14_nonsuppressed", new DataValue("datasubmission.less14nonsuppressed"));
		columnValues.put("less19_suppressed", new DataValue("datasubmission.less19suppressed"));
		columnValues.put("less19_nonsuppressed", new DataValue("datasubmission.less19nonsuppressed"));
		columnValues.put("less24_suppressed", new DataValue("datasubmission.less24suppressed"));
		columnValues.put("less24_nonsuppressed", new DataValue("datasubmission.less24nonsuppressed"));
		columnValues.put("over25_suppressed", new DataValue("datasubmission.over25suppressed"));
		columnValues.put("over25_nonsuppressed", new DataValue("datasubmission.over25nonsuppressed"));
		resource.setColumnValues(columnValues);
		resources.add(resource);
		
		indicator.setResources(resources);
		
		return indicator;
	}
}
