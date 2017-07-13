<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants,
	us.mn.state.health.lims.common.security.IAuthorizationActionConstants,
	us.mn.state.health.lims.common.util.SystemConfiguration" %>
<%@ page import="org.owasp.encoder.Encode" %>

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
String rgNum;
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
rgNum = (String)request.getAttribute("rgNum");

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


var formFieldArray = new Array('sortOrder');
var formFieldsValidArray = new Array(true);
var formFieldsRequiredArray = new Array(false);


function customOnLoad() {
  var category = $("selectedCategory");
  category.focus();
  
  var testNotLocked = '<%= Encode.forJavaScript(request.getParameter(IAuthorizationActionConstants.UPDATE_TESTCOMPONENT_TESTRESULT)) %>';
  var parentSection = window.opener.getSectionB();
  tbody = parentSection.getElementsByTagName("TBODY")[0];
  var trs = tbody.getElementsByTagName("tr");
  var section = getRGSection();
  var prop, rowSpan;
  var selectedResultGroup, outputResultGroups, outputTestResultValues;
  var td1, td2, td3, td4;
  var delRowButton, delRowButtonText;
  var content = "";
  var rowNum = 0; 
  
  for (var i = 0; i < trs.length; i++) {
       var inputs = trs[i].getElementsByTagName("input");
       if (inputs.length > 2) { //this is not an empty edit button row
       
       var rg = inputs[0].value;
       
       if (rg == '<%=Encode.forJavaScript(rgNum)%>') { //this is matching result group
 
    
//recreate rows from parent form for this result group
       tbody = section.getElementsByTagName("TBODY")[0];
       row = document.createElement("<tr>");

        
       var rowFieldIndex = inputs['rowFieldIndex'].value;
       var fld1 = 'testResultValueList[' + rowFieldIndex + ']';
       var fld2 = 'dictionaryEntryIdList[' + rowFieldIndex + ']';
       var fld3 = 'sortList[' + rowFieldIndex + ']';
       var fld4 = 'flagsList[' + rowFieldIndex + ']';
       var fld5 = 'testResultIdList[' + rowFieldIndex + ']';
       var fld6 = 'testResultLastupdatedList[' + rowFieldIndex + ']';
      
       var deToAdd = inputs[fld1].value;
       var deIdToAdd = inputs[fld2].value;
       var sortsToAdd = inputs[fld3].value;
       var flagsToAdd = inputs[fld4].value;
       var testResultIdToAdd = inputs[fld5].value;
       var testResultLastupdatedToAdd = inputs[fld6].value;
       
       td1 = document.createElement('<td width="73%" >');
       td2 = document.createElement('<td width="4%">');
       td3 = document.createElement('<td width="4%">');
       td4 = document.createElement('<td width="3%" >');

       prop = 'dictionaryEntryList[' + rowNum + ']';
       outputDE = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + deToAdd + '\" indexed=\"true\" size=\"80\" readonly >');
     
       prop = 'dictionaryEntryIdList[' + rowNum + ']';
       hiddenDEId = document.createElement('<input type=\"hidden\" name=\"' + prop + '\" value=\"' + deIdToAdd + '\" indexed=\"true\">');
     
       prop = 'sortList[' + rowNum + ']';
       outputSorts = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + sortsToAdd + '\" indexed=\"true\" readonly size=\"4\" >');
       
       prop = 'flagsList[' + rowNum + ']';
       outputFlags = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + flagsToAdd + '\" indexed=\"true\" readonly size=\"4\" >');
       
       prop = 'testResultIdList[' + rowNum + ']';
       hiddenTestResultId = document.createElement('<input type=\"hidden\" name=\"' + prop + '\" value=\"' + testResultIdToAdd + '\" indexed=\"true\">');
       
       prop = 'testResultLastupdatedList[' + rowNum + ']';
       hiddenTestResultLastupdated = document.createElement('<input type=\"hidden\" name=\"' + prop + '\" value=\"' + testResultLastupdatedToAdd + '\" indexed=\"true\">');
       
       
     
       //create delete row button 
	   prop = 'delRow[' + rowNum + ']';
       onClick = "delRow('resultGroupSection', " + rowNum + ");";
       
       if (testNotLocked == "false") {
	        delRowButton = document.createElement('<INPUT TYPE="BUTTON" NAME=' + prop + ' disabled=\"true\" VALUE="X" ONCLICK=\"' + onClick + '\" />');
       } else {
	        delRowButton = document.createElement('<INPUT TYPE="BUTTON" NAME=' + prop + ' VALUE="X" ONCLICK=\"' + onClick + '\" />');
       }
     
       rowNum++;
       dictionaryEntryList[arrayIndex] = deToAdd;
       dictionaryEntryIdList[arrayIndex] = deIdToAdd;
       flagsList[arrayIndex] = flagsToAdd;
       sortList[arrayIndex] = sortsToAdd;
       arrayIndex++;

       td1.appendChild(outputDE);
       td1.appendChild(hiddenDEId);
       td1.appendChild(hiddenTestResultId);
       td1.appendChild(hiddenTestResultLastupdated);
       td2.appendChild(outputSorts);
       td3.appendChild(outputFlags);
       td4.appendChild(delRowButton);
 
          
       row.appendChild(td1);
       row.appendChild(td2);
       row.appendChild(td3);
       row.appendChild(td4);
     
       tbody.appendChild(row);
       
       }
       
      }
    
            
 }
    //alert("This is tbody  " + tbody.innerHTML); 
}

