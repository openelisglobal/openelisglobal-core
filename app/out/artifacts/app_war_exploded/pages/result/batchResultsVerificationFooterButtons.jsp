<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants"
%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<%--bugzilla 2551--%>
<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<%--AIS - bugzilla 1872 --%>
<%--bugzilla 1900 preview report--%>
<%--bugzilla 1510 added various element ids to fix FF errors--%>

<script language="JavaScript1.2">
      
//bugzilla 2053
function editThis(accNum) {
	var selectedTestId = document.getElementById('selectedTestId').value;
	var selectedTestSectionId = document.getElementById('selectedTestSectionId').value;
	var accessionNumber = document.getElementById('accessionNumber').value;
	//bugzilla 1774
	//alert("selectedTestIId " + selectedTestId);
	setAction(window.document.forms[0], 
	'ResultsEntryFrom', 'no', '?accessionNumber=' + accessionNumber + 
	'&testId=' + selectedTestId +'&testSectionId=' + selectedTestSectionId +'&accNum=' + accNum +
	'&ID=');
}

function validateForm(form) {
	//alert("am here in validateForm");
    //return validateBatchResultsVerificationForm(form);
    return true;
}


function checkSave(){
	//alert("am here in check");	
	var selectedRows = window.document.forms[0].elements['selectedRows'];
	
	var flag ="false";
	if (selectedRows != null){
        //If only one checkbox
        if (selectedRows[0] == null) {             
             if( selectedRows.checked){
             	flag="true";
             } 
        } else {	      
	         for (var i = 0; i < selectedRows.length; i++){		           		            
	            if(selectedRows[i].checked){	            
	             	flag="true";
	            }	            
	         }
        }
	 }	      
	if (flag =="true"){	
	  	if(checkClicked()) {
	 		return false;
	    }else{		
			 setAction(window.document.forms[0], 'Update', 'yes', '?ID=');	  
	 	}
	}
}
</script>

<%--bugzilla 1664--%>
<div id="spaceButtons" class="spacebuttons">&nbsp;</div>
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
  	       <td width="5%">&nbsp;</td>
           <%--bugzilla 2551--%>
           <logic:notEmpty name="<%=formName%>" property="sample_TestAnalytes">
  	       <td>
   	         <html:button  onclick="previewReport('');return false;" property="preview" styleId="preview" disabled="<%=Boolean.valueOf(allowEdits).booleanValue()%>">
  			   <bean:message key="label.button.preview.results.report"/>
  			 </html:button>
           </td>
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
	       <td width="20%">&nbsp;</td>
  	       <td>
<%
    String saveDisabled = (String)request.getAttribute(IActionConstants.SAVE_DISABLED);
%> 
  			<html:button onclick="checkSave();" property="save" styleId="save" disabled="<%=Boolean.valueOf(saveDisabled).booleanValue()%>">
  			     <bean:message key="label.button.save"/>
	        </html:button>
  	       </td>
	       </logic:notEmpty>
          <%--bugzilla 2551--%>
          <logic:empty name="<%=formName%>" property="sample_TestAnalytes">
           <td>&nbsp;
           </td>
           <td>&nbsp;
           </td>
           <td>&nbsp;
           </td>
           <td>&nbsp;
           </td>
	       <td width="20%">&nbsp;</td>
  	       <td>&nbsp;
  	       </td>
          </logic:empty>
	       <td>&nbsp;</td>
		   <td>
  			<html:button onclick="setAction(window.document.forms[0], 'Cancel', 'no', '');"  property="cancel" styleId="cancel" >
  			   <bean:message key="label.button.exit"/>
  			</html:button>
	       </td>
  	       <td width="40%">&nbsp;</td>
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
