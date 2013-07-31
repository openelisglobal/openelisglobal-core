<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants, 
	org.apache.struts.Globals,
	us.mn.state.health.lims.qaevent.valueholder.QaEventRoutingSwitchSessionHandler,
	us.mn.state.health.lims.testmanagement.valueholder.TestManagementRoutingSwitchSessionHandler, 
	us.mn.state.health.lims.result.valueholder.ResultsEntryRoutingSwitchSessionHandler,
    us.mn.state.health.lims.common.util.Versioning"
%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%--bugzilla 2053, 2501, 2504, 2502--%>
<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="accessionNumberParm" value='<%=IActionConstants.ACCESSION_NUMBER%>'/>
<bean:define id="viewModeParam" value='<%=IActionConstants.QAEVENTS_ENTRY_PARAM_VIEW_MODE%>'/>
<bean:define id="multipleSampleModeParam" value='<%=IActionConstants.MULTIPLE_SAMPLE_MODE%>'/>
<bean:define id="fullScreen" value='<%=IActionConstants.QAEVENTS_ENTRY_FULL_SCREEN_VIEW%>'/>
<bean:define id="normal" value='<%=IActionConstants.QAEVENTS_ENTRY_NORMAL_VIEW%>'/>
<bean:define id="fullScreenSection" value='<%=IActionConstants.QAEVENTS_ENTRY_FULL_SCREEN_VIEW_SECTION%>'/>
<bean:define id="sampleSection" value='<%=IActionConstants.QAEVENTS_ENTRY_FULL_SCREEN_VIEW_SAMPLE_SECTION%>'/>
<bean:define id="testSection" value='<%=IActionConstants.QAEVENTS_ENTRY_FULL_SCREEN_VIEW_TEST_SECTION%>'/>


<%!
String path = "";
String basePath = "";
String allowEdits = "true";
String qaEventsEntryFromTestManagement = "false";
String qaEventsEntryFromResultsEntry = "false";
String qaEventsEntryFromBatchResultsEntry = "false";
String qaEventsEntryFromSampleTracking = "false";
String qaEventsEntryFromQaEventsEntryLineListing = "false";
String errorMessageAccessionNumber = "";
String accessionNumber = "";
String errorMessageStatusOfSample = "";
String allowDisplayButton = "true";
String viewMode = "";
String fullScreenSection = "";

%>

<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

if (QaEventRoutingSwitchSessionHandler.isSwitchOn(IActionConstants.QA_EVENTS_ENTRY_ROUTING_FROM_TEST_MANAGEMENT, session)) {
  qaEventsEntryFromTestManagement = "true";
} else {
  qaEventsEntryFromTestManagement = "false";
}

if (QaEventRoutingSwitchSessionHandler.isSwitchOn(IActionConstants.QA_EVENTS_ENTRY_ROUTING_FROM_RESULTS_ENTRY, session)) {
  qaEventsEntryFromResultsEntry = "true";
} else {
  qaEventsEntryFromResultsEntry = "false";
}

if (QaEventRoutingSwitchSessionHandler.isSwitchOn(IActionConstants.QA_EVENTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_ENTRY, session)) {
  qaEventsEntryFromBatchResultsEntry = "true";
  allowDisplayButton = "false";
} else {
  qaEventsEntryFromBatchResultsEntry = "false";
  if (allowEdits.equals("true")) {
    allowDisplayButton = "true";
  }
}

if (QaEventRoutingSwitchSessionHandler.isSwitchOn(IActionConstants.QA_EVENTS_ENTRY_ROUTING_FROM_SAMPLE_TRACKING, session)) {
  qaEventsEntryFromSampleTracking = "true";
} else {
  qaEventsEntryFromSampleTracking = "false";
}

if (QaEventRoutingSwitchSessionHandler.isSwitchOn(IActionConstants.QA_EVENTS_ENTRY_ROUTING_FROM_QAEVENTS_ENTRY_LINELISTING, session)) {
  qaEventsEntryFromQaEventsEntryLineListing = "true";
} else {
  qaEventsEntryFromQaEventsEntryLineListing = "false";
}

