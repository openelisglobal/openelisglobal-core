/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/ 
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) ITECH, University of Washington, Seattle WA.  All Rights Reserved.
*
*/
package us.mn.state.health.lims.common.util;

import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.menu.daoimpl.MenuDAOImpl;
import us.mn.state.health.lims.menu.util.MenuUtil;
import us.mn.state.health.lims.menu.valueholder.Menu;
import us.mn.state.health.lims.role.dao.RoleDAO;
import us.mn.state.health.lims.role.daoimpl.RoleDAOImpl;
import us.mn.state.health.lims.role.valueholder.Role;
import us.mn.state.health.lims.siteinformation.dao.SiteInformationDAO;
import us.mn.state.health.lims.siteinformation.daoimpl.SiteInformationDAOImpl;
import us.mn.state.health.lims.siteinformation.valueholder.SiteInformation;

public class ConfigurationSideEffects {
	private static final RoleDAO roleDAO = new RoleDAOImpl();
	private static final SiteInformationDAO siteInformationDAO = new SiteInformationDAOImpl();
	
	public void siteInformationChanged( SiteInformation siteInformation){
		if( "modify results role".equals(siteInformation.getName())){
			Role modifierRole = roleDAO.getRoleByName("Results modifier");
			
			if( modifierRole != null && modifierRole.getId() != null){
				modifierRole.setActive("true".equals(siteInformation.getValue()));
				modifierRole.setSysUserId(siteInformation.getSysUserId());
				roleDAO.updateData(modifierRole);
			}
				
		}
		
		if("siteNumber".equals(siteInformation.getName())){
			SiteInformation accessionFormat = siteInformationDAO.getSiteInformationByName("acessionFormat");
			if( "SiteYearNum".equals(accessionFormat.getValue())){
				SiteInformation accessionPrefix = siteInformationDAO.getSiteInformationByName("Accession number prefix");
				if( GenericValidator.isBlankOrNull(accessionPrefix.getValue())){
					accessionPrefix.setValue(siteInformation.getValue());
					accessionPrefix.setSysUserId(siteInformation.getSysUserId());
					siteInformationDAO.updateData(accessionPrefix);
				}
			}
		}

		if("Patient management tab".equals(siteInformation.getName())){
			MenuDAOImpl menuDAO = new MenuDAOImpl();
			boolean active = "true".equals(siteInformation.getValue());

			Menu parentMenu = menuDAO.getMenuByElementId("menu_patient");
			if( parentMenu != null ){
				parentMenu.setIsActive(active);
				menuDAO.updateData( parentMenu);
			}

			Menu menu = menuDAO.getMenuByElementId("menu_patient_add_or_edit");
			if( menu != null ){
				menu.setIsActive( active);
				menuDAO.updateData(menu);
			}

			MenuUtil.forceRebuild();
		}
	}
}
