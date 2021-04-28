/*
 * $Id: PlanCMTemplate11MirrorActionBean.java,v 1.2 2009/10/06 16:37:08 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/10/06 16:37:08 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.action.plan;

import com.criticalsoftware.certitools.entities.jcr.Template11Mirror;
import com.criticalsoftware.certitools.entities.jcr.Folder;
import com.criticalsoftware.certitools.business.plan.PlanService;
import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.LocalizableError;

/**
 * <description>
 *
 * @author jp-gomes
 */
public class PlanCMTemplate11MirrorActionBean extends PlanCMTemplateActionBean {

    private Template11Mirror template;
    private String folderId;
    private Boolean insertFolderLink;

    @EJBBean(value = "certitools/PlanService")
    private PlanService planService;

    @DefaultHandler
    public Resolution insertTemplate() {
        template.setParentPath(folderId);
        setTemplateToFolder(template);
        return new ForwardResolution(PlanCMOperationsActionBean.class, "insertFolderPrepareTree");
    }

    public Resolution updateTemplate()
            throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException, JackrabbitException {
        Folder folder = planService.findFolder(folderId, false, getUserInSession(), getModuleTypeFromEnum());
        setTemplateToFolder(folder.getTemplate());
        return new ForwardResolution(PlanCMOperationsActionBean.class, "updateFolderPrepareTree");
    }

    public Resolution validateTemplate()
            throws BusinessException, CertitoolsAuthorizationException, JackrabbitException {

        if (insertFolderLink != null && insertFolderLink) {
            ValidationErrors errors = super.getValidationErrors();
            if (template.getSourcePath() == null) {
                errors.addGlobalError(new LocalizableError("error.pei.template.11Mirror.sourcePath"));
            } else {
                //Validate if selected path in will be already inserted
                String[] parentPathStrings = template.getSourcePath().split("/");
                try {
                    planService
                            .findFolder(folderId + "/folders/" + parentPathStrings[parentPathStrings.length - 1], false,
                                    getUserInSession(), getModuleTypeFromEnum());
                    String[] folderIdStrings = folderId.split("/");
                    String invalidPath =
                            folderIdStrings[folderIdStrings.length - 1] + " -> " + parentPathStrings[parentPathStrings
                                    .length - 1];
                    errors.addGlobalError(
                            new LocalizableError("error.pei.template.11Mirror.alreadyExistPath", invalidPath));
                } catch (ObjectNotFoundException e) {
                    /* Do nothing*/
                }
            }
        }
        super.setTemplateToFolder(template);
        return new ForwardResolution(PlanCMOperationsActionBean.class, "insertFolder");
    }

    public Template11Mirror getTemplate() {
        return template;
    }

    public void setTemplate(Template11Mirror template) {
        this.template = template;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public PlanService getPlanService() {
        return planService;
    }

    public void setPlanService(PlanService planService) {
        this.planService = planService;
    }

    public Boolean getInsertFolderLink() {
        return insertFolderLink;
    }

    public void setInsertFolderLink(Boolean insertFolderLink) {
        this.insertFolderLink = insertFolderLink;
    }
}
