<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,java.util.Locale, org.apache.struts.Globals,
	us.mn.state.health.lims.common.action.IActionConstants, 
	us.mn.state.health.lims.common.util.SystemConfiguration" %>     
<%@ page import="org.owasp.encoder.Encode" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="nbse" name="<%=formName%>" property="newbornSampleTwoMap" type="java.util.HashMap"/>
<bean:define id="expectedStatus" value='<%= SystemConfiguration.getInstance().getSampleStatusEntry1Complete() %>' />
<bean:define id="expectedDomain" value='<%= SystemConfiguration.getInstance().getNewbornDomain() %>' />

<%
    Locale locale = (Locale)request.getSession().getAttribute(Globals.LOCALE_KEY);
    String dobKey = us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(locale,"newborn.sample.full.birth.date");
	String errorDobAndTob = us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(locale,"errors.required",dobKey);
	String docKey = us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(locale,"newborn.sample.full.date.of.collection");
	String errorDocAndToc = us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(locale,"errors.required",docKey);
	String gramKey = us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(locale,"page.default.gram.option");
	String poundKey = us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(locale,"page.default.pound.option");
%>

<script language="JavaScript1.2">

var expectedStatus = '<%=expectedStatus%>';
var expectedDomain = '<%=expectedDomain%>';

var formFieldArray = new Array('accessionNumber','lastName','firstName','birthDateForDisplay',
                               'birthTimeForDisplay','collectionDateForDisplay','collectionTimeForDisplay',
                               'birthWeight');
var formFieldsValidArray = new Array(true,true,true,true,true,true,true,true);
var formFieldsRequiredArray = new Array(true,false,false,false,false,false,false,false);
var BLANK = "b|l|a|n|k";

function pageOnLoad() {
   	var accnNumb = $("accessionNumber");
   	var lName = $("lastName");
   	if ( accnNumb.value.length == 0 )
   	    accnNumb.focus();  
   	else
   	    lName.focus();
   	document.forms[0].save.disabled=true;        
}

function setMyCancelAction(form, action, validate, parameters) {
 	setAction(window.document.forms[0], 'Cancel', 'no', '');
}

function validateForm(form) {
    if ( checkForm(form) )
        return validateNewbornSampleTwoForm(form);
}

function checkForm(form) {
    var accnNumb = $("accessionNumber");
    var birthTime = $("birthTimeForDisplay");
    var birthDate = $("birthDateForDisplay");
	var collTime = $("collectionTimeForDisplay");
	var collDate = $("collectionDateForDisplay");    
    var birthWeight = $("birthWeight");
    var selectedBirthWeight = $("selectedBirthWeight");
      
    if ( (birthTime.value.length != 0) && (birthTime.value != '00:00') ) {
        if ( birthDate.value.length == 0 ) {
            alert('<%=errorDobAndTob%>');
            birthDate.focus();
            return false;    
        }    
    }
    if ( (collTime.value.length != 0) && (collTime.value != '00:00') ) {
        if ( collDate.value.length == 0 ) {
            alert('<%=errorDocAndToc%>');
            collDate.focus();
            return false;    
        }    
    }
    
    if ( birthWeight != null )  {
        if ( selectedBirthWeight.value == "<%=poundKey%>" ) {
            var temp = birthWeight.value / 0.0022046226218;
            var grams = Math.round(temp);
            document.forms[0].birthWeight.value = grams;
            document.forms[0].selectedBirthWeight.value = "<%=gramKey%>";
        } else {
            document.forms[0].birthWeight.value = Math.round(birthWeight.value);    
        }       
    }

    return true;
}

function processFailure(xhr) {
}

function processSuccess(xhr) {
    var message = xhr.responseXML.getElementsByTagName("message")[0];
    var formfield = xhr.responseXML.getElementsByTagName("formfield")[0];
    setMessage(message.childNodes[0].nodeValue, formfield.childNodes[0].nodeValue);
    setSave();
}

