/*
 * $Id: Template1Diagram.java,v 1.8 2012/06/01 13:51:51 d-marques Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2012/06/01 13:51:51 $
 * Last changed by : $Author: d-marques $
 */
package com.criticalsoftware.certitools.entities.jcr;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Bean;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

/**
 * Diagram with clickable areas template
 *
 * @author : lt-rico
 */
@Node(extend = Template.class)
public class Template1Diagram extends Template implements TemplateWithImage {

    @Bean
    private Resource resource;

    @Field
    private String imageMap;

    public Template1Diagram() {
        super(Type.TEMPLATE_DIAGRAM.getName());
    }

    public Template1Diagram(Resource resource) {
        super(Type.TEMPLATE_DIAGRAM.getName());
        this.resource = resource;
    }

    public Template1Diagram(Resource resource, String imageMap) {
        super(Type.TEMPLATE_DIAGRAM.getName());
        this.resource = resource;
        this.imageMap = imageMap;
    }

    public Template1Diagram(String imageMap) {
        super(Type.TEMPLATE_DIAGRAM.getName());
        this.imageMap = imageMap;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public String getImageMap() {
        return imageMap;
    }

    public void setImageMap(String imageMap) {
        this.imageMap = imageMap;
    }
}
