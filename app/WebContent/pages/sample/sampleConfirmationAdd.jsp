<%@ page language="java" contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants,
			us.mn.state.health.lims.common.formfields.FormFields,
			us.mn.state.health.lims.common.formfields.FormFields.Field,
			us.mn.state.health.lims.common.provider.validation.AccessionNumberValidatorFactory,
			us.mn.state.health.lims.common.provider.validation.IAccessionNumberValidator,
			us.mn.state.health.lims.sample.bean.SampleConfirmationItem,
			us.mn.state.health.lims.sample.bean.SampleConfirmationTest,
			us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample,
			us.mn.state.health.lims.common.util.IdValuePair,
            us.mn.state.health.lims.common.util.Versioning,
			us.mn.state.health.lims.common.util.StringUtil"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>
<%@ taglib uri="/tags/struts-tiles"     prefix="tiles" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>


<bean:define id="formName" 	value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>' />



<%!String path = "";
	String basePath = "";
	IAccessionNumberValidator accessionNumberValidator;
	boolean useSTNumber = true;
	boolean useMothersName = true;
	boolean useRequesterSiteList = false;
	boolean useProviderInfo = false;
	boolean useInitialSampleCondition = false;
	boolean patientRequired = true;
%>
<%
	path = request.getContextPath();
	basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";

	accessionNumberValidator = new AccessionNumberValidatorFactory().getValidator();

	useSTNumber =  FormFields.getInstance().useField(Field.StNumber);
	useMothersName = FormFields.getInstance().useField(Field.MothersName);
	useRequesterSiteList = FormFields.getInstance().useField(Field.RequesterSiteList);
	useProviderInfo = FormFields.getInstance().useField(Field.ProviderInfo);
	useInitialSampleCondition = FormFields.getInstance().useField(Field.InitialSampleCondition);
	patientRequired = FormFields.getInstance().useField(Field.PatientRequired_SampleConfirmation);
%>

<link rel="stylesheet" type="text/css" href="css/jquery.asmselect.css?ver=<%= Versioning.getBuildNumber() %>" />
<link rel="stylesheet" href="css/jquery_ui/jquery.ui.all.css?ver=<%= Versioning.getBuildNumber() %>">
<link rel="stylesheet" href="css/customAutocomplete.css?ver=<%= Versioning.getBuildNumber() %>">

<script type="text/javascript" src="<%=basePath%>scripts/utilities.jsp"></script>
<script type="text/javascript" src="<%=basePath%>scripts/utilities.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="scripts/jquery.asmselect.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="scripts/ajaxCalls.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="scripts/jquery.asmselect.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="scripts/ui/jquery.ui.core.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="scripts/ui/jquery.ui.widget.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="scripts/ui/jquery.ui.button.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="scripts/ui/jquery.ui.menu.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="scripts/ui/jquery.ui.position.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="scripts/ui/jquery.ui.autocomplete.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="scripts/customAutocomplete.js?ver=<%= Versioning.getBuildNumber() %>"></script>

<script type="text/javascript" >


var useSTNumber = <%= useSTNumber %>;
var useMothersName = <%= useMothersName %>;
var useInitialSampleCondition = <%= useInitialSampleCondition %>;
var dirty = false;

var currentRequestSampleIndex;


function handleMultiSelectChange( e, data ){
	var id = "#multi" + e.target.id;
	var selection = $jq(id)[0];

	if( data.type == "add"){
		appendValueToElementValue( selection, data.value );
	}else{
		var splitValues =  selection.value.split(",");
		selection.value = "";

		for( var i = 0; i < splitValues.length; i++ ){
			if( splitValues[i] != data.value ){
				appendValueToElementValue( selection, splitValues[i] );
			}
		}
	}
}

function appendValueToElementValue( elem, addString ){
	if( elem.val() && elem.val().length > 1 ){
			elem.val( elem.val() + ',');
		}

	elem.val( elem.val() + addString);
}

function populateRequestForSampleType( selector, sampleIndex){
	var selectIndex = selector.selectedIndex;
	var selection;

	fieldValidator.setFieldValidity( false, "requestedTests_" + sampleIndex );

	if( selectIndex == 0 ){
		var requestingTests = $$(".requestingTests_" + sampleIndex );
		var requestedTestTable = $("requestedTests_" + sampleIndex  );
		enableDisableAndClearRequestingTests(requestedTestTable, sampleIndex, requestingTests, true );
	}else{
		selection = selector.options[selectIndex];
		currentRequestSampleIndex = sampleIndex;
		getTestsForSampleType(selection.value, "none", processGetTestSuccess );
	}

	setSaveButton();
}


