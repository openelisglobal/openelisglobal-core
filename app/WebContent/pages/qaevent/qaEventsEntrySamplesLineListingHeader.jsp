<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants, 
	org.apache.struts.Globals,
	us.mn.state.health.lims.common.util.StringUtil,
	us.mn.state.health.lims.qaevent.valueholder.QaEventLineListingRoutingSwitchSessionHandler,
	us.mn.state.health.lims.testmanagement.valueholder.TestManagementRoutingSwitchSessionHandler, 
	us.mn.state.health.lims.result.valueholder.ResultsEntryRoutingSwitchSessionHandler"
%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%--bugzilla 2504--%>
<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="accessionNumberParm" value='<%=IActionConstants.ACCESSION_NUMBER%>'/>

<%!
String path = "";
String basePath = "";
String allowEdits = "true";
String qaEventsEntryLineListingFromQaEventsEntry = "false";
String singleModeAccessionNumberFromSession = "";

%>

<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

if (QaEventLineListingRoutingSwitchSessionHandler.isSwitchOn(IActionConstants.QA_EVENTS_ENTRY_LINELISTING_ROUTING_FROM_QAEVENTS_ENTRY, session)) {
  qaEventsEntryLineListingFromQaEventsEntry = "true";
  if (session.getAttribute(accessionNumberParm) != null && !StringUtil.isNullorNill((String)session.getAttribute(accessionNumberParm))) {
    singleModeAccessionNumberFromSession = (String)session.getAttribute(accessionNumberParm);
  }
} else {
  qaEventsEntryLineListingFromQaEventsEntry = "false";
}

java.util.Locale locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
				
path = request.getContextPath();
basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
					
%>
<script language="JavaScript1.2">

function pageOnLoad() {
  <% request.setAttribute(IActionConstants.POPUP_NOTES, "false"); %>
      
}

function validateForm(form) {
    //return validateQaEventsEntryForm(form);
    return true;
}

function cancelQaEventsEntryLineListing() {
  var qaELineListingFromQaEventsEntry;
  var accessionNumber;
  qaELineListingFromQaEventsEntry = '<%=qaEventsEntryLineListingFromQaEventsEntry%>';
  accessionNumber =   '<%=singleModeAccessionNumberFromSession%>';
  if (qaELineListingFromQaEventsEntry == 'true') {
    setAction(window.document.forms[0], 'CancelToQaEventsEntry', 'no', '?<%=accessionNumberParm%>=' + accessionNumber + '&ID=');
  } else {
     //defaults to going back to QA Events menu
     setAction(window.document.forms[0], 'Cancel', 'no', '?close=true&ID=');
  }
}

function gotoEditSample(accessionNumber) {
  setAction(window.document.forms[0], 'TestManagementFrom', 'yes', '?accessionNumber=' + accessionNumber + '&ID=');
}

function gotoResultsEntry(accessionNumber) {
  setAction(window.document.forms[0], 'ResultsEntryFrom', 'no', '?accessionNumber=' + accessionNumber + '&ID=');
}

function gotoQaEventsEntry(accessionNumber) {
  setAction(window.document.forms[0], 'ViewQaEventsEntryFrom', 'no', '?<%=accessionNumberParm%>=' + accessionNumber + '&ID=');
}

function changeFilter() {
   setAction(window.document.forms[0], 'View', 'no', '?ID=');
}
</script>

<%
	if(null != request.getAttribute(IActionConstants.FORM_NAME))
	{
%>
<table width="100%">
  <tr>
    <td align="center" style="font-weight: bold; color:#336699;BACKGROUND-COLOR: #FFFFFF;"> 
       <font size="2" color="black">
   	      <bean:message key="qaeventsentry.header.message.filter.by" />:
   	   </font>
       &nbsp;&nbsp;&nbsp;&nbsp&nbsp;
   	   <html:select name="<%=formName%>" property="selectedQaEventsCategoryId" onchange="changeFilter();">
			 <app:optionsCollection 
			  name="<%=formName%>" 
			  property="categoryDictionaries" 
    		  label="dictEntry" 
			  value="id" 
			  allowEdits="true"
			  />
       </html:select>
       &nbsp;&nbsp;&nbsp;&nbsp&nbsp;
   	   <font size="2" color="black">
          <bean:write name="<%=formName%>" property="totalCount"/>&nbsp;
          <bean:message key="qaeventsentry.header.message.records"/>
   	   </font>
  	</td>
  </tr>
</table>
<%
	}
%>
<%--no struts validation needed on this form--%>
<%--html:javascript formName="qaEventsEntryLineListingForm"/--%>

