/*
 * $Id: Template3RichText.java,v 1.4 2009/06/02 16:19:41 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/06/02 16:19:41 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.entities.jcr;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

/**
 * A text template
 *
 * @author : lt-rico
 */

@Node(extend = Template.class)
public class Template3RichText extends Template {

    @Field
    private String text;

    public Template3RichText() {
        super(Type.TEMPLATE_RICH_TEXT.getName());
    }

    public Template3RichText(String text) {
        super(Type.TEMPLATE_RICH_TEXT.getName());

        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
