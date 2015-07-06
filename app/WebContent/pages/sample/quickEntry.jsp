<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="us.mn.state.health.lims.common.action.IActionConstants" %>
<%@ page import="us.mn.state.health.lims.common.util.SystemConfiguration" %>   

<%@ page import="java.util.Calendar"  %>
<%@ page import="us.mn.state.health.lims.common.util.DateUtil" %>

<%@ taglib uri="/tags/struts-bean"		prefix="bean" %>
<%@ taglib uri="/tags/struts-html"		prefix="html" %>
<%@ taglib uri="/tags/struts-logic"		prefix="logic" %>
<%@ taglib uri="/tags/labdev-view"		prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<bean:define id="formName"		value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="idSeparator"	value='<%= SystemConfiguration.getInstance().getDefaultIdSeparator() %>' />
<%--bugzilla 1387 --%>
<bean:define id="genericDomain" value='' />

<%--bugzilla 1510 add styleId for compatibility in firefox and for use of firebug debugger--%>
<%--bugzilla 1813 add batch QE functionality--%>
<%--bugzilla 1979 using SampleStatusValidationProvider to validate batch range status--%>
<bean:define id="expectedStatus" value='<%= SystemConfiguration.getInstance().getSampleStatusLabelPrinted() %>' />
<%--bugzilla 2528 get newborn sample type--%>
<bean:define id="newbornSampleType" value='<%= SystemConfiguration.getInstance().getNewbornTypeOfSample() %>' />

<%
// bugzilla 2151
String saveDisabled = (String)request.getAttribute(IActionConstants.SAVE_DISABLED);
String addDisabled = (String)request.getAttribute(IActionConstants.ADD_DISABLED);
String lbLoadMessage = "";

//AIS - bugzilla 1463
Calendar cal = Calendar.getInstance();
int currentYear = cal.get(Calendar.YEAR);

//bugzilla 1979 (lightbox for 2 confirm continue messages)
java.util.Locale locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
lbLoadMessage =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "common.loading.message");
%>

<%--AIS - bugzilla 1463--%>
<input id="currYear" name="currYear" type="hidden" value=<%=currentYear%> />

<script language="JavaScript1.2">
//bugzilla 1979
var lBox;
var formFieldArray = new Array('accessionNumber', 
                               'accessionNumber2',
                               'receivedDateForDisplay',
                               'typeOfSampleDesc',
                               'sourceOfSampleDesc',
                               'sourceOther'
                               );
var formFieldsValidArray = new Array(true, 
                                     true,
									 true, 
									 true, 
									 true,
                                     true);
var formFieldsRequiredArray = new Array(true,
                                        false,
										true, 
										true, 
										false, //AIS - bugzilla 1396
                                        false);
//bugzilla 1397 renamed to prePageOnLoad so this is called before actionError pops up errors					
function prePageOnLoad()
{
    var accessionNumber = $("accessionNumber");
    accessionNumber.focus();

    
    
    //AIS - bugzilla 1397 - Start
    if (getSelectedTestIds() && getSelectedTestIds() != '')
	{		
		var thisForm = document.forms[0];
		//then load both lists from parentform
		var idObj = getSelectedTestIds();
		
		var idObjN = getSelectedTestNames();	
		
		var listOfIds = idObj.value; 
		var listOfNames = idObjN.value; 		
		  
		var slIdArr = new Array();
		var slIdArrN = new Array();
		
		//trim leading ;
		if (listOfIds.indexOf(';') == 0) 
		{
			listOfIds = listOfIds.substring(1);
			listOfNames = listOfNames.substring(1);
		}
		
		slIdArr = listOfIds.split(';');		
		slIdArrN = listOfNames.split(';');    

		thisForm.assignedTests.options.length = 0;
		
		if (slIdArr && slIdArr.length > 0 && slIdArr[0] != '') 
		{
		
			for (var i =0; i< slIdArr.length; i++) 
			{
 				thisForm.assignedTests.options[i] = new Option(slIdArrN[i], slIdArr[i]);
			}
         
		    if (thisForm.assignedTests.options.length > 0)
		    {
		    	thisForm.assignedTests.size = thisForm.assignedTests.options.length;
		    	thisForm.assignedTests.visibility = 'visible';
		    }
		    else
		    {
		    	thisForm.assignedTests.size = 0;
		    	thisForm.assignedTests.visibility = 'none';
		    }			
		}  
	}      
    //AIS - bugzilla 1397 - End
}

