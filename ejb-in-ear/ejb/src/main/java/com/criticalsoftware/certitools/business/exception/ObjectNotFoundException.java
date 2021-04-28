/*
 * $Id: ObjectNotFoundException.java,v 1.16 2010/05/26 16:55:29 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2010/05/26 16:55:29 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.business.exception;

import javax.ejb.ApplicationException;

/**
 * <insert description here>
 *
 * @author : lt-rico
 * @version : $version $
 */
@ApplicationException(rollback = true)
public class ObjectNotFoundException extends Exception {

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
        COMPANY,
        REPOSITORY_FILE,
        LEGAL_DOCUMENT_STATISTICS,
        LEGAL_DOCUMENT_HISTORY,
        LEGAL_DOCUMENT,
        PLAN,
        FOLDER,
        RESOURCE,
        PERMISSION,
        TEMPLATE,
        SM_ACTIVITY,
        SM_ACTIVITY_TYPE,
        SM_DOCUMENT,
        SM_CORRECTIVE_ACTION,
        SM_ANOMALY,
        SM_SECURITY_IMPACT,
        SM_SECURITY_IMPACT_WORK,
        SM_RISK,
        SM_MAINTENANCE,
        SM_MAINTENANCE_TYPE,
        SM_EQUIPMENT,
        SM_EMERGENCY_ACTION,
        SM_EMERGENCY_USER
    }

    private Type type;

    /**
     * Creates a new ObjectNotFoundException.
     *
     * @param message the message string
     * @param cause   the throwable that cause the exception
     * @param type    the associated type of object
     */
    public ObjectNotFoundException(String message, Throwable cause, Type type) {
        super(message, cause);
        this.type = type;
    }

    /**
     * Creates a new ObjectNotFoundException.
     *
     * @param message the message string
     * @param type    the associated type of object
     */
    public ObjectNotFoundException(String message, Type type) {
        super(message);
        this.type = type;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public Type getType() {
        return type;
    }
}