/*
 * $Id: WhatItIsActionBean.java,v 1.2 2009/03/11 01:50:02 lt-rico Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/03/11 01:50:02 $
 * Last changed by : $Author: lt-rico $
 */

package com.criticalsoftware.certitools.presentation.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

/**
 * Home Action Bean
 *
 * @author : lt-rico
 * @version : $version $
 */
public class WhatItIsActionBean extends AbstractActionBean{

    @DontValidate
    @DefaultHandler
    public Resolution main() {
        setAttribute("what", "-on");

        return new ForwardResolution("/WEB-INF/jsps/certitools/whatItIs.jsp");
    }

    public void fillLookupFields() {}
}