/*
 * $Id: TemplateResource.java,v 1.2 2009/06/05 15:39:41 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/06/05 15:39:41 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.entities.jcr;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Bean;

/**
 * <insert description here>
 *
 * @author : lt-rico
 */
@Node(extend = Template.class)
public class TemplateResource extends Template {
    @Bean
    private Resource resource;

    public TemplateResource() {
        super(Type.TEMPLATE_RESOURCE.getName());
    }

    public TemplateResource(Resource resource) {
        super(Type.TEMPLATE_RESOURCE.getName());

        this.resource = resource;
    }

    protected TemplateResource(String name, Resource resource) {
        super(name);
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
