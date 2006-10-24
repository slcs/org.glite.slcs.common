/*
 * $Id: SLCSException.java,v 1.2 2006/10/24 08:37:24 vtschopp Exp $
 * 
 * Created on Jul 6, 2006 by tschopp
 *
 * Copyright (c) 2004. Members of the EGEE Collaboration. http://www.eu-egee.org
 */
package org.glite.slcs;

/** 
 * SLCSException is the generic Exception for the SLCS system.
 *
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.2 $
 */
public class SLCSException extends Exception {

    /**  */
    private static final long serialVersionUID= -8940087455900649750L;

    /**
     * 
     */
    public SLCSException() {
        super();
    }

    /**
     * @param arg0
     */
    public SLCSException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public SLCSException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * @param arg0
     */
    public SLCSException(Throwable arg0) {
        super(arg0);
    }

}
