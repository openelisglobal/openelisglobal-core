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
package us.mn.state.health.lims.sample.action;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.sample.valueholder.Sample;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * The QuickEntryAction class represents the initial 
 * Action for the QuickEntry form of the application.
 * 
 * @author	- Ken Rosha		08/29/2006
 * 02/21/2007 bugzilla 1757 clear out collections for typeOfSample/sourceOfSample since we use ajax
 */
public class QuickEntryAction
	extends BaseAction 
{
	protected ActionForward performAction(ActionMapping 		mapping,
										  ActionForm			form,
										  HttpServletRequest	request,
										  HttpServletResponse	response)
		throws Exception 
	{
		// This is a new quick entry sample
		String forward = "success";
		request.setAttribute(ALLOW_EDITS_KEY, "false");
		
		HttpSession session = request.getSession();
		ArrayList selectedTestIds = new ArrayList();
		session.setAttribute("selectedTestIds", selectedTestIds);

		BaseActionForm dynaForm = (BaseActionForm)form;

		// Initialize the form.
		dynaForm.initialize(mapping);

		Sample	sample	= new Sample();

		// Set received date and entered date to today's date
		Date today = Calendar.getInstance().getTime();
		String dateAsText = DateUtil.formatDateAsText(today);

		SystemConfiguration sysConfig = SystemConfiguration.getInstance();
		
		sample.setReceivedDateForDisplay(dateAsText);
		sample.setEnteredDateForDisplay(dateAsText);
		sample.setReferredCultureFlag(sysConfig.getQuickEntryDefaultReferredCultureFlag());
		sample.setStickerReceivedFlag(sysConfig.getQuickEntryDefaultStickerReceivedFlag());
		
		// default nextItemSequence to 1 (for clinical - always 1)
		sample.setNextItemSequence(sysConfig.getQuickEntryDefaultNextItemSequence());

		// revision is set to 0 on insert
		sample.setRevision(sysConfig.getQuickEntryDefaultRevision());

		sample.setCollectionTimeForDisplay(sysConfig.getQuickEntryDefaultCollectionTimeForDisplay());

		if (sample.getId() != null && !sample.getId().equals("0"))
		{
			request.setAttribute(ID, sample.getId());
		}

		// populate form from valueholder
		PropertyUtils.copyProperties(form, sample);

		PropertyUtils.setProperty(form, "currentDate",		dateAsText);
		request.setAttribute("menuDefinition", "QuickEntryDefinition");
		return mapping.findForward(forward);
	}
	//==============================================================

	protected String getPageTitleKey()
	{
		return "quick.entry.edit.title";
	}
	//==============================================================

	protected String getPageSubtitleKey()
	{
		return "quick.entry.edit.title";
	}
	//==============================================================
}
