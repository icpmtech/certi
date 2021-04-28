/*
 * $Id: Template.java,v 1.15 2009/09/30 09:01:40 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/09/30 09:01:40 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.entities.jcr;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

/**
 * Template object super class
 *
 * @author : lt-rico
 */
@Node(isAbstract = true)
public class Template {

    public enum Type {
        TEMPLATE_INDEX("Template2Index"),
        TEMPLATE_RESOURCE("TemplateResource"),
        TEMPLATE_DIAGRAM("Template1Diagram"),
        TEMPLATE_RICH_TEXT("Template3RichText"),
        TEMPLATE_PLAN_CLICKABLE("Template4PlanClickable"),
        TEMPLATE_CONTACTS("Template5Contacts"),
        TEMPLATE_CONTACTS_ELEMENT("Template5ContactsElement"),
        TEMPLATE_DOCUMENTS("Template6Documents"),
        TEMPLATE_DOCUMENTS_ELEMENT("Template6DocumentsElement"),
        TEMPLATE_FAQ("Template7FAQ"),
        TEMPLATE_FAQ_ELEMENT("Template7FAQElement"),
        TEMPLATE_RISK_ANALYSIS("Template8RiskAnalysis"),
        TEMPLATE_RICH_TEXT_WITH_ATTACH("Template9RichTextWithAttach"),
        TEMPLATE_PROCEDURE("Template10Procedure"),
        TEMPLATE_MIRROR("Template11Mirror"),
        TEMPLATE_MEANS_RESOURCES("Template12MeansResources"),
        TEMPLATE_MEANS_RESOURCES_ELEMENT("Template12MeansResourcesElement");

        private String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Field(jcrMandatory = true)
    protected String name;

    public Template() {
    }

    public Template(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
