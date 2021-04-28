/**
 * $Id: PlanCMTemplate10ProcedureActionBean.java,v 1.1 2009/09/24 16:48:07 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/09/24 16:48:07 $
 * Last changed by : jp-gomes
 */
package com.criticalsoftware.certitools.presentation.action.plan;

import com.criticalsoftware.certitools.entities.jcr.Template10Procedure;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

/**
 * Template 10 - Procedure ActionBean
 *
 * @author : jp-gomes
 */
public class PlanCMTemplate10ProcedureActionBean extends PlanCMTemplateActionBean {

    private Template10Procedure template;

    @DefaultHandler
    public Resolution insertTemplate() {
        setTemplateToFolder(new Template10Procedure());
        return new ForwardResolution(PlanCMOperationsActionBean.class, "insertFolderPrepareTree");
    }

    public Resolution updateTemplate() {
        setTemplateToFolder(new Template10Procedure());
        return new ForwardResolution(PlanCMOperationsActionBean.class, "updateFolderPrepareTree");
    }

    public Template10Procedure getTemplate() {
        return template;
    }

    public void setTemplate(Template10Procedure template) {
        this.template = template;
    }
}