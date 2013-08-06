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
* Copyright (C) The Minnesota Department of Health.  All Rights Reserved.
*
* Contributor(s): CIRG, University of Washington, Seattle WA.
*/
package us.mn.state.health.lims.common.util;

import org.apache.commons.validator.GenericValidator;

import java.util.HashMap;
import java.util.Map;

/*
 * This is an abstract class which represents the configuration properties of the application.  The derived
 * classes will determine how the propertiesValueMap is populated
 */
public abstract class ConfigurationProperties {

	private static final Object lockObj = new Object();
	private static ConfigurationProperties activeConcreteInstance= null;
	protected Map<ConfigurationProperties.Property, String> propertiesValueMap = new HashMap<ConfigurationProperties.Property, String>();
    //These should all be upper case.  As you touch them change them
	public enum Property { AmbiguousDateValue,
						     AmbiguousDateHolder,
						     ReferingLabParentOrg,
						     FormFieldSet,
						     PasswordRequirments,
						     StringContext,
						     StatusRules,
						     MenuTopItems,
						     MenuPermissions,
						     MenuName,
						     SiteCode,
						     SiteName,
						     AccessionFormat,
						     ReflexAction,
						     TrainingInstallation,
						     UseExternalPatientInfo,
						     PatientSearchURL,
						     PatientSearchUserName,
						     PatientSearchPassword,
						     labDirectorName,
						     languageSwitch,
						     reportResults,
						     resultReportingURL,
						     malariaSurveillanceReport,
						     malariaSurveillanceReportURL,						     
						     malariaCaseReport,
						     malariaCaseReportURL,
						     releaseNumber,
						     buildNumber,
						     configurationName,
						     testUsageReportingURL,
						     testUsageReporting,
						     roleRequiredForModifyResults,
						     notesRequiredForModifyResults,
							 resultTechnicianName,
							 autoFillTechNameBox,
							 autoFillTechNameUser,
							 AUTOFILL_COLLECTION_DATE,
							 failedValidationMarker,
							 resultsResendTime,
							 useLogoInReport,
							 trackPatientPayment,
							 ALERT_FOR_INVALID_RESULTS,
							 DEFAULT_LANG_LOCALE,
							 DEFAULT_DATE_LOCALE,
							 CONDENSE_NFS_PANEL,
							 PATIENT_DATA_ON_RESULTS_BY_ROLE,
							 USE_PAGE_NUMBERS_ON_REPORTS,
							 QA_SORT_EVENT_LIST,
							 ALWAYS_VALIDATE_RESULTS,
							 ADDITIONAL_SITE_INFO,
							 SUBJECT_ON_WORKPLAN,
							 NEXT_VISIT_DATE_ON_WORKPLAN,
							 RESULTS_ON_WORKPLAN,
							 ACCEPT_EXTERNAL_ORDERS,
							 SIGNATURES_ON_NONCONFORMITY_REPORTS,
							 NONCONFORMITY_RECEPTION_AS_UNIT,
							 NONCONFORMITY_SAMPLE_COLLECTION_AS_UNIT,
							 PATIENT_REPORT_NO_ALERTS,
							 ACCESSION_NUMBER_PREFIX,
                             NOTE_EXTERNAL_ONLY_FOR_VALIDATION}

	

	public static ConfigurationProperties getInstance(){
		synchronized (lockObj) {
			if (activeConcreteInstance == null) {
				activeConcreteInstance = new DefaultConfigurationProperties();
			}
		}
		return activeConcreteInstance;
	}

	public String getPropertyValue( Property property ){
		loadIfPropertyValueNeeded(property);

		return GenericValidator.isBlankOrNull(propertiesValueMap.get(property)) ? null : propertiesValueMap.get(property).trim();
	}

	public String getPropertyValueUpperCase( Property property ){
		String value = getPropertyValue(property);
		return value == null ? null : value.toUpperCase();
	}

	public String getPropertyValueLowerCase( Property property ){
		String value = getPropertyValue(property);
		return value == null ? null : value.toLowerCase();
	}

	public static void forceReload(){
		activeConcreteInstance = null;
	}

	/*
	 * Allowing for lazy loading.
	 */
	abstract protected void loadIfPropertyValueNeeded(Property property);

	public boolean isPropertyValueEqual(Property property, String target) {
		
		if( target == null){
			return getPropertyValue(property) == null;
		}else{
			return target.equals(getPropertyValue(property));
		}
	}
	
	public boolean isCaseInsensitivePropertyValueEqual(Property property, String target) {
		if( target == null){
			return getPropertyValue(property) == null;
		}else{
			return target.toLowerCase().equals(getPropertyValueLowerCase(property));
		}
	}
	
	public void setPropertyValue( Property property, String value){
		propertiesValueMap.put(property, value);
	}

	/**
	 * For testing only to set a controllable singleton
	 * @param activeConcreteInstance
	 */
	public static void setActiveConcreteInstance( ConfigurationProperties activeConcreteInstance) {
		ConfigurationProperties.activeConcreteInstance = activeConcreteInstance;
	}
}
