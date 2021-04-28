/*
 * $Id: PlanCMTemplate4PlanClickableActionBean.java,v 1.5 2012/10/12 16:40:01 d-marques Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2012/10/12 16:40:01 $
 * Last changed by : $Author: d-marques $
 */
package com.criticalsoftware.certitools.presentation.action.plan;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.plan.PlanService;
import com.criticalsoftware.certitools.entities.jcr.*;
import com.criticalsoftware.certitools.presentation.util.PlanUtils;
import com.criticalsoftware.certitools.presentation.util.TemplateWithImageUtils;
import com.criticalsoftware.certitools.presentation.util.ValidationUtils;
import com.criticalsoftware.certitools.util.Configuration;
import com.lowagie.text.Image;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.ValidationErrors;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

/**
 * Template 4
 *
 * @author jp-gomes
 */
public class PlanCMTemplate4PlanClickableActionBean extends PlanCMTemplateActionBean {

    private Template4PlanClickable template;
    private String text;
    private FileBean fileTemplate4PlanClickable;
    private String folderId;
    private Boolean insertFolderFlag;
    private Folder folder;
    private Boolean replaceImageMap;

    @EJBBean(value = "certitools/PlanService")
    private PlanService planService;

    @DefaultHandler
    public Resolution insertTemplate()
            throws IOException, BusinessException, ObjectNotFoundException, JackrabbitException,
            CertitoolsAuthorizationException {
        setTemplateFile();
        return new ForwardResolution(PlanCMOperationsActionBean.class, "insertFolderPrepareTree");
    }

    public Resolution updateTemplate()
            throws IOException, BusinessException, ObjectNotFoundException, JackrabbitException,
            CertitoolsAuthorizationException {
        setTemplateFile();
        return new ForwardResolution(PlanCMOperationsActionBean.class, "updateFolderPrepareTree");
    }

    public Resolution validateTemplate() throws IOException, ObjectNotFoundException, JackrabbitException,
            BusinessException, CertitoolsAuthorizationException {
        ValidationErrors errors = super.getValidationErrors();
        super.setFolder(folder);

        if (insertFolderFlag) {
            if (fileTemplate4PlanClickable == null) {
                errors.addGlobalError(new LocalizableError("error.pei.template.file.empty"));
                super.setTemplateToFolder(template);
                return new ForwardResolution(PlanCMOperationsActionBean.class, "insertFolder");
            }
        } else {
            Folder oldFolder = planService.findFolder(folderId, false, getUserInSession(), getModuleTypeFromEnum());
            if (!oldFolder.getTemplate().getName().equals(Template4PlanClickable.Type.TEMPLATE_PLAN_CLICKABLE.getName())
                    && fileTemplate4PlanClickable == null) {
                errors.addGlobalError(new LocalizableError("error.pei.template.file.empty"));
                super.setTemplateToFolder(template);
                return new ForwardResolution(PlanCMOperationsActionBean.class, "insertFolder");
            }
        }

        /* Validate file contentType and size*/
        if (fileTemplate4PlanClickable != null) {
            if (!ValidationUtils.validateImageContentType(fileTemplate4PlanClickable.getContentType())) {
                errors.addGlobalError(new LocalizableError("error.pei.template.file.contentType"));
            }
            if (!ValidationUtils.validateImageSize(fileTemplate4PlanClickable.getSize(),
                    Configuration.getInstance().getPEITemplateMaxFileSize())) {
                errors.addGlobalError(new LocalizableError("error.pei.template.file.size",
                        Configuration.getInstance().getPEITemplateMaxFileSizeInMB()));
            }
        }
        //Validate clickable areas
        if (template.getImageMap() != null) {
            int areaCounter = StringUtils.countMatches(template.getImageMap(), "area");
            if (areaCounter > Configuration.getInstance().getPEITemplateMaxClickableAreas()) {
                errors.addGlobalError(new LocalizableError("error.pei.template.4PlanClickable.imagemap",
                        Configuration.getInstance().getPEITemplateMaxClickableAreas()));
            }
        }
        if (errors != null && !errors.isEmpty() && !insertFolderFlag) {
            //Must reload original image
            Folder folder = planService.findFolder(folderId, false, getUserInSession(), getModuleTypeFromEnum());
            if (folder.getTemplate().getName().equals(Template4PlanClickable.Type.TEMPLATE_PLAN_CLICKABLE.getName())) {
                super.setTemplateToFolder(folder.getTemplate());
            } else {
                super.setTemplateToFolder(template);
            }
        } else {
            super.setTemplateToFolder(template);
        }
        return new ForwardResolution(PlanCMOperationsActionBean.class, "insertFolder");
    }

