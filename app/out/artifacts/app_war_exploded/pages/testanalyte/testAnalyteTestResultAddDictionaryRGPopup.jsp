<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants,
	us.mn.state.health.lims.common.util.SystemConfiguration" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />

<%!

String allowEdits = "true";
String aID;
String rgType;
String errorDictEntry = "";
String dictEntry = "";
//bugzilla 1845 added testResult sortOrder
String sortOrder = "";
String errorSortOrder = "";

String path = "";
String basePath = "";
%>
<%
path = request.getContextPath();
basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";

if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}
aID = (String)request.getAttribute("aID");
rgType = (String)request.getAttribute("rgType");

//bugzilla 1494
java.util.Locale locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
dictEntry = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "testanalytetestresult.addDictionaryRGPopup.dictionaryEntry");
errorDictEntry =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "errors.invalid",
                    dictEntry);
sortOrder = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "testanalytetestresult.addNonDictionaryRGPopup.sortOrder");
                    
errorSortOrder = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "errors.invalid",
                    sortOrder);
%>


<script language="JavaScript1.2">

var dictionaryEntryList = new Array();
var dictionaryEntryIdList = new Array();
var flagsList = new Array();
var sortList = new Array();
var arrayIndex = 0;
var dictionaryEntryIsInvalid = false;


var formFieldArray = new Array('sortOrder');
var formFieldsValidArray = new Array(true);
var formFieldsRequiredArray = new Array(false);


function customOnLoad() {
  var category = $("selectedCategory");
  category.focus();
}

var skipcycle = false;
function myOnBlur() {
    //var category = $("selectedCategory");
    //category.focus();
    
    if (!skipcycle){
       window.focus(); 
    }
    mytimer = setTimeout('myOnBlur()', 500);
}



function validateForm(form) {
    //return validateTestAnalyteTestResultAddDictionaryRGPopupForm(form);
    return true;
}

function cancelToParentForm() {
   if (window.opener && !window.opener.closed && window.opener.document.forms[0]) {
       //alert("Going to clear add test clicked");
        //window.opener.clearAddTestClicked();
        window.close();
   } 
}

function getRGSection() {
  var section = $('resultGroupSection');
  return section;
}

function getNextRowNumber(section) {
 //alert("I am in getNextRowNumber()");
 tbody = section.getElementsByTagName("TBODY")[0];
 var trs = tbody.getElementsByTagName("tr");
 //alert("returning " + trs.length);
 return trs.length;
}

function checkNoDuplicateDE() {
 var numberOfRows = getNextRowNumber(getRGSection());
 var deToAdd = $F("dictionaryEntry");
 //alert("numberOfRows = " + numberOfRows);
 var prop;
 if (deToAdd == '') {
    return false;
 }
 for (var i = 0; i < numberOfRows; i++) {
    prop = 'dictionaryEntryList[' + i + ']';
    //alert("This is prop " + $F(prop));
    //bugzilla 2226: ignore case
    if ($F(prop).toLowerCase() == deToAdd.toLowerCase()) {
        //alert("returning false");
         return false;
    }
 }
 //alert("returning true" );
 return true;
}

function addResultGroup(sect) {
   //alert("I am in addResultGroup()");
   
  var deToAdd = $F("dictionaryEntry");
  var deIdToAdd = $F("dictionaryEntryId");
  var flagsToAdd = $F("flags");
  var sortsToAdd = $F("sortOrder");
  var section = $(sect);
  var category = $("selectedCategory");

  var content, prop, onClick;
  var outputDE, outputFlags, hiddenDEId;
  var td1, td2;
  content = "";


     var i = getNextRowNumber(section);   

     tbody = section.getElementsByTagName("TBODY")[0];
     row = document.createElement("<tr>");
     td1 = document.createElement('<td width="76%" >');
     td2 = document.createElement('<td width="4%">');
     td3 = document.createElement('<td width="4%">');

     prop = 'dictionaryEntryList[' + i + ']';
     outputDE = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + deToAdd + '\" indexed=\"true\" size=\"80\" readonly >');
     
     prop = 'dictionaryEntryIdList[' + i + ']';
     hiddenDEId = document.createElement('<input type=\"hidden\" name=\"' + prop + '\" value=\"' + deIdToAdd + '\" indexed=\"true\">');
     
     prop = 'flagsList[' + i + ']';
     outputFlags = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + flagsToAdd + '\" indexed=\"true\" readonly size=\"4\" >');
     
     prop = 'sortList[' + i + ']';
     outputSorts = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + sortsToAdd + '\" indexed=\"true\" readonly size=\"4\" >');
     
     dictionaryEntryList[arrayIndex] = deToAdd;
     dictionaryEntryIdList[arrayIndex] = deIdToAdd;
     flagsList[arrayIndex] = flagsToAdd;
     sortList[arrayIndex] = sortsToAdd;
     arrayIndex++;

     td1.appendChild(outputDE);
     td1.appendChild(hiddenDEId);
     td2.appendChild(outputSorts);
     td3.appendChild(outputFlags);
 
          
     row.appendChild(td1);
     row.appendChild(td2);
     row.appendChild(td3);
     
     tbody.appendChild(row);
    // alert("This is tbody " + tbody.innerHTML);
    
    //blank out selections
      deToAdd.value = '';
      deIdToAdd.value = '';
      flagsToAdd.value = '';
      sortsToAdd.value = '';
      category.selected = '';
     
 
}

