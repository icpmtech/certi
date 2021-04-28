/*
 * $Id: PDFException.java,v 1.2 2009/05/18 13:51:57 lt-rico Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/05/18 13:51:57 $
 * Last changed by : $Author: lt-rico $
 */
package com.criticalsoftware.certitools.business.exception;

/**
 * Itext PDF exception while generating it
 *
 * @author : lt-rico
 */
public class PDFException extends Exception {

    public PDFException() {
        super("Error while generating a PDF");
    }

    public PDFException(String message) {
        super(message);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public PDFException(String message, Throwable cause) {
        super(message, cause);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
