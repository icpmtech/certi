/*
 * $Id: SecurityActionBean.java,v 1.2 2009/06/01 15:27:00 haraujo Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/06/01 15:27:00 $
 * Last changed by : $Author: haraujo $
 */

package com.criticalsoftware.certitools.presentation.action.security;

import com.criticalsoftware.certitools.presentation.action.AbstractActionBean;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.controller.LifecycleStage;
import com.criticalsoftware.certitools.presentation.util.MenuItem;

/**
 * Security Action Bean
 *
 * @author haraujo
 */
public class SecurityActionBean extends AbstractActionBean {
    @DontValidate
    @DefaultHandler
    public Resolution main() {
        return new ForwardResolution("/WEB-INF/jsps/security/unauthorized.jsp");
    }

    @Before(stages = LifecycleStage.BindingAndValidation)
    public void fillLookupFields() {
        getMenu().select(MenuItem.Item.MENU_SECURITY, null);
    }
}