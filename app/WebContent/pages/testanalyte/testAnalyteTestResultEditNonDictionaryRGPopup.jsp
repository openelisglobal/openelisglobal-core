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


<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="titerType" value='<%= SystemConfiguration.getInstance().getTiterType() %>' />
<bean:define id="numericType" value='<%= SystemConfiguration.getInstance().getNumericType() %>' />

<%!

String allowEdits = "true";
String aID;
String rgType;
String rgNum;
String errorTestResultValue = "";
String testResultValue = "";
//bugzilla 1845 added testResult sortOrder
String sortOrder = "";
String errorSortOrder = "";
//bugzilla 1878 added validation for significationDigits required
String significantDigits = "";
String errorSignificantDigits = "";
%>

<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}
aID = (String)request.getAttribute("aID");
rgType = (String)request.getAttribute("rgType");
rgNum = (String)request.getAttribute("rgNum");

//bugzilla 1494
java.util.Locale locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
testResultValue = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "testanalytetestresult.addNonDictionaryRGPopup.testResultValue");
errorTestResultValue =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "errors.invalid",
                    testResultValue);
                    
sortOrder = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "testanalytetestresult.addNonDictionaryRGPopup.sortOrder");
                    
errorSortOrder = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "errors.invalid",
                    sortOrder);
                    
significantDigits = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "testanalytetestresult.addNonDictionaryRGPopup.significantDigits"); 
errorSignificantDigits = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "errors.invalid",
                    significantDigits);

%>


<script language="JavaScript1.2">

var testResultValueList = new Array();
var significantDigitsList = new Array();
var quantLimitList = new Array();
var flagsList = new Array();
var sortList = new Array();
var arrayIndex = 0;


var formFieldArray = new Array('testResultsValue', 'sortOrder', 'significantDigits');
var formFieldsValidArray = new Array(true, true, true);
var formFieldsRequiredArray = new Array(true, false, true);


function customOnLoad() {
  var testResultValue = document.getElementById("testResultValue");
  testResultValue.focus();
  
  var testNotLocked = '<%= Encode.forJavaScript(request.getParameter(IAuthorizationActionConstants.UPDATE_TESTCOMPONENT_TESTRESULT)) %>';
   
  var parentSection = window.opener.getSectionB();
  tbody = parentSection.getElementsByTagName("TBODY")[0];
  var trs = tbody.getElementsByTagName("tr");
  var section = document.getElementById('resultSection');
  var prop, rowSpan, onClick;
  var outputTRV, outputFlags, outputSorts, outputSD, outputQL;
  var td1, td2, td3, td4, td5;
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
       var fld2 = 'significantDigitsList[' + rowFieldIndex + ']';
       var fld3 = 'sortList[' + rowFieldIndex + ']';
       var fld4 = 'flagsList[' + rowFieldIndex + ']';
       var fld5 = 'quantLimitList[' + rowFieldIndex + ']'; 
       var fld6 = 'testResultIdList[' + rowFieldIndex + ']';
       var fld7 = 'testResultLastupdatedList[' + rowFieldIndex + ']';
 
       var testResultValueToAdd = inputs[fld1].value;
       var sdToAdd = inputs[fld2].value;
       var qlToAdd = inputs[fld5].value;
       var flagsToAdd = inputs[fld4].value;
       var sortsToAdd = inputs[fld3].value;
       var testResultIdToAdd = inputs[fld6].value;
       var testResultLastupdatedToAdd = inputs[fld7].value;
     
       td1 = document.createElement('<td width="70%" >');
       td2 = document.createElement('<td width="8%">');
       td3 = document.createElement('<td width="6%">');
       td4 = document.createElement('<td width="6%">');
       td5 = document.createElement('<td width="7%">');
       td6 = document.createElement('<td width="3%" >');

       prop = 'testResultValueList[' + rowNum + ']';
       outputTRV = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + testResultValueToAdd + '\" indexed=\"true\" size=\"130\" readonly >');
    
       prop = 'testResultIdList[' + rowNum + ']';
       outputTrId = document.createElement('<input type=\"hidden\" name=\"' + prop + '\" value=\"' + testResultIdToAdd + '\" indexed=\"true\" >');
    
       prop = 'testResultLastupdatedList[' + rowNum + ']';
       outputLastupdated = document.createElement('<input type=\"hidden\" name=\"' + prop + '\" value=\"' + testResultLastupdatedToAdd + '\" indexed=\"true\" >');
    
       prop = 'significantDigitsList[' + rowNum + ']'; 
       outputSD = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + sdToAdd + '\" indexed=\"true\" size=\"1\" readonly>');
     
       prop = 'flagsList[' + rowNum + ']';
       outputFlags = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + flagsToAdd + '\" indexed=\"true\" size=\"3\" readonly>');
       
       prop = 'sortList[' + rowNum + ']';
       outputSorts = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + sortsToAdd + '\" indexed=\"true\" size=\"3\" readonly>');
       
       prop = 'quantLimitList[' + rowNum + ']'; 
       outputQL =  document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + qlToAdd + '\" indexed=\"true\" size=\"1\" readonly>');
       
       //create delete row button 
	   prop = 'delRow[' + rowNum + ']';
       onClick = "delRow('resultSection', " + rowNum + ");";
       
       if (testNotLocked == "false") {
	        delRowButton = document.createElement('<INPUT TYPE="BUTTON" NAME=' + prop + ' disabled=\"true\" VALUE="X" ONCLICK=\"' + onClick + '\" />');
       } else {
	        delRowButton = document.createElement('<INPUT TYPE="BUTTON" NAME=' + prop + ' VALUE="X" ONCLICK=\"' + onClick + '\" />');
       }
    
       rowNum++;
       
       td1.appendChild(outputTRV);
       td1.appendChild(outputTrId);
       td1.appendChild(outputLastupdated);
       td2.appendChild(outputSorts);
       td3.appendChild(outputFlags);
       td4.appendChild(outputSD);
       td5.appendChild(outputQL);
       td6.appendChild(delRowButton);
          
       row.appendChild(td1);
       row.appendChild(td2);
       row.appendChild(td3);
       row.appendChild(td4);
       row.appendChild(td5);
       row.appendChild(td6);
 
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
 var section = document.getElementById(sect);
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
  var section = document.getElementById(sect);
  tbody = section.getElementsByTagName("TBODY")[0];
  var trs = tbody.getElementsByTagName("tr");
  
  var row = trs[index];
  tbody.deleteRow(index);
  reSortRows(sect, index);
}