function processGetTestSuccess(xhr){
   	//alert(xhr.responseText);
   	var response = xhr.responseXML.getElementsByTagName("formfield").item(0);
   	var tests = response.getElementsByTagName("test");
   	var test;
   	var length = tests.length;

   	var requestingTests = $jq(".requestingTests_" + currentRequestSampleIndex );
   	var requestedTestTable = $("requestedTests_" + currentRequestSampleIndex  );

	enableDisableAndClearRequestingTests(requestedTestTable, currentRequestSampleIndex, requestingTests, length == 0 );


   	for( var i = 0; i < length; ++i){
   		test = tests[i];
		var name = getValueFromXmlElement( test, "name" );
		var id = getValueFromXmlElement( test, "id" );
		insertIntoRequestingList(requestingTests, name, id );
		insertIntoRequestedList(requestedTestTable, name, id, i, currentRequestSampleIndex);
   }

	fieldValidator.setFieldValidity( false, "requestedTests_" + currentRequestSampleIndex );
	requestedTestTable.style.border = "1px solid black";
	setSaveButton();
}

function enableDisableAndClearRequestingTests(requestedTestTable, sampleIndex, requestingTests, disable ){
	var checkBoxRows = $$(".selectionRow_" + sampleIndex);

	for( var i = 0; i < requestingTests.length; ++i){
		requestingTests[i].disabled = disable;
		removeOptions( requestingTests[i] );
	}

	for( i = checkBoxRows.length - 1; i >= 0; --i){
		 requestedTestTable.deleteRow(checkBoxRows[i].rowIndex);
	}
	
	$jq(".referralTestResult_" + sampleIndex ).hide();
	$jq(".referralTestResult_" + sampleIndex ).val( "" );
}

function removeOptions(selectbox){
	for(var i=selectbox.options.length-1; i>0 ; --i){
		selectbox.remove(i);
	}
}

function insertIntoRequestingList(testSelections, name, id ){
	for(var i = 0; i < testSelections.length; ++i ){
		addOptionToSelect(testSelections[i],name,id );
	}
}

function insertIntoRequestedList(requestedTestsTable, name, id, i, sampleIndex){
	var newRow = requestedTestsTable.insertRow(  i);
	newRow.id = "availRow_" + i;
	newRow.className = "selectionRow_" + sampleIndex;

	newRow.insertCell(0);
	var selectionCell = newRow.insertCell(1);

	selectionCell.innerHTML = getCheckBoxHtml(id, i, sampleIndex ) + getTestDisplayRowHtml( name, i );
}

function setPossibleResultsForTest( testSelection, compoundIndex ){
	new Ajax.Request (  'ajaxQueryXML',
            	         {
                	         method: 'get',
                    	     parameters: "provider=SampleEntryPossibleResultsForTest&testId=" + testSelection.value + "&index=" + compoundIndex,
                          	 onSuccess:  processGetPossibleResultsSuccess,
                          	 onFailure:  null
                         	}
                          );

}

function processGetPossibleResultsSuccess(xhr){
   	//alert(xhr.responseText);

  	var response = xhr.responseXML.getElementsByTagName("formfield").item(0);
   	var resultTypeElement = response.getElementsByTagName("resultType")[0];
	var indexElement = response.getElementsByTagName("callerIndex")[0];

   	var resultType =  resultTypeElement.attributes.getNamedItem("value").value;
   	var sourceIndex = indexElement.attributes.getNamedItem("value").value;

   	if('N' == resultType){
   		$("textResult_" + sourceIndex).style.display = "inline";
   		$("dictionaryResult_" + sourceIndex).style.display = "none";
   		$("freeTextResult_" + sourceIndex).style.display = "none";
   	}else if('R' == resultType){
   		$("textResult_" + sourceIndex).style.display = "none";
   		$("dictionaryResult_" + sourceIndex).style.display = "none";
   		$("freeTextResult_" + sourceIndex).style.display = "inline";
   	}else{
   	    var dictionarySelection = $("dictionaryResult_" + sourceIndex);
   	    var dictionaryValues = response.getElementsByTagName("value");

   	    removeOptions( dictionarySelection );

   	    addOptionToSelect( dictionarySelection, "", "0" );
   	    for(var i = 0; i < dictionaryValues.length; ++i ){
   	    	addOptionToSelect( dictionarySelection, dictionaryValues[i].attributes.getNamedItem("name").value, dictionaryValues[i].attributes.getNamedItem("id").value );
   	    }
   	    addOptionToSelect( dictionarySelection, '<%= StringUtil.getMessageForKey("option.notListed")%>', "UseText" );

   		dictionarySelection.style.display = "inline";
   		$("textResult_" + sourceIndex).style.display = "none";
   		$("freeTextResult_" + sourceIndex).style.display = "none";
   	}
}

