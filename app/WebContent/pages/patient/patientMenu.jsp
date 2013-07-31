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
	      <bean:message key="patient.id"/>
	   </th>
	   --%>
	   <th>
	   	  <bean:message key="patient.personId"/>
	   </th>
	   <th>
	   	  <bean:message key="patient.birthDate"/>
	   </th>
	   <th>
	   	  <bean:message key="patient.gender"/>
	   </th>

	</tr>
	<logic:iterate id="patnt" indexId="ctr" name="<%=formName%>" property="menuList" type="us.mn.state.health.lims.patient.valueholder.Patient">
	<bean:define id="patntID" name="patnt" property="id"/>
	  <tr>
	   <td class="textcontent">
	      <html:multibox name="<%=formName%>" property="selectedIDs">
	         <bean:write name="patntID" />
	      </html:multibox>
     
   	   </td>
   	   <%--remove the following 09/12/2006 bugzilla 1399--%>
	   <%--
	   <td class="textcontent">
	      <bean:write name="patnt" property="id"/>
	   </td>
	   --%>
	   <td class="textcontent">
	   	  <bean:write name="patnt" property="person.firstName"/>
	   	  <% out.println(" "); %>
	   	  <bean:write name="patnt" property="person.lastName"/>
	   	  &nbsp;
	   </td>
	   <td class="textcontent">
	       <logic:notEmpty name="patnt" property="birthDateForDisplay">
	           <bean:write name="patnt" property="birthDateForDisplay"/>
	       </logic:notEmpty>
	       <logic:empty name="patnt" property="birthDateForDisplay">
   	        &nbsp;
	       </logic:empty>
	    </td>
	    <td class="textcontent">
	      <logic:notEmpty name="patnt" property="gender">
	        <bean:write name="patnt" property="gender"/>
	      </logic:notEmpty>
	      <logic:empty name="patnt" property="gender">
   	        &nbsp;
	      </logic:empty>
	     
	   </td>
     </tr>
	</logic:iterate>
</table>
