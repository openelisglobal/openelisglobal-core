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
package us.mn.state.health.lims.project.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSDuplicateRecordException;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.resources.ResourceLocator;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.valueholder.UserSessionData;
import us.mn.state.health.lims.program.dao.ProgramDAO;
import us.mn.state.health.lims.program.daoimpl.ProgramDAOImpl;
import us.mn.state.health.lims.project.dao.ProjectDAO;
import us.mn.state.health.lims.project.daoimpl.ProjectDAOImpl;
import us.mn.state.health.lims.project.valueholder.Project;
import us.mn.state.health.lims.scriptlet.dao.ScriptletDAO;
import us.mn.state.health.lims.scriptlet.daoimpl.ScriptletDAOImpl;
import us.mn.state.health.lims.scriptlet.valueholder.Scriptlet;
import us.mn.state.health.lims.systemuser.dao.SystemUserDAO;
import us.mn.state.health.lims.systemuser.daoimpl.SystemUserDAOImpl;
import us.mn.state.health.lims.systemuser.valueholder.SystemUser;

/**
 * @author diane benz
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public class ProjectUpdateAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new Project.
		// If there is a parameter present, we should bring up an existing
		// Project to edit.
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

		// server-side validation (validation.xml)
		ActionMessages errors = dynaForm.validate(mapping, request);
		try {
			errors = validateAll(request, errors, dynaForm);
		} catch (Exception e) {
            //bugzilla 2154
			LogEvent.logError("ProjectUpdateAction","performAction()",e.toString());      		
			ActionError error = new ActionError("errors.ValidationException",
					null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}

		if (errors != null && errors.size() > 0) {
			saveErrors(request, errors);
			// since we forward to jsp - not Action we don't need to repopulate
			// the lists here
			return mapping.findForward(FWD_FAIL);
		}

		List sysUsers = new ArrayList();
		List programs = new ArrayList();
		org.hibernate.Transaction tx = HibernateUtil.getSession().beginTransaction();

		if (dynaForm.get("sysUsers") != null) {
			sysUsers = (List) dynaForm.get("sysUsers");
		} else {
			SystemUserDAO sysUserDAO = new SystemUserDAOImpl();
			sysUsers = sysUserDAO.getAllSystemUsers();
		}
		if (dynaForm.get("programs") != null) {
			programs = (List) dynaForm.get("programs");
		} else {
			ProgramDAO programDAO = new ProgramDAOImpl();
			programs = programDAO.getAllPrograms();
		}

		String start = (String) request.getParameter("startingRecNo");
		String direction = (String) request.getParameter("direction");

		String sysUserId = (String) dynaForm.get("sysUserId");

		SystemUser sysUser = null;

		for (int i = 0; i < sysUsers.size(); i++) {
			SystemUser su = (SystemUser) sysUsers.get(i);
			if (su.getId().equals(sysUserId)) {
				sysUser = su;
				break;
			}
		}

		Project project = new Project();
		//get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId2 = String.valueOf(usd.getSystemUserId());	
		project.setSysUserId(sysUserId2);		
		
		Scriptlet scriptlet = new Scriptlet();
		String scriptletName = (String) dynaForm.get("scriptletName");
		scriptlet.setScriptletName(scriptletName);

		ScriptletDAO scriptletDAO = new ScriptletDAOImpl();
		Scriptlet scr = scriptletDAO.getScriptletByName(scriptlet);

		// populate valueholder from form
		PropertyUtils.copyProperties(project, dynaForm);

		project.setSystemUser(sysUser);
		project.setScriptlet(scr);
		// there is no specification for sticker req flag, but it can't be null
		// so default to N
		if (project.getStickerReqFlag() == null) {
			project.setStickerReqFlag(NO);
		}

		try {

			ProjectDAO projectDAO = new ProjectDAOImpl();

			if (!isNew) {
				// UPDATE
				projectDAO.updateData(project);
			} else {
				// INSERT
				projectDAO.insertData(project);
			}
			tx.commit();
		} catch (LIMSRuntimeException lre) {
            //bugzilla 2154
			LogEvent.logError("ProjectUpdateAction","performAction()",lre.toString());     		
			tx.rollback();
			errors = new ActionMessages();
			ActionError error = null;
			if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
				// how can I get popup instead of struts error at the top of
				// page?
				// ActionMessages errors = dynaForm.validate(mapping, request);
				error = new ActionError("errors.OptimisticLockException", null,
						null);
			} else {
			  //bugzilla 1482
				if (lre.getException() instanceof LIMSDuplicateRecordException) {
					java.util.Locale locale = (java.util.Locale) request.getSession()
					.getAttribute("org.apache.struts.action.LOCALE");
					//bugzilla 2438
					String messageKey = "project.projectNameOrLocalAbbrev";
					String msg =  ResourceLocator.getInstance().getMessageResources().getMessage(
							locale, messageKey);
					error = new ActionError("errors.DuplicateRecord.activate", msg,
							null);

				} else {
					error = new ActionError("errors.UpdateException", null,
							null);
				}	
			}
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			// in case of database exception don't allow another save without
			// cancel first
			//bugzilla 1485: allow change and try updating again (enable save button)
			//request.setAttribute(IActionConstants.ALLOW_EDITS_KEY, "false");
			// disable previous and next
			request.setAttribute(PREVIOUS_DISABLED, "true");
			request.setAttribute(NEXT_DISABLED, "true");
			forward = FWD_FAIL;

		} finally {
            HibernateUtil.closeSession();
        }
		if (forward.equals(FWD_FAIL))
			return mapping.findForward(forward);

		// initialize the form
		dynaForm.initialize(mapping);
		// repopulate the form from valueholder
		PropertyUtils.copyProperties(dynaForm, project);

		PropertyUtils.setProperty(dynaForm, "programs", programs);
		PropertyUtils.setProperty(dynaForm, "sysUsers", sysUsers);

		if ("true".equalsIgnoreCase(request.getParameter("close"))) {
			forward = FWD_CLOSE;
		}

		if (project.getId() != null && !project.getId().equals("0")) {
			request.setAttribute(ID, project.getId());

		}

		//bugzilla 1400
		if (isNew) forward = FWD_SUCCESS_INSERT;
		//bugzilla 1467 added direction for redirect to NextPreviousAction
		return getForward(mapping.findForward(forward), id, start, direction);

	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "project.add.title";
		} else {
			return "project.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "project.add.title";
		} else {
			return "project.edit.title";
		}
	}

	protected ActionMessages validateAll(HttpServletRequest request,
			ActionMessages errors, BaseActionForm dynaForm) throws Exception {

		// scriptlet name validation against database
		String scriptletNameSelected = (String) dynaForm.get("scriptletName");

		if (!StringUtil.isNullorNill(scriptletNameSelected)) {
			Scriptlet scriptlet = new Scriptlet();
			//System.out.println("This is scriptlet name selected "
			//		+ scriptletNameSelected);
			scriptlet.setScriptletName(scriptletNameSelected);
			ScriptletDAO scriptletDAO = new ScriptletDAOImpl();
			scriptlet = scriptletDAO.getScriptletByName(scriptlet);

			String messageKey = "project.scriptletName";

			if (scriptlet == null) {
				// the panelItem is not in database - not valid parentPanelItem
				ActionError error = new ActionError("errors.invalid",
						getMessageForKey(messageKey), null);
				errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			}
		}

		return errors;
	}

}