java.util.Locale locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
accessionNumber =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "qaeventsentry.accessionNumber");
errorMessageAccessionNumber =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "errors.invalid",
                    accessionNumber);
errorMessageStatusOfSample = us.mn.state.health.lims.common.util.resources.ResourceLocator
					.getInstance().getMessageResources().getMessage(locale,
					"error.invalid.sample.status");
					
path = request.getContextPath();
basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
					
//bugzilla 2501
String previousDisabled = "false";
String nextDisabled = "false"; 
String filterByCategoryDisabled = "false";
String multipleSampleMode = "false";
if (request.getAttribute(IActionConstants.PREVIOUS_DISABLED) != null) {
    previousDisabled = (String)request.getAttribute(IActionConstants.PREVIOUS_DISABLED);
}
if (request.getAttribute(IActionConstants.NEXT_DISABLED) != null) {
    nextDisabled = (String)request.getAttribute(IActionConstants.NEXT_DISABLED);
}
if (request.getAttribute(multipleSampleModeParam) != null) {
    multipleSampleMode = (String)request.getAttribute(multipleSampleModeParam);
    if (multipleSampleMode.equals(IActionConstants.FALSE)) {
      //filter only for multiple sample mode/ disable for single sample mode
      filterByCategoryDisabled = IActionConstants.TRUE;
    }
}

viewMode = normal;
if (request.getAttribute(viewModeParam) != null) {
   viewMode = (String)request.getAttribute(viewModeParam);
}
fullScreenSection = sampleSection;
if (request.getAttribute(fullScreenSection) != null) {
   fullScreenSection = (String)request.getAttribute(fullScreenSection);
}
%>
<script language="JavaScript1.2">

function pageOnLoad() {
    var accn = $("accessionNumber");
    //bugzilla 2502 (full screen mode and bad filter - no accession number)
    if (accn != null) {
     accn.focus();
    }
    
   //check if we need to popup the notes (attribute IActionConstants.POPUP_NOTES will only be populated if set in the action)
  //this is needed after update has been chosen on save confirm popup
 
   if ('<%= request.getAttribute(IActionConstants.POPUP_NOTES) %>' != null) {
        var notesRefId = '<%= (String)request.getAttribute(IActionConstants.NOTES_REFID) %>';
        var notesRefTable = '<%= (String)request.getAttribute(IActionConstants.NOTES_REFTABLE) %>';
        var pop = '<%= (String)request.getAttribute(IActionConstants.POPUP_NOTES) %>'
        if (pop == 'true') {
           popupNotes(document.forms[0], notesRefTable, notesRefId);
        } 
   }
  <% request.setAttribute(IActionConstants.POPUP_NOTES, "false"); %>

}

function validateForm(form) {
    //return validateQaEventsEntryForm(form);
    return true;
}

