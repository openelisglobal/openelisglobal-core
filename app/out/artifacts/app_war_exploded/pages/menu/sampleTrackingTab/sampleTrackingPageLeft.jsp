<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>


<table width="100%">
	<tr>
		<td width="15%" valign="top" align="left">
			<tiles:insert attribute="left" />
		</td>
		<td width="21%" valign="top" align="left">
			<tiles:insert attribute="middle" />
		</td>
		<td width="25%" valign="top" align="left">
			<tiles:insert attribute="right" />
		</td>
	</tr>
</table>

