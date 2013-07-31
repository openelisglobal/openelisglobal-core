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
	      <bean:message key="gender.id"/>
	   </th>
	   --%>
	   <th>
	   	  <bean:message key="gender.description"/>
	   </th>
	   <th>
	   	  <bean:message key="gender.genderType"/>
	   </th>
	   
	</tr>
	<logic:iterate id="gen" indexId="ctr" name="<%=formName%>" property="menuList" type="us.mn.state.health.lims.gender.valueholder.Gender">
	<bean:define id="genID" name="gen" property="id"/>
	  <tr>
	   <td class="textcontent">
	      <html:multibox name="<%=formName%>" property="selectedIDs">
	         <bean:write name="genID" />
	      </html:multibox>
     
   	   </td>
   	   <%--remove the following 09/12/2006 bugzilla 1399--%>
	   <%--
	   <td class="textcontent">
	      <bean:write name="gen" property="id"/>
	   </td>
	   --%>
	   <td class="textcontent">
	   	  <bean:write name="gen" property="description"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="gen" property="genderType"/>
	   </td>
       </tr>
	</logic:iterate>
</table>