function addOptionToSelect( selectElement, text, value ){
  	    var option = document.createElement("OPTION");
		option.text = text;
		option.value = value;
		selectElement.options.add(option);
}

function checkDictionaryForUseText(dictionaryElement , sourceIndex){
	var selected = dictionaryElement.options[dictionaryElement.selectedIndex].value;
	var textResult = $("textResult_" + sourceIndex);

	if( "UseText" == selected ){
		textResult.style.display = "inline";
	}else{
		textResult.style.display = "none";
		textResult.value = "";
	}

}

function addNewRequesterTestResult(addButtonElement, sampleIndex){  //request for another test in this lab

	var testRequestTable = $("testRequestTable_" + sampleIndex );
	var testResultRow = $("referralTestId_" + sampleIndex); 
	var maxReferralElement = $("maxReferralTestIndex_" + sampleIndex );
	var newTestIndex = parseInt(maxReferralElement.value) + 1;
	var newRow = testRequestTable.insertRow( addButtonElement.parentNode.parentNode.rowIndex  );

	var protoIDPattern = /[0-9]_0/g;
	var selectedPattern = /selected/i;
	var compoundIndex = sampleIndex + "_" + newTestIndex;

	//this crap is brought to you because of the good folks who brought you IE
	var clonedCells = testResultRow.getElementsByTagName('td');
	var cell;
	for (var i = 0; i < clonedCells.length; ++i ){
		cell = newRow.insertCell(i);
		cell.innerHTML = clonedCells[i].innerHTML.replace(protoIDPattern, compoundIndex).
		                                          replace("Tests_0", "Tests_" + sampleIndex).
		                                          replace("inline", "none").
		                                          replace(selectedPattern, "");
	}

	cell = newRow.insertCell( clonedCells.length );
	cell.innerHTML = "<input type=\'button\' value=\'" +
	                 "<%= StringUtil.getMessageForKey("label.button.remove") %>" +
	                 "\' class=\'textButton\'  onclick=\'removeRequestedTest( this, \"" + sampleIndex + "\" );\' >";

	newRow.className = "extraTest_" + sampleIndex;
        // Jquery fix for IE bug where value of cloned text input remains
        $jq("." + newRow.className).find("#textResult_" + compoundIndex).val('');
	maxReferralElement.value = newTestIndex;
}

