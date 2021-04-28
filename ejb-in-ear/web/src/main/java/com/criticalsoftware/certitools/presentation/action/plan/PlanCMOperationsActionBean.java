/*
 * $Id: PlanCMOperationsActionBean.java,v 1.38 2012/06/01 13:51:51 d-marques Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2012/06/01 13:51:51 $
 * Last changed by : $Author: d-marques $
 */
package com.criticalsoftware.certitools.presentation.action.plan;

import com.aspose.words.SaveFormat;
import com.criticalsoftware.certitools.business.certitools.CompanyService;
import com.criticalsoftware.certitools.business.certitools.ContractService;
import com.criticalsoftware.certitools.business.exception.*;
import com.criticalsoftware.certitools.business.plan.PlanService;
import com.criticalsoftware.certitools.business.plan.TemplateDocxService;
import com.criticalsoftware.certitools.entities.Company;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.TemplateDocx;
import com.criticalsoftware.certitools.entities.jcr.Folder;
import com.criticalsoftware.certitools.entities.jcr.Permission;
import com.criticalsoftware.certitools.entities.jcr.Plan;
import com.criticalsoftware.certitools.entities.jcr.Template;
import com.criticalsoftware.certitools.entities.jcr.Template11Mirror;
import com.criticalsoftware.certitools.entities.jcr.Template2Index;
import com.criticalsoftware.certitools.presentation.action.AbstractActionBean;
import com.criticalsoftware.certitools.presentation.util.DownloadFileResolution;
import com.criticalsoftware.certitools.presentation.util.MenuItem;
import com.criticalsoftware.certitools.presentation.util.PTDateTypeConverter;
import com.criticalsoftware.certitools.presentation.util.TreeOperation;
import com.criticalsoftware.certitools.presentation.util.ValidationUtils;
import com.criticalsoftware.certitools.presentation.util.export.CertitoolsPlanExporter;
import com.criticalsoftware.certitools.presentation.util.export.Plan2Docx;
import com.criticalsoftware.certitools.presentation.util.export.PlanExport;
import com.criticalsoftware.certitools.util.*;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.After;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.localization.LocalizationUtility;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;
import net.sourceforge.stripes.validation.ValidationErrorHandler;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;
import net.sourceforge.stripes.validation.ValidationState;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

/**
 * Emergency Plan Action Bean
 *
 * @author jp-gomes
 */
public class PlanCMOperationsActionBean extends AbstractActionBean implements ValidationErrorHandler {

    @ValidateNestedProperties(value = {
            @Validate(field = "planName", maxlength = 255, required = true, on = {"updatePEIFields"}),
            @Validate(field = "version", maxlength = 255, required = true, on = {"updatePEIFields"}),
            @Validate(field = "authorName", maxlength = 255, required = true, on = {"updatePEIFields"}),
            @Validate(field = "versionDate", required = true, converter = PTDateTypeConverter.class,
                    on = {"updatePEIFields"}),
            @Validate(field = "simulationDate", converter = PTDateTypeConverter.class, on = {"updatePEIFields"})})
    private Plan pei;
    private FileBean companyLogo;
    private FileBean installationPhoto;

    // Control vars
    private Boolean insertFolderFlag;
    private Boolean loadTemplate;
    private String changesInTree;
    private TreeNode nodeUpdated;
    private String treeOperation;
    private Boolean showHelpSection;
    private Boolean loadAdditionalExportInfo;
    private Boolean blockContentTab;
    private Boolean replaceImageMap;

    private Integer tabToOpen;

    @ValidateNestedProperties(value = {
            @Validate(field = "path", required = true, on = "insertTemplate")})
    private Folder folder;
    private List<Long> permissions;
    private List<Long> parentsFolderPermissions;
    private Collection<com.criticalsoftware.certitools.entities.Permission> permissionsList;
    private List<TreeNode> treeNodes;

    @Validate(required = true,
            on = {"updatePEIFields", "expandFolder", "deleteFolder", "validateInsertFolder", "loadFolderInformation",
                  "copyOfflineToOnline", "insertFolderForm", "insertFolderWithTemplate11MirrorForm", "convertLink"})
    private String folderId;
    //@Validate(required = true, on = {"export"})
    private String exportId;
    //@Validate(required = true, on = {"export"})
    private boolean online;

    private Long peiId;

    /* Template */
    public Template template;

    private String param;

    /* Add Folder Link*/
    private List<Contract> contracts;
    private List<Company> companies;
    private Long contractId;
    private Long companyId;
    private List<TreeNode> addFolderLinkTreeNodes;
    private String peiName;
    private Boolean doNotShowTemplatesSelect;
    private Boolean insertFolderLink;
    private List<String> folderReferencesToUpdate;

    private TemplateDocx templateDocx;
    private int exportDocx; // 0 - PDF 1 - DOCX 2 - DOC
    private Boolean exportOnline;
    private Collection<TemplateDocx> templateDocxes;

    @Validate(required = true, on = {"showTemplate"})
    private String templateType;

    @EJBBean(value = "certitools/PlanService")
    private PlanService planService;

    @EJBBean(value = "certitools/CompanyService")
    private CompanyService companyService;

    @EJBBean(value = "certitools/ContractService")
    private ContractService contractService;

    @EJBBean(value = "certitools/TemplateDocxService")
    private TemplateDocxService templateDocxService;

    private static final Logger LOGGER = Logger.getInstance(PlanCMOperationsActionBean.class);

    @DefaultHandler
    @Secure(roles = "peimanager,clientpeimanager")
    public Resolution insertFolderForm()
            throws BusinessException, ObjectNotFoundException, JackrabbitException, CertitoolsAuthorizationException {

        insertFolderLink = false;
        /* Show add Label*/
        insertFolderFlag = true;
        /* Load template Tab*/
        loadTemplate = true;
        /* Set initial values*/
        if (folder == null) {
            folder = new Folder();
            folder.setActive(true);
            folder.setIncludeInMenu(true);
            template = new Template2Index();

            // Set folder order to the max order + 1 of the brothers of this folder
            Folder parentFolder = planService.findFolderAllowed(folderId, true, getUserInSession());
            if (parentFolder.getFolders().size() > 0) {
                int order;
                order = parentFolder.getFolders().get(0).getOrder();
                for (Folder child : parentFolder.getFolders()) {
                    if (child.getOrder() > order) {
                        order = child.getOrder();
                    }
                }

                // protection when order is already at max value
                folder.setOrder((order < Integer.MAX_VALUE) ? order + 1 : order);
            } else {
                folder.setOrder(1);
            }
        }
        //Contract permission List
        permissionsList = planService.findPermissions(getUserInSession(), com.criticalsoftware.certitools.util.PlanUtils.getContractNumberByPath(folderId),
                getModuleTypeFromEnum());
        filterPermissionList(permissionsList);
        parentsFolderPermissions = planService.findParentsFolderPermissions(folderId, true);
        /*no update in tree*/
        treeOperation = TreeOperation.NO_OPERATION.toString();
        tabToOpen = 0;
        showLastPublishAndLastFolderSaveInfo(folderId);

        return new ForwardResolution("/WEB-INF/jsps/plan/planCMFolderMainContent.jsp")
                .addParameter("planModuleType", getPlanModuleType());
    }

