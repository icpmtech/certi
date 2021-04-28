/*
 * $Id: PlanServiceEJB.java,v 1.74 2013/12/18 03:50:12 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2013/12/18 03:50:12 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.business.plan;

import com.criticalsoftware.certitools.persistence.certitools.ContractDAO;
import com.criticalsoftware.certitools.persistence.certitools.ModuleDAO;
import com.criticalsoftware.certitools.persistence.certitools.RepositoryDAO;
import com.criticalsoftware.certitools.persistence.certitools.UserDAO;
import com.criticalsoftware.certitools.persistence.plan.PermissionDAO;
import com.criticalsoftware.certitools.persistence.plan.PlanDAO;
import com.criticalsoftware.certitools.persistence.plan.TemplateDocxDAO;
import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.DeleteException;
import com.criticalsoftware.certitools.business.exception.IsReferencedException;
import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.Module;
import com.criticalsoftware.certitools.entities.Permission;
import com.criticalsoftware.certitools.entities.TemplateDocx;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.UserContract;
import com.criticalsoftware.certitools.entities.jcr.Folder;
import com.criticalsoftware.certitools.entities.jcr.FolderMirrorReference;
import com.criticalsoftware.certitools.entities.jcr.Plan;
import com.criticalsoftware.certitools.entities.jcr.Resource;
import com.criticalsoftware.certitools.entities.jcr.RiskAnalysisElement;
import com.criticalsoftware.certitools.entities.jcr.Template;
import com.criticalsoftware.certitools.entities.jcr.Template11Mirror;
import com.criticalsoftware.certitools.entities.jcr.Template12MeansResourcesElement;
import com.criticalsoftware.certitools.entities.jcr.Template1Diagram;
import com.criticalsoftware.certitools.entities.jcr.Template2Index;
import com.criticalsoftware.certitools.entities.jcr.Template3RichText;
import com.criticalsoftware.certitools.entities.jcr.Template4PlanClickable;
import com.criticalsoftware.certitools.entities.jcr.Template5ContactsElement;
import com.criticalsoftware.certitools.entities.jcr.Template6DocumentsElement;
import com.criticalsoftware.certitools.entities.jcr.Template7FAQ;
import com.criticalsoftware.certitools.entities.jcr.Template7FAQElement;
import com.criticalsoftware.certitools.entities.jcr.Template9RichTextWithAttach;
import com.criticalsoftware.certitools.entities.jcr.TemplateResource;
import com.criticalsoftware.certitools.util.ConfigurationProperties;
import com.criticalsoftware.certitools.util.Logger;
import com.criticalsoftware.certitools.util.ModuleType;
import com.criticalsoftware.certitools.util.PaginatedListWrapper;
import com.criticalsoftware.certitools.util.PlanUtils;
import com.criticalsoftware.certitools.util.TreeNode;
import com.criticalsoftware.certitools.util.TreeNodeComparatorByOrder;
import com.criticalsoftware.certitools.util.Utils;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jcr.RepositoryException;
import javax.naming.NamingException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Plan Service
 *
 * @author : lt-rico
 */
