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
package us.mn.state.health.lims.common.services;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.observationhistory.dao.ObservationHistoryDAO;
import us.mn.state.health.lims.observationhistory.daoimpl.ObservationHistoryDAOImpl;
import us.mn.state.health.lims.observationhistory.valueholder.ObservationHistory;
import us.mn.state.health.lims.observationhistorytype.dao.ObservationHistoryTypeDAO;
import us.mn.state.health.lims.observationhistorytype.daoImpl.ObservationHistoryTypeDAOImpl;
import us.mn.state.health.lims.observationhistorytype.valueholder.ObservationHistoryType;
import us.mn.state.health.lims.sample.valueholder.Sample;

public class ObservationHistoryService{

	private static final ObservationHistoryDAO observationDAO = new ObservationHistoryDAOImpl();
	private static final DictionaryDAO dictionaryDAO = new DictionaryDAOImpl();
	private static final Map<ObservationType, String> observationTypeToIdMap = new HashMap<ObservationType, String>();

	public enum ObservationType{
		INITIAL_SAMPLE_CONDITION("initialSampleCondition"), 
		PAYMENT_STATUS("paymentStatus"), 
		REQUEST_DATE("requestDate"), 
		NEXT_VISIT_DATE("nextVisitDate"), 
		REFERRING_SITE("referringSite"), 
		PRIMARY_ORDER_TYPE("primaryOrderType"), 
		SECONDARY_ORDER_TYPE("secondaryOrderType"), 
		OTHER_SECONDARY_ORDER_TYPE(	"otherSecondaryOrderType"),
		REFERRERS_PATIENT_ID("referrersPatientId");
		
		private String dbName;

		private ObservationType(String dbName){
			this.dbName = dbName;
		}

		public String getDatabaseName(){
			return dbName;
		}
	}

	public static String getIdForType(ObservationType type){
		if(observationTypeToIdMap.isEmpty()){
			initialize();
		}
		return observationTypeToIdMap.get(type);
	}

	
	public static String getValue(ObservationType type, Sample sample){
		if(observationTypeToIdMap.isEmpty()){
			initialize();
		}
		String typeId = getIdForType(type);
		
		if(!GenericValidator.isBlankOrNull(typeId)){
			ObservationHistory observation = observationDAO.getObservationHistoriesBySampleIdAndType(sample.getId(), getIdForType(type));
			if(observation != null){
				if(observation.getValueType().equals(ObservationHistory.ValueType.LITERAL.getCode())){
					return observation.getValue();
				}else{
					if(!GenericValidator.isBlankOrNull(observation.getValue())){
						return dictionaryDAO.getDataForId(observation.getValue()).getDictEntry();
					}
				}

			}
		}
		return null;
	}

	
	private static void initialize(){
		ObservationHistoryType oht;
		ObservationHistoryTypeDAO ohtDAO = new ObservationHistoryTypeDAOImpl();

		for(ObservationType type : ObservationType.values()){
			oht = ohtDAO.getByName(type.getDatabaseName());
			if(oht != null){
				observationTypeToIdMap.put(type, oht.getId());
			}
		}
	}
}
