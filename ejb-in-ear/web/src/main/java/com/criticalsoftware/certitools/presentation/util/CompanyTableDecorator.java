/*
 * $Id: CompanyTableDecorator.java,v 1.2 2009/03/30 10:29:02 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/03/30 10:29:02 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util;

import com.criticalsoftware.certitools.entities.Company;
import org.displaytag.decorator.TableDecorator;

/**
 * <description>
 *
 * @author pjfsilva
 */
public class CompanyTableDecorator extends TableDecorator {
    public String addRowClass() {
        Object currentRowObject = getCurrentRowObject();
        if (!((Company) currentRowObject).isActive()) {
            return (this.isLastRow() ? "lastrow inactive" : "inactive");
        }

        return (this.isLastRow() ? "lastrow" : super.addRowClass());
    }
}