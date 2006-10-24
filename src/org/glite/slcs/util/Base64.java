/*
 * $Id: Base64.java,v 1.2 2006/10/24 08:55:52 vtschopp Exp $
 * 
 * Created on May 30, 2006 by tschopp
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs.util;

/** 
 * Base64 is a wrapper class for the commons-codec apache library.
 *
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.2 $
 */
public class Base64 {

    /** block length in characters to break line */
    static private int BLOCK_LENGTH= 64;

    /** Line breaks */
    static private String BLOCK_SEPARTOR= "\r\n";

    /**
     * BASE64 encodes the given bytes array. 64 chars per line.
     * 
     * @param bytes
     *            The bytes array to encode BASE64.
     * @return The BASE64 string.
     */
    public static String encode(byte[] bytes) {
        return encode(bytes, BLOCK_LENGTH);
    }

    /**
     * BASE64 encodes the given byte array.
     * 
     * @param bytes
     *            The byte array to encode.
     * @return The BASE64 encoded byte array.
     */
    public static byte[] byteEncode(byte[] bytes) {
        return org.apache.commons.codec.binary.Base64.encodeBase64(bytes);
    }

    /**
     * BASE64 encodes the given bytes array.
     * 
     * @param bytes
     *            The bytes array to encode.
     * @param blockLength
     *            The line length of the blocks.
     * @return The resulting BASE64 string.
     */
    public static String encode(byte[] bytes, int blockLength) {
        StringBuffer sb= new StringBuffer();
        byte[] b64Bytes= org.apache.commons.codec.binary.Base64.encodeBase64(bytes);
        for (int i= 0; i < b64Bytes.length; i++) {
            if (i > 0 && i % blockLength == 0) {
                sb.append(BLOCK_SEPARTOR);
            }
            byte b= b64Bytes[i];
            sb.append((char) b);
        }
        return sb.toString();

    }

    /**
     * Decodes the given BASE64 encoded string.
     * 
     * @param b64
     *            The BASE64 encoded string to decode.
     * @return The decoded bytes array.
     */
    public static byte[] decode(String b64) {
        byte[] b64Bytes= b64.getBytes();
        return org.apache.commons.codec.binary.Base64.decodeBase64(b64Bytes);
    }

    /**
     * Decodes the given BASE64 encoded byte array.
     * 
     * @param b64Bytes
     *            The BASE64 encoded byte array to decode.
     * @return The decoded byte array.
     */
    public static byte[] decode(byte[] b64Bytes) {
        return org.apache.commons.codec.binary.Base64.decodeBase64(b64Bytes);
    }

    /**
     * Prevents the instantiation of the class (utility pattern)
     */
    private Base64() {}
}
