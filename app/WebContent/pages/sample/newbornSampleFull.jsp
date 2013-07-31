<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date, java.util.Locale, org.apache.struts.Globals,	
	us.mn.state.health.lims.common.action.IActionConstants, 
	us.mn.state.health.lims.common.util.SystemConfiguration" %>   

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>'/>
<bean:define id="expectedStatus" value='<%= SystemConfiguration.getInstance().getSampleStatusEntry2Complete() %>' />
<bean:define id="expectedDomain" value='<%= SystemConfiguration.getInstance().getNewbornDomain() %>' />

<%
	Locale locale = (Locale)request.getSession().getAttribute(Globals.LOCALE_KEY);
	String doc_dob = us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(locale,"newborn.sample.error.doc.dob");
	String dof_dob = us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(locale,"newborn.sample.error.dof.dob");
	String dot_dob = us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(locale,"newborn.sample.error.dot.dob");
	
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";

	String errorSelectCombo = us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(locale,"humansampleone.cityStateZipPopup.selectone.error");
	String gramKey = us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(locale,"page.default.gram.option");
	String poundKey = us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(locale,"page.default.pound.option");
%>

<script language="JavaScript1.2">
var expectedStatus = '<%=expectedStatus%>';
var expectedDomain = '<%=expectedDomain%>';
var lBox;

function pageOnLoad() {
   	var accnNumb = $("accessionNumber");
   	var gestationalWeek = $("gestationalWeek");
   	
   	var barcode = $("barcode");
   	if ( accnNumb.value.length == 0 ) {
   	    accnNumb.focus();  
   	   	setFields(accnNumb.name,true);
   	 	gestationalWeek.value = "";
   	} else {
       	setFields(accnNumb.name,false);
   	    barcode.focus();
   	}   
}

function setMyCancelAction(form, action, validate, parameters) {
 	setAction(window.document.forms[0], 'Cancel', 'no', '');
}

function validateForm(form) {
	var validated = validateNewbornSampleFullForm(form);
	
	if (validated) {    	
    	if ( checkForm(form) ) {
	        checkValidCityZipCodeCombination();
	    }	
	}
	return false;            
}

function checkForm(form) {
	var isOK = false;
	
    if ( form.birthWeight.value != null )  {
        if ( form.selectedBirthWeight.value == "<%=poundKey%>" ) {
            var temp = form.birthWeight.value / 0.0022046226218;
            var grams = Math.round(temp);
            form.birthWeight.value = grams;
            form.selectedBirthWeight.value = "<%=gramKey%>";
        } else {
            form.birthWeight.value = Math.round(form.birthWeight.value);
        }
        isOK = checkDate(form);
    }
    return isOK;
}

function setFields(fieldName, value) {
    var allElements = document.forms[0].elements;
    
    for(var i = 0; i< allElements.length; i++ ) {
        if ( value ) {
            allElements[i].setAttribute('disabled',value);
        } else {
            allElements[i].removeAttribute('disabled',value);
        }    
        if ( fieldName == allElements[i].name || 
            allElements[i].name == 'cancel' || 
            allElements[i].name == 'accessionNumber' ) {
            allElements[i].removeAttribute('disabled',true);
        }    
    }
}

function processFailure(xhr) {
}

function processSuccess(xhr) {
    var message = xhr.responseXML.getElementsByTagName("message")[0];
    var formfield = xhr.responseXML.getElementsByTagName("formfield")[0];
    setMessage(message.childNodes[0].nodeValue, formfield.childNodes[0].nodeValue);
}
    
function setMessage(message, field) {
    var fieldMessage = field + "Message";
    var mdiv = $(fieldMessage);
    var idField = $(field);
    
    if (message == "invalid") {
	    if ($F(field) == "" && (idField.name != 'accessionNumber')){
            mdiv.className = "blank";
            setFields(idField.name,false);
        } else {
            mdiv.className = "badmessage";
            setFields(idField.name,true);
        }
        if (idField.name == "submitterNumber") {
            var orgName = $("organizationName");
            orgName.innerHTML = "";
        }
    } else {
        mdiv.className = "blank";
        setFields(idField.name,false);
        if (message != "validStatus") {       
	        if (field != 'accessionNumber') {
                //this is to correct case according to a value found in database
		        if (message.length > 5) {
		            if (idField.name == "submitterNumber") {
		                var orgName = $("organizationName");
		                orgName.innerHTML = message.substring(5);
		            }
	            }        	                
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

function validateAccessionNumber(field) {
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
	 } else {		    
	    var myMessage = "invalid";
		var myField = "accessionNumber";		
		setMessage(myMessage, myField);
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
        setFields(fieldIn,true);
   		return;    
    } else {
        setFields(fieldIn,false);
        validateDomain(fieldIn);
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
        setFields(field.name,true);
    } else {
        mdiv.className = "blank";
        setFields(field.name,false);
        setAction(window.document.forms[0], 'View', 'no', ''); 
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
                    setFields(date.name,false);
                } else {
                    mdiv.className = "badmessage";
                    setFields(date.name,true);
                }
            } else {
                mdiv.className = "badmessage";
                setFields(date.name,true);
            }
        }
    }
}

