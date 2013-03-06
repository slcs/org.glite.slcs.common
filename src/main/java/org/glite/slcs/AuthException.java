/*
 * $Id: AuthException.java,v 1.3 2006/10/24 08:40:54 vtschopp Exp $
 * 
 * Created on Jul 17, 2006 by tschopp
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs;

/**
 * AuthException is the EGEE exception to describe a user
 * authentication/authorisation error. The user should not get back much
 * information.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.3 $
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