    @Secure(roles = "peimanager")
    public Resolution insertFolderWithTemplate11MirrorForm()
            throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException, JackrabbitException {
        setValuesFolderLinkFormInsert();
        return new ForwardResolution("/WEB-INF/jsps/plan/planCMFolderMainContent.jsp")
                .addParameter("planModuleType", getPlanModuleType());
    }

    @Secure(roles = "peimanager")
    public Resolution updateFolderLinkFormInsert()
            throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException, JackrabbitException {
        setValuesFolderLinkFormInsert();
        return new ForwardResolution("/WEB-INF/jsps/plan/planCMFolderWithTemplate11Mirror.jsp")
                .addParameter("planModuleType", getPlanModuleType());
    }

    @Secure(roles = "peimanager,clientpeimanager")
    public Resolution copyOfflineToOnline() {
        LOGGER.info("[PlanCMOperationsActionBean - copyOfflineToOnline] - start copying. FolderId: " + folderId);
        try {
            planService.copyOfflineToOnline(folderId, getUserInSession(), getModuleTypeFromEnum());
            showLastPublishAndLastFolderSaveInfo(folderId);
            treeOperation = TreeOperation.NO_OPERATION.toString();
            getContext().getResponse().setHeader("Stripes-Success", "OK");

            return new ForwardResolution("/WEB-INF/jsps/plan/planCMFolderMainContentFooter.jsp")
                    .addParameter("planModuleType", getPlanModuleType());

        } catch (Exception e) {
            LOGGER.info("[PlanCMOperationsActionBean - copyOfflineToOnline] : " + e.getMessage() + " - " + e);
            LOGGER.error("[PlanCMOperationsActionBean - copyOfflineToOnline]", e);
            getContext().getResponse().setHeader("Stripes-Error", "OK");
            return new ForwardResolution("/WEB-INF/jsps/plan/planCMFolderMainContentFooter.jsp")
                    .addParameter("planModuleType", getPlanModuleType());
        }

    }

    @Secure(roles = "peimanager,clientpeimanager")
    public Resolution updatePEIFields()
            throws IOException, JackrabbitException, BusinessException, ObjectNotFoundException,
            CertitoolsAuthorizationException {

        String companyLogoContentType = null;
        InputStream companyLogoInputStream = null;
        String installationPhotoContentType = null;
        InputStream installationPhotoInputStream = null;
        /* Prepare files to Service*/
        if (companyLogo != null) {
            companyLogoContentType = companyLogo.getContentType();
            companyLogoInputStream = companyLogo.getInputStream();
        }
        if (installationPhoto != null) {
            installationPhotoContentType = installationPhoto.getContentType();
            installationPhotoInputStream = installationPhoto.getInputStream();
        }
        /* Update*/
        pei.setPath(folderId);
        planService.update(pei, companyLogoContentType, companyLogoInputStream, installationPhotoContentType,
                installationPhotoInputStream, getUserInSession(), getModuleTypeFromEnum());
        /* Set Update PEI Tree Operation*/
        treeOperation = TreeOperation.PEI_UPDATE.toString();
        //Reload PEI
        return loadFolderInformation();
    }

    @Secure(roles = "peimanager,clientpeimanager")
    public Resolution loadAdditionalFolderInfo()
            throws ObjectNotFoundException, JackrabbitException, BusinessException, CertitoolsAuthorizationException {

        if (insertFolderFlag) {
            setAttribute("additionalFolderInfoHeader", " ");
            setAttribute("additionalFolderInfoFooter", " ");
        } else {
            Folder tempFolder = planService.findFolder(folderId, false, getUserInSession(), getModuleTypeFromEnum());

            if (tempFolder.getFolderHeader() != null) {
                setAttribute("additionalFolderInfoHeader", tempFolder.getFolderHeader());
            } else {
                setAttribute("additionalFolderInfoHeader", " ");
            }
            if (tempFolder.getFolderFooter() != null) {
                setAttribute("additionalFolderInfoFooter", tempFolder.getFolderFooter());
            } else {
                setAttribute("additionalFolderInfoFooter", " ");
            }
        }
        return new ForwardResolution("/WEB-INF/jsps/plan/planCMFolderAdditionalContent.jsp")
                .addParameter("planModuleType", getPlanModuleType());
    }

    @Secure(roles = "peimanager,clientpeimanager")
    public Resolution showTemplate() {
        template = new Template(templateType);
        return new ForwardResolution("/WEB-INF/jsps/plan/planCMFolderTemplate.jsp")
                .addParameter("planModuleType", getPlanModuleType());
    }

    @Secure(roles = "peimanager,clientpeimanager")
    public Resolution insertFolderPrepareTree() throws BusinessException, ObjectNotFoundException, JackrabbitException,
            CertitoolsAuthorizationException, IOException {
        folder = super.getFolder();

        /* Note: In folder Insert, the selected folder (folderId) is the parentFolder*/
        //Insert new Folder with template
        List<TreeNode> treeNodes = insertFolderWithTemplate();

        if (treeNodes == null) {
            throw new BusinessException("Error showing new inserted Folder");
        }

        folderReferencesToUpdate = new ArrayList<String>();
        for (TreeNode treeNode : treeNodes) {
            if (treeNode.getPathToUpdate() != null) {
                folderReferencesToUpdate.add(treeNode.getPathToUpdate());
            }
        }

        List<TreeNode> treeNodesFiltered = new ArrayList<TreeNode>();
        for (TreeNode tnode : treeNodes) {
            if (tnode.getToShowInExpand() == null || tnode.getToShowInExpand()) {
                treeNodesFiltered.add(tnode);
            }
        }

        /* Set parent Id, for tree node insert*/
        setAttribute("parentId", folderId);

        /* update folderId to new inserted folder*/
        folderId = treeNodesFiltered.get(0).getPath();

        //Build JSON children for tree update
        changesInTree = buidTreeNodes(treeNodesFiltered, treeNodesFiltered.get(0).getPath(), false, true);

        /* Set Insert Folder Tree Operation*/
        treeOperation = TreeOperation.INSERT_FOLDER.toString();

        /* Show inserted folder*/
        return loadFolderInformation();
    }

    @Secure(roles = "peimanager,clientpeimanager")
    public Resolution updateFolderPrepareTree() throws BusinessException, ObjectNotFoundException,
            CertitoolsAuthorizationException, IOException, JackrabbitException {
        folder = super.getFolder();
        List<TreeNode> nodes;

        /* Note: In folder Update, the selected folder (folderId) is the folder itself*/

        //Update current folder
        nodeUpdated = insertFolderWithTemplate().get(0);

        String rootPath = com.criticalsoftware.certitools.util.PlanUtils.findRootPath(nodeUpdated.getPath());
        boolean applyBoldToFolders;
        if (rootPath.equals(com.criticalsoftware.certitools.util.PlanUtils.getParentPath(nodeUpdated.getPath()))) {
            /* Parent is a root so get first level folders*/
            nodes = planService.findOfflineSectionsForMenu(getUserInSession(), rootPath, getModuleTypeFromEnum());
            applyBoldToFolders = true;

        } else {
            /* Sub-Folders, normal method*/
            nodes = planService
                    .findSubfoldersForMenu(getUserInSession(), com.criticalsoftware.certitools.util.PlanUtils.getParentPath(nodeUpdated.getPath()),
                            getModuleTypeFromEnum());
            applyBoldToFolders = false;
        }

        /* Prepare changes in tree*/
        Collections.sort(nodes, new TreeNodeComparatorByOrder());
        changesInTree = buidTreeNodes(nodes, nodeUpdated.getPath(), applyBoldToFolders, false);
        treeOperation = TreeOperation.UPDATE_FOLDER.toString();

        /* update folderId to updated folder*/
        folderId = nodeUpdated.getPath();

        /* Show updated folder*/
        return loadFolderInformation();
    }