function setMessage(message, field) {
  idField = $(field);
  if (idField != null ){
  	  //alert("idField name " + idField.name);
      if (idField.name == "accessionNumber") {
         if (message == "invalid") {
           //only submit if accession number isn't left blank
           alert('<%=errorMessageAccessionNumber%>');
           //disable save if accession number is incorrect 
        } else if (message == "invalidStatus") {
        	alert('<%=errorMessageStatusOfSample%>');   
         } else {
              var checkbox = document.getElementById("multipleSampleMode");
             if(checkbox.checked) {
               //bugzilla 2501 go to multiple sample mode positioning to specific accession number
               setAction(window.document.forms[0], 'PositionToRecord', 'no', '?<%=accessionNumberParm%>=' + $F("accessionNumber") + '&ID=');
             } else {
               setAction(window.document.forms[0], 'PreView', 'yes', '?ID=');
             }
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
}

function validateAccessionNumber() {
   var field = $("accessionNumber");
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

function cancelQaEventsEntry() {
  var qaEFromTestMgmt;
  var qaEFromRsltsEntry;
  var qaEFromBtchRsltsEntry;
  var qaEFromSampleTracking;
  var qaEFromLineListing;
  qaEFromTestMgmt = '<%=qaEventsEntryFromTestManagement%>';
  qaEFromRsltsEntry = '<%=qaEventsEntryFromResultsEntry%>';
  qaEFromBtchRsltsEntry = '<%=qaEventsEntryFromBatchResultsEntry%>';
  qaEFromSampleTracking = '<%=qaEventsEntryFromSampleTracking%>';
  qaEFromLineListing = '<%=qaEventsEntryFromQaEventsEntryLineListing%>';

  //bugzilla 2053
  //this sequence of events is important: check auto routing switches FIRST (they take priority over other switches)
  if (qaEFromRsltsEntry == 'true') {
    setAction(window.document.forms[0], 'CancelToResultsEntry', 'no', '?<%=accessionNumberParm%>=' + $F("accessionNumber") + '&ID=')
  } else if (qaEFromBtchRsltsEntry == 'true') {
    setAction(window.document.forms[0], 'CancelToBatchResultsEntry', 'no', '?<%=accessionNumberParm%>=' + $F("accessionNumber") + '&ID=')
  } else if (qaEFromSampleTracking == 'true') {
    setAction(window.document.forms[0], 'CancelToSampleTracking', 'no', '?<%=accessionNumberParm%>=' + $F("accessionNumber") + '&ID=')
  } else if (qaEFromTestMgmt == 'true') {
    setAction(window.document.forms[0], 'CancelToTestManagement', 'no', '?<%=accessionNumberParm%>=' + $F("accessionNumber") + '&ID=');
  } else if (qaEFromLineListing == 'true') {
    setAction(window.document.forms[0], 'CancelToQaEventsEntryLineListing', 'no', '?ID=');    
  } else {
      //defaults to going back to QA Events menu
      setAction(window.document.forms[0], 'Cancel', 'no', '?close=true&ID=');
  }
 
}

function gotoEditSample(form) {
  var accessionNumber = document.getElementById("accessionNumber");
  setAction(window.document.forms[0], 'TestManagementFrom', 'yes', '?accessionNumber=' + accessionNumber.value + '&ID=');
}

function gotoResultsEntry(form) {
    var accessionNumber = document.getElementById("accessionNumber");
	setAction(window.document.forms[0], 'ResultsEntryFrom', 'no', '?accNum=' + accessionNumber.value + '&ID=');
}

//bugzilla 2504
function gotoLineListingView() {
    var accessionNumber = document.getElementById("accessionNumber");
	setAction(window.document.forms[0], 'QaEventsEntryLineListingFrom', 'no', '?accessionNumber=' + accessionNumber.value + '&ID=');
}

function toggleMultipleSampleMode() {
 var checkbox = document.getElementById("multipleSampleMode");
 if(checkbox.checked) {
   //go to multiple sample mode (initialize accession number to null)
   var accn = document.getElementById("accessionNumber");
   //bugzilla 2502 (full screen mode and bad filter - no accession number)
   if (accn != null) {
    accn.value = "";
   }
   setAction(window.document.forms[0], 'PositionToRecord', 'no', '?ID=');
 } else {
   //back to single sample mode
   setAction(window.document.forms[0], '', 'no', '?ID=');
 }
}

function confirmSaveForwardPopup(direction)
{
  var myWin = createSmallConfirmPopup( "", null , null );
  
   <% 
      
      out.println("var message = null;");
   
      String message =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "message.popup.confirm.saveandforward");
        
     out.println("message = '" + message +"';");
     
     String button1 = 	us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					    locale,
                       "label.button.yes");
     String button2 = 	us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					    locale,
                       "label.button.no");
     String button3 = 	us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					    locale,
                       "label.button.cancel");
                       
     String title = 	us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					    locale,
                       "title.popup.confirm.saveandforward");
                       
     String space = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
       		
     
 %> 

    var href = "<%=basePath%>css/openElisCore.css?ver=<%= Versioning.getBuildNumber() %>";
 
    var strHTML = ""; 
 
    strHTML = '<html><link rel="stylesheet" type="text/css" href="' + href + '" /><head><'; 
    strHTML += 'SCRIPT LANGUAGE="JavaScript">var tmr = 0;function fnHandleFocus(){if(window.opener.document.hasFocus()){window.focus();clearInterval(tmr);tmr = 0;}else{if(tmr == 0)tmr = setInterval("fnHandleFocus()", 500);}}</SCRIPT'; 
    strHTML += '><'; 
    strHTML += 'SCRIPT LANGUAGE="javascript" >'
    strHTML += 'var imp= null; function impor(){imp="norefresh";} ';
    strHTML += ' function fcl(){  if(imp!="norefresh") {  window.opener.reFreshCurWindow(); }}';
    
    strHTML += '  function goToNextActionSave(){ ';
    strHTML += ' var reqParms = "?direction=next&ID="; ';
    strHTML += ' window.opener.setAction(window.opener.document.forms[0], "UpdatePositionToRecord", "yes", reqParms);self.close();} ';
    strHTML += '  function goToPreviousActionSave(){ ';
    strHTML += ' var reqParms = "?direction=previous&ID="; ';
    strHTML += ' window.opener.setAction(window.opener.document.forms[0], "UpdatePositionToRecord", "yes", reqParms);self.close();} ';
    
    strHTML += '  function goToNextActionNoSave(){ ';
    strHTML += ' var reqParms = "?direction=next&ID="; ';
    strHTML += ' window.opener.setAction(window.opener.document.forms[0], "PositionToRecord", "no", reqParms);self.close();} ';
    strHTML += '  function goToPreviousActionNoSave(){ ';
    strHTML += ' var reqParms = "?direction=previous&ID="; ';
    strHTML += ' window.opener.setAction(window.opener.document.forms[0], "PositionToRecord", "no", reqParms);self.close();} ';
    
    strHTML += ' setTimeout("impor()",359999);</SCRIPT'; 
    strHTML += '><title>' + "<%=title%>" + '</title></head>'; 
    strHTML += '<body onBlur="fnHandleFocus();" onLoad="fnHandleFocus();" ><form name="confirmSaveIt" method="get" action=""><div id="popupBody"><table><tr><td class="popuplistdata">';
    strHTML += message;
    if (direction == 'next') {
     strHTML += '<br><center><input type="button"  name="save" value="' + "<%=button1%>" + '" onClick="goToNextActionSave();" />';
     strHTML += "<%=space%>";
     strHTML += '<input type="button"  name="save" value="' + "<%=button2%>" + '" onClick="goToNextActionNoSave();"/>';
    } else if (direction == 'previous') {
     strHTML += '<br><center><input type="button"  name="save" value="' + "<%=button1%>" + '" onClick="goToPreviousActionSave();" />';
     strHTML += "<%=space%>";
     strHTML += '<input type="button"  name="save" value="' + "<%=button2%>" + '" onClick="goToPreviousActionNoSave();"/>';
    }
    strHTML += "<%=space%>";
    strHTML += '<input type="button"  name="cancel" value="' + "<%=button3%>" + '" onClick="self.close();" /></center></div>';
    strHTML += '</td></tr></table></form></body></html>'; 

     myWin.document.write(strHTML); 

     myWin.window.document.close(); 

     setTimeout ('myWin.close()', 360000); 
}