function saveItToParentForm(form) {
//alert("I am in saveItToParentForm");
    
   if (window.opener && !window.opener.closed && window.opener.document.forms[0]) {
       //var rgType = $F("selectedResultType");
       for (var i = 0; i < arrayIndex ; i++) {
            window.opener.addRowToSectionB('<%=aID%>', null, '<%=rgType%>', dictionaryEntryList[i], dictionaryEntryIdList[i], flagsList[i], sortList[i], '', '', i, arrayIndex, '');
       }
      window.close();
   }
   

}

//THE FOLLOWING IS FOR VALIDATION OF AUTOCOMPLETE FIELD


function setMessage(message, field) {
     //alert("I am in in setMessage - reg. validation - with message and field " + message + " " + field);
     var valField = $(field);
     //bugzilla 1462 problem with entering instead of selecting from dropdown in ajax autocomplete -> id is not retrieved
     var idField = $(field + "Id");
     if (message == "invalid") {
       //disable save button
       //bugzilla 1494
       alert('<%=errorDictEntry%>');
     } else if (message.length > 5) {
          //bugzilla 1462 problem with entering instead of selecting from dropdown in ajax autocomplete -> id is not retrieved
          //valField.value = message.substring(5);
          idField.value = message.substring(5);
          addResultGroup('resultGroupSection');
     } else if (message == "valid") {           
          addResultGroup('resultGroupSection');
     }

}

function processFailure(xhr) {
  //ajax call failed
}

function processSuccess(xhr) {
  var message = xhr.responseXML.getElementsByTagName("message")[0];
  var formfield = xhr.responseXML.getElementsByTagName("formfield")[0];
  //alert("I am in parseMessage and this is message, formfield " + message + " " + formfield);
  setMessage(message.childNodes[0].nodeValue, formfield.childNodes[0].nodeValue);
}

function validateDictionaryEntryAndAddResultGroup() {
 //alert("I am in validateDictionaryEntryAndAddResultGroup");
  if (checkNoDuplicateDE()) {
     new Ajax.Request (
       'ajaxXML',  //url
       {//options
        method: 'get', //http method
        parameters: 'provider=DictionaryValidationProvider&field=dictionaryEntry&id=' + URLencode($F("dictionaryEntry")),      //request parameters
        //indicator: 'throbbing'
        onSuccess:  processSuccess,
        onFailure:  processFailure
       }
     );
   }
}

function validateSortOrder() {
 var result = true;
 var sortOrder = $F("sortOrder");
 if (sortOrder != '' && !IsNumeric(sortOrder)) { 
   setFieldInvalid('sortOrder');
   setSave();
   alert('<%=errorSortOrder%>');
   result = false;
 } else {
   setFieldValid('sortOrder');
   setSave();
 }
 return result;
}

//get array index for a particular field on form
function getFieldIndex(field) {
  var i;
  for (i = 0; i < formFieldArray.length; i++) {
       if (formFieldArray[i] == field) {
              break;
       }
  }
  return i;
}

//returns true or false 
function isFieldValid(fieldname) {
  var i;
  for (i = 0; i < formFieldArray.length; i++) {
       if (formFieldArray[i] == fieldname) {
              break;
       }
  }
  return formFieldsValidArray[i];
}

