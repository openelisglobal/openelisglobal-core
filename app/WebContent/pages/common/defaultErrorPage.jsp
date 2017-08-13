<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE html>
<html>
<head>
    <base href="<%=basePath%>">
    <title><bean:message key="errors.unhandled.title"/></title>   
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">    
    <meta http-equiv="description" content="Default error page">
    <link rel="stylesheet" type="text/css" href="<%=basePath%>css/bootstrap.css" />
    <script type="text/javascript" src="<%=basePath%>scripts/jquery-1.8.0.min.js"></script>
        
    <!-- Inline css --> 
    <style type="text/css">
    #header {
        margin-bottom: 1em;
        min-width: 970px;
        margin-bottom: 10px;
        padding: 10px 0px 0px 15px;
        height: 88px;
        overflow: visible;
        background: #486086; /* Old browsers */
        background: -moz-linear-gradient(top, #486086 0%, #557aaf 100%); /* FF3.6+ */
        background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#486086), color-stop(100%,#557aaf)); /* Chrome,Safari4+ */
        background: -webkit-linear-gradient(top, #486086 0%,#557aaf 100%); /* Chrome10+,Safari5.1+ */
        background: -o-linear-gradient(top, #486086 0%,#557aaf 100%); /* Opera11.10+ */
        background: -ms-linear-gradient(top, #486086 0%,#557aaf 100%); /* IE10+ */
        filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#486086', endColorstr='#557aaf',GradientType=0 ); /* IE6-9 */
        background: linear-gradient(top, #486086 0%,#557aaf 100%); /* W3C */
    }
    #oe-logo {
        float: left;
        margin: 0 15px 0 0
    }
    #oe-title {
        font-size: 24px;
        line-height: 32px;
        color: #fff;
        font-weight: bold
    }
    .troubleshooting {
        font-family: Menlo, Monaco, Consolas, "Courier New", monospace;
        font-size: 12px;
    }
    </style>
</head>
  
<body id="defaultErrorPage">

<div id="header">
    <div id="oe-logo"><img src="<%=basePath%>images/openelis_logo.png" title="OpenELIS" alt="OpenELIS"></div>
    <div id="oe-title"><bean:message key="homePage.heading"/></div>
</div>
      
<div class="container">

    <h1><bean:message key="errorpage.title"/></h1>
    <br />

    <div class="well">	
        <div class="row">
            <div class="span2">
                <img src="<%=basePath%>images/icon-exclamation.png"  title="<bean:message key="errorpage.title"/>" alt="<bean:message key="errorpage.title"/>" />
            </div>
            <div class="span9">
                <h3><bean:message key="errorpage.head1"/></h3> 

                <p><bean:message key="errorpage.para1"/></p>

                <ul>
                    <li><bean:message key="errorpage.li1"/></li>
                    <li><bean:message key="errorpage.li2"/></li>
                    <li><bean:message key="errorpage.li3"/>
                        <ul>
                            <li><bean:message key="errorpage.li3a"/></li>
                            <li><bean:message key="errorpage.li3b"/></li>
                        </ul>
                    </li>
                    <li><bean:message key="errorpage.li4"/></li>
                </ul>

                <p><bean:message key="errorpage.para2"/></p>

                <br />
                <button class="btn" onclick="history.go( -1 );return true;"><i class="icon-chevron-left"></i> <bean:message key="errorpage.previous.button"/></button>
                <br /><br />

                <h3><bean:message key="errorpage.head2"/></h3>
                <p><bean:message key="errorpage.para3"/></p>
                <Br />
                <div class="troubleshooting">
                    <p><bean:message key="errorpage.info.time"/> <span id="error-time"> </span></p>

                    <p><bean:message key="errorpage.info.page"/><br />
                    <span id="error-path"> </span></p>

                    <p id="previous-path"></p>

                    <div><bean:message key="errorpage.info.browser"/></div>
                    <ul id="browser-details"></ul>
                </div>

            </div>
        </div>
    </div>	

</div>
    
<script type="text/javascript">
$(document).ready(function () {
    // jquery functions for details on error
    // Browser details not localized, but probably ok.
    var myDate = new Date().toDateString() + " " + new Date().toTimeString();
    $("#error-time").html(myDate);
    var browserDetails = "<li>Version: " + navigator.appVersion + "</li>";
    browserDetails += "<li>Cookies Enabled: " + navigator.cookieEnabled + "</li>";
    browserDetails += "<li>Platform: " + navigator.platform + "</li>";
    browserDetails += "<li>User-agent header: " + navigator.userAgent + "</li>";
    $("#browser-details").html(browserDetails);
    var pathname = location.href;
    $("#error-path").text(pathname);
    var referrerPath = document.referrer;
    var referrer = '<bean:message key="errorpage.previous"/><br />' + referrerPath;
    if (referrerPath != '') {
            $("#previous-path").html(referrer);
    }
});	
</script>
    
</body>
</html>


