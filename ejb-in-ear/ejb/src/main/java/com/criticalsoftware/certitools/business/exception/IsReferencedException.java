/*
 * $Id: IsReferencedException.java,v 1.5 2009/10/27 20:02:18 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/10/27 20:02:18 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.business.exception;

/**
 * IsReferencedException is thrown everytime there's an object that's going to be deleted and there's a reference to it
 * in another object in the aplication. In this case the parent object shouldn't be deleted
 *
 * @author pjfsilva
 */
public class IsReferencedException extends Exception {

    /** What entity is referenced? */
    public enum Type {
        USER,
        LICENCE,
        NEWS,
        NEWS_CATEGORIES,
        FAQ,
        LEGAL_DOCUMENT_CATEGORY,
        COUNTRY,
        MODULE,
        LEGISLATION,
        CONTRACT,
        PLAN,
        PLAN_SOURCE,
        PLAN_TARGET,
        FOLDER
    }


    private Type type;

    /**
     * IsReferenced exception.
     *
     * @param message The message to show
     */
    public IsReferencedException(String message) {
        super(message);
    }

    public IsReferencedException(String message, Type type) {
        super(message);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}