<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ page import="us.mn.state.health.lims.common.action.IActionConstants"%>
<%@ page import="us.mn.state.health.lims.common.util.SystemConfiguration"%>
<%@ page import="us.mn.state.health.lims.common.util.Versioning"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="idSeparator" value='<%= SystemConfiguration.getInstance().getDefaultIdSeparator() %>' />

<%!String allowEdits = "true";
String errorMoreRowCount = "";
String path = "";
String basePath = "";
%>
<%
path = request.getContextPath();
basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";

if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
				allowEdits = (String) request
						.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
			}
java.util.Locale locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
			
errorMoreRowCount =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale, "errors.more.rows"); 			
			
			
			
%>
<%--AIS - bugzilla 1853--%>
<script type="text/javascript" src="<%=basePath%>scripts/tableSort.js?ver=<%= Versioning.getBuildNumber() %>"></script>

<script language="JavaScript1.2">

function normalizeString(s) {

  s = s.replace(whtSpMult, " ");  // Collapse any multiple whites space.
  s = s.replace(whtSpEnds, "");   // Remove leading or trailing white space.
  //for collection date
  var ss = s.substring(0,10); 
  if (isDate(ss, "MM/dd/yyyy")){
  	s =ss;
  }
   
  return s;
}

function customOnLoad()
{   
}

function cancelToParentForm()
{
	if (window.opener && !window.opener.closed && window.opener.document.forms[0]) 
	{
		window.close();
	} 
}


function saveItToParentForm(valacc) {
   //this is executed when save is selected 
   var thisval=valacc;
   if (window.opener && !window.opener.closed && window.opener.document.forms[0]) {   
        window.opener.submitTheFormWithAccession(thisval);
        //bugzilla 2447
        //window.close();
   } 
}

function checkRowCount(row){
	var tableE1= document.getElementById("mytable");
	var rowCount = tableE1.rows.length - 1 ;
	if (rowCount > 100 ){	
		 alert("There are "+ rowCount +" records. " + '<%=errorMoreRowCount%>');
	}else{
	 	sortTable('tblBody1', row , true);
	}
	return false;
}

