<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="us.mn.state.health.lims.common.action.IActionConstants,
				us.mn.state.health.lims.referral.action.beanitems.ReferralItem,
				us.mn.state.health.lims.referral.action.beanitems.ReferredTest,
				us.mn.state.health.lims.common.util.IdValuePair,
				us.mn.state.health.lims.common.util.StringUtil,
				java.util.List,java.util.ArrayList,
                us.mn.state.health.lims.common.util.Versioning,
				us.mn.state.health.lims.referral.action.ReferredOutAction.NonNumericTests" %>


<%@ taglib uri="/tags/struts-bean"		prefix="bean" %>
<%@ taglib uri="/tags/struts-html"		prefix="html" %>
<%@ taglib uri="/tags/struts-logic"		prefix="logic" %>

<bean:define id="formName"	value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>' />
<bean:define id="nonNumericTests" name='<%=formName %>' property="nonNumericTests" type="List<NonNumericTests>" />

<script type="text/javascript" src="scripts/jquery.ui.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="scripts/jquery.asmselect.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="scripts/utilities.js?ver=<%= Versioning.getBuildNumber() %>"></script>

<link rel="stylesheet" type="text/css" href="css/jquery.asmselect.css?ver=<%= Versioning.getBuildNumber() %>" />
<script type="text/javascript">

$jq(document).ready( function() {
		$jq("select[multiple]:visible").asmSelect({
				removeLabel: "X"
				// , debugMode: true
			});

		$jq("select[multiple]").change ( function(e, data) {
			handleMultiSelectChange( e, data );
			});

	});


function findRowIndexFromId( id ) {
     var us = id.lastIndexOf("_");
     var index;
     while ( us != -1) {
    	 index = id.substring(us+1, id.length);
    	 id = id.substring(0, us);
	     us = id.lastIndexOf("_");
	 }
     return index;
}
function handleMultiSelectChange( e, data ){
	var id = "#result" + e.target.id;
	var selection = $jq(id)[0];

	var index = findRowIndexFromId(id);
	markModified( index );

	if( data.type == "add"){
		appendValueToElementValue( selection, data.value );
	}else{ //drop
		var splitValues =  selection.value.split(",");
		selection.value = "";

		for( var i = 0; i < splitValues.length; i++ ){
			if( splitValues[i] != data.value ){
				appendValueToElementValue( selection, splitValues[i] );
			}
		}
	}
}

function appendValueToElementValue( e, addString ){
	if( e.value && e.value.length > 1 ){
			e.value += ',';
		}

		e.value += addString;
}


<%//This will generate the lists and hashs to associate the tests with appropriate result types
	List<String> alphaTests = new ArrayList<String>();
	List<String> dictionaryTests = new ArrayList<String>();
	List<String> multiValueTests = new ArrayList<String>();

	for( NonNumericTests nnt : nonNumericTests ){
		if( "A".equals(nnt.testType)){
			alphaTests.add( nnt.testId );
		}else{
			// single or multivalue are both dictionary value based tests.
			dictionaryTests.add(nnt.testId );
			if ("M".equals(nnt.testType)) {
				multiValueTests.add( nnt.testId );
			}
		}
	}

	out.print( "var alphaTests = [ " );
	for( int i = 0; i < alphaTests.size(); i++ ){
		out.print( "\"" + alphaTests.get(i) + "\"" );

		if( i < alphaTests.size() - 1 ){
			out.print( ", ");
		}
	}

	out.println( "];");

	out.print( "var dictionaryTests = [ " );
	for( int i = 0; i < dictionaryTests.size(); i++ ){
		out.print( "\"" + dictionaryTests.get(i)  + "\"" );

		if( i < dictionaryTests.size() - 1 ){
			out.print( ", ");
		}
	}
	out.println( "];");

	// only the list of multivalue tests
	out.print( "var multiValueTests = new Object();");
	for( int i = 0; i < multiValueTests.size(); i++ ){
		String n = multiValueTests.get(i);
		out.print( "multiValueTests[" + n + "] = " + n + ";");
	}
	out.println( "");

	out.print( "var dictionaryMap = { " );
    boolean isFirstTest = true;
    boolean isFirstValue = true;
	for( NonNumericTests nnt : nonNumericTests ){
		if( "D".equals(nnt.testType) || "M".equals(nnt.testType)) {
			if( isFirstTest ){
				isFirstTest = false;
			}else{
				out.print(", ");
			}
			out.print( "\"" + nnt.testId + "\" : { " );

			isFirstValue = true;
			for( IdValuePair pair : nnt.dictionaryValues ){
				if( isFirstValue ){
					isFirstValue = false;
				}else{
					out.print(", ");
				}
				out.print( "\"" + pair.getId() + "\" : " + "\"" + pair.getValue() + "\""  );
			}

			out.print( "}" );
		}
	}
	out.println( "};" );%>

