/*
 * $Id: Template2Index.java,v 1.2 2009/06/17 10:49:17 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/06/17 10:49:17 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.entities.jcr;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

/**
 * Navigable Template
 *
 * @author : lt-rico
 */

@Node(extend = Template.class)
public class Template2Index extends Template {

    public Template2Index() {
        super(Type.TEMPLATE_INDEX.getName());
    }
}