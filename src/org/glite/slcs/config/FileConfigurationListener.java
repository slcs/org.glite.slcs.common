/*
 * $Id: FileConfigurationListener.java,v 1.1 2006/09/21 12:42:55 vtschopp Exp $
 * 
 * Created on Aug 25, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.config;

import java.util.EventListener;

/**
 * FileConfigurationListener is used to monitor FileConfiguration modifications.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public interface FileConfigurationListener extends EventListener {

    /**
     * When the FileConfiguration changed, the listener receives this event
     * notification.
     * 
     * @param event
     *            The FileConfigurationEvent dispatched
     */
    void fileConfigurationChanged(FileConfigurationEvent event);

}
