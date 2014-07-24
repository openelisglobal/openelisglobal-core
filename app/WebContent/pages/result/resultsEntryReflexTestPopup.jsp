<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants,
	us.mn.state.health.lims.common.util.SystemConfiguration,
    us.mn.state.health.lims.test.valueholder.Test,
    us.mn.state.health.lims.testanalyte.valueholder.TestAnalyte,
    us.mn.state.health.lims.analyte.valueholder.Analyte,
	us.mn.state.health.lims.testresult.valueholder.TestResult,
	us.mn.state.health.lims.analysis.valueholder.Analysis" %>
<%@ page import="us.mn.state.health.lims.common.services.TestService" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<%--bugzilla 1802- screen redesign - now a reflex test may be triggered by more than one test so 
    display parent test and component--%>
<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />


<%!

String allowEdits = "true";
String disabled = "false";
String checked = "";
//bugzilla 1882
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
    //return validateResultsEntryReflexTestPopupForm(form);
}

function cancelToParentForm() {
   if (window.opener && !window.opener.closed && window.opener.document.forms[0]) {
   //alert("going to clear save clicked");
        window.opener.clearClicked();
        //alert("closing window");
        window.close();
   } 
}

function saveWithoutAdditionalTestsToParentForm() {
   if (window.opener && !window.opener.closed && window.opener.document.forms[0]) {
   //alert("going to clear save clicked");
        //window.opener.clearSaveClicked();
        //alert("closing window and submitting form");
        window.opener.submitTheForm(window.opener.document.forms[0]);
        window.close();
   } 
}

function saveItToParentForm(form) {
   //this is executed when save is selected 
   if (window.opener && !window.opener.closed && window.opener.document.forms[0]) {
   //alert("Going to clear save clicked");
        //window.opener.clearSaveClicked();
        window.opener.setAddReflexTests(document.forms[0]);
        window.opener.submitTheFormWithAddedReflexTests(window.opener.document.forms[0]);
        window.close();
   } 

  
}

</script>



<table align="center">
<tr>
<td colspan="3"><span class="mediumlabel"><bean:message key="resultsentry.reflexTestPopup.message" /></span></td>
</tr>
<logic:iterate id="reflexTests" indexId="ctr" name="<%=formName%>" property="listOfReflexTests" type="us.mn.state.health.lims.test.valueholder.Test">
  <bean:define id="val" name="reflexTests" property="id"/>
  <bean:define id="parentResults" name="<%=formName%>" property="listOfParentResults" type="java.util.List"/>
  <bean:define id="parentAnalytes" name="<%=formName%>" property="listOfParentAnalytes" type="java.util.List"/>
   <logic:equal name="ctr" value="0">
    <tr>
     <td>&nbsp;</td>
     <td><bean:message key="resultsentry.reflexTestPopup.parent.test"/></td>
     <td><bean:message key="resultsentry.reflexTestPopup.parent.analyte"/></td>
    </tr>
   </logic:equal>
   <tr>
    <td>
     <% 
       TestResult tr = (TestResult)parentResults.get(ctr.intValue()); 
       Test originalTest = tr.getTest();
       
       TestAnalyte ta = (TestAnalyte)parentAnalytes.get(ctr.intValue());
       Analyte originalAnalyte = ta.getAnalyte();
     %>
     <logic:equal name="<%=formName%>" property='<%= "listOfReflexTestsDisabledFlags[" + ctr + "]"%>' value="Y">
      <html:multibox property="selectedAddedTests" name="<%=formName%>" disabled="true"> 
          <bean:write name="reflexTests" property="id"/>
      </html:multibox>
      <bean:write name="reflexTests" property="testName"/>
     </logic:equal>  
     <logic:notEqual name="<%=formName%>" property='<%= "listOfReflexTestsDisabledFlags[" + ctr + "]"%>' value="Y">
      <html:multibox property="selectedAddedTests" name="<%=formName%>"> 
          <bean:write name="reflexTests" property="id"/>
      </html:multibox>
      <bean:write name="reflexTests" property="testName"/>
     </logic:notEqual> 
      
    </td>
    <td>
     <% out.println( TestService.getLocalizedTestName( originalTest )); %>
    </td>
    <td>
     <% out.println(originalAnalyte.getAnalyteName()); %>
    </td>
  </tr>
  <tr>
    <td>&nbsp</td>
  </tr>
  <tr>
    <td>&nbsp</td>
  </tr>
  <tr>
    <td>&nbsp</td>
  </tr>
</logic:iterate>

<%--bugzilla 1882--%>
<% myCounter = 0; %>
<logic:iterate id="reflexTestParentResults" indexId="ctr2" name="<%=formName%>" property="listOfParentResults" type="us.mn.state.health.lims.testresult.valueholder.TestResult">
  <!--bugzilla 2212 only happening on tomcat: need to define val2 as type String-->
  <bean:define id="val2" name="reflexTestParentResults" property="id" type="java.lang.String"/>
  <bean:define id="parentAnalytes2" name="<%=formName%>" property="listOfParentAnalytes" type="java.util.List"/>
  <bean:define id="parentAnalyses" name="<%=formName%>" property="listOfParentAnalyses" type="java.util.List"/>
  <% TestAnalyte myAnalyte = (TestAnalyte)parentAnalytes2.get(myCounter);
     String myAnalyteId = myAnalyte.getId(); 
     Analysis myAnalysis = (Analysis)parentAnalyses.get(myCounter);
     String myAnalysisId = myAnalysis.getId();
  %>
    <html:hidden property="selectedAddedTestParentResults" name="<%=formName%>" value="<%=val2%>"/>
    <html:hidden property="selectedAddedTestParentAnalytes" name="<%=formName%>" value="<%=myAnalyteId%>"/>
    <html:hidden property="selectedAddedTestParentAnalyses" name="<%=formName%>" value="<%=myAnalysisId%>"/>

  <% myCounter++; %>
</logic:iterate>
</table>
