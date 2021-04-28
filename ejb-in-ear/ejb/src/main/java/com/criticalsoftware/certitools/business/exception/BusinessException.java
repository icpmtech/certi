/*
 * $Id: BusinessException.java,v 1.2 2009/03/02 20:39:39 haraujo Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/03/02 20:39:39 $
 * Last changed by : $Author: haraujo $
 */
package com.criticalsoftware.certitools.business.exception;

import javax.ejb.ApplicationException;

/**
 * Bussiness Exception
 *
 * @author : lt-rico
 * @version : $version $
 */
@ApplicationException(rollback = true)
public class BusinessException extends Exception {

    /**
     * Business Exception with corresponding message and throwable.
     *
     * @param message the exception message
     * @param cause   the exception throwable
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Business Exception with corresponding message.
     *
     * @param message the exception message
     */
    public BusinessException(String message) {
        super(message);
    }
}
