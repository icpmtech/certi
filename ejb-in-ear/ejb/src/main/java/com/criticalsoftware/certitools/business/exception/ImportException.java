/*
 * $Id: ImportException.java,v 1.3 2009/08/26 15:08:41 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/08/26 15:08:41 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.business.exception;

import javax.ejb.ApplicationException;

/**
 * Import Exception - Used in the import of users CSV file
 *
 * @author : lt-rico
 */
@ApplicationException(rollback = true)
public class ImportException extends Exception {

    public enum Type {READ, PARSE, INSERT}

    private int lineNumberError;
    private String email;
    private String messageResource;

    // string containing more information about the exception (specific to the real error that occured)
    private String aux;

    private Type type;

    public ImportException() {
    }

    public ImportException(String message, String messageResource, int lineNumberError, String aux, Type type) {
        super(message);
        this.messageResource = messageResource;
        this.lineNumberError = lineNumberError;
        this.aux = aux;
        this.type = type;
    }

    public ImportException(String message, String messageResource, String email, String aux, Type type) {
        super(message);
        this.messageResource = messageResource;
        this.email = email;
        this.aux = aux;
        this.type = type;
    }

    public ImportException(String message, String messageResource, int lineNumberError, String email, String aux, Type type) {
        super(message);
        this.messageResource = messageResource;
        this.lineNumberError = lineNumberError;
        this.email = email;
        this.aux = aux;
        this.type = type;
    }

    public int getLineNumberError() {
        return lineNumberError;
    }

    public String getEmail() {
        return email;
    }

    public Type getType() {
        return type;
    }

    public String getMessageResource() {
        return messageResource;
    }

    public String getAux() {
        return aux;
    }
}