function myCheckTime(time) {
    var fieldMessage = time.name + 'Message';
    var mdiv = $(fieldMessage);
    
    if (checkTime(time)) {
       mdiv.className = "blank";
       setFields(time.name,false);
    } else {
       mdiv.className = "badmessage";
       setFields(time.name,true);
    }
}

function checkBirthWeight(weight) {
 	var field = weight.value;
 	var fieldMessage = weight.name + 'Message';
 	var mdiv = $(fieldMessage);

    if ( isNaN(field) ) {	
	    mdiv.className = "badmessage";
	   	setFields(weight.name,true);
    } else {
        if ( field < 0 ) {
            mdiv.className = "badmessage";
	   	    setFields(weight.name,true);
        } else {
		    mdiv.className = "blank";
	   	    setFields(weight.name,false);
   	    }    
	}	
}

function convertWeight(field) {
    var weight = document.forms[0].birthWeight.value;
              
    if ( field.value.length==0 ) {
        document.forms[0].selectedBirthWeight.value = "<%=gramKey%>";    
    } else {    
        if ( (field.value == '<%=gramKey%>') && (weight.length > 0) ) {
            var temp = weight / 0.0022046226218;
            var grams = Math.round(temp);
            document.forms[0].birthWeight.value = grams;
        } else {
            if (weight.length > 0) { 
                var temp = weight * 0.0022046226218;
                var pounds = Math.round(temp);
                document.forms[0].birthWeight.value = pounds;
            }
        }
    }            
}
   
function formatField(field, toUpper) {
    if (field.value != null && field.value != '') {
        field.value = trim(field.value);
        field.value = field.value.replace(/\s+/g,' ');
        if (toUpper == true) {
            field.value = field.value.toUpperCase();
        }
    }
}

function checkNumber(fieldIn) {
 	var field = fieldIn.value;
 	var fieldMessage = fieldIn.name + 'Message';
 	var mdiv = $(fieldMessage);

    if ( isNaN(field) ) {	
	    mdiv.className = "badmessage";
	   	setFields(fieldIn.name,true);
    } else {
        if ( field < 0 ) {
            mdiv.className = "badmessage";
	   	    setFields(fieldIn.name,true);
        } else {
		    mdiv.className = "blank";
	   	    setFields(fieldIn.name,false);
   	    }    
	}	
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
	        strCheck.indexOf(phone.substring(12,13)) < 0 ) {
	      
	        mdiv.className = "badmessage";
	        setFields(field.name,true);
	    } else {
	        mdiv.className = "blank";
	        setFields(field.name,false);
	    }  
    } else {
	    mdiv.className = "blank";
	    setFields(field.name,false);
    }
}

function validateCity() {
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

function validateZipCode() {   
    var idField = $("zipCode");
    var zip = $F("zipCode");
        
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
        for (i = 0; i < zip.length; i++) { 
            newZip += zip.charAt(i); 
            if (i == 4) { 
                newZip += '-';
            }
        }   
        document.forms[0].zipCode.value = newZip;
    }
}

function validateOrganizationLocalAbbreviation() {
    var idField = $("submitterNumber");
    
    new Ajax.Request (
           'ajaxXML',  //url
             {//options
              method: 'get', //http method
              parameters: 'provider=OrganizationLocalAbbreviationValidationProvider&form=' + document.forms[0].name +'&field=submitterNumber&id=' + escape($F("submitterNumber")),      //request parameters
              //indicator: 'throbbing'
              onSuccess:  processSuccess,
              onFailure:  processFailure
             }
          );
}

