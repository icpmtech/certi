/*
 * $Id: LocalePicker.java,v 1.3 2009/03/09 18:41:29 haraujo Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/03/09 18:41:29 $
 * Last changed by $Author: haraujo $
 */
package com.criticalsoftware.certitools.presentation.util;

import net.sourceforge.stripes.localization.DefaultLocalePicker;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

import com.criticalsoftware.certitools.util.Configuration;

/**
 * Filter that catch request and set locale settings
 *
 * @author jp-gomes
 */
public class LocalePicker extends DefaultLocalePicker {
    public Locale pickLocale(HttpServletRequest httpServletRequest) {
        Locale locale = (Locale) httpServletRequest.getSession().getAttribute("locale");
        if (locale == null) {
            locale = new Locale(Configuration.getInstance().getDefaultLanguage());
            httpServletRequest.getSession().setAttribute("locale", locale);
        }
        return locale;
    }
}