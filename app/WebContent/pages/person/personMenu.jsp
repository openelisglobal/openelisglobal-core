<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	java.util.Hashtable,
	us.mn.state.health.lims.region.valueholder.Region,
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
	      <bean:message key="person.id"/>
	   </th>
	   --%>
	   <th>
	   	  <bean:message key="person.lastName"/>
	   </th>
	   <th>
	   	  <bean:message key="person.firstName"/>
	   </th>
	   <th>
	   	  <bean:message key="person.middleName"/>
	   </th>

	</tr>
	<logic:iterate id="pers" indexId="ctr" name="<%=formName%>" property="menuList" type="us.mn.state.health.lims.person.valueholder.Person">
	<bean:define id="persID" name="pers" property="id"/>
	  <tr>
	   <td class="textcontent">
	      <html:multibox name="<%=formName%>" property="selectedIDs">
	         <bean:write name="persID" />
	      </html:multibox>
     
   	   </td>
   	   <%--remove the following 09/12/2006 bugzilla 1399--%>
	   <%--
	   <td class="textcontent">
	      <bean:write name="pers" property="id"/>
	   </td>
	   --%>
	   <td class="textcontent">
	   	 <logic:notEmpty name="pers" property="lastName">
	        <bean:write name="pers" property="lastName"/>
	      </logic:notEmpty>
	      <logic:empty name="pers" property="lastName">
	      	      &nbsp;
	      </logic:empty>
	   </td>
	   <td class="textcontent">
	      <logic:notEmpty name="pers" property="middleName">
	        <bean:write name="pers" property="middleName"/>
	      </logic:notEmpty>
	      <logic:empty name="pers" property="middleName">
	      	      &nbsp;
	      </logic:empty>
	   </td>
	    <td class="textcontent">
	      <logic:notEmpty name="pers" property="firstName">
	        <bean:write name="pers" property="firstName"/>
	      </logic:notEmpty>
	      <logic:empty name="pers" property="firstName">
	      	      &nbsp;
	      </logic:empty>
	   </td>
     </tr>
	</logic:iterate>
</table>
