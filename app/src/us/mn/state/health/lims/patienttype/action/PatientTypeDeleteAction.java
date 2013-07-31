package us.mn.state.health.lims.patienttype.action;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.DynaActionForm;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.login.valueholder.UserSessionData;
import us.mn.state.health.lims.patienttype.dao.PatientTypeDAO;
import us.mn.state.health.lims.patienttype.daoimpl.PatientTypeDAOImpl;
import us.mn.state.health.lims.patienttype.valueholder.PatientType;
import us.mn.state.health.lims.hibernate.HibernateUtil;

/**
 * Project : LIS<br>
 * File name : PatientTypeDeleteAction.java<br>
 * Description : 
 * @author TienDH
 * @date Aug 20, 2007
 */
public class PatientTypeDeleteAction extends BaseAction {
	static private String FWD_CLOSE = "close";

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new PatientType.
		// If there is a parameter present, we should bring up an existing
		// PatientType to edit.
		String forward = "success";
		System.out.println("I am in PatientTypeDeleteAction");

		DynaActionForm dynaForm = (DynaActionForm) form;
		// get selected PatientTypes
		String[] selectedIDs = (String[]) dynaForm.get("selectedIDs");

		// Vector PatientType = new Vector();
		//get sysUserId from login module
		UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
		String sysUserId = String.valueOf(usd.getSystemUserId());	

		List<PatientType> patientTypes = new ArrayList<PatientType>();		
		for (int i = 0; i < selectedIDs.length; i++) {
			PatientType patientType = new PatientType();
			patientType.setId(selectedIDs[i]);
			patientType.setSysUserId(sysUserId);
			patientTypes.add(patientType);
		}

		org.hibernate.Transaction tx = HibernateUtil.getSession().beginTransaction();	
		ActionMessages errors = null;
		try {
			
			System.out.println("Going to delete PatientType");
			PatientTypeDAO patientTypeDAO = new PatientTypeDAOImpl();
			patientTypeDAO.deleteData(patientTypes);
			System.out.println("Just deleted PatientType");

			dynaForm.initialize(mapping);
			tx.commit();
		} catch (LIMSRuntimeException lre) {
			tx.rollback();
			
			errors = new ActionMessages();
			ActionError error = null;
			if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
				error = new ActionError("errors.OptimisticLockException", null,	null);
			} else {
				error = new ActionError("errors.DeleteException", null, null);
			}
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			forward = FWD_FAIL;
			
		}  finally {
            HibernateUtil.closeSession();
        }							
		if (forward.equals(FWD_FAIL))
			return mapping.findForward(forward);
		
		if (TRUE.equalsIgnoreCase(request.getParameter("close"))) {
			forward = FWD_CLOSE;
		}
		System.out
				.println("I am in PatientTypeMenuDeleteAction setting menuDefinition");
		request.setAttribute("menuDefinition", "PatientTypeMenuDefinition");

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		return null;
	}

	protected String getPageSubtitleKey() {
		return null;
	}
}