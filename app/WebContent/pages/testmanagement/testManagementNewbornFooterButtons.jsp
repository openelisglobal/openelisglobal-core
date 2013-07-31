<%@ page language="java" contentType="text/html; charset=utf-8" 
    import="us.mn.state.health.lims.common.action.IActionConstants, us.mn.state.health.lims.testmanagement.valueholder.TestManagementRoutingSwitchSessionHandler"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>

<bean:define id="accnParm" value='<%= IActionConstants.ACCESSION_NUMBER%>' />

<%
	String comingFromResultsEntry = "false";
	String comingFromQaEventsEntry = "false";
	String comingFromQaEventsEntryLineListing = "false";
	String accessionNumber = null;

	if (TestManagementRoutingSwitchSessionHandler.isSwitchOn(IActionConstants.TEST_MANAGEMENT_ROUTING_FROM_RESULTS_ENTRY, session)) {
  		comingFromResultsEntry = "true";
	} else {
  		comingFromResultsEntry = "false";
	}

	if (TestManagementRoutingSwitchSessionHandler.isSwitchOn(IActionConstants.TEST_MANAGEMENT_ROUTING_FROM_QAEVENTS_ENTRY, session)) {
  		comingFromQaEventsEntry = "true";
	} else {
  		comingFromQaEventsEntry = "false";
	}

	if (TestManagementRoutingSwitchSessionHandler.isSwitchOn(IActionConstants.TEST_MANAGEMENT_ROUTING_FROM_QAEVENTS_ENTRY_LINELISTING, session)) {
  		comingFromQaEventsEntryLineListing = "true";
	} else {
  		comingFromQaEventsEntryLineListing = "false";
	}

%>
<script>

function exit() {
 	var accessionNumber = document.getElementById("accessionNumber").value;
 	var comingFromResultsEntry = '<%=comingFromResultsEntry%>';
  	var comingFromQaEventsEntry = '<%=comingFromQaEventsEntry%>';
 	var comingFromQaEventsEntryLineListing = '<%=comingFromQaEventsEntryLineListing%>';

 	if (comingFromResultsEntry == 'true') {
    	setAction(window.document.forms[0], 'CancelToResultsEntry', 'yes', '?' + '<%=accnParm%>' + '=' + accessionNumber + '&ID=');
 	} else if (comingFromQaEventsEntry == 'true') {
     	setAction(window.document.forms[0], 'CancelToQaEventsEntry', 'yes', '?' + '<%=accnParm%>' + '=' + accessionNumber + '&ID=');
	} else if (comingFromQaEventsEntryLineListing == 'true') {
    	setAction(window.document.forms[0], 'CancelToQaEventsEntryLineListing', 'yes', '?' + '<%=accnParm%>' + '=' + accessionNumber + '&ID=');
 	} else {
		document.forms[0].name='TestManagementForm';
    	setAction(window.document.forms[0], '', '', '');
 	}
}

function gotoQaEvents(form) {
	document.forms[0].name ='TestManagementForm';
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
            <html:button onclick="validateAccessionNumber2();" property="editSample" disabled="<%=Boolean.valueOf(allowEdits).booleanValue()%>">
				<bean:message key="testmanagement.label.button.editSample" />
			</html:button>
				</td>
				<td>
					&nbsp;
				</td>
				<td>
					<html:button onclick="exit();" property="cancel">					 
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
					<html:button onclick="setAction(window.document.forms[0], '', '', '');" property="cancel" disabled="true">
						<bean:message key="testmanagement.label.button.rePrintSampleLabels" />
					</html:button>

				</td>
				<td>
					&nbsp;
				</td>
				<td>
					<html:button onclick="setAction(window.document.forms[0], '', '', '');" property="cancel" disabled="true">
						<bean:message key="label.button.printTestLabels" />
					</html:button>
				</td>

				<td>
					&nbsp;
				</td>
				<td>
					<html:button onclick="setAction(window.document.forms[0], '', '', '');" property="cancel" disabled="true">
						<bean:message key="label.button.printRequestForm" />
					</html:button>
				</td>
				<td>
					&nbsp;
				</td>
			</tr>
		</tbody>
	</table>
</center>