// Get array index for a particular field on form
function getFieldIndex(field)
{
	var i;
	for (i = 0; i < formFieldArray.length; i++)
	{
		if (formFieldArray[i] == field) 
		{
			break;
		}
	}
	return i;
}

// returns true or false 
function isFieldValid(fieldname)
{
	var i;
	for (i = 0; i < formFieldArray.length; i++) 
	{
		if (formFieldArray[i] == fieldname) 
		{
			break;
		}
	}
	return formFieldsValidArray[i];
}

// returns true or false 
function isFieldRequired(field)
{
	var i;
	for (i = 0; i < formFieldArray.length; i++) 
	{
		if (formFieldArray[i] == field) 
		{
			break;
		}
	}
	return formFieldsRequiredArray[i];
}

function setFieldInvalid(field)
{
	var index = getFieldIndex(field);
	formFieldsValidArray[index] = false;
}

function setFieldValid(field) 
{
	var index = getFieldIndex(field);
	formFieldsValidArray[index] = true;
}

//disable or enable save button based on validity of fields - if disabling save focus cursor on first field in error
function setSave() 
{
	//disable or enable save button based on validity of fields
	var obj = document.forms[0].save; 
	obj.disabled = false;
	for (var i = 0; i < formFieldsValidArray.length; i++) 
	{
    	if (formFieldsValidArray[i] == false) 
    	{
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

function isSaveEnabled() 
{
	var enabled = true;
	for (var i = 0; i < formFieldsValidArray.length; i++) 
	{
    	if (formFieldsValidArray[i] == false) 
    	{
			enabled = false;
			break;
		}
	}
	return enabled;
}

// This is for the ADD TEST functionality
function addTestPopup(form) 
{
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

	// Get the sessionID
	var sessionid = '';
	var sessionIndex = form.action.indexOf(';');
	if (sessionIndex >= 0)
	{
		var queryIndex = form.action.indexOf('?');
		var length = form.action.length;
		if (queryIndex > sessionIndex) 
		{
			length = queryIndex;
		}
		sessionid = form.action.substring(sessionIndex,length);
	}
	var href = context+"/QuickEntryAddTestPopup.do"+sessionid;
	//alert("href "+ href);	
	createPopup(href, 880, 500);
}

//This is for the ADD TEST functionality
function setAddTestResults(addTestForm)
{
	var popupPickListOptions = addTestForm.PickList.options;
	var thisForm = document.forms[0];
    //initialize
    document.forms[0].selectedTestIds.value = '';
    //AIS - bugzilla 1397    
    document.forms[0].selectedTestNames.value = '';
    
    thisForm.assignedTests.options.length = 0;
    for (var i = 0; i < popupPickListOptions.length; i++) 
    { 
         document.forms[0].selectedTestIds.value += '<%=idSeparator%>';
         document.forms[0].selectedTestIds.value += popupPickListOptions[i].value;
         
		//AIS - bugzilla 1397
         document.forms[0].selectedTestNames.value += '<%=idSeparator%>';
         document.forms[0].selectedTestNames.value += popupPickListOptions[i].text;         
         
         thisForm.assignedTests.options[i] = new Option(popupPickListOptions[i].text, popupPickListOptions[i].value);
         //bugzilla 1856
         thisForm.assignedTests.options[i].sortFieldA = popupPickListOptions[i].sortFieldA;
         thisForm.assignedTests.options[i].sortFieldB = popupPickListOptions[i].sortFieldB;
    }
    if (thisForm.assignedTests.options.length > 0)
    {
    	thisForm.assignedTests.size = thisForm.assignedTests.options.length;
    	thisForm.assignedTests.visibility = 'visible';
    }
    else
    {
    	thisForm.assignedTests.size = 0;
    	thisForm.assignedTests.visibility = 'none';
    }
    //alert("selectedTestIds:"+selectedTestIds);
    //clear this so that we can add more tests (button is enabled again)
    //clearAddTestClicked();
    
    //bugzilla 1856
    sortOrder = 'sortFieldB';
    sort('assignedTests', null, false);
}

function getSelectedTestIds()
{
  return document.forms[0].selectedTestIds;
}

//AIS - bugzilla 1397 
function getSelectedTestNames()
{
  return document.forms[0].selectedTestNames;
}

//END This is for the ADD TEST functionality


function submitTheForm(form)
{
	//alert("in submitTheForm: before setAction");
	setAction(form, 'Update', 'yes', '?ID=');
	//alert("in submitTheForm: after setAction");
}


function doFocus(field, nextField)
{
	var fieldMessage = field + 'Message';
	var mdiv = $(fieldMessage);
	var focusField = $(field);
	focusField.focus();
	//clear out badmessage that occurs on onblur before selection is made in Ajax drop down (this is a premature badmessage)
	if (mdiv.className == "badmessage") 
	{
		mdiv.className = "blank";
		setFieldValid(field);
		//focus on next field
		var nextF = $(nextField);
		nextF.focus();
	} 
}

function accessionNumberFocus()
{
	var field = 'accessionNumber';
	nextField = 'receivedDateForDisplay';
	doFocus(field, nextField);
}

function receivedDateFocus()
{
	var field = 'receivedDateForDisplay';
	nextField = 'typeOfSampleDesc';
	doFocus(field, nextField);
}

function sampleTypeFocus()
{
	var field = 'typeOfSampleDesc';
	nextField = 'sourceOfSampleDesc';
	doFocus(field, nextField);
}

function sampleSourceFocus()
{ 
	var field = 'sourceOfSampleDesc';
	nextField = 'sourceOther';
	doFocus(field, nextField);
}

//bugzilla 1778
function sourceOtherFocus()
{ 
	var field = 'sourceOther';
	nextField = 'accessionNumber';
	doFocus(field, nextField);
}

//bugzilla 1494 removed validations that can be done through validation.xml
function validateForm(form)
{
    var validated = validateQuickEntryForm(form);
	return validated;
}

//This is for validating individual fields: accession #, received date, sample type, sample source
function setMessage(message, field) 
{
    //alert("I am in in setMessage - reg. validation - with message and field " + message + " " + field);
	var fieldMessage = field + "Message";
	var mdiv = $(fieldMessage);
	idField = $(field);
     
	if (message == "invalid") 
	{
		//some fields are not required
		//AIS - bugzilla 1463
		if (idField.value == "" && !isFieldRequired(field) && (idField.name != 'accessionNumber')) 
		{
			mdiv.className = "blank";
		}
		else
		{
			mdiv.className = "badmessage";
			setFieldInvalid(field);
		}
 	}
	else
	{
		mdiv.className = "blank";
		setFieldValid(field);
		//this is to correct case according to a value found in database
		
		//AIS - bugzilla 1395	
		if ( field != "accessionNumber" && field != "accessionNumber2") {
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

//AIS - bugzilla 1463 Add modifications to this function
function validateAccessionNumber(field) {
	if((field.value.length < 10)){	
	  var myMessage = "invalid";
      //fix error in firefox - no AccessionNumber element
	  var myField = field.name;		
	  setMessage(myMessage, myField);		
	  setSave();	
	}else{	
	  var Accession = field.value;
	  var strCheck = '1234567890'; 
	  var fieldMessage = field.name + "Message";
	  //alert("fieldMessage "+ fieldMessage);
	  var mdiv = $(fieldMessage);	  	  
	  if (  
		strCheck.indexOf(Accession.substring(1,2)) < 0 ||
		strCheck.indexOf(Accession.substring(2,3)) < 0 ||
		strCheck.indexOf(Accession.substring(3,4)) < 0 ||
		strCheck.indexOf(Accession.substring(5,6)) < 0 ||
		strCheck.indexOf(Accession.substring(6,7)) < 0 ||
		strCheck.indexOf(Accession.substring(7,8)) < 0 ||
		strCheck.indexOf(Accession.substring(9,10)) < 0  
  		){  
			mdiv.className = "badmessage";
			setFieldInvalid(field.name);  
		}else{			  
	    	var cal = document.forms[0].currYear.value;	
		 	if (Accession.substring(0,4) == cal ){	
				mdiv.className = "blank";		      
				setFieldValid(field);
				setSave();     
                new Ajax.Request (
                                 'ajaxXML',  //url
                                 {//options
                                    method: 'get', //http method
                                    parameters: 'provider=QuickEntryAccessionNumberValidationProvider&field=' + field.name + '&id=' + escape($F(field.name)),      //request parameters
                                    //indicator: 'throbbing'
                                    onSuccess:  processSuccess,
                                    onFailure:  processFailure
                                 }
                              );
      		}else{			          
		      	mdiv.className = "badmessage";
		  		setFieldInvalid(field.name);			          
			}          
		}		 			
	}

}

function validateAccessionNumber2(field) {
  //bugzilla 2263  
  if (field.value.length == 0) {
      setMessage("valid", field.name);
      setSave();
      return;
  }  
  //only validate if not blank (this is not a required field)
	if ($F("accessionNumber2") != null && $F("accessionNumber2")!= '') {
	  //if applicable check to make sure 2nd accessionNumber is > first
	    if ($F("accessionNumber") == null || $F("accessionNumber") == '' || $F("accessionNumber2") <= $F("accessionNumber")) {
	      setMessage("invalid", field.name);		
	      setSave();
	      return;	
	    }
	
	if((field.value.length < 10)){	
	  var myMessage = "invalid";
      //fix error in firefox - no AccessionNumber element
	  var myField = field.name;		
	  setMessage(myMessage, myField);		
	  setSave();	
	}else{	
	  var Accession = field.value;
	  var strCheck = '1234567890'; 
	  var fieldMessage = field.name + "Message";
	  //alert("fieldMessage "+ fieldMessage);
	  var mdiv = $(fieldMessage);	  	  
	  if (  
		strCheck.indexOf(Accession.substring(1,2)) < 0 ||
		strCheck.indexOf(Accession.substring(2,3)) < 0 ||
		strCheck.indexOf(Accession.substring(3,4)) < 0 ||
		strCheck.indexOf(Accession.substring(5,6)) < 0 ||
		strCheck.indexOf(Accession.substring(6,7)) < 0 ||
		strCheck.indexOf(Accession.substring(7,8)) < 0 ||
		strCheck.indexOf(Accession.substring(9,10)) < 0  
  		){  
			mdiv.className = "badmessage";
			setFieldInvalid(field.name);  
		}else{			  
	    	var cal = document.forms[0].currYear.value;	
		 	if (Accession.substring(0,4) == cal ){	
				mdiv.className = "blank";		      
				setFieldValid(field);
				setSave();     
                new Ajax.Request (
                                 'ajaxXML',  //url
                                 {//options
                                    method: 'get', //http method
                                    parameters: 'provider=QuickEntryAccessionNumberValidationProvider&field=' + field.name + '&id=' + escape($F(field.name)),      //request parameters
                                    //indicator: 'throbbing'
                                    onSuccess:  processSuccess,
                                    onFailure:  processFailure
                                 }
                              );
      		}else{			          
		      	mdiv.className = "badmessage";
		  		setFieldInvalid(field.name);			          
			}          
		}		 			
	}
  }
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
}

function validateSampleSource() {
	/*AIS - bugzilla 1396
      Added if constraint-- to check if it is invalid, only when it is filled in
    */
    if ($F("sourceOfSampleDesc") != "" ) {
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
    }
}

function myCheckDate(date, event, dateCheck, onblur) 
{
	var messageDiv = date.name + 'Message';
	var mdiv = $(messageDiv);
	var validDate = DateFormat(date,date.value,event,dateCheck,'1');
	if (dateCheck) 
	{ 
		if (validDate) 
		{
			var validDate2 = lessThanCurrent(date);
			if (validDate2) 
			{
				mdiv.className = "blank";
				setFieldValid(date.name);
			}
			else
			{
				mdiv.className = "badmessage";
				setFieldInvalid(date.name);
			}
		}
		else
		{
			mdiv.className = "badmessage";
			setFieldInvalid(date.name);
		}
		setSave();
	}
}

function setMyCancelAction(form, action, validate, parameters) 
{   
	//first turn off any further validation
	setAction(window.document.forms[0], 'Cancel', 'no', '');	
}


//DIANE ADDING FUNCTIONS FOR bugzilla 1979
//added this for 1895 to get loading message from MessageResource.properties
function getLbLoadMessage() {
  return '<%=lbLoadMessage%>';
}


//bugzilla 1979 made checkConfirmExit() for no tests added into a lightbox
function checkConfirmExit(replaceLightBox)
{
   var selectedTestIds = getSelectedTestIds();
   
   //if tests have been selected go ahead and save without confirm exit window
   if ( selectedTestIds.value != '') {
      submitTheForm(window.document.forms[0]);
   }
   
   //if no tests have been added and no errors otherwise on page -> go to confirm exit popup
   if (( selectedTestIds.value == '') && isSaveEnabled() == true) {
	 
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
		 'pages/sample/quickEntryConfirmExitLightbox.jsp',
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
     lBox = new lightbox('pages/sample/quickEntryConfirmExitLightbox.jsp', '');    
     var myLightBox = Object.extend(lBox, customLightBox);
     myLightBox.activate();
    }
   
   } else {
     return false;
   }
    
}

//bugzilla 1979 - coming from checkConfirmContinueWithInvalidStatusInRange
function saveItToLightBoxParentForm() {
   //after checking sample statuses - now check whether tests have been assigned
   checkConfirmExit(true);
}

function saveItToParentForm(form) {
 submitTheForm(form);
}

//bugzilla 1979 using lightbox
function checkConfirmContinueWithInvalidStatusInRange(form) {
     //bugzilla 1979 extend lightbox loadInfo function to do what we need for this page
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
		 'pages/sample/quickEntryConfirmInvalidStatusInRangeLightbox.jsp',
		 {method: 'post', parameters: '', onComplete: this.processInfo.bindAsEventListener(this)}
	     );
        }
 
      };

     lBox = new lightbox('pages/sample/quickEntryConfirmInvalidStatusInRangeLightbox.jsp', '');    
     var myLightBox = Object.extend(lBox, customLightBox);
     myLightBox.className = 'lightbox';
     myLightBox.activate();       

}

function processAjaxResultForSampleStatusInRange(xhr) {
  var message = xhr.responseXML.getElementsByTagName("message")[0];
  var formfield = xhr.responseXML.getElementsByTagName("formfield")[0];
  //set hidden variable validSampleStatusInRange
  var msg =  message.childNodes[0].nodeValue;
  var fld =  formfield.childNodes[0].nodeValue;
  //we can submit form now if the combination is valid
  if (msg == "valid") {
     checkConfirmExit(false);
  } else {
     checkConfirmContinueWithInvalidStatusInRange(window.document.forms[0]);
  }
}

function checkValidSampleStatusInRange() {

//only do this check if there is a valid 2nd accesion number (batch Quick Entry) 
if ($F("accessionNumber2") != null && $F("accessionNumber2")!= '') {  
   new Ajax.Request (
            'ajaxXML',  //url
             {//options
              method: 'get', //http method
              parameters: 'provider=SampleStatusValidationProvider&field=accessionNumber&id=' + escape($F("accessionNumber")) + '&toId=' + escape($F("accessionNumber2")) +'&expectedStatus=' + escape(<%=expectedStatus%>),      //request parameters
              //indicator: 'throbbing'
              onSuccess:  processAjaxResultForSampleStatusInRange,
              onFailure:  processFailure
             }
           );  
 } else {
   checkConfirmExit(false);
 }

}
//end bugzilla 1979

//bugzilla 2528
function processSampleType() {
    var sampleType = document.forms[0].typeOfSampleDesc.value;
    if ( sampleType == '<%=newbornSampleType%>' )
	    setAction(window.document.forms[0], 'PopulateNewbornTest', 'no', '');
    
}
//end bugzilla 2528

</script>

<html:hidden property="selectedTestIds" name="<%=formName%>" styleId="selectedTestIds"/>
<%--AIS - bugzilla 1397--%>
<html:hidden property="selectedTestNames" name="<%=formName%>" styleId="selectedTestNames"/>
<html:hidden property="currentDate" name="<%=formName%>" styleId="currentDate"/>
<%--bugzilla 1387 added domain--%>
<html:hidden property="domain" name="<%=formName%>" value="<%=genericDomain%>" styleId="domain"/>

<table width="100%">
	<%--bgm - bugzilla 1665 moved recieved.date here to be the first row above accn # --%>
	<tr> 
		<td width="15%">
			<bean:message key="quick.entry.received.date"/>:<span class="requiredlabel">*</span>
			<font size="1"><%=DateUtil.getDateUserPrompt()%></font>
		</td>
		<td width="85%"> 
			<app:text name="<%=formName%>" 
					  property="receivedDateForDisplay" 
					  onkeyup="myCheckDate(this, event, false,false);" 
					  onblur="myCheckDate(this, event, true, true);" 
					  size="25" 
					  styleClass="text" 
					  styleId="receivedDateForDisplay"/>
			<div id="receivedDateForDisplayMessage" class="blank">&nbsp;</div>
		</td>
	</tr>
	<tr>
		<td width="15%">
		  <table>
		   <tr>
		    <td>
			<bean:message key="quick.entry.accession.number"/>:<span class="requiredlabel">*</span>
			</td>
		   </tr>
		   <tr>
		    <td>&nbsp;</td>
		   </tr>
		  </table>
		</td>
		<td width="85%">
         <table>
		  <tr>
	    	<%--AIS:bugzilla 1463 added maxlength--%> 
		   <td>
			<app:text name="<%=formName%>" 
					  property="accessionNumber" 
					  onblur="validateAccessionNumber(this);" 
					  size="25" 
					  maxlength="10"
					  styleClass="text" 
					  styleId="accessionNumber"/>
			<div id="accessionNumberMessage" class="blank">&nbsp;</div>
		  </td>
		  <td style="float:left">
   		    <font size="1"><bean:message key="quick.entry.accession.number.thru"/></font>
   		    &nbsp;&nbsp;
   		  </td>
   		  <td style="float:left">
			<app:text name="<%=formName%>" 
					  property="accessionNumber2" 
					  onblur="validateAccessionNumber2(this);" 
					  size="25" 
					  maxlength="10"
					  styleClass="text" 
					  styleId="accessionNumber2"/>
			<div id="accessionNumber2Message" class="blank">&nbsp;</div>
		  </td>
		 </tr>
		 <tr>
		  <td>
		  &nbsp;
		  </td>
		  <td>
		  &nbsp;
		  </td>
		  <td valign="top" align="center">
		    <font size="1"><bean:message key="quick.entry.accession.number.batch.message"/></font>
		  </td>
		 </tr>
		</table>
	  </td>
	</tr>
	<tr> 
		<td width="15%">
			<bean:message key="quick.entry.sample.type"/>:<span class="requiredlabel">*</span>
		</td>
	    <td width="85%"> 
			<app:text name="<%=formName%>" 
					  property="typeOfSampleDesc" 
					  onblur="this.value=this.value.toUpperCase();validateSampleType();processSampleType();" 
					  size="25" 
					  styleClass="text" 
					  styleId="typeOfSampleDesc"/>
			<div id="typeOfSampleDescMessage" class="blank">&nbsp;</div>
			<html:hidden property="typeOfSampleId" name="<%=formName%>" styleId="typeOfSampleId"/>
		</td>
	</tr>
	<tr>
		<td width="15%">
			<bean:message key="quick.entry.sample.source"/>: <%--AIS - bugzilla 1396 ( removed required )--%>
		</td>
		<td width="85%"> 
			<app:text name="<%=formName%>" 
					  property="sourceOfSampleDesc" 
					  onblur="this.value=this.value.toUpperCase();validateSampleSource();" 
					  size="25" 
					  styleClass="text" 
					  styleId="sourceOfSampleDesc"/>
			<div id="sourceOfSampleDescMessage" class="blank">&nbsp;</div>
			<html:hidden property="sourceOfSampleId" name="<%=formName%>" styleId="sourceOfSampleId"/>
		</td>
	</tr>
    <%--bugzilla 1778--%>
	<tr>
		<td width="15%">
			<bean:message key="quick.entry.source.other"/>: <%--bugzilla 1778--%>
		</td>
		<td width="85%"> 
			<app:text name="<%=formName%>" 
					  property="sourceOther" 
					  onblur="this.value=this.value.toUpperCase();" 
					  size="40" 
					  styleClass="text" 
					  styleId="sourceOther"/>
		</td>
	</tr>
    <%--bugzilla 1751 move assign test button to above assigned tests--%>
	<tr>
		<td width="15%">
			&nbsp;
		</td>
		<td width="85%"> 
            <html:button onclick="addTestPopup(window.document.forms[0]);"
                            property="addTest" disabled="<%=Boolean.valueOf(addDisabled).booleanValue()%>" >
  		        <bean:message key="label.button.addTest"/>
  	        </html:button>  		  	      			
  		</td>
	</tr>
	<tr>
		<td width="15%" valign="top">
			<bean:message key="quick.entry.assigned.tests"/>:
		</td>
		<td width="85%">
			<select name="assignedTests" 
					id="assignedTests" 
					size="5" 
					multiple="multiple" 
					style="width: 200px" 
					readonly="true">
			</select>
		</td>
	</tr>	
</table>
   
<ajax:autocomplete source="sourceOfSampleDesc"
				   target="sourceOfSampleId"
				   baseUrl="ajaxAutocompleteXML"
				   className="autocomplete"
				   parameters="sourceOfSampleDesc={sourceOfSampleDesc},domain={domain},provider=SampleSourceAutocompleteProvider,fieldName=description,idName=id"
				   minimumCharacters="1"
				   postFunction="sampleSourceFocus" /> 
<ajax:autocomplete source="typeOfSampleDesc"
				   target="typeOfSampleId"
				   baseUrl="ajaxAutocompleteXML"
				   className="autocomplete"
				   parameters="typeOfSampleDesc={typeOfSampleDesc},domain={domain},provider=SampleTypeAutocompleteProvider,fieldName=description,idName=id"
				   minimumCharacters="1"
				   postFunction="sampleTypeFocus"  />  
				   
<%--bugzilla 1512 custom JavascriptValidator--%>
<app:javascript formName="quickEntryForm" staticJavascript="true"/>

