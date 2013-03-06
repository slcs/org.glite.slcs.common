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
package org.glite.slcs;

/** 
 * SLCSException is the generic Exception for the SLCS system.
 *
 * @author Valery Tschopp <tschopp@switch.ch>
 */
public class SLCSException extends Exception {

    /**  */
    private static final long serialVersionUID= -8940087455900649750L;

    /**
     * 
     */
    public SLCSException() {
        super();
    }

    /**
     * @param arg0
     */
    public SLCSException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public SLCSException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * @param arg0
     */
    public SLCSException(Throwable arg0) {
        super(arg0);
    }

}
