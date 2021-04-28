/*
 * $Id: PlanCMTemplateActionBean.java,v 1.2 2009/10/16 10:27:27 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/10/16 10:27:27 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.presentation.action.plan;

import com.criticalsoftware.certitools.entities.jcr.Folder;
import com.criticalsoftware.certitools.entities.jcr.Template;
import com.criticalsoftware.certitools.presentation.action.AbstractActionBean;
import com.criticalsoftware.certitools.presentation.util.MenuItem;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.controller.LifecycleStage;

/**
 * Template abstract action bean
 *
 * @author pjfsilva
 */
public abstract class PlanCMTemplateActionBean extends AbstractActionBean {

    @Before(stages = LifecycleStage.BindingAndValidation)
    public void fillLookupFields() {
        getMenu().select(MenuItem.Item.MENU_PEI, MenuItem.Item.SUB_MENU_PEI_ADMIN);
    }

    public void setTemplateToFolder(Template template) {
        Folder folder = super.getFolder();
        folder.setTemplate(template);
        super.setFolder(folder);
    }
}