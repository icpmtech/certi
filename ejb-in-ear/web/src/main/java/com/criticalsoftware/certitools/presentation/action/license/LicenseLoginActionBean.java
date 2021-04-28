/*
 * $Id: LicenseLoginActionBean.java,v 1.5 2009/03/09 19:00:18 haraujo Exp $
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
package com.criticalsoftware.certitools.presentation.action.license;

import com.criticalsoftware.certitools.presentation.action.AbstractActionBean;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;

/**
 * <description>
 *
 * @author jp-gomes
 */
public class LicenseLoginActionBean extends AbstractActionBean {

    @DefaultHandler
    public Resolution login() {

        if (getUserInSession() != null) {
            return new RedirectResolution(LicenseActionBean.class);

        } else {
            return new ForwardResolution("/WEB-INF/jsps/license/licenseLogin.jsp");
        }
    }

    public void fillLookupFields() {}
}
