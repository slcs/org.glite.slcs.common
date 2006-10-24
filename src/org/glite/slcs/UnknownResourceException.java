/*
 * $Id: UnknownResourceException.java,v 1.3 2006/10/24 08:40:54 vtschopp Exp $
 * 
 * Created on Jul 21, 2006 by tschopp
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs;

/**
 * UnknownResourceException is the EGEE exception to describe a problem with the
 * resource. Though you want to wait a little while first or limit the number of
 * attempts.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.3 $
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
