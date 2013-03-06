/**
 * $Id: UtilsTest.java,v 1.1 2009/01/15 11:57:37 vtschopp Exp $
 */
package org.glite.slcs.util;

import junit.framework.TestCase;

/**
 * Tests some of the Utils methods.
 * 
 * @author tschopp
 */
public class UtilsTest extends TestCase {

    public void testEscapeRFC2253() {
        String dnValue= "A+B=C;<D>,E#";
        String expectedValue= "A\\+B\\=C\\;\\<D\\>\\,E\\#"; 
        String escapedValue= Utils.escapeAttributeValue(dnValue);
        System.out.println("dnValue: " + dnValue);
        System.out.println("escapedValue: " + escapedValue);
        assertEquals(expectedValue, escapedValue);
    }
    
}
