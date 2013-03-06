/*
 * $Id: SLCSException.java,v 1.3 2006/10/24 08:40:54 vtschopp Exp $
 * 
 * Created on Jul 6, 2006 by tschopp
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs;

/** 
 * SLCSException is the generic Exception for the SLCS system.
 *
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.3 $
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
