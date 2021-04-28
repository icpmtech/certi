/*
 * $Id: RepositoryDAOEJB.java,v 1.43 2009/10/27 12:41:26 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/10/27 12:41:26 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.persistence.certitools;

import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.criticalsoftware.certitools.util.File;
import com.criticalsoftware.certitools.util.Logger;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.jackrabbit.api.JackrabbitNodeTypeManager;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.query.QueryImpl;
import org.apache.jackrabbit.core.query.lucene.QueryResultImpl;
import org.jboss.annotation.ejb.LocalBinding;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Repository data access object
 *
 * @author : pjfsilva
 */

@Stateless
@Local(RepositoryDAO.class)
@LocalBinding(jndiBinding = "certitools/RepositoryDAO")
@RolesAllowed({"private"})
@TransactionManagement(value = TransactionManagementType.BEAN)
public class RepositoryDAOEJB implements RepositoryDAO {

    Logger log = Logger.getInstance(RepositoryDAOEJB.class);


    @Resource(mappedName = "java:jcr/local")
    private Repository repository;


    public List<Long> searchLegalDocument(int limit, int offset, String searchPhrase, boolean onlyPublish)
            throws JackrabbitException {
        List<Long> results = null;
        Session session = null;
        searchPhrase = prepareStatement(searchPhrase);
        try {
            session = connect();

            QueryManager queryManager = session.getWorkspace().getQueryManager();

            StringBuilder builder = new StringBuilder();

            builder.append("SELECT * FROM certitools:document WHERE (");
            builder.append("CONTAINS(., '" + searchPhrase + "') ");
            builder.append("OR CONTAINS(jcr:fullDrTitle, '" + searchPhrase + "') ");
            builder.append("OR CONTAINS(jcr:customTitle, '" + searchPhrase + "') ");
            builder.append("OR CONTAINS(jcr:keywords, '" + searchPhrase + "') ");
            builder.append("OR CONTAINS(jcr:summary, '" + searchPhrase + "') ");
            builder.append("OR CONTAINS(jcr:customAbstract, '" + searchPhrase + "') )");

            if (onlyPublish) {
                builder.append("AND CONTAINS(jcr:publish, 'true') ");
            }

            builder.append("ORDER BY jcr:score DESC");

            Query q = queryManager.createQuery(builder.toString(), Query.SQL);

            ((QueryImpl) q).setLimit(limit);
            ((QueryImpl) q).setOffset(offset);

            QueryResult result = q.execute();

            if (((QueryResultImpl) result).getTotalSize() > 0) {
                results = new ArrayList<Long>();

                for (NodeIterator ni = result.getNodes(); ni.hasNext();) {
                    results.add(new Long(ni.nextNode().getParent().getName()));
                }
            }
        } catch (RepositoryException e) {
            throw new JackrabbitException("Error connecting to the content repository", e);
        } finally {
            if (session != null) {
                session.logout();
            }
        }

        return results;
    }

