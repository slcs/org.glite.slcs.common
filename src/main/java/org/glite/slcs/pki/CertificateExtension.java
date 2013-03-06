/*
 * $Id: CertificateExtension.java,v 1.2 2006/10/24 08:54:01 vtschopp Exp $
 * 
 * Created on Aug 28, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs.pki;

import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.x509.X509Extension;

/**
 * CertificateExtension wrapper class to create certificate
 * extension.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 */
public class CertificateExtension {

    /** The X509 extension OID */
    private DERObjectIdentifier oid_= null;

    /** The X509 extension */
    private X509Extension extension_= null;

    /** Is the extension critical? */
    private boolean critical_= false;

    /** The extension name */
    private String name_= null;

    /** the extension value as a descriptive string */
    private String value_= null;

    /**
     * Creates an non-critical extension with the given extension OID and name
     * and X509Extension and value.
     * 
     * @param oid
     *            The OID of the extension.
     * @param name
     *            The formal name of the extension.
     * @param extension
     *            The X509Extension object.
     * @param value
     *            The formal value of the X509Extension.
     */
    protected CertificateExtension(DERObjectIdentifier oid, String name,
            X509Extension extension, String value) {
        this(oid, name, extension, value, false);
    }

    /**
     * Creates an extension with the given extension OID and X509Extension
     * object.
     * <p>
     * The name is set to the OID and the value is set to NotParsed.
     * 
     * @param oid
     *            The extension OID
     * @param extension
     *            The X509Extension object.
     * @param critical
     *            <code>true</code> iff the extension is critical.
     */
    protected CertificateExtension(DERObjectIdentifier oid,
            X509Extension extension, boolean critical) {
        this(oid, oid.getId(), extension, "NotParsed", critical);
    }

    /**
     * Creates a CertificateExtension with the given extension OID and name and
     * X509Extension and value.
     * 
     * @param oid
     *            The OID of the extension.
     * @param name
     *            The formal name of the extension.
     * @param extension
     *            The X509Extension object.
     * @param value
     *            The formal value of the X509Extension.
     * @param critical
     *            <code>true</code> iff the extension is critical.
     */
    protected CertificateExtension(DERObjectIdentifier oid, String name,
            X509Extension extension, String value, boolean critical) {
        this.oid_= oid;
        this.extension_= extension;
        this.critical_= critical;
        this.name_= name;
        this.value_= value;
    }

    /**
     * @return Returns the X.509 extension.
     */
    public X509Extension getExtension() {
        return this.extension_;
    }

    /**
     * @return Returns the oid.
     */
    public DERObjectIdentifier getOID() {
        return this.oid_;
    }

    /**
     * @return If the extension is critical or not.
     */
    public boolean isCritical() {
        return critical_;
    }
    
    /**
     * @return The extension name or OID.
     */
    public String getName() {
        return name_;
    }

    /**
     * @return the extension named value(s).
     */
    public String getValue() {
        return value_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final int PRIME= 31;
        int result= 1;
        result= PRIME * result + (critical_ ? 1231 : 1237);
        result= PRIME * result
                + ((extension_ == null) ? 0 : extension_.hashCode());
        result= PRIME * result + ((oid_ == null) ? 0 : oid_.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final CertificateExtension other= (CertificateExtension) obj;
        if (critical_ != other.critical_)
            return false;
        if (extension_ == null) {
            if (other.extension_ != null)
                return false;
        }
        else if (!extension_.equals(other.extension_))
            return false;
        if (oid_ == null) {
            if (other.oid_ != null)
                return false;
        }
        else if (!oid_.equals(other.oid_))
            return false;
        return true;
    }

    public String toXML() {
        StringBuffer sb= new StringBuffer();
        sb.append("<CertificateExtension");
        sb.append(" name=\"").append(name_).append('"');
        sb.append(" oid=\"").append(oid_).append('"');
        sb.append(" critical=\"").append(critical_).append('"');
        sb.append('>');
        sb.append(value_);
        sb.append("</CertificateExtension>");
        return sb.toString();
    }
}
