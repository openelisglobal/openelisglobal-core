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
 return validatePanelItemForm(form);
}
</script>

<table>
		<tr>
						<td class="label">
							<bean:message key="panelitem.id"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="id" allowEdits="false"/>
						</td>
		</tr>
		
				<tr>
						<td class="label">
							<bean:message key="panelitem.panelParent"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
	   		             	<html:text styleId="parentPanelName" size="30" name="<%=formName%>" property="parentPanelName" /> 
			             	<span id="indicator1" style="display:none;"><img src="<%=basePath%>images/indicator.gif"/></span>
 	   		           		 <input id="selectedPanelId" name="selectedPanelId" type="hidden" size="30" />
 						</td>
 		</tr>
		<tr>
						<td class="label">
							<bean:message key="panelitem.methodName"/>:
						</td>	
						<td> 
					   	  <html:text styleId="methodName" size="30" name="<%=formName%>" property="methodName" /> 
			              <span id="indicator2" style="display:none;"><img src="<%=basePath%>images/indicator.gif"/></span>
 	   		               <input id="selectedMethodName" name="selectedMethodName" type="hidden" size="30" />
						                     
						</td>
		</tr>
		
		<tr>
						<td class="label">
						    <%--bugzilla 1401 added asterisk for required--%>
							<bean:message key="panelitem.testName"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
					   	  <html:text styleId="testName" size="30" name="<%=formName%>" property="testName" /> 
			              <span id="indicator3" style="display:none;"><img src="<%=basePath%>images/indicator.gif"/></span>
 	   					   <input id="selectedTestName" name="selectedTestName" type="hidden" size="30" />
						                     
						</td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="panelitem.sortOrder"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="sortOrder" />
						</td>
		</tr>
	
 		<tr>
		<td>&nbsp;</td>
		</tr>
</table>

 
  <ajax:autocomplete
  source="methodName"
  target="selectedMethodName"
  baseUrl="ajaxAutocompleteXML"
  className="autocomplete"
  parameters="methodName={methodName},provider=MethodAutocompleteProvider,fieldName=methodName,idName=id"
  indicator="indicator2"
  minimumCharacters="1" />
  
    <ajax:autocomplete
  source="testName"
  target="selectedTestName"
  baseUrl="ajaxAutocompleteXML"
  className="autocomplete"
  parameters="testName={testName},provider=TestAutocompleteProvider,fieldName=testName,idName=id"
  indicator="indicator3"
  minimumCharacters="1" />
  
    <ajax:autocomplete
  source="parentPanelName"
  target="selectedPanelId"
  baseUrl="ajaxAutocompleteXML"
  className="autocomplete"
  parameters="parentPanelName={parentPanelName},provider=PanelAutocompleteProvider,fieldName=panelName,idName=id"
  indicator="indicator1"
  minimumCharacters="1" />
  


<html:javascript formName="panelItemForm"/>


