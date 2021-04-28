/*
 * $Id: CertitoolsLocalizationBundleFactory.java,v 1.1 2009/03/09 15:29:57 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/03/09 15:29:57 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util;

import net.sourceforge.stripes.config.Configuration;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Locale;

import net.sourceforge.stripes.localization.LocalizationBundleFactory;

/**
 * @author : jp-gomes
 * @version : $version $
 */
public class CertitoolsLocalizationBundleFactory implements LocalizationBundleFactory {

    public ResourceBundle getErrorMessageBundle(Locale locale) throws MissingResourceException {
        return ResourceBundle.getBundle("StripesResources", locale);
    }

    public ResourceBundle getFormFieldBundle(Locale locale) throws MissingResourceException {
        return ResourceBundle.getBundle("StripesResources", locale);
    }

    public void init(Configuration configuration) throws Exception {
    }
}
