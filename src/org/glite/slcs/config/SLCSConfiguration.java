/*
 * $Id: SLCSConfiguration.java,v 1.5 2009/07/31 12:53:40 vtschopp Exp $
 * 
 * Created on Aug 9, 2006 by tschopp
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs.config;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.glite.slcs.SLCSConfigurationException;

import org.apache.commons.configuration.AbstractConfiguration;
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
 * @version $Revision: 1.5 $
 * @see org.apache.commons.configuration.XMLConfiguration
 */
public abstract class SLCSConfiguration {

    static {
        // replace list delimiter comma (,) by unused character (^)
        AbstractConfiguration.setDelimiter('^');
    }
    
    /** Logging */
    private static Log LOG = LogFactory.getLog(SLCSConfiguration.class);

    /** The file based configuration */
    private FileConfiguration configuration_ = null;

    /**
     * Default constructor
     */
    protected SLCSConfiguration() {
    }

    /**
     * @param filename
     * @throws SLCSConfigurationException
     */
    protected SLCSConfiguration(String filename)
            throws SLCSConfigurationException {
        this.configuration_ = loadConfiguration(filename);
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
        XMLConfiguration config = null;
        try {
            LOG.info("XMLConfiguration file=" + filename);
            config = new XMLConfiguration(filename);
            LOG.debug("XMLConfiguration file=" + config.getFileName());
        } catch (ConfigurationException e) {
            LOG.error("Failed to create XMLConfiguration: " + filename, e);
            throw new SLCSConfigurationException("Failed to create XMLConfiguration: "
                    + filename, e);
        }
        return config;
    }

    /**
     * Creates a XMLConfiguration loaded from the given url.
     * 
     * @param url
     *            The URL of the file to load the XML configuration from.
     * @return The new FileConfiguration object
     * @throws SLCSConfigurationException
     *             If a configuration error occurs while downloading and loading the XML file.
     */
    protected FileConfiguration downloadConfiguration(URL url)
            throws SLCSConfigurationException {
        XMLConfiguration config = null;
        try {
            LOG.info("XMLConfiguration url=" + url);
            config = new XMLConfiguration(url);
            if (LOG.isDebugEnabled()) {
                File configFile = config.getFile();
                LOG.debug("XMLConfiguration file="
                        + configFile.getAbsolutePath());
            }
        } catch (ConfigurationException e) {
            LOG.error("Failed to create XMLConfiguration from: " + url, e);
            throw new SLCSConfigurationException("Failed to create XMLConfiguration: "
                    + url, e);
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
     * Returns the value of the key and throw exception if the key is not
     * defined.
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
     * Returns the value of the key and throw exception if the key is not
     * defined only if throwException is <code>true</code>.
     * 
     * @param name
     *            The key name of the value to read.
     * @param throwException
     *            Throw an exception if the key is not found or not.
     * @return The value or <code>null</code> if the key is not found or the value empty.
     * @throws SLCSConfigurationException
     */
    public String getString(String name, boolean throwException)
            throws SLCSConfigurationException {
        String value = configuration_.getString(name);
        if (value == null || value.equals("")) {
            value = null;
            if (throwException) {
                throw new SLCSConfigurationException(name
                        + " is null or empty: " + getFilename());
            }
        }
        return value;
    }

    /**
     * @param name
     * @return
     */
    public int getInt(String name) {
        return configuration_.getInt(name);
    }

    /**
     * @param name
     *            The configuration key.
     * @return The associated List. Empty if the name is not in configuration.
     */
    public List getList(String name) {
        List list = configuration_.getList(name);
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
        this.configuration_ = configuration;
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
