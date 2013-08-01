<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants"
%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<%--  bugzilla 2503  --%>
<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />

<%
      //bugzilla 2503 add search action
      String noFilter="true";
      if (request.getAttribute(IActionConstants.POPUPFORM_FILTER_BY_TABLE_COLUMN) != null) {
          noFilter = "false";
           
       }
       
       String filterStr="";
       if (request.getAttribute(IActionConstants.POPUP_FORM_SEARCH_STRING) != null ) {
          { 
             filterStr = (String) request.getAttribute(IActionConstants.POPUP_FORM_SEARCH_STRING);
            
           } 
       } 
       
       String filterColumn="";
       if (request.getAttribute(IActionConstants.POPUPFORM_FILTER_BY_TABLE_COLUMN) != null )  {
          {
             filterColumn = (String) request.getAttribute(IActionConstants.POPUPFORM_FILTER_BY_TABLE_COLUMN);
          }
       }
     // end of bugzilla 2503  
%>

<script language="JavaScript1.2">
    
//bugzilla 2503
//to avoid duplicate submit of form on enter
function submitSearchForEnter(e){
    if (enterKeyPressed(e)) {
       var button = document.getElementById("filterButton");
       e.returnValue=false;
       e.cancel = true;
       button.click();
    } 
}

function submitSearchForClick(button){
   
     setAction( window.document.forms[0], 'Search', 'no', '?search=Y'); 
}

function submitNoSearchForClick(button) {
    setAction( window.document.forms[0], '', 'no', 'showall');
}

</script>


<%-- bugzilla 2503 <center>  --%>
<table border="0" cellpadding="0" cellspacing="0">
<tbody valign="left">
<%-- bugzilla 2503 	<tbody valign="middle">  --%>
       
   <logic:notEmpty name="<%=IActionConstants.POPUPFORM_FILTER_BY_TABLE_COLUMN%>"> 
         <tr>
          <td width="20%">&nbsp;</td>
         
          <td width="30%">
             <bean:message key="label.form.filterby"/>
             <bean:message key="<%=filterColumn%>"/>
          </td>
          
          <td width="25%">
             &nbsp
          </td>
          <td width="25%">
             &nbsp
          </td>   
 	   </tr>
      
   </logic:notEmpty> 
       
   <tr>
	   <% 	
		   String allowEdits = "true";
		   if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
    	  	allowEdits = (String)request.getAttribute(us.mn.state.health.lims.common.action.IActionConstants.ALLOW_EDITS_KEY);
    	   }
       %>
             
      <%-- bugzilla 2503 --%>
       <logic:notEmpty name="<%=IActionConstants.POPUPFORM_FILTER_BY_TABLE_COLUMN%>">
  	      
  	        <td width ="20%">
  	           &nbsp
  	        </td>
  	      
 	        <td width="30%">
               <html:text name="<%=formName%>" property = "filterString" onkeypress="submitSearchForEnter(event);" size = "20" maxlength= "20" value="<%=filterStr%>" disabled="<%=Boolean.valueOf(noFilter).booleanValue()%>" />
            </td>
            <td width="25%">
               <html:button property="apply" styleId="filterButton" onclick="submitSearchForClick(this);return false;" disabled="<%=Boolean.valueOf(noFilter).booleanValue()%>">
  	           <bean:message key="label.button.apply"/>
  		       </html:button>
  		       <html:button property="showall" styleId="showAllButton" onclick="submitNoSearchForClick(this);return false;" disabled="<%=Boolean.valueOf(noFilter).booleanValue()%>">
  		       <bean:message key="label.button.showall"/>
  		       </html:button>
  		    </td>  
	   
         </logic:notEmpty>  
           
      
	     <td width="25%" align="right">
	        <app:button property="save" onclick="saveItToParentForm(document.forms[0]);" >	
	      	       	   <bean:message key="label.button.save"/>
	      	</app:button>
    
       
  			<app:button property="cancel" onclick="cancelToParentForm();" >
  						<%--AIS - bugzilla 1860--%>
	      	       	   <bean:message key="label.button.exit"/>
	      	</app:button>
         </td>
   </tr>      
      
 
</tbody>
</table>
<%--   bugzilla 2503 </center> --%>

<%-- bugzilla 2503 --%>
<script language="JavaScript1.2">
   var textName = document.getElementById ("filterString");
   if (textName != null && textName.value != null) 
   {  textName.focus();
      textName.value+='';
   }
</script>