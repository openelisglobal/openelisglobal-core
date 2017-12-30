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
package us.mn.state.health.lims.common.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.ActionRedirect;
import org.apache.struts.action.DynaActionForm;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.common.valueholder.tree.Tree;
import us.mn.state.health.lims.common.valueholder.tree.TreeNode;
import us.mn.state.health.lims.common.valueholder.tree.TreeStateManager;
import us.mn.state.health.lims.login.dao.UserModuleDAO;
import us.mn.state.health.lims.login.daoimpl.UserModuleDAOImpl;

//added for bugzilla 1742 
public abstract class BaseTreeAction extends BaseAction implements IActionConstants {
	String pageSubtitle = null;

	String pageTitle = null;

	//1742
	protected Document data = null;
	protected static final String TREE_NAME = "reports";
	protected static final String DELIMETER = "."; 
	protected int PAGE_SIZE = -1;
	protected int PAGINATED_NODE_CHILD_COUNT = -1;

	public BaseTreeAction() {

	}

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		//return to login page if user session is not found
		//bugzilla 2160
		UserModuleDAO userModuleDAO = new UserModuleDAOImpl();		
		if ( userModuleDAO.isSessionExpired(request) ) {
			ActionMessages errors = new ActionMessages();
			ActionError error = new ActionError("login.error.session.message", null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			return mapping.findForward(LOGIN_PAGE);		
		}	
		
		String pageSubtitle = null;
		String pageTitle = null;

		ActionForward forward = performAction(mapping, form, request, response);
		String pageTitleKey = getPageTitleKey(request, form);
		String pageSubtitleKey = getPageSubtitleKey(request, form);

		String pageTitleKeyParameter = getPageTitleKeyParameter(request, form);
		String pageSubtitleKeyParameter = getPageSubtitleKeyParameter(request,
				form);
	
		//bugzilla 1512 internationalization
		request.getSession().setAttribute(Globals.LOCALE_KEY, SystemConfiguration.getInstance().getDefaultLocale());
		
		// bugzilla 1348
		if (StringUtil.isNullorNill(pageTitleKeyParameter)) {
			pageTitle = getMessageForKey(pageTitleKey);
		} else {
			pageTitle = getMessageForKey(request, pageTitleKey,
					pageTitleKeyParameter);
		}

		// bugzilla 1348
		if (StringUtil.isNullorNill(pageSubtitleKeyParameter)) {
			pageSubtitle = getMessageForKey(pageSubtitleKey);
		} else {
			pageSubtitle = getMessageForKey(request, pageSubtitleKey,
					pageSubtitleKeyParameter);
		}

		if (null != pageTitle)
			request.setAttribute(PAGE_TITLE_KEY, pageTitle);
		if (null != pageSubtitle)
			request.setAttribute(PAGE_SUBTITLE_KEY, pageSubtitle);

		// Set the form attributes
		setFormAttributes(form, request);

		//check for account disabled
		//bugzilla 2160
		if ( userModuleDAO.isAccountDisabled(request) ) {
			ActionMessages errors = new ActionMessages();
			ActionError error = new ActionError("login.error.account.disable", null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			return mapping.findForward(LOGIN_PAGE);						
		}
		
		//check for account locked
		//bugzilla 2160
		if ( userModuleDAO.isAccountLocked(request) ) {
			ActionMessages errors = new ActionMessages();
			ActionError error = new ActionError("login.error.account.lock", null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			return mapping.findForward(LOGIN_PAGE);			
		}
		
		//check for password expired
		//bugzilla 2160
		if ( userModuleDAO.isPasswordExpired(request) ) {
			ActionMessages errors = new ActionMessages();
			ActionError error = new ActionError("login.error.password.expired", null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			return mapping.findForward(LOGIN_PAGE);			
		}
		
		//check for user type (admin or non-admin)
		//bugzilla 2160
		if ( userModuleDAO.isUserAdmin(request) )
			userModuleDAO.enabledAdminButtons(request);
		else {
			if ( !userModuleDAO.isVerifyUserModule(request)) {
				ActionMessages errors = new ActionMessages();
				ActionError error = new ActionError("login.error.module.not.allow", null, null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
				saveErrors(request, errors);
				//bugzilla 2154
				LogEvent.logInfo("BaseTreeAction","execute()","======> NOT ALLOWED ACCESS TO THIS MODULE");
				if ( userModuleDAO.isSessionExpired(request) )
					return mapping.findForward(LOGIN_PAGE);	
				else
					return mapping.findForward(HOME_PAGE);
			}
		}		
		userModuleDAO.setupUserSessionTimeOut(request);
		//System.out.println("Returning this forward from BaseAction " + forward);
		return forward;
	}

	/**
	 * Abstract method that sub classes must implement to perform the desired
	 * action
	 */
	protected abstract ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	/**
	 * Must be implemented by subclasses to set the title for the requested
	 * page. The value returned should be a key String from the
	 * ApplicationResources.properties file.
	 * 
	 * @return the title key for this page.
	 */
	protected abstract String getPageTitleKey();

	/**
	 * Must be implemented by subclasses to set the subtitle for the requested
	 * page. The value returned should be a key String from the
	 * ApplicationResources.properties file.
	 * 
	 * @return the subtitle key this page.
	 */
	protected abstract String getPageSubtitleKey();

	/**
	 * This getPageTitleKey method accepts a request and form parameter so that
	 * a subclass can override the method and conditionally return different
	 * titles.
	 * 
	 * @param request
	 *            the request
	 * @param form
	 *            the form associated with this request.
	 * @return the title key for this page.
	 */
	protected String getPageTitleKey(HttpServletRequest request, ActionForm form) {
		return getPageTitleKey();
	}

	protected String getPageTitleKeyParameter(HttpServletRequest request,
			ActionForm form) {
		return null;
	}

	/**
	 * This getSubtitleKey method accepts a request and form parameter so that a
	 * subclass can override the method and conditionally return different
	 * subtitles.
	 * 
	 * @param request
	 *            the request
	 * @param form
	 *            the form associated with this request.
	 * @return the subtitle key this page.
	 */
	protected String getPageSubtitleKey(HttpServletRequest request,
			ActionForm form) {
		return getPageSubtitleKey();
	}

	protected String getPageSubtitleKeyParameter(HttpServletRequest request,
			ActionForm form) {
		return null;
	}

	protected void setFormAttributes(ActionForm form, HttpServletRequest request)
			throws Exception {
		try {
			if (null != form) {
				DynaActionForm theForm = (DynaActionForm) form;
				theForm.getDynaClass().getName();
				String name = theForm.getDynaClass().getName().toString();
				//use IActionConstants!
				request.setAttribute(FORM_NAME, name);
				request.setAttribute("formType", theForm.getClass().toString());
				String actionName = name.substring(1, name.length() - 4);
				actionName = name.substring(0, 1).toUpperCase() + actionName;
				request.setAttribute(ACTION_KEY, actionName);
                //bugzilla 2154
                LogEvent.logInfo("BaseTreeAction","setFormAttributes()","BaseAction formName = " + actionName);				
			}
		} catch (ClassCastException e) {
			//bugzilla 2154
            LogEvent.logError("BaseTreeAction","setFormAttributes()",e.toString());               				
			throw new ClassCastException("Error Casting form into DynaForm");
		}
	}

	protected ActionForward getForward(ActionForward forward, String id,
			String startingRecNo) {
		ActionRedirect redirect = new ActionRedirect(forward);

		if (id != null)
			redirect.addParameter(ID, id);
		if (startingRecNo != null)
			redirect.addParameter("startingRecNo", startingRecNo);
		//System.out.println("This is redirect " + redirect.getPath());
		return redirect;
	}
	
	//added for bugzilla 1467
	protected ActionForward getForward(ActionForward forward, String id,
			String startingRecNo, String direction) {
		ActionRedirect redirect = new ActionRedirect(forward);
		//bugzilla 2154
		LogEvent.logInfo("BaseTreeAction","getForward()","This is forward " + forward.getRedirect() + " " + forward.getPath());

		if (id != null)
			redirect.addParameter(ID, id);
		if (startingRecNo != null)
			redirect.addParameter("startingRecNo", startingRecNo);
		if (direction != null)
			redirect.addParameter("direction", direction);
		//bugzilla 2154
		LogEvent.logInfo("BaseTreeAction","getForward()","This is redirect " + redirect.getPath());

		return redirect;
	}




	protected abstract void generateTreeNodes(TreeNode parent, HttpServletRequest request) throws Exception;
 
	/**
	 * Code which creates a local tree. This can be put in a Helper class, or an Servlet which the jsp can extend
	 */
	public String generateTree(HttpServletRequest request, String rootName) throws Exception // ignore exception handling for now.... //
	{  
	  TreeNode.setPathDelimeter(DELIMETER.charAt(0));  

	  // initialize the data
	  data = getData(request);

	  // let us process the state of the tree first 
	  TreeStateManager treeStateManager = TreeStateManager.getInstance(request.getSession(), TREE_NAME);
	  
	  // we are going to force certain nodes to be paginated....
	  
	  
	  //TODO look into pagination
	  //treeStateManager.addNodePathAsPaginated("root"+DELIMETER+"section1"+DELIMETER+"report1");
	  
	  // now let us process the state of the tree.
	  treeStateManager.processState(request);
	  
	  // root node should always be expanded.
	  treeStateManager.addNodeAsExpanded("root");
	  TreeNode root = new TreeNode("root");
	  root.setIconSrcOpen("tree_images/base.gif");
	  root.setIconSrcClosed("tree_images/base.gif");
	  
	  //1742 give root the name of the menu
	  java.util.Locale locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
	  String displayName =
			us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
			locale,
	        rootName);
	  root.setDisplayName(displayName);
	  //end 1742
	  
	  generateTreeNodes(root, request);

	  Tree csTree = new Tree(TREE_NAME, root, false); 
	  
	  return csTree.renderTree(request);
	} 




	/** Return the data source. In our case this is a simple xml file, but the data could be coming from the database if required */
	protected Document getData(HttpServletRequest request) throws Exception
	{   
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		String uri = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/pages/menu/reportsTab/reportsPageLeftData.xml";
		// System.out.println("URI : " + uri);
		return db.parse(uri);
	}

	/** Sample implementation of the Server side method which will gets the children of a particular parent. */
	/** 'start' is used to return a particular childset. If a node is not paginated, all children will be returned */
	protected List getChildren(Node parentNode, TreeNode treeNode) throws Exception
	{
	    List list = new ArrayList();
	    NodeList children = parentNode.getChildNodes();
	    String nodePathForNode = getNodePathFromNode(parentNode);
	    
	    int start = treeNode.getPageRangeBegin() == -1 ? 0 : treeNode.getPageRangeBegin();
	    int end   = start + PAGE_SIZE -1;

	    if(nodePathForNode.equals(treeNode.getNodePath()))
	    {
	         // these are the children we want. Let us find only the range we need.
	         int nodeCount = 0;
	         for(int i=0; i<children.getLength(); i++)
	         {
	             Node child = children.item(i);
	             if(child.getNodeType() == Node.ELEMENT_NODE)
	             {
	                if(!treeNode.isPaginated())
	                {
	                    // add all the nodes
	                    list.add(child); 
	                } else
	                {
	                    // only add the node if it is within the range
	                    if(nodeCount>=start && nodeCount <= end) 
	                        list.add(child);
	                }
	                nodeCount++;
	             }
	         }
	         return list;
	    }
	    else if(treeNode.getNodePath().startsWith(nodePathForNode))
	    {
	        // looks promising, let us dig deeper.
	         for(int i=0; i<children.getLength(); i++)
	         {
	             Node child = children.item(i);
	             if(child.getNodeType() == Node.ELEMENT_NODE)
	             {
	                 list = getChildren(child, treeNode);
	                 if(list != null)
	                    return list;
	             }
	         }
	         
	         return null;
	    }
	    else
	    {
	        // this is the wrong guy, return null
	        return null; 
	    }
	}

//	 return a node path from the node, delimeted with the DELIMETER
	protected String getNodePathFromNode(Node node)
	{
	   String returnValue = node.getNodeName();
	   //System.out.println("getNodePathFromNode this is node name " + returnValue);
	   Node currNode = node;
	   //System.out.println("Does this node have a parent node? " + currNode.getParentNode());
	   while(currNode.getParentNode() != null && currNode.getParentNode().getNodeType() == Node.ELEMENT_NODE)
	   {
	      currNode = currNode.getParentNode();
	      returnValue = currNode.getNodeName() + DELIMETER + returnValue;
	      //System.out.println("getNodePathFromNode this is node name after appending " + returnValue);
	   }
	   
	   return returnValue;
	}

	/** Server method which basically determines whether a particular tree node has children or not */
	protected boolean hasChildren(org.w3c.dom.Node node)
	{
	   NodeList nlist = node.getChildNodes();
	   for(int i=0; i<nlist.getLength(); i++) {
	      if(nlist.item(i).getNodeType() == Node.ELEMENT_NODE) {
		      return true;
		  }
	   }

	   return false;
	}

}