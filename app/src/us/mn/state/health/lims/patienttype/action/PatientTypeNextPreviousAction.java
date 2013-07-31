/**
 * Project : LIS<br>
 * File name : PatientTypeNextPreviousAction.java<br>
 * Description : 
 * @author TienDH
 * @date Aug 20, 2007
 */
package us.mn.state.health.lims.patienttype.action;

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
import us.mn.state.health.lims.patienttype.dao.PatientTypeDAO;
import us.mn.state.health.lims.patienttype.daoimpl.PatientTypeDAOImpl;
import us.mn.state.health.lims.patienttype.valueholder.PatientType;

public class PatientTypeNextPreviousAction extends BaseAction {

	@SuppressWarnings("unused")
	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, TRUE);
		request.setAttribute(PREVIOUS_DISABLED, FALSE);
		request.setAttribute(NEXT_DISABLED, FALSE);

		String id = request.getParameter(ID);

		if (StringUtil.isNullorNill(id) || "0".equals(id)) {
			isNew = true;
		} else {
			isNew = false;
		}

		@SuppressWarnings("unused")
		BaseActionForm dynaForm = (BaseActionForm) form;

		String start = (String) request.getParameter("startingRecNo");
		String direction = (String) request.getParameter("direction");
	
		PatientType patientType = new PatientType();
		patientType.setId(id);
		try {

			PatientTypeDAO patientTypeDAO = new PatientTypeDAOImpl();
			//retrieve analyte by id since the name may have changed
			patientTypeDAO.getData(patientType);

			if (FWD_NEXT.equals(direction)) {

				List patientTypes = patientTypeDAO.getNextPatientTypeRecord(patientType
						.getId().toString());
				if (patientTypes != null && patientTypes.size() > 0) {
					patientType = (PatientType) patientTypes.get(0);
					patientTypeDAO.getData(patientType);
					if (patientTypes.size() < 2) {
						// disable next button
						request.setAttribute(NEXT_DISABLED, TRUE);
					}
					id = patientType.getId().toString();
				} else {
					// just disable next button
					request.setAttribute(NEXT_DISABLED, TRUE);
				}
			}

			if (FWD_PREVIOUS.equals(direction)) {

				List listPatientType = patientTypeDAO.getPreviousPatientTypeRecord(patientType
						.getId().toString());
				if (listPatientType != null && listPatientType.size() > 0) {
					patientType = (PatientType) listPatientType.get(0);
					patientTypeDAO.getData(patientType);
					if (listPatientType.size() < 2) {
						// disable previous button
						request.setAttribute(PREVIOUS_DISABLED, TRUE);
					}
					id = patientType.getId().toString();
				} else {
					// just disable next button
					request.setAttribute(PREVIOUS_DISABLED, TRUE);
				}
			}

		} catch (LIMSRuntimeException lre) {
			request.setAttribute(ALLOW_EDITS_KEY, FALSE);
			// disable previous and next
			request.setAttribute(PREVIOUS_DISABLED, TRUE);
			request.setAttribute(NEXT_DISABLED, TRUE);
			forward = FWD_FAIL;
		}
		if (forward.equals(FWD_FAIL))
			return mapping.findForward(forward);

		if (patientType.getId() != null && !patientType.getId().equals("0")) {
			request.setAttribute(ID, patientType.getId());

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