/*
 * $Id: PlanCMTemplate6DocumentsActionBean.java,v 1.1 2009/09/24 16:48:07 pjfsilva Exp $
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

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.ForwardResolution;
import com.criticalsoftware.certitools.entities.jcr.Template6Documents;

/**
 * <description>
 *
 * @author jp-gomes
 */
public class PlanCMTemplate6DocumentsActionBean extends PlanCMTemplateActionBean {

    private Template6Documents template;

    @DefaultHandler
    public Resolution insertTemplate() {
        setTemplateToFolder(new Template6Documents());
        return new ForwardResolution(PlanCMOperationsActionBean.class, "insertFolderPrepareTree");
    }

    public Resolution updateTemplate() {
        setTemplateToFolder(new Template6Documents());
        return new ForwardResolution(PlanCMOperationsActionBean.class, "updateFolderPrepareTree");
    }

    public Template6Documents getTemplate() {
        return template;
    }

    public void setTemplate(Template6Documents template) {
        this.template = template;
    }
}
