<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.systemuser.valueholder.SystemUser,
	us.mn.state.health.lims.systemmodule.valueholder.SystemModule,
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
 return validateSystemUserModuleForm(form);
}
</script>

<table>
		<tr>
						<td class="label">
							<bean:message key="systemusermodule.id"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="id" allowEdits="false"/>
						</td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="systemusermodule.system.user.id"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:select name="<%=formName%>" property="systemUserId">
					   	  		<app:optionsCollection name="<%=formName%>" property="systemusers" label="shortNameForDisplay" value="id" allowEdits="true"/>
                           	</html:select>						
						</td>
		 </tr>
         <tr>
						<td class="label">
							<bean:message key="systemusermodule.system.module.id"/>:<span class="requiredlabel">*</span>
						</td>	
						<td>
							<html:select name="<%=formName%>" property="systemModuleId">
					   	  		<app:optionsCollection name="<%=formName%>" property="systemmodules" label="description" value="id" allowEdits="true"/>
                           	</html:select>						
						</td>
        </tr>
        <tr>						
						<td class="label">
							<bean:message key="systemusermodule.has.select"/>:<span class="requiredlabel">*</span>
						</td>	
						<td>
							<html:text name="<%=formName%>" property="hasSelect" size="1" maxlength="1" onblur="this.value=this.value.toUpperCase()"/>
						</td>
        </tr>
        <tr>						
						<td class="label">
							<bean:message key="systemusermodule.has.add"/>:<span class="requiredlabel">*</span>
						</td>	
						<td>
							<html:text name="<%=formName%>" property="hasAdd" size="1" maxlength="1" onblur="this.value=this.value.toUpperCase()"/>
						</td>	
        </tr>
        <tr>								    
						<td class="label">
							<bean:message key="systemusermodule.has.update"/>:<span class="requiredlabel">*</span>
						</td>	
						<td>
							<html:text name="<%=formName%>" property="hasUpdate" size="1" maxlength="1" onblur="this.value=this.value.toUpperCase()"/>
						</td>
        </tr>
        <tr>									
						<td class="label">
							<bean:message key="systemusermodule.has.delete"/>:<span class="requiredlabel">*</span>
						</td>	
						<td>
							<html:text name="<%=formName%>" property="hasDelete" size="1" maxlength="1" onblur="this.value=this.value.toUpperCase()"/>
						</td>																											
          </tr>
 		<tr>
		<td>&nbsp;</td>
		</tr>
</table>

<html:javascript formName="systemUserModuleForm"/>