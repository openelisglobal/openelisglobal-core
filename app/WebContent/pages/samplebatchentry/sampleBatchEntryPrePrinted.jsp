<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="us.mn.state.health.lims.common.action.IActionConstants,
                 us.mn.state.health.lims.common.util.Versioning,
                 us.mn.state.health.lims.common.provider.validation.AccessionNumberValidatorFactory,
                 us.mn.state.health.lims.common.provider.validation.IAccessionNumberValidator,
                 us.mn.state.health.lims.common.util.StringUtil" %>
<%@ taglib uri="/tags/struts-bean"      prefix="bean" %>
<%@ taglib uri="/tags/struts-html"      prefix="html" %>
<%@ taglib uri="/tags/labdev-view" 		prefix="app" %>
<%@ taglib uri="/tags/struts-tiles"     prefix="tiles" %>

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
var lineSeparator = "";

function nextLabel() {
	postBatchSample(alertSuccess, defaultFailure);
	var recentTextArea = document.getElementById("previous");
	var newRecent = document.getElementById("labNo").value + lineSeparator + recentTextArea.value;
	lineSeparator = "\n";
	if ((newRecent.match(/\n/g)||[]).length >= 3) {
		newRecent = newRecent.slice(0,newRecent.lastIndexOf("\n"));
	}
	recentTextArea.value = newRecent;
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
<table width="100%">
<tr>
<td width="50%">
<table>
	<tr>
		<td>
			<h2> Sample Specific Fields</h2>
		</td>
	</tr>
	<tr>
		<td>
			<tiles:insert attribute="patientInfo" />
		</td>
	</tr>	
	<tr>
		<td>
			<h2>Accession Entry</h2>
		</td>
	</tr>	
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
				<textarea  id="previous" rows="10" cols="50"></textarea>
			</td>
		</tr>
	</table>
	</td>
		
	</tr>		
</table>
</td>
<td width="50%" style="vertical-align:top">
<table id="summary">
	<tr>
		<td>
			<h2> Common Fields</h2>
		</td>
	</tr>
	<tr>
		<td>
			<bean:message key="sample.batchentry.order.currentdate" />: 
			<html:text name='<%=formName %>'
				property="currentDate"
				readonly="true"></html:text>
		</td>
		<td>
			<bean:message key="sample.batchentry.order.currenttime" />: 
			<html:text name='<%=formName %>'
				property="currentTime"
				readonly="true"></html:text>
		</td>
	</tr>
	<tr>
		<td>
			<bean:message key="sample.batchentry.datereceived" />:
			<html:text name='<%=formName %>'
				property="sampleOrderItems.receivedDateForDisplay"
				readonly="true"></html:text>
		</td>
		<td>
			<bean:message key="sample.batchentry.timereceived" />:
			<html:text name='<%=formName %>'
				property="sampleOrderItems.receivedTime"
				readonly="true"></html:text>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<table style="width:100%">
				<tr>
					<th><bean:message key="sample.entry.sample.type"/></th>
					<th><bean:message key="test.testName"/></th>
				</tr>
				<tr>
					<td><%= request.getAttribute("sampleType") %></td>
					<td><%= request.getAttribute("testNames") %></td>
				</tr>
			</table>
			<html:hidden name='<%=formName %>'
				property="sampleXML"/>	
		</td>
	</tr>
</table>
</td>
</tr>
</table>
<a href="" id="getBarcodePDF" target="_blank"></a>
</div>
