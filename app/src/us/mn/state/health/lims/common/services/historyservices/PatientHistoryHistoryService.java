/**
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/ 
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * Copyright (C) ITECH, University of Washington, Seattle WA.  All Rights Reserved.
 *
 */
package us.mn.state.health.lims.common.services.historyservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import us.mn.state.health.lims.audittrail.action.workers.AuditTrailItem;
import us.mn.state.health.lims.audittrail.valueholder.History;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.observationhistory.dao.ObservationHistoryDAO;
import us.mn.state.health.lims.observationhistory.daoimpl.ObservationHistoryDAOImpl;
import us.mn.state.health.lims.observationhistory.valueholder.ObservationHistory;
import us.mn.state.health.lims.observationhistorytype.ObservationHistoryTypeMap;
import us.mn.state.health.lims.referencetables.dao.ReferenceTablesDAO;
import us.mn.state.health.lims.referencetables.daoimpl.ReferenceTablesDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.sampleorganization.dao.SampleOrganizationDAO;
import us.mn.state.health.lims.sampleorganization.daoimpl.SampleOrganizationDAOImpl;
import us.mn.state.health.lims.sampleorganization.valueholder.SampleOrganization;

public class PatientHistoryHistoryService extends HistoryService {
	private static String OBSERVATION_HISTORY_TABLE_ID;
	private static String SAMPLE_ORG_TABLE_ID;

	private static final String ORGANIZATION_ATTRIBUTE = "organization";

	private static ObservationHistoryDAO observationDAO = new ObservationHistoryDAOImpl();
	private static SampleOrganizationDAO sampleOrgDAO = new SampleOrganizationDAOImpl();

	static {
		ReferenceTablesDAO tableDAO = new ReferenceTablesDAOImpl();
		OBSERVATION_HISTORY_TABLE_ID = tableDAO.getReferenceTableByName("observation_history").getId();
		SAMPLE_ORG_TABLE_ID = tableDAO.getReferenceTableByName("SAMPLE_ORGANIZATION").getId();
	}

	public PatientHistoryHistoryService(Sample sample) {
		setUpForPatientHistory(sample);
	}

	@SuppressWarnings("unchecked")
	private void setUpForPatientHistory(Sample sample) {
		attributeToIdentifierMap = new HashMap<String, String>();
		attributeToIdentifierMap.put(ORGANIZATION_ATTRIBUTE, "Referring Organization");

		newValueMap = new HashMap<String, String>();
		
		List<ObservationHistory> observationList = observationDAO.getObservationHistoriesBySampleId(sample.getId());
		
		for( ObservationHistory observation :observationList){
			newValueMap.put(observation.getId(), getObservationValue(observation));
		}

		identifier = sample.getAccessionNumber();

		List<ObservationHistory> observations = observationDAO.getObservationHistoriesBySampleId(sample.getId());

		History searchHistory = new History();
		historyList = new ArrayList<History>();

		for (ObservationHistory observation : observations) {
			searchHistory.setReferenceId(observation.getId());
			searchHistory.setReferenceTable(OBSERVATION_HISTORY_TABLE_ID);
			historyList.addAll(auditTrailDAO.getHistoryByRefIdAndRefTableId(searchHistory));
		}

		SampleOrganization sampleOrg = sampleOrgDAO.getDataBySample(sample);
		if (sampleOrg != null) {
			newValueMap.put(ORGANIZATION_ATTRIBUTE, sampleOrg.getOrganization().getOrganizationName());

			searchHistory.setReferenceId(sampleOrg.getId());
			searchHistory.setReferenceTable(SAMPLE_ORG_TABLE_ID);
			List<History> orgHistory = auditTrailDAO.getHistoryByRefIdAndRefTableId(searchHistory);
			historyList.addAll(orgHistory);
		}

	}

	@Override
	protected void addInsertion(History history, List<AuditTrailItem> items) {
		ObservationHistory observation = new ObservationHistory();
		observation.setId(history.getReferenceId());
		observation = observationDAO.getById(observation);
		if (observation != null) {
			identifier = ObservationHistoryTypeMap.getInstance().getTypeFromId(observation.getObservationHistoryTypeId());
			AuditTrailItem item = getCoreTrail(history);
			item.setNewValue(getObservationValue(observation));
			items.add(item);
		} else {
			setAndAddIfValueNotNull(items, history, ORGANIZATION_ATTRIBUTE);
		}
	}

	@Override
	protected void getObservableChanges(History history, Map<String, String> changeMap, String changes) {

		String status = extractStatus(changes);
		if (status != null) {
			changeMap.put(STATUS_ATTRIBUTE, status);
		}
		String value = extractSimple(changes, "value");
		if (value != null) {
			value = getCorrectValueForHistory(history, value);
			
			changeMap.put(VALUE_ATTRIBUTE, value);
		}

		String orgString = extractSimple(changes, ORGANIZATION_ATTRIBUTE);
		if (orgString != null) {
			String[] orgParts = orgString.split(", ");
			for (String part : orgParts) {
				if (part.startsWith("organizationName")) {
					changeMap.put(ORGANIZATION_ATTRIBUTE, part.split("=")[1]);
				}
			}
		}
	}

	private String getCorrectValueForHistory(History history, String value) {
		ObservationHistory obsHistory = new ObservationHistory();
		obsHistory.setId( history.getReferenceId());
		obsHistory = observationDAO.getById(obsHistory);
		//System.out.println(obsHistory.getObservationHistoryTypeId() + " : " + value);
		if( "D".equals(obsHistory.getValueType())){
			return dictDAO.getDataForId(value).getDictEntry();
		}
		
		return value;
	}

	@Override
	protected String getObjectName() {
		return StringUtil.getMessageForKey("patient.history");
	}

	protected void addItemsForKeys(List<AuditTrailItem> items, History history, Map<String, String> changeMaps) {
		for (String key : changeMaps.keySet()) {
			setIdentifierForKey(key);
			AuditTrailItem item = getCoreTrail(history);
			if (showAttribute()) {
				item.setAttribute(key);
			}
			
			String observationKey = history.getReferenceId();
			
			item.setOldValue( changeMaps.get(key));
			item.setNewValue( newValueMap.get(observationKey));
			newValueMap.put(observationKey, item.getOldValue());
			if (item.newOldDiffer()) {
				items.add(item);
			}
		}
	}
	
	protected String getObservationValue(ObservationHistory observation) {
		if ("D".equals(observation.getValueType())) {
			Dictionary dict = dictDAO.getDataForId(observation.getValue());
			return dict != null ? dict.getDictEntry() : observation.getValue();
		}

		return observation.getValue();
	}

}