    @SuppressWarnings({"unchecked"})
    @Secure(roles = "peimanager,clientpeimanager")
    public Resolution insertFolder() throws Exception {
        ValidationErrors errors = super.getValidationErrors();

        if (errors == null || errors.isEmpty()) {
            super.setFolder(folder);
            if ((insertFolderFlag != null && insertFolderFlag) || (insertFolderLink != null && insertFolderLink)) {
                return new ForwardResolution((Class<? extends ActionBean>)
                        Class.forName("com.criticalsoftware.certitools.presentation.action.plan."
                                + "PlanCM" + template.getName() + "ActionBean"), "insertTemplate")
                        .addParameter("planModuleType", getPlanModuleType());
            } else {
                return new ForwardResolution((Class<? extends ActionBean>)
                        Class.forName("com.criticalsoftware.certitools.presentation.action.plan."
                                + "PlanCM" + template.getName() + "ActionBean"), "updateTemplate")
                        .addParameter("planModuleType", getPlanModuleType());
            }
        } else {
            if (super.getFolder() != null) {
                folder = super.getFolder();
            }
            if (insertFolderLink != null && insertFolderLink) {
                getContext().setEventName("validateInsertFolderLink");
            } else {
                getContext().setEventName("validateInsertFolder");
            }
            return handleValidationErrors(errors);
        }
    }

    @Secure(roles = "peimanager,clientpeimanager")
    public Resolution expandFolder()
            throws JackrabbitException, BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException {
        treeNodes = planService.findSubfoldersForMenu(getUserInSession(), folderId, getModuleTypeFromEnum());
        Collections.sort(treeNodes, new TreeNodeComparatorByOrder());
        getContext().getResponse().setHeader("Stripes-Success", "OK");
        return new StreamingResolution("text/plain", buidTreeNodes(treeNodes, null, false, false));
    }

    @Secure(roles = "peimanager,clientpeimanager")
    public Resolution deleteFolder()
            throws JackrabbitException, ObjectNotFoundException, BusinessException, CertitoolsAuthorizationException {

        /* Note: In folder delete, the selected folder (folderId) is the folder to delete*/
        try {
            folderReferencesToUpdate =
                    planService.deleteFolder(folderId, getUserInSession(), getModuleTypeFromEnum());
        } catch (IsReferencedException e) {
            getContext().getValidationErrors().addGlobalError(
                    new LocalizableError("error.folder.folderMirrorReference"));
            treeOperation = TreeOperation.NO_OPERATION.toString();
            return loadFolderInformation();
        } catch (DeleteException e) {
            getContext().getValidationErrors().addGlobalError(
                    new LocalizableError("error.folder.nodeletepermission"));
            treeOperation = TreeOperation.NO_OPERATION.toString();
            return loadFolderInformation();
        }

        String parentId = com.criticalsoftware.certitools.util.PlanUtils.getParentPath(folderId);
        setAttribute("deletedFolder", folderId);
        setAttribute("parentFolder", parentId);

        /* update folderId to parentFolder (will be the selected one)*/
        folderId = parentId;
        treeOperation = TreeOperation.DELETE_FOLDER.toString();

        return loadFolderInformation();
    }

    @Secure(roles = "peimanager,clientpeimanager")
    public Resolution convertLink()
            throws JackrabbitException, ObjectNotFoundException, BusinessException, CertitoolsAuthorizationException,
            IOException {


        folder = planService.findFolderAllowed(folderId, true, getUserInSession());
        if (folder == null) {
            getContext().getValidationErrors().addGlobalError(
                    new LocalizableError("common.delete"));
        }
        if (folder.getTemplate() != null && folder.getTemplate().getName()
                .equals(Template.Type.TEMPLATE_MIRROR.getName())) {
            Template11Mirror templateMirror = (Template11Mirror) folder.getTemplate();
            planService.deletePlanFolderReference(templateMirror.getSourcePath(), folder);
            planService.convertLinkToNormal(folder);
        }


        nodeUpdated = new TreeNode(folder.getName(), folder.getPath());

        List<TreeNode> nodes;
        String rootPath = com.criticalsoftware.certitools.util.PlanUtils.findRootPath(nodeUpdated.getPath());
        boolean applyBoldToFolders;
        if (rootPath.equals(com.criticalsoftware.certitools.util.PlanUtils.getParentPath(nodeUpdated.getPath()))) {
            /* Parent is a root so get first level folders*/
            nodes = planService.findOfflineSectionsForMenu(getUserInSession(), rootPath, getModuleTypeFromEnum());
            applyBoldToFolders = true;

        } else {
            /* Sub-Folders, normal method*/
            nodes = planService
                    .findSubfoldersForMenu(getUserInSession(), com.criticalsoftware.certitools.util.PlanUtils.getParentPath(nodeUpdated.getPath()),
                            getModuleTypeFromEnum());
            applyBoldToFolders = false;
        }

        /* Prepare changes in tree*/
        Collections.sort(nodes, new TreeNodeComparatorByOrder());
        changesInTree = buidTreeNodes(nodes, nodeUpdated.getPath(), applyBoldToFolders, false);
        treeOperation = TreeOperation.UPDATE_FOLDER.toString();

        /* update folderId to updated folder*/
        folderId = nodeUpdated.getPath();

        /*String parentId = PlanUtils.getParentPath(folderId);
        setAttribute("deletedFolder", folderId);
        setAttribute("parentFolder", parentId);

        folderId = parentId;
        treeOperation = TreeOperation.DELETE_FOLDER.toString();
         */
        peiId = com.criticalsoftware.certitools.util.PlanUtils.getContractNumberByPath(folderId);
        pei = planService.find(getUserInSession(), peiId, getModuleTypeFromEnum());

        loadTemplate = true;
        insertFolderFlag = false;
        insertFolderLink = false;
        //openTreeDirectFolder = true;
        //tabToOpen = 0;
        //refresh = true;
        showLastPublishAndLastFolderSaveInfo(folderId);
        return loadFolderInformation();
        //return new ForwardResolution("/WEB-INF/jsps/plan/planCMFolderMainContent.jsp")
        //        .addParameter("planModuleType", getPlanModuleType()).addParameter("refresh",true);
        //return loadFolderInformation();
    }

