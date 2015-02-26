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
package us.mn.state.health.lims.common.action;

/**
 * IActionConstants.java
 *
 * @author diane benz 10/31/2005
 *
 * This interface contains the constants for bean and attribute names used by
 * the struts action classes.
 * bugzilla 2053 cleaning up routing/forwarding constants
 */
public interface IActionConstants {
	/**
	 * The key for the page title in the request scope.
	 */
	public static final String PAGE_TITLE_KEY = "title";
	/**
	 * The key for the page subtitle in the request scope.
	 */
	public static final String PAGE_SUBTITLE_KEY = "subtitle";

	//bugzilla #1346 add ability to hover over accession number and
    //view patient/person information (first and last name and external id)
	//moved following 3 definitions to here
	public static final String INVALID = "invalid";
	public static final String VALID = "valid";
	public static final String INVALID_TO_LARGE = "invalid_value_to_large";
	public static final String INVALID_TO_SMALL = "invalid_value_to_small";

	public static final String YES = "Y";
	public static final String NO = "N";

	public static final String NOT_APPLICABLE = "N/A";

	//#1347 added BLANK
	public static final String BLANK = "";

	//bugzilla 1798 test linked to parent test
	public static final String CHILD_TYPE_NONE = "0";
	public static final String CHILD_TYPE_REFLEX = "1";
	public static final String CHILD_TYPE_LINK = "2";

	//AIS added these
	//DB changed some of this naming to follow conventions
	public static final String INVALIDSTATUS = "invalidStatus";
	public static final String VALIDSTATUS = "validStatus";
	public static final String MORE_THAN_ONE_ACCESSION_NUMBER = "moreThanOneAccessionNumber";
	public static final String INVALIDOTHERS = "invalidOthers";


	//action forward constants
	//bugzilla 1900
	public static final String FWD = "forward";
	public static final String FWD_CLOSE = "close";
	public static final String FWD_FAIL = "fail";
	public static final String FWD_VALIDATION_ERROR = "error";
	public static final String FWD_SUCCESS = "success";
	public static final String FWD_SUCCESS_INSERT = "insertSuccess";
	public static final String FWD_NEXT = "next";
	public static final String FWD_PREVIOUS = "previous";
	public static final String FWD_SUCCESS_HUMAN = "successHuman";
	public static final String FWD_SUCCESS_ANIMAL = "successAnimal";
	//bugzilla 2566
	public static final String FWD_SUCCESS_NEWBORN = "successNewborn";
	public static final String FWD_FAIL_HUMAN = "failHuman";
	public static final String FWD_FAIL_ANIMAL = "failAnimal";
	//bugzilla 2501
	public static final String FWD_SUCCESS_MULTIPLE_SAMPLE_MODE = "successMultipleSampleMode";
	//bugzilla 2504
	public static final String FWD_SUCCESS_LINE_LISTING_VIEW = "successLineListingView";
	//bugzilla 2502
	public static final String FWD_SUCCESS_FULL_SCREEN_VIEW_TEST_SECTION = "successFullScreenViewTestSection";
	public static final String FWD_SUCCESS_FULL_SCREEN_VIEW_SAMPLE_SECTION = "successFullScreenViewSampleSection";

	//bugzilla 1992 cleanup: These two forwards are for batch results verification (View - only particular test and ViewAll - all tests by accn#)
	public static final String FWD_SUCCESS_OTHER = "successother";
	public static final String FWD_FAIL_OTHER = "failother";

	//bugzilla 2028 routing to QA Events Entry from Results/Batch Results Entry Update Action if UNSATISFACTORY results
	public static final String FWD_SUCCESS_QA_EVENTS_ENTRY = "successQaEventsEntry";
	public static final String UNSATISFACTORY_RESULT = "UNSATISFACTORY";

