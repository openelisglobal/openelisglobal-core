<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date, java.util.List, java.util.Locale,
	org.apache.struts.Globals,
	us.mn.state.health.lims.common.action.IActionConstants,
	us.mn.state.health.lims.common.util.SystemConfiguration" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="idSeparator" value='<%= SystemConfiguration.getInstance().getDefaultIdSeparator() %>' />

<%!
String allowEdits = "true";
Locale locale = null;
String errorNothingSelected = "";
String actionParam = "";
String analysisQaEventParam = "";
%>
<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
	allowEdits = (String) request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
actionParam = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "qaeventsentry.action.title");
analysisQaEventParam = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "qaeventsentry.qaevent.title");
errorNothingSelected =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "errors.required.atLeastOneItem.forTwoSelectBoxes", actionParam, analysisQaEventParam);
%>

<script language="JavaScript1.2">

function nothingSelected() {
  var nthSel = true;
  var pickSel = false;
  var selSel = false;
  // bugzilla 2503
  var pickList = $("pickList");
  var selectList = $("selectList");
  var pickOptions = pickList.options;
  var pickOLength = pickOptions.length;
  var selectOptions = selectList.options;
  var selectOLength = selectOptions.length;
  if (pickOLength > 0 && selectOLength > 0) {  
    for (var i = 0; i < pickOLength; i++) {
      if (pickOptions[i].selected == true) {
        pickSel = true;
      }
    }
    for (var i = 0; i < selectOLength; i++) {
      if (selectOptions[i].selected == true) {
        selSel = true;
      }
    }
    if (pickSel && selSel) {
      nthSel = false;
    }
  }
  return nthSel;
}

function cancelToParentForm()
{
	if (window.opener && !window.opener.closed && window.opener.document.forms[0]) 
	{
		window.close();
	} 
}

function saveItToParentForm(form) 
{
	if (window.opener && !window.opener.closed && window.opener.document.forms[0]) 
	{
		if (nothingSelected()) {
		 alert("<%=errorNothingSelected%>");
		 return false;
		} else {
		window.opener.setAddActionResults(form);
		window.close();
		}
	}
}

function customOnLoad() {
}

</script>
<table align="center">
    <tr>
      <td>
        <strong><bean:message key="qaeventsentry.addActionToQaEventsPopup.action.title"/></strong>
      </td>
   	  <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
      <td>
        <strong><bean:message key="qaeventsentry.addActionToQaEventsPopup.qaevent.title"/></strong>
      </td>
    </tr>
	<tr>
		<td>
		    <!--  bugzilla 2503 -->
			<select name="<%=formName%>"
			        property="SelectList" 
			        id = "SelectList"
			        size="20" 
			        multiple="multiple" 
			        style="width: 300px" 
			        type="us.mn.state.health.lims.action.valueholder.Action"
			        onkeypress="return selectAsYouType(event)" 
			        onblur="clearKeysPressed(event)"					
			        >
			   <logic:iterate id="action" property="SelectList" name="<%=formName%>">
					<bean:define id="actionId" name="action" property="id" />
					<option value="<%=actionId%>">
						<bean:write name="action" property="actionDisplayValue" />
        			</option>
				</logic:iterate>
			</select>
		</td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td>
		    <!--  bugzilla 2503 -->
		    <select name="<%=formName%>"
			        property="PickList"
			        id="PickList"
			        size="20" 
			        multiple="multiple" 
			        style="width: 500px" 
			        type="us.mn.state.health.lims.analysisqaevent.valueholder.AnalysisQaEvent"
			>
			    <logic:iterate id="analQaEvent" property="PickList" name="<%=formName%>">
					<bean:define id="analysisQaEventId" name="analQaEvent" property="id" />
					<option value="<%=analysisQaEventId%>">
						<bean:write name="analQaEvent" property="analysisQaEventDisplayValue" />
        			</option>
				</logic:iterate>
			</select>
		</td>
	</tr>
</table>

