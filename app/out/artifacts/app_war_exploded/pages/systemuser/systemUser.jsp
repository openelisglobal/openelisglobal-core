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
 return validateSystemUserForm(form);
}
</script>

<table>
		<tr>
						<td class="label">
							<bean:message key="systemuser.id"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="id" allowEdits="false"/>
						</td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="systemuser.firstName"/>:<span class="requiredlabel">*</span>
						</td>	
						<td width="20"> 
							<html:text name="<%=formName%>" property="firstName" size="20"/>
						</td>
		 </tr>
         <tr>
						<td class="label">
							<bean:message key="systemuser.lastName"/>:<span class="requiredlabel">*</span>
						</td>	
						<td>
							<html:text name="<%=formName%>" property="lastName"/>
						</td>
          </tr>
          <tr>
						<td class="label">
							<bean:message key="systemuser.loginName"/>:<span class="requiredlabel">*</span>
						</td>	
						<td>
							<html:text name="<%=formName%>" property="loginName"/>
						</td>
          </tr>
          <tr>
						<td class="label">
							<bean:message key="systemuser.initials"/>:<span class="requiredlabel">*</span>
						</td>	
						<td width="3">
							<html:text name="<%=formName%>" property="initials" size="3"/>
						</td>
          </tr>
          <tr>
						<td class="label">
							<bean:message key="systemuser.isEmployee"/>:<span class="requiredlabel">*</span>
						</td>	
						<td width="1">
							<html:text name="<%=formName%>" property="isEmployee" size="1" maxlength="1" onblur="this.value=this.value.toUpperCase()"/>
						</td>
          </tr>
          <tr>
						<td class="label">
							<bean:message key="systemuser.isActive"/>:<span class="requiredlabel">*</span>
						</td>	
						<td width="1">
							<html:text name="<%=formName%>" property="isActive" size="1" maxlength="1" onblur="this.value=this.value.toUpperCase()"/>
						</td>
          </tr>
          <tr>
						<td class="label">
							<bean:message key="systemuser.externalId"/>:<span class="requiredlabel">*</span>
						</td>	
						<td width="80">
							<html:text name="<%=formName%>" property="externalId" size="80"/>
						</td>
          </tr>
 		<tr>
		<td>&nbsp;</td>
		</tr>
</table>

<html:javascript formName="systemUserForm"/>

