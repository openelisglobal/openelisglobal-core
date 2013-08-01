<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants"
%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%--bugzilla 2554--%>
<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<%--bugzilla 1664--%>
<div id="spaceButtons" class="spacebuttons">&nbsp;</div>
<%--bugzilla 2554--%>
<center>
<table border="0" cellpadding="0" cellspacing="0">
<%--bugzilla 2554--%>
	<tbody valign="middle">
		<tr>
		   <% 	
		    String allowEdits = "true";
		    if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
		      		  	allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
    		}
    		
           %>
        <%--bugzilla 2554--%>
        <logic:notEmpty name="<%=formName%>" property="sample_TestAnalytes">
   	    <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
        <%--bugzilla 2467--%>
   	    <td>
  			<html:button  onclick="changeAllCheckBoxStates(true);" property="check" styleId="check" disabled="<%=Boolean.valueOf(allowEdits).booleanValue()%>">
  			   <bean:message key="label.button.checkAll"/>
  			</html:button>
	    </td>
	    <td>&nbsp;</td>
  	    <td>
  			<html:button  onclick="changeAllCheckBoxStates(false)" property="uncheck" styleId="uncheck" disabled="<%=Boolean.valueOf(allowEdits).booleanValue()%>">
  			   <bean:message key="label.button.uncheckAll"/>
  			</html:button>
	    </td>
   	    <td>&nbsp;</td>
	    <%--td>
  			<app:button  onclick="changeAllCheckBoxStates(true);" property="check" allowEdits="<%=allowEdits%>">
  			   <bean:message key="label.button.checkAll"/>
  			</app:button>
	    </td--%>
   	    <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	    <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
  	    <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	   	<td>
  			<html:button onclick="if(checkClicked()) 
							 {
							 	return false;
							 }
							 else {
                              //bugzilla 2254
							  saveThis(window.document.forms[0]);
							  
							 }" property="save" disabled="<%=Boolean.valueOf(allowEdits).booleanValue()%>">
  			   <bean:message key="label.button.save"/>
  			</html:button>
	    </td>
  		<td>&nbsp;</td>
	   	<td>
 			<html:button  onclick="editMultiples(window.document.forms[0]);"
	                 			 property="save" disabled="<%=Boolean.valueOf(allowEdits).booleanValue()%>">
  			   <bean:message key="label.button.editMultiples"/>
  			</html:button>
	    </td>
  		<td>&nbsp;</td>
        <td>
  			<html:button  onclick="setAction(window.document.forms[0], 'Cancel', 'no', '');" property="cancel" >
  			   <bean:message key="label.button.exit"/>
  			</html:button>
	    </td>
<%--bugzilla 2554--%>
        </logic:notEmpty>
        <logic:empty name="<%=formName%>" property="sample_TestAnalytes">
   	    <td width="47%">&nbsp;</td>
        <td width="6%">
  			<html:button  onclick="setAction(window.document.forms[0], 'Cancel', 'no', '');" property="cancel" >
  			   <bean:message key="label.button.exit"/>
  			</html:button>
	    </td>
   	    <td width="47%">&nbsp;</td>
        </logic:empty>
	    </tr>
	 </tbody>
</table>
</center>
<script>
  //bugzilla 1664
  var height = 48; //this is tested with highest resolution (width=1280, height=1024)
  //get the div that creates a space between scrolling area and footer buttons
  var space = document.getElementById("spaceButtons");

  //adjust height according to resolution so buttons aren't covered by the scrolling area
  if (screen.height != getCorrectHeight()) {
     height = getCorrectHeight()/screen.height * height;
  }

  space.style.height = height;
</script>