function addNewRequesterSample( ){ // a new sample which came in with the request
	var maxSampleElement = $("maxSampleIndex");
	var sampleIndex = parseInt(maxSampleElement.value) + 1;
	var protoSampleIDPattern = /_0/g;
	var protoFunction = /this, '0'\)/g;
	var protoIDPattern = /[0-9]_[0-9]/g; //this has to do with the order of replacements
	var selectPrototype;

 	var clone = $jq("#div_0" ).clone(true, true);
	clone.attr("id", "div_" + sampleIndex);
	clone.find(".sampleIndex").val( sampleIndex );
	clone.find("#requesterSampleId_0").val("");
	clone.find("#collectionDate_0").val("");
    clone.find("#interviewTime_0").val("");
	clone.find("#maxReferralTestIndex_0").val(0);
	clone.find("#hideShow_0").val("hidden");
	clone.find("#showHideButton_0").attr("src", "./images/note-add.gif");
    clone.find("#showHideButton_0").attr("onclick", "showHideNotes( '"+ sampleIndex + "' )");
	clone.find("tr:first").append("<td><input type=\"button\" value=\"" +
				                  "<%= StringUtil.getMessageForKey("label.button.remove") %>" +
	                              "\" class=\"textButton\"  onclick=\"removeRequesterTest( this, \'" + sampleIndex +  "\' );\" ></td>");
	
	if( clone.find("div")){
		selectPrototype = $jq("#prototypeID").clone(true, true);
		selectPrototype.attr("id", "initialCondition_" + sampleIndex);
		selectPrototype.attr("multiple", "multiple");
		clone.find("div").replaceWith( selectPrototype );
	}else{
		clone.find("requesterSampleRowTwo").replace(protoMaxPattern, "input value=\"0\"");
	}
	             
	clone.find(".requestingTests_0 option:gt(0)").remove();
	clone.find("#requestedTests_0 tbody").remove();
	clone.find("#note_0").val("");
	clone.find("#noteRow_0").hide();
	clone.find("#dictionaryResult_0_0").hide();
	clone.find("#textResult_0_0").hide();
	clone.find("#freeTextResult_0_0").hide();
	
	clone.html(clone.html().replace(protoSampleIDPattern, "_" + sampleIndex));
	clone.html(clone.html().replace(protoFunction, "this, '" + sampleIndex + "')"));
	clone.html(clone.html().replace("selected", ""));
	clone.html(clone.html().replace( protoIDPattern, sampleIndex + "_0") );
	
	clone.find(".extraTest_" + sampleIndex).remove();
	

	$jq("#div_" + (sampleIndex - 1) ).after(clone);
	
	if(selectPrototype){
		$jq("#initialCondition_" + sampleIndex).asmSelect({removeLabel: "X"});
		selectPrototype.attr("selectedIndex", -1);
	}	
	
	maxSampleElement.value = sampleIndex;

    fieldValidator.setFieldValidity(false, "requestedTests_" + sampleIndex);
    setValidIndicaterOnField( true, "requestedTests_" + sampleIndex);
    setValidIndicaterOnField( true, "interviewTime_" + sampleIndex);
	setValidIndicaterOnField( true, "collectionDate_" + sampleIndex);
	
	setSaveButton();
}

function removeRequestedTest( buttonElement, sampleIndex ){
	$("testRequestTable_" + sampleIndex).deleteRow( buttonElement.parentNode.parentNode.rowIndex );
}

function removeRequesterTest( buttonElement, sampleIndex ){
	var highRowRange = $("requestedTestRow_" + sampleIndex).rowIndex;
	var lowRowRange = buttonElement.parentNode.parentNode.rowIndex;

	for( var i = highRowRange; i >= lowRowRange; --i ){
		$("testRequestTable_" + sampleIndex).deleteRow( i );
	}

	fieldValidator.setFieldValidity(true, "requestedTests_" + sampleIndex  );
	setSaveButton();
}


function getCheckBoxHtml(id, row, sampleIndex ){
	return "<input type='checkbox' id='select_" + row + "' value='" + id + "' onclick='requestedTestChanged(" + sampleIndex + " );'>";
}

function getTestDisplayRowHtml( name, i ){
	return "<label for='select_" + i + "'>" + name + "</label>";
}

function requestedTestChanged( sampleIndex ){
    var requestedTestsTable =  $("requestedTests_" + sampleIndex );
	var requestedTests = requestedTestsTable.getElementsByTagName("input");
	var somethingChecked = false;

	for(var i = 0; i < requestedTests.length; ++i ){
		if( requestedTests[i].checked ){
			somethingChecked = true;
			break;
		}
	}

    requestedTestsTable.style.border = somethingChecked ? "1px solid black" : "1px solid red";
	fieldValidator.setFieldValidity( somethingChecked, "requestedTests_" + sampleIndex );

	setSaveButton();
}

function getValueFromXmlElement( parent, tag ){
	var element = parent.getElementsByTagName( tag );
	return element ? element[0].childNodes[0].nodeValue : "";
}

function showHideSamples(button, targetId){
	if( button.value == "+" ){
		$(targetId).show();
		button.value = "-";
	}else{
		$(targetId).hide();
		button.value = "+";
	}
}

function loadDynamicData(){
	var xml = "<requestedTests>";
	xml += addSamples( );
	xml += "</requestedTests>";

	$("xmlWad").value = xml;
}

function addSamples(  ){
	var samplesXml = "<samples>";
	var sampleIndexs = $$(".sampleIndex");

	for( var i = 0; i < sampleIndexs.length; ++ i){
		samplesXml += addSample( 	"zero" == sampleIndexs[i].value ? "0" : sampleIndexs[i].value);
	}

	samplesXml += "</samples>";

	return samplesXml;
}