@SuppressWarnings("EjbEnvironmentInspection")
@Stateless
@Local(PlanService.class)
@LocalBinding(jndiBinding = "certitools/PlanService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class PlanServiceEJB implements PlanService {

    private static final Logger LOGGER = Logger.getInstance(PlanServiceEJB.class);

    @EJB
    private PlanDAO planDAO;

    @EJB
    private UserDAO userDAO;

    @EJB
    private ContractDAO contractDAO;

    @EJB
    private PermissionDAO permissionDAO;

    @EJB
    private RepositoryDAO repositoryDAO;

    @EJB
    private ModuleDAO moduleDAO;

    @EJB
    private TemplateDocxDAO templateDocxDAO;

    @javax.annotation.Resource
    private SessionContext sessionContext;

    public enum Operation {
        SAVE_WITH_NAME_CHANGE,
        TEMPLATE_CHANGE
    }

    @RolesAllowed(value = "contractmanager")
    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public void insert(Plan pei) throws JackrabbitException {
        planDAO.insert(pei);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"user"})
    public Plan find(User user, Long contractId, boolean loadChildren, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, CertitoolsAuthorizationException {

        if (!isUserAllowedAccessPlan(user, contractId, moduleType)) {
            throw new CertitoolsAuthorizationException("user cannot access Plan");
        }
        Plan pei = planDAO.findPlan(contractId, loadChildren, moduleType);
        if (pei == null) {
            throw new ObjectNotFoundException("[find1] Requested Plan was not found. Plan id: " + contractId,
                    ObjectNotFoundException.Type.PLAN);
        }
        return pei;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"user"})
    public Plan find(User user, Long contractId, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, CertitoolsAuthorizationException {

        if (!isUserAllowedAccessPlan(user, contractId, moduleType)) {
            throw new CertitoolsAuthorizationException("user cannot access plan");
        }
        Plan plan = planDAO.findPlan(contractId, moduleType);
        if (plan == null) {
            throw new ObjectNotFoundException("[find2] Requested plan was not found. Plan id:" + contractId,
                    ObjectNotFoundException.Type.PLAN);
        }
        return plan;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = "user")
    public Plan findPEIWithShowFullListPEI(User userInSession, Long peiId, ModuleType moduleType)
            throws JackrabbitException, CertitoolsAuthorizationException, ObjectNotFoundException {

        if (!isUserAllowedAccessPlan(userInSession, peiId, moduleType)) {

            // check if show full pei company flag is set
            User userInDb = userDAO.findByIdWithContractsPermissionsRoles(userInSession.getId());

            if (userInDb == null) {
                throw new ObjectNotFoundException("Object not found: User", ObjectNotFoundException.Type.USER);
            }

            if (!userInDb.getCompany().isShowFullListPEI()) {
                throw new CertitoolsAuthorizationException("user cannot access Plan with id: " + peiId);
            }
        }
        Plan plan = planDAO.findPlan(peiId, moduleType);
        if (plan == null) {
            throw new ObjectNotFoundException(
                    "[findPEIWithShowFullListPEI] Requested Plan was not found. Plan id:" + peiId,
                    ObjectNotFoundException.Type.PLAN);
        }
        return plan;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public Plan findFullPEI(User user, Long contractId, ModuleType moduleType)
            throws ObjectNotFoundException, JackrabbitException, CertitoolsAuthorizationException {

        if (!isUserAllowedAccessPlan(user, contractId, moduleType)) {
            throw new CertitoolsAuthorizationException("user cannot access Plan");
        }
        Plan pei = planDAO.findFullPEI(contractId, moduleType);
        if (pei == null) {
            throw new ObjectNotFoundException("Plan was not found", ObjectNotFoundException.Type.PLAN);
        }
        return pei;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public Plan findFullPEIForExport(User user, Long contractId, ModuleType moduleType)
            throws ObjectNotFoundException, JackrabbitException, CertitoolsAuthorizationException, BusinessException,
            UnsupportedEncodingException {

        if (!isUserAllowedAccessPlan(user, contractId, moduleType)) {
            throw new CertitoolsAuthorizationException("user cannot access Plan");
        }
        Plan pei = planDAO.findFullPEI(contractId, moduleType);
        if (pei == null) {
            throw new ObjectNotFoundException("Plan was not found", ObjectNotFoundException.Type.PLAN);
        }
        findFullPEIForExportWorker(pei.getOffline());
        findFullPEIForExportWorker(pei.getOnline());
        return pei;
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = "administrator")
    public void deletePlanFolderReferences(long contractId, ModuleType moduleType, String operation)
            throws JackrabbitException, ObjectNotFoundException {

        Plan plan = planDAO.findFullPEI(contractId, moduleType);
        if (plan == null) {
            throw new ObjectNotFoundException("Requested Plan was not found. Plan id: " + contractId,
                    ObjectNotFoundException.Type.PLAN);
        }
        if (operation.equals("BOTH")) {
            deletePlanFolderReferencesWorker(plan.getOffline());
            deletePlanFolderReferencesWorker(plan.getOnline());
        } else if (operation.equals("ONLINE")) {
            deletePlanFolderReferencesWorker(plan.getOnline());
        } else if (operation.equals("OFFLINE")) {
            deletePlanFolderReferencesWorker(plan.getOffline());
        }
    }

    public void deletePlanFolderReference(String path) throws JackrabbitException {
        path = "/" + PlanUtils.ROOT_PLAN_FOLDER + path;
        Folder folder = planDAO.findFolder(path, false);
        if (folder.getFolderMirrorReferences() != null && !folder.getFolderMirrorReferences().isEmpty()) {
            LOGGER.info("Deleting references from path: " + folder.getPath());
            folder.setFolderMirrorReferences(null);
            planDAO.update(folder);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param path       {@inheritDoc}
     * @param linkFolder {@inheritDoc}
     * @throws JackrabbitException {@inheritDoc}
     */
    public void deletePlanFolderReference(String path, Folder linkFolder) throws JackrabbitException {
        Folder folder = planDAO.findFolder(path, false);
        if (folder.getFolderMirrorReferences() != null && !folder.getFolderMirrorReferences().isEmpty()) {
            List<FolderMirrorReference> folderMirrorReferenceList = new ArrayList<FolderMirrorReference>();
            for (FolderMirrorReference mirrorReference : folder.getFolderMirrorReferences()) {
                if (!mirrorReference.getReferencePath().equals(linkFolder.getPath())) {
                    folderMirrorReferenceList.add(mirrorReference);
                } else {
                    LOGGER.info("Deleting references from path: " + folder.getPath());
                }
            }
            folder.setFolderMirrorReferences(folderMirrorReferenceList);
            planDAO.update(folder);
        }
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"peimanager", "clientpeimanager"})
    public List<String> deleteFolder(String path, User userInSession, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, CertitoolsAuthorizationException, BusinessException,
            IsReferencedException, DeleteException {

        List<String> nodesToUpdate = new ArrayList<String>();
        if (!isUserAllowedAccessPlan(userInSession, PlanUtils.getContractNumberByPath(path), moduleType)) {
            throw new CertitoolsAuthorizationException("user cannot acess PEI");
        }

        Folder folder = planDAO.findFolder(path, false);

        //Node exists
        if (folder == null) {
            throw new ObjectNotFoundException("Requested folder was not found. Path: " + path,
                    ObjectNotFoundException.Type.FOLDER);
        }

        List<Folder> childrenFolders = planDAO.findFoldersLoaded(folder.getPath(), false);

        //Check for user permission. If user is clientpeimanager must validate if folder parents and children has template 11 mirror associated
        if (sessionContext.isCallerInRole("clientpeimanager")) {
            //Check to delete folder
            if (folder.getTemplate().getName().equals(Template.Type.TEMPLATE_MIRROR.getName())) {
                throw new DeleteException("clientpeimanager cannot delete folders with mirrors");
            }
            //Check parents
            List<String> pathsToFolder = PlanUtils.findAllPathsToFolder(folder.getPath());
            for (String pathToFolder : pathsToFolder) {
                Folder parentFolder = planDAO.findFolder(pathToFolder, false);
                if (parentFolder != null) {
                    if (parentFolder.getTemplate().getName().equals(Template.Type.TEMPLATE_MIRROR.getName())) {
                        throw new DeleteException("clientpeimanager cannot delete folders with mirrors");
                    }
                }
            }
            //Check children
            if (childrenFolders != null && !childrenFolders.isEmpty()) {
                for (Folder childrenFolder : childrenFolders) {
                    if (childrenFolder.getTemplate().getName().equals(Template.Type.TEMPLATE_MIRROR.getName())) {
                        throw new DeleteException("clientpeimanager cannot delete folders with mirrors");
                    }
                }
            }
        }
        //Must validate if to delete folder has children with references. If it has, delete operation is unauthorized
        if (folder.getFolderMirrorReferences() != null && !folder.getFolderMirrorReferences().isEmpty()) {
            throw new IsReferencedException("Cannot delete folder that has references",
                    IsReferencedException.Type.FOLDER);
        }
        if (childrenFolders != null && !childrenFolders.isEmpty()) {
            for (Folder childrenFolder : childrenFolders) {
                if (childrenFolder.getFolderMirrorReferences() != null && !childrenFolder.getFolderMirrorReferences()
                        .isEmpty()) {
                    throw new IsReferencedException("Cannot delete folder that has references",
                            IsReferencedException.Type.FOLDER);
                }
            }
        }
        //Go to all children, see if it is a Mirror template and update source folders references
        for (Folder childrenFolder : childrenFolders) {
            if (childrenFolder.getTemplate() != null &&
                    childrenFolder.getTemplate().getName().equals(Template11Mirror.Type.TEMPLATE_MIRROR.getName())) {
                int index = 0;
                Template11Mirror template11Mirror = (Template11Mirror) childrenFolder.getTemplate();
                Folder sourceFolder = planDAO.findFolder(template11Mirror.getSourcePath(), false);
                if (sourceFolder != null && sourceFolder.getFolderMirrorReferences() != null && !sourceFolder
                        .getFolderMirrorReferences().isEmpty()) {

                    List<FolderMirrorReference> finalList = new ArrayList<FolderMirrorReference>();
                    for (FolderMirrorReference folderMirrorReference : sourceFolder.getFolderMirrorReferences()) {
                        if (!folderMirrorReference.getReferencePath().equals(childrenFolder.getPath())) {
                            finalList.add(new FolderMirrorReference(index,
                                    folderMirrorReference.getReferenceContractDesignation(),
                                    folderMirrorReference.getReferenceCompanyName(),
                                    folderMirrorReference.getReferencePath()));
                            index++;
                        }
                    }
                    Template toUpdateReferenceTemplate = sourceFolder.getTemplate();
                    sourceFolder.setTemplate(null);
                    sourceFolder.setFolderMirrorReferences(null);
                    planDAO.update(sourceFolder);
                    if (finalList.isEmpty()) {
                        nodesToUpdate.add(sourceFolder.getPath());
                    }
                    sourceFolder.setFolderMirrorReferences(finalList);
                    sourceFolder.setTemplate(toUpdateReferenceTemplate);
                    planDAO.update(sourceFolder);
                }
            }
        }
        planDAO.delete(folder.getPath());
        return nodesToUpdate;
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"peimanager", "clientpeimanager"})
    public TreeNode updateFolder(Folder changedFolder, User userInSession, Long contractId, ModuleType moduleType)
            throws IOException, JackrabbitException, ObjectNotFoundException, BusinessException,
            CertitoolsAuthorizationException {

        if (!isUserAllowedAccessPlan(userInSession, contractId, moduleType)) {
            throw new CertitoolsAuthorizationException("user cannot access Plan");
        }
        Folder folderToUpdate = prepareFolderForUpdate(changedFolder, userInSession);

        if (folderToUpdate.getTemplate() == null) {
            throw new BusinessException("Tying to update folder without template");
        }

        return saveFolderAndMovePath(folderToUpdate);
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"peimanager", "clientpeimanager"})
    public void update(Plan pei, String companyLogoContentType, InputStream companyLogoInputStream,
                       String installationPhotoContentType, InputStream installationPhotoInputStream,
                       User userInSession, ModuleType moduleType)
            throws JackrabbitException, BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException {

        if (!isUserAllowedAccessPlan(userInSession, PlanUtils.getContractNumberByPath(pei.getPath()), moduleType)) {
            throw new CertitoolsAuthorizationException("user cannot acess PEI");
        }
        Plan toUpdatePEI = planDAO.findPlan(PlanUtils.getContractNumberByPath(pei.getPath()), moduleType);

        if (companyLogoContentType != null && companyLogoInputStream != null) {

            Resource companyLogo = new Resource();
            companyLogo.setData(companyLogoInputStream);
            companyLogo.setMimeType(companyLogoContentType);
            toUpdatePEI.setCompanyLogo(companyLogo);
        }

        if (installationPhotoContentType != null && installationPhotoInputStream != null) {

            Resource installationPhoto = new Resource();
            installationPhoto.setData(installationPhotoInputStream);
            installationPhoto.setMimeType(installationPhotoContentType);
            toUpdatePEI.setInstallationPhoto(installationPhoto);
        }

        toUpdatePEI.setAuthorName(pei.getAuthorName());
        toUpdatePEI.setPlanName(pei.getPlanName());
        toUpdatePEI.setSimulationDate(pei.getSimulationDate());
        toUpdatePEI.setVersion(pei.getVersion());
        toUpdatePEI.setVersionDate(pei.getVersionDate());
        toUpdatePEI.setLastSaveDate(new Date());

        planDAO.update(toUpdatePEI);
    }

    @RolesAllowed(value = {"peimanager", "clientpeimanager"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<TreeNode> findOfflineSectionsForMenu(User user, String peiPath, ModuleType moduleType)
            throws JackrabbitException, BusinessException, CertitoolsAuthorizationException, ObjectNotFoundException {

        if (!isUserAllowedAccessPlan(user, PlanUtils.getContractNumberByPath(peiPath), moduleType)) {
            throw new CertitoolsAuthorizationException("user cannot access PEI");
        }
        return planDAO.findOfflineSections(peiPath);
    }

    @RolesAllowed(value = {"peimanager", "clientpeimanager"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<TreeNode> findSubfoldersForMenu(User user, String parentFolderPath, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, BusinessException, CertitoolsAuthorizationException {
        if (!isUserAllowedAccessPlan(user, PlanUtils.getContractNumberByPath(parentFolderPath), moduleType)) {
            throw new CertitoolsAuthorizationException("user cannot access PEI");
        }
        return planDAO.findSubfolders(parentFolderPath);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"peimanager", "clientpeimanager"})
    public Folder findFolder(String pathToFolder, Boolean loadChildren, User user, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, BusinessException, CertitoolsAuthorizationException {
        if (!isUserAllowedAccessPlan(user, PlanUtils.getContractNumberByPath(pathToFolder), moduleType)) {
            throw new CertitoolsAuthorizationException("user cannot acess Plan");
        }
        Folder f = planDAO.findFolder(pathToFolder, loadChildren);
        if (f == null) {
            throw new ObjectNotFoundException("Requested folder was not found " + pathToFolder,
                    ObjectNotFoundException.Type.FOLDER);
        }

        return f;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = "user")
    public Folder findFolderAllAllowed(String pathToFolder, boolean loadChildren)
            throws JackrabbitException, ObjectNotFoundException {
        Folder f = planDAO.findFolder(pathToFolder, loadChildren);
        if (f == null) {
            throw new ObjectNotFoundException("Requested folder was not found " + pathToFolder,
                    ObjectNotFoundException.Type.FOLDER);
        }
        return f;
    }


    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"user"})
    public Folder findFolderAllowed(String pathToFolder, Boolean loadChildren, User userInSession)
            throws JackrabbitException, ObjectNotFoundException, BusinessException, CertitoolsAuthorizationException {
        Folder f = planDAO.findFolder(pathToFolder, loadChildren);
        if (f == null) {
            throw new ObjectNotFoundException("Requested folder was not found. path: " + pathToFolder,
                    ObjectNotFoundException.Type.FOLDER);
        }

        if (sessionContext.isCallerInRole("peimanager") || sessionContext.isCallerInRole("contractmanager")
                || sessionContext.isCallerInRole("administrator")) {
            return f;
        }

        // if user role, check if user has the required permission to access this folder
        // get folder permissions stack (from parents)
        Stack<com.criticalsoftware.certitools.entities.jcr.Permission> permissionStack =
                new Stack<com.criticalsoftware.certitools.entities.jcr.Permission>();

        Folder currentFolder = f;

        while (currentFolder != null) {
            permissionStack.addAll(currentFolder.getPermissions());
            currentFolder = getParent(currentFolder.getPath());
        }

        // check user permissions
        User userInDb = userDAO.findByIdWithContractsPermissionsRoles(userInSession.getId());

        if (userInDb == null) {
            throw new ObjectNotFoundException("Object not found: User", ObjectNotFoundException.Type.USER);
        }

        long contractId = PlanUtils.getContractNumberByPath(pathToFolder);
        UserContract userContract = contractDAO.findUserContract(userInDb.getId(), contractId);

        // check if user is client plan manager of this Plan. If yes, can access everything
        if (isUserClientPlanManager(userContract)) {
            return f;
        }

        // user has permissions?
        if (!isUserPermissionValid(permissionStack, userContract.getPermissions())) {
            throw new CertitoolsAuthorizationException("User cannot access this folder");
        }

        return f;
    }

    @RolesAllowed(value = {"peimanager", "clientpeimanager"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<Resource> findFolderResources(String pathToFolder, boolean isImages, boolean useParentFolder,
                                              ModuleType moduleType)
            throws JackrabbitException, BusinessException {

        List<Resource> resources = new ArrayList<Resource>();
        //Get all brothers and children folders
        //Its a section folder, so must load all resources
        if (PlanUtils.calculateDepth(pathToFolder) == 1) {
            List<TreeNode> sectionNodes = planDAO.findOfflineSections(
                    "/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + moduleType + PlanUtils
                            .getContractNumberByPath(pathToFolder));
            Collections.sort(sectionNodes, new TreeNodeComparatorByOrder());

            if (useParentFolder) {
                for (TreeNode node : sectionNodes) {
                    Folder currentFolder = planDAO.findFolder(node.getPath(), false);
                    if (PlanUtils.isFolderTemplateImage(currentFolder, isImages)) {
                        resources.add(((TemplateResource) currentFolder.getTemplate()).getResource());
                    }

                    resources.addAll(planDAO.findFolderResources(node.getPath(), isImages));
                }
            } else {
                resources = planDAO.findFolderResources(pathToFolder, isImages);
            }
        } else {
            //Get folder parent path to load all folder brothers also
            if (useParentFolder) {
                pathToFolder = PlanUtils.getParentPath(pathToFolder);
            }
            resources = planDAO.findFolderResources(pathToFolder, isImages);
        }
        return resources;
    }

    @RolesAllowed(value = {"peimanager", "clientpeimanager"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<Resource> getResources(String pathToFolder, boolean isImages)
            throws JackrabbitException, BusinessException {
        return planDAO.findFolderResources(pathToFolder, isImages);

    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"peimanager", "clientpeimanager"})
    public void copyOfflineToOnline(String path, User userInSession, ModuleType moduleType)
            throws JackrabbitException,
            ObjectNotFoundException, CertitoolsAuthorizationException, BusinessException {

        LOGGER.info("PlanService.copyOfflineToOnline start method");
        try {

            Date publishDate = new Date();
            long depth = PlanUtils.calculateDepth(path);
            LOGGER.info("PlanService.copyOfflineToOnline PlanUtils.calculateDepth(path): " + path);
            if (depth >= 2) {
                LOGGER.info("PlanDAOEJB.copyOfflineToOnline depth >= 2 depth: " + depth);
                throw new BusinessException("Folder to publish is not Plan folder or section");
            }
            long contractId = PlanUtils.getContractNumberByPath(path);
            LOGGER.info("PlanService.copyOfflineToOnline PlanUtils.getContractNumberByPath(path): " + path);
            if (!isUserAllowedAccessPlan(userInSession, contractId, moduleType)) {
                LOGGER.info("PlanDAOEJB.copyOfflineToOnline !isUserAllowedAccessPlan(userInSession, contractId, moduleType) userInSession: "
                        + userInSession + " contractId " + contractId + " moduleType" + moduleType);
                throw new CertitoolsAuthorizationException("user cannot access Plan");
            }
            Plan pei = planDAO.findPlan(contractId, true, moduleType);
            LOGGER.info("PlanService.copyOfflineToOnline planDAO.findPlan(contractId, true, moduleType): " + contractId + " " + moduleType);
            if (pei == null) {
                LOGGER.info("PlanDAOEJB.copyOfflineToOnline pei == null");
                throw new ObjectNotFoundException("Requested Plan was not found. Plan ID: " + contractId,
                        ObjectNotFoundException.Type.PLAN);
            }

            if (depth == 0) {
                //Full PEI Copy
                //Copy pei properties to online properties
                pei.setPlanNameOnline(pei.getPlanName());
                LOGGER.info("PlanService.copyOfflineToOnline pei.setPlanNameOnline(pei.getPlanName()): " + pei.getPlanName());
                pei.setAuthorNameOnline(pei.getAuthorName());
                LOGGER.info("PlanService.copyOfflineToOnline pei.setAuthorNameOnline(pei.getAuthorName()): " + pei.getAuthorName());
                pei.setSimulationDateOnline(pei.getSimulationDate());
                LOGGER.info("PlanService.copyOfflineToOnline pei.setSimulationDateOnline(pei.getSimulationDate()): " + pei.getSimulationDate());
                pei.setVersionOnline(pei.getVersion());
                LOGGER.info("PlanService.copyOfflineToOnline pei.setVersionOnline(pei.getVersion()): " + pei.getVersion());
                pei.setVersionDateOnline(pei.getVersionDate());
                LOGGER.info("PlanService.copyOfflineToOnline pei.setVersionDateOnline(pei.getVersionDate()): " + pei.getVersionDate());
                pei.setPublishedDate(publishDate);
                LOGGER.info("PlanService.copyOfflineToOnline pei.setPublishedDate(publishDate): " + publishDate);
                pei.setPublishedAuthor(userInSession.getName());
                LOGGER.info("PlanService.copyOfflineToOnline pei.setPublishedAuthor(userInSession.getName()): " + userInSession.getName());
                pei.setLastParcialPublished(null);
                LOGGER.info("PlanService.copyOfflineToOnline pei.setLastParcialPublished(null) ");
                planDAO.update(pei);
                LOGGER.info("PlanService.copyOfflineToOnline planDAO.update(pei) ");
                //update all folders references to online folders
                planDAO.updateFolderPublishInfo(pei.getOffline(), userInSession.getName(), publishDate);
                LOGGER.info("PlanService.copyOfflineToOnline planDAO.updateFolderPublishInfo(pei.getOffline(), userInSession.getName(), publishDate): "
                        + pei.getOffline() + " " + userInSession.getName() + " " + publishDate);
                //Delete online Folder
                planDAO.deleteOnlineFolder(contractId, moduleType);
                LOGGER.info("PlanService.copyOfflineToOnline planDAO.deleteOnlineFolder(contractId, moduleType): " + contractId + " " + moduleType);

                for (Folder folder : pei.getOffline()) {
                    updateOnlineReferences(folder.getPath());
                    LOGGER.info("PlanService.copyOfflineToOnline updateOnlineReferences(folder.getPath()): " + folder.getPath());
                }

            } else {
                //Section copy
                Folder folder = planDAO.findFolder(path, false);
                LOGGER.info("PlanService.copyOfflineToOnline planDAO.findFolder(path, false): " + " " + path);
                if (folder == null) {
                    throw new ObjectNotFoundException("Folder not found for copy", ObjectNotFoundException.Type.FOLDER);
                }
                if (folder.getPublishedRelatedPath() != null) {
                    //Folder was publish at least once so delete online folder
                    planDAO.delete(folder.getPublishedRelatedPath());
                    LOGGER.info("PlanService.copyOfflineToOnline planDAO.delete(folder.getPublishedRelatedPath()): " + folder.getPublishedRelatedPath());
                }
                //update PEI last parcial published date
                pei.setLastParcialPublished(publishDate);
                LOGGER.info("PlanService.copyOfflineToOnline pei.setLastParcialPublished(publishDate): " + publishDate);
                planDAO.update(pei);
                LOGGER.info("PlanService.copyOfflineToOnline planDAO.update(pei) ");
                //update all folders references to online folders
                List<Folder> folders = new ArrayList<Folder>();
                folders.add(folder);
                planDAO.updateFolderPublishInfo(folders, userInSession.getName(), publishDate);
                LOGGER.info("PlanService.copyOfflineToOnline planDAO.updateFolderPublishInfo(folders, userInSession.getName(), publishDate): "
                        +  folders.toString() + " " + userInSession.getName() + " " + publishDate);
                updateOnlineReferences(folder.getPath());
            }
            //Copy PEI
            planDAO.copyOfflineToOnline(path, moduleType);
            LOGGER.info("PlanService.copyOfflineToOnline planDAO.copyOfflineToOnline(path, moduleType) " + path + " " + moduleType);
        } catch (Exception e) {
            LOGGER.error("PlanService copyOfflineToOnline exception, ", e);
            throw new RuntimeException(e);
        }
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"user"})
    public ArrayList<TreeNode> findFoldersTreeAllowed(long contractId, User user, boolean onlineFolder,
                                                      ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, CertitoolsAuthorizationException, BusinessException {
        ArrayList<TreeNode> foldersTree = planDAO.findFoldersTree(contractId, onlineFolder, true, moduleType);

        if (foldersTree == null) {
            throw new ObjectNotFoundException("Online folders not found", ObjectNotFoundException.Type.FOLDER);
        }

        // filter online folders by user permissions
        // 1) check if user is pei manager (certitecna) or administrator or contractmanager (certitecna)
        if (sessionContext.isCallerInRole("peimanager") || sessionContext.isCallerInRole("contractmanager")
                || sessionContext.isCallerInRole("administrator")) {
            return foldersTree;
        }

        // 2) check if user has permission "pei manager" (Client)
        User userInDb = userDAO.findByIdWithContractsRoles(user.getId());

        if (userInDb == null) {
            throw new ObjectNotFoundException("Object not found: User", ObjectNotFoundException.Type.USER);
        }

        UserContract userContract = contractDAO.findUserContract(userInDb.getId(), contractId);

        // check if user contract is valid (within the validity date, etc)
        if (!isUserContractValid(userContract, moduleType)) {
            throw new CertitoolsAuthorizationException("User contract is not valid");
        }

        for (Permission permission : userContract.getPermissions()) {
            if (permission.getName().equals(ConfigurationProperties.PERMISSION_PEI_MANAGER.getKey())) {
                return foldersTree;
            }
        }

        // 3) normal user - filter folders that user can't access
        return filterFoldersByPermissions(foldersTree, userContract.getPermissions());
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"user"})
    public List<Plan> findAllPlansAllowed(User user, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException {
        // if user is peimanager, contractmanager or administrator can see all PEIs
        if (sessionContext.isCallerInRole("peimanager") || sessionContext.isCallerInRole("contractmanager")
                || sessionContext.isCallerInRole("administrator")) {
            List<Plan> planList = planDAO.findAllPlans(moduleType, true);
            Collections.sort(planList);
            return planList;
        }

        // otherwise, if normal user, check which Plans it can access
        User userInDb = userDAO.findByIdWithContractsPermissionsRoles(user.getId());

        if (userInDb == null) {
            throw new ObjectNotFoundException("Object not found: User", ObjectNotFoundException.Type.USER);
        }

        Set<UserContract> userContractSet = userInDb.getUserContract();

        // otherwise, check which contracts are valid, and add the Plans to the list
        ArrayList<Plan> planList = new ArrayList<Plan>();
        Plan planTemp;

        for (UserContract userContract : userContractSet) {
            // check if usercontract is valid
            if (isUserContractValid(userContract, moduleType)) {
                planTemp = planDAO.findPlan(userContract.getContract().getId(), true, moduleType);
                // check if Plan has online content
                if (planTemp != null && planTemp.getOnline() != null && planTemp.getOnline().size() > 0) {
                    planList.add(planTemp);
                }
            }
        }

        if (userInDb.getCompany().isShowFullListPEI()) {
            // get complete list of Plan contracts for this company
            Collection<Contract> contractList =
                    contractDAO.findAllByCompanyAndModule(userInDb.getCompany().getId(), new Module(moduleType));

            for (Contract contract : contractList) {
                if (!isPlanInList(planList, contract.getId())) {
                    planTemp = planDAO.findPlan(contract.getId(), true, moduleType);
                    // check if Plan has online content
                    if (planTemp != null && planTemp.getOnline() != null && planTemp.getOnline().size() > 0) {
                        planTemp.setUserCanAccess(false);
                        planList.add(planTemp);
                    }
                }
            }
        }

        Collections.sort(planList);
        return planList;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"peimanager", "clientpeimanager"})
    public Collection<Permission> findPermissions(User user, long contractId, ModuleType moduleType)
            throws ObjectNotFoundException, CertitoolsAuthorizationException {
        if (!isUserAllowedAccessPlan(user, contractId, moduleType)) {
            throw new CertitoolsAuthorizationException("user cannot access Plan");
        }
        return permissionDAO.find(contractId);
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"peimanager", "clientpeimanager"})
    public void insertPermission(String permission, long contractId, User user, ModuleType moduleType)
            throws BusinessException, CertitoolsAuthorizationException, ObjectNotFoundException {

        if (!isUserAllowedAccessPlan(user, contractId, moduleType)) {
            throw new CertitoolsAuthorizationException("user cannot access Plan");
        }

        if (permissionDAO.findByName(permission, contractId) != null) {
            throw new BusinessException("Permission already exists");
        }
        permissionDAO.insert(new Permission(permission, new Contract(contractId)));
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"peimanager", "clientpeimanager"})
    public void deletePermission(String permission, long contractId, User user, ModuleType moduleType)
            throws ObjectNotFoundException, CertitoolsAuthorizationException {

        if (!isUserAllowedAccessPlan(user, contractId, moduleType)) {
            throw new CertitoolsAuthorizationException("user cannot access Plan");
        }

        Permission p = permissionDAO.findByName(permission, contractId);

        if (p == null) {
            throw new ObjectNotFoundException("Object not found", ObjectNotFoundException.Type.PERMISSION);
        }

        if (p.getUserContracts() != null) {
            deleteUserContractPermission(p.getUserContracts(), p);
        }
        permissionDAO.delete(p);
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"peimanager", "clientpeimanager"})
    public List<TreeNode> insertFolder(Folder newFolder, User userInSession, Long contractId, ModuleType moduleType)
            throws IOException, JackrabbitException, ObjectNotFoundException, BusinessException,
            CertitoolsAuthorizationException {

        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        if (!isUserAllowedAccessPlan(userInSession, contractId, moduleType)) {
            throw new CertitoolsAuthorizationException("user cannot access Plan");
        }
        if (newFolder.getTemplate().getName().equals(Template11Mirror.Type.TEMPLATE_MIRROR.getName())) {
            Template11Mirror template11Mirror = (Template11Mirror) newFolder.getTemplate();
            treeNodes = processTemplate11MirrorInsert(template11Mirror, userInSession);

        } else {
            Folder folderToInsert = prepareFolderForInsert(newFolder, userInSession);
            if (newFolder.getTemplate() == null) {
                folderToInsert.setTemplate(new Template2Index());

            } else {
                folderToInsert.setTemplate(newFolder.getTemplate());
            }
            if (folderToInsert.getTemplate() == null) {
                throw new BusinessException("Tying to insert folder without template");
            }
            planDAO.insert(folderToInsert);
            treeNodes.add(new TreeNode(folderToInsert.getName(), folderToInsert.getPath()));
        }
        return treeNodes;
    }

    @RolesAllowed(value = {"peimanager", "clientpeimanager"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public boolean isPermissionInActivePEI(String permission, long contractId, User user, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, CertitoolsAuthorizationException {

        if (!isUserAllowedAccessPlan(user, contractId, moduleType)) {
            throw new CertitoolsAuthorizationException("user cannot access Plan");
        }

        Permission p = permissionDAO.findByName(permission, contractId);
        if (p == null) {
            throw new ObjectNotFoundException("Object not found", ObjectNotFoundException.Type.PERMISSION);
        }
        return planDAO.isPermissionInActivePEI(p.getId(), contractId, moduleType);
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"peimanager", "clientpeimanager"})
    public boolean isPermissionInActiveUserContract(String permission, long contractId, User user,
                                                    ModuleType moduleType)
            throws ObjectNotFoundException, CertitoolsAuthorizationException {

        if (!isUserAllowedAccessPlan(user, contractId, moduleType)) {
            throw new CertitoolsAuthorizationException("user cannot access Plan");
        }

        Permission p = permissionDAO.findByName(permission, contractId);
        if (p == null) {
            throw new ObjectNotFoundException("Object not found", ObjectNotFoundException.Type.PERMISSION);
        }
        if (p.getUserContracts() != null) {
            for (UserContract userContract : p.getUserContracts()) {
                if (!userContract.getUser().isDeleted() && !userContract.getContract().isDeleted()) {
                    if (userContract.getPermissions().contains(p)) {
                        return true;
                    }
                }
            }
        } else {
            return false;
        }
        return false;
    }

    @RolesAllowed(value = {"user"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public PaginatedListWrapper<RiskAnalysisElement> findRiskAnalysis(
            PaginatedListWrapper<RiskAnalysisElement> paginatedListWrapper,
            String mirrorPath, String path, RiskAnalysisElement riskAnalysisElementToFilter)
            throws JackrabbitException, BusinessException {

        String pathToApply;

        if (!path.equals(mirrorPath)) {
            pathToApply = PlanUtils.simplifyPath(mirrorPath);
        } else {
            pathToApply = PlanUtils.simplifyPath(path);
        }
        path += "/template/riskAnalysis/";

        if (paginatedListWrapper == null) {
            throw new BusinessException("No PaginatedListWrapper initialized ");
        }

        if (paginatedListWrapper.getSortCriterion() == null) {
            paginatedListWrapper.setSortCriterion("product");
        }

        if (paginatedListWrapper.getSortDirection() == null) {
            paginatedListWrapper.setSortDirection(PaginatedListWrapper.Direction.ASCENDING);
        } else {
            if (paginatedListWrapper.getSortDirection().equals(PaginatedListWrapper.Direction.ASC)) {
                paginatedListWrapper.setSortDirection(PaginatedListWrapper.Direction.ASCENDING);
            } else {
                paginatedListWrapper.setSortDirection(PaginatedListWrapper.Direction.DESCENDING);
            }
        }
        int count = planDAO.countAllRiskAnalysis(path, riskAnalysisElementToFilter);
        paginatedListWrapper.setFullListSize(count);

        if (paginatedListWrapper.getExport()) {
            paginatedListWrapper
                    .setList(planDAO.findRiskAnalysis(path, count, 0, paginatedListWrapper.getSortCriterion(),
                            paginatedListWrapper.getSortDirection().value(), null, true));

        } else {
            List<RiskAnalysisElement> elementsList =
                    planDAO.findRiskAnalysis(path, paginatedListWrapper.getResultsPerPage(),
                            paginatedListWrapper.getOffset(), paginatedListWrapper.getSortCriterion(),
                            paginatedListWrapper.getSortDirection().value(), riskAnalysisElementToFilter, false);

            for (RiskAnalysisElement element : elementsList) {
                if (element.getFileFolderLinks() != null && !element.getFileFolderLinks().isEmpty()) {
                    List<String> foldersLinks = new ArrayList<String>();
                    String[] split = element.getFileFolderLinks().split(";");
                    for (String string : split) {
                        foldersLinks.add(pathToApply + "/folders" + string);
                    }
                    element.setFileFolderLinksLists(foldersLinks);
                }
            }
            paginatedListWrapper.setList(elementsList);
        }
        //Place back sort direction values for displayTag
        if (paginatedListWrapper.getSortDirection().equals(PaginatedListWrapper.Direction.ASCENDING)) {
            paginatedListWrapper.setSortDirection(PaginatedListWrapper.Direction.ASC);
        } else {
            paginatedListWrapper.setSortDirection(PaginatedListWrapper.Direction.DESC);
        }

        return paginatedListWrapper;
    }

    @RolesAllowed(value = {"user"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<Folder> findFAQTemplateFolders(String path, User userInSession, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, BusinessException, CertitoolsAuthorizationException,
            UnsupportedEncodingException {
        List<Folder> allFolders = planDAO.findFoldersLoaded(path, true);
        List<Folder> finalList = new ArrayList<Folder>();

        if (allFolders == null) {
            return finalList;
        }
        //Filter by Template
        for (Folder f : allFolders) {
            if (f.getTemplate() instanceof Template7FAQ || f.getTemplate() instanceof Template7FAQElement || f
                    .getTemplate() instanceof Template11Mirror) {
                if (f.getTemplate() instanceof Template7FAQElement) {
                    finalList.add(f);
                } else if (f.getTemplate() instanceof Template7FAQElement) {
                    f.setSpecial(true);
                    finalList.add(f);
                } else if (f.getTemplate() instanceof Template11Mirror) {
                    Template11Mirror template11Mirror = (Template11Mirror) f.getTemplate();
                    Folder sourceFolder = planDAO.findFolder(template11Mirror.getSourcePath(), false);
                    if (sourceFolder != null) {
                        if (sourceFolder.getTemplate() instanceof Template7FAQ) {
                            f.setSpecial(true);
                            f.setTemplate(sourceFolder.getTemplate());
                            finalList.add(f);
                        } else if (sourceFolder.getTemplate() instanceof Template7FAQElement) {
                            f.setTemplate(sourceFolder.getTemplate());
                            Template7FAQElement template7FAQElement = (Template7FAQElement) f.getTemplate();
                            template7FAQElement.setQuestion(parseHTMLTemplate11MirrorWorker(f.getPath(),
                                    template11Mirror.getSourcePath(), template7FAQElement.getQuestion()));
                            template7FAQElement.setAnswer(parseHTMLTemplate11MirrorWorker(f.getPath(),
                                    template11Mirror.getSourcePath(), template7FAQElement.getAnswer()));
                            finalList.add(f);
                        }
                    }
                }
            }
        }
        finalList = filtersFoldersList(finalList, userInSession, path, moduleType);
        return finalList;
    }

    @RolesAllowed(value = {"user"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<String> findDocumentsTemplateFiltersList(String path, User userInSession,
                                                         Template6DocumentsElement elementToLoad, ModuleType moduleType)
            throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException, JackrabbitException {
        List<String> result = new ArrayList<String>();
        List<Folder> documentsElementList = findDocumentsTemplate(path, userInSession, moduleType);

        if (documentsElementList != null) {
            //Must Load type List
            if (elementToLoad == null) {
                for (Folder folder : documentsElementList) {
                    Template6DocumentsElement template = (Template6DocumentsElement) folder.getTemplate();
                    if (!result.contains(template.getContentType())) {
                        result.add(template.getContentType());
                    }
                }
            } else {
                //Load subtype list
                for (Folder folder : documentsElementList) {
                    Template6DocumentsElement template = (Template6DocumentsElement) folder.getTemplate();
                    // check if results is not already inserted and if contentType is correct according to elementToLoad
                    if (!result.contains(template.getContentSubType()) && template.getContentType()
                            .equals(elementToLoad.getContentType())) {
                        if (template.getContentSubType() != null && !template.getContentSubType().isEmpty()) {
                            result.add(template.getContentSubType());
                        }

                    }
                }
            }
        }
        Collections.sort(result);
        result.add(0, "");
        return result;
    }

    @RolesAllowed(value = {"user"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<Folder> findDocumentsTemplateFolders(String path, User userInSession, Template6DocumentsElement filter,
                                                     Boolean isExport, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, BusinessException, CertitoolsAuthorizationException {
        if (filter == null || filter.getContentType().isEmpty() || isExport) {
            return findDocumentsTemplate(path, userInSession, moduleType);
        }

        List<Folder> documentsList = findDocumentsTemplate(path, userInSession, moduleType);
        List<Folder> documentsListFilter = new ArrayList<Folder>();
        for (Folder folder : documentsList) {
            Template6DocumentsElement template = (Template6DocumentsElement) folder.getTemplate();
            //Return entries with filter
            if (template.getContentType().equals(filter.getContentType())) {
                if (filter.getContentSubType() == null || filter.getContentSubType().isEmpty()) {
                    documentsListFilter.add(folder);
                } else {
                    if (filter.getContentSubType().equals(template.getContentSubType())) {
                        documentsListFilter.add(folder);
                    }
                }
            }
        }
        return documentsListFilter;
    }

    @RolesAllowed(value = {"user"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<Folder> findRichTextWithAttachTemplateFolders(String path, User userInSession, ModuleType moduleType)
            throws JackrabbitException, BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException {
        List<Folder> allFolders = new ArrayList<Folder>();
        Folder folder = planDAO.findFolder(path, true);

        for (Folder f : folder.getFolders()) {
            if (f.getActive() && f.getTemplate().getName().equals(Template.Type.TEMPLATE_RESOURCE.getName()) || f
                    .getTemplate().getName().equals(Template.Type.TEMPLATE_MIRROR.getName())) {

                if (f.getTemplate().getName().equals(Template.Type.TEMPLATE_RESOURCE.getName())) {
                    allFolders.add(f);
                } else {
                    Template11Mirror template11Mirror = (Template11Mirror) f.getTemplate();
                    String pathToLoad = template11Mirror.getSourcePath();
                    if (PlanUtils.getOnlineOrOfflineFromPath(f.getPath())
                            .equals("online")) {
                        pathToLoad = pathToLoad.replaceFirst("offline", "online");
                    }
                    Folder referencedFolder = planDAO.findFolder(pathToLoad, false);
                    if (referencedFolder.getTemplate().getName().equals(Template.Type.TEMPLATE_RESOURCE.getName())) {
                        allFolders.add(f);
                    }
                }
            }
        }
        allFolders = filtersFoldersList(allFolders, userInSession, path, moduleType);
        return allFolders;
    }

    @RolesAllowed(value = {"user"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<Folder> findIndexTemplateFolders(String path, User userInSession, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, BusinessException, CertitoolsAuthorizationException {
        List<Folder> allFolders = planDAO.findFoldersByParentFolder(path, true);

        if (allFolders == null) {
            return new ArrayList<Folder>();
        }

        // adjust depth, removing the depth of the parent folder
        int depthParentFolder = PlanUtils.calculateDepth(path);
        for (Folder folder : allFolders) {
            folder.setDepth(folder.getDepth() - depthParentFolder);
        }

        allFolders = filtersFoldersList(allFolders, userInSession, path, moduleType);
        return allFolders;
    }

    @RolesAllowed(value = {"user"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<Folder> findContactsTemplateFolders(String path, User userInSession, String searchPhrase,
                                                    String contactType, ModuleType moduleType)
            throws JackrabbitException, BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException {
        List<Folder> allFolders = planDAO.findFoldersLoaded(path, true);
        List<Folder> finalList = new ArrayList<Folder>();
        if (allFolders == null) {
            return finalList;
        }
        //Filter by Template
        for (Folder f : allFolders) {
            Folder folder = isContactSearchResult(f, searchPhrase, contactType);
            if (folder != null) {
                finalList.add(folder);
            }
        }
        finalList = filtersFoldersList(finalList, userInSession, path, moduleType);
        return finalList;
    }

    @RolesAllowed(value = {"user"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<String> findMeansResourcesTemplateFiltersList(String path, User userInSession, ModuleType moduleType)
            throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException, JackrabbitException {
        List<String> result = new ArrayList<String>();
        List<Folder> foldersList = planDAO.findFoldersLoaded(path, true);

        if (foldersList != null) {
            foldersList = filtersFoldersList(foldersList, userInSession, path, moduleType);

            for (Folder folder : foldersList) {
                Template12MeansResourcesElement template;
                if (folder.getTemplate() instanceof Template12MeansResourcesElement) {
                    template = (Template12MeansResourcesElement) folder.getTemplate();
                    if (!result.contains(template.getResourceType())) {
                        result.add(template.getResourceType());
                    }
                } else if (folder.getTemplate() instanceof Template11Mirror) {
                    Template11Mirror template11Mirror = (Template11Mirror) folder.getTemplate();
                    String pathToLoad = template11Mirror.getSourcePath();
                    if (PlanUtils.getOnlineOrOfflineFromPath(folder.getPath()).equals("online")) {
                        pathToLoad = pathToLoad.replaceFirst("offline", "online");
                    }
                    Folder sourceFolder = planDAO.findFolder(pathToLoad, false);
                    if (sourceFolder != null && (sourceFolder.getTemplate() instanceof Template12MeansResourcesElement)) {
                        template = (Template12MeansResourcesElement) sourceFolder.getTemplate();
                        if (!result.contains(template.getResourceType())) {
                            result.add(template.getResourceType());
                        }
                    }
                }
            }
            Collections.sort(result);
        }
        result.add(0, "");
        return result;
    }

    @RolesAllowed(value = {"user"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<Folder> findMeansResourcesTemplateFolders(String path, User userInSession, String searchPhrase,
                                                          String resourceType, ModuleType moduleType)
            throws JackrabbitException, BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException {
        List<Folder> allFolders = planDAO.findFoldersLoaded(path, true);
        List<Folder> finalList = new ArrayList<Folder>();
        if (allFolders == null) {
            return finalList;
        }
        //Filter list
        for (Folder f : allFolders) {
            Folder folder = isResourceSearchResult(f, searchPhrase, resourceType);
            if (folder != null) {
                finalList.add(folder);
            }
        }
        finalList = filtersFoldersList(finalList, userInSession, path, moduleType);
        return finalList;
    }


    @RolesAllowed(value = {"peimanager", "clientpeimanager"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<TreeNode> findOpenTreeToPath(String path, User user, ModuleType moduleType)
            throws JackrabbitException, BusinessException, CertitoolsAuthorizationException, ObjectNotFoundException {

        if (!isUserAllowedAccessPlan(user, PlanUtils.getContractNumberByPath(path), moduleType)) {
            throw new CertitoolsAuthorizationException("user cannot acess PEI");
        }

        ArrayList<TreeNode> tree = planDAO.findFoldersTree(PlanUtils.getContractNumberByPath(path), false, false,
                moduleType);
        List<TreeNode> toRemove = new ArrayList<TreeNode>();

        for (TreeNode treeNode : tree) {
            if (PlanUtils.calculateDepth(treeNode.getPath()) != 1 && !arePathsRelated(path, treeNode.getPath())) {
                toRemove.add(treeNode);
            }
        }
        tree.removeAll(toRemove);
        return tree;
    }

    @RolesAllowed(value = {"peimanager"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<TreeNode> findAllTreeWithoutTemplate11Mirror(Long contractId, ModuleType moduleType)
            throws JackrabbitException, BusinessException {
        return planDAO.findFoldersTreeWithoutTemplate11Mirror(contractId, moduleType);
    }

    @RolesAllowed(value = {"peimanager", "clientpeimanager"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<Folder> findPlanClickableAndResourcesFolders(String path, ModuleType moduleType)
            throws JackrabbitException, BusinessException {

        List<Folder> allFolders = new ArrayList<Folder>();
        //First PEI sections
        if (PlanUtils.calculateDepth(path) == 1) {
            List<TreeNode> sectionNodes = planDAO.findOfflineSections(
                    "/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + moduleType + PlanUtils.getContractNumberByPath(path));
            Collections.sort(sectionNodes, new TreeNodeComparatorByOrder());
            for (TreeNode node : sectionNodes) {
                allFolders.addAll(planDAO.findFoldersLoaded(node.getPath(), true));
            }
        } else {
            //Get folder parent path to load all folder brothers also
            allFolders = planDAO.findFoldersLoaded(PlanUtils.getParentPath(path), true);
            if (allFolders != null && !allFolders.isEmpty()) {
                allFolders.remove(0);
            }
        }

        List<Folder> toRemoveFolders = new ArrayList<Folder>();
        //Filter by Template
        for (Folder f : allFolders) {
            if (!(f.getTemplate() instanceof TemplateResource || f
                    .getTemplate() instanceof Template4PlanClickable) || !f.getActive()) {
                toRemoveFolders.add(f);
            }
        }
        allFolders.removeAll(toRemoveFolders);
        return allFolders;
    }

    @RolesAllowed(value = {"user"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<String> findRiskAnalysisList(String path, RiskAnalysisElement riskAnalysisElement, boolean ajaxLoad)
            throws JackrabbitException {

        path += "/template/riskAnalysis/";

        List<String> result = new ArrayList<String>();
        List<RiskAnalysisElement> riskAnalysisElements =
                planDAO.findRiskAnalysis(path, 0, 0, null, null, riskAnalysisElement, true);

        for (RiskAnalysisElement riskAE : riskAnalysisElements) {

            if (!ajaxLoad) {
                if (riskAnalysisElement == null) {
                    //Load All products list
                    if (!result.contains(riskAE.getProduct())) {
                        result.add(riskAE.getProduct());
                    }
                } else {
                    if (riskAnalysisElement.getProduct() != null
                            && riskAnalysisElement.getReleaseConditions() == null) {
                        if (!result.contains(riskAE.getReleaseConditions())) {
                            result.add(riskAE.getReleaseConditions());
                        }
                    } else if (riskAnalysisElement.getProduct() != null
                            && riskAnalysisElement.getReleaseConditions() != null) {
                        if (!result.contains(riskAE.getWeather())) {
                            result.add(riskAE.getWeather());
                        }
                    }
                }
            } else {
                if (riskAnalysisElement != null && riskAnalysisElement.getProduct() != null
                        && riskAnalysisElement.getReleaseConditions() == null
                        && riskAnalysisElement.getWeather() == null) {
                    //Load Release Condition List
                    if (!result.contains(riskAE.getReleaseConditions())) {
                        result.add(riskAE.getReleaseConditions());
                    }
                } else if (riskAnalysisElement != null && riskAnalysisElement.getProduct() != null
                        && riskAnalysisElement.getReleaseConditions() != null
                        && riskAnalysisElement.getWeather() == null) {
                    //Load Weather list
                    if (!result.contains(riskAE.getWeather())) {
                        result.add(riskAE.getWeather());
                    }
                }
            }
        }
        //Must sort
        Collections.sort(result);
        result.add(0, "");
        return result;
    }

    @RolesAllowed(value = {"peimanager"})
    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public void copyPlan(Long contractIdSource, Long contractIdTarget, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, IsReferencedException, BusinessException {

        Map<Long, Long> permissionsMap = new HashMap<Long, Long>();

        Plan sourcePlan = planDAO.findPlan(contractIdSource, moduleType);
        if (sourcePlan == null) {
            throw new ObjectNotFoundException("Requested Plan was not found. Plan ID: " + contractIdSource,
                    ObjectNotFoundException.Type.PLAN);
        }
        Plan targetPlan = planDAO.findPlan(contractIdTarget, moduleType);
        if (targetPlan == null) {
            throw new ObjectNotFoundException("Requested Plan was not found. Plan ID: " + contractIdSource,
                    ObjectNotFoundException.Type.PLAN);
        }
        //Keep target pei Attributes
        Plan planAttributesToKeep = new Plan();
        planAttributesToKeep.setAuthorName(targetPlan.getAuthorName());
        planAttributesToKeep.setAuthorNameOnline(targetPlan.getAuthorNameOnline());
        planAttributesToKeep.setVersion(targetPlan.getVersion());
        planAttributesToKeep.setVersionOnline(targetPlan.getVersionOnline());
        planAttributesToKeep.setVersionDate(targetPlan.getVersionDate());
        planAttributesToKeep.setVersionDateOnline(targetPlan.getVersionDateOnline());
        planAttributesToKeep.setSimulationDate(targetPlan.getSimulationDate());
        planAttributesToKeep.setSimulationDateOnline(targetPlan.getSimulationDateOnline());
        planAttributesToKeep.setCompanyLogo(targetPlan.getCompanyLogo());
        planAttributesToKeep.setCompanyLogoOnline(targetPlan.getCompanyLogoOnline());
        planAttributesToKeep.setInstallationPhoto(targetPlan.getInstallationPhoto());
        planAttributesToKeep.setInstallationPhotoOnline(targetPlan.getInstallationPhotoOnline());

        Contract targetContract = contractDAO.findByIdWithUserContract(contractIdTarget);
        if (targetContract == null) {
            throw new ObjectNotFoundException("Contract was not found. Contract ID: " + contractIdTarget,
                    ObjectNotFoundException.Type.CONTRACT);
        }

        //Removing restriction of: Source and Target Plan must not have references and template 11 Mirror templates
        //CERTOOL-538 - Part 1
        /*if (planDAO.isPlanWithFolderReferences(contractIdSource, moduleType)) {
            throw new IsReferencedException("Cannot copy source plan that has referenced folders or templates mirror",
                    IsReferencedException.Type.PLAN_SOURCE);
        }
        */

        if (planDAO.isPlanWithFolderReferences(contractIdTarget, moduleType)) {
            throw new IsReferencedException("Cannot copy source plan that has referenced folders or templates mirror",
                    IsReferencedException.Type.PLAN_TARGET);
        }

        //Go to targetPlan and see if it has Template 11 Mirror templates. If true, must delete references on source folders
        planDAO.deleteSourceFoldersReferences(contractIdTarget, moduleType);

        // remove associations with contract user permissions
        if (targetContract.getUserContract() != null) {
            for (UserContract userContract : targetContract.getUserContract()) {
                userContract.setPermissions(null);
                userDAO.updateUserContract(userContract);
            }
        }
        //clean permission from target
        List<Permission> toRemovePermission = (List<Permission>) permissionDAO.find(contractIdTarget);
        if (toRemovePermission != null) {
            for (Permission permission : toRemovePermission) {
                permissionDAO.delete(permission);
            }
        }
        //Copy source permissions to target, with new Ids
        List<Permission> newTargetPermission = (List<Permission>) permissionDAO.find(contractIdSource);
        if (newTargetPermission != null) {
            for (Permission permission : newTargetPermission) {
                Permission newPermission = permissionDAO.insert(new Permission(permission.getName(), targetContract));
                permissionsMap.put(permission.getId(), newPermission.getId());
            }
        }
        //delete target Plan
        planDAO.delete("/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + moduleType + contractIdTarget);

        //Copy Plan
        planDAO.copyPlan(contractIdSource, contractIdTarget, moduleType);
        //change permissions ids to new ones
        if (!permissionsMap.isEmpty()) {
            Plan changedPEI = planDAO.findFullPEI(contractIdTarget, moduleType);
            updateFolderPermissionsCopyPEI(changedPEI.getOffline(), permissionsMap);
            updateFolderPermissionsCopyPEI(changedPEI.getOnline(), permissionsMap);
        }

        // update Plan
        targetPlan = planDAO.findPlan(contractIdTarget, moduleType);
        targetPlan.setName(contractIdTarget.toString());
        targetPlan.setPlanName(targetContract.getContractDesignation());
        targetPlan.setPlanNameOnline(targetContract.getContractDesignation());
        targetPlan.setAuthorName(planAttributesToKeep.getAuthorName());
        targetPlan.setAuthorNameOnline(planAttributesToKeep.getAuthorNameOnline());
        targetPlan.setVersion(planAttributesToKeep.getVersion());
        targetPlan.setVersionOnline(planAttributesToKeep.getVersionOnline());
        targetPlan.setVersionDate(planAttributesToKeep.getVersionDate());
        targetPlan.setVersionDateOnline(planAttributesToKeep.getVersionDateOnline());
        targetPlan.setSimulationDate(planAttributesToKeep.getSimulationDate());
        targetPlan.setSimulationDateOnline(planAttributesToKeep.getSimulationDateOnline());
        targetPlan.setCompanyLogo(planAttributesToKeep.getCompanyLogo());
        targetPlan.setCompanyLogoOnline(planAttributesToKeep.getCompanyLogoOnline());
        targetPlan.setInstallationPhoto(planAttributesToKeep.getInstallationPhoto());
        targetPlan.setInstallationPhotoOnline(planAttributesToKeep.getInstallationPhotoOnline());
        planDAO.update(targetPlan);

        //Delete target plan docx templates
        targetContract.getTemplatesDocx().size();
        if (targetContract.getTemplatesDocx() != null) {
            for (TemplateDocx templateDocx : targetContract.getTemplatesDocx()) {
                templateDocx.getContracts().remove(targetContract);
                templateDocxDAO.merge(templateDocx);
            }
        }
        //Copy source plan docx templates to target
        Contract sourceContract = contractDAO.findById(contractIdSource);
        sourceContract.getTemplatesDocx().size();
        if (sourceContract.getTemplatesDocx() != null) {
            for (TemplateDocx templateDocx : sourceContract.getTemplatesDocx()) {
                templateDocx.getContracts().add(targetContract);
                templateDocxDAO.merge(templateDocx);
            }
        }

        //(DEPRECATED)We must go to target plan, see if there are template 11 mirror, if exists, go to source folder and add target folder reference
        //insertSourceFolderReference(contractIdTarget, moduleType);
        //CERTOOL-538
        removeReferencesAndLinks(contractIdTarget, moduleType);

        //CERTOOL-538 Remove references from target plan.
        targetPlan = planDAO.findFullPEI(targetContract.getId(), targetContract.getModule().getModuleType());
        if (targetPlan != null) {
            if (targetPlan.getOffline() != null) {
                deletePlanFolderReferencesWorker(targetPlan.getOffline());
            }
            if (targetPlan.getOnline() != null) {
                deletePlanFolderReferencesWorker(targetPlan.getOnline());
            }
        }
    }

    private void removeReferencesAndLinks(Long contractId, ModuleType moduleType)
            throws BusinessException, JackrabbitException {
        Plan plan = planDAO.findFullPEI(contractId, moduleType);
        if (plan.getOnline() != null) {
            removeReferencesAndLinksRecursively(plan.getOnline());
        }
        if (plan.getOffline() != null) {
            removeReferencesAndLinksRecursively(plan.getOffline());
        }
    }

    private void removeReferencesAndLinksRecursively(List<Folder> folders)
            throws JackrabbitException, BusinessException {

        if (folders == null) {
            return;
        }

        for (Folder folder : folders) {
            if (folder.getTemplate() != null && folder.getTemplate().getName()
                    .equals(Template.Type.TEMPLATE_MIRROR.getName())) {

                convertLinkToNormal(folder);

            } else if (folder.getFolderMirrorReferences() != null) {
                //Remove any references to this folder
                folder.setFolderMirrorReferences(null);
            }

            removeReferencesAndLinksRecursively(folder.getFolders());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param folder {@inheritDoc}
     * @throws JackrabbitException {@inheritDoc}
     */
    public void convertLinkToNormal(Folder folder) throws JackrabbitException {

        //Remove the link information before copy the content
        planDAO.delete(folder.getPath());

        //Get the mirror folder so we can see where is the original content
        Template11Mirror linkMetadata = (Template11Mirror) folder.getTemplate();

        //Copy the original content
        planDAO.copyFolder(linkMetadata.getSourcePath(), folder.getPath());

        //Load the metadata of the folder with the content
        Folder originalContent = planDAO.findFolder(linkMetadata.getSourcePath(), true);

        //Set the folder template to be equal as the original
        folder.setTemplate(originalContent.getTemplate());

        planDAO.update(folder);
    }

    @Override
    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public void importPlan(User user, InputStream importFileIS, long contractTargetId, ModuleType moduleType)
            throws IOException, ObjectNotFoundException, CertitoolsAuthorizationException, JackrabbitException,
            NamingException, RepositoryException {
        Plan planTarget = this.find(user, contractTargetId, false, moduleType);

        // read import file and unzip it
        java.io.File tempImportFile = java.io.File.createTempFile("tempImportFile", ".zip");
        OutputStream os = new FileOutputStream(tempImportFile);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = importFileIS.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        importFileIS.close();
        os.close();

        ZipFile zipFile = new ZipFile(tempImportFile);
        InputStream planFileInputStream = null;
        InputStream propertiesInputStream = null;

        Enumeration entries = zipFile.getEntries();
        while (entries.hasMoreElements()) {
            ZipArchiveEntry zipEntry = (ZipArchiveEntry) entries.nextElement();
            if (zipEntry.getName().equalsIgnoreCase(PlanUtils.IMPORT_PROPERTIES_FILE)) {
                propertiesInputStream = zipFile.getInputStream(zipEntry);
            } else if (zipEntry.getName().equalsIgnoreCase(PlanUtils.IMPORT_PLAN_FILE)) {
                planFileInputStream = zipFile.getInputStream(zipEntry);
            }
        }

        // access properties file
        Properties properties = new Properties();
        properties.load(propertiesInputStream);
        properties.getProperty("oldId");

        String relPathImport = "ztempimportplan" + System.currentTimeMillis();
        String absPathImport = "/certitools_plan_root/" + relPathImport;
        String pathImportTarget = planTarget.getPath();


        planDAO.importPlan(planFileInputStream, relPathImport, absPathImport, pathImportTarget);
    }

    public void processImportedPlan(User user, InputStream importFileIS, long contractTargetId, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, CertitoolsAuthorizationException {

        // now we adjust the new imported plan to its new path
        Plan plan = this.findFullPEI(user, contractTargetId, moduleType);
        Long contractSourceId = Long.valueOf(plan.getName());

        // 1) first change planName
        plan.setName("" + contractTargetId);

        //planDAO.update(plan);

        // 2) import and convert all permissions
        // 3) transform all rich text templates to cope with new PEI_ID

        // check all folders
        if (plan.getOnline() != null) {
            plan.getOnline().size();

            for (Folder folder : plan.getOnline()) {
                processImportedPlanFolder(folder, contractTargetId, contractSourceId);
            }
        }
        if (plan.getOffline() != null) {
            plan.getOffline().size();

            for (Folder folder : plan.getOffline()) {
                processImportedPlanFolder(folder, contractTargetId, contractSourceId);
            }
        }
        planDAO.update(plan);
    }

    private void processImportedPlanFolder(Folder folder, long contractTargetId, long contractSourceId)
            throws JackrabbitException {
        boolean updateFolder = false;

        List<com.criticalsoftware.certitools.entities.jcr.Permission> permissions = folder.getPermissions();

        if (permissions != null && !permissions.isEmpty()) {
            // clear permissions
            folder.setPermissions(null);
            updateFolder = true;
        }

        if (folder.getTemplate().getName().equals(Template.Type.TEMPLATE_DIAGRAM.getName())) {
            String text = processImportedPlanFolderTextLinks(((Template1Diagram) folder.getTemplate()).getImageMap(),
                    contractTargetId, contractSourceId);
            ((Template1Diagram) folder.getTemplate()).setImageMap(text);
            updateFolder = true;

        } else if (folder.getTemplate().getName().equals(Template.Type.TEMPLATE_RICH_TEXT.getName())) {
            String text = processImportedPlanFolderTextLinks(((Template3RichText) folder.getTemplate()).getText(),
                    contractTargetId, contractSourceId);
            ((Template3RichText) folder.getTemplate()).setText(text);
            updateFolder = true;

        } else if (folder.getTemplate().getName().equals(Template.Type.TEMPLATE_RICH_TEXT_WITH_ATTACH.getName())) {
            String text =
                    processImportedPlanFolderTextLinks(((Template9RichTextWithAttach) folder.getTemplate()).getText(),
                            contractTargetId, contractSourceId);
            ((Template9RichTextWithAttach) folder.getTemplate()).setText(text);
            updateFolder = true;

        } else if (folder.getTemplate().getName().equals(Template.Type.TEMPLATE_PLAN_CLICKABLE.getName())) {
            String text =
                    processImportedPlanFolderTextLinks(((Template4PlanClickable) folder.getTemplate()).getImageMap(),
                            contractTargetId, contractSourceId);
            ((Template4PlanClickable) folder.getTemplate()).setImageMap(text);
            updateFolder = true;
        } else if (folder.getTemplate().getName().equals(Template.Type.TEMPLATE_FAQ_ELEMENT.getName())) {
            Template7FAQElement template = (Template7FAQElement) folder.getTemplate();
            String text = processImportedPlanFolderTextLinks(template.getQuestion(), contractTargetId, contractSourceId);
            template.setQuestion(text);

            text = processImportedPlanFolderTextLinks(template.getAnswer(), contractTargetId, contractSourceId);
            template.setAnswer(text);
            updateFolder = true;
        }

        if (folder.getFolders().size() != 0) {
            for (Folder subfolder : folder.getFolders()) {
                processImportedPlanFolder(subfolder, contractTargetId, contractSourceId);
            }
        }

        if (updateFolder) {
            planDAO.update(folder);
        }
    }

    /**
     * Replaces all links and references for the old peiId to the new peiId
     *
     * @param text             text to replace
     * @param contractTargetId new peiId
     * @param contractSourceId old peiId
     * @return text with peiId replaced
     */
    private String processImportedPlanFolderTextLinks(String text, long contractTargetId, long contractSourceId) {
        if (text != null) {
            text = text.replaceAll("peiId=" + contractSourceId, "peiId=" + contractTargetId);
        }
        return text;
    }

    @SuppressWarnings({"RedundantIfStatement"})
    public boolean isUserPlanManager(long contractId, User userInSession) throws ObjectNotFoundException {
        if (sessionContext.isCallerInRole("peimanager")) {
            return true;
        }

        User userInDb = userDAO.findByIdWithContractsRoles(userInSession.getId());

        if (userInDb == null) {
            throw new ObjectNotFoundException("Object not found: User", ObjectNotFoundException.Type.USER);
        }

        UserContract userContract = contractDAO.findUserContract(userInDb.getId(), contractId);

        // if user not associated with this PEI, he can't access it
        if (userContract == null) {
            return false;
        }

        // client pei manager of this pei can access all
        if (isUserClientPlanManager(userContract)) {
            return true;
        }

        return false;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<TreeNode> findPlanPermissionFullSchema(User user, long contractId, ModuleType moduleType)
            throws ObjectNotFoundException, JackrabbitException, CertitoolsAuthorizationException {

        if (!isUserAllowedAccessPlan(user, contractId, moduleType)) {
            throw new CertitoolsAuthorizationException("user cannot access plan");
        }
        return planDAO.findTreeNodesForPermissions(contractId, moduleType);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<TreeNode> findPermissionUsages(User user, Long permissionId, ModuleType moduleType)
            throws ObjectNotFoundException, JackrabbitException, CertitoolsAuthorizationException {

        Permission permission = permissionDAO.findById(permissionId);
        if (permission == null) {
            throw new ObjectNotFoundException("Object not found: Permission", ObjectNotFoundException.Type.PERMISSION);
        }
        if (!isUserAllowedAccessPlan(user, permission.getContract().getId(), moduleType)) {
            throw new CertitoolsAuthorizationException("user cannot access plan");
        }
        List<TreeNode> nodes = planDAO.findTreeNodesForPermissions(permission.getContract().getId(), moduleType);

        for (TreeNode node : nodes) {
            if (node.getPermissions() != null && !node.getPermissions().isEmpty()) {
                for (com.criticalsoftware.certitools.entities.jcr.Permission p : node.getPermissions()) {
                    Permission perm = permissionDAO.findById(p.getPermissionId());
                    if (perm == null) {
                        throw new ObjectNotFoundException("Object not found: Permission",
                                ObjectNotFoundException.Type.PERMISSION);
                    }
                    if (perm.getName().equals(permission.getName()) && node.getPermissions().size() == 1) {
                        node.setSpecial(true);
                        break;
                    }
                }
                if (node.getSpecial() == null) {
                    node.setSpecial(false);
                }
            } else {
                //No permissions so user have access
                node.setSpecial(true);
            }
        }
        return nodes;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"peimanager", "clientpeimanager"})
    public List<Long> findParentsFolderPermissions(String path, Boolean considerItself)
            throws JackrabbitException, ObjectNotFoundException {
        List<Long> parentsPermissionsIds = new ArrayList<Long>();
        int length;
        List<String> paths = PlanUtils.findAllPathsToFolder(path);

        if (considerItself) {
            length = paths.size();
        } else {
            length = paths.size() - 1;
        }
        for (int i = 0; i < length; i++) {
            String currentPath = paths.get(i);
            Folder folder = planDAO.findFolder(currentPath, false);
            if (folder == null) {
                throw new ObjectNotFoundException("Object not found: Folder", ObjectNotFoundException.Type.FOLDER);
            }
            if (folder.getPermissions() != null) {
                for (com.criticalsoftware.certitools.entities.jcr.Permission permission : folder.getPermissions()) {
                    if (!parentsPermissionsIds.contains(permission.getPermissionId())) {
                        parentsPermissionsIds.add(permission.getPermissionId());
                    }
                }
            }
        }
        return parentsPermissionsIds;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"user"})
    public List<Folder> findProcedureTemplateFiltersList(String path, User userInSession, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, BusinessException, CertitoolsAuthorizationException {

        List<Folder> result = new ArrayList<Folder>();
        Folder parentFolder = planDAO.findFolder(path, true);
        if (parentFolder == null) {
            throw new ObjectNotFoundException("Object not found: Folder", ObjectNotFoundException.Type.FOLDER);
        }
        if (parentFolder.getFolders() == null) {
            return new ArrayList<Folder>();
        }
        Collections.sort(parentFolder.getFolders());
        for (Folder folder : parentFolder.getFolders()) {
            // configure which templates the procedures indexes
            if (folder.getActive()) {
                if (folder.getTemplate().getName().equals(Template.Type.TEMPLATE_RICH_TEXT.getName())
                        || folder.getTemplate().getName().equals(Template.Type.TEMPLATE_DIAGRAM.getName())
                        || folder.getTemplate().getName()
                        .equals(Template.Type.TEMPLATE_RICH_TEXT_WITH_ATTACH.getName())) {
                    result.add(folder);
                } else if (folder.getTemplate().getName().equals(Template.Type.TEMPLATE_MIRROR.getName())) {
                    Template11Mirror template11Mirror = (Template11Mirror) folder.getTemplate();
                    String pathToLoad = template11Mirror.getSourcePath();
                    if (PlanUtils.getOnlineOrOfflineFromPath(folder.getPath())
                            .equals("online")) {
                        pathToLoad = pathToLoad.replaceFirst("offline", "online");
                    }
                    Folder sourceFolder = planDAO.findFolder(pathToLoad, false);
                    if (sourceFolder.getTemplate().getName().equals(Template.Type.TEMPLATE_RICH_TEXT.getName())
                            || sourceFolder.getTemplate().getName().equals(Template.Type.TEMPLATE_DIAGRAM.getName())
                            || sourceFolder.getTemplate().getName()
                            .equals(Template.Type.TEMPLATE_RICH_TEXT_WITH_ATTACH.getName())) {
                        folder.setTemplate(sourceFolder.getTemplate());
                        result.add(folder);
                    }
                }
            }
        }
        return filtersFoldersList(result, userInSession, path, moduleType);
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"peimanager"})
    public void updateToVersion3() throws JackrabbitException {
        repositoryDAO.updateToVersion3();
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public String parseHTMLTemplate11Mirror(String sourcePath, String referencePath, String text)
            throws UnsupportedEncodingException, BusinessException,
            ObjectNotFoundException, JackrabbitException {
        return parseHTMLTemplate11MirrorWorker(sourcePath, referencePath, text);
    }

    /**
     * Delete folder Mirror references recursively
     *
     * @param folders - folder list
     * @throws JackrabbitException - error in repository
     */
    private void deletePlanFolderReferencesWorker(List<Folder> folders) throws JackrabbitException {
        if (folders != null) {
            for (Folder folder : folders) {
                if (folder.getFolderMirrorReferences() != null && !folder.getFolderMirrorReferences().isEmpty()) {
                    System.out.println("Deleting references from path: " + folder.getPath());
                    folder.setFolderMirrorReferences(null);
                    planDAO.update(folder);
                }
                deletePlanFolderReferencesWorker(folder.getFolders());
            }
        }
    }

    /**
     * Insert Folder Mirror references in plan copy, when copy target plan has template 11 Mirror folders
     *
     * @param contractId - contract Id
     * @param moduleType - module type
     * @throws JackrabbitException - error in repository
     * @throws BusinessException   - error
     */
    private void insertSourceFolderReference(long contractId, ModuleType moduleType)
            throws JackrabbitException, BusinessException {
        Plan plan = planDAO.findFullPEI(contractId, moduleType);
        if (plan.getOnline() != null) {
            insertSourceFolderReferenceWorker(plan.getOnline());
        }
        if (plan.getOffline() != null) {
            insertSourceFolderReferenceWorker(plan.getOffline());
        }
    }

    /**
     * Insert Folder Mirror Worker (see insertSourceFolderReference method javadocs)
     *
     * @param folders - folder to update
     * @throws JackrabbitException - error in repository
     * @throws BusinessException   - error
     */
    private void insertSourceFolderReferenceWorker(List<Folder> folders) throws JackrabbitException, BusinessException {
        if (folders == null) {
            return;
        }
        for (Folder folder : folders) {
            if (folder.getTemplate().getName().equals(Template.Type.TEMPLATE_MIRROR.getName())) {
                Template11Mirror template11Mirror = (Template11Mirror) folder.getTemplate();
                Folder sourceFolder = planDAO.findFolder(template11Mirror.getSourcePath(), false);
                insertFolderReference(sourceFolder, folder.getPath());
            }
            insertSourceFolderReferenceWorker(folder.getFolders());
        }
    }

    /**
     * Insert Folder Mirror (see insertSourceFolderReference method javadocs)
     *
     * @param sourceFolder - folder to update
     * @param targetPath   - target reference path
     * @throws JackrabbitException - error in repository
     * @throws BusinessException   - error
     */
    private void insertFolderReference(Folder sourceFolder, String targetPath)
            throws JackrabbitException, BusinessException {
        List<FolderMirrorReference> newFolderMirrorReferences;
        Template template;
        if (sourceFolder != null && sourceFolder.getFolderMirrorReferences() != null && !sourceFolder
                .getFolderMirrorReferences().isEmpty()) {

            newFolderMirrorReferences = new ArrayList<FolderMirrorReference>(sourceFolder.getFolderMirrorReferences());
            template = sourceFolder.getTemplate();
            Contract contract = contractDAO.findById(PlanUtils.getContractNumberByPath(targetPath));
            FolderMirrorReference newFolderMirrorReference = new FolderMirrorReference(
                    sourceFolder.getFolderMirrorReferences().size() + 1, contract.getContractDesignation(),
                    contract.getCompany().getName(), targetPath);

            if (!newFolderMirrorReferences.contains(newFolderMirrorReference)) {
                newFolderMirrorReferences.add(newFolderMirrorReference);
            }
            sourceFolder.setTemplate(null);
            sourceFolder.setFolderMirrorReferences(null);
            planDAO.update(sourceFolder);
            sourceFolder.setTemplate(template);
            sourceFolder.setFolderMirrorReferences(newFolderMirrorReferences);
            planDAO.update(sourceFolder);
        }
    }

    /**
     * Go to plan folders and check if it is Template 11 Mirror. If it is, parse HTML content
     *
     * @param folders - folders to check
     * @throws JackrabbitException              - error in repository
     * @throws BusinessException                - error in Business
     * @throws ObjectNotFoundException          - Object not found
     * @throws UnsupportedEncodingException     - encode exception
     * @throws CertitoolsAuthorizationException - user cannot access
     */
    private void findFullPEIForExportWorker(List<Folder> folders) throws JackrabbitException, BusinessException,
            ObjectNotFoundException, UnsupportedEncodingException, CertitoolsAuthorizationException {
        for (Folder folder : folders) {
            if (folder.getTemplate().getName().equals(Template.Type.TEMPLATE_MIRROR.getName())) {
                Template11Mirror template11Mirror = (Template11Mirror) folder.getTemplate();
                Folder sourceFolder = planDAO.findFolder(template11Mirror.getSourcePath(), false);
                if (sourceFolder != null) {
                    /* TODO- Template*/
                    if (sourceFolder.getTemplate().getName().equals(Template.Type.TEMPLATE_FAQ_ELEMENT.getName())) {
                        Template7FAQElement template7FAQElement = (Template7FAQElement) sourceFolder.getTemplate();
                        template7FAQElement.setAnswer(parseHTMLTemplate11MirrorWorker(folder.getPath(),
                                template11Mirror.getSourcePath(), template7FAQElement.getAnswer()));
                        template7FAQElement.setQuestion(parseHTMLTemplate11MirrorWorker(folder.getPath(),
                                template11Mirror.getSourcePath(), template7FAQElement.getQuestion()));
                        folder.setTemplate(template7FAQElement);
                    } else if (sourceFolder.getTemplate().getName().equals(Template.Type.TEMPLATE_DIAGRAM.getName())) {
                        Template1Diagram template1Diagram = (Template1Diagram) sourceFolder.getTemplate();
                        template1Diagram.setImageMap(parseHTMLTemplate11MirrorWorker(folder.getPath(),
                                template11Mirror.getSourcePath(), template1Diagram.getImageMap()));
                        folder.setTemplate(template1Diagram);
                    } else if (sourceFolder.getTemplate().getName()
                            .equals(Template.Type.TEMPLATE_PLAN_CLICKABLE.getName())) {
                        Template4PlanClickable template4PlanClickable =
                                (Template4PlanClickable) sourceFolder.getTemplate();
                        template4PlanClickable.setImageMap(parseHTMLTemplate11MirrorWorker(folder.getPath(),
                                template11Mirror.getSourcePath(), template4PlanClickable.getImageMap()));
                        folder.setTemplate(template4PlanClickable);
                    } else if (sourceFolder.getTemplate().getName()
                            .equals(Template.Type.TEMPLATE_RICH_TEXT.getName())) {
                        Template3RichText template3RichText = (Template3RichText) sourceFolder.getTemplate();
                        template3RichText.setText(parseHTMLTemplate11MirrorWorker(folder.getPath(),
                                template11Mirror.getSourcePath(), template3RichText.getText()));
                        folder.setTemplate(template3RichText);
                    } else if (sourceFolder.getTemplate().getName()
                            .equals(Template.Type.TEMPLATE_RICH_TEXT_WITH_ATTACH.getName())) {
                        Template9RichTextWithAttach template9RichTextWithAttach =
                                (Template9RichTextWithAttach) sourceFolder.getTemplate();
                        template9RichTextWithAttach.setText(parseHTMLTemplate11MirrorWorker(folder.getPath(),
                                template11Mirror.getSourcePath(), template9RichTextWithAttach.getText()));
                        folder.setTemplate(template9RichTextWithAttach);
                    } else {
                        folder.setTemplate(sourceFolder.getTemplate());
                    }
                }
            }
            findFullPEIForExportWorker(folder.getFolders());
        }
    }

    private void updateFolderPermissionsCopyPEI(List<Folder> folders, Map<Long, Long> permissionsMap)
            throws JackrabbitException {
        for (Folder folder : folders) {
            if (folder.getPermissions() != null && !folder.getPermissions().isEmpty()) {
                for (com.criticalsoftware.certitools.entities.jcr.Permission permission : folder.getPermissions()) {
                    permission.setPermissionId(permissionsMap.get(permission.getPermissionId()));
                }
                planDAO.update(folder);
            }
            updateFolderPermissionsCopyPEI(folder.getFolders(), permissionsMap);
        }
    }

    /**
     * Checks if the current folder fulfills the search criteria or not. Uses searchphrase and contacttype to check the
     * contact
     *
     * @param currentFolder folder containing the contact to analyse
     * @param searchPhrase  phrase to look for in the contacts fields
     * @param contactType   type of contact
     * @return true if contact matches the search criteria.
     * @throws BusinessException   -error
     * @throws JackrabbitException - error in repository
     */
    private Folder isContactSearchResult(Folder currentFolder, String searchPhrase, String contactType)
            throws JackrabbitException, BusinessException {
        if (!(currentFolder.getTemplate() instanceof Template5ContactsElement) && !(currentFolder
                .getTemplate() instanceof Template11Mirror)) {
            return null;
        }

        if (currentFolder.getTemplate() instanceof Template11Mirror) {
            Template11Mirror template11Mirror = (Template11Mirror) currentFolder.getTemplate();
            String pathToLoad = template11Mirror.getSourcePath();
            if (PlanUtils.getOnlineOrOfflineFromPath(currentFolder.getPath())
                    .equals("online")) {
                pathToLoad = pathToLoad.replaceFirst("offline", "online");
            }
            Folder sourceFolder = planDAO.findFolder(pathToLoad, false);
            if (sourceFolder != null && (sourceFolder.getTemplate() instanceof Template5ContactsElement)) {
                //currentFolder = sourceFolder;
                // TODO jp-gomes needs to confirm if this solves the mirror problem in contacts
                currentFolder.setTemplate(sourceFolder.getTemplate());
            } else {
                return null;
            }
        }

        Template5ContactsElement template = (Template5ContactsElement) currentFolder.getTemplate();
        String folderContactType = template.getContactType();

        if (!contactType.equals("all")) {
            if (!template.getContactType().equals(contactType)) {
                return null;
            }
        }

        if (!StringUtils.isEmpty(searchPhrase)) {
            searchPhrase = StringUtils.upperCase(searchPhrase);

            if (folderContactType.equals(Template5ContactsElement.ContactType.INTERNAL_PERSON.toString())) {
                String entityName = StringUtils.upperCase(template.getEntityName());
                String name = StringUtils.upperCase(template.getName());
                String email = StringUtils.upperCase(template.getEmail());
                String phone = StringUtils.upperCase(template.getPhone());

                if (!StringUtils.contains(entityName, searchPhrase)
                        && !StringUtils.contains(name, searchPhrase)
                        && !StringUtils.contains(email, searchPhrase)
                        && !StringUtils.contains(phone, searchPhrase)) {
                    return null;
                }
            }

            if (folderContactType.equals(Template5ContactsElement.ContactType.EXTERNAL_ENTITY.toString())) {
                String entityType = StringUtils.upperCase(template.getEntityType());
                String email = StringUtils.upperCase(template.getEmail());
                String phone = StringUtils.upperCase(template.getPhone());
                String entityName = StringUtils.upperCase(template.getEntityName());

                if (!StringUtils.contains(entityType, searchPhrase)
                        && !StringUtils.contains(entityName, searchPhrase)
                        && !StringUtils.contains(email, searchPhrase)
                        && !StringUtils.contains(phone, searchPhrase)) {
                    return null;
                }
            }

            if (folderContactType.equals(Template5ContactsElement.ContactType.EMERGENCY_STRUCTURE_PERSON.toString())) {
                String personArea = StringUtils.upperCase(template.getPersonArea());
                String personPosition = StringUtils.upperCase(template.getPersonPosition());
                String name = StringUtils.upperCase(template.getName());
                String email = StringUtils.upperCase(template.getEmail());
                String phone = StringUtils.upperCase(template.getPhone());

                if (!StringUtils.contains(personArea, searchPhrase)
                        && !StringUtils.contains(personPosition, searchPhrase)
                        && !StringUtils.contains(name, searchPhrase) && !StringUtils.contains(email, searchPhrase)
                        && !StringUtils.contains(phone, searchPhrase)) {
                    return null;
                }
            }
        }
        return currentFolder;
    }

    private Folder isResourceSearchResult(Folder currentFolder, String searchPhrase, String resourceType)
            throws JackrabbitException, BusinessException {
        if (!(currentFolder.getTemplate() instanceof Template12MeansResourcesElement) &&
                !(currentFolder.getTemplate() instanceof Template11Mirror)) {
            return null;
        }

        if (currentFolder.getTemplate() instanceof Template11Mirror) {
            Template11Mirror template11Mirror = (Template11Mirror) currentFolder.getTemplate();
            String pathToLoad = template11Mirror.getSourcePath();
            if (PlanUtils.getOnlineOrOfflineFromPath(currentFolder.getPath()).equals("online")) {
                pathToLoad = pathToLoad.replaceFirst("offline", "online");
            }
            Folder sourceFolder = planDAO.findFolder(pathToLoad, false);
            if (sourceFolder != null && (sourceFolder.getTemplate() instanceof Template12MeansResourcesElement)) {
                currentFolder.setTemplate(sourceFolder.getTemplate());
            } else {
                return null;
            }
        }

        Template12MeansResourcesElement template = (Template12MeansResourcesElement) currentFolder.getTemplate();

        if (resourceType != null && !resourceType.isEmpty() && !template.getResourceType().equals(resourceType)) {
            return null;
        }

        if (searchPhrase != null && !searchPhrase.isEmpty()) {
            searchPhrase = StringUtils.upperCase(searchPhrase);

            String entityName = StringUtils.upperCase(template.getEntityName());
            String name = StringUtils.upperCase(template.getResourceName());
            String characteristics = StringUtils.upperCase(template.getCharacteristics());
            String type = StringUtils.upperCase(template.getResourceType());

            if (!StringUtils.contains(entityName, searchPhrase)
                    && !StringUtils.contains(name, searchPhrase)
                    && !StringUtils.contains(characteristics, searchPhrase)
                    && !StringUtils.contains(type, searchPhrase)) {
                return null;
            }
        }
        return currentFolder;
    }

    /**
     * Checks if the PEI with the specified id is in the peiList specified
     *
     * @param peiList list to check
     * @param peiId   id of pei
     * @return true if pei is in the specified list
     */
    private boolean isPlanInList(ArrayList<Plan> peiList, Long peiId) {
        for (Plan pei : peiList) {
            if (pei.getName().equals(peiId.toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Find Documents Elements Template. When template is 11Mirror, load source Folder, check if it is
     * TEMPLATE_DOCUMENTS_ELEMENT, set folder template with source Folder template
     *
     * @param path          - folder path
     * @param userInSession - user
     * @param moduleType    - application module
     * @return - folders
     * @throws JackrabbitException              - error in repository
     * @throws ObjectNotFoundException          - folder not found
     * @throws BusinessException                - error
     * @throws CertitoolsAuthorizationException - no authorization
     */
    private List<Folder> findDocumentsTemplate(String path, User userInSession, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, BusinessException, CertitoolsAuthorizationException {
        List<Folder> allFolders = planDAO.findFoldersLoaded(path, true);
        List<Folder> finalList = new ArrayList<Folder>();

        if (allFolders == null) {
            return finalList;
        }
        //Filter by Template
        for (Folder f : allFolders) {
            if (f.getTemplate().getName().equals(Template.Type.TEMPLATE_DOCUMENTS_ELEMENT.getName())) {
                finalList.add(f);
            } else if (f.getTemplate().getName().equals(Template.Type.TEMPLATE_MIRROR.getName())) {
                Template11Mirror template11Mirror = (Template11Mirror) f.getTemplate();
                String pathToLoad = template11Mirror.getSourcePath();
                if (PlanUtils.getOnlineOrOfflineFromPath(f.getPath())
                        .equals("online")) {
                    pathToLoad = pathToLoad.replaceFirst("offline", "online");
                }
                Folder sourceFolder = planDAO.findFolder(pathToLoad, false);
                if (sourceFolder != null && sourceFolder.getTemplate().getName()
                        .equals(Template.Type.TEMPLATE_DOCUMENTS_ELEMENT.getName())) {
                    f.setTemplate(sourceFolder.getTemplate());
                    finalList.add(f);
                }
            }
        }
        finalList = filtersFoldersList(finalList, userInSession, path, moduleType);
        return finalList;
    }

    private ArrayList<Folder> filterFoldersListByPermissions(List<Folder> foldersTree,
                                                             Collection<Permission> userPermissions) {
        Folder currentNode;
        Folder nextNode;

        // stack of the current permissions. Permissions from different nodes are separated by the special empty
        // permission(path: "",name: "")
        Stack<com.criticalsoftware.certitools.entities.jcr.Permission> permissionsStack =
                new Stack<com.criticalsoftware.certitools.entities.jcr.Permission>();

        ArrayList<Folder> resultFoldersTree = new ArrayList<Folder>();  // the filtered tree
        boolean parentPermissionDenied = false;
        long parentPermissionDeniedDepth = 0;
        boolean permissionDenied;

        for (int i = 0; i < foldersTree.size(); i++) {
            permissionDenied = false;

            // add current node permissions to stack
            currentNode = foldersTree.get(i);
            permissionsStack.addAll(currentNode.getPermissions());
            permissionsStack.add(new com.criticalsoftware.certitools.entities.jcr.Permission("", ""));

            // check parent permission denied
            if (parentPermissionDenied) {
                if (currentNode.getDepth() > parentPermissionDeniedDepth) {
                    permissionDenied = true;
                } else {
                    // reset of parent permission denied
                    parentPermissionDenied = false;
                    parentPermissionDeniedDepth = 0;
                }
            }

            // check if current node permissions are ok
            if (permissionsStack.size() > 0) {
                if (!permissionDenied && isUserPermissionValid(permissionsStack, userPermissions)) {
                    resultFoldersTree.add(currentNode);
                } else {
                    parentPermissionDenied = true;
                    parentPermissionDeniedDepth = currentNode.getDepth();
                }
            } else {
                resultFoldersTree.add(currentNode);
            }

            // remove old permissions from stack
            if (i < foldersTree.size() - 1) {
                nextNode = foldersTree.get(i + 1);
                if (currentNode.getDepth() >= nextNode.getDepth()) {
                    permissionsStack =
                            popPermissions(permissionsStack, (int) (currentNode.getDepth() - nextNode.getDepth() + 1));
                }
            }
        }
        return resultFoldersTree;
    }

    /**
     * Filters an ArrayList representing the tree of folders according to the user permissions
     * <p/>
     * Traverses the ArrayList and for each element: 1) Adds the current node permissions to the stack of permissions 2)
     * Checks the current node permissions, if it's ok adds the node to the result list. This takes into account if any
     * parent node access was denied 3) In case a change of depth is detected, the stack of permissions is pop'ed
     * according to the difference of depth detected. A difference of depth means the next node isn't in the same
     * "branch" as the current node.
     *
     * @param foldersTree     ArrayList with the folders, already sorted by parent and children, e.g.:parent1;children1;
     *                        parent2;child1;child2;parent3;child
     * @param userPermissions permissions of the user
     * @return ArrayList with the tree of folders, filtered by user permissions
     */
    private ArrayList<TreeNode> filterFoldersByPermissions(ArrayList<TreeNode> foldersTree,
                                                           Collection<Permission> userPermissions) {
        TreeNode currentNode;
        TreeNode nextNode;

        // stack of the current permissions. Permissions from different nodes are separated by the special empty
        // permission(path: "",name: "")
        Stack<com.criticalsoftware.certitools.entities.jcr.Permission> permissionsStack =
                new Stack<com.criticalsoftware.certitools.entities.jcr.Permission>();

        ArrayList<TreeNode> resultFoldersTree = new ArrayList<TreeNode>();  // the filtered tree
        boolean parentPermissionDenied = false;
        int parentPermissionDeniedDepth = 0;
        boolean permissionDenied;

        for (int i = 0; i < foldersTree.size(); i++) {
            permissionDenied = false;

            // add current node permissions to stack
            currentNode = foldersTree.get(i);
            permissionsStack.addAll(currentNode.getPermissions());
            permissionsStack.add(new com.criticalsoftware.certitools.entities.jcr.Permission("", ""));

            // check parent permission denied
            if (parentPermissionDenied) {
                if (currentNode.getDepth() > parentPermissionDeniedDepth) {
                    permissionDenied = true;
                } else {
                    // reset of parent permission denied
                    parentPermissionDenied = false;
                    parentPermissionDeniedDepth = 0;
                }
            }

            // check if current node permissions are ok
            if (permissionsStack.size() > 0) {
                if (!permissionDenied && isUserPermissionValid(permissionsStack, userPermissions)) {
                    resultFoldersTree.add(currentNode);
                } else if (!parentPermissionDenied) {
                    currentNode.setAccessAllowed(false);
                    resultFoldersTree.add(currentNode);
                    parentPermissionDenied = true;
                    parentPermissionDeniedDepth = currentNode.getDepth();
                }
            } else {
                resultFoldersTree.add(currentNode);
            }

            // remove old permissions from stack
            if (i < foldersTree.size() - 1) {
                nextNode = foldersTree.get(i + 1);
                if (currentNode.getDepth() >= nextNode.getDepth()) {
                    permissionsStack =
                            popPermissions(permissionsStack, currentNode.getDepth() - nextNode.getDepth() + 1);
                }
            }
        }

        return resultFoldersTree;
    }

    /**
     * Checks if the user has at least 1 permission contained in the permissions stack
     *
     * @param permissionsStack permissions for the current node
     * @param userPermissions  user permissions
     * @return true if user can access the node (user has at least 1 permission contained in the permissions stack)
     */
    @SuppressWarnings({"RedundantIfStatement"})
    private boolean isUserPermissionValid(
            Stack<com.criticalsoftware.certitools.entities.jcr.Permission> permissionsStack,
            Collection<Permission> userPermissions) {

        ArrayList<com.criticalsoftware.certitools.entities.jcr.Permission> permissionsStackFiltered =
                new ArrayList<com.criticalsoftware.certitools.entities.jcr.Permission>();

        // remove separators from permission stack
        for (com.criticalsoftware.certitools.entities.jcr.Permission permission : permissionsStack) {
            if (!permission.getName().equals("")) {
                permissionsStackFiltered.add(permission);
            }
        }

        if (permissionsStackFiltered.size() <= 0) {
            return true;
        }

        int counter = 0;
        for (com.criticalsoftware.certitools.entities.jcr.Permission permission : permissionsStackFiltered) {
            for (Permission userPermission : userPermissions) {
                if (permission.getPermissionId() != null && userPermission.getId() == permission.getPermissionId()) {
                    counter++;
                    break;
                }
            }
        }

        if (counter == permissionsStackFiltered.size()) {
            return true;
        }

        return false;
    }

    /**
     * Pops all permissions of N (depth) folders from the stack
     *
     * @param stack stack with permissions. Each group of permissions (permissions of a Folder) is separated by a empty
     *              permission.
     * @param depth number of folders permissions to remove
     * @return resulting stack
     */
    private Stack<com.criticalsoftware.certitools.entities.jcr.Permission> popPermissions(
            Stack<com.criticalsoftware.certitools.entities.jcr.Permission> stack, int depth) {

        if (stack.size() <= 0) {
            return stack;
        }

        // remove last separator
        stack.pop();

        while (depth > 0) {
            if (stack.size() <= 0) {
                break;
            }

            if (stack.peek().getName().equals("")) {
                depth--;
                if (depth > 0) {
                    stack.pop();
                }
            } else {
                stack.pop();
            }
        }
        return stack;
    }

    /**
     * Checks if the user can access the specified PEI. Rules are: if pei manager, can always access. If client pei
     * manager of this PEI, can always access and if normal user checks if it is associated with this PEI and that the
     * contract is valid
     *
     * @param user       user to check
     * @param contractId id of the PEI/contract
     * @param moduleType - application module
     * @return true if user can access it
     * @throws ObjectNotFoundException user not found
     */
    private boolean isUserAllowedAccessPlan(User user, long contractId, ModuleType moduleType)
            throws ObjectNotFoundException {
        // pei manager can access all
        if (sessionContext.isCallerInRole("peimanager") || sessionContext.isCallerInRole("contractmanager")
                || sessionContext.isCallerInRole("administrator")) {
            return true;
        }

        User userInDb = userDAO.findByIdWithContractsRoles(user.getId());

        if (userInDb == null) {
            throw new ObjectNotFoundException("Object not found: User", ObjectNotFoundException.Type.USER);
        }

        UserContract userContract = contractDAO.findUserContract(userInDb.getId(), contractId);

        // if user not associated with this Plan, he can't access it
        if (userContract == null) {
            return false;
        }

        // client pei manager of this plan can access all
        if (isUserClientPlanManager(userContract)) {
            return true;
        }

        // check if user can access it (contract to this pei is valid)
        return isUserContractValid(userContract, moduleType);
    }

    /**
     * Checks if a user has the role clientpeimanager and that he is pei manager of the specified pei
     *
     * @param userContract to check
     * @return true if user has the role clientpeimanager and that he is pei manager of the specified pei
     */
    private boolean isUserClientPlanManager(UserContract userContract) {
        if (!sessionContext.isCallerInRole("clientpeimanager")) {
            return false;
        }

        if (userContract == null) {
            return false;
        }

        // check if user is client pei manager
        for (Permission permission : userContract.getPermissions()) {
            if (permission.getName().equals(ConfigurationProperties.PERMISSION_PEI_MANAGER.getKey())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the user contract is valid
     * <p/>
     * Rules to check if this user contract is active User is in a Contract that: 0) Type of contract is of the
     * specified moduletype 0.1) If user is clientpeimanager, contract is always valid 1) is active 2) Contract validity
     * is OK 3) if UserContract validity is set, it is OK
     *
     * @param userContract user contract to check
     * @param moduleType   module type of the contract we want to check
     * @return true if contract is valid
     */
    private boolean isUserContractValid(UserContract userContract, ModuleType moduleType) {

        if (userContract == null) {
            return false;
        }

        Module module = new Module(moduleType);
        if (!userContract.getContract().getModule().equals(module)) {
            return false;
        }

        // check if user is clientpeimanager. In that case doesn't check if contract is valid, it can always access it
        // TODO-MODULE
        if ((moduleType.equals(ModuleType.PEI) || moduleType.equals(ModuleType.PRV) || moduleType.equals(ModuleType.GSC)) && isUserClientPlanManager(
                userContract)) {
            return true;
        }

        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(new Date());

        Calendar userContractStartDate = Calendar.getInstance();
        Calendar userContractEndDate = Calendar.getInstance();
        Calendar contractStartDate = Calendar.getInstance();
        Calendar contractEndDate = Calendar.getInstance();

        // userContract must be active
        if (userContract.getContract().isActive()) {
            contractStartDate.setTime(userContract.getContract().getValidityStartDate());
            contractEndDate.setTime(userContract.getContract().getValidityEndDate());
            contractEndDate.add(Calendar.DATE, 1);

            // userContract validity is set, check this first
            if (userContract.getValidityStartDate() != null) {
                userContractStartDate.setTime(userContract.getValidityStartDate());
                userContractEndDate.setTime(userContract.getValidityEndDate());
                userContractEndDate.add(Calendar.DATE, 1);


                if (userContractStartDate.before(nowCalendar) && userContractEndDate.after(nowCalendar) &&
                        contractStartDate.before(nowCalendar) && contractEndDate.after(nowCalendar)) {
                    return true;
                }
            } else {

                // only userContract validity is set
                if (contractStartDate.before(nowCalendar) && contractEndDate.after(nowCalendar)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Go to published online folders, check if it is Template 11 Mirror, go to target references and their children and
     * publish them
     *
     * @param parentPath - folder published
     * @throws JackrabbitException - error in repository
     * @throws BusinessException   - error
     */
    private void updateOnlineReferences(String parentPath) throws JackrabbitException, BusinessException {
        List<Folder> folders = planDAO.findFoldersLoaded(parentPath, false);
        for (Folder folder : folders) {
            if (folder.getFolderMirrorReferences() != null && !folder.getFolderMirrorReferences().isEmpty()) {
                for (FolderMirrorReference folderMirrorReference : folder.getFolderMirrorReferences()) {
                    String path = folderMirrorReference.getReferencePath();
                    if (PlanUtils.getOnlineOrOfflineFromPath(path).equals("offline")) {
                        path = path.replaceFirst("offline", "online");
                    }
                    Folder referencedFolder = planDAO.findFolder(path, false);
                    if (referencedFolder != null && referencedFolder.getTemplate().getName()
                            .equals(Template.Type.TEMPLATE_MIRROR.getName())) {
                        Template11Mirror newTemplate11Mirror = new Template11Mirror();
                        Template11Mirror template11Mirror = (Template11Mirror) referencedFolder.getTemplate();

                        newTemplate11Mirror.setSourceContractId(template11Mirror.getSourceContractId());
                        newTemplate11Mirror.setSourcePath(folder.getPath().replaceFirst("offline", "online"));

                        referencedFolder.setTemplate(null);
                        planDAO.update(referencedFolder);
                        referencedFolder.setTemplate(newTemplate11Mirror);
                        planDAO.update(referencedFolder);
                    }
                }
            }
        }
    }

    private String getNewPath(String oldName, String newName, String oldPath) {
        String newPath = oldPath.substring(0, oldPath.length() - oldName.length());
        newPath += newName;

        return newPath;
    }

    /**
     * delete permission in user contracts where the user or contract is deleted
     *
     * @param userContracts - user contracts
     * @param permission    - permission to clean
     */
    private void deleteUserContractPermission(Collection<UserContract> userContracts, Permission permission) {

        for (UserContract userContract : userContracts) {
            //Must clean user contract
            if (userContract.getUser().isDeleted() || userContract.getContract().isDeleted()) {
                userContract.getPermissions().remove(permission);
                userDAO.updateUserContract(userContract);
            }
        }
    }

    private Folder getParent(String path) throws BusinessException, JackrabbitException {
        if (PlanUtils.calculateDepth(path) <= 1) {
            return null;
        }
        String result = "";
        if (path != null) {
            String[] split = path.split("/");
            if (split.length <= 2) {
                throw new BusinessException("Invalid path");
            }
            for (int i = 1; i < split.length - 2; i++) {
                result += "/" + split[i];
            }
        }
        return planDAO.findFolder(result, false);
    }

    /**
     * This method build folders list according user/folder permission and order list if necessary
     *
     * @param folderListToFilter - list to filter
     * @param userInSession      - User
     * @param path               - forder path to get contract Id
     * @param moduleType         - application module
     * @return filtered folder list
     * @throws BusinessException                - error
     * @throws CertitoolsAuthorizationException - user not have permission
     * @throws ObjectNotFoundException          - object not found
     */
    @SuppressWarnings({"UnusedAssignment"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    private ArrayList<Folder> filtersFoldersList(List<Folder> folderListToFilter, User userInSession,
                                                 String path, ModuleType moduleType)
            throws ObjectNotFoundException, CertitoolsAuthorizationException, BusinessException {

        if (folderListToFilter == null || folderListToFilter.isEmpty()) {
            return (ArrayList<Folder>) folderListToFilter;
        }
        // 1) check if user is pei manager (certitecna) or administrator or contractmanager (certitecna)
        if (sessionContext.isCallerInRole("peimanager") || sessionContext.isCallerInRole("contractmanager")
                || sessionContext.isCallerInRole("administrator")) {
            return (ArrayList<Folder>) folderListToFilter;
        }
        // 2) check if user has permission "pei manager" (Client)
        User userInDb = userDAO.findByIdWithContractsRoles(userInSession.getId());
        if (userInDb == null) {
            throw new ObjectNotFoundException("Object not found: User", ObjectNotFoundException.Type.USER);
        }
        long contractId = PlanUtils.getContractNumberByPath(path);
        UserContract userContract = contractDAO.findUserContract(userInDb.getId(), contractId);

        // check if user contract is valid (within the validity date, etc)
        if (!isUserContractValid(userContract, moduleType)) {
            throw new CertitoolsAuthorizationException("User contract is not valid");
        }

        for (Permission permission : userContract.getPermissions()) {
            if (permission.getName().equals(ConfigurationProperties.PERMISSION_PEI_MANAGER.getKey())) {
                return (ArrayList<Folder>) folderListToFilter;
            }
        }
        return filterFoldersListByPermissions(folderListToFilter, userContract.getPermissions());
    }

    private boolean arePathsRelated(String objectivePath, String candidatePath) {
        //Remove last 2 entrys for compare
        String[] split = candidatePath.split("/");
        candidatePath = "";
        for (int i = 1; i < split.length - 2; i++) {
            candidatePath += "/" + split[i];
        }
        return objectivePath.contains(candidatePath);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    private TreeNode saveFolderAndMovePath(Folder folder) throws JackrabbitException {

        planDAO.update(folder);

        if (folder.getOldName() != null && (!folder.getOldName().equals(folder.getName()))) {
            String path1 = getNewPath(folder.getOldName(), folder.getName(), folder.getPath());
            planDAO.moveToPath(folder.getPath(), path1);
            return new TreeNode(null, path1);
        }

        return new TreeNode(folder.getName(), folder.getPath());
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    private Folder prepareFolderForUpdate(Folder changedFolder, User userInSession)
            throws JackrabbitException, ObjectNotFoundException, BusinessException {

        //Control if name has changed
        boolean folderNameHasChange = false;
        String oldName;
        Date dateToSave = new Date();
        Folder toUpdateFolder = planDAO.findFolder(changedFolder.getPath(), true);
        List<Operation> operations = new ArrayList<Operation>();
        //Folder does not exists
        if (toUpdateFolder == null) {
            throw new ObjectNotFoundException("Requested folder was not found", ObjectNotFoundException.Type.FOLDER);
        }

        if (!changedFolder.getTemplate().getName().equals(toUpdateFolder.getTemplate().getName())) {
            operations.add(Operation.TEMPLATE_CHANGE);
        }

        //New folder path exists
        if (!changedFolder.getName().equals(toUpdateFolder.getName())) {
            //Name changed
            operations.add(Operation.SAVE_WITH_NAME_CHANGE);
            folderNameHasChange = true;
            oldName = toUpdateFolder.getName();
            if (planDAO.findFolder(
                    getNewPath(toUpdateFolder.getName(), changedFolder.getName(), toUpdateFolder.getPath()), false)
                    != null) {
                throw new JackrabbitException("Folder with same path already exists");
            }
        } else {
            oldName = changedFolder.getName();
        }

        List<FolderMirrorReference> references = toUpdateFolder.getFolderMirrorReferences();
        toUpdateFolder.setTemplate(null);
        toUpdateFolder.setFolderMirrorReferences(null);
        planDAO.update(toUpdateFolder);

        toUpdateFolder.setFolderMirrorReferences(references);
        toUpdateFolder.setTemplate(changedFolder.getTemplate());

        if (changedFolder.getActive() == null) {
            toUpdateFolder.setActive(false);
        } else {
            toUpdateFolder.setActive(changedFolder.getActive());
        }

        if (changedFolder.getIncludeInMenu() == null) {
            toUpdateFolder.setIncludeInMenu(false);
        } else {
            toUpdateFolder.setIncludeInMenu(changedFolder.getIncludeInMenu());
        }

        if (changedFolder.getFolderHeader() != null) {
            toUpdateFolder.setFolderHeader(changedFolder.getFolderHeader());
        } else {
            toUpdateFolder.setFolderHeader("");
        }

        if (changedFolder.getFolderFooter() != null) {
            toUpdateFolder.setFolderFooter(changedFolder.getFolderFooter());
        } else {
            toUpdateFolder.setFolderFooter("");
        }

        toUpdateFolder.setName(changedFolder.getName());
        toUpdateFolder.setOrder(changedFolder.getOrder());
        toUpdateFolder.setPermissions(changedFolder.getPermissions());
        toUpdateFolder.setOldName(oldName);
        toUpdateFolder.setLastSaveDate(dateToSave);
        toUpdateFolder.setLastSaveAuthor(userInSession.getName());
        toUpdateFolder.setHelp(changedFolder.getHelp());

        if (changedFolder.getPermissions() != null && changedFolder.getPermissions().size() != 0) {

            List<Permission> toInsertPermissionList = new ArrayList<Permission>(
                    permissionDAO.findByIdForSelect(changedFolder.getPermissions(),
                            PlanUtils.getContractNumberByPath(changedFolder.getPath())));

            if (toInsertPermissionList.size() != changedFolder.getPermissions().size()) {
                throw new BusinessException("could not select all permission from contract");
            }

            List<com.criticalsoftware.certitools.entities.jcr.Permission> permissionList =
                    new ArrayList<com.criticalsoftware.certitools.entities.jcr.Permission>();

            for (Permission permission : toInsertPermissionList) {
                permissionList.add(new com.criticalsoftware.certitools.entities.jcr.Permission("/" + permission.getId(),
                        "" + permission.getName(), permission.getId()));
            }
            toUpdateFolder.setPermissions(permissionList);

        } else {

            if (toUpdateFolder.getPermissions() != null) {
                toUpdateFolder.setPermissions(new ArrayList<com.criticalsoftware.certitools.entities.jcr.Permission>());
            }
        }

        //When folder name changes, update source and target references links
        if (folderNameHasChange) {
            //Update source reference
            if (toUpdateFolder.getTemplate().getName().equals(Template.Type.TEMPLATE_MIRROR.getName())) {
                Template11Mirror template11Mirror = (Template11Mirror) toUpdateFolder.getTemplate();
                List<Folder> sourceFolders = planDAO.findFoldersLoaded(template11Mirror.getSourcePath(), false);
                if (sourceFolders != null) {
                    for (Folder toModifyFolder : sourceFolders) {
                        references = toModifyFolder.getFolderMirrorReferences();
                        List<FolderMirrorReference> newReferences = new ArrayList<FolderMirrorReference>();
                        boolean toUpdate = false;
                        FolderMirrorReference reference;
                        int index = 0;
                        for (FolderMirrorReference folderMirrorReference : references) {
                            if (folderMirrorReference.getReferencePath().startsWith(toUpdateFolder.getPath())) {
                                reference = new FolderMirrorReference(index,
                                        folderMirrorReference.getReferenceContractDesignation(),
                                        folderMirrorReference.getReferenceCompanyName(),
                                        folderMirrorReference.getReferencePath().replaceAll(toUpdateFolder.getPath(),
                                                getNewPath(oldName, changedFolder.getName(),
                                                        toUpdateFolder.getPath())));
                                toUpdate = true;
                            } else {
                                reference = new FolderMirrorReference(index,
                                        folderMirrorReference.getReferenceContractDesignation(),
                                        folderMirrorReference.getReferenceCompanyName(),
                                        folderMirrorReference.getReferencePath());
                            }
                            newReferences.add(reference);
                            index++;
                        }
                        if (toUpdate) {
                            toModifyFolder.setFolderMirrorReferences(null);
                            toModifyFolder.setFolderMirrorReferences(newReferences);
                            planDAO.update(toModifyFolder);
                        }
                    }
                }
            }
        }
        updateFolderReferencesInfo(toUpdateFolder, operations, dateToSave,
                userInSession.getName(), getNewPath(oldName, changedFolder.getName(),
                        toUpdateFolder.getPath()));

        return toUpdateFolder;
    }

    /**
     * When folder is saved and if it has references, must update LastSavedAuthor and LastSavedDate in target folders.
     * Other case is when folder is saved and the name changes. In this case we must go target folder and their children
     * to change Template 11 Mirror reference Path
     *
     * @param sourceFolder - folder saved
     * @param operations   - operation
     * @param date         - change date
     * @param author       - change author
     * @param newPath      - new folder Path
     * @throws JackrabbitException - error in repository
     */
    private void updateFolderReferencesInfo(Folder sourceFolder, List<Operation> operations, Date date,
                                            String author, String newPath)
            throws JackrabbitException {
        if (sourceFolder.getFolderMirrorReferences() != null && !sourceFolder.getFolderMirrorReferences().isEmpty()) {
            for (FolderMirrorReference folderMirrorReference : sourceFolder.getFolderMirrorReferences()) {
                Folder referencedFolder = planDAO.findFolder(folderMirrorReference.getReferencePath(), false);
                if (referencedFolder.getTemplate().getName().equals(Template.Type.TEMPLATE_MIRROR.getName())) {
                    referencedFolder.setLastSaveAuthor(author);
                    referencedFolder.setLastSaveDate(date);

                    if (operations.contains(Operation.SAVE_WITH_NAME_CHANGE) || operations
                            .contains(Operation.TEMPLATE_CHANGE)) {
                        Template11Mirror template11Mirror = (Template11Mirror) referencedFolder.getTemplate();
                        String oldPath = template11Mirror.getSourcePath();
                        template11Mirror.setSourcePath(newPath);
                        template11Mirror.setSourceTemplateName(sourceFolder.getTemplate().getName());
                        referencedFolder.setTemplate(template11Mirror);
                        //Must go to this folder children
                        if (operations.contains(Operation.SAVE_WITH_NAME_CHANGE)) {
                            List<Folder> toModifyFolders = planDAO.findFoldersLoaded(referencedFolder.getPath(), false);
                            if (toModifyFolders != null) {
                                //Do not update the first one
                                toModifyFolders.remove(0);
                                for (Folder toModifyFolder : toModifyFolders) {
                                    if (toModifyFolder.getTemplate().getName()
                                            .equals(Template.Type.TEMPLATE_MIRROR.getName())) {
                                        template11Mirror = (Template11Mirror) toModifyFolder.getTemplate();
                                        template11Mirror.setSourcePath(
                                                template11Mirror.getSourcePath().replaceAll(oldPath, newPath));
                                        toModifyFolder.setTemplate(template11Mirror);
                                        planDAO.update(toModifyFolder);
                                    }
                                }
                            }
                        }
                    }
                    planDAO.update(referencedFolder);
                }
            }
        }
    }

    /**
     * Create folders with Template 11 Mirror for selected folder and their children and update source folder
     * references
     *
     * @param template11Mirror - folder to create mirror
     * @param user             - user in session
     * @return - list with tree nodes to update
     * @throws JackrabbitException - error in repository
     * @throws BusinessException   - error
     */
    private List<TreeNode> processTemplate11MirrorInsert(Template11Mirror template11Mirror, User user)
            throws JackrabbitException, BusinessException {

        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        TreeNode parentTreeNode = null;
        Integer depth = null;
        String sourcePath = null;
        String cssToApply;
        //Insert
        /* Parent Folder*/
        Folder parentFolder = planDAO.findFolder(template11Mirror.getParentPath(), false);
        /* Folders to mirror*/
        List<Folder> sourceFolders = planDAO.findFoldersLoaded(template11Mirror.getSourcePath(), false);

        if (sourceFolders == null || parentFolder == null) {
            throw new BusinessException("cannot find folders to mirror");
        }

        if (PlanUtils.getContractNumberByPath(template11Mirror.getParentPath())
                .equals(PlanUtils.getContractNumberByPath(template11Mirror.getSourcePath()))) {
            cssToApply = "template11Mirror-same-contract";
        } else {
            cssToApply = "template11Mirror-different-contract";
        }

        for (Folder sourceFolder : sourceFolders) {
            //Create new Folders
            boolean toShowInExpand = false;
            Folder newFolder = new Folder();
            newFolder.setName(sourceFolder.getName());
            if (sourceFolders.indexOf(sourceFolder) != 0) {
                newFolder.setPath(
                        parentTreeNode.getPath() + StringUtils
                                .substringAfter(sourceFolder.getPath(), sourcePath));
                if (depth == PlanUtils.calculateDepth(newFolder.getPath())) {
                    toShowInExpand = true;
                }
                treeNodes.add(new TreeNode(newFolder.getName(), newFolder.getPath(), cssToApply,
                        sourceFolder.getPath(), toShowInExpand));
            } else {
                newFolder.setPath(parentFolder.getPath() + "/folders/" + sourceFolder.getName());
                depth = PlanUtils.calculateDepth(newFolder.getPath()) + 1;
                parentTreeNode = new TreeNode(newFolder.getName(), newFolder.getPath(), cssToApply,
                        sourceFolder.getPath(), true);
                sourcePath = sourceFolder.getPath();
            }
            newFolder.setActive(sourceFolder.getActive());
            newFolder.setFolderFooter(sourceFolder.getFolderFooter());
            newFolder.setFolderHeader(sourceFolder.getFolderHeader());
            newFolder.setHelp(sourceFolder.getHelp());
            newFolder.setLastSaveAuthor(user.getName());
            newFolder.setLastSaveDate(new Date());
            newFolder.setOrder(sourceFolder.getOrder());
            newFolder.setPublishedAuthor(null);
            newFolder.setPublishedDate(null);
            newFolder.setIncludeInMenu(sourceFolder.getIncludeInMenu());

            /* Set Template*/
            Template11Mirror newTemplate11Mirror = new Template11Mirror();
            newTemplate11Mirror.setName(template11Mirror.getName());
            newTemplate11Mirror.setSourcePath(sourceFolder.getPath());
            newTemplate11Mirror.setSourceContractId(PlanUtils.getContractNumberByPath(sourceFolder.getPath()));
            newTemplate11Mirror.setSourceTemplateName(sourceFolder.getTemplate().getName());
            newFolder.setTemplate(newTemplate11Mirror);

            planDAO.insert(newFolder);

            //Update sourceFolder Link path
            List<FolderMirrorReference> foldersLinksToMe = new ArrayList<FolderMirrorReference>();
            if (sourceFolder.getFolderMirrorReferences() != null) {
                foldersLinksToMe = sourceFolder.getFolderMirrorReferences();
            }
            Template sourceTemplate = sourceFolder.getTemplate();
            sourceFolder.setFolderMirrorReferences(null);
            sourceFolder.setTemplate(null);
            planDAO.update(sourceFolder);
            Contract contract = contractDAO.findById(PlanUtils.getContractNumberByPath(parentTreeNode.getPath()));
            foldersLinksToMe.add(new FolderMirrorReference(foldersLinksToMe.size(), contract.getContractDesignation(),
                    contract.getCompany().getName(), newFolder.getPath()));
            sourceFolder.setFolderMirrorReferences(foldersLinksToMe);
            sourceFolder.setTemplate(sourceTemplate);
            planDAO.update(sourceFolder);
        }
        Collections.sort(treeNodes, new TreeNodeComparatorByOrder());
        treeNodes.add(0, parentTreeNode);
        return treeNodes;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    private Folder prepareFolderForInsert(Folder newFolder, User userInSession)
            throws JackrabbitException, ObjectNotFoundException, BusinessException {

        Folder parentFolder = planDAO.findFolder(newFolder.getParentPath(), false);

        //Parent node exists
        if (parentFolder == null) {
            throw new ObjectNotFoundException("Requested folder was not found", ObjectNotFoundException.Type.FOLDER);
        }

        //New folder path exists
        if (planDAO.findFolder(parentFolder.getPath() + "/folders/" + newFolder.getName(), false) != null) {
            throw new JackrabbitException("Folder with same path already exists");
        }

        if (newFolder.getActive() == null) {
            newFolder.setActive(false);
        }

        if (newFolder.getIncludeInMenu() == null) {
            newFolder.setIncludeInMenu(false);
        }

        if (newFolder.getFolderHeader() == null) {
            newFolder.setFolderHeader("");
        }

        if (newFolder.getFolderFooter() == null) {
            newFolder.setFolderFooter("");
        }

        newFolder.setLastSaveDate(new Date());
        newFolder.setLastSaveAuthor(userInSession.getName());
        newFolder.setPath(parentFolder.getPath() + "/folders/" + newFolder.getName());
        newFolder.setPublishedAuthor(null);
        newFolder.setPublishedDate(null);
        newFolder.setPublishedRelatedPath(null);

        if (newFolder.getPermissions() != null && newFolder.getPermissions().size() != 0) {

            List<Permission> toInsertPermissionList = new ArrayList<Permission>(
                    permissionDAO.findByIdForSelect(newFolder.getPermissions(),
                            PlanUtils.getContractNumberByPath(newFolder.getParentPath())));

            if (toInsertPermissionList.size() != newFolder.getPermissions().size()) {
                throw new BusinessException("could not select all permission from contract");
            }

            List<com.criticalsoftware.certitools.entities.jcr.Permission> permissionList =
                    new ArrayList<com.criticalsoftware.certitools.entities.jcr.Permission>();

            for (Permission permission : toInsertPermissionList) {
                permissionList.add(new com.criticalsoftware.certitools.entities.jcr.Permission("/" + permission.getId(),
                        "" + permission.getName(), permission.getId()));
            }
            newFolder.setPermissions(permissionList);
        }

        return newFolder;
    }

    private String parseHTMLTemplate11MirrorWorker(String selectedFolderPath, String sourceFolderMirrorPath,
                                                   String text)
            throws UnsupportedEncodingException, BusinessException,
            ObjectNotFoundException, JackrabbitException {

        if (text != null) {
            String regexpAreaPEI = "<area[^>]* href=\"/pei/PEI.action\\?([^\">]+)\"";
            String regexpAreaPlan = "<area[^>]* href=\"/plan/Plan.action\\?([^\">]+)\"";

            String regexpImagePlan = "<img[^>]+ src=\"/plan/Plan.action\\?([^\">]+)\"";
            String regexpImagePEI = "<img[^>]+ src=\"/pei/PEI.action\\?([^\">]+)\"";

            String regexpLink = "<a href=\"([^\">]+)";

            Pattern pattAreaPEI = Pattern.compile(regexpAreaPEI,
                    Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
            Pattern pattAreaPlan = Pattern.compile(regexpAreaPlan,
                    Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

            Pattern pattImagePlan = Pattern.compile(regexpImagePlan,
                    Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.COMMENTS);
            Pattern pattImagePEI = Pattern.compile(regexpImagePEI,
                    Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.COMMENTS);

            Pattern pattLink = Pattern.compile(regexpLink,
                    Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

            Matcher mAreaPEI = pattAreaPEI.matcher(text);
            text = parseHTMLTemplate11MirrorWorkerRecursive(mAreaPEI, selectedFolderPath, sourceFolderMirrorPath,
                    false);
            Matcher mAreaPlan = pattAreaPlan.matcher(text);
            text = parseHTMLTemplate11MirrorWorkerRecursive(mAreaPlan, selectedFolderPath, sourceFolderMirrorPath,
                    false);

            Matcher mImagePEI = pattImagePEI.matcher(text);
            text = parseHTMLTemplate11MirrorWorkerRecursive(mImagePEI, selectedFolderPath, sourceFolderMirrorPath,
                    false);
            Matcher mImagePlan = pattImagePlan.matcher(text);
            text = parseHTMLTemplate11MirrorWorkerRecursive(mImagePlan, selectedFolderPath, sourceFolderMirrorPath,
                    false);

            Matcher mLink = pattLink.matcher(text);
            text = parseHTMLTemplate11MirrorWorkerRecursive(mLink, selectedFolderPath, sourceFolderMirrorPath, true);
            return text;
        }
        return text;
    }

    private String parseHTMLTemplate11MirrorWorkerRecursive(Matcher m, String selectedFolderPath,
                                                            String sourceFolderMirrorPath,
                                                            boolean isLink)
            throws BusinessException, JackrabbitException, UnsupportedEncodingException {

        StringBuffer sb = new StringBuffer();
        Long peiId = null;
        String path = null, tagSrc, tag;
        ModuleType moduleType = null;
        Pattern generalPatter;
        Matcher generalMatcher;

        while (m.find()) {
            tag = m.group(0);
            tagSrc = m.group(1);

            if (isLink || (!isLink && tagSrc.contains("viewResource"))) {
                //Get planModuleType
                String regexpPlanModuleType = "planModuleType=([^&]+)";
                generalPatter = Pattern.compile(regexpPlanModuleType,
                        Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.COMMENTS);
                generalMatcher = generalPatter.matcher(tagSrc);
                if (generalMatcher.find()) {
                    for (ModuleType type : EnumSet.allOf(ModuleType.class)) {
                        if (type.toString().equals(generalMatcher.group(1))) {
                            moduleType = type;
                        }
                    }
                }
                //Get peiId
                String regexpPeiId = "peiId=([^&]+)";
                generalPatter = Pattern.compile(regexpPeiId,
                        Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.COMMENTS);
                generalMatcher = generalPatter.matcher(tagSrc);
                if (generalMatcher.find()) {
                    peiId = Long.parseLong(generalMatcher.group(1));
                }
                //Get Path
                String regexpPath = "path=([^\"]+)";
                generalPatter = Pattern.compile(regexpPath,
                        Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.COMMENTS);
                generalMatcher = generalPatter.matcher(tagSrc);
                if (generalMatcher.find()) {
                    path = generalMatcher.group(1);
                }
                if (peiId != null && path != null) {
                    if (moduleType == null) {
                        moduleType = ModuleType.PEI;
                    }

                    String unescapedPath = StringEscapeUtils.unescapeHtml(path);
                    unescapedPath = Utils.decodeURI(unescapedPath);

                    //See if source Folder exists
                    Folder folder = planDAO.findFolder("/" + com.criticalsoftware.certitools.util.PlanUtils
                            .ROOT_PLAN_FOLDER + "/" + moduleType + peiId + "/"
                            + PlanUtils.getOnlineOrOfflineFromPath(selectedFolderPath) + unescapedPath, false);

                    boolean isReferenceFound = false;
                    String newSrc = tagSrc;
                    if (folder != null && folder.getFolderMirrorReferences() != null && !folder
                            .getFolderMirrorReferences().isEmpty()) {

                        for (FolderMirrorReference folderMirrorReference : folder.getFolderMirrorReferences()) {
                            if (folderMirrorReference.getReferencePath().endsWith(unescapedPath) && PlanUtils
                                    .getContractNumberByPath(folderMirrorReference.getReferencePath()).equals(
                                            PlanUtils.getContractNumberByPath(selectedFolderPath))) {
                                newSrc = newSrc.replace(peiId.toString(),
                                        folderMirrorReference.getReferenceContractId().toString());
                                if (!newSrc.contains("planModuleType")) {
                                    newSrc = "&amp;planModuleType=" + folderMirrorReference.getModuleType().toString()
                                            + "&amp;" + newSrc;
                                } else {
                                    newSrc = newSrc.replace("planModuleType=" + moduleType.toString(),
                                            "planModuleType=" +
                                                    folderMirrorReference.getModuleType().toString());
                                }
                                //Replace path
                                newSrc = newSrc.replace(path, StringEscapeUtils.escapeHtml(PlanUtils.simplifyPath(
                                        folderMirrorReference.getReferencePath())));
                                tag = tag.replace(tagSrc, newSrc);
                                m.appendReplacement(sb, tag);
                                isReferenceFound = true;
                                break;
                            }
                        }
                    }
                    if (!isReferenceFound) {
                        //Replace peiId
                        newSrc = newSrc.replace(peiId.toString(),
                                PlanUtils.getContractNumberByPath(selectedFolderPath).toString());
                        if (!newSrc.contains("planModuleType")) {
                            newSrc =
                                    "&amp;planModuleType="
                                            + PlanUtils.getModuleTypeFromPath(selectedFolderPath)
                                            + "&amp;" + newSrc;
                        } else {
                            newSrc = newSrc.replace("planModuleType=" + moduleType.toString(),
                                    "planModuleType=" + PlanUtils
                                            .getModuleTypeFromPath(selectedFolderPath));
                        }

                        String simplifySourceFolderMirrorPath = PlanUtils.simplifyPath(sourceFolderMirrorPath);
                        String pathDifference = StringUtils.difference(simplifySourceFolderMirrorPath, unescapedPath);

                        if (!pathDifference.isEmpty()) {
                            int indexDifference =
                                    StringUtils.indexOfDifference(simplifySourceFolderMirrorPath, unescapedPath);

                            int nearSlashIndex = getNearSlashIndex(unescapedPath, indexDifference);
                            String newPath = "";
                            String[] splitSelectedPath = selectedFolderPath.split("/");

                            for (int i = 4; i <= splitSelectedPath.length - 2; i++) {
                                newPath += "/" + splitSelectedPath[i] + "/folders";
                                i++;
                            }
                            newPath += unescapedPath.substring(nearSlashIndex, unescapedPath.length());
                            newSrc = newSrc.replace(path,
                                    StringEscapeUtils.escapeHtml(newPath));

                        } else {
                            newSrc = newSrc.replace(path,
                                    StringEscapeUtils.escapeHtml(PlanUtils.simplifyPath(selectedFolderPath)));
                        }
                        tag = tag.replace(tagSrc, newSrc);
                        m.appendReplacement(sb, tag);
                    }
                }
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private int getNearSlashIndex(String path, int index) {
        String pathToSearch = path.substring(0, index);
        for (int i = pathToSearch.length() - 1; i >= 0; i--) {
            if (Character.toString(pathToSearch.charAt(i)).equals("/")) {
                return i;
            }
        }
        return 0;
    }
}