function /*void*/ markModified( index ){
	$( "modified_" + index).value = true;
	$("saveButtonId").disabled = missingRequiredValues();
	makeDirty();
}

function missingRequiredValues(){
	var missing = false;
	
	$jq(".requiredRow").each(function(index, element){
		var children = $jq(element).find(".required");
		if( !((children[1].value == 0 && children[0].value == 0) || 
			(children[1].value != 0 && children[0].value != 0))){
				missing = true;
		}
	});
		
	return missing;
	
}

/*
Checks to see if the test has dictionary results, multi select dictionary results or numeric results and then switch if needed
*/
function /*void*/ updateResultField( index ){

	var testId = $("testSelection_" + index).value;
	var isMultiSelect = multiValueTests[testId] == testId;
	var isDict = dictionaryMap[testId] != undefined;
	var isDictionary = isDict && !isMultiSelect;
	var isNumeric = !(isDictionary || isMultiSelect);

	var i, j;
	var dictionaryLength = dictionaryTests.length;

	var dictionaryValues;

	var numericResult = $("numericResult_" + index);
	var dictionaryResult = $("dictionaryResult_" + index);
	var multiResult = $("MultiSelect_" + index);
	dropAsmSelect(multiResult);
	var resultSelection = (isDictionary == true )?dictionaryResult:multiResult;

	for( i = 0; i < dictionaryLength; i++){
		if( isDictionary || isMultiSelect ){
			numericResult.style.display = "none";
			numericResult.value = "";
			dictionaryValues = dictionaryMap[testId];
			var j;
			if ( isMultiSelect ) { // there is no blank one at the top in a multiselect option list.
				resultSelection.options.length = 0;
				j = 0;
			} else { // leave the blank one at the top for single select dictionary option list.
				resultSelection.options.length = 1;
				j = 1;
			}

			for( var dictionaryId in dictionaryValues ){
				var name = dictionaryValues[dictionaryId];
				resultSelection.options[j++] = new Option( dictionaryValues[dictionaryId], dictionaryId);
			}
			break;
		}
	}
	dictionaryResult.style.display="none";
	numericResult.style.display = "none";
	multiResult.style.display="none";

	if ( isMultiSelect) {
		$("referredType_" + index).value = "M";
		multiResult.style.display="inline";
		$("MultiSelect_" + index).selectedIndex = -1;
		// build asm multi select around our newly created node.
		var ms = $jq("#MultiSelect_" + index);
		ms.asmSelect({removeLabel: "X"
			    // , debugMode: true
				});
		if ( multiResult.onChange == null ) {
			ms.unbind('change');
			ms.change(function(e, data) {
					handleMultiSelectChange( e, data );
					});
		}
	}
	if (isDictionary) {
		$("referredType_" + index).value = "D";
		dictionaryResult.style.display="inline";
	}
	if( isNumeric ){
		$("referredType_" + index).value = "N";
		numericResult.style.display = "inline";
		dictionaryResult.value = 0;
		numericResult.value = "";
	}
}

function dropAsmSelect(multiResult) {
	if (multiResult.parentNode.nodeName == "DIV" ) {
		multiResult.parentNode.parentNode.replaceChild(multiResult, multiResult.parentNode);
	}
}

