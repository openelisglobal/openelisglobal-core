<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants"
%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>
<%!

String allowEdits = "true";
String errorMessageAccessionNumber = "";
String accnNumb = "";
String errorMessageDates = "";

%>
<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

//bugzilla 2393 /bugzilla 2437
java.util.Locale locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
accnNumb =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "resultsentry.accessionNumber");
errorMessageAccessionNumber =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "errors.invalid",
                    accnNumb);
errorMessageDates =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "error.sample.xml.by.sample.flu.begindate.lessthan.enddate");

%>
<script language="JavaScript1.2">

function pageOnLoad() {
    var accn = document.getElementById("accessionNumber1");
    accn.focus();
}

function validateForm(form) {
    //return validateSampleXMLBySampleForm(form);
    return true;
}

function setAccessionNumberValidationMessage(message, field) {
      idField = document.getElementById(field);
      if (message == "invalid") {
       //only submit if accession number isn't left blank
         //bugzilla 1494
         alert('<%=errorMessageAccessionNumber%>');
        //disable save if accession number is incorrect    
     } 
  
}

function processAccessionNumberValidationSuccess(xhr) {
  var message = xhr.responseXML.getElementsByTagName("message")[0];
  var formfield = xhr.responseXML.getElementsByTagName("formfield")[0];
  //alert("I am in parseMessage and this is message, formfield " + message + " " + formfield);
  setAccessionNumberValidationMessage(message.childNodes[0].nodeValue, formfield.childNodes[0].nodeValue);
}

function processFailure(xhr) {
  //ajax call failed
}

function validateAccessionNumber(field) {

     var accessionNumberToValidate = field.name;
     var accessionNumber = field.value;
     
     if (field.value != null && field.value != '') {
      new Ajax.Request (
       'ajaxXML',  //url
       {//options
        method: 'get', //http method
        parameters: 'provider=AccessionNumberValidationProvider&form=' + document.forms[0].name + '&field=' + accessionNumberToValidate + '&id=' + escape(accessionNumber),      //request parameters
        //indicator: 'throbbing'
        onSuccess:  processAccessionNumberValidationSuccess,
        onFailure:  processFailure
       }
      );
     }
}

function myCheckDate(date, event, dateCheck, onblur) 
{
	var messageDiv = date.name + 'Message';
	var mdiv = $(messageDiv);
	mdiv.className = "blank";
	var validDate = DateFormat(date,date.value,event,dateCheck,'1');
	if (dateCheck) 
	{ 
		if (validDate) 
		{
			var validDate2 = lessThanCurrent(date);
			if (validDate2) 
			{
				mdiv.className = "blank";
			}
			else
			{
				mdiv.className = "badmessage";
			}
		}
		else
		{
			mdiv.className = "badmessage";
		}
	}
}

function setButtonClicked(button) {
   var byDateRange = document.getElementById('byDateRange');
   var bySampleRange = document.getElementById('bySampleRange');
   var bySample = document.getElementById('bySample');
   if (button.name == 'downloadinfluenzabysamplerange') {
       byDateRange.value = 'false';
       bySampleRange.value = 'true';
       bySample.value = 'false';
   }
   if (button.name == 'downloadinfluenzabydaterange') {
       byDateRange.value = 'true';
       bySampleRange.value = 'false';
       bySample.value = 'false';
   }
   if (button.name == 'downloadinfluenzabyindividualsample') {
       byDateRange.value = 'false';
       bySampleRange.value = 'false';
       bySample.value = 'true';
   }
   setAction(window.document.forms[0], 'Process', 'no', '');
}

function validateDatesAndSubmit(button, event) {
  var dateFrom = document.getElementById('fromReleasedDateForDisplay');
  var dateTo = document.getElementById('toReleasedDateForDisplay');
  if (DateFormat(dateFrom,dateFrom.value,event,true,'1') && DateFormat(dateTo,dateTo.value,event,true,'1')) {
  	if (fromDateLessThanToDate(dateFrom, dateTo)) {
      setButtonClicked(button);
    } else {
      alert('<%=errorMessageDates%>');
    }
  } else {
     alert('<%=errorMessageDates%>');
  }
}

