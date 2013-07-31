<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date, java.util.List,
	org.apache.struts.Globals,
	us.mn.state.health.lims.common.util.SystemConfiguration,
	us.mn.state.health.lims.testresult.valueholder.TestResult,
	us.mn.state.health.lims.result.valueholder.TestAnalyte_TestResults,
	us.mn.state.health.lims.common.action.IActionConstants" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="idSeparator" value='<%= SystemConfiguration.getInstance().getDefaultIdSeparator() %>' />
<bean:define id="titerType" value='<%= SystemConfiguration.getInstance().getTiterType() %>' />
<bean:define id="numericType" value='<%= SystemConfiguration.getInstance().getNumericType() %>' />
<!--bugzilla 2227-->
<bean:define id="accessionNumberParm" value='<%= IActionConstants.ACCESSION_NUMBER%>' />
<%--bugzilla 2254--%>
<bean:define id="unsatisfactoryResultValue" value='<%= IActionConstants.UNSATISFACTORY_RESULT%>' />
<bean:define id="textSeparator" value='<%= SystemConfiguration.getInstance().getDefaultTextSeparator() %>' />


<%--AIS - bugzilla 1863/1891 Many changes --%>
<%--bugzilla 1908 - mods for tomcat logic tags--%>
<%--bugzilla 1933 change for dictType, titerType, numericType error--%>
<%--bugzilla 1942 status changes - allow result being set to blank--%>
<%!

String allowEdits = "true";
String analysisId = "";
String errorChangeToNoResult = "";
//bugzilla 2467
String popupMessage = "";
//bugzilla 2187
String noResultFound = "";
//bugzilla 2232
String resultDetail = "";
String resultConfirm = "";
%>

<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

analysisId = (String)request.getAttribute("analysisId");
java.util.Locale locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
errorChangeToNoResult = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "resultsentry.changetonoresult.error");
                    
//bugzilla 2187
noResultFound = us.mn.state.health.lims.common.util.resources.ResourceLocator
			        .getInstance().getMessageResources().getMessage(locale,
					"errors.no.result.found");                                  

//bugzilla 2322
resultDetail =      us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "resultentry.detail");
                    
resultConfirm =     us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "resultentry.confirm");

//bugzilla 2467
popupMessage =  	us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "batchresultsentry.popup.display");
%>

<!--bugzilla 2322-->
<!--bugzilla 2628 prevent js problem with single quote in dict entry-->
<% int index = 0; %>
<script language="JavaScript">
    var dictArray = new Array();   
</script>  
<logic:notEmpty name="<%=formName%>" property="sample_TestAnalytes">
    <logic:iterate id="ta_Trs" name="<%=formName%>" indexId="analyte_ctr" property="testAnalyte_TestResults" type="us.mn.state.health.lims.result.valueholder.TestAnalyte_TestResults" >
       
        <!--build dictionary array list-->
        <script>
        <%
            List testResults = ta_Trs.getTestResults();
            for ( int i=0; i<testResults.size(); i++ ) {
                TestResult tr = (TestResult)testResults.get(i);           
            %>            
                dictArray[<%=index++%>] = "<%=tr.getId()%>" + "|TRID|" + "<%= tr.getValue() %>";
            <%                
            }
        %> 
        </script>          
         
    </logic:iterate>
</logic:notEmpty> 

<script language="JavaScript1.2">
var BLANK = "b|l|a|n|k";

function getArrayOfSelectedRows() {
  var fieldObj = window.document.forms[0].elements['selectedRows'];
  
  var rows = new Array();
  
  if (fieldObj != null) {
    //If only one checkbox
    if (fieldObj[0] == null) {
       if (fieldObj.value != null && fieldObj.checked == true) {
         rows[0] = fieldObj.value;
       }
    } else {
      var j = 0;
      for (var i = 0; i < fieldObj.length; i++) {
         if (fieldObj[i].checked == true) {
            rows[j++] = fieldObj[i].value;
         }
       }
    }
   }
  return rows;
}


