<%@ page language="java" contentType="text/html; charset=utf-8" 
    import="us.mn.state.health.lims.common.action.IActionConstants, us.mn.state.health.lims.testmanagement.valueholder.TestManagementRoutingSwitchSessionHandler"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>

<%--bugzilla 1774--%>
<bean:define id="accnParm" value='<%= IActionConstants.ACCESSION_NUMBER%>' />

<%!
//bugzilla 1774
String comingFromResultsEntry = "false";
//bugzilla 2053
String comingFromQaEventsEntry = "false";
//bugzilla 2504
String comingFromQaEventsEntryLineListing = "false";
String accessionNumber = null;
%>
<%

//bugzilla 1774 TEST_MANAGEMENT_ROUTING_FROM_RESULTS_ENTRY
//modified with 2053
if (TestManagementRoutingSwitchSessionHandler.isSwitchOn(IActionConstants.TEST_MANAGEMENT_ROUTING_FROM_RESULTS_ENTRY, session)) {
  comingFromResultsEntry = "true";
} else {
  comingFromResultsEntry = "false";
}

//bugzilla 2053
if (TestManagementRoutingSwitchSessionHandler.isSwitchOn(IActionConstants.TEST_MANAGEMENT_ROUTING_FROM_QAEVENTS_ENTRY, session)) {
  comingFromQaEventsEntry = "true";
} else {
  comingFromQaEventsEntry = "false";
}

//bugzilla 2504
if (TestManagementRoutingSwitchSessionHandler.isSwitchOn(IActionConstants.TEST_MANAGEMENT_ROUTING_FROM_QAEVENTS_ENTRY_LINELISTING, session)) {
  comingFromQaEventsEntryLineListing = "true";
} else {
  comingFromQaEventsEntryLineListing = "false";
}

%>
<script>

//bugzilla 1774
function exit() {
 var accessionNumber = document.getElementById("accessionNumber").value;
 var comingFromResultsEntry = '<%=comingFromResultsEntry%>';
  var comingFromQaEventsEntry = '<%=comingFromQaEventsEntry%>';
//bugzilla 2504
 var comingFromQaEventsEntryLineListing = '<%=comingFromQaEventsEntryLineListing%>';
 if (comingFromResultsEntry == 'true') {
    setAction(window.document.forms[0], 'CancelToResultsEntry', 'yes', '?' + '<%=accnParm%>' + '=' + accessionNumber + '&ID=');
 } else if (comingFromQaEventsEntry == 'true') {
     setAction(window.document.forms[0], 'CancelToQaEventsEntry', 'yes', '?' + '<%=accnParm%>' + '=' + accessionNumber + '&ID=');
//bugzilla 2504
 } else if (comingFromQaEventsEntryLineListing == 'true') {
     setAction(window.document.forms[0], 'CancelToQaEventsEntryLineListing', 'yes', '?' + '<%=accnParm%>' + '=' + accessionNumber + '&ID=');
 } else {
    setAction(window.document.forms[0], '', '', '');
 }
}

function gotoQaEvents(form) {
    setAction(window.document.forms[0], 'ViewQaEventsEntryFrom', 'yes', '');
}
</script>

<center>
	<table border="0" cellpadding="0" cellspacing="0">
		<tbody valign="middle">
			<tr>
<%
String allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);

%>
				<td>
            <html:button onclick="editSamplePopup(window.document.forms[0]);" property="editSample" disabled="<%=Boolean.valueOf(allowEdits).booleanValue()%>">
				<bean:message key="testmanagement.label.button.editSample" />
			</html:button>
				</td>
				<td>
					&nbsp;
				</td>
				<td>
                    <%--bugzilla 1774--%>
					<html:button onclick="exit();" property="cancel">
						<%--AIS - bugzilla 1713--%>						 
						<bean:message key="label.button.exit" />
					</html:button>
				</td>

				<td>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</td>
				<td>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</td>
				<td>
					&nbsp;
				</td>
				
                <td>
                    <html:button  onclick="gotoQaEvents(window.document.forms[0]);" property="qaEvents">
  			           <bean:message key="testmanagement.label.button.qaevents"/>
  			        </html:button>
                </td>
			    <td>
					&nbsp;
				</td>
				<td>
					<app:button onclick="setAction(window.document.forms[0], '', '', '');" property="cancel">
						<bean:message key="testmanagement.label.button.rePrintSampleLabels" />
					</app:button>

				</td>
				<td>
					&nbsp;
				</td>
				<td>
					<app:button onclick="setAction(window.document.forms[0], '', '', '');" property="cancel">
						<bean:message key="label.button.printTestLabels" />
					</app:button>
				</td>

				<td>
					&nbsp;
				</td>
				<td>
					<app:button onclick="setAction(window.document.forms[0], '', '', '');" property="cancel">
						<bean:message key="label.button.printRequestForm" />
					</app:button>
				</td>
				<td>
					&nbsp;
				</td>
			</tr>
		</tbody>
	</table>
</center>