</script>
<%--AIS - bugzilla 1853--%>
<table id="myTable" align="center" border=1 cellspacing=0 cellpadding=0 style='border-collapse:collapse;border:none'>
	<caption style="caption-side:bottom;"><strong><bean:message key="sampletracking.popup.subtitle"/></strong></caption>
	<thead>
		<tr>
			<th class="bre" nowrap="nowrap" align="middle" >
				<a class="sorthead" href="" onclick="this.blur(); return checkRowCount(0);"><bean:message key="sampletracking.popup.accession" /></a>
			</th>
			<th class="bre" nowrap="nowrap" align="middle">
				<a class="sorthead" href="" onclick="this.blur(); return checkRowCount(1);"><bean:message key="sampletracking.popup.submitter" /></a>
			</th>
			<th class="bre" nowrap="nowrap" align="middle">
				<a class="sorthead" href="" onclick="this.blur(); return checkRowCount(2);"><bean:message key="sampletracking.popup.receivedDate" /></a>
			</th>
			<th class="bre" nowrap="nowrap" align="middle">
				<a class="sorthead" href="" onclick="this.blur(); return checkRowCount(3);"><bean:message key="sampletracking.popup.collectionDate" /></a>
			</th>
			<th class="bre" nowrap="nowrap" align="middle">
				<a class="sorthead"href=""  onclick="this.blur(); return checkRowCount(4);"><bean:message key="sampletracking.popup.sampleType" /></a>
			</th>
			<th class="bre" nowrap="nowrap" align="middle">
				<a class="sorthead" href="" onclick="this.blur(); return checkRowCount(5);"><bean:message key="sampletracking.popup.sampleSource" /></a>
			</th>
			<th class="bre" nowrap="nowrap" align="middle">
				<a class="sorthead" href=""  onclick="this.blur(); return checkRowCount(6);"><bean:message key="sampletracking.popup.lastname" /></a>
			</th>
			<th class="bre" nowrap="nowrap" align="middle">
				<a class="sorthead" href="" onclick="this.blur(); return checkRowCount(7);"><bean:message key="sampletracking.popup.firstname" /></a>
			</th>
			<th class="bre" nowrap="nowrap" align="middle">
				<a class="sorthead" href="" onclick="this.blur(); return checkRowCount(8);"><bean:message key="sampletracking.popup.clientRef" /></a>
			</th>
			<th class="bre" nowrap="nowrap" align="middle">
				<a class="sorthead" href="" onclick="this.blur(); return checkRowCount(9);"><bean:message key="sampletracking.popup.dateofBirth" /></a>
			</th>
			<%-- bugzilla 2455 --%>
			<th class="bre" nowrap="nowrap" align="middle">
				<a class="sorthead" href="" onclick="this.blur(); return checkRowCount(10);"><bean:message key="sample.referredCultureFlag" /></a>
			</th>			
		</tr>
	</thead>
	<tbody id="tblBody1">
		<logic:iterate id="item" indexId="i" name="<%=formName%>" property="aId">
			<tr>
				<td align="left" width=118 valign=top style='width:69.55pt;
  					padding:0in 5.4pt 0in 5.4pt;border-color:#336699' nowrap="nowrap">
					<A HREF="javascript:saveItToParentForm('<bean:write name="item" />');"> <strong> <app:write name="<%=formName%>" property='<%= "aId[" + i + "]" %>' /> </strong> </A>
				</td>
				<td align="left" width=118 valign=top style='width:32.55pt;
  					padding:0in 5.4pt 0in 5.4pt;border-color:#336699' nowrap="nowrap">
					<a class="hoverinformation" href="" title='<bean:write name="<%=formName%>" property='<%= "oRgname[" + i + "]" %>' />' onclick="return false;"> <app:write name="<%=formName%>" property='<%= "oRg[" + i + "]" %>' /> </a>
				</td>
				<td align="left" width=118 valign=top style='width:55.55pt;
  					padding:0in 5.4pt 0in 5.4pt;border-color:#336699' nowrap="nowrap">
					<app:write name="<%=formName%>" property='<%= "receivedDate[" + i + "]" %>' />
				</td>
				<td align="left" width=118 valign=top style='width:105.55pt;
  					padding:0in 5.4pt 0in 5.4pt;border-color:#336699' nowrap="nowrap">
					<app:write name="<%=formName%>" property='<%= "collectionDate[" + i + "]" %>' />
					&nbsp;
					<app:write name="<%=formName%>" property='<%= "collectionTime[" + i + "]" %>' />
				</td>
				<td align="left" width=200 valign=top style='width:100.55pt;
  					padding:0in 5.4pt 0in 5.4pt;border-color:#336699' nowrap="nowrap">
					<a class="hoverinformation" href="" title='<bean:write name="<%=formName%>" property='<%= "sampleType[" + i + "]" %>' />' onclick="return false;"> <app:write name="<%=formName%>" property='<%= "sampleTypepart[" + i + "]" %>' /> </a>
				</td>
				<td align="left" width=118 valign=top style='width:80.55pt;
  					padding:0in 5.4pt 0in 5.4pt;border-color:#336699' nowrap="nowrap">
					<a class="hoverinformation" href="" title='<bean:write name="<%=formName%>" property='<%= "sampleSource[" + i + "]" %>' />' onclick="return false;"> <app:write name="<%=formName%>" property='<%= "sampleSourcepart[" + i + "]" %>' /> </a>
				</td>
				<td align="left" width=118 valign=top style='width:65.55pt;
  					padding:0in 5.4pt 0in 5.4pt;border-color:#336699' nowrap="nowrap">
					<a class="hoverinformation" href="" title='<bean:write name="<%=formName%>" property='<%= "lname[" + i + "]" %>' />' onclick="return false;"> <app:write name="<%=formName%>" property='<%= "lnamepart[" + i + "]" %>' /> </a>
				</td>
				<td align="left" width=118 valign=top style='width:65.55pt;
					padding:0in 5.4pt 0in 5.4pt;border-color:#336699' nowrap="nowrap">
					<a class="hoverinformation" href="" title='<bean:write name="<%=formName%>" property='<%= "fname[" + i + "]" %>'/>' onclick="return false;"> <app:write name="<%=formName%>" property='<%= "fnamepart[" + i + "]" %>' /> </a>
				</td>
				<td align="left" width=118 valign=top style='width:50.55pt;
					padding:0in 5.4pt 0in 5.4pt;border-color:#336699' nowrap="nowrap">
					<a class="hoverinformation" href="" title='<bean:write name="<%=formName%>" property='<%= "cReference[" + i + "]" %>' />' onclick="return false;"> <app:write name="<%=formName%>" property='<%= "cReferencepart[" + i + "]" %>' /> </a>
				</td>
				<td align="left" width=118 valign=top style='width:50.55pt;
  					padding:0in 5.4pt 0in 5.4pt;border-color:#336699' nowrap="nowrap">
					<app:write name="<%=formName%>" property='<%= "dOb[" + i + "]" %>' />
				</td>
				<!-- bugzilla 2455 -->
				<td align="left" width=118 valign=top style='width:50.55pt;
  					padding:0in 5.4pt 0in 5.4pt;border-color:#336699' nowrap="nowrap">
					<app:write name="<%=formName%>" property='<%= "specimenOrIsolate[" + i + "]" %>' />
				</td>				
			</tr>
		</logic:iterate>
	</tbody>
</table>