	public static final String ALLOW_EDITS_KEY = "allowEdits";
	//bugzilla 1413
	public static final String MENU_SELECT_LIST_HEADER_SEARCH_STRING = "menuSelectListHeaderSearchString";
	public static final String IN_MENU_SELECT_LIST_HEADER_SEARCH = "inMenuSelectListHeaderSearch";
	public static final String MENU_SEARCH_BY_TABLE_COLUMN ="menuSearchByTableColumn";
	// end of bugzilla 1413

	// bugzilla 2503
	public static final String POPUPFORM_FILTER_BY_TABLE_COLUMN = "popupFormFilterByTableColumn";
	public static final String IN_POPUP_FORM_SEARCH = "inPopupFormSearch";
	public static final String POPUP_FORM_SEARCH_STRING = "popupFormSearchString";

	public static final String ACTION_KEY = "action";

	public static final String FORM_NAME = "formName";

	public static final String DEFAULT = "default";

	public static final String PREVIOUS_DISABLED = "previousDisabled";
	public static final String NEXT_DISABLED = "nextDisabled";
	public static final String DEACTIVATE_DISABLED = "deactivateDisabled";
	//bugzilla 1922
	public static final String ADD_DISABLED = "addDisabled";
	public static final String EDIT_DISABLED = "editDisabled";
	public static final String SAVE_DISABLED = "saveDisabled";
	public static final String VIEW_DISABLED = "viewDisabled";
	//bugzilla 2300
	public static final String CANCEL_DISABLED = "cancelDisabled";
	//bugzilla 2500
	public static final String ADD_DISABLED_ALL_TEST_QAEVENTS_COMPLETED = "addDisabledAllTestQaEventsCompleted";
	//bugzilla 2500
	public static final String ADD_DISABLED_ALL_SAMPLE_QAEVENTS_COMPLETED = "addDisabledAllSampleQaEventsCompleted";

	public static final String CLOSE = "close";
	//bugzilla 2062
	public static final String RECORD_FROZEN_EDIT_DISABLED_KEY = "recordFrozenDisableEdits";


	//bugzilla 2053
	public static final String RESULTS_ENTRY_ROUTING_SWITCH = "resultsEntryRoutingSwitch";
	public static final int RESULTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_VERIFICATION = 1;
	public static final int RESULTS_ENTRY_ROUTING_FROM_QAEVENTS_ENTRY = 2;
	//bugzilla 2504
	public static final int RESULTS_ENTRY_ROUTING_FROM_QAEVENTS_ENTRY_LINELISTING = 3;
	public static final String RESULTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_VERIFICATION_PARAM_TEST_ID = "resultsEntryFromBatchVerificationTestId";
	public static final String RESULTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_VERIFICATION_PARAM_ACCESSION_NUMBER = "resultsEntryFromBatchVerificationAccessionNumber";
	public static final String RESULTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_VERIFICATION_PARAM_TEST_SECTION_ID = "resultsEntryFromBatchVerificationTestSectionId";

	//bugzilla 2053
	public static final String QA_EVENTS_ENTRY_ROUTING_SWITCH = "qaEventsEntryRoutingSwitch";
	public static final int QA_EVENTS_ENTRY_ROUTING_FROM_TEST_MANAGEMENT = 1;
	public static final int QA_EVENTS_ENTRY_ROUTING_FROM_RESULTS_ENTRY = 2;
	public static final int QA_EVENTS_ENTRY_ROUTING_FROM_SAMPLE_TRACKING = 3;
	public static final int QA_EVENTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_ENTRY = 4;
	public static final int QA_EVENTS_ENTRY_ROUTING_FROM_QAEVENTS_ENTRY_LINELISTING = 5;
	public static final String QA_EVENTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_ENTRY_PARAM_TEST_ID = "testIdForUnsatisfactorySamples";
	public static final String QA_EVENTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_ENTRY_PARAM_ACCESSION_NUMBERS = "accessionNumbersForUnsatisfactorySamples";
	public static final String QA_EVENTS_ENTRY_ROUTING_FROM_RESULTS_ENTRY_PARAM_ACCESSION_NUMBER = "accessionNumbersForUnsatisfactorySample";
	//bugzilla 2504
	public static final String QAEVENTS_ENTRY_PARAM_QAEVENT_CATEGORY_ID = "qaEventsEntryCategoryId";
	public static final String QAEVENTS_ENTRY_PARAM_MULTIPLE_SAMPLE_MODE = "qaEventsEntryMultipleSampleMode";


