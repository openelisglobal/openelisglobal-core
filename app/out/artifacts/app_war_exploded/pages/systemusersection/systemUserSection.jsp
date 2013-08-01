<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.systemuser.valueholder.SystemUser,
	us.mn.state.health.lims.test.valueholder.TestSection,
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
 return validateSystemUserSectionForm(form);
}
</script>

<table>
		<tr>
						<td class="label">
							<bean:message key="systemusersection.id"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="id" allowEdits="false"/>
						</td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="systemusersection.system.user.id"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:select name="<%=formName%>" property="systemUserId">
					   	  		<app:optionsCollection name="<%=formName%>" property="systemusers" label="nameForDisplay" value="id" allowEdits="true"/>
                           	</html:select>						
						</td>
		 </tr>
         <tr>
						<td class="label">
							<bean:message key="systemusersection.test.section.id"/>:<span class="requiredlabel">*</span>
						</td>	
						<td>
							<html:select name="<%=formName%>" property="testSectionId">
					   	  		<app:optionsCollection name="<%=formName%>" property="testsections" label="testSectionName" value="id" allowEdits="true"/>
                           	</html:select>						
						</td>
        </tr>
        <tr>						
						<td class="label">
							<bean:message key="systemusersection.has.view"/>:<span class="requiredlabel">*</span>
						</td>	
						<td>
							<html:text name="<%=formName%>" property="hasView" size="1" maxlength="1" onblur="this.value=this.value.toUpperCase()"/>
						</td>
        </tr>
        <tr>						
						<td class="label">
							<bean:message key="systemusersection.has.assign"/>:<span class="requiredlabel">*</span>
						</td>	
						<td>
							<html:text name="<%=formName%>" property="hasAssign" size="1" maxlength="1" onblur="this.value=this.value.toUpperCase()"/>
						</td>	
        </tr>
        <tr>								    
						<td class="label">
							<bean:message key="systemusersection.has.complete"/>:<span class="requiredlabel">*</span>
						</td>	
						<td>
							<html:text name="<%=formName%>" property="hasComplete" size="1" maxlength="1" onblur="this.value=this.value.toUpperCase()"/>
						</td>
        </tr>
        <tr>									
						<td class="label">
							<bean:message key="systemusersection.has.release"/>:<span class="requiredlabel">*</span>
						</td>	
						<td>
							<html:text name="<%=formName%>" property="hasRelease" size="1" maxlength="1" onblur="this.value=this.value.toUpperCase()"/>
						</td>																											
          </tr>
        <tr>									
						<td class="label">
							<bean:message key="systemusersection.has.cancel"/>:<span class="requiredlabel">*</span>
						</td>	
						<td>
							<html:text name="<%=formName%>" property="hasCancel" size="1" maxlength="1" onblur="this.value=this.value.toUpperCase()"/>
						</td>																											
          </tr>          
 		<tr>
		<td>&nbsp;</td>
		</tr>
</table>

<html:javascript formName="systemUserSectionForm"/>