function previousAction(form, ignoreFields) {
  if (isDirty(form, ignoreFields)) {
     confirmSaveForwardPopup('previous');
  } else {
     setAction(form, 'PositionToRecord', 'no', '?direction=previous&ID=');
  }
}


function nextAction(form, ignoreFields) {
  if (isDirty(form, ignoreFields)) {
      //popup to give user option to save, don't save AND go to next, cancel
      confirmSaveForwardPopup('next');
  } else {
      setAction(form, 'PositionToRecord', 'no', '?direction=next&ID=');
  }
}
</script>

<%
	if(null != request.getAttribute(IActionConstants.FORM_NAME))
	{
%>
<%--added for bugzilla 2501--%>
<html:hidden property="noteRefTableId" name="<%=formName%>" styleId="noteRefTableId"/>
<html:hidden property="noteRefId" name="<%=formName%>" styleId="noteRefId"/>
<html:hidden property="noteIds" name="<%=formName%>" styleId="noteIds"/>
<html:hidden property="noteSubjects" name="<%=formName%>" styleId="noteSubjects"/>
<html:hidden property="noteTexts" name="<%=formName%>" styleId="noteTexts"/>
<html:hidden property="noteTypes" name="<%=formName%>" styleId="noteTypes"/>
<html:hidden property="noteLastupdateds" name="<%=formName%>" styleId="noteLastupdateds"/>
<html:hidden property="viewMode" name="<%=formName%>" styleId="viewMode"/>
<html:hidden property="fullScreenSection" name="<%=formName%>" styleId="fullScreenSection"/>
<table width="100%">
    <% 
        //bugzilla 2502 eliminate this row in full screen mode
        if (viewMode.equals(normal)) { 
    %>
	<tr><%--bugzilla 2501--%>
	    <td style="font-family: Arial, Helvetica, sans-serif; font-weight: bold; font-size:140%; color:#336699;BACKGROUND-COLOR: #FFFFFF;"> 
				<logic:notEmpty
					name="<%=IActionConstants.PAGE_SUBTITLE_KEY%>">
					<bean:write name="<%=IActionConstants.PAGE_SUBTITLE_KEY%>" />
				</logic:notEmpty> 
				<logic:empty
					name="<%=IActionConstants.PAGE_SUBTITLE_KEY%>">
					<% if (request.getParameter("ID").equals("0")) { %>
					  <bean:message key="default.add.title" />
					<% } else { %>
					  <bean:message key="default.edit.title" />
					<%}%>
			   </logic:empty> 
			   <app:text name="<%=formName%>" styleId="accessionNumber" property="accessionNumber" allowEdits="true" onkeypress="return noenter()"/>
   			   &nbsp;
		       <app:button  onclick="validateAccessionNumber();"
	                 			 property="view" allowEdits="<%=Boolean.valueOf(allowDisplayButton).booleanValue()%>" >
  			           <bean:message key="label.button.display"/>
  			   </app:button>
      </td>
  </tr>
  <% } %>
<%--added for bugzilla 2501--%>
  <tr>
    <td style="font-weight: bold; color:#336699;BACKGROUND-COLOR: #FFFFFF;"> 
       <html:checkbox name='<%=formName%>' property="multipleSampleMode" styleId="multipleSampleMode" onclick="toggleMultipleSampleMode();"/>
       &nbsp;
  	   <font size="2" color="black">
  	      <bean:message key="qaeventsentry.header.message.show.samples.with.pending.events" />
  	      &nbsp;
  	      <bean:write name="<%=formName%>" property="currentCount"/>&nbsp;
  	      <bean:message key="qaeventsentry.header.of"/>&nbsp;
  	      <bean:write name="<%=formName%>" property="totalCount"/>
  	   </font>
  	   &nbsp;
  	   <html:button onclick="previousAction(window.document.forms[0], '');" property="previous" disabled="<%=Boolean.valueOf(previousDisabled).booleanValue()%>">
  			   <bean:message key="label.button.previous"/>
  	   </html:button>
	   <html:button onclick="nextAction(window.document.forms[0], '');" property="next"  disabled="<%=Boolean.valueOf(nextDisabled).booleanValue()%>">
  		       <bean:message key="label.button.next"/>
  	   </html:button>
  	   &nbsp;&nbsp;&nbsp;&nbsp&nbsp;&nbsp;&nbsp;&nbsp;
   	   <font size="2" color="black">
   	      <bean:message key="qaeventsentry.header.message.filter.by" />:
   	   </font>
   	   &nbsp;
   	   <html:select name="<%=formName%>" property="selectedQaEventsCategoryId" onchange="toggleMultipleSampleMode();" disabled="<%=Boolean.valueOf(filterByCategoryDisabled).booleanValue()%>">
			 <app:optionsCollection 
			  name="<%=formName%>" 
			  property="categoryDictionaries" 
    		  label="dictEntry" 
			  value="id" 
			  allowEdits="true"
			  />
       </html:select>
       &nbsp;&nbsp;&nbsp;&nbsp&nbsp;
       <%--bugzilla 2504--%>
       <a href="" onclick="gotoLineListingView();return false;" style="BACKGROUND-COLOR: #f7f7e7;color:blue">
              <bean:message key="qaeventsentry.header.link.line.listing.view" />
       </a>
  	</td>
  </tr>
</table>
<%
	}
%>
<%--no struts validation needed on this form--%>
<%--html:javascript formName="qaEventsEntryForm"/--%>

