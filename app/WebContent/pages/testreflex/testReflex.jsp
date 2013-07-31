<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<%!

String allowEdits = "true";

%>

<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

%>

<script language="JavaScript1.2">

function validateForm(form) {
     return validateTestReflexForm(form);
}

function resetTestAnalyteDropDown() {
  var testAnalyteDropDown = document.getElementById("testAnalyteId");

  var i;
  for(i=testAnalyteDropDown.options.length-1; i>=0; i--) {
       testAnalyteDropDown.remove(i);
  }

}

function resetTestResultDropDown() {
  var testResultDropDown = document.getElementById("testResultId");

  var i;
  for(i=testResultDropDown.options.length-1;i>=0;i--) {
       testResultDropDown.remove(i);
  }
}

function resetSecondTestAnalyteDropDown() {
  document.getElementById("secondTestAnalyteId").length=0;
}

function resetSecondTestResultDropDown() {
 document.getElementById("secondTestResultId").length=0;
}


function showHideSecondTest(button, targetId){
	if( button.value == "Second Test" ){
		$(targetId).show();
		button.value = "No Second Test";
		$("useSecondTestId").value="true";
	}else{
		$(targetId).hide();
		button.value = "Second Test";
		$("useSecondTestId").value="false";
	}
}

</script>

<table>
		<tr>
			<td class="label">
				<bean:message key="testreflex.id"/>:
			</td>
			<td>
				<app:text name="<%=formName%>" property="id" allowEdits="false"/>
		    </td>
		</tr>
		<tr>
		    <td class="label">
		   		<bean:message key="testreflex.test"/>:<span class="requiredlabel">*</span>
			</td>
			<td><%--bugzilla 1844 use testDisplayValue instead of testName--%>
               <html:select name="<%=formName%>" property="testId" styleId="testId">
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
		</tr>
		<tr>
		    <td class="label">
		   		<bean:message key="testreflex.testAnalyte"/>:<span class="requiredlabel">*</span>
			</td>
			<td>
               <html:select name="<%=formName%>" property="testAnalyteId" styleId="testAnalyteId">
   	        	  <app:optionsCollection
	             	name="<%=formName%>"
	        		property="testAnalytes"
		        	label="analyte.analyteName"
		        	value="id"
		     	/>
	    	   </html:select>
            </td>
		</tr>
		<tr>
		    <td class="label">
		   		<bean:message key="testreflex.testResult"/>:<span class="requiredlabel">*</span>
			</td>
			<td>
               <html:select name="<%=formName%>" property="testResultId" styleId="testResultId">
 	        	  <app:optionsCollection
	             	name="<%=formName%>"
	        		property="testResults"
		        	label="value"
		        	value="id"
		     	/>
	    	   </html:select>
            </td>
		</tr>
</table>
<hr/>
<table>
	<tr>
		<td>
			<html:button property="showHide" value="Second Test" onclick="showHideSecondTest(this, 'secondTest');" />
		</td>
	</tr>
</table>
<div id="secondTest" style="display:none;" >
<html:hidden property="useSecondTest" name="<%=formName%>" styleId="useSecondTestId" />
Both test conditions will have to be satisfied for the reflex test to be added to sample<br>
<table>
<tr>
		    <td class="label">
		   		<bean:message key="testreflex.test"/>:<span class="requiredlabel">*</span>
			</td>
			<td>
               <html:select name="<%=formName%>" property="secondTestId" styleId="secondTestId">
 	        	  <app:optionsCollection
	             	name="<%=formName%>"
	        		property="tests"
		        	label="testDisplayValue"
		        	value="id"
		        	filterProperty="isActive"
		        	filterValue="N"
		     	/>
	    	   </html:select>
            </td>
		</tr>
		<tr>
		    <td class="label">
		   		<bean:message key="testreflex.testAnalyte"/>:<span class="requiredlabel">*</span>
			</td>
			<td>
               <html:select name="<%=formName%>" property="secondTestAnalyteId" styleId="secondTestAnalyteId">
   	        	  <app:optionsCollection
	             	name="<%=formName%>"
	        		property="testAnalytes"
		        	label="analyte.analyteName"
		        	value="id"
		     	/>
	    	   </html:select>
            </td>
		</tr>
		<tr>
		    <td class="label">
		   		<bean:message key="testreflex.testResult"/>:<span class="requiredlabel">*</span>
			</td>
			<td>
               <html:select name="<%=formName%>" property="secondTestResultId" styleId="secondTestResultId">
 	        	  <app:optionsCollection
	             	name="<%=formName%>"
	        		property="testResults"
		        	label="value"
		        	value="id"
		     	/>
	    	   </html:select>
            </td>
		</tr>
</table>
</div>
<hr/>
<table>
		<tr>
		    <td class="label">
		   		<bean:message key="testreflex.addedTest"/>
			</td>
			<td><%--bugzilla 1844--%>
               <html:select name="<%=formName%>" property="addedTestId" styleId="addedTestId">
 	        	  <app:optionsCollection
	             	name="<%=formName%>"
	        		property="addedTests"
		        	label="testName"
		        	value="id"
		        	filterProperty="isActive"
		        	filterValue="N"
		     	/>
	    	   </html:select>
            </td>
		</tr>
		<tr>
		    <td class="label">
		   		<bean:message key="testreflex.addedAction"/>
			</td>
			<td><%--bugzilla 1844--%>
               <html:select name="<%=formName%>" property="actionScriptletId" styleId="actionScriptletId">
 	        	  <app:optionsCollection
	             	name="<%=formName%>"
	        		property="actionScriptlets"
		        	label="scriptletName"
		        	value="id"
		     	/>
	    	   </html:select>
            </td>
		</tr>
 		<tr>
			<td class="label">
				<bean:message key="testreflex.flags"/>:
			</td>
			<td>
				<html:text name="<%=formName%>" property="flags" />
			</td>
		 </tr>
  		<tr>
		<td>&nbsp;</td>
		</tr>
</table>
  <ajax:select
  baseUrl="ajaxSelectDropDownXML"
  source="testId"
  target="testAnalyteId"
  parameters="testId={testId},provider=TestTestAnalyteSelectDropDownProvider,fieldName=analyte.analyteName,idName=id"
  errorFunction="resetTestAnalyteDropDown"
  postFunction="resetTestResultDropDown"
  />

  <ajax:select
  baseUrl="ajaxSelectDropDownXML"
  source="secondTestId"
  target="secondTestAnalyteId"
  parameters="testId={secondTestId},provider=TestTestAnalyteSelectDropDownProvider,fieldName=analyte.analyteName,idName=id"
  errorFunction="resetSecondTestAnalyteDropDown"
  postFunction="resetSecondTestResultDropDown"
  />

  <ajax:select
  baseUrl="ajaxSelectDropDownXML"
  source="testAnalyteId"
  target="testResultId"
  parameters="testAnalyteId={testAnalyteId},provider=TestAnalyteTestResultSelectDropDownProvider,fieldName=value,idName=id"
  errorFunction="resetTestResultDropDown"
  />

 <ajax:select
  baseUrl="ajaxSelectDropDownXML"
  source="secondTestAnalyteId"
  target="secondTestResultId"
  parameters="testAnalyteId={secondTestAnalyteId},provider=TestAnalyteTestResultSelectDropDownProvider,fieldName=value,idName=id"
  errorFunction="resetSecondTestResultDropDown"
  />

<html:javascript formName="testReflexForm"/>

