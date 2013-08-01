<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants"
%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>
<%--bugzila 2393--%>
<%!

String allowEdits = "true";
String errorMessageAccessionNumber = "";
String accnNumb = "";

%>
<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

//bugzilla 1494
java.util.Locale locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
accnNumb =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "resultsentry.accessionNumber");
errorMessageAccessionNumber =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "errors.invalid",
                    accnNumb);

%>
<script language="JavaScript1.2">

function pageOnLoad() {
    var accn = document.getElementById("accessionNumber");
    accn.focus();
}

function validateForm(form) {
    //return validateSampleXMLBySampleForm(form);
    return true;
}

function setAccessionNumberValidationMessage(message, field) {
      idField = document.getElementById(field);
      if (message == "invalid") {
       //only submit if accession number isn't left blank
         //bugzilla 1494
         alert('<%=errorMessageAccessionNumber%>');
        //disable save if accession number is incorrect    
     } else {
         setAction(window.document.forms[0], 'Process', 'no', '');
     }
  
}

function processAccessionNumberValidationSuccess(xhr) {
  var message = xhr.responseXML.getElementsByTagName("message")[0];
  var formfield = xhr.responseXML.getElementsByTagName("formfield")[0];
  //alert("I am in parseMessage and this is message, formfield " + message + " " + formfield);
  setAccessionNumberValidationMessage(message.childNodes[0].nodeValue, formfield.childNodes[0].nodeValue);
}

function processFailure(xhr) {
  //ajax call failed
}

function validateAccessionNumber() {

     new Ajax.Request (
       'ajaxXML',  //url
       {//options
        method: 'get', //http method
        parameters: 'provider=AccessionNumberValidationProvider&form=' + document.forms[0].name + '&field=accessionNumber&id=' + escape($F("accessionNumber")),      //request parameters
        //indicator: 'throbbing'
        onSuccess:  processAccessionNumberValidationSuccess,
        onFailure:  processFailure
       }
     );
}

</script>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />

	      <h1>
				<%--logic:notEmpty
					name="<%=IActionConstants.PAGE_SUBTITLE_KEY%>">
					<bean:write name="<%=IActionConstants.PAGE_SUBTITLE_KEY%>" />
				</logic:notEmpty--%> 
				<logic:empty
					name="<%=IActionConstants.PAGE_SUBTITLE_KEY%>">
					<% if (request.getParameter("ID").equals("0")) { %>
					  <bean:message key="default.add.title" />
					<% } else { %>
					  <bean:message key="default.edit.title" />
					<%}%>
			   </logic:empty> 
			   <app:text name="<%=formName%>" property="accessionNumber" styleId="accessionNumber" allowEdits="true" onkeypress="return noenter()"/>
               &nbsp;&nbsp;&nbsp;
			   <app:button  onclick="validateAccessionNumber();"
	                 			 property="download" allowEdits="true">
  			           <bean:message key="label.button.download"/>
  			   </app:button>
		</h1>
