/*
 * $Id: FileConfigurationEvent.java,v 1.2 2006/10/24 08:47:01 vtschopp Exp $
 * 
 * Created on Aug 25, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs.config;

import java.util.EventObject;

/** 
 * FileConfigurationEvent is a event sent by the FileConfigurationMonitor to all
 * the FileConfigurationListener.
 *
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.2 $
 */
public class FileConfigurationEvent extends EventObject {

    private static final long serialVersionUID= 8947149698171611640L;
    
    /** Event FILE_MODIFIED */
    public static final int FILE_MODIFIED= 0;
    
    /** Event type */
    private int eventType_= FILE_MODIFIED;
    
    /**
     * Constructor
     * @param source
     */
    public FileConfigurationEvent(Object source, int eventType) {
        super(source);
        this.eventType_= eventType;
    }

    /**
     * @return Returns the event type.
     */
    public int getType() {
        return this.eventType_;
    }
    
    
}
