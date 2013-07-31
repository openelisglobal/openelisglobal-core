<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants"
%>   
	
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>


<style>
P {
  font-family: Arial, Helvetica, sans-serif;
  font-size:90%;
  color:#000000;
  background-color : #F7F7E7;
}

DIV {
  background-color : #F7F7E7;
}

A:link {
  font-family: Arial, Helvetica, sans-serif;
  font-size:90%; 
  color:#663300;
  BACKGROUND-COLOR: #f7f7e7;
  }
A:active {
  font-family: Arial, Helvetica, sans-serif;
  font-size:90%; 
  color:#ff6600;
   BACKGROUND-COLOR: #f7f7e7;
  }
A:visited {
  font-family: Arial, Helvetica, sans-serif;
  font-size:90%; 
  color:#996633;
  BACKGROUND-COLOR: #f7f7e7;
  }

A.navigation:link
{
  font-family: Arial, Helvetica, sans-serif;
  font-size:65%; 
  color:#996633;
  BACKGROUND-COLOR: #f7f7e7;
  }

A.navigation:active
{
  font-family: Arial, Helvetica, sans-serif;
  font-size:65%; 
  color:#996633;
   BACKGROUND-COLOR: #cccc99;
  }

A.navigation:visited
{
  font-family: Arial, Helvetica, sans-serif;
  font-size:65%; 
  color:#996633;
   BACKGROUND-COLOR: #f7f7e7;
  }
  
.jstree_link
  {
    font-family: Tahoma, Geneva, Arial, Helvetica, sans-serif;
    font-size: 11px;
    color: #000000;
    text-decoration: none;
  }

.pagenode_link
  {
    font-family: Tahoma, Geneva, Arial, Helvetica, sans-serif;
    font-size: 11px;
    color: #008000;
	text-decoration: none;
	font-style: italic;
  }
  
.tree_popup 
   {
      position: absolute;
      z-index: 2;
      left: 0;
      top: 0;
      visibility: hidden;
      background: #E0E0E0;
    }

.tree_popup_text
   {
       font-family: Tahoma, Geneva, Arial, Helvetica, sans-serif;
       font-size: 11px;
   }

.tree_popup_td 
   {  
        border: #808080; 
        border-style: solid; 
        border-top-width: 1px; 
        border-right-width: 1px; 
        border-bottom-width: 0px; 
        border-left-width: 1px;
        cursor: hand;
   }

.tree_popup_td_bottom 
   {  
    border: #808080; 
    border-style: solid; 
    border-top-width: 1px; 
    border-right-width: 1px; 
    border-bottom-width: 1px; 
    border-left-width: 1px;
    cursor: hand;
   }

</style>
<div id="reportsSubMenu">
<center>
<table cellpadding="0" cellspacing="1" width="100%">
 <tr>
   <td valign="top">
      <%= (String)request.getAttribute(IActionConstants.THE_TREE) %> 
   </td>
 </tr>
</table>
</center>
</div>