    @Secure(roles = "peimanager,clientpeimanager")
    public Resolution loadFolderInformation()
            throws JackrabbitException, ObjectNotFoundException, BusinessException, CertitoolsAuthorizationException {
        if (com.criticalsoftware.certitools.util.PlanUtils.findRootPath(folderId).equals(folderId)) {
            //Root node
            peiId = com.criticalsoftware.certitools.util.PlanUtils.getContractNumberByPath(folderId);
            pei = planService.find(getUserInSession(), peiId, getModuleTypeFromEnum());

            loadTemplate = false;

        } else {
            // Load Folder Information
            folder = planService.findFolder(folderId, false, getUserInSession(), getModuleTypeFromEnum());
            if (folder.getIncludeInMenu() == null) {
                folder.setIncludeInMenu(folder.getIncludeInMenuOrIsNavigable());
            }

            loadTemplate = true;

            //Load permission List
            permissionsList =
                    planService.findPermissions(getUserInSession(), com.criticalsoftware.certitools.util.PlanUtils.getContractNumberByPath(folderId),
                            getModuleTypeFromEnum());
            filterPermissionList(permissionsList);
            //Load folder permissions
            if (folder != null && folder.getPermissions() != null) {
                permissions = new ArrayList<Long>();
                for (Permission permission : folder.getPermissions()) {
                    permissions.add(permission.getPermissionId());
                }
            }

            template = folder.getTemplate();

            if (template != null && template.getName().equals(Template11Mirror.Type.TEMPLATE_MIRROR.getName())) {
                //When template 11 Mirror, build reference link to show in folder properties
                doNotShowTemplatesSelect = true;
                Template11Mirror template11Mirror = (Template11Mirror) template;
                Folder sourceFolder =
                        planService.findFolderAllAllowed(template11Mirror.getSourcePath(), false);
                if (sourceFolder != null) {
                    Contract contract = contractService.findById(template11Mirror.getSourceContractId());
                    setAttribute("pathToShow",
                            buildFolderMirrorLink(contract.getModule().getModuleType().toString(),
                                    contract.getContractDesignation(), contract.getCompany().getName(),
                                    sourceFolder.getPath()));
                } else {
                    //Broken Link: show error message
                    blockContentTab = true;
                    getContext().getValidationErrors()
                            .addGlobalError(new LocalizableError("error.pei.template.11Mirror.brokenLink"));
                    LOGGER.error(
                            "Template11Mirror with broken reference.Template11Mirror Folder path: " + folder.getPath()
                                    + "| Folder Referenced: " + template11Mirror.getSourcePath());
                }
                for (Template.Type type : EnumSet.allOf(Template.Type.class)) {
                    if (type.getName().equals(sourceFolder.getTemplate().getName())) {
                        setAttribute("templateName", type.toString());
                        setAttribute("templateNameSourceFolder", sourceFolder.getTemplate().getName());
                    }
                }
            }

            //Show help section in folders
            if (com.criticalsoftware.certitools.util.PlanUtils.calculateDepth(folderId) == 1) {
                showHelpSection = true;

            } else {
                parentsFolderPermissions = planService.findParentsFolderPermissions(folderId, false);
            }
            peiId = com.criticalsoftware.certitools.util.PlanUtils.getContractNumberByPath(folder.getPath());
            folder.setPath(com.criticalsoftware.certitools.util.PlanUtils.getPathAfterOffline(folder.getPath()));

        }
        /* AJAX call, no update in tree*/
        if (treeOperation == null) {
            treeOperation = TreeOperation.NO_OPERATION.toString();
        }
        insertFolderFlag = false;
        insertFolderLink = false;
        showLastPublishAndLastFolderSaveInfo(folderId);
        return new ForwardResolution("/WEB-INF/jsps/plan/planCMFolderMainContent.jsp")
                .addParameter("planModuleType", getPlanModuleType());
    }


    @Secure(roles = "peimanager,clientpeimanager")
    public Resolution export()
            throws JackrabbitException, ObjectNotFoundException, IOException, PDFException, BusinessException,
            CertitoolsAuthorizationException, AsposeException {

        // check if we selected a DOCX export (>=1) 
        if (exportDocx >= 1) {
            // get template
            File templateDocxFile = templateDocxService.findTemplateDocxFile(templateDocx.getId());
            pei = planService.findFullPEIForExport(getUserInSession(),
                    com.criticalsoftware.certitools.util.PlanUtils.getContractNumberByPath(Utils.decodeURI(exportId)),
                    getModuleTypeFromEnum());

            // check the export format
            int saveFormat;
            if (exportDocx == 2) {
                saveFormat = SaveFormat.DOC;
            } else {
                saveFormat = SaveFormat.DOCX;
            }

            TemplateDocx templateDocxFromDb = templateDocxService.findTemplateDocx(templateDocx.getId());

            Plan2Docx planDocx =
                    new Plan2Docx(exportOnline, pei, templateDocxFromDb, templateDocxFile, getContext().getLocale(),
                            saveFormat);
            PlanExport planExport = planDocx.generateFile();

            return new DownloadFileResolution(planExport.getMimetype(), planExport.getData())
                    .setFilename(planExport.getFilename());

        } else {
            pei = planService
                    .findFullPEIForExport(getUserInSession(),
                            com.criticalsoftware.certitools.util.PlanUtils.getContractNumberByPath(Utils.decodeURI(exportId)),
                            getModuleTypeFromEnum());
            String planExportFilename = (exportOnline ? pei.getPlanNameOnline() : pei.getPlanName());
            planExportFilename = Utils.removeAccentedChars(planExportFilename.replaceAll("/", "_"));

            return new DownloadFileResolution("application/zip",
                    new CertitoolsPlanExporter(exportOnline, pei).zipPEI(getContext()))
                    .setFilename(planExportFilename + ".zip");
        }
    }

    /**
     * Validates Folder properties and makes forward resolution, to template action Bean, for template validation
     *
     * @return - ForwardResolution to selected Template Action Bean
     *
     * @throws JackrabbitException     - error in repository
     * @throws ObjectNotFoundException - error find object
     * @throws ClassNotFoundException  - action Bean to forward does not exist
     */
    @Secure(roles = "peimanager,clientpeimanager")
    public Resolution validateInsertFolder() throws Exception {
        ValidationErrors errors = getContext().getValidationErrors();

        if (folder != null) {
            if (folder.getName() != null) {
                validateFolderNameAndPath(errors);
            } else {
                errors.add("folder.name",
                        new LocalizableError("error.folder.name.empty"));
            }
            if (folder.getOrder() == null) {
                errors.add("folder.order",
                        new LocalizableError("error.folder.order.empty"));
            }
        } else {
            errors.add("folder.name",
                    new LocalizableError("error.folder.name.empty"));
            errors.add("folder.order",
                    new LocalizableError("error.folder.order.empty"));
        }
        super.setValidationErrors(errors);
        return resolveResolution();
    }