//bugzilla 2254
// Has an input-capable field on the form changed to UNSATISFACTORY (route to qa events)? return list of accession numbers
function foundUnsatisfactoryResult(form) {
    var elementsFound = '';
	var elemLength = form.elements.length;
	for (var i=0; i < elemLength; i++) {
		var eElem = form.elements[i];
		
		if (eElem.disabled == true || eElem.readOnly == true)
		{	// Field is disabled, so don't need to check if dirty
			continue;
		}
		var eName = eElem.name;
		if ( eName.length == 0 )
		{	// Name of field is 0 length, so don't do check
			continue;
		}
		
		var eType = eElem.type;
		
		if( "hidden" == eType ) {
				continue;
		}
		
		if ("text" == eType || "TEXTAREA" == eElem.tagName) {
			//bugzilla 2489
			if (eElem.value != eElem.defaultValue) {
			   if (eElem != null && eElem.value != null && eElem.value.indexOf('<%=unsatisfactoryResultValue%>') >= 0) {
				elementsFound += '<%=textSeparator%>' + eElem.name;
			   }
			}
			
		}

	
		if ("checkbox" == eElem.type || "radio" == eElem.type) {
                continue;
		}
		
		if ("SELECT" == eElem.tagName) {
			checkDftSelected(eElem);
			var numOpts = eElem.options.length;
			for (var j=0; j < numOpts; j++) {
				var eopt = eElem.options[j];
				if (eopt.selected != eopt.defaultSelected && eopt.value != null && eopt.value.length > 0) {
                  //bugzilla 2489
				  if (eopt != null && eopt.text != null && eopt.text.length > 0 && eopt.text.indexOf('<%=unsatisfactoryResultValue%>') >= 0 && eopt.selected) {
					elementsFound += '<%=textSeparator%>' + eElem.name;
				  }
				}

			}
		}
	}
	return elementsFound;
}

function saveThis(form) {
  form.elements['stringOfUnsatisfactoryResults'].value = '';
  var stringOfUnsatisfactoryResults = foundUnsatisfactoryResult(form);
  if (stringOfUnsatisfactoryResults != '') {
     form.elements['stringOfUnsatisfactoryResults'].value = stringOfUnsatisfactoryResults;
  }
  setAction(form, 'Update', 'yes', '?ID=');
}

function validateForm(form) {
    //return validateBatchResultsEntryForm(form);
    return true;
}

function editMultiples(form) {
//alert("I am in editMultiples!");

//Were selections made???
  var rows = getArrayOfSelectedRows();
  
  if (rows.length == 0) return false;


  
    //if  no errors otherwise on page -> go to add test popup
	var context = '<%= request.getContextPath() %>';
	var server = '<%= request.getServerName() %>';
	var port = '<%= request.getServerPort() %>';
	var scheme = '<%= request.getScheme() %>';
	
	
	var hostStr = scheme + "://" + server;
	if ( port != 80 && port != 443 )
	{
		hostStr = hostStr + ":" + port;
	}
	hostStr = hostStr + context;

	// Get the sessionID
	 var sessionid = '';

	 var sessionIndex = form.action.indexOf(';');
	 if(sessionIndex >= 0){
		 var queryIndex = form.action.indexOf('?');
		 var length = form.action.length;
		 if (queryIndex > sessionIndex) {
		 	length = queryIndex;
		 }
		 sessionid = form.action.substring(sessionIndex,length);
	 }
	
	var href = context + "/BatchResultsEntryEditMultiplePopup.do" + sessionid;
    //alert("href "+ href);
	
	createPopup( href, null, null );

}

function getSelectedTestResultIds() {
  return document.forms[0].selectedTestResultIds;
}

function setResults(editMultipleForm) {

  var rows = getArrayOfSelectedRows();
    
  for (var i = 0; i < rows.length; i++) {
      updateFromEditMultipleForm(rows[i]);
  }
    //clear selections
  changeAllCheckBoxStates(false);


}

//this is to fix datagrid problem: 2 divs, cloneNode(), form elements repeated, ignore the first two sets
//bugzilla 2124 (removed parameter val - not sure why this was added)
function getCorrectFieldObj(form, element) {
    var fieldObj = null;
	for (var i = form.elements.length -1; i >= 0; i--) {
		if (form.elements[i].name == element) {      
			fieldObj = form.elements[i];      
			break;
		} 
	}
    return fieldObj;
}

