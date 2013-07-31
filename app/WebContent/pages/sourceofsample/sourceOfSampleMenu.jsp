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
	   	  <bean:message key="sourceofsample.id"/>
	   </th>
	   --%>
	   <th><%-- bugzilla 1412 --%>
	   	  <bean:message key="sourceofsample.domain"/>
	   </th>
	   <th>
	   	  <bean:message key="sourceofsample.description"/>
	   </th>
	</tr>
	<logic:iterate id="tos" name="<%=formName%>" indexId="ctr" property="menuList" type="us.mn.state.health.lims.sourceofsample.valueholder.SourceOfSample">
	<bean:define id="tosID" name="tos" property="id"/>
	  <tr>
	   <td class="textcontent">
	      <html:multibox name="<%=formName%>" property="selectedIDs">
	         <bean:write name="tosID" />
	      </html:multibox>
   	   </td>
   	   <%--remove the following 09/12/2006 bugzilla 1399--%>
	   <%--
	   <td class="textcontent">
	      <bean:write name="tos" property="id"/>
	   </td>
	   --%>
	   <td class="textcontent">
	      <logic:notEmpty name="tos" property="domain">
	        <bean:write name="tos" property="domain"/>
	      </logic:notEmpty>
	      &nbsp;
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="tos" property="description"/>
	   </td>
     </tr>
	</logic:iterate>
</table>
