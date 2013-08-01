<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	java.util.Hashtable,
	us.mn.state.health.lims.systemuser.valueholder.SystemUser,
	us.mn.state.health.lims.systemmodule.valueholder.SystemModule,
	us.mn.state.health.lims.common.action.IActionConstants,
	us.mn.state.health.lims.systemusermodule.valueholder.PermissionModule" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />


<table width="100%" border="2">
	<tr>
	   <th>
	     <bean:message key="label.form.select"/>
	   </th>
	   <th>
	   	  <bean:message key="systemusermodule.system.user.id"/> / <bean:message key="systemuser.name"/>
	   </th>
	   <th>
	   	  <bean:message key="systemusermodule.system.module.id"/> / <bean:message key="systemmodule.description"/>
	   </th>
	   <th>
	   	  <bean:message key="systemusermodule.has.select"/>
	   </th>
	   <th>
	   	  <bean:message key="systemusermodule.has.add"/>
	   </th>
	   <th>
	   	  <bean:message key="systemusermodule.has.update"/>
	   </th>
	   <th>
	   	  <bean:message key="systemusermodule.has.delete"/>
	   </th>	   	   
	</tr>
	<logic:iterate id="agentModule" indexId="ctr" name="<%=formName%>" property="menuList" type="PermissionModule">
	<bean:define id="agentId" name="agentModule" property="id"/>
	  <tr>
	   <td class="textcontent">
	      <html:multibox name="<%=formName%>" property="selectedIDs">
	         <bean:write name="agentId" />
	      </html:multibox>
   	   </td>
	   <td class="textcontent">
	   		<logic:notEmpty name="agentModule" property="permissionAgent">
	   	  		<bean:write name="agentModule" property="permissionAgent.id"/>
	   	  		&nbsp;&nbsp;
				<bean:write name="agentModule" property="permissionAgent.displayName"/> 
		   	</logic:notEmpty>
 		   	 &nbsp;
	   </td>
	   <td class="textcontent">
	   		<logic:notEmpty name="agentModule" property="systemModule">
	   	  		<bean:write name="agentModule" property="systemModule.id"/>
	   	  		&nbsp;&nbsp;
	   	  		<bean:write name="agentModule" property="systemModule.description"/>
		   	</logic:notEmpty>
		   	 &nbsp;	   
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="agentModule" property="hasSelect"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="agentModule" property="hasAdd"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="agentModule" property="hasUpdate"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="agentModule" property="hasDelete"/>
	   </td>	   	   	   	   
       </tr>
	</logic:iterate>
</table>
