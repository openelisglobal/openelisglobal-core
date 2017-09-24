<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="us.mn.state.health.lims.common.action.IActionConstants,
                 us.mn.state.health.lims.common.util.Versioning,
                 us.mn.state.health.lims.common.provider.validation.AccessionNumberValidatorFactory,
                 us.mn.state.health.lims.common.provider.validation.IAccessionNumberValidator,
                 us.mn.state.health.lims.common.util.StringUtil" %>
<%@ taglib uri="/tags/struts-bean"      prefix="bean" %>
<%@ taglib uri="/tags/struts-html"      prefix="html" %>
<%@ taglib uri="/tags/labdev-view" 		prefix="app" %>

<%!
String path = "";
String basePath = "";
IAccessionNumberValidator accessionNumberValidator;
%>

<%
path = request.getContextPath();
basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
accessionNumberValidator = new AccessionNumberValidatorFactory().getValidator();
%>

<script type="text/javascript" src="scripts/utilities.js"></script>
<script type="text/javascript" src="scripts/ajaxCalls.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script type="text/javascript">
function nextLabel() {
	postBatchSample(alertSuccess, defaultFailure);
	document.getElementById("labNo").value = "";
	document.getElementById("next").disabled= "true";
}

function reenterLabel() {
	
}

function doneLabelling() {
	
}

function alertSuccess() {
	//alert("success");
}

function checkAccessionNumber(accessionNumber) {
    //check if empty
  //  if (!fieldIsEmptyById("#labNo")) {
        validateAccessionNumberOnServer(false, false, accessionNumber.id, accessionNumber.value, processAccessionSuccess, null);
   // }
   // else {
    //     selectFieldErrorDisplay(false, $("labNo"));
   // }
}

function processScanSuccess(xhr) {
    //alert(xhr.responseText);
    var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
    var returnedData = formField.firstChild.nodeValue;

    var message = xhr.responseXML.getElementsByTagName("message").item(0);

    var success = message.firstChild.nodeValue == "valid";

    if (success) {
        $("labNo").value = returnedData;

    } else {
        alert("<%= StringUtil.getMessageForKey("error.accession.no.next") %>");
        $("labNo").value = "";
    }

    selectFieldErrorDisplay(success, $("labNo"));
    setValidIndicaterOnField(success, "labNo");

}

function processAccessionSuccess(xhr) {
    //alert(xhr.responseText);
    var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
    var message = xhr.responseXML.getElementsByTagName("message").item(0);
    var success = false;

    if (message.firstChild.nodeValue == "valid") {
        success = true;
        document.getElementById("next").disabled = false;
    }
    var labElement = formField.firstChild.nodeValue;
    selectFieldErrorDisplay(success, $(labElement));

    if (!success) {
        alert(message.firstChild.nodeValue);
    }
}
</script>

<bean:define id="formName"      value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>' />

<div id="hidden-fields">
<html:hidden name='<%=formName %>'
	property="currentDate"/>
<html:hidden name='<%=formName %>'
	property="currentTime"/>
<html:hidden name='<%=formName %>'
	property="sampleOrderItems.receivedDateForDisplay"/>
<html:hidden name='<%=formName %>'
	property="sampleOrderItems.receivedTime"/>
</div>

<div>
<table>
	<tr>
		<td>
			<bean:message key="sample.batchentry.preprinted.labno" />:
		</td>
	</tr>
	<tr>
		<td>
			<app:text name="<%=formName%>" property="sampleOrderItems.labNo"
                      maxlength='<%= Integer.toString(accessionNumberValidator.getMaxAccessionLength())%>'
                      onchange="checkAccessionNumber(this);"
                      styleClass="text"
                      styleId="labNo"/>
			<html:button onclick="nextLabel();"
				property="next"
				styleId="next" 
				disabled="true">
			<bean:message key="sample.batchentry.preprinted.next" />
			</html:button>
			<html:button onclick="reenterLabel();"
				property="reenter"
				styleId="reenter" >
			<bean:message key="sample.batchentry.preprinted.reenter" />
			</html:button>
			<html:button onclick="doneLabelling();"
				property="done"
				styleId="done" >
			<bean:message key="sample.batchentry.preprinted.done" />
			</html:button>
		</td>
	</tr>
	<tr>
		<td> <br></td>
	</tr>
	<tr>
	<td>
	<table>
		<tr>
			<td>
				<bean:message key="sample.batchentry.preprinted.summary" />:
			</td>
		</tr>
		<tr>
			<td>
				<textarea name="previous" rows="10" cols="50"></textarea>
			</td>
		</tr>
	</table>
	</td>
		
	</tr>		
</table>
</div>
