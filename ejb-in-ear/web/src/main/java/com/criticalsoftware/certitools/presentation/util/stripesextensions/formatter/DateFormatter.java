/*
 * $Id: DateFormatter.java,v 1.1 2009/10/22 11:23:31 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/10/22 11:23:31 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.presentation.util.stripesextensions.formatter;

import net.sourceforge.stripes.format.Formatter;

import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;

import com.criticalsoftware.certitools.util.Configuration;

/**
 * Date Formatter
 *
 * @author jp-gomes
 */
public class DateFormatter implements Formatter<Date> {

    public void setFormatType(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setFormatPattern(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setLocale(Locale locale) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void init() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String format(Date date) {
        SimpleDateFormat formatter =
                new SimpleDateFormat(Configuration.getInstance().getDatePattern(), new Locale("pt"));
        return formatter.format(date);
    }
}
