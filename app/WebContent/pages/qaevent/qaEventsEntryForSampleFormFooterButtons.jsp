<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants, 
	java.util.Locale,
    us.mn.state.health.lims.common.util.Versioning,
	us.mn.state.health.lims.qaevent.valueholder.QaEventRoutingSwitchSessionHandler"
%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<%--bugzilla 2501, 2053--%>
<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="assignedTestsExist" value="false" />
<bean:define id="qaEventsExist" value="false" />
<%--bugzilla 2053, 2501, 2504, 2502--%>
<bean:define id="accessionNumberParm" value='<%=IActionConstants.ACCESSION_NUMBER%>'/>
<bean:define id="viewModeParam" value='<%=IActionConstants.QAEVENTS_ENTRY_PARAM_VIEW_MODE%>'/>
<bean:define id="fullScreen" value='<%=IActionConstants.QAEVENTS_ENTRY_FULL_SCREEN_VIEW%>'/>
<bean:define id="normal" value='<%=IActionConstants.QAEVENTS_ENTRY_NORMAL_VIEW%>'/>
<bean:define id="fullScreenSection" value='<%=IActionConstants.QAEVENTS_ENTRY_FULL_SCREEN_VIEW_SECTION%>'/>
<bean:define id="sampleSection" value='<%=IActionConstants.QAEVENTS_ENTRY_FULL_SCREEN_VIEW_SAMPLE_SECTION%>'/>
<bean:define id="testSection" value='<%=IActionConstants.QAEVENTS_ENTRY_FULL_SCREEN_VIEW_TEST_SECTION%>'/>

<%!
Locale locale = null;
String path = "";
String basePath = "";
String viewMode = "";
String fullScreenSection = "";
%>
<% 
path = request.getContextPath();
basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);

String resultsEntryForCircularReferenceDisabled = IActionConstants.FALSE;
String editSampleForCircularReferenceDisabled  = IActionConstants.FALSE;
if (QaEventRoutingSwitchSessionHandler.isSwitchOn(IActionConstants.QA_EVENTS_ENTRY_ROUTING_FROM_RESULTS_ENTRY, session)) {
    resultsEntryForCircularReferenceDisabled = IActionConstants.TRUE;
}
if (QaEventRoutingSwitchSessionHandler.isSwitchOn(IActionConstants.QA_EVENTS_ENTRY_ROUTING_FROM_TEST_MANAGEMENT, session)) {
    editSampleForCircularReferenceDisabled = IActionConstants.TRUE;
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

function toggleSampleView() { 
  var viewMode = '<%=viewMode%>';
  var param = "";

  //which mode do we switch to?
   if (viewMode == "<%=normal%>") {
    //goto fullscreen view
    document.getElementById("viewMode").value = "<%=fullScreen%>"
    document.getElementById("fullScreenSection").value = "<%=sampleSection%>";
  } else {
    //goto normal view
    document.getElementById("viewMode").value = "<%=normal%>";
  }
  var checkbox = document.getElementById("multipleSampleMode");
  if(checkbox.checked) {
  //bugzilla 2501 go to multiple sample mode positioning to specific accession number
    setAction(window.document.forms[0], 'PositionToRecord', 'no', '?<%=accessionNumberParm%>=' + $F("accessionNumber") + '&ID=');
  } else {
    setAction(window.document.forms[0], 'PreView', 'yes', '?ID=');
  }

}

function toggleFromSampleToTestView() { 
   document.getElementById("fullScreenSection").value = "<%=testSection%>";
   var checkbox = document.getElementById("multipleSampleMode");
   if(checkbox.checked) {
   //bugzilla 2501 go to multiple sample mode positioning to specific accession number
     setAction(window.document.forms[0], 'PositionToRecord', 'no', '?<%=accessionNumberParm%>=' + $F("accessionNumber") + '&ID=');
   } else {
     setAction(window.document.forms[0], 'PreView', 'yes', '?ID=');
   }

}

//bugzilla 2504 changed function name to be more accurate
function checkAllForSampleCompleted() {
      var checkState = true;
      var selectedRows = window.document.forms[0].elements['selectedSampleQaEventIdsForCompletion'];
      // Toggles through all of the checkboxes defined in the CheckBoxIDs array
      // and updates their value to the checkState input parameter
	  if (selectedRows != null){	
        //If only one checkbox
        if (selectedRows[0] == null) {
             selectedRows.checked = checkState;
        } else {
      
      
         for (var i = 0; i < selectedRows.length; i++)
            selectedRows[i].checked = checkState;
        }
      } 
}

//bugzilla 2504 changed function name to be more accurate
function confirmCompleteAllForSample()
{
  var myWin = createSmallConfirmPopup( "", null , null );
  
   <% 
      
      out.println("var message = null;");
       
      String confirmsave_message =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "qaeventsentry.message.popup.confirm.complete.all.sample");
         
     out.println("confirmsave_message = '" + confirmsave_message +"';");

     String button1 = 	us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					    locale,
                       "label.button.save");
     String button2 = 	us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
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
    strHTML += ' var imp= null; function impor(){imp="norefresh";} '
    strHTML += ' function fcl(){  if(imp!="norefresh") {  opener.reFreshCurWindow(); }}';
     
    strHTML += '  function goToSave(){ ';
