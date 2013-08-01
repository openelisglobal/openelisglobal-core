<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />


<%!

String allowEdits = "true";

%>

<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

%>

<script language="JavaScript1.2">
function validateForm(form) {
 return validateTestTrailerForm(form);
}
</script>

<table>
		<tr>
						<td class="label">
							<bean:message key="testtrailer.id"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="id" allowEdits="false"/>
						</td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="testtrailer.testTrailerName"/>:<span class="requiredlabel">*</span>
						</td>	
						<td width="15"> 
							<html:text name="<%=formName%>" property="testTrailerName" size="15"/>
						</td>
		</tr>
        <tr>
						<td class="label">
							<bean:message key="testtrailer.description"/>:<span class="requiredlabel">*</span>
						</td>	
						<td width="50"> 
							<html:text name="<%=formName%>" property="description" size="50"/>
						</td>
		</tr>
		        <tr>
						<td class="label">
							<bean:message key="testtrailer.text"/>:<span class="requiredlabel">*</span>
						</td>	
						<td width="50"> 
							<%--html:text name="<%=formName%>" property="text"/--%>
						    <html:textarea name="<%=formName%>" property="text" cols="50" rows="4"/>
						</td>
		</tr>

	
 		<tr>
		<td>&nbsp;</td>
		</tr>
</table>

 
<html:javascript formName="testTrailerForm"/>


