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
<%@ taglib prefix="nested" uri="http://jakarta.apache.org/struts/tags-nested" %>

<bean:define id="formName"	value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>' />
<bean:define id="localDBOnly" value='<%=Boolean.toString(ConfigurationProperties.getInstance().getPropertyValueLowerCase(Property.UseExternalPatientInfo).equals("false"))%>' />
<bean:define id="patientSearch" name='<%=formName%>' property='patientSearch' type="us.mn.state.health.lims.patient.action.bean.PatientSearch" />

<%!
	IAccessionNumberValidator accessionNumberValidator;
	boolean supportSTNumber = true;
	boolean supportMothersName = true;
	boolean supportSubjectNumber = true;
	boolean supportNationalID = true;
	boolean supportLabNumber = false;
	String basePath = "";
 %>

 <%
 	supportSTNumber = FormFields.getInstance().useField(Field.StNumber);
  	supportMothersName = FormFields.getInstance().useField(Field.MothersName);
  	supportSubjectNumber = FormFields.getInstance().useField(Field.SubjectNumber);
  	supportNationalID = FormFields.getInstance().useField(Field.NationalID);
  	supportLabNumber = FormFields.getInstance().useField(Field.SEARCH_PATIENT_WITH_LAB_NO);
 	accessionNumberValidator = new AccessionNumberValidatorFactory().getValidator();
 	String path = request.getContextPath();
 	basePath = request.getScheme() + "://" + request.getServerName() + ":"	+ request.getServerPort() + path + "/";
 %>

<script type="text/javascript" src="<%=basePath%>scripts/utilities.js?ver=<%= Versioning.getBuildNumber() %>" ></script>
<script type="text/javascript" src="<%=basePath%>scripts/ajaxCalls.js?ver=<%= Versioning.getBuildNumber() %>" ></script>
<script type="text/javascript">
var validator = new FieldValidator();
validator.setRequiredFields( new Array("quantity") );

var supportSTNumber = <%=supportSTNumber%>;
var supportMothersName = <%=supportMothersName%>;
var supportSubjectNumber = <%=supportSubjectNumber%>;
var supportNationalID = <%=supportNationalID%>;
var supportLabNumber = <%=supportLabNumber%>;
var patientSelectID;
var patientInfoHash = [];
var patientChangeListeners = [];
var localDB = '<%=localDBOnly%>' == "true";
var newSearchInfo = false;


function checkFieldInt(field) {
	if (isNaN(field.value) || field.value.indexOf(".") > -1) {
		validator.setFieldValidity(false, field.id);
		selectFieldErrorDisplay(false, field);
		alert("<bean:message key='siteInfo.number.nonnumber'/>");
	} else if (parseInt(field.value) <= 0) {
		validator.setFieldValidity(false, field.id);
		selectFieldErrorDisplay(false, field);
		alert("<bean:message key='siteInfo.number.invalidnumber'/>");
	} else {
		validator.setFieldValidity(true, field.id);
		selectFieldErrorDisplay(true, field);
	}
	if (validator.isAllValid()) {
		enablePrint();
	} else {
		disablePrint();
	}
}

//hardcoded to enable the order print as it is only field
function enablePrint() {
	document.getElementById("orderPrintButton").disabled = false;
}

//hardcoded to disable the order print as it is only field
function disablePrint() {
	document.getElementById("orderPrintButton").disabled = true;
}

//search patients using labNo
function searchPatients() {
    var criteria = $jq("#searchCriteria").val();
    var labNumber = $jq("#searchValue").val();
    var lastName = "";
    var firstName = "";
    var STNumber = "";
    var subjectNumber = "";
    var nationalID = "";

	newSearchInfo = false;
    $jq("#resultsDiv").hide();
    $jq("#barcodeArea").hide();
    $jq("#searchLabNumber").val('');
    $jq("#searchLabNumber").val(labNumber);

	patientSearch(lastName, firstName, STNumber, subjectNumber, nationalID, labNumber, "", false, processSearchSuccess);

}

