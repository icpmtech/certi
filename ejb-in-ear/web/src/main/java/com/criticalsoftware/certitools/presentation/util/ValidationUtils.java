/*
 * $Id: ValidationUtils.java,v 1.12 2011/06/21 16:14:46 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2011/06/21 16:14:46 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Validation Utils
 *
 * @author jp-gomes
 */
public class ValidationUtils {

    private static List<String> imageFormats = Arrays.asList(PlanUtils.IMAGE_MEDIA_TYPES);

    public static Boolean validateCSVFileFormat(String contentType) {
        return contentType.equals("text/csv") || contentType.equals("application/vnd.ms-excel");
    }

    /**
     * Validate Image ContentType
     *
     * @param contentType - Image content type to validate
     * @return - true if it is a valid content type for an image
     */
    public static Boolean validateImageContentType(String contentType) {
        return imageFormats.contains(contentType);
    }

    /**
     * Validate Image Size
     *
     * @param imageSize    - in bytes
     * @param imageMaxSize - in bytes
     * @return - true is image not exceeds max allowed
     */
    public static Boolean validateImageSize(Long imageSize, Long imageMaxSize) {
        return imageSize <= imageMaxSize;
    }

    /**
     * Validate Forder name (path)
     *
     * @param folderName - forder name to validate
     * @return - null if there are no errors ; characters inserted that trigger the error ex: ',', "["...
     */
    public static String validateFolderPathName(String folderName) {

        StringBuilder sb = new StringBuilder();

//        if (folderName.contains(".")) {
//            sb.append("'.', ");
//        }

        if (folderName.contains("/")) {
            sb.append("<strong>/</strong>, ");
        }

        if (folderName.contains("+")) {
            sb.append("<strong>+</strong>, ");
        }

        if (folderName.contains(":")) {
            sb.append("<strong>:</strong>, ");
        }

        if (folderName.contains("[")) {
            sb.append("<strong>[</strong>, ");
        }

        if (folderName.contains("]")) {
            sb.append("<strong>]</strong>, ");
        }

        if (folderName.contains("*")) {
            sb.append("<strong>*</strong>, ");
        }

        if (folderName.contains("'")) {
            sb.append("<strong>'</strong>, ");
        }

        if (folderName.contains("\"")) {
            sb.append("<strong>\"</strong>, ");
        }

        if (folderName.contains("|")) {
            sb.append("<strong>|</strong>, ");
        }
        if (folderName.contains("%")) {
            sb.append("<strong>%</strong>, ");
        }
        if (folderName.contains("\\")) {
            sb.append("<strong>\\</strong>, ");
        }
        if (folderName.contains("&")) {
            sb.append("<strong>&</strong>, ");
        }
        /*if (folderName.contains("?")) {
            sb.append("<strong>?</strong>, ");
        }*/
        if (folderName.contains("#")) {
            sb.append("<strong>#</strong>, ");
        }

        if (sb.length() == 0) {
            return null;

        } else {
            return sb.substring(0, sb.length() - 2);
        }
    }

    public static String validateFolderPathStartName(String folderName) {

        StringBuilder sb = new StringBuilder();

        if (folderName.startsWith(".") && folderName.length() == 1) {
            sb.append("<strong>.</strong>, ");
        }

        if (sb.length() == 0) {
            return null;

        } else {
            return sb.substring(0, sb.length() - 2);
        }
    }

    public static boolean validateNavigationLetter(String letter) {
        String[] alphabet = {"#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
                "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

        if (StringUtils.isEmpty(letter) || letter.equalsIgnoreCase("ALL")){
            return true;
        }

        for (String alphabetLetter : alphabet) {
            if (letter.equalsIgnoreCase(alphabetLetter)){
                return true;
            }
        }

        return false;
    }
}
