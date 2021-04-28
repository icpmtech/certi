/*
 * $Id: PlansPublicActionBean.java,v 1.1 2010/07/26 18:45:13 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2010/07/26 18:45:13 $
 * Last changed by : $Author: pjfsilva $
 */

package com.criticalsoftware.certitools.presentation.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import org.apache.commons.lang.StringUtils;

/**
 * Home Action Bean
 *
 * @author : lt-rico
 * @version : $version $
 */
public class PlansPublicActionBean extends AbstractActionBean {

    private String module;

    @DontValidate
    @DefaultHandler
    public Resolution main() {
        setAttribute("plans", "-on");

        if (module == null || StringUtils.isBlank(module)) {
            return new ForwardResolution("/WEB-INF/jsps/certitools/plans.jsp");
        } else {
            return new ForwardResolution("/WEB-INF/jsps/certitools/plans" + module +".jsp");
        }
    }

    public void fillLookupFields() {
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }
}