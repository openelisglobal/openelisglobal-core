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
	   <%--remove the following 09/12/2006 bugzilla 1399--%>
	   <%--
	   <th>
	      <bean:message key="systemuser.id"/>
	   </th>
	   --%>
	   <th><%--bugzilla 1412--%>
	   	  <bean:message key="systemuser.lastName"/>
	   </th>
	   <th>
	      <bean:message key="systemuser.firstName"/>
	   </th>
	   <th>
	   	  <bean:message key="systemuser.loginName"/>
	   </th>
	   <th>
	   	  <bean:message key="systemuser.initials"/>
	   </th>
	   <th>
	   	  <bean:message key="systemuser.externalId"/>
	   </th>
	   <th>
	   	  <bean:message key="systemuser.isActive"/>
	   </th>
	   <th>
	   	  <bean:message key="systemuser.isEmployee"/>
	   </th>
	   
	</tr>
	<logic:iterate id="systemUser" indexId="ctr" name="<%=formName%>" property="menuList" type="us.mn.state.health.lims.systemuser.valueholder.SystemUser">
	<bean:define id="systemUserID" name="systemUser" property="id"/>
	  <tr>
	   <td class="textcontent">
	      <html:multibox name="<%=formName%>" property="selectedIDs">
	         <bean:write name="systemUserID" />
	      </html:multibox>
     
   	   </td>
   	   <%--remove the following 09/12/2006 bugzilla 1399--%>
	   <%--
	   <td class="textcontent">
	      <bean:write name="systemUser" property="id"/>
	   </td>
	   --%>
	   <td class="textcontent">
	   	  <bean:write name="systemUser" property="lastName"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="systemUser" property="firstName"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="systemUser" property="loginName"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="systemUser" property="initials"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="systemUser" property="externalId"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="systemUser" property="isActive"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="systemUser" property="isEmployee"/>
	   </td>
       </tr>
	</logic:iterate>
</table>
