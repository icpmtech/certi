/*
 * $Id: PlanCMTemplate6DocumentsElementActionBean.java,v 1.3 2009/10/16 10:27:27 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/10/16 10:27:27 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.presentation.action.plan;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.plan.PlanService;
import com.criticalsoftware.certitools.entities.jcr.Folder;
import com.criticalsoftware.certitools.entities.jcr.Link;
import com.criticalsoftware.certitools.entities.jcr.Resource;
import com.criticalsoftware.certitools.entities.jcr.Template;
import com.criticalsoftware.certitools.entities.jcr.Template6DocumentsElement;
import com.criticalsoftware.certitools.presentation.util.FileName;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.ValidationErrors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * CM Template 6 Documents Element Action Bean
 *
 * @author jp-gomes
 */
public class PlanCMTemplate6DocumentsElementActionBean extends PlanCMTemplateActionBean {

    private Template6DocumentsElement template;
    private List<FileName> fileNames;
    private Boolean insertFolderFlag;
    private List<Boolean> filesChecker;
    private String folderId;

    @EJBBean(value = "certitools/PlanService")
    private PlanService planService;

    @DefaultHandler
    public Resolution insertTemplate()
            throws BusinessException, IOException, ObjectNotFoundException, JackrabbitException,
            CertitoolsAuthorizationException {
        setTemplateFile();
        return new ForwardResolution(PlanCMOperationsActionBean.class, "insertFolderPrepareTree");
    }

    public Resolution updateTemplate()
            throws BusinessException, IOException, ObjectNotFoundException, JackrabbitException,
            CertitoolsAuthorizationException {
        setTemplateFile();
        return new ForwardResolution(PlanCMOperationsActionBean.class, "updateFolderPrepareTree");
    }

    public Resolution validateTemplate()
            throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException, JackrabbitException {
        ValidationErrors errors = super.getValidationErrors();

        if (template == null) {
            errors.add("template.contentType", new LocalizableError("error.pei.template.6DocumentsElement.type"));
            errors.add("template.contentName", new LocalizableError("error.pei.template.6DocumentsElement.name"));
            errors.add("template.contentDate", new LocalizableError("error.pei.template.6DocumentsElement.date"));
        } else {
            if (template.getContentName() == null) {
                errors.add("template.contentName", new LocalizableError("error.pei.template.6DocumentsElement.name"));
            }
            if (template.getContentType() == null) {
                errors.add("template.contentType", new LocalizableError("error.pei.template.6DocumentsElement.type"));
            }
            if (template.getContentDate() == null) {
                errors.add("template.contentDate", new LocalizableError("error.pei.template.6DocumentsElement.date"));
            }
            //Links validation
            if (template.getLinks() != null) {
                for (Link link : template.getLinks()) {
                    if (link == null) {
                        continue;
                    }
                    if (link.getAlias() == null) {
                        errors.addGlobalError(new LocalizableError("error.pei.template.6DocumentsElement.links"));
                    }
                }
            }
        }
        if (errors != null && !errors.isEmpty()) {
            Folder folder = planService.findFolder(folderId, false, getUserInSession(), getModuleTypeFromEnum());
            if (!insertFolderFlag && folder.getTemplate() != null && folder.getTemplate().getName()
                    .equals(Template.Type.TEMPLATE_DOCUMENTS_ELEMENT.getName())) {
                Template6DocumentsElement template6DocumentsElement = (Template6DocumentsElement) folder.getTemplate();
                template.setResources(template6DocumentsElement.getResources());
            }
        }
        super.setTemplateToFolder(template);
        return new ForwardResolution(PlanCMOperationsActionBean.class, "insertFolder");
    }