function processSearchSuccess(xhr) {
	//alert( xhr.responseText );
	var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
	var message = xhr.responseXML.getElementsByTagName("message").item(0);
	var table = $("searchResultTable");

	clearTable(table);
	clearPatientInfoCache();

	if( message.firstChild.nodeValue == "valid" ) {
		$("noPatientFound").hide();
		$("searchResultsDiv").show();
		var resultNodes = formField.getElementsByTagName("result");
		for( var i = 0; i < resultNodes.length; i++ ) {
			addPatientToSearch( table, resultNodes.item(i) );
		}
		if( resultNodes.length == 1 && <%=String.valueOf(patientSearch.isLoadFromServerWithPatient())%>  ){
			handleSelectedPatient();
		}
	} else {
		$("searchResultsDiv").hide();
		$("noPatientFound").show();
		selectPatient( null );
	}
}

function clearSearchResultTable() {
	var table = $("searchResultTable");
	clearTable(table);
	clearPatientInfoCache();
}

function clearTable(table){
	var rows = table.rows.length - 1;
	while( rows > 0 ){
		table.deleteRow( rows-- );
	}
}

function clearPatientInfoCache(){
	patientInfoHash = [];
}

function addPatientToSearch(table, result ){
	var patient = result.getElementsByTagName("patient")[0];
	var firstName = getValueFromXmlElement( patient, "first");
	var lastName = getValueFromXmlElement( patient, "last");
	var gender = getValueFromXmlElement( patient, "gender");
	var DOB = getValueFromXmlElement( patient, "dob");
	var stNumber = getValueFromXmlElement( patient, "ST");
	var subjectNumber = getValueFromXmlElement( patient, "subjectNumber");
	var nationalID = getValueFromXmlElement( patient, "nationalID");
	var mother = getValueFromXmlElement( patient, "mother");
	var pk = getValueFromXmlElement( result, "id");
	var dataSourceName = getValueFromXmlElement( result, "dataSourceName");

	var row = createRow( table, firstName, lastName, gender, DOB, stNumber, subjectNumber, nationalID, mother, pk, dataSourceName );
	addToPatientInfo( firstName, lastName, gender, DOB, stNumber, subjectNumber, nationalID, mother, pk );
	if( row == 1 ){
		patientSelectID = pk;
		$("sel_1").checked = "true";
		selectPatient( pk );
	}
}

function getValueFromXmlElement( parent, tag ){
	var element = parent.getElementsByTagName( tag ).item(0);
	return element ? element.firstChild.nodeValue : "";
}

function createRow(table, firstName, lastName, gender, DOB, stNumber, subjectNumber, nationalID, mother, pk,  dataSourceName){
		var row = table.rows.length;
		var newRow = table.insertRow(row);
		newRow.id = "_" + row;
		var cellCounter = -1;
		var selectionCell = newRow.insertCell(++cellCounter);
		if( !localDB){
			var dataSourceCell = newRow.insertCell(++cellCounter);
			dataSourceCell.innerHTML = nonNullString( dataSourceName );
		}
		var lastNameCell = newRow.insertCell(++cellCounter);
		var firstNameCell = newRow.insertCell(++cellCounter);
		var genderCell = newRow.insertCell(++cellCounter);
		var dobCell = newRow.insertCell(++cellCounter);
		var motherCell = supportMothersName ? newRow.insertCell(++cellCounter) : null;
		var stCell = supportSTNumber ? newRow.insertCell(++cellCounter) : null;
		var subjectNumberCell = supportSubjectNumber ? newRow.insertCell(++cellCounter) : null;
		var nationalCell = supportNationalID ? newRow.insertCell(++cellCounter) : null;
		selectionCell.innerHTML = getSelectionHtml( row, pk );
		lastNameCell.innerHTML = nonNullString( lastName );
		firstNameCell.innerHTML = nonNullString( firstName );
		genderCell.innerHTML = nonNullString( gender );
		if( supportSTNumber){stCell.innerHTML = nonNullString( stNumber );}
		if( supportSubjectNumber){subjectNumberCell.innerHTML = nonNullString( subjectNumber );}
		if( supportNationalID){nationalCell.innerHTML = nonNullString( nationalID );}

		dobCell.innerHTML = nonNullString( DOB );
		if(supportMothersName){motherCell.innerHTML = nonNullString( mother );}

		return row;
}

