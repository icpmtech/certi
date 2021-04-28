/*
 * $Id: PlanCMTemplate3RichTextActionBean.java,v 1.1 2009/09/24 16:48:07 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/09/24 16:48:07 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.action.plan;

import com.criticalsoftware.certitools.entities.jcr.Template3RichText;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

/**
 * Template 3 - Rich text actionbean
 *
 * @author pjfsilva
 */
public class PlanCMTemplate3RichTextActionBean extends PlanCMTemplateActionBean {

    private Template3RichText template;

    @DefaultHandler
    public Resolution insertTemplate() {
        setTemplateToFolder(template);
        return new ForwardResolution(PlanCMOperationsActionBean.class, "insertFolderPrepareTree");
    }

    public Resolution updateTemplate() {
        setTemplateToFolder(template);
        return new ForwardResolution(PlanCMOperationsActionBean.class, "updateFolderPrepareTree");
    }

    public Resolution validateTemplate() {
        super.setTemplateToFolder(template);
        return new ForwardResolution(PlanCMOperationsActionBean.class, "insertFolder");
    }

    public Template3RichText getTemplate() {
        return template;
    }

    public void setTemplate(Template3RichText template) {
        this.template = template;
    }
}
