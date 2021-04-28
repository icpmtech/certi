/*
 * $Id: CertitoolsAuthorizationException.java,v 1.1 2009/03/13 17:05:27 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/03/13 17:05:27 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.business.exception;

import javax.ejb.ApplicationException;

/**
 * Certitools Authorization Exception
 *
 * @author jp-gomes
 */
@ApplicationException(rollback = true)
public class CertitoolsAuthorizationException extends Exception {

    /**
     * CertitoolsAuthorization Exception with corresponding message and throwable.
     *
     * @param message the exception message
     * @param cause   the exception throwable
     */
    public CertitoolsAuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * CertitoolsAuthorization Exception with corresponding message.
     *
     * @param message the exception message
     */
    public CertitoolsAuthorizationException(String message) {
        super(message);
    }
}
