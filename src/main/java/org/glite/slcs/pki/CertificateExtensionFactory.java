/*
 * Copyright (c) 2010-2013 SWITCH
 * Copyright (c) 2006-2010 Members of the EGEE Collaboration
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glite.slcs.pki;

import java.util.StringTokenizer;
import java.util.Vector;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.asn1.x509.X509Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * X.509 certificate extensions factory
 *
 * @author Valery Tschopp <tschopp@switch.ch>
 */
public class CertificateExtensionFactory {

    /** Logging */
    private static Logger LOG= LoggerFactory.getLogger(CertificateExtensionFactory.class);

    /**
     * Creates a CertificateExtension. The id can be the OID or the name as
     * defined below. The values is a comma separated list of value(s)
     * <p>
     * Valid names and values:
     * <ul>
     * <li>KeyUsage
     * <ul>
     * <li>DigitalSignature
     * <li>NonRepudiation
     * <li>KeyEncipherment
     * <li>DataEncipherment
     * <li>KeyAgreement
     * <li>KeyCertSign
     * <li>CRLSign
     * <li>EncipherOnly
     * <li>DecipherOnly
     * </ul>
     * <li>ExtendedKeyUsage
     * <ul>
     * <li>AnyExtendedKeyUsage
     * <li>ServerAuth
     * <li>ClientAuth
     * <li>CodeSigning
     * <li>EmailProtection
     * <li>IPSecEndSystem
     * <li>IPSecTunnel
     * <li>IPSecUser
     * <li>OCSPSigning
     * <li>Smartcardlogon
     * </ul>
     * <li>CertificatePolicies
     * <ul>
     * <li>The policy OID(s)
     * </ul>
     * <li>SubjectAltName
     * <ul>
     * <li>email:EMAIL_ADDRESS
     * <li>dns:HOSTNAME
     * </ul>
     * </ul>
     * <p>
     * Example:
     * <pre>
     * CertificateExtension keyUsageExtension = 
     *       CertificateExtensionFactory.createCertificateExtension("KeyUsage", "DigitalSignature,KeyEncipherment");
     * CertificateExtension subjectAltNameExtension = 
     *       CertificateExtensionFactory.createCertificateExtension("SubjectAltName", "email:john.doe@example.com,dns:www.exmaple.com");
     * </pre>
     * 
     * @param id
     *            The name or the OID of the extension.
     * @param values
     *            A comma separated list of extension value(s).
     * @return The corresponding CertificateExtension or <code>null</code> if
     *         the id (name or oid) is not supported.
     */
    static public CertificateExtension createCertificateExtension(String id,
            String values) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("id:" + id + " value(s):" + values);
        }
        if (id.equals(X509Extension.keyUsage.getId())
                || id.equalsIgnoreCase("KeyUsage")) {
            // parse the comma separated list of key usage
            int usage= 0;
            StringTokenizer st= new StringTokenizer(values, ",");
            while (st.hasMoreElements()) {
                String keyUsage= (String) st.nextElement();
                keyUsage= keyUsage.trim();

                if (keyUsage.equalsIgnoreCase("DigitalSignature")) {
                    usage+= KeyUsage.digitalSignature;
                }
                else if (keyUsage.equalsIgnoreCase("NonRepudiation")) {
                    usage+= KeyUsage.nonRepudiation;
                }
                else if (keyUsage.equalsIgnoreCase("KeyEncipherment")) {
                    usage+= KeyUsage.keyEncipherment;
                }
                else if (keyUsage.equalsIgnoreCase("DataEncipherment")) {
                    usage+= KeyUsage.dataEncipherment;
                }
                else if (keyUsage.equalsIgnoreCase("KeyAgreement")) {
                    usage+= KeyUsage.keyAgreement;
                }
                else if (keyUsage.equalsIgnoreCase("KeyCertSign")) {
                    usage+= KeyUsage.keyCertSign;
                }
                else if (keyUsage.equalsIgnoreCase("CRLSign")) {
                    usage+= KeyUsage.cRLSign;
                }
                else if (keyUsage.equalsIgnoreCase("EncipherOnly")) {
                    usage+= KeyUsage.encipherOnly;
                }
                else if (keyUsage.equalsIgnoreCase("DecipherOnly")) {
                    usage+= KeyUsage.decipherOnly;
                }
                else {
                    LOG.error("Unknown KeyUsage: " + keyUsage);
                }

            }
            return createKeyUsageExtension(usage, values);
        }
        else if (id.equals(X509Extension.extendedKeyUsage.getId())
                || id.equalsIgnoreCase("ExtendedKeyUsage")) {
            // value is a comma separated list of keyPurpose
            Vector<DERObjectIdentifier> keyPurposeIds= new Vector<DERObjectIdentifier>();
            StringTokenizer st= new StringTokenizer(values, ",");
            while (st.hasMoreElements()) {
                String keyPurpose= (String) st.nextElement();
                keyPurpose= keyPurpose.trim();
                if (keyPurpose.equalsIgnoreCase("AnyExtendedKeyUsage")) {
                    keyPurposeIds.add(KeyPurposeId.anyExtendedKeyUsage);
                }
                else if (keyPurpose.equalsIgnoreCase("ServerAuth")) {
                    keyPurposeIds.add(KeyPurposeId.id_kp_serverAuth);
                }
                else if (keyPurpose.equalsIgnoreCase("ClientAuth")) {
                    keyPurposeIds.add(KeyPurposeId.id_kp_clientAuth);
                }
                else if (keyPurpose.equalsIgnoreCase("CodeSigning")) {
                    keyPurposeIds.add(KeyPurposeId.id_kp_codeSigning);
                }
                else if (keyPurpose.equalsIgnoreCase("EmailProtection")) {
                    keyPurposeIds.add(KeyPurposeId.id_kp_emailProtection);
                }
                else if (keyPurpose.equalsIgnoreCase("IPSecEndSystem")) {
                    keyPurposeIds.add(KeyPurposeId.id_kp_ipsecEndSystem);
                }
                else if (keyPurpose.equalsIgnoreCase("IPSecTunnel")) {
                    keyPurposeIds.add(KeyPurposeId.id_kp_ipsecTunnel);
                }
                else if (keyPurpose.equalsIgnoreCase("IPSecUser")) {
                    keyPurposeIds.add(KeyPurposeId.id_kp_ipsecUser);
                }
                else if (keyPurpose.equalsIgnoreCase("TimeStamping")) {
                    keyPurposeIds.add(KeyPurposeId.id_kp_timeStamping);
                }
                else if (keyPurpose.equalsIgnoreCase("OCSPSigning")) {
                    keyPurposeIds.add(KeyPurposeId.id_kp_OCSPSigning);
                }
                else if (keyPurpose.equalsIgnoreCase("Smartcardlogon")) {
                    keyPurposeIds.add(KeyPurposeId.id_kp_smartcardlogon);
                }
                else {
                    LOG.error("Unknown ExtendedKeyUsage: " + keyPurpose);
                }
            }
            return createExtendedKeyUsageExtension(keyPurposeIds, values);
        }
        else if (id.equals(X509Extension.certificatePolicies.getId())
                || id.equalsIgnoreCase("CertificatePolicies")) {
            // values is a comma separated list of policyOIDs
            Vector<String> policyOIDs= new Vector<String>();
            StringTokenizer st= new StringTokenizer(values, ",");
            while (st.hasMoreElements()) {
                String policyOID= (String) st.nextElement();
                policyOID= policyOID.trim();
                policyOIDs.add(policyOID);
            }
            return createCertificatePoliciesExtension(policyOIDs, values);
        }
        else if (id.equals(X509Extension.subjectAlternativeName.getId())
                || id.equalsIgnoreCase("SubjectAltName")) {
            // values is a comma separated list of altername names prefixed with
            // the type (email: or dns:)
            Vector<String> typedSubjectAltNames= new Vector<String>();
            StringTokenizer st= new StringTokenizer(values, ",");
            while (st.hasMoreElements()) {
                String typedAltName= (String) st.nextElement();
                typedAltName= typedAltName.trim();
                typedSubjectAltNames.add(typedAltName);
            }
            return createSubjectAltNameExtension(typedSubjectAltNames, values);
        }
        LOG.error("Unsupported CertificateExtension: " + id);
        return null;
    }

    /**
     * 
     * @param keyPurposeIds
     * @param keyPurposeNames
     * @return
     */
    static protected CertificateExtension createExtendedKeyUsageExtension(
            Vector<DERObjectIdentifier> keyPurposeIds, String keyPurposeNames) {
        ExtendedKeyUsage extendedKeyUsage= new ExtendedKeyUsage(keyPurposeIds);
        X509Extension extendedKeyUsageExtension= new X509Extension(false,
                                                                   new DEROctetString(extendedKeyUsage));
        return new CertificateExtension(X509Extension.extendedKeyUsage,
                                        "ExtendedKeyUsage",
                                        extendedKeyUsageExtension,
                                        keyPurposeNames);
    }

    /**
     * 
     * @param keyPurposeId
     * @param keyPurposeName
     * @return
     */
    static protected CertificateExtension createExtendedKeyUsageExtension(
            KeyPurposeId keyPurposeId, String keyPurposeName) {
        DERSequence keyPurposeIds= new DERSequence(keyPurposeId);
        ExtendedKeyUsage extendedKeyUsage= new ExtendedKeyUsage(keyPurposeIds);
        X509Extension extendedKeyUsageExtension= new X509Extension(false,
                                                                   new DEROctetString(extendedKeyUsage));
        return new CertificateExtension(X509Extension.extendedKeyUsage,
                                        "ExtendedKeyUsage",
                                        extendedKeyUsageExtension,
                                        keyPurposeName);
    }

    /**
     * Creates a RFC882 Subject Alternative Name: email:johndoe@example.com
     * extension.
     * 
     * @param emailAddress
     *            The email address to be included as alternative name.
     * @return The subject alternative name CertificateExtension.
     */
    static protected CertificateExtension createSubjectAltNameExtension(
            String emailAddress) {
        GeneralName subjectAltName= new GeneralName(GeneralName.rfc822Name,
                                                    emailAddress);
        GeneralNames subjectAltNames= new GeneralNames(subjectAltName);
        X509Extension subjectAltNameExtension= new X509Extension(false,
                                                                 new DEROctetString(subjectAltNames));
        return new CertificateExtension(X509Extension.subjectAlternativeName,
                                        "SubjectAltName",
                                        subjectAltNameExtension,
                                        emailAddress);

    }

    /**
     * 
     * @param prefixedAltNames
     * @param values
     * @return
     */
    static protected CertificateExtension createSubjectAltNameExtension(
            Vector<String> prefixedAltNames, String values) {
        ASN1EncodableVector altNames= new ASN1EncodableVector();
        for (String typeAndName : prefixedAltNames) {
            typeAndName= typeAndName.trim();
            if (typeAndName.startsWith("email:")) {
                String emailAddress= typeAndName.substring("email:".length());
                GeneralName altName= new GeneralName(GeneralName.rfc822Name,
                                                     emailAddress);
                altNames.add(altName);

            }
            else if (typeAndName.startsWith("dns:")) {
                String hostname= typeAndName.substring("dns:".length());
                GeneralName altName= new GeneralName(GeneralName.dNSName,
                                                     hostname);
                altNames.add(altName);
            }
            else {
                LOG.error("Unsupported subjectAltName: " + typeAndName);
            }
        }
        DERSequence subjectAltNames= new DERSequence(altNames);
        GeneralNames generalNames= new GeneralNames(subjectAltNames);
        X509Extension subjectAltNameExtension= new X509Extension(false,
                                                                 new DEROctetString(generalNames));
        return new CertificateExtension(X509Extension.subjectAlternativeName,
                                        "SubjectAltName",
                                        subjectAltNameExtension,
                                        values);

    }

    /**
     * Creates a Cerificate Policies: policyOID extension with the given policy
     * OID.
     * 
     * @param policyOID
     *            The policy OID (2.16.756.1.2.*)
     * @return The certificate policies CertificateExtension.
     */
    static protected CertificateExtension createCertificatePoliciesExtension(
            String policyOID) {
        DERObjectIdentifier policyIdentifier= new DERObjectIdentifier(policyOID);
        PolicyInformation policyInformation= new PolicyInformation(policyIdentifier);
        DERSequence certificatePolicies= new DERSequence(policyInformation);
        X509Extension certificatePoliciesExtension= new X509Extension(false,
                                                                      new DEROctetString(certificatePolicies));
        return new CertificateExtension(X509Extension.certificatePolicies,
                                        "CertificatePolicies",
                                        certificatePoliciesExtension,
                                        policyOID);
    }

    /**
     * 
     * @param policyOIDs
     * @param values
     * @return
     */
    static protected CertificateExtension createCertificatePoliciesExtension(
            Vector<String> policyOIDs, String values) {
        ASN1EncodableVector policyInformations= new ASN1EncodableVector();
        for (String policyOid : policyOIDs) {
        	DERObjectIdentifier policyIdentifier= new DERObjectIdentifier(policyOid);
            PolicyInformation policyInformation= new PolicyInformation(policyIdentifier);
            policyInformations.add(policyInformation);

        }
        DERSequence certificatePolicies= new DERSequence(policyInformations);
        X509Extension certificatePoliciesExtension= new X509Extension(false,
                                                                      new DEROctetString(certificatePolicies));
        return new CertificateExtension(X509Extension.certificatePolicies,
                                        "CertificatePolicies",
                                        certificatePoliciesExtension,
                                        values);
    }

    /**
     * Creates a Key Usage extension for the given usage. This extension is
     * critical.
     * 
     * @param usage
     *            The usage is the sum of all KeyUsage values.
     * @param value
     *            The formal value of the usage. Example:
     *            KeyEncipherment,DigitalSignature
     * @return The KeyUsage certificate extension.
     * @see org.bouncycastle.asn1.x509.KeyUsage
     */
    static protected CertificateExtension createKeyUsageExtension(int usage,
            String value) {
        KeyUsage keyUsage= new KeyUsage(usage);
        // KeyUsage is critical
        X509Extension keyUsageExtension= new X509Extension(true,
                                                           new DEROctetString(keyUsage));
        return new CertificateExtension(X509Extension.keyUsage,
                                        "KeyUsage",
                                        keyUsageExtension,
                                        value,
                                        true);
    }

    /**
     * Do not allow instantiation of the factory.
     */
    private CertificateExtensionFactory() {
    }

}