    @ValidationMethod(on = "updatePEIFields", when = ValidationState.ALWAYS)
    @Secure(roles = "peimanager,clientpeimanager")
    public void validateUpdatePEIFields(ValidationErrors errors) {

        if (pei != null && pei.getName() != null) {
            String nameValidationResult = ValidationUtils.validateFolderPathName(pei.getPlanName());

            if (nameValidationResult != null) {
                errors.add("pei.planName",
                        new LocalizableError("error.pei.planName", nameValidationResult));
            }
        }
        /* Images Validation*/
        if (companyLogo != null) {
            if (!ValidationUtils.validateImageContentType(companyLogo.getContentType())) {
                errors.addGlobalError(
                        new LocalizableError("error.pei.peiCompanyLogo.invalidFormat"));
            }

            if (!ValidationUtils.validateImageSize(companyLogo.getSize(),
                    Configuration.getInstance().getPEITemplateMaxFileSize())) {
                errors.addGlobalError(
                        new LocalizableError("error.pei.peiCompanyLogo.invalidSize",
                                Configuration.getInstance().getPEITemplateMaxFileSizeInMB()));
            }
        }
        if (installationPhoto != null) {
            if (!ValidationUtils.validateImageContentType(installationPhoto.getContentType())) {

                errors.addGlobalError(
                        new LocalizableError("error.pei.peiInstallationPhoto.invalidFormat"));
            }

            if (!ValidationUtils.validateImageSize(installationPhoto.getSize(),
                    Configuration.getInstance().getPEITemplateMaxFileSize())) {
                errors.addGlobalError(
                        new LocalizableError("error.pei.peiInstallationPhoto.invalidSize",
                                Configuration.getInstance().getPEITemplateMaxFileSizeInMB()));
            }
        }
    }

    @Secure(roles = "peimanager,clientpeimanager")
    public Resolution handleValidationErrors(ValidationErrors validationErrors) throws Exception {
        /* Validation Errors do not update tree*/
        showLastPublishAndLastFolderSaveInfo(folderId);
        treeOperation = TreeOperation.NO_OPERATION.toString();

        if (getContext().getEventName().equals("validateInsertFolderLink")) {
            return insertFolderWithTemplate11MirrorForm();
        }
        if (getContext().getEventName().equals("validateInsertFolder")) {
            //Show help section in folder
            int depth = com.criticalsoftware.certitools.util.PlanUtils.calculateDepth(folderId) + (insertFolderFlag ? 1 : 0);
            if (depth == 1) {
                showHelpSection = true;
            }
            loadTemplate = true;
            //Load permission List
            permissionsList =
                    planService.findPermissions(getUserInSession(), com.criticalsoftware.certitools.util.PlanUtils.getContractNumberByPath(folderId),
                            getModuleTypeFromEnum());
            filterPermissionList(permissionsList);

            if (insertFolderFlag) {
                parentsFolderPermissions = planService.findParentsFolderPermissions(folderId, true);
            } else {
                parentsFolderPermissions = planService.findParentsFolderPermissions(folderId, false);
            }

            /* When error, loads additional export info*/
            loadAdditionalExportInfo = true;
            setAttribute("additionalFolderInfoHeader", folder.getFolderHeader());
            setAttribute("additionalFolderInfoFooter", folder.getFolderFooter());
            return new ForwardResolution("/WEB-INF/jsps/plan/planCMFolderMainContent.jsp")
                    .addParameter("planModuleType", getPlanModuleType());
        }
        if (getContext().getEventName().equals("updatePEIFields")) {
            insertFolderFlag = false;
            loadTemplate = false;
            return new ForwardResolution("/WEB-INF/jsps/plan/planCMFolderMainContent.jsp")
                    .addParameter("planModuleType", getPlanModuleType());
        }
        return null;
    }

    @Before(stages = LifecycleStage.BindingAndValidation)
    public void fillLookupFields() {
        setPlanMenu(MenuItem.Item.SUB_MENU_PEI_ADMIN, MenuItem.Item.SUB_MENU_SAFETY_ADMIN,
                MenuItem.Item.SUB_MENU_PSI_ADMIN, MenuItem.Item.SUB_MENU_GSC_ADMIN);
    }

    @Before(stages = LifecycleStage.EventHandling)
    public void decodeFolderId() throws UnsupportedEncodingException {
        if (folderId != null) {
            // this is a small hack to allow for the plus sign + in the folder names
            // when the url has %2B browsers decode it to +. If we decode directly the + it is replaced by a space
            // but we want to keep the + so we re-encode the + sign.  CERTOOL-523
            folderId = folderId.replaceAll("\\+", "%2B");
            folderId = Utils.decodeURI(folderId);
        }
    }

    @After(stages = LifecycleStage.EventHandling)
    public void encodeFolderId() throws UnsupportedEncodingException {
        if (folderId != null) {
            setAttribute("encodedFolderId", StringEscapeUtils.escapeJavaScript(folderId));
        }
    }

    /**
     * When method "validateTemplate" is not declared in template action Bean, insert folder. otherwise go to Template
     * action bean to validate template
     *
     * @return - Resolution
     *
     * @throws Exception- error occurred
     */
    @SuppressWarnings({"unchecked"})
    @Secure(roles = "peimanager,clientpeimanager")
    private Resolution resolveResolution() throws Exception {

        Class clss = Class.forName("com.criticalsoftware.certitools.presentation.action.plan."
                + "PlanCM" + template.getName() + "ActionBean");

        try {
            clss.getDeclaredMethod("validateTemplate");
            super.setFolder(folder);
            return new ForwardResolution((Class<? extends ActionBean>)
                    Class.forName("com.criticalsoftware.certitools.presentation.action.plan."
                            + "PlanCM" + template.getName() + "ActionBean"), "validateTemplate")
                    .addParameter("planModuleType", getPlanModuleType()).addParameter("replaceImageMap",replaceImageMap);
        } catch (NoSuchMethodException e) {
            return insertFolder();
        }
    }

    @Secure(roles = "peimanager,clientpeimanager")
    public Resolution planExportForm() throws UnsupportedEncodingException, BusinessException, ObjectNotFoundException,
            CertitoolsAuthorizationException, JackrabbitException {
        templateDocxes = templateDocxService
                .findContractTemplatesDocx(com.criticalsoftware.certitools.util.PlanUtils.getContractNumberByPath(Utils.decodeURI(exportId)));

        if (templateDocxes != null && !templateDocxes.isEmpty()) {
            exportDocx = 1;
        } else {
            exportDocx = 0;
        }
        Plan plan = planService
                .find(getUserInSession(), com.criticalsoftware.certitools.util.PlanUtils.getContractNumberByPath(Utils.decodeURI(exportId)), false,
                        getModuleTypeFromEnum());
        if (plan.getPublishedDate() != null) {
            exportOnline = true;
            online = false;
        } else {
            exportOnline = false;
            online = true;
        }
        return new ForwardResolution("/WEB-INF/jsps/plan/planExport.jsp");
    }

    private void filterPermissionList(Collection<com.criticalsoftware.certitools.entities.Permission> permissions) {
        List<com.criticalsoftware.certitools.entities.Permission> list =
                (List<com.criticalsoftware.certitools.entities.Permission>) permissions;

        Integer indexToRemove = null;
        if (list != null && !list.isEmpty()) {
            for (com.criticalsoftware.certitools.entities.Permission permission : list) {
                if (permission.getName().equals(ConfigurationProperties.PERMISSION_PEI_MANAGER.getKey())) {
                    indexToRemove = list.indexOf(permission);
                    break;
                }
            }
        }
        if (indexToRemove != null) {
            list.remove(indexToRemove.intValue());
        }
    }

