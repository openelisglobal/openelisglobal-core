<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants,
			us.mn.state.health.lims.siteinformation.valueholder.SiteInformation" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />

<table width="80%" border="2">
	<tr>
		<th>&nbsp;</th>
	   	<th><bean:message key="generic.name" /></th>
	   	<th><bean:message key="generic.description"/></th>
	   	<th><bean:message key="generic.value"/></th>
	</tr>
	<logic:iterate id="site" name="<%=formName%>" indexId="ctr" property="menuList" type="SiteInformation">
		<bean:define id="siteId" name="site" property="id"/>
	  	<tr>
	   		<td class="textcontent">
	      		<html:multibox name="<%=formName%>" property="selectedIDs" onclick="output()">
	         		<bean:write name="siteId" />
	      		</html:multibox>
   	   		</td>
   	   		<td class="textcontent">
	    		<bean:write name="site" property="name"/>
	   		</td>
   	  	 	<td class="textcontent">
	   	  		<bean:write name="site" property="description"/>
	   		</td>
	   		<% if( site.getValueType().equals("logoUpload")){ %>
	   		<td class="textcontent">
	   		    <img src="./images/labLogo.jpg?ver=<%= Math.random() %>" 
	   		         height="42" 
	   		         width="42"  />
	   		</td>
	   		<% }else{ %>
	   		<td class="textcontent">
	   	  		<bean:write name="site" property="value"/>
	   		</td>
	   		<% } %>
     	</tr>
	</logic:iterate>
</table>
