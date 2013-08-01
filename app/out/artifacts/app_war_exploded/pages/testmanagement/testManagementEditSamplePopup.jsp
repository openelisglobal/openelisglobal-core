<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants, 
	us.mn.state.health.lims.common.util.SystemConfiguration" %>  

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<%--bugzilla 1510 add styleId for compatibility in firefox and for use of firebug debugger--%>
<%--bugzilla 2069 using organizationLocalAbbreviation--%>
<%--bugzilla 1904 did some reformatting for longer label and extra row--%>
<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="humanDomain" value='<%= SystemConfiguration.getInstance().getHumanDomain() %>' />

<%!
String allowEdits = "true";
//bugzilla 1899
String errorSelectCombo = "";
String lbLoadMessage = "";
%>

<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
		allowEdits = (String) request
			.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}
request.setAttribute(IActionConstants.ALLOW_EDITS_KEY, allowEdits);
			
java.util.Locale locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
//bugzilla 1899
errorSelectCombo =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "humansampleone.cityStateZipPopup.selectone.error");
                    
lbLoadMessage =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "common.loading.message");

%>


<script language="JavaScript1.2">
//bugzilla 1899
var lBox;
var formFieldArray = new Array('birthDateForDisplay',
                                    'chartNumber',
                                    'city',
                                    'clientReference', 
                                    'collectionDateForDisplay',
                                    'collectionTimeForDisplay',
                                    'externalId',
                                    'firstName',
                                    'gender',
                                    'lastName', 
                                    'middleName',
                                    'multipleUnit', 
                                    'organizationLocalAbbreviation', 
                                    'projectIdOrName',
                                    'project2IdOrName',
                                    'providerFirstName', 
                                    'providerLastName',
                                    'providerWorkPhone',
                                    'providerWorkPhoneExtension',
                                    'receivedDateForDisplay',
                                    'referredCultureFlag',
                                    'sourceOfSampleDesc',
                                    'sourceOther',
                                    'state', 
                                    'stickerReceivedFlag',
                                    'streetAddress',
                                    'typeOfSampleDesc',
                                    'zipCode'   
                                    );
