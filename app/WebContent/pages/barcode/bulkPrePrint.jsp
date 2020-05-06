<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="us.mn.state.health.lims.common.action.IActionConstants,
			     us.mn.state.health.lims.common.formfields.FormFields,
			     us.mn.state.health.lims.common.formfields.FormFields.Field,
			     us.mn.state.health.lims.common.provider.validation.AccessionNumberValidatorFactory,
			     us.mn.state.health.lims.common.provider.validation.IAccessionNumberValidator,
			     us.mn.state.health.lims.common.util.ConfigurationProperties.Property,
			     us.mn.state.health.lims.common.util.StringUtil,
			     us.mn.state.health.lims.common.util.*" %>


<%@ taglib uri="/tags/struts-bean"		prefix="bean" %>
<%@ taglib uri="/tags/struts-html"		prefix="html" %>
<%@ taglib uri="/tags/struts-logic"		prefix="logic" %>
<%@ taglib uri="/tags/labdev-view"		prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>
<%@ taglib uri="/tags/struts-tiles"     prefix="tiles" %>
<%@ taglib prefix="nested" uri="http://jakarta.apache.org/struts/tags-nested" %>

<bean:define id="formName"	value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>' />

<link rel="stylesheet" href="css/jquery_ui/jquery.ui.all.css?ver=<%= Versioning.getBuildNumber() %>">
<link rel="stylesheet" href="css/customAutocomplete.css?ver=<%= Versioning.getBuildNumber() %>">

<script src="scripts/ui/jquery.ui.core.js"></script>
<script src="scripts/ui/jquery.ui.widget.js"></script>
<script src="scripts/ui/jquery.ui.button.js"></script>
<script src="scripts/ui/jquery.ui.menu.js"></script>
<script src="scripts/ui/jquery.ui.position.js"></script>
<script src="scripts/ui/jquery.ui.autocomplete.js"></script>
<script src="scripts/customAutocomplete.js"></script>
<script type="text/javascript" src="scripts/ajaxCalls.js"></script>
<script>
function calculateTotal() {
	var numSetsOfLabels = document.getElementById('numSetsOfLabels').value;
	
	var numOrderLabelsPerSet = document.getElementById('numOrderLabelsPerSet').value;
	var numSpecimenLabelsPerSet = document.getElementById('numSpecimenLabelsPerSet').value;
	
	var numTotalOrderLabels = numSetsOfLabels * numOrderLabelsPerSet;
	var numTotalSpecimenLabels = numSetsOfLabels * numSpecimenLabelsPerSet;
	var numTotalLabels = numTotalOrderLabels + numTotalSpecimenLabels ;

// 	document.getElementById('numTotalOrderLabels').value = numTotalOrderLabels;
// 	document.getElementById('numTotalSpecimenLabels').value = numTotalSpecimenLabels;
	document.getElementById('numTotalLabels').value = numTotalLabels;
}

function preprintLabels() {
	var numSetsOfLabels = document.getElementById('numSetsOfLabels').value;
	
	var numOrderLabelsPerSet = document.getElementById('numOrderLabelsPerSet').value;
	var numSpecimenLabelsPerSet = document.getElementById('numSpecimenLabelsPerSet').value;
	
	var testIds = getTestIds();
	
	if (testIds.trim() == '') {
		alert("missing tests");
		return;
	}
	//label info
	var queryString = 'prePrinting=true&numSetsOfLabels=' + numSetsOfLabels 
		+ '&numOrderLabelsPerSet=' + numOrderLabelsPerSet
		+ '&numSpecimenLabelsPerSet=' + numSpecimenLabelsPerSet;
	//facility name
	queryString = queryString + '&facilityName=' + document.getElementById('requesterId').options[document.getElementById('requesterId').selectedIndex].text;
	//test names
	queryString = queryString + '&testIds=' + testIds;
	
	
    document.getElementById("ifpreprintbarcode").src = 'LabelMakerServlet?' + queryString;
	document.getElementById("prePrintedBarcodeArea").show();
}

