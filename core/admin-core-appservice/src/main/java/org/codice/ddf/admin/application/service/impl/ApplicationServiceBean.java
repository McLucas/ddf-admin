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
package org.codice.ddf.admin.application.service.impl;

import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.karaf.features.BundleInfo;
import org.apache.karaf.features.Feature;
import org.codice.ddf.admin.application.plugin.ApplicationConfigurationPlugin;
import org.codice.ddf.admin.application.rest.model.FeatureDto;
import org.codice.ddf.admin.application.service.Application;
import org.codice.ddf.admin.application.service.ApplicationNode;
import org.codice.ddf.admin.application.service.ApplicationService;
import org.codice.ddf.admin.application.service.ApplicationServiceException;
import org.codice.ddf.ui.admin.api.ConfigurationAdminExt;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the Application Service MBean. Provides an MBean interface
 * for the application service api.
 * 
 */
public class ApplicationServiceBean implements ApplicationServiceBeanMBean {

    

	private ObjectName objectName;

    private MBeanServer mBeanServer;

    private ApplicationService appService;

    private static final String MAP_NAME = "name";

    private static final String MAP_VERSION = "version";

    private static final String MAP_DESCRIPTION = "description";

    private static final String MAP_CHILDREN = "children";

    private static final String MAP_STATE = "state";
    
    private static final String MAP_URI = "uri";
    
    private static final String INSTALL_PROFILE_DEFAULT_APPLICATIONS = "defaultApplications";

	private static final String INSTALL_PROFILE_DESCRIPTION = "description";

	private static final String INSTALL_PROFILE_NAME = "name";

    private static final String MAP_DEPENDENCIES = "dependencies";

    private static final String MAP_PARENTS = "parents";
    
    private static final String MAP_STATUS = "status";
    
    private static final String MAP_REPOSITORY = "repository";

    private Logger logger = LoggerFactory.getLogger(ApplicationServiceBeanMBean.class);

    private final ConfigurationAdminExt configAdminExt;
    
    /** the service pid string.*/
    private static final String SERVICE_PID = Constants.SERVICE_PID;

    /** the service factor pid.*/
    private static final String SERVICE_FACTORYPID = ConfigurationAdmin.SERVICE_FACTORYPID;

    /** has all the application configuration plugins.*/
    private List<ApplicationConfigurationPlugin> pluginList;

    /**
     * Creates an instance of an ApplicationServiceBean
     * 
     * @param appService
     *            ApplicationService that is running in the system.
     * @throws ApplicationServiceException
     *             If an error occurs when trying to construct the MBean
     *             objects.
     */
    public ApplicationServiceBean(ApplicationService appService, ConfigurationAdminExt configAdminExt) throws ApplicationServiceException {
        this.appService = appService;
        this.configAdminExt = configAdminExt;
        try {
            objectName = new ObjectName(ApplicationService.class.getName()
                    + ":service=application-service");
            mBeanServer = ManagementFactory.getPlatformMBeanServer();
        } catch (MalformedObjectNameException mone) {
            throw new ApplicationServiceException("Could not create objectname.", mone);
        }
    }

    /**
     * Initializes the initial variables and registers the class to the MBean
     * server. <br/>
     * <br/>
     * <b>NOTE: This should be run before any other operations are performed.
     * Operations will NOT be usable until this is called (and until destroy()
     * is called).</b>
     * 
     * @throws ApplicationServiceException
     *             if an error occurs during registration.
     */
    public void init() throws ApplicationServiceException {
        try {
            try {
                logger.debug("Registering application service MBean under object name: {}",
                        objectName.toString());
                mBeanServer.registerMBean(this, objectName);
            } catch (InstanceAlreadyExistsException iaee) {
                // Try to remove and re-register
                logger.info("Re-registering Application Service MBean");
                mBeanServer.unregisterMBean(objectName);
                mBeanServer.registerMBean(this, objectName);
            }
        } catch (Exception e) {
            logger.warn("Could not register mbean.", e);
            throw new ApplicationServiceException(e);
        }
    }