    public int countLegalDocument(String searchPhrase, boolean onlyPublish) throws JackrabbitException {
        Session session = null;
        searchPhrase = prepareStatement(searchPhrase);
        try {
            session = connect();

            QueryManager queryManager = session.getWorkspace().getQueryManager();
            StringBuilder builder = new StringBuilder();

            builder.append("SELECT * FROM certitools:document WHERE (");
            builder.append("CONTAINS(., '" + searchPhrase + "') ");
            builder.append("OR CONTAINS(jcr:fullDrTitle, '" + searchPhrase + "') ");
            builder.append("OR CONTAINS(jcr:customTitle, '" + searchPhrase + "') ");
            builder.append("OR CONTAINS(jcr:keywords, '" + searchPhrase + "') ");
            builder.append("OR CONTAINS(jcr:summary, '" + searchPhrase + "') ");
            builder.append("OR CONTAINS(jcr:customAbstract, '" + searchPhrase + "') )");

            if (onlyPublish) {
                builder.append("AND CONTAINS(jcr:publish, 'true') ");
            }

            builder.append("ORDER BY jcr:score DESC");

            Query q = queryManager.createQuery(builder.toString(), Query.SQL);

            return ((QueryResultImpl) q.execute()).getTotalSize();

        } catch (RepositoryException e) {
            throw new JackrabbitException("Error connecting to the content repository", e);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }

    public void insertFileOnFolder(Folder folder, File file) throws JackrabbitException {
        Session session = null;
        try {
            session = connect();
            saveFile(session, folder, file);
            session.save();
        } catch (RepositoryException e) {
            throw new JackrabbitException("Error connecting to the content repository", e);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }

    public File findFileOnFolder(Folder folder, Long fileId) throws JackrabbitException {
        Session session = null;
        try {
            session = connect();
            return findFile(session, folder, fileId);
        } catch (RepositoryException e) {
            throw new JackrabbitException("Error connecting to the content repository", e);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }

    public void registerNodeTypes() throws JackrabbitException {

        Session session = null;

        try {
            session = repository.login(new SimpleCredentials("superuser", "".toCharArray()));

            // Get the JackrabbitNodeTypeManager from the Workspace.
            JackrabbitNodeTypeManager manager =
                    (JackrabbitNodeTypeManager) session.getWorkspace().getNodeTypeManager();
            String[] prefixes = session.getWorkspace().getNamespaceRegistry().getPrefixes();
            boolean register = true;
            for (String s : prefixes) {
                if (s.equals("certitools")) {
                    register = false;
                    break;
                }
            }
            if (register) {
                // Register the custom node types
                manager.registerNodeTypes(
                        this.getClass().getClassLoader().getResourceAsStream("CertitoolsNodeType.cnd"),
                        JackrabbitNodeTypeManager.TEXT_X_JCR_CND);
            }


        } catch (Exception e) {
            throw new JackrabbitException("Error while accessing to repository: ", e);
        } finally {
            if (session != null) {
                session.logout();
            }
        }

    }

    public void removeFileOnFolder(Folder folder, Long fileId) throws JackrabbitException {
        Session session = null;
        try {
            session = connect();
            Node fileNode = findNode(session, folder, fileId);
            fileNode.remove();
            session.save();
        } catch (RepositoryException e) {
            throw new JackrabbitException("Error connecting to the content repository", e);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }

    public void shutdown() throws JackrabbitException {
        try {
            ((RepositoryImpl) connect().getRepository()).shutdown();
        } catch (RepositoryException e) {
            throw new JackrabbitException("Error connecting to the content repository", e);
        }
    }

    public void updateFileOnFolder(Folder folder, File file) throws JackrabbitException {

        if (file.getData() == null || file.getContentType() == null) {
            File f = findFileOnFolder(folder, file.getId());
            file.setContentType(f.getContentType());
            file.setData(f.getData());
        }

        insertFileOnFolder(folder, file);

    }

    /************* Private Methods **************************/

    /**
     * Connects to jackrabbit repository
     *
     * @return session
     *
     * @throws RepositoryException when cannot connect
     */
    private Session connect() throws RepositoryException {
        return repository.login(new SimpleCredentials("superuser", "".toCharArray()));
    }

    /**
     * Finds a file in the repository
     *
     * @param session the repository session
     * @param folder  the folder where to look
     * @param fileId  the file id to retrive
     * @return File object
     *
     * @throws RepositoryException when something went wrong
     */
    private File findFile(Session session, Folder folder, Long fileId) throws RepositoryException {
        Node node = findNode(session, folder, fileId);

        File file = new File();
        file.setId(new Long(node.getName()));

        Node content = node.getNode("jcr:content");

        file.setContentType(content.getProperty("jcr:mimeType").getString());
        file.setLastModified(content.getProperty("jcr:lastModified").getDate().getTime());
        file.setData(content.getProperty("jcr:data").getStream());

        if (Folder.LEGISLATION_FOLDER.equals(folder)) {
            file.setFullDrTitle(content.getProperty("jcr:fullDrTitle").getString());
            file.setCustomTitle(content.getProperty("jcr:customTitle").getString());
            file.setSummary(content.getProperty("jcr:summary").getString());
            file.setPublish(new Boolean(content.getProperty("jcr:publish").getString()));

            //these two propeties are not mandatory
            if (content.hasProperty("jcr:keywords")) {
                file.setKeywords(content.getProperty("jcr:keywords").getString());
            }
            if (content.hasProperty("jcr:customAbstract")) {
                file.setCustomAbstract(content.getProperty("jcr:customAbstract").getString());
            }
        }

        return file;
    }

    /**
     * Saves a file
     *
     * @param session the session
     * @param folder  where to
     * @param file    the file object to be saved
     * @throws RepositoryException when something goes wrong
     */
    private void saveFile(Session session, Folder folder, File file) throws RepositoryException {
        Node fileNode = findNode(session, folder, file.getId());
        Node contentNode;
        try {
            if (Folder.LEGISLATION_FOLDER.equals(folder)) {
                contentNode = fileNode.addNode("jcr:content", "certitools:document");
            } else {
                contentNode = fileNode.addNode("jcr:content", "nt:resource");
            }
        } catch (ItemExistsException e) {
            contentNode = fileNode.getNode("jcr:content");
        }
        contentNode.setProperty("jcr:lastModified", Calendar.getInstance());

        if (file.getContentType() != null) {
            contentNode.setProperty("jcr:mimeType", file.getContentType());
        }
        if (file.getData() != null) {
            contentNode.setProperty("jcr:data", file.getData());
        }

        if (Folder.LEGISLATION_FOLDER.equals(folder)) {
            contentNode.setProperty("jcr:fullDrTitle", file.getFullDrTitle());
            contentNode.setProperty("jcr:customTitle", file.getCustomTitle());
            contentNode.setProperty("jcr:summary", file.getSummary());
            contentNode.setProperty("jcr:publish", file.getPublish().booleanValue());
            if (file.getKeywords() != null) {
                contentNode.setProperty("jcr:keywords", file.getKeywords());
            }
            if (file.getCustomAbstract() != null) {
                contentNode.setProperty("jcr:customAbstract", file.getCustomAbstract());
            }
        }
        contentNode.setProperty("jcr:encoding", "UTF-8");
    }

    /**
     * Find a certain node of a file
     *
     * @param session the repository session
     * @param folder  the folder where to look
     * @param fileId  the file id
     * @return the Node
     *
     * @throws RepositoryException if something went wrong
     */
    private Node findNode(Session session, Folder folder, Long fileId) throws RepositoryException {
        Node rootFolder;
        try {
            rootFolder = session.getRootNode().getNode(Folder.ROOT_FOLDER.getName());
        } catch (PathNotFoundException e) {
            rootFolder = session.getRootNode().addNode(Folder.ROOT_FOLDER.getName(), "nt:folder");
        }
        Node parentFolder;
        try {
            parentFolder = rootFolder.getNode(folder.getName());
        } catch (PathNotFoundException e) {
            parentFolder = rootFolder.addNode(folder.getName(), "nt:folder");
        }
        Node fileNode;
        try {
            fileNode = parentFolder.getNode(String.valueOf(fileId));
        } catch (PathNotFoundException e) {
            fileNode = parentFolder.addNode(String.valueOf(fileId), "nt:file");
        }
        return fileNode;
    }

    private String prepareStatement(String searchTerm) {
        return StringEscapeUtils.escapeSql(searchTerm);
    }

    public void updateFullDrTitleField() throws JackrabbitException {
        Session session = null;
        try {
            session = connect();
            Node rootFolder = session.getRootNode().getNode(Folder.ROOT_FOLDER.getName());
            Node parentFolder = rootFolder.getNode(Folder.LEGISLATION_FOLDER.getName());
            Node fileNode;

            NodeIterator it = parentFolder.getNodes();
            String title;
            Long allNodes = it.getSize();
            Long processed = 0L;

            log.info("Gonna update " + allNodes + " nodes in repository");

            while (it.hasNext()) {
                fileNode = it.nextNode();

                Node contentNode = fileNode.getNode("jcr:content");
                title = contentNode.getProperty("jcr:fullDrTitle").getString();

                log.info("Updating " + title + " ");

                title = title + " " + title.substring(title.indexOf("�") + 2, title.indexOf("/")) + " " +
                        title.substring(title.indexOf("/") + 1, title.indexOf(","));
                contentNode.setProperty("jcr:fullDrTitle", title);

                log.info("Updated to: " + title);
                log.info("This one is done! Still " + (allNodes - (processed++)) + " to finish");
            }

            session.save();

        } catch (Exception e) {
            throw new JackrabbitException("Error connecting to the content repository", e);
        } finally {
            if (session != null) {
                session.logout();
            }
        }

    }

    public void updateToVersion3() throws JackrabbitException {
        Session session = null;
        try {
            session = connect();
            System.out.println("Deleting empty /certitools_plan_root");

            Node rootFolder = null;
            try {
                rootFolder = session.getRootNode().getNode("certitools_plan_root");
                rootFolder.remove();
                session.save();
            } catch (PathNotFoundException e) {
                System.out.println("/certitools_plan_root not found, skipping deleting");
            }

            System.out.println("Deleted");

            System.out.println("Moving certitools_pei_root to certitools_plan_root");
            session.move("/certitools_pei_root", "/certitools_plan_root");
            session.save();
            System.out.println("Moved OK.");
            System.out.println("Moving ocm_classname, peiName and peiNameOnline properties");
            rootFolder = session.getRootNode().getNode("certitools_plan_root");
            NodeIterator nodes = rootFolder.getNodes();
            while (nodes.hasNext()) {
                Node node = nodes.nextNode();
                System.out.println("Processing Node: " + node.getName());
                Property property;
                try {
                    property = node.getProperty("peiName");
                    node.setProperty("planName", property.getString());
                    property.remove();
                } catch (PathNotFoundException e) {
                    System.out.println("peiName not found");
                }
                try {
                    property = node.getProperty("peiNameOnline");
                    node.setProperty("planNameOnline", property.getString());
                    property.remove();
                } catch (PathNotFoundException e) {
                    System.out.println("peiNameOnline not found");
                }

                node.setProperty("ocm_classname", "com.criticalsoftware.certitools.entities.jcr.Plan");

                node.setProperty("moduleType", "PEI");

                System.out.println("Node processed.");
            }
            System.out.println("All properties moved.");
            session.save();

            System.out.println("Update to Version 3 done");
        } catch (Exception e) {
            e.printStackTrace();
            throw new JackrabbitException("Error connecting to the content repository", e);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }

}