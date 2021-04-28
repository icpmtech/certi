/*
 * $Id: Template6DocumentsElement.java,v 1.8 2009/10/16 10:27:27 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/10/16 10:27:27 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.entities.jcr;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import java.util.Date;
import java.util.List;

/**
 * Template 6 for Documents Element
 *
 * @author jp-gomes
 */
@Node(extend = Template.class)
public class Template6DocumentsElement extends Template implements Comparable<Template6DocumentsElement> {

    @Field(jcrMandatory = true)
    private String contentName;

    @Field(jcrMandatory = true)
    private String contentType;

    @Field
    private Date contentDate;

    @Field
    private String contentSubType;

    @Collection
    private List<Resource> resources;

    @Collection
    private List<Link> links;

    public Template6DocumentsElement() {
        super(Type.TEMPLATE_DOCUMENTS_ELEMENT.getName());
    }

    public Template6DocumentsElement(List<Resource> resources, Template6DocumentsElement template) {
        super(Type.TEMPLATE_DOCUMENTS_ELEMENT.getName());
        this.resources = resources;
        if (template != null) {
            this.contentName = template.getContentName();
            this.contentType = template.getContentType();
            this.contentDate = template.getContentDate();
            this.contentSubType = template.getContentSubType();
            this.links = template.getLinks();
        }
    }

    public String getContentSubType() {
        return contentSubType;
    }

    public void setContentSubType(String contentSubType) {
        this.contentSubType = contentSubType;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Date getContentDate() {
        return contentDate;
    }

    public void setContentDate(Date contentDate) {
        this.contentDate = contentDate;
    }

    public int compareTo(Template6DocumentsElement o) {
        return this.getContentName().compareTo(o.getContentName());
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
