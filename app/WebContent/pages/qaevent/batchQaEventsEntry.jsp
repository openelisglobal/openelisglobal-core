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

<%!

String allowEdits = "true";
String errorNothingSelected = "";
String qaEventParam = "";
%>

<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

java.util.Locale locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
qaEventParam = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "qaeventsentry.qaevent.title");
errorNothingSelected =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "errors.required.atLeastOneItem.forOneSelectBox", qaEventParam);
%>
<script language="JavaScript1.2">
var BLANK = "b|l|a|n|k";

var formFieldArray = new Array('fromAccessionNumber',
                               'toAccessionNumber'
                               );
var formFieldsValidArray = new Array(true, true);

var formFieldsRequiredArray = new Array(true, false);

function pageOnLoad() {
    var accessionNumber = $("fromAccessionNumber");
    accessionNumber.focus();
}

function nothingSelected() {
  var nthSel = true;
  var selSel = false;
  
  var selectList = $("SelectList");
  var selectOptions = selectList.options;
  var selectOLength = selectOptions.length;
  for (var i = 0; i < selectOLength; i++) {
      if (selectOptions[i].selected == true) {
        selSel = true;
      }
  }
  if (selSel) {
      nthSel = false;
  }
  return nthSel;
}

function loadPickIds() {
  var pickList = "";
 
  var selectList = $("SelectList");
  var selectOptions = selectList.options;
  var selectOLength = selectOptions.length;
  
  var textToSelect;
  var id;
  
  for (var i = 0; i < selectOLength; i++) {
      if (selectOptions[i].selected == true) {
        id = selectOptions[i].value;
        pickList += id;
        pickList += '<%=idSeparator%>';
      }
   }  

  document.getElementById("pickIds").value = pickList;
  
}

