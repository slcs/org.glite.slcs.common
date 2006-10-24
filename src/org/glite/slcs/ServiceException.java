/*
 * $Id: ServiceException.java,v 1.2 2006/10/24 08:37:24 vtschopp Exp $
 * 
 * Created on Jul 18, 2006 by tschopp
 *
 * Copyright (c) 2004. Members of the EGEE Collaboration. http://www.eu-egee.org
 */
package org.glite.slcs;

/**
 * ServiceException is the EGEE exception to describe an invalid interaction
 * with the service or a fault service. The error message contains more
 * information.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.2 $
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
