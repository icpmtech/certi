/*
 * $Id: PlanDAO.java,v 1.21 2013/10/20 02:02:47 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2013/10/20 02:02:47 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.persistence.plan;

import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.entities.jcr.Folder;
import com.criticalsoftware.certitools.entities.jcr.Resource;
import com.criticalsoftware.certitools.entities.jcr.RiskAnalysisElement;
import com.criticalsoftware.certitools.entities.jcr.Plan;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.util.ModuleType;
import com.criticalsoftware.certitools.util.TreeNode;

import javax.jcr.RepositoryException;
import javax.naming.NamingException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Collection;

/**
 * Plan data access object
 *
 * @author : lt-rico
 */
public interface PlanDAO {

    Plan findFullPEI(Long contractId, ModuleType moduleType) throws JackrabbitException;

    /**
     * Get the PEI with a certain id and without loading the offline and online list
     *
     * @param contractId id, matches the contract id associated
     * @param moduleType - module type
     * @return the PEI object
     * @throws JackrabbitException when something wrong with jackrabbit
     */
    Plan findPlan(Long contractId, ModuleType moduleType) throws JackrabbitException;

    /**
     * Gets the pei with the specified id if loadChildren is true, a "touch" to the offline and online folders is done
     *
     * @param contractId   id of the pei/contract to retrieve
     * @param loadChildren if loadChildren is true, offline and online folders are loaded (eager init)
     * @param moduleType   - module type
     * @return PEI
     * @throws JackrabbitException error acessing the Repository
     */
    Plan findPlan(Long contractId, boolean loadChildren, ModuleType moduleType) throws JackrabbitException;

    /**
     * Insert a PEI object
     *
     * @param pei, the PEI object
     * @throws JackrabbitException when something wrong with jackrabbit
     */
    void insert(Plan pei) throws JackrabbitException;

    /**
     * Updates a PEI object
     *
     * @param pei the PEI object
     * @throws JackrabbitException when something wrong with jackrabbit
     */
    void update(Plan pei) throws JackrabbitException;

    /**
     * Insert a Folder object
     *
     * @param folder the folder object
     * @throws JackrabbitException when something wrong with jackrabbit
     */
    void insert(Folder folder) throws JackrabbitException;

    /**
     * Updates a Folder object
     *
     * @param folder the folder object
     * @throws JackrabbitException when something wrong with jackrabbit
     */
    void update(Folder folder) throws JackrabbitException;

    /**
     * Removes a object according to given path
     *
     * @param path object path
     * @return true if delete was sucessful, false if there was an error, usually the error is: "path doesn't exist"
     * @throws JackrabbitException when something wrong with jackrabbit
     */
    boolean delete(String path) throws JackrabbitException;

    /**
     * Get all section folder names to build the menu
     *
     * @param peiPath the PEI path
     * @return a map of section path/name
     * @throws JackrabbitException when something wrong with jackrabbit
     */
    List<TreeNode> findOfflineSections(String peiPath) throws JackrabbitException;

    /**
     * Get a folder with all atomic atributes and permissions loaded
     *
     * @param pathToFolder path to folder
     * @param loadChildren load folder childrens
     * @return a folder object
     * @throws JackrabbitException when something wrong with jackrabbit
     */

    Folder findFolder(String pathToFolder, Boolean loadChildren) throws JackrabbitException;

    /**
     * Get all folder names to build the menu
     *
     * @param parentFolderPath the path of the folder
     * @return a map of subfolders path/names
     * @throws JackrabbitException when something wrong with jackrabbit
     * @throws com.criticalsoftware.certitools.business.exception.BusinessException
     *                             - error
     */
    List<TreeNode> findSubfolders(String parentFolderPath) throws JackrabbitException, BusinessException;

    /**
     * Gets a resource
     *
     * @param resourcePath the resource path
     * @return the Resource object
     * @throws JackrabbitException un exception
     */
    Resource findResource(String resourcePath) throws JackrabbitException;