    private void validateFolderNameAndPath(ValidationErrors errors)
            throws JackrabbitException, ObjectNotFoundException, BusinessException, CertitoolsAuthorizationException {

        String nameValidationResult = ValidationUtils.validateFolderPathName(folder.getName());
        String startNameValidationResult = ValidationUtils.validateFolderPathStartName(folder.getName());
        if (nameValidationResult != null) {
            errors.add("folder.name",
                    new LocalizableError("error.folder.name.invalidChar", nameValidationResult));

        } else if (startNameValidationResult != null) {
            errors.add("folder.name",
                    new LocalizableError("error.folder.name.invalidStartChar", startNameValidationResult));
        } else {
            //No parent node selected
            if (folderId == null) {
                errors.addGlobalError(new LocalizableError("error.folder.selectParent"));

            } else {
                //New Folder so validate if path already exists
                Folder oldFolder = planService.findFolder(folderId, false, getUserInSession(), getModuleTypeFromEnum());
                if (insertFolderFlag) {
                    try {
                        planService.findFolder(
                                folderId + "/folders/" + folder.getName(), false,
                                getUserInSession(), getModuleTypeFromEnum());
                        errors.add("folder.name", new LocalizableError("error.folder.alreadyExist"));
                    } catch (ObjectNotFoundException e) {
                        /* Folder does not exist, so do nothing*/
                    }
                } else {
                    //If new Name, check if it is already in use
                    if (!oldFolder.getName().equals(folder.getName())) {
                        try {
                            if (com.criticalsoftware.certitools.util.PlanUtils.calculateDepth(com.criticalsoftware.certitools.util.PlanUtils.getParentPath(oldFolder.getPath())) == 0) {
                                planService.findFolder(
                                        com.criticalsoftware.certitools.util.PlanUtils.getParentPath(oldFolder.getPath()) + "/offline/" + folder.getName(),
                                        false,
                                        getUserInSession(), getModuleTypeFromEnum());
                            } else {
                                planService.findFolder(
                                        com.criticalsoftware.certitools.util.PlanUtils.getParentPath(oldFolder.getPath()) + "/folders/" + folder.getName(),
                                        false,
                                        getUserInSession(), getModuleTypeFromEnum());
                            }
                            errors.addGlobalError(new LocalizableError("error.folder.alreadyExist"));
                        } catch (ObjectNotFoundException e) {
                            /* Folder does not exist, so do nothing*/
                        }
                    }
                }
            }
        }
    }

    /**
     * Build tree nodes updates for Tree
     *
     * @param treeNodes          - Tree nodes list
     * @param selectPath         - tree node to be open in tree
     * @param applyBoldToFolders - if true, apply bold css property to node name
     * @param isInsert           - when true, load parent and all chindren
     *
     * @return - String that contains updates to apply in tree
     */
    private String buidTreeNodes(List<TreeNode> treeNodes, String selectPath, Boolean applyBoldToFolders,
                                 boolean isInsert) {

        boolean processChildren = false;
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (treeNodes != null && treeNodes.size() > 0) {
            if (treeNodes.size() >= 2 && isInsert) {
                processChildren = true;
                TreeNode baseNode = treeNodes.get(0);
                sb.append("{title: '");
                sb.append(baseNode.getName());
                sb.append("'");
                sb.append(", isFolder: true, isLazy:true, key: '");
                sb.append(baseNode.getPath().replaceAll("\\+", "%2B"));   // encode plus sign + to url encoded
                sb.append("', ");
                if (treeNodes.get(0).getCssToApply() != null) {
                    sb.append("addClass: '");
                    sb.append(treeNodes.get(0).getCssToApply());
                    sb.append("', ");
                }
                sb.append(" expand:true, children:[");
                treeNodes.remove(0);
            }

            for (TreeNode node : treeNodes) {
                sb.append("{title: '");
                sb.append(node.getName());
                sb.append("'");
                sb.append(",addClass: '");
                if (applyBoldToFolders) {
                    sb.append(" strong ");
                }
                if (node.getCssToApply() != null) {
                    sb.append(node.getCssToApply());
                }
                sb.append("'");
                sb.append(", isFolder: true, isLazy:true, key: '");
                sb.append(node.getPath().replaceAll("\\+", "%2B"));  // encode plus sign + to url encoded
                sb.append("'");
                if (selectPath != null && node.getPath().equals(selectPath)) {
                    sb.append(",activate:true, expand:true");
                }
                sb.append("},");
            }
            sb.deleteCharAt(sb.length() - 1);

        }
        sb.append("]");
        if (treeNodes != null && processChildren && isInsert) {
            sb.append("} ]");
        }
        return sb.toString();
    }

    /**
     * Insert or Update folder
     *
     * @return - tree nodes list to be updated
     *
     * @throws IOException             - error
     * @throws JackrabbitException     - error in repository
     * @throws ObjectNotFoundException - folder not exist
     * @throws BusinessException       - error
     * @throws CertitoolsAuthorizationException
     *                                 - user not allowed
     */
    private List<TreeNode> insertFolderWithTemplate()
            throws IOException, JackrabbitException, ObjectNotFoundException, BusinessException,
            CertitoolsAuthorizationException {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        //Set folder Parent Path
        folder.setParentPath(folderId);
        //Set permissions
        if (permissions != null) {
            List<Permission> permissionList = new ArrayList<Permission>();
            for (Long l : permissions) {
                Permission permission = new Permission();
                permission.setPermissionId(l);
                permissionList.add(permission);
            }
            folder.setPermissions(permissionList);
        }
        if ((insertFolderFlag != null && insertFolderFlag) || (insertFolderLink != null && insertFolderLink)) {
            treeNodes =
                    planService.insertFolder(folder, getUserInSession(), com.criticalsoftware.certitools.util.PlanUtils.getContractNumberByPath(folderId),
                            getModuleTypeFromEnum());
        } else {
            folder.setPath(folderId);
            treeNodes.add(
                    planService.updateFolder(folder, getUserInSession(), com.criticalsoftware.certitools.util.PlanUtils.getContractNumberByPath(folderId),
                            getModuleTypeFromEnum()));
        }
        return treeNodes;
    }

