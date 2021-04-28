/*
 * $Id: UserTableDecorator.java,v 1.1 2009/03/30 11:01:51 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on $Date: 2009/03/30 11:01:51 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util;

import org.displaytag.decorator.TableDecorator;
import com.criticalsoftware.certitools.entities.User;

/**
 * <description>
 *
 * @author pjfsilva
 */
public class UserTableDecorator extends TableDecorator {
    public String addRowClass() {
        Object currentRowObject = getCurrentRowObject();
        if (!((User)currentRowObject).isActive()){

            return (this.isLastRow() ? "lastrow inactive" : "inactive");
        }

        return (this.isLastRow() ? "lastrow" : super.addRowClass());
    }
}