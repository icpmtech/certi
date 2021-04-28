/*
 * $Id: LegislationPublicActionBean.java,v 1.1 2009/03/17 20:38:52 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/03/17 20:38:52 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.presentation.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

/**
 * Open resource of legislation action bean
 *
 * @author : lt-rico
 * @version : $version $
 */
public class LegislationPublicActionBean extends AbstractActionBean {

    @DontValidate
    @DefaultHandler
    public Resolution main() {
        setAttribute("legislation", "-on");

        return new ForwardResolution("/WEB-INF/jsps/certitools/legislationPublic.jsp");
    }

    @Override
    public void fillLookupFields() {
    }
}