function /*string*/ addSample( sampleIndex ){

	var sampleXml = "<sample ";
	sampleXml += "requesterSampleId='" + $("requesterSampleId_" + sampleIndex).value + "' ";
	sampleXml += "sampleType='" + ($("sampleType_" + sampleIndex).value) + "' ";
	sampleXml += "collectionDate='" + ($("collectionDate_" + sampleIndex).value) + "' ";
    <% if( FormFields.getInstance().useField(Field.CollectionTime)){ %>
	sampleXml += "collectionTime='" + ($("interviewTime_" + sampleIndex).value) + "' ";
    <% }else{ %>
    sampleXml += "collectionTime='00:00' ";
    <% } %>
	sampleXml += "note ='" + getNote( sampleIndex ) + "' ";
	if( useInitialSampleCondition ){
		var initialConditions = $("initialCondition_" + sampleIndex);
		var optionLength = initialConditions.options.length;
		var xml = " initialConditionIds=' ";
		for( var i = 0; i < optionLength; ++i ){
			if( initialConditions.options[i].selected ){
				xml += initialConditions.options[i].value + ",";
			}
		}

		xml =  xml.substring(0,xml.length - 1);
		xml += "'";
		sampleXml += xml;
	}
	sampleXml += " requestedTests='" + getRequestedTests( sampleIndex ) + "' >";
	sampleXml += "<tests>";
	sampleXml += getTests( sampleIndex );
	sampleXml += "</tests>";
	sampleXml += "</sample>";

	return sampleXml;
}

function /*string*/ getTests( sampleIndex ){
	var tests = "";
	var maxTestIndex = $("maxReferralTestIndex_" + sampleIndex).value;

	for( var i = 0; i <= maxTestIndex; ++i ){
		tests += getTest( sampleIndex, i );
	}

	return tests;
}

function /*string*/ getTest( sampleIndex, testIndex ){
	var compoundIndex = sampleIndex + "_" + testIndex;
	var requestedTest = $("requestedTests_" + compoundIndex);

	if( requestedTest ){
		var id = requestedTest.options[requestedTest.selectedIndex].value;
		var resultType;
		var value;


		if( $("textResult_" + compoundIndex).visible() ){
			resultType = "A";
			value = $jq("#textResult_" + compoundIndex).val();
		}else if( $("freeTextResult_" + compoundIndex).visible() ){
			resultType = "R";
			value = $jq("#freeTextResult_" + compoundIndex).val();
		}else{
			resultType = "D";
			value = $jq("#dictionaryResult_" + compoundIndex).val();
		}

		return "<test id='" +id + "' resultType='" + resultType + "' value='" + value +  "' />";
	}

    return "";
}

function /*string*/ getRequestedTests( sampleIndex ){
	var requestList = "";
	var requestedTests = $("requestedTests_" + sampleIndex ).getElementsByTagName("input");

	for(var i = 0; i < requestedTests.length; ++i ){
		if( requestedTests[i].checked ){
			requestList += requestedTests[i].value + ",";
		}
	}

	return requestList;
}

function /*string*/ getNote( sampleIndex ){
	var singleQuote = /\'/g;
	var doubleQuote = /\"/g;
	return ($("note_" + sampleIndex).value.replace(singleQuote, "\\'" ).replace(doubleQuote, '\\"'));
}

 
</script>
<% if(useInitialSampleCondition){ %>
<div id="sampleConditionPrototype" style="display: none" >
			<select id="prototypeID" title='<%= StringUtil.getMessageForKey("result.multiple_select")%>' > 
			<logic:iterate id="optionValue" name='<%=formName%>' property="initialSampleConditionList" type="IdValuePair" >
						<option value='<%=optionValue.getId()%>' >
							<bean:write name="optionValue" property="value"/>
						</option>
					</logic:iterate>
			</select>
</div>
<% } %>

<input type="hidden" id="maxSampleIndex" value="0" />
<html:hidden name='<%=formName %>' property="requestAsXML" styleId="xmlWad" />
<<<<<<< HEAD

