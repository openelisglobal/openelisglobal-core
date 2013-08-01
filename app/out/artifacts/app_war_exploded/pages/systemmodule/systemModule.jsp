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
 return validateSystemModuleForm(form);
}
</script>

<table>
		<tr>
			<td>&nbsp;</td>
			<td><bean:message key="systemmodule.instructions"/><br/></td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="systemmodule.id"/>:
						</td>
						<td>
							<app:text name="<%=formName%>" property="id" allowEdits="false"/>
						</td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="systemmodule.name"/>:<span class="requiredlabel">*</span>
						</td>
						<td>
							<app:text name="<%=formName%>" property="systemModuleName" size="32"/>
						</td>
		 </tr>
         <tr>
						<td class="label">
							<bean:message key="systemmodule.description"/>:<span class="requiredlabel">*</span>
						</td>
						<td>
							<html:text name="<%=formName%>" property="description" size="80"/>
						</td>
        </tr>
        <tr>
						<td class="label">
							<bean:message key="systemmodule.has.select"/>:<span class="requiredlabel">*</span>
						</td>
						<td>
							<html:text name="<%=formName%>" property="hasSelectFlag" size="1" maxlength="1" onblur="this.value=this.value.toUpperCase()"/>
						</td>
        </tr>
        <tr>
						<td class="label">
							<bean:message key="systemmodule.has.add"/>:<span class="requiredlabel">*</span>
						</td>
						<td>
							<html:text name="<%=formName%>" property="hasAddFlag" size="1" maxlength="1" onblur="this.value=this.value.toUpperCase()"/>
						</td>
        </tr>
        <tr>
						<td class="label">
							<bean:message key="systemmodule.has.update"/>:<span class="requiredlabel">*</span>
						</td>
						<td>
							<html:text name="<%=formName%>" property="hasUpdateFlag" size="1" maxlength="1" onblur="this.value=this.value.toUpperCase()"/>
						</td>
        </tr>
        <tr>
						<td class="label">
							<bean:message key="systemmodule.has.delete"/>:<span class="requiredlabel">*</span>
						</td>
						<td>
							<html:text name="<%=formName%>" property="hasDeleteFlag" size="1" maxlength="1" onblur="this.value=this.value.toUpperCase()"/>
						</td>
          </tr>
 		<tr>
		<td>&nbsp;</td>
		</tr>
</table>

<html:javascript formName="systemModuleForm"/>