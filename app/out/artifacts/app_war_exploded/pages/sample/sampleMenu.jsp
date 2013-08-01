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
	      <bean:message key="sample.id"/>
	   </th>
	   --%>
	   <th>
	   	  <bean:message key="sample.accessionNumber"/>
	   </th>
	   <th>
	   	  <bean:message key="sample.enteredDate"/>
	   </th>
	   <th>
	   	  <bean:message key="sample.referredCultureFlag"/>
	   </th>
	   <th>
	      <bean:message key="sample.sysUserId"/>
	   </th>

	</tr>
	<logic:iterate id="smpl" indexId="ctr" name="<%=formName%>" property="menuList" type="us.mn.state.health.lims.sample.valueholder.Sample">
	<bean:define id="smplID" name="smpl" property="id"/>
	  <tr>
	   <td class="textcontent">
	      <html:multibox name="<%=formName%>" property="selectedIDs">
	         <bean:write name="smplID" />
	      </html:multibox>
     
   	   </td>
   	   <%--remove the following 09/12/2006 bugzilla 1399--%>
	   <%--
	   <td class="textcontent">
	      <bean:write name="smpl" property="id"/>
	   </td>
	   --%>
	   <td class="textcontent">
	   	  <bean:write name="smpl" property="accessionNumber"/>
	   </td>
	   <td class="textcontent">
	      <logic:notEmpty name="smpl" property="enteredDateForDisplay">
	        <bean:write name="smpl" property="enteredDateForDisplay"/>
	      </logic:notEmpty>
	      &nbsp;
	   </td>
	    <td class="textcontent">
	      <logic:notEmpty name="smpl" property="referredCultureFlag">
	        <bean:write name="smpl" property="referredCultureFlag"/>
	      </logic:notEmpty>
	      &nbsp;
	   </td>
	   <td class="textcontent">
	      <logic:notEmpty name="smpl" property="systemUser">
	        <bean:write name="smpl" property="systemUser.nameForDisplay"/>
	      </logic:notEmpty>
	      &nbsp;
	   </td>
     </tr>
	</logic:iterate>
</table>
