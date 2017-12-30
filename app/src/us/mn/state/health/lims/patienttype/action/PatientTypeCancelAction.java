/**
 * Project : LIS<br>
 * File name : PatientTypeCancelAction.java<br>
 * Description : 
 * @author TienDH
 * @date Aug 20, 2007
 */
package us.mn.state.health.lims.patienttype.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import us.mn.state.health.lims.common.action.BaseAction;

public class PatientTypeCancelAction extends BaseAction {

    protected ActionForward performAction(ActionMapping mapping,
	    ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	@SuppressWarnings("unused")
	DynaActionForm dynaForm = (DynaActionForm) form;

	return mapping.findForward(FWD_CLOSE);

    }

    protected String getPageTitleKey() {
	return "patienttype.browse.title";
    }

    protected String getPageSubtitleKey() {
	return "patienttype.browse.title";
    }
}