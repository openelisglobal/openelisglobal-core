<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<%!

String allowEdits = "true";
//bugzilla 1494
String errorMessageNumericDictValue = "";

%>

<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}
//bugzilla 1494
java.util.Locale locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
errorMessageNumericDictValue =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "testresult.error.dictionary.numericvalue");
%>

<script language="JavaScript1.2">
 
function validateForm(form) {
    var validated = validateResultForm(form);
    if (validated) {
       //check if value is numeric (foreign key to dictionary) if type id D 
       var type = document.getElementById("resultType");
       var val = document.getElementById("value");
       if (type.value == 'D' && !IsNumeric(val.value)) {
         //bugzilla 1494
         alert('<%=errorMessageNumericDictValue%>');
          validated = false;
       }
    } 
    return validated;
} 
 

</script>

<table>
		<tr>
						<td class="label">
							<bean:message key="result.id"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="id" allowEdits="false"/>
						</td>
		</tr>
	    <tr>
						<td class="label">
							<bean:message key="result.analyte"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
					   	 			   	    
						<html:select name="<%=formName%>" property="analyteId">
					   	  <app:optionsCollection 
										name="<%=formName%>" 
							    		property="analytes" 
										label="analyteName" 
										value="id"  
							        	filterProperty="isActive" 
							        	filterValue="N"
							 			allowEdits="true"
							/>
                        
					   </html:select>
						</td>
		</tr>
	    <tr>
						<td class="label">
							<bean:message key="result.analysis"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
					   	 			   	    
						<html:select name="<%=formName%>" property="analysisId">
					   	  <app:optionsCollection 
										name="<%=formName%>" 
							    		property="analyses" 
										label="id" 
										value="id"  
					    	 			allowEdits="true"
							/>
                        
					   </html:select>
						</td>
		</tr>
	    <tr>
						<td class="label">
							<bean:message key="result.testResult"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
					   	 			   	    
						<html:select name="<%=formName%>" property="testResultId">
					   	  <app:optionsCollection 
										name="<%=formName%>" 
							    		property="testResults" 
										label="id" 
										value="id"  
							 			allowEdits="true"
							/>
                        
					   </html:select>
						</td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="result.value"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="value" />
						</td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="result.resultType"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="resultType" />
						</td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="result.sortOrder"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="sortOrder" />
						</td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="result.isReportable"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="isReportable" size="1" onblur="this.value=this.value.toUpperCase()"/>
						</td>
		</tr>
 		<tr>
		<td>&nbsp;</td>
		</tr>
</table>


<html:javascript formName="resultForm"/>

