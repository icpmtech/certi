/*
 * $Id: PTDateTypeConverter.java,v 1.1 2009/04/01 11:48:54 lt-rico Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/04/01 11:48:54 $
 * Last changed by : $Author: lt-rico $
 */
package com.criticalsoftware.certitools.presentation.util;

import net.sourceforge.stripes.validation.DateTypeConverter;

import java.util.Locale;

/**
 * <insert description here>
 *
 * @author : lt-rico
 */
public class PTDateTypeConverter extends DateTypeConverter {

    @Override
    public void setLocale(Locale locale) {
        super.setLocale(new Locale("pt"));
    }
}
