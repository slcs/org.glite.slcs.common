/*
 * $Id: X509PrincipalUtil.java,v 1.2 2008/07/01 12:28:01 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs.pki.bouncycastle;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Enumeration;
import java.util.Vector;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.x509.X509DefaultEntryConverter;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.asn1.x509.X509NameEntryConverter;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.util.Strings;

/**
 * Utility class to handle correctly the creation of {@link X509Principal}. The
 * BouncyCastle library (version <= 1.39) doesn't handle correctly escaped
 * literal characters (+, =, ...) in the Principal name.
 * <p>
 * Bug report in BouncyCastle JIRA: <a
 * href="http://www.bouncycastle.org/jira/browse/BJA-119">http://www.bouncycastle.org/jira/browse/BJA-119</a>
 * <p>
 * Usage:
 * <pre>
 * X509PrincipalUtil util = new X509PrincipalUtil();
 * X509Principal p = util.createX509Principal(&quot;CN=Foo\\+Bar,O=SWITCH+O=MAMS,C=CH+C=AU&quot;);
 * </pre>
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @author Xuan Thang Nguyen &lt;xuan.nguyen@its.monash.edu.au&gt;
 * @version $Revision: 1.2 $
 */
public class X509PrincipalUtil {

    /** Logger */
    private static Log LOG = LogFactory.getLog(X509PrincipalUtil.class);

    /** Start to handle + */
    private Boolean start_ = false;

    /**
     * Creates a {@link X509Principal} with the given name.
     * <p>
     * In the <code>name</code> the RDNs, like <code>CN=B+CN=A</code>, will
     * be sorted alphabetically. Literal characters like <code>+</code>,
     * <code>=</code> must be escaped.
     * 
     * @param name
     *            The {@link X509Principal} name.
     * @return the {@link X509Principal}.
     * @throws GeneralSecurityException
     *             if an error occurs.
     */
    public X509Principal createX509Principal(String name)
            throws GeneralSecurityException {
        Vector<DERObjectIdentifier> oids = new Vector<DERObjectIdentifier>();
        Vector<Object> values = new Vector<Object>();
        Vector<Boolean> added = new Vector<Boolean>();
        start_ = false;
        try {
            LdapName ldapName = new LdapName(name);
            LOG.debug("RDNs: " + ldapName.getRdns());
            Rdn[] rdnArray = new Rdn[ldapName.getRdns().size()];
            ldapName.getRdns().toArray(rdnArray);
            for (int i = rdnArray.length - 1; i >= 0; i--) {
                readRdn(rdnArray[i], oids, values, added);
                start_ = false;
            }
            X509Principal principal = buildX509Principal(oids, values, added);
            return principal;
        } catch (Exception e) {
            // NamingException or IOException
            LOG.error("Fail to create X509Principal(" + name + ")", e);
            throw new GeneralSecurityException("Fail to create X509Principal("
                    + name + "): " + e.getMessage(), e);
        }
    }

    /**
     * Reads the LdapName {@link Rdn} component and fills the given vectors.
     * 
     * @param rdn
     *            The {@link Rdn} to read.
     * @param oids
     *            The vector of OID.
     * @param values
     *            The vector of value.
     * @param added
     *            The added status vector.
     * @throws NamingException
     *             if an error occurs.
     */
    private void readRdn(Rdn rdn, Vector<DERObjectIdentifier> oids,
            Vector<Object> values, Vector<Boolean> added)
            throws NamingException {
        LOG.debug("RDN: " + rdn);
        Enumeration<? extends Attribute> attrs = rdn.toAttributes().getAll();
        do {
            if (attrs.hasMoreElements()) {
                Attribute attr = attrs.nextElement();
                readAttr(attr, oids, values, added);
                start_ = true;
            }
        } while (attrs.hasMoreElements());
    }

    /**
     * Reads the given {@link Attribute} and recurses into RDN attributes, fills
     * the given vectors.
     * 
     * @param attr
     *            The {@link Attribute} to read.
     * @param oids
     *            The vector of OID.
     * @param values
     *            The vector of value.
     * @param added
     *            The added status vector.
     * @throws NamingException
     *             if a naming error occurs.
     */
    private void readAttr(Attribute attr, Vector<DERObjectIdentifier> oids,
            Vector<Object> values, Vector<Boolean> added)
            throws NamingException {
        // Recursively looking into each attribute
        LOG.debug("Attribute: " + attr);
        for (int i = 0; i < attr.size(); i++) {
            if (attr.get(i) instanceof Attribute) {
                Attribute rdnAttr = (Attribute) attr.get(i);
                LOG.debug("Attribute RDN: " + rdnAttr);
                readAttr(rdnAttr, oids, values, added);
            }
            else { // Get back the OID from name
                DERObjectIdentifier oid = (DERObjectIdentifier) X509Name.DefaultLookUp.get(Strings.toLowerCase(attr.getID()));
                oids.add(oid);
                Object attrValue = attr.get(i);
                LOG.debug("Attribute value: " + attrValue);
                values.add(attrValue);
                added.add(start_);
                start_ = true;

            }
        }

    }

    /**
     * Builds a {@link X509Principal}, based on the given vectors.
     * 
     * @param ordering
     * @param values
     * @param added
     * @return the {@link X509Principal} or <code>null</code> if an error
     *         occurs.
     * @throws IOException
     *             if a DER encoding error occurs.
     */
    private X509Principal buildX509Principal(
            Vector<DERObjectIdentifier> ordering, Vector<Object> values,
            Vector<Boolean> added) throws IOException {
        X509NameEntryConverter converter = new X509DefaultEntryConverter();
        ASN1EncodableVector vec = new ASN1EncodableVector();
        ASN1EncodableVector sVec = new ASN1EncodableVector();
        DERObjectIdentifier lstOid = null;
        // Bouncycastle's code
        for (int i = 0; i != ordering.size(); i++) {
            ASN1EncodableVector v = new ASN1EncodableVector();
            DERObjectIdentifier oid = ordering.elementAt(i);
            v.add(oid);
            String str = (String) values.elementAt(i);
            v.add(converter.getConvertedValue(oid, str));
            if (lstOid == null || added.elementAt(i)) {
                sVec.add(new DERSequence(v));
            }
            else {
                vec.add(new DERSet(sVec));
                sVec = new ASN1EncodableVector();
                sVec.add(new DERSequence(v));
            }

            lstOid = oid;
        }
        vec.add(new DERSet(sVec));
        DERSequence seq = new DERSequence(vec);
        byte[] bytes = seq.getDEREncoded();
        return new X509Principal(bytes);
    }

}
