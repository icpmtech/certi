/*
 * $Id: PlanCMTemplate5ContactsElementActionBean.java,v 1.4 2009/10/20 18:34:06 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/10/20 18:34:06 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.action.plan;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.plan.PlanService;
import com.criticalsoftware.certitools.entities.jcr.*;
import com.criticalsoftware.certitools.presentation.util.ValidationUtils;
import com.criticalsoftware.certitools.util.Configuration;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.ValidationErrors;

import java.io.IOException;

/**
 * Template 5 - Indivual contact
 *
 * @author pjfsilva
 */
public class PlanCMTemplate5ContactsElementActionBean extends PlanCMTemplateActionBean {
    private Template5ContactsElement template;
    private FileBean filePhoto;
    private Boolean insertFolderFlag;

    private String folderId;

    @EJBBean(value = "certitools/PlanService")
    private PlanService planService;

    @DefaultHandler
    public Resolution insertTemplate()
            throws IOException, BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException,
            JackrabbitException {
        this.setTemplateToFolder();
        return new ForwardResolution(PlanCMOperationsActionBean.class, "insertFolderPrepareTree");
    }

    public Resolution updateTemplate()
            throws IOException, BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException,
            JackrabbitException {
        this.setTemplateToFolder();
        return new ForwardResolution(PlanCMOperationsActionBean.class, "updateFolderPrepareTree");
    }

    public Resolution validateTemplate()
            throws IOException, BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException,
            JackrabbitException {
        ValidationErrors errors = super.getValidationErrors();

        if (filePhoto != null) {
            // validate if the sent file is an image and file size is correct
            if (!ValidationUtils.validateImageContentType(filePhoto.getContentType())) {
                errors.addGlobalError(new LocalizableError("error.pei.image.invalidFormat"));
                filePhoto = null;
            } else if (!ValidationUtils
                    .validateImageSize(filePhoto.getSize(), Configuration.getInstance().getPEITemplateMaxFileSize())) {
                errors.addGlobalError(
                        new LocalizableError("error.pei.image.invalidSize",
                                Configuration.getInstance().getPEITemplateMaxFileSizeInMB()));
                filePhoto = null;
            }
        }

        this.setTemplateToFolder();
        return new ForwardResolution(PlanCMOperationsActionBean.class, "insertFolder");
    }

    private void setTemplateToFolder()
            throws IOException, BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException,
            JackrabbitException {
        if (filePhoto != null) {
            String filePhotoContentType;

            if (filePhoto.getContentType() != null) {
                filePhotoContentType = filePhoto.getContentType();
            } else {
                filePhotoContentType = "application/octet-stream";
            }

            template.setPhoto(new Resource("/" + filePhoto.getFileName(), filePhoto.getFileName(), filePhotoContentType,
                    filePhoto.getInputStream(), "" + filePhoto.getSize()));
        } else {
            if (!insertFolderFlag) {
                // if updating and already has a photo, set template photo to it
                Folder dbFolder = planService.findFolder(folderId, false, getUserInSession(), getModuleTypeFromEnum());

                // this is needed as the user can change to this template, we only want to consider "real" updates to
                // this folder
                if (dbFolder.getTemplate().getName().equals(Template.Type.TEMPLATE_CONTACTS_ELEMENT.getName())) {
                    Template5ContactsElement dbTemplate = (Template5ContactsElement) dbFolder.getTemplate();
                    if (dbTemplate.getPhoto() != null) {
                        template.setPhoto(dbTemplate.getPhoto());
                    }
                }
            }
        }

        super.setTemplateToFolder(template);
    }

    public Template5ContactsElement getTemplate() {
        return template;
    }

    public void setTemplate(Template5ContactsElement template) {
        this.template = template;
    }

    public FileBean getFilePhoto() {
        return filePhoto;
    }

    public void setFilePhoto(FileBean filePhoto) {
        this.filePhoto = filePhoto;
    }

    public Boolean isInsertFolderFlag() {
        return insertFolderFlag;
    }

    public void setInsertFolderFlag(Boolean insertFolderFlag) {
        this.insertFolderFlag = insertFolderFlag;
    }

    public PlanService getPlanService() {
        return planService;
    }

    public void setPlanService(PlanService planService) {
        this.planService = planService;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }
}