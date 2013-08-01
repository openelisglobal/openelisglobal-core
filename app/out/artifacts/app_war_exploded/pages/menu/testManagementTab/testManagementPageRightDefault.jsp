<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>


<table>
	<tr>
		<td valign="top">
			<tiles:insert attribute="top" />
		</td>
	</tr>

	<tr>
		<td valign="top">
			<tiles:insert attribute="middle" />
		</td>
	</tr>

	<tr>
		<td valign="top">
			<tiles:insert attribute="bottom" />
		</td>
	</tr>

</table>
