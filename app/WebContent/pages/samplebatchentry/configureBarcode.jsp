<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="us.mn.state.health.lims.common.action.IActionConstants" %>
<%@ taglib uri="/tags/struts-bean"      prefix="bean" %>
<%@ taglib uri="/tags/struts-html"      prefix="html" %>

<bean:define id="formName" value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>' />


<script type="text/javascript" >

function setBarcodeMethod(method) {
	if (method === "On Demand")	{
		$("onDemand").show();
		$("prePrinted").hide();
	} else if (method === "Pre-Printed") {
		$("prePrinted").show();
		$("onDemand").hide();
	}
	checkValidSubPages();
}

//must use last name if first name is checked
function toggleNameLock(firstName) {
	if (firstName.checked == true) {
		document.getElementById('psuedoLastName').disabled = true;
		document.getElementById('psuedoLastName').checked = true;
		checkLastName();
	} else {
		document.getElementById('psuedoLastName').disabled = false;
	}
}

//functions for using hidden lastName field
function checkLastName() {
	document.getElementsByName('lastNameCheck')[0].disabled = false;
}

function uncheckLastName() {
	document.getElementsByName('lastNameCheck')[0].disabled = true;
}

function toggleLastName() {
	if (document.getElementsByName('lastNameCheck')[0].disabled == false) {
		uncheckLastName();
	} else {
		checkLastName();
	}
}

//validation logic for this 'page'
function configBarcodeValid() {
	if (document.getElementsByName('method')[0].value == 'On Demand') {
		return true;
	} else if (document.getElementsByName('method')[0].value == 'Pre-Printed') {
		 return true;
	} else {
		return false;
	}
}

</script>

Barcode Method : 
<html:select name="<%=formName%>"
		property="method"
 		onchange="setBarcodeMethod(this.value)" >
	<option value="On Demand"><bean:message key="sample.batchentry.barcode.ondemand"/></option>
	<option value="Pre-Printed"><bean:message key="sample.batchentry.barcode.preprinted"/></option>
</html:select>

<div id=onDemand>
<table style="width:100%">
<tr>
<td>
<table>
	<tr>
		<td style="width:35%;">
			<bean:message key="sample.batchentry.barcode.label.number"/> :
		</td>
		<td>
			<html:text name="<%=formName %>"
				property="numberLabels"/>
		</td>
	</tr>
	<tr>
		<td>
			<bean:message key="sample.batchentry.barcode.label.options"/>
		</td>
		<td>
			<html:checkbox name="<%=formName %>"
				property="facilityIDCheck"
				value="true" />
			<bean:message key="sample.batchentry.barcode.label.facilityid"/>
		</td>
		<td>
						<bean:message key="sample.batchentry.barcode.label.facilityid"/>:
		</td>
		<td>
			<html:text name="<%=formName %>"
				property="facilityID" />
		</td>
	</tr>
	<tr>
		<td></td>
		<td>
			<input type="checkbox" 
			id="psuedoLastName"
				onclick="toggleLastName();"> 
			
			<html:hidden name="<%=formName %>"
				property="lastNameCheck"
				disabled="true" 
				value="true"/>
				<bean:message key="sample.batchentry.barcode.label.lastname"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td>
			<html:checkbox name="<%=formName %>"
				property="firstNameCheck"
				onchange="toggleNameLock(this);"
				value="true" />
			<bean:message key="sample.batchentry.barcode.label.firstname"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td>
			<html:checkbox name="<%=formName %>"
				property="patientNumberCheck"
				value="true" />
			<bean:message key="sample.batchentry.barcode.label.patientno"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td>
			<html:checkbox name="<%=formName %>"
				property="nationalIDCheck"
				value="true" />
			<bean:message key="sample.batchentry.barcode.label.nationalid"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td>
			<html:checkbox name="<%=formName %>"
				property="subjectNoCheck"
				value="true" />
			<bean:message key="sample.batchentry.barcode.label.subjectno"/>
		</td>
	</tr>
</table>
</td>
</tr>
</table>
</div>

<div id=prePrinted style="display: none; ">
			
</div>
