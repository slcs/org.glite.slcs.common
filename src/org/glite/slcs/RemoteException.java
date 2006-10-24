/*
 * $Id: RemoteException.java,v 1.3 2006/10/24 08:40:54 vtschopp Exp $
 * 
 * Created on Jul 17, 2006 by tschopp
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs;

/**
 * RemoteException is the EGEE exception to describe a problem to contact the
 * service. You might want to try again.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.3 $
 */
public class RemoteException extends SLCSException {

    private static final long serialVersionUID= 7059760709078114153L;

    public RemoteException() {
        super();
    }

    public RemoteException(String arg0) {
        super(arg0);
    }

    public RemoteException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public RemoteException(Throwable arg0) {
        super(arg0);
    }

}