//bugzilla 2001 - isolate this code and make clearer - this is for textfield highlight 
function highlightResultChanged(field) {
    
    var correctFieldObj = getCorrectFieldObj(window.document.forms[0], field.name); 
    if (isFormFieldDirty(correctFieldObj)) {
       correctFieldObj.className = 'highlight';
    } else {
       correctFieldObj.className = 'withouthighlight';
    }
}
    
//bugzilla 2001 - fix bug in determining whether titer/numeric have been blanked out (use isDirty instead)
function checkBlankResultForNotes(field, notesAttached) {
//if result selection changed and the value is now blank and a note exists then we need to stop them
   if (notesAttached && isFormFieldDirty(field) && field.value == '') {
        alert('<%=errorChangeToNoResult%>');
        return true;
   }
   return false;
}

//bugzilla 2001 isolating titer/numeric validation logic (removed from checkBlankResultForNotes for clarity)
function validateTiterNumeric(field, testResultId) {

  if (field.value != '' ){
    	validateResultValue(field, testResultId );
  }

  highlightResultChanged(field);

  return false;
}


function processFailure(xhr) {
  //ajax call failed
}

function processSuccess(xhr) {
  var message = xhr.responseXML.getElementsByTagName("message")[0];
  var formfield = xhr.responseXML.getElementsByTagName("formfield")[0];
  //alert("I am in parseMessage and this is message, formfield " + message + " " + formfield);
  setMessage(message.childNodes[0].nodeValue, formfield.childNodes[0].nodeValue);
  //setSave();
}

//bugzilla 2361 modified handling of numeric/titer type values
function validateResultValue(fieldI, testResultId){
	//alert ("In validateResultValue "+ fieldI.value + ""+ testResultId );	
	var value = fieldI.value;
    new Ajax.Request (
                  'ajaxXML',  //url
                   {//options
                     method: 'get', //http method
                     parameters: 'provider=ResultsValueValidationProvider&field=fieldI&val=' + value + '&trId=' + testResultId + '&clientType=js',      //request parameters
                     //indicator: 'throbbing'
                     onSuccess:  processSuccess,
                     onFailure:  processFailure
                   }
                  );
}

//bugzilla 2361
function setMessage(message, field) {  
    var listOfMessage = message;
    var messageArr = new Array();

    if (listOfMessage.indexOf('<%=idSeparator%>') == 0){				
    	listOfMessage = listOfMessage.substring(1);
    }
    messageArr = listOfMessage.split('<%=idSeparator%>');
    if (messageArr[0] == "invalid"){ 
	  	alert(messageArr[1]);	             
    }      
}
var req;

//bugzilla 1772 (the checkDftSelected() function in utilities doesn't work in this scenario...Why not?
function myCheckDftSelected(eElem)
{
	var numOpts = eElem.options.length;
	for (var j=0; j < numOpts; j++) {
		var eopt = eElem.options[j];
		if (eopt.defaultSelected) {
			return;
		}
	}
	
	for (var j=0; j < numOpts; j++) {
		var eopt = eElem.options[j];
		if (eopt.selected) {
		   eopt.defaultSelected = true;
		}
	}
	
	for (var j=0; j < numOpts; j++) {
		var eopt = eElem.options[j];
		if (eopt.selected) {
			return;
		}
	}
	
	//this can only happen if nothing selected or defaultSelected
	if (numOpts > 0) {
	   eElem.options[0].selected = true;
   	   eElem.options[0].defaultSelected = true;
	}
}

