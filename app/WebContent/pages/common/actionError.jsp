<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="org.apache.struts.taglib.TagUtils,org.apache.struts.action.*,org.apache.struts.Globals,java.util.Iterator,javax.servlet.jsp.JspException"
	import="us.mn.state.health.lims.common.action.IActionConstants"
	import="us.mn.state.health.lims.common.util.StringUtil"
	import="us.mn.state.health.lims.common.util.resources.ResourceLocator"
	import="us.mn.state.health.lims.common.util.validator.ActionError"
 %>
<%@page import="us.mn.state.health.lims.common.util.SystemConfiguration"%>
<%@page import="org.owasp.encoder.Encode"%>
<!DOCTYPE html>

<%-- removed deprecated calls to methods in org.apache.struts.util.RequestUtils--%>
<%--html:errors/--%>
<%--html:messages/--%>


<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%!
String path = "";
String basePath = "";
%>
<%
path = request.getContextPath();
basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>

<script language="JavaScript1.2">

function onLoad() {

  	// bugzilla 1397 If a page requires special functionality before load that isn't global, create a
	// preOnLoad method (if we need to run js before errors popup)
	if(window.prePageOnLoad)
	{  
		prePageOnLoad();
	}
       
  	// If a page requires special functionality on load that isn't global, create a
	// pageOnLoad method
	if(window.pageOnLoad)
	{  
		pageOnLoad();
	}

}

// The Struts action form object associated with this page. It is initialized in
// the onLoad() function below to ensure that it is available when defined.
var myActionForm;

// Initialize myActionForm variable after load.
myActionForm = document.forms["<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>"];

<% 	ActionMessages errors = null;
  	String fieldName = null;
    try {
    	out.println("var messages = null;");
        //errors = RequestUtils.getActionErrors(pageContext, Globals.ERROR_KEY);
        errors = TagUtils.getInstance().getActionMessages(pageContext, Globals.ERROR_KEY);
 
        Iterator iterator = errors.get();
        ActionMessage error = null;
        String messages = null;
        String message = null;           
        String moduleNotAllowMessageKey = "login.error.module.not.allow";             
                        
		java.util.Locale locale = (java.util.Locale)request.getSession().getAttribute(Globals.LOCALE_KEY);

        while (iterator.hasNext()) {
          	//error = (ActionError)iterator.next();   
            error = (ActionMessage)iterator.next();   
                                   
          	message =
				ResourceLocator.getInstance().getMessageResources().getMessage(
				locale,
                error.getKey(),
                error.getValues());
        
      		if( message == null ){
       			message = error.getKey();
       		}
        
           	if (messages == null) {
          		messages = message;
				out.println("messages = '" + Encode.forHtml(message) +"'");
			}
			else {
				out.println("messages = messages + '\\r\\n' + '" + Encode.forHtml(message) +"'");
			}

			if (error instanceof us.mn.state.health.lims.common.util.validator.ActionError) {
				if (fieldName == null) {
					fieldName = ((us.mn.state.health.lims.common.util.validator.ActionError)error).getFormField();
				}
			}
        }
        
        out.println("var focusElement = null;");
        if (fieldName != null) {
          	out.println("for (var i=0; i<document.forms.length; i++) {");
           	out.println("for (var j=0; j<document.forms[i].elements.length; j++) {");
           	out.println("var element = document.forms[i].elements[j];");
           	out.println("if (element != null && element.type != 'hidden' && element.disabled != true && (element.getAttribute('name') == '" + fieldName + "' || element.id == '" + fieldName + "')) {");
           	out.println("focusElement = element");
           	out.println("}");
           	out.println("}");
           	out.println("}");
        }
    
        // Add code to search links also
        if ( fieldName != null )
        {
           	out.println("if ( focusElement == null ) {");
           	out.println("for ( var k=0; k < document.links.length; k++ ) {");
           	out.println("   if ( document.links[k].id == '" + fieldName + "Link') {");
           	out.println("      focusElement = document.links[k];");
           	out.println("   }");
           	out.println("}");
           	out.println("}");
        }
                        
        String errorKey = null;
        if ( error != null )
            errorKey = (String)error.getKey(); 
           
        //user wants to display the error message on the top instead of popup for (module not allow only)                                                                    
        if (messages != null) {
     		out.println("if (focusElement != null) {focusElement.focus();}");
      		if ( !errorKey.equals(moduleNotAllowMessageKey) && !SystemConfiguration.getInstance().errorsToScreen())
     		    out.println("alert(messages);");         		    
            else{
            	out.println("var lines = messages.split('\\r\\n');"); 
             	out.println("document.write('<center><h1>');");
              	out.println("for( var i = 0; i < lines.length; i++)");
                out.println("document.write(lines[i] + '</br>');");
                out.println("document.write('</h1></center>');");
            }
        }
        //removing the global error from the session
        request.getSession().removeAttribute(Globals.ERROR_KEY);
            
 	 
    } catch (JspException e) {
            //RequestUtils.saveException(pageContext, e);
            TagUtils.getInstance().saveException(pageContext, e);
    }
%>

</script>