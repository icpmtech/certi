/*
 * $Id: RepositoryDAO.java,v 1.24 2010/05/26 16:55:29 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2010/05/26 16:55:29 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.persistence.certitools;

import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.criticalsoftware.certitools.util.File;

import javax.jcr.RepositoryException;
import java.util.List;

import org.apache.jackrabbit.core.config.ConfigurationException;

public interface RepositoryDAO {

    Long NEWSLETTER_LOGO_ID = 1L;

    /**
     * Repository foders names
     */
    enum Folder {
        ROOT_FOLDER("certitools_root"),
        LEGISLATION_FOLDER("legislation"),
        NEWSLETTER_FOLDER("newsletter"),
        CONTRACT_FOLDER("contract"),
        TEMPLATE_DOCX_FOLDER("templatedocx");

        private String name;

        Folder(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

    /**
     * Insert a file on a folder
     * @param folder the folder
     * @param file the file
     * @throws JackrabbitException when something goes wrong
     */
    void insertFileOnFolder(Folder folder, File file) throws JackrabbitException;

    /**
     * Remove a file from a folder
     * @param folder the folder
     * @param fileId the file id
     * @throws JackrabbitException when something goes wrong
     */
    void removeFileOnFolder(Folder folder, Long fileId) throws JackrabbitException;

    /**
     * Finds a a file inside a folder
     * @param folder the folder where to find
     * @param fileId the file id
     * @return file object
     * @throws JackrabbitException when something goes wrong
     */
    File findFileOnFolder(Folder folder, Long fileId) throws JackrabbitException;

    /**
     * Register application specific node types
     * @throws JackrabbitException the exception
     */
    void registerNodeTypes() throws JackrabbitException;

    /**
     * Search for legal documents
     * @param limit to
     * @param offset the number of results per page
     * @param searchPhrase some search phrase
     * @param onlyPublish only publish documents
     * @return List of legal document is to be fetched on database
     * @throws JackrabbitException when something wrong with repository happens
     */
    List<Long> searchLegalDocument(int limit, int offset, String searchPhrase, boolean onlyPublish) throws JackrabbitException;

    /**
     * Count the number of results
     * @param searchPhrase the serach term
     * @param onlyPublish if queries takes in care is document is publish
     * @return number of results
     * @throws JackrabbitException when sopmething goes wrong
     */
    int countLegalDocument(String searchPhrase, boolean onlyPublish) throws JackrabbitException;

    /**
     * ShutDowns properly the repository
     * @throws JackrabbitException the exception
     */
    void shutdown() throws JackrabbitException ;

    /**
     * Update a file in repository
     * @param folder the folder
     * @param file the file
     * @throws JackrabbitException some exception
     */
    void updateFileOnFolder(Folder folder, File file) throws JackrabbitException ;

    /**
     * Update full DR title of legal documents
     * @throws JackrabbitException a repository exception
     */
    public void updateFullDrTitleField() throws JackrabbitException;

    public void updateToVersion3() throws JackrabbitException;
}
