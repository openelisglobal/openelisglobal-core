<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	java.util.Locale,
	us.mn.state.health.lims.common.action.IActionConstants, 
	us.mn.state.health.lims.common.util.SystemConfiguration,
	us.mn.state.health.lims.common.util.resources.ResourceLocator,
	org.apache.struts.Globals" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>
<%--bugzilla 1908 changed some disabled values for Vietnam tomcat/linux--%>
<%--bugzilla 2362, 2356 fix js error with single quotes in data from HSE1--%>
<%--bugzilla 2451: remove asterisks to indicate required for last name, first name, patient id--%>


<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="hse1" name="<%=formName%>" property="humanSampleOneMap" type="java.util.HashMap"/>
<%--bugzilla 1387 --%>
<bean:define id="humanDomain" value='<%= SystemConfiguration.getInstance().getHumanDomain() %>' />
<bean:define id="enablePdfLink" value='<%= SystemConfiguration.getInstance().getEnabledSamplePdf() %>' />

<%--bugzilla 1510 add styleId for compatibility in firefox and for use of firebug debugger--%>
<%--bugzilla 2069 using organizationLocalAbbreviation--%>
<%--bugzilla 1904 did some reformatting to fit new row on screen: Patient ID--%>
<%!

String allowEdits = "true";
String disabled = "false";
String collectionDateMsg = "";
//bugzilla 1895
String errorSelectCombo = "";
String lbLoadMessage = "";

String path = "";
String basePath = "";
%>
<%
path = request.getContextPath();
basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";

if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}
request.setAttribute(IActionConstants.ALLOW_EDITS_KEY, allowEdits);

if (allowEdits.equals("true")) {
  disabled = "false";
}

if (allowEdits.equals("false")) {
  disabled = "true";
}

//bgm - bugzilla 1586 message for collection date being blank
Locale locale = (Locale)request.getSession().getAttribute(Globals.LOCALE_KEY);
collectionDateMsg = ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
					"humansampleone.blank.collectionDate.message");
//bugzilla 1895
errorSelectCombo =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "humansampleone.cityStateZipPopup.selectone.error");
lbLoadMessage =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "common.loading.message");

// bugzilla 2151
String saveDisabled = (String)request.getAttribute(IActionConstants.SAVE_DISABLED);                    
%>


<script language="JavaScript1.2">
//global js variables
//bugzilla 1895
var lBox;
var formFieldArray = new Array('projectIdOrName',
                                    'project2IdOrName',
                                    'accessionNumber', 
                                    'birthDateForDisplay',
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
                                    'providerFirstName',
                                    'providerLastName',
                                    'providerWorkPhone',
                                    'providerWorkPhoneExtension',
                                    'receivedDateForDisplay',
                                    'referredCultureFlag',
                                    //bugzilla 1894
                                    //'sourceOfSampleDesc',
                                    //'sourceOther',
                                    'state', 
                                    'stickerReceivedFlag',
                                    'streetAddress',
                                    'typeOfSampleDesc',
                                    'zipCode'   
                                    );
var formFieldsValidArray = new Array(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                                    //bugzilla 1894
                                    // true, true, 
                                    true, true, true, true, true);
var formFieldsRequiredArray = new Array(false, false, true, false, false, false, false, false, false, false, true, false, false, false, true, 
                                    //bugzilla 2589 submitter unknown is null
                                    false, 
                                    false, false, false, true, true,
                                    //bugzilla 1894
                                    //false, false, 
                                    false, true, false, true, false);
var formFieldsWithAjaxDropDownArray = new Array(true, true, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, 
                                    //bugzilla 1894
                                    //true, false,
                                    false, false, false, true, false);
var BLANK = "b|l|a|n|k";

