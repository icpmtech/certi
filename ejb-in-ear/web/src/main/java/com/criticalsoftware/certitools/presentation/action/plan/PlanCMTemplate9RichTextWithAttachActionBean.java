/*
 * $Id: PlanCMTemplate9RichTextWithAttachActionBean.java,v 1.1 2009/09/24 16:48:07 pjfsilva Exp $
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

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.ForwardResolution;
import com.criticalsoftware.certitools.entities.jcr.Template9RichTextWithAttach;

/**
 * <description>
 *
 * @author jp-gomes
 */
public class PlanCMTemplate9RichTextWithAttachActionBean extends PlanCMTemplateActionBean {

    private Template9RichTextWithAttach template;

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

    public Template9RichTextWithAttach getTemplate() {
        return template;
    }

    public void setTemplate(Template9RichTextWithAttach template) {
        this.template = template;
    }
}
