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
//bugzilla 1494
String errorMessageNumericDictValue = "";

String path = "";
String basePath = "";
%>
<%
path = request.getContextPath();
basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";

if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}
//bugzilla 1494
java.util.Locale locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
errorMessageNumericDictValue =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "testresult.error.dictionary.numericvalue");
%>

<script language="JavaScript1.2">

 
function validateForm(form) {
    var validated = validateTestResultForm(form);
    if (validated) {
       //check if value is numeric (foreign key to dictionary) if type id D 
       var type = document.getElementById("testResultType");
       var val = document.getElementById("resultValue");

       if (type.value == 'D' && !IsNumeric(val.value)) {
         alert('<%=errorMessageNumericDictValue%>');
         validated = false;
       }
    } 

    return validated;
} 
 

</script>

<table>
		<tr>
						<td class="label">
							<bean:message key="testresult.id"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="id" allowEdits="false"/>
						</td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="testresult.testName"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text styleId="testName" size="30" name="<%=formName%>" property="testName" /> 
							<span id="indicator1" style="display:none;"><img src="<%=basePath%>images/indicator.gif"/></span>
	   			              <input id="selectedTestName" name="selectedTestName" type="hidden" size="30" />
						</td>
		 </tr>
         <tr>
						<td class="label">
							<bean:message key="testresult.resultGroup"/>:
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="resultGroup" />
						</td>
		 </tr>
		<tr>
						<td class="label">
							<bean:message key="testresult.flags"/>:
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="flags" />
						</td>
		 </tr>
          <tr>
						<td class="label">
							<bean:message key="testresult.testResultType"/>:
						</td>	
						<td width="1">
							<html:text name="<%=formName%>" property="testResultType" styleId="testResultType" size="1"/>
						</td>
          </tr>
		<tr>
						<td class="label">
							<bean:message key="testresult.value"/>:
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="value" styleId="resultValue"/>
						</td>
		 </tr>
		<tr>
						<td class="label">
							<bean:message key="testresult.significantDigits"/>:
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="significantDigits" />
						</td>
		 </tr>
		<tr>
						<td class="label">
							<bean:message key="testresult.quantLimit"/>:
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="quantLimit" />
						</td>
		 </tr>
		<tr>
						<td class="label">
							<bean:message key="testresult.contLevel"/>:
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="contLevel" />
						</td>
		 </tr>
		<tr>
						<td class="label">
							<bean:message key="testresult.scriptletName"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text styleId="scriptletName" size="30" name="<%=formName%>" property="scriptletName" /> 
							<span id="indicator2" style="display:none;"><img src="<%=basePath%>images/indicator.gif"/></span>
	   			              <input id="selectedScriptletName" name="selectedScriptletName" type="hidden" size="30" />
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
  source="scriptletName"
  target="selectedScriptletName"
  baseUrl="ajaxAutocompleteXML"
  className="autocomplete"
  parameters="scriptletName={scriptletName},provider=ScriptletAutocompleteProvider,fieldName=scriptletName,idName=id"
  indicator="indicator2"
  minimumCharacters="1" />

<html:javascript formName="testResultForm"/>

