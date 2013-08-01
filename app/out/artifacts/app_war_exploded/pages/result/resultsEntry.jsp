<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date, java.util.List, java.util.Locale,
	org.apache.struts.Globals,
	us.mn.state.health.lims.common.action.IActionConstants,
	us.mn.state.health.lims.common.util.StringUtil,
	us.mn.state.health.lims.common.util.DateUtil,
	us.mn.state.health.lims.note.valueholder.Note,
	us.mn.state.health.lims.result.valueholder.Result,
	us.mn.state.health.lims.testresult.valueholder.TestResult,
	us.mn.state.health.lims.result.valueholder.TestAnalyte_TestResults,
    us.mn.state.health.lims.common.util.Versioning,
	us.mn.state.health.lims.common.util.SystemConfiguration" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>
<%--bugzilla 1908 changed some disabled values for Vietnam tomcat/linux--%>
<%--bugzilla 1933 change for dictType, titerType, numericType error--%>
<%--bugzilla 1942: status changes, can now change result to blank, enable notes/disable external notes if analysis status is released--%>
<%--bugzilla 2216 made title (header) static, reduced height of scrollbox div--%>
<%--bugzilla 1798 added functionality to link child test to parent test result--%>
<%--bugzilla 1510 add styleId for compatibility in firefox and for use of firebug debugger--%>
<%--bugzilla 1802 - changes for screen redesign--%>
<%--bugzilla 1883 - changes for new logic for when to popup reflex tests
    we will no longer show disabled options on the popup -- show popup only 
    if reflex tests that haven't been triggered before
   (i.e. where parent analysis/parent result and result analyte/added test are same)
    all reflex tests on popup will be preselected and enabled 
   - have removed variable listOfExistingTestIds as we don't need to check this in popup action for disabling of options
   - only allow a test result to trigger a popup if it has just been changed (isDirty)
--%>
<%--bugzilla 2308 adjust widths to align headers with columns--%>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="idSeparator" value='<%= SystemConfiguration.getInstance().getDefaultIdSeparator() %>' />
<bean:define id="textSeparator" value='<%= SystemConfiguration.getInstance().getDefaultTextSeparator() %>' />
<bean:define id="notesRefTableId" value='<%= SystemConfiguration.getInstance().getResultReferenceTableId() %>' />
<%--bugzilla 2446--%>
<bean:define id="internalNote" value='<%= SystemConfiguration.getInstance().getNoteTypeInternal() %>' />
<bean:define id="externalNote" value='<%= SystemConfiguration.getInstance().getNoteTypeExternal() %>' />
<bean:define id="selectedTestIdParm" value='<%= IActionConstants.SELECTED_TEST_ID%>' />
<bean:define id="analysisIdParm" value='<%= IActionConstants.ANALYSIS_ID%>' />
<bean:define id="analyteIdParm" value='<%= IActionConstants.ANALYTE_ID%>' />
<bean:define id="dictType" value='<%= SystemConfiguration.getInstance().getDictionaryType() %>' />
<bean:define id="titerType" value='<%= SystemConfiguration.getInstance().getTiterType() %>' />
<bean:define id="numericType" value='<%= SystemConfiguration.getInstance().getNumericType() %>' />
<bean:define id="reflexedType" value='<%= IActionConstants.CHILD_TYPE_REFLEX %>' />
<bean:define id="linkedType" value='<%= IActionConstants.CHILD_TYPE_LINK %>' />
<bean:define id="unsatisfactoryResultValue" value='<%= IActionConstants.UNSATISFACTORY_RESULT%>' />
<bean:define id="analysisReleasedStatus" value='<%= SystemConfiguration.getInstance().getAnalysisStatusReleased() %>' />

<%!

String allowEdits = "true";
String analysisId = "";

Locale locale = null;
String errorNoResult = "";
String errorNoIsReportableFlag = "";
String errorChangeToNoResult = "";
String yesOption = "";
String noOption = "";
String errorNoteReflexTestFound = "";
String errorNoteUnsatisfactoryResultFound = "";
String errorAmendUnsatisfactoryResultFound = "";
String tooltipheader = "";
String errorSaveChangesBeforeLinkUnlink = "";
//bugzilla 2446
String internalNotesHeading = "";
String externalNotesHeading = "";

String refId = "";
String notesAttached = "false";

int myCounter = 0;
int ctr1 = 0;
int ctr2 = 0;
int ctr3 = 0; 
int ctr4 = 0;

String path = "";
String basePath = "";

String message = "";
String button1 = "";
String button2 = "";
String button3 = "";
String title = "";
String space = "";
//bugzilla 2322
String resultDetail = "";
String resultConfirm = "";
%>
<%
path = request.getContextPath();
basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";

if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

//this is the test selected for editing
analysisId = (String)request.getAttribute(IActionConstants.ANALYSIS_ID);

locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
errorNoResult =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "resultsentry.notesPopup.noresult.error");
errorNoIsReportableFlag =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "resultsentry.notesPopup.noisreportableflag.error");
errorChangeToNoResult = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "resultsentry.changetonoresult.error");
yesOption = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "page.default.yes.option");
noOption = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "page.default.no.option");
                    
errorNoteReflexTestFound = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "note.notespopup.error.reflex.test.found");
                    
errorNoteUnsatisfactoryResultFound =
                    us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "note.notespopup.error.unsatisfactory.result.found");
errorAmendUnsatisfactoryResultFound =
                    us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "amend.error.unsatisfactory.result.found");
tooltipheader = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "resultsentry.tooltip.header");
                    
errorSaveChangesBeforeLinkUnlink = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "resultsentry.linkChildTestToParentTestResultPopup.savechanges.message");
//bugzilla 2446
internalNotesHeading = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "note.type.internal.heading");
externalNotesHeading = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "note.type.external.heading");              

//bugzilla 2322
resultDetail =      us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "resultentry.detail");
                    
resultConfirm =     us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "resultentry.confirm");
%>

<!--bugzilla 2433-->
<logic:empty name="<%=formName%>" property="testTestAnalytes">
<% request.setAttribute(IActionConstants.SAVE_DISABLED, IActionConstants.TRUE); %>
</logic:empty>

<!--bugzilla 2322-->
<!--bugzilla 2628 problem with single quote in dict entry-->
<% int index = 0; %>
<script language="JavaScript">
    var dictArray = new Array();   
</script>    
<logic:notEmpty name="<%=formName%>" property="testTestAnalytes">
    <logic:iterate id="tst_ta" indexId="ctr" name="<%=formName%>" property="testTestAnalytes" type="us.mn.state.health.lims.result.valueholder.Test_TestAnalyte">
       <%--bugzilla 2542 fix to allow oc4j to work: cannot define same bean twice in one page..rename tas to tas2--%>
        <bean:define id="tas2" name="tst_ta" property="testAnalytes" />
            <logic:notEmpty name="tas2">
                <logic:iterate id="ta" indexId="taCtr" name="tst_ta" property="testAnalyteTestResults" type="us.mn.state.health.lims.result.valueholder.TestAnalyte_TestResults">          
          
                <!--build dictionary array list-->
                <script>
                <%
                    List testResults = ta.getTestResults();
                    for ( int i=0; i<testResults.size(); i++ ) {
                        TestResult tr = (TestResult)testResults.get(i);  
                    %>            
                        dictArray[<%=index++%>] = "<%=tr.getId()%>" + "|TRID|" + "<%= tr.getValue() %>";
                    <%                
                    }
                %> 
                </script>                                      
                
            </logic:iterate>
         </logic:notEmpty>             
    </logic:iterate>
</logic:notEmpty> 

<script language="JavaScript1.2">

function submitTheFormWithAddedReflexTests(form) {
   //alert("I am going to submit the form with added reflex tests " + form.name + " " + document.forms[0].addedReflexTestIds);
   var addedReflexTestIds = document.forms[0].addedReflexTestIds.value;
   var addedReflexTestParentResultIds = document.forms[0].addedReflexTestParentResultIds.value;
   //bugzilla 1882
   var addedReflexTestParentAnalyteIds = document.forms[0].addedReflexTestParentAnalyteIds.value;
   var addedReflexTestParentAnalysisIds = document.forms[0].addedReflexTestParentAnalysisIds.value;
   var param = '?' + '<%=analysisIdParm%>' + '=' + '<%=analysisId%>';
   param += '&addedReflexTestIds=' + addedReflexTestIds;
   param += '&addedReflexTestParentResultIds=' + addedReflexTestParentResultIds;
   //bugzilla 1882
   param += '&addedReflexTestParentAnalyteIds=' + addedReflexTestParentAnalyteIds;
   param += '&addedReflexTestParentAnalysisIds=' + addedReflexTestParentAnalysisIds;
   param += '&ID=';
   //alert("param " + param);
   setAction(window.document.forms[0], 'Update', 'yes', param);
}

//bugzila 2501 added notesRefTableId
function submitTheFormWithNotes(form, notesRefId, notesRefTableId) {
   document.forms[0].noteRefId.value = notesRefId;   
   var noteIds = document.forms[0].noteIds.value;
   var param = '?' + '<%=analysisIdParm%>' + '=' + '<%=analysisId%>';
   param += '&ID=';
   setAction(form, 'NotesUpdate', 'yes', param);
}

