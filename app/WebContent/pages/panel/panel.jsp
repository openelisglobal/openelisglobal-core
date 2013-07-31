<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>


<div id="sound"></div>

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
 return validatePanelForm(form);
}
</script>

<table>
		<tr>
						<td class="label">
							<bean:message key="panel.id"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="id" allowEdits="false"/>
						</td>
		</tr>
		<tr>
						<td class="label">
						    <%--bugzilla 1401 added asterisk for required--%>
							<bean:message key="panel.panelName"/>:<span class="requiredlabel">*</span>
						</td>	
						<td width="12"> 
							<html:text name="<%=formName%>" property="panelName" size="12"/>
						</td>
		</tr>
 		<tr>
						<td class="label">
							<bean:message key="panel.description"/>:<span class="requiredlabel">*</span>
						</td>	
						<td width="50"> 
							<html:text name="<%=formName%>" property="description" size="50" />
						</td>
		</tr>
	
 		<tr>
		<td>&nbsp;</td>
		</tr>
</table>

 
<html:javascript formName="panelForm"/>


