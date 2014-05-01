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
package us.mn.state.health.lims.result.action;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import us.mn.state.health.lims.common.services.DisplayListService;
import us.mn.state.health.lims.common.services.DisplayListService.ListType;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.ConfigurationProperties.Property;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.IdValuePair;
import us.mn.state.health.lims.inventory.action.InventoryUtility;
import us.mn.state.health.lims.inventory.form.InventoryKitItem;
import us.mn.state.health.lims.referral.util.ReferralUtil;
import us.mn.state.health.lims.result.action.util.ResultsLoadUtility;
import us.mn.state.health.lims.result.action.util.ResultsPaging;
import us.mn.state.health.lims.statusofsample.util.StatusRules;
import us.mn.state.health.lims.test.beanItems.TestResultItem;
import us.mn.state.health.lims.test.dao.TestSectionDAO;
import us.mn.state.health.lims.test.daoimpl.TestSectionDAOImpl;
import us.mn.state.health.lims.test.valueholder.TestSection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ResultsLogbookEntryAction extends ResultsLogbookBaseAction {

	private InventoryUtility inventoryUtility = new InventoryUtility();

	protected ActionForward performAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;

		String requestedPage = request.getParameter("page");
		request.getSession().setAttribute(SAVE_DISABLED, TRUE);

		DynaActionForm dynaForm = (DynaActionForm) form;

		currentDate = getCurrentDate(request);
		PropertyUtils.setProperty(dynaForm, "currentDate", currentDate);
		PropertyUtils.setProperty(dynaForm, "logbookType", request.getParameter("type"));
		PropertyUtils.setProperty(dynaForm, "referralReasons", ReferralUtil.getReferralReasons());
		List<IdValuePair> rejectReasons = DisplayListService.getList(ListType.REJECTION_REASONS);
		rejectReasons.add(0, new IdValuePair("0",""));
		PropertyUtils.setProperty(dynaForm, "rejectReasons", rejectReasons);

		setLogbookRequest(request.getParameter("type"));

		String testSectionId = getTestSelectId();
		List<TestResultItem> tests = null;

		ResultsPaging paging = new ResultsPaging();
		List<InventoryKitItem> inventoryList = new ArrayList<InventoryKitItem>();
		ResultsLoadUtility resultsLoadUtility = new ResultsLoadUtility(currentUserId);

		if (GenericValidator.isBlankOrNull(requestedPage)) {
			
			new StatusRules().setAllowableStatusForLoadingResults(resultsLoadUtility);
			
			if (testSectionId != null) {
				tests = resultsLoadUtility.getUnfinishedTestResultItemsInTestSection(testSectionId);
			} else {
				tests = new ArrayList<TestResultItem>();
			}

			if( ConfigurationProperties.getInstance().isPropertyValueEqual(Property.PATIENT_DATA_ON_RESULTS_BY_ROLE, "true") &&   
					!userHasPermissionForModule(request, "PatientResults") ){
				for( TestResultItem resultItem : tests){
					resultItem.setPatientInfo("---");
				}
				
			}
			
			paging.setDatabaseResults(request, dynaForm, tests);

		} else {
			paging.page(request, dynaForm, requestedPage);
		}

		//this does not look right what happens after a new page!!!
		if (resultsLoadUtility.inventoryNeeded() || logbookRequest == logbooks.HIV) {
			inventoryList = inventoryUtility.getExistingActiveInventory();
			PropertyUtils.setProperty(dynaForm, "displayTestKit", true);
		} else {
			PropertyUtils.setProperty(dynaForm, "displayTestKit", false);
		}

		PropertyUtils.setProperty(dynaForm, "inventoryItems", inventoryList);

		setDisplayProperties(dynaForm);

		return mapping.findForward(forward);
	}

	private void setDisplayProperties(DynaActionForm dynaForm) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {

		switch (logbookRequest) {
		case HEMATOLOGY: {
			PropertyUtils.setProperty(dynaForm, "displayTestMethod", true);
			break;
		}
		case CHEM: {
			PropertyUtils.setProperty(dynaForm, "displayTestMethod", true);
			break;
		}
		case BACTERIOLOGY: {
			PropertyUtils.setProperty(dynaForm, "displayTestMethod", true);
			break;
		}
		case PARASITOLOGY: {
			PropertyUtils.setProperty(dynaForm, "displayTestMethod", true);
			break;
		}
		case IMMUNO: {
			PropertyUtils.setProperty(dynaForm, "displayTestMethod", true);
			break;
		}
		case ECBU: {
			PropertyUtils.setProperty(dynaForm, "displayTestMethod", true);
			break;
		}
		case HIV: {
			PropertyUtils.setProperty(dynaForm, "displayTestMethod", true);
			break;
		}
		case MOLECULAR_BIOLOGY: {
			PropertyUtils.setProperty(dynaForm, "displayTestMethod", true);
			break;
		}case LIQUID_BIOLOGY: {
			PropertyUtils.setProperty(dynaForm, "displayTestMethod", true);
			break;
		}case ENDOCRINOLOGY: {
			PropertyUtils.setProperty(dynaForm, "displayTestMethod", true);
			break;
		}
		default: {
			// no-op
		}
		}

	}

	private String getTestSelectId() {

		TestSection testSection = new TestSection();
		String logbookName = getNameForLogbookType(logbookRequest);
		testSection.setTestSectionName(logbookName);

		TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
		testSection = testSectionDAO.getTestSectionByName(testSection);

		return testSection == null ? null : testSection.getId();
	}

	private String getCurrentDate(HttpServletRequest request) {
		Date today = Calendar.getInstance().getTime();
		Locale locale = (Locale) request.getSession().getAttribute("org.apache.struts.action.LOCALE");
		return DateUtil.formatDateAsText(today, locale);

	}

}
