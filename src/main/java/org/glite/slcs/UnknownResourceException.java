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
 * UnknownResourceException is the EGEE exception to describe a problem with the
 * resource. Though you want to wait a little while first or limit the number of
 * attempts.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 */
public class UnknownResourceException extends SLCSException {

    private static final long serialVersionUID= 6039782634545012125L;

    public UnknownResourceException() {
        super();
    }

    public UnknownResourceException(String arg0) {
        super(arg0);
    }

    public UnknownResourceException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public UnknownResourceException(Throwable arg0) {
        super(arg0);
    }

}
