<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants, java.util.Locale"
    import="us.mn.state.health.lims.common.util.Versioning"
%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<%--added for bugzilla 2501--%>
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
	
String previousDisabled = "false";
String nextDisabled = "false"; 
if (request.getAttribute(IActionConstants.PREVIOUS_DISABLED) != null) {
    previousDisabled = (String)request.getAttribute(IActionConstants.PREVIOUS_DISABLED);
}
if (request.getAttribute(IActionConstants.NEXT_DISABLED) != null) {
    nextDisabled = (String)request.getAttribute(IActionConstants.NEXT_DISABLED);
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
function toggleTestView() {
  var viewMode = '<%=viewMode%>';
  var param = "";

  //which mode do we switch to?
   if (viewMode == "<%=normal%>") {
    //goto fullscreen view
    document.getElementById("viewMode").value = "<%=fullScreen%>"
    document.getElementById("fullScreenSection").value = "<%=testSection%>";
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

function toggleFromTestToSampleView() { 
   document.getElementById("fullScreenSection").value = "<%=sampleSection%>";
   var checkbox = document.getElementById("multipleSampleMode");
   if(checkbox.checked) {
   //bugzilla 2501 go to multiple sample mode positioning to specific accession number
     setAction(window.document.forms[0], 'PositionToRecord', 'no', '?<%=accessionNumberParm%>=' + $F("accessionNumber") + '&ID=');
   } else {
     setAction(window.document.forms[0], 'PreView', 'yes', '?ID=');
   }

}

function checkAllTestsCompleted() {
      var checkState = true;
      var selectedRows = window.document.forms[0].elements['selectedAnalysisQaEventIdsForCompletion'];
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


function confirmCompleteAllTests()
{
  var myWin = createSmallConfirmPopup( "", null , null );
  
   <% 
      
      out.println("var message = null;");
       
      String confirmsave_message =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "qaeventsentry.message.popup.confirm.complete.all.test");
         
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
    strHTML += ' window.opener.checkAllTestsCompleted();';
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
<%--bugzilla 2501--%>  
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
		<tr>
		   <% 	
		    String allowEdits = "true";
		    if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
    		  	allowEdits = (String)request.getAttribute(us.mn.state.health.lims.common.action.IActionConstants.ALLOW_EDITS_KEY);
    		}
    		String saveDisabled = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
            //bugzilla 2033 disable add actions if events are completed
    		String addTestEventsDisabled = (String)request.getAttribute(IActionConstants.ADD_DISABLED);
    		String addTestActionsDisabled = (String)request.getAttribute(IActionConstants.ADD_DISABLED);
    		
    		if (request.getAttribute(IActionConstants.ADD_DISABLED_ALL_TEST_QAEVENTS_COMPLETED) != null) {
	    	 String addTestActionsDisabledAllCompleted = (String)request.getAttribute(IActionConstants.ADD_DISABLED_ALL_TEST_QAEVENTS_COMPLETED);
   		     if (addTestActionsDisabledAllCompleted.equals("true")) {
   		      addTestActionsDisabled = "true";
   		     }
   		    }
   		    
     		String addSampleActionsDisabled = (String)request.getAttribute(IActionConstants.ADD_DISABLED);
    		
    		if (request.getAttribute(IActionConstants.ADD_DISABLED_ALL_SAMPLE_QAEVENTS_COMPLETED) != null) {
	    	 String addSampleActionsDisabledAllCompleted = (String)request.getAttribute(IActionConstants.ADD_DISABLED_ALL_SAMPLE_QAEVENTS_COMPLETED);
   		     if (addSampleActionsDisabledAllCompleted.equals("true")) {
   		      addSampleActionsDisabled = "true";
   		     }
   		    }
    		
           %>
          <td width="25%">&nbsp;</td>
          <td width="8%">
            	<html:button onclick="popupAddEventsToTest(document.forms[0]);"
						    property="addtestevents" disabled="<%=Boolean.valueOf(addTestEventsDisabled).booleanValue()%>">
  			       <bean:message key="qaeventsentry.label.button.add.events"/>
  		    	</html:button>
          </td>
          <td width="8%">
            	<html:button onclick="popupAddActionsToTest(document.forms[0]);"
							 property="addtestactions" disabled="<%=Boolean.valueOf(addTestActionsDisabled).booleanValue()%>">
  			       <bean:message key="qaeventsentry.label.button.add.actions"/>
  		    	</html:button>
          </td>
   	      <td width="4%">&nbsp;</td>
   		  <td width="8%">
            	<html:button onclick="confirmCompleteAllTests()"
							 property="completealltests" disabled="<%=Boolean.valueOf(addTestActionsDisabled).booleanValue()%>">
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
							   toggleTestView();
							 }"
							   property="ttv">
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
							   toggleFromTestToSampleView();
							 }"
							 property="ttv">
  			       <bean:message key="qaeventsentry.label.button.full.screen.view.sample"/>
   		    	</html:button>
   		    	<% } else { %>
   		    	&nbsp;
   		    	<% } %>
          </td>
   	      <td width="4%">&nbsp;</td>
          <td width="8%">
   		    	<% if (viewMode.equals(fullScreen)) { 
   		    	    if (!Boolean.valueOf(saveDisabled).booleanValue()) {
   		    	       saveDisabled = addTestActionsDisabled;
   		    	    }
   		    	   } else {
   		    	     //in normal mode -> need to check both samples and tests to see if we want to disable the save button
   		    	     if (!Boolean.valueOf(saveDisabled).booleanValue()) {
   		    	       if (addTestActionsDisabled == "true" && addSampleActionsDisabled == "true") {
   		    	          saveDisabled = "true";
   		    	       }
   		    	     }
   		    	   }
   		    	%>
   		    	
            	<html:button onclick="if(checkClicked()) 
							 {
							 	return false;
							 }
							 else {
							   saveThis(document.forms[0]);
							   //bugzilla 2033 comment#24 
							   //bugzilla 2501 if saveDisabled = true or both addTestActionsDisabled AND addSampleActionsDisabled are true then disable the save button
							 }" property="savetest" disabled="<%=Boolean.valueOf(saveDisabled).booleanValue()%>">
  			       <bean:message key="label.button.save"/>
  		    	</html:button>
            </td>
          <td width="2%">&nbsp;</td>
          <td width="8%">
			<html:button onclick="cancelQaEventsEntry();"  property="canceltest" >
  			   <bean:message key="label.button.exit"/>
  			</html:button>
	      </td>
  		  <td width="13%">&nbsp;</td>
 	    </tr>
	 </tbody>
</table>
</center>
</logic:notEmpty>
<logic:empty name="<%=formName%>" property="accessionNumber">
<center>
<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tbody valign="middle">
	    <tr height="22"><td>&nbsp;</td></tr>
		<tr>
		   <% 	
		    String allowEdits = "true";
		    if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
    		  	allowEdits = (String)request.getAttribute(us.mn.state.health.lims.common.action.IActionConstants.ALLOW_EDITS_KEY);
    		}
           %>
		<td width="47%">&nbsp;</td>
		<td width="6%">
  			<html:button onclick="setAction(window.document.forms[0], 'Cancel', 'no', '');"  property="cancel" >
  			   <bean:message key="label.button.exit"/>
  			</html:button>
	    </td>
		<td width="47%">&nbsp;</td>
 	    </tr>
	 </tbody>
</table>
</center>
</logic:empty>