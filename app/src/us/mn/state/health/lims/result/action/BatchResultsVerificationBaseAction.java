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
*/
package us.mn.state.health.lims.result.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.result.valueholder.ResultsVerificationTestComparator;
import us.mn.state.health.lims.result.valueholder.Sample_TestAnalyte;


/**
 * @author Benzd1
 * bugzilla 1856
 */
public class BatchResultsVerificationBaseAction extends ResultsEntryBaseAction {

	//add in "phantom" tests to complete the hierarchy so we can sort correctly (the phantom tests are removed after)
	protected List completeHierarchyOfTestsForSorting(List sampleTestAnalytes){
		List newSampleTestAnalytes = new ArrayList();
		for (int i = 0; i < sampleTestAnalytes.size(); i++) {
			Sample_TestAnalyte sta = (Sample_TestAnalyte)sampleTestAnalytes.get(i);
			newSampleTestAnalytes.add(sta);
			recursiveHierarchyBuild(sta, sampleTestAnalytes, newSampleTestAnalytes);
		}
		return newSampleTestAnalytes;
	}

	protected List removePhantomTests(List sampleTestAnalytes) {
		Iterator it = sampleTestAnalytes.iterator();
		sampleTestAnalytes = new ArrayList();
		while (it.hasNext()) {
			Sample_TestAnalyte sampleTestAnalyte = (Sample_TestAnalyte)it.next();
            if (!sampleTestAnalyte.isPhantom()) {
				sampleTestAnalytes.add(sampleTestAnalyte);
			}
		}
		return sampleTestAnalytes;
	}

	//bugzilla 1856 
	protected List sortTests(List sampleTestAnalytes) {

		//find root level nodes and fill in children for each Test_TestAnalyte
		List rootLevelNodes = new ArrayList();
		for (int i = 0; i < sampleTestAnalytes.size(); i++) {

			Sample_TestAnalyte sampleTestAnalyte = (Sample_TestAnalyte)sampleTestAnalytes.get(i);
			String analysisId = sampleTestAnalyte.getAnalysis().getId();

			List children = new ArrayList();
			for (int j = 0; j < sampleTestAnalytes.size(); j++) {
				Sample_TestAnalyte sta = (Sample_TestAnalyte)sampleTestAnalytes.get(j);
				if (sta.getAnalysis().getParentAnalysis() != null && sta.getAnalysis().getParentAnalysis().getId().equals(analysisId)) {
						children.add(sta);
				}
			}
			sampleTestAnalyte.setChildren(children);
			if (sampleTestAnalyte.getAnalysis().getParentAnalysis() == null) {
					rootLevelNodes.add(sampleTestAnalyte);
			} 
		}

		//sort rootLevelNodes
		Collections.sort(rootLevelNodes, ResultsVerificationTestComparator.SORT_ORDER_COMPARATOR);

		sampleTestAnalytes = new ArrayList();
		for (int i = 0; i < rootLevelNodes.size(); i++) {
			Sample_TestAnalyte sta = (Sample_TestAnalyte)rootLevelNodes.get(i);
			sampleTestAnalytes.add(sta);
			recursiveSort(sta,sampleTestAnalytes);
		}

		return sampleTestAnalytes;
	}

	//recursively add in phantom tests to build the actual original hierarchy
	//this is so that tests that are part of parent/child relationships can be taken into account for sorting
	private void recursiveHierarchyBuild(Sample_TestAnalyte element, List sampleTestAnalytes, List newSampleTestAnalytes){
		AnalysisDAO analysisDAO = new AnalysisDAOImpl();
		if (element != null && element.getAnalysis().getParentAnalysis() != null) {
			//find out if parent is already in the original list
			boolean alreadyInList = false;
			for (int i = 0; i < sampleTestAnalytes.size(); i++) {
				Sample_TestAnalyte sta = (Sample_TestAnalyte)sampleTestAnalytes.get(i);
				if (element.getAnalysis().getParentAnalysis().getId().equals(sta.getAnalysis().getId())) {
					alreadyInList = true;
				}
			}
			if (!alreadyInList) {
				//add phantom test to list
				Analysis analysis = new Analysis();
				analysis.setId(element.getAnalysis().getParentAnalysis().getId());
				analysisDAO.getData(analysis);
				Sample_TestAnalyte sampleTestAnalyte = new Sample_TestAnalyte();
				sampleTestAnalyte.setAnalysis(analysis);
				sampleTestAnalyte.setPhantom(true);
				newSampleTestAnalytes.add(sampleTestAnalyte);
				//now check for another parent
				recursiveHierarchyBuild(sampleTestAnalyte, sampleTestAnalytes, newSampleTestAnalytes);
			}
		}

	}

	//bugzilla 1856 use recursion and sort children
	private void recursiveSort(Sample_TestAnalyte element, List sampleTestAnalytes) {
		List children = element.getChildren();

		//sort children
		if (children != null && children.size() > 0) {
			Collections.sort(children, ResultsVerificationTestComparator.SORT_ORDER_COMPARATOR);
		}
		for (Iterator <Sample_TestAnalyte>it = children.iterator(); it.hasNext();) {
			Sample_TestAnalyte childElement = ((Sample_TestAnalyte)it.next());
			sampleTestAnalytes.add(childElement);
			recursiveSort(childElement, sampleTestAnalytes);
		}
	}


}