function setMessage(message, field) {
     var fieldMessage = field + "Message";
     var mdiv = $(fieldMessage);
     var idField = $(field);

     if (message == "invalid") {
	   if ($F(field) == "" && !isFieldRequired(field) && (idField.name != 'accessionNumber')){
            mdiv.className = "blank";
       } else {
            mdiv.className = "badmessage";
            setFieldInvalid(field);
       }
     } else {
        mdiv.className = "blank";
        setFieldValid(field);
        if (message != "validStatus") {       
	        if (field != 'accessionNumber') {        
	        } else{     
	            //To check the status of the sample
	            new Ajax.Request (
                          'ajaxXML',  //url
                           {//options
                             method: 'get', //http method
                             parameters: 'provider=SampleStatusValidationProvider&field=accessionNumber&id=' + escape(document.forms[0].accessionNumber.value) + '&expectedStatus=' + escape(expectedStatus),      //request parameters
                             //indicator: 'throbbing'
                             onSuccess:  processSuccess,
                             onFailure:  processFailure
                           }
                          );        
	        } 
        }
    }
}

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

function getFieldIndex(field) {
    var i;
    for (i = 0; i < formFieldArray.length; i++) {
        if (formFieldArray[i] == field) {
            break;
        }
    }
    return i;
}
 
function validateAccessionNumber(field) {
    numbersOnlyCheck(field);
	if (field.value != ""){
	    new Ajax.Request (
                    'ajaxXML',  //url
                    {//options
                     method: 'get', //http method
                     parameters: 'provider=AccessionNumberValidationProvider&form=' + document.forms[0].name + '&field=accessionNumber&id=' + escape(field.value),      //request parameters
                     //indicator: 'throbbing'
                     onSuccess:  processAccessionNumberValidationSuccess,
                     onFailure:  processFailure
                    }
              ); 
	 } else{		    
	    var myMessage = "invalid";
		var myField = "accessionNumber";		
		setMessage(myMessage, myField);		
		setSave();
    }  
}

function processAccessionNumberValidationSuccess(xhr) {
  var message = xhr.responseXML.getElementsByTagName("message")[0];
  var formfield = xhr.responseXML.getElementsByTagName("formfield")[0];
  setAccessionNumberValidationMessage(message.childNodes[0].nodeValue, formfield.childNodes[0].nodeValue);
}

function setAccessionNumberValidationMessage(message, fieldIn) {
    var fieldMessage = fieldIn + 'Message';
    var mdiv = $(fieldMessage);
    if ( (message == 'invalid') || (message == "invalidStatus") ) {        
        mdiv.className = "badmessage";
   		setFieldInvalid(fieldIn);
   		setSave();
   		return;    
    } else {
        validateDomain(fieldIn);
    }       
}

function getBarcode(field) {
	var accNbr = document.forms[0].accessionNumber.value;
    var preNum = '<%=Encode.forJavaScript((String)request.getAttribute("preAccessionNumber"))%>';
    if ( accNbr.length > 0 ) {
        if ( accNbr != preNum ) {
            setAction(window.document.forms[0], 'PopulateData2', 'no', '');                
        }    
    }    
}

function validateDomain(field) {
    new Ajax.Request (
        'ajaxXML',  //url
        {//options
        method: 'get', //http method
        parameters: 'provider=SampleDomainValidationProvider&form=' + document.forms[0].name + '&field=accessionNumber&id=' + escape(document.forms[0].accessionNumber.value) + '&expectedDomain=' + escape(expectedDomain),      //request parameters
        //indicator: 'throbbing'
        onSuccess:  processDomainValidationSuccess,
        onFailure:  processFailure
        }
    ); 
}

function processDomainValidationSuccess(xhr) {
  var message = xhr.responseXML.getElementsByTagName("message")[0];
  var formfield = xhr.responseXML.getElementsByTagName("formfield")[0];
  setDomainValidationMessage(message.childNodes[0].nodeValue, formfield.childNodes[0].nodeValue);
}

function setDomainValidationMessage(message, field) {
    var fieldMessage = field + 'Message';
    var mdiv = $(fieldMessage);

    if ( message == 'invalid') {        
        mdiv.className = "badmessage";
   		setFieldInvalid(field);   		    
        setSave();
        return;
    } else {
        mdiv.className = "blank";
        setFieldValid(field);
        setSaveButton(false);
        getBarcode(field);
    }       
}
    