function display(field) {
	var accNbr = document.forms[0].accessionNumber.value;
	
    if ( accNbr.length == 10 ) {
        setAction(window.document.forms[0], 'View', 'no', '');                
    }
}

function checkMultiple(fieldIn) {
 	var field = fieldIn.value;
 	var fieldMessage = fieldIn.name + 'Message';
 	var mdiv = $(fieldMessage);

 	checkYesNo(fieldIn);
 	
    if ( field == '<%=IActionConstants.YES%>' ) {
        document.forms[0].birthOrder.disabled=false;	
    } else {
    	document.forms[0].birthOrder.disabled=true;
    }   
}

function checkYesNo(fieldIn) {
 	var field = fieldIn.value;
 	var fieldMessage = fieldIn.name + 'Message';
 	var mdiv = $(fieldMessage);

 	if ( field == '<%=IActionConstants.YES%>' || field == '<%=IActionConstants.NO%>' || field.length == 0 ) {
     	mdiv.className = "blank";
	   	setFields(fieldIn.name,false);
    } else {	   	   	
	    mdiv.className = "badmessage";
	   	setFields(fieldIn.name,true);
    }
}

function checkDate(form) {
	var dob = form.birthDateForDisplay.value;
	var doc = form.collectionDateForDisplay.value;
	var dof = form.dateFirstFeedingForDisplay.value;
	var dot = form.dateTransfutionForDisplay.value;
	
	var d1 = new Date(dob.split('/')[2],dob.split('/')[1],dob.split('/')[0]);
	var d2 = new Date(doc.split('/')[2],doc.split('/')[1],doc.split('/')[0]);
	var d3 = new Date(dof.split('/')[2],dof.split('/')[1],dof.split('/')[0]);
	var d4 = new Date(dot.split('/')[2],dot.split('/')[1],dot.split('/')[0]);
	
	if ( doc.length > 0 ) {
	    if ( d2.getTime() < d1.getTime() ) {
            alert('<%=doc_dob%>');
            form.collectionDateForDisplay.focus();
            return false;	
        }	
    }    
    if ( dof.length > 0 ) {
	    if ( d3.getTime() < d1.getTime() ) {
            alert('<%=dof_dob%>');
            form.dateFirstFeedingForDisplay.focus();
            return false;	
        }
    }
    if ( dot.length > 0 ) {
        if ( d4.getTime() < d1.getTime() ) {
            alert('<%=dot_dob%>');
            form.dateTransfutionForDisplay.focus();
            return false;	
        }	        
    }        
	return true;
}

function validateBirthOrder(fieldIn) {
    var field = fieldIn.value;
 	var fieldMessage = fieldIn.name + 'Message';
 	var mdiv = $(fieldMessage);
 	
    if ( field.length > 0 ) {
        if ( field > 10 ) {
            mdiv.className = "badmessage";
	   	    setFields(fieldIn.name,true);    
        }    
    }
}