    private void setTemplateFile() throws IOException, BusinessException, ObjectNotFoundException, JackrabbitException,
            CertitoolsAuthorizationException {
        Folder folder = planService.findFolder(folderId, false, getUserInSession(), getModuleTypeFromEnum());
        boolean defaultContentType = false;

        //Process Links
        List<Link> links = new ArrayList<Link>();
        if (template.getLinks() != null) {
            int counter = 0;
            for (Link link : template.getLinks()) {
                if (link != null) {
                    links.add(new Link("/link" + counter, "link" + counter, link.getHref(), link.getAlias()));
                    counter++;
                }
            }
        }
        template.setLinks(links);

        if (insertFolderFlag || folder.getTemplate() == null || !folder.getTemplate().getName()
                .equals(Template.Type.TEMPLATE_DOCUMENTS_ELEMENT.getName())) {
            /* Insert All Files*/
            if (fileNames != null) {
                List<Resource> resources = new ArrayList<Resource>();
                for (FileName fileName : fileNames) {
                    if (fileName.getFile() != null) {
                        if (fileName.getFile().getContentType() == null) {
                            defaultContentType = true;
                        }
                        if (defaultContentType) {
                            resources.add(new Resource("/" + fileName.getFile().getFileName(),
                                    fileName.getFile().getFileName(), "application/octet-stream",
                                    fileName.getFile().getInputStream(), "" + fileName.getFile().getSize(), null,
                                    fileName.getAlias()));
                        } else {
                            resources.add(new Resource("/" + fileName.getFile().getFileName(),
                                    fileName.getFile().getFileName(), fileName.getFile().getContentType(),
                                    fileName.getFile().getInputStream(), "" + fileName.getFile().getSize(), null,
                                    fileName.getAlias()));
                        }
                    }
                }
                super.setTemplateToFolder(new Template6DocumentsElement(resources, template));
            } else {
                super.setTemplateToFolder(new Template6DocumentsElement(null, template));
            }
        } else {
            if (filesChecker != null) {
                Template6DocumentsElement dbTemplate = (Template6DocumentsElement) folder.getTemplate();
                List<Resource> resourceList;
                List<Resource> toUpdate = new ArrayList<Resource>();
                int forIndex = 0;
                if (dbTemplate.getResources() == null) {
                    resourceList = new ArrayList<Resource>();
                } else {
                    resourceList = dbTemplate.getResources();
                }
                for (Boolean bool : filesChecker) {
                    //Is in DB resources range
                    if (forIndex + 1 <= resourceList.size()) {
                        //File exists so lets process it
                        if (bool != null) {
                            //Or its a new file or is the same. Verify is there is an entry on "files" with this index. if exists, its a new File
                            if (fileNames != null && fileNames.size() > forIndex && fileNames.get(forIndex) != null
                                    && fileNames.get(forIndex).getFile() != null) {
                                //File is new so we have to replace it
                                if (fileNames.get(forIndex).getFile().getContentType() == null) {
                                    defaultContentType = true;
                                }
                                if (defaultContentType) {
                                    toUpdate.add(new Resource("/" + fileNames.get(forIndex).getFile().getFileName(),
                                            fileNames.get(forIndex).getFile().getFileName(), "application/octet-stream",
                                            fileNames.get(forIndex).getFile().getInputStream(),
                                            "" + fileNames.get(forIndex).getFile().getSize(), null,
                                            fileNames.get(forIndex).getAlias()));
                                } else {
                                    toUpdate.add(new Resource("/" + fileNames.get(forIndex).getFile().getFileName(),
                                            fileNames.get(forIndex).getFile().getFileName(),
                                            fileNames.get(forIndex).getFile().getContentType(),
                                            fileNames.get(forIndex).getFile().getInputStream(),
                                            "" + fileNames.get(forIndex).getFile().getSize(),
                                            null, fileNames.get(forIndex).getAlias()));
                                }
                            } else {
                                //Keep the same file, so lets get it from DB resource List
                                Resource resource = resourceList.get(forIndex);
                                try {
                                    if (fileNames != null && fileNames.get(forIndex) != null) {
                                        resource.setAlias(fileNames.get(forIndex).getAlias());
                                    } else {
                                        resource.setAlias("");
                                    }
                                } catch (IndexOutOfBoundsException e) {
                                    resource.setAlias("");
                                }
                                toUpdate.add(resource);
                            }
                        }
                    } else {
                        //Its to Add new File
                        if (fileNames != null && fileNames.size() > forIndex && fileNames.get(forIndex) != null
                                && fileNames.get(forIndex).getFile() != null) {
                            if (fileNames.get(forIndex).getFile().getContentType() == null) {
                                defaultContentType = true;
                            }
                            if (defaultContentType) {
                                toUpdate.add(new Resource("/" + fileNames.get(forIndex).getFile().getFileName(),
                                        fileNames.get(forIndex).getFile().getFileName(), "application/octet-stream",
                                        fileNames.get(forIndex).getFile().getInputStream(),
                                        "" + fileNames.get(forIndex).getFile().getSize(),
                                        null, fileNames.get(forIndex).getAlias()));
                            } else {
                                toUpdate.add(new Resource("/" + fileNames.get(forIndex).getFile().getFileName(),
                                        fileNames.get(forIndex).getFile().getFileName(),
                                        fileNames.get(forIndex).getFile().getContentType(),
                                        fileNames.get(forIndex).getFile().getInputStream(),
                                        "" + fileNames.get(forIndex).getFile().getSize(),
                                        null, fileNames.get(forIndex).getAlias()));
                            }
                        }
                    }
                    forIndex++;
                }
                super.setTemplateToFolder(new Template6DocumentsElement(toUpdate, template));
            } else {
                //There where no files selected
                super.setTemplateToFolder(new Template6DocumentsElement(null, template));
            }
        }
    }

    public Template6DocumentsElement getTemplate() {
        return template;
    }

    public void setTemplate(Template6DocumentsElement template) {
        this.template = template;
    }

    public List<FileName> getFileNames() {
        return fileNames;
    }

    public void setFileNames(List<FileName> fileNames) {
        this.fileNames = fileNames;
    }

    public Boolean getInsertFolderFlag() {
        return insertFolderFlag;
    }

    public void setInsertFolderFlag(Boolean insertFolderFlag) {
        this.insertFolderFlag = insertFolderFlag;
    }

    public List<Boolean> getFilesChecker() {
        return filesChecker;
    }

    public void setFilesChecker(List<Boolean> filesChecker) {
        this.filesChecker = filesChecker;
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