//bugzilla 1798
function submitTheFormWithLinkedTest(linkedTestInformation) {
   //alert("I am going to submit the form with linked test " + linkedTestInformation);
   document.forms[0].linkedParentInformationString.value = linkedTestInformation;
   var param = '';
   setAction(document.forms[0], 'Update', 'yes', param);
}

function confirmSaveForwardToNotesPopup(analyteId, selectedTestId, resultId)
{
  var myWin = createSmallConfirmPopup( "", null , null );
  
   <% 
      
      out.println("var message = null;");
       
      message =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "message.popup.confirm.saveandforward");
         
     out.println("message = '" + message +"';");

     button1 = 	us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					    locale,
                       "label.button.yes");
     button2 = 	us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					    locale,
                       "label.button.no");
     button3 = 	us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					    locale,
                       "label.button.cancel");
                       
     title = 	us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					    locale,
                       "title.popup.confirm.saveandforward");
                       
     space = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
       		
 %> 

    var href = "<%=basePath%>css/openElisCore.css?ver=<%= Versioning.getBuildNumber() %>";
 
    var strHTML = ""; 
  
    strHTML = '<html><link rel="stylesheet" type="text/css" href="' + href + '" /><head><'; 
    //bugzilla 1510 cross-browser use hasFocus not hasFocus()
    //bugzilla 1903 fix problem - go back to hasFocus() for now     
    strHTML += 'SCRIPT LANGUAGE="JavaScript">var tmr = 0;function fnHandleFocus(){if(window.opener.document.hasFocus()){window.focus();clearInterval(tmr);tmr = 0;}else{if(tmr == 0)tmr = setInterval("fnHandleFocus()", 500);}}</SCRIPT'; 
    strHTML += '><'; 
    strHTML += 'SCRIPT LANGUAGE="javascript" >'
    strHTML += ' var imp= null; function impor(){imp="norefresh";} '
    strHTML += ' function fcl(){  if(imp!="norefresh") {  opener.reFreshCurWindow(); }}';
     
    strHTML += '  function goToNotesPopupActionSave(){ ';
    var analyteParm = '?' + '<%=analyteIdParm%>' + '=' + analyteId;
    var selectedTestParm = '&' + '<%=selectedTestIdParm%>' + '=' + selectedTestId;
    strHTML += ' var reqParms = "' + analyteParm + selectedTestParm + '&direction=previous&ID=";';
    strHTML += ' window.opener.setAction(window.opener.document.forms[0], "UpdateToNotesPopup", "yes", reqParms);self.close();} ';
      
    strHTML += '  function goToNotesPopupActionNoSave(){ ';
    var resultTableId = '<%=notesRefTableId%>';
    strHTML += ' var resultTableId = "' + resultTableId + '";';
    strHTML += ' var resultId = "' + resultId + '";';
    strHTML += ' window.opener.popupNotes(window.opener.document.forms[0], resultTableId, resultId);self.close();} ';
    
    strHTML += ' setTimeout("impor()",359999);</SCRIPT'; 
    strHTML += '><title>' + "<%=title%>" + '</title></head>'; 
    strHTML += '<body onBlur="fnHandleFocus();" onload="fnHandleFocus();" ><form name="confirmSaveIt" method="get" action=""><div id="popupBody"><table><tr><td class="popuplistdata">';
    strHTML += message;
  
    strHTML += '<br><center><input type="button"  name="save" value="' + "<%=button1%>" + '" onClick="goToNotesPopupActionSave();" />';
    
    //this option to not save can only be allowed if the result has already been saved to the database (resultId is not null)
   if (resultId != null && resultId != '0') {
      strHTML += "<%=space%>";
      strHTML += '<input type="button"  name="save" value="' + "<%=button2%>" + '" onClick="goToNotesPopupActionNoSave();"/>';
    }
  
    strHTML += "<%=space%>";
    strHTML += '<input type="button"  name="cancel" value="' + "<%=button3%>" + '" onClick="self.close();" /></center></div>';
    strHTML += '</td></tr></table></form></body></html>'; 

     myWin.document.write(strHTML); 

     myWin.window.document.close(); 

     setTimeout ('myWin.close()', 360000); 
}

function confirmSaveAndAmendPopup(analysisId)
{
  var myWin = createSmallConfirmPopup( "", null , null );
  
   <% 
      
      out.println("var message = null;");
       
      //this is already defined in a function above
      message =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "resultsentry.message.popup.confirm.save.and.amend");
         
     out.println("message = '" + message +"';");

     button1 = 	us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					    locale,
                       "label.button.yes");
     button2 = 	us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					    locale,
                       "label.button.no");
     button3 = 	us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					    locale,
                       "label.button.cancel");
                       
     title = 	us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					    locale,
                       "title.popup.confirm.saveandforward");
                       
     space = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
       		
 %> 

    var href = "<%=basePath%>css/openElisCore.css?ver=<%= Versioning.getBuildNumber() %>";
 
    var strHTML = ""; 
  
    strHTML = '<html><link rel="stylesheet" type="text/css" href="' + href + '" /><head><'; 
    //bugzilla 1510 cross-browser use hasFocus not hasFocus()
    //bugzilla 1903 fix problem - go back to hasFocus() for now     
    strHTML += 'SCRIPT LANGUAGE="JavaScript">var tmr = 0;function fnHandleFocus(){if(window.opener.document.hasFocus()){window.focus();clearInterval(tmr);tmr = 0;}else{if(tmr == 0)tmr = setInterval("fnHandleFocus()", 500);}}</SCRIPT'; 
    strHTML += '><'; 
    strHTML += 'SCRIPT LANGUAGE="javascript" >'
    strHTML += ' var imp= null; function impor(){imp="norefresh";} '
    strHTML += ' function fcl(){  if(imp!="norefresh") {  opener.reFreshCurWindow(); }}';
     
    strHTML += '  function goToAmendedResultsEntryActionSave(){ ';
    var analysisParm = '?' + '<%=analysisIdParm%>' + '=' + analysisId;
    strHTML += ' var reqParms = "' + analysisParm + '";';
    strHTML += ' window.opener.setAction(window.opener.document.forms[0], "UpdateToAmend", "yes", reqParms + "&ID=");self.close();} ';
      
    strHTML += '  function goToAmendedResultsEntryActionNoSave(){ ';
    var analysisParm = '?' + '<%=analysisIdParm%>' + '=' + analysisId;
    strHTML += ' var reqParms = "' + analysisParm + '";';
    strHTML += ' window.opener.setAction(window.opener.document.forms[0], "UpdateAmended", "yes", reqParms + "&ID=");self.close();} ';
    
    strHTML += ' setTimeout("impor()",359999);</SCRIPT'; 
    strHTML += '><title>' + "<%=title%>" + '</title></head>'; 
    strHTML += '<body onBlur="fnHandleFocus();" onload="fnHandleFocus();" ><form name="confirmSaveIt" method="get" action=""><div id="popupBody"><table><tr><td class="popuplistdata">';
    strHTML += message;
  
    strHTML += '<br><center><input type="button"  name="save" value="' + "<%=button1%>" + '" onClick="goToAmendedResultsEntryActionSave();" />';
    strHTML += "<%=space%>";
    strHTML += '<input type="button"  name="save" value="' + "<%=button2%>" + '" onClick="goToAmendedResultsEntryActionNoSave();"/>';
  
    strHTML += "<%=space%>";
    strHTML += '<input type="button"  name="cancel" value="' + "<%=button3%>" + '" onClick="self.close();" /></center>';
    strHTML += '</td></tr></table></div></form></body></html>'; 

     myWin.document.write(strHTML); 

     myWin.window.document.close(); 

     setTimeout ('myWin.close()', 360000); 
}

