/*
 * $Id: RemoteException.java,v 1.1 2006/09/21 12:42:52 vtschopp Exp $
 * 
 * Created on Jul 17, 2006 by tschopp
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs;

/**
 * RemoteException is the EGEE exception to describe a problem to contact the
 * service. You might want to try again.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
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
