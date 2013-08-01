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
	      <bean:message key="provider.id"/>
	   </th>
	   --%>
	   <th>
	   	  <bean:message key="provider.personId"/>
	   </th>
	   <th>
	   	  <bean:message key="provider.npi"/>
	   </th>
	   <th>
	   	  <bean:message key="provider.externalId"/>
	   </th>
	   	   <th>
	   	  <bean:message key="provider.providerType"/>
	   </th>

	</tr>
	<logic:iterate id="prov" indexId="ctr" name="<%=formName%>" property="menuList" type="us.mn.state.health.lims.provider.valueholder.Provider">
	<bean:define id="provID" name="prov" property="id"/>
	  <tr>
	   <td class="textcontent">
	      <html:multibox name="<%=formName%>" property="selectedIDs">
	         <bean:write name="provID" />
	      </html:multibox>
     
   	   </td>
   	   <%--remove the following 09/12/2006 bugzilla 1399--%>
	   <%--
	   <td class="textcontent">
	      <bean:write name="prov" property="id"/>
	   </td>
	   --%>
	   <td class="textcontent">
	   	<logic:notEmpty name="prov" property="person.firstName">
	   	  <bean:write name="prov" property="person.firstName"/>
	   	  <% out.println(" "); %>
	   	</logic:notEmpty>
	   	<logic:notEmpty name="prov" property="person.lastName">
	   	  <bean:write name="prov" property="person.lastName"/>
	   	</logic:notEmpty>
	   	&nbsp;
	   </td>
	   <td class="textcontent">
	      <logic:notEmpty name="prov" property="npi">
	        <bean:write name="prov" property="npi"/>
	      </logic:notEmpty>
	      &nbsp;
	   </td>
	    <td class="textcontent">
	      <logic:notEmpty name="prov" property="externalId">
	        <bean:write name="prov" property="externalId"/>
	      </logic:notEmpty>
	      &nbsp;
	   </td>
	   <td class="textcontent">
	      <logic:notEmpty name="prov" property="providerType">
	        <bean:write name="prov" property="providerType"/>
	      </logic:notEmpty>
	      &nbsp;
	   </td>
     </tr>
	</logic:iterate>
</table>
