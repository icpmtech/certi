/*
 * $Id: AdminDeleteException.java,v 1.1 2009/03/30 11:01:51 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/03/30 11:01:51 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.business.exception;

/**
 * Exception raised when user tried to delete the Administrator
 *
 * @author pjfsilva
 */
public class AdminDeleteException extends Exception {

    public AdminDeleteException(String message) {
        super(message);
    }
}