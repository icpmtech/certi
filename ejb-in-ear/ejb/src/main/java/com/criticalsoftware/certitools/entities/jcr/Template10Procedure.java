/**
 * $Id: Template10Procedure.java,v 1.1 2009/07/21 10:41:05 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/07/21 10:41:05 $
 * Last changed by : jp-gomes
 */
package com.criticalsoftware.certitools.entities.jcr;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

/**
 * Template 10 Procedures
 *
 * @author : jp-gomes
 */
@Node(extend = Template.class)
public class Template10Procedure extends Template {

    public Template10Procedure() {
        super(Template.Type.TEMPLATE_PROCEDURE.getName());
    }
}