    /**
     * Destroys the application service bean by unregistering it from the MBean
     * server. <br/>
     * <br/>
     * <b>NOTE: This should be run after all operations are completed and the
     * bean is no longer needed. Operations will NOT be usable after this is
     * called (until init() is called). </b>
     * 
     * @throws ApplicationServiceException
     *             if an error occurs during unregistration.
     */
    public void destroy() throws ApplicationServiceException {
        try {
            if (objectName != null && mBeanServer != null) {
                mBeanServer.unregisterMBean(objectName);
            }
        } catch (Exception e) {
            logger.warn("Exception unregistering mbean: ", e);
            throw new ApplicationServiceException(e);
        }
    }



    @Override
    public List<Map<String, Object>> getInstallationProfiles(){
        List<Feature> installationProfiles = appService.getInstallationProfiles();
        List<Map<String, Object>> profiles = new ArrayList<Map<String, Object>>();

        for(Feature profile : installationProfiles){
            Map<String, Object> profileMap = new HashMap<String, Object>();
            profileMap.put(INSTALL_PROFILE_NAME, profile.getName());
            profileMap.put(INSTALL_PROFILE_DESCRIPTION, profile.getDescription());

            List<String> includedFeatures = new ArrayList<String>();
            for (Feature feature : profile.getDependencies()) {
                includedFeatures.add(feature.getName());
            }
            profileMap.put(INSTALL_PROFILE_DEFAULT_APPLICATIONS, includedFeatures);

            profiles.add(profileMap);
        }

        return profiles;

    }


    @Override
    public List<Map<String, Object>> getApplicationTree() {
        Set<ApplicationNode> rootApplications = appService.getApplicationTree();
        List<Map<String, Object>> applications = new ArrayList<Map<String, Object>>();
        for (ApplicationNode curRoot : rootApplications) {
            applications.add(convertApplicationNode(curRoot));
        }
        logger.debug("Returning {} root applications.", applications.size());
        return applications;
    }

    private Map<String, Object> convertApplicationNode(ApplicationNode application) {
        logger.debug("Converting {} to a map", application.getApplication().getName());
        Map<String, Object> appMap = new HashMap<String, Object>();
        Application internalApplication = application.getApplication();
        appMap.put(MAP_NAME, internalApplication.getName());
        appMap.put(MAP_VERSION, internalApplication.getVersion());
        appMap.put(MAP_DESCRIPTION, internalApplication.getDescription());
        appMap.put(MAP_STATE, application.getStatus().getState().toString());
        appMap.put(MAP_URI, internalApplication.getURI().toString());
        List<Map<String, Object>> children = new ArrayList<Map<String, Object>>();
        for (ApplicationNode curNode : application.getChildren()) {
            children.add(convertApplicationNode(curNode));
        }
        appMap.put(MAP_CHILDREN, children);
        return appMap;
    }

    @Override
    public List<Map<String, Object>> getApplications() {
        Set<ApplicationNode> rootApplications = appService.getApplicationTree();
        List<Map<String, Object>> applications = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> applicationsArray = new ArrayList<Map<String, Object>>();
        for (ApplicationNode curRoot : rootApplications) {
            List<String> parentList = new ArrayList<String>();
            applications.add(convertApplicationEntries(curRoot, parentList, applicationsArray));
        }
        logger.debug("Returning {} root applications.", applications.size());
        return applicationsArray;
    }

