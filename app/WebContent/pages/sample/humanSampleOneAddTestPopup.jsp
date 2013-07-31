<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants,
	us.mn.state.health.lims.common.util.SystemConfiguration" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="idSeparator" value='<%= SystemConfiguration.getInstance().getDefaultIdSeparator() %>' />

<%!

String allowEdits = "true";

%>

<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

%>


<script language="JavaScript1.2">
function customOnLoad() {
   
   if (window.opener.getSelectedTestIds() && window.opener.getSelectedTestIds() != '') {
      //then load both lists from parentform
      var idObj = window.opener.getSelectedTestIds();
      var listOfIds = idObj.value;
       
      var slIdArr = new Array();
      //trim leading ;
      if (listOfIds.indexOf('<%=idSeparator%>') == 0) {
        listOfIds = listOfIds.substring(1);
      }
      
      slIdArr = listOfIds.split('<%=idSeparator%>');
    
     // if sth was previously selected, reinit and reselect
      if (slIdArr && slIdArr.length > 0 && slIdArr[0] != '') {
           initIt();
           for (var i =0; i< slIdArr.length; i++) {
                  reselectOnRedisplay(slIdArr[i]);
           }
       }
   
   }
      
}

function validateForm(form) {
    return validateHumanSampleOneAddTestPopupForm(form);
}

function cancelToParentForm() {
   if (window.opener && !window.opener.closed && window.opener.document.forms[0]) {
       //alert("Going to clear add test clicked");
        //window.opener.clearAddTestClicked();
        window.close();
   } 
}

function saveItToParentForm(form) {
   if (window.opener && !window.opener.closed && window.opener.document.forms[0]) {
       selIt();
       window.opener.setAddTestResults(form);
       //alert("Going to clear add test clicked");
       //window.opener.clearAddTestClicked();
       window.close();
   }
}

</script>



<table align="center">
<tr>
<td>

<select name="SelectList" id="SelectList" size="20" multiple="multiple" style="width: 150px" type="us.mn.state.health.lims.test.valueholder.Test">
	  <logic:iterate id="test" property="SelectList" name="<%=formName%>">
         <bean:define id="name" name="test" property="description" />
         <bean:define id="ID" name="test" property="id" />
         <%--don't display inactive tests--%>
         <logic:equal name="test" property="isActive" value="Y">
         <option value="<%=ID%>">
             <bean:write name="test" property="description" />
         </option>
         </logic:equal>
       </logic:iterate>
</select>
</td>
<td>
    <app:button  styleClass="button" property="save" onclick="addIt();" >
      			   <bean:message key="label.button.picklist.add"/>
    </app:button>
<br>
    <app:button  styleClass="button" property="save" onclick="delIt();" >
      			   <bean:message key="label.button.picklist.remove"/>
    </app:button>
<%--input type="button" value="<-" onclick="delIt();"></input--%>
</td>
<td>
<select name="PickList" id="PickList" size="20" multiple="multiple" style="width: 150px">
</select>
</td>
</tr>
</table>

<%--html:javascript formName="humanSampleOneAddTestPopupForm" staticJavascript="true"/--%>