function /*void*/ insertNewTestRequest(button, index){
	var cell, newRow;
	var insertRow = 0;
	var addedRowCounter = $("addedRowCount_" + index );
	var parent = button.parentNode;
	var newRowMarker = index + "_" + addedRowCounter.value;
	addedRowCounter.value++;

	while( parent && parent.tagName != "TR" ){
		parent = parent.parentNode;
	}

	additionalTests = parent.rowIndex - $("referralRow_" + index ).rowIndex;

	newRow = $("mainTable").insertRow( parent.rowIndex );
	newRow.className = parent.className;
	newRow.id = "additionalRow_" + newRowMarker;

	cell = newRow.insertCell(0);
	cell.colSpan = 3;
	//used to create the xml wad
	cell.innerHTML = '<input type="hidden"  name="addedTest"  value="' + index + '" id="' + newRowMarker +  '"  >';
	cell.innerHTML += '<input type="hidden"  value="N" id="referredType_' + newRowMarker +  '"  >';

	newRow.insertCell(1).innerHTML = "<input type=\"button\"  name=\"remove\"  value=\"" + '<%= StringUtil.getMessageForKey("label.button.remove")%>' + "\" onclick=\"removeRow('" + newRowMarker + "');\" class=\"textButton\"  >";

	cell = newRow.appendChild( $("testSelection_" + index).parentNode.cloneNode(true));

	var selectionNode = cell.getElementsByTagName("select" )[0];
	selectionNode.id = "testSelection_" + newRowMarker;
	selectionNode.selectedIndex = 0;
	selectionNode.onchange = function(){ markModified( index +""); updateResultField(newRowMarker+"") }; //blank "" force to string

	cell = newRow.appendChild( $("resultCell_" + index).cloneNode(true));
	cell.id = "";

	var numericResult = cell.getElementsByTagName("input")[0];
	numericResult.id = "numericResult_"  + newRowMarker;
	numericResult.style.display = "inline";
	numericResult.value = "";

	var dictionaryResult = cell.getElementsByTagName("select")[0];
	var blankDic = dictionaryResult.cloneNode(false); // Make a shallow copy
	blankDic.id = "dictionaryResult_"  + newRowMarker;
	blankDic.style.display = "none";
	blankDic.value = "0";
	cell.replaceChild(blankDic, dictionaryResult);

	var multiSelect = cell.getElementsByTagName("select")[1];;
	var multiSelectAsmDiv = cell.getElementsByTagName("div")[0];
	if (multiSelectAsmDiv ) {
		multiSelect = cell.getElementsByTagName("select")[2];;
		cell.replaceChild( multiSelect, multiSelectAsmDiv );
	}

	multiSelect.id = "MultiSelect_" + newRowMarker;
	multiSelect.style.display = "none";
	multiSelect.title='<%= StringUtil.getMessageForKey("result.multiple_select")%>'

	var resultMulti = cell.getElementsByTagName("input")[1];
	resultMulti.id = "resultMultiSelect_" + newRowMarker;
	resultMulti.value = "";

	var reportDate = newRow.appendChild( $("reportDate_" + index).parentNode.cloneNode(true));
	reportDate.colSpan = 2;
	var reportDateWidget = reportDate.getElementsByTagName("input")[0]
	reportDateWidget.id = "referredReportDate_"  + newRowMarker;
	reportDateWidget.value = "";
	
	$("saveButtonId").disabled = true;
}

function /*void*/ removeRow(rowMarker){
	var row = $("additionalRow_" + rowMarker);
	$("mainTable").deleteRow( row.rowIndex );
}

function /*void*/ handleCancelAction( button, index ){
	var cancelField = $("cancel_" + index )

	if( cancelField.value == "true" ){
		cancelField.value = "false";
		button.value = "Cancel";
	}else{
		cancelField.value = "true";
		button.value = "Restore";
	}
}

function /*void*/ validateDateFormat( dateElement ){
	var valid = true;
	if( dateElement.value.length > 0 ){
		var dateRegEx = new RegExp("\\d{2}/\\d{2}/\\d{4}");
		valid = dateRegEx.test(dateElement.value);
	}

	dateElement.style.borderColor = valid ? "" : "red";
}

function /*void*/ setXMLWads(){
	var i = 0;
	var XMLWadElements = $$(".XMLWad");
	var XMLWad;

	var addedTests = document.getElementsByName("addedTest");

	for( i; i < addedTests.length ; i++ ){
		addTestToXMLWad(addedTests[i] );
	}

	for( i = 0; i < XMLWadElements.length; i++ ){
		if( XMLWadElements[i].value ){
			XMLWad = "<?xml version='1.0' encoding='utf-8'?><tests>"
			XMLWad += XMLWadElements[i].value;
			XMLWad += "</tests>"
			//alert(XMLWad);
			XMLWadElements[i].value = XMLWad;
		}
	}

}

