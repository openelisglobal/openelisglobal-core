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
	   <%--bugzilla 2438--%>
	   <th>
	      <bean:message key="project.localAbbreviation"/>
	   </th>
	   <th>
	   	  <bean:message key="project.projectName"/>
	   </th>
	   <th>
	   	  <bean:message key="project.desc.short"/>
	   </th>
	   <th>
	   	  <bean:message key="project.startedDate"/>
	   </th>
	   <th>
	      <bean:message key="project.owner"/>
	   </th>
	   <th>
	   	  <bean:message key="project.external.reference"/>
	   </th>
	   <th>
	   	  <bean:message key="project.program.programName"/>
	   </th>
	   <th>
	   	  <bean:message key="project.isActive"/>
	   </th>
	   
	</tr>
	<logic:iterate id="proj" indexId="ctr" name="<%=formName%>" property="menuList" type="us.mn.state.health.lims.project.valueholder.Project">
	<bean:define id="projID" name="proj" property="id"/>
	  <tr>
	   <td class="textcontent">
	      <html:multibox name="<%=formName%>" property="selectedIDs">
	         <bean:write name="projID" />
	      </html:multibox>
     
   	   </td>
   	   <%--bugzilla 2438--%>
	   <td class="textcontent">
	      <bean:write name="proj" property="localAbbreviation"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="proj" property="projectName"/>
	   </td>
	   <td class="textcontent">
	      <logic:notEmpty name="proj" property="description">
	   	  <bean:write name="proj" property="description"/>
	   	  </logic:notEmpty>
	   	  <logic:empty name="proj" property="description">
	   	   &nbsp;
	   	  </logic:empty>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="proj" property="startedDateForDisplay"/>
	   </td>
	   <td class="textcontent">
	      <%--bean:write name="proj" property="sysUserId"/--%>
	      <%--bean:write name="proj" property="sysUserName"/--%>
	      <logic:notEmpty name="proj" property="systemUser">
	        <bean:write name="proj" property="systemUser.nameForDisplay"/>
	      </logic:notEmpty>
	      &nbsp;
	   </td>
	   <td class="textcontent">
	    <logic:notEmpty name="proj" property="referenceTo">
	   	  <bean:write name="proj" property="referenceTo"/>
	   	</logic:notEmpty>
	   	<logic:empty name="proj" property="referenceTo">
	   	  &nbsp;
	   	</logic:empty>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="proj" property="programCode"/>
	   	  &nbsp;
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="proj" property="isActive"/>
	   </td>
	     </tr>
	</logic:iterate>
</table>
