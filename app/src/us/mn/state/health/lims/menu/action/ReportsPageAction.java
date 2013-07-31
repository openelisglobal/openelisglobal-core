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
package us.mn.state.health.lims.menu.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import us.mn.state.health.lims.common.action.BaseTreeAction;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.valueholder.tree.TreeNode;
import us.mn.state.health.lims.common.valueholder.tree.TreeStateManager;
import us.mn.state.health.lims.common.log.LogEvent;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class ReportsPageAction extends BaseTreeAction {

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;

		// DynaActionForm dynaForm = (DynaActionForm) form;

		// initialize the form
		// dynaForm.initialize(mapping);

		//System.out.println("I am in ReportsPageAction " + form);
		
		request.setAttribute(THE_TREE, generateTree(request, "homePage.menu.reports.toolTip"));
		//some how this action is null, temporary fixed
		request.setAttribute(ACTION_KEY, "ReportsPage");
		
		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		return StringUtil.getContextualKeyForKey("homePage.title");
	}

	protected String getPageSubtitleKey() {
		return StringUtil.getContextualKeyForKey("homePage.subTitle");
	}
	
	/** Recursive function to generate child nodes */
	protected void generateTreeNodes(TreeNode parent, HttpServletRequest request) throws Exception
	{
	    TreeStateManager treeStateManager = TreeStateManager.getInstance(request.getSession(), TREE_NAME);
		if(treeStateManager.isNodeExpanded(parent.getNodePath()))
		{
			// get the children of this node from the server...
			List l = getChildren(data.getFirstChild(), parent);

		   //1742 to fix bug with parser on osprey app server java version 1.4.1_03
			if (data.getFirstChild().getNodeName().equals("xml")) {
			    Node rootNode = (Node)data.getDocumentElement();
			    l = getChildren(rootNode, parent);
			}
			//end 1742
			
			
			for(int i=0; i<l.size(); i++)
			{
			    org.w3c.dom.Node n = (org.w3c.dom.Node)l.get(i);
			    //1742 The name reportsNodeSubmitForm is specific to each menu we use this tree on
			    TreeNode tn = parent.addChild(n.getNodeName(), "reportsManagementForm", "tree_images/folderopen.gif","tree_images/folder.gif");
				
				// can set PAGE_SIZE and PAGINATED_NODE_CHILD_COUNT differently for pagination support.
	            if(treeStateManager.isNodePathPaginated(tn.getNodePath()))
				{
					tn.setPaginated(true);
					//System.out.println("This is tree node name " + tn.getName());
					
					PAGE_SIZE = SystemConfiguration.getInstance().getDefaultTreePageSize();
					PAGINATED_NODE_CHILD_COUNT = SystemConfiguration.getInstance().getDefaultPaginatedNodeChildCount();

					tn.setPageSize(PAGE_SIZE);
					tn.setPaginatedNodeChildCount(PAGINATED_NODE_CHILD_COUNT);
					
					TreeStateManager.PaginatedNodeData pageNodeData = treeStateManager.getPaginatedNodeData(tn.getNodePath());
	                // System.out.println(pageNodeData);
					
	                tn.setPageRangeBegin(pageNodeData.getPageRangeBegin());
				}
				
				if(hasChildren(n))
				  tn.setHasChildren(true);
				  
				//1742 set displayname and url
				if (n.hasAttributes()) {
				    NamedNodeMap nnm = n.getAttributes();
				    
				    if (nnm.getNamedItem("displayName") != null) {
				      java.util.Locale locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
	                  String displayName =
						us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
						locale,
	                    nnm.getNamedItem("displayName").getNodeValue());
				      tn.setDisplayName(displayName);
				    }
				    
				    if (nnm.getNamedItem("url") != null) {
				       String server = request.getServerName();
	                   int port = request.getServerPort();
	                   String scheme = request.getScheme();
	                   String context = request.getContextPath() + "/";
	                   String hostStr = scheme + "://" + server;
	                   if ( port != 80 && port != 443) {
	                   	hostStr = hostStr + ":" + port;
	                    }
	                    hostStr = hostStr + context;

	                    String urlFromXml = nnm.getNamedItem("url").getNodeValue();
	                    //now replace Test_Id/Project_Id parameter with a value if needed
	                    //bugzilla 1842 - added new TEST_SECTION_ID parameter for Virology Sample List by Test
	                    if (urlFromXml.indexOf(REPORT_TEST_ID_PARAMETER) > 0 || urlFromXml.indexOf(REPORT_PROJECT_ID_PARAMETER) > 0 || urlFromXml.indexOf(REPORT_TEST_SECTION_ID_PARAMETER) > 0) {
	                        Map map = extractURLQueryParameters(urlFromXml);
	                        urlFromXml = constructURLQueryParametersFromMap(urlFromXml, map);
	                    }
				        String completeUrl = hostStr + urlFromXml;
				        //System.out.println("completeUrl " + completeUrl);
				        tn.setUrl(completeUrl, null);
				    }
	            }
	            //end 1742
				  
				if(treeStateManager.isNodeExpanded(tn.getNodePath())) 
	            {
				   tn.setExpanded(true);
				   generateTreeNodes(tn, request);
				}
			}
		}	
	}
	
