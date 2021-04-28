/*
 * $Id: Language.java,v 1.2 2009/03/18 14:50:12 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/03/18 14:50:12 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.util;

/**
 * Enumeration containing Application Languages
 *
 * @author jp-gomes
 */

public enum Language {
    PT("PT"),
    EN("EN");

    private final String language;

    Language(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public static String getDefaultLanguage() {
        return Configuration.getInstance().getDefaultLanguage();
    }
}
