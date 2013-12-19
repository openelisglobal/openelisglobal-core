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

import org.apache.commons.validator.GenericValidator;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.resultlimits.daoimpl.ResultLimitDAOImpl;
import us.mn.state.health.lims.resultlimits.valueholder.ResultLimit;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleDAOImpl;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleTestDAOImpl;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSampleTest;

import java.util.List;

public class ResultService {

	private Result result;
	private Test test;
	private List<ResultLimit> resultLimit;

	public ResultService(Result result) {
		this.result = result;
		test = result.getAnalysis().getTest();
	}

	public String getLabSectionName() {
		return result.getAnalysis().getTestSection().getName();
	}

	public String getTestName() {
		return test.getTestName();
	}

	public String getTestDescription() {
		return test.getDescription();
	}

	public String getSampleType() {
		TypeOfSampleTest sampleTestType = new TypeOfSampleTestDAOImpl().getTypeOfSampleTestForTest(test.getId());

		if (sampleTestType != null) {
			return new TypeOfSampleDAOImpl().getNameForTypeOfSampleId(sampleTestType.getTypeOfSampleId());
		}

		return "";
	}

	public String getLOINCCode() {
		return test.getLoinc();
	}

	public String getTestTime() {
		return result.getAnalysis().getCompletedDateForDisplay();
	}

	public String getTestType() {
		return result.getResultType();
	}

	public String getResultValue() {
		DictionaryDAO dictionaryDAO = new DictionaryDAOImpl();
		if (GenericValidator.isBlankOrNull(result.getValue())) {
			return "";
		}

		if ("D".equals(getTestType())) {
			return dictionaryDAO.getDataForId(result.getValue()).getDictEntry();
		} else if ("M".equals(getTestType())) {
			StringBuilder buffer = new StringBuilder();
			boolean firstPass = true;

			List<Result> results = new ResultDAOImpl().getResultsByAnalysis(result.getAnalysis());
			for (Result multiResult : results) {
				if (!GenericValidator.isBlankOrNull(multiResult.getValue())) {
					if (firstPass) {
						firstPass = false;
					} else {
						buffer.append(",");
					}
					buffer.append(dictionaryDAO.getDataForId(multiResult.getValue()).getDictEntry());
				}
			}
			return buffer.toString();
		} else {
			return result.getValue();
		}
	}

	public String getUOM() {
		return test.getUnitOfMeasure().getUnitOfMeasureName();
	}

	public double getlowNormalRange() {
		return result.getMinNormal();
	}

	public double getHighNormalRange() {
		return result.getMaxNormal();
	}

	/**
	 * 
	 * @return true if any of the result limits for this test have a gender
	 *         specification
	 */
	public boolean ageInRangeCriteria() {
		List<ResultLimit> resultLimits = getResultLimits();

		for (ResultLimit limit : resultLimits) {
			if (limit.getMaxAge() != limit.getMinAge()) {
				return true;
			}
		}

		return false;
	}

	public boolean genderInRangeCritera() {
		List<ResultLimit> resultLimits = getResultLimits();

		for (ResultLimit limit : resultLimits) {
			if (!GenericValidator.isBlankOrNull(limit.getGender())) {
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	private List<ResultLimit> getResultLimits() {
		if (resultLimit == null) {
			resultLimit = new ResultLimitDAOImpl().getAllResultLimitsForTest(test);
		}

		return resultLimit;
	}

	public String getLastUpdatedTime() {
		return  DateUtil.convertTimestampToStringDate(result.getLastupdated());
	}
}
