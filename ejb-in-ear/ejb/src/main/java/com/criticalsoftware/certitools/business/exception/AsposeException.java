/*
 * $Id: AsposeException.java,v 1.2 2010/07/09 17:57:40 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/07/09 17:57:40 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.business.exception;

/**
 * Aspose generic error
 *
 * @author :    pjfsilva
 * @version :   $Revision: 1.2 $
 */
public class AsposeException extends Exception{
    private String folderName = "";

    /**
     * Aspose Exception with corresponding message and throwable.
     *
     * @param message the exception message
     * @param cause   the exception throwable
     */
    public AsposeException(String message, Throwable cause) {
        super(message, cause);
    }

    public AsposeException(String message, Throwable cause, String folderName) {
        super(message, cause);
        this.folderName = folderName;
    }

    /**
     * Aspose Exception with corresponding message.
     *
     * @param message the exception message
     */
    public AsposeException(String message) {
        super(message);
    }

    public AsposeException(String message, Throwable cause, StackTraceElement[] stackTrace) {
        super(message, cause);
    }

    @Override
    public String toString() {
        return super.toString() + "\nAsposeException{" +
                "folderName='" + folderName + '\'' +
                '}';
    }
}