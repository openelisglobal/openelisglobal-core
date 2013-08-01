<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<!-- bugzilla 2314 -->
<html:hidden property="currentDate" name="<%=formName%>" styleId="currentDate"/>
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
 return validateLoginUserForm(form);
}
</script>

<table>
		<tr>
						<td class="label">
							<bean:message key="login.id"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="id" allowEdits="false"/>
						</td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="login.login.name"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="loginName" size="20" maxlength="20"/>
						</td>
		 </tr>
         <tr>
						<td class="label">
							<bean:message key="login.password"/>:<span class="requiredlabel">*</span>
						</td>	
						<td>
							<html:text name="<%=formName%>" property="password" size="20" maxlength="80"/>
						</td>
        </tr>
        <tr>						
						<td class="label">
							<bean:message key="login.password.expired.date"/>:<span class="requiredlabel">*</span>
						</td>	
						<td>
							<app:text name="<%=formName%>" property="passwordExpiredDateForDisplay" size="10" maxlength="10" styleClass="dateText" onkeyup="DateFormat(this,this.value,event,false,'1')" onblur="DateFormat(this,this.value,event,true,'1')"/>						
						</td>
        </tr>
        <tr>						
						<td class="label">
							<bean:message key="login.account.locked"/>:<span class="requiredlabel">*</span>
						</td>	
						<td>
							<html:text name="<%=formName%>" property="accountLocked" size="1" maxlength="1" onblur="this.value=this.value.toUpperCase()"/>
						</td>	
        </tr>
        <tr>								    
						<td class="label">
							<bean:message key="login.account.disabled"/>:<span class="requiredlabel">*</span>
						</td>	
						<td>
							<html:text name="<%=formName%>" property="accountDisabled" size="1" maxlength="1" onblur="this.value=this.value.toUpperCase()"/>
						</td>
        </tr>
        <tr>									
						<td class="label">
							<bean:message key="login.is.admin"/>:<span class="requiredlabel">*</span>
						</td>	
						<td>
							<html:text name="<%=formName%>" property="isAdmin" size="1" maxlength="1" onblur="this.value=this.value.toUpperCase()"/>
						</td>																											
        </tr>
      	<tr>									
						<td class="label">
							<bean:message key="login.timeout"/>:<span class="requiredlabel">*</span>
						</td>	
						<td>
							<html:text name="<%=formName%>" property="userTimeOut" size="3" maxlength="3"/>
						</td>																											
		</tr>          
 		<tr>
		<td>&nbsp;</td>
		</tr>
</table>

<%--bugzilla 2314--%>
<app:javascript formName="loginUserForm"/>