    private void setTemplateFile() throws IOException, BusinessException, ObjectNotFoundException, JackrabbitException,
            CertitoolsAuthorizationException {
        /* New file to upload so, do not save image map*/
        super.setFolder(folder);
        if (fileTemplate4PlanClickable != null) {
            String folderPath;
            if (insertFolderFlag) {
                folderPath = folderId + "/folders/" + folder.getName();
            } else {
                folderPath = PlanUtils.getParentPath(folderId) + "/folders/" + folder.getName();
            }
            Template4PlanClickable template4PlanClickable =
                    new Template4PlanClickable(new Resource("/" + fileTemplate4PlanClickable.getFileName(),
                            fileTemplate4PlanClickable.getFileName(),
                            fileTemplate4PlanClickable.getContentType(), fileTemplate4PlanClickable.getInputStream(),
                            "" + fileTemplate4PlanClickable.getSize(), true),
                            PlanUtils.convertResourceToHTML(folderPath, getContext().getRequest().getContextPath(),
                                    getModuleTypeFromEnum()));
            if (replaceImageMap == null) {
                replaceImageMap = Boolean.FALSE;
            }
            if (!insertFolderFlag && !replaceImageMap) {

                Folder previousVersion =
                        planService.findFolder(folderId, true, getUserInSession(), getModuleTypeFromEnum());
                try {
                    TemplateWithImage previousTemplate = (TemplateWithImage) previousVersion.getTemplate();
                    String htmlContent = previousTemplate.getImageMap();
                    Image newImage = TemplateWithImageUtils.getImage(fileTemplate4PlanClickable.getInputStream());
                    Image oldImage = TemplateWithImageUtils.getImage(previousTemplate.getResource().getData());

                    String htmlResultContent = TemplateWithImageUtils.resizeImageMap(htmlContent, oldImage, newImage);

                    if(htmlResultContent != null){
                        template4PlanClickable.setImageMap(htmlResultContent);
                    }
                } catch (ClassCastException ex) {
                    //Do nothing
                }
            }
            super.setTemplateToFolder(template4PlanClickable);
            super.setTemplateToFolder(template4PlanClickable);

        } else {
            Folder dbFolder = planService.findFolder(folderId, false, getUserInSession(), getModuleTypeFromEnum());
            //User does not changed template
            if (dbFolder.getTemplate().getName().equals(Template.Type.TEMPLATE_PLAN_CLICKABLE.getName())) {
                Template4PlanClickable dbTemplate4PlanClickable = (Template4PlanClickable) dbFolder.getTemplate();
                //set old resource and update image Map

                super.setTemplateToFolder(
                        new Template4PlanClickable(dbTemplate4PlanClickable.getResource(), template.getImageMap()));
            } else {
                //User changed template to this one, and do not upload image, so create empty template
                super.setTemplateToFolder(new Template4PlanClickable());
            }

        }
    }

    public Template4PlanClickable getTemplate() {
        return template;
    }

    public void setTemplate(Template4PlanClickable template) {
        this.template = template;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public FileBean getFileTemplate4PlanClickable() {
        return fileTemplate4PlanClickable;
    }

    public void setFileTemplate4PlanClickable(FileBean fileTemplate4PlanClickable) {
        this.fileTemplate4PlanClickable = fileTemplate4PlanClickable;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public Boolean getInsertFolderFlag() {
        return insertFolderFlag;
    }

    public void setInsertFolderFlag(Boolean insertFolderFlag) {
        this.insertFolderFlag = insertFolderFlag;
    }

    public PlanService getPeiService() {
        return planService;
    }

    public void setPeiService(PlanService planService) {
        this.planService = planService;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public Boolean getReplaceImageMap() {
        return replaceImageMap;
    }

    public void setReplaceImageMap(Boolean replaceImageMap) {
        this.replaceImageMap = replaceImageMap;
    }
}
