/*
 * $Id: Template4PlanClickable.java,v 1.4 2012/06/01 13:51:51 d-marques Exp $
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
 * Template 4 ActionBean
 *
 * @author jp-gomes
 */

@Node(extend = Template.class)
public class Template4PlanClickable extends Template implements TemplateWithImage {

    @Bean
    private Resource resource;

    @Field
    private String imageMap;

    public Template4PlanClickable() {
        super(Type.TEMPLATE_PLAN_CLICKABLE.getName());
    }

    public Template4PlanClickable(Resource resource) {
        super(Type.TEMPLATE_PLAN_CLICKABLE.getName());
        this.resource = resource;
    }

    public Template4PlanClickable(Resource resource, String imageMap) {
        super(Type.TEMPLATE_PLAN_CLICKABLE.getName());
        this.resource = resource;
        this.imageMap = imageMap;
    }

    public Template4PlanClickable(String imageMap) {
        super(Type.TEMPLATE_PLAN_CLICKABLE.getName());
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