<tiles:insert attribute="sampleConfirmationOrder" />
=======
<table width="70%" border="0">
	<tr>
		<td>
			<%=StringUtil.getContextualMessageForKey("quick.entry.accession.number")%>:
			<span class="requiredlabel">*</span>
		</td>
		<td width="15%">
			<app:text name="<%=formName%>" property="labno"
				maxlength='<%= Integer.toString(accessionNumberValidator.getMaxAccessionLength())%>'
				onchange="checkAccessionNumber(this);makeDirty();" styleClass="text"
				styleId="labNo" />
		</td>
		<td id="generate">
			<bean:message key="sample.entry.scanner.instructions" />
			<html:button property="generate" styleClass="textButton"
				onclick="getNextAccessionNumber(); makeDirty();">
				<bean:message key="sample.entry.scanner.generate" />
			</html:button>
		</td>
	</tr>
	<tr>
		<td>
			<bean:message key="quick.entry.received.date" />
			:
			<span class="requiredlabel">*</span>
			<font size="1"><bean:message key="sample.date.format" />
			</font>
		</td>
		<td colspan="2">
			<app:text name="<%=formName%>" property="receivedDate"
				onchange="checkValidDate(this);makeDirty();"
				onkeyup="addDateSlashes(this,event); "
				styleClass="text"
				maxlength="10"
				styleId="receivedDate" />
				
           <% if( FormFields.getInstance().useField(Field.SampleEntryUseReceptionHour)){ %>
               <bean:message key="sample.receptionTime" />:
                   <html:text name="<%=formName %>" 
                   onkeyup="filterTimeKeys(this, event); " 
                   property="recievedTime"
                   styleId="receivedTime" 
                   maxlength="5"
                   onblur="makeDirty(); updateFieldValidity(checkValidTimeEntry(this, true), this.id );"/>
           
           <% } %>
				
		</td>
	</tr>
</table>
<hr />
<h3>
	<bean:message key="sample.entry.requester"/>
</h3>
<table>
	<tr>
		<td>
			<bean:message key="organization.site" />
		</td>
		<td colspan="5">
		<!-- N.B. this is replaced by auto repeate -->
		<html:select styleId="orgRequesterId" 
								     name="<%=formName%>"
								     property="requestingOrganization"
								     onchange="getRequestersForOrg();makeDirty();setSaveButton();"
								     >
							<option value=""></option>
							<logic:iterate name="<%=formName %>" property="requestingOrganizationList" id="org" type="us.mn.state.health.lims.common.util.IdValuePair">
							    <option value="<%=org.getId() %>" ><%= org.getValue() %></option>
							</logic:iterate>
		</html:select>
		</td>
	</tr>
	<tr>
		<td>
			<bean:message key="sample.entry.contact" />
		</td>
		<td colspan="5">
			<html:select name='<%= formName %>'
			             property="personRequesterId"
			             styleId="personRequesterId"
			             onchange="populateRequesterDetail(this); makeDirty();">
				<option value="0"><bean:message key="sample.entry.requester.new" /></option>
			</html:select>
		</td>
	</tr>
	<tr><td>&nbsp;</td>
		<td>
			<bean:message key="person.firstName" />
		</td>
		<td>
			<bean:message key="person.lastName" />
		</td>
		<td>
			<bean:message key="person.phone"/>
		</td>
		<td>
			<bean:message key="person.fax"/>
		</td>
		<td>
			<bean:message key="person.email"/>
		</td>

	</tr>
	<tr>
		<td>&nbsp;</td>
		<td>
			<html:text name='<%=formName%>' property="firstName" styleId="requesterFirstName" />
		</td>
		<td>
			<html:text name='<%=formName%>' property="lastName"  styleId="requesterLastName" />
		</td>
		<td>
			<html:text name='<%=formName%>' property="phone"  styleId="requesterPhone"  />
		</td>
		<td>
			<html:text name='<%=formName%>' property="fax"  styleId="requesterFax" />
		</td>
		<td>
			<html:text name='<%=formName%>' property="e-mail"  styleId="requesterEMail" />
		</td>
>>>>>>> master

<hr/>