    /**
     * Show Last Publish and Last Folder Saved Information
     *
     * @param path - folder path
     *
     * @throws ObjectNotFoundException - folder not found
     * @throws JackrabbitException     - error in repository
     * @throws BusinessException       - error
     * @throws CertitoolsAuthorizationException
     *                                 - user not allowed
     */
    private void showLastPublishAndLastFolderSaveInfo(String path)
            throws ObjectNotFoundException, JackrabbitException, BusinessException, CertitoolsAuthorizationException {

        boolean doNotPublish = false;
        Locale locale = getContext().getLocale();
        //Only export online button if pei was published at least once
        Plan loadedPEI =
                planService.find(getUserInSession(), com.criticalsoftware.certitools.util.PlanUtils.getContractNumberByPath(path), getModuleTypeFromEnum());
        if (loadedPEI.getPublishedDate() != null) {
            exportOnline = true;
        }

        //Only show publish button if pei was saved at least once
        if (loadedPEI.getLastSaveDate() == null) {
            doNotPublish = true;
        }

        long depth = com.criticalsoftware.certitools.util.PlanUtils.calculateDepth(path);
        if (depth == 0) {
            //PEI root
            //Show pei publication info
            setAttribute("peiPublicationInfo",
                    getLastPublishPEIPhrase(loadedPEI.getPublishedDate(), loadedPEI.getPublishedAuthor()));

            //Show pei last partial published info
            if (loadedPEI.getPublishedDate() != null && loadedPEI.getLastParcialPublished() != null) {
                StringBuilder sb = new StringBuilder();
                SimpleDateFormat dateFormat =
                        new SimpleDateFormat(Configuration.getInstance().getDateHourPattern());
                sb.append(LocalizationUtility.getLocalizedFieldName("pei.partialPublishMessage", null, null, locale));
                sb.append(" ");
                sb.append(dateFormat.format(loadedPEI.getLastParcialPublished()));
                setAttribute("peiPublicationParcialInfo", sb.toString());
            }
        } else {
            Folder loadedFolder = planService.findFolder(path, false, getUserInSession(), getModuleTypeFromEnum());

            //Show folder publication info
            setAttribute("peiPublicationInfo",
                    getLastPublishPEIPhrase(loadedFolder.getPublishedDate(), loadedFolder.getPublishedAuthor()));
            /* only show publish button if pei was published at least once */
            if (!doNotPublish) {
                if (loadedPEI.getPublishedDate() == null) {
                    doNotPublish = true;
                } else if (com.criticalsoftware.certitools.util.PlanUtils.calculateDepth(folderId) >= 2) {
                    doNotPublish = true;
                }
            }
            // /Show last saved folder information
            if (loadedFolder.getLastSaveDate() != null) {
                getLastFolderSavePhrase(loadedFolder, locale);

            } else {
                setAttribute("folderLastSavedInfo",
                        LocalizationUtility.getLocalizedFieldName("folder.noPublished", null, null, locale));
            }
        }
        setAttribute("doNotPublish", doNotPublish);
    }

    /**
     * Build last folder saved phrase to show
     *
     * @param loadedFolder - selected folder
     * @param locale       - locale
     *
     * @return - phrase with last saved information
     */
    private String getLastFolderSavePhrase(Folder loadedFolder, Locale locale) {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat dateFormat =
                new SimpleDateFormat(Configuration.getInstance().getDateHourPattern());
        sb.append(LocalizationUtility.getLocalizedFieldName("folder.lastSaveDate", null, null, locale));
        sb.append(" ");
        sb.append(dateFormat.format(loadedFolder.getLastSaveDate()));
        sb.append(" ");
        sb.append(LocalizationUtility.getLocalizedFieldName("common.by", null, null, locale));
        sb.append(" ");
        sb.append(loadedFolder.getLastSaveAuthor());
        setAttribute("folderLastSavedInfo", sb.toString());

        if (loadedFolder.getPublishedDate() == null || loadedFolder.getLastSaveDate()
                .after(loadedFolder.getPublishedDate())) {
            setAttribute("folderLastSavedWarning", true);
        }
        return sb.toString();
    }

    /**
     * Build last plan saved phrase to show
     *
     * @param publishedDate   - publish date
     * @param publishedAuthor - published author
     *
     * @return - phrase
     */
    private String getLastPublishPEIPhrase(Date publishedDate, String publishedAuthor) {
        StringBuilder sb = new StringBuilder();
        Locale locale = getContext().getLocale();

        if (publishedDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(Configuration.getInstance().getDateHourPattern());
            sb.append(LocalizationUtility.getLocalizedFieldName("pei.publishedDate", null, null, locale));
            sb.append(" ");
            sb.append(dateFormat.format(publishedDate));
            sb.append(" ");
            sb.append(LocalizationUtility.getLocalizedFieldName("common.by", null, null, locale));
            sb.append(" ");
            sb.append(publishedAuthor);
            return sb.toString();
        } else {
            return LocalizationUtility.getLocalizedFieldName("pei.noPublished", null, null, locale);
        }
    }

    /**
     * Set all values for Insert link folder
     *
     * @throws BusinessException       - error
     * @throws ObjectNotFoundException - object not found (Contract, etc)
     * @throws CertitoolsAuthorizationException
     *                                 - user cannot acess
     * @throws JackrabbitException     - error in repository
     */
    private void setValuesFolderLinkFormInsert()
            throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException, JackrabbitException {

        //Load List
        Contract selectedContract;
        contracts = new ArrayList<Contract>();
        List<ModuleType> modules = new ArrayList<ModuleType>();
        // TODO-MODULE
        modules.add(ModuleType.PEI);
        modules.add(ModuleType.PRV);
        modules.add(ModuleType.PSI);
        companies = new ArrayList<Company>(companyService.findAllWithPlan(modules));

        if (companyId == null) {
            /* Load the same contract that is selected*/
            selectedContract = contractService.findById(com.criticalsoftware.certitools.util.PlanUtils.getContractNumberByPath(folderId));
            contractId = selectedContract.getId();
            companyId = selectedContract.getCompany().getId();
            contracts =
                    (List<Contract>) contractService
                            .findAllWithUserContractAndPermissionAllowed(companyId, getUserInSession());
        } else {
            contracts =
                    (List<Contract>) contractService
                            .findAllWithUserContractAndPermissionAllowed(companyId, getUserInSession());
        }
        // if contracts list is empty, remove the company
        if (companies.size() == 1 && (contracts == null || contracts.size() <= 0)) {
            companies.remove(0);
        }

        if (contractId == null && contracts.size() > 0) {
            contractId = contracts.get(0).getId();
        }

        selectedContract = contractService.findById(contractId);

        //Filter contracts list to remove LEGISLATION contracts
        List<Contract> finalContracts = new ArrayList<Contract>();
        for (Contract contact : contracts) {
            if (contact.getModule().getModuleType().equals(ModuleType.PEI) || contact.getModule().getModuleType()
                    .equals(ModuleType.PRV) || contact.getModule().getModuleType().equals(ModuleType.PSI)) {
                // TODO-MODULE
                contact.setContractDesignation(
                        (contact.getContractDesignation() + " (" + contact.getModule().getModuleType() + ")"));
                finalContracts.add(contact);
            }
        }
        contracts = finalContracts;

        //Prepare visualization
        loadTemplate = true;
        loadAdditionalExportInfo = false;
        doNotShowTemplatesSelect = true;
        insertFolderLink = true;
        insertFolderFlag = false;
        //find all Tree Nodes without Template11Mirror
        addFolderLinkTreeNodes =
                planService
                        .findAllTreeWithoutTemplate11Mirror(contractId, selectedContract.getModule().getModuleType());
        Plan plan = planService.find(getUserInSession(), contractId, selectedContract.getModule().getModuleType());
        peiName = plan.getPlanName();
        showLastPublishAndLastFolderSaveInfo(folderId);
        template = new Template(Template11Mirror.Type.TEMPLATE_MIRROR.getName());
        treeOperation = TreeOperation.NO_OPERATION.toString();
    }

