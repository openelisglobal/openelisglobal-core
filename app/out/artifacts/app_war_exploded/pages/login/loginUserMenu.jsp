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
	   	  <bean:message key="login.login.name"/>
	   </th>
	   <th>
	   	  <bean:message key="login.password"/>
	   </th>
	   <th>
	   	  <bean:message key="login.password.expired.date"/>
	   </th>
	   <th>
	   	  <bean:message key="login.account.locked"/>
	   </th>
	   <th>
	   	  <bean:message key="login.account.disabled"/>
	   </th>
	   <th>
	   	  <bean:message key="login.is.admin"/>
	   </th>	 
	   <th>
	   	  <bean:message key="login.timeout"/>
	   </th>	  	     	   
	</tr>
	<logic:iterate id="login" indexId="ctr" name="<%=formName%>" property="menuList" type="us.mn.state.health.lims.login.valueholder.Login">
	<bean:define id="loginID" name="login" property="id"/>
	  <tr>
	   <td class="textcontent">
	      <html:multibox name="<%=formName%>" property="selectedIDs">
	         <bean:write name="loginID" />
	      </html:multibox>
     
   	   </td>	  
	   <td class="textcontent">
	   	  <bean:write name="login" property="loginName"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="login" property="password"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="login" property="passwordExpiredDateForDisplay"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="login" property="accountLocked"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="login" property="accountDisabled"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="login" property="isAdmin"/>
	   </td>	
	   <td class="textcontent">
	   	  <bean:write name="login" property="userTimeOut"/>
	   </td>		      	   	   	   
       </tr>
	</logic:iterate>
</table>
