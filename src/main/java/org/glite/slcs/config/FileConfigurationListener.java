/*
 * $Id: FileConfigurationListener.java,v 1.2 2006/10/24 08:47:01 vtschopp Exp $
 * 
 * Created on Aug 25, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs.config;

import java.util.EventListener;

/**
 * FileConfigurationListener is used to monitor FileConfiguration modifications.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.2 $
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
