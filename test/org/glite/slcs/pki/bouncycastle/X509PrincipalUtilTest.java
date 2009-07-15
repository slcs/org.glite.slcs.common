package org.glite.slcs.pki.bouncycastle;

import java.security.GeneralSecurityException;

import javax.security.auth.x500.X500Principal;

import junit.framework.TestCase;

import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.jce.X509Principal;

public class X509PrincipalUtilTest extends TestCase {

    private X509PrincipalUtil x509PrincipalUtil_ = new X509PrincipalUtil();

    public void testRDN() throws GeneralSecurityException {
        String dn = "CN=C+CN=A+CN=B,O=SWITCH,C=CH";
        System.out.println("DN: " + dn);
        X509Principal expected = new X509Principal(dn);
        System.out.println("BC X509Principal: " + expected);
        System.out.println("BC ASN1: " + ASN1Dump.dumpAsString(expected));

        X509Principal p = x509PrincipalUtil_.createX509Principal(dn);
        System.out.println("my X509Principal: " + p);
        System.out.println("my ASN1: " + ASN1Dump.dumpAsString(p));

        assertEquals(expected, p);
        assertEquals(expected.getDERObject(), p.getDERObject());
    }

    public void testEscapedPlus() throws GeneralSecurityException {
        String expected = "CN=Foo\\+Bar,O=SWITCH,C=CH";

        X509Principal p = x509PrincipalUtil_.createX509Principal(expected);
        System.out.println("my X509Principal: " + p);
        System.out.println("my ASN1: " + ASN1Dump.dumpAsString(p));

        assertEquals(expected, p.getName());
    }

    public void testMixed() throws GeneralSecurityException {
        String dn = "DC=Hello\\; World!,CN=Foo\\+Bar,O=A+O=B+O=C,O=Test+OU=Java,C=CH";

        X509Principal p = x509PrincipalUtil_.createX509Principal(dn);
        System.out.println("my X509Principal: " + p);
        System.out.println("my ASN1: " + ASN1Dump.dumpAsString(p));

        assertEquals(dn, p.getName());

    }

    public void testBouncyVsMy() throws GeneralSecurityException {
        String dn = "DC=A+DC=B+DC=E,DC=JUnitTest,CN=X+CN=Y+CN=Y+DC=Z+O=AU";
        // String certificateSubject_ =" C=AU, ST=VIC, L=Mel\\,Bourne,
        // O=Mon\\+nash, OU=Ar\\+cher,
        // CN=SL\\+CS/emailAddress=xthnguyen@yahoo.com";
        // String certificateSubject_= "C=AU, ST=VIC, L=Melbourne, O=Monash,
        // OU=Archer, CN=slcs/emailAddress=xthnguyen@yahoo.com";
        X509Principal p = x509PrincipalUtil_.createX509Principal(dn);
        X509Principal bcp = new X509Principal(dn);
        assertEquals(bcp, p);

        System.out.println("BC ASN1: " + ASN1Dump.dumpAsString(bcp));
        System.out.println("my ASN1: " + ASN1Dump.dumpAsString(p));

        assertEquals(bcp.toASN1Object(), p.toASN1Object());
    }

    public void testEscapeBackslash() throws GeneralSecurityException {
        String subject = "DC=demo,DC=mams,DC=slcs,O=MAMS,CN=Dummy\\+\\;\\=aghf";
        System.out.println("Subject: " + subject);
        // String expected =
        // "DC=demo,DC=mams,DC=slcs,O=MAMS,CN=Dummy\\+\\;aghf=";
        X509Principal p = x509PrincipalUtil_.createX509Principal(subject);
        System.out.println("X509Principal: " + p.getName());
        System.out.println(ASN1Dump.dumpAsString(p));
        assertEquals(subject, new X500Principal(p.getName()).getName());
    }

    public void testEscapeDoubleQuoute() throws GeneralSecurityException {
        String subject = "DC=demo,DC=mams,DC=slcs,O=MAMS,CN=\"Dummy+;aghf=\"";
        System.out.println("Subject: " + subject);
        String expected = "DC=demo,DC=mams,DC=slcs,O=MAMS,CN=Dummy\\+\\;aghf\\=";
        System.out.println("Expected: " + expected);
        X509Principal p = x509PrincipalUtil_.createX509Principal(subject);
        System.out.println("X509Principal: " + p.getName());     
        System.out.println(ASN1Dump.dumpAsString(p));

        // convert to X500
        X500Principal x500= new X500Principal(p.getName());
        System.out.println("X500Principal(default): " + x500.getName());     
        System.out.println("X500Principal(CANONICAL): " + x500.getName(X500Principal.CANONICAL));     
        System.out.println("X500Principal(RFC1779): " + x500.getName(X500Principal.RFC1779));     
        System.out.println("X500Principal(RFC2253): " + x500.getName(X500Principal.RFC2253));     
        
        assertEquals(expected, x500.getName());
    }

    public void testX500PrincipalVsX509PrincipalQuoted() {
        String subject = "DC=test+CN=\"Test=Equal\"";
        System.out.println("Subject: " + subject);
        X500Principal x500= new X500Principal(subject);
        System.out.println("X500Principal: " + x500.getName());
        X509Principal x509= new X509Principal(subject);
        System.out.println("X509Principal: " + x509.getName());
        assertEquals(x500.getName(), x509.getName());
    }

    public void testX500PrincipalVsX509PrincipalEscaped() {
        String subject = "DC=test,CN=Test\\=Equal";
        System.out.println("Subject: " + subject);
        X500Principal x500= new X500Principal(subject);
        System.out.println("X500Principal: " + x500.getName());
        X509Principal x509= new X509Principal(subject);
        System.out.println("X509Principal: " + x509.getName());
        assertEquals(x500.getName(), x509.getName());
    }

    public void testX509PrincipalToX500Principal() {
        String subject = "DC=test,CN=Test\\=Equal";
        System.out.println("Subject: " + subject);
        X509Principal x509= new X509Principal(subject);
        System.out.println("X509Principal: " + x509.getName());
        X500Principal x500= new X500Principal(x509.getName());
        System.out.println("X500Principal(X509Principal): " + x500.getName());
        assertEquals(x500.getName(), x509.getName());
    }

    
    public void testFailure() {
        String subject = "DC=CH,hello";
        try {
            X509Principal p = x509PrincipalUtil_.createX509Principal(subject);
            fail("This should failed: " + p.getName());
        } catch (GeneralSecurityException e) {
            System.out.println("Expected exception: " + e);
        }
    }
    
    public void testSWITCHDN() throws GeneralSecurityException {
        String subject= "DC=ch+DC=switch+DC=slcs,O=Switch - Teleinformatikdienste fuer Lehre und Forschung,CN=Valery Tschopp\\+9FEE5EE3";
        X509Principal p = x509PrincipalUtil_.createX509Principal(subject);
        System.out.println(p.getName());
        System.out.println(ASN1Dump.dumpAsString(p));
    }

    protected void setUp() throws Exception {
        super.setUp();
        
    }
}
