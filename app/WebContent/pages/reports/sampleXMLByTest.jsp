<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants"
%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<script>
function pageOnLoad() {
             var xmlString = document.getElementById("showXML");
             xmlString.innerHTML = document.getElementById("xmlString").value;
}

function resetMethodDropDown() {
  var methodDropDown = document.getElementById("selectedMethodId");
  //remove all options
  var i;
  for(i=methodDropDown.options.length-1;i>=0;i--) {
       methodDropDown.remove(i);
  }
  
  resetTestDropDown();

}


function resetTestDropDown() {
  var testDropDown = document.getElementById("selectedTestId");

  var i;
  for(i=testDropDown.options.length-1;i>=0;i--) {
       testDropDown.remove(i);
  }


}

function checkTest(field) {
  var testDropDown = document.getElementById("selectedTestId");
  if (testDropDown.options.length == 1) {
     submitThis(field);
  }
}

function submitThis(field) {
//alert("submitting " + field.name + " " + field.value + " " + field);
        if (field.value != '') {
            //setMenuAction(this, window.document.forms[0], '', 'yes', '?paging=-1');
            setAction(window.document.forms[0], 'Process', 'no', '');
        }
}

</script>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<html:hidden property="xmlString" name="<%=formName%>" />

<table width="100%">
  <tr> 
    <td colspan="5"> 
      <h2><bean:message key="testanalytetestresult.browse.selectTestName.subtitle"/>:</h2>
    </td>
  </tr>
  <tr> 
    <td colspan="2"><bean:message key="testanalytetestresult.browse.testSection"/></td>
    <td width="22%"><bean:message key="testanalytetestresult.browse.method"/></td>
    <td width="31%"><bean:message key="testanalytetestresult.browse.test"/></td>
    <td width="18%">&nbsp;</td>
  </tr>
  <tr> 
    <td colspan="2"> 
       <html:select name="<%=formName%>" property="selectedTestSectionId" styleId="selectedTestSectionId">
	   	  <app:optionsCollection 
	    	name="<%=formName%>" 
			property="testSections" 
			label="testSectionName" 
			value="id"  
			 />
							        
   	   </html:select>
   	   
    </td>
    <td width="22%"> 
        <html:select name="<%=formName%>" property="selectedMethodId" styleId="selectedMethodId">
 	   	  <app:optionsCollection 
	    	name="<%=formName%>" 
			property="methods" 
			label="methodName" 
			value="id"  
			filterProperty="isActive" 
			filterValue="N"
			/>
							        
   	   </html:select>
    </td>
    <td width="31%"> 
         <html:select name="<%=formName%>" property="selectedTestId" onchange="submitThis(this);" onclick="checkTest(this);" styleId="selectedTestId">
 	   	  <app:optionsCollection 
	    	name="<%=formName%>" 
			property="tests" 
			label="testName" 
			value="id" 
			filterProperty="isActive" 
			filterValue="N"
          />
							        
   	   </html:select>
    </td>
    <td width="18%"> 
    </td>
  </tr>
  <tr>	
  		<td class="label">
			&nbsp;
		</td>	
		<td> 
             <div id="showXML" class="blank">&nbsp;</div>
		</td>
  </tr>
</table>

 
  
  <ajax:select
  baseUrl="ajaxSelectDropDownXML"
  source="selectedTestSectionId"
  target="selectedTestId"
  parameters="selectedTestSectionId={selectedTestSectionId},provider=TestSectionTestSelectDropDownProvider,fieldName=testName,idName=id"
  errorFunction="resetTestDropDown"
  />
  
  <ajax:select
  baseUrl="ajaxSelectDropDownXML"
  source="selectedMethodId"
  target="selectedTestId"
  parameters="selectedTestSectionId={selectedTestSectionId},selectedMethodId={selectedMethodId},provider=MethodTestSelectDropDownProvider,fieldName=testName,idName=id"
  errorFunction="resetTestDropDown"
  />
 