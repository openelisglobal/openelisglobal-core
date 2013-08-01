<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	java.util.Hashtable,
	us.mn.state.health.lims.systemuser.valueholder.SystemUser,
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
	      <bean:message key="method.id"/>
	   </th>
	   --%>
	   <th>
	   	  <bean:message key="method.methodName"/>
	   </th>
	   <th>
	   	  <bean:message key="method.description"/>
	   </th>
	   <th>
	   	  <bean:message key="method.activeBeginDate"/>
	   </th>
       <th>
	   	  <bean:message key="method.isActive"/>
	   </th>
	   
	</tr>
	<logic:iterate id="meth" indexId="ctr" name="<%=formName%>" property="menuList" type="us.mn.state.health.lims.method.valueholder.Method">
	<bean:define id="methID" name="meth" property="id"/>
	  <tr>
	   <td class="textcontent">
	      <html:multibox name="<%=formName%>" property="selectedIDs">
	         <bean:write name="methID" />
	      </html:multibox>
     
   	   </td>
   	   <%--remove the following 09/12/2006 bugzilla 1399--%>
	   <%--
	   <td class="textcontent">
	      <bean:write name="meth" property="id"/>
	   </td>
	   --%>
	   <td class="textcontent">
	   	  <bean:write name="meth" property="methodName"/>
	   </td>
	   <td class="textcontent">
	      <logic:notEmpty name="meth" property="description">
	   	  <bean:write name="meth" property="description"/>
	   	  </logic:notEmpty>
	   	  <logic:empty name="meth" property="description">
	   	   &nbsp;
	   	  </logic:empty>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="meth" property="activeBeginDateForDisplay"/>
	   </td>
       <td class="textcontent">
	   	  <bean:write name="meth" property="isActive"/>
	   </td>
	     </tr>
	</logic:iterate>
</table>