function addResultGroup(sect) {
  //alert("I am in addResultGroup()");
   
 if (validateTestResultValue(true)) {
  var testResultValueToAdd = document.getElementById("testResultValue").value;
  var sdToAdd = document.getElementById("significantDigits").value;
  var qlToAdd = document.getElementById("quantLimit").value;
  var flagsToAdd = document.getElementById("flags").value;
  var sortsToAdd = document.getElementById("sortOrder").value;
  var section = document.getElementById(sect);

  var delRowButton, delRowButtonText;
  var content, prop, onClick;
  var outputTRV, outputFlags, outputSorts, outputSD, outputQL;
  var td1, td2, td3, td4, td5, td6;
  content = "";


     var i = getNextRowNumber(section);   

     tbody = section.getElementsByTagName("TBODY")[0];
     row = document.createElement("<tr>");
     td1 = document.createElement('<td width="75%" >');
     td2 = document.createElement('<td width="5%">');
     td3 = document.createElement('<td width="5%">');
     td4 = document.createElement('<td width="5%">');
     td5 = document.createElement('<td width="5%">');
     td6 = document.createElement('<td width="5%" >');
     

     prop = 'testResultValueList[' + i + ']';
     outputTRV = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + testResultValueToAdd + '\" indexed=\"true\" size=\"120\" readonly >');
     
     prop = 'significantDigitsList[' + i + ']';
     outputSD = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + sdToAdd + '\" indexed=\"true\" readonly size=\"1\">');
     
     prop = 'flagsList[' + i + ']';
     outputFlags = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + flagsToAdd + '\" indexed=\"true\" readonly size=\"3\" >');
     
     prop = 'sortList[' + i + ']';
     outputSorts = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + sortsToAdd + '\" indexed=\"true\" readonly size=\"3\" >');
     
     prop = 'quantLimitList[' + i + ']';
     outputQL = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + qlToAdd + '\" indexed=\"true\" readonly size=\"1\">');
     
     //create delete row button 
	 prop = 'delRow[' + i + ']';
     onClick = "delRow('resultSection', " + i + ");";
     delRowButton = document.createElement('<INPUT TYPE="BUTTON" NAME=' + prop + ' VALUE="X" ONCLICK=\"' + onClick + '\" />');
     
     
     testResultValueList[arrayIndex] = testResultValueToAdd;
     significantDigitsList[arrayIndex] = sdToAdd;
     flagsList[arrayIndex] = flagsToAdd;
     sortList[arrayIndex] = sortsToAdd;
     quantLimitList[arrayIndex] = qlToAdd;
     arrayIndex++;

     td1.appendChild(outputTRV);
     td2.appendChild(outputSorts);
     td3.appendChild(outputFlags);
     td4.appendChild(outputSD);
     td5.appendChild(outputQL);
     td6.appendChild(delRowButton);
          
     row.appendChild(td1);
     row.appendChild(td2);
     row.appendChild(td3);
     row.appendChild(td4);
     row.appendChild(td5);
     row.appendChild(td6);
     
     tbody.appendChild(row);
    // alert("This is tbody " + tbody.innerHTML);
    
    //blank out selections
      testResultValueToAdd.value = '';
      sdToAdd.value = '';
      qlToAdd.value = '';
      flagsToAdd.value = '';
      sortsToAdd.value = '';
     
   }
}