<h3><bean:message key="sample.entry.confirmation.test.request"/></h3>
<input type="hidden" value="0" id="maxReferralSampleIndex_0" />
<div id=div_0>
<table  id="testRequestTable_0">
	<tr id="requesterSampleRowOne">
			<td><bean:message key="sample.entry.test.confirmation.requester.id"/></td>
			<td>
				<input type="hidden" value="zero" class="sampleIndex" />
				<input type="hidden" value="0" id="maxReferralTestIndex_0" />
				<input type="text" id="requesterSampleId_0" onchange=" makeDirty();">
			</td>
			<td><bean:message key="sample.collectionDate"/>&nbsp;<span style="font-size: xx-small; "><bean:message key="sample.date.format"/></span>:</td>
			<td><input type="text"
			           id="collectionDate_0"
			           name="collectionDate_0"
			           maxlength="10"
			           onkeyup="addDateSlashes(this,event);"
			           onchange="checkValidDate(this)"/>
             <% if( FormFields.getInstance().useField(Field.CollectionTime)){ %>
                 <bean:message key="sample.collectionTime" />:
                     <html:text name="<%=formName %>"
                     onkeyup="filterTimeKeys(this, event);" 
                     property="interviewTime" 
                     styleId="interviewTime_0"
                     maxlength="5"
                     onblur="makeDirty(); updateFieldValidity(checkValidTimeEntry(this, true), this.id );"/>
             
             <% } %>
			           
			</td>
			<td>
				<img src="./images/note-add.gif"
						 	     onclick="showHideNotes( '0');"
						 	     id="showHideButton_0"
						    />
                <input type="hidden" name="hideShowFlag" value="hidden" id="hideShow_0" >
			</td>
	</tr>
	<tr id="requesterSampleRowTwo">	
	        <% if(useInitialSampleCondition){ %>
				<td >
					<bean:message key="sample.entry.sample.condition"/>
				</td>
				<td>
				<select id="initialCondition_0"  multiple="multiple" title='<%= StringUtil.getMessageForKey("result.multiple_select")%>' >
					<logic:iterate id="optionValue" name='<%=formName%>' property="initialSampleConditionList" type="IdValuePair" >
								<option value='<%=optionValue.getId()%>' >
									<bean:write name="optionValue" property="value"/>
								</option>
							</logic:iterate>
						</select>
			</td>
			<% } %>
					
			<td><bean:message key="sample.entry.sample.type"/>&nbsp;<span class="requiredlabel">*</span></td>
			<td>
			<select id="sampleType_0"
				    onchange="makeDirty(); populateRequestForSampleType(this, '0');" >
				<option value="0"></option>
				<logic:iterate id="sampleTypes"
							   name='<%=formName %>'
							   property="sampleTypes"
							   type="IdValuePair">
					<option value='<%= sampleTypes.getId() %>'>
						<%=sampleTypes.getValue() %>
					</option>
				</logic:iterate>
			</select>
			</td>
		</tr>
		<tr id="noteRow_0"	style="display: none;">
			<td style="vertical-align:top" align="right"><bean:message key="note.note"/>:</td>
			<td colspan="6" align="left" >
				<textarea id="note_0"
						   onchange="makeDirty();"
			           	   cols="100"
			           	   rows="3" ></textarea>
			</td>
		</tr>
		<tr id="referralTestId_0" >
			<td ><bean:message key="sample.entry.test.confirmation.site.test" /></td>
			<td>
				<select name="requesterTests"
							id="requestedTests_0_0"
							class="requestingTests_0"
							onchange="setPossibleResultsForTest(this, '0_0');"
							 >
					<option value="0" >&nbsp;</option>
				</select>
			</td>
			<td><bean:message key="sample.entry.test.confirmation.site.result"/></td>
			<td>
				<select name="result"
				        style="display: none"
				        class="referralTestResult_0"
				        id="dictionaryResult_0_0"
		        		onchange="checkDictionaryForUseText(this, '0_0');"
				        >
				</select>

				<input type="text" class="referralTestResult_0" name="result" style="display: none" id="textResult_0_0" />
				
				<textarea rows="2" class="referralTestResult_0" name=result style="display: none" id="freeTextResult_0_0" ></textarea>
			</td>
		</tr>
		<tr id="addButtonRow">
			<td>&nbsp;</td>
			<td>
				<input type="button"
				       class=textButton
				       value='<%= StringUtil.getMessageForKey("sampletracking.requester.test.result.add") %>'
				       onclick="addNewRequesterTestResult(this, '0');" />
			</td>
		</tr>
		<tr id="requestedTestRow_0">
			<td style="vertical-align:top"><bean:message key="sample.entry.confirmation.requested.tests"/> : <span class="requiredlabel">*</span></td>

			<td colspan="5">
			<table style="background-color:#EEEEEE; color: black; border : 1px solid red" id="requestedTests_0" >
			</table>
			</td>
		</tr>