//bugzilla 2504 changed function name to be more accurate
    strHTML += ' window.opener.checkAllForSampleCompleted();';
    strHTML += ' window.opener.setAction(window.opener.document.forms[0], "Update", "yes", "?ID=");self.close();} ';
      
    strHTML += ' setTimeout("impor()",359999);</SCRIPT'; 
    strHTML += '><title>' + "<%=title%>" + '</title></head>'; 
    strHTML += '<body onBlur="fnHandleFocus();" onload="fnHandleFocus();" ><form name="confirmSaveIt" method="get" action=""><div id="popupBody"><table><tr><td class="popuplistdata">';
    strHTML += confirmsave_message;
  
    strHTML += '<br><center><input type="button"  name="save" value="' + "<%=button1%>" + '" onClick="goToSave();" />';
    strHTML += "<%=space%>";
    strHTML += '<input type="button"  name="cancel" value="' + "<%=button2%>" + '" onClick="window.opener.clearClicked();self.close();" /></center></div>';
    strHTML += '</td></tr></table></form></body></html>'; 

     myWin.document.write(strHTML); 

     myWin.window.document.close(); 

     setTimeout ('myWin.close()', 360000); 
}


</script>    
<logic:notEmpty name="<%=formName%>" property="accessionNumber">
    <logic:notEmpty name="<%=formName%>" property="testQaEvents">
       <% assignedTestsExist = "true"; %>
       <logic:iterate id="test_qaEvents" name="<%=formName%>" property="testQaEvents" type="us.mn.state.health.lims.qaevent.valueholder.Test_QaEvents">
         <logic:notEmpty property="qaEvents" name="test_qaEvents">
            <% qaEventsExist = "true"; %>
         </logic:notEmpty>
       </logic:iterate>
    </logic:notEmpty>
    