//	1742
	protected String constructURLQueryParametersFromMap(String url, Map map) throws LIMSRuntimeException {
		    String queryString = "";
	
	    	try {
			queryString = url.substring(0, url.indexOf("?"));
			queryString += "?";
			
			Set keySet = map.keySet();
			Iterator keyIt = keySet.iterator();
	        while (keyIt.hasNext()) {
			    String key = (String)keyIt.next();
	            String val = (String)map.get(key);
	            //for test and project id get actual id from SystemConfiguration
				if (key.equals(REPORT_TEST_ID_PARAMETER) || 
					key.equals(REPORT_PROJECT_ID_PARAMETER) ||
		            //bugzilla 1842 (adding Test_Section_Id for Virology Sample List by Test)
					key.equals(REPORT_TEST_SECTION_ID_PARAMETER)) {
					//this is for reporting static ids are encoded
					val = SystemConfiguration.getInstance().getStaticIdByName(val);
				}
				
				queryString += key + "=" + val;
				if (keyIt.hasNext()) {
					queryString += "&";
				}
	        } 
	    	}
	    	catch (Exception e) {
    	    	//bugzilla 2154
			    LogEvent.logError("ReportsPageAction","constructURLQueryParametersFromMap()",e.toString()); 
	        	throw new LIMSRuntimeException("Error in constructURLQueryParametersFromMap " + url, e);
	        }
					
			return queryString;
	}
	
	//1742
	public static Map extractURLQueryParameters(String string) throws LIMSRuntimeException {
		Map params = new HashMap();
		try {
		int indexOfBeginQueryParameters = string.indexOf("?");
		String queryString = string.substring(indexOfBeginQueryParameters + 1);
		List chunks = new ArrayList();
		
		String ampersand = "&";
		if (queryString.indexOf("&amp;") >= 0) {
			ampersand = "&amp;";
		}
		
		while (queryString.indexOf(ampersand) >= 0) {
		  String aChunk = queryString.substring(0, queryString.indexOf(ampersand));
		  chunks.add(aChunk);
		  queryString = queryString.substring(queryString.indexOf(ampersand) + 1);
		}
		//add the rest in
		if (!StringUtil.isNullorNill(queryString)) {
			chunks.add(queryString);
		}

        for (int i = 0; i < chunks.size(); i++) {
        	String aChunk = (String)chunks.get(i);
        	String [] str = aChunk.split("=");
        	params.put(str[0], str[1]);
        }
		} catch (Exception e) {
			throw new LIMSRuntimeException("Error in extractURLQueryParameters " + string , e);
		}

		return params;
	}

}
