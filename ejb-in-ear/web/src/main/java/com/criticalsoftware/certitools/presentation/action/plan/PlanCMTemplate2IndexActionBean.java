/*
 * $Id: PlanCMTemplate2IndexActionBean.java,v 1.1 2009/09/24 16:48:07 pjfsilva Exp $
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

import com.criticalsoftware.certitools.entities.jcr.Template2Index;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.ForwardResolution;

/**
 * Template 2 - Index ActionBean
 *
 * @author jp-gomes
 */
public class PlanCMTemplate2IndexActionBean extends PlanCMTemplateActionBean {

    private Template2Index template;

    @DefaultHandler
    public Resolution insertTemplate() {
        setTemplateToFolder(new Template2Index());
        return new ForwardResolution(PlanCMOperationsActionBean.class, "insertFolderPrepareTree");
    }

    public Resolution updateTemplate() {
        setTemplateToFolder(new Template2Index());
        return new ForwardResolution(PlanCMOperationsActionBean.class, "updateFolderPrepareTree");
    }

    public Template2Index getTemplate() {
        return template;
    }

    public void setTemplate(Template2Index template) {
        this.template = template;
    }
}