function getTestIds() {
	if (document.getElementById('testIds_1')) {
		return document.getElementById('testIds_1').value;
	} else {
		return "";
	}
}

$jq(document).ready(function () {
	calculateTotal()
	
    var dropdown = $jq("select#requesterId");
    autoCompleteWidth = dropdown.width() + 66 + 'px';
    clearNonMatching = true;
    capitialize = true;
    // Actually executes autocomplete
    dropdown.combobox();
    invalidLabID = '<bean:message key="error.site.invalid"/>'; // Alert if value is typed that's not on list. FIX - add bad message icon
    maxRepMsg = '<bean:message key="sample.entry.project.siteMaxMsg"/>';

    resultCallBack = function (textValue) {
        siteListChanged(textValue);
    	processFacilityIDChange();
       // setOrderModified();
        //setCorrectSave();
    };
});

</script>

<div>
<h2>Pre-Print Barcodes</h2>
<table>
	<tr>
		<td>Number of label sets:</td>
		<td><input id="numSetsOfLabels" name="numSetsOfLabels" type="number" value="1" size ="2" onchange="calculateTotal()"/></td>
	</tr>
	<tr>
		<td>Number of order labels per set:</td>
		<td><input id="numOrderLabelsPerSet" name="numOrderLabelsPerSet" type="number" value="2" size ="1" onchange="calculateTotal()"/></td>
		<td>Number of specimen labels per set:</td>
		<td><input id="numSpecimenLabelsPerSet" name="numSpecimenLabelsPerSet" type="number" value="1" size="1" onchange="calculateTotal()"/></td>
	</tr>
	<tr></tr>
<!-- 	<tr> -->
<!-- 		<td>Total Order Labels to Print:</td> -->
<!-- 		<td><input id="numTotalOrderLabels" type="number" value="1" size="2" readonly/></td> -->
<!-- 	</tr> -->
<!-- 	<tr> -->
<!-- 		<td>Total Specimen Labels to Print:</td> -->
<!-- 		<td><input id="numTotalSpecimenLabels" type="number" value="1" size="2" readonly/></td> -->
<!-- 	</tr> -->
	<tr>
		<td>Total Labels to Print:</td>
		<td><input id="numTotalLabels" type="number" value="2" size="2" readonly/></td>
	</tr>
</table>
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
		<td><div id="facility-combobox">
			<logic:equal value="false" name='<%=formName%>' property="sampleOrderItems.readOnly" >
		        <html:select styleId="requesterId"
		                     name="<%=formName%>"
		                     property="facilityID"
		                     onchange="siteListChanged(this);processFacilityIDChange();"
		                     onkeyup="capitalizeValue( this.value );"
		                     
		                >
		            <option value=""></option>
		            <html:optionsCollection name="<%=formName%>" property="sampleOrderItems.referringSiteList" label="value"
		                                    value="id"/>
		        </html:select>
			</logic:equal>
		    <logic:equal value="true" name='<%=formName%>' property="sampleOrderItems.readOnly" >
		            <html:text styleId="requesterId" property="facilityID" name="<%=formName%>" style="width:300px" />
		    </logic:equal>
		</div></td>
	</tr>
</table>
</div>
<div>
<table width="75%">
	<tr>
		<td><h2>Sample</h2></td>
	</tr>
	<tr>
		<td><tiles:insert attribute="sampleAdd"/></td>
	</tr>
	<tr>
		<td>NOTE: If a facility and/or sample and test are added, they will be printed on EVERY label</td>
	</tr>
</table>
</div>
<div>
<button type="button" onclick="preprintLabels()">Pre-Print Labels</button>
</div>
<div style="display:none;" id="prePrintedBarcodeArea">
		<h2><bean:message key="barcode.common.section.barcode.header"/></h2>
		<iframe  src="about:blank" id="ifpreprintbarcode" width="75%" height="300px"></iframe>
</div>
