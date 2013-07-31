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
     return validateAnalysisForm(form);
}

</script>

<table>
		<tr>
						<td class="label">
							<bean:message key="analysis.id"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="id" allowEdits="false"/>
						</td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="analysis.analysisType"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="analysisType" />
						</td>
		 </tr>
		 <tr>
						<td class="label">
							<bean:message key="analysis.testSectionName"/>:
						</td>	
						<td> 
							<html:text styleId="testSectionName" size="30" name="<%=formName%>" property="testSectionName" /> 
							<span id="indicator1" style="display:none;"><img src="<%=basePath%>images/indicator.gif"/></span>
 	   			              <input id="testSectionId" name="testSectionId" type="hidden" />
						</td>
		 </tr>
		 <tr>
						<td class="label">
							<bean:message key="analysis.testName"/>:
						</td>	
						<td> 
							<html:text styleId="testName" size="30" name="<%=formName%>" property="testName" /> 
			    			<span id="indicator2" style="display:none;"><img src="<%=basePath%>images/indicator.gif"/></span>
 	   			              <input id="testId" name="testId" type="hidden" />
						</td>
		 </tr>
 		<tr>
		<td>&nbsp;</td>
		</tr>
</table>

  <ajax:autocomplete
  source="testSectionName"
  target="testSectionId"
  baseUrl="ajaxAutocompleteXML"
  className="autocomplete"
  parameters="testSectionName={testSectionName},provider=TestSectionAutocompleteProvider,fieldName=testSectionName,idName=id"
  indicator="indicator1"
  minimumCharacters="1" />
  
  <ajax:autocomplete
  source="testName"
  target="testId"
  baseUrl="ajaxAutocompleteXML"
  className="autocomplete"
  parameters="testName={testName},provider=TestAutocompleteProvider,fieldName=testName,idName=id"
  indicator="indicator2"
  minimumCharacters="1" />
  


<html:javascript formName="analysisForm"/>

