<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants"
%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<div class="lbfooter">
<center>
<table border="0" cellpadding="0" cellspacing="0">
	<tbody valign="middle">
		<tr>
		   <% 	
		    String allowEdits = "true";
		    if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
    		  	allowEdits = (String)request.getAttribute(us.mn.state.health.lims.common.action.IActionConstants.ALLOW_EDITS_KEY);
    		}
           %>
	     <td>
	      	<html:button styleClass="lbSave" property="save" onclick="saveItToLightBoxParentForm();" styleId="save" disabled="<%=Boolean.valueOf(allowEdits).booleanValue()%>">	
                <bean:message key="label.button.save"/>
	      	</html:button>    
    	 </td>
        
		<td>&nbsp;</td>
		<td>
          <a href="" class="lbAction" rel="deactivate">
  			<html:button  property="cancel" styleId="cancel">
  			   <bean:message key="label.button.cancel"/>
  			</html:button>
          </a>
        </td> 
	    </tr>
	 </tbody>
</table>
</center>
</div>