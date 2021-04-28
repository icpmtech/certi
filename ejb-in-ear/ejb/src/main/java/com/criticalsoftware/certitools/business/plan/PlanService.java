/*
 * $Id: PlanService.java,v 1.35 2013/12/18 03:50:12 pjfsilva Exp $
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

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.DeleteException;
import com.criticalsoftware.certitools.business.exception.IsReferencedException;
import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.entities.Permission;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.jcr.Folder;
import com.criticalsoftware.certitools.entities.jcr.Plan;
import com.criticalsoftware.certitools.entities.jcr.Resource;
import com.criticalsoftware.certitools.entities.jcr.RiskAnalysisElement;
import com.criticalsoftware.certitools.entities.jcr.Template6DocumentsElement;
import com.criticalsoftware.certitools.util.ModuleType;
import com.criticalsoftware.certitools.util.PaginatedListWrapper;
import com.criticalsoftware.certitools.util.TreeNode;

import javax.jcr.RepositoryException;
import javax.naming.NamingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * PEI Service
 *
 * @author : lt-rico
 */
public interface PlanService {

    /**
     * Insert a PEI object
     *
     * @param pei the PEI object
     * @throws JackrabbitException when something goes wrong with jackrabbit
     */
    void insert(Plan pei) throws JackrabbitException;