var formFieldsValidArray = new Array(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
var formFieldsRequiredArray = new Array(false, false, false, true, false, false, false, false, false, false, false, true, 
                         //bugzilla 2589 unknown submitter is null
                         false, 
                         false, false, false, true, true, false, false, false, true, false, true, false);

function pageOnLoad() { 

	//****To remove white spaces in the end of these string (otherwise, it causes invalidation)

	var idField1 = $("city");
	idField1.value = rTrim(idField1.value);	
	var idField2 = $("zipCode");
	idField2.value = rTrim(idField2.value); 
	var idField3 = $("providerWorkPhoneExtension");
	idField3.value = rTrim(idField3.value);
	
}


function rTrim(str)
{
	var space = new String(" \t\n\r");
	var newstr = new String(str);
	if (space.indexOf(newstr.charAt(newstr.length-1)) != -1) {
	    var i = newstr.length - 1; 
	    while (i >= 0 && space.indexOf(newstr.charAt(i)) != -1) { i--; }		
		newstr = newstr.substring(0, i+1);
	}	
	return newstr;
}

function cancelToParentForm() {
   if (window.opener && !window.opener.closed && window.opener.document.forms[0]) {      
        window.close();
   } 
}

//bugzilla 1899 - coming from city/state/zip popup
function saveItToLightBoxParentForm() {

   var city, state, zip;
   var selCombo;
   var selectedCombo = humanSampleCityStateZipPopupForm.elements['selectedCombo'];
   for (var i = 0; i < selectedCombo.length; i++) {
       if (selectedCombo[i].checked == true) {
           selCombo = selectedCombo[i].value;
           document.forms[0].city.value = humanSampleCityStateZipPopupForm.elements['city' + selCombo].value;
           document.forms[0].state.value = humanSampleCityStateZipPopupForm.elements['state' + selCombo].value;
           document.forms[0].zipCode.value = humanSampleCityStateZipPopupForm.elements['zipCode' + selCombo].value;
           //clear out the error divs 
           $("cityMessage").className = "blank";
           $("stateMessage").className = "blank";
           $("zipCodeMessage").className = "blank";
           break;
       }
    }

   if (selCombo != null) {
     checkConfirmExit(true);
   } else {
     alert('<%=errorSelectCombo%>');
   }
}

//bugzilla 1899
function saveItToParentForm(form) {
 submitTheForm(form);
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

function doFocus(field, nextField){
  var fieldMessage = field + 'Message';
  var mdiv = $(fieldMessage);
  var focusField = $(field);
  focusField.focus();
  //clear out badmessage that occurs on onblur before selection is made in Ajax drop down (this is a premature badmessage)
  if (mdiv.className == "badmessage") {
      mdiv.className = "blank";
      setFieldValid(field);
      //focus on next field
      var nextF = $(nextField);
      nextF.focus();
  } 
}

function cityFocus() { 
  var field = 'city';
  var nextField = 'state';
  doFocus(field, nextField);
}

function projectFocus() { 
  var field = 'projectIdOrName';
  var nextField = 'project2IdOrName';
  doFocus(field, nextField);
}

function project2Focus() { 
  var field = 'project2IdOrName';
  var nextField = 'organizationLocalAbbreviation';
  doFocus(field, nextField);
}

function sampleSourceFocus() { 
  var field = 'sourceOfSampleDesc';
  nextField = 'sourceOther';
  doFocus(field, nextField);
}

function sampleTypeFocus() {
  var field = 'typeOfSampleDesc';
  nextField = 'sourceOfSampleDesc';
  doFocus(field, nextField);
}

function validateForm(form) {     
    var validated = validateTestManagementForm(form);    
    return validated;
}

//This is for validating individual fields: submitter#, city, state, sample type, sample source etc
function setMessage(message, field) {
     //alert("I am in in setMessage - reg. validation - with message and field " + message + " " + field);
     var fieldMessage = field + "Message";
     var mdiv = $(fieldMessage);
     idField = $(field);
     
     if (message == "invalid") {
       //some fields are not required
      if (idField.value == "" && !isFieldRequired(field)) {
         mdiv.className = "blank";
       } else {
       mdiv.className = "badmessage";
       setFieldInvalid(field);
       }
      //FOR ORGANIZATION ID the text behind VALID (returned from ajax validation) is the Organization Name and we need to display that
       if (idField.name == "organizationLocalAbbreviation") {
             var orgName = $("organizationName");
             orgName.innerHTML = "";
       }
       //FOR PROJECT INPUT FIELDS the text behind VALID (returned from ajax validation) is the Project Name/or Id and we need to display that
       if (idField.name == "projectIdOrName") {
             var projectNameOrIdDisplay = $("projectNameOrIdDisplay");
             projectNameOrIdDisplay.innerHTML = "";
             //fix js error if invalid project
             //var projectNameOrId = $("projectNameOrId");
             //projectNameOrId.innerHTML = "";
             
       }
       if (idField.name == "project2IdOrName") {
             var project2NameOrIdDisplay = $("project2NameOrIdDisplay");
             project2NameOrIdDisplay.innerHTML = "";
             //fix js error if invalid project
             //var project2NameOrId = $("project2NameOrId");
             //project2NameOrId.innerHTML = "";
       }
     } else {
       mdiv.className = "blank";
       setFieldValid(field);
       
       // Adding the following for "type of sample" and "source of sample" because..
       // Otherwise on selection of the autocomplete value, the record's id is displayed in the form field
       // instead of the description.
       //bugzilla 1894
       if (idField.name == 'sourceOfSampleDesc') {
       //bugzilla 1937 (this was mistakenly changed...changing back)
                $F("sourceOfSampleId") = message.substring(5);
             } else {             
	             if (idField.name == 'typeOfSampleDesc') {
	                $F("typeOfSampleId") = message.substring(5);
	             }              
             }          
             
      //this is to correct case according to a value found in database
      if (message.length > 5) {
         if (idField.name == "organizationLocalAbbreviation") {
            //FOR ORGANIZATION ID the text behind VALID (returned from ajax validation) is the Organization Name and we need to display that
             var orgName = $("organizationName");
             orgName.innerHTML = message.substring(5);
         } else if (idField.name == "projectIdOrName") {
             //FOR PROJECT ID the text behind VALID (returned from ajax validation) is the Project Name and we need to display that
             var projectNameOrIdDisplay = $("projectNameOrIdDisplay");
             projectNameOrIdDisplay.innerHTML = message.substring(5);
             //bugzilla 1894 remove $F() function from left side of assignment operator everywhere - this does not work
             $("projectNameOrId").value = message.substring(5);
         } else if (idField.name == "project2IdOrName") {
             //FOR PROJECT2 ID the text behind VALID (returned from ajax validation) is the Project Name and we need to display that
             var project2NameOrIdDisplay = $("project2NameOrIdDisplay");
             project2NameOrIdDisplay.innerHTML = message.substring(5);
             $("project2NameOrId").value = message.substring(5);
         } else {
             idField.value = message.substring(5);
         } 
      } else {
      //clear out project displayed
         if (idField.name == "projectIdOrName") {
             var projectNameOrIdDisplay = $("projectNameOrIdDisplay");
             projectNameOrIdDisplay.innerHTML = "";
             $("projectNameOrId").value = "";
         } else if (idField.name == "project2IdOrName") {
             var project2NameOrIdDisplay = $("project2NameOrIdDisplay");
             project2NameOrIdDisplay.innerHTML = "";
             $("project2NameOrId").value = "";
         }
      } 
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
  setSave();
}

function validateProjectIdOrName() {

     new Ajax.Request (
       'ajaxXML',  //url
       {//options
        method: 'get', //http method
        parameters: 'provider=BasicProjectIdOrNameValidationProvider&field=projectIdOrName&id=' + escape($F("projectIdOrName")),      //request parameters
        //indicator: 'throbbing'
        onSuccess:  processSuccess,
        onFailure:  processFailure
       }
     );
}

function validateProject2IdOrName() {

     new Ajax.Request (
       'ajaxXML',  //url
       {//options
        method: 'get', //http method
        parameters: 'provider=BasicProjectIdOrNameValidationProvider&field=project2IdOrName&id=' + escape($F("project2IdOrName")),      //request parameters
        //indicator: 'throbbing'
        onSuccess:  processSuccess,
        onFailure:  processFailure
       }
     );
}

function validateOrganizationLocalAbbreviation() {

     new Ajax.Request (
       'ajaxXML',  //url
       {//options
        method: 'get', //http method
        parameters: 'provider=OrganizationLocalAbbreviationValidationProvider&field=organizationLocalAbbreviation&id=' + escape($F("organizationLocalAbbreviation")),      //request parameters
        //indicator: 'throbbing'
        onSuccess:  processSuccess,
        onFailure:  processFailure
       }
     );
}

//bugzilla 1899
function validateCity() {
 //alert("I am in validateCity");
 var idField = $("city");
 var fieldName = idField.name;
 //bugzilla 1765 city validation no longer validates combo city/zip
 new Ajax.Request (
           'ajaxXML',  //url
             {//options
              method: 'get', //http method
              parameters: 'provider=CityValidationProvider&field=city&id=' + escape($F("city")),      //request parameters
              //indicator: 'throbbing'
              onSuccess:  processSuccess,
              onFailure:  processFailure
             }
          );
}

//bugzilla 1899
function validateState() {
 //alert("I am in validateState");
 new Ajax.Request (
           'ajaxXML',  //url
             {//options
              method: 'get', //http method
              parameters: 'provider=StateValidationProvider&field=state&id=' + escape($F("state")),      //request parameters
              //indicator: 'throbbing'
              onSuccess:  processSuccess,
              onFailure:  processFailure
             }
          );
}


function validateSampleType() {

     new Ajax.Request (
       'ajaxXML',  //url
       {//options
        method: 'get', //http method
        parameters: 'provider=QuickEntrySampleTypeValidationProvider&field=typeOfSampleDesc&id=' + escape($F("typeOfSampleDesc")),      //request parameters
        //indicator: 'throbbing'
        onSuccess:  processSuccess,
        onFailure:  processFailure
       }
     );
     
     //bugzilla 2273 
     document.forms[0].save.disabled = false;     
}

function validateSampleSource() {

   if ($F("sourceOfSampleDesc") != "") {
     new Ajax.Request (
       'ajaxXML',  //url
       {//options
        method: 'get', //http method
        parameters: 'provider=QuickEntrySampleSourceValidationProvider&field=sourceOfSampleDesc&id=' + escape($F("sourceOfSampleDesc")),      //request parameters
        //indicator: 'throbbing'
        onSuccess:  processSuccess,
        onFailure:  processFailure
       }
     );
   } else {
	$("sourceOfSampleId").value = "";
   }	
}


function invalidZip() {
    mdiv = $("zipCodeMessage");
    mdiv.className = "badmessage";
    document.forms[0].save.disabled = true;
    //fixed bug while working on bugzilla 1899
    setFieldInvalid("zipCode");
}

//bugzilla 1899
function validateZipCode() {
 //alert("I am in validateZipCode");
 var idField = $("zipCode");
 
 //check if we even need to use Ajax validation:
 var zip = $F("zipCode");
 var digitCheck = "0123456789";
 if (zip != '' && zip.length != 5 && zip.length != 9 && zip.length != 10) {
   invalidZip();
   return;
 }
 
 if (zip.length == 10) {
    if (zip.indexOf('-') != 5) {
      invalidZip();
      return;
    }
 }
 
 
 var IsNumber=true;
 var Char;

 if (zip.length ==5 || zip.length ==9) {
 for (i = 0; i < zip.length && IsNumber == true; i++) 
 { 
    Char = zip.charAt(i); 
    if (digitCheck.indexOf(Char) == -1) 
    {
       IsNumber = false;
    }
  }
 } else {
  for (i = 0; i < zip.length && IsNumber == true; i++) 
 { 
    Char = zip.charAt(i); 
    if (digitCheck.indexOf(Char) == -1 && i != 5) 
    {
       IsNumber = false;
    }
  }
 }
 if (!isNumber) {
    invalidZip();
    return;
 }
 //bugzilla 1765 zip validator no longer validates combo city/zip
 new Ajax.Request (
           'ajaxXML',  //url
             {//options
              method: 'get', //http method
              parameters: 'provider=ZipValidationProvider&field=zipCode&id=' + escape($F("zipCode")),      //request parameters
              //indicator: 'throbbing'
              onSuccess:  processSuccess,
              onFailure:  processFailure
             }
          );
 
 if (zip.length == 9) {
   var newZip = '';
   //insert dash
    for (i = 0; i < zip.length; i++) 
   { 
    newZip += zip.charAt(i); 
    if (i == 4) { 
       newZip += '-';
    }
   }
   
   document.forms[0].zipCode.value = newZip;
 }
   
}



function myCheckTime(time) {
    var fieldMessage = time.name + 'Message';
    var mdiv = $(fieldMessage);
    if (checkTime(time)) {
       mdiv.className = "blank";
       setFieldValid(time.name);
    } else {
       mdiv.className = "badmessage";
       setFieldInvalid(time.name);
    }
   setSave();
}

function myCheckExtension(field) {
  var messageDiv = field.name + 'Message';
  var mdiv = $(messageDiv);
  var ext = field.value;
  var strCheck = '1234567890';
  if (ext != '') {
    if (ext.length > 4 ||
        strCheck.indexOf(ext.substring(0,1)) < 0 ||
        strCheck.indexOf(ext.substring(1,2)) < 0 ||
        strCheck.indexOf(ext.substring(2,3)) < 0 ||
        strCheck.indexOf(ext.substring(3,4)) < 0 
       )
    {
      mdiv.className = "badmessage";
      setFieldInvalid(field.name);
      
    } else { 
      mdiv.className = "blank";
      setFieldValid(field.name);
    }
  } else {   
      mdiv.className = "blank";
      setFieldValid(field.name);
  }
   setSave();
}

function myCheckPhone(field) {
  var messageDiv = field.name + 'Message';
  var mdiv = $(messageDiv);
  var phone = field.value;
  var strCheck = '1234567890';
  if (phone != '') {
  if (phone.length != 13 ||
      phone.substring(0,1) != '(' ||
      phone.substring(4,5) != ')' ||
      phone.substring(8,9) != '-' ||
      strCheck.indexOf(phone.substring(1,2)) < 0 ||
      strCheck.indexOf(phone.substring(2,3)) < 0 ||
      strCheck.indexOf(phone.substring(3,4)) < 0 ||
      strCheck.indexOf(phone.substring(5,6)) < 0 ||
      strCheck.indexOf(phone.substring(6,7)) < 0 ||
      strCheck.indexOf(phone.substring(7,8)) < 0 ||
      strCheck.indexOf(phone.substring(9,10)) < 0 ||
      strCheck.indexOf(phone.substring(10,11)) < 0 ||
      strCheck.indexOf(phone.substring(11,12)) < 0 ||
      strCheck.indexOf(phone.substring(12,13)) < 0
      )
  {
      
      mdiv.className = "badmessage";
      setFieldInvalid(field.name);
  } else {
      mdiv.className = "blank";
      setFieldValid(field.name);
  }  
  } else {
      mdiv.className = "blank";
      setFieldValid(field.name);
  }
   setSave();
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


function setMyCancelAction(form, action, validate, parameters) {
   
 //first turn off any further validation
 setAction(window.document.forms[0], 'Cancel', 'no', '');
	
}

//added this for 1899 to get loading message from MessageResource.properties
function getLbLoadMessage() {
  return '<%=lbLoadMessage%>';
}

function submitTheForm(form) {
   //setAction(form, 'Update', 'yes', '?ID=');
   setAction(form, 'UpdateSampleDemographicsAnd', 'yes', '?ID=');	
}

//bugzilla 1899
function checkConfirmExit(replaceLightBox)
{
   	var collectionDate = $("collectionDateForDisplay");
   	collectionDate = $F("collectionDateForDisplay");
   	//alert("In checkConfirmExit()... collectionDate value is: " + collectionDate);
   
   	//if tests have been selected go ahead and save without confirm exit window
   	if ( collectionDate != '' && collectionDate !=null && collectionDate != undefined) {
   		//alert("Collection Date must not be null");   		
      	submitTheForm(window.document.forms[0]);
       
   	}
   	
   	if(collectionDate == '' || collectionDate == null || collectionDate == undefined) {
	 
 	 //define customLightBox
 	    var customLightBox = { 
          
	      loadInfo : function() { 
	        var myAjax = new Ajax.Request(
		    //bugzilla 1895 changed this from this.content (along with replacing initialize function)
	        this.url,
	        {method: 'post', parameters: '', onComplete: this.processInfo.bindAsEventListener(this)}
	        );
	      },
       
        // Display Ajax response
        processInfo: function(response){
            new Insertion.Before($('lbLoadMessage'), response.responseText);
 	    	$('lightbox').className = "done";	
	    	this.actions();			
	    },
	   
	   insert: function(e){
	     Element.remove($('lbContent'));
	     var myAjax = new Ajax.Request(
		 'pages/sample/humanSampleConfirmExitLightbox.jsp',
		 {method: 'post', parameters: '', onComplete: this.processInfo.bindAsEventListener(this)}
	     );
        }
 
     };
     
    //replaceLightBox is true if we are coming from a previous popup (lightbox)
    if (replaceLightBox) {
     //reuse existing lBox
     var myLightBox = Object.extend(lBox, customLightBox);
     myLightBox.insert();
    } else {
     lBox = new lightbox('pages/sample/humanSampleConfirmExitLightbox.jsp', '');    
     var myLightBox = Object.extend(lBox, customLightBox);
     myLightBox.activate();
    }
   
   } else {
     return false;
   }
    
}

//bugzilla 1899
function popupCityStateZipSelection(form) {
    

     var url = 'HumanSampleCityStateZipPopup.do';
     var href = "";
      
     href += '?city=' + $F("city");
     href += '&state=' + $F("state");
     href += '&zipCode=' + $F("zipCode");
     
     //alert("href "+ href);
     
     //bugzilla 1895 extend lightbox loadInfo function to do what we need for this page
     var customLightBox = { 

         loadInfo : function() { 
            var myAjax = new Ajax.Request(
		    //bugzilla 1895 changed this from this.content (along with replacing initialize function)
            this.url,
            {method: 'get', parameters: this.parameters, onComplete: this.processInfo.bindAsEventListener(this)}
	        );
          }
 
      };
       

     lBox = new lightbox(url, href);
     var myLightBox = Object.extend(lBox, customLightBox);
     myLightBox.className = 'lightbox';
     
     myLightBox.activate(); 
}

function processAjaxResultForCityStateZipCombo(xhr) {
  var message = xhr.responseXML.getElementsByTagName("message")[0];
  var formfield = xhr.responseXML.getElementsByTagName("formfield")[0];
  //set hidden variable validCityStateZipCombo
  var msg =  message.childNodes[0].nodeValue;
  var fld =  formfield.childNodes[0].nodeValue;
  //we can submit form now if the combination is valid
  if (msg == "valid") {
     checkConfirmExit(false);
  } else {
     popupCityStateZipSelection(window.document.forms[0]);
  }
}

function checkValidCityZipCodeCombination() {
 var idField = $("city");
 var fieldName = idField.name;
 var zip = '';
 if (isFieldValid(fieldName) && ($("zipCode") != '')) {
   zip = $F("zipCode");
 }
 
 var state = '';
 if (isFieldValid(fieldName) && ($("state") != '')) {
   state = $F("state");
 }
 new Ajax.Request (
           'ajaxXML',  //url
             {//options
              method: 'get', //http method
              parameters: 'provider=CityStateZipComboValidationProvider&field=city&city=' + escape($F("city")) + "&zipCode=" + zip + "&state=" + state,      //request parameters
              //indicator: 'throbbing'
              onSuccess:  processAjaxResultForCityStateZipCombo,
              onFailure:  processFailure
             }
          );

}

//bugzilla 2079
function customOnLoad()
{   
	focusOnFirstInputField();    
}
</script>

<!-- Ais | For validating the date fields-->
<html:hidden property="currentDate" name="<%=formName%>" styleId="currentDate"/>

<html:hidden property="domain" name="<%=formName%>" value="<%=humanDomain%>" styleId="domain"/>

<!-- for optimistic locking-->
<html:hidden property="personLastupdated" name="<%=formName%>" styleId="personLastupdated"/>
<html:hidden property="patientLastupdated" name="<%=formName%>" styleId="patientLastupdated"/>
<html:hidden property="providerLastupdated" name="<%=formName%>" styleId="providerLastupdated"/>
<html:hidden property="providerPersonLastupdated" name="<%=formName%>" styleId="providerPersonLastupdated"/>
<html:hidden property="sampleHumanLastupdated" name="<%=formName%>" styleId="sampleHumanLastupdated" />
<html:hidden property="sampleOrganizationLastupdated" name="<%=formName%>" styleId="sampleOrganizationLastupdated"/>
<html:hidden property="sampleItemLastupdated" name="<%=formName%>" styleId="sampleItemLastupdated"/>




<table align="center">
	<tr>
		<td colspan="4" align="center">
			<em>
				<h2>
					<bean:message key="testmanagement.popup.subtitle" />
				</h2> </em>
				<%--bugzilla 2101 --%>
				<h4>
				    <bean:message key="qaeventsentry.accessionNumber" />:
				    <bean:write property="accessionNumber" name="<%=formName%>"/>
				</h4>
		</td>
	</tr>
</table>

<table width="100%">
	<tr><%--bugzilla 2438 increase size of projet.name--%>
		<td colspan="2" valign="top">
			<table width="95%">
				<tr>
					<td width="163">
						<bean:message key="humansampleone.projectNumber" />
						:
					</td>
					<td colspan="3"><%--bugzilla 2438 increase size of name--%>
						<app:text name="<%=formName%>" property="projectIdOrName" onblur="validateProjectIdOrName();" size="50" maxlength="50" styleClass="text" styleId="projectIdOrName"/>
						<div id="projectIdOrNameMessage" class="blank">
							&nbsp;
						</div>
						<div id="projectNameOrIdDisplay" class="blank">
							&nbsp;
						</div>
						<html:hidden property="projectNameOrId" name="<%=formName%>" styleId="projectNameOrId"/>
					</td>
				</tr>
				<tr>
					<td width="163">
						<bean:message key="humansampleone.project2Number" />
						:
					</td>
					<td colspan="3"><%--bugzilla 2438 increase size of name--%>
						<app:text name="<%=formName%>" property="project2IdOrName" onblur="validateProject2IdOrName();" size="50" maxlength="50" styleClass="text" styleId="project2IdOrName"/>
						<div id="project2IdOrNameMessage" class="blank">
							&nbsp;
						</div>
						<div id="project2NameOrIdDisplay" class="blank">
							&nbsp;
						</div>
						<html:hidden property="project2NameOrId" name="<%=formName%>" styleId="project2NameOrId"/>
					</td>
				</tr>
			</table>

		</td>
	</tr>
	<tr>
		<td width="50%" valign="top">
			<table width="95%">
				<tr>
					<td colspan="4">
						<h2 align="left">
							<bean:message key="humansampleone.subtitle.requestor" />
						</h2>
					</td>
				</tr>
				<%--Submitter Number--%>
				<tr>
					<td width="163">
                       <!--bugzilla 2589 no longer required-->
						<bean:message key="humansampleone.provider.organization.localAbbreviation" />:
					</td>
					<td colspan="3">
						<app:text name="<%=formName%>" property="organizationLocalAbbreviation" onblur="validateOrganizationLocalAbbreviation();" size="15" maxlength="10" styleClass="text"  styleId="organizationLocalAbbreviation"/>
						<div id="organizationLocalAbbreviationMessage" class="blank">
							&nbsp;
						</div>
						<div id="organizationName" class="blank">
							&nbsp;
						</div>
					</td>

				</tr>
				<%--Provider Person information--%>
				<tr>
					<td width="163">
					    <%--bugzilla 2495--%>
						<bean:message key="humansampleone.provider.lastName" />
						: <font size="1"><bean:message key="humansampleone.provider.addionalOrClinician" /></font>
					</td>
					<td colspan="3">
						<app:text name="<%=formName%>" property="providerLastName" size="35" maxlength="30" styleId="providerLastName" onblur="this.value=this.value.toUpperCase();"/>
					</td>
				</tr>
				<tr>
					<td width="163">
						<bean:message key="humansampleone.provider.firstName" />
						: <font size="1"><bean:message key="humansampleone.provider.addionalOrClinician" /></font>
					</td>
					<td colspan="3">
					    <%--bugzilla 2495--%>
						<app:text name="<%=formName%>" property="providerFirstName" size="35" maxlength="20" styleId="providerFirstName" onblur="this.value=this.value.toUpperCase();"/>
					</td>
				</tr>
				<tr>
					<td width="163">
						<bean:message key="humansampleone.provider.workPhone" />
						: <font size="1"> <bean:message key="humansampleone.phone.additionalFormat" /></font>
					</td>
					<td width="173">
						<app:text name="<%=formName%>" property="providerWorkPhone" onblur="myCheckPhone(this)" onkeyup="javascript:getIt(this,event)" size="15" maxlength="17" styleClass="text" styleId="providerWorkPhone"/>
						<div id="providerWorkPhoneMessage" class="blank">
							&nbsp;
						</div>
					</td>
					<td width="32">
						<bean:message key="humansampleone.provider.workPhone.extension" />:
					</td>
					<td width="129">
						<app:text name="<%=formName%>" property="providerWorkPhoneExtension" onblur="myCheckExtension(this)" size="4" maxlength="5" styleClass="text" styleId="providerWorkPhoneExtension"/>
						<div id="providerWorkPhoneExtensionMessage" class="blank">
							&nbsp;
						</div>
					</td>
				</tr>
				<tr>
					<td width="163">
						&nbsp;
					</td>
					<td width="173">
						&nbsp;
					</td>
					<td width="32">
						&nbsp;
					</td>
					<td width="129">
						&nbsp;
					</td>
				</tr>
				<tr>
					<td width="163">
						&nbsp;
					</td>
					<td width="173">
						&nbsp;
					</td>
					<td width="32">
						&nbsp;
					</td>
					<td width="129">
						&nbsp;
					</td>
				</tr>
				<tr>
					<td width="163">
						&nbsp;
					</td>
					<td width="173">
						&nbsp;
					</td>
					<td width="32">
						&nbsp;
					</td>
					<td width="129">
						&nbsp;
					</td>
				</tr>
				<tr>
					<td width="163">
						&nbsp;
					</td>
					<td width="173">
						&nbsp;
					</td>
					<td width="32">
						&nbsp;
					</td>
					<td width="129">
						&nbsp;
					</td>
				</tr>
			</table>
		</td>
		<td width="50%" valign="top">
			<table width="100%">
				<tr>
					<td colspan="5">
						<h2>
							<bean:message key="humansampleone.subtitle.patient" />
						</h2>
					</td>
				</tr>
                <%--bugzilla 1904 moved external id to top--%>
				<tr>
					<td width="116">
						<bean:message key="patient.externalId" />
						<%--bugzilla 2495--%>
						:<span class="requiredlabel"></span>
					</td>
					<td colspan="4">
					    <%--bugzilla 2276--%>
					    <%--bugzilla 2495--%>
						<app:text name="<%=formName%>" property="externalId" size="35" maxlength="20" styleId="externalId" onblur="this.value=this.value.toUpperCase();"/>
					</td>
				</tr>
				<tr>
					<td width="116">
						<bean:message key="person.lastName" />
						<%--bugzilla 2495--%>
						:<span class="requiredlabel"></span>
					</td>
					<td colspan="4">
					    <%--bugzilla 2495--%>
						<app:text name="<%=formName%>" property="lastName" size="49" maxlength="30" styleId="lastName" onblur="this.value=this.value.toUpperCase();"/>
					</td>
				</tr>
				<tr>
					<td width="116">
						<bean:message key="person.firstName" />
						<%--bugzilla 2495--%>
						:<span class="requiredlabel"></span>
					</td>
					<td colspan="2">
					    <%--bugzilla 2495--%>
						<app:text name="<%=formName%>" property="firstName" size="35" maxlength="20" styleId="firstName" onblur="this.value=this.value.toUpperCase();"/>
					</td>
					<td width="31">
						<bean:message key="person.middleName" />
						:
					</td>
					<td width="132">
					    <%--bugzilla 2495--%>
						<app:text name="<%=formName%>" property="middleName" size="4" maxlength="20" styleId="middleName" onblur="this.value=this.value.toUpperCase();"/>
					</td>
				</tr>
				<tr>
					<td width="116">
						<bean:message key="person.streetAddress" />
						:
					</td>
					<td colspan="2">
					    <%--bugzilla 2495--%>
						<app:text name="<%=formName%>" property="streetAddress" size="35" maxlength="30" styleId="streetAddress" onblur="this.value=this.value.toUpperCase();"/>
					</td>
					<td width="31">
						<bean:message key="person.multipleUnit" />
						:
					</td>
					<td width="132">
					    <%--bugzilla 2495--%>
						<app:text name="<%=formName%>" property="multipleUnit" size="4" maxlength="30" styleId="multipleUnit" onblur="this.value=this.value.toUpperCase();"/>
					</td>
				</tr>
				<tr>
					<td width="116">
						<bean:message key="person.city" />
						:
					</td>
					<td colspan="4">
						<app:text name="<%=formName%>" property="city" onblur="this.value=this.value.toUpperCase();validateCity();" size="49" maxlength="30" styleClass="text" styleId="city"/>
						<div id="cityMessage" class="blank">
							&nbsp;
						</div>
						<html:hidden property="cityId" name="<%=formName%>" styleId="cityId"/>
					</td>
				</tr>
				<tr>
					<td width="116">
						<bean:message key="person.state" />
						:
					</td>
					<td width="276">
						<app:text name="<%=formName%>" property="state" onblur="this.value=this.value.toUpperCase();validateState();" size="5" maxlength="2" styleClass="text" styleId="state"/>
						<div id="stateMessage" class="blank">
							&nbsp;
						</div>
					</td>
					<td width="29">
						&nbsp;
					</td>
					<td width="31">
						<bean:message key="person.zipCode" />
						:
					</td>
					<td width="132">
						<app:text name="<%=formName%>" property="zipCode" onblur="validateZipCode()" size="13" maxlength="10" styleClass="text" styleId="zipCode"/>
						<div id="zipCodeMessage" class="blank">
							&nbsp;
						</div>
					</td>
				</tr>
                <%--bugzilla 1904 added new field chart number--%>
				<tr>
					<td width="116">
						<bean:message key="patient.chartNumber" />:
					</td>
					<td colspan="2">
					    <%--bugzilla 2495--%>
						<app:text name="<%=formName%>" property="chartNumber" size="35" maxlength="20" styleId="chartNumber" onblur="this.value=this.value.toUpperCase();"/>
					</td>
					<td width="31">
						<bean:message key="patient.gender" />
						:
					</td>
					<td width="132">
						<app:text name="<%=formName%>" property="gender" onblur="this.value=this.value.toUpperCase()" size="4" maxlength="1" styleId="gender" />
					</td>
				</tr>
				<tr>
					<td width="116">
						<bean:message key="patient.birthDate" />
						: <font size="1"><bean:message key="humansampleone.date.additionalFormat" /></font>
					</td>
					<td colspan="4">
						<app:text name="<%=formName%>" property="birthDateForDisplay" size="20" maxlength="10" onkeyup="myCheckDate(this, event, false,false)" onblur="myCheckDate(this, event, true, true)" styleClass="text" styleId="birthDateForDisplay"/>
						<div id="birthDateForDisplayMessage" class="blank">
							&nbsp;
						</div>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<%--bottom sample information--%>
<table width="100%">
	<tr>
		<td colspan="8">
			<h2>
				<bean:message key="humansampleone.subtitle.sample" />
			</h2>
		</td>
	</tr>
	<tr>
		<td width="121">
			<bean:message key="sample.collectionDate" />
			: <font size="1"><bean:message key="humansampleone.date.additionalFormat" /></font>
		</td>
		<td width="212">
			<app:text name="<%=formName%>" property="collectionDateForDisplay" size="20" maxlength="10" onkeyup="myCheckDate(this, event, false,false)" onblur="myCheckDate(this, event, true, true)" styleClass="text" styleId="collectionDateForDisplay"/>
			<div id="collectionDateForDisplayMessage" class="blank">
				&nbsp;
			</div>
		</td>
		<td width="109">
			<bean:message key="sample.collectionTime" />
			: <font size="1"><bean:message key="humansampleone.time.additionalFormat" /></font>
		</td>
		<td width="215">
			<app:text name="<%=formName%>" property="collectionTimeForDisplay" size="13" maxlength="15" onblur="myCheckTime(this);" styleClass="text" styleId="collectionTimeForDisplay"/>
			<div id="collectionTimeForDisplayMessage" class="blank">
				&nbsp;
			</div>
		</td>
		<td width="150">
			<bean:message key="sample.clientReference" />
			:
		</td>
		<td width="81">
		    <%--bugzilla 2495--%>
			<app:text name="<%=formName%>" property="clientReference" size="20" maxlength="20" styleId="clientReference" onblur="this.value=this.value.toUpperCase();"/>
		</td>
		<td width="100">
			<bean:message key="sample.referredCultureFlag" />
			:<span class="requiredlabel">*</span>
		</td>
		<td width="76">
		    <%--bugzilla 2495--%>
		    <%--bugzilla 2543 remove one onblur for OC4J error--%>
			<app:text name="<%=formName%>" property="referredCultureFlag" size="4" maxlength="1" onblur="this.value=this.value.toUpperCase()" styleId="referredCultureFlag"/>
		</td>
	</tr>
	<tr>
		<td width="121">
			<bean:message key="sampleitem.typeOfSample" />
			:<span class="requiredlabel">*</span>
		</td>
		<td width="212">
			<app:text name="<%=formName%>" property="typeOfSampleDesc" onblur="validateSampleType();" size="25" maxlength="30" styleClass="text" styleId="typeOfSampleDesc"/>
			<div id="typeOfSampleDescMessage" class="blank">
				&nbsp;
			</div>
			<html:hidden property="typeOfSampleId" name="<%=formName%>" styleId="typeOfSampleId"/>
		</td>
		<td width="109">
			<bean:message key="sampleitem.sourceOfSample" />
			:
		</td>
		<td width="215">
		    <%--bugzilla 2495--%>
			<app:text name="<%=formName%>" property="sourceOfSampleDesc" onblur="this.value=this.value.toUpperCase();validateSampleSource();" size="25" maxlength="30" styleClass="text" styleId="sourceOfSampleDesc"/>
			<div id="sourceOfSampleDescMessage" class="blank">
				&nbsp;
			</div>
			<html:hidden property="sourceOfSampleId" name="<%=formName%>" styleId="sourceOfSampleId"/>
		</td>
		<td width="52">
			<bean:message key="sampleitem.sourceOther" />
			:
		</td>
		<td colspan="3">
		    <%--bugzilla 2495--%>
			<app:text name="<%=formName%>" property="sourceOther" size="25" maxlength="40" styleId="sourceOther" onblur="this.value=this.value.toUpperCase();"/>
		</td>
	</tr>
	<tr>
		<td width="121">
			<bean:message key="sample.receivedDate" />
			:<span class="requiredlabel">*</span> <font size="1"><bean:message key="humansampleone.date.additionalFormat" /></font>
		</td>
		<td width="212">
			<app:text name="<%=formName%>" property="receivedDateForDisplay" onkeyup="myCheckDate(this, event, false,false)" onblur="myCheckDate(this, event, true, true)" size="20" maxlength="10" styleClass="text" styleId="receivedDateForDisplay"/>
			<div id="receivedDateForDisplayMessage" class="blank">
				&nbsp;
			</div>
		</td>
		<td width="109">
			<bean:message key="sample.stickerReceivedFlag" />
			:<span class="requiredlabel">*</span>
		</td>
		<td width="215">
			<app:text name="<%=formName%>" property="stickerReceivedFlag" size="4" maxlength="1" onblur="this.value=this.value.toUpperCase()" styleId="stickerReceivedFlag"/>
		</td>
		<td width="52">
			&nbsp;
		</td>
		<td width="179">
			&nbsp;
		</td>
		<td width="100">
			&nbsp;
		</td>
		<td width="76">
			&nbsp;
		</td>
	</tr>
	<tr>
		<td width="121">
			&nbsp;
		</td>
		<td width="212">
			&nbsp;
		</td>
		<td width="109">
			&nbsp;
		</td>
		<td width="215">
			&nbsp;
		</td>
		<td width="52">
			&nbsp;
		</td>
		<td width="179">
			&nbsp;
		</td>
		<td width="100">
			&nbsp;
		</td>
		<td width="76">
			&nbsp;
		</td>
	</tr>
</table>


  <ajax:autocomplete
  source="projectIdOrName"
  target="projectNameOrId"
  baseUrl="ajaxAutocompleteXML"
  className="autocomplete"
  parameters="projectName={projectIdOrName},provider=BasicProjectAutocompleteProvider,fieldName=projectName,idName=id"
  minimumCharacters="1" 
  postFunction="projectFocus"
  />
  
  <ajax:autocomplete
  source="project2IdOrName"
  target="project2NameOrId"
  baseUrl="ajaxAutocompleteXML"
  className="autocomplete"
  parameters="projectName={project2IdOrName},provider=BasicProjectAutocompleteProvider,fieldName=projectName,idName=id"
  minimumCharacters="1" 
  postFunction="project2Focus"
  />

  <ajax:autocomplete
  source="city"
  target="cityId"
  baseUrl="ajaxAutocompleteXML"
  className="autocomplete"
  parameters="city={city},provider=CityAutocompleteProvider,fieldName=city,idName=id"
  minimumCharacters="1"
  postFunction="cityFocus"
   />
   
  <ajax:autocomplete
  source="sourceOfSampleDesc"
  target="sourceOfSampleId"
  baseUrl="ajaxAutocompleteXML"
  className="autocomplete"
  parameters="sourceOfSampleDesc={sourceOfSampleDesc},domain={domain},provider=SampleSourceAutocompleteProvider,fieldName=description,idName=id"
  minimumCharacters="1"
  postFunction="sampleSourceFocus"
   />
   
  <ajax:autocomplete
  source="typeOfSampleDesc"
  target="typeOfSampleId"
  baseUrl="ajaxAutocompleteXML"
  className="autocomplete"
  parameters="typeOfSampleDesc={typeOfSampleDesc},domain={domain},provider=SampleTypeAutocompleteProvider,fieldName=description,idName=id"
  minimumCharacters="1"
  postFunction="sampleTypeFocus"
   />


<html:javascript formName="testManagementForm" staticJavascript="true" />

