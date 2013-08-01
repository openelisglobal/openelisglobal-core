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
	      <bean:message key="action.id"/>
	   </th>
	   --%>
	   <th>
	   	  <bean:message key="action.type"/>
	   </th>
	   <th>
	   	  <bean:message key="action.code"/>
	   </th>
	   <th>
	   	  <bean:message key="action.description"/>
	   </th>
   
	</tr>
	<logic:iterate id="act" indexId="ctr" name="<%=formName%>" property="menuList" type="us.mn.state.health.lims.action.valueholder.Action">
	<bean:define id="actID" name="act" property="id"/>
	  <tr>
	   <td class="textcontent">
	      <html:multibox name="<%=formName%>" property="selectedIDs">
	         <bean:write name="actID" />
	      </html:multibox>
     
   	   </td>
   	   <%--remove the following 09/12/2006 bugzilla 1399--%>
	   <%--
	   <td class="textcontent">
	      <bean:write name="act" property="id"/>
	   </td>
	   --%>
	   <td class="textcontent">
	   	  <bean:write name="act" property="type"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="act" property="code"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="act" property="description"/>
	   </td>
       </tr>
	</logic:iterate>
</table>