//function myOnBlur() {
    //var testResultValue = document.getElementById("testResultValue");
    //testResultValue.focus();
//}

var skipcycle = false;
function myOnBlur() {
    //var category = document.getElementById("selectedCategory");
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

function getNextRowNumber(section) {
 //alert("I am in getNextRowNumber()");
 tbody = section.getElementsByTagName("TBODY")[0];
 var trs = tbody.getElementsByTagName("tr");
 //alert("returning " + trs.length);
 return trs.length;
}



function saveItToParentForm(form) {
    
   if (window.opener && !window.opener.closed && window.opener.document.forms[0]) {
       var section = document.getElementById('resultSection');
       tbody = section.getElementsByTagName("TBODY")[0];
       var trs = tbody.getElementsByTagName("tr");
       window.opener.replaceResultGroup('<%=Encode.forJavaScript(rgType)%>', '<%=Encode.forJavaScript(rgNum)%>', trs);
       window.close();
   }
   

}

function IsNumeric(strString)
   //  check for valid numeric strings	
   {
   var strValidChars = "0123456789";
   var strChar;
   var blnResult = true;

   if (strString.length == 0) return false;

   //  test strString consists of valid characters listed above
   for (i = 0; i < strString.length && blnResult == true; i++)
      {
      strChar = strString.charAt(i);
      if (strValidChars.indexOf(strChar) == -1)
         {
         blnResult = false;
         }
      }
   return blnResult;
}

function disableSave() {
      //bugzilla 1494
      alert('<%=errorTestResultValue%>');
      //disable add button
      document.getElementById("add").disabled = true;
}

function enableSave() {
      //enable add button
      document.getElementById("add").disabled = false;
}
   
//AIS - bugzilla 1891
function validateTestResultValue(blankCheck) {
//alert("I am ain validateTestResultValue()");
    var testResultValue = document.getElementById("testResultValue").value;
   var result = true; 
  if ((!blankCheck && testResultValue != '') || blankCheck) {  
	  var indexOfComma = testResultValue.indexOf(',');
      if (indexOfComma < 0) {
           setFieldInvalid('testResultValue');
           setSave();
           result = false;
           alert('<%=errorTestResultValue%>');
      } else {
         var tempRV = testResultValue.substring(0, indexOfComma) + testResultValue.substring(indexOfComma + 1);
         //bugzilla 2317: check for length of numeric not greater 80
         if (IsNumeric(tempRV) && tempRV.length <= 79) {
           setFieldValid('testResultValue');
           setSave();
         } else {
           setFieldInvalid('testResultValue');
           setSave();
           result = false;
           alert('<%=errorTestResultValue%>');
         }
      }
      if ((result == true) && ('<%=Encode.forJavaScript(rgType)%>' == '<%=titerType%>')) {              
		var tempone = testResultValue.substring(0, indexOfComma);
		var temptwo = testResultValue.substring(indexOfComma + 1); 							
		var x = Math.max(tempone, temptwo);		
		if ((x == tempone) && (x != temptwo)) {
		 	result = false;	
		 }      		     	
      	if ( (tempone%10 == 0) && (temptwo%10 == 0) ) {      	
			if ( (tempone > 20480) || (temptwo > 20480) ){  				 	 
				result = false; 
			}
	      	tempone = tempone/10;
	      	temptwo = temptwo/10;
      	}else if ( (tempone > 2048) || (temptwo > 2048) ){       		     	 
			result = false;
		}
      	//x is a power of two ==> (x > 0) and ((x & (x − 1)) == 0) 
      	if ((
	         (( tempone > 0) && ((tempone & (tempone-1)) ==0))    	       
		    &&
		     (( temptwo > 0) && ((temptwo & (temptwo-1)) ==0))
		     
		    ) == false ){		   		  
	         	result = false;	                 	
        }        
        if (result == false ){            
       		 alert('<%=errorTestResultValue%>');         
        }            
     }   
  }  
  return result;
}

function validateSortOrder() {
 var result = true;
 var sortOrder = document.getElementById("sortOrder").value;
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

function validateSignificantDigits() {
	//AIS - bugzilla 1891
	//bugzilla 2360 validate for valid digits and length
    var result = true;
	if ('<%=Encode.forJavaScript(rgType)%>' != '<%=titerType%>') {    
    	 var significantDigits = document.getElementById("significantDigits").value;
		 
		 var strValidChars = "0123";
         var strChar;
         var blnResult = true;

        if (significantDigits.length == 0) blnResult = false;

        //  test significantDigits consists of valid characters listed above
        for (i = 0; i < significantDigits.length && blnResult == true; i++) {
            strChar = significantDigits.charAt(i);
            if (strValidChars.indexOf(strChar) == -1) {
                  blnResult = false;
            }
        }
     	if (blnResult == false) { 
		   setFieldInvalid('significantDigits');
		   setSave();
		   alert('<%=errorSignificantDigits%>');
		   result = false;
		} else {
		   setFieldValid('significantDigits');
		   setSave();
		}
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
  var obj = document.getElementById("add"); 
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
          <td width="16%"><bean:message key="testanalytetestresult.addNonDictionaryRGPopup.testResultValue"/>:</td>
          <td width="84%"> 
             	<html:text  size="40" name="<%=formName%>" property="testResultValue" onblur="skipcycle=false;validateTestResultValue(false)" onfocus="skipcycle=true" /> 
          </td>
        </tr>
        <tr> 
          <td width="16%"><bean:message key="testanalytetestresult.addNonDictionaryRGPopup.sortOrder"/>:</td>
          <td width="84%"> 
              	<html:text size="40" name="<%=formName%>" property="sortOrder" onfocus="skipcycle=true" onblur="skipcycle=false;validateSortOrder()" /> 
          </td>
        </tr>
        <tr> 
          <td width="16%"><bean:message key="testanalytetestresult.addNonDictionaryRGPopup.flags"/>:</td>
          <td width="84%"> 
              	<html:text size="40" name="<%=formName%>" property="flags" onfocus="skipcycle=true" onblur="skipcycle=false" /> 
          </td>
        </tr>
        <tr>
          <td width="16%"><bean:message key="testanalytetestresult.addNonDictionaryRGPopup.significantDigits"/>:</td>
          <td width="84%"> 
              	<html:text size="40" name="<%=formName%>" property="significantDigits" onfocus="skipcycle=true" onblur="skipcycle=false;validateSignificantDigits()" /> 
          </td>
        </tr>
        <tr>
          <td width="16%"><bean:message key="testanalytetestresult.addNonDictionaryRGPopup.quantLimit"/>:</td>
          <td width="84%"> 
              	<html:text size="40" name="<%=formName%>" property="quantLimit" onfocus="skipcycle=true" onblur="skipcycle=false" /> 
          </td>
        </tr>
        <tr> 
          <td width="16%">&nbsp;</td>
          <td width="84%">
  			<html:button onclick="addResultGroup('resultSection');"  property="add" onfocus="skipcycle=true" onblur="skipcycle=false" >
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
<td id="h1" width="78%"><bean:message key="testanalytetestresult.addNonDictionaryRGPopup.selectedTestResults.title"/>:</td>
<td id="h2" width="5%"><bean:message key="testanalytetestresult.addNonDictionaryRGPopup.sortOrder"/>:</td>
<td id="h3" width="5%"><bean:message key="testanalytetestresult.addNonDictionaryRGPopup.flags"/>:</td>
<td id="h4" width="4%"><bean:message key="testanalytetestresult.addNonDictionaryRGPopup.significantDigits.short"/>:</td>
<td id="h5" width="4%"><bean:message key="testanalytetestresult.addNonDictionaryRGPopup.quantLimit.short"/>:</td>
<td id="h6" width="4%"></td>
</tr>

</table>
 
<div class="scrollvertical">
<table id="resultSection" class="blank" width="100%">
<tbody>
</tbody>
</table>
</div>


<%--html:javascript formName="testAnalyteTestResultAddDictionaryRGPopupForm" staticJavascript="true"/--%>

