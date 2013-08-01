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
String path = "";
String basePath = "";
%>
<%
path = request.getContextPath();
basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";

if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

%>

<script language="JavaScript1.2">
function validateForm(form) {
 return validateMessageOrganizationForm(form);
}
</script>

<table>
		<tr>
						<td class="label">
							<bean:message key="messageorganization.id"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="id" allowEdits="false"/>
						</td>
		</tr>
		<tr>
						<td class="label">
						    <%--bugzilla 1401 removed asterisk for required--%>
							<bean:message key="messageorganization.organization"/>:<span class="requiredlabel">*</span>
						</td>
						<td> 
	   		             	<html:text styleId="organizationName" size="40" name="<%=formName%>" property="organizationName" /> 
	   						<span id="indicator1" style="display:none;"><img src="<%=basePath%>images/indicator.gif"/></span>
 	   		            		<input id="selectedOrganizationId" name="selectedOrganizationId" type="hidden" size="40" />
 						</td>	
        </tr>
		<tr>
						<td class="label">
							<bean:message key="messageorganization.description"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="description" onkeypress="return noenter()"/>
						</td>
		 </tr>
          <tr>
						<td class="label">
							<bean:message key="messageorganization.isActive"/>:<span class="requiredlabel">*</span>
						</td>	
						<td width="1">
							<html:text name="<%=formName%>" property="isActive" size="1" onblur="this.value=this.value.toUpperCase()"/>
						</td>
          </tr>
         <tr>
						<td class="label">
							<bean:message key="messageorganization.activeBeginDate"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="activeBeginDateForDisplay" styleClass="dateText" onkeyup="DateFormat(this,this.value,event,false,'1')" onblur="DateFormat(this,this.value,event,true,'1')"/>
						</td>
		</tr>
        <tr>
						<td class="label">
							<bean:message key="messageorganization.activeEndDate"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="activeEndDateForDisplay" styleClass="dateText" onkeyup="DateFormat(this,this.value,event,false,'1')" onblur="DateFormat(this,this.value,event,true,'1')"/>
						</td>
		</tr>
 		<tr>
		<td>&nbsp;</td>
		</tr>
</table>

  <ajax:autocomplete
  source="organizationName"
  target="selectedOrganizationId"
  baseUrl="ajaxAutocompleteXML"
  className="autocomplete"
  parameters="organizationName={organizationName},provider=OrganizationAutocompleteProvider,fieldName=organizationName,idName=id"
  indicator="indicator1"
  minimumCharacters="1" />

<app:javascript formName="messageOrganizationForm"/>