function updateFromEditMultipleForm(row) {
	var selectedTestResultIds = window.document.forms[0].elements['selectedTestResultIds'].value;
	var selectedTestResultValues = window.document.forms[0].elements['selectedTestResultValues'].value;     
	var selIdArr = new Array();
	var selValArr = new Array();
	
	if (selectedTestResultIds.indexOf('<%=idSeparator%>') == 0) {
	selectedTestResultIds = selectedTestResultIds.substring(1);
	}
	
	if (selectedTestResultValues.indexOf('<%=idSeparator%>') == 0) {
	   selectedTestResultValues = selectedTestResultValues.substring(1);
	 }
	
	selIdArr = selectedTestResultIds.split('<%=idSeparator%>');
	selValArr = selectedTestResultValues.split('<%=idSeparator%>');
	
	for (var i = 0; i < selIdArr.length; i++) {
	  var rsltId = selIdArr[i];
	  if (rsltId == BLANK) {
	    //1772
	    rsltId = '';
	  }
	  var fieldObj; 
	  var rsltVal = selValArr[i];
	  if (rsltVal == BLANK) {               
	    rsltVal = '';
	  }	
	  
	  if (rsltVal != 'null'){
		//THIS IS TO FIX DATAGRID PROBLEM WITH DIVS AND CLONED FORM ELEMENTS (TEXT BOXES)              
		var fieldObj = getCorrectFieldObj(window.document.forms[0], 'sample_TestAnalytes[' + row + '].testResultValues[' + i + ']');
        //bugzilla 2124 set the value on the main form (this line was missing..)
       	fieldObj.value = rsltVal;
       	//bugzilla 2159
     	highlightResultChanged(fieldObj)
	  }else{
		//THIS IS TO FIX DATAGRID PROBLEM WITH DIVS AND CLONED FORM ELEMENTS (SELECT DROP DOWNS) 	              
		var fieldObj = getCorrectFieldObj(window.document.forms[0], 'sample_TestAnalytes[' + row + '].sampleTestResultIds[' + i + ']');
		   
		myCheckDftSelected(fieldObj);			
		for (var j=0; j<fieldObj.options.length; j++) {
			//bugzilla 1772 highlight changed fields
			fieldObj.className = 'withouthighlight';
			if (rsltId == fieldObj.options[j].value)  {
				//bugzilla 1772 highlight changed fields
				if (!fieldObj.options[j].defaultSelected) {
					fieldObj.className = 'highlight';
				}
				fieldObj.options[j].selected = true;
				break;
	  			}
			}
	    }
	}
}

function changeAllCheckBoxStates(checkState)
{
      var selectedRows = window.document.forms[0].elements['selectedRows'];
      // Toggles through all of the checkboxes defined in the CheckBoxIDs array
      // and updates their value to the checkState input parameter
      if (selectedRows != null)
      {
        //If only one checkbox
        if (selectedRows[0] == null) {
             selectedRows.checked = checkState;
        } else {
      
      
         for (var i = 0; i < selectedRows.length; i++)
            selectedRows[i].checked = checkState;
        }
      } 
//bugzilla 2467
	  if (checkState){
		alert( (selectedRows.length/4) + ' <%=popupMessage%>');
      }
}

//bugzilla 2227
function resultsEntryHistoryBySamplePopup (form, accessionNumber) {

  //if there is an error on the page we cannot go to add test
   //clear button clicked flag to allow add test again
   //if (isSaveEnabled() != true) {
       //clearAddTestClicked();
   //}
   
    //if  no errors otherwise on page -> go to add test popup
	var context = '<%= request.getContextPath() %>';
	var server = '<%= request.getServerName() %>';
	var port = '<%= request.getServerPort() %>';
	var scheme = '<%= request.getScheme() %>';
	
	
	var hostStr = scheme + "://" + server;
	if ( port != 80 && port != 443 )
	{
		hostStr = hostStr + ":" + port;
	}
	hostStr = hostStr + context;

	// Get the sessionID
	 var sessionid = '';

	 var sessionIndex = form.action.indexOf(';');
	 if(sessionIndex >= 0){
		 var queryIndex = form.action.indexOf('?');
		 var length = form.action.length;
		 if (queryIndex > sessionIndex) {
		 	length = queryIndex;
		 }
		 sessionid = form.action.substring(sessionIndex,length);
	 }
 
    var param = '?' + '<%=accessionNumberParm%>' + '=' + accessionNumber;
 	var href = context + "/ResultsEntryHistoryBySamplePopup.do" + param + sessionid;
    //alert("href "+ href);
	
	createPopup( href, 1250, 500 );
}