function confirmAmendPopup(analysisId)
{
  var myWin = createSmallConfirmPopup( "", null , null );
  
   <% 
      
      out.println("var message = null;");
       
      //this is already defined in a function above
      message =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "resultsentry.message.popup.confirm.amend");
         
     out.println("message = '" + message +"';");

     button1 = 	us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					    locale,
                       "label.button.yes");
     button2 = 	us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					    locale,
                       "label.button.no");
                      
     title = 	us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					    locale,
                       "title.popup.confirm.saveandforward");
                       
     space = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
       		
 %> 

    var href = "<%=basePath%>css/openElisCore.css?ver=<%= Versioning.getBuildNumber() %>";
 
    strHTML = '<html><link rel="stylesheet" type="text/css" href="' + href + '" /><head><'; 
    //bugzilla 1510 cross-browser use hasFocus not hasFocus()
    //bugzilla 1903 fix problem - go back to hasFocus() for now     
    strHTML += 'SCRIPT LANGUAGE="JavaScript">var tmr = 0;function fnHandleFocus(){if(window.opener.document.hasFocus()){window.focus();clearInterval(tmr);tmr = 0;}else{if(tmr == 0)tmr = setInterval("fnHandleFocus()", 500);}}</SCRIPT'; 
    strHTML += '><'; 
    strHTML += 'SCRIPT LANGUAGE="javascript" >'
    strHTML += ' var imp= null; function impor(){imp="norefresh";} '
    strHTML += ' function fcl(){  if(imp!="norefresh") {  opener.reFreshCurWindow(); }}';
     
    strHTML += '  function goToAmendedResultsEntryActionNoSave(){ ';
    var analysisParm = '?' + '<%=analysisIdParm%>' + '=' + analysisId;
    strHTML += ' var reqParms = "' + analysisParm + '";';
    strHTML += ' window.opener.setAction(window.opener.document.forms[0], "UpdateAmended", "yes", reqParms + "&ID=");self.close();} ';
    
    strHTML += ' setTimeout("impor()",359999);</SCRIPT'; 
    strHTML += '><title>' + "<%=title%>" + '</title></head>'; 
    strHTML += '<body onBlur="fnHandleFocus();" onload="fnHandleFocus();" ><form name="confirmSaveIt" method="get" action=""><div id="popupBody"><table><tr><td class="popuplistdata">';
    strHTML += message;
  
    strHTML += '<br><center><input type="button"  name="save" value="' + "<%=button1%>" + '" onClick="goToAmendedResultsEntryActionNoSave();" />';
  
    strHTML += "<%=space%>";
    strHTML += '<input type="button"  name="cancel" value="' + "<%=button2%>" + '" onClick="self.close();" /></center>';
    strHTML += '</td></tr></table></div></form></body></html>'; 

    myWin.document.write(strHTML); 

    myWin.window.document.close(); 

    setTimeout ('myWin.close()', 360000); 
}

//bugzilla 1798
function confirmUnlink(analysisId)
{
  var myWin = createSmallConfirmPopup( "", null , null );
  
   <% 
      
      out.println("var message = null;");
       
      String unlink_message =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "resultsentry.linkChildTestToParentTestResult.message.confirm.unlink");
         
     out.println("unlink_message = '" + unlink_message +"';");

     String unlink_button1 = 	us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					    locale,
                       "label.button.yes");
     String unlink_button2 = 	us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					    locale,
                       "label.button.cancel");
                       
     String unlink_title = 	us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					    locale,
                       "title.popup.confirm.saveandforward");
                       
     String unlink_space = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
       		
 %> 

    var href = "<%=basePath%>css/openElisCore.css?ver=<%= Versioning.getBuildNumber() %>";
 
    var strHTML = ""; 
  
    strHTML = '<html><link rel="stylesheet" type="text/css" href="' + href + '" /><head><'; 
    strHTML += 'SCRIPT LANGUAGE="JavaScript">var tmr = 0;function fnHandleFocus(){if(window.opener.document.hasFocus()){window.focus();clearInterval(tmr);tmr = 0;}else{if(tmr == 0)tmr = setInterval("fnHandleFocus()", 500);}}</SCRIPT'; 
    strHTML += '><'; 
    strHTML += 'SCRIPT LANGUAGE="javascript" >'
    strHTML += ' var imp= null; function impor(){imp="norefresh";} '
    strHTML += ' function fcl(){  if(imp!="norefresh") {  opener.reFreshCurWindow(); }}';
     
    strHTML += '  function goToUnlink(){ ';
    strHTML += ' window.opener.document.forms[0].unlinkedParentInformationString.value = ' + analysisId + ';';
    strHTML += ' var reqParms = "?direction=previous&ID=";';
    strHTML += ' window.opener.setAction(window.opener.document.forms[0], "Update", "yes", reqParms);self.close();} ';
      
    strHTML += ' setTimeout("impor()",359999);</SCRIPT'; 
    strHTML += '><title>' + "<%=unlink_title%>" + '</title></head>'; 
    strHTML += '<body onBlur="fnHandleFocus();" onload="fnHandleFocus();" ><form name="confirmSaveIt" method="get" action=""><div id="popupBody"><table><tr><td class="popuplistdata">';
    strHTML += unlink_message;
  
    strHTML += '<br><center><input type="button"  name="save" value="' + "<%=unlink_button1%>" + '" onClick="goToUnlink();" />';
    strHTML += "<%=unlink_space%>";
    strHTML += '<input type="button"  name="cancel" value="' + "<%=unlink_button2%>" + '" onClick="self.close();" /></center></div>';
    strHTML += '</td></tr></table></form></body></html>'; 

     myWin.document.write(strHTML); 

     myWin.window.document.close(); 

     setTimeout ('myWin.close()', 360000); 
}


function isBlankResult(form, ctr) {
   //get selected testResult if one is selected
   var testResultElement = 'selectedTestResultIds[' + ctr + ']';
   var selectedTestResultId = document.getElementById(testResultElement).value;
   if (selectedTestResultId == null || selectedTestResultId == '') {
     alert('<%=errorNoResult%>');
     return true;
   }
   return false;
}

function isBlankIsReportableFlag(form, ctr) {
  //get selected isReportableFlag if one is selected
  var isReportableElement = 'selectedResultIsReportableFlags[' + ctr + ']';
  var selectedResultIsReportableFlag = document.getElementById(isReportableElement).value;
  	if (selectedResultIsReportableFlag == null || selectedResultIsReportableFlag == '') {
    	 alert('<%=errorNoIsReportableFlag%>');
     	return true;
  	}
  return false;
}


function myPopupNotes(form, resultId, ctr, analyteId, selectedTestId, disabled) {
  //make sure a result is selected
  if (! isBlankResult(form, ctr)) {
   if (! isBlankIsReportableFlag(form, ctr)) {
     if (isDirty(form, '')) {
        if (showReflexTestPopup() == "true" ) {
           //If there are possible reflex tests then make User save these before editing notes!
           alert('<%=errorNoteReflexTestFound%>');
        } else if (foundUnsatisfactoryResult(form)) {
           //Don't allow forward to notes if we are required to go to Qa Events Entry
           alert('<%=errorNoteUnsatisfactoryResultFound%>');
        } else {
        //we need analyteId to know which result note was clicked, we need selectedTestId to get back to same test selection after form update, 
        //we need resultId: if it is null then we cannot allow going to notes without a SAVE
        confirmSaveForwardToNotesPopup(analyteId, selectedTestId, resultId); 
        }
     } else {
        //bugzilla 1942 add disabled param: disable external notes if analysis status is released
        popupNotes(document.forms[0], '<%=notesRefTableId%>', resultId, disabled);
     }
    }
  }
}

//bugzilla 2361 modfiied handling of titer and numeric type validation
function validateResultValue(value, testResultId){
	//alert ("In validateResultValue "+ value + ""+ testResultId );	
    new Ajax.Request (
                  'ajaxXML',  //url
                   {//options
                     method: 'get', //http method
                     parameters: 'provider=ResultsValueValidationProvider&field=resultValueN&val=' + value + '&trId=' + testResultId + '&clientType=js',      //request parameters
                     //indicator: 'throbbing'
                     onSuccess:  processSuccess,
                     onFailure:  processFailure
                   }
                  );
}

