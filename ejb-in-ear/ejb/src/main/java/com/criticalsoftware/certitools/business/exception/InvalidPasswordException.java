/*
 * $Id: InvalidPasswordException.java,v 1.2 2009/03/23 00:08:43 lt-rico Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/03/23 00:08:43 $
 * Last changed by : $Author: lt-rico $
 */
package com.criticalsoftware.certitools.business.exception;

import javax.ejb.ApplicationException;

/**
 * <insert description here>
 *
 * @author : lt-rico
 */
@ApplicationException(rollback = true)
public class InvalidPasswordException extends Exception {
    
    public InvalidPasswordException(String message) {
        super(message);
    }
}
