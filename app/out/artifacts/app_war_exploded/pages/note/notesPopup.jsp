<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants,
	us.mn.state.health.lims.common.util.SystemConfiguration" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<%--bugzilla 1942: status changes --%>
<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="refId" value='<%= (String)request.getAttribute("id") %>' />
<bean:define id="refTableId" value='<%= (String)request.getAttribute("tableId") %>' />
<bean:define id="noteTypeInternal" value='<%= SystemConfiguration.getInstance().getNoteTypeInternal() %>' />
<bean:define id="noteTypeExternal" value='<%= SystemConfiguration.getInstance().getNoteTypeExternal() %>' />
<bean:define id="disableExternalNotes" name="<%=formName%>" property="disableExternalNotes" type="java.lang.String" />
<%--for every table that needs to be connected to notes we need to define a default subject --%>

<%!

String allowEdits = "true";
String disabled = "false";
String checked = "";
String defaultSubject = "NOTE";

//add notes titles
String addNoteText  = "";
String addNoteSubject = "";
String addNoteType = "";
String resultDefaultSubject = "";
String editBlankNoteError = "";
String addBlankNoteError = "";
//bugzilla 1889
String moreThanOneExternalNoteError = "";

//bugzilla 1888
String isInternalNote = "false";
String isExternalNote = "false";

%>

<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}



java.util.Locale locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
addNoteSubject =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "note.subject");
addNoteText =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "note.text");
addNoteType =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "note.notetype");
                    
resultDefaultSubject =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "result.note.subject.default");

editBlankNoteError =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,                    
                    "note.notespopup.error.edit.blank");
addBlankNoteError =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,                    
                    "note.notespopup.error.add.blank");   
//bugzilla 1889
moreThanOneExternalNoteError =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,                    
                    "note.notespopup.error.morethanone.external.note");

if (refTableId.equals(SystemConfiguration.getInstance().getResultReferenceTableId())) {
        defaultSubject = resultDefaultSubject;
}
%>


<script language="JavaScript1.2">
function customOnLoad() {
  focusOnFirstInputField();
}

function validateNotesPopupForm(form) {
   var validated = false;
   validated = true;
   return validated;

}

function validateForm(form) {
    return validateNotesPopupForm(form);
}

function cancelToParentForm() {
   if (window.opener && !window.opener.closed && window.opener.document.forms[0]) {
        window.opener.clearClicked();
        window.close();
   } 
}

function saveItToParentForm(form) {
   //this is executed when save is selected 
   if (window.opener && !window.opener.closed && window.opener.document.forms[0]) {

        //initialize notes
        window.opener.initializeNotes();
        
        //bugzilla 1889
        var showEditBlankNoteError = false;
        var showAddBlankNoteError = false;
        var showMoreThanOneExternalNoteError = false;
        var externalNoteCount = 0;
        
        //existing notes
        <logic:iterate id="existingNote" indexId="ctr" name="<%=formName%>" property="notes" type="us.mn.state.health.lims.note.valueholder.Note" >
             var subjField = '<%= "notes[" + ctr + "].subject" %>';
             var textField = '<%= "notes[" + ctr + "].text" %>';
             var typeField = '<%= "notes[" + ctr + "].noteType" %>';
             if (document.getElementById(textField) != null && document.getElementById(subjField) != null && document.getElementById(textField).value != '' && document.getElementById(subjField).value != '') {
               window.opener.setANote(document.forms[0], '<%= existingNote.getId() %>', document.getElementById(subjField).value, document.getElementById(textField).value, document.getElementById(typeField).value, '<%= existingNote.getLastupdated().toString() %>');
             } else {
               showEditBlankNoteError = true;
             }
             //count external notes while looping (cannot be more than one)
             if (document.getElementById(typeField) != null && document.getElementById(typeField).value == '<%=noteTypeExternal%>') {
               externalNoteCount++;
             }
        </logic:iterate>
        
        
        //added notes
          var sect = 'addedRows';
          var section = document.getElementById(sect);
          var tbody = section.getElementsByTagName("TBODY")[0];
          var trs = tbody.getElementsByTagName("tr");
          var propText, propType, propSubject;


          for (var i = 0; i < trs.length; i++) {
               var inputs = trs[i].getElementsByTagName("input");
               //only do this for non-title rows
               if (inputs != null && inputs.length > 0) {
                propText = "newNoteTexts[" + i + "]";
                propSubject = "newNoteSubjects[" + i + "]";
                propType = "newNoteTypes[" + i + "]";
                var txt = document.getElementById(propText);
                var subj = document.getElementById(propSubject);
                var typ = document.getElementById(propType);
                                
                if (txt != null && subj != null && txt.value != '' && subj.value != '') {
                  window.opener.setANote(document.forms[0], '0', replaceWhiteSpaceWithBlank(subj.value), replaceWhiteSpaceWithBlank(txt.value), typ.value, '');
                } else {
                  showAddBlankNoteError = true;
                }
                //count external notes while looping (cannot be more than one)
                if (typ != null && typ.value == '<%=noteTypeExternal%>') {
                    externalNoteCount++;
                }
               }
           }
           
        //bugzilla 1889 check to make sure that not more than one external note being added
        if (externalNoteCount > 1) {
          showMoreThanOneExternalNoteError = true;
        }
        
        if (showMoreThanOneExternalNoteError || showEditBlankNoteError || showAddBlankNoteError) {
            if (showMoreThanOneExternalNoteError) {
              alert("<%=moreThanOneExternalNoteError%>");
            }
            if (showAddBlankNoteError) {
              alert("<%=addBlankNoteError%>");
            }
            if (showEditBlankNoteError) {
              alert("<%=editBlankNoteError%>");
            }
            return;
        } else {
          //bugzilla 2501
          window.opener.submitTheFormWithNotes(window.opener.document.forms[0], form.refId.value, form.refTableId.value);
          window.close();
        }
   } 
}