function getSelectionHtml( row, key){
	return "<input name='selPatient' id='sel_" + row + "' value='" + key + "' onclick='selectPatient(this.value)' type='radio'>";
}

function /*String*/ nonNullString( target ){
	return target == "null" ? "" : target;
}

function addToPatientInfo( firstName, lastName, gender, DOB, stNumber, subjectNumber, nationalID, mother, pk ){
	var info = [];
	info["first"] = nonNullString( firstName );
	info["last"] = nonNullString( lastName );
	info["gender"] = nonNullString( gender );
	info["DOB"] = nonNullString( DOB );
	info["ST"] = nonNullString( stNumber );
	info["subjectNumber"] = nonNullString( subjectNumber );
	info["national"] = nonNullString( nationalID );
	info["mother"] = nonNullString( mother );

	patientInfoHash[pk] = info;
}


function selectPatient( patientID ){
    var i;
	if( patientID ){
		patientSelectID = patientID;
		var info = patientInfoHash[patientID];
		for(i = 0; i < patientChangeListeners.length; i++){
			patientChangeListeners[i](info["first"],info["last"],info["gender"],info["DOB"],info["ST"],info["subjectNumber"],info["national"],info["mother"], patientID);
		}
	} else {
		for(i = 0; i < patientChangeListeners.length; i++){
			patientChangeListeners[i]("","","","","","","","", null);
		}
	}
}

function /*void*/ addPatientChangedListener( listener ){
	patientChangeListeners.push( listener );
}


function /*void*/ handleEnterEvent( ){
		if( newSearchInfo ){
			searchPatients();
		}
		return false;
}

function /*void*/ dirtySearchInfo(e){ 
	var code = e ? e.which : window.event.keyCode;
	if( code != 13 ){
		newSearchInfo = true; 
	}
}

function /*void*/ doNothing(){ 
}

function enableSearchButton(eventCode){
    var valueElem = $jq("#searchValue");
    var criteriaElem  = $jq('#searchCriteria');
    var searchButton = $jq("#searchButton");
    if( valueElem.val() && criteriaElem.val() != "0" && valueElem.val() != '<%=StringUtil.getMessageForKey("label.select.search.here")%>'){
        searchButton.removeAttr("disabled");
        if( eventCode == 13 ){
            searchButton.click();
        }
    } else {
        searchButton.attr("disabled", "disabled");
    }
    if(criteriaElem.val() == "5" ){
        valueElem.attr("maxlength","<%=Integer.toString(accessionNumberValidator.getMaxAccessionLength())%>");
    } else {
        valueElem.attr("maxlength","120");
    }
}

function handleSelectedPatient(){
    var accessionNumber = "";
    if($jq("#searchCriteria").val() == 5){//lab number
        accessionNumber = $jq("#searchValue").val();
    }

    $("searchResultsDiv").style.display = "none";
    var form = document.forms[0];
    form.action = '<%=formName%>'.sub('Form','') + ".do?accessionNumber=" + accessionNumber + "&patientId=" + patientSelectID;
    if( !(typeof requestType === 'undefined') ){
        form.action += "&type=" + requestType;
    }
    
    form.submit();
}

function firstClick(){
    var searchValue = $jq("#searchValue");
    searchValue.val("");
    searchValue.removeAttr("onkeydown");
}

function messageRestore(element ){
    if( !element.value ){
        element.maxlength = 120;
        element.value = '<%=StringUtil.getMessageForKey("label.select.search.here")%>';
        element.onkeydown = firstClick;
        setCaretPosition(element, 0);
    }
}

function cursorAtFront(element){

    if( element.onkeydown){
        setCaretPosition( element, 0);
    }
}

