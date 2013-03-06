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

import java.util.EventObject;

/** 
 * FileConfigurationEvent is a event sent by the FileConfigurationMonitor to all
 * the FileConfigurationListener.
 *
 * @author Valery Tschopp <tschopp@switch.ch>
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