function replaceInputIndex(aName, newIndex) {
    var indexOfOpenBrace = aName.indexOf("[");
    var indexOfCloseBrace = aName.indexOf("]");
    var aNewNameBefore = aName.substring(0, indexOfOpenBrace);
    var aNewNameAfter = aName.substring(indexOfCloseBrace + 1);
    var aNewName = aNewNameBefore + '[' + newIndex +  ']' + aNewNameAfter;
    return aNewName;
}

function reSortRows(sect, index) {
 var section = document.getElementById(sect);
 var tbody = section.getElementsByTagName("TBODY")[0];
 var trs = tbody.getElementsByTagName("tr");
   for (var i = index; i < trs.length; i++) {
        var row = trs[i];
        var inputs = row.getElementsByTagName("input");
 
        var newElement;
        var parent;
        var size;
        var text;
        var prop;
        var value;
        var onClick;
        
          
        for (var x = 0; x < inputs.length; x++) {
        
           var aName = inputs[x].name;          
        
           var aNewName = replaceInputIndex(aName, i);
           
           newElement =  document.createElement(aNewName);  
           
           var type = inputs[x].type;
           prop = inputs[x].name;
           
           //need to replace the index input field  - but not for rowFieldIndex
           if (prop == 'delRow') {
              value = i;
           } 
           
           
 
           if (type == 'button') {
                       
              //get function name for onclick
              var indexOfOpenBrace = prop.indexOf('[');
              var functionName = prop.substring(0, indexOfOpenBrace);
              var onClick = functionName + '(\'' + sect + '\', ' + i + ');';
              
      	      newElement = document.createElement('<INPUT TYPE="BUTTON" NAME=' + prop + ' VALUE=\"' + value + '\" ONCLICK=\"' + onClick + '\" />')
              
           } 
       
           if (type == 'button') {
             parent = inputs[x].parentNode;
             parent.replaceChild(newElement, inputs[x]);
           }
           
            
        }
        
        
    }
//alert("This is tbody after reSort " + tbody.innerHTML);
}


function delRow(sect, index) {
  var section = document.getElementById(sect);
  var tbody = section.getElementsByTagName("TBODY")[0];
  var trs = tbody.getElementsByTagName("tr");
  var tmpEl;
  
  //delete data row first
    tbody.deleteRow(index);
    reSortRows(sect, index);

  
  //now delete title row
   tbody.deleteRow(index-1);
   reSortRows(sect, index-1);

}

function getNextRowNumber(section) {
 //alert("I am in getNextRowNumber()");
 var tbody = section.getElementsByTagName("TBODY")[0];
 var trs = tbody.getElementsByTagName("tr");
 //alert("returning " + trs.length);
 return trs.length;
}