function pageOnLoad() {
  var allowEdits = '<%=allowEdits%>';
  if ( '<%=enablePdfLink%>' == '<%=IActionConstants.YES%>' ) {
      document.forms[0].selectedAccessionNumberTwo.focus();
  } else {
	  if (allowEdits == 'true') {
	    var projectIdOrName = $("projectIdOrName");
		projectIdOrName.focus();
	  } else {
		var accnNumb = $("accessionNumber");
		accnNumb.focus();
	  }
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

//this returns true or false - does this field have an ajax dropdown?
function isFieldAjaxDropDown(field) {
  var i;
  for (i = 0; i < formFieldArray.length; i++) {
       if (formFieldArray[i] == field) {
              break;
       }
  }
  return formFieldsWithAjaxDropDownArray[i];
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

//disable or enable save button based on validity of fields
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

function doFocus(field, nextField){
//since we will not do any more ajax on this clear out the flag that prevents validating other fields through ajax
  $("errorInField").value = "";
  var fieldMessage = field + 'Message';
  var mdiv = $(fieldMessage);
  var focusField = $(field);
  focusField.focus();
  //clear out badmessage that occurs on onblur before selection is made in Ajax drop down (this is a premature badmessage)
  if (mdiv.className == "badmessage") {
      mdiv.className = "blank";
      setFieldValid(field);
      //retrieve value for hiddenfields
      var hidden2 = "de2" + field;
      var hidden3 = "de3" + field;
      $(hidden2).value = $F(hidden3);
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

//bugzilla1894
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
    var validated = validateHumanSampleTwoForm(form);
    return validated;
}

function getDE1ValueFromHashMap(field) {
      var de1Value = "";
      var fld = $(field);    

        if (field == "projectIdOrName") {
           if (IsNumeric(fld.value)) {
               de1Value = "<%= hse1.get("projectId") %>";
            } else {
               de1Value = "<%= hse1.get("projectName") %>";
            }
         }
         
         if (field == "project2IdOrName") {
           if (IsNumeric(fld.value)) {
               de1Value = "<%= hse1.get("projectId2") %>";
            } else {
               de1Value = "<%= hse1.get("projectName2") %>";
            }
         }
            
          if (field == "birthDateForDisplay") 
            de1Value = "<%= hse1.get("birthDateForDisplay") %>";
          if (field == "collectionDateForDisplay") 
            de1Value = "<%= hse1.get("collectionDateForDisplay") %>";
          if (field == "collectionTimeForDisplay") 
            de1Value = "<%= hse1.get("collectionTimeForDisplay") %>";
          if (field == "chartNumber") 
            de1Value = "<%= hse1.get("chartNumber") %>";
          if (field == "city") 
            de1Value = "<%= hse1.get("city") %>";
          if (field == "clientReference") 
            de1Value = "<%= hse1.get("clientReference") %>";
          if (field == "externalId") 
            de1Value = "<%= hse1.get("externalId") %>";
          if (field == "firstName") 
            de1Value = "<%= hse1.get("firstName") %>";
          if (field == "gender") 
            de1Value = "<%= hse1.get("gender") %>";
          if (field == "lastName") 
            de1Value = "<%= hse1.get("lastName") %>";
          if (field == "multipleUnit") 
            de1Value = "<%= hse1.get("multipleUnit") %>";
          if (field == "middleName") 
            de1Value = "<%= hse1.get("middleName") %>";
          if (field == "organizationLocalAbbreviation") 
            de1Value = "<%= hse1.get("organizationLocalAbbreviation") %>";
          if (field == "providerFirstName") 
            de1Value = "<%= hse1.get("providerFirstName") %>";
          if (field == "providerLastName") 
            de1Value = "<%= hse1.get("providerLastName") %>";
          if (field == "providerWorkPhone") 
            de1Value = "<%= hse1.get("providerWorkPhone") %>";
          if (field == "providerWorkPhoneExtension") 
            de1Value = "<%= hse1.get("providerWorkPhoneExtension") %>";
          if (field == "receivedDateForDisplay") 
            de1Value = "<%= hse1.get("receivedDateForDisplay") %>";
          if (field == "referredCultureFlag") 
            de1Value = "<%= hse1.get("referredCultureFlag") %>";
          if (field == "sourceOfSampleDesc") 
            de1Value = "<%= hse1.get("sourceOfSampleDesc") %>";
          //bugzilla 1465
          if (field == "sourceOfSampleId") 
            de1Value = "<%= hse1.get("sourceOfSampleId") %>";
          if (field == "sourceOther") 
            de1Value = "<%= hse1.get("sourceOther") %>";
          if (field == "state") 
            de1Value = "<%= hse1.get("state") %>";
          if (field == "stickerReceivedFlag") 
            de1Value = "<%= hse1.get("stickerReceivedFlag") %>";
          if (field == "streetAddress") 
            de1Value = "<%= hse1.get("streetAddress") %>";
          if (field == "typeOfSampleDesc") 
            de1Value = "<%= hse1.get("typeOfSampleDesc") %>";
          //bugzilla 1465
          if (field == "typeOfSampleId") 
            de1Value = "<%= hse1.get("typeOfSampleId") %>";
          if (field == "zipCode") 
            de1Value = "<%= hse1.get("zipCode") %>";
 
   return de1Value;      
}

//This is for validating individual fields: submitter#, city, state, sample type, sample source etc
function setMessage(message, field) {
     //alert("I am in in setMessage - reg. validation - with message and field " + message + " " + field);
     var fieldMessage = field + "Message";
     var mdiv = $(fieldMessage);
     var idField = $(field);
     //alert("message " + message);
     if (message == "invalid") {
       //some fields are not required
       if (idField.value == "" && !isFieldRequired(field)) {
         mdiv.className = "blank";
         compareWithDE1(field);
       } else {
       mdiv.className = "badmessage";
       setFieldInvalid(field);
       //since we don't position cursor on invalid back to invalid field need to allow validation of next field
       $("errorInField").value = "";
        //reset the hidden field for comparison so we can start over after an invalid entry
       if (field != 'accessionNumber') {
           var hidden2 = "de2" + field;
           if (isFieldAjaxDropDown(field)) {//e.g. city, source, type, projectIdOrName, project2IdOrName
             var hidden3 = "de3" + field;
             //store this in case it needs to be retrieved on ajax selection - with mouse
             if ($F(hidden2) != BLANK) {
             //alert("this is hidden2 " + hidden2 + " hidden3 " + hidden3);
                 $(hidden3).value = $F(hidden2);
             } else {
                 $(hidden3).value = "";
             }
           }
           //this forces it to poll first time over
           $(hidden2).value = "";
       } else { 
        //invalid accessionNumber 
          //this will reload the form
          setFieldInvalid(field);
          //only submit if accession number isn't left blank
          if ($F(field) != '') {
            document.forms[0].blankscreen.value = "true";
            //bugzilla 1507
            //setAction(window.document.forms[0], 'PopulateHashMapFromDE1', 'yes', '?ID=');
          }
       }
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
             //var projectNameOrId = $("projectNameOrId");
             //projectNameOrId.innerHTML = "";
             
       }
       if (idField.name == "project2IdOrName") {
             var project2NameOrIdDisplay = $("project2NameOrIdDisplay");
             project2NameOrIdDisplay.innerHTML = "";
             //var project2NameOrId = $("project2NameOrId");
             //project2NameOrId.innerHTML = "";
       }
     } else {
       mdiv.className = "blank";
       //if it is valid then compare with DE1 (except accessionNumber)
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
             //bugzilla 1465
             if (idField.name == 'sourceOfSampleDesc') {
                idField = $("sourceOfSampleId");
                idField.value = message.substring(5);
             } else if (idField.name == 'typeOfSampleDesc') {
                idField = $("typeOfSampleId");
                idField.value = message.substring(5);
             } else {
                idField.value = message.substring(5);
             }
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
          compareWithDE1(field);
        } else {
          //this will get the info based on accession number
          setFieldValid(field);
          document.forms[0].blankscreen.value = "true";
          setAction(window.document.forms[0], 'PopulateHashMapFromDE1', 'yes', '?ID=');
       }
     }
  setSave();
}



//This is for validating individual fields: submitter#, city, state, sample type, sample source etc
function setDE1Message(message, field) {
//alert("set de1 message " + message + " " + field);
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

//BGM - bugzilla 1495 added form=humanSampleTwo to be passed in to validate status code
function validateAccessionNumber(field) {

	    //bgm - bugzilla 1624 numbersOnlyCheck()
	    numbersOnlyCheck(field);
	    
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
}

function validatePollFirstTime(field) {
        var message = "";
        var fld = $(field);    
        
        //get the values from DE1 from the HashMap hse1 loaded in HumanSampleTwoUpdateAction
        message = getDE1ValueFromHashMap(field);
       
      
         //alert("This is message " + message);      
           
           
        //alert("validatePollFirstTime xml " + message + field);
        var entry1 = message;

        var entry2 = fld.value;
        //hide the dbase value and don't change this
        var hidden1 = "de1" + field;
        var hidden2 = "de2" + field;
        //alert("validatePollFirstTime This is hidden1 " + hidden1 + " " + $(hidden1));
        $(hidden1).value = entry1;
  
  //IS THIS A FIX FOR ? EACH TIME BLANK DATE? (REQUIRED)
       //if (entry2 == "" && !isFieldRequired(field)) {
         if (entry2 == "") {
              $(hidden2).value = BLANK;
        } else {
              $(hidden2).value = entry2;
        }
        //GOOD DEBUG STATEMENT for comparing DE1 - DE2
//alert("I am in poll first time this is entry1 and entry2 and hidden2" + entry1 + " " + entry2  + " " + $F(hidden2));
        if(entry1!=entry2){
           	fld.value = "";
        	//fld.focus(); //bgm - bugzilla 1666 allow tab to next field
        	//alert("In validatePollFirstTime() entry1 != entry2 field: " + field);
            setDE1Message('invalid', field);
            //playSound();   
           	//window.setTimeout(fld.focus(),500);
        } else {
         //blank out the second hidden value to start over if needed
            $("errorInField").value = "";
            $(hidden2).value = "";
            setDE1Message('valid', field);
        }
}

function validatePollNotFirstTime(field) {
        var message = "";
        var fld = $(field);    
       
		var entry2 = fld.value;
		var hidden1 = "de1" + field;
		var hidden2 = "de2" + field;
        var entry1 = $F(hidden1);
        var entry2FirstTime = $F(hidden2);
        
        //alert("This is poll not first time " + entry1 + " " + entry2FirstTime + " " + entry2);
        
        //if a field is not required a blank is coded as BLANK
        if ($F(hidden2) == BLANK) {
           entry2FirstTime = "";
        }
        if(entry1==entry2){
        	//after this time confirmed if this field is changed you need to confirm twice again
           	$(hidden2).value = "";
            $("errorInField").value = "";
        	setDE1Message('valid', field);
        }
        if(entry2FirstTime==entry2){
         	//after this time confirmed if this field is changed you need to confirm twice again
         	$(hidden2).value = "";
            $("errorInField").value = "";
          	setDE1Message('valid', field);
        }   
        //this entry didn't match database (entry1) or the stored previous entry so start over
        if (entry1 != entry2 && entry2FirstTime != entry2) {
          //force to start over
          if (entry2 == "" && !isFieldRequired(field)) {
              $(hidden2).value = BLANK;
          } else {
              $(hidden2).value = entry2;
          }
            fld.value = "";
          	setDE1Message('invalid', field);
            //fld.focus(); //bgm - bugzilla 1666 commented out to allow tab to next field
           	//playSound();   
           	//window.setTimeout(fld.focus(),500);
         }
 
}


function compareWithDE1(field) {
	  //alert("I am in compareWithDE1 " + field);
	  var hidden2 = "de2" + field;
	  //alert("Decides whether poll first or not --- This is hidden2 " + $F(hidden2));
	  if(""==$F(hidden2)){
	  	validatePollFirstTime(field);
	  } else{
	    validatePollNotFirstTime(field);
	  }
}

function validateCity() {
	 var errField = $("errorInField");
	 if(errField.value=="" || "city"==errField.value){
	  errField.value="city";
	  var field = 'city';
	  var idField = $("city");
	  
	 //alert("I am in validateCity");
	
	//if the entry matches what is in HashMap then we don't need to do anything else - just return
	  if (idField.value == "<%= hse1.get("city") %>") {
	   var hidden2 = "de2" + idField.name;
	   $(hidden2).value = "";
	   setDE1Message('valid', idField.name);
	   errField.value = "";
	   return;
	  }
	 //bugzila 1765 changes to city validation - no longer validates city/zip combo
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
}

function validateProjectIdOrName() {
	  var errField = $("errorInField");
	  if(errField.value=="" || 'projectIdOrName'==errField.value){
	     errField.value="projectIdOrName";
	     var field = "projectIdOrName";
	     //alert("I am in validateProjectIdOrName");
	     var idField = $("projectIdOrName");
	 
	 //if the entry matches what is in HashMap then we don't need to do anything else - just return
	  if (IsNumeric(idField.value)) {
	          
	     if (idField.value == "<%= hse1.get("projectId") %>") {
	          var hidden2 = "de2" + idField.name;
	          $(hidden2).value = "";
	          setDE1Message('valid', idField.name);
	          var projectNameOrIdDisplay = $("projectNameOrIdDisplay");
	          projectNameOrIdDisplay.innerHTML = "<%= hse1.get("projectName") %>";
	          errField.value = "";
	          return;
	     }
	     
	     
	  } else {
	     
	     if (idField.value == "<%= hse1.get("projectName") %>") {
	          var hidden2 = "de2" + idField.name;
	          $(hidden2).value = "";
	          setDE1Message('valid', idField.name);
	          var projectNameOrIdDisplay = $("projectNameOrIdDisplay");
	          projectNameOrIdDisplay.innerHTML = "<%= hse1.get("projectId") %>";
	          errField.value = "";
	          return;
	     }
	     
	  }
	

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
  
}

function validateProject2IdOrName() {
	  var errField = $("errorInField");
	  if(errField.value=="" || 'project2IdOrName'==errField.value){
	     errField.value="project2IdOrName";
	     var field = "project2IdOrName";
	    //alert("I am in validateProject2IdOrName");
	
	    var idField = $("project2IdOrName");
	 
	 //if the entry matches what is in HashMap then we don't need to do anything else - just return
	  if (IsNumeric(idField.value)) {
	          
	     if (idField.value == "<%= hse1.get("project2Id") %>") {
	          var hidden2 = "de2" + idField.name;
	          $(hidden2).value = "";
	          setDE1Message('valid', idField.name);
	          var project2NameOrIdDisplay = $("project2NameOrIdDisplay");
	          project2NameOrIdDisplay.innerHTML = "<%= hse1.get("project2Name") %>";
	          errField.value = "";
	          return;
	     }
	     
	     
	  } else {
	     
	     if (idField.value == "<%= hse1.get("project2Name") %>") {
	          var hidden2 = "de2" + idField.name;
	          $(hidden2).value = "";
	          setDE1Message('valid', idField.name);
	          var project2NameOrIdDisplay = $("project2NameOrIdDisplay");
	          project2NameOrIdDisplay.innerHTML = "<%= hse1.get("project2Id") %>";
	          errField.value = "";
	          return;
	     }
	     
	  }

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

	//alert("field value: " + field + " field length: " + field.length + " fieldMessage: " + fieldMessage );
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
	  var field = "organizationLocalAbbreviation";
	  var idField = $("organizationLocalAbbreviation");
	  numbersOnlyCheck(idField);
	
	  var errField = $("errorInField");
	  if(errField.value=="" || 'organizationLocalAbbreviation'==errField.value){
	     errField.value="organizationLocalAbbreviation";
	
	//if the entry matches what is in HashMap then we don't need to do anything else - just return
	  if (idField.value == "<%= hse1.get("organizationLocalAbbreviation") %>") {
	      var hidden2 = "de2" + idField.name;
	      $(hidden2).value = "";
	      setDE1Message('valid', idField.name);
	      var orgName = $("organizationName");
	      orgName.innerHTML = "<%= hse1.get("organizationNameForDisplay") %>";
	      errField.value = "";
	      return;
	     }
	
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

}

//this is for fields that were otherwise not validated in DE1 through AJAX (not required or specific format etc)
function validateFieldCompareDE1(field) {
     var fieldName = field.name;
     var errField = $("errorInField");
     //alert("I am in validateFieldCompareDE1 " + field.name + " " + errField.value);
  
     if (errField.value == "" || fieldName == errField.value) {
        errField.value = fieldName;
        //alert("In validateFieldCompareDE1() about to call compareWithDE1() fieldName: " + fieldName);
      }
      //bgm - bugzilla 1666 we need to always do this for the hse2 field check against hse1 field.
        compareWithDE1(fieldName);
}

function validateSampleSource() {
	  var errField = $("errorInField");
	  if(errField.value=="" || 'sourceOfSampleDesc'==errField.value){
	   errField.value='sourceOfSampleDesc';
	   var idField = $("sourceOfSampleDesc");
	
	//if the entry matches what is in HashMap then we don't need to do anything else - just return
	  if (idField.value == "<%= hse1.get("sourceOfSampleDesc") %>") {
	      var hidden2 = "de2" + idField.name;
	      $(hidden2).value = "";
	      setDE1Message('valid', idField.name);
	      errField.value = "";
	      //bugzilla 1465 set the sourceOfSampleId (in case typed instead of selected)
	      var id = $("sourceOfSampleId");
          id.value =  "<%= hse1.get("sourceOfSampleId") %>";	      
	      return;
	    }
	     
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
}

function validateSampleType() {
	  var errField = $("errorInField");
	  if(errField.value=="" || 'typeOfSampleDesc'==errField.value){
	   errField.value='typeOfSampleDesc';
	   var idField = $("typeOfSampleDesc");
	
	//if the entry matches what is in HashMap then we don't need to do anything else - just return
	  if (idField.value == "<%= hse1.get("typeOfSampleDesc") %>") {
	      var hidden2 = "de2" + idField.name;
	      $(hidden2).value = "";
	      setDE1Message('valid', idField.name);
	      errField.value = "";
	      //bugzilla 1465 set the typeOfSampleId (in case typed instead of selected)
          var id = $("typeOfSampleId");
          id.value =  "<%= hse1.get("typeOfSampleId") %>";	 
	      return;
	    }else{//bgm - bugzilla 1666 need to set question message
	    	setDE1Message('invalid', idField.name);
	    }
	
	
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

	  }
}

function validateState() {
	  var errField = $("errorInField");
	  if(errField.value=="" || "state"==errField.value){
	   errField.value="state";
	   var field = "state";
	   //alert("I am in validateState " + field);
	   var idField = $("state");
	
	//if the entry matches what is in HashMap then we don't need to do anything else - just return
	  if (idField.value == "<%= hse1.get("state") %>") {
	      var hidden2 = "de2" + idField.name;
	      $(hidden2).value = "";
	      setDE1Message('valid', idField.name);
	      errField.value = "";
	      return;
	  }
	    
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

}

function invalidZip() {
    var mdiv = $("zipCodeMessage");
    mdiv.className = "badmessage";
    document.forms[0].save.disabled = true;
}

function validateZipCode() {
	  var errField = $("errorInField");
	  if(errField.value=="" || 'zipCode'==errField.value){
	   errField.value='zipCode';
	   var field = 'zipCode';
	   var idField = $("zipCode");
	
	//if the entry matches what is in HashMap then we don't need to do anything else - just return
	  if (idField.value == "<%= hse1.get("zipCode") %>") {
	      var hidden2 = "de2" + idField.name;
	      $(hidden2).value = "";
	      setDE1Message('valid', idField.name);
	      errField.value = "";
	      return;
	  }
	  
	
	
	 
	 //check if we even need to use Ajax validation:
	 var zip = idField.value;
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

     //bugzilla 1765 changes to zip validator - no longer validates city/zip combo
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
}


function myCheckTime(time) {
    var fieldMessage = time.name + 'Message';
    var mdiv = $(fieldMessage);
    if (checkTime(time)) {
       mdiv.className = "blank";
       setFieldValid(time.name);
       validateFieldCompareDE1(time);
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
	      validateFieldCompareDE1(field);
	    }
	  } else {   
	      mdiv.className = "blank";
	      setFieldValid(field.name);
	      validateFieldCompareDE1(field);
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
	      validateFieldCompareDE1(field);
	  }  
	  } else {
	      mdiv.className = "blank";
	      setFieldValid(field.name);
	      validateFieldCompareDE1(field);
	
	  }
	   setSave();
}

function myCheckDate(date, event, dateCheck, onblur) {
	 if(date !=null && date != '' && date != undefined) {
	 var messageDiv = date.name + 'Message';
	 var mdiv = $(messageDiv);
	 	//bgm - bugzilla 1586 date check not needed here if blank
	 var validDate = DateFormat(date,date.value,event,dateCheck,'1');
	 if (dateCheck) { 
	   if (validDate) {
	       var validDate2 = lessThanCurrent(date);
	       if (validDate2) {
	          mdiv.className = "blank";
	          setFieldValid(date.name);
	          if (onblur) {
	          validateFieldCompareDE1(date);
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

function setMySaveAction(form, action, validate, parameters) {
   
	 //first turn off any further validation
	 var errField = $("errorInField");
	 errField.value='save';
	 
	 setAction(window.document.forms[0], 'Update', 'yes', '?ID=');
		
}

function setMyCancelAction(form, action, validate, parameters) {
   
	 //first turn off any further validation
	 var errField = $("errorInField");
	 errField.value='cancel';
	 
	 setAction(window.document.forms[0], 'Cancel', 'no', '');
		
}

//additional for HSEII
function playSound(){ 
  $("sound").innerHTML= '<embed src="<%=basePath%>media/DING.WAV" loop=false autostart="true" width="0" height="0" hidden>' 
}

function submitTheForm(form) {
   setAction(form, 'Update', 'yes', '?ID=');
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
            new Insertion.Before($('lbLoadMessage'), "<div id='lbContent'>" + response.responseText + "</div>");
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

function saveItToParentForm() {
 submitTheForm(window.document.forms[0]);
}

//bugzilla 1895
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

//bugzilla 1765
//bugzilla 1895
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
     //bugzilla 1895
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
    var field = document.forms[0].selectedAccessionNumberTwo.value;
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
	var accNbr = document.forms[0].selectedAccessionNumberTwo.value;
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
        parameters: 'provider=FileValidationProvider&form=humanSampleTwo&field=accessionNumber&id=' + escape(field.value),      //request parameters
        //indicator: 'throbbing'
        onSuccess:  checkPDF,
        onFailure:  processFailure
        }
    ); 
}  

//bugzilla 2474
function formatHSEField(field, toUpper) {
   if (field.value != null && field.value != '') {
       field.value = trim(field.value);
       field.value = field.value.replace(/\s+/g,' ');
       if (toUpper == true) {
          field.value = field.value.toUpperCase();
       }
   }
}
</script>


<html:hidden property="currentDate" name="<%=formName%>" styleId="currentDate"/>
<html:hidden property="blankscreen" name="<%=formName%>" styleId="blankscreen"/>
<html:hidden property="errorInField" value="" styleId="errorInField"/>
<%--bugzilla 1387 added domain--%>
<html:hidden property="domain" name="<%=formName%>" value="<%=humanDomain%>" styleId="domain"/>

<!-- for optimistic locking-->
<html:hidden property="personLastupdated" name="<%=formName%>" styleId="personLastupdated"/>
<html:hidden property="patientLastupdated" name="<%=formName%>" styleId="patientLastupdated"/>
<html:hidden property="providerLastupdated" name="<%=formName%>" styleId="providerLastupdated"/>
<html:hidden property="providerPersonLastupdated" name="<%=formName%>" styleId="providerPersonLastupdated"/>
<html:hidden property="sampleHumanLastupdated" name="<%=formName%>" styleId="sampleHumanLastupdated"/>
<html:hidden property="sampleOrganizationLastupdated" name="<%=formName%>" styleId="sampleOrganizationLastupdated"/>
<html:hidden property="sampleItemLastupdated" name="<%=formName%>" styleId="sampleItemLastupdated"/>
<html:hidden property="sampleProject1Lastupdated" name="<%=formName%>" styleId="sampleProject1Lastupdated"/>
<html:hidden property="sampleProject2Lastupdated" name="<%=formName%>" styleId="sampleProject2Lastupdated"/>

<div id="sound"></div>

<div id="container">
<table width="100%">
<%--logic:iterate id="hse1" name="<%=formName%>" indexId="ctr" property="humanSampleOneMap" type="java.util.HashMap"--%>
  <%--bean:write property="humanSampleOneMap(projectName)" name="<%=formName%>"/--%>
<%--/logic:iterate--%>
  <tr> 
 <%--bugzilla 2438 increase size of project name--%>
    <td colspan="2" valign="top"> 
      <table width="95%">
<%
    if ( enablePdfLink.equals(IActionConstants.YES) ) { %>      
        <tr> 
            <td width="163"> 
                <bean:message key="sampletracking.popup.subtitle"/>
            </td>          
            <td colspan="3"> 
	            <html:select name="<%=formName%>" property="selectedAccessionNumberTwo" onchange="selectAccessionNumber(this);">
		            <app:optionsCollection name="<%=formName%>" property="accessionNumberListTwo"  label="label" value="value"/>		            
                </html:select>
            </td>
        </tr>        
<%  } %>
      
        <tr> 
          <td width="163"> 
             <bean:message key="sample.accessionNumber"/>:<span class="requiredlabel">*</span>
          </td>
          <td colspan="3"> 
				  <app:text name="<%=formName%>" property="accessionNumber" styleId="accessionNumber" styleClass="text" onblur="formatHSEField(this, false);validateAccessionNumber(this);" size="20" maxlength="10" />
	              <div id="accessionNumberMessage" class="blank" >&nbsp;</div>  
	              <div id="pdfLink" class="blank">&nbsp;</div>  	              
          </td>
        </tr>
        <tr>
         <td width="163">
           	<bean:message key="humansampleone.projectNumber"/>:
		  </td>
          <td colspan="3"><%--bugzilla 2438 increase size of project name--%>
   				<app:text name="<%=formName%>" property="projectIdOrName" styleId="projectIdOrName" onblur="formatHSEField(this, true);validateProjectIdOrName();" size="50" maxlength="50" styleClass="text" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>"/>
		         <div id="projectIdOrNameMessage" class="blank">&nbsp;</div>
		         <div id="projectNameOrIdDisplay" class="blank">&nbsp;</div>
		         <html:hidden property="projectNameOrId" name="<%=formName%>" styleId="projectNameOrId"/>
          </td>
         </tr>
         <tr>
          <td width="163">
           	<bean:message key="humansampleone.project2Number"/>:
		  </td>
          <td colspan="3"> <%--bugzilla 2438 increase size of project name--%>
				<app:text name="<%=formName%>" property="project2IdOrName" styleId="project2IdOrName" onblur="formatHSEField(this, true);validateProject2IdOrName();" size="50" maxlength="50" styleClass="text" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>"/>
		         <div id="project2IdOrNameMessage" class="blank">&nbsp;</div>
		         <div id="project2NameOrIdDisplay" class="blank">&nbsp;</div>
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
				<app:text name="<%=formName%>" property="organizationLocalAbbreviation" styleId="organizationLocalAbbreviation" onblur="formatHSEField(this, false);validateOrganizationLocalAbbreviation();" size="15" maxlength="10" styleClass="text" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>"/>
		        <div id="OrganizationLocalAbbreviationMessage" class="blank" >&nbsp;</div> 
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
				 
		 			<app:text name="<%=formName%>" property="providerLastName" styleId="providerLastName" size="35" maxlength="30" styleClass="text" onblur="formatHSEField(this, true);validateFieldCompareDE1(this);" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>"/>
       				<div id="providerLastNameMessage" class="blank" >&nbsp;</div>
		  </td>
        </tr>
        <tr> 
          <td width="163">
          		<bean:message key="humansampleone.provider.firstName"/>:
                <font size="1"><bean:message key="humansampleone.provider.addionalOrClinician"/></font>
		  </td>
		  <td colspan="3">  
			 <app:text name="<%=formName%>" property="providerFirstName" styleId="providerFirstName" size="35" maxlength="20" styleClass="text" onblur="formatHSEField(this, true);validateFieldCompareDE1(this);" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>"/>
             <div id="providerFirstNameMessage" class="blank" >&nbsp;</div>
          </td>
        </tr>
        <tr> 
          <td width="137">
					   <bean:message key="humansampleone.provider.workPhone"/>:
					   <font size="1"> <bean:message key="humansampleone.phone.additionalFormat" /></font>
		  </td>
		  <td width="186"> 
		  	   <app:text name="<%=formName%>" property="providerWorkPhone" styleId="providerWorkPhone" onblur="formatHSEField(this, false);myCheckPhone(this)" onkeyup="javascript:getIt(this,event)" size="15" maxlength="13" styleClass="text" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>"/>
		       	<div id="providerWorkPhoneMessage" class="blank" >&nbsp;</div>
		  </td>
          <td width="32">
				       <bean:message key="humansampleone.provider.workPhone.extension"/>:
		  </td>
          <td width="142"> 
          		<app:text name="<%=formName%>" property="providerWorkPhoneExtension" styleId="providerWorkPhoneExtension" size="4" maxlength="4" styleClass="text" onblur="formatHSEField(this, false);myCheckExtension(this);" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>"/>
   		       	<div id="providerWorkPhoneExtensionMessage" class="blank" >&nbsp;</div>
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
	       <app:text name="<%=formName%>" property="externalId" styleId="externalId" styleClass="text" size="35" maxlength="20" onblur="formatHSEField(this, true);validateFieldCompareDE1(this);" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>"/>
   	       <div id="externalIdMessage" class="blank">&nbsp;</div>
	      </td>
      </tr>
      <tr> 
          <td width="116">
					<bean:message key="person.lastName"/>:
		  </td>
          <td colspan="4"> 
          		<app:text name="<%=formName%>" property="lastName" styleId="lastName" styleClass="text" size="49" maxlength="30" onblur="formatHSEField(this, true);validateFieldCompareDE1(this);" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>"/>
   		       	<div id="lastNameMessage" class="blank" >&nbsp;</div>
          </td>
        </tr>
        <tr> 
          <td width="116">
		     <bean:message key="person.firstName"/>:
          </td>
          <td colspan="2"> 
            	<app:text name="<%=formName%>" property="firstName" styleId="firstName" size="35" maxlength="20" styleClass="text" onblur="formatHSEField(this, true);validateFieldCompareDE1(this);" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>"/>
   		       	<div id="firstNameMessage" class="blank" >&nbsp;</div>
          </td>
          <td width="31">
			<bean:message key="person.middleName"/>:
          </td>
          <td width="132"> 
          	<app:text name="<%=formName%>" property="middleName" styleId="middleName" styleClass="text" size="4" maxlength="20" onblur="formatHSEField(this, true);validateFieldCompareDE1(this);" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>"/>
           	<div id="middleNameMessage" class="blank" >&nbsp;</div>
           </td>
        </tr>
        <tr> 
          <td width="116">
			<bean:message key="person.streetAddress"/>:
	      </td>
          <td colspan="2"> 
          	<app:text name="<%=formName%>" property="streetAddress" styleId="streetAddress" styleClass="text" size="35" maxlength="30" onblur="formatHSEField(this, true);validateFieldCompareDE1(this);" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>"/>
           	<div id="streetAddressMessage" class="blank" >&nbsp;</div>
          </td>
          <td width="31">
	     	<bean:message key="person.multipleUnit"/>:
          </td>
          <td width="132"> 
          	<app:text name="<%=formName%>" property="multipleUnit" styleId="multipleUnit" styleClass="text" size="4" maxlength="30" onblur="formatHSEField(this, true);validateFieldCompareDE1(this);" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>"/>
       		           	<div id="multipleUnitMessage" class="blank" >&nbsp;</div>
           </td>
        </tr>
        <tr> 
          <td width="116">
	    	<bean:message key="person.city"/>:
          </td>
          <td colspan="4"> 
          	<app:text name="<%=formName%>" property="city" styleId="city" size="49" maxlength="30" styleClass="text" onblur="formatHSEField(this, true);validateCity();" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>"/>
         	    <div id="cityMessage" class="blank">&nbsp;</div>
          </td>
        </tr>
        <tr> 
          <td width="116">
	    	  <bean:message key="person.state"/>:
		  </td>
          <td width="276"> 
          		<app:text name="<%=formName%>" property="state" styleId="state" size="5" maxlength="2" styleClass="text" onblur="formatHSEField(this, true);validateState();" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>"/>
	                <div id="stateMessage" class="blank">&nbsp;</div>
  		  </td>
           <td width="29">&nbsp;</td>
          <td width="31"><bean:message key="person.zipCode" />:
          </td>
          <td width="132"> 
          		<app:text name="<%=formName%>" property="zipCode" styleId="zipCode" size="13" maxlength="10" styleClass="text" onblur="formatHSEField(this, false);validateZipCode()" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>"/>
	               <div id="zipCodeMessage" class="blank">&nbsp;</div>
           </td>
 
        </tr>
        <%--bugzilla 1904 Medical Record/Chart # is a new field--%>
        <tr> 
          <td width="116">
		    <bean:message key="patient.chartNumber"/>:
          </td>
          <td colspan="2"> 
          	<app:text name="<%=formName%>" property="chartNumber" styleId="chartNumber" size="35" maxlength="20" styleClass="text" onblur="formatHSEField(this, true);validateFieldCompareDE1(this);" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>"/>
       		<div id="chartNumberMessage" class="blank" >&nbsp;</div>
          </td>
          <td width="31">
           <%--bugzilla 1715 gender is required--%>
	       <bean:message key="patient.gender"/>:<span class="requiredlabel">*</span>
           <font size="1"><bean:message key="patient.gender.options"/></font>
	      </td>
          <td width="132"> 
          	 <app:text name="<%=formName%>" property="gender" styleId="gender" size="4" maxlength="1" styleClass="text" onblur="formatHSEField(this, true);validateFieldCompareDE1(this);" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>"/>
             <div id="genderMessage" class="blank">&nbsp;</div>
          </td>
        </tr>
        <tr> 
          <td width="116">
		     <bean:message key="patient.birthDate"/>:
	         <font size="1"><bean:message key="humansampleone.date.additionalFormat" /></font>
	      </td>
          <td colspan="4"> 
          	   <app:text name="<%=formName%>" property="birthDateForDisplay" styleId="birthDateForDisplay" size="20" maxlength="10" styleClass="text" onkeyup="myCheckDate(this, event, false, false)" onblur="formatHSEField(this, false);myCheckDate(this, event, true, true);" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>" />
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
          <app:text name="<%=formName%>" property="collectionDateForDisplay" styleId="collectionDateForDisplay" size="20" maxlength="10" styleClass="text" onkeyup="myCheckDate(this, event, false,false)" onblur="formatHSEField(this, false);myCheckDate(this, event, true,true);" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>"/>
          <div id="collectionDateForDisplayMessage" class="blank">&nbsp;</div>
    </td>
    <td width="109">
		 <bean:message key="sample.collectionTime"/>:
         <font size="1"><bean:message key="humansampleone.time.additionalFormat" /></font>
    </td>
    <td width="100"> 
          <app:text name="<%=formName%>" property="collectionTimeForDisplay" styleId="collectionTimeForDisplay" size="13" maxlength="15" styleClass="text" onblur="formatHSEField(this, false);myCheckTime(this);" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>"/>
           <div id="collectionTimeForDisplayMessage" class="blank">&nbsp;</div>
    </td>
    <td width="150">
			<bean:message key="sample.clientReference"/>:
	</td>
    <td width="196"> 
    	  <app:text name="<%=formName%>" property="clientReference" styleId="clientReference" size="20" maxlength="10" styleClass="text" onblur="formatHSEField(this, true);validateFieldCompareDE1(this);" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>"/>
          <div id="clientReferenceMessage" class="blank">&nbsp;</div>
    </td>
    <td width="100">
			<bean:message key="sample.referredCultureFlag"/>:<span class="requiredlabel">*</span>
    </td>
    <td width="76"> 	
      <app:text name="<%=formName%>" property="referredCultureFlag" styleId="referredCultureFlag" size="4" maxlength="1" styleClass="text" onblur="formatHSEField(this, true);validateFieldCompareDE1(this);" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>"/>
      <div id="referredCultureFlagMessage" class="blank">&nbsp;</div>
   </td>
  </tr>
  <tr> 
    <td width="121">
	       <bean:message key="sampleitem.typeOfSample"/>:<span class="requiredlabel">*</span>
	</td>
    <td width="212"> 
    	 <app:text name="<%=formName%>" property="typeOfSampleDesc" styleId="typeOfSampleDesc" styleClass="text" size="25" maxlength="30" onblur="formatHSEField(this, true);validateSampleType();" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>"/>
         <div id="typeOfSampleDescMessage" class="blank">&nbsp;</div>
 	</td>
 	<%--bugzilla 1894
    <td width="109">
	       <bean:message key="sampleitem.sourceOfSample"/>:
	</td>
    <td width="215"> 
    	 <app:text name="<%=formName%>" property="sourceOfSampleDesc" styleId="sourceOfSampleDesc" styleClass="text" size="25" maxlength="30" onblur="formatHSEField(this, true);validateSampleSource();" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>"/>
          <div id="sourceOfSampleDescMessage" class="blank">&nbsp;</div>
     </td>
    <td width="52">
		<bean:message key="sampleitem.sourceOther"/>:
    </td>
    <td colspan="3"> 
    	<app:text name="<%=formName%>" property="sourceOther" styleId="sourceOther" styleClass="text" size="25" maxlength="40" onblur="formatHSEField(this, true);validateFieldCompareDE1(this);" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>"/>
	   <div id="sourceOtherMessage" class="blank">&nbsp;</div>
    </td>
    end bugzilla 1894--%>
  </tr>
  <tr> 
    <td width="121">
			<bean:message key="sample.receivedDate"/>:<span class="requiredlabel">*</span>
	         <font size="1"><bean:message key="humansampleone.date.additionalFormat" /></font>
    </td>
    <td width="212"> 
    		<%--received date will only display but cannot be updated on HSE II--%>
		<app:text name="<%=formName%>" property="receivedDateForDisplay" styleId="receivedDateForDisplay" size="20" styleClass="text" onkeyup="myCheckDate(this, event, false,false)" onblur="formatHSEField(this, false);myCheckDate(this, event, true,true)" disabled="true"/>
		<%--div id="receivedDateForDisplayMessage" class="blank">&nbsp;</div--%>
	</td>
    <td width="109">
			<bean:message key="sample.stickerReceivedFlag"/>:<span class="requiredlabel">*</span>
	</td>
    <td width="215"> 
    	<app:text name="<%=formName%>" property="stickerReceivedFlag" styleId="stickerReceivedFlag" size="4" maxlength="1" styleClass="text" onblur="formatHSEField(this, true);validateFieldCompareDE1(this);" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>"/>
	   	 <div id="stickerReceivedFlagMessage" class="blank">&nbsp;</div>
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
<%--hidden fields--%>
<%-- organizationLocalAbbreviation --%>
   		<input id="de1organizationLocalAbbreviation" type="hidden" name="de1organizationLocalAbbreviation"/>
	    <input id="de2organizationLocalAbbreviation" type="hidden" name="de2organizationLocalAbbreviation"/>
<%-- projectIdOrName --%>
   		<input id="de1projectIdOrName" type="hidden" name="de1projectIdOrName"/>
	    <input id="de2projectIdOrName" type="hidden" name="de2projectIdOrName"/>
	    <input type="hidden" name="de3projectIdOrName" id="de3projectIdOrName"/>
<%-- project2IdOrName --%>
   		<input id="de1project2IdOrName" type="hidden" name="de1project2IdOrName"/>
	    <input id="de2project2IdOrName" type="hidden" name="de2project2IdOrName"/>
	    <input type="hidden" name="de3project2IdOrName" id="de3project2IdOrName"/>
<%--providerLastName--%>
		<input type="hidden" name="de1providerLastName" id="de1providerLastName"/>
        <input type="hidden" name="de2providerLastName" id="de2providerLastName"/>
<%--providerFirstName--%>
		<input type="hidden" name="de1providerFirstName" id="de1providerFirstName"/>
    	<input type="hidden" name="de2providerFirstName" id="de2providerFirstName"/>
<%--providerWorkPhone--%>
   		<input type="hidden" name="de1providerWorkPhone" id="de1providerWorkPhone"/>
	    <input type="hidden" name="de2providerWorkPhone" id="de2providerWorkPhone"/>
<%--providerWorkPhoneExtension--%>
  		<input type="hidden" name="de1providerWorkPhoneExtension" id="de1providerWorkPhoneExtension"/>
        <input type="hidden" name="de2providerWorkPhoneExtension" id="de2providerWorkPhoneExtension"/>
<%--lastName--%>
   		<input type="hidden" name="de1lastName" id="de1lastName"/>
	    <input type="hidden" name="de2lastName" id="de2lastName"/>
<%--firstName--%>
  		<input type="hidden" name="de1firstName" id="de1firstName"/>
	    <input type="hidden" name="de2firstName" id="de2firstName"/>
<%--middleName--%>
   		<input type="hidden" name="de1middleName" id="de1middleName"/>
	    <input type="hidden" name="de2middleName" id="de2middleName"/>
<%--streetAddress--%>
   		<input type="hidden" name="de1streetAddress" id="de1streetAddress"/>
	    <input type="hidden" name="de2streetAddress" id="de2streetAddress"/>
<%--chartNumber--%>
   		<input type="hidden" name="de1chartNumber" id="de1chartNumber"/>
	    <input type="hidden" name="de2chartNumber" id="de2chartNumber"/>
<%--city--%>
       	<input type="hidden" name="de1city" id="de1city"/>
       	<input type="hidden" name="de2city" id="de2city"/>
       	<%--need 3rd hidden field for ajax to retrieve old value--%>
       	<input type="hidden" name="de3city" id="de3city"/>
        <html:hidden property="cityId" name="<%=formName%>" styleId="cityId"/>
<%--state--%>
        <input type="hidden" name="de1state" id="de1state"/>
        <input type="hidden" name="de2state" id="de2state"/>
 
<%--zipCode--%>
 	   <input type="hidden" name="de1zipCode" id="de1zipCode"/>
       <input type="hidden" name="de2zipCode" id="de2zipCode"/>
           
<%--multipleUnit--%>
   		<input type="hidden" name="de1multipleUnit" id="de1multipleUnit"/>
	    <input type="hidden" name="de2multipleUnit" id="de2multipleUnit"/>
<%--externalId--%>
		<input type="hidden" name="de1externalId" id="de1externalId"/>
		<input type="hidden" name="de2externalId" id="de2externalId"/>
<%--gender--%>
	    <input type="hidden" name="de1gender" id="de1gender"/>
	    <input type="hidden" name="de2gender" id="de2gender"/>
<%--birthDateForDisplay--%>
   	   <input type="hidden" name="de1birthDateForDisplay" id="de1birthDateForDisplay"/>
   	   <input type="hidden" name="de2birthDateForDisplay" id="de2birthDateForDisplay"/>
<%--collectionDateForDisplay--%>
	 <input type="hidden" name="de1collectionDateForDisplay" id="de1collectionDateForDisplay"/>
	 <input type="hidden" name="de2collectionDateForDisplay" id="de2collectionDateForDisplay"/>
<%--collectionTimeForDisplay--%>
	 <input type="hidden" name="de1collectionTimeForDisplay" id="de1collectionTimeForDisplay"/>
	 <input type="hidden" name="de2collectionTimeForDisplay" id="de2collectionTimeForDisplay"/>
<%--clientReference--%>
	  <input type="hidden" name="de1clientReference" id="de1clientReference"/>
	  <input type="hidden" name="de2clientReference" id="de2clientReference"/>
<%--referredCultureFlag--%>
	  <input type="hidden" name="de1referredCultureFlag" id="de1referredCultureFlag"/>
	 <input type="hidden" name="de2referredCultureFlag" id="de2referredCultureFlag"/>
<%--typeOfSampleDesc--%>
	   <input type="hidden" name="de1typeOfSampleDesc" id="de1typeOfSampleDesc"/>
	   <input type="hidden" name="de2typeOfSampleDesc" id="de2typeOfSampleDesc"/>
	   <input type="hidden" name="de3typeOfSampleDesc" id="de3typeOfSampleDesc"/>
        <html:hidden property="typeOfSampleId" name="<%=formName%>" styleId="typeOfSampleId"/>	    
<%--sourceOfSampleDesc--%>	    
	 	   <input type="hidden" name="de1sourceOfSampleDesc" id="de1sourceOfSampleDesc"/>
		   <input type="hidden" name="de2sourceOfSampleDesc" id="de2sourceOfSampleDesc"/>
		   <input type="hidden" name="de3sourceOfSampleDesc" id="de3sourceOfSampleDesc"/>
           <html:hidden property="sourceOfSampleId" name="<%=formName%>" styleId="sourceOfSampleId"/>
<%--sourceOther--%>
	 	   <input type="hidden" name="de1sourceOther" id="de1sourceOther"/>
		   <input type="hidden" name="de2sourceOther" id="de2sourceOther"/>
<%--receivedDateForDisplay--%>
	 	   <input type="hidden" name="de1receivedDateForDisplay" id="de1receivedDateForDisplay"/>
		   <input type="hidden" name="de2receivedDateForDisplay" id="de2receivedDateForDisplay"/>
<%--stickerReceivedFlag--%>
	 	   <input type="hidden" name="de1stickerReceivedFlag" id="de1stickerReceivedFlag"/>
		   <input type="hidden" name="de2stickerReceivedFlag" id="de2stickerReceivedFlag"/>
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
  <%-- bugzilla 1545 remove city autocomplete
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
  
  <%--bugzilla 1894 
  <ajax:autocomplete
  source="sourceOfSampleDesc"
  target="sourceOfSampleId"
  baseUrl="ajaxAutocompleteXML"
  className="autocomplete"
  parameters="sourceOfSampleDesc={sourceOfSampleDesc},domain={domain},provider=SampleSourceAutocompleteProvider,fieldName=description,idName=id"
  minimumCharacters="1"
  postFunction="sampleSourceFocus"
   />
  end bugzilla 1894--%>
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
<app:javascript formName="humanSampleTwoForm" staticJavascript="true"/>

