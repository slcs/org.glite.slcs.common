/*
 * $Id: FileConfigurationEvent.java,v 1.1 2006/09/21 12:42:55 vtschopp Exp $
 * 
 * Created on Aug 25, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.config;

import java.util.EventObject;

/** 
 * FileConfigurationEvent is a event sent by the FileConfigurationMonitor to all
 * the FileConfigurationListener.
 *
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
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
