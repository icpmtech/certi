/*
 * $Id: Template9RichTextWithAttach.java,v 1.1 2009/06/19 09:52:49 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/06/19 09:52:49 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.entities.jcr;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;

/**
 * Template 10 : Rich Text with Attach
 *
 * @author jp-gomes
 */
@Node(extend = Template.class)
public class Template9RichTextWithAttach extends Template {

    @Field
    private String text;

    public Template9RichTextWithAttach() {
        super(Type.TEMPLATE_RICH_TEXT_WITH_ATTACH.getName());
    }

    public Template9RichTextWithAttach(String text) {
        super(Type.TEMPLATE_RICH_TEXT_WITH_ATTACH.getName());

        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}