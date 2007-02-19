/*
 * $Id: FileConfigurationMonitor.java,v 1.4 2007/02/19 13:19:37 vtschopp Exp $
 * 
 * Created on Aug 25, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs.config;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * FileConfigurationMonitor monitors if a file have been modified, and if so
 * send a FileConfigurationEvent to all the registered
 * FileConfigurationListener.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.4 $
 */
public class FileConfigurationMonitor extends Thread {

    /** Default sleep time between 2 check (300000 millis = 300 sec = 5 min) */
    public static long DEFAULT_MONITORING_INTERVAL = 300000;

    /** Logging */
    private static Log LOG = LogFactory.getLog(FileConfigurationMonitor.class);

    /** List of FileConfigurationListener */
    private List listeners_ = null;

    /** the monitored File used by the FileConfiguration */
    private File file_ = null;

    /** last modified timestamp of monitored file */
    private long lastModified_ = 0;

    /** pause between to check (millis) */
    private long monitoringInterval_ = DEFAULT_MONITORING_INTERVAL;

    /** Monitoring thread stopper */
    private volatile boolean running_ = false;

    /**
     * Creates a FileConfigurationMonitor for the given FileConfiguration.
     * 
     * @param fileConfiguration
     *            The FileConfiguration associated with the file to monitor.
     * @param monitoringInterval
     *            The time (in seconds) between 2 checks.
     * @param listener
     *            The FileConfigurationListener (this).
     * @return The new FileConfigurationMonitor object.
     */
    public static FileConfigurationMonitor createFileConfigurationMonitor(
            FileConfiguration fileConfiguration, String monitoringInterval,
            FileConfigurationListener listener) {
        // parse GroupsFileMonitoringInterval parameter
        long interval = FileConfigurationMonitor.DEFAULT_MONITORING_INTERVAL;
        try {
            // interval is in seconds
            interval = Integer.parseInt(monitoringInterval);
            interval *= 1000;
        } catch (NumberFormatException e) {
            LOG.warn("FileMonitoringInterval does not contain a valid interval time (second). Using default: "
                    + interval);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("file: " + fileConfiguration.getFileName()
                    + " interval: " + interval + ")");
        }

        // create the FileConfigurationMontitor
        FileConfigurationMonitor fileConfigurationMonitor = new FileConfigurationMonitor(fileConfiguration, interval);
        fileConfigurationMonitor.addFileConfigurationListener(listener);
        return fileConfigurationMonitor;
    }

    /**
     * Constr. Monitor the file associated with the given FileConfiguration and
     * check for change every DEFAULT_MONITORING_INTERVAL (5 min).
     * 
     * @param fileConfiguration
     *            The FileConfiguration file to monitor
     */
    public FileConfigurationMonitor(FileConfiguration fileConfiguration) {
        this(fileConfiguration, DEFAULT_MONITORING_INTERVAL);
    }

    /**
     * Const. Monitor the file associated with the given FileConfiguration and
     * check for change every given <code>monitoringInterval</code>.
     * 
     * @param fileConfiguration
     *            The FileConfiguration file to monitor.
     * @param monitoringInterval
     *            The pause between 2 check in millis.
     */
    public FileConfigurationMonitor(FileConfiguration fileConfiguration,
            long monitoringInterval) {
        super("FileConfigurationMonitor(" + fileConfiguration.getFileName()
                + ")");
        setDaemon(true);
        // set reloading strategy
        fileConfiguration.setReloadingStrategy(new FileChangedReloadingStrategy());

        this.listeners_ = new Vector();
        this.file_ = fileConfiguration.getFile();
        this.lastModified_ = file_.lastModified();
        this.monitoringInterval_ = monitoringInterval;
    }

    /**
     * Starts to monitor the file for modification.
     */
    public void run() {
        running_ = true;
        LOG.info("Monitor for file: " + file_.getAbsolutePath()
                + " started");
        // start the monitoring thread for the file
        while (running_) {
            try {
                Thread.sleep(monitoringInterval_);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("checking last modified for: "
                            + file_.getAbsolutePath());
                }
                long currentLastModified = file_.lastModified();
                if (currentLastModified > lastModified_) {
                    lastModified_ = currentLastModified;
                    // dipatch the event to listener
                    LOG.info("File " + file_.getAbsolutePath() + " changed");
                    dispatchFileConfigurationEvent(FileConfigurationEvent.FILE_MODIFIED);
                }
            } catch (InterruptedException e) {
                running_ = false;
            }
        }
        LOG.info("Monitor for file: " + file_.getAbsolutePath()
                + " terminated.");
    }

    /**
     * Stops to monitor the file.
     */
    public void shutdown() {
        running_ = false;
        interrupt();
    }

    /**
     * Adds the listener to the FileConfigurationListener list.
     * 
     * @param listener
     *            The listener to add.
     * @see FileConfigurationListener
     */
    public void addFileConfigurationListener(FileConfigurationListener listener) {
        listeners_.add(listener);
    }

    /**
     * Removes the listener from the FileConfigurationListener list.
     * 
     * @param listener
     *            The listener to remove.
     * @return <code>true</code> if the listener was in the list,
     *         <code>false</code> otherwise.
     * @see FileConfigurationListener
     */
    public boolean removeFileConfigurationListener(
            FileConfigurationListener listener) {
        return listeners_.remove(listener);
    }

    /**
     * Disptaches the FileConfigurationEvent type to all
     * FileConfigurationListener registered.
     * 
     * @param eventType
     *            The FileConfigurationEvent type.
     */
    protected void dispatchFileConfigurationEvent(int eventType) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("eventType=" + eventType);
        }
        if (!listeners_.isEmpty()) {
            FileConfigurationEvent event = new FileConfigurationEvent(this, eventType);
            Iterator listeners = listeners_.iterator();
            while (listeners.hasNext()) {
                FileConfigurationListener listener = (FileConfigurationListener) listeners.next();
                listener.fileConfigurationChanged(event);
            }
        }
    }
}
