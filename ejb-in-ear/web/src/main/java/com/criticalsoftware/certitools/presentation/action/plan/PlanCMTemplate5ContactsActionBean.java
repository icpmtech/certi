/*
 * $Id: PlanCMTemplate5ContactsActionBean.java,v 1.1 2009/09/24 16:48:07 pjfsilva Exp $
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

import com.criticalsoftware.certitools.entities.jcr.Template5Contacts;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

/**
 * Template 5 - Contacts list / search
 *
 * @author pjfsilva
 */
public class PlanCMTemplate5ContactsActionBean extends PlanCMTemplateActionBean {

    private Template5Contacts template;

    @DefaultHandler
    public Resolution insertTemplate() {
        setTemplateToFolder(new Template5Contacts());
        return new ForwardResolution(PlanCMOperationsActionBean.class, "insertFolderPrepareTree");
    }

    public Resolution updateTemplate() {
        setTemplateToFolder(new Template5Contacts());
        return new ForwardResolution(PlanCMOperationsActionBean.class, "updateFolderPrepareTree");
    }

    public Template5Contacts getTemplate() {
        return template;
    }

    public void setTemplate(Template5Contacts template) {
        this.template = template;
    }
}