/*
 * $Id: Utils.java,v 1.5 2010/12/30 18:43:55 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/12/30 18:43:55 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.security.MessageDigest;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.entities.Permission;
import com.criticalsoftware.certitools.entities.UserContract;

/**
 * Application util class
 *
 * @author jp-gomes
 */
public class Utils {

    /**
     * Encode URI
     *
     * @param text - text to encode
     *
     * @return - encoded string
     *
     * @throws UnsupportedEncodingException - error
     */
    public static String encodeURI(String text) throws UnsupportedEncodingException {
        return URLEncoder.encode(text, "UTF-8");
    }

    /**
     * Decode URI
     *
     * @param text - text to decode
     *
     * @return - text decoded
     *
     * @throws UnsupportedEncodingException - error
     */
    public static String decodeURI(String text) throws UnsupportedEncodingException {
        return URLDecoder.decode(text, "UTF-8");
    }

    /**
     * Remove accented chars from String
     *
     * @param s - string to remove accents
     *
     * @return - string with no accents
     */
    public static String removeAccentedChars(String s) {
        s = s.replaceAll("[����]", "e");
        s = s.replaceAll("[����]", "e");
        s = s.replaceAll("[��]", "u");
        s = s.replaceAll("[��]", "i");
        s = s.replaceAll("[��]", "a");
        s = s.replaceAll("�", "o");
        s = s.replaceAll("�", "c");
        s = s.replaceAll("�", "C");
        s = s.replaceAll("[����]", "a");
        s = s.replaceAll("[���]", "e");
        s = s.replaceAll("[���]", "i");
        s = s.replaceAll("[����]", "o");
        s = s.replaceAll("[���]", "u");
        s = s.replaceAll("[����]", "E");
        s = s.replaceAll("[��]", "U");
        s = s.replaceAll("[��]", "I");
        s = s.replaceAll("[��]", "A");
        s = s.replaceAll("�", "O");
        s = s.replaceAll("[����]", "A");
        s = s.replaceAll("[���]", "E");
        s = s.replaceAll("[���]", "I");
        s = s.replaceAll("[����]", "O");
        s = s.replaceAll("[���]", "U");
        return s;
    }

    /**
     * Strip tags from string
     *
     * @param HTMLString - string to strip
     *
     * @return - string
     */
    public static String stripTags(String HTMLString) {
        return HTMLString.replaceAll("\\<.*?>", "");
    }

    /**
     * Encrypts a string using MD5
     *
     * @param str the string to be encrypted.
     *
     * @return an MD5 representation of the string.
     *
     * @throws BusinessException
     *          throws an exception
     */

    public static String encryptMD5(String str) throws BusinessException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            BigInteger hash = new BigInteger(1, md.digest(str.getBytes("UTF-8")));
            String s = hash.toString(16);
            if (s.length() % 2 != 0) {
                s = "0" + s;
            }
            return s;

        } catch (Exception e) {
            throw new BusinessException("Error while encrypting password", e);
        }
    }

    public static boolean isUserClientPlanManager(UserContract userContract) {
        if (userContract == null) {
            return false;
        }

        // check if user is client pei manager
        for (Permission permission : userContract.getPermissions()) {
            if (permission.getName().equals(ConfigurationProperties.PERMISSION_PEI_MANAGER.getKey())) {
                return true;
            }
        }
        return false;
    }
}
