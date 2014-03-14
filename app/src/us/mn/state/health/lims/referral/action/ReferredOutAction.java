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
package us.mn.state.health.lims.referral.action;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.services.NoteService;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.IdValuePair;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.note.valueholder.Note;
import us.mn.state.health.lims.organization.dao.OrganizationDAO;
import us.mn.state.health.lims.organization.daoimpl.OrganizationDAOImpl;
import us.mn.state.health.lims.organization.valueholder.Organization;
import us.mn.state.health.lims.referral.action.beanitems.IReferralResultTest;
import us.mn.state.health.lims.referral.action.beanitems.ReferralItem;
import us.mn.state.health.lims.referral.action.beanitems.ReferredTest;
import us.mn.state.health.lims.referral.dao.ReferralDAO;
import us.mn.state.health.lims.referral.dao.ReferralResultDAO;
import us.mn.state.health.lims.referral.daoimpl.ReferralDAOImpl;
import us.mn.state.health.lims.referral.daoimpl.ReferralResultDAOImpl;
import us.mn.state.health.lims.referral.util.ReferralUtil;
import us.mn.state.health.lims.referral.valueholder.Referral;
import us.mn.state.health.lims.referral.valueholder.ReferralResult;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;
import us.mn.state.health.lims.typeofsample.dao.TypeOfSampleDAO;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleDAOImpl;
import us.mn.state.health.lims.typeofsample.util.TypeOfSampleUtil;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class ReferredOutAction extends BaseAction {

	private static final String REFERRAL_LAB = "referralLab";
	private static ReferralResultDAO referralResultDAO = new ReferralResultDAOImpl();
	private static TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
	private static ResultDAO resultDAO = new ResultDAOImpl();
	private static DictionaryDAO dictionaryDAO = new DictionaryDAOImpl();
	private List<NonNumericTests> nonNumericTests;
	private static String RESULT_REFERENCE_TABLE_ID = NoteService.getTableReferenceId( "RESULT" );

	@Override
	protected String getPageSubtitleKey() {
		return "referral.out.manage";
	}

	@Override
	protected String getPageTitleKey() {
		return "referral.out.manage";
	}

	@Override
	protected ActionForward performAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		DynaActionForm dynaForm = (DynaActionForm) form;

		request.getSession().setAttribute(SAVE_DISABLED, TRUE);

		List<ReferralItem> referralItems = getReferralItems();
		PropertyUtils.setProperty(dynaForm, "referralItems", referralItems);
		PropertyUtils.setProperty(dynaForm, "referralReasons", ReferralUtil.getReferralReasons());

		List<IdValuePair> referralOrganizations = getReferralOrganizations();
		PropertyUtils.setProperty(dynaForm, "referralOrganizations", referralOrganizations);

		nonNumericTests = getNonNumericTests(referralItems);
		PropertyUtils.setProperty(dynaForm, "nonNumericTests", nonNumericTests);

		fillInDictionaryValuesForReferralItems(referralItems);

		return mapping.findForward(IActionConstants.FWD_SUCCESS);
	}

	private void fillInDictionaryValuesForReferralItems(List<ReferralItem> referralItems) {
		for (ReferralItem referralItem : referralItems) {
			String referredResultType = referralItem.getReferredResultType();
            if (isSelectList(referredResultType)) {
				referralItem.setDictionaryResults(getDictionaryValuesForTest(referralItem.getReferredTestId()));
			}

			if (referralItem.getAdditionalTests() != null) {
				for (ReferredTest test : referralItem.getAdditionalTests()) {
					if (isSelectList(test.getReferredResultType())) {
						test.setDictionaryResults(getDictionaryValuesForTest(test.getReferredTestId()));
					}
				}
			}
		}

	}

	private List<IdValuePair> getReferralOrganizations() {
		List<IdValuePair> pairs = new ArrayList<IdValuePair>();

		OrganizationDAO orgDAO = new OrganizationDAOImpl();
		List<Organization> orgs = orgDAO.getOrganizationsByTypeName("organizationName", REFERRAL_LAB);
		pairs.add(new IdValuePair("0", ""));

		for (Organization org : orgs) {
			pairs.add(new IdValuePair(org.getId(), org.getOrganizationName()));
		}

		return pairs;
	}

	private List<ReferralItem> getReferralItems() {
		List<ReferralItem> referralItems = new ArrayList<ReferralItem>();
		ReferralDAO referralDAO = new ReferralDAOImpl();

		List<Referral> referralList = referralDAO.getAllUncanceledOpenReferrals();

		for (Referral referral : referralList) {
			ReferralItem referralItem = null;
			referralItem = getReferralItem(referral);
			if (referralItem != null) {
				referralItems.add(referralItem);
			}
		}
		
		Collections.sort(referralItems, new ReferralComparator());

		return referralItems;
	}
	
	private final static class ReferralComparator implements Comparator<ReferralItem>{
        @Override
        public int compare(ReferralItem left, ReferralItem right) {
            int result = left.getAccessionNumber().compareTo(right.getAccessionNumber());
            if (result != 0) {
                return  result;
            }
            result = left.getSampleType().compareTo(right.getSampleType());
            if (result != 0) {
                return result;
            }
            return result = left.getReferringTestName().compareTo(right.getReferringTestName());
        }	    
	}

	private ReferralItem getReferralItem(Referral referral) {
		boolean allReferralResultsHaveResults = true;
		List<ReferralResult> referralResults = referralResultDAO.getReferralResultsForReferral(referral.getId());
		for (ReferralResult referralResult : referralResults) {
			if (referralResult.getResult() == null || GenericValidator.isBlankOrNull(referralResult.getResult().getValue())) {
				allReferralResultsHaveResults = false;
				break;
			}
		}

		if (allReferralResultsHaveResults) {
			return null;
		}

		ReferralItem referralItem = new ReferralItem();

		Analysis analysis = referral.getAnalysis();
		SampleItem sampleItem = analysis.getSampleItem();

		referralItem.setCanceled(false);
		referralItem.setReferredResultType("N");
		referralItem.setAccessionNumber(sampleItem.getSample().getAccessionNumber());

		TypeOfSample typeOfSample = typeOfSampleDAO.getTypeOfSampleById(sampleItem.getTypeOfSampleId());
		referralItem.setSampleType(typeOfSample.getLocalizedName());

		referralItem.setReferringTestName(referral.getAnalysis().getTest().getLocalizedName());

		List<Result> resultList = resultDAO.getResultsByAnalysis(analysis);
		String resultString = "";

		if (!resultList.isEmpty()) {
			Result result = resultList.get(0);
			resultString = getAppropriateResultValue(resultList);
			referralItem.setCasualResultId(result.getId());
			
			List<Note> notes = NoteService.getNotesForObjectAndTable( result.getId(), RESULT_REFERENCE_TABLE_ID );
			if (!(notes == null || notes.isEmpty())) {
				Collections.sort(notes, new Comparator<Note>() {
					@Override
					public int compare(Note o1, Note o2) {
						return Integer.parseInt(o1.getId()) - Integer.parseInt(o2.getId());
					}
				});
				
				StringBuilder noteBuilder = new StringBuilder();
				
				for(Note note : notes){
					noteBuilder.append(note.getText());
					noteBuilder.append("<br/>");
				}
				
				noteBuilder.setLength( noteBuilder.lastIndexOf("<br/>"));
				referralItem.setPastNotes(noteBuilder.toString());

			}
		}

		referralItem.setReferralId(referral.getId());
		if (!referralResults.isEmpty()) {
		    referralResults = setReferralItem(referralItem, referralResults);
			if (referralResults.size() >= 1) {
				referralItem.setAdditionalTests(getAdditionalReferralTests(referralResults/* PAH, referral */));
			}
		}
		referralItem.setReferralResults(resultString);
		referralItem.setReferralDate(DateUtil.convertTimestampToStringDate(referral.getRequestDate()));
		referralItem.setReferredSendDate(getSendDateOrDefault(referral));
		referralItem.setReferrer(referral.getRequesterName());
		referralItem.setReferralReasonId(referral.getReferralReasonId());
		referralItem.setTestSelectionList(getTestsForTypeOfSample(typeOfSample));
		referralItem.setReferralId(referral.getId());
		if (referral.getOrganization() != null) {
			referralItem.setReferredInstituteId(referral.getOrganization().getId());
		}

		return referralItem;
	}

	private String getSendDateOrDefault(Referral referral) {
		if (referral.getSentDate() == null) {
			return DateUtil.getCurrentDateAsText();
		} else {
			return DateUtil.convertTimestampToStringDate(referral.getSentDate());
		}
	}

	private List<ReferredTest> getAdditionalReferralTests(List<ReferralResult> referralResults /*, Referral referral */) {
		List<ReferredTest> testList = new ArrayList<ReferredTest>();

		while( referralResults.size() > 0 ) {
		    ReferralResult referralResult = referralResults.get(0); // use the top one to load various bits of information. 
		    ReferredTest referralTest = new ReferredTest();
		    referralTest.setReferralId(referralResult.getReferralId());
		    referralResults = setReferralItem(referralTest, referralResults); // remove one or more referralResults from the list as needed (for multiResults).
			referralTest.setReferredReportDate(DateUtil.convertTimestampToStringDate(referralResult.getReferralReportDate()));
			referralTest.setReferralResultId(referralResult.getId());
			testList.add(referralTest);
		}
		return testList;
	}

	/**
	 * Move everything appropriate to the referralItem including one or more of the referralResults from the given list.
	 * Note: This method removes an item from the referralResults list.
	 * @param referralItem
	 * @param referralResults
	 */
	private List<ReferralResult> setReferralItem(IReferralResultTest referralItem, List<ReferralResult> referralResults) {
	    List<ReferralResult> leftOvers = new ArrayList<ReferralResult>(referralResults);
		ReferralResult baseResult = referralResults.remove(0);
		leftOvers.remove(0);
		referralItem.setReferredTestId(baseResult.getTestId());
		referralItem.setReferredReportDate(DateUtil.convertTimestampToStringDate(baseResult.getReferralReportDate()));
		Result result = baseResult.getResult();
		String resultType = (result != null)?result.getResultType():"N";
		referralItem.setReferredResultType(resultType);
		if ( !"M".equals(resultType) ) {
            if (result != null && result.getId() != null) {
    			String resultValue = GenericValidator.isBlankOrNull(result.getValue()) ? "" : result.getValue();
    			referralItem.setReferredResult(resultValue);
    			referralItem.setReferredDictionaryResult(resultValue);
    		}
		} else {
            String multiResultValue = GenericValidator.isBlankOrNull(result.getValue()) ? "" : result.getValue();
		    for (ReferralResult referralResult : referralResults) {
                if (baseResult.getTestId().equals(referralResult.getTestId()) ) {
                    multiResultValue += ", " + referralResult.getResult().getValue(); 
                    leftOvers.remove(referralResult);                    
                }
            }
		    referralItem.setReferredMultiDictionaryResult(multiResultValue);
		}
		return leftOvers;
	}

	private List<IdValuePair> getDictionaryValuesForTest(String testId) {
		if (!GenericValidator.isBlankOrNull(testId)) {
			for (NonNumericTests test : nonNumericTests) {
				if (testId.equals(test.testId)) {
					return test.dictionaryValues;
				}
			}
		}
		return new ArrayList<IdValuePair>();
	}

	private String getAppropriateResultValue(List<Result> results) {
	    Result result = results.get(0);
		if ("D".equals(result.getResultType())) {
			Dictionary dictionary = dictionaryDAO.getDictionaryById(result.getValue());
			if (dictionary != null) {
				return dictionary.getLocalizedName();
			}
		} else if ("M".equals(result.getResultType())) {
            Dictionary dictionary = new Dictionary();
            StringBuilder multiResult = new StringBuilder();
        
            for( Result subResult : results){
                dictionary.setId(subResult.getValue());
                dictionaryDAO.getData(dictionary);
        
                if( dictionary.getId() != null ){
                    multiResult.append(dictionary.getLocalizedName());
                    multiResult.append(", ");
                }
            }
        
            if ( multiResult.length() > 0 ) {
                multiResult.setLength(multiResult.length() - 2); //remove last ", "
            }
        
            return multiResult.toString();
        } else {
			String resultValue = GenericValidator.isBlankOrNull(result.getValue()) ? "" : result.getValue();

			if ( !GenericValidator.isBlankOrNull(resultValue) &&
				 result.getAnalysis().getTest().getUnitOfMeasure() != null) {
				resultValue += " " + result.getAnalysis().getTest().getUnitOfMeasure().getName();
			}

			return resultValue;
		}

		return "";
	}

	private List<IdValuePair> getTestsForTypeOfSample(TypeOfSample typeOfSample) {
		List<Test> testList = TypeOfSampleUtil.getTestListBySampleTypeId(typeOfSample.getId(), null, false);

		List<IdValuePair> valueList = new ArrayList<IdValuePair>();

		for (Test test : testList) {
			valueList.add(new IdValuePair(test.getId(), test.getLocalizedName()));
		}

		return valueList;
	}

	private List<NonNumericTests> getNonNumericTests(List<ReferralItem> referralItems) {
		DictionaryDAO dictionaryDAO = new DictionaryDAOImpl();
		Set<String> testIdSet = new HashSet<String>();

		for (ReferralItem item : referralItems) {
			for (IdValuePair pair : item.getTestSelectionList()) {
				testIdSet.add(pair.getId());
			}
		}

		List<NonNumericTests> nonNumericTestList = new ArrayList<NonNumericTests>();
		TestResultDAO testResultDAO = new TestResultDAOImpl();
		for (String testId : testIdSet) {
			List<TestResult> testResultList = testResultDAO.getTestResultsByTest(testId);

			if (!(testResultList == null || testResultList.isEmpty())) {
				NonNumericTests nonNumericTests = new NonNumericTests();

				nonNumericTests.testId = testId;
                nonNumericTests.testType = testResultList.get(0).getTestResultType();
				boolean isSelectList = isSelectList(nonNumericTests.testType);

				if (isSelectList) {
					List<IdValuePair> dictionaryValues = new ArrayList<IdValuePair>();
					for (TestResult testResult : testResultList) {
						if (isSelectList(testResult.getTestResultType())) {
							String resultName = dictionaryDAO.getDictionaryById(testResult.getValue()).getLocalizedName();
							dictionaryValues.add(new IdValuePair(testResult.getValue(), resultName));
						}
					}

					nonNumericTests.dictionaryValues = dictionaryValues;
				}

				if (nonNumericTests.testType != null) {
					nonNumericTestList.add(nonNumericTests);
				}
			}

		}

		return nonNumericTestList;
	}

    /**
     * The types of testResults which mean that there will be a list of choices/options to select from.
     * @param testResultType
     * @return true if it is one of the types which means a list of choices, false otherwise.
     */
	public static boolean isSelectList(String testResultType) {
        return "DMQ".contains( testResultType );
    }

	public class NonNumericTests {
		public String testId;
		public String testType;
		public List<IdValuePair> dictionaryValues;
	}
}
