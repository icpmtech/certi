/*
 * $Id: JackrabbitException.java,v 1.1 2009/03/25 17:56:06 lt-rico Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/03/25 17:56:06 $
 * Last changed by : $Author: lt-rico $
 */
package com.criticalsoftware.certitools.business.exception;

import javax.ejb.ApplicationException;

/**
 * Jackrabbit Exception
 *
 * @author : lt-rico
 * @version : $version $
 */
@ApplicationException(rollback = true)
public class JackrabbitException extends Exception {

    /**
     * Business Exception with corresponding message and throwable.
     *
     * @param message the exception message
     * @param cause   the exception throwable
     */
    public JackrabbitException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Business Exception with corresponding message.
     *
     * @param message the exception message
     */
    public JackrabbitException(String message) {
        super(message);
    }
}