function numbersOnlyCheck(fieldIn) {
 	var field = fieldIn.value;
 	var fieldMessage = fieldIn.name + 'Message';
 	var digitCheck = "0123456789";  
 	var IsNumber = true;
 	var Char;
 	var charCd;
 	var mdiv = $(fieldMessage);

	if(field ==null || field == '' || field == undefined){
		mdiv.className = "badmessage";
   		setFieldInvalid(field.name);
   		setSaveButton(true);
	} else {
	 	for (i = 0; i < field.length && IsNumber == true; i++) { 
	    	Char = field.charAt(i); 
	    	if (digitCheck.indexOf(Char) == -1) {
	       		IsNumber = false;  
	       		break;         		       		
	    	}
	 	}
	
		if(!IsNumber){
			mdiv.className = "badmessage";
	   		setFieldInvalid(field.name);
	   		setSaveButton(true);
		}else{
			mdiv.className = "blank";
	   		setFieldValid(field.name);
	   		setSaveButton(false);
		}
	}	
	setSave();
}

function setFieldValid(field) {
   var index = getFieldIndex(field);
   formFieldsValidArray[index] = true;
}

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

function myCheckDate(date, event, dateCheck, onblur) {
    if(date !=null && date != '' && date != undefined) {
	    var messageDiv = date.name + 'Message';
	    var mdiv = $(messageDiv);
	    var validDate = DateFormat(date,date.value,event,dateCheck,'1');
	    if (dateCheck) { 
	        if (validDate) {
	            var validDate2 = lessThanCurrent(date);
	            if (validDate2) {
	                mdiv.className = "blank";
	                setFieldValid(date.name);
	                if (onblur) {
	                    validateFieldCompareNBE(date);
	                }
	            } else {
	                mdiv.className = "badmessage";
	                setFieldInvalid(date.name);
	            }
	        } else {
	            mdiv.className = "badmessage";
	            setFieldInvalid(date.name);
	        }
	        setSave();
	    }
    }
}

function myCheckTime(time) {
    var fieldMessage = time.name + 'Message';
    var mdiv = $(fieldMessage);
    if (checkTime(time)) {
       mdiv.className = "blank";
       setFieldValid(time.name);
       validateFieldCompareNBE(time);
    } else {
       mdiv.className = "badmessage";
       setFieldInvalid(time.name);
    }
   setSave();
}

function checkBirthWeight(fieldIn) {
 	var field = fieldIn.value;
 	var fieldMessage = fieldIn.name + 'Message';
 	var mdiv = $(fieldMessage);

    if ( isNaN(field) ) {	
	    mdiv.className = "badmessage";
	   	setFieldInvalid(field.name);
	   	setSaveButton(true);
    } else {
        if ( field < 0 ) {
            mdiv.className = "badmessage";
	   	    setFieldInvalid(field.name);
	   	    setSaveButton(true);
        } else {
		    mdiv.className = "blank";
		    setFieldValid(field.name);
		    validateFieldCompareNBE(fieldIn);
	   	    setSaveButton(false);
   	    }    
	}	
	setSave();    
}

function convertWeight(field) {
    var weight = document.forms[0].birthWeight.value;          
    if ( field.value.length==0 ) {
        document.forms[0].selectedBirthWeight.value = "<%=gramKey%>";    
    } 
    else {    
        if ( (field.value == '<%=gramKey%>') && (weight.length > 0) ) {
            var temp = weight / 0.0022046226218;
            var grams = Math.round(temp);
            document.forms[0].birthWeight.value = grams;
        } 
        else {
            if (weight.length > 0) { 
                var temp = weight * 0.0022046226218;
                var pounds = Math.round(temp);
                document.forms[0].birthWeight.value = pounds;
            }
        }
    }            
}
   
function setSaveButton(value) {
    document.forms[0].save.disabled = value;    
}

function validateFieldCompareNBE(field) {
    var fieldName = field.name;
    var errField = $("errorInField");
  
    if (errField.value == "" || fieldName == errField.value) {
       errField.value = fieldName;
    }
    compareWithNBE(fieldName);
}

function compareWithNBE(field) {
    var hidden2 = "nbe2" + field;
    if(""==$F(hidden2)){
	    validatePollFirstTime(field);
    } else {
	    validatePollNotFirstTime(field);
    }	
}

function validatePollFirstTime(field) {
    var message = "";
    var fld = $(field);    
    message = getNBEValueFromHashMap(field);
       
    var entry1 = message;
    var entry2 = fld.value;
    
    var hidden1 = "nbe1" + field;
    var hidden2 = "nbe2" + field;
    $(hidden1).value = entry1;
  
    if (entry2 == "") {
        $(hidden2).value = BLANK;
    } else {
        $(hidden2).value = entry2;
    }

    if( entry1 != entry2 ) {
        fld.value = "";
        setNBEMessage('invalid', field);
    } else {
        $("errorInField").value = "";
        $(hidden2).value = "";
        setNBEMessage('valid', field);
    }
}