function addRowToPopupPage() {
  var sect = 'addedRows';
  var section = document.getElementById(sect);
   
  var prop, onClick;
  var inputText, inputSubject, noteTypeDropDown;
  var delRowButton;
  var delRowButtonText;
  var td1, td2, td3, td4;

  //do headers
  var i = getNextRowNumber(section);
  var tbody = section.getElementsByTagName("TBODY")[0];
  var row = document.createElement('TR');
  var td1 = createTableCellWithWidth('20%');
  td1.appendChild(document.createTextNode('<%=addNoteSubject%>'));
  var td2 = createTableCellWithWidth('71%');
  td2.appendChild(document.createTextNode('<%=addNoteText%>'));
  var td3 = createTableCellWithWidth('5%');
  td3.appendChild(document.createTextNode('<%=addNoteType%>'));
  var td4 = createTableCellWithWidth('5%');
  row.appendChild(td1);
  row.appendChild(td2);
  row.appendChild(td3);
  row.appendChild(td4);
  tbody.appendChild(row);

  row = document.createElement('TR');
  td1 = createTableCellWithWidth('20%');
  td2 = createTableCellWithWidth('71%');
  td3 = createTableCellWithWidth('5%');
  td4 = createTableCellWithWidth('5%');
  i++;
      
  prop = 'newNoteTexts[' + i + ']';
  inputText = document.createElement('TEXTAREA');
  inputText.setAttribute('id', prop);
  inputText.setAttribute('indexed', 'true');
  inputText.setAttribute('cols','50');
  inputText.setAttribute('rows','4');

  prop = 'newNoteSubjects[' + i + ']';
  inputSubject = document.createElement('INPUT');
  inputSubject.setAttribute('type', 'text');
  inputSubject.setAttribute('id', prop);
  inputSubject.setAttribute('indexed', 'true');
  inputSubject.setAttribute('value','<%=defaultSubject%>');
    
  prop = 'newNoteTypes[' + i + ']';
  //create select/dropdown for type
  noteTypeDropDown = document.createElement('select');
  noteTypeDropDown.setAttribute('id', prop);
  noteTypeDropDown.setAttribute('indexed', 'true');
  noteTypeDropDown.setAttribute('onfocus', 'skipcycle=true');
  noteTypeDropDown.setAttribute('onblur', 'skipcycle=false');
    	
  var noteTypeDropDownOption = document.createElement('option');
  noteTypeDropDownOption.setAttribute('value','<%=noteTypeInternal%>');
  noteTypeDropDownOption.appendChild(document.createTextNode('<%=noteTypeInternal%>'));
  noteTypeDropDownOption.setAttribute('selected', 'selected');
  noteTypeDropDown.appendChild(noteTypeDropDownOption);
  
  if (! <%=disableExternalNotes%>) {
  noteTypeDropDownOption = document.createElement('option');
  noteTypeDropDownOption.setAttribute('value','<%=noteTypeExternal%>');
  noteTypeDropDownOption.appendChild(document.createTextNode('<%=noteTypeExternal%>'));
  noteTypeDropDown.appendChild(noteTypeDropDownOption);
  }
	 
  //create delete row button 
  prop = 'delRow[' + i + ']';
  onClick = 'delRow(\'' + sect + '\', ' + i + ');';
  delRowButton = document.createElement('INPUT');
  delRowButton.setAttribute('type', 'BUTTON');
  delRowButton.setAttribute('name', prop);
  delRowButton.setAttribute('value', 'X');
  delRowButton.setAttribute('onclick',onClick);
  
  td1.appendChild(inputSubject);
  td2.appendChild(inputText);
  td3.appendChild(noteTypeDropDown);
  td4.appendChild(delRowButton);
  
  row.appendChild(td1);
  row.appendChild(td2);
  row.appendChild(td3);
  row.appendChild(td4);
  
  tbody.appendChild(row);
  var focusField = 'newNoteTexts[' + i + ']';
  document.getElementById(focusField).focus();
  //alert("This is tbody " + tbody.innerHTML);
}

function createTableCellWithWidth( width ){
	var cell = document.createElement("TD");
	cell.setAttribute( "width", width);
	return cell;
}
</script>