	//bugzilla 2504
	public static final String QA_EVENTS_ENTRY_LINELISTING_ROUTING_SWITCH = "qaEventsEntryLineListingRoutingSwitch";
	public static final int QA_EVENTS_ENTRY_LINELISTING_ROUTING_FROM_QAEVENTS_ENTRY = 1;
	public static final String QAEVENTS_ENTRY_LINELISTING_PARAM_QAEVENT_CATEGORY_ID = "qaEventsEntryLineListingCategoryId";
	//bugzilla 2502, 2504
	public static final String QAEVENTS_ENTRY_PARAM_VIEW_MODE = "qaEventsEntryViewMode";
	public static final String QAEVENTS_ENTRY_NORMAL_VIEW = "qaEventsEntryNormalView";
	public static final String QAEVENTS_ENTRY_FULL_SCREEN_VIEW = "qaEventsEntryFullScreenView";
	public static final String QAEVENTS_ENTRY_FULL_SCREEN_VIEW_SECTION = "qaEventsEntryFullScreenViewSection";
	public static final String QAEVENTS_ENTRY_FULL_SCREEN_VIEW_SAMPLE_SECTION = "sampleSection";
	public static final String QAEVENTS_ENTRY_FULL_SCREEN_VIEW_TEST_SECTION = "testSection";

//	bugzilla 2053
	public static final String TEST_MANAGEMENT_ROUTING_SWITCH = "testManagementRoutingSwitch";
	public static final int TEST_MANAGEMENT_ROUTING_FROM_RESULTS_ENTRY = 1;
	public static final int TEST_MANAGEMENT_ROUTING_FROM_QAEVENTS_ENTRY = 2;
	//bugzilla 2504
	public static final int TEST_MANAGEMENT_ROUTING_FROM_QAEVENTS_ENTRY_LINELISTING = 3;

	//bugzilla 1411
	public static final String MENU_TOTAL_RECORDS = "totalRecordCount";
	public static final String MENU_FROM_RECORD = "fromRecordCount";
	public static final String MENU_TO_RECORD = "toRecordCount";
	//end bugzilla 1411

	//added for bugzilla 1467
	public static final String TRUE = "true";
	public static final String FALSE = "false";

	//added for bugzilla 1431
	public static final String OPEN_REPORTS_MODULE = "openreports";

	//added for bugzilla 1518 free form notes for result table
	public static final String NOTES_REFID = "refid";
	public static final String NOTES_REFTABLE = "reftable";
	//bugzilla 1942
	public static final String NOTES_EXTERNAL_NOTES_DISABLED = "externalNotesDisabled";
	public static final String POPUP_NOTES = "popupNotes";
	public static final String SELECTED_TEST_ID = "selectedTestId";
	public static final String ANALYSIS_ID = "analysisId";
	public static final String ANALYTE_ID = "analyteId";

	//bugzilla 2028
	public static final String QA_EVENT_ID = "qaEventId";

	//bugzilla 1742
	public static final String REPORT_TEST_ID_PARAMETER = "Test_Id";
	public static final String REPORT_PROJECT_ID_PARAMETER = "Project_Id";
	//bugzilla 1842 (adding Test_Section_Id for Virology Sample List by Test)
	public static final String REPORT_TEST_SECTION_ID_PARAMETER = "Test_Section_Id";
	public static final String THE_TREE = "tree";


	//bugzilla 1802
	public static final String ACCESSION_NUMBER = "accessionNumber";
	//bugzilla 2501 (used for actual versus requested)
	public static final String ACCESSION_NUMBER_REQUESTED = "requestedAccessionNumber";

	//bugzilla 1926
	public static final String AUDIT_TRAIL_DELETE = "D";
	public static final String AUDIT_TRAIL_UPDATE = "U";
	public static final String AUDIT_TRAIL_INSERT = "I";

