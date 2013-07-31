<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants, 
	us.mn.state.health.lims.common.util.SystemConfiguration" %>  
	
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="idSeparator" value='<%= SystemConfiguration.getInstance().getDefaultIdSeparator() %>' />
<bean:define id="accessionNumberParm" value='<%= IActionConstants.ACCESSION_NUMBER%>' />
<bean:define id="expectedStatus" value='<%= SystemConfiguration.getInstance().getSampleStatusEntry2Complete() %>' />
<bean:define id="expectedDomain" value='<%= SystemConfiguration.getInstance().getNewbornDomain() %>' />

<%
	String allowEdits = "true";
	String accnNumb = "";
	String errorMessageAccessionNumber = "";	
	String quickEntry = "";
	String errorMessageLabelPrinted = "";

	if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
		allowEdits = (String) request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
	}

	request.setAttribute(IActionConstants.ALLOW_EDITS_KEY, allowEdits);
	java.util.Locale locale = (java.util.Locale) request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
	accnNumb = us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(locale,"quick.entry.accession.number");
	quickEntry = us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(locale,"quick.entry.title");				
			
	errorMessageAccessionNumber = us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(locale,"errors.invalid", accnNumb);
	errorMessageLabelPrinted = us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(locale,"error.testmanagement.entrystatus", quickEntry);

%>

<script language="JavaScript1.2">

var expectedStatus = '<%=expectedStatus%>';
var expectedDomain = '<%=expectedDomain%>';

function pageOnLoad() {
	var accnNumb = $("accessionNumber");
}

function setAccessionNumberValidationMessage(message, field) {
    idField = $(field);

    if (message == "invalid") { 
        setButtons(true); 
       	alert('<%=errorMessageAccessionNumber%>');
       	document.forms[0].accessionNumber.focus();
    } else if (message == "invalidStatus") {
        setButtons(true);
     	alert('<%=errorMessageLabelPrinted%>');
     	document.forms[0].accessionNumber.focus();
    } else {
        setButtons(false); 
        validateDomain(field);
    }
}

function processAccessionNumberValidationSuccess(xhr) {
	var message = xhr.responseXML.getElementsByTagName("message")[0];
  	var formfield = xhr.responseXML.getElementsByTagName("formfield")[0];
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

function validateForm(form) {    
    return true;
}

function resultsEntryHistoryBySamplePopup () {

	var form = document.forms[0];
  	var accessionNumber = $F("accessionNumber");
   
    //if  no errors otherwise on page -> go to add test popup
	var context = '<%= request.getContextPath() %>';
	var server = '<%= request.getServerName() %>';
	var port = '<%= request.getServerPort() %>';
	var scheme = '<%= request.getScheme() %>';
	
	var hostStr = scheme + "://" + server;
	if ( port != 80 && port != 443 ) {
		hostStr = hostStr + ":" + port;
	}
	hostStr = hostStr + context;
	
	// Get the sessionID
	var sessionid = '';
	var sessionIndex = form.action.indexOf(';');

	if(sessionIndex >= 0){
		var queryIndex = form.action.indexOf('?');
		var length = form.action.length;
		if (queryIndex > sessionIndex) {
			length = queryIndex;
		}
		sessionid = form.action.substring(sessionIndex,length);
	}
 
    var param = '?' + '<%=accessionNumberParm%>' + '=' + accessionNumber;
 	var href = context + "/ResultsEntryHistoryBySamplePopup.do" + param + sessionid;
    //alert("href "+ href);
	createPopup( href, 1250, 500 );
}

function validateDomain(field) {
    new Ajax.Request (
        'ajaxXML',  //url
        {//options
        method: 'get', //http method
        parameters: 'provider=SampleDomainValidationProvider&form=' + document.forms[0].name + '&field=accessionNumber&id=' + escape(document.forms[0].accessionNumber.value) + '&expectedDomain=' + escape(expectedDomain),      //request parameters
        //indicator: 'throbbing'
        onSuccess:  processDomainValidationSuccess,
        onFailure:  processFailure
        }
    ); 
}

function processDomainValidationSuccess(xhr) {
    var message = xhr.responseXML.getElementsByTagName("message")[0];
    var formfield = xhr.responseXML.getElementsByTagName("formfield")[0];
    setDomainValidationMessage(message.childNodes[0].nodeValue, formfield.childNodes[0].nodeValue);
}

function setDomainValidationMessage(message, field) {   
    if ( message == 'invalid') {        
        setButtons(true);
        alert('Invalid Domain');
        document.forms[0].accessionNumber.focus();
    } else {
        setButtons(false);
        setAction(window.document.forms[0], 'ViewSampleDemographicsAnd', 'no', '');
    }       
}

</script>

<table border="0" width="100%">
	<tr>
		<td valign="top" width="10%" noWrap>
			<bean:message key="sample.accessionNumber"/>: <span class="requiredlabel">*</span>
		</td>
		<td valign="top" width="20%" noWrap>
			<app:text name="<%=formName%>" property="accessionNumber" allowEdits="true" maxlength="10" onkeypress="return noenter()" styleId="accessionNumber"/>
		</td>
		<td valign="top">
			<html:button onclick="validateAccessionNumber();" property="cancel">
				<bean:message key="label.button.display" />
			</html:button>
			&nbsp;
			<logic:equal name="<%=formName%>" property="sampleHasTestRevisions" value="true">
				<html:button onclick="resultsEntryHistoryBySamplePopup();return false;" property="cancel">
				  <bean:message key="testmanagement.label.button.history" />
			    </html:button>
			</logic:equal>
		</td>
	</tr>
</table>