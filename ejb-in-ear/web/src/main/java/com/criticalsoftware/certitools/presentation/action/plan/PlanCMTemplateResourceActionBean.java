/*
 * $Id: PlanCMTemplateResourceActionBean.java,v 1.2 2009/10/06 16:37:08 pjfsilva Exp $
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

import com.criticalsoftware.certitools.entities.jcr.Resource;
import com.criticalsoftware.certitools.entities.jcr.TemplateResource;
import com.criticalsoftware.certitools.entities.jcr.Folder;
import com.criticalsoftware.certitools.entities.jcr.Template;
import com.criticalsoftware.certitools.business.plan.PlanService;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.LocalizableError;

import java.io.IOException;

/**
 * Template Resource actionbean
 *
 * @author pjfsilva
 */
public class PlanCMTemplateResourceActionBean extends PlanCMTemplateActionBean {

    private TemplateResource template;
    private FileBean file;
    private String folderId;
    private Boolean insertFolderFlag;

    @EJBBean(value = "certitools/PlanService")
    private PlanService planService;

    @DefaultHandler
    public Resolution insertTemplate() throws IOException, ObjectNotFoundException, JackrabbitException,
            BusinessException, CertitoolsAuthorizationException {
        setTemplateFile();
        return new ForwardResolution(PlanCMOperationsActionBean.class, "insertFolderPrepareTree");
    }

    public Resolution updateTemplate() throws IOException, ObjectNotFoundException, JackrabbitException,
            BusinessException, CertitoolsAuthorizationException {
        setTemplateFile();
        return new ForwardResolution(PlanCMOperationsActionBean.class, "updateFolderPrepareTree");
    }

    public Resolution validateTemplate()
            throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException, JackrabbitException {
        ValidationErrors errors = super.getValidationErrors();
        /* is insert so file is required*/
        if (insertFolderFlag) {
            if (file == null) {
                errors.addGlobalError(new LocalizableError("error.pei.template.file.empty"));
            }
        } else {
            Folder oldFolder = planService.findFolder(folderId, false, getUserInSession(), getModuleTypeFromEnum());
            if (!oldFolder.getTemplate().getName().equals(TemplateResource.Type.TEMPLATE_RESOURCE.getName())
                    && file == null) {
                errors.addGlobalError(new LocalizableError("error.pei.template.file.empty"));
            }
        }
        super.setTemplateToFolder(template);
        return new ForwardResolution(PlanCMOperationsActionBean.class, "insertFolder");
    }

    private void setTemplateFile() throws IOException, ObjectNotFoundException, JackrabbitException, BusinessException,
            CertitoolsAuthorizationException {
        if (file != null) {
            boolean isImage = false;
            boolean defaultContentType = false;

            if (file.getContentType() != null) {
                if (file.getContentType().startsWith("image")) {
                    isImage = true;
                }
            } else {
                defaultContentType = true;
            }
            if (defaultContentType) {
                super.setTemplateToFolder(
                        new TemplateResource(new Resource("/" + file.getFileName(), file.getFileName(),
                                "application/octet-stream", file.getInputStream(), "" + file.getSize(), isImage)));
            } else {
                super.setTemplateToFolder(
                        new TemplateResource(new Resource("/" + file.getFileName(), file.getFileName(),
                                file.getContentType(), file.getInputStream(), "" + file.getSize(), isImage)));
            }
        } else {
            Folder dbFolder = planService.findFolder(folderId, false, getUserInSession(), getModuleTypeFromEnum());
            //User does not changed template
            if (dbFolder.getTemplate().getName().equals(Template.Type.TEMPLATE_RESOURCE.getName())) {
                TemplateResource dbTemplateResource = (TemplateResource) dbFolder.getTemplate();
                //set old resource
                super.setTemplateToFolder(
                        new TemplateResource(dbTemplateResource.getResource()));
            } else {
                //User changed template to this one, and do not upload image, so create empty template
                super.setTemplateToFolder(new TemplateResource());
            }
        }
    }

    public TemplateResource getTemplate() {
        return template;
    }

    public void setTemplate(TemplateResource template) {
        this.template = template;
    }

    public FileBean getFile() {
        return file;
    }

    public void setFile(FileBean file) {
        this.file = file;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public PlanService getPeiService() {
        return planService;
    }

    public void setPeiService(PlanService planService) {
        this.planService = planService;
    }

    public Boolean getInsertFolderFlag() {
        return insertFolderFlag;
    }

    public void setInsertFolderFlag(Boolean insertFolderFlag) {
        this.insertFolderFlag = insertFolderFlag;
    }
}
