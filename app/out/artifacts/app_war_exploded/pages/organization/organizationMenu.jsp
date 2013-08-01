<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	java.util.Hashtable,
	us.mn.state.health.lims.organization.valueholder.Organization,
	us.mn.state.health.lims.common.action.IActionConstants,
	us.mn.state.health.lims.common.formfields.FormFields,
	us.mn.state.health.lims.common.util.StringUtil" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<%!
	boolean useOrgLocalAbbrev = true;
	boolean useOrgState = true;
	boolean useZipCode = true;
	boolean useMLS = true;
 %>

 <%
 	useOrgLocalAbbrev = FormFields.getInstance().useField(FormFields.Field.OrgLocalAbrev);
 	useOrgState = FormFields.getInstance().useField(FormFields.Field.OrgState);
 	useZipCode = FormFields.getInstance().useField(FormFields.Field.ZipCode);
 	useMLS = FormFields.getInstance().useField(FormFields.Field.MLS);
  %>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />


<table width="100%" border=2">
	<tr>
	   <th>
	     <bean:message key="label.form.select"/>
	   </th>
		<% if( useOrgLocalAbbrev) { %>
	   <th><%--bugzilla 2069 added--%>
	   	  <bean:message key="organization.localAbbreviation"/>
	   </th>
	   <% } %>
	   <th><%--bugzilla 1412 rearrange--%>
	   	  <bean:message key="organization.organizationName"/>
	   </th>
	   <th>
	   	  <bean:message key="organization.parent"/>
	   </th>
	   <th>
	   	  <%= StringUtil.getContextualMessageForKey("organization.short") %>
	   </th>
	   <th>
	      <bean:message key="organization.isActive"/>
	   </th>
	   <th>
	   	  <bean:message key="organization.streetAddress"/>
	   </th>
	   <th>
	   	  <bean:message key="organization.city"/>
	   </th>
	   <% if( useOrgState){ %>
	   <th>
	   	  <bean:message key="organization.state"/>
	   </th>
	   <% } %>
	   <% if( useZipCode ){ %>
	    <th>
	   	  <bean:message key="organization.zipCode"/>
	   </th>
	   <% } %>
	   <th>
	   	  <bean:message key="organization.clia.number"/>
	   </th>
	   <% if( useMLS ){ %>
	   <th>
	   	  <bean:message key="organization.mls.lab"/>
	   </th>
	   <% } %>

	</tr>
	<logic:iterate id="org" indexId="ctr" name="<%=formName%>" property="menuList" type="us.mn.state.health.lims.organization.valueholder.Organization">
	<bean:define id="orgID" name="org" property="id"/>
	<logic:notEmpty name="org" property="organization">
	 <bean:define id="parentOrgID" name="org" property="organization.id"/>
	</logic:notEmpty>

	  <tr>
	   <td class="textcontent">
	      <html:multibox name="<%=formName%>" property="selectedIDs" onclick="output()" >
	         <bean:write name="orgID" />
	      </html:multibox>

   	   </td>
		<% if( useOrgLocalAbbrev ){ %>
	   <td class="textcontent">
	   	  <bean:write name="org" property="organizationLocalAbbreviation"/>
	   </td>
	   <% } %>
	   <td class="textcontent">
	   	  <bean:write name="org" property="organizationName"/>
	   </td>
	   <td class="textcontent">
	    <logic:notEmpty name="org" property="organization">
	        <bean:write name="org" property="organization.organizationName"/>
	    </logic:notEmpty>
	      &nbsp;
	   </td>
	   <td class="textcontent">
	      <logic:notEmpty name="org" property="shortName">
	   	  <bean:write name="org" property="shortName"/>
	   	  </logic:notEmpty>
	   	  <logic:empty name="org" property="shortName">
	   	   &nbsp;
	   	  </logic:empty>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="org" property="isActive"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="org" property="streetAddress"/>
	   	  &nbsp;
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="org" property="city"/>
	   	  &nbsp;
	   </td>
	   <% if( useOrgState ){ %>
	  <td class="textcontent">
	   	  <bean:write name="org" property="state"/>
	   	  &nbsp;
	   </td>
	   <% } %>
	   <% if( useZipCode ){ %>
	   <td class="textcontent">
	   	  <bean:write name="org" property="zipCode"/>
	   	  &nbsp;
	   </td>
	   <% } %>
	   <td class="textcontent">
	   	  <bean:write name="org" property="cliaNum"/>
	   	  &nbsp;
	   </td>
	   <% if( useMLS ){ %>
	   <td class="textcontent">
	     <bean:write name="org" property="mlsLabFlag"/>
	   	  &nbsp;
	    </td>
	   <% } %>
     </tr>
	</logic:iterate>
</table>
