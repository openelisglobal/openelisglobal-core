<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants"
%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>
<%--bugzilla 1844--%>
<bean:define id="testType"	value='<%= IActionConstants.ASSIGNABLE_TEST_TYPE_TEST %>' />
<%!

//bugzilla 1494
String errorDate = "";

%>

<%

//bugzilla 1494
java.util.Locale locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
errorDate =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "error.date");
%>
<script>
//bugzilla 1844
sortOrder = 'sortFieldB';

//bugzilla 1844, 2293
function toggleSort() {
  if (sortOrder == 'sortFieldA') {
    //then it should be sorted by description now
    sortOrder = 'sortFieldB';
  } else {
    sortOrder = 'sortFieldA';
  } 
  //pass in parms id of select tag, null type, change label to alternate = true
  sort('selectedTestId', null, true);
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

function submitThis() {
   var field = document.getElementById("selectedTestId");
//alert("submitting " + field.name + " " + field.value + " " + field);
        if (field.value != '') {
            //setMenuAction(this, window.document.forms[0], '', 'yes', '?paging=-1');
            setAction(window.document.forms[0], 'View', 'no', '?ID=');
        }
}

function myCheckDate(date, event, dateCheck, onblur) {
 var validDate = DateFormat(date,date.value,event,dateCheck,'1');
 if (dateCheck) { 
   if (!validDate) {
     alert('<%=errorDate%>');
   }
 }
}

//bugzilla 2550
function clearSelections() {
   var field = document.getElementById("selectedTestId");
   var field2 = document.getElementById("receivedDateForDisplay");
   var field3 = document.getElementById("selectedTestSectionId");
   
   //below we will need to know if a test section was selected
   var savedField3 = field3.value;
   
   field.value = '';
   field2.value = '';
   field3.value = '';
   
//force resetting test drop down by firing change event on test section drop down
// (only reset test drop down if a test section was previously selected and test drop down filtered accordingly)
if (savedField3 != '') {
//On IE
if(field3.fireEvent) {
 field3.fireEvent("onchange");
}
//On Gecko based browsers
if(document.createEvent) {
 var evt = document.createEvent("HTMLEvents");
 if(evt.initEvent) {
  evt.initEvent("change", true, true);
 }
 if(field3.dispatchEvent) {
  field3.dispatchEvent(evt);
 } 
}
}

   
}
</script>


<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />

<html:hidden property="currentDate" name="<%=formName%>" />
<table width="100%">
  <tr> 
    <td colspan="2"><bean:message key="batchresultsentry.browse.receivedDate"/></td>
    <td width="22%"><bean:message key="batchresultsentry.browse.testSection"/></td>
    <td width="31%"><%--bugzilla 1844--%>
      <bean:message key="batchresultsentry.browse.test"/>&nbsp;&nbsp;
      <app:button styleClass="button" 
	    		  property="save"		    					    			
		   	      onclick="toggleSort();">
			      <bean:message key="label.button.toggle.sort"/>
      </app:button>
    </td>
    <td width="18%">&nbsp;</td>
  </tr>
  <tr> 
    <td colspan="2" >
     <app:text name="<%=formName%>" property="receivedDateForDisplay" allowEdits="true" onkeyup="myCheckDate(this, event, false,false)" onblur="myCheckDate(this, event, true, true)" styleClass="text" />
    </td>
    <td width="22"> 
       <html:select name="<%=formName%>" property="selectedTestSectionId" styleId="selectedTestSectionId">
	   	  <app:optionsCollection 
	    	name="<%=formName%>" 
			property="testSections" 
			label="testSectionName" 
			value="id"  
			 />
							        
   	   </html:select>
    </td><%--bugzilla 2550--%>
    <td width="24%"> 
        <%--bugzilla 1844, 2293 using custom select and option tags for sorting--%>
         <app:select name="<%=formName%>" property="selectedTestId" onclick="checkTest(this);" styleId="selectedTestId">
 	   	  <app:sortableOptionsCollection 
	    	name="<%=formName%>" 
			property="tests" 
			label="alternateTestDisplayValue" 
			value="id" 
			filterProperty="isActive" 
			filterValue="N"
			sortFieldA="testName"
			sortFieldB="description"
			alternateLabel="testDisplayValue"
			maxLength="35"
          />
							        
   	    </app:select>
    </td><%--bugzilla 2550--%>
    <td width="17%"> 
<%
    String viewDisabled = (String)request.getAttribute(IActionConstants.VIEW_DISABLED);
%>
       <html:button onclick="submitThis();" property="view" disabled="<%=Boolean.valueOf(viewDisabled).booleanValue()%>">
  			   <bean:message key="label.button.viewBatchResults"/>
  	   </html:button> 	      
    </td>
    <%--bugzilla 2550--%>
    <td width="7%"> 
       <html:button onclick="clearSelections();" property="clear" styleId="clear">
  			   <bean:message key="label.button.clear.selection"/>
  	   </html:button>
    </td>
  </tr>
</table>
 
<%--bugzilla 1844, 2293: we need to override/extend base AjaxJspTag.select (ajaxtags1.2) to allow for toggle-sorting
     of test dropdown and display of alternate value and in order to handle max width of dropdown)--%>
