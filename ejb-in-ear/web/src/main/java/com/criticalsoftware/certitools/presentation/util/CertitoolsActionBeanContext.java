/*
 * $Id: CertitoolsActionBeanContext.java,v 1.3 2009/03/09 18:13:32 haraujo Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/03/09 18:13:32 $
 * Last changed by : $Author: haraujo $
 */
package com.criticalsoftware.certitools.presentation.util;

import net.sourceforge.stripes.action.ActionBeanContext;

/**
 * Certitools action bean context
 *
 * @author : lt-rico
 * @version : $version $
 */
public class CertitoolsActionBeanContext extends ActionBeanContext {
    /**
     * Logs the user out by invalidating the session.
     */
    public void logout() {
        getRequest().getSession().invalidate();
    }
}
