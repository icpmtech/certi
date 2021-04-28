/*
 * $Id: HTMLEscapeAndNL2BR.java,v 1.2 2009/07/15 17:56:35 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/07/15 17:56:35 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.presentation.util;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Util class to escape a string to make it safe to show in a JSP and replaces the \n to <br>
 *
 * @author pjfsilva
 */
public class HTMLEscapeAndNL2BR {

    public static String replaceAndEscape(String string) {
        if (string != null) {
            string = StringEscapeUtils.escapeXml(string);
            return string.replaceAll("\\n", "<br/>");
        }
        return null;
    }

    public static String replace(String text) {
        if (text != null) {
            return text.replaceAll("\\n", "<br/>");
        }
        return null;
    }
}