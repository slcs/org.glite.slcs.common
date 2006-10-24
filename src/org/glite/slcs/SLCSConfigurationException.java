/*
 * $Id: SLCSConfigurationException.java,v 1.2 2006/10/24 08:37:24 vtschopp Exp $
 * 
 * Created on Aug 6, 2006 by tschopp
 *
 * Copyright (c) 2004. Members of the EGEE Collaboration. http://www.eu-egee.org
 */
package org.glite.slcs;

/**
 * SLCSConfigurationException is used for configuration errors.
 *
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.2 $
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
