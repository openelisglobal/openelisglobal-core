<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="us.mn.state.health.lims.common.action.IActionConstants,
                 us.mn.state.health.lims.common.util.Versioning,
                 us.mn.state.health.lims.common.util.StringUtil,
                 us.mn.state.health.lims.sample.bean.SampleOrderItem" %>
<%@ taglib uri="/tags/struts-bean"      prefix="bean" %>
<%@ taglib uri="/tags/struts-html"      prefix="html" %>
<%@ taglib uri="/tags/struts-logic"     prefix="logic" %>
<%@ taglib uri="/tags/labdev-view"      prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<bean:define id="formName"      value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>' />
<bean:parameter id="firstNameCheck" name="firstNameCheck" value="false" />
<bean:parameter id="lastNameCheck" name="lastNameCheck" value="false" />
<bean:parameter id="facilityIDCheck" name="facilityIDCheck" value="false" />
<bean:parameter id="facilityID" name="facilityID" value="" />
<bean:parameter id="patientNumberCheck" name="patientNumberCheck" value="false" />
<bean:parameter id="nationalIDCheck" name="nationalIDCheck" value="false" />
<bean:parameter id="subjectNoCheck" name="subjectNoCheck" value="false" />
<bean:parameter id="sampleXML" name="sampleXML" value="none"/>

<script type="text/javascript" src="scripts/utilities.js"></script>
<script type="text/javascript" src="scripts/ajaxCalls.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script>
var lineSeparator = "";

//Adds warning when leaving page
window.onbeforeunload = formWarning;
function formWarning(){ 
	//firefox overwrites any message that is put as a page closing message
	return "Are you sure you want to leave this page?";
}

function printLabel() {
	document.getElementById("printButtonId").disabled = true;
	document.getElementById("reprintButtonId").disabled = false;
	document.getElementById("nextButtonId").disabled = false;
}

function nextLabel() {
	document.getElementById("printButtonId").disabled = false;
	document.getElementById("reprintButtonId").disabled = true;
	document.getElementById("nextButtonId").disabled = true;
	//move current accession number into the summary space (max 3)
	var recentTextArea = document.getElementById("recentSummary");
	var newRecent = document.getElementById("labNo").value + lineSeparator + recentTextArea.value;
	lineSeparator = "\n";
	if ((newRecent.match(/\n/g)||[]).length >= 3) {
		newRecent = newRecent.slice(0,newRecent.lastIndexOf("\n"));
	}
	recentTextArea.value = newRecent;
}

//functions for generating and checking accession number
function getNextAccessionNumber() {
    generateNextScanNumber(processScanSuccess, defaultFailure);
}

function checkAccessionNumber(accessionNumber) {
    //check if empty
    if (!fieldIsEmptyById("labNo")) {
        validateAccessionNumberOnServer(false, false, accessionNumber.id, accessionNumber.value, processAccessionSuccess, null);
    } else {
         selectFieldErrorDisplay(false, $("labNo"));
    }
}