    /**
     * Get all folder resources
     *
     * @param pathToFolder path to folder
     * @param type         with resource type
     * @return a list with resources
     * @throws JackrabbitException when something wrong with jackrabbit
     */
    List<Resource> findFolderResources(String pathToFolder, boolean type) throws JackrabbitException;

    /**
     * Finds all folders that are children of this path (Recursive) this depth setted
     *
     * @param path         - path to find
     * @param filterActive - filter by folder "active" property
     * @return - Flat list with all children, and their chlidren..
     * @throws JackrabbitException - when error
     */
    List<Folder> findFoldersLoaded(String path, boolean filterActive) throws JackrabbitException;

    /**
     * Returns all the folders (online or offline) of the specified PEI, organized in a Tree, composed by TreeNode
     *
     * @param contractId    id of contract/PEI
     * @param onlineFolder  if true return the folders from online version, otherwise from offline version
     * @param filterResults if results should be filtered by isNavigable (and includeInMenu)
     * @param moduleType    - module type
     * @return all the folders (online or offline) of the specified PEI, organized in a Tree, composed by TreeNode
     * @throws JackrabbitException error in JackRabbit
     * @throws BusinessException   - error
     */
    ArrayList<TreeNode> findFoldersTree(long contractId, boolean onlineFolder, boolean filterResults,
                                        ModuleType moduleType) throws JackrabbitException, BusinessException;

    ArrayList<TreeNode> findFoldersTreeWithoutTemplate11Mirror(long contractId, ModuleType moduleType)
            throws JackrabbitException, BusinessException;

    /**
     * Returns all the folders (online or offline) children of the specified folder. Online or offline information is
     * already determined by the path of the parentFolder given
     *
     * @param pathParentFolder path of parent folder
     * @param filterResults    if results should be filtered by isNavigable (and includeInMenu)
     * @return all the folders (online or offline) children of the specified folder
     * @throws JackrabbitException error in JackRabbit
     * @throws com.criticalsoftware.certitools.business.exception.BusinessException
     *                             - error
     */
    public ArrayList<Folder> findFoldersByParentFolder(String pathParentFolder, boolean filterResults)
            throws JackrabbitException, BusinessException;

    /**
     * Returns all PEI available. If withOnlineFolders only returns PEIs with online folders
     *
     * @param moduleType        - module type
     * @param withOnlineFolders if true only returns PEIs with online folders  @return PEI
     * @return plans list
     * @throws JackrabbitException when something went wrong with jackrabbit
     */
    List<Plan> findAllPlans(ModuleType moduleType, boolean withOnlineFolders) throws JackrabbitException;

    void moveToPath(String oldPath, String newPath) throws JackrabbitException;

    /**
     * Deletes the online folder, installationPhotoOnline and companyLogoOnline of the specified PEI
     *
     * @param contractId id of the PEI/contract
     * @param moduleType - module type
     * @throws JackrabbitException Jackrabbit error
     */
    void deleteOnlineFolder(long contractId, ModuleType moduleType) throws JackrabbitException;

    /**
     * Copies the offline folder, installationPhoto and companyLogo to the online folder, installationPhotoOnline and
     * companyLogoOnline of the specified PEI or copy a specific folder to online folder
     *
     * @param path       id of the PEI/contract
     * @param moduleType - module type
     * @throws JackrabbitException Jackrabbit error
     * @throws BusinessException   error
     */
    void copyOfflineToOnline(String path, ModuleType moduleType) throws JackrabbitException, BusinessException;

    /**
     * Checks if the specified pei (contractId/peiId) has at least one folder that uses the specified permission
     * (permissionId)
     *
     * @param permissionId id of the permission to search for
     * @param contractId   id of the pei/contract
     * @param moduleType   - module type
     * @return true if permission is used in the specified pei
     * @throws JackrabbitException jackrabbit error
     */
    boolean isPermissionInActivePEI(Long permissionId, long contractId, ModuleType moduleType)
            throws JackrabbitException;

