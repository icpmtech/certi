/*
 * $Id: Template7FAQElement.java,v 1.1 2009/06/19 09:41:16 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/06/19 09:41:16 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.entities.jcr;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;

/**
 * <description>
 *
 * @author jp-gomes
 */
@Node(extend = Template.class)
public class Template7FAQElement extends Template {

    @Field
    private String question;

    @Field
    private String answer;

    public Template7FAQElement() {
        super(Type.TEMPLATE_FAQ_ELEMENT.getName());
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
