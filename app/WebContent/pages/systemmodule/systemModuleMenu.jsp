<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	java.util.Hashtable,
	us.mn.state.health.lims.common.action.IActionConstants" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />


<table width="100%" border=2">
	<tr>
	   <th>
	     <bean:message key="label.form.select"/>
	   </th>
	   <th>
	   	  <bean:message key="systemmodule.name"/>
	   </th>
	   <th>
	   	  <bean:message key="systemmodule.description"/>
	   </th>
	   <th>
	   	  <bean:message key="systemmodule.has.select"/>
	   </th>
	   <th>
	   	  <bean:message key="systemmodule.has.add"/>
	   </th>
	   <th>
	   	  <bean:message key="systemmodule.has.update"/>
	   </th>
	   <th>
	   	  <bean:message key="systemmodule.has.delete"/>
	   </th>	   	   
	</tr>
	<logic:iterate id="systemModule" indexId="ctr" name="<%=formName%>" property="menuList" type="us.mn.state.health.lims.systemmodule.valueholder.SystemModule">
	<bean:define id="systemModuleID" name="systemModule" property="id"/>
	  <tr>
	   <td class="textcontent">
	      <html:multibox name="<%=formName%>" property="selectedIDs">
	         <bean:write name="systemModuleID" />
	      </html:multibox>
     
   	   </td>
	   <td class="textcontent">
	   	  <bean:write name="systemModule" property="systemModuleName"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="systemModule" property="description"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="systemModule" property="hasSelectFlag"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="systemModule" property="hasAddFlag"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="systemModule" property="hasUpdateFlag"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="systemModule" property="hasDeleteFlag"/>
	   </td>	   	   	   	   
       </tr>
	</logic:iterate>
</table>
