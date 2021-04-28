/*
 * $Id: LegislationService.java,v 1.25 2009/09/23 10:53:14 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/09/23 10:53:14 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.business.legislation;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.DocumentNotExistsException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.entities.*;
import com.criticalsoftware.certitools.util.File;
import com.criticalsoftware.certitools.util.PaginatedListWrapper;

import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * Legal document service
 *
 * @author : lt-rico
 */
public interface LegislationService {


    /**
     * Find All Legal Documents sorted by all Categories
     *
     * @return - Legal Document list
     */
    List<LegalDocument> findAllLegalDocumentSortByCategory();

    void fixCategoriesCounter();

    /**
     * Find legal documents by type, number and year for auto complete
     *
     * @param documentTypeId - document type Id
     * @param number         - legal document number
     * @param year           - legal document year
     * @return - legal document list
     */
    LegalDocument findLegalDocumentByTypeNumberAndYear(Long documentTypeId, String number, String year);

    /**
     * Find a legal document fully loaded (with associated legal documents and categories fetched)
     *
     * @param id                           - legal document id
     * @param sortAssociatedLegalDocuments - sort or not associated legal documents
     * @return - legal document
     *
     * @throws DocumentNotExistsException - when the document does not exist
     */
    LegalDocument findLegalDocumentFullyLoadedById(Long id, Boolean sortAssociatedLegalDocuments)
            throws DocumentNotExistsException;


    /**
     * Find legal documents by type, number and year for auto complete
     *
     * @param documentTypeId - document type Id
     * @param number         - legal document number
     * @param year           - legal document year
     * @return - legal document list
     */
    List<LegalDocument> findLegalDocumentsByTypeAutoComplete(Long documentTypeId, String number, String year);

    /**
     * Finds legal documents based on a search string
     *
     * @param paginatedListWrapper the wrapper
     * @param searchLegislation    string to search
     * @return the wrapper with the objects
     *
     * @throws BusinessException a business exception if something goes wrong
     */
    PaginatedListWrapper<LegalDocument> findLegalDocuments(PaginatedListWrapper<LegalDocument> paginatedListWrapper,
                                                           String searchLegislation) throws BusinessException;

    /**
     * Find legal documents by category, to display. if it is admin, returns also not published legal documents
     *
     * @param paginatedListWrapper - wrapper
     * @param categoryId           - category
     * @param isAdmin              - if it is a user with privileges
     * @return - the wrapper with the objects
     *
     * @throws BusinessException - a business exception if something goes wrong
     */
    PaginatedListWrapper<LegalDocument> findLegalDocumentsByCategory(
            PaginatedListWrapper<LegalDocument> paginatedListWrapper, Long categoryId, Boolean isAdmin)
            throws BusinessException;

    /**
     * Find last published legal documents
     *
     * @param history - number of documents (per user there aren t more then 5)
     * @return - List
     */
    List<LegalDocument> findLegalDocumentPublishedHistory(Long history);

    /**
     * Count number of associated legal documents that a document has
     *
     * @param legalDocumentId - legal document to count
     * @return - counter
     */
    Integer findLegalDocumentAssociationsCounter(Long legalDocumentId);

    /** *****Legal Document Category ********************************** */

    /**
     * Find Legal Document Category by id
     *
     * @param legalDocumentCategoryId - id
     * @return - Legal Document Category
     *
     * @throws ObjectNotFoundException - then object not found
     */
    LegalDocumentCategory findLegalDocumentCategoryById(Long legalDocumentCategoryId) throws ObjectNotFoundException;

    /**
     * Find All Legal Document Category children, by depth and parent Id
     *
     * @param id    - parent Id
     * @param depth - depth
     * @return - List of Legal Document Categories
     */
    List<LegalDocumentCategory> findLegalDocumentCategoriesByDepthAndId(Long id, Long depth);

    /**
     * Find Legal Document Category Top parents
     *
     * @return - List
     */
    List<LegalDocumentCategory> findTopParentDocumentCategory();

