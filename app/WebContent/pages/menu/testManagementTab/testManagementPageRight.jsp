<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>

<%!String menuDef = "default";

	%>
<%if (request.getAttribute("menuDefinition") != null) {
				menuDef = (String) request.getAttribute("menuDefinition");
			}
			//System.out.println("menuDef " + menuDef);
%>
<bean:define id="menuDef" value="<%=menuDef%>" />