function /*void*/ addTestToXMLWad(headElement){
	var referralIndex = headElement.value;
	var marker = headElement.id;

	var testSelection = $("testSelection_" + marker);
	var testId = testSelection.options[testSelection.selectedIndex].value;
	var reportDate = $("referredReportDate_"  + marker).value;
	var resultType = $("referredType_" + marker).value;
	var result = $("numericResult_"  + marker).value;
	if ( resultType == "D" ) {
		result = $("dictionaryResult_"  + marker).value ;
	}
	if ( resultType == "M" ) {
		result = $("resultMultiSelect_"  + marker).value;
	}

	var existingXML_Element = $("textXML_" + referralIndex );
	var existingXML = existingXML_Element.value;
	existingXML += '<test ';
		existingXML += 'testId=\"' + testId + '\" ';
		existingXML += 'resultType=\"' + resultType + '\" ';
		existingXML += 'result=\"' + result + '\" ';
		existingXML += 'report =\"' + reportDate + '\" ';
	existingXML += ' />';

	existingXML_Element.value = existingXML;
}

function /*void*/ savePage(){
	setXMLWads();

  window.onbeforeunload = null; // Added to flag that formWarning alert isn't needed.
	var form = window.document.forms[0];
	form.action = '<%=formName%>'.sub('Form','') + "Update.do";
	form.submit();
}
function  /*void*/ setMyCancelAction(form, action, validate, parameters)
{
	//first turn off any further validation
	setAction(window.document.forms[0], 'Cancel', 'no', '');
}
</script>

<logic:notEmpty name="<%=formName%>"  property="referralItems" >

