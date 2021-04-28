/*
 * $Id: DisplayErrorsActionBean.java,v 1.1 2009/07/08 20:50:48 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/07/08 20:50:48 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.presentation.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.security.action.Secure;

/**
 * <Display Errors for FileUploadLimitExceededException>
 *
 * @author jp-gomes
 */
public class DisplayErrorsActionBean extends AbstractActionBean {

    private Boolean fileUploadException;
    private Long maxFileSize;

    @DefaultHandler
    @Secure(roles = "user")
    public Resolution displayErrors() {
        setAttribute("fileUploadException", fileUploadException);
        setAttribute("maxFileSize", maxFileSize);
        return new ForwardResolution("/WEB-INF/error.jsp");
    }

    public void fillLookupFields() {
    }

    public Boolean getFileUploadException() {
        return fileUploadException;
    }

    public void setFileUploadException(Boolean fileUploadException) {
        this.fileUploadException = fileUploadException;
    }

    public Long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(Long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
}