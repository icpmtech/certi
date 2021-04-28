/*
 * $Id: Template5ContactsElement.java,v 1.3 2009/10/16 16:27:10 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/10/16 16:27:10 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.entities.jcr;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Bean;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;

/**
 * One individual Contact 
 *
 * @author pjfsilva
 */
@Node(extend = Template.class)
public class Template5ContactsElement extends Template {

    public enum ContactType {EXTERNAL_ENTITY, INTERNAL_PERSON, EMERGENCY_STRUCTURE_PERSON}

    @Field(jcrMandatory = true)
    private String contactType;

    @Field
    private String email;

    @Field
    private String phone;

    @Field
    private String mobile;

    @Bean
    private Resource photo;

    @Field
    private String entityName;

    @Field
    private String entityType;

    @Field
    private String personName;

    @Field
    private String personPosition;

    @Field()
    private String personArea;

    public Template5ContactsElement() {
        super(Type.TEMPLATE_CONTACTS_ELEMENT.getName());
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Resource getPhoto() {
        return photo;
    }

    public void setPhoto(Resource photo) {
        this.photo = photo;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonPosition() {
        return personPosition;
    }

    public void setPersonPosition(String personPosition) {
        this.personPosition = personPosition;
    }

    public String getPersonArea() {
        return personArea;
    }

    public void setPersonArea(String personArea) {
        this.personArea = personArea;
    }
}