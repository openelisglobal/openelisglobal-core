<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants,
	us.mn.state.health.lims.common.util.SystemConfiguration" %>
<%@ page import="org.owasp.encoder.Encode" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="idSeparator" value='<%= SystemConfiguration.getInstance().getDefaultIdSeparator() %>' />
<bean:define id="dictType" value='<%= SystemConfiguration.getInstance().getDictionaryType() %>' />
<%!

String allowEdits = "true";
String aID;
%>

<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}
aID = (String)request.getAttribute("aID");
%>


<script language="JavaScript1.2">
function customOnLoad() {
}

function validateForm(form) {
    //return validateTestAnalyteTestResultAddRGPopupForm(form);
    return true;
}

function cancelToParentForm() {
   if (window.opener && !window.opener.closed && window.opener.document.forms[0]) {
        window.close();
   } 
}




//This does NOT save to parent form but continues to either Dictionary or Non-Dictionary entry popup
function saveItToParentForm(form) {
 if (window.opener && !window.opener.closed && window.opener.document.forms[0]) {
   
   var rgType;
   var selectedRT = document.forms[0].elements['selectedResultType'];
   
   for (var i = 0; i < selectedRT.length; i++) {
       if (selectedRT[i].checked == true) {
          rgType = selectedRT[i].value;
          break;
       }
   }
   if (rgType == '<%=dictType%>') {
        window.opener.popupAddDictionaryRG(window, '<%=Encode.forJavaScript(aID)%>', rgType);
   } else {
        window.opener.popupAddNonDictionaryRG(window, '<%=Encode.forJavaScript(aID)%>', rgType);
   }
  }
}


</script>



<table align="center">
<logic:iterate id="rt" indexId="ctr" name="<%=formName%>" property="resultTypes" type="us.mn.state.health.lims.typeoftestresult.valueholder.TypeOfTestResult">
  <bean:define id="val" name="rt" property="testResultType"/>
  <tr>
    <td>
     <bean:write property="description" name="rt" />
    </td> 
    <td>
      <input type="radio" name="selectedResultType" value="<%=val%>" />
      <%--html:radio name="<%=formName%>" property="selectedResultType" value="<%=val%>" /--%>
    </td>
  </tr>
</logic:iterate>

</table>

<%--html:javascript formName="testAnalyteTestResultAddRGPopupForm" staticJavascript="true"/--%>