//returns true or false 
function isFieldRequired(field) {
  var i;
  for (i = 0; i < formFieldArray.length; i++) {
       if (formFieldArray[i] == field) {
              break;
       }
  }
  return formFieldsRequiredArray[i];
}

function setFieldInvalid(field) {
   var index = getFieldIndex(field);
   formFieldsValidArray[index] = false;
}

function setFieldValid(field) {
   var index = getFieldIndex(field);
   formFieldsValidArray[index] = true;
}

//disable or enable save button based on validity of fields - if disabling save focus cursor on first field in error
function setSave() {
  //disable or enable save button based on validity of fields
  var obj = $("add"); 
  obj.disabled = false;
  for (var i = 0; i < formFieldsValidArray.length; i++) {
       if (formFieldsValidArray[i] == false) {
           obj.disabled = true;
           break;
       }
  }

}

function isSaveEnabled() {
   var enabled = true;
   for (var i = 0; i < formFieldsValidArray.length; i++) {
       if (formFieldsValidArray[i] == false) {
           enabled = false;
           break;
       }
  }
  return enabled;
}

</script>

<table width="100%">
        <tr> 
          <td width="16%"><bean:message key="testanalytetestresult.addDictionaryRGPopup.category"/>:</td>
          <td width="84%"> 
          <html:select name="<%=formName%>" property="selectedCategory" onfocus="skipcycle=true" onblur="skipcycle=false"> 
    	   	  <app:optionsCollection 
	    	name="<%=formName%>" 
			property="categories" 
			label="description" 
			value="categoryName" 
           />
      	   </html:select>
          </td>
        </tr>
        <tr> 
          <td width="16%"><bean:message key="testanalytetestresult.addDictionaryRGPopup.dictionaryEntry"/>:</td>
          <td width="84%"> 
              	<html:text styleId="dictionaryEntry" size="40" name="<%=formName%>" property="dictionaryEntry" onfocus="skipcycle=true" onblur="skipcycle=false" /> 
              	<span id="indicator1" style="display:none;"><img src="<%=basePath%>images/indicator.gif"/></span>
	   			<input id="dictionaryEntryId" name="dictionaryEntryId" type="hidden" size="30" />
          </td>
        </tr>
        <tr>
          <td width="16%"><bean:message key="testanalytetestresult.addDictionaryRGPopup.sortOrder"/>:</td>
          <td width="84%"> 
            <input type="text" name="sortOrder" size="3" onfocus="skipcycle=true" onblur="skipcycle=false;validateSortOrder()" />
          </td>
        </tr>
        <tr>
          <td width="16%"><bean:message key="testanalytetestresult.addDictionaryRGPopup.flags"/>:</td>
          <td width="84%"> 
            <input type="text" name="flags" size="3" onfocus="skipcycle=true" onblur="skipcycle=false" />
          </td>
        </tr>
        <tr> 
          <td width="16%">&nbsp;</td>
          <td width="84%">
  			<html:button property="add" onclick="validateDictionaryEntryAndAddResultGroup('resultGroupSection');" onfocus="skipcycle=true" onblur="skipcycle=false" >
  			   <bean:message key="label.button.add"/>
  			</html:button>
	    </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
 
 
<table width="100%" border=2">
<tr>
<td id="h1" width="74%"><bean:message key="testanalytetestresult.addDictionaryRGPopup.selectedTestResults.title"/>:</th>
<td id="h2" width="4%"><bean:message key="testanalytetestresult.addDictionaryRGPopup.sortOrder"/>:</th>
<td id="h3" width="4%"><bean:message key="testanalytetestresult.addDictionaryRGPopup.flags"/>:</th>
<td id="h4" width="2%">&nbsp;</th>
</tr>

</table>
 
<div class="scrollvertical">
<table id="resultGroupSection" class="blank" width="100%">
<tbody>
</tbody>
</table>
</div>


<%--html:javascript formName="testAnalyteTestResultAddDictionaryRGPopupForm" staticJavascript="true"/--%>
  <%--bugzilla 1847 use dictEntryDisplayValue--%>
  <ajax:autocomplete
  source="dictionaryEntry"
  target="dictionaryEntryId"
  baseUrl="ajaxAutocompleteXML"
  className="autocomplete"
  parameters="dictionaryEntry={dictionaryEntry},dictionaryCategory={selectedCategory},provider=DictionaryAutocompleteProvider,fieldName=dictEntryDisplayValue,idName=id"
  indicator="indicator1"
  minimumCharacters="1" />