function replaceInputIndex(aName, newIndex) {
    var indexOfOpenBrace = aName.indexOf("[");
    var indexOfCloseBrace = aName.indexOf("]");
    var aNewNameBefore = aName.substring(0, indexOfOpenBrace);
    var aNewNameAfter = aName.substring(indexOfCloseBrace + 1);
    var aNewName = aNewNameBefore + '[' + newIndex +  ']' + aNewNameAfter;
    return aNewName;
}

function getArrayName(aName) {
  var arrayName = aName;
  var i = arrayName.indexOf('[');
  if (i >= 0) {
     arrayName = arrayName.substring(0, i);
  }
  return arrayName;
}

function reSortRows(sect, index) {
 var section = $(sect);
 tbody = section.getElementsByTagName("TBODY")[0];
 var trs = tbody.getElementsByTagName("tr");
   for (var i = index; i < trs.length; i++) {
        var row = trs[i];
        var inputs = row.getElementsByTagName("input");
 
        var newElement;
        var parent;
        var size;
        var text;
        var prop;
        var value;
        var onClick;
        
          
        for (var x = 0; x < inputs.length; x++) {
        
           var aName = inputs[x].name;          
        
           var aNewName = replaceInputIndex(aName, i);
           
           newElement =  document.createElement(aNewName);  
           
           var type = inputs[x].type;
           prop = inputs[x].name;
           
           //need to replace the index input field  - but not for rowFieldIndex
           if (prop == 'rowFieldIndex') {
              value = i;
              //there are some simple (not indexed) input fields
           } else if (prop == 'rgGrouping'){
              value = inputs[x].value;
           } else  {
              prop = replaceInputIndex(prop, i);
              value = inputs[x].value;
           }
           
           
 
           if (type == 'button') {
                       
              //get function name for onclick
              var indexOfOpenBrace = prop.indexOf('[');
              var functionName = prop.substring(0, indexOfOpenBrace);
              var onClick = functionName + '(\'' + sect + '\', ' + i + ');';
              
      	      newElement = document.createElement('<INPUT TYPE="BUTTON" NAME=' + prop + ' VALUE=\"' + value + '\" ONCLICK=\"' + onClick + '\" />')
              
              
           } else if (type == 'hidden' || type == 'text' || type == 'select') {
               size = inputs[x].size;
               
               if (inputs[x].readonly) {
                     newElement = document.createElement('<input type=\"' + type + '\" size=\"' + size + '\" name=\"' + prop + '\" value=\"' + value + '\" indexed=\"true\" readonly >');
               } else {
                     newElement = document.createElement('<input type=\"' + type + '\" size=\"' + size + '\" name=\"' + prop + '\" value=\"' + value + '\" indexed=\"true\">');
               }
               
               //for selectedAnalyteResultGroups we need to re-create the id also 'rgForAnalyte#' 
               if (getArrayName(prop) == 'selectedAnalyteResultGroups') {
                       newElement = document.createElement('<input id=\"' + inputs[x].id + '\" type=\"' + type + '\" size=\"' + size + '\" name=\"' + prop + '\" value=\"' + value + '\" indexed=\"true\" readonly >');
               }
           } 
       
           parent = inputs[x].parentNode;
           parent.replaceChild(newElement, inputs[x]);
           
            
        }
        
        
    }
//alert("This is tbody after reSort " + tbody.innerHTML);
}

