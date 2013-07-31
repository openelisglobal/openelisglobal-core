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
	      <bean:message key="receivercodeelement.id"/>
	   </th>
	   --%>
	   <th>
	   	  <bean:message key="receivercodeelement.messageOrganization"/>
	   </th>
	   <th>
	   	  <bean:message key="receivercodeelement.codeElementType"/>
	   </th>
	   <th>
	   	  <bean:message key="receivercodeelement.identifier"/>
	   </th>
	   <th>
	   	  <bean:message key="receivercodeelement.text"/>
	   </th>
	   <th>
	   	  <bean:message key="receivercodeelement.codesystem"/>
	   </th>
  
	</tr>
	<logic:iterate id="rce" indexId="ctr" name="<%=formName%>" property="menuList" type="us.mn.state.health.lims.receivercodeelement.valueholder.ReceiverCodeElement">
	<bean:define id="rceID" name="rce" property="id"/>
	  <tr>
	   <td class="textcontent">
	      <html:multibox name="<%=formName%>" property="selectedIDs">
	         <bean:write name="rceID" />
	      </html:multibox>
     
   	   </td>
	   <td class="textcontent">
	   	 <logic:notEmpty name="rce" property="messageOrganization">
	   	  <logic:notEmpty name="rce" property="messageOrganization.organization">
	        <bean:write name="rce" property="messageOrganization.organization.organizationName" />
	      </logic:notEmpty>
	     </logic:notEmpty>
	   </td>
	   <td class="textcontent">
	   	 <logic:notEmpty name="rce" property="codeElementType">
	        <bean:write name="rce" property="codeElementType.text" />
	     </logic:notEmpty>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="rce" property="identifier"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="rce" property="text"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="rce" property="codeSystem"/>
	   </td>
       </tr>
	</logic:iterate>
</table>
