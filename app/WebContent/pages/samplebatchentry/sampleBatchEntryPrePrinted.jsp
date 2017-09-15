<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="us.mn.state.health.lims.common.action.IActionConstants,
                 us.mn.state.health.lims.common.util.Versioning,
                 us.mn.state.health.lims.common.util.StringUtil" %>
<%@ taglib uri="/tags/struts-bean"      prefix="bean" %>
<%@ taglib uri="/tags/struts-html"      prefix="html" %>

<%!
String path = "";
String basePath = "";
%>

<%
path = request.getContextPath();
basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>

<script type="text/javascript" src="scripts/utilities.js"></script>
<script type="text/javascript" src="scripts/ajaxCalls.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript">
function nextLabel() {
	
}

function reenterLabel() {
	
}

function doneLabelling() {
	
}

function checkAccessionNumber(accessionNumber) {
    //check if empty
    if (!fieldIsEmptyById("labNo")) {
        validateAccessionNumberOnServer(false, false, accessionNumber.id, accessionNumber.value, processAccessionSuccess, null);
    }
    else {
         selectFieldErrorDisplay(false, $("labNo"));
    }
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
    }
    var labElement = formField.firstChild.nodeValue;
    selectFieldErrorDisplay(success, $(labElement));

    if (!success) {
        alert(message.firstChild.nodeValue);
    }
}
</script>

<bean:define id="formName"      value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>' />
<div>
<table>
	<tr>
		<td>
			<bean:message key="sample.batchentry.preprinted.labno" />:
		</td>
	</tr>
	<tr>
		<td>
			<html:text name="<%=formName%>" 
				property="labNo" >
			</html:text>
			<html:button onclick="nextLabel();"
				property="next">
			<bean:message key="sample.batchentry.preprinted.next" />
			</html:button>
			<html:button onclick="reenterLabel();"
				property="reenter">
			<bean:message key="sample.batchentry.preprinted.reenter" />
			</html:button>
			<html:button onclick="doneLabelling();"
				property="done">
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
