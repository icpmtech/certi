/*
 * $Id: PlanDAOEJB.java,v 1.36 2013/12/16 18:35:10 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2013/12/16 18:35:10 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.persistence.plan;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.jcr.*;
import com.criticalsoftware.certitools.util.Logger;
import com.criticalsoftware.certitools.util.ModuleType;
import com.criticalsoftware.certitools.util.PlanUtils;
import com.criticalsoftware.certitools.util.TreeNode;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.core.query.QueryImpl;
import org.apache.jackrabbit.ocm.exception.ObjectContentManagerException;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.AnnotationMapperImpl;
import org.apache.jackrabbit.ocm.nodemanagement.impl.RepositoryUtil;
import org.apache.jackrabbit.ocm.query.Filter;
import org.apache.jackrabbit.util.ISO9075;
import org.jboss.annotation.ejb.LocalBinding;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jcr.*;
import javax.jcr.query.QueryResult;
import javax.naming.NamingException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * PEI data access object implementation
 *
 * @author : lt-rico
 */
@Stateless
@Local(PlanDAO.class)
@LocalBinding(jndiBinding = "certitools/PlanDAO")
@RolesAllowed("private")
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class PlanDAOEJB implements PlanDAO {

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = Logger.getInstance(PlanDAOEJB.class);

    private static Mapper mapper;

    // field required by isPermissionInActivePlan recursive method to hold the result
    private boolean isPermissionInActivePlan = false;
    private boolean isPlanWithFolderReferences = false;
    private boolean isPlanWithFolderTemplate11Mirror = false;

    static {
        List<Class> classes = new ArrayList<Class>();
        // Call this method for each persistent class
        classes.add(HierarchyNode.class);
        classes.add(Plan.class);
        classes.add(Folder.class);
        classes.add(Resource.class);
        classes.add(Permission.class);
        classes.add(Template.class);
        classes.add(RiskAnalysisElement.class);
        classes.add(FolderMirrorReference.class);
        classes.add(Link.class);

        for (Template.Type type : EnumSet.allOf(Template.Type.class)) {
            try {
                classes.add(Class.forName("com.criticalsoftware.certitools.entities.jcr." + type.getName()));
            } catch (ClassNotFoundException e) {
                LOGGER.error("[PlanDAOEJB] Template not found");
            }
        }

        mapper = new AnnotationMapperImpl(classes);
    }

    @SuppressWarnings("EjbEnvironmentInspection")
    @javax.annotation.Resource(mappedName = "java:jcr/local")
    private Repository repository;

    public Plan findFullPEI(Long contractId, ModuleType moduleType) throws JackrabbitException {
        Plan pei;
        ObjectContentManager ocm = null;
        try {
            ocm = getObjectManager(connect());
            pei = (Plan) ocm.getObject("/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + moduleType + contractId);
            loadChildrens(pei.getOnline());
            loadChildrens(pei.getOffline());
        } catch (RepositoryException e) {
            throw new JackrabbitException("Error connecting to the content repository", e);
        } finally {
            if (ocm != null) {
                ocm.logout();
            }
        }
        return pei;
    }

    public Plan findPlan(Long contractId, ModuleType moduleType) throws JackrabbitException {
        return findPlan(contractId, false, moduleType);
    }

    public Plan findPlan(Long contractId, boolean loadChildren, ModuleType moduleType) throws JackrabbitException {
        Plan pei;
        ObjectContentManager ocm = null;
        try {
            ocm = getObjectManager(connect());
            pei = (Plan) ocm.getObject("/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + moduleType + contractId);

            if (loadChildren && pei != null) {
                if (pei.getOffline() != null) {
                    pei.getOffline().size();
                }
                if (pei.getOnline() != null) {
                    pei.getOnline().size();
                }
            }
        } catch (RepositoryException e) {
            throw new JackrabbitException("Error connecting to the content repository", e);
        } finally {
            if (ocm != null) {
                ocm.logout();
            }
        }
        return pei;
    }

    public void insert(Plan pei) throws JackrabbitException {
        pei.setPath("/" + PlanUtils.ROOT_PLAN_FOLDER + pei.getPath());
        insertObject(pei);
    }

    public void update(Plan pei) throws JackrabbitException {
        updateObject(pei);
    }

    public void insert(Folder folder) throws JackrabbitException {
        insertObject(folder);
    }

    public void update(Folder folder) throws JackrabbitException {
        updateObject(folder);
    }

    public boolean delete(String path) throws JackrabbitException {
        ObjectContentManager ocm = null;
        try {
            ocm = getObjectManager(connect());
            ocm.remove(path);
            ocm.save();
        } catch (ObjectContentManagerException e) {
            // path doesn't exist
            return false;
        } catch (RepositoryException e) {
            throw new JackrabbitException("Error removing from the content repository", e);
        }
        finally {
            if (ocm != null) {
                ocm.logout();
            }
        }

        return true;
    }

    public List<TreeNode> findOfflineSections(String peiPath) throws JackrabbitException {
        List<TreeNode> nodes = null;
        ObjectContentManager ocm = null;
        try {
            ocm = getObjectManager(connect());
            Plan pei = (Plan) ocm.getObject(peiPath);
            nodes = new ArrayList<TreeNode>();
            pei.getOffline().size();
            for (Folder f : pei.getOffline()) {
                if (f.getFolderMirrorReferences() != null && !f.getFolderMirrorReferences().isEmpty()) {
                    nodes.add(new TreeNode(f.getOrder(), f.getName(), f.getPath(), "mirrorReferences"));
                } else {
                    nodes.add(new TreeNode(f.getOrder(), f.getName(), f.getPath()));
                }

            }
        } catch (RepositoryException e) {
            throw new JackrabbitException("Error connecting to the content repository", e);
        } finally {
            if (ocm != null) {
                ocm.logout();
            }
        }
        return nodes;
    }

    public Folder findFolder(String pathToFolder, Boolean loadChildren) throws JackrabbitException {
        Folder folder;
        ObjectContentManager ocm = null;
        try {
            ocm = getObjectManager(connect());
            folder = (Folder) ocm.getObject(pathToFolder);
            if (folder != null) {
                if (loadChildren) {
                    folder.getFolders().size();
                }
            }
        } catch (RepositoryException e) {
            throw new JackrabbitException("Error connecting to the content repository", e);
        } finally {
            if (ocm != null) {
                ocm.logout();
            }
        }
        return folder;
    }

    public List<TreeNode> findSubfolders(String parentFolderPath) throws JackrabbitException, BusinessException {
        List<TreeNode> nodes = new ArrayList<TreeNode>();
        ObjectContentManager ocm = null;
        try {
            ocm = getObjectManager(connect());
            Folder folder = (Folder) ocm.getObject(parentFolderPath);
            if (folder != null && folder.getFolders() != null) {
                for (Folder f : folder.getFolders()) {
                    if (f.getFolderMirrorReferences() != null && !f.getFolderMirrorReferences().isEmpty()) {
                        nodes.add(new TreeNode(f.getOrder(), f.getName(), f.getPath(), "mirrorReferences"));
                    } else if (f.getTemplate() != null && f.getTemplate().getName()
                            .equals(Template.Type.TEMPLATE_MIRROR.getName())) {
                        Template11Mirror template11Mirror = (Template11Mirror) f.getTemplate();
                        //Same contract, arrow up
                        if (template11Mirror.getSourceContractId()
                                .equals(PlanUtils.getContractNumberByPath(parentFolderPath))) {
                            nodes.add(new TreeNode(f.getOrder(), f.getName(), f.getPath(),
                                    "template11Mirror-same-contract"));
                        } else {
                            nodes.add(new TreeNode(f.getOrder(), f.getName(), f.getPath(),
                                    "template11Mirror-different-contract"));
                        }
                    } else {
                        nodes.add(new TreeNode(f.getOrder(), f.getName(), f.getPath()));
                    }
                }
            }
        } catch (RepositoryException e) {
            throw new JackrabbitException("Error connecting to the content repository", e);
        } finally {
            if (ocm != null) {
                ocm.logout();
            }
        }
        return nodes;
    }

    public Resource findResource(String pathToResource) throws JackrabbitException {
        Resource resource;
        ObjectContentManager ocm = null;
        try {
            ocm = getObjectManager(connect());
            resource = (Resource) ocm.getObject(pathToResource);

        } catch (RepositoryException e) {
            throw new JackrabbitException("Error connecting to the content repository", e);
        } finally {
            if (ocm != null) {
                ocm.logout();
            }
        }
        return resource;
    }

    @SuppressWarnings({"unchecked"})
    public List<Resource> findFolderResources(String pathToFolder, boolean isImage) throws JackrabbitException {
        List<Resource> resources = new ArrayList<Resource>();
        ObjectContentManager ocm = null;
        Session session;
        try {
            session = connect();
            ocm = getObjectManager(session);
            Folder folder = (Folder) ocm.getObject(pathToFolder);
            if (folder != null) {
                fetchResources(folder.getFolders(), resources, isImage);
            }
        } catch (RepositoryException e) {
            throw new JackrabbitException("Error connecting to the content repository", e);
        } finally {
            if (ocm != null) {
                ocm.logout();
            }
        }
        return resources;
    }

    public List<Folder> findFoldersLoaded(String path, boolean filterActive) throws JackrabbitException {
        ObjectContentManager ocm = null;
        Folder folder;
        List<Folder> foldersList = new ArrayList<Folder>();

        try {
            ocm = getObjectManager(connect());
            folder = (Folder) ocm.getObject(path);

            if (folder == null) {
                return null;
            }

            if (folder.getFolders() == null) {
                return null;
            }
            converToFolderArrayList(folder, foldersList, true, 0, filterActive);
        } catch (RepositoryException e) {
            throw new JackrabbitException("Error connecting to the content repository", e);
        } finally {
            if (ocm != null) {
                ocm.logout();
            }
        }
        return foldersList;
    }

    public ArrayList<TreeNode> findTreeNodesForPermissions(long contractId, ModuleType moduleType)
            throws JackrabbitException {
        // reset the results list
        resultArrayList = new ArrayList<TreeNode>();
        TreeNode resultTree = new TreeNode("root", "/", 0);

        Plan pei;
        ObjectContentManager ocm = null;
        try {
            ocm = getObjectManager(connect());
            pei = (Plan) ocm.getObject("/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + moduleType + contractId);

            if (pei == null) {
                return null;
            }

            List<Folder> folders;
            if (pei.getOffline() == null) {
                return null;
            }

            pei.getOffline().size();
            folders = pei.getOffline();

            for (Folder folder : folders) {
                findChildrenFoldersForPermissions(folder, resultTree);
            }
        } catch (RepositoryException e) {
            throw new JackrabbitException("Error connecting to the content repository", e);
        } finally {
            if (ocm != null) {
                ocm.logout();
            }
        }
        // convert the tree result to a flat arraylist and remove the first node (root node)
        convertToArrayList(resultTree);
        resultArrayList.remove(0);

        return resultArrayList;
    }

    public ArrayList<TreeNode> findFoldersTree(long contractId, boolean onlineFolder, boolean filterResults,
                                               ModuleType moduleType) throws JackrabbitException, BusinessException {

        // reset the results list
        resultArrayList = new ArrayList<TreeNode>();
        TreeNode resultTree = new TreeNode("root", "/", 0);

        Plan pei;
        ObjectContentManager ocm = null;
        try {
            ocm = getObjectManager(connect());
            pei = (Plan) ocm.getObject("/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + moduleType + contractId);

            if (pei == null) {
                return null;
            }

            List<Folder> folders;

            if (onlineFolder) {
                if (pei.getOnline() == null) {
                    return null;
                }

                pei.getOnline().size();

                folders = pei.getOnline();
            } else {
                if (pei.getOffline() == null) {
                    return null;
                }

                pei.getOffline().size();

                folders = pei.getOffline();
            }
            for (Folder folder : folders) {
                findChildrenfolder(folder, resultTree, filterResults, filterResults, 0, false);
            }

        } catch (RepositoryException e) {
            throw new JackrabbitException("Error connecting to the content repository", e);
        } finally {
            if (ocm != null) {
                ocm.logout();
            }
        }

        // convert the tree result to a flat arraylist and remove the first node (root node)
        convertToArrayList(resultTree);
        resultArrayList.remove(0);

        return resultArrayList;
    }

    public ArrayList<TreeNode> findFoldersTreeWithoutTemplate11Mirror(long contractId, ModuleType moduleType)
            throws JackrabbitException, BusinessException {
        // reset the results list
        resultArrayList = new ArrayList<TreeNode>();
        TreeNode resultTree = new TreeNode("root", "/", 0);

        Plan pei;
        ObjectContentManager ocm = null;
        try {
            ocm = getObjectManager(connect());
            pei = (Plan) ocm.getObject("/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + moduleType + contractId);

            if (pei == null) {
                return null;
            }

            List<Folder> folders;
            if (pei.getOffline() == null) {
                return null;
            }
            pei.getOffline().size();
            folders = pei.getOffline();

            for (Folder folder : folders) {
                findChildrenFolderWithoutTemplate11Mirror(folder, resultTree, 0);
            }

        } catch (RepositoryException e) {
            throw new JackrabbitException("Error connecting to the content repository", e);
        } finally {
            if (ocm != null) {
                ocm.logout();
            }
        }
        // convert the tree result to a flat arraylist and remove the first node (root node)
        convertToArrayList(resultTree);
        resultArrayList.remove(0);
        return resultArrayList;
    }

    public ArrayList<Folder> findFoldersByParentFolder(String pathParentFolder, boolean filterResults)
            throws JackrabbitException, BusinessException {

        // reset the results list
        resultArrayListFolders = new ArrayList<Folder>();
        TreeNode resultTree = new TreeNode("root", "/", 0);

        ObjectContentManager ocm = null;
        Folder folder;

        try {
            ocm = getObjectManager(connect());
            folder = (Folder) ocm.getObject(pathParentFolder);

            if (folder == null) {
                return null;
            }

            if (folder.getFolders() == null) {
                return null;
            }

            for (Folder folderTemp : folder.getFolders()) {
                findChildrenfolder(folderTemp, resultTree, filterResults, filterResults, 0, true);
            }

        } catch (RepositoryException e) {
            throw new JackrabbitException("[findFoldersTreeByParentFolder] Error connecting to the content repository",
                    e);
        } finally {
            if (ocm != null) {
                ocm.logout();
            }
        }
        // convert the tree result to a flat arraylist and remove the first node (root node)
        convertToArrayListFolders(resultTree);
        resultArrayListFolders.remove(0);

        // add the parent folder
        resultArrayListFolders.add(0, folder);
        return resultArrayListFolders;
    }

    @SuppressWarnings({"unchecked"})
    public List<Plan> findAllPlans(ModuleType moduleType, boolean withOnlineFolders) throws JackrabbitException {
        Collection<Plan> result;
        List<Plan> resultFiltered = new ArrayList<Plan>();

        ObjectContentManager ocm = null;

        try {
            ocm = getObjectManager(connect());
            org.apache.jackrabbit.ocm.query.QueryManager queryManager = ocm.getQueryManager();
            Filter filter = queryManager.createFilter(Plan.class);
            filter.addEqualTo("moduleType", moduleType.toString());
            org.apache.jackrabbit.ocm.query.Query query = queryManager.createQuery(filter);
            result = ocm.getObjects(query);

            if (withOnlineFolders) {
                for (Plan pei : result) {

                    if (pei.getOnline() != null) {
                        pei.getOnline().size();
                    }

                    /*if (pei.getOnline() != null && pei.getOnline().size() > 0) {
                        resultFiltered.add(pei);
                    }
                    */
                    resultFiltered.add(pei);
                }
            } else {
                resultFiltered = new ArrayList<Plan>(result);
            }

        } catch (RepositoryException e) {
            throw new JackrabbitException("Error connecting to the content repository", e);
        } finally {
            if (ocm != null) {
                ocm.logout();
            }
        }

        return resultFiltered;
    }

    public void moveToPath(String oldPath, String newPath) throws JackrabbitException {
        ObjectContentManager ocm = null;
        try {
            Session s = connect();
            ocm = getObjectManager(s);

            if (ocm.getObject(newPath) != null) {
                throw new JackrabbitException("Destiny Path allready exists");
            }

            ocm.move(oldPath, newPath);
            ocm.save();

        } catch (RepositoryException e) {
            throw new JackrabbitException("Error inserting in the content repository", e);
        } finally {
            if (ocm != null) {
                ocm.logout();
            }
        }
    }

    public void deleteOnlineFolder(long contractId, ModuleType moduleType) throws JackrabbitException {
        try {
            delete("/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + moduleType + contractId + "/online");
            delete("/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + moduleType + contractId + "/installationPhotoOnline");
            delete("/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + moduleType + contractId + "/companyLogoOnline");

        } catch (JackrabbitException e) {
            throw new JackrabbitException("Error deleteOnlineFolder", e);
        }
    }

    @SuppressWarnings({"ConstantConditions", "EmptyCatchBlock"})
    public void copyOfflineToOnline(String path, ModuleType moduleType) throws JackrabbitException, BusinessException {

        LOGGER.info("PlanDAOEJB.copyOfflineToOnline start method");
        Session session = null;
        try {
            session = connect();
            Workspace workspace = session.getWorkspace();
            LOGGER.info("PlanDAOEJB.copyOfflineToOnline session.getWorkspace()");

            // copy pei resources to online

            if (PlanUtils.calculateDepth(path) == 0) {
                long contractId = PlanUtils.getContractNumberByPath(path);
                LOGGER.info("PlanDAOEJB.copyOfflineToOnline PlanUtils.getContractNumberByPath(path): " + path);
                try {
                    session.getItem(
                            "/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + moduleType + contractId + "/installationPhoto");
                    workspace.copy(
                            "/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + moduleType + contractId + "/installationPhoto",
                            "/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + moduleType + contractId
                                    + "/installationPhotoOnline");
                } catch (PathNotFoundException e) {
                    LOGGER.error("PlanDAOEJB.copyOfflineToOnline PathNotFoundException: ", e);
                    // ignore this exception
                }

                try {
                    session.getItem("/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + moduleType + contractId + "/companyLogo");
                    workspace.copy("/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + moduleType + contractId + "/companyLogo",
                            "/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + moduleType + contractId + "/companyLogoOnline");
                } catch (PathNotFoundException e) {
                    LOGGER.error("PlanDAOEJB.copyOfflineToOnline PathNotFoundException: ", e);
                    // ignore this exception
                }
                workspace.copy("/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + moduleType + contractId + "/offline",
                        "/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + moduleType + contractId + "/online");
            } else {
                workspace.copy(path, path.replaceAll("offline", "online"));
            }

        } catch (RepositoryException e) {
            LOGGER.error("PlanDAOEJB.copyOfflineToOnline RepositoryException: ", e);
            throw new JackrabbitException("Error copyOfflineToOnline", e);
        }
        finally {
            if (session != null) {
                session.logout();
            }
        }
    }

    public boolean isPermissionInActivePEI(Long permissionId, long contractId, ModuleType moduleType)
            throws JackrabbitException {
        isPermissionInActivePlan = false;
        Plan plan;
        ObjectContentManager ocm = null;

        try {
            // get the pei
            ocm = getObjectManager(connect());
            plan = (Plan) ocm.getObject("/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + moduleType + contractId);

            if (plan == null) {
                return false;
            }

            // check online folder
            if (plan.getOnline() != null) {
                plan.getOnline().size();

                for (Folder folder : plan.getOnline()) {
                    isPermissionInActivePEI(folder, permissionId);
                }
            }

            // check offline folder
            if (plan.getOffline() != null) {
                plan.getOffline().size();

                for (Folder folder : plan.getOffline()) {
                    isPermissionInActivePEI(folder, permissionId);
                }
            }
        }
        catch (RepositoryException e) {
            throw new JackrabbitException("Error connecting to the content repository", e);
        }
        finally {
            if (ocm != null) {
                ocm.logout();
            }
        }

        return isPermissionInActivePlan;
    }

    public Integer countAllRiskAnalysis(String path, RiskAnalysisElement riskAnalysisElementToFilter)
            throws JackrabbitException {
        Boolean criteria = false;
        StringBuilder sb = new StringBuilder();
        Session session = null;
        //Escape path
        path = ISO9075.encodePath(path);

        try {
            session = connect();
            javax.jcr.query.QueryManager queryManager = session.getWorkspace().getQueryManager();

            sb.append("/jcr:root");
            sb.append(path);
            sb.append("/*");
            if (riskAnalysisElementToFilter != null) {
                if (riskAnalysisElementToFilter.getProduct() != null) {
                    sb.append("[@product='")
                            .append(riskAnalysisElementToFilter.getProduct())
                            .append("' ");
                    criteria = true;
                }
                if (riskAnalysisElementToFilter.getReleaseConditions() != null) {
                    if (criteria) {
                        sb.append(" and ");
                    } else {
                        sb.append("[");
                        criteria = true;
                    }
                    sb.append("@releaseConditions='")
                            .append(riskAnalysisElementToFilter.getReleaseConditions())
                            .append("' ");
                }
                if (riskAnalysisElementToFilter.getWeather() != null) {
                    if (criteria) {
                        sb.append(" and ");
                    } else {
                        sb.append(" [ ");
                        criteria = true;
                    }
                    sb.append("@weather='")
                            .append(riskAnalysisElementToFilter.getWeather())
                            .append("' ");
                }
                if (criteria) {
                    sb.append("]");
                }
            }
            javax.jcr.query.Query q = queryManager.createQuery(sb.toString(), javax.jcr.query.Query.XPATH);
            QueryResult result = q.execute();

            int finalResult = 0;
            for (NodeIterator ni = result.getNodes(); ni.hasNext();) {
                finalResult++;
                ni.nextNode();
            }
            return finalResult;
        }
        catch (RepositoryException e) {
            throw new JackrabbitException("Error connecting to the content repository", e);
        }
        finally {
            if (session != null) {
                session.logout();
            }
        }
    }

    public List<RiskAnalysisElement> findRiskAnalysis(String path, int limit, int offset, String sortCriteria,
                                                      String sortDirection,
                                                      RiskAnalysisElement riskAnalysisElementToFilter,
                                                      Boolean allResults)
            throws JackrabbitException {

        List<RiskAnalysisElement> results = new ArrayList<RiskAnalysisElement>();
        Boolean criteria = false;
        StringBuilder sb = new StringBuilder();
        Session session = null;
        //Escape path
        path = ISO9075.encodePath(path);

        try {
            session = connect();
            javax.jcr.query.QueryManager queryManager = session.getWorkspace().getQueryManager();

            sb.append("/jcr:root");
            sb.append(path);
            sb.append("/*");
            if (riskAnalysisElementToFilter != null) {
                if (riskAnalysisElementToFilter.getProduct() != null) {
                    sb.append("[@product='")
                            .append(riskAnalysisElementToFilter.getProduct())
                            .append("' ");
                    criteria = true;
                }
                if (riskAnalysisElementToFilter.getReleaseConditions() != null) {
                    if (criteria) {
                        sb.append(" and ");
                    } else {
                        sb.append("[");
                        criteria = true;
                    }
                    sb.append("@releaseConditions='")
                            .append(riskAnalysisElementToFilter.getReleaseConditions())
                            .append("' ");
                }
                if (riskAnalysisElementToFilter.getWeather() != null) {
                    if (criteria) {
                        sb.append(" and ");
                    } else {
                        sb.append("[");
                        criteria = true;
                    }
                    sb.append("@weather='")
                            .append(riskAnalysisElementToFilter.getWeather()).append("' ");
                }
                if (criteria) {
                    sb.append("]");
                }
            }

            if (sortCriteria != null) {
                sb.append(" order by @");
                sb.append(sortCriteria);
            }

            if (sortDirection != null) {
                sb.append(" ");
                sb.append(sortDirection);
            }

            javax.jcr.query.Query q = queryManager.createQuery(sb.toString(), javax.jcr.query.Query.XPATH);

            if (allResults == null || !allResults) {
                ((QueryImpl) q).setLimit(limit);
                ((QueryImpl) q).setOffset(offset);
            }
            QueryResult result = q.execute();


            for (NodeIterator ni = result.getNodes(); ni.hasNext();) {
                Node currentNode = ni.nextNode();
                RiskAnalysisElement rAE = new RiskAnalysisElement();
                rAE.setProduct(currentNode.getProperty("product").getString());
                rAE.setReleaseConditions(currentNode.getProperty("releaseConditions").getString());
                rAE.setWeather(currentNode.getProperty("weather").getString());
                rAE.setIgnitionPoint(currentNode.getProperty("ignitionPoint").getString());
                rAE.setRadiation(currentNode.getProperty("radiation").getString());
                rAE.setPressurized(currentNode.getProperty("pressurized").getString());
                rAE.setToxicity(currentNode.getProperty("toxicity").getString());
                rAE.setFileFolderLinks(currentNode.getProperty("fileFolderLinks").getString());
                results.add(rAE);
            }
        }
        catch (RepositoryException e) {
            throw new JackrabbitException("Error connecting to the content repository", e);
        }
        finally {
            if (session != null) {
                session.logout();
            }
        }
        return results;
    }

    public void copyPlan(Long contractIdSource, Long contractIdTarget, ModuleType moduleType)
            throws JackrabbitException {
        Session session = null;
        try {
            session = connect();
            Workspace workspace = session.getWorkspace();
            workspace.copy("/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + moduleType + contractIdSource,
                    "/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + moduleType + contractIdTarget);

        } catch (RepositoryException e) {
            throw new JackrabbitException("Error copyPEI", e);
        }
        finally {
            if (session != null) {
                session.logout();
            }
        }

    }

    public void updateFolderPublishInfo(List<Folder> folders, String publishedAuthor, Date publishedDate)
            throws JackrabbitException {

        ObjectContentManager ocm = null;
        try {
            ocm = getObjectManager(connect());
            for (Folder folder : folders) {
                Folder loadedFolder = (Folder) ocm.getObject(folder.getPath());
                loadedFolder.getFolders().size();
                updateFolderPublishInfoWorker(loadedFolder, publishedAuthor, publishedDate);
            }
        } catch (RepositoryException e) {
            throw new JackrabbitException("Error connecting to the content repository", e);
        } finally {
            if (ocm != null) {
                ocm.logout();
            }
        }
    }

    public void updatePlansFolderReferencePath(Collection<Contract> contracts, String contractDesignation,
                                               String companyName) throws JackrabbitException {
        Plan plan;
        ObjectContentManager ocm = null;
        try {
            ocm = getObjectManager(connect());
            for (Contract contract : contracts) {
                plan = (Plan) ocm.getObject(
                        "/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + contract.getModule().getModuleType() + contract
                                .getId());
                if (plan != null) {
                    updateChildrenReferencePath(plan.getOffline(), contractDesignation, companyName);
                }

            }
        } catch (RepositoryException e) {
            throw new JackrabbitException("Error connecting to the content repository", e);
        } finally {
            if (ocm != null) {
                ocm.logout();
            }
        }
    }

    public boolean isPlanWithFolderReferences(long contractId, ModuleType moduleType) throws JackrabbitException {

        isPlanWithFolderReferences = false;
        Plan plan;
        ObjectContentManager ocm = null;

        try {
            // get the pei
            ocm = getObjectManager(connect());
            plan = (Plan) ocm.getObject("/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + moduleType + contractId);

            if (plan == null) {
                return true;
            }
            //check offline folder
            if (plan.getOffline() != null) {
                plan.getOffline().size();
                checkPlanForFolderReferencesWorker(plan.getOffline());
            }
            if (isPlanWithFolderReferences) {
                return isPlanWithFolderReferences;
            } else {
                //check online folder
                if (plan.getOnline() != null) {
                    plan.getOnline().size();
                    checkPlanForFolderReferencesWorker(plan.getOnline());
                    return isPlanWithFolderReferences;
                }
            }
        }
        catch (RepositoryException e) {
            throw new JackrabbitException("Error connecting to the content repository", e);
        }
        finally {
            if (ocm != null) {
                ocm.logout();
            }
        }
        return true;
    }

    public boolean isPlanWithFolderTemplate11Mirror(long contractId, ModuleType moduleType) throws JackrabbitException {

        isPlanWithFolderTemplate11Mirror = false;
        Plan plan;
        ObjectContentManager ocm = null;

        try {
            // get the pei
            ocm = getObjectManager(connect());
            plan = (Plan) ocm.getObject("/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + moduleType + contractId);

            if (plan == null) {
                return true;
            }
            //check offline folder
            if (plan.getOffline() != null) {
                plan.getOffline().size();
                checkPlanForFolderTempalte11MirrorWorker(plan.getOffline());
            }
            if (isPlanWithFolderTemplate11Mirror) {
                return isPlanWithFolderTemplate11Mirror;
            } else {
                //check online folder
                if (plan.getOnline() != null) {
                    plan.getOnline().size();
                    checkPlanForFolderTempalte11MirrorWorker(plan.getOnline());
                    return isPlanWithFolderTemplate11Mirror;
                }
            }
        }
        catch (RepositoryException e) {
            throw new JackrabbitException("Error connecting to the content repository", e);
        }
        finally {
            if (ocm != null) {
                ocm.logout();
            }
        }
        return true;
    }

    public void deleteSourceFoldersReferences(long contractId, ModuleType moduleType)
            throws JackrabbitException, BusinessException {
        Plan pei;
        ObjectContentManager ocm = null;
        try {
            ocm = getObjectManager(connect());
            pei = (Plan) ocm.getObject("/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + moduleType + contractId);

            if (pei != null) {
                if (pei.getOffline() != null) {
                    pei.getOffline().size();
                    deleteSourceFoldersReferencesWorker(pei.getOffline());
                }
            }
        } catch (RepositoryException e) {
            throw new JackrabbitException("Error connecting to the content repository", e);
        } finally {
            if (ocm != null) {
                ocm.logout();
            }
        }
    }

    /**
     * {@inheritDoc}
     * @param srcFolder {@inheritDoc}
     * @param dstFolder {@inheritDoc}
     * @throws JackrabbitException {@inheritDoc}
     */
    public void copyFolder(String srcFolder, String dstFolder) throws JackrabbitException {
        Session session = null;
        try {
            session = connect();
            Workspace workspace = session.getWorkspace();
            workspace.copy(srcFolder,dstFolder);
        } catch (RepositoryException e) {
            throw new JackrabbitException("Error copyPEI", e);
        } finally {
            if (session != null) {
                session.logout();
            }
        }

    }

    public void importPlan(InputStream planFileInputStream, String relPathImport, String absPathImport,
                           String pathImportTarget) throws NamingException, RepositoryException, IOException {
        Session session = connect();

        // The import process is a bit tricky: first we import the plan to a random path, then we move it to the target
        // plan path
        Node plansRootNode = session.getRootNode().getNode(PlanUtils.ROOT_PLAN_FOLDER);

        // we create the random node to harbour the imported plan
        Node tempImportPlanRandomNode = plansRootNode.addNode(relPathImport);

        // then we import it, difference in paths is just from relPath to abs path
        LOGGER.info("Import plan to " + absPathImport);
        session.importXML(absPathImport, planFileInputStream, ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);
        Node tempImportPlanRootContentNode = tempImportPlanRandomNode.getNodes().nextNode();

        // remove old plan
        Item item = session.getItem(pathImportTarget);
        LOGGER.info("Removing: " + pathImportTarget);
        item.remove();

        // move imported plan to the old place
        session.save();
        LOGGER.info("Moving folder " + tempImportPlanRootContentNode.getPath() + " to " + pathImportTarget);
        session.getWorkspace().move(tempImportPlanRootContentNode.getPath(), pathImportTarget);

        // remove imported plan empty node
        tempImportPlanRandomNode.remove();

        // save all
        session.save();
    }

    /* ***************Private Methods ************************ */

    private void findChildrenFolderWithoutTemplate11Mirror(Folder folder, TreeNode parent,
                                                           int subtractFromDepth)
            throws JackrabbitException, BusinessException {

        // determines what CSS to apply to this folder
        String cssToApply = "";
        if (folder.getFolderMirrorReferences() != null && !folder.getFolderMirrorReferences().isEmpty()) {
            cssToApply = "mirrorReferences";
        } else if (folder.getTemplate() != null && folder.getTemplate().getName()
                .equals(Template.Type.TEMPLATE_MIRROR.getName())) {

            Template11Mirror template11Mirror = (Template11Mirror) folder.getTemplate();
            if (template11Mirror.getSourceContractId().equals(PlanUtils.getContractNumberByPath(folder.getPath()))) {
                cssToApply = "template11Mirror-same-contract";
            } else {
                cssToApply = "template11Mirror-different-contract";
            }
        }
        TreeNode currentTreeNode =
                new TreeNode(folder.getName(), folder.getPath(),
                        PlanUtils.calculateDepth(folder.getPath()) - subtractFromDepth,
                        folder.getOrder(),
                        folder.getPermissions(), cssToApply, folder.getTemplate().getName());

        // if folder isn't active or is not navigable, don't add it or its children
        if (folder.getTemplate().getName().equals(Template.Type.TEMPLATE_MIRROR.getName())) {
            return;
        }

        List<Folder> childrenFolders = findFoldersLoaded(folder.getPath(), true);
        if (childrenFolders != null && !childrenFolders.isEmpty()) {
            for (Folder childrenFolder : childrenFolders) {
                if (childrenFolder.getTemplate().getName().equals(Template.Type.TEMPLATE_MIRROR.getName())) {
                    return;
                }
            }
        }
        parent.addChildren(currentTreeNode);
        if (folder.getFolders().size() > 0) {
            for (Folder subfolder : folder.getFolders()) {
                findChildrenFolderWithoutTemplate11Mirror(subfolder, currentTreeNode, subtractFromDepth);
            }
        }
    }

    /**
     * Delete folder reference
     *
     * @param sourceFolder - source Folder
     * @param targetPath   - target Path
     * @throws JackrabbitException - error in repository
     * @throws BusinessException   - error
     */
    private void deleteFolderReference(Folder sourceFolder, String targetPath)
            throws JackrabbitException, BusinessException {
        List<FolderMirrorReference> newFolderMirrorReferences;
        Template template;
        boolean toUpdate = false;
        if (sourceFolder != null && sourceFolder.getFolderMirrorReferences() != null && !sourceFolder
                .getFolderMirrorReferences().isEmpty()) {
            newFolderMirrorReferences = new ArrayList<FolderMirrorReference>();
            template = sourceFolder.getTemplate();
            int index = 0;
            for (FolderMirrorReference folderMirrorReference : sourceFolder.getFolderMirrorReferences()) {
                if (!PlanUtils.getContractNumberByPath(folderMirrorReference.getReferencePath())
                        .equals(PlanUtils.getContractNumberByPath(targetPath))) {
                    FolderMirrorReference newFolderMirrorReference = new FolderMirrorReference(index,
                            folderMirrorReference.getReferenceContractDesignation(),
                            folderMirrorReference.getReferenceCompanyName(), folderMirrorReference.getReferencePath());
                    newFolderMirrorReferences.add(newFolderMirrorReference);
                    index++;
                } else {
                    toUpdate = true;
                }
            }
            if (toUpdate) {
                sourceFolder.setTemplate(null);
                sourceFolder.setFolderMirrorReferences(null);
                update(sourceFolder);
                sourceFolder.setTemplate(template);
                sourceFolder.setFolderMirrorReferences(newFolderMirrorReferences);
                update(sourceFolder);
            }
        }
    }

    private void deleteSourceFoldersReferencesWorker(List<Folder> folders)
            throws JackrabbitException, BusinessException {
        if (folders != null) {
            for (Folder folder : folders) {
                if (folder.getTemplate().getName().equals(Template.Type.TEMPLATE_MIRROR.getName())) {
                    Template11Mirror template11Mirror = (Template11Mirror) folder.getTemplate();
                    Folder sourceFolder = findFolder(template11Mirror.getSourcePath(), false);
                    deleteFolderReference(sourceFolder, folder.getPath());
                    if (sourceFolder != null && sourceFolder.getPublishedRelatedPath() != null) {
                        Folder onlineFolder = findFolder(sourceFolder.getPublishedRelatedPath(), false);
                        deleteFolderReference(onlineFolder, folder.getPath());
                    }
                }
                deleteSourceFoldersReferencesWorker(folder.getFolders());
            }
        }
    }

    private void checkPlanForFolderTempalte11MirrorWorker(List<Folder> folders) {
        for (Folder folder : folders) {
            if (folder.getTemplate() != null && folder.getTemplate().getName()
                    .equals(Template.Type.TEMPLATE_MIRROR.getName())) {
                isPlanWithFolderTemplate11Mirror = true;
                return;
            }
            checkPlanForFolderTempalte11MirrorWorker(folder.getFolders());
        }
    }

    private void checkPlanForFolderReferencesWorker(List<Folder> folders) {
        for (Folder folder : folders) {
            if (folder.getFolderMirrorReferences() != null && !folder.getFolderMirrorReferences().isEmpty()) {
                isPlanWithFolderReferences = true;
                return;
            }
            checkPlanForFolderReferencesWorker(folder.getFolders());
        }
    }

    private void updateChildrenReferencePath(List<Folder> folders, String contractDesignation, String companyName)
            throws JackrabbitException {
        for (Folder folder : folders) {
            if (folder.getFolderMirrorReferences() != null) {
                for (FolderMirrorReference folderMirrorReference : folder.getFolderMirrorReferences()) {
                    if (companyName != null) {
                        folderMirrorReference.setReferenceCompanyName(companyName);
                    }
                    if (contractDesignation != null) {
                        folderMirrorReference.setReferenceContractDesignation(contractDesignation);
                    }
                    updateObject(folderMirrorReference);
                }
            }
            updateChildrenReferencePath(folder.getFolders(), contractDesignation, companyName);
        }
    }

    private void updateFolderPublishInfoWorker(Folder folder, String publishedAuthor, Date publishedDate)
            throws JackrabbitException {
        folder.setPublishedDate(publishedDate);
        folder.setPublishedAuthor(publishedAuthor);
        folder.setPublishedRelatedPath(StringUtils.replaceOnce(folder.getPath(), "offline", "online"));
        LOGGER.debug(folder.getName());
        update(folder);

        if (folder.getFolders() != null) {
            for (Folder fRec : folder.getFolders()) {
                updateFolderPublishInfoWorker(fRec, publishedAuthor, publishedDate);
            }
        }
    }

    /**
     * Recursive method that traverses a PEI and checks if the specified permission is used in any of the PEI folders.
     * Holds the result in the boolean field isPermissionInActivePEI
     *
     * @param folder       current folder to check
     * @param permissionId id of the permission to search
     */
    private void isPermissionInActivePEI(Folder folder, Long permissionId) {
        if (isPermissionInActivePlan) {
            return;
        }

        if (isPermissionInFolder(folder, permissionId)) {
            isPermissionInActivePlan = true;
        }
        if (folder.getFolders().size() != 0) {
            for (Folder subfolder : folder.getFolders()) {
                isPermissionInActivePEI(subfolder, permissionId);
            }
        }
    }

    /**
     * Checks if a folder contains the specified permission
     *
     * @param folder       folder to analyze
     * @param permissionId id of the permission
     * @return true if folder contains the permission with the specified id
     */
    private boolean isPermissionInFolder(Folder folder, Long permissionId) {
        for (Permission permission : folder.getPermissions()) {
            if (permission.getPermissionId().equals(permissionId)) {
                return true;
            }
        }

        return false;
    }

    private ObjectContentManager getObjectManager(Session session) throws RepositoryException {
        checkNodes(session);
        return new ObjectContentManagerImpl(session, mapper);
    }

    private Session connect() throws RepositoryException {
        return RepositoryUtil.login(repository, "superuser", "");
    }

    private void checkNodes(Session session) throws RepositoryException {
        try {
            session.getRootNode().getNode(PlanUtils.ROOT_PLAN_FOLDER);
        } catch (PathNotFoundException e) {
            session.getRootNode().addNode(PlanUtils.ROOT_PLAN_FOLDER, "nt:unstructured");
        }
    }

    private void updateObject(Object jcrObject) throws JackrabbitException {
        ObjectContentManager ocm = null;
        try {
            ocm = getObjectManager(connect());
            ocm.update(jcrObject);
            ocm.save();
        } catch (RepositoryException e) {
            throw new JackrabbitException("Error updating the content repository", e);
        } finally {
            if (ocm != null) {
                ocm.logout();
            }
        }
    }


    private void insertObject(Object o) throws JackrabbitException {
        ObjectContentManager ocm = null;
        try {
            Session s = connect();
            ocm = getObjectManager(s);
            if (ocm.getObject(((HierarchyNode) o).getPath()) != null) {
                throw new JackrabbitException("Node with same path allready exists");
            }
            ocm.insert(o);
            ocm.save();

        } catch (RepositoryException e) {
            throw new JackrabbitException("Error inserting in the content repository", e);
        } finally {
            if (ocm != null) {
                ocm.logout();
            }
        }
    }

    /**
     * Recursively traverses the folders and their children, building a tree saved in the resulTree TreeNode
     *
     * @param folder                     current folder
     * @param parent                     parent of the current folder
     * @param filterResults              if results should be filtered by isNavigable
     * @param filterResultsByIncludeMenu if results should be filtered by the include menu option
     * @param subtractFromDepth          in the tree some folders are not included (because of the includeInMenu
     *                                   property). The children of these folders need to have their depth corrected,
     *                                   this int represents the compensation needed. Default should be 0.
     * @param saveFolder                 if true, we keep the folder in the treeNode
     * @throws com.criticalsoftware.certitools.business.exception.BusinessException
     *          - error
     */
    private void findChildrenfolder(Folder folder, TreeNode parent, boolean filterResults,
                                    boolean filterResultsByIncludeMenu, int subtractFromDepth, boolean saveFolder)
            throws BusinessException {

        // determines what CSS to apply to this folder
        String cssToApply = "";
        if (folder.getFolderMirrorReferences() != null && !folder.getFolderMirrorReferences().isEmpty()) {
            cssToApply = "mirrorReferences";
        } else if (folder.getTemplate() != null && folder.getTemplate().getName()
                .equals(Template.Type.TEMPLATE_MIRROR.getName())) {
            Template11Mirror template11Mirror = (Template11Mirror) folder.getTemplate();
            if (template11Mirror.getSourceContractId().equals(PlanUtils.getContractNumberByPath(folder.getPath()))) {
                cssToApply = "template11Mirror-same-contract";
            } else {
                cssToApply = "template11Mirror-different-contract";
            }
        }
        TreeNode currentTreeNode =
                new TreeNode(folder.getName(), folder.getPath(),
                        PlanUtils.calculateDepth(folder.getPath()) - subtractFromDepth,
                        folder.getOrder(),
                        folder.getPermissions(), cssToApply, folder.getTemplate().getName());

        // If we need to save the folder, add it to the tree node
        if (saveFolder) {
            folder.setDepth(
                    currentTreeNode.getDepth()); // TODO check if this is safe or we need to clone to another object and change it
            currentTreeNode.setFolder(folder);
        }

        // if folder isn't active or is not navigable, don't add it or its children
        if (filterResults) {
            if (!folder.getActive() || !folder.isNavigable()) {
                return;
            }

            if (!folder.isNavigableChildren()) {
                parent.addChildren(currentTreeNode);
                return;
            }
        }

        // check if folder is to be included in navigation menu or not
        if (filterResultsByIncludeMenu && !folder.getIncludeInMenuOrIsNavigable()) {
            subtractFromDepth++;
            currentTreeNode = parent;
        } else {
            parent.addChildren(currentTreeNode);
        }

        if (folder.getFolders().size() > 0) {
            for (Folder subfolder : folder.getFolders()) {
                findChildrenfolder(subfolder, currentTreeNode, filterResults, filterResultsByIncludeMenu,
                        subtractFromDepth, saveFolder);
            }
        }
    }

    private ArrayList<TreeNode> resultArrayList;

    /**
     * Converts the Tree saved in the TreeNode to a flat ArrayList with the elements ordered by their parent Uses the
     * field resultArrayList
     *
     * @param node TreeNode containing the tree to convert
     */
    private void convertToArrayList(TreeNode node) {
        if (node.getChildren() == null || node.getChildren().size() == 0) {
            resultArrayList.add(node);
        } else {
            resultArrayList.add(node);
            for (TreeNode o : node.getChildren()) {
                convertToArrayList(o);
            }
        }
    }

    private ArrayList<Folder> resultArrayListFolders;

    /**
     * Converts the Tree saved in the TreeNode to a flat ArrayList with the elements ordered by their parent Uses the
     * field resultArrayListFolders. Similar to convertToArrayList but returns the results in a folder list
     *
     * @param node TreeNode containing the tree to convert
     */
    private void convertToArrayListFolders(TreeNode node) {
        if (node.getChildren() == null || node.getChildren().size() == 0) {
            resultArrayListFolders.add(node.getFolder());
        } else {
            resultArrayListFolders.add(node.getFolder());
            for (TreeNode o : node.getChildren()) {
                convertToArrayListFolders(o);
            }
        }
    }

    private void converToFolderArrayList(Folder folder, List<Folder> list, boolean isFirstElement, int toSubtract,
                                         boolean filterActive) {

        if (isFirstElement) {
            toSubtract = PlanUtils.calculateDepth(folder.getPath());
        }

        folder.setDepth(PlanUtils.calculateDepth(folder.getPath()) - toSubtract);
        if ((filterActive && folder.getActive()) || !filterActive) {
            list.add(folder);
        }
        if (folder.getFolders() != null) {
            //Sort Folders
            List<Folder> foldersTemp = folder.getFolders();
            Collections.sort(foldersTemp);
            folder.setFolders(foldersTemp);
            for (Folder f : folder.getFolders()) {
                converToFolderArrayList(f, list, false, toSubtract, filterActive);
            }
        }
    }

    private void fetchResources(List<Folder> folders, List<Resource> resources, boolean isImage) {
        for (Folder f : folders) {
            if (f.getTemplate() != null && f.getTemplate().getName()
                    .equals(Template.Type.TEMPLATE_RESOURCE.getName())) {
                List<String> s = Arrays.asList(PlanUtils.IMAGE_MEDIA_TYPES);
                if (!isImage || s.contains(((TemplateResource) f.getTemplate()).getResource().getMimeType())) {
                    if (f.getActive()) {
                        resources.add(((TemplateResource) f.getTemplate()).getResource());
                    }
                }
            }
            fetchResources(f.getFolders(), resources, isImage);
        }
    }

    private void loadChildrens(List<Folder> folders) throws JackrabbitException {
        for (Folder folder : folders) {
            loadChildrens(folder.getFolders());
        }
    }

    private void findChildrenFoldersForPermissions(Folder folder, TreeNode parent) {
        TreeNode currentTreeNode =
                new TreeNode(folder.getName(), folder.getPath(), PlanUtils.calculateDepth(folder.getPath()),
                        folder.getOrder(),
                        folder.getPermissions());

        Set<Permission> permissions = new HashSet<Permission>();
        if (parent.getPermissions() != null && !parent.getPermissions().isEmpty()) {
            permissions.addAll(parent.getPermissions());
        }

        if (currentTreeNode.getPermissions() != null && !currentTreeNode.getPermissions().isEmpty()) {
            permissions.addAll(currentTreeNode.getPermissions());
        }
        currentTreeNode.setPermissions(permissions);
        parent.addChildren(currentTreeNode);
        if (folder.getFolders().size() != 0) {
            for (Folder subfolder : folder.getFolders()) {
                findChildrenFoldersForPermissions(subfolder, currentTreeNode);
            }
        }
    }
}

