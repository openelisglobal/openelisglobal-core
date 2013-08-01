<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants" %>

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
 return validateReferenceTablesForm(form);
}
</script>

<table>
		<tr>
						<td class="label">
							<bean:message key="referencetables.id"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="id" allowEdits="false"/>
						</td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="referencetables.tableName"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
					    	<html:text name="<%=formName%>" property = "tableName" size = "40" maxlength= "40" onblur="this.value=this.value.toUpperCase()"/>
						</td>
		 </tr>
         <tr>
						<td class="label">
							<bean:message key="referencetables.keepHistory"/>:<span class="requiredlabel">*</span>
						</td>	
						<td>
							<html:text name="<%=formName%>" property="keepHistory" size = "1" maxlength= "1" onblur="this.value=this.value.toUpperCase()"/>
						</td>
          </tr>
          <tr>
                        <td class="label">
                           <bean:message key="referencetables.Hl7Encoded"/>:<span class="requiredlabel">*</span>
                        </td>
                        <td>
                           <html:text name="<%=formName%>" property="isHl7Encoded" size = "1" maxlength = "1" onblur="this.value=this.value.toUpperCase()"/>
                         <td>
 		<tr>
		<td>&nbsp;</td>
		</tr>
</table>

<html:javascript formName="referenceTablesForm"/>
