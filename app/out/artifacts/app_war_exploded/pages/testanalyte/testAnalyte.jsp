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
     return validateTestAnalyteForm(form);
}
</script>

<table>
		<tr>
						<td class="label">
							<bean:message key="testanalyte.id"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="id" allowEdits="false"/>
						</td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="testanalyte.testName"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text styleId="testName" size="30" name="<%=formName%>" property="testName" /> 
							<span id="indicator1" style="display:none;"><img src="<%=basePath%>images/indicator.gif"/></span>
	   			              <input id="selectedTestName" name="selectedTestName" type="hidden" size="30" />
						</td>
		 </tr>
		<tr>
						<td class="label">
							<bean:message key="testanalyte.analyteName"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text styleId="analyteName" size="30" name="<%=formName%>" property="analyteName" /> 
							<span id="indicator2" style="display:none;"><img src="<%=basePath%>images/indicator.gif"/></span>
	   			              <input id="selectedAnalyteName" name="selectedAnalyteName" type="hidden" size="30" />
						</td>
		 </tr>
         <tr>
						<td class="label">
							<bean:message key="testanalyte.resultGroup"/>:
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="resultGroup" />
						</td>
		 </tr>
		<tr>
						<td class="label">
							<bean:message key="testanalyte.sortOrder"/>:
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="sortOrder" />
						</td>
		 </tr>
          <tr>
						<td class="label">
							<bean:message key="testanalyte.testAnalyteType"/>:
						</td>	
						<td width="1">
							<html:text name="<%=formName%>" property="testAnalyteType" size="1"/>
						</td>
          </tr>
  		<tr>
		<td>&nbsp;</td>
		</tr>
</table>

  <ajax:autocomplete
  source="testName"
  target="selectedTestName"
  baseUrl="ajaxAutocompleteXML"
  className="autocomplete"
  parameters="testName={testName},provider=TestAutocompleteProvider,fieldName=testName,idName=id"
  indicator="indicator1"
  minimumCharacters="1" />
  
  <ajax:autocomplete
  source="analyteName"
  target="selectedAnalyteName"
  baseUrl="ajaxAutocompleteXML"
  className="autocomplete"
  parameters="parentAnalyteName={analyteName},provider=AnalyteAutocompleteProvider,fieldName=analyteName,idName=id"
  indicator="indicator2"
  minimumCharacters="1" />

<html:javascript formName="testAnalyteForm"/>