<center>
<table border="0" cellpadding="0" cellspacing="0">
	<tbody valign="middle">
	 <tr width="100%">
		   <% 	
		    String allowEdits = "true";
		    if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
    		  	allowEdits = (String)request.getAttribute(us.mn.state.health.lims.common.action.IActionConstants.ALLOW_EDITS_KEY);
    		}
    		String saveDisabled = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
    		String saveSampleDisabled = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
            //bugzilla 2033 disable add actions if events are completed
    		String addSampleEventsDisabled = (String)request.getAttribute(IActionConstants.ADD_DISABLED);
    		String addSampleActionsDisabled = (String)request.getAttribute(IActionConstants.ADD_DISABLED);
    		
    		if (request.getAttribute(IActionConstants.ADD_DISABLED_ALL_SAMPLE_QAEVENTS_COMPLETED) != null) {
	    	 String addSampleActionsDisabledAllCompleted = (String)request.getAttribute(IActionConstants.ADD_DISABLED_ALL_SAMPLE_QAEVENTS_COMPLETED);
   		     if (addSampleActionsDisabledAllCompleted.equals("true")) {
   		      addSampleActionsDisabled = "true";
   		     }
   		    }
    		
           %>
       <td width="5%">&nbsp;</td>
       <td width="8%">
            	<html:button onclick="gotoEditSample(document.forms[0]);"
						    property="editsample" disabled="<%=Boolean.valueOf(editSampleForCircularReferenceDisabled).booleanValue()%>">
  			       <bean:message key="qaeventsentry.label.button.edit.sample"/>
  		    	</html:button>
       </td>
       <td width="8%">
            	<html:button onclick="gotoResultsEntry(document.forms[0]);"
						    property="resultsentry" disabled="<%=Boolean.valueOf(resultsEntryForCircularReferenceDisabled).booleanValue()%>">
  			       <bean:message key="qaeventsentry.label.button.resultsentry"/>
  		    	</html:button>
       </td> 
       <td width="4%">&nbsp;</td>
       <td width="8%">
            	<html:button onclick="popupAddEventsToSample(document.forms[0]);"
						    property="addsamppleevents" disabled="<%=Boolean.valueOf(addSampleEventsDisabled).booleanValue()%>">
  			       <bean:message key="qaeventsentry.label.button.add.events"/>
  		    	</html:button>
       </td>
       <td width="8%">
            	<html:button onclick="popupAddActionsToSample(document.forms[0]);"
							 property="addsampleactions" disabled="<%=Boolean.valueOf(addSampleActionsDisabled).booleanValue()%>">
  			       <bean:message key="qaeventsentry.label.button.add.actions"/>
  		    	</html:button>
       </td>
   	   <td width="4%">&nbsp;</td>
   	   <td width="8%"><%--bugzilla 2504  changed function name to be more accurate--%>
            	<html:button onclick="confirmCompleteAllForSample()"
							 property="completeallforsample" disabled="<%=Boolean.valueOf(addSampleActionsDisabled).booleanValue()%>">
  			       <bean:message key="qaeventsentry.label.button.complete.all"/>
  		    	</html:button>
       </td>
  	   <td width="4%">&nbsp;</td>
       <td width="8%">
                <html:button onclick="if(checkClicked()) 
							 {
							 	return false;
							 }
							 else {
							   toggleSampleView();
							 }"
							 property="tsv">
		           <% if (viewMode.equals(normal)) { %>
  			       <bean:message key="qaeventsentry.label.button.full.screen.view"/>
  			       <% } else { %>
   			       <bean:message key="qaeventsentry.label.button.normal.view"/>
   			       <% } %>
   			       
  		    	</html:button>
  		</td>
  		<td width="2%">&nbsp;</td>
  		<td width="8%">
  		    	<% if (viewMode.equals(fullScreen)) { %>
  		      	<html:button onclick="if(checkClicked()) 
							 {
							 	return false;
							 }
							 else {
							   toggleFromSampleToTestView();
							 }"
							 property="tsv">
  			       <bean:message key="qaeventsentry.label.button.full.screen.view.test"/>
   		    	</html:button>
   		    	<% }  else { %>
   		    	&nbsp;
   		    	<% }%>
   		</td>
   		<td width="4%">&nbsp;</td>
   		<td width="8%">
		    	<% if (viewMode.equals(fullScreen)) { %>
   		    	<html:button onclick="if(checkClicked()) 
							 {
							 	return false;
							 }
							 else {
							   saveThis(document.forms[0]);
							 }" property="savesample" disabled="<%=(Boolean.valueOf(saveDisabled).booleanValue()) || (Boolean.valueOf(addSampleActionsDisabled).booleanValue())%>">
  			       <bean:message key="label.button.save"/>
  		    	</html:button>
  		    	<% } else { %>
  		    	&nbsp;
  		    	<% } %>
  		 </td>
  		 <td width="2%">&nbsp;</td>
  		 <td width="8%">
 		    	<% if (viewMode.equals(fullScreen)) { %>
			    <html:button onclick="cancelQaEventsEntry();"  property="cancelsample" >
  			      <bean:message key="label.button.exit"/>
  			    </html:button>
  		    	<% } else {  %>
  		    	&nbsp;
  		    	<% } %>
         </td>
        <td width="13%">&nbsp;</td>
 	    </tr>
	 </tbody>
</table>
</center>
</logic:notEmpty>
