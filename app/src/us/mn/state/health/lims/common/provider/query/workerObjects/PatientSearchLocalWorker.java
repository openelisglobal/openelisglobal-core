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
package us.mn.state.health.lims.common.provider.query.workerObjects;

import org.apache.commons.validator.GenericValidator;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.provider.query.PatientSearchResults;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.sample.dao.SearchResultsDAO;
import us.mn.state.health.lims.sample.daoimpl.SearchResultsDAOImp;

import java.util.List;

public class PatientSearchLocalWorker extends PatientSearchWorker {

	@Override
	public String createSearchResultXML(String lastName, String firstName, String STNumber, String subjectNumber, String nationalID,
			String patientID, String guid, StringBuilder xml)  {

		String success = IActionConstants.VALID;

		if( GenericValidator.isBlankOrNull(lastName) &&
				GenericValidator.isBlankOrNull(firstName) &&
				GenericValidator.isBlankOrNull(STNumber) &&
				GenericValidator.isBlankOrNull(subjectNumber) &&
				GenericValidator.isBlankOrNull(nationalID) &&
				GenericValidator.isBlankOrNull(patientID) &&
				GenericValidator.isBlankOrNull(guid)){

			xml.append("No search terms were entered");
			return IActionConstants.INVALID;
		}

		SearchResultsDAO search = new SearchResultsDAOImp();
		List<PatientSearchResults> results = search.getSearchResults(lastName, firstName, STNumber, subjectNumber, nationalID, nationalID, patientID, guid);

		sortPatients(results);

		if (results.size() > 0) {
			for (PatientSearchResults singleResult : results) {
				singleResult.setDataSourceName(StringUtil.getMessageForKey("patient.local.source"));
				appendSearchResultRow(singleResult, xml);
			}
		}else{
			success = IActionConstants.INVALID;

			xml.append("No results were found for search.  Check spelling or remove some of the fields");
		}

		return success;
	}
}