</table>
<hr/>
</div>		

<input type="button"
	   class=textButton
	   value="<%= StringUtil.getMessageForKey("sampletracking.requester.sample.add") %>"
	   onclick="addNewRequesterSample( )" />

<hr/>
<hr style="width: 100%; height: 5px" />
<html:hidden name="<%=formName%>" property="patientPK" styleId="patientPK"/>
<table style="width:100%">
	<tr>
		<td style="width:15%;text-align:left;">
            <input type="button" value="+" onclick="showHideSamples(this, 'patientInfo');">
			<bean:message key="sample.entry.patient" />:
			<% if ( patientRequired ) { %><span class="requiredlabel">*</span><% } %>
		</td>
		<td style="width:15%" id="firstName"><b>&nbsp;</b></td>
		<td style="width:15%">
			<% if(useMothersName){ %><bean:message key="patient.mother.name"/>:<% } %>
		</td>
		<td style="width:15%" id="mother"><b>&nbsp;</b></td>
		<td style="width:10%">
			<% if( useSTNumber){ %><bean:message key="patient.ST.number"/>:<% } %>
		</td>
		<td style="width:15%" id="st"><b>&nbsp;</b></td>
		<td style="width:5%">&nbsp;</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td id="lastName"><b>&nbsp;</b></td>
		<td>
			<bean:message key="patient.birthDate"/>:
		</td>
		<td id="dob"><b>&nbsp;</b></td>
		<td>
			<%=StringUtil.getContextualMessageForKey("patient.NationalID") %>:
		</td>
		<td id="national"><b>&nbsp;</b></td>
		<td>
			<bean:message key="patient.gender"/>:
		</td>
		<td id="gender"><b>&nbsp;</b></td>
	</tr>
</table>

<div id="patientInfo" class="colorFill" style="display:none;" >
	<tiles:insert attribute="patientInfo" />
</div>

<script type="text/javascript" >

function /*void*/ setSave(){
	if(patientFormValid()){
		setSaveButton();
	}else{
		$("saveButtonId").disabled = true;
	}
}

//all methods here either overwrite methods in tiles or all called after they are loaded
var dirty=false;
function makeDirty(){
	dirty=true;
	if( typeof(showSuccessMessage) != 'undefinded' ){
		showSuccessMessage(false); //refers to last save
	}
	// Adds warning when leaving page if content has been entered into makeDirty form fields
	function formWarning(){ 
    return "<bean:message key="banner.menu.dataLossWarning"/>";
	}
	window.onbeforeunload = formWarning;
}

function savePage(){
	loadDynamicData();
	//alert( $("xmlWad").value );
	
	window.onbeforeunload = null; // Added to flag that formWarning alert isn't needed.
	var form = window.document.forms[0];
	form.action = "SampleConfirmationUpdate.do";
	form.submit();
}

function setValidIndicaterOnField( success, element){
	//Note the method this is overriding uses the name of the element, which does not have to be unique, set the id == name
	if( !element.id ){ //element is id
		element = $(element);
	}

	element.style.borderColor = success ? "" : "red";
	element.style.borderWidth = success ? "" : "2";
}

 

 


 
// Moving autocomplete to end - needs to be at bottom for IE to trigger properly
$jq(document).ready( function() {
        //fieldValidator declared in utilities.js
        fieldValidator.addRequiredField( "labNo" );
        fieldValidator.addRequiredField( "receivedDate" );
        fieldValidator.setFieldValidity(false, "requestedTests_0");

        $jq("select[multiple]").asmSelect({
                        removeLabel: "X"
                });

        $jq("select[multiple]").change(function(e, data) {
                // handleMultiSelectChange( e, data );
                });

});

</script>
<!--
 <ajax:autocomplete
  source="requestingOrg"
  target="selectedOrganizationId"
  baseUrl="ajaxAutocompleteXML"
  className="autocomplete"
  parameters="organizationName={requestingOrg},orgType=Refering lab,provider=OrganizationAutocompleteProvider,fieldName=organizationName,idName=id"
  indicator="indicator1"
  minimumCharacters="1" />
 -->