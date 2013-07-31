<%@ page language="java" contentType="text/html; charset=utf-8"%>


<script language="JavaScript1.2">
function pageOnLoad() { 	
        //bugzilla 2044 changed to go to 'ViewSampleDemographicsAnd'	instead of 'SampleDemographicsAnd'
	 	window.opener.setAction(window.opener.document.forms[0], 'ViewSampleDemographicsAnd', 'yes', '');
     	window.close();
}
</script>
