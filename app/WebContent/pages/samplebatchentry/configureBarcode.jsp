<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="us.mn.state.health.lims.common.action.IActionConstants,
                 us.mn.state.health.lims.common.formfields.FormFields,
                 us.mn.state.health.lims.common.formfields.FormFields.Field" %>
<%@ taglib uri="/tags/struts-bean"      prefix="bean" %>
<%@ taglib uri="/tags/struts-html"      prefix="html" %>

<bean:define id="formName" value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>' />


<script type="text/javascript" >

function togglePatientInfo() {
	if (document.getElementsByName('patientInfoCheck')[0].disabled == false) {
		document.getElementsByName('patientInfoCheck')[0].disabled = true;
	} else {
		document.getElementsByName('patientInfoCheck')[0].disabled = false;
	}
}

function checkFacilityID() {
	document.getElementsByName('facilityIDCheck')[0].disabled = false;
}
function uncheckFacilityID() {
	document.getElementsByName('facilityIDCheck')[0].disabled = true;
}
function toggleFacilityID() {
	if (document.getElementsByName('facilityIDCheck')[0].disabled == false) {
		uncheckFacilityID();
	} else {
		checkFacilityID();
	}
}
function processFacilityIDChange() {
	if (document.getElementsByName('facilityID')[0].value != '') {
		checkFacilityID();
		document.getElementById('psuedoFacilityID').checked = true;
		document.getElementById('psuedoFacilityID').disabled = true;
	} else {
		document.getElementById('psuedoFacilityID').disabled = false;
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
		property="method">
	<option value="On Demand"><bean:message key="sample.batchentry.barcode.ondemand"/></option>
	<option value="Pre-Printed"><bean:message key="sample.batchentry.barcode.preprinted"/></option>
</html:select>

<table style="width:100%">
<tr>
<td>
<table>
	<tr>
 		<td>
			<bean:message key="sample.batchentry.barcode.label.options"/>
		</td>
		<td>
			<input type="checkbox"
			id="psuedoFacilityID"
			onchange="toggleFacilityID();">
			<html:hidden name="<%=formName %>"
				property="facilityIDCheck"
				disabled="true"
				value="true" />
			<bean:message key="sample.batchentry.barcode.label.facilityid"/>
		</td>
		<td>
			<bean:message key="sample.batchentry.barcode.label.facilityid"/>:
		</td>
		<td>
			<html:text name="<%=formName %>"
				property="facilityID"
				onkeyup="processFacilityIDChange();" />
		</td>
	</tr>
	<tr>
		<td></td>
		<td>
			<input type="checkbox"
			id="psuedoPatientInfo"
			onchange="togglePatientInfo()"
			/>
			<html:hidden name="<%=formName %>"
				property="patientInfoCheck"
				disabled="true"
				value="true" />
			<bean:message key="sample.batchentry.barcode.label.patientinfo"/>
		</td>
	</tr>
</table>
</td>
</tr>
</table>
