<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date, java.util.List,
	org.apache.struts.Globals,
	us.mn.state.health.lims.common.util.SystemConfiguration,
	us.mn.state.health.lims.common.action.IActionConstants,
	java.util.Collection,
	java.util.ArrayList,
	us.mn.state.health.lims.inventory.form.InventoryKitItem,
	us.mn.state.health.lims.test.beanItems.TestResultItem,
	us.mn.state.health.lims.common.util.IdValuePair,
	us.mn.state.health.lims.common.util.StringUtil,
	us.mn.state.health.lims.common.util.ConfigurationProperties,
	us.mn.state.health.lims.common.util.ConfigurationProperties.Property" %>

<%@ taglib uri="/tags/struts-bean"		prefix="bean" %>
<%@ taglib uri="/tags/struts-html"		prefix="html" %>
<%@ taglib uri="/tags/struts-logic"		prefix="logic" %>
<%@ taglib uri="/tags/labdev-view"		prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax" %>

<bean:define id="formName"	value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>' />
<bean:define id="workplanType"	value='<%=(String) request.getParameter("type")%>' />
<bean:define id="tests" name="<%=formName%>" property="workplanTests" />
<bean:size id="testCount" name="tests" />

<%!
	boolean showAccessionNumber = false;
	String currentAccessionNumber = "";
	int rowColorIndex = 2;
%>
<%

	String basePath = "";
	String path = request.getContextPath();
	basePath = request.getScheme() + "://" + request.getServerName() + ":"
	+ request.getServerPort() + path + "/";
	currentAccessionNumber = "";

%>

<script type="text/javascript" >

function  /*void*/ setMyCancelAction(form, action, validate, parameters)
{
	//first turn off any further validation
	setAction(window.document.forms[0], 'Cancel', 'no', '');
}

function disableEnableTest(checkbox, index){

	if (checkbox.checked) {
		$("row_" + index).style.background = "#cccccc";
	}else {
		checkbox.checked = "";
		$("row_" + index).style.backgroundColor = "";
	}
}

function printWorkplan() {

	var form = window.document.forms[0];
	form.action = "PrintWorkplanReport.do";
	form.target = "_blank";
	form.submit();
}

</script>
<logic:notEqual name="testCount" value="0">
<bean:size name='<%= formName %>' property="workplanTests" id="size" />

<html:button property="print" styleId="print"  onclick="printWorkplan();"  >
	<bean:message key="workplan.print"/>
</html:button>
<br/><br/>
<bean:message key="label.total.tests"/> = <bean:write name="size"/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<img src="./images/nonconforming.gif" /> = <bean:message key="result.nonconforming.item"/>
<br/><br/>
<Table width="90%" border="0" cellspacing="0">
	<tr>
		<th width="5%" style="text-align: left;">
			<bean:message key="label.button.remove"/>
		</th>
		<% if( workplanType.equals("test") ){ %>
			<th width="3%">&nbsp;</th>
		<% } %>
    	<th width="10%" style="text-align: left;">
    		<%= StringUtil.getContextualMessageForKey("quick.entry.accession.number") %>
		</th>
		<% if(ConfigurationProperties.getInstance().isPropertyValueEqual(Property.SUBJECT_ON_WORKPLAN, "true")){ %>
		<th width="5%" style="text-align: left;">
			<%= StringUtil.getContextualMessageForKey("patient.subject.number") %>
		</th>	
		<% } %>
		<% if(ConfigurationProperties.getInstance().isPropertyValueEqual(Property.NEXT_VISIT_DATE_ON_WORKPLAN, "true")){ %>
	    <th width="5%" style="text-align: left;">
	    	<bean:message key="sample.entry.nextVisit.date"/>
	    </th>
	    <% } %>
		<% if( !workplanType.equals("test") ){ %>
		<th width="3%">&nbsp;</th>
		<th width="30%" style="text-align: left;">
	  		<bean:message key="sample.entry.project.testName"/>
		</th>
		<% } %>
		<th width="20%" style="text-align: left;">
	  		<bean:message key="sample.receivedDate"/>&nbsp;&nbsp;
		</th>
  	</tr>

	<logic:iterate id="workplanTests" name="<%=formName%>"  property="workplanTests" indexId="index" type="TestResultItem">
			<% showAccessionNumber = !workplanTests.getAccessionNumber().equals(currentAccessionNumber);
				   if( showAccessionNumber ){
					currentAccessionNumber = workplanTests.getAccessionNumber();
					rowColorIndex++; } %>
     		<tr id='<%="row_" + index %>' class='<%=(rowColorIndex % 2 == 0) ? "evenRow" : "oddRow" %>'  >
     			<td id='<%="cell_" + index %>'>
					<html:checkbox name="workplanTests"
						   property="notIncludedInWorkplan"
						   styleId='<%="includedCheck_" + index %>'
						   styleClass="includedCheck"
						   indexed="true"
						   onclick='<%="disableEnableTest(this," + index + ");" %>' />
				</td>
				<% if( workplanType.equals("test") ){ %>
				<td>
					<logic:equal name="workplanTests" property="nonconforming" value="true">
						<img src="./images/nonconforming.gif" />
					</logic:equal>
				</td>	
				<% } %>
	    		<td>
	      		<% if( showAccessionNumber ){%>
	      			<bean:write name="workplanTests" property="accessionNumber"/>
				<% } %>
	    		</td>
	    		<% if( ConfigurationProperties.getInstance().isPropertyValueEqual(Property.SUBJECT_ON_WORKPLAN, "true")){ %>
	    		<td>
	    			<% if(showAccessionNumber ){ %>
	    			<bean:write name="workplanTests" property="patientInfo"/>
	    			<% } %>
	    		</td>
	    		<% } %>
	    			<% if(ConfigurationProperties.getInstance().isPropertyValueEqual(Property.NEXT_VISIT_DATE_ON_WORKPLAN, "true")){ %>
	    			<td>
	    			<% if(showAccessionNumber ){ %>
		    			<bean:write name="workplanTests" property="nextVisitDate"/>
		    		<% } %>	
	    			</td>
	    		<% } %>
	    		<% if( !workplanType.equals("test") ){ %>
	    		<td>
		    		<logic:equal name="workplanTests" property="nonconforming" value="true">
						<img src="./images/nonconforming.gif" />
					</logic:equal>
				</td>
				<td>
					<bean:write name="workplanTests" property="testName"/>
				</td>
				<% } %>
	    		<td>
	      			<bean:write name="workplanTests" property="receivedDate"/>
	    		</td>
      		</tr>
  	</logic:iterate>
	<tr>
	    <td colspan="8"><hr/></td>
    </tr>
	<tr>
		<td>
      		<html:button property="print" styleId="print"  onclick="printWorkplan();"  >
				<bean:message key="workplan.print"/>
			</html:button>
		</td>
	</tr>
</Table>
</logic:notEqual>
<logic:equal name="testCount"  value="0">
	<h2><%= StringUtil.getContextualMessageForKey("result.noTestsFound") %></h2>
</logic:equal>

