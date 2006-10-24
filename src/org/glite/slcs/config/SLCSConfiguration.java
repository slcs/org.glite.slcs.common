/*
 * $Id: SLCSConfiguration.java,v 1.2 2006/10/24 08:47:01 vtschopp Exp $
 * 
 * Created on Aug 9, 2006 by tschopp
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs.config;

import java.io.File;
import java.util.List;

import org.glite.slcs.SLCSConfigurationException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * SLCSConfiguration is a wrapper class for a XML file based configuration.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.2 $
 * @see org.apache.commons.configuration.XMLConfiguration
 */
public abstract class SLCSConfiguration {

    /** Logging */
    private static Log LOG= LogFactory.getLog(SLCSConfiguration.class);

    /** The file based configuration */
    private FileConfiguration configuration_= null;

    /**
     * Default constructor
     */
    protected SLCSConfiguration() {
    }

    /**
     * 
     * @param filename
     * @throws SLCSConfigurationException
     */
    protected SLCSConfiguration(String filename)
            throws SLCSConfigurationException {
        this.configuration_= loadConfiguration(filename);
        checkConfiguration();
    }

    /**
     * Creates a XMLConfiguration loaded with the given file.
     * 
     * @param filename
     *            The file name to load the XML configuration from.
     * @return The new FileConfiguration object
     * @throws SLCSConfigurationException
     *             If a configuration error occurs while loading the XML file.
     */
    protected FileConfiguration loadConfiguration(String filename)
            throws SLCSConfigurationException {
        FileConfiguration config= null;
        try {
            LOG.info("XMLConfiguration file=" + filename);
            config= new XMLConfiguration(filename);
            if (LOG.isDebugEnabled()) {
                File configFile= config.getFile();
                LOG.debug("XMLConfiguration file="
                        + configFile.getAbsolutePath());
            }
        } catch (ConfigurationException e) {
            LOG.error("Failed to create XMLConfiguration: " + filename, e);
            throw new SLCSConfigurationException("Failed to create XMLConfiguration: "
                                                         + filename,
                                                 e);
        }

        return config;
    }

    /**
     * Checks the validity of the configuration
     * 
     * @throws SLCSConfigurationException
     *             iff the configuration is not valid
     */
    abstract protected void checkConfiguration()
            throws SLCSConfigurationException;

    /**
     * 
     * @param name
     *            The key of the value to get
     * @return The value of for this key
     * @throws SLCSConfigurationException
     *             if the key is missing from configuration or empty
     */
    public String getString(String name) throws SLCSConfigurationException {
        return getString(name, true);
    }

    /**
     * 
     * @param name
     * @param safe
     * @return
     * @throws SLCSConfigurationException
     */
    public String getString(String name, boolean safe)
            throws SLCSConfigurationException {
        String value= configuration_.getString(name);
        if (safe && (value == null || value.equals(""))) {
            throw new SLCSConfigurationException(name + " is null or empty: "
                    + getFilename());
        }
        return value;
    }

    /**
     * 
     * @param name
     * @return
     */
    public int getInt(String name) {
        return configuration_.getInt(name);
    }

    /**
     * 
     * @param name
     *            The configuration key.
     * @return The associated List. Empty if the name is not in configuration.
     */
    public List getList(String name) {
        List list= configuration_.getList(name);
        return list;
    }

    /**
     * Checks if configuration key <code>name</code> is defined
     * 
     * @param name
     *            The configuration key name.
     * @return <code>true</code> iff the key is defined, <code>false</code>
     *         otherwise.
     */
    public boolean contains(String name) {
        return configuration_.containsKey(name);
    }

    /**
     * Sets the FileConfiguration and checks for validity.
     * 
     * @param configuration
     *            The FileConfiguration to set.
     * @throws SLCSConfigurationException
     */
    protected void setFileConfiguration(FileConfiguration configuration)
            throws SLCSConfigurationException {
        this.configuration_= configuration;
        checkConfiguration();
    }

    /**
     * @return The FileConfiguration interface.
     */
    public FileConfiguration getFileConfiguration() {
        return this.configuration_;
    }

    
    /**
     * @return The Configuration interface.
     */
    public Configuration getConfiguration() {
        return this.configuration_;
    }

    /**
     * @return The XML configuration absolute filename.
     */
    protected String getFilename() {
        return this.configuration_.getFile().getAbsolutePath();
    }

}
