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
<bean:define id="idSeparator" value='<%= SystemConfiguration.getInstance().getDefaultIdSeparator() %>' />
<%--bugzilla 1387--%>
<bean:define id="humanDomain" value='<%= SystemConfiguration.getInstance().getHumanDomain() %>' />
<%--bugzilla 1765 changes to SampleStatusValidationProvider to make more generic--%>
<bean:define id="expectedStatus" value='<%= SystemConfiguration.getInstance().getSampleStatusQuickEntryComplete() %>' />
<bean:define id="enablePdfLink" value='<%= SystemConfiguration.getInstance().getEnabledSamplePdf() %>' />

<%--bugzilla 1510 add styleId for compatibility in firefox and for use of firebug debugger--%>
<%--bugzilla 2069 using organizationLocalAbbreviation--%>
<%--bugzilla 1904 did some reformatting to fit new row on screen: Patient ID--%>
<%--bugzilla 1844 removed add test code - no longer needed here--%>
<%--bugzilla 2451: remove asterisks to indicate required for last name, first name, patient id--%>
<%!

String errorMessagePatientNameOrId = "";
String errorSelectCombo = "";
String lbLoadMessage = "";

%>

<%
// bugzilla 2151
String saveDisabled = (String)request.getAttribute(IActionConstants.SAVE_DISABLED);

//bugzilla 1494
java.util.Locale locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
errorMessagePatientNameOrId =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "humansampleone.validation.patient.nameorid");

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

var lBox;
var formFieldArray = new Array('birthDateForDisplay',
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
                                    //AIS - bugzilla 1408                                    
                                    //'sourceOfSampleDesc',
                                    //'sourceOther',
                                    'state', 
                                    'stickerReceivedFlag',
                                    'streetAddress',
                                    //bugzilla 1894
                                    'typeOfSampleDesc',
                                    'zipCode'   
                                    );
var formFieldsValidArray = new Array(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true,
									 //AIS - bugzilla 1408
									 //true, true,
									 true, true, true, 
									 //bugzilla 1894
									 true, 
									 true);
//bugzilla 1894 the array was missing 2 elements
//bugzilla 2589 organization (submitter) is not required (unknown is now null)
var formFieldsRequiredArray = new Array(false, false, false, true, false, false, false, true, false, false, false, false, false, false, false, false, true, true, true, true,
								     //AIS - bugzilla 1408
									 //false, false, 
									 false, true, false, 
									 //bugzilla 1894
									 true, 
									 false);


