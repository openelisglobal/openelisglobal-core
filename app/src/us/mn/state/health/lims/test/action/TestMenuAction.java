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
package us.mn.state.health.lims.test.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.*;

import us.mn.state.health.lims.common.action.BaseMenuAction;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.login.dao.UserTestSectionDAO;
import us.mn.state.health.lims.login.daoimpl.UserTestSectionDAOImpl;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class TestMenuAction extends BaseMenuAction {

	protected List createMenuList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
	    
		List tests = new ArrayList();

		String stringStartingRecNo = (String) request
				.getAttribute("startingRecNo");
		int startingRecNo = Integer.parseInt(stringStartingRecNo);
		
		// bugzilla 2371
		 String searchString=(String) request
       .getParameter("searchString");
		 
		String doingSearch=(String)request
       .getParameter("search");
	
        
		TestDAO testDAO = new TestDAOImpl();
						
	    tests = testDAO.getPageOfTests(startingRecNo);
		// end of bugzilla 2371
		
		//Get tests by user system id
		//bugzilla 2160
		UserTestSectionDAO userTestSectionDAO = new UserTestSectionDAOImpl();
		
//		 bugzilla 2371
//why get the tests and then get them again and this method maybe does and maybe doesn't get them by user id
		tests = userTestSectionDAO.getPageOfTestsBySysUserId(request,startingRecNo, doingSearch, searchString);

		request.setAttribute("menuDefinition", "TestMenuDefinition");

		// bugzilla 1411 set pagination variables 
		// bugzilla 2371 set pagination variables for searched results.
		if (!StringUtil.isNullorNill(doingSearch) && doingSearch.equals(YES))
			request.setAttribute(MENU_TOTAL_RECORDS, String.valueOf(testDAO
					.getAllSearchedTotalTestCount (request, searchString)));
		else
		    request.setAttribute(MENU_TOTAL_RECORDS, String.valueOf(testDAO
				.getTotalTestCount()));
		request.setAttribute(MENU_FROM_RECORD, String.valueOf(startingRecNo));
		int numOfRecs = 0;
		if (tests != null) {
			if (tests.size() > SystemConfiguration.getInstance()
					.getDefaultPageSize()) {
				numOfRecs = SystemConfiguration.getInstance()
						.getDefaultPageSize();
			} else {
				numOfRecs = tests.size();
			}
			numOfRecs--;
		}
		int endingRecNo = startingRecNo + numOfRecs;
		request.setAttribute(MENU_TO_RECORD, String.valueOf(endingRecNo));
		//end bugzilla 1411
		
		//bugzilla 2371
		request.setAttribute(MENU_SEARCH_BY_TABLE_COLUMN, "test.description");
		
			
		if (!StringUtil.isNullorNill(doingSearch) && doingSearch.equals(YES) ) {
		 
		   request.setAttribute(IN_MENU_SELECT_LIST_HEADER_SEARCH, "true");
		   
		   request.setAttribute(MENU_SELECT_LIST_HEADER_SEARCH_STRING, searchString );
		}
		
		return tests;
	}

	protected String getPageTitleKey() {
		return "test.browse.title";
	}

	protected String getPageSubtitleKey() {
		return "test.browse.title";
	}

	protected int getPageSize() {
		return SystemConfiguration.getInstance().getDefaultPageSize();
	}

	@Override
	protected String getDeactivateDisabled() {
		return "true";
	}
	
    @Override
	protected String getAddDisabled() {
        return "true";
    }
    
    @Override
    protected String getEditDisabled() {
        return "false";
    }   
    
    /**
     * Because there is code in UswerModuleDAO.enabledAdminButtons to deal with users and what they are allowed to do.
     * When we really want turn off a button for a site regardless who the user is, we have to go last and do it here.
     * Yes, this is a hack.        
     * 
     * @see us.mn.state.health.lims.common.action.BaseAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                    HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        ActionForward forward = super.execute(mapping, form, request, response);
        request.setAttribute(ADD_DISABLED, getAddDisabled());        
        return forward;
    }
}
