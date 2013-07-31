
package us.mn.state.health.lims.testreflex.action.util;

import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.ConfigurationProperties.Property;

public class ReflexActionFactory {


	public static ReflexAction getReflexAction() {
			String reflexAction = ConfigurationProperties.getInstance().getPropertyValue(Property.ReflexAction);
			
			if( "Haiti".equals(reflexAction)){
				return new HaitiReflexActions();
			}else if( "LNSP_Haiti".equals(reflexAction)){
				return new LNSPHaitiReflexActions();
			}else if( "RetroCI".equals(reflexAction)){
				return new RetroCIReflexActions();
			}
		
		return null;
	}
	
	
}
