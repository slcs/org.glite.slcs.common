/*
 * $Id: AuthException.java,v 1.1 2006/09/21 12:42:52 vtschopp Exp $
 * 
 * Created on Jul 17, 2006 by tschopp
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs;

/**
 * AuthException is the EGEE exception to describe a user
 * authentication/authorisation error. The user should not get back much
 * information.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public class AuthException extends SLCSException {

    private static final long serialVersionUID= -6501759181083992945L;

    public AuthException() {
        super();
    }

    public AuthException(String arg0) {
        super(arg0);
    }

    public AuthException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public AuthException(Throwable arg0) {
        super(arg0);
    }

}