<html:hidden property="refTableId" name="<%=formName%>" value="<%=refTableId%>"/>
<html:hidden property="refId" name="<%=formName%>" value="<%=refId%>"/>
<table align="center">
<tr><td colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*&nbsp;&nbsp;&nbsp;<bean:message key="note.internal.note.instruction"/></td></tr>
<tr><td colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*&nbsp;&nbsp;&nbsp;<bean:message key="note.external.note.instruction"/></td></tr>
<tr><td>&nbsp;</td></tr>
<logic:notEmpty name="<%=formName%>" property="notes">
<logic:iterate id="aNote" indexId="ctr" name="<%=formName%>" property="notes" type="us.mn.state.health.lims.note.valueholder.Note">
 <bean:define id="subj" name="aNote" property="subject" />
 <bean:define id="txt" name="aNote" property="text"/>
 <bean:define id="sysusr" name="<%=formName%>" property='<%= "notes[" + ctr + "].systemUser"%>' type="us.mn.state.health.lims.systemuser.valueholder.SystemUser" />
   <tr>
		<td class="label">
			<bean:message key="note.subject"/>:
		</td>	
		<td> 
			<app:text name="<%=formName%>" property='<%= "notes[" + ctr + "].subject"%>' allowEdits="false" />
		</td>
  </tr>
  <tr>
		<td class="label">
			<bean:message key="note.notetype"/>:<span class="requiredlabel">*</span>
		</td>	
		<td> 
		   	<html:select name="<%=formName%>" property='<%= "notes[" + ctr + "].noteType"%>' onfocus="skipcycle=true" onblur="skipcycle=false">
				<%-- bugzilla 2106: removed blank --%>
				<html:option value="I"><bean:message key="note.type.internal"/></html:option>
				<% if (!disableExternalNotes.equals("true")) { %> 
				  <html:option value="E"><bean:message key="note.type.external"/></html:option>
				<% } %>
			</html:select>
    	</td>
  </tr>
   <tr>
		<td class="label">
			<bean:message key="note.sysuser"/>:
		</td>	
		<td> 
			<bean:write name="sysusr" property="firstName"/>&nbsp;<bean:write name="sysusr" property="lastName"/>
		</td>
  </tr>
  <tr>
	    <td class="label">
			<bean:message key="note.text"/>:<span class="requiredlabel">*</span>
		</td>	
	    <logic:equal name="<%=formName%>" property='<%= "notes[" + ctr + "].noteType"%>' value="<%=noteTypeInternal%>">
		     <% isInternalNote = "true"; %>
		</logic:equal>
		<logic:notEqual name="<%=formName%>" property='<%= "notes[" + ctr + "].noteType"%>' value="<%=noteTypeInternal%>">
		     <% isInternalNote = "false"; %>
		</logic:notEqual>
		
		<logic:equal name="<%=formName%>" property='<%= "notes[" + ctr + "].noteType"%>' value="<%=noteTypeExternal%>">
		     <% isExternalNote = "true"; %>
		</logic:equal>
		<logic:notEqual name="<%=formName%>" property='<%= "notes[" + ctr + "].noteType"%>' value="<%=noteTypeExternal%>">
		     <% isExternalNote = "false"; %>
		</logic:notEqual>
		
		<% if (disableExternalNotes.equals("true")) { %> <%--bugzilla 1942 if disableExternalNotes then we cannot EDIT either external OR internal--%>
     	<td> 
		    <html:textarea name="<%=formName%>" property='<%= "notes[" + ctr + "].text"%>' cols="50" rows="4" disabled="true"/>
		</td>
		<% } else { %>
		<td> 
		    <html:textarea name="<%=formName%>" property='<%= "notes[" + ctr + "].text"%>' cols="50" rows="4" disabled="<%=Boolean.valueOf(isInternalNote).booleanValue()%>"/>
		</td>
	    <% } %>
   </tr>
   <input name="<%=formName%>" property='<%= "notes[" + ctr + "].id"%>' type="hidden" />
   <input name="<%=formName%>" property='<%= "notes[" + ctr + "].lastupdated"%>' type="hidden" />
   <tr>
    <td>&nbsp</td>
  </tr>
</logic:iterate>
</logic:notEmpty>
</table>

<div class="scrollvertical"  style="height: 130px;">
<table id="addedRows" class="blank" width="100%" border="1">
<tbody>
</tbody>
</table>
</div>
