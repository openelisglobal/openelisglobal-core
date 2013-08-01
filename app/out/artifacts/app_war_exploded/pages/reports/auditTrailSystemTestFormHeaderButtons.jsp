<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants"
%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>
<%!

String allowEdits = "true";
String errorMessageDate = "";

%>
<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

//bugzilla 2393 /bugzilla 2437
java.util.Locale locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
errorMessageDate =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "error.sample.xml.by.sample.flu.begindate.lessthan.enddate");
%>
<script language="JavaScript1.2">

function pageOnLoad() {
    var su = document.getElementById("selectedSystemUserId");
    su.focus();
}

function validateForm(form) {
    return true;
}

function myCheckDate(date, event, dateCheck, onblur) 
{
	var messageDiv = date.name + 'Message';
	var mdiv = $(messageDiv);
	mdiv.className = "blank";
	var validDate = DateFormat(date,date.value,event,dateCheck,'1');
	if (dateCheck) 
	{ 
		if (validDate) 
		{
			var validDate2 = lessThanCurrent(date);
			if (validDate2) 
			{
				mdiv.className = "blank";
			}
			else
			{
				mdiv.className = "badmessage";
			}
		}
		else
		{
			mdiv.className = "badmessage";
		}
	}
}

function validateDateAndSubmit(button, event) {
  var date = document.getElementById('dateModified');
  if (date.value == null || date.value == '') {
   alert('<%=errorMessageDate%>');
  } else {
    submitThis();
  }
}

function submitThis() {
      setAction(window.document.forms[0], 'Process', 'no', '');
}

</script>
<%
	if(null != request.getAttribute(IActionConstants.FORM_NAME))
	{
%>
	      <h1>
				<logic:notEmpty
					name="<%=IActionConstants.PAGE_SUBTITLE_KEY%>">
					<bean:write name="<%=IActionConstants.PAGE_SUBTITLE_KEY%>" />
				</logic:notEmpty> 
				<logic:empty
					name="<%=IActionConstants.PAGE_SUBTITLE_KEY%>">
					<% if (request.getParameter("ID").equals("0")) { %>
					  <bean:message key="default.add.title" />
					<% } else { %>
					  <bean:message key="default.edit.title" />
					<%}%>
			   </logic:empty> 
		</h1>
<%
	}
%>
<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<table>
	<tr>
	 <td>
	   <table>
	   		<tr>
						<td class="label">
							<bean:message key="report.audit.trail.systemtest.user"/>:
						</td>	
						<td> 
					   	<html:select name="<%=formName%>" property="selectedSystemUserId">
					   	  <app:optionsCollection 
										name="<%=formName%>" 
							    		property="systemUsers" 
										label="nameForDisplay" 
										value="id"  
							        	filterProperty="isActive" 
							        	filterValue="N"
							 			allowEdits="true"
							/>
                        
					   </html:select>

						</td>
                        <td>&nbsp;&nbsp;</td>
        				<td class="label">
							<bean:message key="report.audit.trail.systemtest.refTable"/>:
						</td>	
						<td> 
					   	<html:select name="<%=formName%>" property="selectedReferenceTableId">
                          <%--bugzilla 2571 go through ReferenceTablesDAO to get reference tables info--%>
					   	  <app:optionsCollection 
										name="<%=formName%>"
							    		property="referenceTableList" 
										label="name" 
										value="id"  
							   			allowEdits="true"
							/>
                        
					   </html:select>

						</td>
    			        <td>&nbsp;&nbsp;</td>
    					<td class="label">
							<bean:message key="report.audit.trail.systemtest.date"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
	             		  <app:text name="<%=formName%>" 
						  property="dateModified" 
						  onkeyup="myCheckDate(this, event, false,false);" 
						  onblur="myCheckDate(this, event, true, true);" 
						  size="10" 
						  styleClass="text" 
						  styleId="dateModified"/>
			              <div id="dateModifiedMessage" class="blank">&nbsp;</div>
		                </td>
   			        <td>&nbsp;&nbsp;</td>
        <td>  
           <html:button onclick="submitThis();" property="display">
  			   <bean:message key="label.button.display"/>
  	       </html:button>
  	   	</td>
  	   </tr>
  	  </table>
  	 </td>
  	</tr>
  </table>
