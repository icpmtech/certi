/*
 * $Id: PlanCMTemplate7FAQActionBean.java,v 1.1 2009/09/24 16:48:07 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/09/24 16:48:07 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.action.plan;

import com.criticalsoftware.certitools.entities.jcr.Template7FAQ;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

/**
 * <description>
 *
 * @author jp-gomes
 */
public class PlanCMTemplate7FAQActionBean extends PlanCMTemplateActionBean {

    private Template7FAQ template;

    @DefaultHandler
    public Resolution insertTemplate() {
        setTemplateToFolder(new Template7FAQ());
        return new ForwardResolution(PlanCMOperationsActionBean.class, "insertFolderPrepareTree");
    }

    public Resolution updateTemplate() {
        setTemplateToFolder(new Template7FAQ());
        return new ForwardResolution(PlanCMOperationsActionBean.class, "updateFolderPrepareTree");
    }

    public Template7FAQ getTemplate() {
        return template;
    }

    public void setTemplate(Template7FAQ template) {
        this.template = template;
    }
}
