/*
 * $Id: ServiceException.java,v 1.3 2006/10/24 08:40:54 vtschopp Exp $
 * 
 * Created on Jul 18, 2006 by tschopp
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs;

/**
 * ServiceException is the EGEE exception to describe an invalid interaction
 * with the service or a fault service. The error message contains more
 * information.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.3 $
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