//bugzilla 2322
function viewDictionary(form) {
    var selectedId = (form.options[form.selectedIndex].value);
    var defaultBoxLength = "20";
    var select;
    for ( var i=0; i<dictArray.length; i++ ) {
        var trid  = dictArray[i].split("|TRID|");        
        if ( (trid[0] == selectedId) && (trid[1].length >= defaultBoxLength) ) {
            select = confirm("<%=resultDetail%>:\n\n " + trid[1] + "\n\n<%=resultConfirm%>"); 
            if ( !select )  {
                form.selectedIndex = 0;
            }
            break;                                     
        }
    }
}

</script>
<%--bugzilla 2187--%>
<%--bugzilla 2553 fix 2187 for other app servers i.e. OC4J--%>
<script> var noSelections = false; </script>
<logic:empty name="<%=formName%>" property="selectedTestSectionId">
  <logic:empty name="<%=formName%>" property="selectedTestId">
    <logic:empty name="<%=formName%>" property="receivedDateForDisplay">
    <%-- if all 3 are blank then don't display the message --%>
      <script> noSelections = true; </script>
    </logic:empty>
  </logic:empty>
</logic:empty>

<logic:empty name="<%=formName%>" property="sample_TestAnalytes">
    <script>if (!noSelections) {alert('<%=noResultFound%>');}</script>
</logic:empty>

<html:hidden property="selectedTestResultIds" name="<%=formName%>" />
<html:hidden property="selectedTestResultValues" name="<%=formName%>" />
<%--bugzilla 2254--%>
<html:hidden property="stringOfUnsatisfactoryResults" name="<%=formName%>" />

<logic:notEmpty name="<%=formName%>" property="sample_TestAnalytes">
 <bean:define id="sTestAnalytes" name="<%=formName%>" property="sample_TestAnalytes" type="java.util.List"/>
 <bean:size id="numberOfRows" name="sTestAnalytes" />
 <table width="100%">
   <logic:notEqual name="<%=numberOfRows.toString()%>" value="0" >
     <bean:define id="sTestAnalyte" name="<%=formName%>" property='<%= "sample_TestAnalytes[0]"%>' type="us.mn.state.health.lims.result.valueholder.Sample_TestAnalyte" />
	 <bean:define id="tAnalytes" name="sTestAnalyte" property="testAnalytes" type="java.util.List" />
     <bean:size id="numberOfColumns" name="tAnalytes" />
     <logic:notEqual name="<%=numberOfColumns.toString()%>" value="0">
      <tr> 
       <td colspan="<%=numberOfColumns.intValue() + 1%>"> 
         <h2><bean:message key="batchresultsentry.browse.testResults.title"/></h2>
       </td>
     </tr>
   </logic:notEqual>
  </logic:notEqual>
</table>
</logic:notEmpty>