    /**
     * Get a PEI object associated with a contract
     *
     * @param user         - user in session
     * @param contractId   - contract id
     * @param loadChildren if true all folders area loaded
     * @param moduleType   - application module
     * @return a PEI object
     * @throws JackrabbitException              Jackrrabit error
     * @throws ObjectNotFoundException          PEI not found
     * @throws CertitoolsAuthorizationException user acess PEI
     */
    public Plan find(User user, Long contractId, boolean loadChildren, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, CertitoolsAuthorizationException;

    /**
     * Get a PEI object associated with a contract
     *
     * @param user       - user in session
     * @param contractId - contract Id
     * @param moduleType - application module
     * @return a PEI object
     * @throws JackrabbitException              Jackrrabit error
     * @throws ObjectNotFoundException          PEI not found
     * @throws CertitoolsAuthorizationException user acess PEI
     */
    Plan find(User user, Long contractId, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, CertitoolsAuthorizationException;

    Plan findPEIWithShowFullListPEI(User userInSession, Long peiId, ModuleType moduleType)
            throws JackrabbitException, CertitoolsAuthorizationException, ObjectNotFoundException;

    /**
     * Finds a full PEI for export
     *
     * @param user       - user in session
     * @param contractId the contract Id
     * @param moduleType - application module
     * @return a PEI object
     * @throws ObjectNotFoundException          when PEI not found
     * @throws JackrabbitException              when something wrong with jackrabbit
     * @throws CertitoolsAuthorizationException - user cannot acess PEI
     */
    Plan findFullPEI(User user, Long contractId, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, CertitoolsAuthorizationException;

    /**
     * Load full Plan for export. Go to all folders and check if folder template is 11Mirror. If it is go to
     * template11Mirror source Folder and parse HTML content for export
     *
     * @param user       - user in session
     * @param contractId - contract Id
     * @param moduleType - module Type
     * @return - Plan loadad
     * @throws ObjectNotFoundException          - parse error
     * @throws JackrabbitException              - error in repository
     * @throws CertitoolsAuthorizationException - user cannot acess
     * @throws BusinessException                - error
     * @throws UnsupportedEncodingException     - encode exception
     */
    Plan findFullPEIForExport(User user, Long contractId, ModuleType moduleType)
            throws ObjectNotFoundException, JackrabbitException, CertitoolsAuthorizationException, BusinessException,
            UnsupportedEncodingException;

    /**
     * Delete folder
     *
     * @param path          - folder path to delete
     * @param userInSession - user in session
     * @param moduleType    - module type
     * @return - null if cannot delete (validation error); empty string if there are nothing to update in tree ; folder
     * path to update in tree
     * @throws JackrabbitException              - error in repository
     * @throws ObjectNotFoundException          - object not found
     * @throws CertitoolsAuthorizationException - user cannot acess
     * @throws BusinessException                - error
     * @throws IsReferencedException            - error in repository
     * @throws DeleteException                  - user does not have permission to delete folder
     */
    List<String> deleteFolder(String path, User userInSession, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, CertitoolsAuthorizationException, BusinessException,
            IsReferencedException, DeleteException;

    /**
     * Update a PEI obect
     *
     * @param pei                          the pei to be updated
     * @param companyLogoContentType       content type of company logo
     * @param companyLogoInputStream       input stream of company logo
     * @param installationPhotoContentType content type of installation photo
     * @param installationPhotoInputStream input stream of installation photo
     * @param userInSession                user to validate
     * @param moduleType                   - module type
     * @throws JackrabbitException              when something goes wrong with jackrabbit
     * @throws BusinessException                when error
     * @throws ObjectNotFoundException          PEI not found
     * @throws CertitoolsAuthorizationException - when not authorized
     */
    void update(Plan pei, String companyLogoContentType, InputStream companyLogoInputStream,
                String installationPhotoContentType, InputStream installationPhotoInputStream, User userInSession,
                ModuleType moduleType)
            throws JackrabbitException, BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException;


    TreeNode updateFolder(Folder newFolder, User userInSession, Long contractId, ModuleType moduleType)
            throws IOException, JackrabbitException, ObjectNotFoundException, BusinessException,
            CertitoolsAuthorizationException;

    /**
     * Get a map representation of the PEI offline sections for contructing the menu
     *
     * @param user       user to validate
     * @param peiPath    the PEI object path
     * @param moduleType - application module
     * @return map with section path as a key and section name as value
     * @throws JackrabbitException              when something goes wrong with jackrabbit
     * @throws BusinessException                - error
     * @throws CertitoolsAuthorizationException - user cannot acess PEI
     * @throws ObjectNotFoundException          - pei not found
     */
    List<TreeNode> findOfflineSectionsForMenu(User user, String peiPath, ModuleType moduleType)
            throws JackrabbitException, BusinessException, CertitoolsAuthorizationException, ObjectNotFoundException;

    /**
     * Get a map representation of a folder subfolders for contructing the menu
     *
     * @param user             user to validate
     * @param parentFolderPath the parent folder path
     * @param moduleType       - application module
     * @return map with subfolder path as a key and subfolder name as value
     * @throws JackrabbitException              when something goes wrong with jackrabbit
     * @throws BusinessException                - error
     * @throws CertitoolsAuthorizationException - user cannot acess PEI
     * @throws ObjectNotFoundException          - pei not found
     */
    List<TreeNode> findSubfoldersForMenu(User user, String parentFolderPath, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, BusinessException, CertitoolsAuthorizationException;

    /**
     * Find folder. this method doesn t check for folder permissions
     *
     * @param pathToFolder - folder path
     * @param loadChildren - to load children
     * @return - folder
     * @throws JackrabbitException     - error in repository
     * @throws ObjectNotFoundException - path not found
     */
    Folder findFolderAllAllowed(String pathToFolder, boolean loadChildren)
            throws JackrabbitException, ObjectNotFoundException;

    /**
     * Get a folder object according to given path
     *
     * @param pathToFolder path of folder
     * @param loadChildren load children param
     * @param user         user
     * @param moduleType   - module type
     * @return Folder object
     * @throws JackrabbitException              when something goes wrong with jackrabbit
     * @throws BusinessException                - error
     * @throws CertitoolsAuthorizationException - user cannot acess PEI
     * @throws ObjectNotFoundException          - pei not found
     */
    Folder findFolder(String pathToFolder, Boolean loadChildren, User user, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, BusinessException, CertitoolsAuthorizationException;

    /**
     * Find a folder with the specified path, loading children and checking if user is allowed to check this folder
     *
     * @param pathToFolder  path of folder
     * @param loadChildren  load children?
     * @param userInSession user trying to access this pei
     * @return folder
     * @throws JackrabbitException              when something goes wrong with jackrabbit
     * @throws ObjectNotFoundException          user not found
     * @throws BusinessException                path not valid
     * @throws CertitoolsAuthorizationException not authorized to see this folder
     */
    public Folder findFolderAllowed(String pathToFolder, Boolean loadChildren, User userInSession)
            throws JackrabbitException, ObjectNotFoundException, BusinessException, CertitoolsAuthorizationException;

    /**
     * Get a list resources constructed from folder and subfolder path wich have template resource
     *
     * @param pathToFolder    folder path
     * @param isImage         is image type
     * @param useParentFolder use folder parent to load
     * @param moduleType      - application module
     * @return list of resources
     * @throws JackrabbitException when somethig goes wrong
     * @throws BusinessException   - error
     */
    List<Resource> findFolderResources(String pathToFolder, boolean isImage, boolean useParentFolder,
                                       ModuleType moduleType)
            throws JackrabbitException, BusinessException;

    List<Resource> getResources(String pathToFolder, boolean isImages)
            throws JackrabbitException, BusinessException;

    /**
     * Publishes the PEI or a specific folder (this is detect by the path) of the contract specified
     *
     * @param path          path to publish
     * @param userInSession User trying to do this operation
     * @param moduleType    - application module
     * @throws JackrabbitException                                                  error in Jackrabbit
     * @throws ObjectNotFoundException                                              PEI not found
     * @throws CertitoolsAuthorizationException                                     User isn't client pei manager or pei manager for this PEI
     * @throws BusinessException - error
     */
    void copyOfflineToOnline(String path, User userInSession, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, CertitoolsAuthorizationException, BusinessException;

    /**
     * Returns all the nodes of the specified branch (online/offline) of the specified PEI that this user can access
     *
     * @param contractId   id of the contract/PEI to search
     * @param user         user that wants to access this PEI
     * @param onlineFolder if true, returns the online branch
     * @param moduleType   - application module
     * @return list of TreeNodes the user can access, ordered by parent/children (children are ordered by the order
     * field and alphabetical name)
     * @throws JackrabbitException                                                  jackrabbit exception when something goes wrong
     * @throws ObjectNotFoundException                                              PEI not found
     * @throws CertitoolsAuthorizationException                                     in case of the User or Client Pei Manager trying to access a PEI that they don't
     *                                                                              have a valid contract associated
     * @throws BusinessException - error
     */
    public ArrayList<TreeNode> findFoldersTreeAllowed(long contractId, User user, boolean onlineFolder,
                                                      ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, CertitoolsAuthorizationException, BusinessException;

    /**
     * If user is peimanager, can see all PEIs. Otherwise, only get PEIs associated with that user
     *
     * @param user       User trying to access all PEIs
     * @param moduleType - application module
     * @return List of PEIs
     * @throws JackrabbitException     Error with Jackrabbit
     * @throws ObjectNotFoundException User not found
     */
    public List<Plan> findAllPlansAllowed(User user, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException;

    /**
     * Find all permissions associated with a contract.
     *
     * @param user       user to validate
     * @param contractId Contract ID
     * @param moduleType - application module
     * @return List of permissions
     * @throws CertitoolsAuthorizationException user cannot acess PEI
     * @throws ObjectNotFoundException          User not found
     */
    public Collection<Permission> findPermissions(User user, long contractId, ModuleType moduleType)
            throws ObjectNotFoundException, CertitoolsAuthorizationException;

    /**
     * Create a new permission
     *
     * @param permission Permission name
     * @param contractId Contract ID
     * @param user-      user in session
     * @param moduleType - application module
     * @throws BusinessException                when error
     * @throws CertitoolsAuthorizationException user cannot acess PEI
     * @throws ObjectNotFoundException          object not found
     */
    public void insertPermission(String permission, long contractId, User user, ModuleType moduleType)
            throws BusinessException, CertitoolsAuthorizationException, ObjectNotFoundException;

    /**
     * Deletes a permission. Passing the permission name because it is unique for the given contract.
     *
     * @param permission Permission name
     * @param contractId Contract ID
     * @param user       - user in session
     * @param moduleType - application module
     * @throws ObjectNotFoundException          when folder not found
     * @throws CertitoolsAuthorizationException -  user cannot acess PEI
     */
    public void deletePermission(String permission, long contractId, User user, ModuleType moduleType)
            throws ObjectNotFoundException, CertitoolsAuthorizationException;

    /**
     * Insert Folder with Resource Template
     *
     * @param newFolder     new Folder to Insert
     * @param userInSession user in session
     * @param contractId    contract Id
     * @param moduleType    - application module
     * @return tree node insertd
     * @throws IOException                      a IO exception when reading file
     * @throws JackrabbitException              jackrabbit exception when something goes wrong
     * @throws ObjectNotFoundException          when folder not found
     * @throws BusinessException                when permission not found
     * @throws CertitoolsAuthorizationException - when not authorized
     */
    List<TreeNode> insertFolder(Folder newFolder, User userInSession, Long contractId, ModuleType moduleType)
            throws IOException, JackrabbitException, ObjectNotFoundException, BusinessException,
            CertitoolsAuthorizationException;

    boolean isPermissionInActivePEI(String permission, long contractId, User user, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, CertitoolsAuthorizationException;

    boolean isPermissionInActiveUserContract(String permission, long contractId, User user, ModuleType moduleType)
            throws ObjectNotFoundException, CertitoolsAuthorizationException;

    /**
     * Finds all path Risk Analysis, paginated and with filters.
     *
     * @param paginatedListWrapper        - paginatedListWrapper
     * @param mirrorPath                  - folder mirror path
     * @param path                        - path to search
     * @param riskAnalysisElementToFilter - search criterias
     * @return - list with Risk Analysis
     * @throws JackrabbitException - when error in repository
     * @throws BusinessException   - error
     */
    PaginatedListWrapper<RiskAnalysisElement> findRiskAnalysis(
            PaginatedListWrapper<RiskAnalysisElement> paginatedListWrapper, String mirrorPath, String path,
            RiskAnalysisElement riskAnalysisElementToFilter) throws JackrabbitException, BusinessException;


    /**
     * Find All Folders with FAQ and FAQElement Templates that are children of path. This return a Flat list with
     * Folders that contains each folder this depth setted. "special" attribute is setted if folder template is instance
     * of TemplateFAQ for web tier.
     *
     * @param path          - beginning path folder
     * @param userInSession - user
     * @param moduleType    - application module
     * @return - list
     * @throws JackrabbitException                  - repository error
     * @throws ObjectNotFoundException              - object not found error
     * @throws BusinessException                    - business error
     * @throws CertitoolsAuthorizationException     - user cannot acess
     * @throws java.io.UnsupportedEncodingException - encode error
     */
    List<Folder> findFAQTemplateFolders(String path, User userInSession, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, BusinessException, CertitoolsAuthorizationException,
            UnsupportedEncodingException;

    List<String> findDocumentsTemplateFiltersList(String path, User userInSession, Template6DocumentsElement element,
                                                  ModuleType moduleType)
            throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException, JackrabbitException;

    /**
     * Find All Folders with DocumentsElement Templates that are children of path. This return a Flat list with Folders
     * that contains each folder.
     *
     * @param path          - path to search
     * @param userInSession - user
     * @param filter        filter to apply
     * @param isExport      - is displaytag export request
     * @param moduleType    - application module
     * @return - DocumentsElement list
     * @throws JackrabbitException               - error in repository
     * @throws ObjectNotFoundException           - folder not found
     * @throws BusinessException                 - error
     * @throws CertitoolsAuthorizationException- cannot acess
     */
    List<Folder> findDocumentsTemplateFolders(String path, User userInSession, Template6DocumentsElement filter,
                                              Boolean isExport, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, BusinessException, CertitoolsAuthorizationException;


    List<Folder> findRichTextWithAttachTemplateFolders(String path, User userInSession, ModuleType moduleType)
            throws JackrabbitException, BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException;

    /**
     * Find All Folders with Index Templates that are children of path. This return a Flat list with Folders that
     * contains each folder this depth setted.
     *
     * @param path          - beginning path folder
     * @param userInSession - user
     * @param moduleType    - application module
     * @return - list
     * @throws JackrabbitException              - repository error
     * @throws ObjectNotFoundException          - object not found error
     * @throws BusinessException                - business error
     * @throws CertitoolsAuthorizationException - user cannot acess
     */
    List<Folder> findIndexTemplateFolders(String path, User userInSession, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, BusinessException, CertitoolsAuthorizationException;

    List<Folder> findContactsTemplateFolders(String path, User userInSession, String searchPhrase,
                                             String contactType, ModuleType moduleType)
            throws JackrabbitException, BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException;

    /**
     * Find all the different resource types of the Folders with MeansResourcesTemplates that are children of path.
     *
     * @param path          The path.
     * @param userInSession The logged user.
     * @param moduleType    The application module code.
     * @return The list of types found.
     * @throws BusinessException
     * @throws ObjectNotFoundException
     * @throws CertitoolsAuthorizationException
     * @throws JackrabbitException
     */
    List<String> findMeansResourcesTemplateFiltersList(String path, User userInSession, ModuleType moduleType)
            throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException, JackrabbitException;

    /**
     * Find the Folders with MeansResourcesTemplates that are children of path and have the given type
     * and include the given search string.
     *
     * @param path          The path.
     * @param userInSession The logged user.
     * @param searchPhrase  The search filter.
     * @param resourceType  The resource type filter.
     * @param moduleType    The application module code.
     * @return The list a Folders found.
     * @throws JackrabbitException
     * @throws BusinessException
     * @throws ObjectNotFoundException
     * @throws CertitoolsAuthorizationException
     */
    List<Folder> findMeansResourcesTemplateFolders(String path, User userInSession, String searchPhrase,
                                                   String resourceType, ModuleType moduleType)
            throws JackrabbitException, BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException;

    /**
     * Load Flat Folders List for Tree, that contains path parents, brothers and children. Each tree Node has setted the
     * name, path, depth and if it has children
     *
     * @param path       - path
     * @param user       - user
     * @param moduleType -  application module
     * @return - list
     * @throws JackrabbitException              - when error in repository
     * @throws BusinessException                - when error
     * @throws CertitoolsAuthorizationException - when user cannot acess PEI
     * @throws ObjectNotFoundException          - object not found
     */
    List<TreeNode> findOpenTreeToPath(String path, User user, ModuleType moduleType)
            throws JackrabbitException, BusinessException, CertitoolsAuthorizationException, ObjectNotFoundException;

    /**
     * Find All Tree sections for plan
     *
     * @param contractId - contractId
     * @param moduleType - module
     * @return - contract tree nodes
     * @throws JackrabbitException                                                  - error
     * @throws BusinessException - error
     */
    List<TreeNode> findAllTreeWithoutTemplate11Mirror(Long contractId, ModuleType moduleType)
            throws JackrabbitException, BusinessException;

    List<Folder> findPlanClickableAndResourcesFolders(String path, ModuleType moduleType)
            throws JackrabbitException, BusinessException;

    List<String> findRiskAnalysisList(String path, RiskAnalysisElement riskAnalysisElement, boolean ajaxLoad)
            throws JackrabbitException;

    /**
     * PEI Copy from source contract to target contract
     *
     * @param contractIdSource - source contract
     * @param contractIdTarget - target contract
     * @param moduleType       -  application module
     * @throws JackrabbitException     - error in repository
     * @throws ObjectNotFoundException - pei not found
     * @throws IsReferencedException   - when folder has FolderMirror references, user cannot copy plan
     * @throws BusinessException       - error in copy
     */
    void copyPlan(Long contractIdSource, Long contractIdTarget, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, IsReferencedException, BusinessException;

    List<TreeNode> findPlanPermissionFullSchema(User user, long contractId, ModuleType moduleType)
            throws ObjectNotFoundException, JackrabbitException, CertitoolsAuthorizationException;

    boolean isUserPlanManager(long contractId, User userInSession) throws ObjectNotFoundException;

    List<TreeNode> findPermissionUsages(User user, Long permissionId, ModuleType moduleType)
            throws ObjectNotFoundException, JackrabbitException, CertitoolsAuthorizationException;

    List<Long> findParentsFolderPermissions(String path, Boolean considerItself)
            throws JackrabbitException, ObjectNotFoundException;

    List<Folder> findProcedureTemplateFiltersList(String path, User userInSession, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, BusinessException, CertitoolsAuthorizationException;

    void updateToVersion3() throws JackrabbitException;

    /**
     * Parse template HMTL. Search in text for internal links, images etc and replace src path for target link.
     *
     * @param sourcePath    - source path
     * @param referencePath - target Path
     * @param text          - text to parse
     * @return - text parsed
     * @throws UnsupportedEncodingException - encoding exception
     * @throws BusinessException            - business exception
     * @throws ObjectNotFoundException      - folder not found
     * @throws JackrabbitException          - error in repository
     */
    String parseHTMLTemplate11Mirror(String sourcePath, String referencePath, String text)
            throws UnsupportedEncodingException, BusinessException,
            ObjectNotFoundException, JackrabbitException;

    /**
     * Delete Plan Folder references.
     *
     * @param contractId - contract Id
     * @param moduleType - application module
     * @param operation- accept three values : BOTH: delete online and offline references, ONLINE: delete online
     *                   references, OFFLINE: delete offline references
     * @throws JackrabbitException     - error in repository
     * @throws ObjectNotFoundException - plan not found
     */
    void deletePlanFolderReferences(long contractId, ModuleType moduleType, String operation)
            throws JackrabbitException, ObjectNotFoundException;

    void deletePlanFolderReference(String path) throws JackrabbitException;

    /**
     * Removes all references in path to the linkFolder
     *
     * @param path       path of the folder with the references
     * @param linkFolder folder with path equal to the reference to be removed
     * @throws JackrabbitException error in repository
     */
    void deletePlanFolderReference(String path, Folder linkFolder) throws JackrabbitException;

    /**
     * Converts a folder of type link to normal
     *
     * @param folder the folder of type link
     * @throws JackrabbitException error in repository
     */
    void convertLinkToNormal(Folder folder) throws JackrabbitException;

    void importPlan(User user, InputStream importFileIS, long contractTargetId, ModuleType moduleType)
            throws IOException, ObjectNotFoundException, CertitoolsAuthorizationException, JackrabbitException, NamingException, RepositoryException;

    void processImportedPlan(User user, InputStream importFileIS, long contractTargetId, ModuleType moduleType)
            throws JackrabbitException, ObjectNotFoundException, CertitoolsAuthorizationException;
}