function checkConfirmExit(replaceLightBox) {
   	var collectionDate = $("collectionDateForDisplay");
   	collectionDate = $F("collectionDateForDisplay");
       
    if ( collectionDate != '' && collectionDate !=null && collectionDate != undefined) {
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
    if ($("zipCode") != '') {
        zip = $F("zipCode");
    }
 
    var state = '';
    if ($("state") != '') {
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

function saveItToLightBoxParentForm() {
    var city, state, zip;
    var selCombo;
    var selectedCombo = humanSampleCityStateZipPopupForm.elements['selectedCombo'];
   
    if ( selectedCombo.length == undefined ) {
        if ( selectedCombo.checked ) {    
            selCombo = "0"
            document.forms[0].city.value = humanSampleCityStateZipPopupForm.elements['city' + selCombo].value;
            document.forms[0].state.value = humanSampleCityStateZipPopupForm.elements['state' + selCombo].value;
            document.forms[0].zipCode.value = humanSampleCityStateZipPopupForm.elements['zipCode' + selCombo].value;
            //clear out the error divs 
            $("cityMessage").className = "blank";
            $("stateMessage").className = "blank";
            $("zipCodeMessage").className = "blank";
        }
    } else {    
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
    }
       
    if (selCombo != null) {
        checkConfirmExit(true);
    } else {
        alert('<%=errorSelectCombo%>');
    }
}

function submitTheForm(form) {
   setAction(form, 'Update', 'no', '');	
}

function checkGender(fieldIn) {
 	var field = fieldIn.value;
 	var fieldMessage = fieldIn.name + 'Message';
 	var mdiv = $(fieldMessage);

 	if ( field.length > 0 ) {
		if ( field != 'M' && field != 'F' && field != 'U' ) {
			mdiv.className = "badmessage";
	   	    setFields(fieldIn.name,true); 	
		} else {
			mdiv.className = "blank";
			setFields(fieldIn.name,false);
		}
 	} else {
        mdiv.className = "blank";
		setFields(fieldIn.name,false); 	
    }		
}

function saveItToParentForm() {
    submitTheForm(window.document.forms[0]);
}

</script>

<table border="0">
    <tr>
	    <td>
	        <bean:message key="sample.accessionNumber"/>:<span class="requiredlabel">*</span>
	    </td>	
        <td> 
		    <html:text name="<%=formName%>" property="accessionNumber" styleId="accessionNumber" styleClass="text" onblur="validateAccessionNumber(this);" size="20" maxlength="10"/>
			<div id="accessionNumberMessage" class="blank" >&nbsp;</div>
        </td>        
        <td>
            <bean:message key="newborn.sample.full.barcode"/>:<span class="requiredlabel">*</span>
        </td>
        <td>
            <html:text name="<%=formName%>" property="barcode" styleId="barcode" styleClass="text" onblur="checkNumber(this);" size="20" maxlength="11"/>
			<div id="barcodeMessage" class="blank">&nbsp;</div>
        </td>
		<td colspan="2">
			<html:button onclick="display(this);" property="exit">
				<bean:message key="label.button.display" />
			</html:button>
		</td>       
    </tr>
    <tr>    
        <td>
            <bean:message key="newborn.sample.full.medical.record"/>:     
        </td>
        <td>
            <html:text name="<%=formName%>" property="medicalRecordNumber" styleId="medicalRecordNumber" styleClass="text" onblur="formatField(this,true);" size="10" maxlength="18"/>
        </td>
        <td>
            <bean:message key="newborn.sample.full.y.number"/>:     
        </td>
        <td colspan="3">
            <html:text name="<%=formName%>" property="ynumber" styleId="ynumber" styleClass="text" onblur="formatField(this,true);" size="20" maxlength="18"/>
        </td>
    </tr>
    <tr><td colspan="6">&nbsp;</td></tr>
</table>

<table border="0" width="100%">    
    <tr>
        <td colspan="8"> 
            <h2 align="left"><bean:message key="newborn.sample.full.infant.info"/></h2>
        </td>    
    </tr>
    <tr>
        <td>
            <bean:message key="newborn.sample.full.last.name"/>:<span class="requiredlabel">*</span>     
        </td>
        <td>
            <html:text name="<%=formName%>" property="lastName" styleId="lastName" styleClass="text" onblur="formatField(this,true);" size="20" maxlength="30"/>
        </td>
        <td>
            <bean:message key="newborn.sample.full.first.name"/>:<span class="requiredlabel">*</span>     
        </td>
        <td>
            <html:text name="<%=formName%>" property="firstName" styleId="firstName" styleClass="text" onblur="formatField(this,true);" size="20" maxlength="20"/>
        </td>
        <td>
            <bean:message key="newborn.sample.full.birth.date"/>:<span class="requiredlabel">*</span><br>
	        <font size="1"><bean:message key="humansampleone.date.additionalFormat"/></font>
        </td>
        <td>
            <app:text name="<%=formName%>" property="birthDateForDisplay" styleId="birthDateForDisplay" size="10" maxlength="10" onkeyup="myCheckDate(this, event, false,false)" onblur="myCheckDate(this, event, true, true)" styleClass="text"/>
	        <div id="birthDateForDisplayMessage" class="blank">&nbsp;</div>
        </td>
        <td>
            <bean:message key="newborn.time.of.birth"/>:<span class="requiredlabel">*</span><br>
            <font size="1"><bean:message key="humansampleone.time.additionalFormat"/></font>
        </td>		
        <td>
            <app:text name="<%=formName%>" property="birthTimeForDisplay" styleId="birthTimeForDisplay" size="5" maxlength="5" onblur="myCheckTime(this);" styleClass="text"/>
            <div id="birthTimeForDisplayMessage" class="blank">&nbsp;</div>
        </td>	            
    </tr>
	<tr>
        <td>
            <bean:message key="newborn.sample.full.weight"/>:<span class="requiredlabel">*</span>     
        </td>
        <td>
            <app:text name="<%=formName%>" property="birthWeight" styleId="birthWeight" size="7" maxlength="10" styleClass="text" onblur="checkBirthWeight(this);"/>
            <div id="birthWeightMessage" class="blank">&nbsp;</div>
            <html:select name="<%=formName%>" property="selectedBirthWeight" onchange="convertWeight(this);">
			<app:optionsCollection name="<%=formName%>" property="birthWeightList" label="label" value="value"/>
			</html:select>	
        </td>
        <td>
            <bean:message key="newborn.sample.full.multiple.birth"/>:     
        </td>
        <td>
            <html:text name="<%=formName%>" property="multipleBirth" styleId="multipleBirth" styleClass="text" onblur="formatField(this,true);checkMultiple(this);" size="1" maxlength="1"/>
            <html:text name="<%=formName%>" property="birthOrder" styleId="birthOrder" styleClass="text" onblur="checkNumber(this);validateBirthOrder(this);" size="2" maxlength="2"/>
            <div id="multipleBirthMessage" class="blank">&nbsp;</div>
            <div id="birthOrderMessage" class="blank">&nbsp;</div>
        </td>
        <td>
            <bean:message key="newborn.sample.full.gestational.week"/>:     
        </td>
        <td>
            <html:text name="<%=formName%>" property="gestationalWeek" styleId="gestationalWeek" styleClass="text" onblur="checkNumber(this);" size="5"/>
            <div id="gestationalWeekMessage" class="blank">&nbsp;</div>
        </td>
        <td>
            <bean:message key="newborn.sample.full.gender"/>:
			<font size="1"><bean:message key="patient.gender.options"/></font>
        </td>
        <td>
            <app:text name="<%=formName%>" property="gender" styleId="gender" onblur="formatField(this,true);checkGender(this);" size="5" maxlength="1"/>
            <div id="genderMessage" class="blank">&nbsp;</div>
        </td>		
    </tr>
    <tr>
        <td>
            <bean:message key="newborn.sample.full.date.of.first.feeding"/>:<br>
            <font size="1"><bean:message key="humansampleone.date.additionalFormat"/></font>
        </td>		
        <td>
            <app:text name="<%=formName%>" property="dateFirstFeedingForDisplay" styleId="dateFirstFeedingForDisplay" size="10" maxlength="10" onkeyup="myCheckDate(this, event, false,false)" onblur="myCheckDate(this, event, true, true)" styleClass="text"/>
	        <div id="dateFirstFeedingForDisplayMessage" class="blank">&nbsp;</div>
        </td>
        <td>
            <bean:message key="newborn.sample.full.time.of.first.feeding"/>:<br>
            <font size="1"><bean:message key="humansampleone.time.additionalFormat"/></font>
        </td>		
        <td colspan="6">
            <app:text name="<%=formName%>" property="timeFirstFeedingForDisplay" styleId="timeFirstFeedingForDisplay" size="5" maxlength="5" onblur="myCheckTime(this);" styleClass="text"/>
            <div id="timeFirstFeedingForDisplayMessage" class="blank">&nbsp;</div>
        </td>
    </tr>
    <tr>        
        <td>
            <bean:message key="newborn.sample.full.type.of.feeding"/>:     
        </td>
        <td colspan="7">
            <table border="0">
                <tr>
                    <td>
                        <bean:message key="newborn.sample.full.breast"/>:
                    </td>    
                    <td>
                        <html:text name="<%=formName%>" property="breast" styleId="breast" styleClass="text" onblur="formatField(this,true);checkMultiple(this);" size="1" maxlength="1"/>           
                        <div id="breastMessage" class="blank">&nbsp;</div>
                    </td>
                    <td>
                        <bean:message key="newborn.sample.full.tpn"/>:
                    </td>
                    <td>
                        <html:text name="<%=formName%>" property="tpn" styleId="tpn" styleClass="text" onblur="formatField(this,true);checkMultiple(this);" size="1" maxlength="1"/>           
                        <div id="tpnMessage" class="blank">&nbsp;</div>
                    </td>
                    <td>
                        <bean:message key="newborn.sample.full.formula"/>:
                    </td>
                    <td>
                        <html:text name="<%=formName%>" property="formula" styleId="formula" styleClass="text" onblur="formatField(this,true);checkMultiple(this);" size="1" maxlength="1"/>           
                        <div id="formulaMessage" class="blank">&nbsp;</div>
                    </td>
                    <td>
                        <bean:message key="newborn.sample.full.milk"/>:
                    </td>
                    <td>
                        <html:text name="<%=formName%>" property="milk" styleId="milk" styleClass="text" onblur="formatField(this,true);checkMultiple(this);" size="1" maxlength="1"/>           
                        <div id="milkMessage" class="blank">&nbsp;</div>
                    </td>
                    <td>
                        <bean:message key="newborn.sample.full.soy"/>:
                    </td>
                    <td>
                        <html:text name="<%=formName%>" property="soy" styleId="soy" styleClass="text" onblur="formatField(this,true);checkMultiple(this);" size="1" maxlength="1"/>           
                        <div id="soyMessage" class="blank">&nbsp;</div>
                    </td>
                </tr>                        
            </table>    
        </td>    
    </tr>
    <tr>	            
        <td>
		    <bean:message key="newborn.sample.full.date.of.collection"/>:<span class="requiredlabel">*</span><br>
	        <font size="1"><bean:message key="humansampleone.date.additionalFormat"/></font>
	    </td>    
        <td> 
            <app:text name="<%=formName%>" property="collectionDateForDisplay" styleId="collectionDateForDisplay" size="10" maxlength="10" onkeyup="myCheckDate(this, event, false,false)" onblur="myCheckDate(this, event, true, true)" styleClass="text"/>
            <div id="collectionDateForDisplayMessage" class="blank">&nbsp;</div>
        </td>
        <td>
            <bean:message key="newborn.sample.full.time.of.collection"/>:<span class="requiredlabel">*</span><br>
            <font size="1"><bean:message key="humansampleone.time.additionalFormat"/></font>
        </td>		
        <td colspan="5">
            <app:text name="<%=formName%>" property="collectionTimeForDisplay" styleId="collectionTimeForDisplay" size="5" maxlength="5" onblur="myCheckTime(this);" styleClass="text"/>
            <div id="collectionTimeForDisplayMessage" class="blank">&nbsp;</div>
        </td>
    </tr>        
    <tr>    
        <td>
            <bean:message key="newborn.sample.full.clinical.info"/>:     
        </td>
        <td colspan="5">
            <table border="0">
                <tr>
                    <td>
                        <bean:message key="newborn.sample.full.jaundice"/>: 
                    </td>
                    <td>
                        <html:text name="<%=formName%>" property="jaundice" styleId="jaundice" styleClass="text" onblur="formatField(this,true);checkMultiple(this);" size="1" maxlength="1"/>           
                        <div id="jaundiceMessage" class="blank">&nbsp;</div>
                    </td>
                    <td>
                        <bean:message key="newborn.sample.full.antibiotic"/>: 
                    </td>
                    <td>
                        <html:text name="<%=formName%>" property="antibiotic" styleId="antibiotic" styleClass="text" onblur="formatField(this,true);checkMultiple(this);" size="1" maxlength="1"/>           
                        <div id="antibioticMessage" class="blank">&nbsp;</div>
                    </td>
                    <td>
                        <bean:message key="newborn.sample.full.transfused"/>: 
                    </td>
                    <td>
                        <html:text name="<%=formName%>" property="transfused" styleId="transfused" styleClass="text" onblur="formatField(this,true);checkMultiple(this);" size="1" maxlength="1"/>           
                        <div id="transfusedMessage" class="blank">&nbsp;</div>
                    </td>                     
                </tr>
            </table>
        </td>
        <td>
		    <bean:message key="newborn.sample.full.date.of.transfusion"/>:<br>
	        <font size="1"><bean:message key="humansampleone.date.additionalFormat"/></font>
	    </td>    
        <td> 
            <app:text name="<%=formName%>" property="dateTransfutionForDisplay" styleId="dateTransfutionForDisplay" size="10" maxlength="10" onkeyup="myCheckDate(this, event, false,false)" onblur="myCheckDate(this, event, true, true)" styleClass="text"/>
            <div id="dateTransfutionForDisplayMessage" class="blank">&nbsp;</div>
        </td>	
    </tr>
    <tr><td colspan="8">&nbsp;</td></tr>
</table>
  
<table border="0" width="100%">  
    <tr>
        <td colspan="8"> 
            <h2 align="left"><bean:message key="newborn.sample.full.risk.factor"/></h2>
        </td>    
    </tr>
    <tr>
        <td>
            <bean:message key="newborn.sample.full.nicu.patient"/>:     
        </td>
        <td>
            <html:text name="<%=formName%>" property="nicuPatient" styleId="nicuPatient" styleClass="text" onblur="formatField(this,true);checkMultiple(this);" size="1" maxlength="1"/>           
            <div id="nicuPatientMessage" class="blank">&nbsp;</div>
        </td>
        <td>
            <bean:message key="newborn.sample.full.birth.defect"/>:     
        </td>
        <td>
            <html:text name="<%=formName%>" property="birthDefect" styleId="birthDefect" styleClass="text" onblur="formatField(this,true);checkMultiple(this);" size="1" maxlength="1"/>           
            <div id="birthDefectMessage" class="blank">&nbsp;</div>
        </td>
        <td align="right">
            <bean:message key="newborn.sample.full.maternal.pregnancy.compensation"/>:
        </td>
        <td colspan="3">
            <html:text name="<%=formName%>" property="pregnancyComplication" styleId="pregnancyComplication" styleClass="text" onblur="formatField(this,true);checkMultiple(this);" size="1" maxlength="1"/>           
            <div id="pregnancyComplicationMessage" class="blank">&nbsp;</div>
        </td>
    </tr>
    <tr>
        <td>
            <bean:message key="newborn.sample.full.deceased.sibling"/>:     
        </td>
        <td>
            <html:text name="<%=formName%>" property="deceasedSibling" styleId="deceasedSibling" styleClass="text" onblur="formatField(this,true);checkMultiple(this);" size="1" maxlength="1"/>           
            <div id="deceasedSiblingMessage" class="blank">&nbsp;</div>
        </td>
        <td>
            <bean:message key="newborn.sample.full.cause.of.death"/>:     
        </td>
        <td colspan="5">
            <html:text name="<%=formName%>" property="causeOfDeath" styleId="causeOfDeath" styleClass="text" onblur="formatField(this,true);" size="108" maxlength="50"/>
        </td>
    </tr>  
    <tr>
        <td colspan="3">
            <bean:message key="newborn.sample.full.family.history"/>:     
        </td>
        <td>
            <html:text name="<%=formName%>" property="familyHistory" styleId="familyHistory" styleClass="text" onblur="formatField(this,true);checkMultiple(this);" size="1" maxlength="1"/>           
            <div id="familyHistoryMessage" class="blank">&nbsp;</div>
        </td>
        <td align="right">
            <bean:message key="newborn.sample.full.other"/>:     
        </td>
        <td>
            <html:text name="<%=formName%>" property="other" styleId="other" styleClass="text" onblur="formatField(this,true);" size="50" maxlength="100"/>
        </td>     
    </tr>                   				
    <tr><td colspan="8">&nbsp;</td></tr>
</table>

<table border="0" width="100%">  
    <tr>
        <td colspan="8"> 
            <h2 align="left"><bean:message key="newborn.sample.full.mother.info"/></h2>
        </td>
    </tr>
    <tr>        
        <td>
            <bean:message key="newborn.sample.full.last.name"/>:
        </td>
        <td>
            <html:text name="<%=formName%>" property="motherLastName" styleId="motherLastName" styleClass="text" onblur="formatField(this,true);" size="20"/>
        </td>
        <td>
            <bean:message key="newborn.sample.full.first.name"/>:
        </td>
        <td>
            <html:text name="<%=formName%>" property="motherFirstName" styleId="motherFirstName" styleClass="text" onblur="formatField(this,true);" size="20"/>
        </td>
        <td>
            <bean:message key="newborn.sample.full.birth.date"/>:<br>
	        <font size="1"><bean:message key="humansampleone.date.additionalFormat"/></font>
        </td>
        <td>           
            <app:text name="<%=formName%>" property="motherBirthDateForDisplay" styleId="motherBirthDateForDisplay" size="10" maxlength="10" onkeyup="myCheckDate(this, event, false,false)" onblur="myCheckDate(this, event, true, true)" styleClass="text"/>
            <div id="motherBirthDateForDisplayMessage" class="blank">&nbsp;</div>                        
        </td>
        <td>
             <bean:message key="newborn.sample.full.phone"/>:<br>
		     <font size="1"> <bean:message key="humansampleone.phone.additionalFormat"/></font>
        </td>
        <td>
            <app:text name="<%=formName%>" property="motherPhoneNumber" styleId="motherPhoneNumber" onblur="formatField(this, false);myCheckPhone(this)" onkeyup="javascript:getIt(this,event)" size="27" maxlength="13" styleClass="text"/>
		    <div id="motherPhoneNumberMessage" class="blank" >&nbsp;</div>
        </td>                          
    </tr>	
    <tr>
        <td>
            <bean:message key="newborn.sample.full.street.address"/>:
        </td>
        <td colspan="7">
            <html:text name="<%=formName%>" property="motherStreetAddress" styleId="motherStreetAddress" styleClass="text" onblur="formatField(this,true);" size="70"/>
        </td>
    </tr>
    <tr>     
        <td>
           <bean:message key="newborn.sample.full.city"/>:
        </td>
        <td colspan="3">
            <html:text name="<%=formName%>" property="city" styleId="city" styleClass="text" onblur="formatField(this,true);validateCity()" size="70"/>
            <span id="indicator1" style="display:none;"><img src="<%=basePath%>images/indicator.gif"/></span>
	        <input id="cityID" name="cityID" type="hidden" size="30" />
            <div id="cityMessage" class="blank" >&nbsp;</div>
        </td>
        <td>
            <bean:message key="newborn.sample.full.state"/>:
        </td>
        <td>
            <html:text name="<%=formName%>" property="state" styleId="state" styleClass="text" onblur="formatField(this,true);validateState()" size="2"/>
            <div id="stateMessage" class="blank" >&nbsp;</div>
        </td>
        <td>
            <bean:message key="newborn.sample.full.zip.code"/>:
        </td>
        <td>
            <html:text name="<%=formName%>" property="zipCode" styleId="zipCode" styleClass="text" onblur="validateZipCode()" size="10"/>
            <div id="zipCodeMessage" class="blank">&nbsp;</div>
        </td>
    </tr>    
    <tr><td colspan="8">&nbsp;</td></tr>
</table>

<table border="0" width="100%">
    <tr>
        <td colspan="8"> 
            <h2 align="left"><bean:message key="newborn.sample.full.provider.info"/></h2>
        </td>        
    </tr>
    <tr>
        <td>
            <bean:message key="newborn.sample.full.submitter.number"/>:<span class="requiredlabel">*</span>
        </td>
        <td colspan="7">
            <html:text name="<%=formName%>" property="submitterNumber" styleId="submitterNumber" styleClass="text"  onblur="validateOrganizationLocalAbbreviation();" size="20"/>
            <div id="submitterNumberMessage" class="blank">&nbsp;</div>
		    <div id="organizationName" class="blank">&nbsp;</div>
        </td>
    </tr>
    <tr>
       <td>
            <bean:message key="newborn.sample.full.physician.last.name"/>:
        </td>
        <td>
            <html:text name="<%=formName%>" property="physicianLastName" styleId="physicianLastName" styleClass="text" onblur="formatField(this,true);" size="30" maxlength="30"/>
        </td>
        <td>
            <bean:message key="newborn.sample.full.physician.first.name"/>:
        </td>
        <td>
            <html:text name="<%=formName%>" property="physicianFirstName" styleId="physicianFirstName" styleClass="text" onblur="formatField(this,true);" size="30" maxlength="20"/>
        </td>
        <td>
            <bean:message key="newborn.sample.full.physician.phone"/>:<br>
            <font size="1"> <bean:message key="humansampleone.phone.additionalFormat"/></font>
        </td>
        <td colspan="3"><!--bugzilla 2620 styleClass defined twice-->
            <html:text name="<%=formName%>" property="physicianPhoneNumber" styleId="physicianPhoneNumber" styleClass="text" onblur="formatField(this, false);myCheckPhone(this)" onkeyup="javascript:getIt(this,event)" size="24" maxlength="13" />
			<div id="physicianPhoneNumberMessage" class="blank" >&nbsp;</div>
        </td>          
    </tr> 
    <tr>
        <td colspan="8">&nbsp;</td>
    </tr>        
</table>

  <ajax:autocomplete
  source="city"
  target="cityID"
  baseUrl="ajaxAutocompleteXML"
  className="autocomplete"
  parameters="city={city},provider=CityAutocompleteProvider,fieldName=city,idName=id"
  indicator="indicator1"
  minimumCharacters="3"
  />

<app:javascript formName="newbornSampleFullForm" staticJavascript="true"/>