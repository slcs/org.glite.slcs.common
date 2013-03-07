/*
 * Copyright (c) 2010-2013 SWITCH
 * Copyright (c) 2006-2010 Members of the EGEE Collaboration
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glite.slcs.config;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FileConfigurationMonitor checks if the monitored file have been modified, and
 * if so sends a {@link FileConfigurationEvent} to all the registered
 * {@link FileConfigurationListener}.
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 */
public class FileConfigurationMonitor extends Timer {

    /** Default sleep time between 2 check (300000 millis = 300 sec = 5 min) */
    public static long DEFAULT_MONITORING_INTERVAL = 300000;

    /** Logging */
    static private Logger LOG = LoggerFactory.getLogger(FileConfigurationMonitor.class);

    /** List of FileConfigurationListener */
    private List<FileConfigurationListener> listeners_ = null;

    /** the monitored File used by the FileConfiguration */
    private File file_ = null;

    /** last modified timestamp of monitored file */
    private long lastModified_ = 0;

    /** pause between to check (millis) */
    private long monitoringInterval_ = DEFAULT_MONITORING_INTERVAL;

    /**
     * Creates a FileConfigurationMonitor for the given FileConfiguration.
     * 
     * @param file
     *            The file associated with the file to monitor.
     * @param monitoringInterval
     *            The time (in seconds) between 2 checks.
     * @param listener
     *            The FileConfigurationListener (this).
     * @return The new FileConfigurationMonitor object.
     */
    public static FileConfigurationMonitor createFileConfigurationMonitor(
            File file, String monitoringInterval,
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
            LOG.debug("file: " + file.getName() + " interval: " + interval
                    + ")");
        }

        // create the FileConfigurationMontitor
        FileConfigurationMonitor fileConfigurationMonitor = new FileConfigurationMonitor(
                file, interval);
        fileConfigurationMonitor.addFileConfigurationListener(listener);
        return fileConfigurationMonitor;
    }

    /**
     * Constr. Monitor the file associated with the given FileConfiguration and
     * check for change every DEFAULT_MONITORING_INTERVAL (5 min).
     * 
     * @param file
     *            The file to monitor
     */
    public FileConfigurationMonitor(File file) {
        this(file, DEFAULT_MONITORING_INTERVAL);
    }

    /**
     * Const. Monitor the file associated with the given FileConfiguration and
     * check for change every given <code>monitoringInterval</code>.
     * 
     * @param file
     *            The file to monitor.
     * @param monitoringInterval
     *            The pause between 2 check in millis.
     */
    public FileConfigurationMonitor(File file, long monitoringInterval) {
        // daemonize
        super(true);

        this.listeners_ = new Vector<FileConfigurationListener>();
        this.file_ = file;
        this.lastModified_ = file_.lastModified();
        this.monitoringInterval_ = monitoringInterval;
    }

    /**
     * FileConfigurationMonitorTask scheduled by the FileConfigurationMonitor to
     * checks last modification timestamp of the monitored file and dispatch
     * event to the listeners.
     */
    private class FileConfigurationMonitorTask extends TimerTask {
        /**
         * Checks the file for modification and send event to listeners.
         */
        public void run() {
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
        }
    }

    /**
     * Starts the FileConfigurationMonitorTask to monitor the file.
     */
    public void start() {
        LOG.info("schedule the FileConfigurationMonitorTask ("
                + monitoringInterval_ + " ms) for file: "
                + file_.getAbsolutePath());
        scheduleAtFixedRate(new FileConfigurationMonitorTask(), 0,
                monitoringInterval_);
    }

    /**
     * Stops to monitor the file.
     */
    public void shutdown() {
        LOG.info("cancel the FileConfigurationMonitorTask");
        this.cancel();
        // empty the listeners list
        this.listeners_.clear();
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
            FileConfigurationEvent event = new FileConfigurationEvent(this,
                    eventType);
            Iterator<FileConfigurationListener> listeners = listeners_.iterator();
            while (listeners.hasNext()) {
                FileConfigurationListener listener = (FileConfigurationListener) listeners.next();
                listener.fileConfigurationChanged(event);
            }
        }
    }
}
