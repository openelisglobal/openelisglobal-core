<%@ page language="java" contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants,
	us.mn.state.health.lims.common.util.StringUtil,
	us.mn.state.health.lims.userrole.action.UserRoleAction"%>
<%@ page import="us.mn.state.health.lims.analyzer.valueholder.Analyzer" %>
<%@ page import="us.mn.state.health.lims.test.valueholder.Test" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>

<bean:define id="formName" value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>' />
<bean:define id="analyzerList" name="<%=formName%>" property="analyzerList" type="java.util.List<Analyzer>" />
<bean:define id="analyzerName" name="<%=formName%>" property="analyzerId" />
<bean:define id="testList" name="<%=formName%>" property="testList" type="java.util.List<Test>" />
<bean:define id="testName" name="<%=formName%>" property="testId" />

<%!String allowEdits = "true";%>

<%
	if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
		allowEdits = (String) request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
	}
%>


<script language="JavaScript1.2">

	$jq(document).ready( function() {
		$jq("#analyzerIdHidden").val($jq("#analyzerId").val());
		if($jq("#analyzerId").val() != 0 ){
			$jq("#analyzerId").attr('disabled', 'disabled');
			$jq("#analyzerTestNameId").attr('disabled','disabled');
		}
	});
function validateForm(form) {

	if( $("analyzerId").selectedIndex == 0 ||
	    $("analyzerTestNameId").value == null ||
	    $("testId").selectedIndex == 0 ){
	    		alert('<%=StringUtil.getMessageForKey("error.all.required") %>');
	    		return false;
	    }

    return true;
}

	function copyToHiddenAnalyzer( element ){
		$jq("#analyzerIdHidden").val(element.value);
	}

</script>

<table width="60%">
	<tr>
		<td class="label" width="20%">
			<bean:message key="analyzer.label" />
			:
			<span class="requiredlabel">*</span>
		</td>
		<td width="80%">
			<html:hidden name="<%=formName%>" property="analyzerId" styleId="analyzerIdHidden" />
			<select id="analyzerId" onchange="copyToHiddenAnalyzer(this);" >
				<option value="0"></option>
				<% for( Analyzer analyzer : analyzerList ){%>
				<option value="<%=analyzer.getId() %>" <%= analyzer.getName().equals(analyzerName) ? "selected='selected'" : "" %> ><%=analyzer.getName()%></option>
				<% } %>
			</select>

		</td>
	</tr>
	<tr>
		<td class="label" >
		<bean:message key="analyzer.test.name" />
			:
			<span class="requiredlabel">*</span>
		</td>
		<td >
			<html:text name='<%=formName%>' property="analyzerTestName" styleId="analyzerTestNameId" />
		</td>
	</tr>
	<tr>
		<td class="label" >
		<bean:message key="analyzer.test.actual.name" />
			:
			<span class="requiredlabel">*</span>
		</td>
		<td >
			<html:select name="<%=formName%>" property="testId" styleId="testId" >
				<html:option value="0">&nbsp;</html:option>
				<% for( Test test : testList ){%>
				<option value="<%=test.getId() %>" <%= test.getName().equals(testName) ? "selected='selected'" : "" %> ><%=test.getName()%></option>
				<% } %>
			</html:select>
		</td>
	</tr>
	<tr>
		<td>
			&nbsp;
		</td>
	</tr>
</table>