function saveThis() {
	if (nothingSelected()) {
		 alert("<%=errorNothingSelected%>");
		 return false;
	} else {
	     loadPickIds();
         setAction(window.document.forms[0], 'Update', 'yes', '?ID=');
	}
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
  var obj = document.forms[0].save; 
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

function validateForm(form) {
    //return validateBatchQaEvemtsEntryForm(form);
    return true;
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

function numbersOnlyCheck(fieldIn) {
	
 	var field = fieldIn.value;
 	var fieldMessage = fieldIn.name + 'Message';
 	var digitCheck = "0123456789";  
 	var IsNumber = true;
 	var Char;
 	var charCd;
 	var mdiv = $(fieldMessage);

	
	//alert("field: " + field + " field length: " + field.length + " fieldMessage: " + fieldMessage );
	if(field ==null || field == '' || field == undefined){
		mdiv.className = "badmessage";
   		setFieldInvalid(field.name);
	}else{
	 	for (i = 0; i < field.length && IsNumber == true; i++) { 
	    	Char = field.charAt(i); 
	    	if (digitCheck.indexOf(Char) == -1) {
	       		IsNumber = false;  
	       		break;         		       		
	    	}
	 	}
	 	//alert("After for loop, IsNumber is: " + IsNumber);
	
		if(!IsNumber){
			mdiv.className = "badmessage";
	   		setFieldInvalid(field.name);
		}else{
			mdiv.className = "blank";
	   		setFieldValid(field.name);
		}
	}
	
	setSave();
}

function validateAccessionNumber(field) {

 	var fieldMessage = field.name + 'Message';
  	var mdiv = $(fieldMessage);
 	
    if (field.name == 'toAccessionNumber' && ($F("toAccessionNumber")== null || $F("toAccessionNumber")== '')) {
          mdiv.className = "blank";
          setFieldValid(field.name);
    	  setSave();
	      return;	
    }
    
	if ($F("toAccessionNumber") != null && $F("toAccessionNumber")!= '') {
	  //if applicable check to make sure 2nd accessionNumber is > first
	    if ($F("fromAccessionNumber") == null || $F("fromAccessionNumber") == '' || ($F("toAccessionNumber") != null && $F("toAccessionNumber") != '' && $F("toAccessionNumber") <= $F("fromAccessionNumber"))) {
	      setMessage("invalid", field.name);		
	      setSave();
	      return;	
	    }
	}
	
	numbersOnlyCheck(field);

	if (field.value != ""){
	    new Ajax.Request (
                    'ajaxXML',  //url
                    {//options
                     method: 'get', //http method
                     parameters: 'provider=AccessionNumberValidationProvider&form=' + document.forms[0].name + '&field=' + field.name + '&id=' + escape(field.value),      //request parameters
                     //indicator: 'throbbing'
                     onSuccess:  processSuccess,
                     onFailure:  processFailure
                    }
              ); 
	 } else{		    
	    var myMessage = "invalid";
		var myField = field.name;		
		setMessage(myMessage, myField);		
     } 
}


function setMessage(message, field) {
     var fieldMessage = field + "Message";
     var mdiv = $(fieldMessage);
     var idField = $(field);
     
     if (message == "invalid") {
       mdiv.className = "badmessage";
       setFieldInvalid(field);
     } else {
       mdiv.className = "blank";
       setFieldValid(field);
    }
    setSave();
 }

var req;
</script>

<html:hidden name="<%=formName%>" property="pickIds" styleId="pickIds"/>
<table align="center">
	<tr>
		<td width="48%">
		  <table height="100%" align="right">
		    <tr>
		     <td scope="row" align="left">
              <strong><bean:message key="batchqaeventsentry.qaevents.title"/></strong>
             </td>
            </tr>
            <tr>
		      <td scope="row"><%--bugzilla 2548 increase width--%>
		        <select name="<%=formName%>" 
			        id="SelectList" 
			        property="SelectList"
			        size="24" 
			        multiple="multiple" 
			        style="width: 800px" 
			        type="us.mn.state.health.lims.qaevent.valueholder.QaEvent"
			        onkeypress="return selectAsYouType(event)" 
			        onblur="clearKeysPressed(event)"					
			        >
		    	   <logic:iterate id="qaEvent" property="SelectList" name="<%=formName%>">
					<bean:define id="qaEventId" name="qaEvent" property="id" />
					<option value="<%=qaEventId%>">
						<bean:write name="qaEvent" property="qaEventDisplayValue" />
        			</option>
				  </logic:iterate>
			    </select>
			  </td>
			 </tr>
		  </table>
		</td>
		<td width="4%">&nbsp;</td>
		<td width="48%">
		 <table height="100%" align="left">
		   <tr>
		     <td>
     	          <strong><bean:message key="batchqaeventsentry.range.from"/>:</strong>&nbsp;
     	     </td>
     	     <td>&nbsp;</td>
      	     <td align="left">
     	          <app:text name="<%=formName%>" property="fromAccessionNumber" styleId="fromAccessionNumber" styleClass="text" onblur="validateAccessionNumber(this);" size="20" maxlength="10" />
   	              <div id="fromAccessionNumberMessage" class="blank" >&nbsp;</div> 
     		 </td>
     		 <td>
     		   &nbsp;
     		 </td>
     	 </tr>
     	 <tr>
     		 <td>
     	          <strong><bean:message key="batchqaeventsentry.range.to"/>:</strong>&nbsp;
       	     </td>
       	     <td>
                &nbsp;
             </td>
             <td align="left">
     	          <app:text name="<%=formName%>" property="toAccessionNumber" styleId="toAccessionNumber" styleClass="text" onblur="validateAccessionNumber(this);" size="20" maxlength="10" />
   	              <div id="toAccessionNumberMessage" class="blank" >&nbsp;</div> 
		     </td>
			 <td>
     		   &nbsp;
     		 </td>
	    </tr>
	    <tr>
		     <td colspan="3">
		       <strong><bean:message key="batchqaeventsentry.range.skips"/>:</strong>&nbsp;<bean:message key="batchqaeventsentry.range.skips.detail"/>
		     </td>
			 <td colspan="1">
     		   &nbsp;
     		 </td>
        </tr>
        <tr>
             <td colspan="3">
                <%--html:textarea name="<%=formName%>" property="skipAccessionNumber" cols="11" rows="20" /--%>
                <textarea name="skipAccessionNumber" cols="11" rows="20" wrap="soft"></textarea>
	 	     </td>
			 <td colspan="1">
     		   &nbsp;
     		 </td>
	 	 </tr>
	  </table>
	</td>
  </tr>
</table>
