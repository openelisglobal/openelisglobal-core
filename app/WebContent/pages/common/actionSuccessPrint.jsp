<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.util.StringUtil"
%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<script type="text/javascript">

function /*void*/ showSuccessMessage( show ){
	$("successMsg").style.visibility = show ? 'visible' : 'hidden';
}

function printBarcode(success, failure) {
	var labNo = document.getElementById('searchValue').value;
	document.getElementById('getBarcodePDF').href = "LabelMakerServlet";
	document.getElementById('getBarcodePDF').click();
}

</script>



<div id="successMsg" style="text-align:center; color:seagreen;  width : 100%;font-size:170%; visibility : hidden">
	<bean:message key="save.success"/>
	<div>
		<input type="button"
        	value="<%= StringUtil.getMessageForKey("barcode.common.button.print")%>"
        	id="printBarcodeButton"
        	onclick="printBarcode();">
        <a href="" id="getBarcodePDF" target="_blank"></a>
	</div>
</div>