//determines whether we need to show reflex test popup
function showReflexTestPopup() {
  var showpopup = 'false';
  
  var listOfSelectedIds = '';
  <% ctr1 = 0; %>
  <logic:notEmpty name="<%=formName%>" property="testTestAnalytes">
  <logic:iterate id="tst_ta" name="<%=formName%>" property="testTestAnalytes" >
  <bean:define id="analysisId2" name="tst_ta" property="analysis.id"/>
  <logic:notEmpty name="tst_ta" property="testAnalyteTestResults">
  <logic:iterate id="ta_Tr_bean2" indexId="ctr" name="tst_ta" property="testAnalyteTestResults" type="us.mn.state.health.lims.result.valueholder.TestAnalyte_TestResults">
   <bean:define id="testAnalyte2" name="ta_Tr_bean2" property="testAnalyte"/>
   <bean:define id="analyteId2" name="testAnalyte2" property="analyte.id"/>
  //get the value of testresult selected (we need this to pass on to popup as listOfSelectedIds
     var dropDownSelect = document.forms[0].elements['selectedTestResultIds[<%=ctr1%>]'];
     var length = dropDownSelect.length;
     var selectedValue;
     var selectedIndex;
 
     //bugzilla 1883 doesn't warrant reflex test popup if not dirty (changed)
     var isDirty = isFormFieldDirty(dropDownSelect);
     
     for (var i = 0; i < length; i++) {
        if (dropDownSelect[i].selected) {
          selectedValue = dropDownSelect[i].value;
          selectedIndex = i;
          <% if (ctr1 != 0) { %>
             listOfSelectedIds  += '<%=idSeparator%>';
          <% } %>
          listOfSelectedIds += selectedValue;
          break;
        }
     }
     
     
     //load added reflex tests attached to all possible test results (if there are any then we need to direct to popup
     var addedReflexTests = new Array();
     <bean:define id="testResultReflexTestList2" name="ta_Tr_bean2" property="testResultReflexTests"/>
      <logic:notEmpty name="testResultReflexTestList2">
         <logic:iterate id="aReflexTestList2"  indexId="indx" name="testResultReflexTestList2" type="java.util.List">
          <logic:notEmpty name="aReflexTestList2">
            var addedReflexTestIdList = '';
           <logic:iterate id="aReflexTest2" indexId="indx2" name="aReflexTestList2" type="us.mn.state.health.lims.test.valueholder.Test" >
            <bean:define id="addedTestId2" name="aReflexTest2" property="id"/>
             addedReflexTestIdList += '<%=addedTestId2%>';
             addedReflexTestIdList += '<%=idSeparator%>';
           </logic:iterate>
             addedReflexTests[<%=indx%>] = addedReflexTestIdList;
          </logic:notEmpty>
          <logic:empty name="aReflexTestList2">
           addedReflexTests[<%=indx%>] = "";
          </logic:empty>
         </logic:iterate>
      </logic:notEmpty>
     
     //bugzilla 1883 
     //1. does result trigger reflex test?
     //2. has this result just changed? (if hasn't changed don't show reflex popup)
     //3. has same reflex rule already triggerd an added child reflex test?
     if (selectedIndex > 0 && isDirty ) { //now check to make sure this one hasn't already triggered a reflex test
       //determine whether this testResult has at least one reflex test and warrants changing showpopup to true
       //this is because selectedIndex includes the blank drop down option and addedReflexTests does not
       var resetIndex = selectedIndex - 1;
   	   if (addedReflexTests[resetIndex] != null && addedReflexTests[resetIndex] != "") {
    
         var reflexRuleAlreadyTriggered = false;        

         <logic:notEmpty name="<%=formName%>" property="testTestAnalytes">
           <logic:iterate id="tst_ta_ForExistingReflexTests" indexId="ctr" name="<%=formName%>" property="testTestAnalytes" type="us.mn.state.health.lims.result.valueholder.Test_TestAnalyte">
            <bean:define id="analysis_ForExistingReflexTests" name="tst_ta_ForExistingReflexTests" property="analysis" type="us.mn.state.health.lims.analysis.valueholder.Analysis" />
            <bean:define id="test_ForExistingReflexTests" name="tst_ta_ForExistingReflexTests" property="analysis.test.id" />
            <logic:notEmpty name="analysis_ForExistingReflexTests" property="parentAnalysis">
              <bean:define id="parentAnalysisId_ForExistingReflexTests" name="analysis_ForExistingReflexTests" property="parentAnalysis.id" />
              <bean:define id="parentResultAnalyteId_ForExistingReflexTests" name="analysis_ForExistingReflexTests" property="parentResult.analyte.id" />
 
              var myReflexArray = addedReflexTests[resetIndex].split('<%=idSeparator%>');
              if (('<%=parentAnalysisId_ForExistingReflexTests%>' == '<%=analysisId2%>')
                && ('<%=parentResultAnalyteId_ForExistingReflexTests%>' == '<%=analyteId2%>') 
                && (myReflexArray.indexOf('<%=test_ForExistingReflexTests%>') >= 0)){
                        reflexRuleAlreadyTriggered = true;
               }
                       
            </logic:notEmpty>
          </logic:iterate>
        </logic:notEmpty>


        //bugzilla 1883 make sure it changed else it doesn't warrant test reflex popup
        if (!reflexRuleAlreadyTriggered) {
               showpopup = "true";
	    }
      }
    }
    <% ctr1++; %>
 </logic:iterate>
 </logic:notEmpty>
 </logic:iterate>
 </logic:notEmpty>
 
  return showpopup;
}


function saveThis(form) {
  //bugzilla 2254
  document.forms[0].elements['hasNewUnsatisfactoryResult'].value = 'false';
  if (foundUnsatisfactoryResult(form)) {
     document.forms[0].elements['hasNewUnsatisfactoryResult'].value = 'true';
  }
  //bugzilla 2016 removed a preSave function that was blanking out values
  var showpopup = "false";
  var listOfSelectedIds = '';
//bugzilla 1684
  var listOfSelectedIdAnalytes = '';
  //bugzilla 1882
  var listOfSelectedIdAnalyses = '';
  <% ctr3 = 0; %>
  <logic:notEmpty name="<%=formName%>" property="testTestAnalytes">
   <logic:iterate id="tst_ta" name="<%=formName%>" property="testTestAnalytes">
    <%--bugzilla 1882--%>
    <bean:define id="analysisId" name="tst_ta" property="analysis.id"/>
    <logic:notEmpty name="tst_ta" property="testAnalyteTestResults">
     <logic:iterate id="ta_Tr_bean" indexId="ctr" name="tst_ta" property="testAnalyteTestResults" type="us.mn.state.health.lims.result.valueholder.TestAnalyte_TestResults">
      <bean:define id="testAnalyte" name="ta_Tr_bean" property="testAnalyte"/>
      <bean:define id="testAnalyteId" name="testAnalyte" property="id"/>
      <bean:define id="analyteId" name="testAnalyte" property="analyte.id"/>
      //get the value of testresult selected (we need this to pass on to popup as listOfSelectedIds
      var dropDownSelect = document.forms[0].elements['selectedTestResultIds[<%=ctr3%>]'];
      var length = dropDownSelect.length;
      var selectedValue;
      var selectedIndex;
      
      //bugzilla 1883 doesn't warrant reflex test popup if not dirty (changed)
      var isDirty = isFormFieldDirty(dropDownSelect);
      
      
      for (var i = 0; i < length; i++) {
        if (dropDownSelect[i].selected) {
          selectedValue = dropDownSelect[i].value;
          //this validates that the isReportable flag is set for this selected result
          if (selectedValue !== null && selectedValue !=  '' && isBlankIsReportableFlag(form, '<%=ctr3%>')) {
             clearClicked();
             return;
          }
          selectedIndex = i;
          break;
        }
      }
     
     //load added reflex tests attached to all possible test results (if there are any then we need to direct to popup
     var addedReflexTests = new Array();
     <bean:define id="testResultReflexTestList" name="ta_Tr_bean" property="testResultReflexTests"/>
      <logic:notEmpty name="testResultReflexTestList">
         <logic:iterate id="aReflexTestList"  indexId="indx" name="testResultReflexTestList" type="java.util.List">
          <logic:notEmpty name="aReflexTestList">
            var addedReflexTestIdList = '';
           <logic:iterate id="aReflexTest" indexId="indx2" name="aReflexTestList" type="us.mn.state.health.lims.test.valueholder.Test" >
            <bean:define id="addedTestId" name="aReflexTest" property="id"/>
             addedReflexTestIdList += '<%=addedTestId%>';
             addedReflexTestIdList += '<%=idSeparator%>';
           </logic:iterate>
             addedReflexTests[<%=indx%>] = addedReflexTestIdList;
          </logic:notEmpty>
          <logic:empty name="aReflexTestList">
           addedReflexTests[<%=indx%>] = "";
          </logic:empty>
         </logic:iterate>
      </logic:notEmpty>
     
     //bugzilla 1883
     //1. does result trigger reflex test?
     //2. has this result just changed? (if hasn't changed don't show reflex popup)
     //3. has same reflex rule already triggerd an added child reflex test?
     if (selectedIndex > 0 && isDirty) {
        //determine whether this testResult has at least one reflex test and warrants changing showpopup to true
        //the following line is because selectedIndex includes the blank drop down option and addedReflexTests does not
            var resetIndex = selectedIndex - 1;
   	        if (addedReflexTests[resetIndex] != null && addedReflexTests[resetIndex] != "") {
   	        
   	         //now check to make sure this one hasn't already triggered this reflex test
             var reflexRuleAlreadyTriggered = false;        

              <logic:notEmpty name="<%=formName%>" property="testTestAnalytes">
               <logic:iterate id="tst_ta_ForExistingReflexTests2" indexId="ctr" name="<%=formName%>" property="testTestAnalytes" type="us.mn.state.health.lims.result.valueholder.Test_TestAnalyte">
                <bean:define id="analysis_ForExistingReflexTests2" name="tst_ta_ForExistingReflexTests2" property="analysis" type="us.mn.state.health.lims.analysis.valueholder.Analysis" />
                <bean:define id="test_ForExistingReflexTests2" name="tst_ta_ForExistingReflexTests2" property="analysis.test.id" />
                <logic:notEmpty name="analysis_ForExistingReflexTests2" property="parentAnalysis">
                  <bean:define id="parentAnalysisId_ForExistingReflexTests2" name="analysis_ForExistingReflexTests2" property="parentAnalysis.id" />
                  <bean:define id="parentResultAnalyteId_ForExistingReflexTests2" name="analysis_ForExistingReflexTests2" property="parentResult.analyte.id" />
                  
                  var myReflexArray = addedReflexTests[resetIndex].split('<%=idSeparator%>');
                  if (('<%=parentAnalysisId_ForExistingReflexTests2%>' == '<%=analysisId%>')
                     && ('<%=parentResultAnalyteId_ForExistingReflexTests2%>' == '<%=analyteId%>') 
                     && (myReflexArray.indexOf('<%=test_ForExistingReflexTests2%>') >= 0)){
                          reflexRuleAlreadyTriggered = true;
                  }
                       
                </logic:notEmpty>
               </logic:iterate>
              </logic:notEmpty>
   
           
              if (!reflexRuleAlreadyTriggered) {
              
      	          showpopup = "true";
                 <% if (ctr3 != 0) { %>
                     listOfSelectedIds  += '<%=idSeparator%>';
                     listOfSelectedIdAnalytes += '<%=idSeparator%>';
                     listOfSelectedIdAnalyses += '<%=idSeparator%>';
                 <% } %>
                listOfSelectedIds += selectedValue;
                if (selectedValue != null && selectedValue != '') {
                 listOfSelectedIdAnalytes += '<%=testAnalyteId%>';
                 listOfSelectedIdAnalyses += '<%=analysisId%>';
                }

            }
          }
        }
     <% ctr3++; %>
    </logic:iterate>
   </logic:notEmpty>
  </logic:iterate>
 </logic:notEmpty>
  
//if there are ANY reflex tests we need to show popup
if (showpopup == "true") {
    //if  no errors otherwise on page -> go to add test popup
	var context = '<%= request.getContextPath() %>';
	var server = '<%= request.getServerName() %>';
	var port = '<%= request.getServerPort() %>';
	var scheme = '<%= request.getScheme() %>';
	
	
	var hostStr = scheme + "://" + server;
	if ( port != 80 && port != 443 )
	{
		hostStr = hostStr + ":" + port;
	}
	hostStr = hostStr + context;

	// Get the sessionID
	 var sessionid = '';

	 var sessionIndex = form.action.indexOf(';');
	 if(sessionIndex >= 0){
		 var queryIndex = form.action.indexOf('?');
		 var length = form.action.length;
		 if (queryIndex > sessionIndex) {
		 	length = queryIndex;
		 }
		 sessionid = form.action.substring(sessionIndex,length);
	 }
	
	 //check if there are any tests (reflex) that they want to add before updating
     var href = context + "/ResultsEntryReflexTestPopup.do" + sessionid;
	 href +=  '?analysisId=' + '<%=analysisId%>';
	 href += '&listOfSelectedIds=' + listOfSelectedIds;
    //bugzilla 1684
	href += '&listOfSelectedIdAnalytes=' + listOfSelectedIdAnalytes;
    //bugzilla 1882
    href += '&listOfSelectedIdAnalyses=' + listOfSelectedIdAnalyses;
	
    //alert("href "+ href);
    
    //bugzilla 1763 - make SAVE button useable again
    clearClicked();
    
	createPopup( href, null, null );

  } else {
    //no added tests (reflex) so go ahead and just update
    var param = '?' + '<%=analysisIdParm%>' + '=' + '<%=analysisId%>' + '&ID=';
    //alert("setting action");
    setAction(window.document.forms[0], 'Update', 'yes', param);
  }
}

