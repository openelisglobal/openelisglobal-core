<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants,
	us.mn.state.health.lims.common.util.SystemConfiguration,
    us.mn.state.health.lims.test.valueholder.Test,
    us.mn.state.health.lims.testanalyte.valueholder.TestAnalyte,
    us.mn.state.health.lims.analyte.valueholder.Analyte,
    us.mn.state.health.lims.result.valueholder.Result,
	us.mn.state.health.lims.testresult.valueholder.TestResult,
	us.mn.state.health.lims.analysis.valueholder.Analysis" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>


<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="idSeparator" value='<%= SystemConfiguration.getInstance().getDefaultIdSeparator() %>' />



<%!

String allowEdits = "true";
String disabled = "false";
String checked = "";
int myCounter = 0;

%>

<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}
%>


<script language="JavaScript1.2">

function customOnLoad() {
  //default the cursor to save button
  var save = document.getElementById('save');
  save.focus();
}


function validateForm(form) {
    //return validateResultsEntryLinkChildTestToParentTestResultPopupForm(form);
}

function cancelToParentForm() {
   if (window.opener && !window.opener.closed && window.opener.document.forms[0]) {
   //alert("going to clear save clicked");
        window.opener.clearClicked();
        //alert("closing window");
        window.close();
   } 
}

function getRadioValue() {

var rad_val = "";

for (var i=0; i < document.forms[0].selectedLinkedParentAnalysisParentResult.length; i++) {
 
   if (document.forms[0].selectedLinkedParentAnalysisParentResult[i].checked) {
      var rad_val = document.forms[0].selectedLinkedParentAnalysisParentResult[i].value;
   }
} 
return rad_val;
}

function saveItToParentForm(form) {
   //this is executed when save is selected 
   if (window.opener && !window.opener.closed && window.opener.document.forms[0]) {
      var radioValue = getRadioValue();
      if (radioValue != null && radioValue != '') {
       window.opener.submitTheFormWithLinkedTest(radioValue);
      }
      window.close();
   } 
  
}

</script>



<table align="center">
<tr>
<td colspan="3"><span class="mediumlabel"><bean:message key="resultsentry.linkChildTestToParentTestResultPopup.message" /></span></td>
<td colspan="1">&nbsp;</td>
</tr>
<tr>
<td colspan="4">&nbsp;</td>
</tr>
<tr>
<td colspan="4">&nbsp;</td>
</tr>
<bean:define id="childAnalysisId" name="<%=formName%>" property="childAnalysisId"/>
<logic:iterate id="test" indexId="ctr" name="<%=formName%>" property="listOfParentAnalyses" type="us.mn.state.health.lims.analysis.valueholder.Analysis">
  <bean:define id="parentResults" name="<%=formName%>" property="listOfParentResults" type="java.util.List"/>
  <bean:define id="parentAnalytes" name="<%=formName%>" property="listOfParentAnalytes" type="java.util.List"/>
  <logic:equal name="ctr" value="0">
    <tr>
     <td><bean:message key="resultsentry.linkChildTestToParentTestResultPopup.parent.test"/></td>
     <td><bean:message key="resultsentry.linkChildTestToParentTestResultPopup.parent.analyte"/></td>
     <td><bean:message key="resultsentry.linkChildTestToParentTestResultPopup.parent.result"/></td>
     <td>&nbsp;</td>
    </tr>
   </logic:equal>
   <tr>
     <% 
       Result r = (Result)parentResults.get(ctr.intValue()); 
       Analyte a = (Analyte)parentAnalytes.get(ctr.intValue());
     %>
    <td>
     <% out.println(test.getTest().getName()); %>
    </td>
    <td>
     <% out.println(a.getName()); %>
    </td>
    <td>
     <% out.println(r.getValue()); %>
    </td>
    <td>
      <html:radio property="selectedLinkedParentAnalysisParentResult" name="<%=formName%>" value='<%= childAnalysisId + idSeparator + test.getId() + idSeparator + r.getId() %>'/> 
    </td>
  </tr>
</logic:iterate>


</table>
