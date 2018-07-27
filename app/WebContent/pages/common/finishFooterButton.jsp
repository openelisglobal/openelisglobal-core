<%@ page language="java" contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>


	<table border="0" cellpadding="0" cellspacing="4" width="100%">
		<tbody valign="middle">
			<tr>
				<td align="middle">
					<html:button onclick="finish();"
						property="finishButton"
						styleId="finishButtonId">
						<bean:message key="footer.button.finish" />
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