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
package us.mn.state.health.lims.common.provider.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.common.util.XMLUtil;
import us.mn.state.health.lims.panel.dao.PanelDAO;
import us.mn.state.health.lims.panel.daoimpl.PanelDAOImpl;
import us.mn.state.health.lims.panel.valueholder.Panel;
import us.mn.state.health.lims.panelitem.dao.PanelItemDAO;
import us.mn.state.health.lims.panelitem.daoimpl.PanelItemDAOImpl;
import us.mn.state.health.lims.panelitem.valueholder.PanelItem;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.daoimpl.TestSectionDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.typeofsample.dao.TypeOfSamplePanelDAO;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSamplePanelDAOImpl;
import us.mn.state.health.lims.typeofsample.util.TypeOfSampleUtil;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSamplePanel;

public class SampleEntryTestsForTypeProvider extends BaseQueryProvider{
	private TestDAO testDAO = new TestDAOImpl();
	private PanelDAO panelDAO = new PanelDAOImpl();
	private static final String USER_TEST_SECTION_ID;

	static{
		USER_TEST_SECTION_ID = new TestSectionDAOImpl().getTestSectionByName("user").getId();
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

		String sampleType = request.getParameter("sampleType");
		String labOrderType = request.getParameter("labOrderType");

		StringBuilder xml = new StringBuilder();

		String result = createSearchResultXML(sampleType, labOrderType, xml);

		ajaxServlet.sendData(xml.toString(), result, request, response);

	}

	private String createSearchResultXML(String sampleType, String labOrderType, StringBuilder xml){

		String success = VALID;

		List<Test> tests = TypeOfSampleUtil.getTestListBySampleTypeId(sampleType, labOrderType, true);

		Collections.sort(tests, new Comparator<Test>(){
			@Override
			public int compare(Test t1, Test t2){
				if(GenericValidator.isBlankOrNull(t1.getSortOrder()) || GenericValidator.isBlankOrNull(t2.getSortOrder())){
					return t1.getTestName().compareTo(t2.getTestName());
				}

				try{
					int t1Sort = Integer.parseInt(t1.getSortOrder());
					int t2Sort = Integer.parseInt(t2.getSortOrder());

					if(t1Sort > t2Sort){
						return 1;
					}else if(t1Sort < t2Sort){
						return -1;
					}else{
						return 0;
					}

				}catch(NumberFormatException e){
					return t1.getTestName().compareTo(t2.getTestName());
				}

			}
		});

		addTests(tests, xml);

		List<TypeOfSamplePanel> panelList = getPanelList(sampleType);
		List<PanelTestMap> panelMap = linkTestsToPanels(panelList, tests);

		addPanels(panelMap, xml);

		return success;
	}

	private void addTests(List<Test> tests, StringBuilder xml){
		xml.append("<tests>");
		for(Test test : tests){
			addTest(test, xml);
		}

		xml.append("</tests>");
	}

	private void addTest(Test test, StringBuilder xml){
		xml.append("<test>");
		XMLUtil.appendKeyValue("name", StringEscapeUtils.escapeXml(test.getTestName()), xml);
		XMLUtil.appendKeyValue("id", test.getId(), xml);
		XMLUtil.appendKeyValue("userBenchChoice", String.valueOf(USER_TEST_SECTION_ID.equals(test.getTestSection().getId())), xml);
		xml.append("</test>");
	}

	private void addPanels(List<PanelTestMap> panelMap, StringBuilder xml){
		panelMap = sortPanels(panelMap);

		xml.append("<panels>");
		for(PanelTestMap testMap : panelMap){
			addPanel(testMap, xml);
		}
		xml.append("</panels>");
	}

	private List<PanelTestMap> sortPanels(List<PanelTestMap> panelMap){

		Collections.sort(panelMap, new Comparator<PanelTestMap>(){
			@Override
			public int compare(PanelTestMap o1, PanelTestMap o2){
				return o1.getPanelOrder() - o2.getPanelOrder();
			}
		});

		return panelMap;
	}

	private void addPanel(PanelTestMap testMap, StringBuilder xml){
		xml.append("<panel>");
		XMLUtil.appendKeyValue("name", testMap.getName(), xml);
		XMLUtil.appendKeyValue("id", testMap.getPanelId(), xml);
		XMLUtil.appendKeyValue("testMap", testMap.getTestMaps(), xml);
		xml.append("</panel>");
	}

	private List<TypeOfSamplePanel> getPanelList(String sampleType){
		TypeOfSamplePanelDAO samplePanelDAO = new TypeOfSamplePanelDAOImpl();
		return samplePanelDAO.getTypeOfSamplePanelsForSampleType(sampleType);
	}

	private List<PanelTestMap> linkTestsToPanels(List<TypeOfSamplePanel> panelList, List<Test> tests){
		List<PanelTestMap> selected = new ArrayList<PanelTestMap>();

		Map<String, Integer> testNameOrderMap = new HashMap<String, Integer>();

		for(int i = 0; i < tests.size(); i++){
			testNameOrderMap.put(tests.get(i).getTestName(), new Integer(i));
		}

		PanelItemDAO panelItemDAO = new PanelItemDAOImpl();

		for(TypeOfSamplePanel samplePanel : panelList){
			Panel panel = panelDAO.getPanelById(samplePanel.getPanelId());
			if("Y".equals(panel.getIsActive())){
				String matchTests = getTestIndexesForPanels(samplePanel.getPanelId(), testNameOrderMap, panelItemDAO);
				if(!GenericValidator.isBlankOrNull(matchTests)){
					int panelOrder = panelDAO.getPanelById(samplePanel.getPanelId()).getSortOrderInt();
					selected.add(new PanelTestMap(samplePanel.getPanelId(), panelOrder, panel.getPanelName(), matchTests));
				}
			}
		}

		return selected;
	}

	@SuppressWarnings("unchecked")
	private String getTestIndexesForPanels(String panelId, Map<String, Integer> testIdOrderMap, PanelItemDAO panelItemDAO){
		StringBuilder indexes = new StringBuilder();
		List<PanelItem> items = panelItemDAO.getPanelItemsForPanel(panelId);

		for(PanelItem item : items){
			String derivedNameFromPanel = getDerivedNameFromPanel(item);
			if(derivedNameFromPanel != null){
				Integer index = testIdOrderMap.get(derivedNameFromPanel);

				if(index != null){
					indexes.append(index.toString());
					indexes.append(",");
				}
			}
		}

		String withExtraComma = indexes.toString();
		return withExtraComma.length() > 0 ? withExtraComma.substring(0, withExtraComma.length() - 1) : "";
	}

	private String getDerivedNameFromPanel(PanelItem item){
		//This cover the transition in the DBbetween the panel_item being linked by name
		// to being linked by id
		if(item.getTestId() != null){
			Test test = testDAO.getTestById(item.getTestId());
			if(test != null){
				return test.getTestName();
			}
		}else{
			return item.getTestName();
		}

		return null;
	}

	public class PanelTestMap{
		private String name;
		private String testMaps;
		private String panelId;
		private int panelOrder;

		public PanelTestMap(String panelId, int panelOrder, String panelName, String map){
			name = panelName;
			testMaps = map;
			this.panelId = panelId;
			this.panelOrder = panelOrder;
		}

		public String getName(){
			return name;
		}

		public String getTestMaps(){
			return testMaps;
		}

		public String getPanelId(){
			return panelId;
		}

		public int getPanelOrder(){
			return panelOrder;
		}
	}

}
