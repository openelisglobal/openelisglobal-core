<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ page import="us.mn.state.health.lims.common.action.IActionConstants"%>
<%@ page import="us.mn.state.health.lims.common.util.SystemConfiguration"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="idSeparator" value='<%= SystemConfiguration.getInstance().getDefaultIdSeparator() %>' />
<%--replaced 1776 with 1844,2293--%>
<bean:define id="panelType"	value='<%= IActionConstants.ASSIGNABLE_TEST_TYPE_PANEL %>' />
<bean:define id="testType"	value='<%= IActionConstants.ASSIGNABLE_TEST_TYPE_TEST %>' />
<%!String allowEdits = "true";%>
<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
				allowEdits = (String) request
						.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
			}
%>

<script language="JavaScript1.2">
//bugzilla 1844, 2293 
//when page is first loaded the sort order of individual tests is by name (numeric code)
//sortorder of panels is always sort to top and by description
var sortOrder ;
var panelArray = new Array();


//****Repeated the funtion for not to have the error message when nothing is selected
function selIt(btn) {
//alert("Here I am selIt");
  var pickList = document.getElementById("PickList");
  var pickOptions = pickList.options;
  var pickOLength = pickOptions.length;
  if (pickOLength < 1) {  
    return false;
  }
  for (var i = 0; i < pickOLength; i++) {
    pickOptions[i].selected = true;
  }
  return true;
}


function customOnLoad()
{   

   //bugzilla 2293, 1844 do some initializing for the test panel
    sortOrder = 'sortFieldB';
    
    //bugzilla 2293, 1844 load total array of panels 
    <logic:iterate id="aTest" indexId="counter" property="SelectList" name="<%=formName%>" type="us.mn.state.health.lims.test.valueholder.AssignableTest">
     <bean:define id="aTestId" name="aTest" property="id" type="java.lang.String"/>
 	  <bean:define id="aTestType" name="aTest" property="type" type="java.lang.String"/>
 	  <bean:define id="aTestName" name="aTest" property="assignableTestName" type="java.lang.String"/>
 	    <logic:equal name="aTestType" value="<%=panelType%>">
 	     <logic:notEmpty name="aTest" property="listOfAssignableTests">
 	      panelArray['<%=counter%>'] = '<%=aTestId%>';
 	      <bean:define id="tests" name="aTest" property="listOfAssignableTests" type="java.util.ArrayList"/>
 	      <logic:iterate id="panelItemTest" name="tests" type="us.mn.state.health.lims.test.valueholder.AssignableTest">
 	       <bean:define id="aPanelItemTestId" name="panelItemTest" property="id" type="java.lang.String"/>
 	        panelArray['<%=counter%>'] += '<%=idSeparator%>' + '<%=aPanelItemTestId%>';
 	      </logic:iterate>
 	     </logic:notEmpty>
        </logic:equal>
	</logic:iterate>

	if (window.opener.getSelectedTestIds() && window.opener.getSelectedTestIds() != '')
	{
		//then load both lists from parentform
		var idObj = window.opener.getSelectedTestIds();
		var listOfIds = idObj.value;    
		var slIdArr = new Array();
		//trim leading ;
		if (listOfIds.indexOf('<%=idSeparator%>') == 0) 
		{
			listOfIds = listOfIds.substring(1);
		}
		slIdArr = listOfIds.split('<%=idSeparator%>');
    
		// if sth was previously selected, reinit and reselect
		if (slIdArr && slIdArr.length > 0 && slIdArr[0] != '') 
		{
			initIt();
			for (var i =0; i< slIdArr.length; i++) 
			{
			    //bugzilla 1844 includeSorting
				reselectOnRedisplay(slIdArr[i], true);
			}
			//bugzilla 1856 sort after reselect
			sort('PickList', null, false);
		}   
	} 
	//bugzilla 1810
	focusOnFirstInputField();    
}

function cancelToParentForm()
{
	if (window.opener && !window.opener.closed && window.opener.document.forms[0]) 
	{
		window.close();
	} 
}

function saveItToParentForm(form) 
{
	if (window.opener && !window.opener.closed && window.opener.document.forms[0]) 
	{
		selIt();
		window.opener.setAddTestResults(form);
		window.close();
	}
}