function submitTheForm(form) {
   //alert("I am going to submit the form " + form.name);
   var param = '?' + '<%=analysisIdParm%>' + '=' + '<%=analysisId%>' + '&ID=';
   setAction(form, 'Update', 'yes', param);
}

//This is for the adding additional reflex tests selected on popup
function setAddReflexTests(form) {

    var addedTestOptions = form.selectedAddedTests;
    var addedTestParentResults = form.selectedAddedTestParentResults;
    //bugzilla 1882
    var addedTestParentAnalytes = form.selectedAddedTestParentAnalytes;
    var addedTestParentAnalyses = form.selectedAddedTestParentAnalyses;
 
    //initialize
    document.forms[0].addedReflexTestIds.value = '';
    document.forms[0].addedReflexTestParentResultIds.value = '';
    //bugzilla 1882
    document.forms[0].addedReflexTestParentAnalyteIds.value = '';
    document.forms[0].addedReflexTestParentAnalysisIds.value = '';


    if (addedTestOptions != null) {
    
     //If only one checkbox
     if (addedTestOptions[0] == null) {
       if (addedTestOptions != null && addedTestOptions.checked == true) {
          document.forms[0].addedReflexTestIds.value = addedTestOptions.value;
          document.forms[0].addedReflexTestParentResultIds.value = addedTestParentResults.value;
          //bugzilla 1882
          document.forms[0].addedReflexTestParentAnalyteIds.value = addedTestParentAnalytes.value;
          document.forms[0].addedReflexTestParentAnalysisIds.value = addedTestParentAnalyses.value;
       }
     } else {
    
        for (var i = 0; i < addedTestOptions.length; i++) { 
          if (addedTestOptions[i].checked) {
            if (i > 0) {
              document.forms[0].addedReflexTestIds.value += '<%=idSeparator%>';
              document.forms[0].addedReflexTestParentResultIds.value += '<%=idSeparator%>';
              //bugzilla 1882
              document.forms[0].addedReflexTestParentAnalyteIds.value += '<%=idSeparator%>';
              document.forms[0].addedReflexTestParentAnalysisIds.value += '<%=idSeparator%>';
            }
            document.forms[0].addedReflexTestIds.value += addedTestOptions[i].value;
            document.forms[0].addedReflexTestParentResultIds.value += addedTestParentResults[i].value;
            //bugzilla 1882
            document.forms[0].addedReflexTestParentAnalyteIds.value += addedTestParentAnalytes[i].value;
            document.forms[0].addedReflexTestParentAnalysisIds.value += addedTestParentAnalyses[i].value;
          }
        }
      
     }
     
    }
}

function initializeNotes() {
    //initialize
    document.forms[0].noteIds.value = '';
    document.forms[0].noteSubjects.value = '';
    document.forms[0].noteTexts.value = '';
    document.forms[0].noteTypes.value = '';
    document.forms[0].noteLastupdateds.value = '';
}

//This is for notes from notesPopup
function setANote(form, id, subject, text, type, lastupdated) {

    document.forms[0].noteIds.value += id + '<%=textSeparator%>';
    if (subject == null || subject == '') {
      document.forms[0].noteSubjects.value += ' ' + '<%=textSeparator%>';
    } else {
      document.forms[0].noteSubjects.value += subject + '<%=textSeparator%>';
    }
    if (text== null || text == '') {
      document.forms[0].noteTexts.value += ' ' + '<%=textSeparator%>';
    } else {
      document.forms[0].noteTexts.value += text + '<%=textSeparator%>';
    }
    if (type == null || type == '') {
      document.forms[0].noteTypes.value += ' ' + '<%=textSeparator%>';
    } else {
      document.forms[0].noteTypes.value += type + '<%=textSeparator%>';
    }
    document.forms[0].noteLastupdateds.value += lastupdated + '<%=textSeparator%>';
}

//bugzilla 2001 - fix bug in determining whether titer/numeric have been blanked out (use isDirty instead)
function checkBlankResultForNotes(field, notesAttached) {
//if result selection changed and the value is now blank and a note exists then we need to stop them
   if (notesAttached && isFormFieldDirty(field) && field.value == '') {
        alert('<%=errorChangeToNoResult%>');
        return true;
   }
   return false;
}

//bugzilla 2001 isolating titer/numeric validation logic (removed from checkBlankResultForNotes for clarity)
function validateTiterNumeric(field) {

 		var myStr = field.name ;
 		var myReplace = /.+\[(\d+)\]/;
		var indexVal = myStr.replace(myReplace, "$1");

		if (field.value != '' ){
		 <% ctr4 = 0; %>
		 <logic:notEmpty name="<%=formName%>" property="testTestAnalytes">
		   <logic:iterate id="tst_ta" name="<%=formName%>" property="testTestAnalytes">
			<logic:notEmpty name="tst_ta" property="testAnalyteTestResults">
			    <logic:iterate id="ta_Tr_bean2" indexId="ctr" name="tst_ta" property="testAnalyteTestResults" type="us.mn.state.health.lims.result.valueholder.TestAnalyte_TestResults">
					if ( <%=ctr4%> == indexVal ){
						var dropDownSelect = document.forms[0].elements['selectedTestResultIds[<%=ctr4%>]'];
						var selectedValue = dropDownSelect.value;
						validateResultValue(field.value , selectedValue );													
					}
				 <% ctr4++; %>
				</logic:iterate>
			</logic:notEmpty>	
		   </logic:iterate>
		  </logic:notEmpty>
		}
	 

   return false;
}

//bugzilla 1798
function linkToParentPopup(button) {

    if (isDirty(document.forms[0], '')) {
          alert('<%=errorSaveChangesBeforeLinkUnlink%>');
    } else {
    var analysisId = button.name;
    var accessionNumber = document.forms[0].accessionNumber.value;
	var context = '<%= request.getContextPath() %>';
	var server = '<%= request.getServerName() %>';
	var port = '<%= request.getServerPort() %>';
	var scheme = '<%= request.getScheme() %>';
	
	
	var hostStr = scheme + "://" + server;
	if ( port != 80 && port != 443 )
	{
		hostStr = hostStr + ":" + port;
	}
	hostStr = hostStr + context;

	// Get the sessionID
	 var sessionid = '';

	 var sessionIndex = document.forms[0].action.indexOf(';');
	 if(sessionIndex >= 0){
		 var queryIndex = document.forms[0].action.indexOf('?');
		 var length = document.forms[0].action.length;
		 if (queryIndex > sessionIndex) {
		 	length = queryIndex;
		 }
		 sessionid = document.forms[0].action.substring(sessionIndex,length);
	 }
	
	 //check if there are any tests (reflex) that they want to add before updating
     var href = context + "/ResultsEntryLinkChildTestToParentTestResultPopup.do" + sessionid;
     href += '?accessionNumber=' + accessionNumber;
	 href +=  '&analysisId=' + analysisId;

    //alert("href "+ href);
    
    createPopup( href, null, null );
	}
	
    clearClicked();

}