	public static final String ID = "ID";

	//bugzilla 2028 QA_EVENT
	public static final String SAMPLE_TYPE_NOT_GIVEN = "NOT GIVEN";

	//user info
	public static final String USER_SESSION_DATA         = "userSessionData";
	public static final String LOGIN_PAGE				 = "loginPage";
	public static final String HOME_PAGE				 = "homePage";
	public static final String MAIN_PAGE                 = "Main";
	//bugzilla 2286
	public static final String FWD_CHANGE_PASS   = "changePassword";
	public static final String LOGIN_FAILED_CNT  = "loginFailedCount";
	public static final String ACCOUNT_LOCK_TIME = "lockTime";

	//bugzilla 2131
	public static final String TEMP_PDF_FILE = "tempPDFFile";

	//bugzilla 1847
	public static final String LOCAL_CODE_DICT_ENTRY_SEPARATOR_STRING = ": ";

	//bugzilla 2265
	public static final String RESULTS_REPORT_TYPE_PARAM = "type";
	public static final String RESULTS_REPORT_TYPE_ORIGINAL = "original";
	public static final String RESULTS_REPORT_TYPE_AMENDED = "amended";
	//bugzilla 1900
	public static final String RESULTS_REPORT_TYPE_PREVIEW = "preview";

	//bugzilla 2293
	public static final String ASSIGNABLE_TEST_TYPE_TEST = "testType";
	public static final String ASSIGNABLE_TEST_TYPE_PANEL = "panelType";

	//bugzilla 1900
	public static final String ACCESSION_NUMBERS = "accessionNumbers";
	public static final String SELECTED_TEST_SECTION_ID = "selectedTestSectionId";

	//bugzilla 2380
	public static final String NO_LABEL_PRINTING = "NONE";

	//bugzilla 2501
	public static final String CURRENT_RECORD = "currentSampleRecord";
	public static final String TOTAL_RECORDS = "totalSampleRecords";
	public static final String MULTIPLE_SAMPLE_MODE = "multipleSampleMode";

	public static final String PERMITTED_ACTIONS_MAP = "permittedActions";

	public static final String FORM_FIELD_SET_HAITI = "HAITI";
	public static final String FORM_FIELD_SET_LNSP_HAITI = "LNSP_HAITI";
	public static final String FORM_FIELD_SET_LNSP_CI = "LNSP_CI";
	public static final String FORM_FIELD_SET_CDI = "CDI";
	public static final String FORM_FIELD_SET_CI_IPCI = "CI_IPCI";
	public static final String FORM_FIELD_SET_CI_REGIONAL = "CI_REGIONAL";
	public static final String FORM_FIELD_SET_KENYA = "KENYA";

	public static final String ACTION_REFINEMENT_SEPARATOR = ":";

	public static final String ANALYSIS_TYPE_MANUAL = "MANUAL";
	public static final String ANALYSIS_TYPE_AUTO = "AUTO";

	public static final String STATUS_RULES_HAITI = "HAITI";
	public static final String STATUS_RULES_HAITI_LNSP = "LNSP_HAITI";
	public static final String STATUS_RULES_RETROCI = "RETROCI";
	public static final String SAMPLE_EDIT_WRITABLE = "SampleEditWritable";

	public static final String RESULTS_SESSION_CACHE = "ResultsSessionCache";
	public static final String RESULTS_PAGE_MAPPING_SESSION_CACHE = "ResultsPageMappingSessionCache";
	public static final int PAGING_SIZE = 60;
	
	/**
	 * The system_module name used to determine if the current user is allowed to edit primary patient IDs (subject number & site subject number).
	 */
	public static final String MODULE_ACCESS_PATIENT_SUBJECTNOS_EDIT = "Access.patient.subjectNos.edit";
    public static final String MODULE_ACCESS_SAMPLE_ACCESSIONNO_EDIT = "Access.sample.accessionNo.edit";
    
    public static final String ORG_SAMPLE_TYPE_REFERRER = "R";
}