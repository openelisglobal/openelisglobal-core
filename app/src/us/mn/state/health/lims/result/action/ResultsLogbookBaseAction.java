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
 * Copyright (C) CIRG, University of Washington, Seattle WA.  All Rights Reserved.
 *
 */
package us.mn.state.health.lims.result.action;

import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.util.StringUtil;

public abstract class ResultsLogbookBaseAction extends BaseAction {

	protected enum logbooks {
		UNKNOWN, 
		HEMATOLOGY, 
		CHEM, 
		BACTERIOLOGY, 
		PARASITOLOGY, 
		IMMUNO, 
		ECBU, 
		HIV, 
		SEROLOGY, 
		SEROLOGIE,
		VIROLOGY, 
		VIROLOGIE, 
		MYCROBACTERIOLOGY, 
		MYCOBACTERIOLOGY, 
		MOLECULAR_BIOLOGY, 
		LIQUID_BIOLOGY, 
		ENDOCRINOLOGY,
		CYTOBACTERIOLOGY,
		MYCOLOGY,
		HEMATO_IMMUNOLOGY,
		SEROLOGY_IMMUNOLOGY,
        MALARIA
	}

	protected static String DEFAULT_NAME = "Default";
	protected static String HEMATOLOGY_NAME = "Hematology";
	protected static String CHEM_NAME = "Biochemistry";
	protected static String BACTERIA_NAME = "Bacteria";
	protected static String PARASITOLOGY_NAME = "Parasitology";
	protected static String IMMUNO_NAME = "Immunology";
	protected static String ECBU_NAME = "ECBU";
	protected static String SEROLOGY_NAME = "Serology";
	protected static String SEROLOGIE_NAME = "Serologie";
	protected static String VIROLOGY_NAME = "Virology";
	protected static String VIROLOGIE_NAME = "Virologie";
	protected static String MYCROBACTERIOLOGY_NAME = "Mycrobacteriology";
	protected static String MYCOBACTERIOLOGY_NAME = "Mycobacteriology";
	protected static String MOLECULAR_BIOLOGY = "Biologie Moleculaire";
	protected static String LIQUID_BIOLOGY = "Liquides biologique";
	protected static String ENDOCRINOLOGY = "Endocrinologie";
	protected static String CYTOBACTERIOLOGY = "Cytobacteriologie";
	protected static String MYCOLOGY = "mycology";
	protected static String HEMATO_IMMUNOLOGY = "Hemto-Immunology";
	protected static String SEROLOGY_IMMUNOLOGY = "Serology-Immunology";
    protected static String MALARIA = "Malaria";
	
	protected String currentDate = "";
	protected logbooks logbookRequest = logbooks.UNKNOWN;
	protected static String VCT_NAME = "VCT";

	protected void setLogbookRequest(String requestType) {
		if (!GenericValidator.isBlankOrNull(requestType)) {

			if (requestType.equals("hematology")) {
				logbookRequest = logbooks.HEMATOLOGY;
			} else if (requestType.equals("chem") || requestType.equals("biochemistry")) {
				logbookRequest = logbooks.CHEM;
			} else if (requestType.equals("bacteriology")) {
				logbookRequest = logbooks.BACTERIOLOGY;
			} else if (requestType.equals("parasitology")) {
				logbookRequest = logbooks.PARASITOLOGY;
			} else if (requestType.equals("immuno") || requestType.equals("immunology")) {
				logbookRequest = logbooks.IMMUNO;
			} else if (requestType.equals("ECBU")) {
				logbookRequest = logbooks.ECBU;
			} else if (requestType.equals("HIV")) {
				logbookRequest = logbooks.HIV;
			} else if (requestType.equals("serology")) {
				logbookRequest = logbooks.SEROLOGY;
			}else if (requestType.equals("serologie")) {
				logbookRequest = logbooks.SEROLOGIE;
			} else if (requestType.equals("virology")) {
				logbookRequest = logbooks.VIROLOGY;
			} else if (requestType.equals("virologie")) {
				logbookRequest = logbooks.VIROLOGIE;
			} else if (requestType.equals("mycrobacteriology")) {
				logbookRequest = logbooks.MYCROBACTERIOLOGY;
			} else if (requestType.equals("mycobacteriology")) {
				logbookRequest = logbooks.MYCOBACTERIOLOGY;
			} else if (requestType.equals("molecularBio")) {
				logbookRequest = logbooks.MOLECULAR_BIOLOGY;
			}else if (requestType.equals("liquidBio")) {
				logbookRequest = logbooks.LIQUID_BIOLOGY;
			}else if (requestType.equals("endocrin")) {
				logbookRequest = logbooks.ENDOCRINOLOGY;
			}else if ( requestType.equals("cytobacteriology")){
				logbookRequest = logbooks.CYTOBACTERIOLOGY;
			}else if ( requestType.equals("mycology")){
				logbookRequest = logbooks.MYCOLOGY;
			}else if ( requestType.equals("hemato-immunology")){
				logbookRequest = logbooks.HEMATO_IMMUNOLOGY;
			}else if ( requestType.equals("serolo-immunology")){
				logbookRequest = logbooks.SEROLOGY_IMMUNOLOGY;
			}else if( requestType.equals("malaria") ){
                logbookRequest = logbooks.MALARIA;
            }
		}
	}

	protected String getPageTitleKey() {
		return "banner.menu.results";
	}