    private Map<String, Object> convertApplicationEntries(ApplicationNode application, List<String> parentList, List<Map<String, Object>> applicationsArray) {
        logger.debug("Converting {} to a map", application.getApplication().getName());
        Map<String, Object> appMap = new HashMap<String, Object>();
        Application internalApplication = application.getApplication();
        appMap.put(MAP_NAME, internalApplication.getName());
        appMap.put(MAP_VERSION, internalApplication.getVersion());
        appMap.put(MAP_DESCRIPTION, internalApplication.getDescription());
        appMap.put(MAP_STATE, application.getStatus().getState().toString());
        appMap.put(MAP_URI, internalApplication.getURI().toString());
        List<String> childrenList = new ArrayList<String>();
        parentList.add(internalApplication.getName());
        List<String> transferParentList = new ArrayList<String>();
        appMap.put(MAP_PARENTS, parentList);

        for (ApplicationNode curNode : application.getChildren()) {
            Application node = curNode.getApplication();
            childrenList.add(node.getName());
            makeDependencyList(childrenList, curNode);

            convertApplicationEntries(curNode, parentList, applicationsArray);
        }
        appMap.put(MAP_DEPENDENCIES, childrenList);

        if (parentList.size() == 1) {
            transferParentList.clear();
            appMap.put(MAP_PARENTS, transferParentList);
        }
        else {
            int index = parentList.indexOf(internalApplication.getName());
            for (int i = 0; i < index; i++) {
                transferParentList.add(parentList.get(i));
            }
            appMap.put(MAP_PARENTS, transferParentList);
            parentList.clear();
            parentList.addAll(transferParentList);
        }
        applicationsArray.add(appMap);
        return appMap;
    }

    private void makeDependencyList(List<String> childrenList, ApplicationNode application) {
        logger.debug("Getting Dependency List", application.getApplication().getName());
        for (ApplicationNode curNode : application.getChildren()) {
            Application node = curNode.getApplication();
            childrenList.add(node.getName());
            makeDependencyList(childrenList, curNode);
        }
    }

    @Override
    public synchronized boolean startApplication(String appName) {
        try {
            logger.debug("Starting application with name {}", appName);
            appService.startApplication(appName);
            logger.debug("Finished installing application {}", appName);
            return true;
        } catch (ApplicationServiceException ase) {
            logger.warn("Application " + appName + " was not successfully started.", ase);
            return false;
        }
    }

    @Override
    public synchronized boolean stopApplication(String appName) {
        try {
            logger.debug("Stopping application with name {}", appName);
            appService.stopApplication(appName);
            logger.debug("Finished stopping application {}", appName);
            return true;
        } catch (ApplicationServiceException ase) {
            logger.warn("Application " + appName + " was not successfully stopped.", ase);
            return false;
        }
    }

    @Override
    public void addApplications(List<Map<String, Object>> applicationURLList) {
        for (Map<String, Object> curURL : applicationURLList) {
            try {
                appService.addApplication(new URI((String) curURL.get("value")));
            } catch (URISyntaxException use) {
                logger.warn("Could not add application with url {}, not a valid URL.", 
                        curURL.get("value"));
            } catch (ApplicationServiceException ase) {
                logger.warn("Could not add application with url {} due to error.", 
                        curURL.get("value"), ase);
            }
        }
    }
    
    @Override
    public void removeApplication(String applicationURL) {
        if (!StringUtils.isEmpty(applicationURL)) {
            try {
                logger.debug("Removing application with URL: {}", applicationURL);
                appService.removeApplication(new URI((String) applicationURL));
            } catch (URISyntaxException use) {
                logger.warn("Could not remove application with url {}, not a valid URL.", 
                        applicationURL);
            } catch (ApplicationServiceException ase) {
                logger.warn("Could not remove application with url {} due to error.", 
                        applicationURL, ase);
            }
        }
    }

