/*
 * $Id: RemoteException.java,v 1.2 2006/10/24 08:37:24 vtschopp Exp $
 * 
 * Created on Jul 17, 2006 by tschopp
 *
 * Copyright (c) 2004. Members of the EGEE Collaboration. http://www.eu-egee.org
 */
package org.glite.slcs;

/**
 * RemoteException is the EGEE exception to describe a problem to contact the
 * service. You might want to try again.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.2 $
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