function setCaretPosition(ctrl, pos){
    if(ctrl.setSelectionRange){
        ctrl.focus();
        ctrl.setSelectionRange(pos,pos);
    } else if (ctrl.createTextRange) {
        var range = ctrl.createTextRange();
        range.collapse(true);
        range.moveEnd('character', pos);
        range.moveStart('character', pos);
        range.select();
    }
}

function printBarcode(button) {
	var labNo = document.getElementsByName('accessionNumber')[0].value;
	var patientId = document.getElementsByName('patientId')[0].value;
	var type = "";
	var quantity = "";
	if (confirm("<%=StringUtil.getMessageForKey("barcode.message.reprint.confirmation")%>")) {
        if (button.id == "defaultPrintButton") {
        type = "default";
        } else if (button.id == "orderPrintButton") {
        type = "order";
        quantity = document.getElementById('quantity').value;
        } else if (button.id == "printBlankBarcodeButton") {
        type = "blank";
        } else {
        type = "specimen";
        labNo = button.id;
        quantity = 1;
        }
        $jq("#searchLabNumber").val('');
        document.getElementById("ifbarcode").src = 'LabelMakerServlet?labNo=' + labNo + '&type='
                + type + '&patientId=' + patientId + '&quantity=' + quantity;
        document.getElementById("barcodeArea").show();
        }
    }

    function checkPrint() {
        var disableButton = false;
        if (document.getElementById('labelType').selectedIndex == 0) {
        disableButton = true;
        } else if (!document.getElementById('quantity').value) {
        disableButton = true;
        }
        document.getElementById('printBarcodeButton').disabled = disableButton;
    }

    function finish() {
        window.location = "Dashboard.do";
    }
</script>

<input type="hidden" id="searchLabNumber">
<html:hidden name="<%=formName%>" property="accessionNumber"/>
<html:hidden name="<%=formName%>" property="patientId"/>

