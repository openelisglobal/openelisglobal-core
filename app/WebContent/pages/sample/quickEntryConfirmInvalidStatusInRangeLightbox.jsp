<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants, 
	us.mn.state.health.lims.common.util.SystemConfiguration" %>   

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<!--bugzilla 1895-->
<div id="lbContent">
<form id='quickEntryConfirmExitLightboxForm'>
<table width="100%" cellpadding="0" cellspacing="0">
		<tr>
			<td class="popuplistheader"><bean:message key="quickentry.confirmExitPopup.title"/></td>
		</tr>
		<tr>
			<td class="popuplistdata">
			  <table width="100%" align="center">
                  <tr>
                   <td width="100%" align="center">
                         <bean:message key="quickentry.samplesinrange.badstatus"/>
                   </td>
                  </tr>
             </table>
            </td>
		</tr>
		<tr>
		 <td>
		  <div class="lbfooter">
           <center>
				<table border="0" cellpadding="0" cellspacing="0">
					<tbody valign="middle">
						<tr>
					     <td>
					      	<html:button styleClass="lbSave" property="save" onclick="saveItToLightBoxParentForm(document.forms[0]);" styleId="save">	
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
	  </td>
	</tr>
  </table>
 </form>
</div>