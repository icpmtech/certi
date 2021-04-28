/*
 * $Id: FAQTableModuleTypeColumnDecorator.java,v 1.1 2009/03/12 02:53:35 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/03/12 02:53:35 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.presentation.util;

import com.criticalsoftware.certitools.util.ModuleType;
import net.sourceforge.stripes.localization.LocalizationUtility;
import org.displaytag.decorator.DisplaytagColumnDecorator;
import org.displaytag.exception.DecoratorException;
import org.displaytag.properties.MediaTypeEnum;

import javax.servlet.jsp.PageContext;

/**
 * FAQ Table Column Decorator
 *
 * @author jp-gomes
 */
public class FAQTableModuleTypeColumnDecorator implements DisplaytagColumnDecorator {
    public Object decorate(Object o, PageContext pageContext, MediaTypeEnum mediaTypeEnum) throws DecoratorException {
        ModuleType moduleType = (ModuleType) o;
        return LocalizationUtility
                .getLocalizedFieldName(moduleType.getKey(), null, null,
                        pageContext.getRequest().getLocale());
    }
}
