/*
 * $Id: SLCSConfigurationException.java,v 1.3 2006/10/24 08:40:54 vtschopp Exp $
 * 
 * Created on Aug 6, 2006 by tschopp
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs;

/**
 * SLCSConfigurationException is used for configuration errors.
 *
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.3 $
 */
public class SLCSConfigurationException extends SLCSException {

    private static final long serialVersionUID= -3477638825210342371L;

    public SLCSConfigurationException() {
        super();
    }

    public SLCSConfigurationException(String arg0) {
        super(arg0);
    }

    public SLCSConfigurationException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public SLCSConfigurationException(Throwable arg0) {
        super(arg0);
    }

}
