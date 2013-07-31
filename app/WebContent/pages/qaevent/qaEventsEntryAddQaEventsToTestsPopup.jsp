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
String qaEventParam = "";
String testParam = "";
%>
<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
	allowEdits = (String) request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
qaEventParam = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "qaeventsentry.qaevent.title");
testParam = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "qaeventsentry.test.title");
errorNothingSelected =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "errors.required.atLeastOneItem.forTwoSelectBoxes", qaEventParam, testParam);
%>

<script language="JavaScript1.2">

function nothingSelected() {
  var nthSel = true;
  var pickSel = false;
  var selSel = false;
  
  var pickList = $("PickList");
  var selectList = $("SelectList")
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
		window.opener.setAddQaEventResults(form);
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
        <strong><bean:message key="qaeventsentry.addQaEventToTestsPopup.qaevent.title"/></strong>
      </td>
   	  <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
      <td>
        <strong><bean:message key="qaeventsentry.addQaEventToTestsPopup.test.title"/></strong>
      </td>
    </tr>
	<tr>
		<td><%--bugzilla 2548 increase width--%>
			<select name="SelectList" 
			        id="SelectList" 
			        size="20" 
			        multiple="multiple" 
			        style="width: 600px" 
			        type="us.mn.state.health.lims.qaevent.valueholder.QaEvent"
			        onkeypress="return selectAsYouType(event)" 
			        onblur="clearKeysPressed(event)"					
			        >
			   <logic:iterate id="qaEvent" property="SelectList" name="<%=formName%>">
					<bean:define id="qaEventId" name="qaEvent" property="id" />
					<option value="<%=qaEventId%>">
						<bean:write name="qaEvent" property="qaEventDisplayValue" />
        			</option>
				</logic:iterate>
			</select>
		</td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td><%--bugzilla 2548 increase width--%>
			<select name="PickList"
			        id="PickList" 
			        size="20" 
			        multiple="multiple" 
			        style="width: 600px" 
			        type="us.mn.state.health.lims.test.valueholder.Test"
			>
			    <logic:iterate id="test" property="PickList" name="<%=formName%>">
					<bean:define id="testId" name="test" property="id" />
					<option value="<%=testId%>">
						<bean:write name="test" property="testDisplayValue" />
        			</option>
				</logic:iterate>
			</select>
		</td>
	</tr>
</table>

