/*
 * $Id: DeleteException.java,v 1.1 2009/10/27 20:02:18 jp-gomes Exp $
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
 * <description>
 *
 * @author jp-gomes
 */
public class DeleteException extends Exception {

    public enum Type {
        FOLDER
    }


    private Type type;

    /**
     * DeleteException exception.
     *
     * @param message The message to show
     */
    public DeleteException(String message) {
        super(message);
    }

    public DeleteException(String message, Type type) {
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