function delRow(sect, index) {
  var section = $(sect);
  tbody = section.getElementsByTagName("TBODY")[0];
  var trs = tbody.getElementsByTagName("tr");
  
  var row = trs[index];
  tbody.deleteRow(index);
  reSortRows(sect, index);
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
  var td1, td2, td3, td4;
  var delRowButton, delRowButtonText;
  content = "";

     var i = getNextRowNumber(section);   

     tbody = section.getElementsByTagName("TBODY")[0];
     row = document.createElement("<tr>");
     td1 = document.createElement('<td width="73%" >');
     td2 = document.createElement('<td width="4%">');
     td3 = document.createElement('<td width="4%">');
     td4 = document.createElement('<td width="3%" >');

     prop = 'dictionaryEntryList[' + i + ']';
     outputDE = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + deToAdd + '\" indexed=\"true\" size=\"80\" readonly >');
     
     prop = 'dictionaryEntryIdList[' + i + ']';
     hiddenDEId = document.createElement('<input type=\"hidden\" name=\"' + prop + '\" value=\"' + deIdToAdd + '\" indexed=\"true\">');
     
     prop = 'sortList[' + i + ']';
     outputSorts = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + sortsToAdd + '\" indexed=\"true\" readonly size=\"4\" >');
 
     prop = 'flagsList[' + i + ']';
     outputFlags = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + flagsToAdd + '\" indexed=\"true\" readonly size=\"4\" >');
     
     //create delete row button 
     prop = 'delRow[' + i + ']';
     onClick = "delRow('resultGroupSection', " + i + ");";
     delRowButton = document.createElement('<INPUT TYPE="BUTTON" NAME=' + prop + ' VALUE="X" ONCLICK=\"' + onClick + '\" />');
     
     dictionaryEntryList[arrayIndex] = deToAdd;
     dictionaryEntryIdList[arrayIndex] = deIdToAdd;
     flagsList[arrayIndex] = flagsToAdd;
     arrayIndex++;

     td1.appendChild(outputDE);
     td1.appendChild(hiddenDEId);
     td2.appendChild(outputSorts);
     td3.appendChild(outputFlags);
     td4.appendChild(delRowButton);
 
          
     row.appendChild(td1);
     row.appendChild(td2);
     row.appendChild(td3);
     row.appendChild(td4);
     
     tbody.appendChild(row);
    // alert("This is tbody " + tbody.innerHTML);
    
    //blank out selections
      deToAdd.value = '';
      deIdToAdd.value = '';
      flagsToAdd.value = '';
      sortsToAdd.value = '';
      category.selected = '';
     
  
}

//function myOnBlur() {
    //var category = $("selectedCategory");
    //category.focus();
//}

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
        window.close();
   } 
}

function getRGSection() {
  var section = $('resultGroupSection');
  return section;
}

function getNextRowNumber(section) {
 tbody = section.getElementsByTagName("TBODY")[0];
 var trs = tbody.getElementsByTagName("tr");
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

function saveItToParentForm(form) {

   if (window.opener && !window.opener.closed && window.opener.document.forms[0]) {
       var section = getRGSection();
       tbody = section.getElementsByTagName("TBODY")[0];
       var trs = tbody.getElementsByTagName("tr");
       window.opener.replaceResultGroup('<%=Encode.forJavaScript(rgType)%>', '<%=Encode.forJavaScript(rgNum)%>', trs);
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
 //alert("I am in validateDictionaryEntry");
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
            <input type="text" name="sortOrder" size="3" onfocus="skipcycle=true" onblur="skipcycle=false;validateSortOrder()" >
          </td>
        </tr>
        <tr>
          <td width="16%"><bean:message key="testanalytetestresult.addDictionaryRGPopup.flags"/>:</td>
          <td width="84%"> 
            <input type="text" name="flags" size="3" onfocus="skipcycle=true" onblur="skipcycle=false" >
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
<td id="h1" width="72%"><bean:message key="testanalytetestresult.addDictionaryRGPopup.selectedTestResults.title"/>:</th>
<td id="h2" width="4%"><bean:message key="testanalytetestresult.addDictionaryRGPopup.sortOrder"/>:</th>
<td id="h3" width="4%"><bean:message key="testanalytetestresult.addDictionaryRGPopup.flags"/>:</th>
<td id="h4" width="4%">&nbsp;</th>
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
