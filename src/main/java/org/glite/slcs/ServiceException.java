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
 * ServiceException is the EGEE exception to describe an invalid interaction
 * with the service or a fault service. The error message contains more
 * information.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 */
public class ServiceException extends SLCSException {

    private static final long serialVersionUID= 163795222416407994L;

    public ServiceException() {
        super();
    }

    public ServiceException(String arg0) {
        super(arg0);
    }

    public ServiceException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public ServiceException(Throwable arg0) {
        super(arg0);
    }

}
