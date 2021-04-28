/*
 * $Id: EscapeTag.java,v 1.2 2009/10/08 11:19:16 jp-gomes Exp $
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

import com.criticalsoftware.certitools.util.Logger;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.IOException;

/**
 * Tag to Escape fields
 *
 * @author jp-gomes
 */
public class EscapeTag extends CertitoolsTagSupport {
    private static final Logger log = Logger.getInstance(EscapeTag.class);
    private static final String ESCAPE_JS = "js";
    private String type;
    private String value;

    public int doStartTag() throws JspException {
        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {
        JspWriter context = getPageContext().getOut();
        if (type.equals(ESCAPE_JS)) {
            try {
                context.write(StringEscapeUtils.escapeJavaScript(value));
            } catch (IOException e) {
                log.error("Error trying to escape string");
            }
        }
        return EVAL_PAGE;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