//bugzilla 2293, 1844
function selectTests(){
	var selectList = document.getElementById("SelectList");  
	var selectIndex = selectList.selectedIndex;		
	var selectOptions = selectList.options;
	
	for (var i = 0; i < selectOptions.length; i++) {
		if (selectList[i] != null && selectList[i].type == '<%=panelType%>' && selectList[i].selected) {
    	    //get corresponding sub test ids
			var panelIds = new Array();
			for (var x = 0; x < panelArray.length; x++) {
     		  panelIds = panelArray[x].split('<%=idSeparator%>');
			  if (selectList[i].value == panelIds[0]) {
			   break;
		      }
		    }
			//find the individual tests already in the drop down contained in the panel
 		    for (var y = 1; y < panelIds.length; y++) {	   
 		     for (var z = 0; z < selectOptions.length; z++) {
 			   if (selectList[z] != null && selectList[z].type == '<%=testType%>' && selectList[z].value == panelIds[y]) {
 			     selectList[z].selected = 'true';
 			     break;
 			   }
		     }
	        }
	        //now remove the panel from the list as we have selected the actual individual tests that correspond to it
        	selectOptions[i] = null;
        	//make sure we reset the index since we are removing this option
        	i--;
   	   }
   }
	 
addIt(true);

}

//bugzilla 1844, 2293
function toggleSort() {
  if (sortOrder == 'sortFieldA') {
    //then it should be sorted by description now
    sortOrder = 'sortFieldB';
  } else {
    sortOrder = 'sortFieldA';
  } 
  sort('SelectList', '<%=testType%>', false);
  sort('PickList', '<%=testType%>', false);
  
}

</script>
<table align="center">
	<tr>
		<td>
            <%--bugzilla 1810 added onkeypress and onblur--%>
            <%--bugzilla 1844, 2293 using custom select and option tags for sorting and distinguish between panel and test type AssignableTests--%>
			<app:select name="<%=formName%>" 
			        property="SelectList"
					size="20" 
					multiple="multiple" 
					style="width: 300px" 
					onkeypress="return selectAsYouType(event)" onblur="clearKeysPressed(event)"					
					>
				<logic:iterate id="test" 
							   property="SelectList" 
							   name="<%=formName%>"
							   type="us.mn.state.health.lims.test.valueholder.AssignableTest">
				<bean:define id="name" name="test" property="displayValue" type="java.lang.String"/>
				<bean:define id="id" name="test" property="id" type="java.lang.String" />
				<bean:define id="tooltip" name="test" property="tooltipText" type="java.lang.String" />
    			<bean:define id="sortFieldA" name="test" property="assignableTestName" type="java.lang.String" />
    			<bean:define id="sortFieldB" name="test" property="description" type="java.lang.String" />
     			<bean:define id="type" name="test" property="type" type="java.lang.String" />
     			    <%--1844, 2293 using custom option tag in order to add attributes type, sortFieldA, sortFieldB--%>
					<app:sortableOption value="<%=id%>" title="<%=tooltip%>" type="<%=type%>" sortFieldA="<%=sortFieldA%>" sortFieldB="<%=sortFieldB%>"><bean:write name="test" property="displayValue" /></app:sortableOption>
				</logic:iterate>
			</app:select>
            <%--bugzilla 1844--%>
			<br>
                <app:button styleClass="button" 
		    			property="save"		    					    			
		    			onclick="toggleSort();">
				        <bean:message key="label.button.toggle.sort"/>
    		   </app:button>
            <br>
		</td>
		<%--AIS - bugzilla 1812 --%>
		<td valign="bottom">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<%--replaced 1776 with 1844,2293--%>
		    <app:button styleClass="button" 
		    			property="save"		    					    			
		    			onclick="selectTests();" >
				<bean:message key="label.button.picklist.add"/>
    		</app:button>
            <%--bugzilla 1844--%>
			<br/>
		    <app:button styleClass="button" 
		    			property="save" 
		    			onclick="delIt(true);" >
				<bean:message key="label.button.picklist.remove"/>
		    </app:button>
		</td>
		<td>
			<select name="PickList" 
					id="PickList" 
					size="20" 
					multiple="multiple" 
					style="width: 300px" />
		</td>
	</tr>
</table>