	protected String getPageSubtitleKey() {
		String key = null;

		switch (logbookRequest) {
		case HEMATOLOGY: {
			key = StringUtil.getContextualKeyForKey("results.logbook.hemato");
			break;
		}
		case CHEM: {
			key = StringUtil.getContextualKeyForKey("results.logbook.chem");
			break;
		}
		case BACTERIOLOGY: {
			key = StringUtil.getContextualKeyForKey("results.logbook.bacteria");
			break;
		}
		case PARASITOLOGY: {
			key = StringUtil.getContextualKeyForKey("results.logbook.parasitology");
			break;
		}
		case IMMUNO: {
			key = StringUtil.getContextualKeyForKey("results.logbook.immunoSerology");
			break;
		}
		case ECBU: {
			key = StringUtil.getContextualKeyForKey("results.logbook.ecbu");
			break;
		}
		case HIV: {
			key = StringUtil.getContextualKeyForKey("results.logbook.vct");
			break;
		}
		case HEMATO_IMMUNOLOGY:{
			key = StringUtil.getContextualKeyForKey("results.logbook.hemato-immunology");
			break;
		}
		case SEROLOGIE:{
			key = StringUtil.getContextualKeyForKey("results.logbook.serology");
			break;
		}
		case SEROLOGY: {
			key = StringUtil.getContextualKeyForKey("results.logbook.serology");
			break;
		}
		case VIROLOGY: {
			key = StringUtil.getContextualKeyForKey("results.logbook.virology");
			break;
		}
		case VIROLOGIE: {
			key = StringUtil.getContextualKeyForKey("results.logbook.virology");
			break;
		}
		case MYCROBACTERIOLOGY: {
			key = StringUtil.getContextualKeyForKey("banner.menu.results.logbook.mycobacteriology");
			break;
		}
		case MYCOBACTERIOLOGY: {
			key = StringUtil.getContextualKeyForKey("banner.menu.results.logbook.mycobacteriology");
			break;
		}
		case MOLECULAR_BIOLOGY:{
			key = StringUtil.getContextualKeyForKey("results.logbook.molecularBio");
			break;
		}
		case LIQUID_BIOLOGY:{
			key = StringUtil.getContextualKeyForKey("banner.menu.results.logbook.liquidBio");
			break;
		}
		case ENDOCRINOLOGY:{
			key = StringUtil.getContextualKeyForKey("banner.menu.results.logbook.endocrinology");
			break;
		}
		case CYTOBACTERIOLOGY:{
			key = StringUtil.getContextualKeyForKey("results.logbook.bacteria");
			break;
		}
		case MYCOLOGY:{
			key = StringUtil.getContextualKeyForKey("results.logbook.mycology");
			break;
		}
		case SEROLOGY_IMMUNOLOGY:{
            key = StringUtil.getContextualKeyForKey("banner.menu.results.logbook.serology");
			break;
		}
        case MALARIA:{
            key = StringUtil.getContextualKeyForKey("banner.menu.results.logbook.malaria");
            break;
        }
		default: {
			key = StringUtil.getContextualKeyForKey("banner.menu.results");
		}
		}

		return key;
	}
	
	protected String getNameForLogbookType(logbooks request) {

		String name = DEFAULT_NAME;

		switch (logbookRequest) {
		case HEMATOLOGY: {
			name = HEMATOLOGY_NAME;
			break;
		}
		case CHEM: {
			name = CHEM_NAME;
			break;
		}
		case BACTERIOLOGY: {
			name = BACTERIA_NAME;
			break;
		}
		case PARASITOLOGY: {
			name = PARASITOLOGY_NAME;
			break;
		}
		case IMMUNO: {
			name = IMMUNO_NAME;
			break;
		}
		case ECBU: {
			name = ECBU_NAME;
			break;
		}
		case HIV: {
			name = VCT_NAME;
			break;
		}
		case SEROLOGY: {
			name = SEROLOGY_NAME;
			break;
		}
		case SEROLOGIE: {
			name = SEROLOGIE_NAME;
			break;
		}
		case VIROLOGY: {
			name = VIROLOGY_NAME;
			break;
		}
		case VIROLOGIE: {
			name = VIROLOGIE_NAME;
			break;
		}
		case MYCROBACTERIOLOGY: {
			name = MYCROBACTERIOLOGY_NAME;
			break;
		}
		case MYCOBACTERIOLOGY: {
			name = MYCOBACTERIOLOGY_NAME;
			break;
		}
		case MOLECULAR_BIOLOGY: {
			name = MOLECULAR_BIOLOGY;
			break;
		}
		case LIQUID_BIOLOGY: {
			name = LIQUID_BIOLOGY;
			break;
		}
		case ENDOCRINOLOGY: {
			name = ENDOCRINOLOGY;
			break;
		}
		case CYTOBACTERIOLOGY:{
			name = CYTOBACTERIOLOGY;
			break;
		}
		case MYCOLOGY:{
			name = MYCOLOGY;
			break;
		}case SEROLOGY_IMMUNOLOGY:{
			name = SEROLOGY_IMMUNOLOGY;
			break;
		}case HEMATO_IMMUNOLOGY:{
			name = HEMATO_IMMUNOLOGY;
			break;
		}case MALARIA:{
            name = MALARIA;
            break;
        }
		default: {
			// no-op
		}
		}

		return name;
	}

}