</script>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<input id="byDateRange" name="byDateRange" type="hidden" />
<input id="bySampleRange" name="bySampleRange" type="hidden" />
<input id="bySample" name="bySample" type="hidden" />
<table width="100%">
	<tr> 
		<td width="100%">
			<bean:message key="report.sample.xml.by.sample.flu.enter.accessionnumber"/>:
		</td>
    </tr>
    <tr>
     <td>
      <table width=100%>
       <tr>
		<td width="8%" align="left"> 
			<app:text name="<%=formName%>" 
					  property="accessionNumber1" 
					  onblur="validateAccessionNumber(this);" 
					  size="10" 
					  maxlength="10"
					  styleClass="text" 
					  styleId="accessionNumber1"/>
           <div id="accessionNumber1Message" class="blank">&nbsp;</div>
        </td>
        <td width="2%" align="left">
         &nbsp;
        </td>
        <td width="8%" align="left">
			<app:text name="<%=formName%>" 
					  property="accessionNumber2" 
					  onblur="validateAccessionNumber(this);" 
					  size="10" 
					  maxlength="10"
					  styleClass="text" 
					  styleId="accessionNumber2"/>
           <div id="accessionNumber2Message" class="blank">&nbsp;</div>
        </td>
        <td width="2%" align="left">
         &nbsp;
        </td>
        <td width="8%" align="left">
			<app:text name="<%=formName%>" 
					  property="accessionNumber3" 
					  onblur="validateAccessionNumber(this);" 
					  size="10" 
					  maxlength="10"
					  styleClass="text" 
					  styleId="accessionNumber3"/>
           <div id="accessionNumber3Message" class="blank">&nbsp;</div>
        </td>
        <td width="72%">
         &nbsp;
        </td>
     </tr>
     </table>
     </td>
     </tr>
     <tr>
     <td>
     <table>
     <tr>
        <td width="8%" align="left">
			<app:text name="<%=formName%>" 
					  property="accessionNumber4" 
					  onblur="validateAccessionNumber(this);" 
					  size="10" 
					  maxlength="10"
					  styleClass="text" 
					  styleId="accessionNumber4"/>
           <div id="accessionNumber4Message" class="blank">&nbsp;</div>
        </td>
        <td width="2%" align="left">
         &nbsp;
        </td>
        <td width="8%" align="left">
			<app:text name="<%=formName%>" 
					  property="accessionNumber5" 
					  onblur="validateAccessionNumber(this);" 
					  size="10" 
					  maxlength="10"
					  styleClass="text" 
					  styleId="accessionNumber5"/>
           <div id="accessionNumber5Message" class="blank">&nbsp;</div>
		</td>
        <td width="2%" align="left">
         &nbsp;
        </td>
		<td width="8%" align="left"> 
			<app:text name="<%=formName%>" 
					  property="accessionNumber6" 
					  onblur="validateAccessionNumber(this);" 
					  size="10" 
					  maxlength="10"
					  styleClass="text" 
					  styleId="accessionNumber6"/>
           <div id="accessionNumber6Message" class="blank">&nbsp;</div>
       </td>
		<td width="72%" align="left">
		  &nbsp;
		</td>		
	</tr>
	</table>
	</td>
	</tr>
    <tr>
    <td>
    <table>
    <tr>
        <td width="8%" align="left">
			<app:text name="<%=formName%>" 
					  property="accessionNumber7" 
					  onblur="validateAccessionNumber(this);" 
					  size="10" 
					  maxlength="10"
					  styleClass="text" 
					  styleId="accessionNumber7"/>
           <div id="accessionNumber7Message" class="blank">&nbsp;</div>
       </td>
        <td width="2%" align="left">
         &nbsp;
        </td>
        <td width="8%" align="left">
			<app:text name="<%=formName%>" 
					  property="accessionNumber8" 
					  onblur="validateAccessionNumber(this);" 
					  size="10" 
					  maxlength="10"
					  styleClass="text" 
					  styleId="accessionNumber8"/>
           <div id="accessionNumber8Message" class="blank">&nbsp;</div>
       </td>
        <td width="2%" align="left">
         &nbsp;
        </td>
        <td width="8%" align="left">
			<app:text name="<%=formName%>" 
					  property="accessionNumber9" 
					  onblur="validateAccessionNumber(this);" 
					  size="10" 
					  maxlength="10"
					  styleClass="text" 
					  styleId="accessionNumber9"/>
           <div id="accessionNumber9Message" class="blank">&nbsp;</div>
       </td>
        <td width="72%" align="left">
         &nbsp;
        </td>
      </tr>
      </table>
      </td>
      </tr>
      <tr>
       <td width="5%" align="left" valign="top">
     	   <app:button  property="downloadinfluenzabyindividualsample" onclick="setButtonClicked(this);" allowEdits="true">
  	       <bean:message key="label.button.download"/>
  	      </app:button>
		</td>
        <td width="95%">
         &nbsp;
        </td>
  	   </tr>
  	  </table>
	 </td>		
	</tr>
	<tr>
		<td width="100%" align="left">
			<bean:message key="report.sample.xml.by.sample.flu.enter.sample.range"/>:
		</td>
   </tr>
   <tr>
    <td>
    <table width="100%">
    <tr>
	<td width="10%" valign="top" align="left">
		<bean:message key="report.sample.xml.by.sample.flu.enter.sample.range.from"/>:
	</td>
	<td width="10%" valign="top" align="left">
		<bean:message key="report.sample.xml.by.sample.flu.enter.sample.range.to"/>:
	</td>
	<td width="80%" align="left">
		&nbsp;&nbsp;&nbsp;
	</td>
    </tr>
    </table>
    </td>
   </tr>
   <tr>
    <td>
     <table width="100%">
     <tr>
	<td width="10%" valign="top" align="left">
					<app:text name="<%=formName%>" 
					  property="fromAccessionNumber" 
					  onblur="validateAccessionNumber(this);" 
					  size="10" 
					  maxlength="10"
					  styleClass="text" 
					  styleId="fromAccessionNumber"/>
	<div id="fromAccessionNumberMessage" class="blank">&nbsp;</div>
	</td>
	<td width="10%" valign="top" align="left"> 
			<app:text name="<%=formName%>" 
					  property="toAccessionNumber" 
					  onblur="validateAccessionNumber(this);" 
					  size="10" 
					  maxlength="10"
					  styleClass="text" 
					  styleId="toAccessionNumber"/>
	<div id="toAccessionNumberMessage" class="blank">&nbsp;</div>
	</td>
	<td width="80%" valign="top" align="left">
		&nbsp;&nbsp;&nbsp;
	</td>
	</tr>
	</table>
	</td>
	</tr>
	<tr>
	<td>
	<table>
	<tr>
	<td width="5%" valign="top" align="left">
  	<app:button  onclick="setButtonClicked(this);"
		 property="downloadinfluenzabysamplerange" allowEdits="true">
  	   <bean:message key="label.button.download"/>
  	</app:button>
  	</td>
  	<td width=95%" align="left">
  	 &nbsp;
  	</td>
  	</tr>
  	</table>
	</td>
	</tr>
	<tr> 
		<td width="100%" align="left">
			<bean:message key="report.sample.xml.by.sample.flu.enter.date.range"/>:
		</td>
    </tr>
    <tr>
     <td>
      <table width="100%">
       <tr>
    	<td width="10%" valign="top" align="left">
			<bean:message key="report.sample.xml.by.sample.flu.enter.date.range.from"/>:
		</td>
		<td width="10%" valign="top" align="left">
			<bean:message key="report.sample.xml.by.sample.flu.enter.date.range.to"/>:
		</td>
		<td width="80%">
			&nbsp;&nbsp;&nbsp;
		</td>
	   </tr>
	   </table>
	   </td>
	   </tr>
	   <tr>
	    <td>
	    <table width="100%">
	    <tr>
		<td width="10%" align="left"> 
			<app:text name="<%=formName%>" 
					  property="fromReleasedDateForDisplay" 
					  onkeyup="myCheckDate(this, event, false,false);" 
					  onblur="myCheckDate(this, event, true, true);" 
					  size="10" 
					  styleClass="text" 
					  styleId="fromReleasedDateForDisplay"/>
			<div id="fromReleasedDateForDisplayMessage" class="blank">&nbsp;</div>
		</td>
		<td width="10%" align="left"> 
			<app:text name="<%=formName%>" 
					  property="toReleasedDateForDisplay" 
					  onkeyup="myCheckDate(this, event, false,false);" 
					  onblur="myCheckDate(this, event, true, true);" 
					  size="10" 
					  styleClass="text" 
					  styleId="toReleasedDateForDisplay"/>
			<div id="toReleasedDateForDisplayMessage" class="blank">&nbsp;</div>
	    </td>
		<td width="80%">
			&nbsp;&nbsp;&nbsp;
		</td>
	  </tr>
	  </table>
	  </td>
	  </tr>
	  <tr>
	   <td width="5%" valign="top" align="left">
     	<app:button  property="downloadinfluenzabydaterange" onclick="validateDatesAndSubmit(this, event);"
     	    allowEdits="true">
  	       <bean:message key="label.button.download"/>
  	    </app:button>
 	  </td>
  	<td width=95%" align="left">
  	 &nbsp;
  	</td>
  	</tr>
  	</table>
	</td>		
	</tr>
</table>
<html:javascript formName="influenzaSampleXMLBySampleForm"/>
