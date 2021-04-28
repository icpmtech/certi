/*
 * $Id: Template6Documents.java,v 1.1 2009/06/15 12:56:03 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/06/15 12:56:03 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.entities.jcr;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

/**
 * Template 6 : Documents
 *
 * @author jp-gomes
 */
@Node(extend = Template.class)
public class Template6Documents extends Template {

    public Template6Documents() {
        super(Type.TEMPLATE_DOCUMENTS.getName());
    }
}