    /** {@inheritDoc}.*/
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getServices(String applicationID) {
		List<Map<String, Object>> services = configAdminExt.listServices(getDefaultFactoryLdapFilter(), getDefaultLdapFilter());
		List<Map<String, Object>> returnValues = new ArrayList<Map<String, Object>>();
				
		if (!services.isEmpty()) {
			Application app = appService.getApplication(applicationID);

			if (app != null) {
				try {
					Set<BundleInfo> bundles = app.getBundles();

					Set<String> bundleLocations = new HashSet<String>();
					for (BundleInfo info : bundles) {
						bundleLocations.add(info.getLocation());
					}

					for (Map<String, Object> service : services) {
						if (service.containsKey("configurations")) {
							List<Map<String, Object>> configurations = (List<Map<String, Object>>) service.get("configurations");
							for (Map<String, Object> item : configurations) {
								if (item.containsKey("bundle_location")) {
									String bundleLocation = (String) item.get("bundle_location");
									if (bundleLocations.contains(bundleLocation)) {
										returnValues.add(service);
									}
								}
							}
						}
					}

				} catch (ApplicationServiceException e) {
					logger.warn("There was an error while trying to access the application", e);
					return new ArrayList<Map<String, Object>>();
				}
			}
		}

		return returnValues;
	}
	
    private String getDefaultFactoryLdapFilter() {
        List<String> filterList = new ArrayList<String>();
        if (CollectionUtils.isNotEmpty(filterList)) {
            StringBuilder ldapFilter = new StringBuilder();
            ldapFilter.append("(");
            ldapFilter.append("|");

            for (String fpid : filterList) {
                ldapFilter.append("(");
                ldapFilter.append(SERVICE_FACTORYPID);
                ldapFilter.append("=");
                ldapFilter.append(fpid);
                ldapFilter.append(")");
            }

            ldapFilter.append(")");

            return ldapFilter.toString();
        }
        return "(" + SERVICE_FACTORYPID + "=" + "*)";
    }

    private String getDefaultLdapFilter() {
    	List<String> filterList = new ArrayList<String>();
        if (CollectionUtils.isNotEmpty(filterList)) {
            StringBuilder ldapFilter = new StringBuilder();
            ldapFilter.append("(");
            ldapFilter.append("|");

            for (String fpid : filterList) {
                ldapFilter.append("(");
                ldapFilter.append(SERVICE_PID);
                ldapFilter.append("=");
                ldapFilter.append(fpid);
                ldapFilter.append("*");
                ldapFilter.append(")");
            }

            ldapFilter.append(")");

            return ldapFilter.toString();
        }
        return "(" + SERVICE_PID + "=" + "*)";
    }

    @Override
    public List<Map<String, Object>> getConfigurationPlugins(String appName) {
        List<Map<String, Object>> returnValues = new ArrayList<Map<String, Object>>();

        for (ApplicationConfigurationPlugin plugin : pluginList) {
            if (plugin.matchesApplicationName(appName)) {
                returnValues.add(plugin.toJSON());
            }
        }

        return returnValues;
    }

    /**
     * Getter method for the plugin list.
     * @return the plugin list.
     */
    public List<ApplicationConfigurationPlugin> getPluginList() {
        return pluginList;
    }

    /**
     * Setter method for the plugin list.
     * @param pluginList the plugin list.
     */
    public void setPluginList(List<ApplicationConfigurationPlugin> pluginList) {
        this.pluginList = pluginList;
    }

    @Override
    public List<Map<String, Object>> getAllFeatures() {
        return getFeatureMap(appService.getAllFeatures());
    }

    @Override
    public List<Map<String, Object>> findApplicationFeatures(String applicationName) {
        return getFeatureMap(appService.findApplicationFeatures(applicationName));
    }

    private List<Map<String, Object>> getFeatureMap(List<FeatureDto> featureViews) {
        List<Map<String, Object>> features = new ArrayList<Map<String, Object>>();
        try {
            for (FeatureDto feature : featureViews) {
                Map<String, Object> featureMap = new HashMap<String, Object>();
                featureMap.put(MAP_NAME, feature.getName());
                featureMap.put(MAP_VERSION, feature.getVersion());
                featureMap.put(MAP_STATUS, feature.getStatus());
                featureMap.put(MAP_REPOSITORY, feature.getRepository());
                features.add(featureMap);
            }
        } catch (Exception ex) {
            logger.warn("getFeatureMap Exception: " + ex);
        }
        return features;
    }

}
