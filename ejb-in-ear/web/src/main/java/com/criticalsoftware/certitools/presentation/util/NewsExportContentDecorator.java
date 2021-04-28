/*
 * $Id: NewsExportContentDecorator.java,v 1.1 2009/10/28 13:43:43 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/10/28 13:43:43 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.presentation.util;

import org.displaytag.decorator.DisplaytagColumnDecorator;
import org.displaytag.exception.DecoratorException;
import org.displaytag.properties.MediaTypeEnum;
import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.jsp.PageContext;

/**
 * New Export Content Decorator
 *
 * @author jp-gomes
 */
public class NewsExportContentDecorator implements DisplaytagColumnDecorator {

    public Object decorate(Object o, PageContext pageContext, MediaTypeEnum mediaTypeEnum) throws DecoratorException {
        if (o instanceof String) {
            return StringEscapeUtils.unescapeHtml(Utils.stripTags((String) o));
        }
        return "";
    }
}
