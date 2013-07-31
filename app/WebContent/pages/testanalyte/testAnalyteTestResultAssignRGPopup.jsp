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
String aID;
%>

<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}
aID = (String)request.getAttribute("aID");
%>


<script language="JavaScript1.2">
function customOnLoad() {
   
  var parentSection = window.opener.getSectionB();
  tbody = parentSection.getElementsByTagName("TBODY")[0];
  var trs = tbody.getElementsByTagName("tr");
  //popup window section
  var section = document.getElementById('resultSection');
  var thisTbody = section.getElementsByTagName("TBODY")[0];
  var rowsWithData = new Array();
  var rowsWithDataIndex = 0;
    
  //this is index for rgGrouping array: array holds count of testResults for each resultgroup found
  var index = 0;
  var rgGrouping = new Array();
  var rg2, count;
  for (var i = 0; i < trs.length; i++) {
     var inputs = trs[i].getElementsByTagName("input");
     var rg = inputs['rgGrouping'].value;
     //skip rows where there is no data
     if (inputs.length > 1) {
      rowsWithData[rowsWithDataIndex++] = trs[i];
      if (rg == rg2) {
         count++;
      } else  {
         //store total in rgGrouping array for this rg
         if (rg2 != null) {
           rgGrouping[index++] = count;
         } 
         //initialize count
         count = 1;
         rg2 = rg;
      }
     }
  }
  rgGrouping[index] = count;
  
  var rowIndex = 0;
  for (var k = 0; k < rgGrouping.length; k++) {
  
    for (var l = 0; l < rgGrouping[k]; l++, rowIndex++) {
       var prop, rowSpan;
       var selectedResultGroup, outputResultGroups, outputTestResultValues;
       var td1, td2, td3;
       var content = "";
       
       var inputs = rowsWithData[rowIndex].getElementsByTagName("input");
       
       if (inputs.length > 1) {
  
         row = document.createElement("<tr>");
        //alert("I am at row index " + rowIndex); 
         var tds = rowsWithData[rowIndex].getElementsByTagName("td");
       
         rowSpan = rgGrouping[k];
      
         td1 = document.createElement('<td rowspan=\"' + rowSpan + '\" width=\"4%\"' + ' valign=\"top\" >');
         td2 = document.createElement('<td rowspan=\"' + rowSpan + '\" width=\"3%\"' + ' valign=\"top\" >');
         td3 = document.createElement('<td width=\"93%\"' + ' valign=\"top\" >');
 
         var rg = inputs['rgGrouping'].value; 

         var rowFieldIndex = inputs['rowFieldIndex'].value;
         prop = 'testResultResultGroupList[' + rowFieldIndex + ']';
         outputResultGroups = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + rg + '\" indexed=\"true\" readonly size=\"1\" >');
      
         selectedResultGroup = document.createElement('<input type=\"radio\" name=\"selectedResultGroup\" value=\"' + rg + '\" >');
  
         var prop = 'testResultValueList[' + rowFieldIndex + ']';
         var trv = inputs[prop].value;     
         outputTestResultValues = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + trv + '\" indexed=\"true\" readonly size=\"150\" >');
  
         td1.appendChild(selectedResultGroup);
         td2.appendChild(outputResultGroups);
         td3.appendChild(outputTestResultValues);

         if (l == 0) {
            row.appendChild(td1);
            row.appendChild(td2);
         }
         row.appendChild(td3);
          
         thisTbody.appendChild(row);

        }
      
       }
     }
    //alert("This is thisTbody  " + thisTbody.innerHTML); 
}

function validateForm(form) {
    //return validateTestAnalyteTestResultAssignRGPopupForm(form);
}

function cancelToParentForm() {
   if (window.opener && !window.opener.closed && window.opener.document.forms[0]) {
        window.close();
   } 
}

function saveItToParentForm(form) {
 if (window.opener && !window.opener.closed && window.opener.document.forms[0]) {
    var resultGroupNumber;
    var selectedRG = document.forms[0].elements['selectedResultGroup'];
    if (selectedRG != null) {
     if (selectedRG.length == null) {
       if (selectedRG.checked == true) {
          resultGroupNumber = selectedRG.value;
       }
     } else {
        for (var i = 0; i < selectedRG.length; i++) {
           if (selectedRG[i].checked == true) {
             resultGroupNumber = selectedRG[i].value;
             break;
           }
        }
      } 
     }
  
    //now update parent form sectionA resultGroup
    if (resultGroupNumber != null) {
     var rgForAnalyte = window.opener.document.getElementById("rgForAnalyte" + '<%=aID%>');
     rgForAnalyte.value = resultGroupNumber;
    } else {
     var rgForAnalyte = window.opener.document.getElementById("rgForAnalyte" + '<%=aID%>');
     rgForAnalyte.value = '';
    }

   
    window.close();
   }
}

</script>


 
<table width="100%" border=2">
<tr>
<td id="h1" width="4%"><bean:message key="label.form.select"/>:</th>
<td id="h2" width="3%"><bean:message key="testanalytetestresult.assignRGPopup.resultGroup"/>:</th>
<td id="h3" width="93%"><bean:message key="testanalytetestresult.assignRGPopup.testResultValue"/>:</th>
</tr>

</table>
 
<div class="scrollvertical">
<table id="resultSection" class="blank" width="100%">
<tbody>
</tbody>
</table>
</div>


<%--html:javascript formName="testAnalyteTestResultAssignRGPopupForm" staticJavascript="true"/--%>

