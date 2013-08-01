<%@ page language="java" contentType="text/html; charset=utf-8" import="java.util.Date,us.mn.state.health.lims.common.action.IActionConstants,us.mn.state.health.lims.common.util.SystemConfiguration"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>


<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="idSeparator" value='<%= SystemConfiguration.getInstance().getDefaultIdSeparator() %>' />
<%!
String allowEdits = "true";

	%>

<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
				allowEdits = (String) request
						.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
			}

			request.setAttribute(IActionConstants.ALLOW_EDITS_KEY, allowEdits);

			%>

<table>
	<tr>
		<td colspan="4">
			<h2 align="left">
				<bean:message key="sampletracking.subtitle.patient" />
			</h2>
		</td>
	</tr>

	<tr>
		<td>
			<bean:message key="sampletracking.provider.patientId" />:
		</td>

		<td>
			<app:text name="<%=formName%>" property="externalId" size="30" styleClass="text" />
		</td>

	</tr>


	<tr>
		<td>
			<bean:message key="sampletracking.provider.clientRef" />:
		</td>

		<td>
			<app:text name="<%=formName%>" property="clientReference" size="30" styleClass="text" />
		</td>
	</tr>


	<tr>
		<td>
			<bean:message key="sampletracking.provider.lastname" />:
		</td>

		<td>
			<app:text name="<%=formName%>" property="lastname" size="30" styleClass="text" />
		</td>
	</tr>


	<tr>
		<td>
			<bean:message key="sampletracking.provider.firstname" />:
		</td>

		<td>
			<app:text name="<%=formName%>" property="firstname" size="30" styleClass="text" />
		</td>
	</tr>
</table>