    /**
     * Build Mirror link to show in folder properties
     *
     * @param contractModuleType  - contract module type
     * @param contractDesignation - contract designation
     * @param companyName         - company name
     * @param completePath        - complete reference path
     *
     * @return - phrase
     */
    private String buildFolderMirrorLink(String contractModuleType,
                                         String contractDesignation, String companyName, String completePath) {
        StringBuilder sb = new StringBuilder();
        sb.append(contractModuleType);
        sb.append(": ");
        sb.append("<strong>");
        sb.append(companyName);
        sb.append("</strong>");
        sb.append(" > ");
        sb.append("<strong>");
        sb.append(contractDesignation);
        sb.append("</strong>");
        sb.append(": ");
        String pathToFolder = PlanUtils.getPathAfterOffline(completePath);
        pathToFolder = pathToFolder.replaceAll("/folders", "");
        sb.append(pathToFolder);

        return sb.toString();
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
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

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public Plan getPei() {
        return pei;
    }

    public void setPei(Plan pei) {
        this.pei = pei;
    }

    public FileBean getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(FileBean companyLogo) {
        this.companyLogo = companyLogo;
    }

    public FileBean getInstallationPhoto() {
        return installationPhoto;
    }

    public void setInstallationPhoto(FileBean installationPhoto) {
        this.installationPhoto = installationPhoto;
    }

    public List<TreeNode> getTreeNodes() {
        return treeNodes;
    }

    public void setTreeNodes(List<TreeNode> treeNodes) {
        this.treeNodes = treeNodes;
    }

    public Boolean getLoadTemplate() {
        return loadTemplate;
    }

    public void setLoadTemplate(Boolean loadTemplate) {
        this.loadTemplate = loadTemplate;
    }

    public Template.Type[] getTypes() {
        final Locale locale = getContext().getLocale();

        Template.Type[] values = Template.Type.values();
        Arrays.sort(values, new Comparator<Template.Type>() {
            public int compare(Template.Type t1, Template.Type t2) {
                String name1 =
                        LocalizationUtility.getLocalizedFieldName("pei.template." + t1, null, null, locale);
                String name2 =
                        LocalizationUtility.getLocalizedFieldName("pei.template." + t2, null, null, locale);
                name1 = name1.replaceAll("�", "I");
                name2 = name2.replaceAll("�", "I");
                return name1.compareTo(name2);
            }
        });
        return values;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public List<Long> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Long> permissions) {
        this.permissions = permissions;
    }

    public String getTreeOperation() {
        return treeOperation;
    }

    public void setTreeOperation(String treeOperation) {
        this.treeOperation = treeOperation;
    }

    public String getChangesInTree() {
        return changesInTree;
    }

    public void setChangesInTree(String changesInTree) {
        this.changesInTree = changesInTree;
    }

    public TreeNode getNodeUpdated() {
        return nodeUpdated;
    }

    public void setNodeUpdated(TreeNode nodeUpdated) {
        this.nodeUpdated = nodeUpdated;
    }

    public Collection<com.criticalsoftware.certitools.entities.Permission> getPermissionsList() {
        return permissionsList;
    }

    public void setPermissionsList(Collection<com.criticalsoftware.certitools.entities.Permission> permissionsList) {
        this.permissionsList = permissionsList;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public Integer getTabToOpen() {
        return tabToOpen;
    }

    public void setTabToOpen(Integer tabToOpen) {
        this.tabToOpen = tabToOpen;
    }

    public String getExportId() {
        return exportId;
    }

    public void setExportId(String exportId) {
        this.exportId = exportId;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public Boolean getShowHelpSection() {
        return showHelpSection;
    }

    public void setShowHelpSection(Boolean showHelpSection) {
        this.showHelpSection = showHelpSection;
    }

    public Long getPeiId() {
        return peiId;
    }

    public void setPeiId(Long peiId) {
        this.peiId = peiId;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public List<Long> getParentsFolderPermissions() {
        return parentsFolderPermissions;
    }

    public void setParentsFolderPermissions(List<Long> parentsFolderPermissions) {
        this.parentsFolderPermissions = parentsFolderPermissions;
    }

    public Boolean getLoadAdditionalExportInfo() {
        return loadAdditionalExportInfo;
    }

    public void setLoadAdditionalExportInfo(Boolean loadAdditionalExportInfo) {
        this.loadAdditionalExportInfo = loadAdditionalExportInfo;
    }

    public PlanService getPlanService() {
        return planService;
    }

    public void setPlanService(PlanService planService) {
        this.planService = planService;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public CompanyService getCompanyService() {
        return companyService;
    }

    public void setCompanyService(CompanyService companyService) {
        this.companyService = companyService;
    }

    public ContractService getContractService() {
        return contractService;
    }

    public void setContractService(ContractService contractService) {
        this.contractService = contractService;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public List<Contract> getContracts() {
        return contracts;
    }

    public void setContracts(List<Contract> contracts) {
        this.contracts = contracts;
    }

    public List<TreeNode> getAddFolderLinkTreeNodes() {
        return addFolderLinkTreeNodes;
    }

    public void setAddFolderLinkTreeNodes(List<TreeNode> addFolderLinkTreeNodes) {
        this.addFolderLinkTreeNodes = addFolderLinkTreeNodes;
    }

    public String getPeiName() {
        return peiName;
    }

    public void setPeiName(String peiName) {
        this.peiName = peiName;
    }

    public Boolean getDoNotShowTemplatesSelect() {
        return doNotShowTemplatesSelect;
    }

    public void setDoNotShowTemplatesSelect(Boolean doNotShowTemplatesSelect) {
        this.doNotShowTemplatesSelect = doNotShowTemplatesSelect;
    }

    public Boolean getInsertFolderLink() {
        return insertFolderLink;
    }

    public void setInsertFolderLink(Boolean insertFolderLink) {
        this.insertFolderLink = insertFolderLink;
    }

    public List<String> getFolderReferencesToUpdate() {
        return folderReferencesToUpdate;
    }

    public void setFolderReferencesToUpdate(List<String> folderReferencesToUpdate) {
        this.folderReferencesToUpdate = folderReferencesToUpdate;
    }

    public Boolean getBlockContentTab() {
        return blockContentTab;
    }

    public void setBlockContentTab(Boolean blockContentTab) {
        this.blockContentTab = blockContentTab;
    }

    public TemplateDocx getTemplateDocx() {
        return templateDocx;
    }

    public void setTemplateDocx(TemplateDocx templateDocx) {
        this.templateDocx = templateDocx;
    }

    public Boolean getExportOnline() {
        return exportOnline;
    }

    public void setExportOnline(Boolean exportOnline) {
        this.exportOnline = exportOnline;
    }

    public Collection<TemplateDocx> getTemplateDocxes() {
        return templateDocxes;
    }

    public void setTemplateDocxes(Collection<TemplateDocx> templateDocxes) {
        this.templateDocxes = templateDocxes;
    }

    public TemplateDocxService getTemplateDocxService() {
        return templateDocxService;
    }

    public void setTemplateDocxService(TemplateDocxService templateDocxService) {
        this.templateDocxService = templateDocxService;
    }

    public int getExportDocx() {
        return exportDocx;
    }

    public void setExportDocx(int exportDocx) {
        this.exportDocx = exportDocx;
    }

    public Boolean getReplaceImageMap() {
        return replaceImageMap;
    }

    public void setReplaceImageMap(Boolean replaceImageMap) {
        this.replaceImageMap = replaceImageMap;
    }
}