<%--ajax:select
  baseUrl="ajaxSelectDropDownXML"
  source="selectedTestSectionId"
  target="selectedTestId"
  parameters="selectedTestSectionId={selectedTestSectionId},provider=TestSectionTestSelectDropDownProvider,fieldName=testDisplayValue,idName=id,sortFieldA=testName,sortFieldB=description,alternateLabel=alternateTestDisplayValue"
  errorFunction="resetTestDropDown"
  parser="MyResponseXmlParser" 
/--%>

<script type="text/javascript">
//bugzilla 1844 this script is to replace ajax:select tag and extend functionality for toggle sort and label


//overriding AjaxTags ResponseXmlParser for Select Tag parse() function
//in order to add parsing of new custom  sortFieldA, sortFieldB, alternateLabel option attributes
MyResponseXmlParser = Class.create();
MyResponseXmlParser = Object.extend(new AbstractResponseParser(), {
  initialize: function() {
    this.type = "xml";
  },

  load: function(request) {
    this.content = request.responseXML;
    this.parse();
  },
  
  parse: function() {
    var root = this.content.documentElement;
    var responseNodes = root.getElementsByTagName("response");
    this.itemList = new Array();
    if (responseNodes.length > 0) {
      var responseNode = responseNodes[0];
      var itemNodes = responseNode.getElementsByTagName("item");
      for (var i=0; i<itemNodes.length; i++) {
        var nameNodes = itemNodes[i].getElementsByTagName("name");
        var valueNodes = itemNodes[i].getElementsByTagName("value");
        //1844
        var sortFieldANodes = itemNodes[i].getElementsByTagName("sortFieldA");
        var sortFieldBNodes = itemNodes[i].getElementsByTagName("sortFieldB");
        var alternateLabelNodes = itemNodes[i].getElementsByTagName("alternateLabel");
        if (nameNodes.length > 0 && valueNodes.length > 0 && sortFieldANodes.length > 0 && sortFieldBNodes.length > 0 && alternateLabelNodes.length > 0) {
          var name = nameNodes[0].firstChild.nodeValue;
          var value = valueNodes[0].firstChild.nodeValue;
          //1844
          var sortFieldA = sortFieldANodes[0].firstChild.nodeValue;
          var sortFieldB = sortFieldBNodes[0].firstChild.nodeValue;
          var alternateLabel = alternateLabelNodes[0].firstChild.nodeValue;
           this.itemList.push(new Array(name, value, sortFieldA, sortFieldB, alternateLabel));
        }
      }
    }
  }
});


function truncateToMaxLength(label) {
	// truncate to maxLength
	var truncateLength = 35;
	if (label.length > truncateLength) {
	  // now find next space after truncateLength (don't truncate in middle of word if possible)
		var indexOfNextSpaceForTruncation = label.indexOf(" ", truncateLength);
		if (indexOfNextSpaceForTruncation >= 0) {
			label = label.substring(0, indexOfNextSpaceForTruncation);
		} else {
			label = label.substring(0, label.length);
		}
	}
    return label;
}

var SortableSelect = Class.create();  
Object.extend(SortableSelect.prototype,AjaxJspTag.Select.prototype);     
// extend AjaxJspTag.Select (ajaxtags 1.2) to add sorting ability 
Object.extend(SortableSelect.prototype, {  
                
    initialize: function(url, options) {
        // super constructor
        AjaxJspTag.Select.prototype.initialize.call(this, url, options);  
        this.url = url;
        this.setOptions(options);
        this.setListeners();

        if (this.options.executeOnLoad == "true") {
          this.execute();
        }
    },

   handler: function(request, options) {
    // build an array of option values to be set as selected
    var defaultSelectedValues = (options.defaultOptions || '').split(",");

    $(options.target).options.length = 0;
    $(options.target).disabled = false;
    for (var i=0; i<options.items.length; i++) {
      //1844:for this page we want label to have maxLength of 35 so truncate if necessary
      options.items[i][0] = truncateToMaxLength(options.items[i][0]);
      var newOption = new Option(options.items[i][0], options.items[i][1]);
      //1844
      newOption.sortFieldA = options.items[i][2];
      newOption.sortFieldB = options.items[i][3];
      newOption.alternateLabel = options.items[i][4];
      //$(options.target).options[i] = new Option(options.items[i][0], options.items[i][1]);
      // set the option as selected if it is in the default list
      for (j=0; j<defaultSelectedValues.length && newOption.selected == false; j++) {
        if (defaultSelectedValues[j] == options.items[i][1]) {
          newOption.selected = true;
        }
      }
      $(options.target).options[i] = newOption;
    }
  }
});

new SortableSelect("ajaxSelectDropDownXML", 
{
parameters: "selectedTestSectionId={selectedTestSectionId},provider=TestSectionTestSelectDropDownProvider,fieldName=alternateTestDisplayValue,idName=id,sortFieldA=testName,sortFieldB=description,alternateLabel=testDisplayValue",
parser: MyResponseXmlParser,
target: "selectedTestId",
source: "selectedTestSectionId",
errorFunction: resetTestDropDown
}
)

</script>

