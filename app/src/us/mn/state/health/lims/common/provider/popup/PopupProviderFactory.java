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
* Copyright (C) The Minnesota Department of Health.  All Rights Reserved.
*/
package us.mn.state.health.lims.common.provider.popup;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.resources.ResourceLocator;

/**
 * This class will abstract the PopupProvider creation. It will read the
 * name of the class file from properties file and create the class
 * 
 * @version 1.0
 * @author diane benz
 * 
 */

public class PopupProviderFactory {

	private static PopupProviderFactory instance; // Instance of this

	// class

	// Properties object that holds popup provider mappings
	private Properties popupProviderClassMap = null;

	/**
	 * Singleton global access for PopupProviderFactory
	 * 
	 */

	public static PopupProviderFactory getInstance() {
		if (instance == null) {
			synchronized (PopupProviderFactory.class) {
				if (instance == null) {
					instance = new PopupProviderFactory();
				}
			}

		}
		return instance;
	}

	/**
	 * Create an object for the full class name passed in.
	 * 
	 * @param String
	 *            full class name
	 * @return Object Created object
	 */
	protected Object createObject(String className) throws LIMSRuntimeException {
		Object object = null;
		try {
			Class classDefinition = Class.forName(className);
			object = classDefinition.newInstance();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("PopupProviderFactory","createObject()",e.toString());
			throw new LIMSRuntimeException("Unable to create an object for "
					+ className, e, LogEvent.getLog(PopupProviderFactory.class));
		}
		return object;
	}

	/**
	 * Search for the PopupProvider implementation class name in the
	 * Popup.properties file for the given PopupProvider name
	 * 
	 * @param String
	 *            PopupProvider name e.g "HumanSampleCityStateZipPopupProvider"
	 * @return String Full implementation class e.g
	 *         "us.mn.state.health.lims.common.popup.provider"
	 */
	protected String getPopupProviderClassName(
			String popupProvidername) throws LIMSRuntimeException {
		if (popupProviderClassMap == null) { // Need to load the property
			// object with the class
			// mappings
			ResourceLocator rl = ResourceLocator.getInstance();
			InputStream propertyStream = null;
			// Now load a java.util.Properties object with the properties
			popupProviderClassMap = new Properties();
			try {
				propertyStream = rl
						.getNamedResourceAsInputStream(ResourceLocator.AJAX_PROPERTIES);

				popupProviderClassMap.load(propertyStream);
			} catch (IOException e) {
				//bugzilla 2154
				LogEvent.logError("PopupProviderFactory","getPopupProviderClassName()",e.toString());
				throw new LIMSRuntimeException(
						"Unable to load popup provider class mappings.",
						e, LogEvent.getLog(PopupProviderFactory.class));
			} finally {
				if (null != propertyStream) {
					try {
						propertyStream.close();
						propertyStream = null;
					} catch (Exception e) {
						//bugzilla 2154
						LogEvent.logError("PopupProviderFactory","getPopupProviderClassName()",e.toString());
					}
				}
			}
		}

		String mapping = popupProviderClassMap
				.getProperty(popupProvidername);
		if (mapping == null) {
			//bugzilla 2154
			LogEvent.logError("PopupProviderFactory","getPopupProviderClassName()",popupProvidername);
			throw new LIMSRuntimeException(
					"getPopupProviderClassName - Unable to find mapping for "
							+ popupProvidername);
		}
		return mapping;
	}

	/**
	 * Popup Provider creation method
	 * 
	 * @param name
	 * @return Popup Provider object
	 * 
	 */
	public BasePopupProvider getPopupProvider(String name)
			throws LIMSRuntimeException {
		BasePopupProvider provider = null;

		String className = getPopupProviderClassName(name);

		provider = (BasePopupProvider) createObject(className);

		return provider;
	}
	
}