function unlinkFromParent(button) {
    if (isDirty(document.forms[0], '')) {
          alert('<%=errorSaveChangesBeforeLinkUnlink%>');
    } else {
      var analysisId = button.name;
      confirmUnlink(analysisId); 
    }
    clearClicked();
}

function gotoAmendedResultsEntry(button) {
  if (isDirty(window.document.forms[0], '')) {
        if (foundUnsatisfactoryResult(window.document.forms[0])) {
           //Don't allow forward to amend if we are required to go to Qa Events Entry
           alert('<%=errorAmendUnsatisfactoryResultFound%>');
        } else {
          confirmSaveAndAmendPopup(button.name); 
        }
  } else {
      confirmAmendPopup(button.name);
      //setAction(window.document.forms[0], 'AmendedResultsEntryUpdate', 'yes', '?<%=analysisIdParm%>=' + button.name + '&ID=');
  }
}

function resultsEntryHistoryPopup (form, analysisId) {

  //if there is an error on the page we cannot go to add test
   //clear button clicked flag to allow add test again
   //if (isSaveEnabled() != true) {
       //clearAddTestClicked();
   //}
   
    //if  no errors otherwise on page -> go to add test popup
	var context = '<%= request.getContextPath() %>';
	var server = '<%= request.getServerName() %>';
	var port = '<%= request.getServerPort() %>';
	var scheme = '<%= request.getScheme() %>';
	
	
	var hostStr = scheme + "://" + server;
	if ( port != 80 && port != 443 )
	{
		hostStr = hostStr + ":" + port;
	}
	hostStr = hostStr + context;

	// Get the sessionID
	 var sessionid = '';

	 var sessionIndex = form.action.indexOf(';');
	 if(sessionIndex >= 0){
		 var queryIndex = form.action.indexOf('?');
		 var length = form.action.length;
		 if (queryIndex > sessionIndex) {
		 	length = queryIndex;
		 }
		 sessionid = form.action.substring(sessionIndex,length);
	 }
 
    var param = '?' + '<%=analysisIdParm%>' + '=' + analysisId;
 	var href = context + "/ResultsEntryHistoryPopup.do" + param + sessionid;
    //alert("href "+ href);
	
	createPopup( href, null, null );
}

//bugzilla 2227
// Has an input-capable field on the form changed to UNSATISFACTORY (route to qa events)?
function foundUnsatisfactoryResult(form) {

	var elemLength = form.elements.length;
	for (var i=0; i < elemLength; i++) {
		var eElem = form.elements[i];
		
		if (eElem.disabled == true || eElem.readOnly == true)
		{	// Field is disabled, so don't need to check if dirty
			continue;
		}
		var eName = eElem.name;
		if ( eName.length == 0 )
		{	// Name of field is 0 length, so don't do check
			continue;
		}
		
		var eType = eElem.type;
		
		if( "hidden" == eType ) {
				continue;
		}
		
		if ("text" == eType || "TEXTAREA" == eElem.tagName) {
            //bugzilla 2489
			if (eElem.value != eElem.defaultValue) {
			   if (eElem != null && eElem.value != null && eElem.value.indexOf('<%=unsatisfactoryResultValue%>') >= 0) {
				return true;
			   }
			}
		}

	
		if ("checkbox" == eElem.type || "radio" == eElem.type) {
                continue;
		}
		
		if ("SELECT" == eElem.tagName) {
			checkDftSelected(eElem);
			var numOpts = eElem.options.length;
			for (var j=0; j < numOpts; j++) {
				var eopt = eElem.options[j];
				if (eopt.selected != eopt.defaultSelected && eopt.value != null && eopt.value.length > 0) {
                  //bugzilla 2489
				  if (eopt != null && eopt.text != null && eopt.text.length > 0 && eopt.text.indexOf('<%=unsatisfactoryResultValue%>') >= 0 && eopt.selected) {
					return true;
				  }
				}
			}
		}
	}

	return false;
}

//bugzilla 2322
function viewDictionary(form) {
    var selectedId = (form.options[form.selectedIndex].value);
    var defaultBoxLength = "35";
    var select;
    for ( var i=0; i<dictArray.length; i++ ) {
        var trid  = dictArray[i].split("|TRID|");        
        if ( (trid[0] == selectedId) && (trid[1].length >= defaultBoxLength) ) {
            select = confirm("<%=resultDetail%>:\n\n " + trid[1] + "\n\n<%=resultConfirm%>"); 
            if ( !select )  {
                form.selectedIndex = 0;
            }
            break;                                     
        }
    }
}

</script>

<html:hidden property="addedReflexTestIds" name="<%=formName%>" styleId="addedReflexTestIds"/>
<html:hidden property="addedReflexTestParentResultIds" name="<%=formName%>" styleId="addedReflexTestParentResultIds"/>
<%--bugzilla 1882--%>
<html:hidden property="addedReflexTestParentAnalyteIds" name="<%=formName%>" styleId="addedReflexTestParentAnalyteIds"/>
<html:hidden property="addedReflexTestParentAnalysisIds" name="<%=formName%>" styleId="addedReflexTestParentAnalysisIds"/>
<html:hidden property="linkedParentInformationString" name="<%=formName%>" styleId="linkedParentInformationString"/>
<html:hidden property="unlinkedParentInformationString" name="<%=formName%>" styleId="unlinkedParentInformationString"/>

<html:hidden property="noteRefId" name="<%=formName%>" styleId="noteRefId"/>
<html:hidden property="noteIds" name="<%=formName%>" styleId="noteIds"/>
<html:hidden property="noteSubjects" name="<%=formName%>" styleId="noteSubjects"/>
<html:hidden property="noteTexts" name="<%=formName%>" styleId="noteTexts"/>
<html:hidden property="noteTypes" name="<%=formName%>" styleId="noteTypes"/>
<html:hidden property="noteLastupdateds" name="<%=formName%>" styleId="noteLastupdateds"/>
<html:hidden property="sampleLastupdated" name="<%=formName%>" styleId="sampleLastupdated"/>
<%--bugzilla 2254--%>
<html:hidden property="hasNewUnsatisfactoryResult" name="<%=formName%>" />

