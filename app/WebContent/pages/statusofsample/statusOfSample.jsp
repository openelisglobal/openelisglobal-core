<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />

<%!

String allowEdits = "true";

%>

<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

%>

<script type="text/javascript" language="JavaScript1.2">
function validateForm(form) {
    return validateStatusOfSampleForm(form);
}

function numbersOnly(evt) { 	 	
 	 	
 	var messageDiv = "blank";
 	messageDiv = 'codeMessage';
 	var mdiv = document.getElementById(messageDiv); 
 	mdiv.className = "blank";
 	document.forms[0].save.disabled = false;
 	
 	var field = "code";
 	var validity = "valid";
 	var codeMsg = "Enter numbers only please!";
 	var retVal = true;
 	evt = (evt) ? evt : event;
 	var charCode = (evt.charCode) ? evt.charCode : 
 		((evt.keyCode) ? evt.keyCode : ((evt.which) ? evt.which : 0));
 	
 	if(charCode >31 && (charCode <48 || charCode >57)) { 		
 		mdiv.className = "badmessage";
 		document.forms[0].save.disabled = true;
 		//alert("Enter numbers only!");
 		retVal = false;
 		return retVal;
 	}
 	
 	return retVal;
}


function validateStatusType() {
		
	var field = document.getElementById("statusType");	
	//alert("field value: " + field.value);
	var messageDiv = "statusTypeMessage"; 	
 	var mdiv = document.getElementById(messageDiv); 
 	mdiv.className = "blank";
 	//alert("mdiv.className: " + mdiv.className);
 	 	
 	document.forms[0].save.disabled = false; 
 	if(field.value !=null){ 	
 		if(field.value.toLowerCase() != "sample"){
 			if(field.value.toLowerCase() != "analysis") {
	
				mdiv.className = "badmessage";
 				document.forms[0].save.disabled = true;
 				//alert("Status Type needs to be \"Sample\" or \"Analysis\"!");		
			}
		}
 	}	 	 				
		
}



</script>

<table id="statusOfSample" cellspacing="1" cellpadding="1" border="0">
		<tr vertical="center">
			<td class="label">
				<bean:message key="statusofsample.id"/>:
			</td>	
			<td align="top"> 
				<app:text name="<%=formName%>" property="id" allowEdits="false"/>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr vertical="center">
			<td class="label">
				<bean:message key="statusofsample.name"/>:<span class="requiredlabel">*</span>
			</td>	
			<td> 
 				<html:text name="<%=formName%>" property="statusOfSampleName" size="30" maxlength="30"/>
			</td>
		</tr>
		<tr vertical="center">
			<td class="label">
				<bean:message key="statusofsample.description"/>:<span class="requiredlabel">*</span>
			</td>	
			<td> 
    			<%--bugzilla 1393 increase size to 40--%>
				<html:textarea name="<%=formName%>" property="description" cols="50" rows="4"/>
			</td>
		</tr>
 		<tr vertical="center">
			<td class="label">
				<bean:message key="statusofsample.code"/>:<span class="requiredlabel">*</span>				
			</td>	
			<td>     			
				<html:text name="<%=formName%>" property="code" size="3" maxlength="3" />				
			</td>						
		</tr>
		<tr vertical="center">																
			<td class="label">
				<bean:message key="statusofsample.statustype"/>:<span class="requiredlabel">*</span>
			</td>
			<td>     						
				<%-- html:text name="<%=formName%>" property="statusType" size="10" maxlength="10" onblur="validateStatusType();" />
				<div id="statusTypeMessage" class="blank">&nbsp;</div> --%>
				<%--bgm bugzilla 1566 aligned fields and added select options --%>
				<html:select name="<%=formName%>" property="statusType">
					<html:option value=""> </html:option>
					<html:option value="SAMPLE">SAMPLE</html:option>
					<html:option value="ANALYSIS">ANALYSIS</html:option>
				</html:select>
			</td>
						
		</tr>
		
 		<tr>
			<td>&nbsp;</td>
		</tr>
</table>

<html:javascript formName="statusOfSampleForm"/>