function validatePollNotFirstTime(field) {
    var message = "";
    var fld = $(field);    
       
	var entry2 = fld.value;
	var hidden1 = "nbe1" + field;
	var hidden2 = "nbe2" + field;
    var entry1 = $F(hidden1);
    var entry2FirstTime = $F(hidden2);
        
    if ($F(hidden2) == BLANK) {
       entry2FirstTime = "";
    }
    if(entry1==entry2){
      	$(hidden2).value = "";
        $("errorInField").value = "";
      	setNBEMessage('valid', field);
    }
    if(entry2FirstTime==entry2){
       	$(hidden2).value = "";
        $("errorInField").value = "";
      	setNBEMessage('valid', field);
    }   
    if (entry1 != entry2 && entry2FirstTime != entry2) {
        if (entry2 == "" && !isFieldRequired(field)) {
            $(hidden2).value = BLANK;
        } else {
            $(hidden2).value = entry2;
        }
        fld.value = "";
        setNBEMessage('invalid', field);
    }
}

function getNBEValueFromHashMap(field) {
    var value = "";    
    if (field == "lastName")
        value = "<%= nbse.get("lastName") %>";
    if ( field == "firstName" )
        value = "<%= nbse.get("firstName") %>";    
    if (field == "birthDateForDisplay") 
        value = "<%= nbse.get("birthDateForDisplay") %>";
    if (field == "birthTimeForDisplay") 
        value = "<%= nbse.get("birthTimeForDisplay") %>";
	if (field == "collectionDateForDisplay") 
    	value = "<%= nbse.get("collectionDateForDisplay") %>";
	if (field == "collectionTimeForDisplay") 
    	value = "<%= nbse.get("collectionTimeForDisplay") %>";        
    if (field == "birthWeight") 
        value = "<%= nbse.get("birthWeight") %>";
                            
    return value;
}

function formatHSEField(field, toUpper) {
   if (field.value != null && field.value != '') {
       field.value = trim(field.value);
       field.value = field.value.replace(/\s+/g,' ');
       if (toUpper == true) {
          field.value = field.value.toUpperCase();
       }
   }
}

function setNBEMessage(message, field) {
     var fieldMessage = field + "Message";
     var mdiv = $(fieldMessage);
     if (message == "invalid") {
        mdiv.className = "questionmessage";
        setFieldInvalid(field);
     } else {
        mdiv.className = "blank";
        setFieldValid(field);
     }
      setSave();
}


</script>

<html:hidden property="newbornDomain" name="<%=formName%>" styleId="newbornDomain"/>
<html:hidden property="currentDate" name="<%=formName%>" styleId="currentDate"/>
<html:hidden property="errorInField" value="" styleId="errorInField"/>