<div id="outerDiv">
  <div id="innerDiv">
    <logic:notEmpty name="<%=formName%>" property="sample_TestAnalytes">
      <bean:define id="smplTestAnalytes" name="<%=formName%>" property="sample_TestAnalytes" type="java.util.List"/>
      <bean:size id="numOfRows" name="smplTestAnalytes" />
       <table>
         <logic:notEqual name="<%=numOfRows.toString()%>" value="0" >
           <bean:define id="smplTestAnalyte" name="<%=formName%>" property='<%= "sample_TestAnalytes[0]"%>' type="us.mn.state.health.lims.result.valueholder.Sample_TestAnalyte" />
	       <bean:define id="tstAnalytes" name="smplTestAnalyte" property="testAnalytes" type="java.util.List" />
           <bean:size id="numOfColumns" name="tstAnalytes" />
           <logic:notEqual name="<%=numOfColumns.toString()%>" value="0">
            <logic:iterate id="sampleTestAnalyte" name="<%=formName%>" indexId="sample_ctr" property="sample_TestAnalytes" type="us.mn.state.health.lims.result.valueholder.Sample_TestAnalyte">
              <logic:equal name="sample_ctr" value="0">
                <tr> 
                  <th class="bre" nowrap="nowrap" align="middle"><bean:message key="batchresultsentry.browse.accessionNumber"/></th>
                  <th class="bre" nowrap="nowrap">&nbsp;</th>
                  <logic:iterate id="testAnalyte" indexId="ctr" name="tstAnalytes" type="us.mn.state.health.lims.testanalyte.valueholder.TestAnalyte">
                    <bean:define id="analyte" name="testAnalyte" property="analyte" />
                    <th class="bre" nowrap="nowrap" align="middle"><bean:write name="analyte" property="analyteName" /> </th>
                  </logic:iterate>
                 </tr>
                </logic:equal>
               <bean:define id="sample" name="sampleTestAnalyte" property="sample" type="us.mn.state.health.lims.sample.valueholder.Sample"/>
               <bean:define id="person" name="sampleTestAnalyte" property="person" type="us.mn.state.health.lims.person.valueholder.Person"/>
               <bean:define id="patient" name="sampleTestAnalyte" property="patient" type="us.mn.state.health.lims.patient.valueholder.Patient"/>
                 <tr>
                    <th class="bre" align="middle">
                     <a class="hoverinformation" href="" title='<%=person.getFirstName() + " " + person.getLastName() + "/ " + patient.getExternalId() %>' onclick="return false;">
                        <bean:write name="sample" property="accessionNumber"/>&nbsp;	
                     </a> 
                     <%--bugzilla 2227 link to history--%>
                     <logic:equal name="sampleTestAnalyte" property="sampleHasTestRevisions" value="true">     
                     <a href="" onclick="resultsEntryHistoryBySamplePopup(document.forms[0], '<%= sample.getAccessionNumber()%>');return false;" style="BACKGROUND-COLOR: #cccc99;color:blue">
                            <bean:message key="resultsentry.label.hyperlink.history" />
                     </a>
                     </logic:equal>
                   </th>
                   <td class="bre">
	                  <html:multibox name='<%=formName%>' property="selectedRows">
	                    <bean:write name="sample_ctr" />
	                  </html:multibox>
   	               </td>
         
                   <logic:iterate id="ta_Trs" name="<%=formName%>" indexId="analyte_ctr" property="testAnalyte_TestResults" type="us.mn.state.health.lims.result.valueholder.TestAnalyte_TestResults" >
                    <%	
							List testResults = ta_Trs.getTestResults();                           
							TestResult testresult = null;
							if (testResults != null && testResults.size() > 0) {
							 	testresult = (TestResult) testResults.get(0);
							}
							
	                 %>
                             
                    <bean:define id="resId" name="<%=formName%>" property='<%= "sample_TestAnalytes[" + sample_ctr + "].resultIds[" +  analyte_ctr +"]"%>'/>
                    <%--bugzilla 1942 results can be deleted ONLY if no notes attached to result--%>
                    <bean:define id="notesAttached" name="<%=formName%>" property='<%= "sample_TestAnalytes[" + sample_ctr + "].resultHasNotesList[" +  analyte_ctr +"]"%>'/>
                
                    <td class="bre" align="middle"> 
                    
             		<%if (null != testresult) {
             		 	if (testresult.getTestResultType().equalsIgnoreCase(numericType)) {            		 
             		 %>     
                 	<html:hidden name="<%=formName%>" property='<%= "resultValueN[" + sample_ctr + "].testResultValues[" +  analyte_ctr +"]"%>' />
   	       		 	
   	       		 	<app:text name="<%=formName%>" property= '<%= "sample_TestAnalytes[" + sample_ctr + "].testResultValues[" +  analyte_ctr +"]"%>'  size="5" styleClass="withouthighlight" 
   	       		 	onblur='<%= "checkBlankResultForNotes(this," + notesAttached + ");validateTiterNumeric(this," + testresult.getId() + ");" %>' />
       	    
   	       		 	<%--bugzilla 2322--%>
   	     			<html:select name="<%=formName%>" 
   	     			property='<%= "sample_TestAnalytes[" + sample_ctr + "].sampleTestResultIds[" +  analyte_ctr +"]"%>' 
   	     			style="display:none" value='<%= testresult.getId() %>' onchange='viewDictionary(this);'
   	     			>
 	             	     <app:optionsCollection 
	                    	name="ta_Trs" 
	            	    	property="testResults" 
		                	label="value" 
		                	maxLength="20"
	             	    	value="id" 
	                     />
	                  </html:select>
	        
   	             
   	             <%}else if (testresult.getTestResultType().equalsIgnoreCase(titerType)) {            		 
             		 %> 
   	             
   	             <html:hidden name="<%=formName%>" property='<%= "resultValueT[" + sample_ctr + "].testResultValues[" +  analyte_ctr +"]"%>' />
   	       		 	
   	       		 	1:<app:text name="<%=formName%>" property= '<%= "sample_TestAnalytes[" + sample_ctr + "].testResultValues[" +  analyte_ctr +"]"%>'  size="5" styleClass="withouthighlight" 
   	       		 	onblur='<%= "checkBlankResultForNotes(this," + notesAttached + ");validateTiterNumeric(this," + testresult.getId() + ");" %>' />
   	     			
   	       		 	<%--bugzilla 2322--%>
   	     			<html:select name="<%=formName%>" 
   	     			property='<%= "sample_TestAnalytes[" + sample_ctr + "].sampleTestResultIds[" +  analyte_ctr +"]"%>' 
   	     			style="display:none" value='<%= testresult.getId() %>' onchange='viewDictionary(this);'
   	     			>
 	             	     <app:optionsCollection 
	                    	name="ta_Trs" 
	            	    	property="testResults" 
		                	label="value" 
		                	maxLength="20"
	             	    	value="id" 
	                     />
	                  </html:select>
   	             
   	             
   	             
   	             <% }else{ %>
   	             
                     <%--bugzilla 1772 highlight changed fields--%>
                     <%--bugzilla 2322--%>
                       <html:select name="<%=formName%>" styleClass="withouthighlight" property='<%= "sample_TestAnalytes[" + sample_ctr + "].sampleTestResultIds[" +  analyte_ctr +"]"%>' 
                      onchange='<%= "checkBlankResultForNotes(this," + notesAttached + ");highlightResultChanged(this); viewDictionary(this);" %>'>
                                            
 	             	     <app:optionsCollection 
	                    	name="ta_Trs" 
	            	    	property="testResults" 
		                	label="value" 
		                	maxLength="20"
	             	    	value="id" 
	                     />
	                  </html:select>	                  
	                 <html:hidden property= '<%= "sample_TestAnalytes[" + sample_ctr + "].testResultValues[" +  analyte_ctr +"]"%>' name="<%=formName%>" value="-1" />  
   				
   				 <% } }else{%>  
   				 
   				      <%--bugzilla 1772 highlight changed fields--%>
   				      <%--bugzilla 2322--%>
                       <html:select name="<%=formName%>" styleClass="withouthighlight" property='<%= "sample_TestAnalytes[" + sample_ctr + "].sampleTestResultIds[" +  analyte_ctr +"]"%>' onchange='viewDictionary(this);'>   
                      
 	             	     <app:optionsCollection 
	                    	name="ta_Trs" 
	            	    	property="testResults" 
		                	label="value" 
		                	maxLength="20"
	             	    	value="id" 
	                     />
	                  </html:select>	                  
	                 <html:hidden property= '<%= "sample_TestAnalytes[" + sample_ctr + "].testResultValues[" +  analyte_ctr +"]"%>' name="<%=formName%>" value="-1" />  
   				
   				  <% }%>  
	                  <html:hidden name="<%=formName%>" property='<%= "sample_TestAnalytes[" + sample_ctr + "].resultLastupdatedList[" +  analyte_ctr +"]"%>' />
                    </td>
                   </logic:iterate>
                 </tr>
               </logic:iterate>
             </logic:notEqual>
           </logic:notEqual>
        </table>
    </logic:notEmpty>
  </div>
</div>
<app:javascript formName="batchResultsEntryForm"/>