<table width="100%" border="0" cellspacing="0" cellpadding="1" id="mainTable" >
  <tr>
  	<th colspan="5" class="headerGroup" ><bean:message key="referral.header.group.request"/></th>
  	<th colspan="3" class="leftVertical headerGroup"><bean:message key="referral.header.group.results"/></th>
  </tr>
  <tr >
    <th><bean:message key="referral.reason"/><span class="requiredlabel">*</span></th>
    <th><bean:message key="referral.referer"/></th>
    <th><bean:message key="referral.institute"/><span class="requiredlabel">*</span></th>
    <th><bean:message key="referral.sent.date"/></th>
    <th><bean:message key="test.testName"/><span class="requiredlabel">*</span></th>
    <th class="leftVertical"><bean:message key="result.result"/></th>
    <th><bean:message key="referral.report.date"/></th>
    <th><bean:message key="label.button.cancel.referral"/></th>
  </tr>
  <logic:iterate id="referralItems" name="<%=formName%>"  property="referralItems" indexId="index" type="ReferralItem" >
	<html:hidden  styleId='<%= "textXML_" + index %>' name="referralItems" property="additionalTestsXMLWad" indexed="true" styleClass="XMLWad" />
	<html:hidden styleId='<%= "referralResultId_" + index %>' name="referralItems" property="referralResultId" indexed="true" />
	<html:hidden styleId='<%= "referralId_" + index %>' name="referralItems" property="referralId" indexed="true" />
	<html:hidden styleId='<%= "referredType_" + index %>' name="referralItems" property="referredResultType" indexed="true" />
	<html:hidden styleId='<%= "modified_" + index %>' name="referralItems" property="modified" indexed="true" />
	<html:hidden styleId='<%= "causalResultId_" + index %>' name="referralItems" property="casualResultId" indexed="true" />
	<input type="hidden"
		   value='<%= referralItems.getAdditionalTests() == null ? 0 : referralItems.getAdditionalTests().size()  %>'
		   id='<%="addedRowCount_" + index%>' />
	<bean:define id="rowColor" value='<%=(index % 2 == 0) ? "oddRow" : "evenRow" %>' />

	<tr class='<%=rowColor%>Head' id='<%="referralRow_" + index%>'  >
		<td colspan="2" class="HeadSeperator" >
		 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%= StringUtil.getContextualMessageForKey("resultsentry.accessionNumber") %>: <b><bean:write name="referralItems" property="accessionNumber"/></b>
    	</td>
    	<td colspan="3" class="HeadSeperator" >
    		<bean:message key="referral.request.date"/>: <b><bean:write name="referralItems" property="referralDate"/></b>
    	</td>
    	<td colspan='3' class="HeadSeperator leftVertical"  >
	</tr>
	<tr class='<%=rowColor%>Head' id='<%="referralRow_" + index%>' >
		<td colspan="2">
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="resultsentry.sampleType"/>: <b><bean:write name="referralItems" property="sampleType"/></b>
		</td>
		<td colspan="2" >
			<bean:message key="test.testName"/>: <b><bean:write name="referralItems" property="referringTestName"/></b>
		</td>
		<td >
			<bean:message key="result.original.result"/>: <b><bean:write name="referralItems" property="referralResults"/></b>
		</td>
		<td colspan="3"  class="leftVertical" >
			&nbsp;
		</td>
	</tr>
	<tr class='<%=rowColor + " requiredRow"%>' id='<%="referralRow_" + index%>' >
		<td>
			<select name="<%="referralItems[" + index + "].referralReasonId"%>"
			        id='<%="referralReasonId_" + index%>'
			        onchange='<%="markModified(\"" + index + "\"); " %>' />
			<logic:iterate id="optionValue" name='<%=formName %>' property="referralReasons" type="IdValuePair" >
					<option value='<%=optionValue.getId()%>'  <%if(optionValue.getId().equals(referralItems.getReferralReasonId())) out.print("selected");%>  >
							<bean:write name="optionValue" property="value"/>
					</option>
			</logic:iterate>
			</select>
		</td>
		<td>
			<html:text name="referralItems"
					   property="referrer"
					   onchange='<%="markModified(\'" + index + "\'); " %>'
					   indexed="true"/>
		</td>
		<td>
			<select name='<%="referralItems[" + index + "].referredInstituteId"%>'
					class="required"
					onchange='<%="markModified(\"" + index + "\");"%>' >
			<logic:iterate id="optionValue" name='<%=formName %>' property="referralOrganizations" type="IdValuePair" >
				<option value='<%=optionValue.getId()%>' <%if(optionValue.getId().equals(referralItems.getReferredInstituteId())) out.print("selected");%>   >
					<bean:write name="optionValue" property="value"/>
				</option>
			</logic:iterate>
			</select>
		</td>
		<td>
			<html:text name='referralItems'
					   property="referredSendDate"
					   indexed="true"
					   size="8"
					   maxlength="10"
					   onchange='<%="markModified(\'" + index + "\');  validateDateFormat(this);"%>'
					   styleId='<%="sendDate_" + index %>'/>
		</td>
		<td>
			<select name='<%="referralItems[" + index + "].referredTestId"%>'
					onchange='<%="markModified(\"" + index + "\"); updateResultField(\"" + index + "\");"%>'
					id='<%="testSelection_" + index%>' class="required">
					<option value='0' ></option>

			<logic:iterate id="optionValue" name='referralItems' property="testSelectionList" type="IdValuePair" >
				<option value='<%=optionValue.getId()%>' <%if(optionValue.getId().equals(referralItems.getReferredTestId())) out.print("selected");%>   >
					<bean:write name="optionValue" property="value"/>
				</option>
			</logic:iterate>
			</select>
		</td>
		<td class="leftVertical" id='<%="resultCell_" + index %>'><% String referredResultType = referralItems.getReferredResultType(); %>
			<input type="text"
			       name='<%= "referralItems[" + index + "].referredResult" %>'
			       value='<%= referralItems.getReferredResult() %>'
			       onchange='<%= "markModified(\"" + index + "\");" %>'
			       <%if(!("N".equals(referredResultType) || "A".equals(referredResultType) || "R".equals(referredResultType))) out.print("style='display : none'");%>
			       id='<%= "numericResult_" + index %>'
				     />
			<select name='<%= "referralItems[" + index + "].referredDictionaryResult" %>'
			       id='<%= "dictionaryResult_" + index %>'
			       onchange='<%="markModified(\"" + index + "\"); " %>'
			       <%if(!"D".equals(referredResultType)) out.print("style='display: none'");%>
				   >
				<option value="0" ></option>
				<logic:notEmpty name="referralItems" property="dictionaryResults">
				<logic:iterate id="optionValue" name="referralItems" property="dictionaryResults" type="IdValuePair" >
					<option value='<%=optionValue.getId()%>'  <%if(optionValue.getId().equals(referralItems.getReferredDictionaryResult())) out.print("selected");%>  >
							<bean:write name="optionValue" property="value"/>
					</option>
				</logic:iterate>
				</logic:notEmpty>
			</select>
			<select
				   name='<%= "referralItems[" + index + "].referredDictionaryResult" %>'
			       id='<%= "MultiSelect_" + index %>'
			       onchange='<%="markModified(\"" + index + "\"); " %>'
				   <%if(!("M".equals(referredResultType))) out.print("style='display: none'");%>
				   multiple="multiple"
				   title='<%= StringUtil.getMessageForKey("result.multiple_select")%>'
				   >
				<logic:notEmpty name="referralItems" property="dictionaryResults">
					<logic:iterate id="optionValue" name="referralItems" property="dictionaryResults" type="IdValuePair" >
						<option value='<%=optionValue.getId()%>'
							<%if(StringUtil.textInCommaSeperatedValues(optionValue.getId(), referralItems.getReferredMultiDictionaryResult())) out.print("selected"); %>
						  >
								<bean:write name="optionValue" property="value"/>
						</option>
					</logic:iterate>
				</logic:notEmpty>
			</select>
			<html:hidden name="referralItems"  indexed="true" property="referredMultiDictionaryResult" styleId='<%= "resultMultiSelect_" + index %>' />
		</td>
		<td>
			<html:text name='referralItems'
					   property="referredReportDate"
					   indexed="true"
					   size="8"
					   maxlength="10"
					   onchange='<%="markModified(\'" + index + "\');  validateDateFormat(this);" %>'
					   styleId='<%="reportDate_" + index %>'/>
		</td>
		<td>
			<html:checkbox name="referralItems" property="canceled" onchange='<%="markModified(\'" + index + "\'); value=true" %>' indexed="true"  />
		</td>
	</tr>
	<logic:notEmpty name="referralItems"  property="additionalTests" >
	<logic:iterate id="additionalTests" name="referralItems"  property="additionalTests" indexId="testIndex" type="ReferredTest" >
		<tr class='<%= rowColor %>' ><% referredResultType = additionalTests.getReferredResultType(); %>
			<td colspan="4" style='text-align: right'>
				<input type="hidden"
				       name='<%="referralItems[" + index + "].additionalTests[" + testIndex + "].referralResultId" %>'
				       value='<%=additionalTests.getReferralResultId() %>' />
				<input type="hidden"
					   id='<%="referredType_" + index + "_" + testIndex %>'
				       name='<%="referralItems[" + index + "].additionalTests[" + testIndex + "].referredResultType" %>'
				       value='<%=additionalTests.getReferredResultType() %>' />
				<label for='<%="remove" + index + "_" + testIndex %>' ><bean:message key="label.button.remove"/></label>
				<input type="checkbox"
				       name='<%="referralItems[" + index + "].additionalTests[" + testIndex + "].remove" %>'
				       id='<%="remove" + index + "_" + testIndex %>'
				       onchange='<%="markModified(\"" + index + "\");" %>' >
			</td>
			<td>
				<select name='<%="referralItems[" + index + "].additionalTests[" + testIndex + "].referredTestId" %>'
						onchange='<%="markModified(\"" + index + "\"); updateResultField(\"" + index + "_" + testIndex + "\");" %>'
						id='<%="testSelection_" + index + "_" + testIndex %>' class="required" />
						<option value='0' ></option>
						<logic:iterate id="optionValue" name='referralItems' property="testSelectionList" type="IdValuePair" >
							<option value='<%=optionValue.getId()%>' <%if(optionValue.getId().equals(additionalTests.getReferredTestId())) out.print("selected"); %>  >
								<bean:write name="optionValue" property="value"/>
							</option>
						</logic:iterate>
				</select>
			</td>
			<td class="leftVertical">
				<input type="text"
					   name='<%="referralItems[" + index + "].additionalTests[" + testIndex + "].referredResult" %>'
					   value='<%= additionalTests.getReferredResult() %>'
					   onchange='<%="markModified(\"" + index + "\");" %>'
					   id='<%="numericResult_" + index + "_" + testIndex %>'
					    <%if(!("N".equals(referredResultType)) || "A".equals(referredResultType)) out.print("style='display : none'");%> />


				<select name='<%= "referralItems[" + index + "].additionalTests[" + testIndex + "].referredDictionaryResult" %>'
			       id='<%= "dictionaryResult_" + index + "_" + testIndex  %>'
			       onchange='<%="markModified(" + index + ");" %>'
				   <%if(!"D".equals(additionalTests.getReferredResultType())) out.print("style='display: none'");%>  >
					<option value="0" ></option>
					<logic:notEmpty name="additionalTests" property="dictionaryResults">
						<logic:iterate id="optionValue" name="additionalTests" property="dictionaryResults" type="IdValuePair" >
							<option value='<%=optionValue.getId()%>'  <%if(optionValue.getId().equals(additionalTests.getReferredDictionaryResult())) out.print("selected");%>  >
									<bean:write name="optionValue" property="value"/>
							</option>
						</logic:iterate>
					</logic:notEmpty>
				</select>
				<select name='<%= "referralItems[" + index + "].additionalTests[" + testIndex + "].referredDictionaryResult" %>'
				       id='<%= "MultiSelect_" + index + "_" + testIndex %>'
				       onchange='<%="markModified(\"" + index + "\"); " %>'
					   <%if(!("M".equals(referredResultType))) out.print("style='display: none'");%>
					   multiple="multiple"
					   title='<%= StringUtil.getMessageForKey("result.multiple_select")%>'
					   >
					<logic:notEmpty name="additionalTests" property="dictionaryResults">
					<logic:iterate id="optionValue" name="additionalTests" property="dictionaryResults" type="IdValuePair" >
						<option value='<%=optionValue.getId()%>'
							<%if(StringUtil.textInCommaSeperatedValues(optionValue.getId(), additionalTests.getReferredMultiDictionaryResult())) out.print("selected"); %> >
								<bean:write name="optionValue" property="value"/>
						</option>
					</logic:iterate>
					</logic:notEmpty>
				</select>
				<input type="hidden" style="display: none"
					id='<%="resultMultiSelect_" + index + "_" + testIndex %>'
					name='<%= "referralItems[" + index + "].additionalTests[" + testIndex + "].referredMultiDictionaryResult" %>' />
			</td>
			<td>
				<input type="text"
					   name='<%="referralItems[" + index + "].additionalTests[" + testIndex + "].referredReportDate" %>'
					   value='<%= additionalTests.getReferredReportDate() %>'
					   onchange='<%="markModified(\"" + index + "\");  validateDateFormat(this);" %>'
					   size="8"
					   maxlength="10"/>
			</td>
			<td >&nbsp;</td>
		</tr>
	</logic:iterate>
	</logic:notEmpty>
	<tr class='<%= rowColor %>'>
		<td colspan="3"></td>
		<td colspan='1' >
			<input type="button"
				   name="addRequest"
				   value="<%= StringUtil.getMessageForKey("referral.addTest")%>"
				   class="textButton"
				   onclick='<%="insertNewTestRequest(this," + index + ");"  %>' >
		</td>
		<td colspan='1' align="right" style="padding:5px">
						 	<img src="./images/note-add.gif"
						 	     onclick='<%= "showHideNotes(" + index + ");" %>'
						 	     id='<%="showHideButton_" + index %>'
						    />
			<input type="hidden" id='<%="hideShow_" + index %>' value="hidden" />
		</td>
		<td colspan='3' class="leftVertical">&nbsp</td>
	</tr>
	<logic:notEmpty name="referralItems" property="pastNotes" >
		<tr class='<%= rowColor %>'>
			<td  valign="top" align="right">Prior Notes:</td>
			<td colspan="4" align="left" >
				<%= referralItems.getPastNotes() %>
			</td>
			<td colspan='3' class="leftVertical">
		</tr>
	</logic:notEmpty>
	<tr id='<%="noteRow_" + index %>'
		class='<%= rowColor %>'
		style="display: none;">
		<td valign="top" align="right"><bean:message key="note.note"/>:</td>
		<td colspan="6" align="left" >
			<html:textarea styleId='<%="note_" + index %>'
						   onchange='<%="markModified(" + index + ");"%>'
					   	   name="referralItems"
			           	   property="note"
			           	   indexed="true"
			           	   cols="80"
			           	   rows="3" />
		</td>
	</tr>
  </logic:iterate>
</table>
</logic:notEmpty>
<logic:empty name="<%=formName%>"  property="referralItems" >
<h2><bean:message key="referral.noReferralItems"/></h2>
</logic:empty>

<script type="text/javascript" language="JavaScript1.2">
var dirty = false;
//all methods here either overwrite methods in tiles or all called after they are loaded

function /*void*/ makeDirty(){
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

</script>
