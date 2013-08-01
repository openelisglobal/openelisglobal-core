<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants" %>

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
	 
	   <th>
	   	  <bean:message key="referencetables.tableName"/>
	   </th>
	   
	   <th>
	   	  <bean:message key="referencetables.keepHistory"/>
	   </th>
	   
	   <th>
	      <bean:message key ="referencetables.Hl7Encoded"/>
	   </th>
		   
	</tr>
	<logic:iterate id="refTbl" indexId="ctr" name="<%=formName%>" property="menuList" type="us.mn.state.health.lims.referencetables.valueholder.ReferenceTables">
	<bean:define id="referenceTablesID" name="refTbl" property="id"/>
	  <tr>
	   <td class="textcontent">
	      <html:multibox name="<%=formName%>" property="selectedIDs">
	         <bean:write name="referenceTablesID" />
	      </html:multibox>
   	   </td>
   
	   <td class="textcontent">
	   	  <bean:write name="refTbl" property="tableName"/>
	   </td>
	   
	   <td class="textcontent">             
	   	  <bean:write name="refTbl" property="keepHistory"/>
	   </td>
	   
	   <td class="textcontent">
	      <bean:write name="refTbl" property="isHl7Encoded"/>
	    </td>
       </tr>
	</logic:iterate>
</table>
