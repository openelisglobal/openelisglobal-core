<%@ page language="java" contentType="text/html; charset=utf-8"
	import="java.util.Date,java.util.Hashtable,us.mn.state.health.lims.common.action.IActionConstants"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>

<bean:define id="formName"
	value='<%=(String) request
									.getAttribute(IActionConstants.FORM_NAME)%>' />

<table width="100%" border=2">
	<tr>
		<th>
			<bean:message key="paymenttype.form.select" />
		</th>
		<th>
			<bean:message key="paymenttype.type" />
		</th>
		<th>
			<bean:message key="paymenttype.description" />
		</th>
	</tr>		
	<logic:iterate id="paymenttype" indexId="ctr" name="<%=formName%>"
		property="menuList"
		type="vn.com.gcs.lis.paymenttype.valueholder.PaymentType">
		<bean:define id="paymenttypeID" name="paymenttype" property="id" />
		<tr>
			<td class="textcontent">
				<html:multibox name='<%=formName%>' property="selectedIDs">
					<bean:write name="paymenttypeID" />
				</html:multibox>
			</td>
			<td class="textcontent">
				<bean:write name="paymenttype" property="type" />
			</td>
			<td class="textcontent">
				<bean:write name="paymenttype" property="description" />
			</td>			
		</tr>
	</logic:iterate>
</table>
