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
package us.mn.state.health.lims.qaevent.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.qaevent.dao.QaEventDAO;
import us.mn.state.health.lims.qaevent.daoimpl.QaEventDAOImpl;
import us.mn.state.health.lims.qaevent.valueholder.QaEvent;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class QaEventNextPreviousAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "false");
		request.setAttribute(NEXT_DISABLED, "false");

		String id = request.getParameter(ID);

		if (StringUtil.isNullorNill(id) || "0".equals(id)) {
			isNew = true;
		} else {
			isNew = false;
		}

		BaseActionForm dynaForm = (BaseActionForm) form;

		String start = (String) request.getParameter("startingRecNo");
		String direction = (String) request.getParameter("direction");

		QaEvent qaEvent = new QaEvent();

		qaEvent.setId(id);
		try {

			QaEventDAO qaEventDAO = new QaEventDAOImpl();

			qaEventDAO.getData(qaEvent);

			if (FWD_NEXT.equals(direction)) {

				List qaEvents = qaEventDAO.getNextQaEventRecord(qaEvent
						.getQaEventName());
				if (qaEvents != null && qaEvents.size() > 0) {
					qaEvent = (QaEvent) qaEvents.get(0);
					qaEventDAO.getData(qaEvent);
					if (qaEvents.size() < 2) {
						// disable next button
						request.setAttribute(NEXT_DISABLED, "true");
					}
					id = qaEvent.getId();
				} else {
					// just disable next button
					request.setAttribute(NEXT_DISABLED, "true");
				}
			}

			if (FWD_PREVIOUS.equals(direction)) {
				List qaEvents = qaEventDAO.getPreviousQaEventRecord(qaEvent
						.getQaEventName());
				if (qaEvents != null && qaEvents.size() > 0) {
					qaEvent = (QaEvent) qaEvents.get(0);
					qaEventDAO.getData(qaEvent);
					if (qaEvents.size() < 2) {
						// disable previous button
						request.setAttribute(PREVIOUS_DISABLED, "true");
					}
					id = qaEvent.getId();
				} else {
					// just disable next button
					request.setAttribute(PREVIOUS_DISABLED, "true");
				}
			}

		} catch (LIMSRuntimeException lre) {
    		//bugzilla 2154
			LogEvent.logError("QaEventNextPreviousAction","performAction()",lre.toString());
			request.setAttribute(ALLOW_EDITS_KEY, "false");
			// disable previous and next
			request.setAttribute(PREVIOUS_DISABLED, "true");
			request.setAttribute(NEXT_DISABLED, "true");
			forward = FWD_FAIL;
		}
		if (forward.equals(FWD_FAIL))
			return mapping.findForward(forward);

		if (qaEvent.getId() != null && !qaEvent.getId().equals("0")) {
			request.setAttribute(ID, qaEvent.getId());

		}

		return getForward(mapping.findForward(forward), id, start);

	}

	protected String getPageTitleKey() {
		return null;
	}

	protected String getPageSubtitleKey() {
		return null;
	}

}