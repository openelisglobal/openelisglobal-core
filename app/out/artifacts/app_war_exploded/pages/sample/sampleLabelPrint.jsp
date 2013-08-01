<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />

<%
    String okDisabled = (String)request.getAttribute(IActionConstants.SAVE_DISABLED);
%>

<script language="JavaScript1.2">
function validateForm(form) {
    return validateSampleLabelPrintForm(form);
}


</script>

<table>
		<tr>
						<td class="label">
							<bean:message key="sample.label.print.count"/>:
						</td>	
						<td> 
					        <html:text name="<%=formName%>" property="numberOfSamples" onkeypress="return noenter()" disabled="<%=Boolean.valueOf(okDisabled).booleanValue()%>"/>
						</td>
		</tr>
        <%--bugzilla 2167: added message about accession numbers for which labels have printed --%>
	    <logic:notEmpty name="<%=formName%>" property="accessionNumbersPrinted">
	     <bean:define id="accessionNumbers" name="<%=formName%>" property="accessionNumbersPrinted" type="java.lang.String"/>
		  <tr>
	
						<td> 
					        <bean:message key="sample.label.print.accessionnumbers" arg0="<%=accessionNumbers%>"/>
						</td>
    	  </tr>
 		</logic:notEmpty>
	    <logic:notEmpty name="<%=formName%>" property="accessionNumberPrinted">
	     <bean:define id="accessionNumber" name="<%=formName%>" property="accessionNumberPrinted" type="java.lang.String"/>
		  <tr>
	
						<td> 
					        <bean:message key="sample.label.print.accessionnumber" arg0="<%=accessionNumber%>"/>
						</td>
    	  </tr>
 		</logic:notEmpty>
        <%--bugzilla 2380--%>
        <logic:notEmpty name="<%=formName%>" property="accessionNumbersGenerated">
	     <bean:define id="accessionNumbersNoPrint" name="<%=formName%>" property="accessionNumbersGenerated" type="java.lang.String"/>
		  <tr>
	
						<td> 
					        <bean:message key="sample.label.noprint.accessionnumbers" arg0="<%=accessionNumbersNoPrint%>"/>
						</td>
    	  </tr>
 		</logic:notEmpty>
	    <logic:notEmpty name="<%=formName%>" property="accessionNumberGenerated">
	     <bean:define id="accessionNumberNoPrint" name="<%=formName%>" property="accessionNumberGenerated" type="java.lang.String"/>
		  <tr>
	
						<td> 
					        <bean:message key="sample.label.noprint.accessionnumber" arg0="<%=accessionNumberNoPrint%>"/>
						</td>
    	  </tr>
 		</logic:notEmpty>
</table>

<html:javascript formName="sampleLabelPrintForm"/>