function pageOnLoad() {
	//AIS - bugzilla 1408
    //var projectIdOrName = $("projectIdOrName");
    //projectIdOrName.focus();
    if ( '<%=enablePdfLink%>' == '<%=IActionConstants.YES%>' ) {
   	    var selectedAccNbr = $("selectedAccessionNumberOne");
   	    //bugzilla 2131
   	    if ( selectedAccNbr != null ) {
   	      selectedAccNbr.focus();
   	      selectAccessionNumber(selectedAccNbr);
	    }    
    } else {
       	var accnNumb = $("accessionNumber");
       	accnNumb.focus();  
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
	<%--bugzilla 2151 --%>
	<%
	    if ( Boolean.valueOf(saveDisabled).booleanValue() ) { %>
	        obj.disabled = true;
    <% } %>	  
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

function submitTheForm(form) {
   setAction(form, 'Update', 'yes', '?ID=');
}


<%--AIS - bugzilla 1408 - Added --%>
function setMySaveAction(form, action, validate, parameters) {	 
	 setAction(window.document.forms[0], 'Update', 'yes', '?ID=');		
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

//function sampleSourceFocus() { 
  //var field = 'sourceOfSampleDesc';
  //nextField = 'sourceOther';
  //doFocus(field, nextField);
//}

//bugzilla 1894
function sampleTypeFocus() {
  var field = 'typeOfSampleDesc';
  nextField = 'stickerReceivedFlag';
  doFocus(field, nextField);
}

function validateForm(form) {
    var validated = validateHumanSampleOneForm(form);
    if (validated) {
       var firstName = form.firstName.value;
       var lastName = form.lastName.value;
       var externalId = form.externalId.value;
       if ((externalId == '' && lastName == '') || (externalId == '' && firstName == '')) {
         //bugzilla 1494
         alert('<%=errorMessagePatientNameOrId%>');
          //clearSaveClicked();
          validated = false;
       }
    } else {
      // bugzilla #1376
      //clearSaveClicked();
    }
    return validated;
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

//This is for validating individual fields: submitter#, city, state, sample type, sample source etc
function setMessage(message, field) {
     //alert("I am in in setMessage - reg. validation - with message and field " + message + " " + field);
     var fieldMessage = field + "Message";
     var mdiv = $(fieldMessage);
     var idField = $(field);
     
     if (message == "invalid") {
       //some fields are not required
       //AIS - bugzilla 1408
	   if ($F(field) == "" && !isFieldRequired(field) && (idField.name != 'accessionNumber')){
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
       //AIS - bugzilla 1408 - added extra if condition
       //bugzilla 1765 changes to SampleStatusValidationProvider
       if (message != "validStatus") {       
	       if (field != 'accessionNumber') {        
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
		             var projectNameOrId = $("projectNameOrId");
		             projectNameOrId.value = message.substring(5);
		         } else if (idField.name == "project2IdOrName") {
		             //FOR PROJECT2 ID the text behind VALID (returned from ajax validation) is the Project Name and we need to display that
		             var project2NameOrIdDisplay = $("project2NameOrIdDisplay");
		             project2NameOrIdDisplay.innerHTML = message.substring(5);
		             var project2NameOrId = $("project2NameOrId");
		             project2NameOrId.value = message.substring(5);
		         } else {
                      //bugzilla 1894
                     if (idField.name == 'sourceOfSampleDesc') {
                       idField = $("sourceOfSampleId");
                      } else if (idField.name == 'typeOfSampleDesc') {
                       idField = $("typeOfSampleId");
                      } 
		             idField.value = message.substring(5);
		         } 
		      } else {
		      //clear out project displayed
		         if (idField.name == "projectIdOrName") {
		             var projectNameOrIdDisplay = $("projectNameOrIdDisplay");
		             projectNameOrIdDisplay.innerHTML = "";
		             var projectNameOrId = $("projectNameOrId");
		             projectNameOrId.value = "";
		         } else if (idField.name == "project2IdOrName") {
		             var project2NameOrIdDisplay = $("project2NameOrIdDisplay");
		             project2NameOrIdDisplay.innerHTML = "";
		             var project2NameOrId = $("project2NameOrId");
		             project2NameOrId.value = "";
		         }
		      }
	      }else{     
	        //To check the status of the sample
            //bugzilla 1765 changes to SampleStatusValidationProvider
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

//AIS - bugzilla 1408 -Start
//BGM - bugzilla 1495 added form=humanSampleOne to be passed in to validate status code
function validateAccessionNumber(field) {
	//bgm bugzilla 1624
	numbersOnlyCheck(field);

	if (field.value != ""){
	    new Ajax.Request (
                    'ajaxXML',  //url
                    {//options
                     method: 'get', //http method
                     parameters: 'provider=AccessionNumberValidationProvider&form=' + document.forms[0].name + '&field=accessionNumber&id=' + escape(field.value),      //request parameters
                     //indicator: 'throbbing'
                     onSuccess:  processSuccess,
                     onFailure:  processFailure
                    }
              ); 
	 } else{		    
	    var myMessage = "invalid";
        //bugzilla 1510 error appears for AccessionNumber (no id found) in firefox
		var myField = "accessionNumber";		
		setMessage(myMessage, myField);		
		setSave();
     } 
}
//AIS - bugzilla 1408 -End

function validateProjectIdOrName() {
 //alert("I am in validateProjectIdOrName");
 var idField = $("projectIdOrName");
 new Ajax.Request (
           'ajaxXML',  //url
             {//options
              method: 'get', //http method
              parameters: 'provider=ProjectIdOrNameValidationProvider&field=projectIdOrName&id=' + escape($F("projectIdOrName")),      //request parameters
              //indicator: 'throbbing'
              onSuccess:  processSuccess,
              onFailure:  processFailure
             }
          );
}

function validateProject2IdOrName() {
 //alert("I am in validateProject2IdOrName");
 new Ajax.Request (
           'ajaxXML',  //url
             {//options
              method: 'get', //http method
              parameters: 'provider=ProjectIdOrNameValidationProvider&field=project2IdOrName&id=' + escape($F("project2IdOrName")),      //request parameters
              //indicator: 'throbbing'
              onSuccess:  processSuccess,
              onFailure:  processFailure
             }
          );
}

//bgm bugzilla 1624 digit check
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
	    //bugzilla 2589 no longer required
		mdiv.className = "blank";
   		setFieldValid(field.name);
   		document.forms[0].save.disabled = false;   		
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
	   		document.forms[0].save.disabled = true;   		
		}else{
			mdiv.className = "blank";
	   		setFieldValid(field.name);
	   		document.forms[0].save.disabled = false;
		}
	}
	
	setSave();
}

function validateOrganizationLocalAbbreviation() {
//alert("I am in validateOrganizationLocalAbbreviation");
 var idField = $("organizationLocalAbbreviation");
 
 //bgm bugzilla 1624 call numbersOnlyCheck()
 numbersOnlyCheck(idField);   
 
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

//bugzilla 1894
function validateSampleType() {
 //alert("I am in validateSampleType");
 var field = "typeOfSampleDesc";
 if ($F(field) != "") {
   new Ajax.Request (
           'ajaxXML',  //url
             {//options
              method: 'get', //http method
              parameters: 'provider=HumanSampleTypeValidationProvider&field=typeOfSampleDesc&id=' + escape($F("typeOfSampleDesc")),      //request parameters
              //indicator: 'throbbing'
              onSuccess:  processSuccess,
              onFailure:  processFailure
             }
          );
  } else {
     var myMessage = "invalid";
 	 setMessage(myMessage, field);		
	 setSave();
  }
}

function validateSampleSource() {
 //alert("I am in validateSampleSource");
 new Ajax.Request (
           'ajaxXML',  //url
             {//options
              method: 'get', //http method
              parameters: 'provider=HumanSampleSourceValidationProvider&field=sourceOfSampleDesc&id=' + escape($F("sourceOfSampleDesc")),      //request parameters
              //indicator: 'throbbing'
              onSuccess:  processSuccess,
              onFailure:  processFailure
             }
          );
}

function invalidZip() {
    mdiv = $("zipCodeMessage");
    mdiv.className = "badmessage";
    document.forms[0].save.disabled = true;
    //fixed bug while working on bugzilla 1895
    setFieldInvalid("zipCode");
}

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

//bgm - bugzilla 1586 need to check for date being null before evaluating.
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

//added this for 1895 to get loading message from MessageResource.properties
function getLbLoadMessage() {
  return '<%=lbLoadMessage%>';
}


//bugzilla 1586 made checkConfirmExit() for blank collection date check
//bugzilla 1895 now using lightbox instead of popups
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

//bugzilla 1895 - coming from city/state/zip popup
function saveItToLightBoxParentForm() {

   var city, state, zip;
   var selCombo;
   //bugzilla 1510 this is to work around FF bug - selectedCombo was not refreshed when multiple tries
   // was getting old value from container rather than lbContent
   //this will get the value from the popup form
   var selectedCombo = null;
   for (var x = 0; x < document.forms.length; x++) { 
     var form = document.forms[x];
     if (form.elements['selectedCombo'] != null)
      selectedCombo = form.elements['selectedCombo'];
   }

   for (var i = 0; i < selectedCombo.length; i++) {
       if (selectedCombo[i].checked) {
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

function saveItToParentForm(form) {
 submitTheForm(form);
}


//bugzilla 1765
//bugzilla 1895 changing from popup to lightbox
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
//end bugzilla 1765

//bugzilla 2131
function loadPDF() {
    var field = document.forms[0].selectedAccessionNumberOne.value;
	var context = '<%= request.getContextPath() %>';
	var server = '<%= request.getServerName() %>';
	var port = '<%= request.getServerPort() %>';
	var scheme = '<%= request.getScheme() %>';	
	var hostStr = scheme + "://" + server;
	if (port != 80 && port != 443)
	{
		hostStr = hostStr + ":" + port;
	}
	hostStr = hostStr + context;
	var href = context+"/ViewPDF.do?accessionNumber="+field;
	createPopup(href, 880, 500);
}

function checkPDF(xhr) {
    var message = xhr.responseXML.getElementsByTagName("message")[0];
    var msg =  message.childNodes[0].nodeValue;
	var accNbr = document.forms[0].selectedAccessionNumberOne.value;
    document.forms[0].accessionNumber.value=accNbr;
    validateAccessionNumber(document.forms[0].accessionNumber);
                        
    if ( accNbr.length > 0 ) {
        if ( msg == "valid" ) {
            document.getElementById("pdfLink").innerHTML = "<a href=\"#\" onclick=\"loadPDF(); return false;\"><bean:message key='human.sample.pdf.link'/></a>";
        } else {
            document.getElementById("pdfLink").innerHTML = "";  
        }    
    }
}
function selectAccessionNumber(field) {
    new Ajax.Request (
        'ajaxXML',  //url
        {//options
        method: 'get', //http method
        parameters: 'provider=FileValidationProvider&form=humanSampleOne&field=accessionNumber&id=' + escape(field.value),      //request parameters
        //indicator: 'throbbing'
        onSuccess:  checkPDF,
        onFailure:  processFailure
        }
    ); 
}  

</script>


<html:hidden property="selectedTestIds" name="<%=formName%>" styleId="selectedTestIds"/>
<html:hidden property="currentDate" name="<%=formName%>" styleId="currentDate"/>
<%--bugzilla 1387 added domain--%>
<html:hidden property="domain" name="<%=formName%>" value="<%=humanDomain%>" styleId="domain"/>

<div id="container">
<table width="100%">
 <tr><%--bugzilla 2438--%>
   <td colspan="2" valign="top">
     <table width="95%">
     <!-- AIS - bugzilla 1408 Start-->     
<%
    if ( enablePdfLink.equals(IActionConstants.YES) ) { %>      
        <tr> 
            <td width="163"> 
                <bean:message key="sampletracking.popup.subtitle"/>
            </td>          
            <td colspan="3"> 
	            <html:select name="<%=formName%>" property="selectedAccessionNumberOne" onchange="selectAccessionNumber(this);">
		            <app:optionsCollection name="<%=formName%>" property="accessionNumberListOne"  label="label" value="value"/>
                </html:select>
            </td>
        </tr>
<%  } %>
      <tr> 
          <td width="163"> 
             <bean:message key="sample.accessionNumber"/>:<span class="requiredlabel">*</span>
          </td>
          <td colspan="3"> 
				  <app:text name="<%=formName%>" property="accessionNumber" styleId="accessionNumber" styleClass="text" onblur="validateAccessionNumber(this);" size="20" maxlength="10" />
	              <div id="accessionNumberMessage" class="blank" >&nbsp;</div>  
            <div id="pdfLink" class="blank">&nbsp;</div>  
          </td>
        </tr>
	 <!-- AIS - bugzilla 1408 End-->             
       <tr>
         <td width="163">
           	<bean:message key="humansampleone.projectNumber"/>:
		  </td>
          <td colspan="3"><%--bugzilla 2438 increase size of project name--%> 
   				<app:text name="<%=formName%>" property="projectIdOrName" styleId="projectIdOrName" onblur="this.value=this.value.toUpperCase();validateProjectIdOrName();" size="50" maxlength="50" styleClass="text" />
		         <div id="projectIdOrNameMessage" class="blank">&nbsp;</div>
		         <div id="projectNameOrIdDisplay" class="blank">&nbsp;</div>
		         <html:hidden property="projectNameOrId" name="<%=formName%>" styleId="projectNameOrId"/>
         </td>
         </tr>
         <tr>
          <td width="163">
           	<bean:message key="humansampleone.project2Number"/>:
		  </td>
          <td colspan="3"><%--bugzilla 2438 increase size of project name--%> 
				<app:text name="<%=formName%>" property="project2IdOrName" styleId="project2IdOrName" onblur="this.value=this.value.toUpperCase();validateProject2IdOrName();" size="50" maxlength="50" styleClass="text" />
		         <div id="project2IdOrNameMessage" class="blank">&nbsp;</div>
		         <div id="project2NameOrIdDisplay" class="blank">&nbsp;</div>
                 <html:hidden property="project2NameOrId" name="<%=formName%>" styleId="project2NameOrId"/>
          </td>
        </tr>
       <%--Ais General - Fixed the ending '>' --%>
	   </table>
     </td>
 </tr>
  <tr> 
    <td width="50%" valign="top"> 
      <table width="95%">
        <tr> 
          <td colspan="4"> 
             <h2 align="left"><bean:message key="humansampleone.subtitle.requestor"/> </h2>
          </td>
        </tr>
        <%--Submitter Number--%>
       <tr>
          <td width="163">
                    <!--bugzilla 2589 no longer required-->
					<bean:message key="humansampleone.provider.organization.localAbbreviation"/>:
		  </td>
          <td colspan="3"> 
				<app:text name="<%=formName%>" property="organizationLocalAbbreviation" styleId="organizationLocalAbbreviation" onblur="validateOrganizationLocalAbbreviation();" size="15" maxlength="10" styleClass="text" />
		         <div id="organizationLocalAbbreviationMessage" class="blank">&nbsp;</div>
		         <div id="organizationName" class="blank">&nbsp;</div>
          </td>

        </tr>
        		<%--Provider Person information--%>
        <tr> 
          <td width="163">
	   			<bean:message key="humansampleone.provider.lastName"/>:
	   			<font size="1"><bean:message key="humansampleone.provider.addionalOrClinician"/></font>
	   	  </td>
		  <td colspan="3"> 
				 <app:text name="<%=formName%>" property="providerLastName" styleId="providerLastName" size="35" maxlength="30" onblur="this.value=this.value.toUpperCase();" />
		  </td>
        </tr>
        <tr> 
          <td width="163">
          		<bean:message key="humansampleone.provider.firstName"/>:
                <font size="1"><bean:message key="humansampleone.provider.addionalOrClinician"/></font>
		  </td>
		  <td colspan="3">  
				<app:text name="<%=formName%>" property="providerFirstName" styleId="providerFirstName" size="35" maxlength="20" onblur="this.value=this.value.toUpperCase();"/>
          </td>
        </tr>
        <tr> 
          <td width="163">
					   <bean:message key="humansampleone.provider.workPhone"/>:
					   <font size="1"> <bean:message key="humansampleone.phone.additionalFormat" /></font>
		  </td>
		  <td width="173"> 
				   <app:text name="<%=formName%>" property="providerWorkPhone" styleId="providerWorkPhone" onblur="myCheckPhone(this)" onkeyup="javascript:getIt(this,event)" size="15" maxlength="13" styleClass="text" />
				          <div id="providerWorkPhoneMessage" class="blank">&nbsp;</div>
		  </td>
          <td width="32">
				       <bean:message key="humansampleone.provider.workPhone.extension"/>:
		  </td>
          <td width="129"> 
				   <app:text name="<%=formName%>" property="providerWorkPhoneExtension" styleId="providerWorkPhoneExtension" onblur="myCheckExtension(this)" size="4" maxlength="4" styleClass="text" />
				    <div id="providerWorkPhoneExtensionMessage" class="blank">&nbsp;</div>
		  </td>
        </tr>
        <tr> 
          <td width="163">&nbsp;</td>
          <td width="173">&nbsp; </td>
          <td width="32">&nbsp;</td>
          <td width="129">&nbsp;</td>
        </tr>
        <tr> 
          <td width="163">&nbsp;</td>
          <td width="173">&nbsp; </td>
          <td width="32">&nbsp;</td>
          <td width="129">&nbsp;</td>
        </tr>
        <tr> 
          <td width="163">&nbsp;</td>
          <td width="173">&nbsp; </td>
          <td width="32">&nbsp;</td>
          <td width="129">&nbsp; </td>
        </tr>
        <tr>
          <td width="163">&nbsp;</td>
          <td width="173">&nbsp;</td>
          <td width="32">&nbsp;</td>
          <td width="129">&nbsp;</td>
        </tr>
      </table>
    </td>
  <td width="50%" valign="top"> 
    <table width="100%">
      <tr> 
        <td colspan="5"> 
          <h2>
            <bean:message key="humansampleone.subtitle.patient"/>
          </h2>
        </td>
      </tr>
      <%--bugzilla 1904 moved external id to top and renamed to Patient ID--%>
      <tr> 
          <td width="116">
		    <bean:message key="patient.externalId"/>:
          </td>
          <td colspan="4"> 
	       <app:text name="<%=formName%>" property="externalId" styleId="externalId" size="35" maxlength="20" onblur="this.value=this.value.toUpperCase();"/>
	      </td>
      </tr>
      <tr> 
          <td width="116">
					<bean:message key="person.lastName"/>:
		  </td>
          <td colspan="4"> 
				<app:text name="<%=formName%>" property="lastName" styleId="lastName" size="49" maxlength="30" onblur="this.value=this.value.toUpperCase();"/>
            </td>
        </tr>
        <tr> 
          <td width="116">
		     <bean:message key="person.firstName"/>:
          </td>
          <td colspan="2"> 
	         <app:text name="<%=formName%>" property="firstName" styleId="firstName" size="35" maxlength="20" onblur="this.value=this.value.toUpperCase();"/>
          </td>
          <td width="31">
			<bean:message key="person.middleName"/>:
          </td>
          <td width="132"> 
	        <app:text name="<%=formName%>" property="middleName" styleId="middleName" size="4" maxlength="20" onblur="this.value=this.value.toUpperCase();"/>
          </td>
        </tr>
        <tr> 
          <td width="116">
			<bean:message key="person.streetAddress"/>:
	      </td>
          <td colspan="2"> 
	       <app:text name="<%=formName%>" property="streetAddress" styleId="streetAddress" size="35" maxlength="30" onblur="this.value=this.value.toUpperCase();"/>
          </td>
          <td width="31">
	     	<bean:message key="person.multipleUnit"/>:
          </td>
          <td width="132"> 
        	<app:text name="<%=formName%>" property="multipleUnit" styleId="multipleUnit" size="4" maxlength="30" onblur="this.value=this.value.toUpperCase();"/>
          </td>
        </tr>
        <tr> 
          <td width="116">
	    	<bean:message key="person.city"/>:
          </td>
          <td colspan="4"> 
	        <app:text name="<%=formName%>" property="city" styleId="city" onblur="this.value=this.value.toUpperCase();validateCity();" size="49" maxlength="30" styleClass="text" />
	        <div id="cityMessage" class="blank">&nbsp;</div>
	        <html:hidden property="cityId" name="<%=formName%>" styleId="cityId"/>
            <input id="validCityStateZipCombo" name="validCityStateZipCombo" type="hidden" />
          </td>
        </tr>
        <tr> 
          <td width="116">
	    	  <bean:message key="person.state"/>:
		  </td>
          <td width="276"> 
		    <app:text name="<%=formName%>" property="state" styleId="state" onblur="this.value=this.value.toUpperCase();validateState();" size="5" maxlength="2" styleClass="text" />
		    <div id="stateMessage" class="blank">&nbsp;</div>
		  </td>
          <td width="29">&nbsp;</td>
          <td width="31"><bean:message key="person.zipCode" />:
          </td>
          <td width="132"> 
		    <app:text name="<%=formName%>" property="zipCode" styleId="zipCode" onblur="validateZipCode()" size="13" maxlength="10" styleClass="text" />
		    <div id="zipCodeMessage" class="blank">&nbsp;</div>
          </td>
        </tr>
        <%--bugzilla 1904 Medical Record/Chart # is a new field--%>
        <tr> 
          <td width="116">
		    <bean:message key="patient.chartNumber"/>:
          </td>
          <td colspan="2"> 
	       <app:text name="<%=formName%>" property="chartNumber" styleId="chartNumber" size="35" maxlength="20" onblur="this.value=this.value.toUpperCase();"/>
	      </td>
          <td width="31">
           <%--bugzilla 1715 gender is required--%>
	       <bean:message key="patient.gender"/>:<span class="requiredlabel">*</span>
           <font size="1"><bean:message key="patient.gender.options"/></font>
	      </td>
          <td width="132"> 
	         <app:text name="<%=formName%>" property="gender" styleId="gender" onblur="this.value=this.value.toUpperCase()" size="4" maxlength="1" />
          </td>
        </tr>
        <tr> 
          <td width="116">
		     <bean:message key="patient.birthDate"/>:
	         <font size="1"><bean:message key="humansampleone.date.additionalFormat" /></font>
	      </td>
          <td colspan="4"> 
	        <app:text name="<%=formName%>" property="birthDateForDisplay" styleId="birthDateForDisplay" size="20" maxlength="10" onkeyup="myCheckDate(this, event, false,false)" onblur="myCheckDate(this, event, true, true)" styleClass="text" />
	        <div id="birthDateForDisplayMessage" class="blank">&nbsp;</div>
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
          <bean:message key="humansampleone.subtitle.sample"/>
      </h2>
    </td>
  </tr>
  <tr> 
    <td width="121">
		 <bean:message key="sample.collectionDate"/>:
	         <font size="1"><bean:message key="humansampleone.date.additionalFormat" /></font>
	</td>
    <td width="212"> 
      <app:text name="<%=formName%>" property="collectionDateForDisplay" styleId="collectionDateForDisplay" size="20" maxlength="10" onkeyup="myCheckDate(this, event, false,false)" onblur="myCheckDate(this, event, true, true)" styleClass="text" />
      <div id="collectionDateForDisplayMessage" class="blank">&nbsp;</div>
    </td>
    <td width="109">
		 <bean:message key="sample.collectionTime"/>:
         <font size="1"><bean:message key="humansampleone.time.additionalFormat" /></font>
    </td>
    <td width="100"> 
      <app:text name="<%=formName%>" property="collectionTimeForDisplay" styleId="collectionTimeForDisplay" size="13" maxlength="15" onblur="myCheckTime(this);" styleClass="text" />
      <div id="collectionTimeForDisplayMessage" class="blank">&nbsp;</div>
    </td>
    <td width="150">
			<bean:message key="sample.clientReference"/>:
	</td>
    <td width="196"> 
	  <app:text name="<%=formName%>" property="clientReference" styleId="clientReference" size="20" maxlength="10" onblur="this.value=this.value.toUpperCase();"/>
    </td>
    <td width="100">
			<bean:message key="sample.referredCultureFlag"/>:<span class="requiredlabel">*</span>
    </td>
    <td width="76"> 	
         <app:text name="<%=formName%>" property="referredCultureFlag" styleId="referredCultureFlag" size="4" maxlength="1" onblur="this.value=this.value.toUpperCase()"/>
   </td>
  </tr>
  <%--bugzilla 1894 -start --%>
  <tr> 
    <td width="121">
	       <bean:message key="sampleitem.typeOfSample"/>:<span class="requiredlabel">*</span>
	</td>
    <td width="212"> 
	 <app:text name="<%=formName%>" property="typeOfSampleDesc" styleId="typeOfSampleDesc" onblur="this.value=this.value.toUpperCase();validateSampleType();" size="25" styleClass="text" />
	 <div id="typeOfSampleDescMessage" class="blank">&nbsp;</div>
	 <html:hidden property="typeOfSampleId" name="<%=formName%>" styleId="typeOfSampleId"/>
	</td>
 </tr>
 <%-- end 1894--%>
 <%-- begin 1408 
    <td width="109">
	       <bean:message key="sampleitem.sourceOfSample"/>:
	</td>
    <td width="215"> 
	 <app:text name="<%=formName%>" property="sourceOfSampleDesc" styleId="sourceOfSampleDesc" onblur="this.value=this.value.toUpperCase();validateSampleSource();" size="25" styleClass="text" />
     <div id="sourceOfSampleDescMessage" class="blank">&nbsp;</div>
     <html:hidden property="sourceOfSampleId" name="<%=formName%>" styleId="sourceOfSampleId"/>
    </td>
    <td width="52">
		<bean:message key="sampleitem.sourceOther"/>:
    </td>
    <td colspan="3"> 
	   <app:text name="<%=formName%>" property="sourceOther" styleId="sourceOther" size="25"/>
    </td>
  </tr>
  AIS - bugzilla 1408 -end --%>  
  <tr> 
    <%--AIS - bugzilla 1408 -start
    <td width="121">
			<bean:message key="sample.receivedDate"/>:<span class="requiredlabel">*</span>
	         <font size="1"><bean:message key="humansampleone.date.additionalFormat" /></font>
    </td>
    <td width="212"> 
		<app:text name="<%=formName%>" property="receivedDateForDisplay" styleId="receivedDateForDisplay" onkeyup="myCheckDate(this, event, false,false)" onblur="myCheckDate(this, event, true, true)" size="20" styleClass="text" />
		<div id="receivedDateForDisplayMessage" class="blank">&nbsp;</div>
	</td>
	AIS - bugzilla 1408 -end --%>		
    <td width="109">
			<bean:message key="sample.stickerReceivedFlag"/>:<span class="requiredlabel">*</span>
	</td>
    <td width="215"> 
		<app:text name="<%=formName%>" property="stickerReceivedFlag" styleId="stickerReceivedFlag" size="4" maxlength="1" onblur="this.value=this.value.toUpperCase()"/>
    </td>
    <td width="52">&nbsp;</td>
    <td width="179">&nbsp;</td>
    <td width="100">&nbsp;</td>
    <td width="76">&nbsp;</td>
  </tr>
  <tr> 
    <td width="121">&nbsp; </td>
    <td width="212">&nbsp; </td>
    <td width="109">&nbsp;</td>
    <td width="215">&nbsp;</td>
    <td width="52">&nbsp;</td>
    <td width="179">&nbsp;</td>
    <td width="100">&nbsp;</td>
    <td width="76">&nbsp;</td>
 </tr>
</table>
</div>
<%--bugzilla 2438 use localAbbreviation instead of id--%>
  <ajax:autocomplete
  source="projectIdOrName"
  target="projectNameOrId"
  baseUrl="ajaxAutocompleteXML"
  className="autocomplete"
  parameters="projectName={projectIdOrName},provider=ProjectAutocompleteProvider,fieldName=projectName,idName=localAbbreviation"
  minimumCharacters="1" 
  postFunction="projectFocus"
  />
  
  <ajax:autocomplete
  source="project2IdOrName"
  target="project2NameOrId"
  baseUrl="ajaxAutocompleteXML"
  className="autocomplete"
  parameters="projectName={project2IdOrName},provider=ProjectAutocompleteProvider,fieldName=projectName,idName=localAbbreviation"
  minimumCharacters="1" 
  postFunction="project2Focus"
  />

 <%--bugzilla 1545 remove autocomplete for city
  <ajax:autocomplete
  source="city"
  target="cityId"
  baseUrl="ajaxAutocompleteXML"
  className="autocomplete"
  parameters="city={city},provider=CityAutocompleteProvider,fieldName=city,idName=id"
  minimumCharacters="1"
  postFunction="cityFocus"
   />
 --%>
   <%-- AIS - bugzilla 1408
  <ajax:autocomplete
  source="sourceOfSampleDesc"
  target="sourceOfSampleId"
  baseUrl="ajaxAutocompleteXML"
  className="autocomplete"
  parameters="sourceOfSampleDesc={sourceOfSampleDesc},domain={domain},provider=SampleSourceAutocompleteProvider,fieldName=description,idName=id"
  minimumCharacters="1"
  postFunction="sampleSourceFocus"
   />
  AIS - bugzilla 1408 --%>   
  <%--bugzilla 1894 added sample type back--%>
  <ajax:autocomplete
  source="typeOfSampleDesc"
  target="typeOfSampleId"
  baseUrl="ajaxAutocompleteXML"
  className="autocomplete"
  parameters="typeOfSampleDesc={typeOfSampleDesc},domain={domain},provider=SampleTypeAutocompleteProvider,fieldName=description,idName=id"
  minimumCharacters="1"
  postFunction="sampleTypeFocus"
   />
 

<%--bugzilla 1512 custom JavascriptValidator--%>
<app:javascript formName="humanSampleOneForm" staticJavascript="true"/>