<table>
	<tr>
		<td>
		    <bean:message key="sample.accessionNumber"/>:<span class="requiredlabel">*</span>
		</td>	
		<td> 
		    <html:text name="<%=formName%>" property="accessionNumber" styleId="accessionNumber" styleClass="text" onblur="validateAccessionNumber(this);" size="20" maxlength="10" />
			<div id="accessionNumberMessage" class="blank" >&nbsp;</div>
        </td>
        <td>
            <bean:message key="newborn.sample.barcode"/>:
        </td>
        <td>
            <html:text name="<%=formName%>" property="barcode" styleId="barcode" styleClass="text" size="20" disabled="true"/>
        </td>                
	</tr>
 	<tr>
        <td>
            <bean:message key="newborn.sample.full.last.name"/>:
        </td>		
        <td>
            <html:text name="<%=formName%>" property="lastName" styleId="lastName" styleClass="text" onblur="formatHSEField(this,true);validateFieldCompareNBE(this);" size="20" maxlength="30"/>
            <div id="lastNameMessage" class="blank" >&nbsp;</div>
        </td>
        <td>
            <bean:message key="newborn.sample.full.first.name"/>:
        </td>		
        <td>
            <html:text name="<%=formName%>" property="firstName" styleId="firstName" styleClass="text" onblur="formatHSEField(this,true);validateFieldCompareNBE(this);" size="20" maxlength="20"/>
            <div id="firstNameMessage" class="blank" >&nbsp;</div>
        </td>	            		
	</tr>
	<tr>
        <td>
            <bean:message key="newborn.sample.full.birth.date"/>:<br>
	        <font size="1"><bean:message key="humansampleone.date.additionalFormat" /></font>
	    </td>
        <td>
            <app:text name="<%=formName%>" property="birthDateForDisplay" styleId="birthDateForDisplay" size="20" maxlength="10" onkeyup="myCheckDate(this, event, false,false)" onblur="myCheckDate(this, event, true, true)" styleClass="text" />
	        <div id="birthDateForDisplayMessage" class="blank">&nbsp;</div>
        </td>
        <td>
            <bean:message key="newborn.time.of.birth"/>:<br>
            <font size="1"><bean:message key="humansampleone.time.additionalFormat" /></font>
        </td>		
        <td>
            <app:text name="<%=formName%>" property="birthTimeForDisplay" styleId="birthTimeForDisplay" size="13" maxlength="15" onblur="myCheckTime(this);" styleClass="text" />
            <div id="birthTimeForDisplayMessage" class="blank">&nbsp;</div>
        </td>	            		
	</tr>
	<tr>
        <td>
            <bean:message key="newborn.sample.full.date.of.collection"/>:<br>
	        <font size="1"><bean:message key="humansampleone.date.additionalFormat" /></font>
	    </td>
        <td>
            <app:text name="<%=formName%>" property="collectionDateForDisplay" styleId="collectionDateForDisplay" size="20" maxlength="10" onkeyup="myCheckDate(this, event, false,false)" onblur="myCheckDate(this, event, true, true)" styleClass="text" />
	        <div id="collectionDateForDisplayMessage" class="blank">&nbsp;</div>
        </td>
        <td>
            <bean:message key="newborn.sample.full.time.of.collection"/>:<br>
            <font size="1"><bean:message key="humansampleone.time.additionalFormat" /></font>
        </td>		
        <td>
            <app:text name="<%=formName%>" property="collectionTimeForDisplay" styleId="collectionTimeForDisplay" size="13" maxlength="15" onblur="myCheckTime(this);" styleClass="text" />
            <div id="collectionTimeForDisplayMessage" class="blank">&nbsp;</div>
        </td>	            		
	</tr>
	<tr>		
        <td>
            <bean:message key="newborn.birth.weight"/>:
        </td>		
        <td>
            <app:text name="<%=formName%>" property="birthWeight" styleId="birthWeight" size="6" maxlength="10" styleClass="text" onblur="checkBirthWeight(this);"/>
            <div id="birthWeightMessage" class="blank">&nbsp;</div>
            <html:select name="<%=formName%>" property="selectedBirthWeight" onchange="convertWeight(this);">
				<app:optionsCollection name="<%=formName%>" property="birthWeightList" label="label" value="value" />
			</html:select>	
        </td>			
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
</table>

<%--hidden fields--%>
<input type="hidden" name="nbe1lastName" id="nbe1lastName"/>
<input type="hidden" name="nbe2lastName" id="nbe2lastName"/>

<input type="hidden" name="nbe1firstName" id="nbe1firstName"/>
<input type="hidden" name="nbe2firstName" id="nbe2firstName"/>

<input type="hidden" name="nbe1birthDateForDisplay" id="nbe1birthDateForDisplay"/>
<input type="hidden" name="nbe2birthDateForDisplay" id="nbe2birthDateForDisplay"/>
<input type="hidden" name="nbe1birthTimeForDisplay" id="nbe1birthTimeForDisplay"/>
<input type="hidden" name="nbe2birthTimeForDisplay" id="nbe2birthTimeForDisplay"/>

<input type="hidden" name="nbe1collectionDateForDisplay" id="nbe1collectionDateForDisplay"/>
<input type="hidden" name="nbe2collectionDateForDisplay" id="nbe2collectionDateForDisplay"/>
<input type="hidden" name="nbe1collectionTimeForDisplay" id="nbe1collectionTimeForDisplay"/>
<input type="hidden" name="nbe2collectionTimeForDisplay" id="nbe2collectionTimeForDisplay"/>

<input type="hidden" name="nbe1birthWeight" id="nbe1birthWeight"/>
<input type="hidden" name="nbe2birthWeight" id="nbe2birthWeight"/>

<app:javascript formName="newbornSampleTwoForm" staticJavascript="true"/>