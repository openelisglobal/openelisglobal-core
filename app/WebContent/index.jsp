<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<logic:redirect forward="loginPage"/>

<%--

Redirect default requests to homePage global ActionForward.
By using a redirect, the user-agent will change address to match the path of our homePage ActionForward. 

--%>