//function that processes return from getNextAccessionNumber (calls submit and print)
function processScanSuccess(xhr) {
    //alert(xhr.responseText);
    var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
    var returnedData = formField.firstChild.nodeValue;
    var message = xhr.responseXML.getElementsByTagName("message").item(0);
    var success = message.firstChild.nodeValue == "valid";
    if (success) {
        document.getElementById("labNo").value = returnedData;
        postBatchSample(alertSuccess, defaultFailure);
        printLabel();
    } else {
        alert("<%= StringUtil.getMessageForKey("error.accession.no.next") %>");
        document.getElementById("labNo").value = "";
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

function alertSuccess(){
	//alert("success");
}

function finish() {
    window.location = "SampleBatchEntrySetup.do";
}
</script>

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
			<hr>
		</td>
	</tr>
	<tr>
		<td> 
			<bean:message key="sample.batchentry.ondemand.fields" />
			:
		</td>
	</tr>
<logic:equal name="facilityIDCheck" value="true">
	<logic:notEqual name="facilityID" value="">
		<tr>
			<td>
				<bean:message key="sample.batchentry.barcode.label.facilityid" /> 
				: <html:text name="<%=formName %>"
					property="facilityID" 
					readonly="true"/>
			</td>
		</tr>		
	</logic:notEqual>		
	<logic:equal name="facilityID" value="">
		<tr>
			<td>
				<bean:message key="sample.batchentry.barcode.label.facilityid" /> 
				: <html:text name="<%=formName %>"
					property="facilityID"/>
			</td>
		</tr>
	</logic:equal>
</logic:equal>
<logic:equal name="firstNameCheck" value="true">
	<tr>
		<td>
			<bean:message key="sample.batchentry.barcode.label.firstname" /> 
			: <html:text name="<%=formName %>"
				property="firstName" />
		</td>
	</tr>
</logic:equal>
<logic:equal name="lastNameCheck" value="true">
	<tr>
		<td>
			<bean:message key="sample.batchentry.barcode.label.lastname" /> 
			: <html:text name="<%=formName %>"
				property="lastName" />
		</td>
	</tr>
</logic:equal>
<logic:equal name="patientNumberCheck" value="true">
	<tr>
		<td>
			<bean:message key="sample.batchentry.barcode.label.patientno" /> 
			: <html:text name="<%=formName %>"
				property="patientNumber" />
		</td>
	</tr>
</logic:equal>
<logic:equal name="nationalIDCheck" value="true">
	<tr>
		<td>
			<bean:message key="sample.batchentry.barcode.label.nationalid" /> 
			: <html:text name="<%=formName %>"
				property="nationalID" />
		</td>
	</tr>
</logic:equal>
<logic:equal name="subjectNoCheck" value="true">
	<tr>
		<td>
			<bean:message key="sample.batchentry.barcode.label.subjectno" /> 
			: <html:text name="<%=formName %>"
				property="subjectNo" />
		</td>
	</tr>
</logic:equal>
	<tr>
		<td>
			<hr>
		</td>
	</tr>
	<tr>
		<td>
			<!-- gets next accession, and calls submit and print if success -->
			<html:button onclick="getNextAccessionNumber();"
				property="print"
				styleId="printButtonId">
				<bean:message key="sample.batchentry.ondemand.saveprint" />
			</html:button>
			<!-- just prints last label -->
			<html:button onclick="printLabel();"
				property="reprint"
				styleId="reprintButtonId"
				disabled="true">
				<bean:message key="sample.batchentry.ondemand.reprint" />
			</html:button>
			<!-- sets up for next label to be printed -->
			<html:button onclick="nextLabel();"
				property="next"
				styleId="nextButtonId"
				disabled="true">
				<bean:message key="sample.batchentry.ondemand.next" />
			</html:button>
		</td>
	</tr>
	<tr>
		<td> <br></td>
	</tr>
		<tr>
			<td>
				<bean:message key="sample.batchentry.ondemand.current" />:
			</td>
		</tr>
	<tr>
		<td>
			<app:text name="<%=formName%>" property="labNo"
            	onchange="checkAccessionNumber(this);"
                styleClass="text"
                styleId="labNo"
                readonly="true"/>
		</td>
	</tr>
	<tr>
	<td>
	<table>
		<tr>
			<td>
				<bean:message key="sample.batchentry.ondemand.previous" />:
			</td>
		</tr>
		<tr>
			<td>
				<textarea name="recentSummary" 
					id="recentSummary" 
					rows="5" 
					cols="50"
					readonly="true"></textarea>
			</td>
		</tr>
	</table>
	</td>
		
	</tr>		
</table>
</td>
<td width="50%" style="vertical-align:top">
<table>
	<tr>
		<td>
			<textarea rows="5" 
				cols="50"
				readonly="true"><%=sampleXML%></textarea>
		</td>
	</tr>
</table>
</td>
</tr>
</table>
</div>