    /**
     * Count all Risk Analysis for pagination
     *
     * @param path                        - path to search
     * @param riskAnalysisElementToFilter - search criterias
     * @return - count result
     * @throws JackrabbitException - when error
     */
    Integer countAllRiskAnalysis(String path, RiskAnalysisElement riskAnalysisElementToFilter)
            throws JackrabbitException;

    /**
     * Finds all path Risk Analysis, paginated and with filters.
     *
     * @param path                        - path to search
     * @param limit                       - max limit of results
     * @param offset                      - first result index
     * @param sortCriteria                - attribute to sort
     * @param sortDirection               - sort direction
     * @param riskAnalysisElementToFilter - search criterias
     * @param allResults                  - all results are return
     * @return - list with Risk Analysis
     * @throws JackrabbitException - when error
     */
    List<RiskAnalysisElement> findRiskAnalysis(String path, int limit, int offset, String sortCriteria,
                                               String sortDirection, RiskAnalysisElement riskAnalysisElementToFilter,
                                               Boolean allResults)
            throws JackrabbitException;

    /**
     * PEI Copy from source contract to target contract
     *
     * @param contractIdSource - source contract
     * @param contractIdTarget - target contract
     * @param moduleType       - module type
     * @throws JackrabbitException - error in repository
     */
    void copyPlan(Long contractIdSource, Long contractIdTarget, ModuleType moduleType) throws JackrabbitException;

    ArrayList<TreeNode> findTreeNodesForPermissions(long contractId, ModuleType moduleType) throws JackrabbitException;

    /**
     * Update all folders list and their children, new publish information : publishedDate and publishedAuthor
     *
     * @param folders         - to update folders
     * @param publishedAuthor - update author
     * @param publishedDate   - update date
     * @throws JackrabbitException - error in repository
     */
    void updateFolderPublishInfo(List<Folder> folders, String publishedAuthor, Date publishedDate)
            throws JackrabbitException;

    /**
     * Update plans Folders Mirror References when contract designation or company name changes
     *
     * @param contracts           - contracts to update
     * @param contractDesignation - new contractDesignation
     * @param companyName         - new companyName
     * @throws JackrabbitException - error in repository
     */
    void updatePlansFolderReferencePath(Collection<Contract> contracts, String contractDesignation,
                                        String companyName) throws JackrabbitException;

    /**
     * Check if plan has references to other plan.
     *
     * @param contractId - contract Id
     * @param moduleType - module Type
     * @return - true if it has references
     * @throws JackrabbitException - error in repository
     */
    boolean isPlanWithFolderReferences(long contractId, ModuleType moduleType) throws JackrabbitException;

    /**
     * Check if plan has Folders with template 11 Mirror
     *
     * @param contractId - contract Id
     * @param moduleType - application Module
     * @return - true if plan has folders with template 11 Mirror
     * @throws JackrabbitException - error in repository
     */
    boolean isPlanWithFolderTemplate11Mirror(long contractId, ModuleType moduleType) throws JackrabbitException;

    /**
     * Remove source folders references, online and offline
     *
     * @param contractId - contract Id
     * @param moduleType - module Type
     * @throws JackrabbitException - error in repository
     * @throws BusinessException   - error
     */
    void deleteSourceFoldersReferences(long contractId, ModuleType moduleType)
            throws JackrabbitException, BusinessException;

    /**
     * Performs the copy of a folder and all its content
     *
     * @param sourceFolder source folder
     * @param targetFolder target folder
     * @throws JackrabbitException error in repository
     */
    void copyFolder(String sourceFolder, String targetFolder) throws JackrabbitException;

    void importPlan(InputStream planFileInputStream, String relPathImport, String absPathImport,
                    String pathImportTarget) throws NamingException, RepositoryException, IOException;
}
