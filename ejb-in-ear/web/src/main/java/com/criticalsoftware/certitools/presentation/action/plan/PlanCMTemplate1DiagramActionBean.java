/*
 * $Id: PlanCMTemplate1DiagramActionBean.java,v 1.6 2012/08/01 18:40:28 d-marques Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2012/08/01 18:40:28 $
 * Last changed by : $Author: d-marques $
 */
package com.criticalsoftware.certitools.presentation.action.plan;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.plan.PlanService;
import com.criticalsoftware.certitools.entities.jcr.Folder;
import com.criticalsoftware.certitools.entities.jcr.Resource;
import com.criticalsoftware.certitools.entities.jcr.Template;
import com.criticalsoftware.certitools.entities.jcr.Template1Diagram;
import com.criticalsoftware.certitools.entities.jcr.TemplateWithImage;
import com.criticalsoftware.certitools.presentation.util.PlanUtils;
import com.criticalsoftware.certitools.presentation.util.TemplateWithImageUtils;
import com.criticalsoftware.certitools.presentation.util.ValidationUtils;
import com.criticalsoftware.certitools.util.Configuration;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.ValidationErrors;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

/**
 * Template 1 - diagram with notes
 *
 * @author pjfsilva
 */
public class PlanCMTemplate1DiagramActionBean extends PlanCMTemplateActionBean {
    private Template1Diagram template;
    private String text;
    private FileBean fileTemplate1;
    private String folderId;
    private Boolean insertFolderFlag;
    private Folder folder;
    private Boolean replaceImageMap;

    @EJBBean(value = "certitools/PlanService")
    private PlanService planService;

    @DefaultHandler
    public Resolution insertTemplate()
            throws IOException, BusinessException, ObjectNotFoundException, JackrabbitException,
            CertitoolsAuthorizationException, BadElementException {
        setTemplateFile();
        return new ForwardResolution(PlanCMOperationsActionBean.class, "insertFolderPrepareTree");
    }

    public Resolution updateTemplate()
            throws IOException, BusinessException, ObjectNotFoundException, JackrabbitException,
            CertitoolsAuthorizationException, BadElementException {
        setTemplateFile();
        return new ForwardResolution(PlanCMOperationsActionBean.class, "updateFolderPrepareTree");
    }

    public Resolution validateTemplate() throws IOException, ObjectNotFoundException, JackrabbitException,
            BusinessException, CertitoolsAuthorizationException {
        ValidationErrors errors = super.getValidationErrors();
        super.setFolder(folder);

        if (insertFolderFlag) {
            if (fileTemplate1 == null) {
                errors.addGlobalError(new LocalizableError("error.pei.template.file.empty"));
                super.setTemplateToFolder(template);
                return new ForwardResolution(PlanCMOperationsActionBean.class, "insertFolder");
            }
        } else {
            Folder oldFolder = planService.findFolder(folderId, false, getUserInSession(), getModuleTypeFromEnum());
            if (!oldFolder.getTemplate().getName().equals(Template1Diagram.Type.TEMPLATE_DIAGRAM.getName())
                    && fileTemplate1 == null) {
                errors.addGlobalError(new LocalizableError("error.pei.template.file.empty"));
                super.setTemplateToFolder(template);
                return new ForwardResolution(PlanCMOperationsActionBean.class, "insertFolder");
            }
        }

        /* Validate file contentType and size*/
        if (fileTemplate1 != null) {
            if (!ValidationUtils.validateImageContentType(fileTemplate1.getContentType())) {
                errors.addGlobalError(new LocalizableError("error.pei.template.file.contentType"));
            }
            if (!ValidationUtils.validateImageSize(fileTemplate1.getSize(),
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
            if (folder.getTemplate().getName().equals(Template1Diagram.Type.TEMPLATE_DIAGRAM.getName())) {
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
            CertitoolsAuthorizationException, BadElementException {
        /* New file to upload so, do not save image map*/
        super.setFolder(folder);
        if (fileTemplate1 != null) {
            String folderPath;
            if (insertFolderFlag) {
                folderPath = folderId + "/folders/" + folder.getName();
            } else {
                folderPath = PlanUtils.getParentPath(folderId) + "/folders/" + folder.getName();
            }

            Template1Diagram template1Diagram = new Template1Diagram(new Resource("/" + fileTemplate1.getFileName(),
                    fileTemplate1.getFileName(),
                    fileTemplate1.getContentType(), fileTemplate1.getInputStream(),
                    "" + fileTemplate1.getSize(), true),
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
                    String htmlContent = template.getImageMap();
                    Image newImage = TemplateWithImageUtils.getImage(fileTemplate1.getInputStream());
                    Image oldImage = TemplateWithImageUtils.getImage(previousTemplate.getResource().getData());

                    String unescapedHtml = StringEscapeUtils.unescapeHtml(htmlContent);

                    String htmlResultContent = TemplateWithImageUtils.resizeImageMap(unescapedHtml, oldImage, newImage);

                    if(htmlResultContent != null){
                        template1Diagram.setImageMap(htmlResultContent);
                    }
                } catch (ClassCastException ex) {
                    //Do nothing
                }
            }
            super.setTemplateToFolder(template1Diagram);

        } else {
            Folder dbFolder = planService.findFolder(folderId, false, getUserInSession(), getModuleTypeFromEnum());
            //User does not changed template
            if (dbFolder.getTemplate().getName().equals(Template.Type.TEMPLATE_DIAGRAM.getName())) {
                Template1Diagram dbTemplate1Diagram = (Template1Diagram) dbFolder.getTemplate();
                //set old resource and update image Map
                super.setTemplateToFolder(
                        new Template1Diagram(dbTemplate1Diagram.getResource(), template.getImageMap()));
            } else {
                //User changed template to this one, and do not upload image, so create empty template
                super.setTemplateToFolder(new Template1Diagram());
            }
        }
    }

    public Template1Diagram getTemplate() {
        return template;
    }

    public void setTemplate(Template1Diagram template) {
        this.template = template;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public FileBean getFileTemplate1() {
        return fileTemplate1;
    }

    public void setFileTemplate1(FileBean fileTemplate1) {
        this.fileTemplate1 = fileTemplate1;
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
