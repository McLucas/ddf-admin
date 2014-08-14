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
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the basic work for an ApplicationConfigurationPlugin. 
 * @author Jeren
 *
 */
public class AbstractApplicationConfigurationPlugin implements ApplicationConfigurationPlugin {
	/** the display name. Protected so implementers can set this.*/
	protected String displayName = "";
	/** the location of the iframe. Protected so implementers can set this.*/
	protected String iframeLocation = "";
	/** the application name. Protected so implementers can set this.*/
	protected String applicationName = ApplicationConfigurationPlugin.ALL_APPLICATION_KEY;

	/** {@inheritDoc}.*/
	@Override
	public String getApplicationName() {
		return applicationName;
	}

	/** {@inheritDoc}.*/
	@Override
	public String getDisplayName() {
		return displayName;
	}

	/** {@inheritDoc}.*/
	@Override
	public URI getIframeLocation() {
		return URI.create(iframeLocation);
	}
	
	/** {@inheritDoc}.*/
	@Override
	public Map<String, Object> toJSON() {
		Map<String, Object> jsonMapping = new HashMap<String, Object>();
		
		jsonMapping.put(ApplicationConfigurationPlugin.APPLICATION_NAME_KEY, applicationName);
		jsonMapping.put(ApplicationConfigurationPlugin.DISPLAY_NAME_KEY, displayName);
		jsonMapping.put(ApplicationConfigurationPlugin.IFRAME_LOCATION_KEY, iframeLocation);
		
		return jsonMapping;
	}

	/** {@inheritDoc}.*/
	@Override
	public boolean matchesApplicationName(String appName) {
		return (applicationName.equals(ApplicationConfigurationPlugin.ALL_APPLICATION_KEY) || applicationName.equals(appName));
	}

}
