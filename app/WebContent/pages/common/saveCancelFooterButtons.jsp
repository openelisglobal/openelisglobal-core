<%@ page language="java" contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>


<%
	String saveDisabled = (String) request.getSession().getAttribute(IActionConstants.SAVE_DISABLED);
%>

	<table border="0" cellpadding="0" cellspacing="4" width="100%">
		<tbody valign="middle">
			<tr>
				<td align="right">
					<html:button onclick="savePage();"
						property="save"
						styleId="saveButtonId"
						disabled="<%=Boolean.valueOf(saveDisabled).booleanValue()%>">
						<bean:message key="label.button.save" />
					</html:button>
				</td>
				<td>
					<html:button
						onclick="setMyCancelAction(window.document.forms[0], 'Cancel', 'no', '');"
						property="cancel"
						styleId="cancelButtonId"
						>
						<bean:message key="label.button.cancel" />
					</html:button>
				</td>
			</tr>
		</tbody>
	</table>

<script type="text/javascript">

<%if( request.getAttribute(IActionConstants.FWD_SUCCESS) != null &&
      ((Boolean)request.getAttribute(IActionConstants.FWD_SUCCESS)) ) { %>
if( typeof(showSuccessMessage) != 'undefined' ){
	showSuccessMessage( true );
}
<% } %>

</script>