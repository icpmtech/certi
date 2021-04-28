/*
 * $Id: CertitoolsTagSupport.java,v 1.2 2009/10/08 11:19:16 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/10/08 11:19:16 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.presentation.tag;

import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspException;
import javax.servlet.http.HttpServletRequest;

/**
 * Certitools Tag Support
 *
 * @author jp-gomes
 */
public abstract class CertitoolsTagSupport implements Tag {
    private PageContext pageContext;

    private Tag parent;

    public PageContext getPageContext() {
        return pageContext;
    }

    public void setPageContext(PageContext pageContext) {
        this.pageContext = pageContext;
    }

    public Tag getParent() {
        return parent;
    }

    public void setParent(Tag parent) {
        this.parent = parent;
    }

    public String getContextPath() {
        return ((HttpServletRequest) getPageContext().getRequest()).getContextPath();
    }

    public abstract int doStartTag() throws JspException;

    public abstract int doEndTag() throws JspException;

    public void release() {
    }
}

