/**
 * Copyright (c) Codice Foundation
 *
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 *
 **/
package org.codice.ddf.admin.application.plugin;

import java.net.URI;
import java.util.Map;

/**
 * Defines an application configuration plugin. 
 * @author Jeren
 *
 */
public interface ApplicationConfigurationPlugin {
	/** key to mark that a plugin should be used for ALL applications.*/
	public static final String ALL_APPLICATION_KEY = "ALL";
	/** key for the display name. Used for creating json.*/
	public static final String DISPLAY_NAME_KEY = "displayName";
	/** key for the application name. Used for creating json.*/
	public static final String APPLICATION_NAME_KEY = "applicationName";
	/** key for the iframe location. Used for creating json.*/
	public static final String IFRAME_LOCATION_KEY = "iframeLocation";
	
	/**
	 * Returns the application name.
	 * @return the application name.
	 */
	public String getApplicationName();
	
	/**
	 * Returns the display name.
	 * @return the display name.
	 */
	public String getDisplayName();
	
	/**
	 * Returns the iframe location.
	 * @return the iframe location.
	 */
    public URI getIframeLocation();
    
    /**
     * Utility method that will handle the conversion of this object to something
     * jolokia can convert to json.
     * @return a constructed  map that jolokia can convert to json.
     */
    public Map<String, Object> toJSON();
    
    /**
     * Handles figuring out if this plugin is matching to the app name sent in. This will
     * handle the case where a plugin should be used for all.
     * @param appName - the name of the application we are going to test.
     * @return yes if the application matches, or should be applied to all applications, false if it doesn't.
     */
    public boolean matchesApplicationName(String appName);

}
