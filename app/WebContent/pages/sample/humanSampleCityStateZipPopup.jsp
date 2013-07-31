<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date, java.util.Locale,
	us.mn.state.health.lims.common.action.IActionConstants,
	us.mn.state.health.lims.common.util.SystemConfiguration" %>

<%--bugzilla 1765 new popup--%>
<%--bugzilla 1895 lightbox instead of popup--%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />


<table width="100%" align="center">
  <tr>
    <td width="75%" bgcolor="#CCCC99" align="center">
     <bean:message key="person.city"/>
    </td>
    <td width="5%" bgcolor="#CCCC99" align="center">
     <bean:message key="person.state"/>
    </td>
    <td width="12%" bgcolor="#CCCC99" align="center">
     <bean:message key="person.zipCode"/>
    </td> 
    <td width="8%">
     &nbsp;
    </td>
  </tr>
</table>
<div class='lbscrollvertical'>
<table width="100%" align="center">
<logic:notEmpty name="<%=formName%>" property="validCombos">
<logic:iterate id="csz" indexId="ctr" name="<%=formName%>" property="validCombos" type="us.mn.state.health.lims.citystatezip.valueholder.CityStateZip">
   <bean:define id="cszId" name="csz" property="id"/>
   <bean:define id="cityValue" name="csz" property="city"/>   
   <bean:define id="stateValue" name="csz" property="state"/>   
   <bean:define id="zipCodeValue" name="csz" property="zipCode"/>   
    <tr>
    <td width="80%">
     <bean:write property="city" name="csz" />
     <input type="hidden" property='<%="city" + cszId%>' id='<%="city" + cszId%>' value="<%=cityValue%>" />
    </td>
    <td width="5%">
     <bean:write property="state" name="csz" />
     <input type="hidden" property='<%="state" + cszId%>' id='<%="state" + cszId%>' value="<%=stateValue%>" />
    </td>
    <td width="10%">
     <bean:write property="zipCode" name="csz" />
     <input type="hidden" property='<%="zipCode" + cszId%>' id='<%="zipCode" + cszId%>' value="<%=zipCodeValue%>" />
    </td> 
    <td width="5%">
      <input type="radio" name="selectedCombo" id="selectedCombo" value="<%=cszId%>" />
    </td>
  </tr>
</logic:iterate>
</logic:notEmpty>
</table>
</div>


