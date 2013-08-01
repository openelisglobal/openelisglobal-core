<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date, java.util.List, java.util.Locale,
	org.apache.struts.Globals,
	us.mn.state.health.lims.common.action.IActionConstants,
	us.mn.state.health.lims.common.util.StringUtil,
	us.mn.state.health.lims.note.valueholder.Note,
    us.mn.state.health.lims.qaevent.valueholder.QaEvent,
	us.mn.state.health.lims.sampleqaevent.valueholder.SampleQaEvent,
	us.mn.state.health.lims.sampleqaeventaction.valueholder.SampleQaEventAction,
    us.mn.state.health.lims.common.util.Versioning,
	us.mn.state.health.lims.common.util.SystemConfiguration" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<%--bugzilla 2053, 2501, 2504, 2502--%>
<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<%!

String allowEdits = "true";

Locale locale = null;

String notes = "";

String path = "";
String basePath = "";

%>
<%
path = request.getContextPath();
basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";

if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
request.setAttribute(IActionConstants.ADD_DISABLED_ALL_SAMPLE_QAEVENTS_COMPLETED, "true");

          
%>
<%--bugzilla 2545--%>
<style>
tr.normal td {background-color:#f7f7e7;}
tr.highlighted td {background-color:#00FFFF;}
A:none {background-color:#f7f7e7;color:blue;}
A:visited {background-color:#f7f7e7;color:blue;}
A:active {background-color:#00FFFF;color:blue;}
A:hover {background-color:#00FFFF;color:blue;}
</style>

<script language="JavaScript1.2">


function confirmCompleteAllForSample(accessionNumber)
{
  var myWin = createSmallConfirmPopup( "", null , null );
  
   <% 
      
      out.println("var message = null;");
       
      String confirmsave_message =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "qaeventsentry.message.popup.confirm.complete.all.sample.and.test");
         
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
    strHTML += ' window.opener.setAction(window.opener.document.forms[0], "Update", "yes", "?accessionNumber=' + accessionNumber + '&ID=");self.close();} ';
      
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

//bugzilla 2545
function highlight(x){
 x.className="highlighted";
}
//bugzilla 2545
function undohighlight(x){
 x.className="normal";
}

</script>    
<logic:notEmpty name="<%=formName%>" property="samplesWithPendingQaEvents">
<table width="100%" align="center" border=1>
		<tr>
		    <td colspan="1" scope="row" align="middle" width="140" style="font-weight: bold; color:#336699;">
             <font size="2" color="black">
				<bean:message key="qaeventsentry.header.linelistingview.title.accessionnumber" />
			 </font>
            </td>
		    <td colspan="1" scope="row" align="middle" width="156" style="font-weight: bold; color:#336699;">
             <font size="2" color="black">
				<bean:message key="qaeventsentry.header.linelistingview.title.first.name" />
			 </font>
			</td>
		    <td colspan="1" scope="row" align="middle" width="156" style="font-weight: bold; color:#336699;">
             <font size="2" color="black">
				<bean:message key="qaeventsentry.header.linelistingview.title.last.name" />
			 </font>
			</td>
		    <td colspan="1" scope="row" align="middle" width="92" style="font-weight: bold; color:#336699;">
             <font size="2" color="black">
				<bean:message key="qaeventsentry.header.linelistingview.title.weight" />
			 </font>
			</td>
		    <td colspan="1" scope="row" align="middle" width="92" style="font-weight: bold; color:#336699;">
             <font size="2" color="black">
				<bean:message key="qaeventsentry.header.linelistingview.title.yellowcard" />
			 </font>
			</td>
		    <td colspan="4" scope="row" align="middle" width="400" style="font-weight: bold; color:#336699;">
             <font size="2" color="black">
				<bean:message key="qaeventsentry.header.linelistingview.title.action" />
			 </font>
			</td>
		</tr>
</table>
<div class="scrollvertical" style="height:710px;width:100%;BACKGROUND-COLOR: #f7f7e7;">
<table width="100%" border=1>
		<logic:iterate id="sampleWithPendingQaEvents" indexId="i" name="<%=formName%>" property="samplesWithPendingQaEvents" type="us.mn.state.health.lims.qaevent.valueholder.QaEventLineListingViewData">
		 <bean:define id="sample" name="sampleWithPendingQaEvents" property="sample"/>
		 <bean:define id="accessionNumber" name="sample" property="accessionNumber" />
            <%--bugzilla 2545--%>
 			<tr class="normal" onmouseover="highlight(this);" onmouseout="undohighlight(this)">
			    <td colspan="1" scope="row" align="left" valign="top" width="150" style="color:black;">
				  <app:write name="sample" property="accessionNumber" />
				</td>
				<td colspan="1" scope="row" align="left" valign="top" width="170" style="color:black;">
				  <logic:notEmpty name="sampleWithPendingQaEvents" property="patient">
				    <logic:notEmpty name="sampleWithPendingQaEvents" property = "patient.person">
				     <logic:notEmpty name="sampleWithPendingQaEvents" property = "patient.person.firstName">
				      <app:write name="sampleWithPendingQaEvents" property="patient.person.firstName" />
				     </logic:notEmpty>
				     <logic:empty name="sampleWithPendingQaEvents" property = "patient.person.firstName">
				      &nbsp;
				     </logic:empty>
				    </logic:notEmpty>
				    <logic:empty name="sampleWithPendingQaEvents" property = "patient.person">
				     &nbsp;
				    </logic:empty>
				  </logic:notEmpty>
				  <logic:empty name="sampleWithPendingQaEvents" property = "patient">
				     &nbsp;
				  </logic:empty>
				</td>
				<td colspan="1" scope="row" align="left" valign="top" width="170" style="color:black;">
				  <logic:notEmpty name="sampleWithPendingQaEvents" property="patient">
				   <logic:notEmpty name="sampleWithPendingQaEvents" property = "patient.person">
   				     <logic:notEmpty name="sampleWithPendingQaEvents" property = "patient.person.lastName">
				      <app:write name="sampleWithPendingQaEvents" property="patient.person.lastName" />
				     </logic:notEmpty>
				     <logic:empty name="sampleWithPendingQaEvents" property = "patient.person.lastName">
				      &nbsp;
				     </logic:empty>
  				    </logic:notEmpty>
				    <logic:empty name="sampleWithPendingQaEvents" property = "patient.person">
				     &nbsp;
				    </logic:empty>
				  </logic:notEmpty>
				  <logic:empty name="sampleWithPendingQaEvents" property = "patient">
				     &nbsp;
				  </logic:empty>
				</td>
				<td colspan="1" scope="row" align="left" valign="top" width="100" style="color:black;">
				  <logic:notEmpty name="sampleWithPendingQaEvents" property="weight">
					<app:write name="sampleWithPendingQaEvents" property="weight" />
				  </logic:notEmpty>
				  <logic:empty name="sampleWithPendingQaEvents" property="weight">
                    &nbsp;
				  </logic:empty>
				</td>
				<td colspan="1" scope="row" align="left" valign="top" width="100" style="color:black;">
				  <logic:notEmpty name="sampleWithPendingQaEvents" property="yellowCard">
					<app:write name="sampleWithPendingQaEvents" property="yellowCard" />
				  </logic:notEmpty>
				  <logic:empty name="sampleWithPendingQaEvents" property="yellowCard">
                    &nbsp;
				  </logic:empty>
				</td>
                <%--bugzilla 2545--%>
				<td colspan="1" scope="row" align="middle" valign="top" width="100" >
					<a href="" onclick='gotoEditSample("<%=accessionNumber%>");return false;'><bean:message key="qaeventsentry.label.link.edit.sample" /></a>
				</td>
				<td colspan="1" scope="row" align="middle" valign="top" width="100" >
					<a href="" onclick='gotoResultsEntry("<%=accessionNumber%>");return false;'><bean:message key="qaeventsentry.label.link.resultsentry" /></a>
				</td>
				<td colspan="1" scope="row" align="middle" valign="top" width="100" >
					<a href="" onclick='gotoQaEventsEntry("<%=accessionNumber%>");return false;'><bean:message key="qaeventsentry.label.link.qaeventsentry" /></a>
				</td>
				<td colspan="1" scope="row" align="middle" valign="top" width="100" >
				   <a href="" onclick='confirmCompleteAllForSample("<%=accessionNumber%>");return false;'><bean:message key="qaeventsentry.label.link.complete" /></a>
				</td>
			</tr>
		</logic:iterate>
</table>
</div>
</logic:notEmpty>