    /**
     * Build list with all legal documents cateory, for tree in web tier.
     *
     * @param categoryList - list of categories to build tree
     * @return - list in shape of tree
     */
    List<LegalDocumentCategory> findAllLegalDocumentCategoryForTree(List<LegalDocumentCategory> categoryList);

    /** *************Legal Document State ********************************** */

    /**
     * Finds all Legal Documents States
     *
     * @return - List with Documents States
     */
    List<LegalDocumentState> findAllLegalDocumentState();

    /** ****************Legal Document Type ********************************** */

    /**
     * Finds all Legal Documents Types
     *
     * @return - List with Documents Types
     */
    List<LegalDocumentType> findAllLegalDocumentTypes();

    /** **********************************Legal Document CRUD ********************************** */


    /**
     * Insert Legal document in the database. Also insert one file in jackrabbit repository, with the same id of legal
     * document
     *
     * @param legalDocument           - legal document to insert
     * @param legalDocumentCategories - legal document categories
     * @param contentType             - file uploaded contentType
     * @param inputStream             - file data
     * @param fileName                - file name
     * @throws ObjectNotFoundException - when object not found
     * @throws BusinessException-      when trying to insert an already inserted legal document (same number/year)
     */
    void insertLegislation(LegalDocument legalDocument, Set<Long> legalDocumentCategories,
                           String contentType, InputStream inputStream, String fileName) throws
            ObjectNotFoundException, BusinessException;


    /**
     * Update Legal document in the database. Also insert or update file in jackrabbit repository, with the same id of
     * legal
     *
     * @param legalDocument           - legal document to update
     * @param legalDocumentCategories - legal document categories
     * @param contentType             - file uploaded contentType
     * @param inputStream             - file data
     * @param fileName                - file name
     * @param user                    - user that update the record
     * @throws ObjectNotFoundException    - when object not found
     * @throws BusinessException-         when trying to insert an already inserted legal document (same number/year)
     * @throws DocumentNotExistsException - when the document does not exist
     */
    void updateLegislation(LegalDocument legalDocument, Set<Long> legalDocumentCategories,
                           String contentType, InputStream inputStream, String fileName, User user)
            throws ObjectNotFoundException, BusinessException, DocumentNotExistsException;

    /**
     * Delete legal document from database. Remove legal document from history, update legal document category counters
     * and remove associated file from jackrabbit
     *
     * @param documentId - document id to remove
     * @throws ObjectNotFoundException - then document does not exists
     * @throws BusinessException       - when error in jackrabbit
     */
    void deleteLegalDocument(Long documentId) throws ObjectNotFoundException, BusinessException;

    /** ***********************Legal Document CRUD ********************************** */

    /**
     * Find legal Document file in jackrabbit repository
     *
     * @param legalDocumentId - legal document id
     * @return - File
     *
     * @throws BusinessException          - when problem in jackrabbit
     * @throws DocumentNotExistsException - when document does not exist
     * @throws CertitoolsAuthorizationException
     *                                    - when user is not authorized to download file
     */
    File findLegalDocumentFileToDownload(Long legalDocumentId) throws BusinessException, DocumentNotExistsException,
            CertitoolsAuthorizationException;

    /** **********************************Legal Document Statistics ********************************** */

    /**
     * Finds all view statistics
     *
     * @param initDate the init date of the report type
     * @param endDate  the end date of the report type
     * @return a collection of legal document statistics
     */
    List<LegalDocumentStatistics> findVisualizationAndDownloadStatistcs(Date initDate, Date endDate);

    /**
     * Finds all search term statistics
     *
     * @param initDate the init date of the report type
     * @param endDate  the end date of the report type
     * @return a collection of legal document statistics
     */
    List<LegalDocumentStatistics> findSearchTermStatistcs(Date initDate, Date endDate);

    Collection<LegalDocument> findAll();

    LegalDocumentCategory findLegalDocumentCategoryByName(String category, Long depth, String parent);

    LegalDocumentType findLegalDocumentTypeByName(String type);

    LegalDocumentState findLegalDocumentStateByName(String state);

    /**
     * updates the repository
     *
     * @throws BusinessException - error 
     */
    public void updateRepository() throws BusinessException;

    public void updateFilenames() throws BusinessException;
}
