/*
 * $Id: LogoutActionBean.java,v 1.1 2009/03/09 19:00:18 haraujo Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/03/09 19:00:18 $
 * Last changed by : $Author: haraujo $
 */
package com.criticalsoftware.certitools.presentation.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;

/**
 * @author : jp-gomes
 * @version : $version $
 */
public class LogoutActionBean extends AbstractActionBean {

    @DefaultHandler
    public Resolution logout() {
        getContext().getRequest().getSession().invalidate();
        return new RedirectResolution("/");
    }

    public void fillLookupFields() {}
}
