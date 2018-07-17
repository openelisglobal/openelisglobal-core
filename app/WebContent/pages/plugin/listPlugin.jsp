<%@ page language="java" contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>

<bean:define id="formName" value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>' />

<h3><bean:message key="plugin.installed.plugins"/></h3>

<ul>
    <logic:iterate id="pluginNames" name="<%=formName%>" property="pluginList">
		<li>
		<bean:write name="pluginNames" />
		</li>
	</logic:iterate>
</ul>