<div id="PatientPage" class=" patientSearch" style="display:inline;" >

	<h2><bean:message key="sample.entry.search"/></h2>
    <logic:present property="warning" name="<%=formName%>" >
        <h3 class="important-text"><bean:message key="order.modify.search.warning" /></h3>
    </logic:present>
    <input type="hidden" 
    		id="searchCriteria" 
    		class="patientSearch" 
    		value="5" /> 
    		
   	<bean:message key="barcode.print.search.accessionnumber"/>:
	<bean:message key="sample.search.scanner.instructions"/>
    <input size="35"
           maxlength="120"
           id="searchValue"
           class="patientSearch"
           value='<%=StringUtil.getMessageForKey("label.select.search.here")%>'
           type="text"
           onclick="cursorAtFront(this)"
           onkeydown='firstClick();'
           onkeyup="messageRestore(this);enableSearchButton(event.which);"
            tabindex="2"/>

    <input type="button"
           name="searchButton"
           class="patientSearch"
           value="<%= StringUtil.getMessageForKey("label.patient.search")%>"
           id="searchButton"
           onclick="searchPatients()"
           disabled="disabled" >

	<div id="noPatientFound" align="center" style="display:none" >
		<h1><bean:message key="patient.search.not.found"/></h1>
	</div>
	<div id="searchResultsDiv" style="display:none;">
		<% if( localDBOnly.equals("false")){ %>
		<table id="searchResultTable" style="width:90%">
			<tr>
				<th width="2%"></th>
				<th width="10%" >
					<bean:message key="patient.data.source" />
				</th>
		<% } else { %>
		<table id="searchResultTable" width="70%">
			<tr>
				<th width="2%"></th>
		<% } %>
				<th width="18%">
					<bean:message key="patient.epiLastName"/>
				</th>
				<th width="15%">
					<bean:message key="patient.epiFirstName"/>
				</th>
				<th width="5%">
					<bean:message key="patient.gender"/>
				</th>
				<th width="11%">
					<bean:message key="patient.birthDate"/>
				</th>
				<% if( supportMothersName ){ %>
				<th width="20%">
					<bean:message key="patient.mother.name"/>
				</th>
				<% } if(supportSTNumber){ %>
				<th width="12%">
					<bean:message key="patient.ST.number"/>
				</th>
				<% } %>
				<% if(supportSubjectNumber){ %>
				<th width="12%">
					<bean:message key="patient.subject.number"/>
				</th>
				<% } %>
				<% if(supportNationalID){ %>
				<th width="12%">
                    <%=StringUtil.getContextualMessageForKey("patient.NationalID") %>
                </th>
                <% } %>
			</tr>
		</table>
		</div>
		<br/>
		<logic:notEqual name="<%=formName%>" property="accessionNumber" value="">
		<table  id="patientInfo" width="50%">
			<tr>
				<th>
					<bean:message key="sample.entry.patient"/>
				</th>
				<th>
					<bean:message key="patient.birthDate"/>
				</th>
				<th>
					<bean:message key="patient.gender"/>
				</th>
				<th>
					<bean:message key="patient.NationalID"/>
				</th>
			</tr>
			<tr>
				<td>
					<bean:write name="<%=formName%>" property="patientName"/>&nbsp;
				</td>
				<td>
					<bean:write name="<%=formName%>" property="dob"/>&nbsp;
				</td>
				<td>
					<bean:write name="<%=formName%>" property="gender"/>&nbsp;
				</td>
				<td>
					<bean:write name="<%=formName%>" property="nationalId"/>
				</td>
			</tr>
		</table>
		<h2><bean:message key="barcode.print.section.set"/></h2>
        <bean:message key="barcode.print.set.instruction"/>
        
        <input type="button" 
        	id="defaultPrintButton"
        	value="<bean:message key='barcode.print.set.button'/>"
        	onclick="printBarcode(this);"/>
        <h2><bean:message key="barcode.print.section.individual"/></h2>
        <table width="50%">
        	<tr>
        		<th>
        			<bean:message key="barcode.print.individual.type"/>
        		</th>
        		<th>
        			<bean:message key="barcode.print.individual.labnumber"/>
        		</th>
        		<th>
        			<bean:message key="barcode.print.individual.info"/>
        		</th>
        		<th>
        			<bean:message key="barcode.print.individual.number"/>
        		</th>
        	<tr>
	        	<td>
	        		<bean:message key="barcode.label.type.order"/>
	        	</td>
        		<td>
        			<bean:write name="<%=formName%>" property="accessionNumber" />
        		</td>
        		<td>
        		</td>
        		<td>
        			<input type="text"
        				id="quantity"
        				value="1"
        				onchange="checkFieldInt(this)" >
        		</td>
        		<td>
        			<input type="button" 
			        	id="orderPrintButton"
			        	value="<bean:message key='barcode.print.individual.button'/>"
			        	onclick="printBarcode(this);">
        		</td>
        	</tr>
        	<logic:present name="<%=formName%>"  property="existingTests">
        	<logic:iterate id="existingTests" name="<%=formName%>"  property="existingTests" indexId="index" type="us.mn.state.health.lims.sample.bean.SampleEditItem">
        		<tr>
        			<td>
        				<bean:message key="barcode.label.type.specimen"/>
        			</td>
        			<td>
        				<bean:write name="existingTests" property="accessionNumber"/>
        			</td>
        			<td>
        				<bean:write name="existingTests" property="sampleType"/>
        			</td>
        			<td>
        				1
        			</td>
        			<td>
        				<input type="button"
        					id='<bean:write name="existingTests" property="accessionNumber"/>'
        					value="<bean:message key='barcode.print.individual.button'/>"
        					onclick="printBarcode(this);">
        			</td>
        		</tr>
        	</logic:iterate>
        	</logic:present>
        </table>
		</div>
	
	</logic:notEqual>
        
	<div style="display:none;" id="barcodeArea">
		<h2><bean:message key="barcode.common.section.barcode.header"/></h2>
		<iframe  src="about:blank" id="ifbarcode" width="75%" height="300px"></iframe>
	</div>
