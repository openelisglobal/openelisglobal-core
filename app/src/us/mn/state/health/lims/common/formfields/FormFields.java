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
package us.mn.state.health.lims.common.formfields;

import java.util.Map;

/*
 * These are different fields on the forms which can be turned on and off by configuration.
 * Note that the administration menu is in it's own class because it is a big area confined to it a single page
 */
public class FormFields {
	//Note- these should all be upper case, change as you touch and add form name to name
	public static enum Field {
		AKA,
		StNumber,
		MothersName,
		PatientType,
		InsuranceNumber,
		CollectionDate,
		CollectionTime,
		RequesterSiteList,
		OrgLocalAbrev,
		OrgState,
		ZipCode,
		MLS,
		InlineOrganizationTypes,
		SubjectNumber,
		ProviderInfo,
		NationalID,
		Occupation,
		Commune,
		MotherInitial,
		ResultsAccept,
		SearchSampleStatus,
		OrganizationAddressInfo,
		OrganizationCLIA,
		OrganizationParent,
		OrganizationShortName,
		OrganizationMultiUnit,
		OrganizationOrgId,
		Project,
		SampleCondition,
		NON_CONFORMITY_SITE_LIST,                 // site (patient entry or nonconforming) is defined by a list of sites.
		NON_CONFORMITY_SITE_LIST_USER_ADDABLE,
		NON_CONFORMITY_PROVIDER_ADDRESS,
		AddressCity,
		AddressDepartment,
		AddressCommune,
		AddressVillage,
		DepersonalizedResults,
		SEARCH_PATIENT_WITH_LAB_NO,
		ResultsReferral,
		ValueHozSpaceOnResults,  //favors a layout which values horizontal space over vertical space
		InitialSampleCondition,
		PatientRequired,         // By default, a (minimal) patient to go with a sample is required.
		PatientRequired_SampleConfirmation,
		QAFullProviderInfo,         // Store doctor info. as a simple observation history field or as person record.
		QASubjectNumber,
		QATimeWithDate, 
		PatientIDRequired,
		PatientIDRequired_SampleConfirmation,
		PatientNameRequired,
		PatientAgeRequired_SampleConfirmation,
		PatientGenderRequired_SampleConfirmation,
		PatientAgeRequired_SampleEntry,
		PatientGenderRequired_SampleEntry,	
		SampleEntryUseReceptionHour,
		SampleEntryUseRequestDate,
		SampleEntryNextVisitDate,
		SampleEntryRequestingSiteSampleId,
		SampleEntryReferralSiteNameRequired,
		SampleEntryReferralSiteNameCapitialized,
		SampleEntryReferralSiteCode,
		SampleEntryProviderFax,
		SampleEntryProviderEmail,
		SampleEntryHealthFacilityAddress,
		SampleEntryLabOrderTypes,
		SampleEntrySampleCollector,
		SampleEntryRequesterLastNameRequired,
		SAMPLE_ENTRY_USE_REFFERING_PATIENT_NUMBER,
		PatientPhone,
		PatientHealthRegion,
		PatientHealthDistrict,
		PatientNationality,
		PatientMarriageStatus,
		PatientEducation,
		SampleEntryPatientClinical,
		QA_DOCUMENT_NUMBER
	}

	private static FormFields instance = null;

	private Map<FormFields.Field, Boolean> fields;

	private FormFields(){
		fields = new DefaultFormFields().getFieldFormSet();
	}

	public static FormFields getInstance(){
		if( instance == null){
			instance = new FormFields();
		}

		return instance;
	}

	public boolean useField( FormFields.Field field){
		return fields.get(field);
	}
}