<table>
  <tr>
    <%--bugzilla 1664 removed height --%>
    <td valign="top">
       <%--bugzilla 1664 changed height of div --%>
       <%--bugzilla 2074 changed height of div --%>
       <table border="1">
         <tr><%--bugzilla 2308--%>
          <td colspan="1" scope="row" width="200" bgcolor="#CCCC99"><strong><bean:message key="resultsentry.tests.title"/></strong></td>
          <td colspan="1" scope="row" width="40" bgcolor="#CCCC99"><strong><bean:message key="resultsentry.tests.testisreportable.title"/></strong></td>
          <td colspan="1" scope="row" width="70" bgcolor="#CCCC99"><strong><bean:message key="resultsentry.tests.parentlink.title"/></strong></td>
          <td colspan="1" scope="row" width="169" bgcolor="#CCCC99"><strong><bean:message key="resultsentry.tests.component.title"/></strong></td>
          <td colspan="2" scope="row" width="615" bgcolor="#CCCC99"><strong><bean:message key="resultsEntry.tests.result.title"/></strong></td>
          <td colspan="1" scope="row" width="40" bgcolor="#CCCC99"><strong><bean:message key="resultsentry.tests.resultisreportable.title"/></strong></td>
          <td colspan="1" scope="row" width="40" bgcolor="#CCCC99"><strong><bean:message key="resultsentry.tests.notes.title"/></strong></td>
         </tr>
       </table><%--bugzilla 2310 need background for break--%>
       <div class="scrollvertical" style="height:400px;width:100%;BACKGROUND-COLOR: #f7f7e7;">
       <table border="1">
     	<!-- bgm bugzilla 1587 added check for empty collection -->
         <% myCounter = 0; %>
         <logic:notEmpty name="<%=formName%>" property="testTestAnalytes">
         <logic:iterate id="tst_ta" indexId="ctr" name="<%=formName%>" property="testTestAnalytes" type="us.mn.state.health.lims.result.valueholder.Test_TestAnalyte">
          <bean:define id="tas" name="tst_ta" property="testAnalytes" />
          <bean:define id="test" name="tst_ta" property="test" />
          <bean:define id="testId" name="test" property="id" />
          <bean:define id="analysis" name="tst_ta" property="analysis" type="us.mn.state.health.lims.analysis.valueholder.Analysis" />
          
          <%--bugzilla 2028 Qa Events determine value of hasPendingQaEvents--%>
          <bean:define id="hasPendingQaEvents" value="false"/>
          <logic:notEmpty name="tst_ta" property="analysisQaEvents">
             <logic:iterate id="qaEvents" indexId="qaEvent_ctr" name="tst_ta" property="analysisQaEvents" type="us.mn.state.health.lims.analysisqaevent.valueholder.AnalysisQaEvent">
               <logic:empty name="qaEvents" property="completedDate">
                  <% hasPendingQaEvents = "true"; %>
               </logic:empty>
             </logic:iterate>
          </logic:notEmpty>
  
          <bean:size id="ct" name="tas" />
          <%--diane bug fix on 2257--%>
          <bean:define id="disabled" value="false"/>
 
           <logic:notEmpty name="tas">
             <logic:iterate id="ta" indexId="taCtr" name="tst_ta" property="testAnalyteTestResults" type="us.mn.state.health.lims.result.valueholder.TestAnalyte_TestResults">
               <tr height="100%">               
	            <logic:equal name="taCtr" value="0" >
                 <%--bugzilla 1769 valign top--%>                 
	             <td width="200" rowspan="<%=ct%>" scope="row" valign="top">
	             <strong><bean:write name="test" property="testDisplayValue"/></strong>
	               <%--bugzilla 2227 display revision numbers > 0--%>
	                <logic:notEmpty name="analysis" property="revision">
	                  <logic:notEqual name="analysis" property="revision" value="0">
	                    <p style="font: bold; color: red;BACKGROUND-COLOR: #f7f7e7"><bean:message key="resultsentry.label.revision.number"/>: <bean:write name="analysis" property="revision"/></p>
	                     <%--need to add parameter testid--%>       
	                     <a href="" onclick="resultsEntryHistoryPopup(document.forms[0], '<%=analysis.getId()%>');return false;" style="BACKGROUND-COLOR: #f7f7e7;color:blue">
                            <bean:message key="resultsentry.label.hyperlink.history" />
                         </a>
	                  </logic:notEqual>
	                </logic:notEmpty>
	                <logic:equal name="analysis" property="status" value="4">
	                  <%--bugzilla 2377 need to start new line for amend button always--%>
 	     	          <p style="BACKGROUND-COLOR: #f7f7e7">
                      <html:button style="font-size:80%;" onclick="gotoAmendedResultsEntry(this);return false;" property='<%= analysis.getId() %>'>
  			                  <bean:message key="resultsentry.button.amend"/>
  		    	       </html:button>
                     <logic:equal name="analysis" property="revision" value="0">
	     	          </p>
                     </logic:equal>

  		    	    </logic:equal>
	                <%--bugzilla 2028, sub-bugzilla 2035--%>
	                 <logic:notEmpty name="tst_ta" property="analysisQaEvents">
	                  <%--bugzilla 2035 change link color to blue and have a separate paragraph for links--%>
	                  <p style="BACKGROUND-COLOR: #f7f7e7">
                        <html:link action="/ViewQaEventsEntryFromResultsEntry" paramName="<%=formName%>" paramId="accessionNumber" paramProperty="accessionNumber" style="BACKGROUND-COLOR: #f7f7e7;color:blue">
	                      <% if (hasPendingQaEvents.equals("true")) { %>
                            <bean:message key="resultsentry.label.hyperlink.pending.qaevents" />
                          <% } else { %>
                            <bean:message key="resultsentry.label.hyperlink.completed.qaevents" />
                         <% } %>
                        </html:link>
                      </p>
	                 </logic:notEmpty>
	               
	               <% 
                       //diane bug fix on 2257
	                  if ((!StringUtil.isNullorNill((String)analysis.getStatus()) && (analysis.getStatus().equals(analysisReleasedStatus))) || StringUtil.isNullorNill((String)analysis.getStatus())) {
	                     disabled = "true";
	                   }
                       //see bug 2277 allowEdits is confusing  it currently means same as disabled
                       if (allowEdits.equals("true")) {
                         disabled = "true";
	                  }
	               %>
                   <%--input type="submit" name="<%=testId%>" value="Edit" onclick="submit(this)"--%>
	             </td>
                  <td width="40" rowspan="<%=ct%>" scope="row" valign="top">
                   <center>
                    <html:select name="<%=formName%>" styleId='<%= "selectedTestIsReportableFlags[" + ctr + "]"%>' property='<%= "selectedTestIsReportableFlags[" + ctr + "]"%>' disabled="<%=Boolean.valueOf(disabled).booleanValue()%>" >
  	                 <html:option value="<%=yesOption%>"><bean:message key="page.default.yes.option"/></html:option>
					 <html:option value="<%=noOption%>"><bean:message key="page.default.no.option"/></html:option>
                    </html:select>
                   </center>
	              </td>
                  <%--parent link column--%>
                  <% 	
		             String saveDisabled = (String)request.getAttribute(us.mn.state.health.lims.common.action.IActionConstants.SAVE_DISABLED);
		             String buttonForTestLinkDisabled = "false";
		             if (saveDisabled.equals(IActionConstants.TRUE) || disabled.equals(IActionConstants.TRUE)) {
		               buttonForTestLinkDisabled = IActionConstants.TRUE;
		             }
                  %>
                  <td width="70" rowspan=<%=ct%>" scope="row" valign="top">
                   <logic:notEmpty name="analysis" property="parentAnalysis">
                     <bean:define id="parentAnalysis" name="analysis" property="parentAnalysis" />
                     <bean:define id="parentResult" name="analysis" property="parentResult" />
                     <bean:define id="parentAnalysisTest" name="parentAnalysis" property="test" />
                     <bean:define id="parentAnalysisTestName" name="parentAnalysisTest" property="testDisplayValue" />
                     <bean:define id="parentResultValue" name="parentResult" property="value" />
                     <bean:define id="parentTestAnalyteName" name="parentResult" property="analyte.analyteName" />
                     <bean:define id="unlinkAnalysisId" name="analysis" property="id" type="java.lang.String"/>
                     <bean:define id="childType" name="ta" property="childType" />
                     <bean:define id="canBeUnlinked" name="ta" property="canBeUnlinked" />
                     <%--bugzilla 2627 reflexedType can now also be unlinked! and only tests where canBeUnlinked is false cannot be unlinked--%>
                     <% if (canBeUnlinked.equals(IActionConstants.FALSE)) { %>
                     <%--bugzilla 1906 prototype based Tooltip for formatting and to fix timeout problem--%>
                     <div id='<%= "questionLink" + ctr %>'>
                       	<html:button style="color:#000000; background-color: #00FFFF; font-size:80%;" property='<%= "questionLink" + ctr %>' onclick="return false;">
  			                  <bean:message key="resultsentry.button.parent.from.reflex"/>
  	     	            </html:button>
  	                 </div>
                     <div class='<%= "tooltip for_questionLink" + ctr %>' >
                       <span class="tooltip">
                         <h4><%=tooltipheader%></h4>
                         <% out.println(parentAnalysisTestName + ":" + parentTestAnalyteName + ":" + parentResultValue); %>
                       </span>
                     </div>
              	     
                  
                     <% } else { %>
                     <%--bugzilla 1906 prototype based Tooltip for formatting and to fix timeout problem--%>
                     <div id='<%= "questionLink" + ctr %>'>
                       	<html:button style="color:#000000; background-color: #00FFFF; font-size:80%;" onclick="if(checkClicked()) 
							 {
							 	return false;
							 }
							 else {
							   unlinkFromParent(this);
							 }" property='<%= unlinkAnalysisId %>' disabled="<%=Boolean.valueOf(buttonForTestLinkDisabled).booleanValue()%>">
  			                  <bean:message key="resultsentry.button.unlink.from.parent"/>
  	     	            </html:button>
  	                 </div>
                     <div class='<%= "tooltip for_questionLink" + ctr %>' >
                       <span class="tooltip">
                         <h4><%=tooltipheader%></h4>
                         <% out.println(parentAnalysisTestName + ":" + parentTestAnalyteName + ":" + parentResultValue); %>
                       </span>
                     </div>
                  <%} %>
                     </logic:notEmpty>
                     <bean:define id="linkable" name="ta" property="canBeLinked"/>
                     <logic:equal name="linkable" value="true">
                     <bean:define id="linkAnalysisId" name="analysis" property="id" type="java.lang.String"/>
                       <div id='<%= "questionLink" + ctr %>'>
                          	  <html:button style="font-size:80%;" onclick="if(checkClicked()) 
							 {
							 	return false;
							 }
							 else {
							   linkToParentPopup(this);
							 }" property='<%= linkAnalysisId %>' disabled="<%=Boolean.valueOf(buttonForTestLinkDisabled).booleanValue()%>">
  			                  <bean:message key="resultsentry.button.link.to.parent"/>
  		    	              </html:button>
                       </div>
                    </logic:equal>
                  </td>
                 </logic:equal>                  
            
                  <td width="170">
                     <bean:write name="ta" property="testAnalyte.analyte.analyteName"/>:
                  </td>
             
                  <bean:define id="taId" name="ta" property="testAnalyte.analyte.id" />
                 
                      <%	//AIS - bugzilla 1797
           		    	List testResults = ((TestAnalyte_TestResults)ta).getTestResults();   
                       //bugzilla 1829 null check
			    		TestResult testresult = null;
			     		if (testResults != null && testResults.size() > 0) {
			     		  testresult = (TestResult) testResults.get(0);
			    		}
			    		
			    		String refId = "0";
			    		if (((TestAnalyte_TestResults)ta).getResultId() != null) {
			    		   refId = ((TestAnalyte_TestResults)ta).getResultId();
			    		}
		               %> 
		               
		               <%--bugzilla 1942 moved the result notes logic to here before display of results so we
		                   can determine if a result is allowed to be changed to blank - not allowed if notes have been entered = notesAttached--%>
		            <%
   	                    StringBuffer sbuf = new StringBuffer();
   	                    String notes = "";
   	                    notesAttached = "false";
   	                    if (ta != null) {
   	                      List resultNotes = ((TestAnalyte_TestResults)ta).getResultNotes();
   	                      for (int i = 0; i < resultNotes.size(); i++) {
   	                        notesAttached = "true";
   	                        if (i > 0) {
   	                          sbuf.append("\n");
   	                        }
   	                        Note note = (Note)resultNotes.get(i);
   	                        sbuf.append(note.getText());
   	                      }
   	                      notes = sbuf.toString();
   	                    }
                      %>  
                      
                  <td width="305">
                     <% if (testresult != null && testresult.getTestResultType().equalsIgnoreCase(numericType))
                       { 
                     %>
                    <!--bugzilla 2243: titer/numeric-type needs to be disabled as well when in verified status-->
                    <app:text name="<%=formName%>" styleId='<%= "resultValueN[" + myCounter + "]"%>' property= '<%= "resultValueN[" + myCounter + "]"%>' onblur='<%= "checkBlankResultForNotes(this," + notesAttached + ");validateTiterNumeric(this);return false;" %>' size="5" styleClass="text" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>" /> 	     		  
                 	<%--this next dropdown is not displayed for numeric type--%>                                                                                                                                        
   	     			<html:select name="<%=formName%>" styleId='<%= "selectedTestResultIds[" + myCounter + "]"%>' property='<%= "selectedTestResultIds[" + myCounter + "]"%>' style="display:none" value='<%= testresult.getId() %>'>
   	     			  <app:optionsCollection 
	                	name="ta" 
	            		property="testResults" 
		            	label="value" 
	             		value="id" 
	              		maxLength="35"
	                  />
					</html:select> 
   	               <% }
   	               
   	               
   	               if (testresult != null && testresult.getTestResultType().equalsIgnoreCase(dictType)){ %> 
                 	<%--this next field is not displayed (hidden) for dictionary type--%>  	             
   	                <html:hidden property= '<%= "resultValueN[" + myCounter + "]"%>' name="<%=formName%>" value="-1" styleId='<%= "resultValueN[" + myCounter + "]"%>'/> 
   	                <%--AIS - bugzilla 1864--%>	 
                    <%--bugzilla 2243--%>
  	                <%--bugzilla 2322--%>
  	                <html:select name="<%=formName%>" property='<%= "selectedTestResultIds[" + myCounter + "]"%>' onchange='<%= "checkBlankResultForNotes(this," + notesAttached + "); viewDictionary(this);"%>' disabled="<%=Boolean.valueOf(disabled).booleanValue()%>">         
  	             	  <app:optionsCollection 
	                	name="ta" 
	            		property="testResults" 
		            	label="value" 
	             		value="id" 
	             		maxLength="35"
	                   />
					</html:select>
   	              
   	                <% }
   	                
   	                if (testresult != null && testresult.getTestResultType().equalsIgnoreCase(titerType)){ %>
   	                
   	                <table width="50">
	                     <tr>	                     
		                     <td width="10">1:</td>
		                     <td width="40">	
		                        <!--bugzilla 2243: titer-type needs to be disabled as well when in verified status-->
 		                     	<app:text name="<%=formName%>" property= '<%= "resultValueN[" + myCounter + "]"%>' onblur='<%= "checkBlankResultForNotes(this," + notesAttached + ");validateTiterNumeric(this);return false;" %>' size="5" styleClass="text" disabled="<%=Boolean.valueOf(disabled).booleanValue()%>" /> 	     		  
		                 	 </td>
	                     </tr>
                     </table>
   	                 <%--this next dropdown is not displayed for titer type--%>  
   	                 <%--bugzilla 2322--%>  
   	                 <html:select name="<%=formName%>" property='<%= "selectedTestResultIds[" + myCounter + "]"%>' style="display:none" value='<%= testresult.getId() %>' onchange='viewDictionary(this);'>
   	     			  <app:optionsCollection 
	                	name="ta" 
	            		property="testResults" 
		            	label="value" 
	             		value="id" 
	              		maxLength="35"
	                  />
					 </html:select> 
   	                
   	                 <% } %>
   	                
	              </td>
	              <td width="305">
	                 <logic:notEmpty name="ta" property="resultValue" >
	                    <% if (testresult != null && testresult.getTestResultType().equalsIgnoreCase(titerType))
                       {%>1:<%}%><bean:write name="ta" property="resultValue"/>
	                 </logic:notEmpty>
	              </td>
                  <td width="40">
                    <center>
                    <html:select name="<%=formName%>" styleId='<%= "selectedResultIsReportableFlags[" + myCounter + "]"%>' property='<%= "selectedResultIsReportableFlags[" + myCounter + "]"%>' disabled="<%=Boolean.valueOf(disabled).booleanValue()%>">
  	                 <html:option value="<%=yesOption%>"><bean:message key="page.default.yes.option"/></html:option>
					 <html:option value="<%=noOption%>"><bean:message key="page.default.no.option"/></html:option>
                    </html:select>
                   </center>
	              </td>
 
                  <td width="35">
                   <%--bugzilla 2244--%>
                   <div id='<%= "notepad" + myCounter %>'>
                   <a href="" onclick="myPopupNotes(document.forms[0], '<%=refId%>', '<%=myCounter%>', '<%=taId%>', '<%=testId%>', '<%=disabled%>');return false;">
                      <div id='<%= "resultNote" + myCounter %>'
   	                    <% if (notes.equals("")){ %>
   	                    	class="notepad" 
   	                   <% }else{ %>
   	                   		class="notepaddata"
   	                   <% } %>
   	                    style="background-color: #f7f7e7;">&nbsp;</div>  
   	                   
                     </a>
                   </div>
                   <%--bugzilla 2244 notepad tooltip--%>
                   <div id='<%= "notepad_tooltip" + myCounter %>' class='<%= "tooltip for_notepad" + myCounter %>'>
                     <span class="tooltip">
                       <%  
                        int tempWidth = 0;
   	                    if (ta != null) {
   	                      List resultNotes = ((TestAnalyte_TestResults)ta).getResultNotes();
                          //bugzilla 2446 sort notes, add heading, date and time
   	                      String noteType = "";
   	                      for (int i = 0; i < resultNotes.size(); i++) {
   	                        Note note = (Note)resultNotes.get(i);
   	                        if (!note.getNoteType().equals(noteType)) {
   	                          noteType = note.getNoteType();
   	                          //don't break if first line
   	                          if (i != 0) {
   	                            out.println("<br/>");
   	                          }
   	                          if (noteType.equals(internalNote) ) {
   	                            out.println("<b>" + internalNotesHeading +":</b>");
   	                          } else if (noteType.equals(externalNote) ){
   	                            out.println("<b>" + externalNotesHeading +":</b>");
  	                          }
   	                        }
   	                        String dateAndTime = DateUtil.convertTimestampToStringDateAndTime(note.getLastupdated(), locale.toString());
   	                        //bugzilla 2480
			  	            String text = dateAndTime + " - " + note.getSystemUser().getNameForDisplay() + " - " + note.getText();
   	                        if ( !StringUtil.isNullorNill(text)) {
   	                         out.println("<br/>");
   	                        }
                            //wrap after 50 characters at first space found
   	                        List list = StringUtil.createChunksOfText(text, 50, true);
   	                        for (int textIndx = 0; textIndx < list.size(); textIndx++) {
   	                          String str = (String)list.get(textIndx);
   	                          if (textIndx > 0) {
   	                           out.println("<br/>");
   	                          }
                              out.println(str);
  	                          if (str.length() > tempWidth) {
   	                            tempWidth = str.length();
   	                          }
   	                        }
   	                      }
   	                    }
   	                   %>
   	                   <script>
   	                     //set textWidth in custom tooltip 
   	                     var tt = document.getElementById('<%= "notepad_tooltip" + myCounter %>');
   	                     tt.textWidth = <%= tempWidth%>;
   	                   </script>
                     </span>
                  </div>
                  <%--end bugzilla 2244 notepad tooltip--%>
                  </td>
                 <% myCounter++; %>
                </tr>
             </logic:iterate>
           </logic:notEmpty>
         </logic:iterate>
         </logic:notEmpty> 
       </table>
       <%--bugzilla 2310 need additional line at the bottom so that tooltips don't go offscreen--%>
       <br/>
      </div>
    </td>
  </tr>  
</table>
   
